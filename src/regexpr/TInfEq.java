package regexpr;

public class TInfEq implements IInfEq{
	
  private IInfEq infEq ; 	
  public TInfEq(IInfEq infEq)	
  {
  	this.infEq = infEq ;
  }
  
  private long tInfEq = 0 ;
  public boolean infEq(int iExpr1, int iExpr2) throws GCException	 
  {
  	long t0 = System.nanoTime() ;
  	boolean res = infEq.infEq(iExpr1, iExpr2) ;
  	tInfEq += System.nanoTime()  - t0;
  	return res ;
  }
  
  public void printTimes()
  {
  	System.out.println("tInfEq = " 
  		+ util.Time.toString(tInfEq)) ;
  }
  
}