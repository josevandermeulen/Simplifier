package regexpr ;
import util.* ;
import syntax.* ;


public class Simplify{
	
	IExpressions exprs ;	
	Rules rules ;	
	MakeDFA mDFA ;
	MinimizeNew minm ; 
	SolveEquations solve ;
	SolveEquations solveSpecial ;
	
	static final int MAXDEPTHSOLVE = 10 ;
	static final int MAXDEPTHSOLVESPECIAL = 15 ;
	
  int[] allSubExprs ;
	boolean[] dejaVu ;
	boolean[] simplified ;
	boolean[] minimal ;
	
	public boolean isMinimal(int iExpr)
	{
		return minimal[iExpr] ;
	}
	
	GenMinExprs gen ;
	public void setGenMinExprs(GenMinExprs gen)
	{
		this.gen = gen ;
	}
	
	public Simplify(IExpressions exprs,
		IEquiv eq, IInfEq infEq, MakeDFA mDFA, MinimizeNew minm)
	{
		this.exprs = exprs ;
		this.mDFA = mDFA ;
		this.rules = new Rules(exprs, eq, infEq) ;
		this.minm = minm ;
		solve = new SolveEquations(exprs) ;
		solveSpecial  = new SolveEquations(exprs, new SEOptimizer3(exprs)) ;
		reinit() ;
	}
	
  public void reinit()
  {
		dejaVu = new boolean[exprs.memorySize()] ;  	
		simplified = new boolean[exprs.memorySize()] ; 
		minimal = new boolean[exprs.memorySize()] ; 
		{
			int iExpr = 0 ;
			while (iExpr != exprs.FIRST_POS_IN_HASH_TABLE)
			{
				simplified[iExpr] = true ;
				minimal[iExpr] = true ;
        iExpr ++ ;
			}
		}
		//System.out.println("Simplified : " + simplified) ;
	  allSubExprs = new int[exprs.memorySize()] ;
		solve.reinit() ;
		solveSpecial.reinit() ;
  }
   
	
	int tabSubExpr(int i, int iExpr, int[] a)
	// We add to a[], from index i, all sub-expressions
	// of iExpr not already added to a[], nor already simplified.
	// The first free index is returned.
	{
		if (dejaVu[iExpr] 
			  || simplified[(iExpr)])
			return i ;
		
		dejaVu[iExpr] = true ;
		
  	a[i ++] = iExpr ;
  	
  	int[] tabE = null ;
		switch(exprs.type(iExpr))
		{
		  case Expressions.STAR :
		  // We make an exception for unions "understar"
		  // we do not add the union but only its subexpressions
		  // because simplifying the union without noticing
		  // it is "under star" is costly but not useful.
		  	tabE = exprs.tabE(exprs.tabS(iExpr)[0]) ;
		  	break ;
		  	
		  case Expressions.UNION :
		  	tabE = exprs.tabE(iExpr) ;
		  	break ;		
		  	
		  default :
		    tabE = exprs.tabS(iExpr) ;
		}
		
		
		try{
			if (tabE == null)
				throw new Error("tabE == null " + iExpr) ;
			
		  int j = 0 ;
		  while (j != tabE.length)
		  {
			  i = tabSubExpr(i, tabE[j], a) ;
			  j ++ ;
		  }
		}catch (Exception e){System.out.println(exprs.toString(iExpr)) ;}
		
		return i ;
	}
	
	int preSimplify(int iExpr) throws GCException
	{
		int[] tabE = null ;
		switch(exprs.type(iExpr))
		{
		  case Expressions.STAR :
		  case Expressions.NOT :
		  	tabE = exprs.tabE(exprs.tabS(iExpr)[0]) ;
		  	break ;
		  	
		  case Expressions.UNION :
		  	tabE = exprs.tabE(iExpr) ;
		  	break ;		
		  	
			  	
		  case Expressions.CONCAT :
 		  case Expressions.DELTA :
 		  case Expressions.DIFF :
 		  case Expressions.INTER :
		  	tabE = exprs.tabS(iExpr) ;
		  	break ;		
		  	
		  default :
		    return iExpr ;
		}
		
		int[] tabES = new int[tabE.length] ;
		{
			int i = 0 ;
			while (i != tabE.length)
			{
				tabES[i] = exprs.bestExpr(tabE[i]) ;
				i ++ ;
			}
		}
		
		int iExprL = - 1 ;
		switch(exprs.type(iExpr))
		{
		  case Expressions.STAR :
		  	iExprL = exprs.star(exprs.union(tabES)) ;
		  	break ;
		  	
			case Expressions.NOT :
		  	iExprL = exprs.not(exprs.union(tabES)) ;
		  	break ;
		  	
		  case Expressions.UNION :
		  	iExprL = exprs.union(tabES) ;
		  	break ;	
		  	
		  case Expressions.CONCAT :
		  	iExprL = exprs.concat(tabES[0], tabES[1]) ;
		  	if (minimal[tabES[0]] && exprs.type(tabES[1]) == exprs.LETTER ||
		  		  minimal[tabES[1]] && exprs.type(tabES[0]) == exprs.LETTER)
		  	 {
		  	 	 //System.out.println("bingo1") ;
		  	 	 minimal[iExprL] = true ;
		  	 }
		  	break ;
		  	
 		  case Expressions.DELTA :
 		  case Expressions.DIFF :
 		  case Expressions.INTER :
		  	iExprL = exprs.op(exprs.type(iExpr), tabES[0], tabES[1]) ;
		  	break ;
		  	
		  default :
		    return iExpr ;
		}
		
		exprs.unify(iExprL, iExpr) ;
		
		return iExprL ;
		
	}
	
	public int simplify(int iExpr0) throws GCException
	{
		return simplify(iExpr0, true, true, true, false) ;
	}
  
	public int simplify(int iExpr0, boolean simpl, boolean reduce,
		boolean solveEqs, boolean iter) throws GCException
	{ 		
		
	  int i = tabSubExpr(0, iExpr0, allSubExprs) ;
	  	  
	  while (i != 0)
	  {
	  	 -- i ;   
	  	 
	  	 //if (i == 0)
	  	 //System.out.println("i = " + i) ;
	    
	  	  	int iExpr = allSubExprs[i] ;
	  	  	  	  	
	  	  	/*if (simplified[(iExpr)]) // not very likely...
	  	  	{
	  	  		continue ;
	  	  	}*/
	  	  	
	        int iExprL = preSimplify(iExpr) ;
	        
	        //if (i == 0)
	  	    //System.out.println("preSimplify " + i) ;
	        
	        if (minimal[(iExprL)]) 
	  	  	{
	  	  		simplified[(iExpr)] = true ;
	  	  		continue ;
	  	  	}
	  	  	
	        boolean alreadyDone = true ;
	        
	        int iExprS ;
	        switch (exprs.type(iExprL))
	        {
	        	 case Expressions.DELTA :
 		         case Expressions.DIFF :
 		         case Expressions.INTER :
 		         	 
 		         
	           minm.minimize(iExprL) ;

	           int iS0 = exprs.bestExpr(iExprL) ;
	           if (simplified[iS0])
	           	 continue ;
	           
             iExprS = solveSpecial.solveEqu(iS0, MAXDEPTHSOLVESPECIAL) ;
             if (simplified[iExprS])             	 
	           	 continue ;
             
             break ;
             
             
             case Expressions.NOT :
             	 
             int iSub = exprs.tabS(iExprL)[0] ;	 
 		         
 		         minm.minimize(iSub) ; 		         
 		         mDFA.computeEquations(exprs.bestExpr(iSub)) ;
 		         mDFA.negateEquations() ;	
 		         
 		         int iNot = exprs.not(exprs.bestExpr(iSub));
		         
 		         iExprS = solveSpecial.solveEqu(iNot, MAXDEPTHSOLVESPECIAL) ; 		         
 	 		       exprs.unify(iExprS, iExprL) ;
 	 		       
	           //if (simplified[iExprS])             	 
	           //	 continue ;
	           
 		         break ;
 		         
 		         default : iExprS = iExprL ;
 		         alreadyDone = false ; break ;        	
	        }
	        
	        if (alreadyDone)
	        {
	        	if (iter)
	        	{
	        		int i0 = i ;
	        	  i = tabSubExpr(i0, iExprS, allSubExprs) ;
	            continue ;
	          }
	        }
	        
	        if (simpl)
	         {
	         	 iExprS = rules.simplify(iExprS) ; 
	         }
	         
	         //if (i == 0)
	  	    //System.out.println("simplify " + i) ;

	         /*if (simplified[(iExprS)]) // 
	  	  	{
	  	  		continue ;
	  	  	}*/
	        
	        if (reduce)
	        {	        	
	          //if (i == 0)
	        	//System.out.println("minimize before" + i) ;

	          minm.minimize(exprs.bestExpr(iExprS)) ;
	          //if (i == 0)
	  	      //System.out.println("minimize after" + i) ;

	          
	          if (gen != null)
	          {	
	          	int iExprT = gen.checkIsMin(exprs.bestExpr(iExprS)) ; 
	          	
	          	//if (i == 0)
	  	        //System.out.println("gen.checkIsMin " + i) ;
	          	
              if (iExprT != - 1) 
              {
              	//System.out.println("bingo2") ;
            	  minimal[iExprT] = true ;
            	  simplified[iExprT] = true ;
            	  simplified[iExpr] = true ;
            	  continue ;
            	}
            }
            
	          int iExprM = exprs.bestExpr(iExprS) ;
	          
	          //if (simplified[iExprM])
	          //	continue ;
	          	          
	          if (solveEqs)
            iExprS = solve.solveEqu(iExprM, MAXDEPTHSOLVE) ;
            //if (i == 0)
	  	      //  System.out.println("gen.checkIsMin " + i) ;
            //if (simplified[iExprS])
	          //	continue ;
	        }
	        
	        if (iter)
          {  
            int i0 = i ;
	        	i = tabSubExpr(i0, exprs.bestExpr(iExprS), allSubExprs) ;          
	          if (i != i0)
	          continue ;
	        }
	        
	        //if (i == 0)
	  	    //    System.out.println("simplified " + i) ;
	        //System.out.println(((Expressions)exprs).toStringType(1999994));
	        
	        //minm.minimizeBackground() ;
	       	simplified[iExpr] = true ;
 	  }	
 	  	 
 	  /*int iBest = exprs.bestExpr(iExpr0) ;*/
 	  //System.out.println("nextIEq.size() = " + 
 	  //	((Background)exprs).nextIEq.size()) ;
 	  
 	  //minm.minimizeBackground() ;
 	  
 	  //System.out.println("nextIEq.size() = " + 
 	  //	((Background)exprs).nextIEq.size()) ;
 	  
 	  
 	  return exprs.bestExpr(iExpr0) ;

	}
		
	


}




