package regexpr;
import syntax.* ;

public class EquivA extends ChecksA implements IEquiv{
	
	public EquivA(IExpressions exprs){
		super(exprs) ;
	}

  	
	void initRTod(int iExpr1, int iExpr2)  throws GCException
  {
    	int[] tabE1 = exprs.tabE(iExpr1) ;
    	int i = 0 ;  
    	while (i != tabE1.length)
  	  {
  	  	addToRtod(tabE1[i], iExpr2) ;
  	  	i ++ ;
  	  } 
  	  
  	  int[] tabE2 = exprs.tabE(iExpr2) ;
    	int j = 0 ;  
    	while (j != tabE2.length)
  	  {
  	  	addToRtod(tabE2[j], iExpr1) ;
  	  	j ++ ;
  	  } 	  
  }
  
  
  public boolean eq(int iExpr1, int iExpr2) throws GCException
  {  	
  	return checkPD(iExpr1, iExpr2) ;
  }
  
  public boolean compare(int iExpr1, int iExpr2) throws GCException
  {  	
  	return eq(iExpr1, iExpr2) ;
  }
  


} 