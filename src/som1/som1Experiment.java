

//run multiple tests

public class som1Experiment extends CANTExperiment {

  public som1Experiment () {
    trainingLength = 20000; 
    inTest = false;
  }
  public boolean experimentDone(int CANTStep) {
    return false;
  }
  
  public void switchToTest () {
    System.out.println("swithctotest ");
    inTest = true;
  }
  
  private void  printNumFiring(int currentStep) {
		som1Net somNet = (som1Net) getNet("SomNet");
		int numFiring = 0;
		
		for (int i = 0; i < somNet.getSize(); i ++)
			if (somNet.neurons[i].getFired()) numFiring++;
		
		//System.out.println(currentStep + " " + numFiring);
	  
  }
  
  public boolean isEndEpoch(int currentStep) {
	som1Net inputNet = (som1Net) getNet("BaseNet");
	
	if ((currentStep %5000) == 0)
	  {
		inputNet.printSomLocales();
		inputNet.setLearningRate(inputNet.getLearningRate()*0.7f);
	   }
	printNumFiring(currentStep);
	
    if ((currentStep% inputNet.getCyclesPerRun()) == 0 ) return (true);
    return (false);
    
}
  public void endEpoch() {
		//som1Net somNet = (som1Net) getNet("SomNet");
		//somNet.setInitialFatigue(0.0);

	//each epoch will pick an input in one of three 10x10 areas;
	som1Net inputNet = (som1Net) getNet("BaseNet");
	
	int points[];
	
	points = new int[20];
	int cPoints = 20;
	
	int area = CANT23.random.nextInt(3);
	
	
	int xOffset =  0;
	int yOffset =  100;
	
	if (area == 0) {
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
	som1Net net = (som1Net) getNet(netName);
    
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
    System.out.println("Som1");
  }
}
