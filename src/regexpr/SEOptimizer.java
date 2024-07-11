package regexpr ;
import regexpr.*;
import syntax.* ;

class SEOptimizer extends ASEOptimizer{
	

	public SEOptimizer(IExpressions exprs)
	{
		super(exprs) ;		
	}

 	public int[][] tItA(int iF, int[] tI, int[] tE, int[][] tTabDE, int n) throws GCException
 	{
 		
 		super.tItA(iF, tI, tE, tTabDE, n) ;
		
		if (tabDF == null)
			throw new Error() ;
		
		int[][] tSelected = selectCovered(tabDF, tTabDE, n) ;
		int[] tISelected = tSelected[0] ;
		int[] tabDCover = tSelected[1] ;
		
		
		if (tISelected.length != 0)
		{
			tabDF = subtract(tabDF, tabDCover) ; 
			//if (tISelected.length != 1)
			//System.out.println("bingo " + tISelected.length) ;
		}
		
		
		//System.out.println("tabDF[1] = " + exprs.toString(tabDF[1])) ;
		//System.out.println("tabDF[2] = " + exprs.toString(tabDF[2])) ;
		int j ;
		int[] tIS ; int[] tAS ;
		if (tabDF[0] == exprs.one())
		{
			tIS = new int[tISelected.length + 1] ;
			tAS = new int[tISelected.length + 1] ;
			tIS[0] = 0 ;
			tAS[0] = exprs.one() ;
			j = 1 ;
		}
 		else
 		{ 			
			tIS = new int[tISelected.length] ;
			tAS = new int[tISelected.length] ;
			j = 0 ;
 		}
 		
 		
 		int k = 0 ;
 		while (j != tIS.length)
 		{
 			tIS[j] = tISelected[k] ;
 			tAS[j] = exprs.one() ;
 			j ++ ; k ++ ;
 		}
		
 		
	  return new int[][]{tIS, tAS};

 	}
 	
 	
 	
 	boolean cover(int[] tabDF, int[] tabDE)
 	// Says if tabDF covers tabDE
 	{
 		boolean covers = true ;
 		int x = 0 ;
 		while (x != tabDF.length && covers)
 		{
 			covers = tabDE[x] == exprs.zero() || tabDE[x] == tabDF[x] ;
 			x ++ ;
 		}
 		return covers ;
 	}
 	
 	int[] subtract(int[] tabDF, int[] tabDCover)
 	// Pré : tabDF covers tabDCover
 	// We return the least tabD such that tabD + tabDCover = tabDF
 	{
 		int[] tabD = new int[tabDF.length] ;
 		int x = 0 ;
 		while (x != tabD.length)
 		{
 			if (tabDF[x] != tabDCover[x])
 				tabD[x] = tabDF[x] ;
 			else
 				tabD[x] = exprs.zero() ;
 			x ++ ;
 		}
 		return tabD ;
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
 					if (tabDCover[x] == exprs.zero())
 						tabDCover[x] = tabDE[x] ;
 					x ++ ;
 				}
 				
 				exprs.addToBuffer(iBuf, i) ;
 			}
 			i ++ ;
 		}
 		return new int[][]{exprs.bufferToTabE(iBuf), tabDCover}  ;
 	}
 	
 	
 	
 	
}

