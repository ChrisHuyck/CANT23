

import java.util.*;

public class AutoNet extends CANTNet {
  
  public AutoNet(){
  }
  
  public AutoNet(String name,int cols, int rows,int topology){
    super(name,cols,rows,topology);
    //cyclesToStimulatePerRun = 20;
  }

//This really slows down processing.  
/*public void updateIncomingStrengthsSlow(){
   for (int cNeuron = 0; cNeuron < getTotalNeurons(); cNeuron++) {
     CANTNeuronSpontaneousFatigue modNeuron = (CANTNeuronSpontaneousFatigue)
       neurons[cNeuron];
     modNeuron.setIncomingStrength(modNeuron.getIncomingStrength(3));
   }
}*/

public void zeroIncomingStrengths(){
  for (int cNeuron = 0; cNeuron < getTotalNeurons(); cNeuron++) {
	 CANTNeuronDecor modNeuron = (CANTNeuronDecor) neurons[cNeuron];
	 //CANTNeuronSpontaneousFatigue modNeuron = (CANTNeuronSpontaneousFatigue) neurons[cNeuron];
     modNeuron.setIncomingStrength(0);
  }
}

  //need this to subclass experiment
  public void changePattern(int cantStep)
    {
    //note this only runs on the first step.  The rest of the time it
    //is in endEpoch.
    if (cantStep ==0){	
      int curPattern = AutoCANT23.patReader.getPattern(cantStep);
      curPattern %= getTotalPatterns();
      setCurrentPattern(curPattern);
      ((CANTPattern)patterns.get(curPattern)).arrange(getNeuronsToStimulate());
    }
  }
  
  //covered this from CANTNet so I can get the anti-Hebbian like learning.  The one that is
  //relevant is getLearninOn == 1;
	public void learn() {
		if (getLearningOn() == 0)
			return;
		// System.out.println(getName() + " " + axonalStrengthMedian + " " +
		// saturationBase);

		// This is relatively new. If learning is 3, we call learn in the
		// subclass.
		if (getLearningOn() == 3) {
			subclassLearn();
			return;
		}

		int totalNeurons = size();
		for (int neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++) {
			if (getLearningOn() == 2)// only learn if the to neurons net is of
										// learn type 1
				neurons[neuronIndex].restrictedLearn();
			else {
				CANTNeuronDecor fromNeuron = (CANTNeuronDecor)neurons[neuronIndex];
				fromNeuron.setSynapseFirings();
				fromNeuron.learnPostCompense(); 
				//fromNeuron.learnPostCompenseWithAnti();
				//neurons[neuronIndex].learn4();
			}
		}
	}

  public void readBetweenAllNets() {
    int netsChecked = 0;
    Enumeration <?> eNum = CANT23.nets.elements();
    AutoNet net = (AutoNet) eNum.nextElement();
    AutoNet baseNet = net;
    AutoNet outputNet = net;

    System.out.println("Auto Read Between");

    AutoNet gasNet = net;
    do {
      System.out.println(net.getName());
      if (net.getName().compareTo("BaseNet") == 0)
        baseNet = net;
      else if (net.getName().compareTo("GasNet") == 0)
        gasNet = net;
      else if (net.getName().compareTo("OutputNet") == 0)
        outputNet = net;
      else
        System.out.println(net.getName() + " missed net in connect all");
      netsChecked++;
      if (netsChecked < 3)
        net = (AutoNet) eNum.nextElement();
    } while (netsChecked < 3);

    baseNet.readConnectTo(gasNet);
    gasNet.readConnectTo(baseNet);
    gasNet.readConnectTo(outputNet);
    outputNet.readConnectTo(gasNet);
  }
  
  
  
  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
    //get all of the propogation done in each net in step.
    AutoCANT23.runOneStepStart();
	
    Enumeration <?> eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      AutoNet net = (AutoNet)eNum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      AutoNet net = (AutoNet)eNum.nextElement();
      net.setExternalActivation(CANTStep);
    }
    
    //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      AutoNet net = (AutoNet)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      AutoNet net = (AutoNet)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      AutoNet net = (AutoNet)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      AutoNet net = (AutoNet)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      AutoNet net = (AutoNet)eNum.nextElement();
      net.learn();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      AutoNet net = (AutoNet)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      AutoNet net = (AutoNet)eNum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
//if (net.getName().compareTo("VerbNet") == 0)   System.out.println(net.neurons[0].getFatigue() +   " verb Neuron " + net.neurons[0].getActivation());
    }
  }


  public CANTNet getNewNet(String name,int cols, int rows,int topology){
    AutoNet net = new AutoNet (name,cols,rows,topology);
    return (net);
  } 

  protected void createNeurons() {
    //System.out.print(" creating fatigue decorrelating neurons ");
    totalNeurons = 0;
    neurons = new CANTNeuronDecor[cols*rows];
    //neurons = new CANTNeuronSpontaneousFatigue[cols*rows];
    for(int i=0;i< cols*rows;i++)
      {
      neurons[i] = new CANTNeuronDecor(totalNeurons++,this);
      //neurons[i] = new CANTNeuronSpontaneousFatigue(totalNeurons++,this);
      neurons[i].setCompensatoryBase(10.0);
      }
    setInitialFatigue();
  }

  //--------fWorks-------------------------
  public void connectBaseToGasNet(AutoNet gasNet) {
    for (int fromNeuron = 0; fromNeuron < getSize(); fromNeuron++) {
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = CANT23.random.nextInt(gasNet.getSize());
        //no Inhibitory connections to gas
        if (!neurons[fromNeuron].isInhibitory()) 
          neurons[fromNeuron].addConnection(gasNet.neurons[toNeuron], 0.001);
      }
    }
  }


  public void connectGasToBaseNet(AutoNet baseNet) {
    for (int fromNeuron = 0; fromNeuron < getSize(); fromNeuron++) {
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = CANT23.random.nextInt(baseNet.getSize());
        if (neurons[fromNeuron].isInhibitory()) 
          neurons[fromNeuron].addConnection(baseNet.neurons[toNeuron],-0.03);
      }
    }
  }

  public void connectGasToOutputNet(AutoNet outputNet) {
    for (int fromNeuron = 0; fromNeuron < getSize(); fromNeuron++) {
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = CANT23.random.nextInt(outputNet.getSize());
        if (!neurons[fromNeuron].isInhibitory()) 
          neurons[fromNeuron].addConnection(outputNet.neurons[toNeuron],0.03);
        //else 
          //neurons[fromNeuron].addConnection(outputNet.neurons[toNeuron],-0.03);
      }
    }
  }

  public void connectOutputToGasNet(AutoNet gasNet) {
    for (int fromNeuron = 0; fromNeuron < getSize(); fromNeuron++) {
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = CANT23.random.nextInt(gasNet.getSize());
        if (!neurons[fromNeuron].isInhibitory()) 
          neurons[fromNeuron].addConnection(gasNet.neurons[toNeuron], 0.03);
      }
    }
  }

  //--------!fWorks-------------------------
  public void connectBaseToOutput(AutoNet outputNet) {
    for (int fromNeuron = 0; fromNeuron < getSize(); fromNeuron++) {
      for (int synapse = 0; synapse < 5; synapse++) {
        int toNeuron = CANT23.random.nextInt(outputNet.getSize());
        if (neurons[fromNeuron].isInhibitory()) 
          neurons[fromNeuron].addConnection(outputNet.neurons[toNeuron],-0.03);
        else
          neurons[fromNeuron].addConnection(outputNet.neurons[toNeuron], 0.03);
      }
    }
  }

  protected void setConnections() {
    for(int i=0;i< cols*rows;i++)
      {
      if (neurons[i].isInhibitory())
        setConnectionsRandomly(i,20,0.01);
      else
        setConnectionsRandomly(i,20,0.01);
      }
  }

  //Fatigue should be relatively random to start.
  public void setInitialFatigue() {
    for(int i=0;i< cols*rows;i++) {
      float newFatigue = CANT23.random.nextFloat();
      newFatigue *= getActivationThreshold()*2;
      newFatigue -= getActivationThreshold();
      neurons[i].setFatigue(newFatigue);
    }
  }

  //
  public void setInitialFatigue(double topVal) {
    double range = getActivationThreshold() + topVal;
    for(int i=0;i< cols*rows;i++) {
      float newFatigue = CANT23.random.nextFloat();
      newFatigue *= range;
      newFatigue -= getActivationThreshold();
      neurons[i].setFatigue(newFatigue);
    }
  }

  private void setInputTopology() {
      //setConnections(); no internal connections
  }
  
  private void setOtherTopology() {
      setConnections();
  }


  public void initializeNeurons() {
    //set up topologies.
    createNeurons();
    setInitialFatigue();

    if (topology == 1){
      //System.out.println("xor2 input topology ");
      setInputTopology();
    }
    else if (topology == 2){
      //System.out.println("xor2 output topology ");
      setOtherTopology();
    }
    else if (topology == 3){
      //System.out.println("xor2 gas topology ");
      setOtherTopology();
    }
    else 
    System.out.println("bad toppology specified "+ topology);
  }
 
 /*
    int toSynapses = 0;
    double toWeight = 0.0;
    for (int synapse = 0; synapse < neuron.getCurrentSynapses(); synapse ++) {
      int toNeuronID = neuron.synapses[synapse].toNeuron.getId();
      if ((toNeuronID >= (toCA*200))  && (toNeuronID < ((toCA+1) *200))) {
        toSynapses++;
        toWeight += neuron.synapses[synapse].getWeight();
      }
    }
    if (toSynapses > 0)
      return (toWeight/toSynapses);
    return 0.0;
  }

private double getTotalWeights(CANTNeuron neuron,String toSubnet) {
    double toWeight = 0.0;
    for (int synapse = 0; synapse < neuron.getCurrentSynapses(); synapse ++) {
      int toNeuronID = neuron.synapses[synapse].toNeuron.getId();
      if (neuron.synapses[synapse].toNeuron.parentNet.getName().compareTo(
        toSubnet) == 0){
        toWeight += neuron.synapses[synapse].getWeight();
      }
    }
    return (toWeight);
}
*/

  private double getTotalWeights(CANTNeuron neuron) {
    double toWeight = 0.0;
    for (int synapse = 0; synapse < neuron.getCurrentSynapses(); synapse ++) {
      toWeight += neuron.synapses[synapse].getWeight();
    }
    return (toWeight);
  }

  //print out the weights to baseE,baseA,baseB,outputN, and outputY
  private void printToCAWeights(CANTNeuron neuron) {
    AutoNet baseNet = (AutoNet) CANT23.experiment.getNet("BaseNet");
    AutoNet outputNet = (AutoNet) CANT23.experiment.getNet("OutputNet");

    double oneWeight = 0;
    double twoWeight = 0;
    double threeWeight = 0;
    double fourWeight = 0;
    double fiveWeight = 0;
    for (int synapse = 0; synapse < neuron.getCurrentSynapses();synapse++) {
      CANTNeuron toNeuron = neuron.synapses[synapse].toNeuron;
      int toId = toNeuron.getId();
      String toNet = toNeuron.parentNet.getName();
      if (toId < 200) {
        if (toNet.compareTo("BaseNet") == 0)
          oneWeight += neuron.synapses[synapse].getWeight();
        else
          fourWeight += neuron.synapses[synapse].getWeight();
      }
      else if (toId < 400) {
        if (toNet.compareTo("BaseNet") == 0)
          twoWeight += neuron.synapses[synapse].getWeight();
        else
          fiveWeight += neuron.synapses[synapse].getWeight();
      }
      else 
        threeWeight += neuron.synapses[synapse].getWeight();
    }

    System.out.format("%1d %2$03.2f %3$03.2f %4$03.2f %5$03.2f %6$03.2f %n", 
      neuron.getId(), oneWeight, twoWeight, threeWeight,fourWeight,fiveWeight);
  }


  public void kludge () {
    System.out.println("kludge" + AutoCANT23.kludge );
    AutoNet net = (AutoNet) CANT23.experiment.getNet("OutputNet");

    if (AutoCANT23.kludge == 0) {
      net = (AutoNet) CANT23.experiment.getNet("OutputNet");
      for (int i= 100; i < 110; i++) 
        {
       double totalWeights = getTotalWeights(net.neurons[i]);
        System.out.println(i + " " + totalWeights + " " +  net.neurons[i].getFatigue()); 
        }
    }
    else if (AutoCANT23.kludge == 2) {
        for (int i= 390; i < 400; i++) 
          {

         double totalWeights = getTotalWeights(net.neurons[i]);
          System.out.println(i + " " + totalWeights + " " +  net.neurons[i].getFatigue()); 
          }
      }
    else {
        for (int i= 0; i < 10; i++) 
        {
        System.out.println(i + " act " +  net.neurons[i].getActivation()); 
        }
    }
    /*
           double avgWeights;
      double totalWeights = getTotalWeights(net.neurons[i]);
      avgWeights = getTotalWeights(net.neurons[i],"OutputNet");
      System.out.println(i + " " + avgWeights + " " + totalWeights); 
      //System.out.println(i + " " + this.neurons[i].getActivation() + 
      //                      " " + this.neurons[i].getFatigue());
      */
  }
  
  public void measure(int currentStep) {
    System.out.println("measure " + neurons[0].getActivation() + " " + 
      neurons[0].getFired() + " " + 
	  currentStep);
  }
}
