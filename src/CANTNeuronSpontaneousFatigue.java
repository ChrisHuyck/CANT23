
import java.util.*;

public class CANTNeuronSpontaneousFatigue extends CANTNeuron {
  double incomingStrength = 0.0;
  //Cover function for the variable.  Need to update (say) every cycle.
  public double getIncomingStrength() {
    return incomingStrength;}
  public void setIncomingStrength(double newStrength)
    {incomingStrength=newStrength;}
  
  private double fatigueCompensatoryBase = 10;

public CANTNeuronSpontaneousFatigue(int neuronID, CANTNet net) {
  super(neuronID, net);
  compensatoryBase = fatigueCompensatoryBase;
} 

public CANTNeuronSpontaneousFatigue(int neuronID, CANTNet net, 
  double weightChange) {
  super(neuronID, net);
  compensatoryBase = fatigueCompensatoryBase;
} 

public CANTNeuronSpontaneousFatigue(int neuronID ,CANTNet net, 
  double weightChange, 
  int synapsesPerNeuron) {
  super(neuronID,net,synapsesPerNeuron);
  compensatoryBase = fatigueCompensatoryBase;
}


public void setFired(int undone,int undone2){
  super.setFired();
  if (getFired())
	if (parentNet.getName().compareTo("GasNet") == 0)
    System.out.println("Fired " + getId() + " " + parentNet.getName());
}
  
private float getNewFatigue () {
  if (fatigue < 0) {
      //return (fatigue - parentNet.getFatigueRecoveryRate()/3);
    double base = 3 - fatigue;
    double change =  Math.pow(base,-4);
    return (fatigue - (float) change);
  }
  else
    return(fatigue - parentNet.getFatigueRecoveryRate());
}

public void modifyFatigue(){
  if (getFired()) {
    if (fatigue>-.25)
      fatigue += parentNet.getFatigueRate();
    else
      fatigue= fatigue/2;
    //System.out.println(getId()+" fatigue= "+fatigue+ " currentActivation "+ currentActivation+ " ");
  }
  else { fatigue = getNewFatigue(); }
}

  //----------------------New Learning Rules  
  //The learning rules in CANTNeuron allow the weights to change by up 
  //to the maximum amount.  Here we moved LR out of getIncreaseBase and
  //getDecreaseBase so that it can change by at most LR each cycle.
  //Note axonalStrengthmdedian isn't right in this version of learn4.

    public double getDecreaseBase(double currentStrength) {
      double modifiedStrength = currentStrength/(parentNet.getAxonalStrengthMedian()*2);
      return (modifiedStrength);
    }

  public double getIncreaseBase(double currentStrength) {
    double modifiedStrength = currentStrength/(parentNet.getAxonalStrengthMedian()*2);
    double estimateNeuronNotFires = (1-modifiedStrength);
    return (estimateNeuronNotFires*(parentNet.getAxonalStrengthMedian()*2));
  }

//Look at all the neurons with positive incoming weights and return the 
//sum.
public double getIncomingStrength(int type) {
    //if type == 1 positive, 2 negative, 3 both
    double incomingStrength = 0.0;
    String netName = parentNet.getName();
    //loop through all nets
    Enumeration <?> eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      CANTNet net = (CANTNet)eNum.nextElement();
      //loop through all neurons 
      for (int cNeuron = 0; cNeuron < net.getTotalNeurons(); cNeuron++) {
        //loop through all synapses
        for (int cSynapse = 0; cSynapse < net.neurons[cNeuron].
          getCurrentSynapses(); cSynapse++) {
          CANTNeuron toNeuron=net.neurons[cNeuron].synapses[cSynapse].toNeuron;
          //if toNeuron is this one then
          if ((getId() == toNeuron.getId()) &&
              (netName.compareTo(toNeuron.parentNet.getName()) == 0)){
             double incomingWeight=net.neurons[cNeuron].synapses[cSynapse].
               getWeight();
             if ((type ==1) && (incomingWeight > 0))
	       incomingStrength+=incomingWeight;
             else if ((type ==2) && (incomingWeight < 0))
	       incomingStrength+=incomingWeight;
             else if (type ==3) 
	       incomingStrength+=incomingWeight;
             else if ((type < 1) && (type > 3))
               System.out.println("error in type in getIncomingStrength");
	  }
	}
      }
    }
    return incomingStrength;
}

public double getStrengthCompensatoryModifier(double strength,
                                      double saturationBase){

  if (! parentNet.isCompensatoryLearningOn()) return 1.0;
  if (strength < 0) strength *= -1.0;
  double compensatoryModifier = (saturationBase-strength)/
    parentNet.getCompensatoryDivisor();
  double result = Math.pow(compensatoryBase,compensatoryModifier);
  return (result);
}

protected double getWeakCompensatoryModifier(double strength,
                           double saturationBase){

  if (! parentNet.isCompensatoryLearningOn()) return 1.0;
  if (strength < 0) strength *= -1.0;
  double compensatoryFromModifier =(strength-saturationBase)/
    parentNet.getCompensatoryDivisor();
  double result = Math.pow(compensatoryBase,compensatoryFromModifier);
  return (result);
}

  //I noted some problems with inhibitory neurons (too much inhibitory
  //strength e.g. lots of -.99s).  This fixed those.  It only uses
  //a compensatory modifier from the presynaptic neuron.
  public void learnold(){
    double totalConnectionStrength;
    double modification;
    double fromCompensatoryStrengthModifier,fromCompensatoryWeakModifier;

    //Only learn when the from neuron is active
    if (!getFired()) return;

    totalConnectionStrength = getTotalConnectionStrength();
    fromCompensatoryWeakModifier = 
	   getWeakCompensatoryModifier(totalConnectionStrength);
    fromCompensatoryStrengthModifier = 
	   getStrengthCompensatoryModifier(totalConnectionStrength);
    //System.out.println("mods "+totalConnectionStrength+" "+fromCompensatoryWeakModifier+" "+fromCompensatoryStrengthModifier);


    //Test each Synapse from the active neuron
    for (int synap=0; synap < getCurrentSynapses(); synap++) {
      double connectionStrength = synapses[synap].getWeight();
      CANTNeuron toNeuron = synapses[synap].getTo();

      //If both Neurons were active,
      if (toNeuron.getFired()) {
        if (!isInhibitory){
          modification = getIncreaseBase(connectionStrength);
          modification *= fromCompensatoryStrengthModifier;
          if (modification > 1.0) modification = 1.0;
          modification  *= parentNet.getLearningRate();
          connectionStrength = connectionStrength+modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("Inc Exc "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification);
        }

        else{//decrease inhibition
          modification = getDecreaseBase(connectionStrength);
          modification *= fromCompensatoryWeakModifier;
          if (modification > 1.0) modification = 1.0;
          modification  *= parentNet.getLearningRate();
          connectionStrength = connectionStrength-modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("dec Inh "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification + " " + totalConnectionStrength);
        }
      } //end of to neuron active


      //if to Neuron is inactive
      else {
        if (!isInhibitory){
          modification = getDecreaseBase(connectionStrength);
          modification *= fromCompensatoryWeakModifier;
          if (modification > 1.0) modification = 1.0;
          modification  *= parentNet.getLearningRate();
          connectionStrength = connectionStrength-modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("dec Exc "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification);
        }

        else {
          modification = getIncreaseBase(connectionStrength);
          modification *= fromCompensatoryStrengthModifier;
          if (modification > 1.0) modification = 1.0;
          modification  *= parentNet.getLearningRate();
          connectionStrength = (connectionStrength)-modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("Inc Inh"+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification);
        }
      } // end of to Neuron inactive
    }
  }


public void learnPostAndPreCompense(){
		  
	double totalConnectionStrength;
    double modification;
    double fromCompensatoryStrengthModifier,fromCompensatoryWeakModifier;
    double totalToConnectionStrength;
    double toCompensatoryStrengthModifier,toCompensatoryWeakModifier;


    //Only learn when the from neuron is active
    if (!getFired()) return;

    totalConnectionStrength = getTotalConnectionStrength();
    fromCompensatoryWeakModifier = 
      getWeakCompensatoryModifier(totalConnectionStrength,
      parentNet.getSaturationBase());
    fromCompensatoryStrengthModifier = getStrengthCompensatoryModifier(
      totalConnectionStrength, parentNet.getSaturationBase());
    //System.out.println("mods "+totalConnectionStrength+" "+fromCompensatoryWeakModifier+" "+fromCompensatoryStrengthModifier);

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
          modification *= fromCompensatoryStrengthModifier;
          modification *= toCompensatoryStrengthModifier;
          if (modification > 1.0) modification = 1.0;
          modification  *= parentNet.getLearningRate();
          connectionStrength = connectionStrength+modification;
          synapses[synap].setWeight(connectionStrength);
	  //System.out.println("Inc Exc "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification + " "+fromCompensatoryStrengthModifier + " " + totalConnectionStrength + " " + parentNet.getSaturationBase());
        }

        else{//decrease inhibition
          modification = getDecreaseBase(connectionStrength);
          modification *= fromCompensatoryWeakModifier;
          if (modification > 1.0) modification = 1.0;
          modification  *= parentNet.getLearningRate();
          connectionStrength = connectionStrength-modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("dec Inh "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification + " " + totalConnectionStrength);
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
          modification *= fromCompensatoryWeakModifier;
          modification *= toCompensatoryWeakModifier;
          if (modification > 1.0) modification = 1.0;
          modification  *= parentNet.getLearningRate();
          connectionStrength = connectionStrength-modification;
          synapses[synap].setWeight(connectionStrength);
	  //System.out.println("dec Exc "+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification + " " + fromCompensatoryWeakModifier);
        }

        else {
          modification = getIncreaseBase(connectionStrength);
          modification *= fromCompensatoryStrengthModifier;
          if (modification > 1.0) modification = 1.0;
          modification  *= parentNet.getLearningRate();
          connectionStrength = (connectionStrength)-modification;
          synapses[synap].setWeight(connectionStrength);
          //System.out.println("Inc Inh"+this.getId()+" "+toNeuron.getId()+" "+synapses[synap].getWeight()+" "+modification);
        }
      } // end of to Neuron inactive
    }
  }
  
public void learn4(){
	//need to put in inhibition
//public void learnPostCompense(){
  
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
}//end of class
