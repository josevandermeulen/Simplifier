package syntax;

public class NonGroundTerm{
	
	/*  les lettres x, y, z sont considérées
	   comme des variables :
	   
	   si term.f[i] = 'x', par exemple,
	   on modifie term en faisant :
	   term.d[i] = - 1 ; et 
	   term.g[i] = le numéro attribué à 'x';
	   
  */
  public String toString()
  {
  	return "" + term ;
  }
  
  static char[] allVars = new char[]{'x', 'y', 'z'} ;
  
  static boolean isVar(char c)
  {
  	int i = 0 ;
  	while (i != allVars.length)
  	{
  		if (allVars[i] == c)
  			return true ;
  		i ++ ;
  	} 	
  	return false ;
  }
  
  static int numVar(char c, char[] tVars)
  // Si c est dans tVars renvoyer l'indice correspondant.
  // Renvoyer tVars.length sinon
  {
  	int i = 0 ;
  	while (i != tVars.length && tVars[i] != c) i ++ ;
  	return i ;
  }
  
  static char[] addVar(char c, char[] tVars)
  // créer un nouveau tableau en ajoutant c à la fin de tVars
  {
  	char[] newTab = new char[tVars.length + 1] ;
  	
  	int i = 0 ;
  	while (i != tVars.length)
  	{
  		newTab[i] = tVars[i] ;
  		i ++ ;
  	}
  	newTab[i] = c ;
  	
  	return newTab ;
  }
   
	
	final Term term ;
	final char[] vars ; 
	// les variables utilisées, de gauche à droite
	String vars()
	{
		String s = "[" ;
		int i = 0 ;
		while (i != vars.length)
		{
			s += vars[i] + "/" + i + (i == vars.length - 1 ? "]" : ", ") ;
			i ++ ;
		}
		
		if (i == 0)
			s += "]" ;
		
		return s ;			
	}
	
	static char[] identifyVariables(Term term, char[] lVars) 
	{
		return identifyVariables(term.f.length - 1, term, lVars) ;
	}
	
	static char[] identifyVariables(int i, Term term, char[] lVars) 
	{		
		if (term.g[i] > 0)
		{
			lVars = identifyVariables(term.g[i], term, lVars) ;			
			if (term.d[i] > 0)
				lVars = identifyVariables(term.d[i], term, lVars) ;
			return lVars ;
		}
		
		char c = term.f[i] ;
		if (isVar(c))
		{
			term.d[i] = - 1 ;
			int numC  = numVar(c, lVars) ;
			term.g[i] = numC ;
			if (numC == lVars.length)
				lVars = addVar(c, lVars) ;
		}
		
		return lVars ;
	}
	
	public NonGroundTerm(Term term, char[] lVars)
	{
		this.term = term ;		
		vars = identifyVariables(term, lVars) ;		
	}
	
	public NonGroundTerm(Term term)
	{
		this(term, new char[]{}) ;		
	}
	
	
	
}