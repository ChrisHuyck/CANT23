
import java.util.Enumeration;

public class congressNet extends CANTNet {
  
  public congressNet(){
  }
  
  public congressNet(String name,int cols, int rows,int topology){
    super(name,cols,rows,topology);
    //cyclesToStimulatePerRun = 20;
  }

//This really slows down processing.  
public void updateIncomingStrengthsSlow(){ 
   for (int cNeuron = 0; cNeuron < getTotalNeurons(); cNeuron++) {
     CANTNeuronSpontaneousFatigue modNeuron = (CANTNeuronSpontaneousFatigue)
       neurons[cNeuron];
     modNeuron.setIncomingStrength(modNeuron.getIncomingStrength(3));
   }
}

public void zeroIncomingStrengths(){
  for (int cNeuron = 0; cNeuron < getTotalNeurons(); cNeuron++) {
     CANTNeuronSpontaneousFatigue modNeuron = (CANTNeuronSpontaneousFatigue)
       neurons[cNeuron];
     modNeuron.setIncomingStrength(0);
  }
}

  //need this to subclass experiment
  public void changePattern(int cantStep)
    {
    //note this only runs on the first step.  The rest of the time it
    //is in endEpoch.
    if (cantStep ==0){	
      int curPattern = CANT23congress.patReader.getPattern(cantStep);
      curPattern %= getTotalPatterns();
      setCurrentPattern(curPattern);
      ((CANTPattern)patterns.get(curPattern)).arrange(getNeuronsToStimulate());
    }
  }
  
  public void readBetweenAllNets() {
    int netsChecked = 0;
    Enumeration <?> eNum = CANT23.nets.elements();
    congressNet net = (congressNet) eNum.nextElement();
    congressNet baseNet = net;
    congressNet outputNet = net;

    System.out.println("congress2 read Between");

    congressNet gasNet = net;
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
        net = (congressNet) eNum.nextElement();
    } while (netsChecked < 3);

    baseNet.readConnectTo(gasNet);
    gasNet.readConnectTo(baseNet);
    gasNet.readConnectTo(outputNet);
    outputNet.readConnectTo(gasNet);
  }
  
  
  
  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
    //get all of the propogation done in each net in step.
    CANT23congress.runOneStepStart();
	
    Enumeration <?> eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      congressNet net = (congressNet)eNum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      congressNet net = (congressNet)eNum.nextElement();
      net.setExternalActivation(CANTStep);
    }
    
    //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      congressNet net = (congressNet)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      congressNet net = (congressNet)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      congressNet net = (congressNet)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      congressNet net = (congressNet)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      congressNet net = (congressNet)eNum.nextElement();
      net.learn();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      congressNet net = (congressNet)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      congressNet net = (congressNet)eNum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
//if (net.getName().compareTo("VerbNet") == 0)   System.out.println(net.neurons[0].getFatigue() +   " verb Neuron " + net.neurons[0].getActivation());
    }
  }


  public CANTNet getNewNet(String name,int cols, int rows,int topology){
    congressNet net = new congressNet (name,cols,rows,topology);
    return (net);
  } 

  protected void createNeurons() {
    //System.out.print(" creating fatigue neurons ");
    totalNeurons = 0;
    neurons = new CANTNeuronSpontaneousFatigue[cols*rows];
    for(int i=0;i< cols*rows;i++)
      {
      neurons[i] = new CANTNeuronSpontaneousFatigue(totalNeurons++,this);
      neurons[i].setCompensatoryBase(10.0);
      }
    setInitialFatigue();
  }

  //--------fWorks-------------------------
  public void connectBaseToGasNet(congressNet gasNet) {
    for (int fromNeuron = 0; fromNeuron < getSize(); fromNeuron++) {
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = CANT23.random.nextInt(gasNet.getSize());
        //no Inhibitory connections to gas
        if (!neurons[fromNeuron].isInhibitory()) 
          neurons[fromNeuron].addConnection(gasNet.neurons[toNeuron], 0.001);
      }
    }
  }


  public void connectGasToBaseNet(congressNet baseNet) {
    for (int fromNeuron = 0; fromNeuron < getSize(); fromNeuron++) {
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = CANT23.random.nextInt(baseNet.getSize());
        if (neurons[fromNeuron].isInhibitory()) 
          neurons[fromNeuron].addConnection(baseNet.neurons[toNeuron],-0.03);
      }
    }
  }

  public void connectGasToOutputNet(congressNet outputNet) {
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

  public void connectOutputToGasNet(congressNet gasNet) {
    for (int fromNeuron = 0; fromNeuron < getSize(); fromNeuron++) {
      for (int synapse = 0; synapse < 10; synapse++) {
        int toNeuron = CANT23.random.nextInt(gasNet.getSize());
        if (!neurons[fromNeuron].isInhibitory()) 
          neurons[fromNeuron].addConnection(gasNet.neurons[toNeuron], 0.03);
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

  

  public void kludge () {
    System.out.println("kludge" + CANT23congress.kludge );
    congressNet net = (congressNet) CANT23.experiment.getNet("OutputNet");

    if (CANT23congress.kludge == 1) {
      net = (congressNet) CANT23.experiment.getNet("BaseNet");
      for (int i= 0; i < 10; i++) 
        {
       double totalWeights = getTotalWeights(net.neurons[i]);
        System.out.println(i + " " + totalWeights + " " +  net.neurons[i].getFatigue()); 
        }
    }
    else if (CANT23congress.kludge == 0) {
        for (int i= 0; i < 10; i++) 
          {
          double totalWeights = getTotalWeights(net.neurons[i]);
          CANTNeuronSpontaneousFatigue temp = (CANTNeuronSpontaneousFatigue) net.neurons[i];
          System.out.println(i + " " + totalWeights + " " +  temp.getIncomingStrength()); 
          }
      }
    else if (CANT23congress.kludge == 2) {
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