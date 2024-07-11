package regexpr;
import syntax.* ;

public class MakeDFA implements IMakeDFA{
	
	IExpressions exprs ;
	long[] dejaVu ;
  int[] toDerive ;
  boolean[] hasDFA ;
	long onStack = 0 ;
	long developped = 0 ;
  
  
	public MakeDFA(IExpressions exprs)
	{
		this.exprs = exprs ;		
	}
	
	

	
	
  int nbrDevelopped = 0 ;
  int nbrHasDFA = 0 ;
  public void computeAllDeriv(int iExpr0) throws GCException
	{
		
		dejaVu = exprs.getDejaVu() ;
		toDerive = exprs.getToDerive() ;		
		hasDFA = ((Expressions)exprs).getHasDFA() ;	
	  onStack = exprs.newMagicNumber() ;
	  developped = exprs.newMagicNumber() ;
	  	  
		int topE = 0 ;
		int topF = 1 ;
		toDerive[0] = iExpr0 ; 
	  dejaVu[iExpr0] = onStack ;	
	  
		while (topE != topF)
    {		    	
      int iExpr = toDerive[topE ++] ;
      
      if (hasDFA[iExpr])
      {
      	//System.out.println("hasDFA " + (++ nbrHasDFA)) ;
      	continue ;
      }
      
       if (dejaVu[exprs.bestExpr(iExpr)] == developped)
       {
      	//System.out.println("developped " + (++ nbrDevelopped)) ;
      	continue ;
      }
      
      int[] tabD = ((Expressions)exprs).computeTabDeriv(iExpr) ;
      		    		    	    
      int x = 1 ;
		  while (x != tabD.length)
	    {
	  	 int iExprx = tabD[x] ;
	  	 
	  	 if (dejaVu[iExprx] <  onStack)
			 {
			 	 	dejaVu[iExprx] = onStack ;
				 	toDerive[topF ++] = iExprx ;	
			 }      
			  x ++ ;
		  }   
		  dejaVu[exprs.bestExpr(iExpr)] = developped ;
	  } 	
	  
	  //System.out.println("topF == " + topF) ;
	  //((Background)exprs).bestifyEquations() ;
	}	
	
	public void computeAllDerivSpecial(int iExpr0, int i) throws GCException
	{
		
		hasDFA = ((Expressions)exprs).getHasDFA() ;		
		if (hasDFA[iExpr0])
			return ;
		
		
		dejaVu = exprs.getDejaVu() ;
		toDerive = exprs.getToDerive() ;		
	  onStack = exprs.newMagicNumber() ;
	  developped = exprs.newMagicNumber() ;
	  
	  int[] tabD0 = exprs.tabD(iExpr0) ;
	  if (tabD0 != null)
	    tabD0 = ((Expressions)exprs).computeTabDeriv(iExpr0) ;
	  
		int topE = 0 ;
		int topF = 1 ;
		toDerive[0] = iExpr0 ; 
	  dejaVu[iExpr0] = onStack ;	
	  
		while (topE != topF)
    {		    	
      int iExpr = toDerive[topE ++] ;
      
      //if (i == 0)
      //System.out.println(exprs.toString(iExpr) + " " + exprs.size(iExpr)) ;
      
      if (hasDFA[iExpr])
      {
      	continue ;
      }
      
       if (dejaVu[exprs.bestExpr(iExpr)] == developped)
       {
      	//System.out.println("developped " + (++ nbrDevelopped)) ;
      	continue ;
      }
      
      int[] tabD = ((Expressions)exprs).computeTabDeriv(iExpr) ;
      		    		    	    
      int x = 1 ;
		  while (x != tabD.length)
	    {
	  	 int iExprx = tabD[x] ;
	  	 
	  	 if (dejaVu[iExprx] <  onStack)
			 {
			 	 	dejaVu[iExprx] = onStack ;
				 	toDerive[topF ++] = iExprx ;	
			 }      
			  x ++ ;
		  }   
		  dejaVu[exprs.bestExpr(iExpr)] = developped ;
	  } 	
	  
	  if (i == 0)
	  System.out.println("topF == " + topF) ;
	  //((Background)exprs).bestifyEquations() ;
	}	
	
	public void computeBestEquations() throws GCException
	{		
		int i = 0 ;
		while (i != leftParts.length)
		{
			int iExpri = leftParts[i] ;
			//if (iExpri == exprs.bestExpr(iExpri))
			{	
				bestifyTabD(rightParts[i]) ;
			  exprs.addEquation(exprs.bestExpr(iExpri), rightParts[i]) ;
			}
			i ++ ;
		}	
	}
	
	public void bestifyTabD(int[] tabD)
	{		
		int x = 1 ;
		while (x != tabD.length)
		{
			tabD[x] = exprs.bestExpr(tabD[x]) ;
			x ++ ;
		}
	}
	
	public int computeBestEquations(int iExpr0) throws GCException
	{
		int nbrBests = countTheBests() ;
		computeBestEquations(iExpr0, nbrBests) ;
		return nbrBests ;
	}
	
	public void computeBestEquations(int iExpr0, int nbrBests) throws GCException
	{		
		long[] dejaVu = exprs.getDejaVu() ;
		long magicNumber = exprs.newMagicNumber() ;
		int[] toDerive = exprs.getToDerive() ;
		
		int top = 1 ;
		toDerive[0] = iExpr0 ;
		int nbrFoundBests = 0 ;

		while (top != 0 && nbrBests != nbrFoundBests)
		{
			int iExpr = toDerive[-- top] ;
			int bExpr = exprs.bestExpr(iExpr) ;
			nbrFoundBests ++ ;

			int[] tabD = exprs.exprToTabD(iExpr) ;
			{
				int x = 1 ;
				while (x != tabD.length)
				{
					int iExprx = tabD[x] ;
					int bExprx = exprs.bestExpr(iExprx) ;
					if (dejaVu[bExprx] != magicNumber)
					{
						dejaVu[bExprx] = magicNumber ;
						toDerive[top ++] = iExprx ;
					}
					x ++ ;
				}			
			}
			bestifyTabD(tabD) ;
			exprs.addEquation(bExpr, tabD) ;					
		}
	}
	
	int countTheBests()
	{
		long[] dejaVu = exprs.getDejaVu() ;
		long magicNumber = exprs.newMagicNumber() ;
		int count = 0 ;
		
		int i = 0 ;
		while (i != leftParts.length)
		{
			int iExpr = exprs.bestExpr(leftParts[i]) ;
			
			if (dejaVu[iExpr] != magicNumber)
			{
				count ++ ;
				dejaVu[iExpr] = magicNumber ;
			}
			i ++ ;
		}
		
		return count ;
	}
	
	int computeLeftPartsOfEquations(int iExpr0) throws GCException
	// Pré: a complete set of Equations exits for iExpr.
	// The left parts of these equations are put at the bottom of toDerive,
	// using a breadth-first order
	// The number of these equations is returned.
	{
		onStack = exprs.newMagicNumber() ;
		int topE = 0 ;
		int topF = 1 ;
		iExpr0 = exprs.bestExpr(iExpr0) ; 
		toDerive[0] = iExpr0 ; 
	  dejaVu[iExpr0] =  onStack ;	
	  hasDFA[iExpr0] = true ;
	  
		while (topE != topF)
    {		    	
      int iExpr = toDerive[topE ++] ;     
      int[] tabD = exprs.exprToTabD(iExpr) ;
      if (tabD == null)
      	System.out.println("problem ! ") ;
      		    		    	    
      int x = 1 ;
		  while (x != tabD.length)
	    {
	  	 int iExprx = tabD[x] ; //exprs.bestExpr(tabD[x]) ;
	  		
	  	 if (dejaVu[iExprx] !=  onStack)
			 {
			 	  hasDFA[iExprx] = true ;
				 	dejaVu[iExprx] =  onStack ;
				 	toDerive[topF ++] = iExprx ;	
			 }      
			  x ++ ;
		 }   
	  } 		  	  
	  return topF ;
	}
	
	int[] leftParts ;
	int[][] rightParts ;
	public void computeEquations(int iExpr) throws GCException
	{
		int nbrEquations = computeLeftPartsOfEquations(iExpr) ;
		leftParts = new int[nbrEquations] ;
		rightParts = new int[nbrEquations][] ;
		
		int i = 0 ;
		while (i != leftParts.length)
		{
			int iExpri = toDerive[i] ;
			leftParts[i] = iExpri ;
			rightParts[i] = exprs.exprToTabD(iExpri) ;
			i ++ ;
		}		
	}

	public int[] leftParts()
	{
		return leftParts ;
	}
		
	public int[][] rightParts()
	{
		return rightParts ;
	}
	
	
	int hashCode(int iExpr0)
	{
		int nl = exprs.nbrLetters() + 1 ;
		int hashCode = 1 ;
		onStack = exprs.newMagicNumber() ;
		int topE = 0 ;
		int topF = 1 ;
		iExpr0 = exprs.bestExpr(iExpr0) ; 
		toDerive[0] = iExpr0 ; 
	  dejaVu[iExpr0] =  onStack ;	
	  
		while (topE != topF)
    {		    	
      int iExpr = toDerive[topE ++] ;     
      int[] tabD = exprs.exprToTabD(iExpr) ;
      if (tabD == null)
      	System.out.println("problem ! ") ;
      hashCode = hashCode * nl + tabD[0] ;
      		    		    	    
      int x = 1 ;
		  while (x != tabD.length)
	    {
	  	 int iExprx = tabD[x] ;
	  	 
	  	 	 hashCode *=  nl ;
	  	 	 
	  	 	 if (iExprx < nl)
	  	 	  hashCode += iExprx + 1 ;//exprs.bestExpr(tabD[x]) ;
	  		
	  	 if (dejaVu[iExprx] !=  onStack)
			 {
				 	dejaVu[iExprx] =  onStack ;
				 	toDerive[topF ++] = iExprx ;	
			 }      
			  x ++ ;
		 }   
		 //System.out.println(hashCode) ;
	  } 		  	  
	  	  
	  return hashCode ;	
	}
	
	
		
  public void negateEquations()  throws GCException
  // Pré : The equations for iExpr exist.
  // We compute the equations for not(iExpr)
	{
		int[] leftPartsN = new int[leftParts.length] ;
		int[][] rightPartsN = new int[leftParts.length][] ;
		
		int i = 0 ;
		while (i != leftParts.length)
		{
			int iExpri = leftParts[i] ;
			leftPartsN[i] = exprs.not(iExpri) ;
			
			{
				int[] rightPartsI = rightParts[i] ;
				int[] rightPartsIN = new int[rightPartsI.length] ;

        if (rightPartsI[0] == exprs.one())
        	rightPartsIN[0] = exprs.zero() ;
        else
        	rightPartsIN[0] = exprs.one() ;
        
        int x = 1 ;
        while (x != rightPartsI.length)
        {
        	rightPartsIN[x] = exprs.not(rightPartsI[x]) ;
        	x ++ ;
        }
        
        rightPartsN[i] = rightPartsIN ;
        exprs.addEquation(leftPartsN[i], rightPartsN[i]) ;
			}
			i ++ ;
		}		
	}
	
	
	
}













