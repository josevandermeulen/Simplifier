import regexpr.*;
import syntax.* ;

public class Demo {
	
  	
	static java.util.Scanner in = new java.util.Scanner(System.in) ;
	
	static int MEMORYSIZE = 1000000 ; // One million
	static int NBRLETTERS = 2 ; // guess what ?
	static int MAXNBRLETTERS = 26 ; // 
	static int LINELENGTH = 80 ;
	static 	Expressions exprs ;
	static 	IEquiv eq  ;
	static 	IEquiv eqBLC  ;
	static  IInfEq infEq  ;
	static  MakeDFA mDFA ;
	static  MinimizeNew minm ;
	static  Simplify simplifier ;
	static boolean interactive = false ;
	static Line lparser  ;
	static GenMinExprs gen ;
	

	
	static void initAllClasses(int memorySize, int nbrLetters, int tOE)
	{
		exprs = null ;

		if (tOE == 0) 
			exprs = new Expressions(memorySize, nbrLetters) ;		
		if (tOE == 1) 
			exprs = new SizedExpressions(memorySize, nbrLetters) ;
		if (tOE == 2)
			exprs = new Background(memorySize, nbrLetters) ;
		
	 	eq = new Equiv(exprs) ;
	 	eqBLC = new EquivBLC(exprs) ;
	  infEq = new InfEqA(exprs) ;
	  mDFA = new MakeDFA(exprs) ;
	  minm = new MinimizeNew(exprs, mDFA) ;
	  simplifier = new Simplify(exprs, eq, infEq, mDFA, minm) ;
	}
	
	static void reinitMemory()
	{			  
	  int memorySize = lparser.memorySize() ;
	  int nbrLetters  = lparser.nbrLetters() ;
	  if (memorySize != - 1 || nbrLetters != - 1)
	  {
	  	if (memorySize != - 1)
	  		exprs.setMemorySize(memorySize) ;
	  	
	    if (nbrLetters != - 1)
	  		exprs.setNbrLetters(nbrLetters) ;
	
      exprs.reinit() ; 
      simplifier.reinit() ;     	

	  }
	}
	
	static boolean changeMemory()
	{
		if (lparser.type() == Line.CMD &&  
			     lparser.typeOfCommand() == Line.M)
		{ 	  
			if (! interactive)
			System.out.println(lparser.getString()) ;
		
			reinitMemory() ;

			lparser.readLine(in) ;
      return true ;
		}
		
		return false ;
	}
	
	static void showDFA()
	{
		reinitMemory() ;
		
	  	  
	  boolean gCCalled = false ;
	  int i = 0 ;
	  long t0 = System.nanoTime() ;
	  Term term = null ;
	  
	  System.out.println("[" + i ++ + "]") ;
	  
	  byte ltype = lparser.readLine(in) ;
		loop : while (ltype == Line.EXPR ||
			     ltype == Line.CMD &&  
			     lparser.typeOfCommand() == Line.M)
		{		
			
		  if (changeMemory())
			   continue loop ;
			
			try{
				
			  if (! gCCalled)
			  {
				  
				  
				  if (! interactive)
			  	printLine(lparser.getString(), "Original expression") ;
	      
	        term = lparser.getTerm() ;
	      
			    if (term.nbrLetters() > exprs.nbrLetters())
	        {	        
	          exprs.setNbrLetters(term.nbrLetters()) ;
	          //simplifier.reinit() ;
	          exprs.reinit() ; 
	        }
	      }
        
	      int iExpr = exprs.toExpression(term) ;

	      printExpr(iExpr, "Normalized expression") ;
	      
	      
	      int iExprs = simplifier.simplify(iExpr) ;
	      
	      printExpr(iExprs, "Simplified expression") ;
	      System.out.println(sLine('=', LINELENGTH)) ;
          
 		    //mDFA.computeAllDeriv(iExprs) ;
 		    //mDFA.computeEquations(iExprs) ;
 		    //minm.minimize(mDFA.leftParts(), mDFA.rightParts()) ;
 		    minm.minimize(iExprs) ;
 		    
	      System.out.println(sLine('-', LINELENGTH)) ;
 		    
 		    System.out.println("Equations [MDFA] : ") ;
 		    System.out.println("----------------") ;
	      	      
 		    exprs.printEquations(mDFA.leftParts(), mDFA.rightParts()) ;
	      System.out.println(sLine('=', LINELENGTH)) ;
	      
        gCCalled = false ;
      }
      catch (GCException e)
      {             	
      	 exprs.reinit() ; 
      	 simplifier.reinit() ;  

      	 if (gCCalled)
      	 {
      		 System.out.println("Failure on input [" + (i - 1) + "]") ;
      		 gCCalled = false ;
      	 }
      	 else
         {  
        	 gCCalled = true ;  
        	 continue loop ;
         }       
       }
      System.out.println("[" + i ++ + "]") ; 
      ltype = lparser.readLine(in) ;
		}
		
		long totalTime = System.nanoTime() - t0 ;
		
		if (! interactive)
	  System.out.println("Total time = " + util.Time.toString(totalTime)
	  	+ " sec") ;     
	  
	  System.out.println("iExprList.size() = " + ((Expressions)exprs).iExprList.size()) ;
	  System.out.println(sLine('=', LINELENGTH)) ;
			 		 
		
	}
	
  static void computeDFA()
	{
		reinitMemory() ;
	  	  
	  boolean gCCalled = false ;
	  int i = 0 ;
	  long t0 = System.nanoTime() ;
	  Term term = null ;
	  
	  System.out.println("[" + i ++ + "]") ;
	  
	  byte ltype = lparser.readLine(in) ;
		loop : while (ltype == Line.EXPR ||
			     ltype == Line.CMD &&  
			     lparser.typeOfCommand() == Line.M)
		{		
			
		  if (changeMemory())
			   continue loop ;
			
			try{
				
			  if (! gCCalled)
			  {
				  
				  
				  if (! interactive)
			  	printLine(lparser.getString(), "Original expression") ;
	      
	        term = lparser.getTerm() ;
	      
			    if (Term.nbrLetters(term) > exprs.nbrLetters())
	        {	        
	          exprs.setNbrLetters(term.nbrLetters(term)) ;
	          simplifier.reinit() ;
	          exprs.reinit() ; 
	        }
	      }
        
	      int iExpr = exprs.toExpression(term) ;

	      printExpr(iExpr, "Normalized expression") ;
	      
	      
	     // int iExprs = simplifier.simplify(iExpr) ;
	      
	      //printExpr(iExprs, "Simplified expression") ;
	      //System.out.println(sLine('=', LINELENGTH)) ;
          
 		    mDFA.computeAllDeriv(iExpr) ;
 		    //mDFA.computeEquations(iExprs) ;
 		    //minm.minimize(mDFA.leftParts(), mDFA.rightParts()) ;
 		    
	      System.out.println(sLine('-', LINELENGTH)) ;
 		    
 		    //System.out.println("Equations [MDFA] : ") ;
 		    //System.out.println("----------------") ;
	      	      
 		    //((Expressions)exprs).printEquations(mDFA.leftParts(), mDFA.rightParts()) ;
	      System.out.println(sLine('=', LINELENGTH)) ;
	      
        gCCalled = false ;
      }
      catch (GCException e)
      {             	
      	 exprs.reinit() ; 
         simplifier.reinit() ;  

      	 if (gCCalled)
      	 {
      		 System.out.println("Failure on input [" + (i - 1) + "]") ;
      		 gCCalled = false ;
      	 }
      	 else
         {  
        	 gCCalled = true ;  
        	 continue loop ;
         }       
       }
      System.out.println("[" + i ++ + "]") ; 
      ltype = lparser.readLine(in) ;
		}
		
		long totalTime = System.nanoTime() - t0 ;
		
		if (! interactive)
	  System.out.println("Total time = " + util.Time.toString(totalTime)
	  	+ " sec") ;     
	  
	  System.out.println("iExprList.size() = " + ((Expressions)exprs).iExprList.size()) ;
	  System.out.println(sLine('=', LINELENGTH)) ;
			 		 
		
	}
	
	
	static GenMinExprs makeGenMinExprs(Expressions exprs)
	{
	   
	  		int nl = exprs.nbrLetters() ;
	  		int memorySize = exprs.memorySize() ;
	  		int maxL = 0 ;
	  		
	  		if (nl == 1 && memorySize >= 2000000)
	  		{
	  			maxL = 40 ;
	  			memorySize = 2000000 ;
	  		}
	  		
	  		if (nl == 2 && memorySize >= 1000000)
	  			if (memorySize >= 5000000)
	  			{
	  				memorySize = 5000000 ;
	  				maxL = 15 ;
	  			}
	  			else
	  			{
	  				memorySize = 1000000 ;
	  				maxL = 13 ;
	  			} ;
	  		
	  		if (nl == 3 && memorySize >= 1000000)
	  			if (memorySize >= 2000000)
	  			 {
	  			 	 memorySize = 2000000 ;
	  			 	 maxL = 12 ;	
	  			 }
	  			 else
	  			 {
	  			 	 memorySize = 1000000 ;
	  			 	 maxL = 11 ;	
	  			 } 
	  			 
	  	  if (nl == 4 && memorySize >= 2000000)
	  			if (memorySize >= 3000000)
	  			 {
	  			 	 memorySize = 3000000 ;
	  			 	 maxL = 11 ;	
	  			 }
	  			 else
	  			 {
	  			 	 memorySize = 2000000 ;
	  			 	 maxL = 10 ;	
	  			 }
	  			 
	  		if (maxL != 0)	 
 	      {
 	        return new GenMinExprs((Background)exprs, memorySize, nl, maxL) ;
 	      }
 	      else
 	      {
 	      	
 	      	return null ;
 	      }
 	      
 	}
 
 	static boolean checkIsMin(int iExpr, Expressions exprs, GenMinExprs gen) throws GCException
 	{
 		
 		switch (exprs.type(iExpr))
 		{
 		  case Expressions.ZERO :
 		  case Expressions.ONE :
 		  case Expressions.LETTER : return true ;
 		  
 		  case Expressions.CONCAT :
 		   // int iWSL = exprs.removeExtremeLetters(iExpr) ;
 		    return gen.checkIsMin(iExpr) != - 1 ;
 		  
 			default : return gen.checkIsMin(iExpr) != - 1 ;
 		}
 	}
 	    
	static void simplify()  //throws GCException
	{

		boolean simpl = false ;
		boolean reduce = false ;
		boolean iter = false ;
		boolean solveEqs = false ;
		boolean minimize = false ;
		boolean lifting = true ;
		boolean normalize = false ;
		boolean checkEq = false ;
	  
	  {
	  	char[] tabC = lparser.stringCommand().toCharArray() ;
	    int i = 0 ;
	    while (i != tabC.length)
	    {
	    	switch (tabC[i])
	    	{
	       	case 'S' : simpl = true ; break ;
	       	case 'R' : reduce = true ; break ;
	       	case 'I' : iter = true ; break ;
	       	case 'N' : lifting = false ; // no lifting	       		          
	        case 'B' : normalize = true ; break ; // redundant normalization
	       	case 'O' : reduce = true ;
	       		solveEqs = true ; break ;
	        case 'M' : reduce = true ;
	        	minimize = true ; break ;
	        case 'C' : checkEq = true ; 
	        	        break ;
	       	
	       default : System.err.println("Bad parameter : " + tabC[i] + " must be S, R, I, O, M, N, B, C.");
	    	}
	    	i ++ ;
	    }	
	    
	    if (!simpl && ! reduce)
	    	simpl = true ;
	  }

	  reinitMemory() ;
	  
	  if (minimize)
	  {
	  	gen = makeGenMinExprs((Background) exprs) ;
	  	simplifier.setGenMinExprs(gen) ;
	  }

	  	
	  int sumSizes = 0 ;
	  int nbrExpr = 0 ;
	  int nbrMin = 0 ;
	  long sumSizesMin = 0 ;
	  long sumSizesT = 0 ;
	  long sumSizesN = 0 ;
	  long sumSizesL = 0 ;
	  long sumSizesNL = 0 ;
	  boolean gCCalled = false ;
	  
	  int i = 0 ;
	  long t0 = System.nanoTime() ;
	  long tN = 0 ;
	  long tL = 0 ;
	  long tNL = 0 ;
	  long tS = 0 ;
	  long tC = 0 ;
		long stN = 0 ;
	  long stL = 0 ;
	  long stNL = 0 ;
	  long stS = 0 ;
	  long stC = 0 ;
	  
	  long sizeT = 0 ;
	  long sizeN = 0 ;
	  long sizeL = 0 ;
	  long sizeNL = 0 ;
	  
	  Term term = null ;
	  
	  System.out.println("[" + i ++ + "]") ;
	  byte ltype = lparser.readLine(in) ;
		loop : while (ltype == Line.EXPR ||
			     ltype == Line.CMD &&  
			     lparser.typeOfCommand() == Line.M)
		{		
			
		  if (changeMemory())
			   continue loop ;
			
			try{
				
				
			  if (! gCCalled)
			  {
			  	printLine(lparser.getString(), "Original expression") ;
	      
	        term = lparser.getTerm() ;
	        
			    if (Term.nbrLetters(term) > exprs.nbrLetters())
	        {	        
	          exprs.setNbrLetters(Term.nbrLetters(term)) ;
	          exprs.reinit() ; 
	          simplifier.reinit() ;
	          
	          if (minimize)
	          {
	          	gen = makeGenMinExprs((Background) exprs) ;
	          	simplifier.setGenMinExprs(gen) ;
	  	      }
	        }
	        
	        sizeT = term.size() ;
	        
	      }
	      	      
	      int iExprN =  - 1 ;
	      int iExpr  =  - 1 ;
	      
	      if (normalize)
	      {
	      	long t0N = System.nanoTime() ;
	      	
	      	iExpr = iExprN = exprs.toExpression(term) ;
	      	
	      	tN = System.nanoTime() - t0N ;
	      	
	      	sizeN = exprs.size(iExpr) ;
	      		      	
	        printExpr(iExpr, "Normalized expression") ;
	      }
	      
	      if (lifting)
	      {
	      	long t0L = System.nanoTime() ;
	      	
	      	Term aterm = new ATerm(term).getATerm() ;
	      	sizeL = aterm.size() ;
	      	
	      	long t0NL = System.nanoTime() ;
	      	tL = t0NL - t0L ;
	      	
	        int iExprL = exprs.toExpression(aterm) ;
	        
	        sizeNL = exprs.size(iExprL) ;
	        
	        tNL = System.nanoTime() - t0NL ;
	        printExpr(iExprL, "Normalized lifted expression") ;	
	        
           
	        //if (normalize)
	        //	exprs.unify(iExprL, iExpr) ;       
	        iExpr = exprs.bestExpr(iExprL) ;
	      }

	      long t0S = System.nanoTime() ;
	      
	      int iExprs = simplifier.simplify(iExpr, simpl, reduce, solveEqs, iter) ;
	       
	      tS = System.nanoTime() - t0S ;
	      
	      
      	      
	     // boolean isMin = (minimize ? checkIsMin(iExprs, exprs, gen) : false) ;
	      
	      if (simplifier.isMinimal(iExprs))
	      {
	      	nbrMin ++ ;
	      	sumSizesMin += exprs.size(iExprs) ;
	      	printExpr(iExprs, "Minimal expression") ;	
	      }
	      else
	        printExpr(iExprs, "Simplified expression") ;
	      
	      if (normalize && checkEq  
	      	    && exprs.bestExpr(iExprN) !=  exprs.bestExpr(iExprs))
	      {
	        	int nbrIExpr = exprs.iExprList.size() ;
	        	
	        	long t0C = System.nanoTime() ;
	        	boolean eq1 = eqBLC.eq(exprs.bestExpr(iExprN), exprs.bestExpr(iExprs)) ;
	        	
	        	tC = System.nanoTime() - t0C ;
	        	
	        	//System.out.println("-----> " + (exprs.iExprList.size() - nbrIExpr)) ;	 
	        	
	        	if (! eq1)
	        	throw new Error("Expressions not equivalent !") ;
	      } 
	    
	      System.out.println(sLine('=', LINELENGTH)) ;
	      
	      sumSizes += exprs.size(iExprs) ;
	      
	      sumSizesT += sizeT ;
	      sumSizesN += sizeN  ;
	      sumSizesL += sizeL  ;
	      sumSizesNL += sizeNL  ;
	      
	      stN += tN ;
	      stL += tL ;
	      stNL += tNL ;
	      stS += tS ;
	      stC += tC ;
	      
	      nbrExpr ++ ;	      
        gCCalled = false ;
      }
      catch (GCException e)
      {             
      	 if (minimize && gen != null)
      	   gen.gc() ; 
         else
       	   exprs.reinit() ;
       
         simplifier.reinit() ;  
         
         
 	    

      	 if (gCCalled)
      	 {
      		 System.out.println("Failure on input [" + (i - 1) + "]") ;
      		 gCCalled = false ;
      	 }
      	 else
         {  
        	 gCCalled = true ;  
        	 continue loop ;
         }       
       }
      				  
      System.out.println("[" + i ++ + "]") ; 
      ltype = lparser.readLine(in) ;
		}
		
		long totalTime = System.nanoTime() - t0 ;
		
		System.out.println(sLine('=', LINELENGTH)) ;

		
		if (! interactive)
	  {
	  	
	  	if (normalize)
	  	System.out.println("Normalisation time = " + util.Time.toString(stN)
	  	+ " sec") ;  	
	  	
		  if (lifting)
	  	{
	  		 System.out.println("Lifting time = " + util.Time.toString(stL)
	  	   + " sec") ;  	
	  	
	  		 System.out.println("Norm-lifting time = " + util.Time.toString(stNL)
	  	   + " sec") ;  
	  	}
	  	
	  	if (checkEq)
	  	System.out.println("Checking time = " + util.Time.toString(stC)
	  	+ " sec") ; 

	  	System.out.println("Simplification time = " + util.Time.toString(stS)
	  	+ " sec") ;  	 	
	  		  	
	  	System.out.println("TNLS time = " + util.Time.toString(stN + stL + stNL + stS)
	  	+ " sec") ;  
	  	
	  	System.out.println("Total time = " + util.Time.toString(totalTime)
	  	+ " sec") ; 
	  	
	  	System.out.println(sLine('=', LINELENGTH)) ;
 	  		  	
	  }
	  
	  if (nbrExpr != 0)	  
	  {
	  	
	  	System.out.println("Average size = " + (sumSizesT * 100 / nbrExpr / 100.0) + " [terms]") ; 
	  	
	  	if (normalize)
	  	System.out.println("Average size = " + (sumSizesN * 100l / nbrExpr / 100.0) + " [normalized expressions]") ; 
	  	
	    if (lifting)
	  	{
	    	System.out.println("Average size = " + (sumSizesL * 100 / nbrExpr / 100.0) + " [lifted terms]") ; 
	  		  	
	      System.out.println("Average size = " + (sumSizesNL * 100 / nbrExpr / 100.0) + " [lifted expressions]") ;
	    }
	  		  	
	  	System.out.println("Average size = " + (sumSizes * 100 / nbrExpr / 100.0) + " [simplified expressions]") ; 
	  	
	  }
	
  	if (nbrMin != 0)
	  {
	  	System.out.println("Average size = " + (sumSizesMin * 100 / nbrMin / 100.0) + " [minimal expressions]") ; 
	  	System.out.println("Number of minimal expressions = " + nbrMin) ;
	  }
  
	  System.out.println("iExprList.size() = " + ((Expressions)exprs).iExprList.size()) ;
	  System.out.println(sLine('=', LINELENGTH)) ;
			 		 
	}
	
	

	
	static String sLine(char c, int length)
	{
		if (length <= 0)
			return "" ;
		
		char[] tC = new char[length] ;
		
		int i = 0 ;
		while (i != tC.length)
			tC[i ++] = c ;
		
		return String.valueOf(tC) ;
	}
	
	
	
	static void printExpr(int iExpr, String comment)
	{
		printExpr(iExpr, comment, LINELENGTH) ;
	}
	
  static void printExpr(int iExpr, String comment, int maxlength)
  {
	  System.out.println(sLine('-', maxlength)) ;      
  	System.out.println(comment) ;

  	if (exprs.size(iExpr) % Expressions.BEAUCOUP <= maxlength * 2)
	      System.out.println(exprs.toString(iExpr)) ;
	    
	  System.out.println("iExpr = " + iExpr + " size = "  + exprs.size(iExpr)) ;
	  //System.out.println(sLine('-', maxlength)) ;      
  }
  
	static void printLine(String line, String comment)
	{
		printLine(line, comment, LINELENGTH) ;
	}
	
  static void printLine(String line, String comment, int maxlength)
  {
  	if (interactive)
  		return ;
  	
	  System.out.println(sLine('-', maxlength)) ;      
  	System.out.println(comment) ;

  	if (line.length() <= maxlength * 2.5)
	      System.out.println(line) ;
	    
	  System.out.println("length = "  + line.length()) ;
	  //System.out.println(sLine('-', maxlength)) ;      
  }

  
  
  static String verdict(String cmd, boolean verdict)
  {
  	
  	if (cmd.equals("E") || cmd.equals("EA") || cmd.equals("EP") ||
  		  cmd.equals("EUF") || cmd.equals("EBLC"))
  	  if (verdict)
  	  	return "==" ;
  	  else
  	  	return "=/=" ;
  	  
  	if (cmd.equals("I") || cmd.equals("IA"))
  	  if (verdict)
  	  	return "<=" ;
  	  else
  	  	return "</=" ;
  	  
  	return "?" ;  	  
  }
  
  static ICheck reinitComp(String cmd)
  {
  	ICheck comp = null ;
  	if (cmd.equals("E"))
  		comp = new Equiv(exprs) ;
  	else if (cmd.equals("EA"))
  		comp = new EquivA(exprs) ;
  	else if (cmd.equals("EP"))
  		comp = new EquivP(exprs) ;
  	else if (cmd.equals("EUF"))
  		comp = new EquivUF(exprs) ;
  	else if (cmd.equals("EBLC"))
  		comp = new EquivBLC(exprs) ;
  	else if (cmd.equals("I"))
  		comp = new InfEq(exprs) ;
  	else if (cmd.equals("IA"))
  		comp = new InfEqA(exprs) ;
  	else throw new Error("Unknown comparison : " + cmd) ;
  	
  	return comp ;
  }
  
  static void compare()
  {
  	reinitMemory() ;
  	
  	String cmd = lparser.stringCommand() ;
  	
  	ICheck comp = reinitComp(cmd) ;
  	
  	long t0 = System.nanoTime() ;
  	
   	Term[] terms = new Term[2]  ;
  	int iTerm = 0 ;
  	
  	boolean gCCalled = false ;
  	int i = 0 ;
  	System.out.println("[" + i ++ + "]") ;		

	  byte ltype = lparser.readLine(in) ;

		loop : while (ltype == Line.EXPR ||
			     ltype == Line.CMD &&  
			     lparser.typeOfCommand() == Line.M)
		{		
			 if (changeMemory())			  
			  	continue loop ;
					   
			 try{
				
			   if (! gCCalled)
				 {    				 
	         terms[iTerm] = lparser.getTerm() ;	   			     
	         iTerm ++ ;
	       
	         if (iTerm != 2)
	         {
	       	   ltype = lparser.readLine(in) ;
			  	   continue loop ;
	         }
	         iTerm = 0 ;
	                
	       }
             
	       if (Term.nbrLetters(terms) > exprs.nbrLetters())
	       {	        
	         exprs.setNbrLetters(Term.nbrLetters(terms)) ;
	         exprs.reinit() ; 
	       }	
	         
	       int[] iExpr = exprs.toExpression(terms) ;

	       printExpr(iExpr[0], "Expr1");
	       printExpr(iExpr[1], "Expr2");
	         	       	          
	       boolean verdict = comp.compare(iExpr[0], iExpr[1]) ;
	       System.out.println("iExpr1 " + verdict(cmd, verdict) + " iExpr2") ;
	       System.out.println(sLine('=', LINELENGTH)) ;
	
         gCCalled = false ;
         
        }
        catch (GCException e)
        { 
      	    exprs.reinit() ; 
            comp = reinitComp(cmd) ;
            
      	    if (gCCalled)
      	    {
      		    System.out.println("Failure on input [" + (i - 1) + "]") ;     		    
      		    gCCalled = false ;
      	    }
      	    else
            {  
 
        	    gCCalled = true ;  
        	    continue loop ;
            }       
         }
         
       if (iTerm == 0)
				 	 System.out.println("[" + i ++ + "]") ;		  
       ltype = lparser.readLine(in) ;		
		}  
		
	  long totalTime = System.nanoTime() - t0 ;
	  
	  if (! interactive)
		System.out.println("Total time = " + util.Time.toString(totalTime)
	  	+ " sec") ;   
	  System.out.println("iExprList.size() = " + ((Expressions)exprs).iExprList.size()) ;
	  System.out.println(sLine('=', LINELENGTH)) ;
  }
  
  public static void main0(String[] args, int typeOfExpressions) throws GCException
	{		
		
		// Defaults
		int memorySize = MEMORYSIZE ;
		int nbrLetters = NBRLETTERS ;
		String cmd = "SOI" ;
		
		
		//in.nextLine() ;
		
		String line = "" ;
		boolean hasCmd = false ;
		{
			int i = 0 ;
			while (i != args.length)
			{
				try{				
					Integer.parseInt(args[i]) ;
			   	line += args[i] + " " ;
			  }
			  catch(NumberFormatException e)
			  {
			  	if (args[i].equals(">"))
			  		interactive = true ;
			  	else
			  	{
			  		hasCmd = true ;
			      line = args[i] + " " + line ;
			    }
			  }
				i ++ ;
			}
		}
		
		if (! hasCmd)
			line = cmd  + " " + line ;
		
		if (args.length == 0 || args.length == 1 && interactive)
			line = cmd + " " + memorySize + " " + nbrLetters ;
		
		lparser = new Line(interactive) ;
		
		byte typeOfLine = lparser.parseLine(line) ;
			
	  if (lparser.memorySize() != - 1)
	  	memorySize = lparser.memorySize() ;
	  
	  if (lparser.nbrLetters() != - 1)
	  	nbrLetters = lparser.nbrLetters() ;
		
	 initAllClasses(memorySize, nbrLetters, typeOfExpressions) ;			
		
		while (typeOfLine == Line.CMD)
		{			
			System.out.println(lparser.getString()) ;
			
			switch(lparser.typeOfCommand())
			{
			  case Line.S :
			  	   simplify() ;			  	   
				  break ;
				  
				case Line.E :
				case Line.I :	
					   compare() ;
				  break ;
				  
			  case Line.D :
			 	     showDFA() ; //computeDFA() ;
				  break ;
				  
				case Line.M :						 
					   changeMemory() ;
				  break ;				  
			}			
		  typeOfLine = lparser.type() ;
	  }
	
  }
  
  public static void main(String[] args)  throws GCException
  {
  	if (args.length == 0)
  		main0(args, 2) ;
  	else
  	{
  		String truc = args[args.length - 1] ;
  		if (!(truc.equals("Ex")||truc.equals("Si")||truc.equals("Ba")))
  			main0(args, 2) ;
  		else
  		{
  			
  		  String[] toto = new String[args.length - 1] ;
  	
  			int typeOfExpressions = 2 ;
  	  	if (truc.equals("Ex"))
  			typeOfExpressions = 0 ;
  		  if (truc.equals("Si"))
  			typeOfExpressions = 1 ;
  		  if (truc.equals("Ba"))
  			typeOfExpressions = 2 ;
  		
  		  int i = 0 ;
  		  while (i!= toto.length)
  		  {
  		  	toto[i] = args[i] ;
  		    i ++ ;
  		  }
  		  	
  		  main0(toto, typeOfExpressions) ;
  		} 		
  	}
  }

} 