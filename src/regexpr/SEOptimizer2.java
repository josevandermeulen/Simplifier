package regexpr ;
import regexpr.*;
import syntax.* ;

class SEOptimizer2 extends SEOptimizer{
	

	public SEOptimizer2(IExpressions exprs)
	{
		super(exprs) ;		
	}

 		
 	boolean cover(int[] tabDF, int[] tabDE)
 	// Says if tabDF covers tabDE
 	// in the sense that tabDE[x] subseteq tabDF
 	{
 		boolean covers = true ;
 		int x = 0 ;
 		while (x != tabDF.length && covers)
 		{
 			covers = exprs.isSubsumed(tabDE[x], tabDF[x]) ;
 			x ++ ;
 		}
 		return covers ;
 	}
 	
 	
 	
 	int[][] selectCovered(int[] tabDF, int[][] tTabDE, int n) throws GCException 
 	// We select all the tTabDE[i] which are covered by tabDF
 	// and we choose, among them, some which cover tabDF
 	// as much as possible. We avoid to choose ones that are
 	// "subsumed" (i.e. covered) by already chosen ones.
 	// We preferably select those with the smallest index i.
 	// We return an array with the chosen indices.
 	// as well as tabDCover 
 	{
 		// First, we select the indices within a buffer
 		int[] tabDCover = new int[tabDF.length] ;
 		int iBuf = exprs.newBuffer() ;
 		int i = 1 ;
 		while (i != n + 1 && ! cover(tabDCover, tabDF))
 		{
 			int[] tabDE = tTabDE[i] ;
 			if (cover(tabDF, tabDE) && ! cover(tabDCover, tabDE))
 			{
 				int x = 0 ;
 				while (x != tabDE.length)
 				{
 					tabDCover[x] = exprs.union(tabDE[x], tabDCover[x]) ;
 					x ++ ;
 				}				
 				exprs.addToBuffer(iBuf, i) ;
 			}
 			i ++ ;
 		}
 		
 		return new int[][]{exprs.bufferToTabE(iBuf), tabDCover}  ;
 	}
 	
 	
 	
 	
}

