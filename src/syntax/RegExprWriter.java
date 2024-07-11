package syntax;

public class RegExprWriter implements TermWriter{
	
	public String toString(Term term) 
	{
	   return unionToTerm(term.f.length - 1, term) ;
  }
  
  static String unionToTerm(int i, Term term)
  {
  	if (term.f[i] == '+')
  		return unionToTerm(term.g[i], term) + " + "
  	       + concToTerm(term.d[i], term) ;
  	       
  	return concToTerm(i, term)  ;    
  }
  
  static String concToTerm(int i, Term term)
  {
  	if (term.f[i] == '+')
  		return "(" + unionToTerm(i, term) + ")" ;
  	
    if (term.f[i] == '.')
  		return concToTerm(term.g[i], term)
  	       + pstarToTerm(term.d[i], term) ;
  	       
    return 	pstarToTerm(i, term) ;       
  }
    
  static String pstarToTerm(int i, Term term)
  {
  	if (term.f[i] == '+')
  		return "(" + unionToTerm(i, term) + ")" ;
  	
    if (term.f[i] == '.')
  		return "(" + concToTerm(i, term) + ")" ;
  	        	
    if (term.f[i] == '*')
  		return pstarToTerm(term.g[i], term) + "*";
  	
  	if (term.f[i] == '\\')
  		return "(" + unionToTerm(term.g[i], term)
  	             + " \\ " + 
  	            unionToTerm(term.d[i], term) + ")" ;
  	     
   	
  	if (term.f[i] == '&')
  		return "(" + unionToTerm(term.g[i], term)
  	             + " \\ " + 
  	            unionToTerm(term.d[i], term) + ")" ;
  	     
   	
  	if (term.f[i] == '^')
  		return "(" + unionToTerm(term.g[i], term)
  	             + " \\ " + 
  	            unionToTerm(term.d[i], term) + ")" ;
  	     
  	
  	return "" + term.f[i] ; 
  }
  
}