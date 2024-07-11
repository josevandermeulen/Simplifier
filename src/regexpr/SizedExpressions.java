package regexpr ;
import util.* ;

public class SizedExpressions extends Expressions{
	
	/* 
	
	expressions + equations 
	
	*/
	
	int[] tree ; // Structure UnionFind des expressions égales
	
	long[] sizeExprs ; // size of expressions
	
	boolean[] hasMDFA ;
		
	// Liste de paires d'identifiants d'expressions à unifier
	int iBuf = 0 ;


		
	public SizedExpressions(final int memorySize, int nbrLetters)
	{
		super(memorySize, nbrLetters) ;
		reinit() ;
	}
	
	public void reinit()
	{
		super.reinit() ;
		
		System.gc() ;		
		tree = new int[memorySize] ;

		
		{
			int i = 0 ;
			while (i != tree.length)
			{
				tree[i] = - 1 ;				
				i ++ ;				
			}
		}
		
		sizeExprs = new long[memorySize] ;
		
		hasMDFA   = new boolean[tree.length] ;	
	}
	
  public void setHasMDFA()
	{
		if (hasMDFA == null)
			hasMDFA = new boolean[memorySize] ;
	}	
	
	public boolean[] getHasMDFA()
	{
		setHasMDFA() ;
		return hasMDFA ;
	}
	
	
	public long size(int iExpr)
	{
		if (sizeExprs[iExpr] != 0)
			return sizeExprs[iExpr] ;
			
		switch (type(iExpr))
		{
		  case ZERO : 
		  case ONE : break ;
		  case LETTER : sizeExprs[iExpr] = 1 ;
		  	break ;
		  case UNION :
		  	{
		  		int[] tabE = tabE(iExpr) ;
		  		int i = 0 ;
		  		long length = tabE.length - 1 ;
		  		while (i != tabE.length)
		  		{
		  			length += size(tabE[i]) ;
		  			i ++ ;
		  		}
		  		sizeExprs[iExpr] = length ;
					break ;
		  	}
			case CONCAT :
				{
					int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					long length = size(iP) + size(iS) + 1 ;
					
					sizeExprs[iExpr] = length ;
					break ;
				}
		  case BOX :
		  case STAR :
			  {
					int iSub = tabNexpr[iExpr][0] ;	
					sizeExprs[iExpr] = size(iSub) + 1 ;
					break ;
				}		  
			case NOT :
			  {
					int iSub = tabNexpr[iExpr][0] ;	
					sizeExprs[iExpr] = size(iSub) + BEAUCOUP ;
					break ;
				}
			case DELTA :
			case DIFF :
			case INTER :
				{
					int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					sizeExprs[iExpr] =  size(iP) + size(iS) + BEAUCOUP ;
					break ;
		    }  
		  default : throw new Error("unkown type " + iExpr + " [size]") ;
		}
		
		return sizeExprs[iExpr] ;
	}
	
	public int bestExpr(int iExpr)
	{
		int best = iExpr ;
		while (tree[best] >= 0)
		{

			best = tree[best] ;
		}

		while (iExpr != best)
		{
			int sExpr = tree[iExpr] ;
			tree[iExpr] = best ;
			iExpr = sExpr ;
		}
		
		return best ;
	}
	
	public int[] unionFind(int iExpr1, int iExpr2)
	{	
		iExpr1 = bestExpr(iExpr1) ;		
		iExpr2 = bestExpr(iExpr2) ;
	
		//if (tree[iExpr1] > tree[iExpr2])
		if (size(iExpr1) > size(iExpr2))
		{
			int t = iExpr1 ; iExpr1 = iExpr2 ; iExpr2 = t ;
		}

		tree[iExpr1] += tree[iExpr2] ;	
		tree[iExpr2] = iExpr1 ;	
		
		return new int[]{iExpr1, iExpr2} ;
	}
	
	
	
	
	
	
	void salomaaRuleOld(int iExpr, int[] tabD)  throws GCException
	{
		
		
		int countEq = 0 ;
		{
			int x = 1 ;
			while (x != tabD.length)
			{
				if (tabD[x] == iExpr)
					countEq ++ ;
				x ++ ;
			}			
		}
		
		if (countEq == 0)
			return ;
		
		//System.out.println("salomaaRule " + iExpr + " " + countEq) ;
		//System.out.println("salomaaRule " + toString(iExpr) + " " + countEq) ;
	  //System.out.println("salomaaRule a " + toString(tabD[1])) ;
		//System.out.println("salomaaRule b " + toString(tabD[2])) ;

	
		int nbrNeq = (tabD[0] == one() ? 1 : 0) + tabD.length - countEq - 1 ;
		int[] tabEq = new int[countEq] ;
		int[] tabNeq = new int[nbrNeq] ;
		{
			
			if (tabD[0] == one())
			{	
				tabNeq[0] = one() ;
			}
			
			int x = 1 ;
			int i = (tabD[0] == one() ? 1 : 0) ;
			int j = 0 ;
			while (x != tabD.length)
			{
				int iLetter = iLetter((char)('a' + x - 1)) ;
				if (tabD[x] == iExpr)
				{
					tabEq[j ++] =  iLetter ;
				}
				else
					tabNeq[i ++] = concat(iLetter, tabD[x]) ;
				x ++ ;
			}			
		}
		int iA = union(tabEq) ;
		int iB = union(tabNeq) ;
		int iExprN = concat(star(iA), iB) ;
		
		
		unify(iExpr, iExprN) ;
	}
	
	
	void salomaaRule(int iExpr, int[] tabD)  throws GCException
	{
		
		boolean[] dejaVu = new boolean[tabD.length] ;

		int iA = zero() ;
		{
			int x = 1 ;
			while (x != tabD.length)
			{
				if (tabD[x] == iExpr)
				{
					iA = union(iA, iLetter((char)('a' + x - 1))) ;
					dejaVu[x] = true ;
				}
				x ++ ;
			}			
		}

	  int iB = tabD[0] ;
	  {
	  	int x = 1 ;
	  	while (x != tabD.length)
	  	{
	  		if (! dejaVu[x])
	  		{
	  			int iC = tabD[x] ;
	  			int iL = iLetter((char)('a' + x - 1)) ;
	  			int y = x + 1 ;
	  			while (y != tabD.length)
	  			{
	  				if (! dejaVu[y])
	  				  if (tabD[y] == iC)
	  				  {
	  					  iL = union(iL, iLetter((char)('a' + y - 1))) ;
	  					  dejaVu[y] = true ;
	  				  }
	  				y ++ ;
	  			}
	  			iB = union(iB, concat(iL, iC)) ;
	  		}
	  		x ++ ;	  			
	  	}
	  }
	  
	  int iExprP = concat(star(iA), iB) ;
	  //System.out.println("iExpr = " + toString(iExpr)) ;
	  //System.out.println("iExpr' = " + toString(concat(star(iA), iB))) ;
		
	  //if (size(iExprP) < size(iExpr))
	  {
	     
	  	
	  	try{unify(iExpr, iExprP) ;	  
	  	}
	  	catch(Error e)
	  	{
	  		System.out.println("iExpr = " + toString(iExpr)) ;
	      System.out.println("iExpr' = " + toString(iExprP)) ;
	  	}
	  }
	}
	


	
	
	
	public void unify(int iExpr1, int iExpr2)  throws GCException
	{		
		if (bestExpr(iExpr1) != bestExpr(iExpr2))
		{
			//unionFind(iExpr1, iExpr2) ;	
			int[] twoExprs = unionFind(iExpr1, iExpr2) ;	
			
			iExpr1 = twoExprs[0] ;
			iExpr2 = twoExprs[1] ;
			
			int[] tabD1 = exprToTabD(iExpr1) ;
			
			if (tabD1 == null)
			{
				int[] tabD2 = exprToTabD(iExpr2) ;
				if (tabD2 != null)
					 addEquation(iExpr1, tabD2) ;
			}
			
		}
	}
	

	
}