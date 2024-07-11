package regexpr;
import util.*;

public class Rules {
	
	static final int MAXSIZEUNION = 10 ;
	
	Minimize minm ;

	int[] tI  ;
  final IEquiv eq ;
  final IInfEq infEq ;
  final IExpressions exprs ;

	
	public Rules(IExpressions exprs, 
		IEquiv eq, IInfEq infEq)
	{
		this.exprs = exprs ;
		this.eq = eq ;
		this.infEq = infEq ;
 	}
 	
 	
 	
 
  	
	public boolean infEq(int iExpr1 , int iExpr2) throws GCException
 	// true if and only if iExpr1 <= iExpr2
 	{		
 		iExpr1 = exprs.bestExpr(iExpr1) ;
 		iExpr2 = exprs.bestExpr(iExpr2) ;
 		
 		return infEq.infEq(iExpr1 , iExpr2) ; 		
 	}		
 	
 
 	

 	
 	public boolean eq(int iExpr1 , int iExpr2) throws GCException
 	// true if only if iExpr1 =equiv iExpr2
 	{  	
 		iExpr1 = exprs.bestExpr(iExpr1) ;
 		iExpr2 = exprs.bestExpr(iExpr2) ;

 		if (iExpr1 == iExpr2)
 			return true ;
 		
 		return eq.eq(iExpr1 , iExpr2) ;
 	}

 	
 	public int simplify(int iExpr) throws GCException
 	{ 		
 		int iExprs = simplifyPlain(iExpr) ;
 		//System.out.println("size(iExpr) = " + exprs.size(iExpr)) ;
 		//System.out.println("size(iExprs) = " + exprs.size(iExprs)) ;
		
 		if (exprs.hasOne(iExprs) 
 			&& exprs.type(iExpr) != Expressions.STAR
 			&& exprs.type(iExpr) != Expressions.DIFF
 			&& eq(iExprs, exprs.star(iExprs)))		
 			//&& infEq(exprs.star(iExprs), iExprs))		less efficient ?
 		{
 			int iExprsStar = exprs.star(iExprs) ;
 			
 			if (exprs.type(iExprsStar) == Expressions.STAR)
 			{
 				iExprs = simplifyStar(iExprsStar) ; 		  
 			  exprs.unify(iExpr, iExprs) ;
 			}
 		}
		
 		return exprs.bestExpr(iExpr) ;
 	}
 	
 	
 	public int simplifyPlain(int iExpr) throws GCException
 	{ 		 

 			switch (exprs.type(iExpr))
 			{
 				case Expressions.ZERO :
 				case Expressions.ONE :
 				case Expressions.LETTER : 
 					return  iExpr ;
				   					
 				case Expressions.UNION :
 				{
 					iExpr = exprs.elimOne(iExpr) ;
 					simplifyUNION(iExpr) ;
 					simplifyUNIONOld(iExpr) ;
 					iExpr = exprs.bestExpr(iExpr) ;
 					return exprs.fold(iExpr) ;
 				}
 					
 				case Expressions.CONCAT :
 				{
 					return simplifyCONCAT(iExpr) ;					
 				}

 				case Expressions.STAR :
 			  {
 		      return simplifyStar(iExpr) ; 		 
 				}
 				
 			  case Expressions.DIFF :
 			  {
 			  	return simplifyDIFF(iExpr) ;
 			  }
 				
          
        default : throw new Error("Rules.simplify") ;
 			}
 	}
 	
 
 	//--------------------------------------------------------------- 
	

	
	int simplifyStar(int iExpr)   throws GCException
	{			
		int iSub = exprs.tabS(iExpr)[0] ;

		int iSimpl = simplifyUnderStar(iSub, iExpr) ;
		
		//int iSimpl = exprs.fold(iSimpl) ;

		int iSimplStar = exprs.star(iSimpl) ;
		
		//System.out.println(exprs.toString(iExpr)) ;
		//System.out.println(exprs.toString(iSimplStar)) ;

	  exprs.unify(iSimplStar, iExpr) ;
		
		return exprs.bestExpr(iSimplStar) ; 
	}
	

	int simplifyUnderStar(int iExpr, int iExprStar)   throws GCException
	{
		
		int iBuf = exprs.newBuffer() ;	
		
		computeMinimal(iBuf, iExpr, iExprStar) ;
		
		//exprs.printBuffer(iBuf) ;
		
		int[] tabE = exprs.bufferToTabE(iBuf) ;
		
		int iExprs = exprs.union(tabE) ;
		
		if (exprs.type(iExprs) == Expressions.UNION)
		{
			//System.out.println("iExprs1 " + exprs.toString(iExprs)) ;
			if (exprs.tabE(iExprs).length > MAXSIZEUNION)
			  iExprs = exprs.fold(iExprs) ;
			
			if (exprs.tabE(iExprs).length <=  MAXSIZEUNION)
			{
				iExprs = simplifyUnionUnderStar(iExprs) ;
				iExprs = exprs.fold(iExprs) ;
			}
			//System.out.println("iExprs2 " + exprs.toString(iExprs)) ;
			
		}
		//System.out.println("iExprs " + exprs.toString(iExprs)) ;
		
		return iExprs ;
	}
	
  void computeMinimal(int iBuf, int iExpr, int iExprStar) throws GCException
	// Enfin, une spécification ..	
	// We add to iBuf a number  F1, .. , Fm of sub -expressions
	// "minimales" de E (iExpr) such that (F1 + .. + Fm)* = E*
	// Faudra trier ça pour en extraire une sous-union.. ???
	{
		//System.out.println("computeMinimal : " + exprs.toString(iExpr)) ;
		switch (exprs.type(iExpr))
		{
			case Expressions.ONE :
				return  ;
				
			case Expressions.LETTER :
				exprs.addToBuffer(iBuf, iExpr) ;
				return ;
				
			case Expressions.STAR :
		  {
				int iSub = exprs.tabS(iExpr)[0] ;
				computeMinimal(iBuf, iSub, iExprStar) ;	
				return ;
			}
			
			case Expressions.UNION :
			{
				int[] tabE = exprs.tabE(iExpr) ;
				int i = 0 ;
				while (i != tabE.length)
				{
					computeMinimal(iBuf, tabE[i], iExprStar) ;
					i ++ ;
				}
				return ;
			}
			
			case Expressions.CONCAT :
			{	
				int iExpr1 = exprs.tabS(iExpr)[0] ;
				int iExpr2 = exprs.tabS(iExpr)[1] ;
				
				if (checkCut(iExpr1, iExpr2, iExprStar))
				{ 
						computeMinimal(iBuf, iExpr1, iExprStar) ;
						computeMinimal(iBuf, iExpr2, iExprStar) ;
						return ;
				}
									
				while (exprs.type(iExpr2) == Expressions.CONCAT)
				{
					iExpr1 = exprs.concat(iExpr1, exprs.tabS(iExpr2)[0]) ;
				  iExpr2 = exprs.tabS(iExpr2)[1] ;
				  
				  if (checkCut(iExpr1, iExpr2, iExprStar))
					{ 
						computeMinimal(iBuf, iExpr1, iExprStar) ;
						computeMinimal(iBuf, iExpr2, iExprStar) ;
						return ;
				  }
				}
				
			  exprs.addToBuffer(iBuf, iExpr) ;			  
			  return ;
			}
			
			default : throw new Error("computeMinimal " + iExpr) ;
		}			
	}

	
	
	
	boolean checkCut(int iExpr1, int iExpr2, int iExpr) throws GCException
	{
		boolean bingo = checkCutPlain(iExpr1, iExpr2, iExpr) ;
		if (bingo)
		{	
			//System.out.println("iExpr1 : " + exprs.toString(iExpr1)) ;
			//System.out.println("iExpr2 : " + exprs.toString(iExpr2)) ;
		}
		return bingo ;
	}
	
	boolean checkCutPlain(int iExpr1, int iExpr2, int iExpr) throws GCException
	// Sachant que (.. + concat(iExpr1, iExpr2) + ..)* = iExpr
	// déterminer si iExpr1 <= iExpr et iExpr2 <= iExpr
	// Pre : iExpr1, iExpr2 != 1
	{
		if (exprs.hasOne(iExpr1))
			if (exprs.hasOne(iExpr2))
				 return true ;
			 else
			 	 return infEq(iExpr1, iExpr) ;
		else
			if (exprs.hasOne(iExpr2))
				 return infEq(iExpr2, iExpr) ;
			else
				return infEq(iExpr1, iExpr) 
			       && infEq(iExpr2, iExpr); 
	}
	
	boolean checkCutWeak(int iExpr1, int iExpr2, int iExpr) throws GCException
	// Sachant que (.. + concat(iExpr1, iExpr2) + ..)* = iExpr
	// déterminer si iExpr1 <= iExpr et iExpr2 <= iExpr
	// Pre : iExpr1, iExpr2 != 1
	{
		if (exprs.hasOne(iExpr1)
			  && exprs.hasOne(iExpr2))
				 return true ;
		else
			 	 return false ;
	}
	

	
 	//---------------------------------------------------------------
 	
 	int simplifyCONCAT(int iExpr) throws GCException
 	{
 		int iSimpl = simplifyCONCATAux(iExpr) ;	
 	  exprs.unify(iSimpl, iExpr) ;				
 		return exprs.bestExpr(iSimpl) ; 		
 	}
 	
 	
 	
 	int simplifyCONCATAux(int iExpr) throws GCException
 	// Pré : E = concat(A, concat(B, C)) (si C = 1, E = concat(A, B))
 	//   on suppose concat(B, C) "simplifié".
 	// Si pas CONCAT, on renvoie iExpr
 	{
 		
 		if (exprs.type(iExpr) != Expressions.CONCAT)
 			return iExpr ;
 			
 		int iA = exprs.tabS(iExpr)[0] ;
 		if (! exprs.hasOne(iA))
 			return iExpr ;
 			
 		int iS = exprs.tabS(iExpr)[1] ;
 		int iB ; int iC ;
 		if (exprs.type(iS) == Expressions.CONCAT)
 		{
 			iB = exprs.tabS(iS)[0] ;
 			iC = exprs.tabS(iS)[1] ;
 		}
 		else
 		{
 			iB = iS ;
 			iC = exprs.one() ;
 		}
 		
 		
 		if (exprs.type(iB) == Expressions.STAR && infEq(iA, iB))
 		{		
 			return exprs.concat(iB, iC) ;
 		}
 		
 		
 		if (exprs.type(iA) == Expressions.STAR)
 		{			
 			if (exprs.hasOne(iB) && infEq(iB, iA))
 				return simplifyCONCATAux(exprs.concat(iA, iC)) ;
 		}
 		
 		return iExpr ;		
 	}
 	
 	
  int simplifyUnionUnderStarOld(int iExpr)  throws GCException
  // Pre : type(iExpr) = UNION
  {
  	int[] tabE = exprs.tabE(iExpr) ;
  	
  	int i = 0 ;
  	int iPStar = exprs.star(tabE[0]) ;
  	int iBufP = exprs.newBuffer() ;
  	int[] tabEP = new int[]{tabE[0]} ;
  	// 1 <= i <= tabE.length
  	// iPStar equiv star(union(tabE[0..i[))
  	while (i != tabE.length)
  	{
  		if (infEq(tabE[i], iPStar))
  		{
  			i ++ ;
  			continue ; 			
  		}
  		
  		int iTabiStar = exprs.star(tabE[i]) ;
  		int j = 0 ;
  		while (j != tabEP.length)
  		{
  			if (! infEq(tabEP[j], iTabiStar))
  				exprs.addToBuffer(iBufP, tabEP[j]) ;
  			j ++ ;
  		}
  		exprs.addToBuffer(iBufP, tabE[i]) ;
  		
  		tabEP = exprs.bufferToTabEAndReinit(iBufP) ;
  		iPStar = exprs.star(exprs.union(tabEP)) ; 		
  		i ++ ;
  	}
  	
  	exprs.free(iBufP) ;
  	
  	return exprs.union(tabEP) ;
  }
  
  int[] removeZeroes(int[] tabE, int j)
  // Pré :
  // tabE contains j elements =/= 0;
  // tabE is strictly sorted, if zeroes are ignored
  {
  	int[] tabR = new int[j] ;
  	int i = 0 ;
  	int k = 0 ;
  	while (k != tabE.length)
  	{
  		if (tabE[k] != 0)
  			tabR[i ++] = tabE[k] ;
  		k ++ ;
  	}
  	return tabR ;
  }
  
  int union(int tabE[], int j) throws GCException 
  {
  	int[] tabR = removeZeroes(tabE, j) ;
  	
  	if (tabR.length > 1)
			return exprs.makeExpr(Expressions.UNION, tabR) ;
		else if (tabR.length == 1)
			return tabR[0] ;
		else 
			return exprs.zero() ;
  }
  
 	int simplifyUnionUnderStar(int iExpr)  throws GCException
  // Pre : type(iExpr) = UNION
  {
  	int[] tabE = exprs.tabE(iExpr) ;
  	int[] tabEP = new int[tabE.length] ;

  	int i = 1 ;
  	int j = 1 ;
  	int iP = tabE[0] ;
  	int iPStar = exprs.star(iP) ;
  	tabEP[0] = iP ;
  	// 1 <= j <= i <= tabE.length
  	// iPStar equiv star(union(tabE[0..i[))
  	// j is the number of non zeroes in tabEP
  	while (i != tabE.length)
  	{
  		if (infEq(tabE[i], iPStar))
  		{
  			i ++ ;
  			continue ; 			
  		}
  		
  		tabEP[i] = tabE[i] ;
  		j ++ ;
  		int k = 0 ;
  		while (k != i)
  		{
  			int iPk = tabEP[k] ;
  			if (iPk == 0)
  			{
  				k ++ ;
  				continue ;
  			}
  			
  			tabEP[k] = 0 ;
  			iP = union(tabEP, j - 1) ;
  			iPStar = exprs.star(iP) ;
  			
  			if (infEq(iPk, iPStar))
  			{
  				j -- ;
  			}
  			else
  				tabEP[k] = iPk ;
  			
  			k ++ ;
  		}
  		
  		iP = union(tabEP, j) ;
  	  iPStar = exprs.star(iP) ;
  	  
  		i ++ ;
  	}
  	
  	return union(tabEP, j) ;
  }
 	
  void sort(int[] a, long[] size)
  // We sort a, according to size
  {
  	int i = 0 ;
  	// sorted from 0 to i
  	while (i != a.length - 1)
  	{
  		i ++ ;
  		int ai = a[i] ;
  		long si = size[i] ;
  		int j = i ;
  		// le trou est en j 
  		while (j != 0 && size[j - 1] > si)
  		{
  			a[j] = a[j - 1] ;
  			size[j] = size[j - 1] ;
  			j -- ;
  		}
  		a[j] = ai ;
  		size[j] = si ;
  	}
  }
  
  int simplifyUNION(int iExpr) throws GCException
 	// Pré : E est une union
 	{

 		int[] tabE = exprs.tabE(iExpr) ;

 		
 		int iExprF = exprs.fold(iExpr) ;
 		
 		
 		if (tabE.length > MAXSIZEUNION)
 		{
 			iExpr = iExprF ;
 			tabE = exprs.tabE(iExpr) ;
 			if (tabE.length == 1)
 				return iExpr ;
 		}
 		
 		int[] a  = new int[tabE.length] ;
 		long[] s = new long[tabE.length] ;
 		{
 			int i = 0 ;
 			while (i != tabE.length)
 			{
 				a[i] = tabE[i] ;
 				s[i] = exprs.size(a[i]) ; 			
 			  i ++ ;
 			}
 		}
 		

 		sort(a, s) ;

 		tabE = a ; 		 		
 		{
 		  int i = tabE.length ;
 			while (i != 0)
 			{
 				i -- ;
 				int tabEi = tabE[i] ;
 				tabE[i] = exprs.zero() ;
 				int iExpri = exprs.union(tabE) ;
 				boolean bingo = infEq(iExpr, iExpri) ;
 				if (! bingo)
 				{
 					tabE[i] = tabEi ;
 				} 
 				else
 				{
 					exprs.unify(iExpr, iExpri) ;

 					iExpr = iExpri ;
 				}
 			}
 		}

 		//int iExprS = exprs.fold(iExpr) ;
 		//exprs.unify(iExpr, iExprS) ;
 		
    return exprs.bestExpr(iExpr) ;
 	}
 	
  
 	int simplifyUNIONOld(int iExpr) throws GCException
 	// Pré : E est une union
 	{
 		int[] tabE = exprs.tabE(iExpr) ;
 		
 		if (tabE.length > MAXSIZEUNION)
 			iExpr = exprs.fold(iExpr) ;
 		
 		tabE = exprs.tabE(iExpr) ; 		
 		if (tabE.length <=  MAXSIZEUNION)
 		{
 			int i = 1 ;
 		  int iF = tabE[0] ;
 		  while (i != tabE.length)
 		  {
 		  	iF = simpleUnionList(iF, tabE[i]) ;
 			  i ++ ;
 		  }
 		  
 		  int iFF = exprs.fold(iF) ;
 		  exprs.unify(iFF, iExpr) ;
 		}
		 		 

    return exprs.bestExpr(iExpr) ;
 	}
 	
 	
 	int simpleUnionList(int iF, int iG) throws GCException
 	// iF est pê une union mais iG pas.
 	{
 		int[] tabF = exprs.tabE(iF) ;
 	  
 			
 		int j = 0 ;
 		while (j != tabF.length)
 		{
 			iG = simpleUnion(tabF[j], iG) ;
 			j ++ ;
 		}
 	  
 		return iG ;
 	}
 	
 	 	 	
 	public int simpleUnion(int iExpr1, int iExpr2) throws GCException
 	{
 		
 		
 		int iExpr1S = exprs.bestExpr(iExpr1) ;
 		int iExpr2S = exprs.bestExpr(iExpr2) ;
 		
 		//System.out.println("iExpr1S : " + exprs.toString(iExpr1S) + " " + iExpr1S) ;
 		//System.out.println("iExpr2S : " + exprs.toString(iExpr2S) + " " + iExpr2S) ;
 		
 		
 		if (infEq(iExpr1S, iExpr2S))
 			return iExpr2S ;
 		
  	if (infEq(iExpr2S, iExpr1S))
 			return iExpr1S ;
 		
 		int iUnion = exprs.bestExpr(exprs.union(iExpr1S, iExpr2S)) ;
 		
 		//System.out.println("union : " + exprs.toString(iUnion) + " " + iUnion) ;

 		
 		return exprs.bestExpr(exprs.union(iExpr1S, iExpr2S)) ;
 		
 	}
 	
 	
 	
 	int simplifyDIFF(int iExpr) throws GCException
 	{
 		int iA = exprs.tabS(iExpr)[0] ;
 		int iB = exprs.tabS(iExpr)[1] ;
 		
 		int iDiff = iExpr ;
 			  	
 		if (infEq(iA, iB))
 			 iDiff = exprs.zero() ;
 			  	
 		if (eq(iB, exprs.zero()))
 			 iDiff = exprs.bestExpr(iA) ;
 		 
 		if (iDiff != iExpr)
 		{ 
 			exprs.unify(iDiff, iExpr) ;
 		}
 	
    return exprs.bestExpr(iExpr) ;
 	}
 	
	
 	
 	
 
 	
 	
 	
   
}




























