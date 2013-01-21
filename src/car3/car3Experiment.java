

//run multiple tests

public class car3Experiment extends CANTExperiment {
	
  private int numInputTypes = 20;

  public car3Experiment () {
    trainingLength = 20000; 
    inTest = false;
  }
  public boolean experimentDone(int CANTStep) {
	if (CANTStep > trainingLength + (75 * numInputTypes*2) + 75 )
		return true;
    return false;
    
  }
  
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
  }
  
  private void  printNumFiring(int currentStep) {
		car3Net somNet = (car3Net) getNet("SomNet");
		int numFiring = 0;
		
		for (int i = 0; i < somNet.getSize(); i ++)
			if (somNet.neurons[i].getFired()) numFiring++;
		
		//System.out.println(currentStep + " " + numFiring);
	  
  }
  
  public boolean isEndEpoch(int currentStep) {
	car3Net inputNet = (car3Net) getNet("BaseNet");
	
	if ((currentStep %5000) == 0)
	  {
		//inputNet.printSomLocales();
		inputNet.setLearningRate(inputNet.getLearningRate()*0.7f);
	   }
	printNumFiring(currentStep);
	
    if ((currentStep% inputNet.getCyclesPerRun()) == 0 ) 
    	{
    	car3CANT.resetForNewTest();
    	return (true);
    	}
    return (false);
    
}
  public void endEpoch() {

	car3Net outputNet = (car3Net) getNet("OutputNet");
	car3Net baseNet = (car3Net) getNet("BaseNet");

	int epoch = (CANT23.CANTStep / 75);
	// System.out.println("End Epoch " + CANT23.CANTStep + " " + epoch);
	car3CANT.patReader.setPattern(baseNet, outputNet,this);
	baseNet.setNeuronsToStimulate(180);
	if (inTest) {
		outputNet.setNeuronsToStimulate(0);
		    //AutoCANT23.resetForNewTest();
		    }
	else
		outputNet.setNeuronsToStimulate(100);
  }
  
 
  public double printConnectionWeights(int step, String netName) {
	double totWeight = 0;
	int posNeurons = 0;
	car3Net net = (car3Net) getNet(netName);
    
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
    	
  public void printExpName () {
    System.out.println("Som2");
  }
  
  private double correlationMatrix[][] = new double[40][40];
  
  private void printCorrelations(car3Net net) {
	  
		double correlation;
		int correctSame = 0;
		int correctDiff = 0;
		
		for (int measure1=0; measure1<(numInputTypes*2); measure1++)
		{
			int measure1Cycle = (measure1*75) + 45; //if training cycles changes 45 may change
			for (int measure2=measure1+1; measure2<(numInputTypes*2); measure2++) {
				int measure2Cycle = (measure2*75) + 45; //if training cycles changes 45 may change
			    net.measure.setMeasure1(measure1Cycle);
			    net.measure.setMeasure2(measure2Cycle);
			    correlation = net.measure.Measure();
			    correlationMatrix[measure1][measure2]= correlation;
			    //System.out.println("Correlation Between " + measure1Cycle +
			    	//	" and " + measure2Cycle + " is " + correlation);
			    
			    if ((measure1 % numInputTypes) == (measure2 % numInputTypes)){
			    	if (correlation > 0) correctSame++;
			    }
			    else {	
			    	if (correlation < 0) correctDiff++;
			    }
			}
		}

	    System.out.println("Results " + correctSame + " " + correctDiff);
  }
  
  private int makeDecision(int testItem){
	int result = -1;
	double bestAnswer = -1.0;
	int testRow;
	int testCol;
	
	for (int compareItem=0;compareItem < numInputTypes*2; compareItem++){
		if (testItem < compareItem) {
			testRow = testItem;
			testCol = compareItem;
		}
		else {
			testRow = compareItem;
			testCol = testItem;		
		}
		if (testItem != compareItem){
			double newAnswer = correlationMatrix[testRow][testCol];
			if (newAnswer > bestAnswer) {
				bestAnswer = newAnswer;
				result = compareItem;
			}
		}
		
	}
	
	return result;  
  }
  
	private void printDecisions() {
		int correctDecisions = 0;
		for (int item = 0; item < numInputTypes * 2; item++) {
			int result = makeDecision(item);
			if ((result % numInputTypes) == (item % numInputTypes))
				correctDecisions++;
		}
		System.out.println("Decisions " + correctDecisions);
	}

	public void measure(int currentStep) {
		if (currentStep == trainingLength + (75 * numInputTypes*2) + 75 ){
			System.out.println("Measure Now " + currentStep);
			car3Net net = (car3Net) getNet("SomNet");

			printCorrelations(net);
			printDecisions();
		}

	}
  
} //end class
