package regexpr ;
import regexpr.*;
import syntax.* ;

abstract class ASEOptimizer{
	
	IExpressions exprs ;	
	int[] tabDF ;
	int[] originalTabDF ;
 
	
	public ASEOptimizer(IExpressions exprs)
	{
		this.exprs = exprs ;		
	}

 	public int[][] tItA(int iF, int[] tI, int[] tE, int[][] tTabDE, int n) throws GCException
 	{
 		
 		tabDF = exprs.exprToTabD(iF) ;		
		originalTabDF = tabDF ;
			
		return null ;
 	}
 	
 	public int[] tabDF()
 	{
 		return tabDF ;
 	} 
 	 	
 	public int[] originalTabDF()
 	{
 		return originalTabDF ;
 	}
 		
}

