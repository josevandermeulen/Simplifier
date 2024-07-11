package regexpr ;
import regexpr.*;
import syntax.* ;

public class SolveEquations{
	
	IExpressions exprs ;

	Equiv eq = null ;
	InfEq infEq = null ;
	ASEOptimizer opt ;
	
	int[] tI ;
	boolean[] isSolved ;
	int MAXDEPTH = 7 ;
	int maxdepth ;
	int[][] tTabDE ; 
  int[] tE ;
  
  
  
  public void setOptimizer(ASEOptimizer opt)
  {
  	this.opt = opt ;
  }
	
  
  public void setInfEq(InfEq infEq)
  {
  	this.infEq = infEq ;
  }
	
  
  public void setEquiv(Equiv eq)
  {
  	this.eq = eq ;
  }
	
	public SolveEquations(IExpressions exprs, ASEOptimizer opt)
	{
		this.exprs = exprs ;
		setOptimizer(opt) ;
		reinit() ;
	}
	
	public SolveEquations(IExpressions exprs)
	{
		this(exprs, new SEOptimizer(exprs)) ;		
	}
	
	public void reinit()
	{
	  tI = new int[exprs.memorySize()] ;
	  isSolved = new boolean[exprs.memorySize()] ;
	  eq = new EquivUF(exprs) ;
	  infEq = new InfEqA(exprs) ;
	}
	

	
	
	int solveEqu(int iExpr) throws GCException
 	{		
 		return solveEqu(iExpr, MAXDEPTH) ;
 	} 
 	
 	int solveEqu(int iExpr, int maxdepth) throws GCException
 	{

    this.maxdepth = maxdepth ;
    tTabDE = new int[maxdepth + 2][] ;
	  tE = new int[maxdepth + 2] ;
    
 		int[][] tJA ;
 		
 		try {			
 			tJA = solveEquAux(0, iExpr) ;			
 		}
 		catch(GCException gce)
 		{
 			reinit() ;
 			//tI  = new int[exprs.memorySize()] ;
 			//return exprs.bestExpr(iExpr) ;
 			throw gce ;
 		}
 	  
 		
 		int[] tJ = tJA[0] ;
 		int[] tA = tJA[1] ;
 		
 		int res ;
 			
 			
 		if (tJ.length == 0)
 			res = exprs.zero() ;
 		else
 			res = exprs.elimOne(tA[0]) ;	
 		
 		//System.out.println("tA[0] = " + exprs.toString(tA[0])) ;
 		//System.out.println("elimOne(tA[0]) = " + exprs.toString(res)) ;
	
    /*if (exprs.size(res) < exprs.size(iExpr))
    {
    	//System.out.println("iExpr = " + exprs.toString(iExpr)) ;
 		  //System.out.println("res = " + exprs.toString(res)) ;
    }*/
    
    exprs.unify(res, exprs.bestExpr(iExpr)) ;
    
 		return exprs.bestExpr(res)  ;
 	}
 	
	
 	int[][] solveEquAux(final int n, final int iF)  throws GCException
 	// We consider n expressions iE1, ..., iEn
 	// They are such that tI[iEi] = i
 	// We also consider iF, another expression.
 	// We want to return n + 1 expressions iA0, iA1, ..., iAn
 	// such that L(F) = L(A0) U ... U L(Ai . Ei) U ...
 	// However, as probably most of the Ai are empty,
 	// We return two arrays {i_0, ..., i_k}
 	// such that i_0 < ... < i_k
 	// and A_i_0 , ... , A_i_k are the non empty Ais
 	{
 		//System.out.println("n = " + n + " iF = " + exprs.toString(iF)) ;
 		
 		// For convenience, we first consider the case when iF = exprs.zero()
 		if (iF == exprs.zero())
 			return new int[][]{{},{}} ;
 		
 		// iF is equal to one of the iEi if and only tI[iF] = i =/= 0 			
 		int indice = tI[iF] ;
 		
 		if (indice != 0)
 			return new int[][]{{indice},{exprs.one()}} ;
 		
 		if (n > maxdepth)
 			return new int[][]{{0},{exprs.bestExpr(iF)}} ; 		
	  
 		// We compute solutions for the same problem with iE1, ..., iEn+1
 		// and the derivatives of iF
		int[][] mergedTab = opt.tItA(iF, tI, tE, tTabDE, n) ;
		
		// Good optimization but sometimes we loose precision.
		if (mergedTab[0].length == 0 && isSolved[iF])
			return new int[][]{{0}, {exprs.bestExpr(iF)}} ;
		
		// Otherwise, we add iF in the list
 		  tI[iF] = n + 1 ;
 	
		
		 int[] tabDF = opt.tabDF ;
     tTabDE[n + 1] = opt.originalTabDF ;
     tE[n + 1] = iF ;
        
 	
 		
 		 int[] dejaVu = new int[tabDF.length] ;
 		
 		 {    	
    	int x = 1 ;
    	while (x != tabDF.length)
    	{
    		if (dejaVu[x] == 0)
    		{ 
    			int y = x + 1 ;
    		  while (y != tabDF.length)
    		  {
    			  if (dejaVu[y] == 0 && tabDF[y] == tabDF[x])
    			    dejaVu[y] = x ;
    			  y ++ ;
    		  }
    		}
    		x ++ ;
    	}   	
     }	
    
	
    int[][][] ttSolved = new int[tabDF.length][][] ;

    {    	
    	int x = 1 ;
    	while (x != tabDF.length)
    	{
    		if (dejaVu[x] == 0)
    			ttSolved[x] = solveEquAux(n + 1, tabDF[x]) ;
    		x ++ ;
    	}   	
    }	
    		
    {    	
    	int x = 1 ;
    	while (x != tabDF.length)
    	{
    		int y = x ;
    		if (dejaVu[x] != 0)
    			y = dejaVu[x] ;
    		
    		mergedTab =  merge(mergedTab, ttSolved[y], x) ;
    		x ++ ;
    	}   	
    }	
    
    tI[iF] = 0 ;
    tTabDE[n + 1] = null ;
    
    int iC = exprs.one() ;
    int[] rI = new int[]{} ;
    int[] rA = new int[]{}  ;
    
    if (mergedTab[0].length != 0)
    {
    	int i = mergedTab[0][mergedTab[0].length - 1] ;
    	if (i == n + 1)
    	{
    		iC = exprs.star(mergedTab[1][mergedTab[1].length - 1]) ;
    		rI = new int[mergedTab[0].length - 1] ;
        rA = new int[mergedTab[0].length - 1] ;
    	}
    	else
    	{
    		rI = new int[mergedTab[0].length] ;
        rA = new int[mergedTab[0].length] ;
    	}	
    }
    

     
    int ir1 = 0 ;
    int ir = 0 ;
    
    while (ir != rI.length)
    {
    	rI[ir1] = mergedTab[0][ir] ;
    	rA[ir1] = specialConcatStar(iC, mergedTab[1][ir]) ;
    	ir1 ++ ; ir ++ ;
    }
    
    //checkSpec(iF, tE, rI, rA ) ;
    
    //if (rI.length > 1)
    //System.out.println("return n = " + n + " rI.length = " + rI.length) ;
    
    if (rI.length == 1 && rI[0] == 0)
    {
    	isSolved[iF] = true ;
    	exprs.unify(iF, rA[0]) ;
    }
    
    return new int[][]{rI, rA} ;
    
 	}
 	
 	
 	int[][] merge(int[][] tabIA1, int[][] tabIA2, int x)  throws GCException
 	// We have two arrays of arrays {i1 < ... < in} {A1, .., An}
 	// which we merge into a single one
 	{
 		
 		int[] tabI2 = tabIA2[0] ;
 		if (tabI2.length == 0)
 			return tabIA1 ;
 		
    int[] tabI1 = tabIA1[0] ;
    //if (tabI1.length == 0)
 		//	return tabIA2 ;
 		
 		// First, we compute the number of distinct i_s
    
 		int i1 = 0 ; int i2 = 0 ;
 		int n = 0 ;
 		while (i1 != tabI1.length && i2 != tabI2.length)
 		{
 			n ++ ;
 			if (tabI1[i1] < tabI2[i2])
 				i1 ++ ;
 			else if (tabI1[i1] > tabI2[i2])
 				i2 ++ ;
 			else
 				{ i1 ++ ; i2 ++ ; } 			
 		}
 		
 		n += tabI1.length - i1 + tabI2.length - i2 ;
 		
 		int[] tabIR = new int[n] ;
 		int[] tabAR = new int[n] ;
 		
 		// We merge :
 		int[] tabA1 = tabIA1[1] ;
 		int[] tabA2 = tabIA2[1] ;
 		
 		if (tabA1.length != tabI1.length || tabA2.length != tabI2.length)
 			throw new Error() ;
 		
 		i1 = 0 ; i2 = 0 ; n = 0 ;
 		while (i1 != tabI1.length && i2 != tabI2.length)
 		{
 			
 			if (tabI1[i1] < tabI2[i2])
 			{	
 				tabIR[n] = tabI1[i1] ;
 				tabAR[n] = tabA1[i1] ;
 				n ++ ; i1 ++ ;
 				
 			}
 			else if (tabI1[i1] > tabI2[i2])
 			{	
 				tabIR[n] = tabI2[i2] ;
 				tabAR[n] = 
 				  (exprs.leftDistribute(exprs.iLetter((char)('a' + x - 1)), tabA2[i2])) ;
 				n ++ ; i2 ++ ;			
 			}
 			else
 			{ 
 			  tabIR[n] = tabI2[i2] ;
 			  tabAR[n] = exprs.fold(exprs.union(tabA1[i1],
 			  	exprs.leftDistribute(exprs.iLetter((char)('a' + x - 1)), tabA2[i2]))) ; 			  
 			  n ++ ; i1 ++ ; i2 ++ ; 
 			} 			
 		}
 		
 		while (i1 != tabI1.length)
 		{	
 				tabIR[n] = tabI1[i1] ;
 				tabAR[n] = tabA1[i1] ;
 				n ++ ; i1 ++ ; 						
 		}
 		
 		while (i2 != tabI2.length)
 		{	
 				tabIR[n] = tabI2[i2] ;
 				tabAR[n] = (exprs.leftDistribute(exprs.iLetter((char)('a' + x - 1)), tabA2[i2])) ;
 				n ++ ; i2 ++ ; 						
 		}
 		
 		return new int[][]{tabIR, tabAR} ;
 	}
 	
 	void checkSpec(int iF, int[] tE, int[] tI, int[] tA)  throws GCException
 	// check that F = ... + Ai . Ei +...
 	{
 		
 		
 		int[] tabS = new int[tI.length] ;
 		{
 			int i = 0 ;
 			while (i != tI.length)
 			{
 				if (tI[i] == 0)
 					tabS[i] = tA[i] ;
 				else
 					tabS[i] = exprs.concat(tA[i], tE[tI[i]]) ;
 				i ++ ;
 			}
 		}
 		int iS = exprs.union(tabS) ;
 		if (! eq.eq(iF, iS))
 		{
 			System.out.print(exprs.toString(iF) + " =/= ") ;
 			{
 			  int i = 0 ;
 			  while (i != tI.length)
 			  {
 				  if (tI[i] == 0)
 					  System.out.print(exprs.toString(tA[i])) ;
 				  else
 					  System.out.print(exprs.toString(tA[i]) + " . " + exprs.toString(tE[tI[i]])) ;
 					
 					  System.out.print(" + ") ;
 				  i ++ ;
 			  }
 	  	}
 	  	System.out.println() ;
 			
 		}
 	}
 	
 	//-------- The code under this line is not currently used. ------------
 	// Must be reworked. (In fact, it is.)
 	
 	int isStar(int iExpr)  throws GCException
 	// if E eq E*, return A* where A == bestExpr(E)
 	{
 		if (exprs.type(iExpr) == Expressions.STAR)
 			return iExpr ;
 		
 		int iEStar = exprs.star(iExpr) ;
 		boolean equal = eq.eq(iExpr,  iEStar) ;
 		
 		if (equal)
 		{
 			exprs.unify(iExpr,  iEStar) ;
 			return exprs.bestExpr(iExpr) ;			
 		}		
 		return - 1 ;
 	} 	
 	
 	int isStar(int iAStar, int iBStar)  throws GCException
 	{
 		int iE = exprs.concat(iAStar, iBStar) ;
 		int iEStar = exprs.star(iE) ;
 		
 		boolean equal = eq.eq(iE,  iEStar) ;
 		
 		if (equal)
 		{
 			//System.out.println("BINGO isStar ") ;
 			int iA = exprs.tabS(iAStar)[0] ;
 			int iB = exprs.tabS(iBStar)[0] ;
 			
 				
 			int iRes = exprs.star(exprs.union(iA, iB)) ;			
 			return exprs.bestExpr(iRes) ;
 			
 		}
 		
 		return - 1 ;
 	}
 		
 	int decompStarRight(int iA, int iBStar)  throws GCException
 	// if  A = B* C return C 
 	// otherwise - 1
 	{
 		int iC ;
 		if (exprs.type(iA) == Expressions.CONCAT && eq.eq(exprs.tabS(iA)[0], iBStar))
 			iC = exprs.tabS(iA)[1] ;
 		else 
 			iC = - 1 ;
 		
 		return iC ; 			
 	}
 	
  int decompStarLeft(int iAStar, int iB)  throws GCException
 	// if  B = C A* return C 
 	// otherwise - 1
 	{
 		if (exprs.type(iB) != Expressions.CONCAT)
 			return - 1 ;
 		
 		int iC = exprs.tabS(iB)[0] ;
 		int iReste = exprs.tabS(iB)[1] ;
 		while (exprs.type(iReste) == Expressions.CONCAT)
 		{
 			iC = exprs.concat(iC, exprs.tabS(iReste)[0]) ;
 			iReste = exprs.tabS(iReste)[1] ;
 		}
 		
 		if (eq.eq(iAStar, iReste))
 			return exprs.bestExpr(iC) ;
 		else
 			return - 1 ;
 	}
 	
 	
 	
 	int specialConcatStar(int iExpr)  throws GCException
 	{
 		
 		int iAStar ; 
 		if (exprs.type(iExpr) == Expressions.CONCAT)
 		{
 			iAStar = exprs.tabS(iExpr)[0] ;
 			if ((iAStar = isStar(iAStar)) != - 1)
 			{
 				int iS = exprs.tabS(iExpr)[1] ;
 				return specialConcatStar(iAStar, iS) ;
 			}
 	  }
 	  
 		return iExpr ;
 	}
 	
 	
 	int specialConcatStar(int iAStar, int iExpr)  throws GCException
 	// Pré : AStar = A* 
 	// Let E = B . S,
 	//   if B <= A* && B >= 1  [1]
  //      if S = 1 --> A*
 	//      else concatStar(AStar, S)
 	//   else
 	//   if E = B* . S   [2]
 	//      if A = B* . C  [2.1]
 	//         if S = 1 --> (B + C)*
 	//         else concatStar((B + C)*, S)
 	//       else if A <= B * [2.2]
 	//          if S = 1 --> B*
 	//          else concatStar(B*, S)
 	//      else if B = C . A*  [2.3]
 	//          if S = 1 --> (A + C)*
 	//          else concatStar((A + C)*, S)
 	//      else if A* B* = (A* B*)*  [2.4]
 	//          if S = 1 --> (A + B)*
 	//          else concatStar((A + B)*, S)
 	// sinon --> A* E
 	{
 		
    if (exprs.type(iAStar) != Expressions.STAR)
    	return exprs.concat(iAStar, iExpr) ;
 		
 		int iP ; int iS ; 
 		if (exprs.type(iExpr) == Expressions.CONCAT)
 		{
 			iP = exprs.tabS(iExpr)[0] ;
 			iS = exprs.tabS(iExpr)[1] ;
 		}
 		else 
 		{
 			iP = iExpr ;
 			iS = exprs.one() ;
 		}
 		
 		
 		if (exprs.hasOne(iP) && infEq.infEq(iP, iAStar))
 		{ // [1]
 			
 			
 			if (iS == exprs.one())
 			  return iAStar ;
 			else
 			{
 				//System.out.println("// [1]") ;
 				return specialConcatStar(iAStar, iS) ;
 			}
 		}
 		
    
    if (exprs.type(iP) == Expressions.STAR)
    {
    	// [2]
 			//System.out.println("// [2]") ;

    	int iBStar = iP ;
    	int iA = exprs.tabS(iAStar)[0] ;
    	int iB = exprs.tabS(iBStar)[0] ;

    	
    	int iC = decompStarRight(iA, iBStar) ;
    	if (iC != - 1)
    	{	
    		// [2.1]
 			//System.out.println("// [2.1]") ;

    		int iSStar = exprs.star(exprs.union(iB, iC)) ;
    		if (iS == exprs.one())
 			    return iSStar ;
 			  else
 				  return specialConcatStar(iSStar, iS) ;		
    	}
    	
    	if (infEq.infEq(iA, iBStar))
    	{
    		// [2.2]
 			  //System.out.println("// [2.2]") ;

    		if (iS == exprs.one())
 			     return iBStar ;
 			  else
 				   return specialConcatStar(iBStar, iS) ;
 			}
 			
 			iC = decompStarLeft(iAStar, iB) ;
 			if (iC != - 1)
    	{	
    		// [2.3]
        //System.out.println("// [2.3]") ;
        
    		int iSStar = exprs.star(exprs.union(iA, iC)) ;
    		if (iS == exprs.one())
 			    return iSStar ;
 			  else
 				  return specialConcatStar(iSStar, iS) ;		
    	}
 			
 			

 			int iABStar = isStar(iAStar, iBStar) ;
 			if (iABStar != - 1)
 			{
 				// [2.4]
 			  
 				if (iS == exprs.one())
 			    return iABStar ;
 			  else
 			  {
 			  	//System.out.println("// [2.4]") ;
 			  	return specialConcatStar(iABStar, iS) ;
 			  }
 			}
 			
    } 		
    
 		return exprs.concat(iAStar, iExpr) ;
 		
 	}
 
 	
 	
 	public static void main(String[] args) throws Exception
 	{
 		/*Background exprs = new Background(1000000, 2) ;
 		SolveEquations solve = new SolveEquations(exprs) ;
 		MakeDFA mDFA = new MakeDFA(exprs) ;
 		Minimize minm = new Minimize(exprs) ;
 		
 		Term term = RegExprReader.toTerm(args[0]) ;
 		int iExpr = term.toExpression(exprs) ;
 		System.out.println(exprs.toString(iExpr)) ;
 		
 		mDFA.computeAllDeriv(iExpr) ;
	  mDFA.computeEquations(iExpr) ;
 		minm.minimize(mDFA.leftParts(), mDFA.rightParts()) ;
 		
 		System.out.println(exprs.toString(exprs.bestExpr(
 			mDFA.leftParts()[0]))) ;
 		System.out.println(exprs.toString(exprs.bestExpr(iExpr))) ;

 		int iExprS = solve.solveEqu(exprs.bestExpr(iExpr)) ;
 		System.out.println(exprs.toString(iExprS)) ;*/

 	}
 	
 	
}

