package regexpr;

public class ChecksA extends InfEq{
	
	int[] pTab ;
	public ChecksA(IExpressions exprs){
		super(exprs) ;
		pTab = ((Expressions)exprs).getPTab() ;

	}
	
	long onStack ;
  long subsumed ;
  
	void initMagicNumber() 
	{
		magicNumber = exprs.newMagicNumber() ;
		onStack = magicNumber ;
		subsumed = exprs.newMagicNumber() ;

	}
	
	boolean RtodIsEmpty() 
	{
		while (topM != topE)
		{
			int iPair = toDerive[topE + 1] ;
			if (dejaVu[iPair] == subsumed)
  		{
  			++ topE ;
  		}
  		else 
  			break ;
		}
		
		return topM == topE ;
	}
	
	boolean checkAndUpdateRelation(int iExpr1, int iExpr2) throws GCException
	{
		int iPair = exprs.pair(iExpr1, iExpr2) ;
		boolean inRel = false ;
  	
  	if (dejaVu[iPair] < onStack)
  	{
  		subsume(iPair) ;  		
  		inRel = dejaVu[iPair] == subsumed ;    	  	
    }
    else
    	inRel = true ;
    
    if (! inRel)
    {
    	iBest1 = iExpr1 ;
    	iBest2 = iExpr2 ;
    }
    
    return inRel ;
	}

	
  void subsume(int iPD)
  // Soit iPD = PAIR(iF, iFNew)
  // On parcourt la liste des expressions DIFF(iF, iF')
  // Si iFNew est subsumé par l'une d'elle on ne la change pas.
  // Sinon on en enlève celles qui sont subsumées par IFNew et on
  // rajoute iPD au début
  {
  	if (exprs.type(iPD) != Expressions.PAIR)
  			throw new Error("0 " + exprs.toString(iPD)) ;
  	
  	int iF = exprs.tabS(iPD)[0] ;
  	if (dejaVu[iF] != magicNumber)
  	{
  		dejaVu[iF] = magicNumber ;
  		pTab[iF] = - 1 ;
  		
  	}  	  	
  	int iFNew = exprs.tabS(iPD)[1] ; 	
  	int prec = iF ;
  	int ptr = pTab[iF] ;
  	int cp = 0 ;
  	
  	while (ptr != - 1)
  	{
  		if (exprs.type(ptr) != Expressions.PAIR)
  			throw new Error(exprs.toString(ptr)) ;		
  		
  		int iFi = exprs.tabS(ptr)[1] ;

  		cp  = exprs.subsumeTest(iFNew, iFi) ;
  		if (cp != 0)
  		{
 			break ;
  		} 		
  		prec = ptr ;
  		ptr = pTab[ptr] ;
  	}
  	  	
  	if (cp >= 2)
  	{
  		if (cp == 2)
  		{
  			pTab[prec] = pTab[ptr] ;
  			pTab[ptr] = pTab[iF] ;
  			pTab[iF] = ptr ;
  			dejaVu[iPD] = subsumed ;
  		}
  		return ;
  	}
  	
  	boolean cpNotNull = false ;
  	
  	while (ptr != - 1)
  	{
  		if (cp == 1)
  		{
  			cpNotNull = true ;
  			dejaVu[ptr] = subsumed ;
  			ptr = pTab[ptr] ;
  			pTab[prec] = ptr ;
  		}
  		else
  		{
  			prec = ptr ;
  			ptr = pTab[ptr] ;
  		}
  		
  		if (ptr != - 1)
  		{
  			int iFi = exprs.tabS(ptr)[1] ; 			
  		  cp  = exprs.subsumeTest(iFNew, iFi) ; 		  
  		}
  	}
  	
  	if (cpNotNull)
  	{
  		pTab[iPD] = pTab[iF] ;
  	  pTab[iF] = iPD ;
  	}
  	else
  	{
  	  pTab[prec] = iPD ;
  	  pTab[iPD] = - 1 ;
  	}
  }
  

} 