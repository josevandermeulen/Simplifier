package regexpr;
import syntax.* ;

public class InfEqA extends ChecksA {
	
	public InfEqA(IExpressions exprs){
		super(exprs) ;
	}
   
  
  public boolean infEq(int iExpr1, int iExpr2) throws GCException
  {  	
  	return checkPD(iExpr1, iExpr2) ;
  }

} 