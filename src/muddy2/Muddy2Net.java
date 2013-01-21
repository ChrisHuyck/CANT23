


import java.util.Enumeration;

public class Muddy2Net extends CANTNet {
  
  public Muddy2Net(){
  }
  
  public Muddy2Net(String name,int cols, int rows,int topology){
    super(name,cols,rows,topology);
    //cyclesToStimulatePerRun = 20;
  }

  //need this to subclass experiment
  public void changePattern(int cantStep)
    {
	int testFlavor = 13;
	int fact1;
	int fact2;
	int fact3;
	
    //note this only runs on the first step.
	//change curPattern for initial state of each net.
	  //for q1+ +p1-p2-p3 0,0,0,      0,0,0   -> 0
	  //for q1+ -p1+p2-p3 0,0,0,      1,1,1   -> 1
	  //for q1+ -p1-p2+p3 0,0,0,      2,2,2   -> 2
	  //for q1+ +p1+p2-p3 0,0,0,      1,0,3   -> 3
	  //for q1+ +p1-p2+p3 0,0,0,      2,3,0   -> 4
	  //for q1+ -p1+p2+p3 0,0,0,      3,2,1   -> 5
	  //for q1+ +p1+p2+p3 0,0,0,      3,3,3   -> 6
	  //for q1  +p1-p2-p3             4,5,5   -> 7  
	  //for q1  -p1+p2-p3             5,4,5   -> 8  
	  //for q1  -p1-p2+p3             5,4,5   -> 9  
	  //for q2  +p1+p2-p3             6,6,7   -> 10
	  //for q2  +p1-p2+p3             6,7,6   -> 11
	  //for q2  -p1+p2+p3             7,6,6   -> 12
	  //for q3  +p1+p2+p3             8,8,8   -> 13
	if  ((testFlavor >= 0) && (testFlavor <=2)) fact1 = fact2 = fact3 = testFlavor;
    else if  (testFlavor == 3) {fact1 = 1; fact2 = 0; fact3 = 3;}
    else if  (testFlavor == 4) {fact1 = 2; fact2 = 3; fact3 = 0;}
    else if  (testFlavor == 5) {fact1 = 3; fact2 = 2; fact3 = 1;}
    else if  (testFlavor == 6) {fact1 = 3; fact2 = 3; fact3 = 3;}
    else if  (testFlavor == 7) {fact1 = 4; fact2 = 5; fact3 = 5;}
    else if  (testFlavor == 8) {fact1 = 5; fact2 = 4; fact3 = 5;}
    else if  (testFlavor == 9) {fact1 = 5; fact2 = 5; fact3 = 4;}
    else if  (testFlavor == 10) {fact1 = 6; fact2 = 6; fact3 = 7;}
    else if  (testFlavor == 11) {fact1 = 6; fact2 = 7; fact3 = 6;}
    else if  (testFlavor == 12) {fact1 = 7; fact2 = 6; fact3 = 6;}
    else if  (testFlavor == 13) {fact1 = 8; fact2 = 8; fact3 = 8;}
    else {
    	System.out.println(" Error in Muddy2Net change pattern");
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
      else if (getName().compareTo("ClockNet") == 0){
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
    Muddy2Net net = (Muddy2Net) eNum.nextElement();
    Muddy2Net baseNet = net;
    Muddy2Net outputNet = net;

    System.out.println("Undone Muddy read Between");

  }
  
  
  
  public void runAllOneStep(int CANTStep) {
    //This series of loops is really chaotic, but I needed to
    //get all of the propogation done in each net in step.
    Muddy2CANT23.runOneStepStart();
	
    Enumeration <?> eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
      //net.runOneStep(CANTStep);
      net.changePattern(CANTStep);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
      net.setExternalActivation(CANTStep);
    }
    
    //net.propogateChange();  
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
      net.setNeuronsFired();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
      net.setDecay ();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
      net.spreadActivation();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
      net.setFatigue();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
      net.learn();
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
      net.cantFrame.runOneStep(CANTStep+1);
    }
    eNum = CANT23.nets.elements();
    while (eNum.hasMoreElements()) {
      Muddy2Net net = (Muddy2Net)eNum.nextElement();
      if (net.recordingActivation) net.setMeasure(CANTStep); 	  
//if (net.getName().compareTo("VerbNet") == 0)   System.out.println(net.neurons[0].getFatigue() +   " verb Neuron " + net.neurons[0].getActivation());
    }
  }


  public CANTNet getNewNet(String name,int cols, int rows,int topology){
    Muddy2Net net = new Muddy2Net (name,cols,rows,topology);
    return (net);
  } 

	// This works by having the fact(s) constantly activating the rule.
	// The weight gradually increases over time, but most of the weight
	// comes from the rule oscillator. So the facts will eventually give ~ .5
	// and ~.4 by cycle 18. The rule oscillator gives 1.81
	private int factNeurons = 30;
	private double fullWeight = 0.05;

	private void connectFactToRule(Muddy2Net ruleNet, int factNum, int ruleNum,
			int factsNeeded) {
		double weightForFact = fullWeight / factsNeeded;

		for (int offset = 0; offset < factNeurons; offset++) {
			int fromNeuron = factNum * factNeurons + offset;
			int toNeuron = offset % 5;
			toNeuron += ruleStart + (ruleSize * ruleNum);
			neurons[fromNeuron].addConnection(ruleNet.neurons[toNeuron],weightForFact);
		}
	}

	public void connectFactToRuleNet(Muddy2Net ruleNet) {
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
		
		//new for Muddy2
		connectFactToRule(ruleNet, 8, 4, 3); // q1+  (-p2-p3)  -> q1 and p1 
		connectFactToRule(ruleNet, 5, 4, 3); // (q1+) -p2(-p3) -> q1 and p1 
		connectFactToRule(ruleNet, 7, 4, 3); // (q1+ -p2)-p3   -> q1 and p1
		connectFactToRule(ruleNet, 9, 5, 2); // q2+ (-p2) -> p1 q2 (stop q2+)
		connectFactToRule(ruleNet, 5, 5, 2); // (q2+) -p2 -> p1 q2 (stop q2+)
		connectFactToRule(ruleNet, 9, 6, 2); // q2+ (-p3) -> p1 q2 (stop q2+)
		connectFactToRule(ruleNet, 7, 6, 2); // (q2+) -p3 -> p1 q2 (stop q2+)
		
		
		//Temporal rules.
		connectFactToRule(ruleNet, 8, temporalRuleStart, 3); //q1+, -k2p2, k3p3 -> q2+ (stop q1+)
		connectFactToRule(ruleNet, 9, temporalRuleStart+1, 3); //q2+, -k2p2, k3p3 -> q3 (stop q2+)	
	}

	// The rule turns on the fact by starting the first bit.
	private void connectRuleToFact(Muddy2Net factNet, int ruleNum, int factNum) {
		for (int offset = 0; offset < 5; offset++) {
			int fromNeuron = ruleStart + (ruleNum * ruleSize) + offset;
			int toNeuron = offset + (factNum * factNeurons);
			neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], 2.3);
		}
	}

	private void connectRuleToStopFact(Muddy2Net factNet, int ruleNum, int factNum) {
		for (int offset = 0; offset < 5; offset++) {
			int fromNeuron = ruleStart + (ruleNum * ruleSize) + offset;
			for (int toOffset = 0; toOffset < 30; toOffset+=5) {
			  int toNeuron = offset + (factNum * factNeurons) + toOffset;
			  neurons[fromNeuron].addConnection(factNet.neurons[toNeuron], -2.3);
			}
		}
	}

	public void connectRuleToFactNet(Muddy2Net factNet) {
		connectRuleToFact(factNet, 0, 0);// connect rule 0 to fact o (p1).
		connectRuleToFact(factNet, 1, 0);// connect rule 1 to fact o (p1).
		connectRuleToFact(factNet, 2, 0);// connect rule 2 to fact o (p1).
		connectRuleToFact(factNet, 3, 0);// connect rule 3 to fact o (p1).
		connectRuleToFact(factNet, 4, 0);// q1+-p2-p3->p1
		connectRuleToFact(factNet, 4, 1);// q1+-p2-p3->q1
		connectRuleToStopFact(factNet, 4, 8);// q1+-p2-p3->stop(q1+)
		connectRuleToStopFact(factNet, 4, 10);// q1+-p2-p3->stop(-K1p1)
		connectRuleToFact(factNet, 5, 0); //      q2+ -p2 -> p1   q2 stop q2+)
		connectRuleToFact(factNet, 5, 2); //      q2+ -p2 -> (p1) q2 (stop q2+)
		connectRuleToStopFact(factNet, 5, 9);//  q2+ -p2 -> (p1 q2) stop q2+
		connectRuleToFact(factNet, 6, 0); //      q2+ -p3 -> p1   q2 stop q2+)
		connectRuleToFact(factNet, 6, 2); //      q2+ -p3 -> (p1) q2 (stop q2+)
		connectRuleToStopFact(factNet, 6, 9);//  q2+ -p3 -> (p1 q2) stop q2+
			
		connectRuleToFact(factNet, temporalRuleStart, 9);// q1+k2p2+k3p3->q2+ stop q1+
		connectRuleToStopFact(factNet, temporalRuleStart, 8);//q1+k2p2+k3p3->q2+ stop q1+
		connectRuleToFact(factNet, temporalRuleStart+1, 3);//q2+, -k2p2, k3p3 -> q3 (stop q2+)
		connectRuleToStopFact(factNet, temporalRuleStart+1, 9);//q2+, -k2p2, k3p3 -> (q3) stop q2+
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

  private int numFacts = 11;
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

	private int numRules = 7;
	//rule 0-3 from Muddy
	//rule 0 if q3 -> p1  (these notes are from agent1s perspective but work for all three.
	//rule 1 if q1 -p2 -p3 -> p1
	//rule 2 if q2 -p2 -> p1
	//rule 3 if q2 -p3 -> p1
	//rule 4 if q1+, -p2 and -p3 -> p1 q1 (stop q1+).
	//rule 5 if q2+, -p2 -> p1 q2 (stop q2+)
	//rule 6 if q2+, -p3 -> p1 q2 (stop q2+)
	private int temporalRuleStart = 9;
	//rule 9 temporal if q1+, -k2p2, k3p3 -> q2+ (stop q1+)
	//rule 10 temporal if q2+, -k2p2, k3p3 -> q3 (stop q2+)
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


	public void connectFactToOtherRuleNet(Muddy2Net otherRuleNet) {
		//if q1+, -k2p2, k3p3 -> q2+ (stop q1+)  -k2p2 stimualtes r1.rule5
		connectFactToRule(otherRuleNet, 10, temporalRuleStart, 3); // q1+  -k2p2 -k3p3  -> q2+ and (stop q1+)
		//if q2+, -k2p2, k3p3 -> q3 (stop q2+)
		connectFactToRule(otherRuleNet, 10, temporalRuleStart+1, 3); // q1+  -k2p2 -k3p3  -> q2+ and (stop q1+)
	}
		
		private int numClockSteps = 50;

	// On the penultimate clock step, send activity to the rule net.
	// On the ultimate step, erase that activity. It's like the rule oscillator.
	public void connectClockToRule(Muddy2Net ruleNet, int ruleNum) {
		double weight = 1.81;
		for (int neuronOffset = 0; neuronOffset < 5; neuronOffset++) {
			int fromNeuron1 = ((numClockSteps - 2) * 5) + neuronOffset;
			int toNeuron = ruleStart + (ruleNum * ruleSize) + neuronOffset;
			neurons[fromNeuron1].addConnection(ruleNet.neurons[toNeuron],
					weight);
			int fromNeuron2 = fromNeuron1+5;
			neurons[fromNeuron2].addConnection(ruleNet.neurons[toNeuron],
					-weight / 1.12); // Decay = 1.12
		}
	}

	// connect all temporal rules to the clock
	public void connectClockToRuleNet(Muddy2Net ruleNet) {
		connectClockToRule(ruleNet, temporalRuleStart);
		connectClockToRule(ruleNet, temporalRuleStart+1);
	}

	//This just sets up a clock that goes through sets of 5 up to limit 
	//then restarts.
	private void setClockTopology() {
		// set up oscillator on top.
		double weight = 0.45;
		for (int step = 0; step < numClockSteps; step++) {
			for (int fromNeuronIt = 0; fromNeuronIt < 5; fromNeuronIt++) {
				int fromNeuron = fromNeuronIt + (5*step);
				for (int toNeuronIt = 0; toNeuronIt < 5; toNeuronIt++) {
					int toStep = (step +1)%numClockSteps;
					int toNeuron = toNeuronIt + (5 * toStep);
					neurons[fromNeuron].addConnection(neurons[toNeuron],weight);
				}
			}
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
    else if (topology == 3){
        System.out.println("muddy clock topology ");
        setClockTopology();
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
    System.out.println("kludge" + Muddy2CANT23.kludge );
    Muddy2Net net = (Muddy2Net) CANT23.experiment.getNet("Rule1Net");

    if (Muddy2CANT23.kludge == 0) {
        for (int i= 390; i < 395; i++) 
          {
            System.out.println(i +  " " +  net.neurons[i].getActivation()); 
          }
      }
    else if (Muddy2CANT23.kludge == 2) {
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

