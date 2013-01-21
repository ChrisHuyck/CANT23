import java.util.Enumeration;

public class som1Net extends CANTNet {
	 
  public som1Net(){
  }
  
  public som1Net(String name,int cols, int rows,int topology){
    super(name,cols,rows,topology);
	cyclesToStimulatePerRun = 40;
  }

//This really slows down processing.  
public void updateIncomingStrengthsSlow(){
   for (int cNeuron = 0; cNeuron < getTotalNeurons(); cNeuron++) {
     CANTNeuronSpontaneousFatigue modNeuron = (CANTNeuronSpontaneousFatigue)
       neurons[cNeuron];
     modNeuron.setIncomingStrength(modNeuron.getIncomingStrength(3));
   }
}

public void zeroIncomingStrengths(){
  for (int cNeuron = 0; cNeuron < getTotalNeurons(); cNeuron++) {
     CANTNeuronSpontaneousFatigue modNeuron = (CANTNeuronSpontaneousFatigue)
       neurons[cNeuron];
     modNeuron.setIncomingStrength(0);
  }
}

  //need this to subclass experiment
  public void changePattern(int cantStep)
    {
    //note this only runs on the first step.  The rest of the time it
    //is in experiment.endEpoch.
    if (cantStep ==0){	 
      int curPattern = 0;
      curPattern %= getTotalPatterns();
      setCurrentPattern(curPattern);
      ((CANTPattern)patterns.get(curPattern)).arrange(getNeuronsToStimulate());
    }
  }
  
  public void readBetweenAllNets() {
    int netsChecked = 0;
    Enumeration <?> eNum = CANT23.nets.elements();
    som1Net net = (som1Net) eNum.nextElement();
    som1Net baseNet = net;
    som1Net somNet = net;

    System.out.println("som1 read Between");

      som1Net gasNet = net;
      do {
        System.out.println(net.getName());
        if (net.getName().compareTo("BaseNet") == 0)
          baseNet = net;
        else if (net.getName().compareTo("SomNet") == 0)
          somNet = net;
        else
          System.out.println(net.getName() + " missed net in connect all");
        netsChecked++;
        if (netsChecked < 2)
          net = (som1Net) eNum.nextElement();
      } while (netsChecked < 2);

      baseNet.readConnectTo(somNet);
  }
  
  
  
  public void runAllOneStep(int CANTStep) {
	//printSOMOn();
	  
    //This series of loops is really chaotic, but I needed to
    //get all of the propagation done in each net in step.
    som1CANT.runOneStepStart();
	
    Enumeration <?> eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
      net.setExternalActivation(CANTStep);
    }
    
    //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
      net.learn();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      som1Net net = (som1Net)eNum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
//if (net.getName().compareTo("VerbNet") == 0)   System.out.println(net.neurons[0].getFatigue() +   " verb Neuron " + net.neurons[0].getActivation());
    }
  }


  public CANTNet getNewNet(String name,int cols, int rows,int topology){
    som1Net net = new som1Net (name,cols,rows,topology);
    return (net);
  } 

	protected void createNeurons() {
		if (topology == 1) {
	        totalNeurons = 0;
	        neurons = new CANTNeuron[cols*rows];
	        for(int i=0;i< cols*rows;i++)
	          {
	          neurons[i] = new CANTNeuron(totalNeurons++,this);
	          neurons[i].setCompensatoryBase(10.0);
	          }
		}
		else if (topology == 2) {
			System.out.println(" creating fatigue neurons ");
			totalNeurons = 0;
			neurons = new CANTNeuronSpontaneousFatigue[cols * rows];
			for (int i = 0; i < cols * rows; i++) {
				neurons[i] = new CANTNeuronSpontaneousFatigue(totalNeurons++,
						this);
				neurons[i].setCompensatoryBase(10.0);
			}
			setInitialFatigue();
		}
		else System.out.println("error in som1Net.createNuerons ");

	}

  //For each som neuron select one of its X input and one Y to be big
  private void selectSomNeuronBigInput(som1Net somNet) {
	  double bigWeight = 0.25;
	  for (int toNeuron = 0; toNeuron < somNet.getSize(); toNeuron ++) {
	    //pick an x/10
		int xInputBin =  CANT23.random.nextInt(10);
	    //pick a y/10
		int yInputBin =  CANT23.random.nextInt(10);
        //find a good axon 
		boolean axonFound = false;
		while (!axonFound) {
			//pick one of the fromNeurons
			int fromNeuron = CANT23.random.nextInt(10);
			fromNeuron += xInputBin *10;
			//loop through the fromNeurons axons and if one is the toNeuron, axon is found
			for (int synapse = 0; synapse < neurons[fromNeuron].getCurrentSynapses(); synapse++){
              int testToId = neurons[fromNeuron].synapses[synapse].toNeuron.getId();
              if (toNeuron == testToId){
            	  axonFound = true;
            	  //change that weight
            	  neurons[fromNeuron].synapses[synapse].setWeight(bigWeight);
              }
			}	
		}
		axonFound = false;
		while (!axonFound) {
			//pick one of the fromNeurons
			int fromNeuron = CANT23.random.nextInt(10);
			fromNeuron += (yInputBin *10) + 100;
			//loop through the fromNeurons axons and if one is the toNeuron, axon is found
			for (int synapse = 0; synapse < neurons[fromNeuron].getCurrentSynapses(); synapse++){
              int testToId = neurons[fromNeuron].synapses[synapse].toNeuron.getId();
              if (toNeuron == testToId){
            	  axonFound = true;
            	  //change that weight
            	  neurons[fromNeuron].synapses[synapse].setWeight(bigWeight);
              }
			}	
		}

	  }
  }
  
  public void connectBaseToSomNet(som1Net somNet) {
	  
	//Loop through each Som neuron.  Each should get a connection from
	//one of 10 bins of X, and Y inputs.  The X are the first hundred base, and 
	//Y the second 
    for (int toNeuron = 0; toNeuron < somNet.getSize(); toNeuron++) {
      for (int synapse = 0; synapse < 10; synapse++) {
    	int fromNeuron = (synapse*10) + CANT23.random.nextInt(10);
        neurons[fromNeuron].addConnection(somNet.neurons[toNeuron], 0.001);
    	fromNeuron = 100+ (synapse*10) + CANT23.random.nextInt(10);
        neurons[fromNeuron].addConnection(somNet.neurons[toNeuron], 0.001);
      }
    }
    selectSomNeuronBigInput(somNet);
  }




  protected void setConnections() {
    for(int i=0;i< cols*rows;i++)
      {
      if (neurons[i].isInhibitory())
        setConnectionsRandomly(i,20,0.01);
      else
        setConnectionsRandomly(i,20,0.01);
      }
  }

  //Fatigue should be relatively random to start.
  public void setInitialFatigue() {
    for(int i=0;i< cols*rows;i++) {
      float newFatigue = CANT23.random.nextFloat();
      newFatigue *= getActivationThreshold()*2;
      newFatigue -= getActivationThreshold();
      neurons[i].setFatigue(newFatigue);
    }
  }

  //set fatigue between threshold and bottomVal
  public void setInitialFatigue(double bottomVal) {
    double range = getActivationThreshold() - bottomVal;
    for(int i=0;i< cols*rows;i++) {
      float newFatigue = CANT23.random.nextFloat();
      newFatigue *= range;
      newFatigue += bottomVal;
      neurons[i].setFatigue(newFatigue);
    }
  }

  private void setInputTopology() {
      //setConnections(); no internal connections
  }
  
	private void setSOMTopology() {
		//make 10 CAs
/*		for (int CA = 0; CA < 10; CA++) {
		  for (int neuron = 0; neuron < 100; neuron++){	 
			int fromNeuron = neuron + (CA*100);
			if ((neuron % 2) == 0) {
				neurons[fromNeuron].setInhibitory(true);
				for (int synapses = 0; synapses < 30;synapses++) {
			      int toCA = CANT23.random.nextInt(9);
			      if (toCA >= CA) toCA++;
				  int toNeuron = CANT23.random.nextInt(100);
				  toNeuron += toCA*100;
				  addConnection(fromNeuron, toNeuron, 0.7);
				}
			} else {
				neurons[fromNeuron].setInhibitory(false);
				for (int synapses = 0; synapses < 30; synapses++) {
					int toNeuron = ((synapses*7)+neuron)%100;
					toNeuron += CA*100;
					addConnection(fromNeuron, toNeuron, 0.3);
				}
			}
		  }
		} */
		for (int CA = 0; CA < 5; CA++) {
		  for (int neuron = 0; neuron < 100; neuron++){	 
			int fromNeuron = neuron + (CA*100);
			if ((neuron % 2) == 0) {
				neurons[fromNeuron].setInhibitory(true);
				for (int synapses = 0; synapses < 30;synapses++) {
			      int toCA = CANT23.random.nextInt(4);
			      if (toCA >= CA) toCA++;
				  int toNeuron = CANT23.random.nextInt(100);
				  toNeuron += toCA*100;
				  addConnection(fromNeuron, toNeuron, 0.7);
				}
			} else {
				neurons[fromNeuron].setInhibitory(false);
				for (int synapses = 0; synapses < 30; synapses++) {
					int toNeuron = ((synapses*7)+neuron)%100;
					toNeuron += CA*100;
					addConnection(fromNeuron, toNeuron, 0.3);
				}
			}
		  }
		} 
	}


  public void initializeNeurons() {
    //set up topologies.
    createNeurons();
    
    if (topology == 1){
      //System.out.println("som1 input topology ");  	
        setInputTopology();
    }
    else if (topology == 2){
      //System.out.println("som1 som topology ");
      setSOMTopology();
    }
    else 
    System.out.println("bad toppology specified "+ topology);
  }
 
 /*
    int toSynapses = 0;
    double toWeight = 0.0;
    for (int synapse = 0; synapse < neuron.getCurrentSynapses(); synapse ++) {
      int toNeuronID = neuron.synapses[synapse].toNeuron.getId();
      if ((toNeuronID >= (toCA*200))  && (toNeuronID < ((toCA+1) *200))) {
        toSynapses++;
        toWeight += neuron.synapses[synapse].getWeight();
      }
    }
    if (toSynapses > 0)
      return (toWeight/toSynapses);
    return 0.0;
  }

private double getTotalWeights(CANTNeuron neuron,String toSubnet) {
    double toWeight = 0.0;
    for (int synapse = 0; synapse < neuron.getCurrentSynapses(); synapse ++) {
      int toNeuronID = neuron.synapses[synapse].toNeuron.getId();
      if (neuron.synapses[synapse].toNeuron.parentNet.getName().compareTo(
        toSubnet) == 0){
        toWeight += neuron.synapses[synapse].getWeight();
      }
    }
    return (toWeight);
}
*/

  private double getTotalWeights(CANTNeuron neuron) {
    double toWeight = 0.0;
    for (int synapse = 0; synapse < neuron.getCurrentSynapses(); synapse ++) {
      toWeight += neuron.synapses[synapse].getWeight();
    }
    return (toWeight);
  }

  //print out the weights to baseE,baseA,baseB,outputN, and outputY
  private void printToCAWeights(CANTNeuron neuron) {
    som1Net baseNet = (som1Net) CANT23.experiment.getNet("BaseNet");
    som1Net outputNet = (som1Net) CANT23.experiment.getNet("OutputNet");

    double oneWeight = 0;
    double twoWeight = 0;
    double threeWeight = 0;
    double fourWeight = 0;
    double fiveWeight = 0;
    for (int synapse = 0; synapse < neuron.getCurrentSynapses();synapse++) {
      CANTNeuron toNeuron = neuron.synapses[synapse].toNeuron;
      int toId = toNeuron.getId();
      String toNet = toNeuron.parentNet.getName();
      if (toId < 200) {
        if (toNet.compareTo("BaseNet") == 0)
          oneWeight += neuron.synapses[synapse].getWeight();
        else
          fourWeight += neuron.synapses[synapse].getWeight();
      }
      else if (toId < 400) {
        if (toNet.compareTo("BaseNet") == 0)
          twoWeight += neuron.synapses[synapse].getWeight();
        else
          fiveWeight += neuron.synapses[synapse].getWeight();
      }
      else 
        threeWeight += neuron.synapses[synapse].getWeight();
    }

    System.out.format("%1d %2$03.2f %3$03.2f %4$03.2f %5$03.2f %6$03.2f %n", 
      neuron.getId(), oneWeight, twoWeight, threeWeight,fourWeight,fiveWeight);
  }

    private int getGasNeuronsFired () {
      int total = 0;
      
      som1Net gasNet = (som1Net) CANT23.experiment.getNet("GasNet");
      for (int i= 0; i < getSize(); i++) {
    	  if (gasNet.neurons[i].getFired()) total++;
      }
      return total;
    }
    
    
	private void antiHebbian(int fromOffset, int synapse) {
		if (getGasNeuronsFired() < 250) return;

		CANTNeuronSpontaneousFatigue toNeuron = (CANTNeuronSpontaneousFatigue) neurons[fromOffset].synapses[synapse]
				.getTo();
		double connectionStrength = neurons[fromOffset].synapses[synapse]
				.getWeight();

		neurons[fromOffset].setInhibitory(true);

		if (toNeuron.getFired()) {
			double modification = neurons[fromOffset].getIncreaseBase(-1
					* connectionStrength);
			modification *= getLearningRate();
			connectionStrength = connectionStrength - modification;
			neurons[fromOffset].synapses[synapse].setWeight(connectionStrength);
			// System.out.println(fromOffset + " anti compense - " +
			// connectionStrength + " " + modification);
		} else {
			double modification = neurons[fromOffset].getDecreaseBase(-1
					* connectionStrength);
			modification *= getLearningRate();
			connectionStrength = connectionStrength + modification;
			neurons[fromOffset].synapses[synapse].setWeight(connectionStrength);
			// System.out.println(fromOffset + " anti compense + " +
			// connectionStrength + " " + modification);

		}
	}

    private void compense(int fromOffset, int synapse) {
		CANTNeuronSpontaneousFatigue toNeuron = (CANTNeuronSpontaneousFatigue) 
				neurons[fromOffset].synapses[synapse].getTo();
        double connectionStrength = neurons[fromOffset].synapses[synapse].getWeight();

        neurons[fromOffset].setInhibitory(false);

        
		if (toNeuron.getFired())
		  {
          double modification = neurons[fromOffset].getIncreaseBase(connectionStrength);
          modification *= getLearningRate();
          connectionStrength = connectionStrength+modification;
          neurons[fromOffset].synapses[synapse].setWeight(connectionStrength);
//System.out.println(fromOffset + " hebb compense + " + connectionStrength + " " + modification); 
		  }
		else 
		  {
	          double modification = neurons[fromOffset].getDecreaseBase(connectionStrength);
	          modification *= getLearningRate();
	          connectionStrength = connectionStrength-modification;
	          neurons[fromOffset].synapses[synapse].setWeight(connectionStrength);
	//System.out.println(fromOffset + " hebb compense - " + connectionStrength + " " + modification); 
			  }
	}
    
    private int useAntiHebbian = 2; //0, no; 1; 50/50 yes; 2 ;25/25/50 yes

	private void hebbianAntiHebbianLearn() {
		for (int neuronOffset = 0; neuronOffset < getSize(); neuronOffset++) {
			// Only learn when the from neuron is active
			if (neurons[neuronOffset].getFired()) {
				if (neurons[neuronOffset].isInhibitory()
						&& (useAntiHebbian == 1)) {
					// loop through the synapses
					for (int synap = 0; synap < neurons[neuronOffset]
							.getCurrentSynapses(); synap++) {
						CANTNeuronSpontaneousFatigue toNeuron = (CANTNeuronSpontaneousFatigue) neurons[neuronOffset].synapses[synap]
								.getTo();
						antiHebbian(neuronOffset, synap);
					}
				}
				else if (neurons[neuronOffset].isInhibitory() && (useAntiHebbian == 2)
						&& ((neuronOffset%4)==0)) {
					for (int synap = 0; synap < neurons[neuronOffset]
							.getCurrentSynapses(); synap++) {
						CANTNeuronSpontaneousFatigue toNeuron = (CANTNeuronSpontaneousFatigue) neurons[neuronOffset].synapses[synap]
								.getTo();
						antiHebbian(neuronOffset, synap);
					}
				}

				else neurons[neuronOffset].learn4();
			}
		}
	}

  public void learn() {
	int learningOn = getLearningOn();
    if (learningOn == 0) return;
    else if (learningOn == 1)
    {
	    int totalNeurons = size();
	    for (int neuronIndex = 0; neuronIndex < totalNeurons; neuronIndex++) 
	      {
	      neurons[neuronIndex].learn4();
	      }
    }
    else  if (learningOn == 3) {
    	hebbianAntiHebbianLearn();
    }
    else 
    	System.out.println(" bad learning on in som1Net learn " + learningOn); 
 }
  
  private int somOn = -1;

	private void printSOMOn() {
		som1Net somNet = (som1Net) CANT23.experiment.getNet("SomNet");

		//for (int CA = 0; CA < 10; CA++) {
			for (int CA = 0; CA < 5; CA++) {
			int neuronsFiring = 0;
			for (int i = 0; i < 100; i++) {
				if (somNet.neurons[i + (CA * 100)].getFired())
					neuronsFiring++;
			}
			if (neuronsFiring > 50) {
				if (somOn == -1) {
					somOn = CA;
					System.out.println(CA + " CA on " + CANT23.CANTStep);
				} else if (CA != somOn) {
					System.out.println(CA + " CA and CA  " + somOn + " on "
							+ CANT23.CANTStep);
				}
			} else if ((CA == somOn) && (neuronsFiring == 0)) {
				System.out.println(CA + " CA off " + CANT23.CANTStep);
				somOn = -1;
			}
		}
	}

  private void printSOMPlace(int CA){
	//Look through all the input neurons and see what the weights are to this CA.
    som1Net inputNet = (som1Net) CANT23.experiment.getNet("BaseNet");
	double xWeights[];
	int xSynapses[];
	double yWeights[];
	int ySynapses[];
	xWeights = new double[10];
	xSynapses = new int[10];
	yWeights = new double[10];
	ySynapses = new int[10];
	
	 System.out.println("To CA " + CA);
			 
	for (int i = 0; i < 10; i++){
		xWeights[i]=0.0;
		xSynapses[i] = 0;
		yWeights[i]=0.0;
		ySynapses[i] = 0;
	}
	
	//Get the x values by looking at all the synapses
	for (int neuronOffset =0; neuronOffset < 100; neuronOffset++) {
		for (int syn=0; syn<inputNet.neurons[neuronOffset].getCurrentSynapses();syn++) {
			int toNeuronId = inputNet.neurons[neuronOffset].synapses[syn].toNeuron.getId();
			if ((toNeuronId/100) == CA) {
			  int xBin = neuronOffset/10;
			  xWeights[xBin] += inputNet.neurons[neuronOffset].synapses[syn].getWeight();
			  xSynapses[xBin]++;
			}
		}
	}
	
	//Get the y values by looking at all the synapses
	for (int neuronOffset =100; neuronOffset < 200; neuronOffset++) {
		for (int syn=0; syn<inputNet.neurons[neuronOffset].getCurrentSynapses();syn++) {
			int toNeuronId = inputNet.neurons[neuronOffset].synapses[syn].toNeuron.getId();
			if ((toNeuronId/100) == CA) {
			  int yBin = (neuronOffset-100)/10;
			  yWeights[yBin] += inputNet.neurons[neuronOffset].synapses[syn].getWeight();
			  ySynapses[yBin]++;
			}
		}
	}

	//print out the values to this CA.
	for (int i = 0; i < 10; i++){
        System.out.println(i + " " + xWeights[i] +  " " + yWeights[i]);
	}
  }
  
  public void printSomLocales() {
	  System.out.println("Soms " + CANT23.CANTStep);
	  for (int CA = 0; CA < 5; CA++) {
			//Look through all the input neurons and see what the weights are to this CA.
		    som1Net inputNet = (som1Net) CANT23.experiment.getNet("BaseNet");
			double xWeights[];
			int xSynapses[];
			double yWeights[];
			int ySynapses[];
			xWeights = new double[10];
			xSynapses = new int[10];
			yWeights = new double[10];
			ySynapses = new int[10];
						 
			for (int i = 0; i < 10; i++){
				xWeights[i]=0.0;
				xSynapses[i] = 0;
				yWeights[i]=0.0;
				ySynapses[i] = 0;
			}
			
			//Get the x values by looking at all the synapses
			for (int neuronOffset =0; neuronOffset < 100; neuronOffset++) {
				for (int syn=0; syn<inputNet.neurons[neuronOffset].getCurrentSynapses();syn++) {
					int toNeuronId = inputNet.neurons[neuronOffset].synapses[syn].toNeuron.getId();
					if ((toNeuronId/100) == CA) {
					  int xBin = neuronOffset/10;
					  xWeights[xBin] += inputNet.neurons[neuronOffset].synapses[syn].getWeight();
					  xSynapses[xBin]++;
					}
				}
			}
			
			//Get the y values by looking at all the synapses
			for (int neuronOffset =100; neuronOffset < 200; neuronOffset++) {
				for (int syn=0; syn<inputNet.neurons[neuronOffset].getCurrentSynapses();syn++) {
					int toNeuronId = inputNet.neurons[neuronOffset].synapses[syn].toNeuron.getId();
					if ((toNeuronId/100) == CA) {
					  int yBin = (neuronOffset-100)/10;
					  yWeights[yBin] += inputNet.neurons[neuronOffset].synapses[syn].getWeight();
					  ySynapses[yBin]++;
					}
				}
			}

		int bestX = 0;
		double bestXVal = xWeights[0];	
		for (int i = 1; i < 10; i++){
			if (bestXVal < xWeights[i]) {
				bestX = i;
				bestXVal = xWeights[i];
			}			
		}
		int bestY = 0;
		double bestYVal = yWeights[0];	
		for (int i = 1; i < 10; i++){
			if (bestYVal < yWeights[i]) {
				bestY = i;
				bestYVal = yWeights[i];
			}			
		}
		//print out the values to this CA.
	    System.out.println(CA + " " + bestX + " " +bestY);
	  }
  }
  
  public void kludge () {
    System.out.println("kludge" + som1CANT.kludge );
    som1Net net = (som1Net) CANT23.experiment.getNet("SomNet");

    if (som1CANT.kludge == 0) {
      net = (som1Net) CANT23.experiment.getNet("BaseNet");
      for (int i= 0; i < 10; i++) 
        {
       double totalWeights = getTotalWeights(net.neurons[i]);
        System.out.println(i + " " + totalWeights + " " +  net.neurons[i].getFatigue()); 
        }
    }
    else if (som1CANT.kludge == 1) {
        for (int i= 0; i < 10; i++) 
          {
          double totalWeights = getTotalWeights(net.neurons[i]);
          double totalIncWeight = ((CANTNeuronSpontaneousFatigue)net.neurons[i]).getIncomingStrength();
          System.out.println(i + " " + totalWeights + " " +  totalIncWeight); 
          }
      }
    else if (som1CANT.kludge == 2) {
    	for (int i = 0; i < 10; i++)
    	  printSOMPlace(i);
    }
    else if (som1CANT.kludge == 3) {
        for (int i= 0; i < 10; i++) 
          {
          System.out.println(i + " " +  net.neurons[i].getFatigue()); 
          }
      }
    else {
        for (int i= 0; i < 10; i++) 
        {
        System.out.println(i + " act " +  net.neurons[i].getActivation()); 
        }
    }
    /*
           double avgWeights;
      double totalWeights = getTotalWeights(net.neurons[i]);
      avgWeights = getTotalWeights(net.neurons[i],"OutputNet");
      System.out.println(i + " " + avgWeights + " " + totalWeights); 
      //System.out.println(i + " " + this.neurons[i].getActivation() + 
      //                      " " + this.neurons[i].getFatigue());
      */
  }
  
  public void measure(int currentStep) {
    System.out.println("measure " + neurons[0].getActivation() + " " + 
      neurons[0].getFired() + " " + 
	  currentStep);
  }
}

