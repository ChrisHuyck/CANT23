
//run multiple tests

public class xor3Experiment extends CANTExperiment {

  public xor3Experiment () {
    trainingLength = 20000; 
    inTest = false;
  }
  public boolean experimentDone(int CANTStep) {
	if ((correct+inCorrect) >= 100) 
		{
        System.out.println(Xor3CANT.getNumSystems() + " Yes " + correct + " No " + inCorrect);

		return true;
		}
    return false;
  }
  
	public void switchToSpontaneousOnly() {
		xor3Net gasNet = (xor3Net) getNet("GasNet");
		xor3Net outputNet = (xor3Net) getNet("OutputNet");
		xor3Net inputNet = (xor3Net) getNet("BaseNet");

		//System.out.println("swithctoSpont ");
		Xor3CANT.inputModel = 3;
		outputNet.setNeuronsToStimulate(0);
		inputNet.setNeuronsToStimulate(0);
		gasNet.setNeuronsToStimulate(0);
	}

  public void switchToTest () {
    //System.out.println("swithctotest ");
    inTest = true;
    Xor3CANT.inputModel = 3;
    xor3Net  outputNet = (xor3Net)getNet("OutputNet");
    outputNet.setNeuronsToStimulate(0);
    if (!Xor3CANT.learnWhileTesting)
      outputNet.setLearningOn(0);
    xor3Net  inputNet = (xor3Net)getNet("BaseNet");
    if (!Xor3CANT.learnWhileTesting)
      inputNet.setLearningOn(0);
    if (Xor3CANT.fWorks) {
      xor3Net  gasNet = (xor3Net)getNet("GasNet");
      if (!Xor3CANT.learnWhileTesting)
        gasNet.setLearningOn(0);
    }
  }
  
  public boolean isEndEpoch(int currentStep) {
    if ((currentStep%20) == 0 ) return (true);
    return (false);
    
}
  public void endEpoch() {
    xor3Net  outputNet = (xor3Net)getNet("OutputNet");
    xor3Net  baseNet = (xor3Net)getNet("BaseNet");

    //Xor3CANT.resetForNewTest();

    if (Xor3CANT.inputModel == 0)
      {
      baseNet.setNeuronsToStimulate(0);
      outputNet.setNeuronsToStimulate(0);
      return;
      }

    int epoch = (CANT23.CANTStep / 20);
    //System.out.println("End Epoch "  + CANT23.CANTStep + " " + epoch);
    
    if (Xor3CANT.inputModel == 1) {
      int patternNum = epoch %5;
      if (patternNum < 3) {
        baseNet.setNeuronsToStimulate(100);
        baseNet.setCurrentPattern(patternNum);
        CANTPattern newPattern=((CANTPattern)baseNet.patterns.get(patternNum));
        newPattern.arrange(baseNet.getNeuronsToStimulate());
        outputNet.setNeuronsToStimulate(0);
      }
      else {
        patternNum -= 3;
        baseNet.setNeuronsToStimulate(0);
        outputNet.setNeuronsToStimulate(100);
        outputNet.setCurrentPattern(patternNum);
        CANTPattern newPattern = 
          ((CANTPattern)outputNet.patterns.get(patternNum));
        newPattern.arrange(outputNet.getNeuronsToStimulate());
      }
    }
    else if (Xor3CANT.inputModel == 2) {
      int epochType = epoch %4;
      int inputNeuronsToStimulate = 600;
      int outputNeuronsToStimulate = 400;
      if (epochType == 0) { //3-0
        baseNet.setNeuronsToStimulate(inputNeuronsToStimulate);
        outputNet.setNeuronsToStimulate(outputNeuronsToStimulate);
        baseNet.setCurrentPattern(3);
        CANTPattern newPattern=((CANTPattern)baseNet.patterns.get(3));
        newPattern.arrange(baseNet.getNeuronsToStimulate());
        outputNet.setCurrentPattern(0);
        newPattern=((CANTPattern)outputNet.patterns.get(0));
        newPattern.arrange(outputNet.getNeuronsToStimulate());
      }
      else if (epochType == 1) { //4-1
        baseNet.setNeuronsToStimulate(inputNeuronsToStimulate);
        outputNet.setNeuronsToStimulate(outputNeuronsToStimulate);
        baseNet.setCurrentPattern(4);
        CANTPattern newPattern=((CANTPattern)baseNet.patterns.get(4));
        newPattern.arrange(baseNet.getNeuronsToStimulate());
        outputNet.setCurrentPattern(1);
        newPattern=((CANTPattern)outputNet.patterns.get(1));
        newPattern.arrange(outputNet.getNeuronsToStimulate());
      }
      else if (epochType == 2) { //5-1
        baseNet.setNeuronsToStimulate(inputNeuronsToStimulate);
        outputNet.setNeuronsToStimulate(outputNeuronsToStimulate);
        baseNet.setCurrentPattern(5);
        CANTPattern newPattern=((CANTPattern)baseNet.patterns.get(5));
        newPattern.arrange(baseNet.getNeuronsToStimulate());
        outputNet.setCurrentPattern(1);
        newPattern=((CANTPattern)outputNet.patterns.get(1));
        newPattern.arrange(outputNet.getNeuronsToStimulate());
      }
      else { //0-0
        baseNet.setNeuronsToStimulate(inputNeuronsToStimulate);
        outputNet.setNeuronsToStimulate(outputNeuronsToStimulate);
        baseNet.setCurrentPattern(0);
        CANTPattern newPattern=((CANTPattern)baseNet.patterns.get(0));
        newPattern.arrange(baseNet.getNeuronsToStimulate());
        outputNet.setCurrentPattern(0);
        newPattern=((CANTPattern)outputNet.patterns.get(0));
        newPattern.arrange(outputNet.getNeuronsToStimulate());
      }
    }
    else if (Xor3CANT.inputModel == 3) {
      outputNet.setNeuronsToStimulate(0);
      int epochType = epoch %4;
      if (epochType == 0) { //3-0
    	baseNet.setNeuronsToStimulate(600);
        baseNet.setCurrentPattern(3);
        CANTPattern newPattern=((CANTPattern)baseNet.patterns.get(3));
        newPattern.arrange(baseNet.getNeuronsToStimulate());
      }
      else if (epochType == 1) { //4-1
    	baseNet.setNeuronsToStimulate(400);
        baseNet.setCurrentPattern(4);
        CANTPattern newPattern=((CANTPattern)baseNet.patterns.get(4));
        newPattern.arrange(baseNet.getNeuronsToStimulate());
      }
      else if (epochType == 2) { //5-1
      	baseNet.setNeuronsToStimulate(400);
        baseNet.setCurrentPattern(5);
        CANTPattern newPattern=((CANTPattern)baseNet.patterns.get(5));
        newPattern.arrange(baseNet.getNeuronsToStimulate());
      }
      else { //0-0
      	baseNet.setNeuronsToStimulate(200);
        baseNet.setCurrentPattern(0);
        CANTPattern newPattern=((CANTPattern)baseNet.patterns.get(0));
        newPattern.arrange(baseNet.getNeuronsToStimulate());
      }
    }
    else 
      System.out.println("Bad inputModel "  + CANT23.CANTStep);
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

  public double printConnectionWeights(int step, String netName) {
	double totWeight = 0;
	int posNeurons = 0;
	xor3Net net = (xor3Net) getNet(netName);
    
	for (int i = 0; i < net.getSize(); i++) {
	  if (!net.neurons[i].isInhibitory()) {
		posNeurons++;
	    for (int synapse=0;synapse < net.neurons[i].getCurrentSynapses(); synapse++) {
		  totWeight += net.neurons[i].synapses[synapse].getWeight(); 
	    }    	   
	  }
	}
	return totWeight/posNeurons;
	//System.out.println("Weights " + step + " " + totWeight/totSynapses);
  }
  
  
  private double getAvgWeights(int fromCA,int toCA,int fromNetRef,
    int toNetRef) {
    xor3Net baseNet = (xor3Net) getNet("BaseNet");
    xor3Net outputNet = (xor3Net) getNet("OutputNet");
    xor3Net fromNet;
    xor3Net toNet;
    int fromStart;
    int toStart;
    int CASize = 200;
    double totWeight = 0;
    int totSynapses = 0;

    if (fromNetRef == 0) fromNet= baseNet;
    else fromNet = outputNet;
    if (toNetRef == 0) toNet= baseNet;
    else toNet = outputNet;

    fromStart = fromCA*CASize;
    toStart = toCA*CASize;

    for (int i = fromStart; i < fromStart+CASize; i++) {
      if (!fromNet.neurons[i].isInhibitory()) {
	    for (int synapse=0;synapse < fromNet.neurons[i].getCurrentSynapses();
	      synapse++) {
          if (fromNet.neurons[i].synapses[synapse].toNeuron.parentNet ==
	      toNet) {

  	    int toId = fromNet.neurons[i].synapses[synapse].toNeuron.getId();
            if ((toId >=toStart) && (toId < (toStart+CASize))) {
              totSynapses++;
              totWeight += fromNet.neurons[i].synapses[synapse].getWeight();
	    }
	  }
	}
      }
    }

    return totWeight/totSynapses;
  }

  public void oldprintConnectionWeights(int step) {
    float EtoEAvg = (float)0.0;
    float EtoAAvg = (float)0.0;
    float AtoAAvg = (float)0.0;
    float AtoEAvg = (float)0.0;
    float EtoNoAvg = (float)0.0;
    float EtoYesAvg = (float)0.0;
    float AtoNoAvg = (float)0.0;
    float AtoYesAvg = (float)0.0; 

    if ((step % 20) != 0) return;

    EtoEAvg = (float)getAvgWeights(0,0,0,0);
    EtoAAvg = (float)getAvgWeights(0,1,0,0);
    AtoAAvg = (float)getAvgWeights(1,1,0,0);
    AtoEAvg = (float)getAvgWeights(1,0,0,0);
    System.out.println("Weights " + step + " EtoE " + EtoEAvg +
                                           " EtoA " + EtoAAvg +
                                           " AtoA " + AtoAAvg +
                                           " AtoE " + AtoEAvg);

    if (!Xor3CANT.fWorks) {
      EtoNoAvg = (float)getAvgWeights(0,0,0,1);
      EtoYesAvg = (float)getAvgWeights(0,1,0,1);
      AtoNoAvg = (float)getAvgWeights(1,0,0,1);
      AtoYesAvg = (float)getAvgWeights(1,1,0,1);
      System.out.println("Weights " + step + " EtoN " + EtoNoAvg + 
                                           " EtoY " + EtoYesAvg +
                                           " AtoN " + AtoNoAvg +
                                           " AtoY " + AtoYesAvg);
    }
  }

  public int printCorrectAnswer () {
    xor3Net  baseNet = (xor3Net)getNet("BaseNet");
    int patternNum= baseNet.getCurrentPattern();
    if (debugLevel > 0) System.out.print(" Test " + patternNum + " ");
    if (patternNum == 3) {
    	if (debugLevel > 0) System.out.print(" No ");
      return 0;
    }
    else if (patternNum == 4) {
    	if (debugLevel > 0) System.out.print(" Yes ");
      return 1;
    }
    else if (patternNum == 5) {
    	if (debugLevel > 0) System.out.print(" Yes ");
      return 1;
    }
    else if (patternNum == 0) {
    	if (debugLevel > 0) System.out.print(" No ");
      return 0;
    }
    else
      System.out.print(" print correct answer error ");
    return -1;

  }

  private int Ns = 0;
  private int Ys = 0;
  private int correct = 0;
  private int inCorrect = 0;
  private int debugLevel = 0;
 
  public void measure (int CANTStep) {
    xor3Net baseNet = (xor3Net) getNet("BaseNet");
    xor3Net outputNet = (xor3Net) getNet("OutputNet");
    int numBaseFired = 0;


    for (int i = 0; i < baseNet.getSize(); i++) {
      if (baseNet.neurons[i].getFired())
	  numBaseFired++;
    }

    int numOutputFired = 0;
    for (int i = 0; i < outputNet.getSize(); i++) {
     
	if (outputNet.neurons[i].getFired()) {
          if (i < 200) Ns ++;
          else if (i < 400) Ys++;
	  numOutputFired++;
	}
    }
    if (inTest) {
	//System.out.println("Step " + CANTStep);
	if ((CANTStep > (trainingLength + 500)) && ((CANTStep % 20) == 0)) {
          Xor3CANT.resetForNewTest();
	      if (debugLevel > 0) System.out.print("Test " + CANTStep + " " + Ns + " " + Ys);
          int answer = printCorrectAnswer();
          if (Ys > Ns) {
            if (answer == 1)
		correct ++;
            else inCorrect++;
            if (debugLevel > 0) System.out.println(" Yes " + correct + " " + inCorrect);
	  }
          else  {
            if (answer == 0)
		correct ++;
            else inCorrect++;
            if (debugLevel > 0) System.out.println(" No " + correct + " " + inCorrect);
	  }
	}
    }
    else
      System.out.println(CANTStep + " train " + numBaseFired + " " + numOutputFired);

    //reset for choice
    if ((inTest) && ((CANTStep % 20) == 0))
      {
      Ns = 0;
      Ys = 0;
      }


  }
  public void printExpName () {
    //System.out.println("Xor 3");
  }

}

