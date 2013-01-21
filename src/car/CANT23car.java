

import java.util.*;

public class CANT23car extends CANT23{
  public static String ExperimentXMLFile;
  public static carNet nullNet;
  public static carExperiment experiment;
  public static patternReader patReader;
  public static int kludge = 0;
  
  public static void main(String args[]){
System.out.println("initialize CANT car");
    ExperimentXMLFile = "src/car/car.xml";
    seed = 1;
    initRandom();
    readNewSystem();
    positionWindows();
    delayBetweenSteps=0;
    patReader = new patternReader("src/car/train10data");
    //patReader = new patternReader("src/car/traindata");
  }

  protected static void readNewSystem() {
    //System.out.println("readNewSystem");
    nullNet = new carNet();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new CANT23car.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    carNet net = (carNet) experiment.getNet("BaseNet");
    workerThread.start();	
    connectAllNets();	
    }
  
  private static void connectAllNets() {
    carNet baseNet = (carNet)experiment.getNet("BaseNet");
    carNet outputNet = (carNet)experiment.getNet("OutputNet");
	
	carNet gasNet = (carNet)experiment.getNet("GasNet");
	baseNet.connectBaseToGasNet(gasNet);
	//gasNet.connectGasToBaseNet(baseNet);
	gasNet.connectGasToOutputNet(outputNet);
	outputNet.connectOutputToGasNet(gasNet);
  }


  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    experiment = new carExperiment();
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
      carNet net = (carNet)eNum.nextElement();
      net.setInitialFatigue(0.0);
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
      carNet net = (carNet)eNum.nextElement();
      net.zeroIncomingStrengths();
    }
    updateIncomingStrengths();

    eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      carNet net = (carNet)eNum.nextElement();
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
    carNet baseNet = (carNet)experiment.getNet("BaseNet");
    carNet outputNet = (carNet)experiment.getNet("OutputNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,300);
    baseNet.cantFrame.matrix.addStringsToPrint ("Buying",30,200);
    baseNet.cantFrame.matrix.addStringsToPrint ("Maint",50);
    baseNet.cantFrame.matrix.addStringsToPrint ("doors",70);
    baseNet.cantFrame.matrix.addStringsToPrint ("persons",90);
    baseNet.cantFrame.matrix.addStringsToPrint ("log_boot",110);
    baseNet.cantFrame.matrix.addStringsToPrint ("safety",130);
    baseNet.cantFrame.setVisible(true);
    
    carNet gasNet = (carNet)experiment.getNet("GasNet");
    gasNet.cantFrame.setLocation(500,0);
    gasNet.cantFrame.setSize (300,300);
    gasNet.cantFrame.setVisible(true);
   
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