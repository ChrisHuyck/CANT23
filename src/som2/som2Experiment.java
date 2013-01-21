

//run multiple tests

public class som2Experiment extends CANTExperiment {
	
  private int numInputTypes = 20;

  public som2Experiment () {
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
		som2Net somNet = (som2Net) getNet("SomNet");
		int numFiring = 0;
		
		for (int i = 0; i < somNet.getSize(); i ++)
			if (somNet.neurons[i].getFired()) numFiring++;
		
		//System.out.println(currentStep + " " + numFiring);
	  
  }
  
  public boolean isEndEpoch(int currentStep) {
	som2Net inputNet = (som2Net) getNet("BaseNet");
	
	if ((currentStep %5000) == 0)
	  {
		//inputNet.printSomLocales();
		inputNet.setLearningRate(inputNet.getLearningRate()*0.7f);
	   }
	printNumFiring(currentStep);
	
    if ((currentStep% inputNet.getCyclesPerRun()) == 0 ) 
    	{
    	som2CANT.resetForNewTest();
    	return (true);
    	}
    return (false);
    
}
  public void endEpoch() {
		//som2Net somNet = (som2Net) getNet("SomNet");
		//somNet.setInitialFatigue(0.0);

	//each epoch will pick an input in one of three 10x10 areas;
	som2Net inputNet = (som2Net) getNet("BaseNet");
	
	int points[];
	
	points = new int[20];
	int cPoints = 20;
	int area;
	
	if (!inTest) {
		area = CANT23.random.nextInt(numInputTypes);
	}
	else { 
		area = CANT23.CANTStep /75;
		area %= numInputTypes;	
	}
	
	int xOffset =  0;
	//int yOffset =  100; //10x20 net
	int yOffset =  200;
	
	//for (19,19) type (19,9) type (8,9)
	//xOffset += (area*10);
	//yOffset += (area*10);
	
	
	//(10,0),(11,1)..(19,9),(10,10)(11,11)..(19,19)
	//if (area < 10) xOffset += 100;
	
	//(0,0),(1,1)..(9,9),(9,0),(0,1),(1,2)..(8,9)
	/*if (area >= 10) {
		yOffset -= 100;
		if (area == 10) xOffset -= 10;
		else xOffset -= 110;
	}*/
	
	//for (0,0)(1,0)(2,0)(3,0) (4,1)
	xOffset += ((area%5)*10);
	yOffset += ((area/4)*10);
	
	
	
/*	if (area == 0) {
		xOffset+= 0; //first area is bottom left could be ignored
	}
	else if (area == 1) {
		xOffset+= 50; 
		yOffset+= 20; 
	}
	else if (area == 2) {
		xOffset+= 20; 
		yOffset+= 40; 
	}
	else  {
		System.out.println("Error in endepoch " + area );
	}
	*/

	for (int i = 0; i < (cPoints/2); i++) {
	  points[2*i] = xOffset + i;
	  points[(2*i) + 1] = yOffset+ i;
	}

	//System.out.println("Pattern " + xOffset + " " + yOffset + " " + CANT23.CANTStep );

	CANTPattern newPat = new CANTPattern(inputNet, "bob", 0, cPoints, points);
	inputNet.addNewPattern(newPat);
  }
  
 
  public double printConnectionWeights(int step, String netName) {
	double totWeight = 0;
	int posNeurons = 0;
	som2Net net = (som2Net) getNet(netName);
    
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
  
  private void printCorrelations(som2Net net) {
	  
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
			som2Net net = (som2Net) getNet("SomNet");

			printCorrelations(net);
			printDecisions();
		}

	}
  
} //end class
