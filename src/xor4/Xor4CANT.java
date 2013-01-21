



import java.util.*;

public class Xor4CANT extends CANT23{
  public static boolean learnWhileTesting = false;
  public static int  spontaneousOnlyCycles = 0;
	
  public static String ExperimentXMLFile;
  public static xor4Net nullNet;
  public static xor4Experiment experiment; 
  public static boolean fWorks;  //this is the switch between 2 (failing)
                                 //and 3 (working?) nets.  Set in main.
  public static int inputModel; //0 no input, 1, 1 CA at a time, 2
                                //xor train, and 3 xor test.
  public static int kludge = 1;
  
  public static void main(String args[]){
System.out.println("initialize CANT xor4");
    fWorks = true;
    //fWorks = false;
    if (fWorks) 
      ExperimentXMLFile = "src/xor4/xor4.xml";
    else 
      ExperimentXMLFile = "xorFail.xml";
    seed = 1;
    initRandom();
    readNewSystem();
    positionWindows();
    delayBetweenSteps=0;
  }

  protected static void readNewSystem() {
    //System.out.println("readNewSystem");
    nullNet = new xor4Net();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new Xor4CANT.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    xor4Net net = (xor4Net) experiment.getNet("BaseNet");
    workerThread.start();	
    connectAllNets();	
    inputModel = 2;
    }
  
  private static void connectAllNets() {
    xor4Net baseNet = (xor4Net)experiment.getNet("BaseNet");
    xor4Net outputNet = (xor4Net)experiment.getNet("OutputNet");
	
    if (fWorks) {
      xor4Net gasNet = (xor4Net)experiment.getNet("GasNet");
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
    experiment = new xor4Experiment();
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
    if (experiment.trainingLength == (CANTStep + spontaneousOnlyCycles)) 
    	experiment.switchToTest();
    if ((spontaneousOnlyCycles > 0) && (experiment.trainingLength == CANTStep))
    	experiment.switchToSpontaneousOnly();
    
    //if (experiment.getInTest()) 
    	experiment.measure(CANTStep);
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
      xor4Net net = (xor4Net)eNum.nextElement();
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
      xor4Net net = (xor4Net)eNum.nextElement();
      net.zeroIncomingStrengths();
    }
    updateIncomingStrengths();

    eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      xor4Net net = (xor4Net)eNum.nextElement();
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
    xor4Net baseNet = (xor4Net)experiment.getNet("BaseNet");
    xor4Net outputNet = (xor4Net)experiment.getNet("OutputNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,300);
    baseNet.cantFrame.matrix.addStringsToPrint ("Energy",30,150);
    baseNet.cantFrame.matrix.addStringsToPrint ("A",100);
    baseNet.cantFrame.matrix.addStringsToPrint ("B",170);
    baseNet.cantFrame.setVisible(true);
    
    if (fWorks) {
      xor4Net gasNet = (xor4Net)experiment.getNet("GasNet");
  
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