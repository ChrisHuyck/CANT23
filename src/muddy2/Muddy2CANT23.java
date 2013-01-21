

import java.util.*;

public class Muddy2CANT23 extends CANT23{
  public static String ExperimentXMLFile;
  public static Muddy2Net nullNet;
  public static Muddy2Experiment experiment;
  public static int kludge = 0;
  
  public static void main(String args[]){
System.out.println("initialize CANT Muddy");
    ExperimentXMLFile = "src/muddy2/muddy2.xml";
    seed = 1;
    initRandom();
    readNewSystem();
    positionWindows();
    delayBetweenSteps=10;
  }

  protected static void readNewSystem() {
    //System.out.println("readNewSystem");
    nullNet = new Muddy2Net();
	
    nets = NetManager.readNets(ExperimentXMLFile,nullNet);
    workerThread = new Muddy2CANT23.WorkerThread();
    initializeExperiment();
    experiment.printExpName();
    Muddy2Net net = (Muddy2Net) experiment.getNet("BaseNet");
    workerThread.start();	
    connectAllNets();	
    }
  
  private static void connectAllNets() {
    Muddy2Net fact1Net = (Muddy2Net)experiment.getNet("BaseNet");
    Muddy2Net fact2Net = (Muddy2Net)experiment.getNet("Fact2Net");
    Muddy2Net fact3Net = (Muddy2Net)experiment.getNet("Fact3Net");
	Muddy2Net rule1Net = (Muddy2Net)experiment.getNet("Rule1Net");
	Muddy2Net rule2Net = (Muddy2Net)experiment.getNet("Rule2Net");
	Muddy2Net rule3Net = (Muddy2Net)experiment.getNet("Rule3Net");
	Muddy2Net clockNet = (Muddy2Net)experiment.getNet("ClockNet");
	
	fact1Net.connectFactToRuleNet(rule1Net); 
	fact2Net.connectFactToRuleNet(rule2Net); 
	fact3Net.connectFactToRuleNet(rule3Net); 
	rule1Net.connectRuleToFactNet(fact1Net); 
	rule2Net.connectRuleToFactNet(fact2Net); 
	rule3Net.connectRuleToFactNet(fact3Net); 
	
	clockNet.connectClockToRuleNet(rule1Net);
	clockNet.connectClockToRuleNet(rule2Net);
	clockNet.connectClockToRuleNet(rule3Net);
	
	fact2Net.connectFactToOtherRuleNet(rule1Net); 
	fact3Net.connectFactToOtherRuleNet(rule1Net); 
	fact1Net.connectFactToOtherRuleNet(rule2Net); 
	fact3Net.connectFactToOtherRuleNet(rule2Net); 
	fact1Net.connectFactToOtherRuleNet(rule3Net); 
	fact2Net.connectFactToOtherRuleNet(rule3Net); 

  }


  //set up the experiment specific parameters.
  private static void initializeExperiment() {
    experiment = new Muddy2Experiment();
    System.out.println("initialize muddy Experiment");
    experiment.printExpName();
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
  
  private static int numSystems = 1;
  public static synchronized int getNumSystems() {return numSystems;}
  
  public static synchronized void runOneStep() {
    //runOneStepStart();

      //System.out.println("Step " + CANTStep);
    Enumeration <?> eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
    }

    eNum = nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
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
    Muddy2Net fact1Net = (Muddy2Net)experiment.getNet("BaseNet");
    Muddy2Net fact2Net = (Muddy2Net)experiment.getNet("Fact2Net");
    Muddy2Net fact3Net = (Muddy2Net)experiment.getNet("Fact3Net");
    Muddy2Net rule1Net = (Muddy2Net)experiment.getNet("Rule1Net");
    Muddy2Net rule2Net = (Muddy2Net)experiment.getNet("Rule2Net");
    Muddy2Net rule3Net = (Muddy2Net)experiment.getNet("Rule3Net");
    Muddy2Net clockNet = (Muddy2Net)experiment.getNet("ClockNet");
    
    fact1Net.cantFrame.setLocation(0,0);
    fact1Net.cantFrame.setSize (500,300);
    fact1Net.cantFrame.matrix.addStringsToPrint ("p1",20,200);
    fact1Net.cantFrame.matrix.addStringsToPrint ("q1 - q3",30);
    fact1Net.cantFrame.matrix.addStringsToPrint ("p2 -p2",50);
    fact1Net.cantFrame.matrix.addStringsToPrint ("p3 -p3",60);
    fact1Net.cantFrame.matrix.addStringsToPrint ("q1+ q2+",70);
    fact1Net.cantFrame.matrix.addStringsToPrint ("-K1p1",80);
    fact1Net.cantFrame.setVisible(true);
    
    fact2Net.cantFrame.setLocation(0,300);
    fact2Net.cantFrame.setSize (300,300);
    fact2Net.cantFrame.matrix.addStringsToPrint ("p2",20,200);
    fact2Net.cantFrame.matrix.addStringsToPrint ("q1 - q3",30);
    fact2Net.cantFrame.matrix.addStringsToPrint ("p1 -p1",50);
    fact2Net.cantFrame.matrix.addStringsToPrint ("p3 -p3",60);
    fact2Net.cantFrame.matrix.addStringsToPrint ("q1+ q2+",70);
    fact2Net.cantFrame.matrix.addStringsToPrint ("-K2p2",80);
    fact2Net.cantFrame.setVisible(true);
    
    fact3Net.cantFrame.setLocation(0,600);
    fact3Net.cantFrame.setSize (300,300);
    fact3Net.cantFrame.matrix.addStringsToPrint ("p3",20,200);
    fact3Net.cantFrame.matrix.addStringsToPrint ("q1 - q3",30);
    fact3Net.cantFrame.matrix.addStringsToPrint ("p1 -p1",50);
    fact3Net.cantFrame.matrix.addStringsToPrint ("p2 -p2",60);
    fact3Net.cantFrame.matrix.addStringsToPrint ("q1+ q2+",70);
    fact3Net.cantFrame.matrix.addStringsToPrint ("-K3p3",80);
    fact3Net.cantFrame.setVisible(true);
    
    rule1Net.cantFrame.setLocation(500,0);
    rule1Net.cantFrame.setSize (300,300);
    rule1Net.cantFrame.matrix.addStringsToPrint ("Rules On",20,200);
    rule1Net.cantFrame.matrix.addStringsToPrint ("Rule 1-4",40);
    rule1Net.cantFrame.matrix.addStringsToPrint ("Rule 5-",65);
    rule1Net.cantFrame.setVisible(true);
    
    rule2Net.cantFrame.setLocation(500,300);
    rule2Net.cantFrame.setSize (300,300);
    rule2Net.cantFrame.matrix.addStringsToPrint ("Rules On",20,200);
    rule2Net.cantFrame.matrix.addStringsToPrint ("Rule 1-4",50);
    rule2Net.cantFrame.matrix.addStringsToPrint ("Rule 5-",65);
    rule2Net.cantFrame.setVisible(true);

    rule3Net.cantFrame.setLocation(500,600);
    rule3Net.cantFrame.setSize (300,300);
    rule3Net.cantFrame.matrix.addStringsToPrint ("Rules On",20,200);
    rule3Net.cantFrame.matrix.addStringsToPrint ("Rule 1-4",50);
    rule3Net.cantFrame.matrix.addStringsToPrint ("Rule 5-",65);
    rule3Net.cantFrame.setVisible(true);

    clockNet.cantFrame.setLocation(800,0);
    clockNet.cantFrame.setSize (300,300);
    clockNet.cantFrame.matrix.addStringsToPrint ("Start",20,200);
    clockNet.cantFrame.setVisible(true);
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

