package regexpr;

public abstract class AInfEq implements ICheck{
	
 public abstract boolean infEq(int iExpr1, int iExpr2) throws GCException ; 
 
 public boolean compare(int iExpr1, int iExpr2) throws GCException
 {
 	 return infEq( iExpr1, iExpr2) ;
 }

}