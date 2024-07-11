package regexpr;
import syntax.* ;

public class EquivP extends EquivBLC{
	
	public EquivP(IExpressions exprs)
	{
		super(exprs) ;		
	}
	
	void checkAndUpdateRelation(int[] tabD1, int[] tabD2) throws GCException
	{		  					
    int x = 1 ;
    while (x != tabD1.length)
    {		     		      	 
  		if (! checkAndUpdateRelation(tabD1[x], tabD2[x]))
  		{
  			 addToRtod(tabD1[x], tabD2[x]) ;
  		} 		 
  		x ++ ;
    }  		 			  
	}
	
}