package regexpr;
import syntax.* ;

public class InfEq extends AChecks implements IInfEq, ICheck{
	
	public InfEq(IExpressions exprs){
		super(exprs, true) ;
	}
	
	boolean unCompatible(int[] tabD1, int[] tabD2) 
	{
		boolean compatible = true ;
		  
		int x = 0 ;
		while (x != tabD1.length && compatible)
		{
			compatible = 
			    tabD1[x] == exprs.zero() || exprs.notZero(tabD2[x]) ; 				             
			x ++ ;
		}
		
		return ! compatible ;
	}
	
  public boolean infEq(int iExpr1, int iExpr2) throws GCException
  {  	
  	return check(iExpr1, iExpr2) ;
  }
  
  public boolean compare(int iExpr1, int iExpr2) throws GCException
  {
  	return infEq(iExpr1, iExpr2) ;
  }
  
} 