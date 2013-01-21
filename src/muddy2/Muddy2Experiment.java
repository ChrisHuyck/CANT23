

public class Muddy2Experiment extends CANTExperiment {
  public int correctAnswer = -1;

  public Muddy2Experiment () {
	trainingLength = 1; 
    inTest = false;
  }
  
 
  public boolean experimentDone(int CANTStep) {
    return false;
  }
  
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
    Muddy2Net  fact1Net = (Muddy2Net)getNet("BaseNet");
    Muddy2Net  fact2Net = (Muddy2Net)getNet("Fact2Net");
    Muddy2Net  fact3Net = (Muddy2Net)getNet("Fact3Net");
    Muddy2Net  rule1Net = (Muddy2Net)getNet("Rule1Net");
    Muddy2Net  rule2Net = (Muddy2Net)getNet("Rule2Net");
    Muddy2Net  rule3Net = (Muddy2Net)getNet("Rule3Net");
    Muddy2Net  clockNet = (Muddy2Net)getNet("ClockNet");
    fact1Net.setNeuronsToStimulate(0);    
    fact2Net.setNeuronsToStimulate(0);    
    fact3Net.setNeuronsToStimulate(0);    
    rule1Net.setNeuronsToStimulate(0);
    rule2Net.setNeuronsToStimulate(0);
    rule3Net.setNeuronsToStimulate(0);
    clockNet.setNeuronsToStimulate(0);
  }
  
  public boolean isEndEpoch(int currentStep) {
       return (false);   
}
 
    /*  private void runAgain() {
   System.out.println("Results of Sentence " + sentence + " " + CANT23.CANTStep);
   printSymbolicResult();
  	clearFastBindNeurons();
	clearAllNets();
	currentWord = -2 ;
	CANT23.CANTStep=-1;
	cyclePushLastFinished = 0;
    CANT23.setRunning(true);
  }
    */

 
	
	public void measure(int CANTStep) {
	}

  public void printExpName () {
    //System.out.println("congress");
  }
}


