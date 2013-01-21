
//This synapse additionally keeps track of recent firing behaviours to 
//use this in learning.
public class MemSynapse extends Synapse{
	
	private int prePostCoFired;
	private int preButNotPostFired;
	private int postButNotPreFired;
	private int neitherFired;
	
	public int getCoFirings() {return prePostCoFired;}

	public void resetCoFirings() {
		prePostCoFired = 0;
		preButNotPostFired = 0;
		postButNotPreFired = 0;
		neitherFired = 0;		
	}
	
	public MemSynapse (CANTNet net,CANTNeuron from,CANTNeuron to,double wt) {
		super(net,from,to,wt);
		resetCoFirings();
	}
	
	public void setFirings(){
		boolean toFired = toNeuron.getFired();
		boolean fromFired = fromNeuron.getFired();
		if (fromFired && toFired) prePostCoFired++;
		else if (fromFired && !toFired) preButNotPostFired++;
		else if (!fromFired && toFired) postButNotPreFired++;
		else neitherFired++;
		
	}

}
