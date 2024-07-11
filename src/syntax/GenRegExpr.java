package syntax ;


import util.BigNumber ;
import java.util.Random ;
import regexpr.* ;
import util.* ;
import java.util.Scanner ;
//import fichiers.text ;

public class GenRegExpr{
	
	Random rand = new Random() ;
	
	double[][] Fr ;
	double[] Pomega ;
	double[] Pstar ;
	
	static int choose(double[] fr, double x)
	{
		int inf = 1 ;
		int sup = fr.length - 1 ;
		while (inf < sup)
		{
			int m = inf + (sup - inf - 1) / 2  ;
			if (fr[m] < x) 
				inf = m + 1 ;
			else
				sup = m ;			
		}
		
		return sup ;
	}
	 
	char chooseOp(int l, boolean StarAllowed)
	{
		double limit = 0.5 ;
		if (StarAllowed)
			limit = Pomega[l] ;
		double	x = rand.nextDouble() ;
		
		if (x <= limit)
			return '+' ;
		
		if (x <= 2 * limit)
			return '.' ;
		
		return '*' ;
		
	}
	
	char chooseLetter()
	// choisir '1' ou une lettre parmi les nbrOfLetters premières
	{
		int x = (int)(rand.nextDouble() * (nbrOfLetters + 1)) ;
		if (x == 0)
			return '1' ;
		return (char)('a' + x - 1) ;
	}
	
	void genSubTerm(int i, int l, char[] f, int[] g, int[] d, boolean StarAllowed)
	// 1 <= l ; 0 <= i + l <= f.length
	// générer "aléatoirement" un sous-terme (reg expr)
	// de taille l, à partir de la position i
	{
	  if (l == 1)
	  {
	  	f[i] = chooseLetter() ;
	  	g[i] = 0 ;
	  	d[i] = 0 ;
	  	return ;
	  }
		  
	  if (l == 2)
	  {
	  	f[i] = chooseLetter() ;
	  	g[i] = 0 ;
	  	d[i] = 0 ;
	  	f[i + 1] = '*' ;
	  	g[i + 1] = i ;
	  	d[i + 1] = 0 ;
	  	return ;
	  }
	  
	  char op = chooseOp(l, StarAllowed) ;
	  
	  if (op == '*')
	  {
	  	genSubTerm(i, l - 1, f, g, d, false) ;
	  	f[i + l - 1] = op ;
	  	g[i + l - 1] = i + l - 2 ;
	  	d[i + l - 1] = 0 ;
	  	return ;
	  }
	  
	  double x = rand.nextDouble() ;
	  int l1 = choose(Fr[l], x) ;
	  
	  genSubTerm(i, l1, f, g, d, true) ;
	  genSubTerm(i + l1, l - l1 - 1, f, g, d, true) ;
	  f[i + l - 1] = op ;
	  g[i + l - 1] = i + l1 - 1 ;
	  d[i + l - 1] = i + l - 2 ;
	
	}
		
	
	
	public Term genTerm(int l)
	// 1 <= l <= maxLength
	// créer aléatoirement un terme de taille l
	// Tous les termes ont "exactement" la même probabilité
	// d'être choisis (en supposant que le générateur de nombres
	// aléatoires est parfaitement uniforme).
	{
		
		char[] f = new char[l] ;
		int[] g = new int[l] ;
		int[] d = new int[l] ; 
		
		genSubTerm(0, l, f, g, d, true) ;
		
		return new Term(f, g, d) ;
	}
	
	public Term genTermStar(int l)
	// 1 <= l <= maxLength
	// créer aléatoirement un terme de taille l
	// Tous les termes ont "exactement" la même probabilité
	// d'être choisis (en supposant que le générateur de nombres
	// aléatoires est parfaitement uniforme).
	{
		
		char[] f = new char[l] ;
		int[] g = new int[l] ;
		int[] d = new int[l] ; 
		
		g[l - 1] = l - 2 ;
		f[l - 1] = '*' ;
		d[l - 1] = 0 ;
		
		genSubTerm(0, l - 1, f, g, d, true) ;
		
		Term t = new Term(f, g, d) ;
				
		return t ;
	}
	
	final int nbrOfLetters ;
	final int maxLength ;
	
	/*public static void genTextFile(int nbrLetters, int l, int n)
	{
		RegExprWriter rw =  new RegExprWriter() ;
		GenRegExpr gr = new GenRegExpr(nbrLetters, l) ;
		int i = 0 ;
		while (i != n)
		{
			System.out.println(rw.toString(gr.genTerm(l))) ;
				i ++ ;
		}
		System.out.println(";") ;
	}
	
	public static void genTextFileNotInfEq(int nbrLetters, int l, int n)
	{
				char[] normalLetters = new char[256];
		{
	  	int c =  1 ;
		  while (c != 256)
			{
				normalLetters[c] = (char) (c ++) ;
				
		    //System.out.println(c + " " + normalLetters[c - 1]) ;
		  }
	  }	
	  char[] actualLetter = normalLetters ;
	
		int memorySize = 5000000 ;
	  Expressions exprs = new Expressions(memorySize, actualLetter) ;
	  Flags flags = new Flags(memorySize) ;
	  
	  GPartition part  = new GPartition(exprs, null, nbrLetters, flags) ;
	  Equations eqs   = new Equations(exprs, part, flags) ;
	  part.eqs = eqs ;
	  PartitionNew partN = new PartitionNew(part, exprs, flags) ;
	  Rules rules = new Rules(exprs, eqs, part, partN, nbrLetters) ;	

	  GC gc = new GC(exprs, flags, eqs, part, new int[]{}, rules, partN);
	
		RegExprWriter rw =  new RegExprWriter() ;
		GenRegExpr gr = new GenRegExpr(nbrLetters, l) ;
		
		
		int count = 0 ;
		while (count != n)
		{
			//System.out.println(rw.toString(gr.genTerm(l))) ;
			try{
		    //Term tMax = RegExprReader.toTerm("(a + b)*") ;
		    //int  iEMax = exprs.termToExpression(tMax) ;

			  Term t1 = gr.genTerm(l) ;
			  int iExpr1 = exprs.termToExpression(t1) ;
			  
			  Term t2 = gr.genTerm(l) ;
			  int iExpr2 = exprs.termToExpression(t2) ;
			  
			  boolean  infEq = eqs.infEqAnti(iExpr1, iExpr2, nbrLetters) ;
			  if (! infEq)
			  {   
			     count += 2 ;
			  	 System.err.println(rw.toString(t1)) ;
			  	 System.err.println(rw.toString(t2)) ;
			    
			  }
			}
		  catch(GCException e)
		    { gc.gc(exprs.zero(), - 1, false) ;  }
		}
		System.err.println(";") ;
	}
		
	public static void genTextFileEq(int nbrLetters, int l, int n, String exprEq)
	{
				char[] normalLetters = new char[256];
		{
	  	int c =  1 ;
		  while (c != 256)
			{
				normalLetters[c] = (char) (c ++) ;
				
		    //System.out.println(c + " " + normalLetters[c - 1]) ;
		  }
	  }	
	  char[] actualLetter = normalLetters ;
	
		int memorySize = 5000000 ;
	  Expressions exprs = new Expressions(memorySize, actualLetter) ;
	  Flags flags = new Flags(memorySize) ;
	  
	  GPartition part  = new GPartition(exprs, null, nbrLetters, flags) ;
	  Equations eqs   = new Equations(exprs, part, flags) ;
	  part.eqs = eqs ;
	  PartitionNew partN = new PartitionNew(part, exprs, flags) ;
	  Rules rules = new Rules(exprs, eqs, part, partN, nbrLetters) ;	

	  GC gc = new GC(exprs, flags, eqs, part, new int[]{}, rules, partN);
	
		RegExprWriter rw =  new RegExprWriter() ;
		GenRegExpr gr = new GenRegExpr(nbrLetters, l) ;
		
		
		int i = 0 ;
		int count = 0 ;
		while (count != n)
		{
			//System.out.println(rw.toString(gr.genTerm(l))) ;
			try{
		    Term tMax = RegExprReader.toTerm(exprEq) ;
		    int  iEMax = exprs.termToExpression(tMax) ;

			  Term t = gr.genTerm(l) ;
			  int iExpr = exprs.termToExpression(t) ;
			  boolean infEq = eqs.infEqAnti(iEMax, iExpr, nbrLetters) ;
			  if (infEq)
			  	infEq = eqs.infEqAnti(iExpr, exprs.star(iEMax), nbrLetters) ;
			  System.out.println(infEq + " [" + i + "]") ;
			  if (infEq)
			    {
			     count ++ ;
			  	 System.err.println(rw.toString(t)) ;
			    }
			  }
		  catch(GCException e)
		    { gc.gc(exprs.zero(), - 1, false) ;  }
		  i ++ ;
		}
		System.err.println(";") ;
	}
	
		
	public static void genTextFileInfEq(int nbrLetters, int l, int n)
	{
				char[] normalLetters = new char[256];
		{
	  	int c =  1 ;
		  while (c != 256)
			{
				normalLetters[c] = (char) (c ++) ;
				
		    //System.out.println(c + " " + normalLetters[c - 1]) ;
		  }
	  }	
	  char[] actualLetter = normalLetters ;
	
		int memorySize = 5000000 ;
	  Expressions exprs = new Expressions(memorySize, actualLetter) ;
	  Flags flags = new Flags(memorySize) ;
	  
	  GPartition part  = new GPartition(exprs, null, nbrLetters, flags) ;
	  Equations eqs   = new Equations(exprs, part, flags) ;
	  part.eqs = eqs ;
	  PartitionNew partN = new PartitionNew(part, exprs, flags) ;
	  Rules rules = new Rules(exprs, eqs, part, partN, nbrLetters) ;	

	  GC gc = new GC(exprs, flags, eqs, part, new int[]{}, rules, partN);
	
		RegExprWriter rw =  new RegExprWriter() ;
		GenRegExpr gr = new GenRegExpr(nbrLetters, l) ;
		
		
		int count = 0 ;
		while (count < n)
		{
			//System.out.println(rw.toString(gr.genTerm(l))) ;
			try{
								 
				Term tMax1 = RegExprReader.toTerm("(a + b)*") ;
		    int  iEMax1 = exprs.termToExpression(tMax1) ;

		    Term tInf = gr.genTerm(l) ;
		    int  iEInf = exprs.termToExpression(tInf) ;

			  Term tSup = gr.genTermStar(l) ;
			  int iESup = exprs.termToExpression(tSup) ;
			  while (eqs.infEqAnti(iEMax1, iESup, nbrLetters))
			  {
			  	tSup = gr.genTermStar(l) ;
			  	iESup = exprs.termToExpression(tSup) ;
			  }
			  
			  
			  boolean infEq = eqs.infEqAnti(iEInf, iESup, nbrLetters) ;
			  while (!infEq)
			  {
			  	tInf = gr.genTerm(l) ;
			  	iEInf = exprs.termToExpression(tInf) ;
			  	infEq = eqs.infEqAnti(iEInf, iESup, nbrLetters) ;
			  }
			  System.out.println(infEq + " [" + count + "]") ;
			  if (infEq)
			    {
			     count += 2 ;
			  	 System.err.println(rw.toString(tInf)) ;
			  	 System.err.println(rw.toString(tSup)) ;
			    }
			  }
		  catch(GCException e)
		    { gc.gc(exprs.zero(), - 1, false) ;  }
		}
		System.err.println(";") ;
	}*/
	
	
	public GenRegExpr(int nbrOfLetters, int maxLength)
	{
		this.nbrOfLetters = nbrOfLetters ;
		this.maxLength = maxLength ;
		BigNumber[] Nstar = new BigNumber[maxLength + 1] ;
	  BigNumber[] Nomega = new BigNumber[maxLength + 1] ;
	  BigNumber[] N = new BigNumber[maxLength + 1] ;
	  
	  Fr = new double[maxLength + 1][] ;
	  Pomega = new double[maxLength + 1] ;
	  Pstar = new double[maxLength + 1] ;
	
		
		N[0] = new BigNumber() ;
		N[1] = new BigNumber(1 + nbrOfLetters) ;
		N[2] = new BigNumber(1 + nbrOfLetters) ;
		Nstar[0] = new BigNumber() ;
		Nstar[1] = new BigNumber() ;
		Nstar[2] = new BigNumber(1 + nbrOfLetters) ;
		Nomega[0] = new BigNumber() ;
		Nomega[1] = new BigNumber() ;
		Nomega[2] = new BigNumber() ;
		
		int l = 3 ;
		while (l != N.length)
		{
			// Calcul de Nomega[l] ;
			BigNumber x = new BigNumber(0)  ;
			int l1 = 1 ;
			while (l1 != l - 1)
			{
				x.plusTimes(N[l1], N[l - l1 - 1]) ;
				l1 ++ ;
			}
			Nomega[l] = x ;
			Nstar[l] = Nomega[l - 1] ;
			
			N[l] = new BigNumber() ;
			N[l].plus(Nomega[l], Nomega[l]) ;
			N[l].plus(Nstar[l]) ;
			
			//System.out.println(l + " " + N[l] + " ; ") ;
			
			Pomega[l] = Nomega[l].divide(N[l]).toDouble() ;
			Pstar[l] = Nstar[l].divide(N[l]).toDouble() ;
			
			//System.out.println(l + " Pr.+ = " + Pomega[l] + " Pr* = " + Pstar[l]) ; 
			
			Fr[l] = new double[l] ;
			BigNumber z = new BigNumber() ;
			int l2 = 1 ;
			while (l2 != l - 1)
			{
				BigNumber y = (N[l2].times(N[l - l2 - 1])).divide(Nomega[l]) ;
				z.plus(y) ;
			 
			  Fr[l][l2] = z.toDouble() ;
			  //System.out.println("Fr[" + l2 + "|" + l + "] = " 
			 	// + Fr[l][l2]) ;
			  
				l2 ++ ;
			}
			
			l ++ ;
		}
  }
  
  /*public static void genDecreasingPairs(final int n)
  // n >= 1
  // Gen pairs such as : <aa + ab + ba + bb, (a + b)(a + b)> (example for n = 2)
  {
  	System.out.println("2 1000 100") ;
  	String[] toto = {"a", "b"} ;
  	int i = 1 ;
  	while (i != n)
  	{
  		String[] toto1 = new String[toto.length * 2] ;
  		int ja = 0 ;
  		while (ja != toto.length)
  		{
  			toto1[ja] = "a" + toto[ja] ;
  			ja ++ ;
  		}
  		
  		int jb = 0 ;
  		while (jb != toto.length)
  		{
  			toto1[toto.length + jb] = "b" + toto[jb] ;
  			jb ++ ;
  		}
  		toto = toto1 ;
  		i ++ ;
  	}
  	
  	System.out.print(toto[0]) ;
  	int j = 1 ;
  	while (j != toto.length)
  	{
  		System.out.print(" + " + toto[j]) ;
  		j ++ ;
  	}
  	
  	System.out.println() ;
  	
  	//--------------
  	
  	int k = 0 ;
  	while (k != n)
  	{
  		System.out.print("(a + b)") ;
  		k ++ ;
  	}
  	
  	System.out.println() ;
  	System.out.println(";") ;

  }*/
  
  public static void main(String[] args)
  {
  	if (args.length != 3)
  	{
  		System.err.println("This programs needs three arguments : " 
  			+ "nbrLetters expression-length number-of-expressions.") ;
  		return ;
  	}
  	
  	int nbrLetters = Integer.parseInt(args[0]) ;
  	if (nbrLetters < 0 || nbrLetters > 26)
  	{
  		System.err.println("Wrong nbrLetters : 0 <= nbrLetters <= 26") ;
  		return ;
  	}
  	
  	int l = Integer.parseInt(args[1]) ;
  	if (l < 2)
  	{
  		System.err.println("Wrong expression-length : must be >= 2") ;
  		return ;
  	}
  	
  	int n = Integer.parseInt(args[2]) ;
  	if (n < 0)
  	{
  		System.err.println("Wrong number-of-expressions : must be >= 0") ;
  		return ;
  	}
  	
  	RegExprWriter rw =  new RegExprWriter() ;
  	//System.err.println(nbrLetters + " " + l + " " + n) ;
  	//genTextFileNotInfEq(nbrLetters, l, n) ;
  	GenRegExpr g = new GenRegExpr(nbrLetters, l) ;
  	int i = 0 ;
  	while (i != n)
  	{
  		System.out.println(rw.toString(g.genTermStar(l))) ;
  		i ++ ;
  	}
  }
  
}