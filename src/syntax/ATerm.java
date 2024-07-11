package syntax;
import regexpr.* ;

public class ATerm{
	
	// We simplify a term according to the alpha abstractions
	// of Kahrs and Dunciman
	
	Term t ;
	int tl ;
	Term ta ;
	
  int[] alpha1  ;
	int[] alpha  ;
	int[] alphas  ;
	
	public Term getATerm()
	{
		return ta ;
	}
	
	public ATerm(Term t)
	{
		
		
		this.t = t ;
		tl = t.f.length ;
		
		alpha1 = new int[tl] ;
	  alpha = new int[tl] ;
	  alphas = new int[tl] ;
	  
		computeAlphas() ;
		ta = makeAlphaTerm() ;
	}	
	
	static int[] bitOfLetter = new int[27] ;
	// This array maps 1 and every letter to an integer
	// 000...010...0 where guess the position of 1
	static{
		int i = 0 ;
		int x = 1 ;
		while (i != bitOfLetter.length)
		{
			bitOfLetter[i] = x ;
			x *= 2 ;
			i ++ ;
		}		
	}
	
	static int bitOfChar(char c)
	// Idem but c is a char : '1', 'a', ..., 'z'
	{
		if (c == '1')
			return bitOfLetter[0] ;
		else
			return bitOfLetter[c - 'a' + 1] ;
	}
	
  static int best(int alpha1, int alpha2)
  {
  	if (nbrOnes(alpha1) > nbrOnes(alpha2))
  		return alpha1 ;
  	else
  		return alpha2 ;
  }
	
	boolean has1(int i)
	{
		return alpha1[i] % 2 == 1 ;
	}

	void computeAlphas()
	{
		computeAlphas(tl - 1) ;
	}
	
	void computeAlphas(int i)
	{
		if ('a' <= t.f[i] && t.f[i] <= 'z')
		{
			alpha1[i] = alpha[i] = bitOfChar(t.f[i]) ;
			return ;
		}
		
		switch (t.f[i])
		{
		  case '0' : return  ;
		  	
		  case '1' : 
		  {
		  	alpha1[i] = 1 ;
		  	//alphas[i] = 1 ;
			  return ;
		  }
			
		  case '*' : 
		  {
		  	computeAlphas(t.g[i]) ;
		  	alpha1[i] = alpha1[t.g[i]] | 1 ;
		  	alpha[i]  = alpha[t.g[i]] ;
		  	alphas[i] = (alpha1[i] / 2) * 2 ;
			  return ;
		  }
		  
			
		  case '.' : 
		  {
			  computeAlphas(t.g[i]) ;
			  computeAlphas(t.d[i]) ;
			  alpha[i] = alpha[t.g[i]] | alpha[t.d[i]] ;
			  
			  if (has1(t.g[i]) && has1(t.d[i]))
			  {
			  	alpha1[i] = alpha1[t.g[i]] | alpha1[t.d[i]] ;
			  	alphas[i] = best(alphas[t.g[i]], alphas[t.d[i]]) ;
			  }
			  else if (has1(t.g[i]))
			  {
			  	alpha1[i] = alpha1[t.d[i]] ;
			  	alphas[i] = alphas[t.d[i]] ;
			  }
			  else if (has1(t.d[i]))
			  {
			  	alpha1[i] = alpha1[t.g[i]] ;
			  	alphas[i] = alphas[t.g[i]] ;
			  }
			  
			  return ;			  
		  }
		  		 			
		  case '+' : 
		  {
			  computeAlphas(t.g[i]) ;
			  computeAlphas(t.d[i]) ;
			  
			  alpha[i]  = alpha[t.g[i]] | alpha[t.d[i]] ;
			  alpha1[i] = alpha1[t.g[i]] | alpha1[t.d[i]] ;
			  alphas[i] = best(alphas[t.g[i]], alphas[t.d[i]]) ;
			               
			  return ;			  
		  } 
		  
		  case '!' :	
		  	computeAlphas(t.g[i]) ;
		  	alpha[i]  = alpha[t.g[i]]  ;	 
		  	alpha1[i] = ~ alpha1[t.g[i]] ;
		  	return ;
		  	
		  case '\\' :
		  case '^' :
		  case '&' :
		  {
			  computeAlphas(t.g[i]) ;
			  computeAlphas(t.d[i]) ;
			  
			  alpha[i] = alpha[t.g[i]] | alpha[t.d[i]] ;	
			  
			  switch (t.f[i])
			  {
			  	case '\\' :
			  		alpha1[i] = alpha1[t.g[i]] & (~ alpha1[t.d[i]]);	break ;
		      case '^' :
		      	alpha1[i] = alpha1[t.g[i]] ^ alpha1[t.d[i]] ;	break ;
		      case '&' :
		      	alpha1[i] = alpha1[t.g[i]] & alpha1[t.d[i]] ;	break ;
			  }
			  
			  return ;			  
		  } 
			
		  
		  default :
		   
		  throw new Error("Error in computeAlphas : f[" + i + "] = " + t.f[i]) ;
		}
	}
	
	
		
	static int nbrOnes(int x)
	// nbr of 1 in x = 00*10*1...0*10*
	{
		int nbr = 0 ;
		while (x != 0)
		{
			if (x % 2 == 1)
				nbr ++ ;
			
			x /= 2 ;
		}
		
		return nbr ;
	}
	
	static int sumLength(int alpha1)
	{
		int nbr = nbrOnes(alpha1) ;
		if (nbr == 0)
			return - 1 ;
		else
		 return nbr * 2 - 1 ;
	}
	
	boolean subset(int alphax, int alphay)
	// alphax subset alphay ?
	{
		return (alphax & alphay) == alphax ;
	}
	
	int computeAlphaSizes()
	{
		return computeAlphaSizes(tl - 1) + 1 ;
	}
	
	int computeAlphaSizes(int i)
	// Size of a simplified version of subterm(t, i)
	{
		
		if ('a' <= t.f[i] && t.f[i] <= 'z')
		{
			return 1 ;
		}
		
		switch (t.f[i])
		{
		  case '0' : 		  	
		  case '1' : 
		  {
			  return 1 ;
		  }
		
		  case '*' :

			  if (subset(alpha[i], alpha1[i]))
				{
					int x = sumLength(alpha1[i] / 2) + 1 ;
					if (x == 0)
						x ++ ;
					return x ;
				}
				
				int is = t.g[i] ;
			  if (t.f[is] == '+')
			  {
			    int ix = t.g[is] ;
			    int iy = t.d[is] ;
			    	
			    if (subset(alpha[ix], alpha1[ix] | alpha1[iy]))
			    	return sumLength(alpha1[ix] / 2) + computeAlphaSizes(iy) + 2 ;		
			    
	  			if (subset(alpha[iy], alpha1[iy] | alpha1[ix]))
			    	return sumLength(alpha1[iy] / 2) + computeAlphaSizes(ix) + 2 ;			    				    
			  }
			  
			  return computeAlphaSizes(is) + 1 ;
			  
			case '.' :			
			{	
				int ig = t.g[i] ;
				int id = t.d[i] ;
				
				if (has1(ig) && subset(alpha[ig], alphas[id]) && alpha[id] == alphas[id])
					return computeAlphaSizes(id) ;
				
				if (has1(id) && subset(alpha[id], alphas[ig])  && alpha[ig] == alphas[ig])
					return computeAlphaSizes(ig) ;
					   
					
			  return computeAlphaSizes(ig) + computeAlphaSizes(id) + 1 ;
			}
			  		  
			case '+' :			
			{	
				int ig = t.g[i] ;
				int id = t.d[i] ;
				
				if (subset(alpha[id], alphas[ig]) && alphas[ig] != 0)
				{
					return computeAlphaSizes(ig) ;
				}
					
				if (subset(alpha[ig], alphas[id]) && alphas[id] != 0)
				{
					return computeAlphaSizes(id) ;
				}
					   					
			  return computeAlphaSizes(ig) + computeAlphaSizes(id) + 1 ;
			}
			
			case '!' :
				return computeAlphaSizes(t.g[i]) + 1 ;
				
		  case '\\' :
		  case '^' :
		  case '&' :
		  {
		  	int ig = t.g[i] ;
				int id = t.d[i] ;
		  	return computeAlphaSizes(ig) + computeAlphaSizes(id) + 1;
			}
			
			default :
		   
		  throw new Error("Error in computeAlphaSizes : f[" + i + "] = " + t.f[i]) ;		  
		}
	}
		
	int makeAlphaTermStar(int ia, int alpha, int[] g, char[] f, int[] d)
	{
		
		int x = alpha / 2 ;
	  	
	  	//special case 0* = 1* = 1
	  if (x == 0)
	  {
	  	g[ia] = d[ia] = 0 ;
		  f[ia] = '1' ;
			 return ia ;
	  }
	  	
		
		ia = makeAlphaTermUnion(ia, alpha, g, f, d) ;
		
		ia ++ ;
	  g[ia] = ia - 1 ;
	  d[ia] = 0 ;
		f[ia] = '*' ;
		  
	  return ia ;
	}
	
	int makeAlphaTermUnion(int ia, int alpha1, int[] g, char[] f, int[] d)
	  //
	  // We add at position ia the expression
	  // (x_1 + ... + x_n)*
	  // where the x_i correspond to the bits equal to 1 in alpha
	  // (except the rightmost one)
	  {
	  	
	  	int x = alpha1 / 2 ;
	  	
	  	if (x == 0)
	    {
			 return 0 ;
	    }
	  	
	  	char xi = 'a' ;
	  	while (x % 2 == 0)
	  	{
	  		x /= 2 ;
	  		xi ++ ;
	  	}
	  	
	  	g[ia] = 0 ;
	  	d[ia] = 0 ;
		  f[ia] = xi ;
		  x /= 2 ;
	    xi ++ ;
	  		  	
	  	while (x != 0)
	  	{
	  		if (x % 2 == 1)
	  		{
	  			ia ++ ;
	  			g[ia] = 0 ;
	  			d[ia] = 0 ;
		  	  f[ia] = xi ;
		  	  ia ++ ;
		  	  g[ia] = ia - 2 ;
	  			d[ia] = ia - 1 ;
		  	  f[ia] = '+' ;
	  		}  		
	  		x /= 2 ;
	  		xi ++ ;  		
	  	}  	
	  	
	  	return ia ;
	  }
	
		Term makeAlphaTerm()
		{
			int lat = computeAlphaSizes() ;
			System.out.println("lat = " + lat) ;
			int[] g = new int[lat] ;
			char[] f = new char[lat] ;
			int[] d = new int[lat] ;
			
			makeAlphaTerm(tl - 1, 1, g, f, d) ;
			
			return new Term(f, g, d) ;
		}
		
		int makeAlphaTerm(int i, int ia, int[] g, char[] f, int[] d)
		// We build at(i) in g, f, d, from ia
		// and we return the position of the root of at(i)
		{
			
			if ('a' <= t.f[i] && t.f[i] <= 'z')
		  {
		  	g[ia] = d[ia] = 0 ;
		  	f[ia] = t.f[i] ;
			  return ia ;
		  }
		
		  switch (t.f[i])
		  {
		    case '0' : 
		    case '1' :
		    	g[ia] = d[ia] = 0 ;
		  	  f[ia] = t.f[i] ;
			    return ia ;	
		  
		    case '*' :

			    if (subset(alpha[i], alpha1[i]))
			    {		    	
			    	return makeAlphaTermStar(ia, alpha1[i], g, f, d) ;
			    }
			    
			    int is = t.g[i] ;
			    if (t.f[is] == '+')
			    {
			    	int ix = t.g[is] ;
			    	int iy = t.d[is] ;
			    	
			    	
			    	if (subset(alpha[ix], alpha1[ix] | alpha1[iy]))
			    	{
			    		int iyN = makeAlphaTerm(iy, ia, g, f, d) ;
			    		int ixN = makeAlphaTermUnion(iyN + 1, alpha1[ix], g, f, d) ;
			    		if (ixN == 0)
			    		{
			    			ia = iyN + 1 ;
			    	  	g[ia] = iyN ;
			    		  f[ia] = '*' ;
			    		  d[ia] = 0 ;		
			    		  return ia ;
			    		}

			    		ia = ixN + 1 ;
			    		g[ia] = ixN ;
			    		f[ia] = '+' ;
			    		d[ia] = iyN ;
			    		
			    		ia ++ ;
			    		g[ia] = ia - 1 ;
			    		f[ia] = '*' ;
			    		d[ia] = 0 ;
			    		
			    		return ia ;
			    	}
			    	
			    	if (subset(alpha[iy], alpha1[iy] | alpha1[ix]))
			    	{
			    		int ixN = makeAlphaTerm(ix, ia, g, f, d) ;
			    		int iyN = makeAlphaTermUnion(ixN + 1, alpha1[iy], g, f, d) ;
			    		
			    		if (iyN == 0)
			    		{
			    			ia = ixN + 1 ;
			    	  	g[ia] = ixN ;
			    		  f[ia] = '*' ;
			    		  d[ia] = 0 ;		
			    		  
			    		  return ia ;
			    		}
			    		
			    		
			    		ia = iyN + 1 ;
			    		g[ia] = ixN ;
			    		f[ia] = '+' ;
			    		d[ia] = iyN ;
			    		
			    		ia ++ ;
			    		g[ia] = ia - 1 ;
			    		f[ia] = '*' ;
			    		d[ia] = 0 ;
			    		
			    		return ia ;
			    	}
			    	
			    }
			    
			    ia = makeAlphaTerm(t.g[i], ia, g, f, d) ;
			    ia ++ ;
			    g[ia] = ia - 1 ;
		  	  f[ia] = '*' ;
		  	  d[ia] = 0 ;
			    return ia ;	
						  
			  case '.' :			
			    {	
				    int ig = t.g[i] ;
				    int id = t.d[i] ;
					
					
				    if (has1(ig) && subset(alpha[ig], alphas[id]) && alpha[id] == alphas[id])
					   return makeAlphaTerm(id, ia, g, f, d) ;
					   
				    if (has1(id) && subset(alpha[id], alphas[ig]) && alpha[ig] == alphas[ig])
					   return makeAlphaTerm(ig, ia, g, f, d) ;
					   
					  int iag = makeAlphaTerm(ig, ia, g, f, d) ;
					  int iad = makeAlphaTerm(id, iag + 1, g, f, d) ;
					  ia = iad + 1 ;
					
			      g[ia] = iag ;
		  	    f[ia] = '.' ;
		  	    d[ia] = iad ;
			      return ia ;	
			  }
			  		  
			  case '+' :			
			  {	
				  int ig = t.g[i] ;
				  int id = t.d[i] ;
					 
					if (subset(alpha[id], alphas[ig]) && alphas[ig] != 0)
					   return makeAlphaTerm(ig, ia, g, f, d) ;
					
					
				  if (subset(alpha[ig], alphas[id]) && alphas[id] != 0)
					   return makeAlphaTerm(id, ia, g, f, d) ;
					   
					 
					
			     int iag = makeAlphaTerm(ig, ia, g, f, d) ;
					 int iad = makeAlphaTerm(id, iag + 1, g, f, d) ;
					 ia = iad + 1 ;
					
			     g[ia] = iag ;
		  	   f[ia] = '+' ;
		  	   d[ia] = iad ;
			     return ia ;	
			  }
			  
			  			
			case '!' :
			{
		  	int ig = t.g[i] ;
		  	
		  	int iag = makeAlphaTerm(ig, ia, g, f, d) ;
			  ia = iag + 1 ;
					
			  g[ia] = iag ;
		  	f[ia] = '!' ;
		  	d[ia] = 0 ;
			  return ia ;	
		  	
			}	
				
		  case '\\' :
		  case '^' :
		  case '&' :
		  {
		  	int ig = t.g[i] ;
				int id = t.d[i] ;
		  	
		  	int iag = makeAlphaTerm(ig, ia, g, f, d) ;
				int iad = makeAlphaTerm(id, iag + 1, g, f, d) ;
			  ia = iad + 1 ;
					
			  g[ia] = iag ;
		  	f[ia] = t.f[i] ;
		  	d[ia] = iad ;
			  return ia ;	
		  	
			}
			
			default :
		   
		  throw new Error("Error in makeAlphaTerm : f[" + i + "] = " + t.f[i]) ;		 
		}
	}
	
	public static void main(String[] arg) throws Exception
  {
  	Term t = RegExprReader.toTerm(arg[0]) ;
  	t.print() ;
  	Term ta = new ATerm(t).getATerm() ;
  	ta.print() ;

  }
	
}

