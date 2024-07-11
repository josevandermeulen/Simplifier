package regexpr;
import syntax.* ;

public abstract class AChecks {
	
	IExpressions exprs ;
	long[] dejaVu ;
  int[] toDerive ;
  boolean usesDejaVu ;
  
  	
	int topE ;
	int topM ;
	long magicNumber ;
	int iBest1 ;
	int iBest2 ;
	
	int[] tabD(int iExpr)   throws GCException
	{
		return exprs.tabD(iExpr) ;
	}
		
 
	public AChecks(IExpressions exprs,  boolean usesDejaVu)
	{
		this.exprs = exprs ;
		this.usesDejaVu = usesDejaVu ;	
	}
	
	void initMagicNumber() 
	{
		magicNumber = exprs.newMagicNumber() ;		
	}
	
	void addToRtod(int iExpr1, int iExpr2)  throws GCException
	{
		int iPair = exprs.pair(iExpr1, iExpr2) ;
		
		toDerive[++ topM] = iPair ;
		dejaVu[iPair] = magicNumber ;
	}
	
	boolean RtodIsEmpty() 
	{
		return topM == topE ;
	}
	
	int chooseInRtod()
	{
		int iPair = toDerive[++ topE] ;
		
		return iPair ;
	}
	
	abstract boolean unCompatible(int[] tabD1, int[] tabD2) ;
	
	boolean checkAndUpdateRelation(int iExpr1, int iExpr2) throws GCException
	{
		iBest1 = iExpr1 ;
  	iBest2 = iExpr2 ;
  	
  	//System.out.println(iBest1 + " " + iBest2) ;
  	
  	if (iBest1 == iBest2)
  		return true ;
  	
  	if (dejaVu[exprs.pair(iBest1, iBest2)] == magicNumber)
  		return true ;
  	
  	return false ;
  			  
	}	
	
	void checkAndUpdateRelation(int[] tabD1, int[] tabD2) throws GCException
	{		  	
		if (tabD1 == tabD2)
			return ;
    int x = 1 ;
    while (x != tabD1.length)
    {		     		      	 
  		if (! checkAndUpdateRelation(tabD1[x], tabD2[x]))
  		{
  			 addToRtod(iBest1, iBest2) ;
  		} 		 
  		x ++ ;
    }  		 			  
	}
	
	void initRTod(int iExpr1, int iExpr2)  throws GCException
  {
    	int[] tabE = exprs.tabE(iExpr1) ;
    	int i = 0 ;  
    	while (i != tabE.length)
  	  {
  	  	addToRtod(tabE[i], iExpr2) ;
  	  	i ++ ;
  	  }
  }

 public boolean checkPD(int iExpr1, int iExpr2) throws GCException
  {
  	if (usesDejaVu)
		  dejaVu = exprs.getDejaVu() ;	
		toDerive = exprs.getToDerive() ;		
		
  	initMagicNumber() ;
   	
    boolean check = true ;
    
    topE = - 1 ;
    topM = - 1 ;
            
    initRTod(iExpr1, iExpr2) ;
  	
  	while (! RtodIsEmpty())
  	{
      
  		int iPair = chooseInRtod() ;
  		
  		
		
  		int iF2 = exprs.tabS(iPair)[1] ;
  		int[] tabD2 = tabD(iF2) ;
  		
  		{
  		  int iF1 = exprs.tabS(iPair)[0] ;
        int[] tabE = exprs.tabE(iF1) ;
    	  int i = 0 ;  
    	  while (i != tabE.length)
  	    {
  		    int[] tabD1 = tabD(tabE[i]) ;  		
  		    if (unCompatible(tabD1, tabD2))
  		    { 			
  			    check = false ;
  			    break ; 
  		    } 		
  		    checkAndUpdateRelation(tabD1, tabD2) ;
  		    i ++ ;
  		  }
  		}
  	}  	 	
  	
  	exprs.collectPairs() ;
  	return check ; 
  }
  
  public boolean check(int iExpr1, int iExpr2) throws GCException
  {
  	if (usesDejaVu)
		  dejaVu = exprs.getDejaVu() ;	
		
		exprs.collectPairs() ;

		toDerive = exprs.getToDerive() ;		
  	
  	initMagicNumber() ;
   	
    boolean check = true ;
    
    topE = - 1 ;
    topM = - 1 ;
        
  	addToRtod(iExpr1, iExpr2) ;
  	
  	while (! RtodIsEmpty())
  	{

  		int iPair = chooseInRtod() ;
  		

  		int iF1 = exprs.tabS(iPair)[0] ;
  		int iF2 = exprs.tabS(iPair)[1] ;
  		
  		int[] tabD1 = tabD(iF1) ;
  		int[] tabD2 = tabD(iF2) ;
  		
  		if (unCompatible(tabD1, tabD2))
  		{ 			
  			check = false ;
  			break ; 
  		}
  		  		  		      		

  		checkAndUpdateRelation(tabD1, tabD2) ;
  		

  	}  	 	
  	
  	exprs.collectPairs() ;
  	return check ; 
  }
	
}