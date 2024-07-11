package regexpr;

public class TMakeDFA implements IMakeDFA{
	
  private IMakeDFA mDFA ; 	
  public TMakeDFA(IMakeDFA mDFA)	
  {
  	this.mDFA = mDFA ;
  }
  
  private long tComputeAllDeriv = 0 ;
  public void computeAllDeriv(int iExpr) throws GCException	 
  {
  	long t0 = System.nanoTime() ;
  	mDFA.computeAllDeriv(iExpr) ;
  	tComputeAllDeriv += System.nanoTime()  - t0;
  }
  
  public void printTimes()
  {
  	System.out.println("tComputeAllDeriv = " 
  		+ util.Time.toString(tComputeAllDeriv)) ;
  }
  
}