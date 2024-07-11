package regexpr ;
import regexpr.*;
import syntax.* ;

class NonSEOptimizer extends ASEOptimizer{	
	
	public NonSEOptimizer(IExpressions exprs)
	{
		super(exprs) ;		
	}

 
 	public int[][] tItA(int iF, int[] tI, int[] tE, int[][] tTabDE, int n) throws GCException
 	{
 		
 		super.tItA(iF, tI, tE, tTabDE, n) ;
 		
		if (tabDF[0] == exprs.one()) 		
	    return new int[][]{{0}, {exprs.one()}};
	  else 
	  	return new int[][]{{}, {}};

 	}
 		
}

