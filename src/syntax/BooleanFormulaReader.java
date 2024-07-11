package syntax;

class BooleanFormulaReader extends TermReader{
	

  static Term toTerm(String term){
  	BooleanFormulaReader bfr = new BooleanFormulaReader() ;
  	bfr.putLexer(new Lexer(term.toCharArray())) ;
  	
  	try{
  	  return bfr.makeTerm() ;
  	}catch (Exception e)
  	{
  		System.out.println(e) ;
  		return null ;
  	}
  	
  }
  
  BooleanFormulaReader(Lexer l)
  {
  	super(l) ;
  }
  
  BooleanFormulaReader()
  {
  	super() ;
  }

  /* Syntaxe
  
     <BF>   ::= <DISJ>
     <DISJ> ::= <CONJ> | <DISJ> + <CONJ>
     <CONJ> ::= <PNEG> | <CONJ> <PNEG>
     <PNEG> ::= ( <DISJ> ) | <LETR> | ! <PNEG>
     <LETR> ::= a | b | ... | z | 0 | 1
  */
  
  int count  ;
  
  static boolean constant(char c)
  {
  	return ('a' <= c && c <= 'z') || c == '0' || c == '1' ;
  }
  
  int computeSizeOfTerm() throws ReaderException
  {
  	count = 0 ;
  	lDisj() ; 
  	return count ;
  }
  
  void lDisj() throws ReaderException
  // Si un préfixe de t[i ->] est une DISJ
  // non suivie d'un '+'
  // ajouter à count le nombre de symboles
  // de la représentation de cette DISJ
  // et renvoyer l'indice du dernier caractère
  // de cette DISJ
  // Sinon, renvoyer une Exception
  {
  	lConj() ;
  	char c = l.curChar() ;
  	while (c != 0 && c == '+')
  	{
  		count ++ ;
  		l.move() ;
  		lConj() ;  	
  		c = l.curChar()  ;
  	}
  }
  
  void lConj() throws ReaderException
  // Même genre pour les CONJ
  {
  	lPneg() ;
  	char c = l.curChar() ;

  	while (c != 0 
  		&& (c == '(' || c == '!'
  	     || constant(c)))
  	{  	
  		count ++ ;
  		//l.move() ;
  		lPneg() ;  	
  		c = l.curChar() ;			
  	}
  }
  
  void lPneg() throws ReaderException
  // Même genre pour les PNEG
  {
  	char c = l.curChar() ;
  	if (c == '(')
  	{
  		l.move() ;
  		lDisj() ;
  		c = l.curChar() ;
  		
  		if (c != ')')
  			throw new ReaderException("une ) était attendue "
  				+ " au lieu de " + (c == 0 ? "rien " : c), l.pos()) ;
  			
  	  l.move() ;  			
  	  return  ;
  	}
  	
  	if (c == '!')
  	{
  		count ++ ;
  		l.move() ;
  		lPneg() ;
  		return ;
  	}
  	
  	if (constant(c))
  	{
  		count ++ ;
  		l.move()  ;
  		return ;
  	}
  	
  	throw new ReaderException("une lettre ou ! ou ( "
  		+ "était attendue au lieu de " + c,
  				 l.pos()) ;
  }

  
  
  int free ;
  
  
  Term makeTerm() throws ReaderException
  {

  		int pos = l.pos() ;
  		
  		int size = computeSizeOfTerm() ;
  		
  		l.reset(pos) ;
  		
  		char[] f = new char[size + 1] ;
      int[] g  = new int[size + 1] ;
      int[] d  = new int[size + 1] ;
  
  		free = 1 ;
  		
  		makeDisj(f, g, d) ;
  		
  		return new Term(f, g, d) ;
  	
  }
  
  void makeDisj(char[] f, int[] g, int[] d)
  {
  	makeConj(f, g, d) ;
  	int pos = free - 1 ;
  	
  	char c = l.curChar() ;
  	while (c != 0 && c == '+')
  	{
  		l.move() ;
  		makeConj(f, g, d) ;  	
  		
  		f[free] = c ;
  		g[free] = pos ;
  		d[free] = free - 1 ;
  		pos = free ;
  		free ++ ;  
  		
  		c = l.curChar() ;
  	}
  }
  
  void makeConj(char[] f, int[] g, int[] d)
  // Même genre pour les CONJ
  {
  	makePneg(f, g, d) ;
    int pos = free - 1 ;

  	char c = l.curChar() ;

  	while (c != 0 
  		&& (c == '(' || c == '!'
  	     || constant(c)))
  	{  	
  		makePneg(f, g, d) ;  
  		
  		f[free] = '.' ;
  		g[free] = pos ;
  		d[free] = free - 1 ;
  		pos = free ;
  		free ++ ;  
  		
  		c = l.curChar() ;			
  	}
  }
  
  void makePneg(char[] f, int[] g, int[] d)
  // Même genre pour les PNEG
  {
  	char c = l.curChar() ;

  	if (c == '(')
  	{
  		l.move() ;
      makeDisj(f, g, d) ;
  		l.move() ;
  		return ;
  	}
  	
  	if (c == '!')
  	{
  		l.move() ;
  		makePneg(f, g, d) ;
  		
  		f[free] = '!' ;
  		g[free] = free - 1 ;
  		// d[free] = 0 ; // inutile
  		free ++ ; 
  		return ;
  	}
  	
  	if (constant(c))
  	{  		
  		f[free] = c ;
  		// g[free] = 0 ;
  		// d[free] = 0 ; // inutile
  		free ++ ;  
  		
  		l.move() ;
  		return ;
  	}
  	
  	System.out.println("On ne peut pas arriver ici.") ;
  }

  
  
  public static void main(String[] arg) throws Exception
  {
  	System.out.println("Expression : " + arg[0]) ;
  	
  	
 	   Lexer  l = new Lexer(arg[0].toCharArray()) ;
  	 TermReader toto = new BooleanFormulaReader() ; 
  	 toto.putLexer(l) ;
  	 AxiomReader ar = new AxiomReader(toto) ;
  	 Axiom axiom = ar.makeAxiom() ;
  	 
  	 //TermWriter tr = new BooleanFormulaWriter() ;
  	 //System.out.println(tr.toString(term)) ;
  	 
  	 //AxiomReader lulu = new AxiomReader(toto) ;
  	 
  	 //  	System.out.println("La taille de l'expression est "
  	 //+ toto.computeSizeOfTerm(0, arg[0].toCharArray()) + ".") ;
 
  	 //Term term = 	toto.toTerm(arg[0]) ;
  	 //Axiom axiom = lulu.makeAxiom() ;
  	 //term.print() ;
  	 
  	 AxiomWriter fifi = new AxiomWriter(new BooleanFormulaWriter()) ; 
  	 //System.out.println() ;
  	 System.out.println(fifi.toString(axiom)) ;

  }
}







