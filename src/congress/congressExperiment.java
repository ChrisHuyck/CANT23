
//run multiple tests

public class congressExperiment extends CANTExperiment {
  public int correctAnswer = -1;

  public congressExperiment () {
    trainingLength = 109*20*2;//data items * length of epoch * rehearsal 
    inTest = false;
  }
  
  private int correctTest = 0;
  private int incorrectTest = 0; 
  
  public boolean experimentDone(int CANTStep) {
	/*if ((correct+inCorrect) >= 100) 
		{
        System.out.println(CANT23congress.getNumSystems() + " Yes " + correct + " No " + inCorrect);

		return true;
		}
		*/
    return false;
  }
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
    
    congressNet  outputNet = (congressNet)getNet("OutputNet");
    outputNet.setNeuronsToStimulate(0);
    outputNet.setLearningOn(0);
    congressNet  inputNet = (congressNet)getNet("BaseNet");
    inputNet.setLearningOn(0);
    congressNet  gasNet = (congressNet)getNet("GasNet");
    gasNet.setLearningOn(0);
    
    CANT23congress.resetForNewTest();
    CANT23congress.patReader.switchReadFile("src/congress/train2.txt");
  }
  
  public boolean isEndEpoch(int currentStep) {
    if ((currentStep%20) == 0 ) return (true);
    return (false);   
}

	public void endEpoch() {
		congressNet outputNet = (congressNet) getNet("OutputNet");
		congressNet baseNet = (congressNet) getNet("BaseNet");

		
		int epoch = (CANT23.CANTStep / 20);
		// System.out.println("End Epoch " + CANT23.CANTStep + " " + epoch);
		CANT23congress.patReader.setCongressPattern(baseNet, outputNet);
		baseNet.setNeuronsToStimulate(750);
		if (inTest) {
			outputNet.setNeuronsToStimulate(0);
  		    //CANT23congress.resetForNewTest();
  		    }
		else
			outputNet.setNeuronsToStimulate(200);
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

 
    private int reps = 0;
    private int dems = 0;
	
	public void measure(int CANTStep) {
		congressNet outputNet = (congressNet) getNet("OutputNet");

		for (int i = 0; i < 200; i++) {
			if (outputNet.neurons[i].getFired()) {
				reps++;
			}
			if (outputNet.neurons[i + 200].getFired()) {
				dems++;
			}
		}
		// if (inTest) {
		// System.out.println("Step " + CANTStep);
		if (isEndEpoch(CANTStep)) {
		  int ans;
		  if (reps >= dems) ans = 0;
		  else ans = 1;

		  if (correctAnswer == ans) correctTest++;
		  else incorrectTest ++;

		  System.out.println(CANTStep + " " + reps + " " + dems + " " +  correctAnswer + 
				  " " +  ans + " " +  correctTest + " " + incorrectTest);
		  reps = dems = 0;	  
		}
	}

  public void printExpName () {
    //System.out.println("congress");
  }
}

