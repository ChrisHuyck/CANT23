
import java.awt.*;

public class Matrix extends Panel{

  private static final int TOP_BORDER=10, LEFT_BORDER=10;
  public static final int SQUARE_SIZE = 6;//12;

  private int NUM_LABELS = 70;
  private int cols,rows;
  private int[][] cells;
  private String[] textToPrint;
  private int [] textPosToPrint=new int[NUM_LABELS];
  private int textHPosToPrint;
  private int numTexts=0;


    public Matrix (int Cols, int Rows) {
      cols = Cols;
      rows = Rows;
      this.setBounds(LEFT_BORDER, TOP_BORDER, cols*SQUARE_SIZE, rows*SQUARE_SIZE);
      cells=new int [cols][rows];
      textToPrint=new String[NUM_LABELS];
      textPosToPrint[0] = -1;
      textHPosToPrint = -1;
      clear();
    }
	
	//need this to get the scroll bars working.
	public Dimension getPreferredSize()
	{
	  return new Dimension (LEFT_BORDER+(cols*SQUARE_SIZE), 
	                        TOP_BORDER+(rows*SQUARE_SIZE) + 25 );
	}
	
    public void clear () {
      for (int i =0;i <cols; i++)
      for (int j =0;j <rows; j++)
	      cells[i][j]=0;
    }
    public int setState(int x, int y, int state) {
      if (x< cols && y< rows && x>-1 && y>-1) {
        cells[x][y]=state;
        return state;
      }
      return 0;
    }


  public void addStringsToPrint(String s, int vPos, int hPos) 
  {
    try {
      textToPrint[numTexts] = new String(s);
      textPosToPrint[numTexts] = vPos;
      textHPosToPrint = hPos;
      numTexts++;
    }
   catch (Exception e) {
    System.err.println("add Strings in matrix v h died" + e.toString());
    System.exit(1);  }  
  }

  public void addStringsToPrint(String s, int vPos) 
  {
    try {
      textToPrint[numTexts] = new String(s);
      textPosToPrint[numTexts] = vPos;
      numTexts++;
    }
   catch (Exception e) {
    System.err.println("add strings in matrix v died" + e.toString());
    System.exit(1);  }  
  }

  public void addStringsToPrint(String s) 
  {
    try {
      textToPrint[numTexts] = new String(s);
      numTexts++;
    }
    catch (Exception e) {
     System.err.println("add Strings in matrix died" + e.toString());
     System.exit(1);  }
  }
	  
  private void printStrings(Graphics g) 
  {
  	for (int i = 0; i < numTexts;i++)
	{
//System.out.println("prints "+textToPrint[i]+textPosToPrint[i]+i);
      g.setColor(Color.black);
      if (textHPosToPrint > 0)
	g.drawString(textToPrint[i],textHPosToPrint,textPosToPrint[i]);
      else if (textPosToPrint[0] > 0)
	g.drawString(textToPrint[i],400,textPosToPrint[i]);
      else	
	g.drawString(textToPrint[i],400,360*(i+1));
      }
  }

    public void paint(Graphics g) {

      g.setColor(Color.white);
      g.fillRect(LEFT_BORDER, TOP_BORDER, cols*SQUARE_SIZE, rows*SQUARE_SIZE);
      g.setColor(Color.blue);
      g.drawRect(LEFT_BORDER, TOP_BORDER, cols*SQUARE_SIZE, rows*SQUARE_SIZE);

      //set the color for drawing circles

            g.setColor(Color.blue.darker());
      //draw circles if the cell is on.
      for (int ycnt=0; ycnt<rows; ycnt++) {
        for (int xcnt=0; xcnt<cols; xcnt++) {
          if (cells[xcnt][ycnt]!=0) {
            g.fillOval(LEFT_BORDER+(xcnt*SQUARE_SIZE),
			   TOP_BORDER+(ycnt*SQUARE_SIZE),
               SQUARE_SIZE,SQUARE_SIZE);
          }
        }
      }
      //this creates the grid
      int acrossA=LEFT_BORDER; //leftborder
      int acrossB=acrossA + (cols*SQUARE_SIZE);  //matches LeftBorder of Bacllayout
      int downA=TOP_BORDER;   //matches top border lentgh
      int downB=downA + (rows*SQUARE_SIZE);

      g.setColor(Color.blue);
      //draw horizontal lines
      for (int row=1; row < rows; row++) 
	    {
        if ((row%10) == 0) 
          g.setColor(new Color(210,23,45));
		int j = downA + (row*SQUARE_SIZE);  
        g.drawLine(acrossA,j,acrossB,j);
        g.setColor(Color.blue);
      }

      //draw vertical lines
      for(int col=0; col < cols; col++) {
        if ((col % 10) == 0)
          g.setColor(Color.red);
        int i = acrossA + (SQUARE_SIZE*col);
        g.drawLine(i,downA,i,downB);
        g.setColor(Color.blue);
      } 
	  
	  printStrings(g);
    }

    public int rows() { return rows; }
    public int cols() { return cols; }
/*
    public Dimension getSize() {
      return new Dimension((cols*SQUARE_SIZE)+LEFT_BORDER,(rows*SQUARE_SIZE)+TOP_BORDER);
    }
*/
    public int getHeight() {
      return rows*SQUARE_SIZE;
    }
    public int getWidth() {
      return cols*SQUARE_SIZE;
    }

}