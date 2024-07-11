package regexpr ;
import syntax.* ;
import fichiers.binaryFile ;

public class GenMinExprs{
	
	/* The goal is to generate all minimal regexprs with a given
	   number of letters until a specified amount of memory
	   is used.
	   
	   We also maintain the MDFA of those expressions in order
	   to check if other expressions are equal to one of them.
	   
	   For efficiency we can keep in memory other short but not
	   minimal expressions but this take more space.
	
	*/
	
	Background exprs ;
	MultiList minExprs ; // lists of min exprs of a given size
	final int EMMES = 50 ; // expected maxMinExprsSize
	MultiList minMDFAs ; // lists of MDFAs of the min exprs
	int EMDFAS = - 1 ; 
	int nl ;
	Equiv equ ;	
	MinimizeNew minm ;
	MakeDFA mDFA ;
	//SolveEquations solve ;
	GC gc ;
	
  static int LINELENGTH = 80 ;

	
	public MultiList getMinMDFAs()
	{
		return minMDFAs ;
	}
	
	public MultiList getMinExprs()
	{
		return minExprs ;
	}

	public GenMinExprs(Background exprs, int memorySize, int nl, int maxL) // to check whether expressions are minimal
	{
		this.exprs = exprs ;
		readMinExprs(memorySize, nl, maxL) ;
		mDFA = new MakeDFA(exprs) ;

		minm = new MinimizeNew(exprs, mDFA) ;
		equ = new Equiv(exprs) ;
		gc = new GC(exprs, minMDFAs) ;
	}
	
	public GenMinExprs(Background exprs) // to generate minimal expressions
	{
		this.exprs = exprs ;
		
		EMDFAS = exprs.memorySize() / 10 * 11 + 1 ;
		minExprs = new MultiList(EMMES, exprs.memorySize()) ;
		reinit(exprs) ;
		minMDFAs = new MultiList(EMDFAS, exprs.memorySize()) ;
		nl = exprs.nbrLetters() ;	
		//System.out.println(minMDFAs) ;
	}
	
	public void gc()
	{
		gc.gc() ;
	}
	
	void reinit(Background exprs)
	// Not used, except in constructor...
	{
		//nl = exprs.nbrLetters() ;
		mDFA = new MakeDFA(exprs) ;
		minm = new MinimizeNew(exprs, mDFA) ;
		equ = new Equiv(exprs) ;
		//solve = new SolveEquations(exprs, new SEOptimizer3(exprs)) ;
		gc = new GC(exprs, minMDFAs) ;
	}
	
	// generate all minimal expressions in a binary file
	static void genMinExprs(int memorySize, int nl, int maxL)  throws GCException
	{
		Background exprs = new Background(memorySize, nl) ;
		GenMinExprs gen = new GenMinExprs(exprs) ;
		
		gen.genMinExprs(maxL) ;
		
		String fileName = "minExprs/file-"
			+ nl + "-" + memorySize + "-" + maxL + ".bin" ;
		binaryFile f = new binaryFile(fileName) ;
		f.rewrite() ;
		if (f.ioError() != 0)
			throw new Error("File " + fileName + " can't be opened.") ;
		
		{ 
			int m = 0 ;
			while (m != gen.EMDFAS)
			{
				int iExpr = gen.minMDFAs.first(m) ;
				while (iExpr != - 1)
				{
					f.write(iExpr) ;
				  f.write(m) ;
				  f.write(exprs.type[iExpr]) ;
				  {
					  int[] tabE = exprs.tabNexpr[iExpr] ;
				    f.write(tabE.length) ;				
					  int i = 0 ;
					  while (i != tabE.length)
					  {
						  f.write(tabE[i]) ;
						  i ++ ;
					  }					
				  }
				  f.write(exprs.tabCode[iExpr]) ;
				  f.write(exprs.sizeExprs[iExpr]) ;
				  
				  iExpr = gen.minMDFAs.next(iExpr) ;
				}
										
				m ++ ;
			}
		}
		
		f.close() ;
	}
	
	public void readMinExprs(int memorySize, int nl, int maxL)  //throws GCException
	// Pré : exprs is just initialized or re-initialized
	{
		
		if (nl != exprs.nbrLetters())
			throw new Error(nl + " = nl != exprs.nbrLetters() = "
				+ exprs.nbrLetters()) ;
			
		EMDFAS = memorySize / 10 * 11 + 1 ;
			
		minMDFAs = new MultiList(EMDFAS, exprs.memorySize()) ;
	  gc = new GC(exprs, minMDFAs) ;

		String fileName = "minExprs/file-"
			+ nl + "-" + memorySize + "-" + maxL + ".bin" ;
		binaryFile f = new binaryFile(fileName) ;
		f.reset() ;
		if (f.ioError() != 0)
			throw new Error("File " + fileName + " can't be opened.") ;
		
		System.out.println("File " + fileName + " is read.") ;
		
		while (! f.eof())
		{
			int iExpr = f.read_int() ;
			if (iExpr >= exprs.memorySize())
				throw new Error(iExpr + " = iExpr >= exprs.memorySize() = "
					+ exprs.memorySize()) ;
			
			int m = f.read_int() ;
			minMDFAs.add(m, iExpr) ;
			
			exprs.type[iExpr] = f.read_byte() ;
			
			int lTabE = f.read_int() ;
			int[] tabE = new int[lTabE] ;
			exprs.tabNexpr[iExpr] = tabE ;
			{
				int i = 0 ;
				while (i != tabE.length)
				{
					tabE[i] = f.read_int() ;
					i ++ ;
				}
			}
			
			exprs.tabCode[iExpr] = f.read_int() ;
			exprs.sizeExprs[iExpr] = f.read_long() ;
			
			int pos = exprs.pos(iExpr) ;
			exprs.hashTable.add(pos, iExpr) ;
			exprs.iExprList.add(iExpr) ;
		}
		
		f.close() ;
	}
	

  public void genMinExprs(int maxL)  throws GCException
  // We gen expressions (and MDFAs) until
  // there exists an identifier iExpr > amount
  {
  	// We initialize the lists with 1, a, b, ... (
  	
  	
  	{
  		
  		  minExprs.add(0, exprs.one()) ;
  	    //System.out.println(1) ;
  	    int m1 = minm.minimize(exprs.one()) ;
  	    if (m1 < 0)
  		    m1 = -m1 ;
  	
  	    m1 %= EMDFAS ;
  	    minMDFAs.add(m1, exprs.one()) ;

  		  char x = 'a' ;
  		  while (x != 'a' + nl)
  		  {
  		  	int iExpr = exprs.iLetter(x) ;
  			  minExprs.add(1, iExpr) ;
  			  //System.out.println(exprs.toString(minExprs.first(1))) ;
  			
          int m = minm.minimize(iExpr) ;
  	    
  	      if (m < 0)
  		    m = -m ;
  	
  	      m %= EMDFAS ;
  	      minMDFAs.add(m, iExpr) ;

  	    x ++ ;
  		 }
  	}
  	
  	// We compute all small expressions in increasing order
  	// (ignoring normalization), we check if they are minimal.
  	// If it is so, we add them in their lists
  	
  	{
  		long t0 = System.nanoTime() ;
  		int l = 2 ;
      
  	  while (l <= maxL) 			
       {
       	 System.out.println() ;

  			int p = 0 ;
  			int q = l ;
  			//System.out.println("l = " + l + " p = " + p + " q = " + q) ;
  			while (p != l)
  			{
  				int iExprP = minExprs.first(p) ;
  				while (iExprP != - 1)
  				{
  					//System.out.println("iExprP = " + exprs.toString(iExprP)) ;
  					int iExprQ = minExprs.first(q - 1) ;
  				  while (iExprQ != - 1)
  				  {
  				  	
  				  	
  				  	//System.out.println("l = " + l + " p = " + p + " q = " + q) ;
  				  	//System.out.println("iExprQ = " + exprs.toString(iExprQ)) ;
              if (exprs.type(iExprP) != Expressions.CONCAT)
  				  	{
  				  		int iExprC ;
  				  		
  				  		try {
  				  			iExprC = exprs.concat(iExprP, iExprQ) ; 
  				  		}catch(GCException e)
  				  		{
  				  			gc.gc() ;
  				  			iExprC = exprs.concat(iExprP, iExprQ) ; 
  				  		}
  				  		
  				  	  if (exprs.size(iExprC) == l)
  				  	  checkIsMin(iExprC, l) ;
  				  	}
  				  	
  				  	if (exprs.type(iExprP) != Expressions.UNION)
  				  	{
  				  		if (iExprP < exprs.tabE(iExprQ)[0])
  				  	  {
  				  		  int iExprU ;
  				  		  try{
  				  		  	iExprU = exprs.union(iExprP, iExprQ) ;
  				  		  }catch(GCException e)
  				  		  {
  				  			  gc.gc() ;
  				  		  	iExprU = exprs.union(iExprP, iExprQ)  ; 
  				  		  }
  				  		  if (exprs.size(iExprU) == l)
  				  	    checkIsMin(iExprU, l) ;
  				  	  }		
  				  	}
  					  iExprQ = minExprs.next(iExprQ) ;
  				  } 
  				  
  				  if (p == l - 1)
  			   	{
  					  int iExprS;
  					  try{
  				  		  	iExprS = exprs.star(iExprP) ;
  				  		  }catch(GCException e)
  				  		  {
  				  			  gc.gc() ;
  				  		  	iExprS = exprs.star(iExprP) ;
  				  		  }
  					  if (exprs.size(iExprS) == l)
  				  	  checkIsMin(iExprS, l) ;
  				  }
  				  
  					iExprP = minExprs.next(iExprP) ;
  				}   				
   			  //System.out.println(" p = " + p + " q = " + q) ;
 				
  				p ++ ; q -- ;
  			}			
  			
  			System.out.println("l = " + l + " finished : iExprList.size " +
			  exprs.iExprList.size()) ;
			  
			  l ++ ;
  		}
      
  		gc.gc() ;
      System.out.println("Total time = " + util.Time.toString(System.nanoTime() - t0) 
	  	+ " sec [GenMinExprs]") ;  	
  	}
  }
  
  
  
  int count = 0 ;
  int countBetter = 0 ;
  int sumDelta = 0 ;
  
  int checkIsMin(int iExpr, int l) throws GCException
  // Phase 1 (generation)
  {
  	
  	int m = 0 ;
  	try {
  		m = minm.minimize(iExpr) ;
  	}
    catch(GCException e)
    {
    	//System.out.println("4 " + iExpr + " " + m
  		//	+ " " + exprs.size(iExpr) + " " + (count)) ;
  		
    	gc.gc(iExpr) ;

    	//System.out.println("4' " + exprs.toString(iExpr) + " " + m
  		//	+ " " + exprs.size(iExpr) + " " + (count)) ;
    	m = minm.minimize(iExpr) ;
    	//System.out.println("4'' " + iExpr + " " + m
  		//	+ " " + exprs.size(iExpr) + " " + (count)) ;
    }
  	
  	if (m < 0)
  		m = -m ;  	
  	if (m < 0)
  		m = 1 ;
  	
  	m %= EMDFAS ;
  	
  	int iExprM = minMDFAs.first(m) ;
  	boolean exists = false ;
  	while (iExprM != - 1 && ! exists)
  	{
  		try{
  			exists = equ.eq(iExpr, iExprM) ;
  	  }
  	  catch(GCException e)
  		{
  			//System.out.println("5 " + exprs.toString(iExpr) + " " + m
  			//+ " " + exprs.size(iExpr) + " " + (count)) ;
  			gc.gc(iExpr) ;
  			//reinit(exprs) ;
  			exists = equ.eq(iExpr, iExprM) ;
  	  }
  		if (exists)
  			break ;
  		iExprM = minMDFAs.next(iExprM) ;
  	}
  	
  	
  	if (! exists)
  	{
  		++ count ;
  		
  		minMDFAs.add(m, iExpr) ;  
  		minExprs.add(l, iExpr) ;
  		return iExpr ;
  	}
  	else
  	{		
  		return iExprM ;
  	}
  }
  
  //----------------------------------------------------------------------
  
  public int checkIsMin(int iExpr0) throws GCException
  // For simplifying
  {
  	
  	int[][] tabPGS = exprs.removeExtremeLetters(iExpr0) ;
  	int iExpr = exprs.concat(tabPGS[1], exprs.one()) ;
  	
  	int m = minm.minimize(iExpr) ;
  	
  	if (m < 0)
  		m = -m ;  	
  	if (m < 0)
  		m = 1 ;
  	
  	m %= EMDFAS ;
  	
  	int iExprM = minMDFAs.first(m) ;
  	boolean exists = false ;
  	while (iExprM != - 1)
  	{
  		exists = equ.eq(iExpr, iExprM) ; 	  
  		if (exists)
  			break ;
  		
  		iExprM = minMDFAs.next(iExprM) ;
  	}
  	
  	
  	if (! exists)
  	{
  		return - 1 ;
  	}
  	else
  	{
  		exprs.unify(iExpr, iExprM) ;
  		exprs.unify(iExpr0, exprs.concat(tabPGS[0], 
  			       exprs.concat(iExprM, 
  			     	   exprs.concat(tabPGS[2], exprs.one())))) ;
  		return exprs.bestExpr(iExpr0) ;
  	}
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
	
	void printExpr(int iExpr, String comment)
	{
		printExpr(iExpr, comment, LINELENGTH) ;
	}
	
  void printExpr(int iExpr, String comment, int maxlength)
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
  	
	  System.out.println(sLine('-', maxlength)) ;      
  	System.out.println(comment) ;

  	if (line.length() <= maxlength * 2.5)
	      System.out.println(line) ;
	    
	  System.out.println("length = "  + line.length()) ;
	  //System.out.println(sLine('-', maxlength)) ;      
  }

 public static void main(String[] args) throws GCException
 {
 	 // args[0] : number of letters
 	 // args[1] : memory size
 	 // args[2] : max length of minimal expressions
 	 int nl = Integer.parseInt(args[0]) ;
 	 int memorySize = Integer.parseInt(args[1]) ;
 	 int maxL = Integer.parseInt(args[2]) ;
 	 Background exprs = new Background(memorySize, nl) ;
 	 GenMinExprs gen = new GenMinExprs(exprs) ;
 	 
 	 gen.readMinExprs(memorySize, nl, maxL) ;
 	 
 	 MakeDFA mDFA = new MakeDFA(exprs) ;
 	 Simplify simplifier = new Simplify(exprs, new EquivUF(exprs), new InfEqA(exprs), mDFA, new MinimizeNew(exprs, mDFA)) ;
 	 
 	 simplifier.setGenMinExprs(gen) ;
 	 System.out.println(gen.minMDFAs) ;
 	 
 	 
 	 java.util.Scanner in = new java.util.Scanner(System.in) ;
 	 int sumSizes = 0 ;
 	 int sumSizesMin = 0 ;
	 int nbrExpr = 0 ;
	 int nbrMinimal = 0 ;
	 boolean gCCalled = false ;
	 int i = 0 ;
	 long t0 = System.nanoTime() ;
	 Term term = null ;
	 
	 in.nextLine() ;

	 System.out.println("[" + i ++ + "]") ;
   String line = in.nextLine() ;
	 loop : while (! line.equals(";"))
		{		
			try{
				
			  if (! gCCalled)
			  {
			  	printLine(line, "Original expression") ;     
	        term = RegExprReader.toTerm(line) ;	        
	      }
	      
	      
	              
	      int iExpr = exprs.toExpression(term) ;
	      //if (gCCalled)
	      //System.out.println(iExpr + " " + exprs.toString(iExpr)) ;

	      gen.printExpr(iExpr, "Normalized expression") ;
	      
	      int iExprs = simplifier.simplify(iExpr, true, true, true, true) ;
	      
	      sumSizes += exprs.size(iExprs) ;
	      
	      int iExprM = gen.checkIsMin(iExprs) ;
	      
	      if (iExprM != - 1)
	      {
	      	gen.printExpr(iExprM, "Minimal expression") ;      
	        sumSizesMin += exprs.size(iExprM) ; 
	        nbrMinimal ++ ;
	      }
	      else
	      {
	      	gen.printExpr(iExprs, "Simplified expression") ;
	      }
	      	
	      nbrExpr ++ ;	      
        gCCalled = false ;
      }
      catch (GCException e1)
      {             	
         //System.out.println("coucou 0 " + gCCalled) ;
         //gen.gc = new GC(exprs, gen.minMDFAs) ;
 	       
         //gen.reinit(exprs) ;	
         gen.gc() ;
 	       simplifier.reinit() ; 
 	       //simplifier.setGenMinExprs(gen) ;
 	       //gen.readMinExprs(memorySize, nl, maxL) ;

      	 if (gCCalled)
      	 {
      		 System.out.println("Failure on input [" + (i - 1) + "]") ;
      		 gCCalled = false ;
      	 }
      	 else
         {  
         	 //System.out.println("coucou 1 ") ;
        	 gCCalled = true ;  
        	 continue loop ;
         }       
       }
      				  
      System.out.println("[" + i ++ + "]") ; 
      line = in.nextLine();
		}
		
		long totalTime = System.nanoTime() - t0 ;   
	  
	  if (nbrExpr != 0)
	  {
	  	System.out.println("Average size = " + (sumSizes * 100 / nbrExpr / 100.0) + " [simplified expressions]") ;   	  	
	  }
	  
		if (nbrMinimal != 0)
	  {	  	
	   System.out.println("Average size = " + (sumSizesMin * 100 / nbrMinimal / 100.0) + " [minimal expressions]") ;   	  	
	  }
	  
	  System.out.println("Nbr minimal expressions = " + nbrMinimal) ;
	
	  System.out.println("iExprList.size() = " + exprs.iExprList.size()) ;
 	 
 }
 
 public static void main1(String[] args) throws GCException
 {
 	 // args[0] : number of letters
 	 // args[1] : memory size
 	 // args[2] : max length of minimal expressions
 	 int nl = Integer.parseInt(args[0]) ;
 	 int memorySize = Integer.parseInt(args[1]) ;
 	 int maxL = Integer.parseInt(args[2]) ;
   genMinExprs(memorySize, nl, maxL) ;
 }

}