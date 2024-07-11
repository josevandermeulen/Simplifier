package syntax;
import regexpr.* ;

public class Term{
	
	static int nbrLetters ;
	static char[] usedLetters ;
	static char[] actualLetters ;
	static char[] convertLetters ;
	
	
	char[] f ;
	int[] g ;
	int[] d ;
	
	public Term(char[] f, int[] g, int[] d)
	{
		
		
		this.f = f ;
		this.g = g ;
		this.d = d ;		
	}	
	
	public int size()
	{
		return f.length - 1 ;
	}
	
	public static int nbrLetters(Term[] t)
	{
		char[] usedLetters = new char[128] ;
		int nbrLetters = 0 ;
		int i = 0 ;
		while (i != t.length)
		{
			nbrLetters = t[i].countLetters(usedLetters, nbrLetters) ;
			i ++ ;
		}	
		
		return nbrLetters ;
	}	
	
	public static int nbrLetters(Term	t)
	{		
		return nbrLetters(new Term[]{t}) ;
	}
	
	public static void computeActualLetters(Term[] t, char maxLetter)
	{
		usedLetters = new char[128] ;
		nbrLetters = 0 ;
		int i = 0 ;
		while (i != t.length)
		{
			nbrLetters = t[i].countLetters(usedLetters, nbrLetters) ;
			i ++ ;
		}		
		
		compactListOfLetters(maxLetter) ;
	}
	
  public static void computeActualLetters(Term t, char maxLetter)
  {
  	computeActualLetters(new Term[]{t}, maxLetter) ;
  }

	public static void computeActualLetters(Term t1, Term t2, char maxLetter)
  {
  	computeActualLetters(new Term[]{t1, t2}, maxLetter) ;
  }
	
	int countLetters(char[] usedLetters, int nbr)
	{
		int i = 0 ;
		while (i != f.length)
		{
			if ('a' <= f[i] && f[i] <= 'z')
				nbr = checkLetter(f[i], usedLetters, nbr) ;
			i ++ ;
		}
		return nbr ;
	}
		
	public void print()
	{
		int i = 0 ;
		while (i != f.length)
		{
			System.out.println("i = " + i + " f = " + f[i] 
				+ " g = " + g[i] + " d = " + d[i]) ;
			i ++ ;
		}
	}
	
	public static void printActualLetters()
	{
		char i = 'a' ;
		while (i != 'z' + 1)
		{
			System.out.println("act[" + i + "] = " + actualLetters[i]) ;
			i ++ ;
		}
	}
	
	
		
	public static void compactListOfLetters(char maxLetter)
	{
	  char[] letterList = new char[26] ;
		int posFirstGreater = - 1;

		{int i = 0 ;
		char c = 'a' ;
		while (c != 'z' + 1)
		{
			if (usedLetters[c] != 0)
			{
				if (c > maxLetter && posFirstGreater == - 1)
					posFirstGreater = i ;
				letterList[i ++] = c ;
			}
			c ++ ;
		}}
		
		if (posFirstGreater == - 1)
			posFirstGreater = nbrLetters() ;
		
		actualLetters = new char[128] ;
		convertLetters = new char[128] ;
		
		{char c = 'a' ;
		while (c != 'z' + 1)
		{
			actualLetters[c] = c ;
			convertLetters[c] = c ;
			c ++ ;
		}}
		
		
		{
			int i = 0 ; 
			int j = posFirstGreater ;
			char c = 'a' ; 
		  while (c != 'a' + nbrLetters())
		  {
			  if (i < posFirstGreater && c == letterList[i])
			  {  
			  	//actualLetters[c] = letterList[i] ;
			    //convertLetters[letterList[i]] = c ;
			    i ++ ;
			  }
			  else if (j < nbrLetters())
			  {  
			  	actualLetters[c] = letterList[j] ;
			    convertLetters[letterList[j]] = c ;
			    j ++ ;
			  }	
			 
			  c ++ ;
		  }
		}
		
	}
	
	static char convert(char c)
	{
		return convertLetters[c] ;
	}
	
	static int checkLetter(char c, char[] usedLetters, int nbr)
	{
		if (usedLetters[c] == 0)
    {
    	usedLetters[c] = c ;
    	nbr ++ ;
    }
    return nbr ;
	}
	
	static public int nbrLetters() 
	{
		return nbrLetters ;
	}
	
	static public char[] actualLetters() 
	{
		return actualLetters ;
	}
	
	
	int nbrFlatConcat(int i)
	{
		if (f[i] != '.')
			return 1 ;
		else
			return nbrFlatConcat(g[i]) + nbrFlatConcat(d[i]) ;
	}
	
	int toNonConcatList(int[] tabE, int j, IExpressions all, int i) throws GCException
	{
		if (f[i] != '.')
		{
			tabE[j] = toExpression(all, i) ;
			return j + 1 ;
		}
		else
		{
			j =  toNonConcatList(tabE, j, all, g[i]) ;
			return toNonConcatList(tabE, j, all, d[i]) ;
		}
	}
	
		
	int[] toNonConcatList(IExpressions all, int i) throws GCException
	{
		int[] tabE = new int[nbrFlatConcat(i)] ;
		toNonConcatList(tabE, 0, all, i) ;
		return tabE ;
	}
	
		
	int nbrFlatUnion(int i)
	{
		if (f[i] != '+')
			return 1 ;
		else
			return nbrFlatUnion(g[i]) + nbrFlatUnion(d[i]) ;
	}
	
	int toNonUnionList(int[] tabE, int j, IExpressions all, int i) throws GCException
	{
		if (f[i] != '+')
		{
			tabE[j] = toExpression(all, i) ;
			return j + 1 ;
		}
		else
		{
			j =  toNonUnionList(tabE, j, all, g[i]) ;
			return toNonUnionList(tabE, j, all, d[i]) ;
		}
	}
	
		
	int[] toNonUnionList(IExpressions all, int i) throws GCException
	{
		int[] tabE = new int[nbrFlatUnion(i)] ;
		toNonUnionList(tabE, 0, all, i) ;
	  return tabE ;
	}
		
	int toExpression(IExpressions all, int i)  throws GCException
	{
		if (f[i] == '0')
			return all.zero() ;
		
		if (f[i] == '1')
			return all.one() ;
		
		if ('a' <= f[i] && f[i] <= 'z')
			return all.iLetter(convert(f[i])) ;
		
		if (f[i] == '*')
			return all.star(toExpression(all, g[i])) ;
			
		if (f[i] == '!')
			return all.not(toExpression(all, g[i])) ;
				
		if (f[i] == '.')
		{
			int[] tabE = toNonConcatList(all, i) ;
			return all.concat(tabE) ;			
		}
						
		if (f[i] == '+')
		{
			int[] tabE = toNonUnionList(all, i) ;
			return (all.union(tabE)) ;			
		}
		
		if (f[i] == '\\')
			return all.diff(toExpression(all, g[i]), toExpression(all, d[i])) ;
			
		if (f[i] == '&')
		{
			int iA = toExpression(all, g[i]) ;
			int iB = toExpression(all, d[i]) ;
			return all.inter(iA, iB) ;			
		}
					
		if (f[i] == '^')
		{
			int iA = toExpression(all, g[i]) ;
			int iB = toExpression(all, d[i]) ;
			return all.delta(iA, iB) ;			
		}
		
		throw new Error("Error in toExpression : f[" + i + "] = " + f[i]) ;
	}	

	public int toExpression(IExpressions all) throws GCException
	{
		try {
		  return toExpression(all, f.length - 1) ;
		}
		catch(GCException e)
		{
			System.out.println("Unexpected call to GC in toExpression") ;
			throw e ;
		}
	}
	
	
	
	
}

