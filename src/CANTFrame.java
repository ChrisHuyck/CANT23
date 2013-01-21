
import java.awt.*;
import java.awt.event.*;

public class CANTFrame extends Frame implements ActionListener, ItemListener{

//The Net that the frame is associated with. It's a 1-1 relationship (though)
//a net may not have a frame.


private  final Color BACKGROUND_COLOR = new Color(100,130,180);

private CANTNet parentNet;
public Matrix matrix;	   //The grid
private Label  timeStepLabel;
private Label  outputLabel;
private Button enterParamButton ;
private Button stepButton ;
private Button startButton ;
private TextField userParamField;
private int userParamType=0; //1=Pattern Type, 2=Net Size, 3 Stimuli Active

private MenuBar bar;
public ScrollPane scrollPane;

private Menu runMenu;
private CANTMenuItem resetRun;
private CANTMenuItem setDelay;
private CANTMenuItem selectPattern;
private CANTMenuItem PatternModifier;
private CANTMenuItem setStimuliActivated;
private CANTMenuItem cyclesPerRun;
private CheckboxMenuItem changeEachTime;
private CheckboxMenuItem spontActPattern;
private CheckboxMenuItem associationTest;
private CheckboxMenuItem allowRunOn;
private CheckboxMenuItem neuronsFatigue;
private CheckboxMenuItem compensatoryLearningOn;
private CheckboxMenuItem learningOn;

private Menu netMenu;
private MenuItem saveNet;
private MenuItem saveAllNets;
private MenuItem readNet;
private MenuItem readAllNets;

private Menu setParametersMenu;
private CANTMenuItem Fatigue_Rate; //The following 8 items in Neuron.java
private CANTMenuItem Fatigue_Recovery_Rate;
private CANTMenuItem Decay;
private CANTMenuItem Learning_Rate;
private CANTMenuItem activationThreshold;
private CANTMenuItem Compensatory_Divisor;
private CANTMenuItem Axonal_Strength_Median;
private CANTMenuItem Saturationbase;
private CANTMenuItem likelihoodOfInhibitoryNeuron; // Decide inhibitory neurons
private CANTMenuItem connectionStrength;
private CANTMenuItem generateNetType;

private Menu measureMenu;
private MenuItem measureCo1;
private MenuItem measureCo2;
private MenuItem printCorrelation;
private MenuItem kludge;


public CANTFrame(CANTNet net,int cols, int rows,boolean isBase) {
  super ("CANT Simulator: "+ net.getName());
  parentNet = net;

  //create the grid
  matrix = new Matrix(cols,rows);

  //Create buttons and other controls
  timeStepLabel = new Label("TStep:            ");

  outputLabel = new Label("Active Neurons:        ");
  userParamField = new TextField(20);
  enterParamButton = new Button("Enter Parameter");
  enterParamButton.addActionListener(this);
  stepButton = new Button("Step");
  stepButton.addActionListener(this);
  startButton = new Button("Start");
  startButton.addActionListener(this);

  //sets up the page arrangement
  setLayout(new BorderLayout());

  //put the controls on the page
  Panel topPanel = new Panel(new FlowLayout());
  topPanel.add(timeStepLabel);
  topPanel.add(userParamField);
  topPanel.add(enterParamButton);
  if (isBase){
    topPanel.add(startButton);
    topPanel.add(stepButton);
  }
  topPanel.add(outputLabel);

  add(topPanel,BorderLayout.NORTH);
  scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
  //scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
  scrollPane.add(matrix);
  matrix.repaint();
  add(scrollPane);
//  add(scrollPane,BorderLayout.CENTER);


  //Create menus
  createMenus();

  addWindowListener(new WindowHandler());
  setBackground(BACKGROUND_COLOR);
  setSize (200+(matrix.getHeight()),
    200+(matrix.getHeight()));
  setLocation(200,0);
  }

   private void createMenus() {
   bar = new MenuBar();

   //execute menu
   runMenu = new Menu ("Execution");
   resetRun = new CANTMenuItem("Reset Run",this,runMenu);
   setDelay = new CANTMenuItem("Set Run Delay",this,runMenu);
   selectPattern = new CANTMenuItem("Select Pattern",this,runMenu);
   setStimuliActivated = new CANTMenuItem("Set Stimuli Number",this,runMenu);
   PatternModifier = new CANTMenuItem("Pattern Modifier ",this,runMenu);
   changeEachTime = new CANTCheckboxMenuItem("Change Pattern Each Time",this,parentNet.isChangeEachTime(),runMenu);
   cyclesPerRun = new CANTMenuItem("Cycles Per Run",this,runMenu);

   runMenu.addSeparator();

   spontActPattern = new CANTCheckboxMenuItem("Spontaneous Activation",
                     this,parentNet.isSpontaneousActivationOn(),runMenu);
   associationTest = new CANTCheckboxMenuItem("Association Test On",this,
                     parentNet.associationTest,runMenu);
   allowRunOn = new CANTCheckboxMenuItem("Allow Run On",this,
                     parentNet.allowRunOn,runMenu);
   neuronsFatigue = new CANTCheckboxMenuItem("Neurons Fatigue",this,
                    parentNet.isNeuronsFatigue(),runMenu);

   runMenu.addSeparator();
   compensatoryLearningOn = new CANTCheckboxMenuItem("Compensatory Learning On",this,
                            parentNet.isCompensatoryLearningOn(),runMenu);
   learningOn = new CANTCheckboxMenuItem("Learning On",this,
                    parentNet.isLearningOn(),runMenu);

  //Net Menu
  netMenu = new Menu ("Net");
  saveNet = new MenuItem("Save Network");
  netMenu.add(saveNet);
  saveNet.addActionListener(this);
  readNet = new MenuItem("Read Network");
  netMenu.add(readNet);
  readNet.addActionListener(this);
  if (parentNet.getName().compareTo("BaseNet") == 0)
    {
    saveAllNets = new MenuItem("Save All Networks");
    netMenu.add(saveAllNets);
    saveAllNets.addActionListener(this);
    readAllNets = new MenuItem("Read All Networks");
    netMenu.add(readAllNets);
    readAllNets.addActionListener(this);
    }


  //Set Parameters Menu
  setParametersMenu = new Menu ("Set Parameters");
  Fatigue_Rate = new CANTMenuItem("Fatigue Rate",this,setParametersMenu);
  Fatigue_Recovery_Rate = new CANTMenuItem("Fatigue Recovery Rate",this,setParametersMenu);
  Decay = new CANTMenuItem("Decay Rate",this,setParametersMenu);
  activationThreshold = new CANTMenuItem("Activation Threshold",this,setParametersMenu);
  Learning_Rate = new CANTMenuItem("Learning Rate",this,setParametersMenu);

  setParametersMenu.addSeparator();
  Compensatory_Divisor = new CANTMenuItem("Compensatory Divisor",this,setParametersMenu);
  Axonal_Strength_Median = new CANTMenuItem("Synaptic Strength Median",this,setParametersMenu);
  Saturationbase = new CANTMenuItem("Saturation Base",this,setParametersMenu);
  likelihoodOfInhibitoryNeuron = new CANTMenuItem("Set Inhibitory Neuron",this,setParametersMenu);
  connectionStrength = new CANTMenuItem("Base Connection Weights",this,setParametersMenu);
  generateNetType = new CANTMenuItem("Generated Network Type",this,setParametersMenu);

  //Measure Menu
  measureMenu = new Menu ("Measure");
  measureCo1 = new CANTMenuItem("Set Time 1",this,measureMenu);;
  measureCo2 = new CANTMenuItem("Set Time 2",this,measureMenu);;
  printCorrelation = new MenuItem("Print Correlation");
  measureMenu.add(printCorrelation);
  printCorrelation.addActionListener(this);
  kludge = new MenuItem("kludge");
  measureMenu.add(kludge);
  kludge.addActionListener(this);

  bar.add(runMenu);
  bar.add(netMenu);
  bar.add(setParametersMenu);
  bar.add(measureMenu);
  setMenuBar(bar);
}


//Catch events from the frame (buttons menus)
public void actionPerformed (ActionEvent evt) {
  if (evt.getSource()==startButton)
    start();
  else if (evt.getSource()==enterParamButton)
    enterUserParameter();
  else if (evt.getSource()==stepButton) {
    CANT23.setRunning(false);
    //need to call run all one step to update all nets.
    parentNet.runAllOneStep(CANT23.CANTStep);
	CANT23.CANTStep++;
    //CANT23.runOneStep(CANT23.CANTStep);
  }
   else if (evt.getSource()== resetRun) {
      //set the run state to the initial state
     //reset run does not reset the weights of synapses abd the currentpattern
          parentNet.clear();
          CANT23.setRunning(false);
          CANT23.CANTStep=0;
          matrix.clear();
          timeStepLabel.setText("TStep:  " + CANT23.CANTStep);
          outputLabel.setText("Active Neurons: " );
          repaint();
   }
  else if (evt.getSource()== setDelay) {
    userParamField.setText(Integer.toString(CANT23.delayBetweenSteps));
    userParamType=23;
  }
  else if (evt.getSource() == saveNet ) {
    parentNet.write();
  }
  else if (evt.getSource() == saveAllNets ) {
	CANT23.saveAllNets();      
  }
  else if (evt.getSource() == readNet ) {
    parentNet.readNet(false);  //crhz undone should be true
  }
  else if (evt.getSource() == readAllNets ) {
    parentNet.readAllNets();
	
    //I shouldn't need to do this but should merely be able to call
    //parentNet.readBetweenAllNets();
    //However the dynamic binding seems to fail and always call the
    //CANTNet version, so I do this.
    //  	if (parentNet instanceof CANTNetParse1) 
    //{
    //System.out.println("Parse1 Net");    
    //   CANTNetParse1 net = (CANTNetParse1)parentNet;
    //   net.readBetweenAllNets();
    //}  
    //else if (parentNet instanceof CANTNet) 
    // {
    //System.out.println("CANT Net");      
   parentNet.readBetweenAllNets();
   // }
  }

   else if (evt.getSource()== selectPattern) {
      userParamField.setText(
	     Integer.toString(parentNet.getCurrentPattern()));
      userParamType=1;
   }
   else if (evt.getSource() == setStimuliActivated ) {
      userParamField.setText(Integer.toString(parentNet.getNeuronsToStimulate()));
      userParamType=3;
   }
   else if (evt.getSource()== Fatigue_Rate ) {
      userParamField.setText(Float.toString(parentNet.getFatigueRate()));
      userParamType=4;
   }
   else if (evt.getSource()== Decay ) {
      userParamField.setText(Float.toString(parentNet.getDecay()));
      userParamType= 5;
   }
   else if (evt.getSource()== Fatigue_Recovery_Rate ) {
      userParamField.setText(Float.toString(parentNet.getFatigueRecoveryRate()));
      userParamType=6;
   }
   else if (evt.getSource()== Axonal_Strength_Median ) {
      userParamField.setText(Double.toString(parentNet.getAxonalStrengthMedian()));
      userParamType=7;
   }
   else if (evt.getSource()== Learning_Rate ) {
      userParamField.setText(Float.toString(parentNet.getLearningRate()));
      userParamType=8;
   }
   else if (evt.getSource()== Compensatory_Divisor ) {
      userParamField.setText(Integer.toString(parentNet.getCompensatoryDivisor()));
      userParamType=9;
   }
   else if (evt.getSource()== Saturationbase ) {
      userParamField.setText(Float.toString(parentNet.getSaturationBase()));
      userParamType=10;
   }
   else if (evt.getSource()== likelihoodOfInhibitoryNeuron) {
      userParamField.setText(
             Integer.toString(parentNet.getLikelihoodOfInhibitoryNeuron()));
      userParamType=11;
   }
   else if (evt.getSource()== cyclesPerRun) {
      userParamField.setText(
             Integer.toString(parentNet.getCyclesPerRun()));
      userParamType=12;
   }
   else if (evt.getSource()== activationThreshold) {
      userParamField.setText(Double.toString(parentNet.getActivationThreshold()));
      userParamType=13;
   }
   else if (evt.getSource()== connectionStrength) {
      userParamField.setText(Double.toString(parentNet.getConnectionStrength()));
      userParamType=14;
   }
   else if (evt.getSource()== measureCo1) {
      userParamField.setText(Integer.toString(parentNet.measure.MeasureCo1));
      userParamType=17; }
   else if (evt.getSource()== measureCo2) {
      userParamField.setText(Integer.toString(parentNet.measure.MeasureCo2));
      userParamType=18; }
   else if (evt.getSource()== printCorrelation) {
      String OutputString = "Step " + parentNet.measure.MeasureCo1 + " and Step " +
         parentNet.measure.MeasureCo2 + " ";
      OutputString = OutputString + parentNet.measure.Measure() + "\n";
      System.out.println(OutputString);      
	  }
   else if (evt.getSource()== kludge) {
      String OutputString = "kludge ";
      parentNet.kludge();      
     }
   else if (evt.getSource()== PatternModifier) {
      //UserParamField.setText(Integer.toString(parentNet.inputPattern.patternModifier));
      userParamType=25;
   }
   else
      System.out.print ( "uncaught user event\n");
}


public void itemStateChanged(ItemEvent evt) {

   if (evt.getSource() == changeEachTime ) {
           if (! this.changeEachTime.getState()) {
         parentNet.setChangeEachTime(false);
      }
      else {
         parentNet.setChangeEachTime(true);
      }
   }

   else if (evt.getSource() == allowRunOn ) {
      if (! this.allowRunOn.getState()) {
         parentNet.allowRunOn=false;
      }
      else {
         parentNet.allowRunOn=true;
      }
   }
   else if (evt.getSource() == neuronsFatigue) {
      if (! this.neuronsFatigue.getState()) {
         parentNet.setNeuronsFatigue(false);
      }
      else {
         parentNet.setNeuronsFatigue(true);
      }
   }
   else if (evt.getSource() == spontActPattern ) {
      if (! this.spontActPattern.getState()) {
         parentNet.setSpontaneousActivationOn(false);
      }
      else {
         parentNet.setSpontaneousActivationOn(true);
      }
   }
   else if (evt.getSource() == compensatoryLearningOn ) {
      if (! this.compensatoryLearningOn.getState()) {
             parentNet.setCompensatoryLearningOn(false);
      }
      else {
          parentNet.setCompensatoryLearningOn(true);
      }
   }
   else if (evt.getSource() == learningOn ) {
      if (! this.learningOn.getState()) {
         parentNet.setLearningOn(false);
      }
      else {
         parentNet.setLearningOn(true);
      }
   }

   else System.out.print ( "uncaught user item event\n");
}


private void start() {
    CANT23.setRunning(true);
}

//Convert a positive integer string to a string.  Returns -1 on failure
private int stringToInteger(String Str){
   int IntValue;
   try{
      IntValue = (new Integer (Str)).intValue();
   }

   catch(NumberFormatException e) {
      return -1;
   }

   return IntValue;
}

//Depending on the Parameter Type (set internally) Change the Parameter.
//If it fails leave a message in the text box.
private void enterUserParameter() {

   if (userParamType == 1) {
      int NewPatternType = stringToInteger(userParamField.getText());
      if (parentNet.selectPattern(NewPatternType))
         userParamType = 0;
      else
         userParamField.setText("Error Incorrect Pattern Number");
   }

   //undone this won't work now 7/4
   else if (userParamType == 2) {
      int ColSize = stringToInteger(userParamField.getText());
      if ((ColSize > 0) && (ColSize <= 100))
     {
//         NewCols = ColSize;
         userParamType = 0;
     }
   }

   else if (userParamType == 3) {
      int NeuronsActivated = stringToInteger(userParamField.getText());
      if (NeuronsActivated >= 0)
         {
          parentNet.setNeuronsToStimulate(NeuronsActivated);
          userParamType = 0;
         }
      else
         userParamField.setText("Error Expecting Integer Below 200 Neurons Activated");
   }

      /*anita add the following program */
   else if (userParamType ==4)  {

      String text4 = (userParamField.getText());
      float Fatigue = Float.parseFloat(text4);
      if ((Fatigue>=0) && (Fatigue<=4))
          parentNet.setFatigueRate(Fatigue);
      }

   else if (userParamType ==5)  {
      float Decay = Float.parseFloat(userParamField.getText());
      if (( Decay >= 1.0) && ( Decay <= 50.0))
          parentNet.setDecay(Decay);
      else
         userParamField.setText("Error Expecting Float Between 1 and 50 Decay");
      }

   else if (userParamType ==6)  {
      float Fatigue_Recovery_Rate = Float.parseFloat(userParamField.getText());
      if ((Fatigue_Recovery_Rate >=0) && (Fatigue_Recovery_Rate<=4))
          parentNet.setFatigueRecoveryRate(Fatigue_Recovery_Rate);
      }

   else if (userParamType ==7)  {
      float Axonal_Strength_Median = Float.parseFloat(userParamField.getText());
      if ((Axonal_Strength_Median>=0) && (Axonal_Strength_Median<=2))
         parentNet.setAxonalStrengthMedian(Axonal_Strength_Median);

      }

   else if (userParamType ==8)  {
      float Learning_Rate = Float.parseFloat(userParamField.getText());
      if ((Learning_Rate>=0) && (Learning_Rate<=2))
          parentNet.setLearningRate(Learning_Rate);
      }

   else if (userParamType ==9)  {
      int Compensatory_Divisor = Integer.parseInt(userParamField.getText());
      if ((Compensatory_Divisor > 0) && (Compensatory_Divisor <= 40))
          parentNet.setCompensatoryDivisor(Compensatory_Divisor);
      }

   else if (userParamType == 10)  {
      float Saturationbase = Float.parseFloat(userParamField.getText());
      if (( Saturationbase>=0) && ( Saturationbase<=60))
          parentNet.setSaturationBase(Saturationbase);
     }

     else if (userParamType == 11)  {
      int inhibition =Integer.parseInt(userParamField.getText());
      if (( inhibition>=0) && ( inhibition <=100))
          parentNet.setLikelihoodOfInhibitoryNeuron(inhibition);
     }
     else if (userParamType == 12)  {
      int Cycles = Integer.parseInt(userParamField.getText());
      if (( Cycles>= 1) && ( Cycles<= 1000))
          parentNet.setCyclesPerRun(Cycles);
     }

    else if (userParamType == 13)  {
      double Threshold = Float.parseFloat(userParamField.getText());
      if (( Threshold >= 0.0) && ( Threshold <= 20.0))
          parentNet.setActivationThreshold(Threshold);
     else
        userParamField.setText("Error Expecting Float Below 20 Threshold");   }

    else if (userParamType ==14)  {
      float conn_Str = Float.parseFloat(userParamField.getText());
      if (( conn_Str >= 0.01) && ( conn_Str <= 0.6))
          parentNet.setConnectionStrength(conn_Str);
     }
    else if (userParamType == 17)  {
     int measureCycle = Integer.parseInt(userParamField.getText());
     if ((measureCycle>= 1) && ( measureCycle<= 1000))
         parentNet.measure.MeasureCo1 = measureCycle;
    }
    else if (userParamType == 18)  {
     int measureCycle = Integer.parseInt(userParamField.getText());
     if ((measureCycle>= 1) && ( measureCycle<= 1000))
         parentNet.measure.MeasureCo2 = measureCycle;
    }

    else if (userParamType ==22)  {
      if (userParamField.getText() == "Y")
        parentNet.allowRunOn = true;
      else
        parentNet.allowRunOn = false;
     }
    else if (userParamType ==23)  {
      CANT23.delayBetweenSteps = Integer.parseInt(userParamField.getText());
    }
    else if (userParamType ==24)  {
      if (userParamField.getText() == "Y")
        parentNet.setNeuronsFatigue(true);
      else
        parentNet.setNeuronsFatigue(false);
     }

     else if (userParamType ==25)  {
      int patternModifier1 = Integer.parseInt(userParamField.getText());
      if (( patternModifier1>= 0) && ( patternModifier1 <= 30000)){
        //undone pattern m odifier
        }
     }

   else
      System.out.print ( "Bad Parameter Type Value\n");

}
    public void paint(Graphics __g) {
        setBackground(BACKGROUND_COLOR);
        matrix.repaint();
    }

    private void updateMatrix() {
        int rows=matrix.rows();
        int cols=matrix.cols();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int neuronNum = (row*cols) + col;
                if (parentNet.neurons[neuronNum].getFired())
                    matrix.setState(col,row,1);
                else
                    matrix.setState(col,row,0);
             }
        }
    }
    public void runOneStep(int cantStep) {
        timeStepLabel.setText("TStep:  " + cantStep);
        outputLabel.setText("Active Neurons: " + parentNet.getActives());
        updateMatrix();
        repaint();
        try{
           Thread.sleep(CANT23.delayBetweenSteps);
        }
        catch(InterruptedException e) {
           System.out.print ( e.toString() + " \n");
        }
    }
    private class WindowHandler extends WindowAdapter{
            public void windowClosing(WindowEvent e){
                   e.getWindow().dispose();
                   System.exit(1);

            }
    }

}//end of CantFrame class
