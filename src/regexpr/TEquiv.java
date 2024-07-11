package regexpr;

public class TEquiv implements IEquiv{
	
  private IEquiv eq ; 	
  public TEquiv(IEquiv eq)	
  {
  	this.eq = eq ;
  }
  
  private long tEq = 0 ;
  public boolean eq(int iExpr1, int iExpr2) throws GCException	 
  {
  	long t0 = System.nanoTime() ;
  	boolean res = eq.eq(iExpr1, iExpr2) ;
  	tEq += System.nanoTime()  - t0;
  	return res ;
  }
  
  public void printTimes()
  {
  	System.out.println("tEq = " 
  		+ util.Time.toString(tEq)) ;
  }
  
}