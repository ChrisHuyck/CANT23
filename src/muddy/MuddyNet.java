

import java.util.Enumeration;

public class MuddyNet extends CANTNet {
  
  public MuddyNet(){
  }
  
  public MuddyNet(String name,int cols, int rows,int topology){
    super(name,cols,rows,topology);
    //cyclesToStimulatePerRun = 20;
  }

  //need this to subclass experiment
  public void changePattern(int cantStep)
    {
	int testFlavor = 7;
	int fact1;
	int fact2;
	int fact3;
	
    //note this only runs on the first step.
	//change curPattern for initial state of each net.
	  //for q3 0,0,0,      0,0,0   -> 1
	  //for q2 p1 p2 0,0,0,2,2,3   -> 2
	  //for q2 p1 p3 0,0,0,1,3,2   -> 3
	  //for q2 p2 p3 0,0,0,3,1,1   -> 4
	  //for q1 p1  0,0,0,  4,4,4   -> 5
	  //for q1 p2  0,0,0,  5,5,5   ->6
	  //for q1 p3  0,0,0,  6,6,6   -> 7
    if (testFlavor == 1) {fact1 = 0;	fact2 = 0; 	fact3 = 0; }
    else if (testFlavor == 2) {fact1 = 2; fact2 = 2; fact3 = 3;  }
    else if (testFlavor == 3) {fact1 = 1; fact2 = 3; fact3 = 2;  }
    else if (testFlavor == 4) {fact1 = 3; fact2 = 1; fact3 = 1;  }
    else if (testFlavor == 5) {fact1 = 4; fact2 = 4; fact3 = 4;  }
    else if (testFlavor == 6) {fact1 = 5; fact2 = 5; fact3 = 5;  }
    else if (testFlavor == 7) {fact1 = 6; fact2 = 6; fact3 = 6;  }
    else {
    	System.out.println(" Error in MuddyNet change pattern");
    	return;
    }
    
    if (cantStep ==0){	
      if (getName().compareTo("Rule1Net") == 0){
    	  int curPattern = 0;  
          setCurrentPattern(curPattern);
      }
      else if (getName().compareTo("Rule2Net") == 0){
    	  int curPattern = 0;  
          setCurrentPattern(curPattern);
      }
      else if (getName().compareTo("Rule3Net") == 0){
    	  int curPattern = 0;  
          setCurrentPattern(curPattern);
      }
      else if (getName().compareTo("BaseNet") == 0){ //BaseNet Fact1
          int curPattern = fact1;  //change this to get a different input pattern 
                               //0-3 look at muddy.xml 
          setCurrentPattern(curPattern);
         }     
      else if (getName().compareTo("Fact2Net") == 0){
    	  int curPattern = fact2;  
          setCurrentPattern(curPattern);
      }
      else if (getName().compareTo("Fact3Net") == 0){
    	  int curPattern = fact3;  
          setCurrentPattern(curPattern);
      }

    }
  }
  
  public void readBetweenAllNets() {
    int netsChecked = 0;
    Enumeration <?> eNum = CANT23.nets.elements();
    MuddyNet net = (MuddyNet) eNum.nextElement();
    MuddyNet baseNet = net;
    MuddyNet outputNet = net;

    System.out.println("Muddy read Between");

    MuddyNet gasNet = net;
    do {
      System.out.println(net.getName());
      if (net.getName().compareTo("BaseNet") == 0)
        baseNet = net;
      else if (net.getName().compareTo("GasNet") == 0)
        gasNet = net;
      else if (net.getName().compareTo("OutputNet") == 0)
        outputNet = net;
      else
        System.out.println(net.getName() + " missed net in connect all");
      netsChecked++;
      if (netsChecked < 3)
        net = (MuddyNet) eNum.nextElement();
    } while (netsChecked < 3);

    baseNet.readConnectTo(gasNet);
    gasNet.readConnectTo(baseNet);
    gasNet.readConnectTo(outputNet);
    outputNet.readConnectTo(gasNet);
  }
  
  
  
  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
    //get all of the propogation done in each net in step.
    MuddyCANT23.runOneStepStart();
	
    Enumeration <?> eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      MuddyNet net = (MuddyNet)eNum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      MuddyNet net = (MuddyNet)eNum.nextElement();
      net.setExternalActivation(CANTStep);
    }
    
    //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      MuddyNet net = (MuddyNet)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      MuddyNet net = (MuddyNet)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      MuddyNet net = (MuddyNet)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      MuddyNet net = (MuddyNet)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      MuddyNet net = (MuddyNet)eNum.nextElement();
      net.learn();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      MuddyNet net = (MuddyNet)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      MuddyNet net = (MuddyNet)eNum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
//if (net.getName().compareTo("VerbNet") == 0)   System.out.println(net.neurons[0].getFatigue() +   " verb Neuron " + net.neurons[0].getActivation());
    }
  }


  public CANTNet getNewNet(String name,int cols, int rows,int topology){
    MuddyNet net = new MuddyNet (name,cols,rows,topology);
    return (net);
  } 

	// This works by having the fact(s) constantly activating the rule.
	// The weight gradually increases over time, but most of the weight
	// comes from the rule oscillator. So the facts will eventually give ~ .5
	// and ~.4 by cycle 18. The rule oscillator gives 1.81
	private int factNeurons = 30;
	private double fullWeight = 0.05;

	private void connectFactToRule(MuddyNet ruleNet, int factNum, int ruleNum,
			int factsNeeded) {
		double weightForFact = fullWeight / factsNeeded;

		for (int offset = 0; offset < factNeurons; offset++) {
			int fromNeuron = factNum * factNeurons + offset;
			int toNeuron = offset % 5;
			toNeuron += ruleStart + (ruleSize * ruleNum);
			neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],weightForFact);
		}
	}

	public void connectFactToRuleNet(MuddyNet ruleNet) {
		connectFactToRule(ruleNet, 3, 0, 1); // connect fact 3 (q3) to rule 0,
												// to do it by itself 1.
		connectFactToRule(ruleNet, 1, 1, 3); // connect fact 1 (q1) to rule 1,
												// to do it with 3.
		connectFactToRule(ruleNet, 5, 1, 3); // connect fact 5 (-p2) to rule 1,
												// to do it with 3.
		connectFactToRule(ruleNet, 7, 1, 3); // connect fact 7 (-p3) to rule 1,
												// to do it with 3.
		connectFactToRule(ruleNet, 2, 2, 2); // connect fact 2 (q2) to rule 2 (needs 2)
		connectFactToRule(ruleNet, 7, 2, 2); // connect fact 2 (-p3) to rule 2 (needs 2)
		connectFactToRule(ruleNet, 2, 3, 2); // connect fact 2 (q2) to rule 3 (needs 2)
		connectFactToRule(ruleNet, 5, 3, 2); // connect fact 2 (-q2) to rule 3 (needs 2)
	}

	// The rule turns on the fact by starting the first bit.
	private void connectRuleToFact(MuddyNet factNet, int ruleNum, int factNum) {
		for (int offset = 0; offset < 5; offset++) {
			int fromNeuron = ruleStart + (ruleNum * ruleSize) + offset;
			int toNeuron = offset + (factNum * factNeurons);
			neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], 2.3);
		}
	}

	public void connectRuleToFactNet(MuddyNet factNet) {
		connectRuleToFact(factNet, 0, 0);// connect rule 0 to fact o (p1).
		connectRuleToFact(factNet, 1, 0);// connect rule 1 to fact o (p1).
		connectRuleToFact(factNet, 2, 0);// connect rule 2 to fact o (p1).
		connectRuleToFact(factNet, 3, 0);// connect rule 3 to fact o (p1).
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

  private int numFacts = 8;
  //facts are just oscillators with 5 neurons on each cycle once switched on, cycling
  //through 6 sets.  (Fc = .045 Fr = .01) 
  private void setFactTopology() {
	  double weight = 0.45;
	  for (int fact = 0 ; fact < numFacts; fact++)
	  {
		  for (int fromNeuronIt = 0; fromNeuronIt < 5; fromNeuronIt ++ )
		  {
			  int fromNeuron1 = (fact * factNeurons) + fromNeuronIt;
			  int fromNeuron2 = (fact * factNeurons) + fromNeuronIt + 5;
			  int fromNeuron3 = (fact * factNeurons) + fromNeuronIt + 10;
			  int fromNeuron4 = (fact * factNeurons) + fromNeuronIt + 15;
			  int fromNeuron5 = (fact * factNeurons) + fromNeuronIt + 20;
			  int fromNeuron6 = (fact * factNeurons) + fromNeuronIt + 25;
			  for (int toNeuronIt = 0; toNeuronIt < 5; toNeuronIt ++) {
				  int toNeuron1 = (fact * factNeurons) + toNeuronIt + 5;
				  int toNeuron2 = (fact * factNeurons) + toNeuronIt + 10;
				  int toNeuron3 = (fact * factNeurons) + toNeuronIt + 15;
				  int toNeuron4 = (fact * factNeurons) + toNeuronIt + 20;
				  int toNeuron5 = (fact * factNeurons) + toNeuronIt + 25;
				  int toNeuron6 = (fact * factNeurons) + toNeuronIt;
				  neurons[fromNeuron1].addConnection(neurons[toNeuron1], weight);
				  neurons[fromNeuron2].addConnection(neurons[toNeuron2], weight);
				  neurons[fromNeuron3].addConnection(neurons[toNeuron3], weight);
				  neurons[fromNeuron4].addConnection(neurons[toNeuron4], weight);
				  neurons[fromNeuron5].addConnection(neurons[toNeuron5], weight);
				  neurons[fromNeuron6].addConnection(neurons[toNeuron6], weight);
			  }
		  }
	  }
  }
  
	private int ruleSize = 30;
	private int ruleStart = 90;

	// On the third oscillation turn on rules that are supported.
	// This is done by connecting the third oscillator to all the rules, but
	// not enough to fire the rule by itself.  Also inhibit from the fourth.  
	private void connectRuleOscillatorToRule(int ruleNum) {
		double weight = 1.81;
		for (int neuronOffset = 0; neuronOffset < 5; neuronOffset++) {
			int fromNeuron1 = 10 + neuronOffset;
			int toNeuron = ruleStart + (ruleNum * ruleSize) + neuronOffset;
			neurons[fromNeuron1].addConnection(neurons[toNeuron], weight);
			int fromNeuron2 = 15 + neuronOffset;
			neurons[fromNeuron2].addConnection(neurons[toNeuron], -weight/1.12); //Decay = 1.12
		}
	}
	
	//rule shuts down oscillator
	private void connectRuleToRuleOscillator(int ruleNum) {
		for (int neuronOffset = 0; neuronOffset < 5; neuronOffset++) {
			int fromNeuron = ruleStart + (ruleNum*ruleSize)+ neuronOffset;
			for (int oscillatorStep = 0; oscillatorStep < 6; oscillatorStep++ )
				{
			    int toNeuron = neuronOffset + (oscillatorStep*5);
			    neurons[fromNeuron].addConnection(neurons[toNeuron], -0.5);
				}
		}
		
	}

	private int numRules = 4;
	private void setRuleTopology() {
		// set up oscillator on top.
		double weight = 0.45;
		for (int fromNeuronIt = 0; fromNeuronIt < 5; fromNeuronIt++) {
			int fromNeuron1 = fromNeuronIt;
			int fromNeuron2 = fromNeuronIt + 5;
			int fromNeuron3 = fromNeuronIt + 10;
			int fromNeuron4 = fromNeuronIt + 15;
			int fromNeuron5 = fromNeuronIt + 20;
			int fromNeuron6 = fromNeuronIt + 25;
			for (int toNeuronIt = 0; toNeuronIt < 5; toNeuronIt++) {
				int toNeuron1 = toNeuronIt + 5;
				int toNeuron2 = toNeuronIt + 10;
				int toNeuron3 = toNeuronIt + 15;
				int toNeuron4 = toNeuronIt + 20;
				int toNeuron5 = toNeuronIt + 25;
				int toNeuron6 = toNeuronIt;
				neurons[fromNeuron1].addConnection(neurons[toNeuron1], weight);
				neurons[fromNeuron2].addConnection(neurons[toNeuron2], weight);
				neurons[fromNeuron3].addConnection(neurons[toNeuron3], weight);
				neurons[fromNeuron4].addConnection(neurons[toNeuron4], weight);
				neurons[fromNeuron5].addConnection(neurons[toNeuron5], weight);
				neurons[fromNeuron6].addConnection(neurons[toNeuron6], weight);
			}
		}

		for (int rule = 0; rule < numRules; rule++ ) {
			connectRuleOscillatorToRule(rule);
			connectRuleToRuleOscillator(rule);
		}	
	}


  public void initializeNeurons() {
    //set up topologies.
    createNeurons();
   
    if (topology == 1){
      System.out.println("muddy fact topology ");
      setFactTopology();
    }
    else if (topology == 2){
      System.out.println("muddy rule topology ");
      setRuleTopology();
    }

    else 
    System.out.println("bad toppology specified "+ topology);
  }
 

  private double getTotalWeights(CANTNeuron neuron) {
    double toWeight = 0.0;
    for (int synapse = 0; synapse < neuron.getCurrentSynapses(); synapse ++) {
      toWeight += neuron.synapses[synapse].getWeight();
    }
    return (toWeight);
  }

  

  public void kludge () {
    System.out.println("kludge" + MuddyCANT23.kludge );
    MuddyNet net = (MuddyNet) CANT23.experiment.getNet("Rule1Net");

    if (MuddyCANT23.kludge == 0) {
        for (int i= 90; i < 95; i++) 
          {
            System.out.println(i +  " " +  net.neurons[i].getActivation()); 
          }
      }
    else if (MuddyCANT23.kludge == 2) {
        for (int i= 390; i < 400; i++) 
          {

         double totalWeights = getTotalWeights(net.neurons[i]);
          System.out.println(i + " " + totalWeights + " " +  net.neurons[i].getFatigue()); 
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

