package syntax;

public class RegExprReader extends TermReader{
	
  static Term previous = null ;
  
  static public void suppressPrevious()
  {
  	previous = null ;
  }
  
  public static Term toTerm(String line){
  	RegExprReader rer = new RegExprReader() ;
  	Lexer l = new Lexer(line.toCharArray());
  	rer.putLexer(l) ;
  	
  	try{
  	  Term t = rer.makeTerm() ;
  	  //l.move() ;
  	  char c = l.curChar() ;
  	  if (c != 0)
  	  {
  	  	System.err.println("character " + c + " at position " + l.pos() + " is useless.") ;
  		  return null ;
  		}
  		else 
  			return t ;
  	}catch (ReaderException e)
  	{
  		System.err.println(e + " " + e.msg1 + " at position " + e.pos + ".") ;
  		System.err.println(line) ;
  		return null ;
  	} 	
  }
  
  RegExprReader(Lexer l)
  {
  	super(l) ;
  }
  
  RegExprReader()
  {
  	super() ;
  }

  /* Syntaxe
  
     <RE>   ::= <UNION>
     <UNION> ::= <CONC> | <UNION> + <CONC>
     <CONC> ::= <PSTAR> | <CONC> <PSTAR>
     <PSTAR> ::= <PCOMPL> | <PSTAR> *
     <PCOMPL> ::= <PLETR> | ! <PLETR>
     <PLETR> ::= (<UNION>) | (<UNION> \ & ^ <UNION>) | <LETR>
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
  	lUnion() ; 
  	return count ;
  }
  
  void lUnion() throws ReaderException
  {
  	lConc() ;
  	char c = l.curChar() ;
  	while (c == '+')
  	{
  		count ++ ;
  		l.move() ;
  		lConc() ;  	
  		c = l.curChar()  ;
  	}
  }
  
  void lConc() throws ReaderException
  {
  	lPstar() ;
  	char c = l.curChar() ;

  	while (c == '(' || constant(c))
  	{  	
  		count ++ ;
  		//l.move() ;
  		lPstar() ;  	
  		c = l.curChar() ;			
  	}
  }
  
  void lPstar() throws ReaderException
  {
  	lPcompl() ;
  	char c = l.curChar() ;

  	while (c == '*')
  	{  	
  		count ++ ;
  		l.move() ;
  		c = l.curChar() ;			
  	}
  }
  
  void lPcompl() throws ReaderException
  {
  	char c = l.curChar() ;
  	
  	if (c == '!')
  	{
  		count ++ ;
  		l.move() ;
  	}
  	
  	lPletr() ; 		
  }
  
  void lPletr() throws ReaderException
  // Même genre pour les PNEG
  {
  	char c = l.curChar() ;
  	if (c == '(')
  	{
  		l.move() ;
  		lUnion() ;
  		c = l.curChar() ;
  		
  		if (c == '\\' || c == '&' || c == '^')
  		{
  			count ++ ;
  			l.move() ;
  		  lUnion() ;
  		  c = l.curChar() ;  			
  		}
  		
  		if (c != ')')
  			throw new ReaderException("a ) was expected "
  				+ " instead of " + (c == 0 ? "nothing " : c), l.pos()) ;
  			
  	  l.move() ;  			
  	  return  ;
  	}
  	
  	
  	if (constant(c))
  	{
  		count ++ ;
  		l.move()  ;
  		return ;
  	}
  	
  	throw new ReaderException("a letter or ( "
  		+ "was expected instead of " + (c == 0 ? "nothing " : c),
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
  		
  		makeUnion(f, g, d) ;
  		
  		return new Term(f, g, d) ;  	
  }
  
  void makeUnion(char[] f, int[] g, int[] d)
  {
  	makeConc(f, g, d) ;
  	int pos = free - 1 ;
  	
  	char c = l.curChar() ;
  	while (c == '+')
  	{
  		l.move() ;
  		makeConc(f, g, d) ;  	
  		
  		f[free] = c ;
  		g[free] = pos ;
  		d[free] = free - 1 ;
  		pos = free ;
  		free ++ ;  
  		
  		c = l.curChar() ;
  	}
  }
  
  void makeConc(char[] f, int[] g, int[] d)
  // Même genre pour les CONJ
  {
  	makePstar(f, g, d) ;
    int pos = free - 1 ;

  	char c = l.curChar() ;

  	while (c == '(' || constant(c))
  	{  	
  		makePstar(f, g, d) ;  
  		
  		f[free] = '.' ;
  		g[free] = pos ;
  		d[free] = free - 1 ;
  		pos = free ;
  		free ++ ;  
  		
  		c = l.curChar() ;			
  	}
  }
  
    
  void makePstar(char[] f, int[] g, int[] d)
  // Même genre pour les CONJ
  {
  	makePcompl(f, g, d) ;
    int pos = free - 1 ;

  	char c = l.curChar() ;

  	while (c == '*')
  	{  	
  		//makePstar(f, g, d) ;  
  		
  		f[free] = '*' ;
  		g[free] = pos ;
  		pos = free ;
  		free ++ ;  
  		
  		l.move() ;
  		c = l.curChar() ;			
  	}
  }
  
  void makePcompl(char[] f, int[] g, int[] d)
  // Même genre pour les CONJ
  {
  	char c = l.curChar() ;
  	
  	if (c == '!')
  	{
  		l.move() ;
  		makePletr(f, g, d) ;	
  		f[free] = '!' ;
  		g[free] = free - 1 ;
  		free ++ ;  
  	}
  	else
  	  makePletr(f, g, d) ;	
  }
  
  
  
  void makePletr(char[] f, int[] g, int[] d)
  // Même genre pour les PNEG
  {
  	char c = l.curChar() ;

  	if (c == '(')
  	{
  		l.move() ;
      makeUnion(f, g, d) ;
  		int pos = free - 1 ;		
  		c = l.curChar() ;
  		
  		if (c == '\\' || c == '&' || c == '^')
  		{
  			l.move() ;
  			makeUnion(f, g, d) ;
  			
  			  		
  		  f[free] = c ;
  		  g[free] = pos ;
  		  d[free] = free - 1 ;
  		  pos = free ;
  		  free ++ ;    		  
  		}
  		
  		l.move() ;	
  		return ;
  	}
  	
  	if (constant(c))
  	{  		
  		f[free] = (c) ;
  		// g[free] = 0 ;
  		// d[free] = 0 ; // inutile
  		free ++ ;  
  		
  		l.move() ;
  		return ;
  	}
  	
  	System.out.println("You may not arrive here.") ;
  }

  
  
  public static void main(String[] arg) throws Exception
  {
  	Term t = toTerm(arg[0]) ;
  	t.print() ;

  }
}







