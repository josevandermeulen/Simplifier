package regexpr;
import syntax.* ;

public class Equiv extends AChecks implements IEquiv, ICheck{
	
	public Equiv(IExpressions exprs)
	{
		this(exprs, true) ;
	}	
	
	Equiv(IExpressions exprs, boolean usesDejaVu)
	{
		super(exprs, usesDejaVu) ;
	}	
	
  boolean unCompatible(int[] tabD1, int[] tabD2) 
	{
		boolean compatible = true ;
		  
		int x = 0 ;
		while (x != tabD1.length && compatible)
		{
			compatible = 
			    (exprs.notZero(tabD1[x]) && exprs.notZero(tabD2[x]))
			    ||(tabD1[x] == exprs.zero() && tabD2[x] == exprs.zero()) ; 			             
			x ++ ;
		}
		
		return ! compatible ;
	}
		
	public boolean eq(int iExpr1, int iExpr2) throws GCException
  {
  	boolean check = check(iExpr1, iExpr2) ;
  	
  	//if (check)
  	//	((Expressions)exprs).unifyPairs() ;
  	//else
  	
  	exprs.collectPairs() ;
  	

  	
  	return check ;
  }
	
	public boolean compare(int iExpr1, int iExpr2) throws GCException
  {
  	return eq(iExpr1, iExpr2) ;
  }
}