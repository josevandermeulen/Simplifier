package syntax;

class BooleanFormulaWriter implements TermWriter{
	
	public 	String toString(Term term) 
	{
	   return disjToTerm(term.f.length - 1, term) ;
  }
  
  static String disjToTerm(int i, Term term)
  {
  	if (term.f[i] == '+')
  		return disjToTerm(term.g[i], term) + " + "
  	       + conjToTerm(term.d[i], term) ;
  	       
  	return conjToTerm(i, term)  ;    
  }
  
  static String conjToTerm(int i, Term term)
  {
  	if (term.f[i] == '+')
  		return "(" + disjToTerm(i, term) + ")" ;
  	
    if (term.f[i] == '.')
  		return conjToTerm(term.g[i], term)
  	       + pnegToTerm(term.d[i], term) ;
  	       
    return 	pnegToTerm(i, term) ;       
  }
    
  static String pnegToTerm(int i, Term term)
  {
  	if (term.f[i] == '+')
  		return "(" + disjToTerm(i, term) + ")" ;
  	
    if (term.f[i] == '.')
  		return "(" + conjToTerm(i, term) + ")" ;
  	        	
    if (term.f[i] == '!')
  		return "!" + pnegToTerm(term.g[i], term) ;
  	
  	return "" + term.f[i] ; 
  }
  
}