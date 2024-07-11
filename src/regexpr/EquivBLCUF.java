package regexpr;
import syntax.* ;

public class EquivBLCUF extends EquivUF{
  
	public EquivBLCUF(IExpressions exprs)
	{
		super(exprs) ;		
	}
	
	boolean checkAndUpdateRelation(int iExpr1, int iExpr2) throws GCException
	{
		boolean check = super.checkAndUpdateRelation(iExpr1, iExpr2) ;
  		
	  if (check)
	  	return true ;
	  
	  boolean[] dejaVu1 = new boolean [topM + 1] ;
  	boolean fixPointReached = false ;
  	while (! fixPointReached)
    {
  		fixPointReached = true ;
  		int i = 0 ;
  		while (i != topM + 1)
  		{  			   	 	 
  			if (dejaVu1[i])
  			{
  			  i ++ ;
  			  continue ;
  			}
  			   	 	 
  			int iX = exprs.tabS(toDerive[i])[0] ;
  			int iY = exprs.tabS(toDerive[i])[1] ;
  			   	 	   
  			if (exprs.isSubsumed(iX, iBest1))
  			{
  			   int iBestNew = exprs.union(iBest1, iY) ;
  			   dejaVu1[i] = true ;
  			   if (iBestNew != iBest1)
  			   { 
  			   	 	iBest1 = iBestNew ;
  			   	 	fixPointReached = false ;
  			   }
  			}
        else if (exprs.isSubsumed(iY, iBest1))
  			{
  			   int iBestNew = exprs.union(iBest1, iX) ;
  			   dejaVu1[i] = true ;
  			   if (iBestNew != iBest1)
  			   { 
  			   	  iBest1 = iBestNew ;
  			   	 	fixPointReached = false ;
  			   }
   		  } 			   	     
  			i ++ ;  			   	 	   
  	  }
    }	
  			   	 
	  dejaVu1 = new boolean [topM + 1] ;
  	fixPointReached = false ;
  	while (! fixPointReached)
    {
  		fixPointReached = true ;
  		int i = 0 ;
  		while (i != topM + 1)
  		{  			   	 	 
  			if (dejaVu1[i])
  			{
  			  i ++ ;
  			  continue ;
  			}
  			   	 	 
  			int iX = exprs.tabS(toDerive[i])[0] ;
  			int iY = exprs.tabS(toDerive[i])[1] ;
  			   	 	   
  			if (exprs.isSubsumed(iX, iBest2))
  			{
  			   int iBestNew = exprs.union(iBest2, iY) ;
  			   dejaVu1[i] = true ;
  			   if (iBestNew != iBest2)
  			   { 
  			   	 	iBest2 = iBestNew ;
  			   	 	fixPointReached = false ;
  			   }
  			}
        else if (exprs.isSubsumed(iY, iBest2))
  			{
  			   int iBestNew = exprs.union(iBest2, iX) ;
  			   dejaVu1[i] = true ;
  			   if (iBestNew != iBest2)
  			   { 
  			   	  iBest2 = iBestNew ;
  			   	 	fixPointReached = false ;
  			   }
   		  } 			   	     
  			i ++ ;  			   	 	   
  	  }
    }	
    
    return iBest1 == iBest2 ;
	  
	}	
	
}