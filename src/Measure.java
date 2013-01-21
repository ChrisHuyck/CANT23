
//This class is for measuring nets.  It stores firing behavior
//and uses a Pearson's measurement.


public class Measure {
  private int size;
  public int MeasureCo1 = (int) 20;
  public int MeasureCo2 = (int) 170;
  private int FireNodes[] [] ;
  private static final int CYCLESREMEMBERED = 4000;
  
  private int getSize() {return size;}
  public void setMeasure1(int value) {MeasureCo1 = value%CYCLESREMEMBERED;}
  public void setMeasure2(int value) {MeasureCo2 = value%CYCLESREMEMBERED;}
  
  public Measure(int numNeurons) {
  	size = numNeurons;
    FireNodes = new int[CYCLESREMEMBERED][getSize()];
  }

  public int CountFireNodes (int Cycle){
    int Result = 0;
   
    for ( int counter = 0; counter <getSize(); counter++ )
      if (FireNodes[Cycle][counter] == 1)
	    Result++;
    return Result;
  }

  public double Measure(){
    double Correlation;
    double Sxx =0;
    double Syy =0;
    double Sxy =0;
    double MeanS;
    double MeanY;
    double Sx=0;
    double Sy=0;
	 
//    if (!RecordingActivation) {
//      System.out.print("Recording Activation Off\n");
//    }
   
    for ( int counter = 0; counter <getSize(); counter++ )
      Sx += FireNodes[MeasureCo1][counter];
    MeanS = Sx/getSize();

    for ( int counter = 0; counter < getSize(); counter++ )
      { Sy += FireNodes[MeasureCo2][counter]; }
    MeanY = Sy/getSize();

    for ( int counter = 0; counter <getSize(); counter++ ) {
      Sxx += (FireNodes[MeasureCo1][counter]- MeanS)*(FireNodes[MeasureCo1][counter]- MeanS);
      Syy += (FireNodes[MeasureCo2][counter]- MeanY)*(FireNodes[MeasureCo2][counter]- MeanY);
      Sxy += (FireNodes[MeasureCo1][counter]- MeanS)*(FireNodes[MeasureCo2][counter]- MeanY); }

    Correlation = Sxy/(Math.sqrt (Sxx*Syy));
	return Correlation;
  }
  
  public void setActiveState(int Step, int nodeNum, int value) {
    FireNodes[Step%CYCLESREMEMBERED][nodeNum] = value;
  }

}