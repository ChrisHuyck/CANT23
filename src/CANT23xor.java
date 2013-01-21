

import java.util.*;

public class CANT23xor extends CANT23{
  public static String ExperimentXMLFile;
  public static xorNet nullNet;
  public static xorExperiment experiment; 
  public static boolean fWorks;  //this is the switch between 2 (failing)
                                 //and 3 (working?) nets.  Set in main.
  public static int inputModel; //0 no input, 1, 1 CA at a time, 2
                                //xor train, and 3 xor test.
  public static int kludge = 0;
  
  public static void main(String args[]){
System.out.println("initialize CANT xor2");
    fWorks = true;
    //fWorks = false;
    if (fWorks) 
      ExperimentXMLFile = "xor2/xorWork.xml";
    else 
      ExperimentXMLFile = "xor2/xorFail.xml";
    seed = 1;
    initRandom();
    readNewSystem();
    positionWindows();
    delayBetweenSteps=0;
  }

  protected static void readNewSystem() {
    //System.out.println("readNewSystem");
    nullNet = new xorNet();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new CANT23xor.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    xorNet net = (xorNet) experiment.getNet("BaseNet");
    workerThread.start();	
    connectAllNets();	
    inputModel = 2;
    }
  
  private static void connectAllNets() {
    xorNet baseNet = (xorNet)experiment.getNet("BaseNet");
    xorNet outputNet = (xorNet)experiment.getNet("OutputNet");
	
    if (fWorks) {
      xorNet gasNet = (xorNet)experiment.getNet("GasNet");
      baseNet.connectBaseToGasNet(gasNet);
      //gasNet.connectGasToBaseNet(baseNet);
      gasNet.connectGasToOutputNet(outputNet);
      outputNet.connectOutputToGasNet(gasNet);
    }
    else {
      baseNet.connectBaseToOutput(outputNet);
      outputNet.connectOutputToBase(baseNet);
    }
  }


  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    experiment = new xorExperiment();
    //System.out.println("initialize xor 2 Experiment");
    experiment.printExpName();
  }

  
private static void updateIncomingStrengths() {
  //loop through all nets
  Enumeration <?> eNum = CANT23.nets.elements();
  while (eNum.hasMoreElements()) {
    CANTNet net = (CANTNet)eNum.nextElement();
    //loop through all neurons 
    for (int cNeuron = 0; cNeuron < net.getTotalNeurons(); cNeuron++) {
      //loop through all synapses
      for (int cSynapse = 0; cSynapse < net.neurons[cNeuron].
        getCurrentSynapses(); cSynapse++) {
        if (net.neurons[cNeuron].synapses[cSynapse].getWeight() > 0) {
          double currentIncomingWeight = ((CANTNeuronSpontaneousFatigue)net.neurons[cNeuron].
        	synapses[cSynapse].toNeuron).getIncomingStrength();
          ((CANTNeuronSpontaneousFatigue)net.neurons[cNeuron].synapses[cSynapse].toNeuron)
          .setIncomingStrength(currentIncomingWeight +
            net.neurons[cNeuron].synapses[cSynapse].getWeight());
        }
      }
    }
  }
}

  public static void runOneStepStart() {
    if (experiment.trainingLength == CANTStep) experiment.switchToTest();
    
    if (experiment.getInTest()) experiment.measure(CANTStep);
    //else  
    	//{
    	//if ((CANTStep % 20) == 0)
    	  //System.out.println("Weights " + CANTStep + " " + 
    	    //experiment.printConnectionWeights(CANTStep,"BaseNet") + " " +
    	    //experiment.printConnectionWeights(CANTStep,"GasNet"));
    	//}

    if (experiment.isEndEpoch(CANTStep))
      experiment.endEpoch();
  }
  
  public static synchronized void resetForNewTest() {
    Enumeration <?> eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      xorNet net = (xorNet)eNum.nextElement();
      //      net.setInitialFatigue(-1.0);
      for (int i = 0; i < net.getSize(); i++) {
	  net.neurons[i].setActivation(0.0);
      }
    }
  }

  private static int numSystems = 1;
  public static synchronized int getNumSystems() {return numSystems;}
  
  public static synchronized void runOneStep() {
    //runOneStepStart();

      //System.out.println("Step " + CANTStep);
    Enumeration <?> eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      xorNet net = (xorNet)eNum.nextElement();
      net.zeroIncomingStrengths();
    }
    updateIncomingStrengths();

    eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      xorNet net = (xorNet)eNum.nextElement();
    if (net.getName().compareTo("BaseNet") == 0)
      {
      net.runAllOneStep(CANTStep); 
      CANTStep++;
      }
    }
   
    //System.out.println("Incremenet cantvis1step"+CANTStep);

  if (experiment.experimentDone(CANTStep)) 
    {
    //System.out.println("experiment done "+CANTStep);
    closeSystem();
  	numSystems++;
  	readNewSystem();
  	//makeNewSystem(numSystems);
    }
  }
  
  
 private  static void positionWindows() {
    xorNet baseNet = (xorNet)experiment.getNet("BaseNet");
    xorNet outputNet = (xorNet)experiment.getNet("OutputNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,300);
    baseNet.cantFrame.matrix.addStringsToPrint ("Energy",30,150);
    baseNet.cantFrame.matrix.addStringsToPrint ("A",100);
    baseNet.cantFrame.matrix.addStringsToPrint ("B",170);
    baseNet.cantFrame.setVisible(true);
    
    if (fWorks) {
      xorNet gasNet = (xorNet)experiment.getNet("GasNet");
  
      gasNet.cantFrame.setLocation(500,0);
      gasNet.cantFrame.setSize (300,300);
      gasNet.cantFrame.setVisible(true);
    }
   
    outputNet.cantFrame.setLocation(0,300);
    outputNet.cantFrame.setSize (300,300);
    outputNet.cantFrame.matrix.addStringsToPrint ("No",30,150);
    outputNet.cantFrame.matrix.addStringsToPrint ("Yes",100);
    outputNet.cantFrame.setVisible(true);
  }
  
  //embedded Thread class
  public static class WorkerThread extends CANT23.WorkerThread{
    public void run(){
      //System.out.println("xor Thread ");
      while(true){
         if(isRunning){
           runOneStep();
         }
         else{
           try{sleep(delayBetweenSteps);}
		   catch(InterruptedException ie){ie.printStackTrace();}
             }//else
       }//while
    }//run
  }//WorkerThread class
}