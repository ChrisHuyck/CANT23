
public class MuddyExperiment extends CANTExperiment {
  public int correctAnswer = -1;

  public MuddyExperiment () {
	trainingLength = 1; 
    inTest = false;
  }
  
 
  public boolean experimentDone(int CANTStep) {
    return false;
  }
  
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
    MuddyNet  fact1Net = (MuddyNet)getNet("BaseNet");
    MuddyNet  fact2Net = (MuddyNet)getNet("Fact2Net");
    MuddyNet  fact3Net = (MuddyNet)getNet("Fact3Net");
    MuddyNet  rule1Net = (MuddyNet)getNet("Rule1Net");
    MuddyNet  rule2Net = (MuddyNet)getNet("Rule2Net");
    MuddyNet  rule3Net = (MuddyNet)getNet("Rule3Net");
    fact1Net.setNeuronsToStimulate(0);    
    fact2Net.setNeuronsToStimulate(0);    
    fact3Net.setNeuronsToStimulate(0);    
    rule1Net.setNeuronsToStimulate(0);
    rule2Net.setNeuronsToStimulate(0);
    rule3Net.setNeuronsToStimulate(0);
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

