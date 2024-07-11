package regexpr ;
import regexpr.*;
import syntax.* ;

class SEOptimizer3 extends ASEOptimizer{
	
	IEquiv eq  ;
	
	public SEOptimizer3(IExpressions exprs)
	{
		super(exprs) ;	
		eq = new Equiv(exprs) ;
	}

	
 	public int[][] tItA(int iF, int[] tI, int[] tE, int[][] tTabDE, int n) throws GCException
 	{
 		
 		super.tItA(iF, tI, tE, tTabDE, n) ;
 		
 		int[][] tItA = selectCovered(iF, tE, n) ;
 		int[] tIS = tItA[0] ;
 		int[] tA = tItA[1] ;
 		
 		if (tIS.length == 2)
 		{
 			tabDF = new int[tabDF.length] ;
 			return tItA ;
 		}
 		
 		if (tabDF[0] == exprs.one()) 		
	    return new int[][]{{0}, {exprs.one()}};
	  else 
	  	return new int[][]{{}, {}};
 	}
 	
	
	int diff(int iF, int iE)  throws GCException 
	// if iF >= iE and by chance we find iS such that iF = union(iE, iS)
	// we return iS ; otherwise - 1 is returned.
	{
		exprs.collectPairs() ;
		
		int iDiff = diffAux(iF, iE) ;
		
		//if (iDiff != - 1)
		//System.out.println("iDiff = " + exprs.toString(iDiff)) ;
		
		return iDiff ;
	}
	
 	int diffAux(int iF, int iE)  throws GCException 
	// if iF >= iE and by chance we find iS such that iF = union(iE, iS)
	// we return iS ; otherwise - 1 is returned.
	{
		
		if (exprs.bestExpr(iF) == exprs.bestExpr(iE))
			return 0 ;		
		
		if (iF == exprs.zero() && exprs.notZero(iE))
			return - 1 ;
		
		if (exprs.existsPair(iF, iE))
			return - 1 ;
		
		int iPair = exprs.pair(iF, iE) ;
		
		int[] tabDF = exprs.tabD(iF) ;
		int[] tabDE = exprs.tabD(iE) ;
		
		if (tabDF[0] == exprs.zero() && tabDE[0] == exprs.one())
			return - 1 ;
		
		int[] tabDiff = new int[tabDF.length] ;
		
		if (tabDF[0] == exprs.one() && tabDE[0] == exprs.zero())
			tabDiff[0] = exprs.one() ;
		else
			tabDiff[0] = exprs.zero() ;
		
		boolean weAreLucky = true ;
		int x = 1 ;
		while (x != tabDF.length && weAreLucky)
		{
			int iDiff = diffAux(tabDF[x], tabDE[x]) ;
			weAreLucky = iDiff != - 1 ;
			
			if (weAreLucky)
			tabDiff[x] = exprs.concat(exprs.iLetter((char)('a' + x - 1)), iDiff) ;
		  x ++ ;
		}
		
		exprs.removePair(iPair) ;
		
		if (weAreLucky)
			return exprs.fold(tabDiff) ;
		else return - 1 ;
		
	}

 	
 	int[][] selectCovered(int iF, int[] tE, int n) throws GCException 
 	{
 		int i = 1 ;
 		boolean weAreLucky = false ;
 		int iDiff = - 1 ;
 		while (i != n + 1 && ! weAreLucky)
 		{
 			iDiff = diff(iF, tE[i]) ;
 			
 			if (iDiff != - 1)
 			{
 				weAreLucky = true ;
 			}
 			else
 			 i ++ ;
 		}
 		
 		if (weAreLucky)
 			return new int[][]{{0, i}, {iDiff, exprs.one()}}  ;
 		else
 			return new int[][]{{}, {}}  ;
 	}
 	
 	int[][] selectCoveredNew(int iF, int[] tE, int n) throws GCException 
 	// bad idea
 	{
 		boolean[] tI = new boolean[n + 1] ;
 		int i = 1 ;
 		
 		int iDiff = iF ;
 		int count = 1 ;
 		
 		while (i != n + 1 && iDiff != exprs.zero())
 		{
 			int iDiffNew = diff(iDiff, tE[i]) ;
 			
 			if (iDiffNew != - 1)
 			{
 				tI[i] = true ;
 				iDiff = iDiffNew ;
 				count ++ ;
 			} 			
 		  i ++ ;
 		}
 		
 		if (count > 2)
 			System.out.println("BINGO COUNT " + count) ;
 			
 		{
 			int[] tRI = new int[count] ;
 			int[] tRA = new int[count] ;
 			tRI[0] = 0 ;
 			tRA[0] = iDiff ;
 			
 			int j = 0 ;
 			int k = 1 ;
 			while (k != tRI.length)
 			{
 				if (tI[j])
 				{ 
 					tRI[k] = j ;
 					tRA[k] = exprs.one() ;
 					k ++ ;
 				}
 				j ++ ;
 			}
 			return new int[][]{tRI, tRA} ;
 		}
 		//return new int[][]{{}, {}}  ;
 	}
 	
 	
 	
 	
}

