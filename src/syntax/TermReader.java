package syntax;

abstract class TermReader{
	
	Lexer l ;
	
	TermReader()
	{
	}
 	
	TermReader(Lexer l)
	{
		putLexer(l) ;
	}
  
	void putLexer(Lexer l)
	{
		this.l = l ;
	}
	
	abstract Term makeTerm() throws ReaderException ;
		
}

