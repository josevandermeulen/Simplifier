package syntax;
class AxiomReader {
	
	//abstract Axiom toAxiom(String axiom) ;
	
	TermReader tr ;
	Lexer l ;
	
	AxiomReader(TermReader tr)
	{
		this.tr = tr ;
		this.l = tr.l ;
	}

			
	Axiom makeAxiom() throws ReaderException
	{
		Term head = tr.makeTerm() ;
		if (head == null)
			return null ;
		
	  char c = l.curChar() ;
		
		if (c != '=')
		{
			throw new ReaderException
			("le symbole = était attendu"
			 + " au lieu de " + (c == 0 ? "rien" : c), l.pos()) ;
		}
		
		l.move() ;
		c = l.curChar() ;
		
		boolean halfOrFull = 
		  (c == ':' ?
		  	Axiom.HALF : Axiom.FULL) ;
		  
		if (c == ':')
			l.move() ;
		
		
		Term tail = tr.makeTerm() ;
	  		if (tail == null)
			return null ;
		
		
	  c = l.curChar() ;
	  
		if (c != ';')
		{
			throw new ReaderException
			("le symbole ; était attendu"
		   	+ " au lieu de " + (c == 0 ? "rien" : c), l.pos()) ;
		}
	
		return new Axiom(head, tail, halfOrFull) ;
	}

}
