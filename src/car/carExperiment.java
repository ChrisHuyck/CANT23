
//run multiple tests

public class carExperiment extends CANTExperiment {
  
  public carExperiment () {
    trainingLength = 432*20*2;//data items * length of epoch * rehearsal 
    inTest = false;
  }
  
  private int correctTest = 0;
  private int incorrectTest = 0; 
  
  public boolean experimentDone(int CANTStep) {
	/*if ((correct+inCorrect) >= 100) 
		{
        System.out.println(CANT23car.getNumSystems() + " Yes " + correct + " No " + inCorrect);

		return true;
		}
		*/
    return false;
  }
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
    
    carNet  outputNet = (carNet)getNet("OutputNet");
    outputNet.setNeuronsToStimulate(0);
    outputNet.setLearningOn(0);
    carNet  inputNet = (carNet)getNet("BaseNet");
    inputNet.setLearningOn(0);
    carNet  gasNet = (carNet)getNet("GasNet");
    gasNet.setLearningOn(0);
    
    CANT23car.resetForNewTest();
    CANT23car.patReader.switchReadFile("src/car/train10data");
    //CANT23car.patReader.switchReadFile("src/car/testdata");
  }
  
  public boolean isEndEpoch(int currentStep) {
    if ((currentStep%20) == 0 ) return (true);
    return (false);   
}

	public void endEpoch() {
		carNet outputNet = (carNet) getNet("OutputNet");
		carNet baseNet = (carNet) getNet("BaseNet");

		
		int epoch = (CANT23.CANTStep / 20);
		// System.out.println("End Epoch " + CANT23.CANTStep + " " + epoch);
		CANT23car.patReader.setPattern(baseNet, outputNet,CANT23car.experiment);
		baseNet.setNeuronsToStimulate(180);
		if (inTest) {
			outputNet.setNeuronsToStimulate(0);
  		    //CANT23car.resetForNewTest();
  		    }
		else
			outputNet.setNeuronsToStimulate(100);
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

 
    private int unaccs = 0;
    private int accs = 0;
    private int goods = 0;
    private int vgoods = 0;
	
	public void measure(int CANTStep) {
		carNet outputNet = (carNet) getNet("OutputNet");

		for (int i = 0; i < 100; i++) {
			if (outputNet.neurons[i].getFired()) {
				unaccs++;
			}
			if (outputNet.neurons[i + 100].getFired()) {
				accs++;
			}
			if (outputNet.neurons[i + 200].getFired()) {
				goods++;
			}
			if (outputNet.neurons[i + 300].getFired()) {
				vgoods++;
			}
		}
		// if (inTest) {
		// System.out.println("Step " + CANTStep);
		if (isEndEpoch(CANTStep)) {
		  int ans;
		  if ((unaccs >= accs) && (unaccs >= goods ) && (unaccs >= vgoods)) ans = 0;
		  else if ((accs > unaccs) && (accs >= goods ) && (accs >= vgoods)) ans = 1;
		  else if ((goods > unaccs) && (goods > accs) && (goods >= vgoods)) ans = 2;
		  else if ((vgoods > unaccs) && (vgoods > accs) && (vgoods > goods)) ans = 3;
		  else ans = -1;

		  if (correctAnswer == ans) correctTest++;
		  else incorrectTest ++;

		  System.out.println(CANTStep + " " + unaccs + " " + accs + " " + goods + " " +
				  vgoods + " " + correctAnswer + " " + ans + " " +
				  correctTest + " " + incorrectTest);
		  unaccs = accs = goods = vgoods = 0;	  
		}
	}

  public void printExpName () {
    //System.out.println("car");
  }

}