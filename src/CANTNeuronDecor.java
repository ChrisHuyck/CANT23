
public class CANTNeuronDecor extends CANTNeuronSpontaneousFatigue {

	public CANTNeuronDecor(int neuronID, CANTNet net) {
		super(neuronID, net);
		synapses = new MemSynapse[100];
	}

 public CANTNeuronDecor(int neuronId ,CANTNet net, int synapsesPerNeuron) {
	 super (neuronId,net,synapsesPerNeuron);
	 System.out.print(" error creating neuron decor ");
   }
 
 public void addConnection(CANTNeuron to, double weight) {
     //search for an existing connection.
     for (int cSynapse = 0 ; cSynapse < getCurrentSynapses(); cSynapse ++){
       if (synapses[cSynapse].getTo() == to){
         flag= true;
	  //          synapses[cSynapse].setWeight(weight);
         flag=false;
         return;
       }
     }
     synapses[getCurrentSynapses()] = new MemSynapse(parentNet,this,to,weight);
     incCurrentSynapses();
   }
 
 public void setSynapseFirings(){
     for (int cSynapse = 0 ; cSynapse < getCurrentSynapses(); cSynapse ++){
    	 MemSynapse theSynapse = (MemSynapse) synapses[cSynapse];
    	 theSynapse.setFirings();
     }	 
 }
 
 //Just scammed CANTNet.learn4.  As it runs, it's compensatory learning based
 //compensating on the weights to the post-synaptic neuron.
 public void learnPostCompense(){
		//need to put in inhibition
	  double totalConnectionStrength;
	  double modification;
	  double totalToConnectionStrength;
	  double toCompensatoryStrengthModifier,toCompensatoryWeakModifier;


	  //Only learn when the from neuron is active
	  if (!getFired()) return;

	  totalConnectionStrength = getTotalConnectionStrength();
	  
	  //Test each Synapse from the active neuron
	  for (int synap=0; synap < getCurrentSynapses(); synap++) {
	    double connectionStrength = synapses[synap].getWeight();
	    CANTNeuronSpontaneousFatigue toNeuron = (CANTNeuronSpontaneousFatigue)
	      synapses[synap].getTo();

	    //If both Neurons were active,
	    if (toNeuron.getFired()) {
	      if (!isInhibitory){
	        totalToConnectionStrength = toNeuron.getIncomingStrength();
	        toCompensatoryStrengthModifier = 
	          getStrengthCompensatoryModifier(totalToConnectionStrength,
	          toNeuron.parentNet.getSaturationBase());
	        modification = getIncreaseBase(connectionStrength);
	        modification *= toCompensatoryStrengthModifier;
	        if (modification > 1.0) modification = 1.0;
	        modification  *= parentNet.getLearningRate();
	        connectionStrength = connectionStrength+modification;
	        synapses[synap].setWeight(connectionStrength);
		  //System.out.println("Inc Exc "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification + " "+fromCompensatoryStrengthModifier + " " + totalConnectionStrength + " " + parentNet.getSaturationBase());
	      }

	      else{//decrease inhibition (move toward 0)
	    	totalToConnectionStrength = toNeuron.getIncomingStrength();
	        modification = getIncreaseBase(1+connectionStrength);
	        toCompensatoryStrengthModifier = 
	           getStrengthCompensatoryModifier(totalToConnectionStrength,
	           toNeuron.parentNet.getSaturationBase());
	        modification *= toCompensatoryStrengthModifier;
	        if (modification > 1.0) modification = 1.0;
	        modification  *= parentNet.getLearningRate();
	        connectionStrength = (connectionStrength)+modification;
	        synapses[synap].setWeight(connectionStrength);
	        //System.out.println("dec Inh "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification + " " + totalToConnectionStrength);
	      }
	    } //end of to neuron active


	    //if to Neuron is inactive
	    else {
	      if (!isInhibitory){
	        totalToConnectionStrength = toNeuron.getIncomingStrength();
	        toCompensatoryWeakModifier = 
	          getWeakCompensatoryModifier(totalToConnectionStrength,
		    toNeuron.parentNet.getSaturationBase());
	        modification = getDecreaseBase(connectionStrength);
	        modification *= toCompensatoryWeakModifier;
	        if (modification > 1.0) modification = 1.0;
	        modification  *= parentNet.getLearningRate();
	        connectionStrength = connectionStrength-modification;
	        synapses[synap].setWeight(connectionStrength);
		  //System.out.println("dec Exc "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification + " " + fromCompensatoryWeakModifier);
	      }

	      else { //increase inhibition (move away from 0)
	    	  totalToConnectionStrength = toNeuron.getIncomingStrength();
	          modification = getDecreaseBase(1+connectionStrength);
	          toCompensatoryWeakModifier = 
	                  getWeakCompensatoryModifier(totalToConnectionStrength,
	                  toNeuron.parentNet.getSaturationBase());
	          modification *= toCompensatoryWeakModifier;
	          if (modification > 1.0) modification = 1.0;
	          modification  *= parentNet.getLearningRate();
	          connectionStrength = connectionStrength-modification;
	          synapses[synap].setWeight(connectionStrength);
	    	 //System.out.println("Inc Inh"+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification);
	      }
	    } // end of to Neuron inactive
	  }
	  }
 
 //
	private void learnAntiHebbian() {
		if (getTotalConnectionStrength() <= parentNet.getSaturationBase()) return;
		
        int coFiringTotal = 0;
        float coFiringAverage;
		//get coFirings
		for (int synap = 0; synap < getCurrentSynapses(); synap++) {
			MemSynapse synapse = (MemSynapse)synapses[synap];
			coFiringTotal += synapse.getCoFirings();
		}
		coFiringAverage = coFiringTotal/getCurrentSynapses();
		
		for (int synap = 0; synap < getCurrentSynapses(); synap++) {
			MemSynapse synapse = (MemSynapse)synapses[synap];
  		    double connectionStrength = synapse.getWeight();
  	        //decrease (again non-firings)
			if (synapse.getCoFirings() == 0) {
				synapse.setWeight(connectionStrength - 0.2);
			}

			//decrease bottom half
			else if (synapse.getCoFirings() <= coFiringAverage) {
				synapse.setWeight(connectionStrength - 0.1);
			}

			//increase tophalf
			else{ 
				synapse.setWeight(connectionStrength + 0.1);
			}
				
			//reset synaptic firings.
			synapse.resetCoFirings();
		}

	}

 //Just took postcompense from above, but for increasing, it does something
 //anti-Hebbian if S>Sb
	public void learnPostCompenseWithAnti() {
		//add in antiHebbian bit for saturated neurons
		if ((AutoCANT23.CANTStep % 10) == 0) {
			learnAntiHebbian();
		}
		
		learnPostCompense();
	}
}
