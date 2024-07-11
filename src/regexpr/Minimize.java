package regexpr ;
import util.* ;

public class Minimize{
	
	int[] tabE ;
	int[][] tTabD ;
	IExpressions exprs ;
	MakeDFA mDFA ;
	
	int[] hashTable ;
	int[] classNum ;
	
	public Minimize(IExpressions exprs)
	{
		this.exprs = exprs ;		
	}	
	
	public Minimize(IExpressions exprs, MakeDFA mDFA)
	{
		this.exprs = exprs ;	
		this.mDFA = mDFA ;
	}
	
	public void getDFA(int[] tabE, int[][] tTabD)
	{
		this.tabE = tabE ;
		this.tTabD = tTabD ;
	}
	
	public void minimize(int[] tabE, int[][] tTabD)  throws GCException
  {
		getDFA(tabE, tTabD) ;
		minimize() ;
	}
	
	//int bingo = 0 ;
	public void minimize(int iExpr) throws GCException
	{
		 boolean[] hasMDFA = ((SizedExpressions) exprs).getHasMDFA() ;
		 /*if (hasMDFA[exprs.bestExpr(iExpr)])
		 {
		 	 return ;
		 }*/
		 
		 mDFA.computeAllDeriv(iExpr) ;
	   mDFA.computeEquations(iExpr) ;
	   
	   	   
	   minimize(mDFA.leftParts(), mDFA.rightParts()) ;
	   
	   if (exprs instanceof SizedExpressions && 
	   	 ! (exprs instanceof Background))
	   { 
	   	   mDFA.computeBestEquations() ;   	   
	   }
	}
	
	public void minimize() throws GCException
	{
		if (tabE.length <= 1)
		{
			nbrClass = 1 ;
			return  ;
		}
		
	  hashTable = new int[tabE.length * 31 / 7 + 1] ;
		classNum  = new int[tabE.length * 31 / 7 + 1] ;
		
		
		int i = 0 ;
		while (i != tabE.length)
		{
			hashTable[findPos(tabE[i])] = tabE[i] ;
			i ++ ;
		}
		
	  minimizeClasses() ;
	  
		if (exprs instanceof SizedExpressions)
		mergeClasses() ;
	}
	
	int findPos(int iExpr)
	{
		int pos = iExpr % hashTable.length ;
		if (pos == 0)
			pos = 1 ;
		
		while (hashTable[pos] != iExpr && hashTable[pos] != 0)
			pos = succ(pos) ;
		
		return pos ;
	}
	
	int succ(int pos)
	{
		pos ++ ;
		if (pos == hashTable.length)
			pos = 1 ;
		
		return pos ;
	}
	
	int cClass(int iExpr)
	// current class number of iExpr
	{
		return classNum[findPos(iExpr)] ;
	}
	
	void setClass(int iExpr, int classN)
	{
		classNum[findPos(iExpr)] = classN ;
	}
	
	
	boolean equiv(int iExpr1, int iExpr2)
	{
		return cClass(iExpr1) == cClass(iExpr2) ;
	}
	
	boolean equiv(int[] tabD1, int[] tabD2)
	{
		boolean equiv = true ;
		int i = 0 ;
		while (i != tabD1.length && equiv)
		{
			equiv = equiv(tabD1[i], tabD2[i]) ;
			i ++ ;
		}
		
		return equiv ;
	}
	
	public int nbrClasses()
	{
		return nbrClass ;
	}
	
	int nbrClass ; // nombre courant de classes
	int[] nextCell ;
	int[] iExprInCell ;
	int[] firstCell ;
	int firstFree ;
	
	boolean[] classWithAtLeastTwo ;
	int firstClassWithTwo ;
	int[] nextClassWithTwo ;
	
	void minimizeClasses()
	// minimiser l'automate défini par tabE et tTabD
	{
		nbrClass = 0 ; // nombre courant de classes
	  nextCell = new int[tabE.length] ;
	  iExprInCell = new int[tabE.length] ;
	  firstCell = new int[tabE.length] ;
	  firstFree = 0 ;
	  
	  classWithAtLeastTwo  = new boolean [tabE.length] ;
	  firstClassWithTwo  = - 1 ;
	  nextClassWithTwo = new int[tabE.length] ;
	
	
		
	  // Mettre les états non acceptants dans C_0
	  // et les états acceptants dans C_1
	  
	  firstCell[0] = - 1 ;
	  firstCell[1] = - 1 ;
	  
	  {
	  	int Ofirst = exprs.type(tTabD[0][0]) ;
	  	firstClassWithTwo = - 1 ;
	  	
	  	int i = 0 ;
	  	while (i != tTabD.length)
	  	{
	  		iExprInCell[firstFree] = i ;	
	  		
	  		int numClass = 0 ;
	  		if (exprs.type(tTabD[i][0]) != Ofirst)
	  			numClass = 1 ;
	  		
	  		setClass(tabE[i], numClass) ;
	  		
	  		if (firstCell[numClass] != - 1)
	  		{	if (! classWithAtLeastTwo[numClass])
	  	   	{
	  			  classWithAtLeastTwo[numClass] = true ;
	  			  nextClassWithTwo[numClass] = firstClassWithTwo ;
	  			  firstClassWithTwo = numClass ;
	  		  }
	  		}
	  		else
	  			  nbrClass ++ ;
	  		
	  		nextCell[firstFree] = firstCell[numClass] ;
	  		firstCell[numClass] = firstFree ++ ;	

	  		i ++ ;
	  	}	  	
	  }
	  
	  int nbrClassNew = nbrClass ;
	  boolean firstEntry = true ;
	  while (firstEntry || nbrClassNew != nbrClass)
	  {
	  	firstEntry = false ;
	  	changeClasses(nbrClassNew) ;

	  	nbrClass = nbrClassNew ;


	  	
	  	int cl = firstClassWithTwo ;
	  	firstClassWithTwo = - 1 ;
	  	while (cl != - 1)
	  	{
	  		classWithAtLeastTwo[cl] = false ;
	  		int nextCl = nextClassWithTwo[cl] ;
	  		
	  	  int nbrClassNew0 = nbrClassNew ;	
	  		int firstI = iExprInCell[firstCell[cl]] ; 
	  		int next = nextCell[firstCell[cl]] ;
	  		nextCell[firstCell[cl]] = - 1 ;
	  		
	  		while (next != - 1)
	  		{
	  			int i = iExprInCell[next] ;
	  			int pos = next ;
	  			next = nextCell[next] ;
	  			
	  			if (equiv(tTabD[i], tTabD[firstI]))
	  			{
	  				nextCell[pos] = firstCell[cl] ;
	  				firstCell[cl] = pos ;
	  				if (! classWithAtLeastTwo[cl])
	  	    	{
	  			    classWithAtLeastTwo[cl] = true ;
	  			    nextClassWithTwo[cl] = firstClassWithTwo ;
	  			    firstClassWithTwo = cl ;
	  		    }	  				
	  				continue ;
	  			}
	  			
	  			int cl0 = nbrClassNew0 ;
	  			boolean classNotFound = true ;
	  			while (cl0 != nbrClassNew && classNotFound)
	  			{
	  				int j = iExprInCell[firstCell[cl0]] ;
	  	  				
	  				if (equiv(tTabD[i], tTabD[j]))
	  		  	{
	  				  nextCell[pos] = firstCell[cl0] ;
	  				  firstCell[cl0] = pos ;
	  				  classNotFound = false ;
	  				  
	  				  if (! classWithAtLeastTwo[cl0])
	  	    	  {
	  			      classWithAtLeastTwo[cl0] = true ;
	  			      nextClassWithTwo[cl0] = firstClassWithTwo ;
	  			      firstClassWithTwo = cl0 ;
	  		      }	  				  
	  			  }
	  			  cl0 ++ ;
	  			}
	  			
	  			if (classNotFound)
	  			{
	  				nextCell[pos] = - 1 ;
	  				firstCell[cl0] = pos ;
	  				nbrClassNew ++ ;
	  			}
	  		}  	  		
	  		cl = nextCl ;
	  	}	  	 		  	
	  }	 
	}
	

		
	public void changeClasses(int nbrClassNew)
	{
		int cl = nbrClass ;
		while (cl != nbrClassNew)
		{
			
			int pos = firstCell[cl] ;
			while (pos != - 1)
			{
				int iExpr = tabE[iExprInCell[pos]] ;
				setClass(iExpr, cl) ;
				
				pos = nextCell[pos] ;
			}
			cl ++ ;
		}
	}
	
	public void mergeClasses() throws GCException
	{

		boolean[] hasMDFA = ((SizedExpressions) exprs).getHasMDFA() ;
		
		int cl = firstClassWithTwo ;
		while (cl != - 1)
		{
			int pos = firstCell[cl] ;
			int iExpr1 = tabE[iExprInCell[pos]] ;
			hasMDFA[iExpr1] = true ;
		
			pos = nextCell[pos] ;
			
			while (pos != - 1)
			{
				int iExpr2 = tabE[iExprInCell[pos]] ;
				hasMDFA[iExpr2] = true ;
				
			  ((SizedExpressions) exprs).unify(iExpr1, iExpr2) ;
			  

				pos = nextCell[pos] ;
			}		
			cl =  nextClassWithTwo[cl] ;
		}					
	}
}












