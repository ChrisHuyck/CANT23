



import java.util.*;

public class som1CANT extends CANT23{
  public static String ExperimentXMLFile;
  public static som1Net nullNet;
  public static som1Experiment experiment; 
  public static int kludge = 3;
  
  public static void main(String args[]){
System.out.println("initialize CANT som1");
    ExperimentXMLFile = "src/som1/som1.xml";
    seed = 1;
    initRandom();
    readNewSystem();
    positionWindows();
    delayBetweenSteps=0;
  }

  protected static void readNewSystem() {
    //System.out.println("readNewSystem");
    nullNet = new som1Net();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new som1CANT.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    workerThread.start();	
    connectAllNets();	
    
    som1Net somNet = (som1Net) experiment.getNet("SomNet");
    somNet.zeroIncomingStrengths();
    updateIncomingStrengths();
    somNet.setCyclesPerRun(20);
    }
  
  private static void connectAllNets() {
    som1Net baseNet = (som1Net)experiment.getNet("BaseNet");
	som1Net somNet = (som1Net)experiment.getNet("SomNet");
    
	baseNet.connectBaseToSomNet(somNet);
  }


  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    experiment = new som1Experiment();
    //System.out.println("initialize som1 Experiment");
    experiment.printExpName();
  }

  
private static void updateIncomingStrengths() {
  //loop through all nets
  //Enumeration <?> eNum = CANT23.nets.elements();
  //while (eNum.hasMoreElements()) {
    //CANTNet net = (CANTNet)eNum.nextElement();

	//consider if input is also learning with post compensatory need to fix this.
    som1Net net = (som1Net)experiment.getNet("SomNet");

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
      som1Net net = (som1Net)eNum.nextElement();
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
	  
	//this assumes only somNet has fatiguing neurons.
    som1Net somNet = (som1Net)experiment.getNet("SomNet");
    somNet.zeroIncomingStrengths();
    updateIncomingStrengths();

    Enumeration <?> eNum = nets.elements();
    eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
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
    som1Net baseNet = (som1Net)experiment.getNet("BaseNet");
    som1Net somNet = (som1Net)experiment.getNet("SomNet");
  
    baseNet.cantFrame.setLocation(0,0);
    baseNet.cantFrame.setSize (500,300);
    baseNet.cantFrame.setVisible(true);  
    
  
    somNet.cantFrame.setLocation(500,0);
    somNet.cantFrame.setSize (400,400);
    somNet.cantFrame.matrix.addStringsToPrint ("0",30,180);
    somNet.cantFrame.matrix.addStringsToPrint ("1",50);
    somNet.cantFrame.matrix.addStringsToPrint ("2",80);
    somNet.cantFrame.matrix.addStringsToPrint ("3",100);
    somNet.cantFrame.matrix.addStringsToPrint ("4",130);
    somNet.cantFrame.matrix.addStringsToPrint ("5",150);
    somNet.cantFrame.matrix.addStringsToPrint ("6",170);
    somNet.cantFrame.matrix.addStringsToPrint ("7",200);
    somNet.cantFrame.matrix.addStringsToPrint ("8",220);
    somNet.cantFrame.matrix.addStringsToPrint ("9",250);
    somNet.cantFrame.setVisible(true);  
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