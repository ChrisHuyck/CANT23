import java.util.*;

public class car3CANT extends CANT23{
  public static String ExperimentXMLFile;
  public static car3Net nullNet;
  public static car3Experiment experiment; 
  public static patternReader patReader;
  public static String trainNetName = "src/car2/train20data";

  public static int kludge = 3;
  
  public static void main(String args[]){
System.out.println("initialize CANT car3");
    ExperimentXMLFile = "src/car3/car3.xml";
    seed = 1;
    initRandom();
    readNewSystem();
    positionWindows();
    delayBetweenSteps=0;
    patReader = new patternReader(trainNetName);
  }

  protected static void readNewSystem() {
    //System.out.println("readNewSystem");
    nullNet = new car3Net();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new car3CANT.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    workerThread.start();	
    connectAllNets();	
    
    car3Net somNet = (car3Net) experiment.getNet("SomNet");
    somNet.zeroIncomingStrengths();
    updateIncomingStrengths();
    somNet.setCyclesPerRun(20);
    }
  
  private static void connectAllNets() {
    car3Net baseNet = (car3Net)experiment.getNet("BaseNet");
	car3Net somNet = (car3Net)experiment.getNet("SomNet");
	car3Net outputNet = (car3Net)experiment.getNet("OutputNet");
    
	baseNet.connectBaseToSomNet(somNet);
	somNet.connectSomToOutputNet(outputNet);
	outputNet.connectOutputToSomNet(somNet);
  }


  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    experiment = new car3Experiment();
    //System.out.println("initialize car3 Experiment");
    experiment.printExpName();
  }

  
private static void updateIncomingStrengths() {
  //loop through all nets
  //Enumeration <?> eNum = CANT23.nets.elements();
  //while (eNum.hasMoreElements()) {
    //CANTNet net = (CANTNet)eNum.nextElement();

	//consider if input is also learning with post compensatory need to fix this.
    car3Net net = (car3Net)experiment.getNet("SomNet");

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
//  }
}

  public static void runOneStepStart() {
    if (experiment.trainingLength == CANTStep ) 
    	experiment.switchToTest();
    
    //if (experiment.getInTest()) 
    	experiment.measure(CANTStep);
 
    if (experiment.isEndEpoch(CANTStep))
      experiment.endEpoch();
  }
  
  public static synchronized void resetForNewTest() {
	if(!experiment.getInTest()) return;

	Enumeration <?> eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      car3Net net = (car3Net)eNum.nextElement();
      //      net.setInitialFatigue(-1.0);
      for (int i = 0; i < net.getSize(); i++) {
 	     net.neurons[i].setFatigue((float)0.0);
	     net.neurons[i].setActivation(0.0);
      }
    }
  }

  private static int numSystems = 1;
  public static synchronized int getNumSystems() {return numSystems;}
  
  public static synchronized void runOneStep() {
    //runOneStepStart();

      //System.out.println("Step " + CANTStep);
	  
	//this assumes only somNet has fatiguing neurons.
    car3Net somNet = (car3Net)experiment.getNet("SomNet");
    somNet.zeroIncomingStrengths();
    updateIncomingStrengths();

    Enumeration <?> eNum = nets.elements();
    eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      car3Net net = (car3Net)eNum.nextElement();
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
    car3Net baseNet = (car3Net)experiment.getNet("BaseNet");
    car3Net somNet = (car3Net)experiment.getNet("SomNet");
    car3Net outputNet = (car3Net)experiment.getNet("OutputNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,400);
    baseNet.cantFrame.setVisible(true);  
    
  
    somNet.cantFrame.setLocation(500,0);
    somNet.cantFrame.setSize (300,400);
    somNet.cantFrame.matrix.addStringsToPrint ("0",30,180);
    somNet.cantFrame.matrix.addStringsToPrint ("1",50);
    somNet.cantFrame.setVisible(true);
    
    outputNet.cantFrame.setLocation(800,0);
    outputNet.cantFrame.setSize (300,400);
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
