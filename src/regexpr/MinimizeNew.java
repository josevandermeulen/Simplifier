package regexpr ;
import util.* ;
import syntax.* ;

public class MinimizeNew{
	
	final byte DFAC = 0 ;
	// We minimize a complete DFA
	// i.e. all ids are the left part of an equation.
	
	//final byte DFAI = 1 ;
	// We minimize an incomplete DFA
	// i.e. it is possibly the case that
	// some ids ids are not the left part of an equation.	
	// We prefer not use it for simplicity
	
	final byte DFAR = 2 ;
	// We minimize a complete DFA
	// "extracted" from an incomplete DFA
	// by marking the ids that are not the left part of an equation.	
	
	final byte DFAN = 3 ;
	// We minimize a a "normalized DFA" using 
	// states 1, 2, ..., n (a special case of complete)
	
	byte sortOfDFA ; 
	// The actual choice.
	
	int[] tabE ;
	int[][] tTabD ;
	IExpressions exprs ;
	MakeDFA mDFA ;
	int nl ;
	boolean[] hasNoDFA ;	
	int[] actualId ;
	
  // for garbage collection
	void cleanup()
	{
		invTab = null ;
		actualId = null ;
		part = null ;
		classNumber = null ;
		dejaVu = null ;
		newSize = null ;
		newClassNumber = null ;
		I = null ;
		precNbr = null ;
		hasNoDFA = null ;
	}
	
	//--------- To unify the identifiers in the same classes -------
			
	public void mergeClasses() throws GCException
	{
    int i = 0 ;
		while (i != m)
		{
			int state = part.first(i) ;
			if (state == - 1)
			// This may happen only if m == 1
		  // and all expressions are equal to 0. !!!! beuaark !
			{
				i ++ ;
				continue ;
			}
			int iExpr1 = actualId[state] ;
			state = part.next(state) ;

			while (state != - 1)
      {
      	int iExpr2 = actualId[state] ;
      	((SizedExpressions) exprs).unify(iExpr1, iExpr2) ;
      	//System.out.println(exprs.size(exprs.bestExpr(iExpr1))) ;
      	state = part.next(state) ;
      }
			i ++ ;
		}		
		
	}
	
	//-------------------- Constructors -------------------------
	
	
	public MinimizeNew(IExpressions exprs)
	// This is used either to minimize the DFA of an expression
	// or to minimize the set of equations of the background
	{
		this.exprs = exprs ;
		this.nl = exprs.nbrLetters() ;
	}
		
	public MinimizeNew()
	// Used to minimize a "normalized DFA" using 
	// states 1, 2, ..., n (a special case of complete)
	{
		
	}
	
	public MinimizeNew(IExpressions exprs, MakeDFA mDFA)
	// This is used to compute the minimal DFA of an expression
	// i.e., a DFAC is first computed and minimized afterwards.
	{
		this(exprs) ;	
		this.mDFA = mDFA ;
	}
	
  //-------------------- Constructors end -------------------------
		
	public void getDFA(int[] tabE, int[][] tTabD)
	// To get a DFA from outside.
	// It can be any kind of DFA, incomplete, complete, or
	// normalized.
	{
		this.tabE = tabE ;
		this.tTabD = tTabD ;
	}
	
	public void minimize(int[] tabE, int[][] tTabD, byte sortOfDFA, String FB)  throws GCException
  {
  	this.sortOfDFA = sortOfDFA ;
		getDFA(tabE, tTabD) ;
		minimize(FB) ;
		cleanup() ;
	}
	

	public void minimize() throws GCException
	{
		minimize("F") ; // For instance
	}
	
	public void minimize(String FB) throws GCException
	{
		if (tabE.length <= 1)
		{
			// Nothing must change.
			m = tabE.length ;
			return  ;
		}
				
		int nbrGoodEq = tabE.length ;
		if (sortOfDFA != DFAN)
	    nbrGoodEq = normalizeEquations() ;
	  
	    
	  if (sortOfDFA == DFAR)
	  {
	    	//printDFA() ;
	  	  markBadEquations(nbrGoodEq) ;	 	
	  	  
	  	  if (FB.equals("B"))
	  	  reduceInvTab() ;
	  	  //printDFA() ;
	  }	    
	         
		initPartition(nbrGoodEq) ;
		
		//if (sortOfDFA == DFAI)
		//printClasses() ;
		if (m >= 2)
		{
			if (FB.equals("F"))
			browseForward(nbrGoodEq) ;
		  else if (FB.equals("B"))
			browseBackward(nbrGoodEq) ;
		  else throw new Error("not F nor B") ;
		}
			  
		if (sortOfDFA != DFAN && exprs instanceof SizedExpressions)
		  mergeClasses() ;
	}
	
  public int minimize(int iExpr) throws GCException
  {
  	
  	return minimize(iExpr, "F") ;
  }
  
  /*public int minimizeSpecial(int iExpr, int i) throws GCException
  {
  	
  	return minimizeSpecial(iExpr, "F", i) ;
  }*/

	
	/*public int minimizeSpecial(int iExpr, String FB, int i) throws GCException
	{
		 boolean[] hasMDFA = ((SizedExpressions) exprs).getHasMDFA() ;
		 if (hasMDFA[exprs.bestExpr(iExpr)])
		 {
		 	 return ;
		 }
		 
		 
		 mDFA.computeAllDerivSpecial(iExpr, i) ; 
		 

		 //if (i == 0)
		 
	   mDFA.computeEquations(iExpr) ;	   
 	   
	   
     minimize(mDFA.leftParts(), mDFA.rightParts(), DFAC, FB) ;
     
     
	   mDFA.computeEquations(exprs.bestExpr(iExpr)) ;
	   
	   if (exprs instanceof SizedExpressions) // ????
	   { 
	   	   return mDFA.hashCode(exprs.bestExpr(iExpr)) ;   	   
	   }
	   else
	   	 return mDFA.leftParts().length ;
	}	*/
	
	
	public int minimize(int iExpr, String FB) throws GCException
	{
		 boolean[] hasMDFA = ((SizedExpressions) exprs).getHasMDFA() ;
		 /*if (hasMDFA[exprs.bestExpr(iExpr)])
		 {
		 	 return ;
		 }*/
		 
		 mDFA.computeAllDeriv(iExpr) ; 
		 //System.out.println("iExpr = " + exprs.toString(iExpr)) ;
	   mDFA.computeEquations(iExpr) ;	    	   
	   //printDFA(mDFA.leftParts(), mDFA.rightParts()) ;
     minimize(mDFA.leftParts(), mDFA.rightParts(), DFAC, FB) ;
	   mDFA.computeEquations(exprs.bestExpr(iExpr)) ;
	   
	   if (exprs instanceof SizedExpressions) // ????
	   { 
	   	   return mDFA.hashCode(exprs.bestExpr(iExpr)) ;   	   
	   }
	   else
	   	 return mDFA.leftParts().length ;
	}
	
	public void minimizeBackground(byte sortOfDFA, String FB) throws GCException
	// Pre: sortOfDFA must be DFAI or DFAR
	{
		int nbrEq = ((Background)exprs).nextIEq.size() ;
		//System.out.println("nbrEq = " + nbrEq) ;
		int[] tabE = new int[nbrEq] ;
		int[][] tTabD = new int[nbrEq][nl] ;
		((Background)exprs).getEquations(tabE, tTabD) ;
		
		minimize(tabE, tTabD, sortOfDFA, FB) ;
	}
	
	public void minimizeBackground()  throws GCException
  {
		minimizeBackground(DFAR, "B") ; 
		// Faster than "F", at least for large sets of equations
		// At least in the "worst case"
	}
  
  
	void markBadEquations(int nbrGoodEq)
	// This is needed for incomplete DFAs
	// We mark (in hasNoDFA) the ids that have 
	// not a complete (sub) DFA
	{
				
		int ltab = tabE.length ;
	  hasNoDFA = new boolean[tabE.length + 1] ;

		invertEquations(nl) ;
	
		// Mark "bad equations"
		
	  int nbrHasDFA = tabE.length ;

		{
			OneList badEq = new OneList(tabE.length + 1) ;
			
			{
				int i = nbrGoodEq ;
				while (i != tabE.length)
				{
					badEq.add(tabE[i]) ;
					i ++ ;
				}
			}
			

			{
				int C = badEq.first() ;
				
				while (C != - 1)
				{
					
				  while (C != - 1)
				  {
				  	int nextC = badEq.next(C) ;
				  	
				  	//System.out.println("hasNoDFA[E" + C + "] = "
				  	//	+ hasNoDFA[C]) ;
				  	
					  nbrHasDFA -- ;
						hasNoDFA[C] = true ;

						int a = 0 ;
						while (a != nl)
						{
						  int[] pred = invTab[C][a] ;
					    
					  	int j = 0 ;
						  while (j != pred.length)
						  {
						    if (! hasNoDFA[pred[j]])
						    {
						    	badEq.add(pred[j]) ;
							  }
							  j ++ ;
						  }
						  a ++ ;
						}		
						badEq.remove(C) ;
					  C = nextC ;
				  }	
				  C = badEq.first() ;
			  }		
		  }
		}
		//printInvTab() ;
	}
  
  
	
	int normalizeEquations()
	// We rename identifiers to 0, 1, ..., tabE.length - 1 ;
	{
		int fact = 1 ;
		if (sortOfDFA == DFAR)
			fact = nl ;
			
		int[] hashTable = new int[tabE.length * 31 * fact / 7 + 1] ;
		int[] normNum  = new int[tabE.length * 31 * fact / 7 + 1] ;
		actualId = new int[tabE.length * fact + 1] ;

		{
			int i = 0 ;
		  while (i != tabE.length)
		  {
			  int pos = findPos(tabE[i] + 1, hashTable) ;
			  hashTable[pos] = tabE[i] + 1 ;
			  normNum[pos] = i + 1 ;
			  actualId[i + 1] = tabE[i] ;
			  i ++ ;
			}
		}
		
		int nbrEq = tabE.length ;
		
		int nbrE = tabE.length ;
		{
			int i = 0 ;
		  while (i != tTabD.length)
		  {
		  	int[] tTabDi = tTabD[i] ;
		  	int x = 1 ;
		  	while (x != tTabDi.length)
		  	{
			     int pos = findPos(tTabDi[x] + 1, hashTable) ;
			     if (hashTable[pos] == 0)
			     {  
			     	 hashTable[pos] = tTabDi[x] + 1 ;
			       normNum[pos] = ++ nbrE ;
			       actualId[nbrE] = tTabDi[x] ;
			     }
			     x ++ ;
			  }
			  i ++ ;
			}
		}
		
		int[] tabEN = new int[nbrE] ;
		int[][] tTabDN = new int[nbrE][] ;
		
		{
			int i = 0 ;
		  while (i != tabE.length)
		  {
			  tabEN[i] = convertIExpr(tabE[i] + 1, hashTable, normNum) ; 
			  
			  int[] tabD = tTabD[i] ;
			  int[] tabDN = new int[tabD.length] ;
			  
			  tabDN[0] = tabD[0] ;
			  
			  int j = 1 ;
			  while (j != tabD.length)
			  {			  	
			  	tabDN[j] = convertIExpr(tabD[j] + 1, hashTable, normNum) ;			  	
			    j ++ ;
			  }		
			  tTabDN[i] = tabDN ;
			  i ++ ;
			}
			
			while (i != tabEN.length)
			{
				tabEN[i] = i + 1 ;
				tTabDN[i] = new int[]{ - 1} ;
				i ++ ;				
			}
		}
		
		this.tabE = tabEN ;
		this.tTabD = tTabDN ;
		return nbrEq ;
		
		//System.out.println("nbrE = " + nbrE + " " + tabEN.length) ;
		
	}
	

	int convertIExpr(int iExpr, int[] hashTable, int[] normNum)
	{
		int pos = findPos(iExpr, hashTable) ;
		return normNum[pos] ;
	}
	
	int findPos(int iExpr, int[] hashTable)
	{
		int pos = iExpr % hashTable.length ;
		if (pos == 0)
			pos = 1 ;
		
		while (hashTable[pos] != iExpr && hashTable[pos] != 0)
			pos = succ(pos, hashTable) ;
		
		return pos ;
	}
	
	int succ(int pos, int[] hashTable)
	{
		pos ++ ;
		if (pos == hashTable.length)
			pos = 1 ;
		
		return pos ;
	}
	
	//-----------------------------------------------------
	
	int[][][] invTab ;

	void invertEquations(int nl)
	// This is needed by HopCroft's algorithm
	// but also by the 'standard' algorithm applied to 
	// incomplete DFAs (case DFAR)
	{
		// We count, for every letter x, and every identifier iE
		// how many identifiers iE' are "pointing" to iE :
		// i.e. iE' = ... x.iE ...
		
		int[][] howMany = new int[tabE.length][nl] ;
		
		
		
		{
			int i = 0 ;
			while (i != howMany.length)
			{
				int[] tabDi = tTabD[i] ;
				int j = 1 ;
				while (j != tabDi.length)
				{
					if (tabDi[j] == 0)
						throw new Error("tabDi[" + j + "] = 0 " + i) ;
					howMany[tabDi[j] - 1][j - 1] ++ ;
					j ++ ;
				}
				i ++ ;
			}
		}
		
		/*{
			int i = 0 ;
			while (i != howMany.length)
			{
				int[] tabDi = tTabD[i] ;
				int j = 1 ;
				while (j != tabDi.length)
				{
					System.out.print(howMany[i][j - 1] + " ") ;
					j ++ ;
				}
				System.out.println() ;
				i ++ ;
			}
		}	*/	
		
		invTab = new int[tabE.length + 1][nl][] ;
		{
			int i = 0 ;
			while (i != howMany.length)
			{
				int j = 0 ;
				while (j != howMany[i].length)
				{
					invTab[i + 1][j] = new int[howMany[i][j]] ;
					j ++ ;
				}
				i ++ ;
			}
		}	
		
		{
			int i = 0 ;
			while (i != tTabD.length)
			{
				int[] tabDi = tTabD[i] ;
				int j = 1 ;
				while (j !=  tabDi.length)
				{
					invTab[tabDi[j]][j - 1][-- howMany[tabDi[j] - 1][j - 1]] = i + 1 ;
					j ++ ;
				}
				i ++ ;
			}
		}	 	
	}
	
	//---------------- the partition ------------------------------
	
	MultiList part ;
	int[] size ; // size of classes
	int m = - 1 ; // Number of classes
	int[] classNumber ; // The index of the class of every state	
	
	
	void initPartition(int nbrGoodEq)
	{
		part = new MultiList(nbrGoodEq, tabE.length + 1) ;
		size = new int[nbrGoodEq + 1] ;
		classNumber = new int[nbrGoodEq + 1] ;
		{		
			// We create the first partition
			// All accepting states in part(0)
			// All rejecting states in part(1)
			int i = 0 ;
			while (i != nbrGoodEq)
			{
				if (sortOfDFA == DFAR
					&& hasNoDFA[tabE[i]])
				{
					i ++ ;
					continue ;
				}
				
				if (tTabD[i][0] == 1)
				{
					part.add(0, tabE[i]) ;
					size[0] ++ ;
					classNumber[tabE[i]] = 0 ;
				}
				if (tTabD[i][0] == 0)
				{
					part.add(1, tabE[i]) ;
					size[1] ++ ;
					classNumber[tabE[i]] = 1 ;
				}
				i ++ ;
			}		
		}
		
		m = 0 ;
		if (size[0] != 0)
		{  
			m ++ ;
		}
		if (size[1] != 0)
		{  
			m ++ ;
		}
		//System.out.println("m = " + m) ;	
	}
	

// ------------------ Classical algorithm --------------------
	
	void browseForwardOnce(int nbrGoodEq)
	{
		// We go through the partition and we split it into
		// non equivalent classes
				
		int C = 0 ;
		int m0 = m ;
		
		while (C != m0)
		{
			int C1 = m ;
			splitForward(C) ;	  	
			C ++ ;
		}
		
		// C = m0 ;			
		while (C != m && m != nbrGoodEq)
		{
			  int E = part.first(C) ;
			  while (E != - 1)
			  {
				  classNumber[E] = C ;
				  E = part.next(E) ;
			  }
			 C ++ ;
		}
	}
	
	void browseForward(int nbrGoodEq)
	{
		if (m < 2)
			return ;
			
		//int count = 1 ;
		int m0 = m ;
		browseForwardOnce(nbrGoodEq) ;
		//System.out.println("m = " + m) ;
		while (m != m0 && m != nbrGoodEq)
		{
			m0 = m ;
		  //count ++ ;
			browseForwardOnce(nbrGoodEq) ;
			//System.out.println("m = " + m) ;
		}		
		//System.out.println("count = " + count) ;
	}
	
	
	void splitForward(int C)
	// We split part(C) into non equivalent states
	// wrt to the previous partition.
	{
		//if (C == 0)
		 // System.out.println("splitForward(C" + C + ") [" + size[C] + "]") ;
		
		
		while (size[C] != 1)
		{
		
		  int firstState = part.first(C) ;
		  int currentState = part.next(firstState) ;
		
		  while(currentState != - 1)
		  {
			  int nextState = part.next(currentState) ;
			
			  if (! equivalent(firstState, currentState))
			  {
			  	part.remove(C, currentState) ;
				  size[C] -- ;
				
				  part.add(m, currentState) ;
				  size[m] ++ ;
			  }
			  currentState = nextState ;
		  }
		
		  if (size[m] == 0)
			  return ;
		
		  C = m ;
			m ++ ;
		}
	}
	
	boolean equivalent(int s1, int s2)
	{
		int[] tabD1 = tTabD[s1 - 1] ;		
		int[] tabD2 = tTabD[s2 - 1] ;
		
		
		boolean equivalent = true ;
		int x = 1 ;
		while (x != tabD1.length && equivalent)
		{			
			equivalent = classNumber[tabD1[x]] == classNumber[tabD2[x]] ;
			x ++ ;
		}
		
		/*if (!equivalent)
		{
			
			System.out.println("E" + s1 + " --a--> C" + classNumber[tabD1[1]]) ;
			System.out.println("E" + s1 + " --b--> C" + classNumber[tabD1[2]]) ;
			System.out.println("E" + s2 + " --a--> C" + classNumber[tabD2[1]]) ;
			System.out.println("E" + s2 + " --b--> C" + classNumber[tabD2[2]]) ;
		}*/
		
		return equivalent ;
	}
	
 
		
//============================ HopCroft's algorithm ==========
// A bit mysterious


	
	int[] dejaVu ;
	int[] newSize ;
	int[] newClassNumber ;
	int magicNumber = 0 ;	
	OneList[] I ;
	int[][] precNbr ;
	
	void reduceInvTab()
	
	{
		  int i = 1 ;
			while (i != invTab.length)
			{
				int[][] invTabi = invTab[i] ;
				int j = 0 ;
				while (j !=  invTabi.length)
				{
					int k = 0 ;
					int[] invTabij = invTabi[j] ;
					int count = invTabij.length ;					
					while (k != invTabij.length)
					{
						if (hasNoDFA[invTabij[k]])
						count -- ;						
						k ++ ;
					}
					
					if (count != invTabij.length)
					{
						int[] invTabijN = new int[count] ;
						int kN = 0 ;
						k = 0 ;
						while (k != invTabij.length)
					  {
						  if (! hasNoDFA[invTabij[k]])
						  {
						  	invTabijN[kN] = invTabij[k] ;
						    kN ++ ;
						  }
						  k ++ ;
					  }
					  invTabi[j] = invTabijN ;
					}					
					j ++ ;
				}
				i ++ ;
			}
	}
	

	
	
	
	
	int a = 0 ;
	int C;		
	int countChooseAClass = 0 ;
	void chooseAClass()
	{
		a = 0 ;
		C = - 1 ;
		while (a != nl && C == - 1)
		{
			C = I[a].first() ;	
			
			if (C != - 1)
			{
				I[a].remove(C) ;		
			}
			else
			a ++ ;
		}
			
		countChooseAClass ++ ;	
	}
	
	
	void addE(int C, int E)
	{
		//System.out.println("E" + E + " is put into C" + C) ;

		part.add(C, E) ;
		
		int x = 0 ;
		while (x != nl)
		{
			if (invTab[E][x].length != 0)
				precNbr[C][x] ++ ;
			
			x ++ ;
		}		
	}
	
	void removeE(int C, int E)
	{
		//System.out.println("E" + E + " is removed from C" + C) ;
		part.remove(C, E) ;
		
		int x = 0 ;
		while (x != nl)
		{
			if (invTab[E][x].length != 0)
				precNbr[C][x] -- ;
			
			x ++ ;
		}
	}
	


	
	void putTheBestInI(int Cs, int CsNew)
	{						
		{
			int x = 0 ;
			while (x != nl)
			{
				if (precNbr[Cs][x] != 0 && precNbr[CsNew][x] != 0)
				{
					if (precNbr[Cs][x] <= precNbr[CsNew][x] 
						    && ! I[x].inList(Cs)) 
					{  
						countAddCs1 ++ ;
						I[x].add(Cs) ;
					}
					else
					{  
						countAddCsNew1 ++ ;
						I[x].add(CsNew) ;
					}	
				}
				else if (precNbr[CsNew][x] != 0 && I[x].inList(Cs))
				{	
					countAddCsNew ++ ;
					I[x].remove(Cs) ;
					I[x].add(CsNew) ;
				}					
				x ++ ;
			}
		}
	}
	
	int countAddCsNew = 0 ;
	int countAddCsNew1 = 0 ;
	int countAddCs1 = 0 ;
	

	
	
	void splitBackwardOnce(int Ci, int a)
	{
		// We compute the list of predecessors
		// of Ci through a
		
		int[] preds ;
		
		{
			int E = part.first(Ci) ;
		  int nbrPred = 0 ;
		  while (E != - 1)
		  {
			  nbrPred += invTab[E][a].length ;
			  E = part.next(E) ;
		  }
		  preds = new int[nbrPred] ;
		}
		
		
		{
			int E = part.first(Ci) ;
		  int nbrPred = 0 ;
		  while (E != - 1)
		  {
			  int[] predsEa = invTab[E][a] ;
			  int i = 0 ;
			  while (i != predsEa.length)
			  {
			  	preds[nbrPred ++] = predsEa[i ++] ;
			  }
			  E = part.next(E) ;
		  }
		}
		//showPreds(Ci, a, preds) ;
			  
	  // We reach the classes Cs	such that Cs --x--> Ci
	  // and how many elements of them points into Ci
	  // We also count the classes
	  int nbrCs = 0 ;
	  {
	  	magicNumber ++ ;
	  	
	  	int k = 0 ;
	  	while (k != preds.length)
	  	{
	  		
	  		int Cs = classNumber[preds[k]] ;	  		
	  		
	  		if (dejaVu[Cs] != magicNumber)
	  		{
	  			dejaVu[Cs] = magicNumber ;
	  			newSize[Cs] = 0 ;
	  			nbrCs ++ ;
	  		}	  			
	  		newSize[Cs] ++ ;		  		  
	  		k ++ ;
	  	}	  	
	  }
	  
	  //System.out.println("nbrCs = " + nbrCs) ;
	  
	  int[] tabC = new int[nbrCs * 2] ;
	  int tabCL = 0 ;
	  
	  // We split the classes into two new ones unless
	  // all their elements points into Ci
	  // We put the classes number int
	  int newM ;
	  {
	  	magicNumber ++ ;
	  	newM = m ;
	  	
	  	int k = 0 ;
	  	while (k != preds.length)
	  	{
	  		int E = preds[k] ;
	  		
	  		int Cs = classNumber[E] ;
	  		
	  		if (dejaVu[Cs] != magicNumber)
	  		{
	  			dejaVu[Cs] = magicNumber ;  	
	  			
	  			tabC[tabCL ++] = Cs ;
	  			
	  			if (newSize[Cs] != size[Cs])
	  			{ 	  				
	  				tabC[tabCL ++] =  newM ;
	  			  newClassNumber[Cs] = newM ;
	  			  newM ++ ;
	  			}	
	  			else
	  				tabC[tabCL ++] = - 1 ;
	  			
	  		}
	  		
	  		if (newSize[Cs] != size[Cs])
	  		{  			
	  			removeE(Cs, E) ;	  				  			
	  			int CsNew = newClassNumber[Cs] ;
	  			addE(CsNew, E) ;
	  		}	  		  
	  		k ++ ;
	  	}	
	  }
	  
	  
	  {
	  	int i = 0 ;
	  	while (i != tabCL)
	  	{
	  		int Cs = tabC[i] ;
	  		int CsNew = tabC[i + 1] ;	  		
	  			
	  		
	  		//printI() ;
	  		if (CsNew != - 1)
	  		{
	  			size[CsNew] = newSize[Cs] ;
	  		  size[Cs] -= newSize[Cs] ;		
	  		  
	  		  
	  		  putTheBestInI(Cs, CsNew) ;
	  		  
	  			int E = part.first(CsNew) ;
	  		  while (E != - 1)
	  		  {
	  			  classNumber[E] = CsNew ;
	  			  E = part.next(E) ;
	  		  } 
	  		}
	  		
	  		i += 2 ;
	  	}	  	
	  }
	  
	  m = newM ;
	  	  
	}
	
	
	void browseBackward(int nbrGoodEq)
	{
		
				
		if (m < 2)
			return ;

		{
			dejaVu = new int[nbrGoodEq] ;
	    newSize = new int[nbrGoodEq] ;
	    newClassNumber = new int[nbrGoodEq] ;
	    I = new OneList[nl] ;
	    int x = 0 ;
	    while (x != nl)
	    {
	    	I[x] = new OneList(tabE.length) ;
	    	x ++ ;
	    }
	    precNbr = new int[nbrGoodEq][nl] ;
		}
		
		{
			int C = 0 ;
			while (C != m)
			{
				int E = part.first(C) ;
				while (E != - 1)
				{
					int x = 0 ;
					while (x != nl)
					{
						if (invTab[E][x].length != 0)
							precNbr[C][x] ++ ;
						x ++ ;
					}					
					E = part.next(E) ;
				}
				C ++ ;
			}
		}
		
		putTheBestInI(0, 1) ;			
		chooseAClass() ;
		while (C != - 1 && m != nbrGoodEq)
		{
			
			//System.out.println("splitBackwardOnceNew(C" + C + ", " + ((char)('a' + a) + ")")) ;
			splitBackwardOnce(C, a) ;
			chooseAClass() ;
		}
	}
	
	//---------------- Hopcroft's algorithm end -------------- 
	
	// Check methods
	
	void checkMinimalClasses()
	{
		
		int Ci = 0 ;
		loop : while (Ci != m)
		{
			int E = part.first(Ci) ;
			int E2 = part.next(E) ;
			while (E2 != - 1)
			{
				int E3 = part.next(E2) ;
				if (!equivalent(E, E2))
				{
					//printClasses() ;
					System.out.println("not equivalent in C" + Ci + " : E" + E + " E" + E2) ; 
					
					break loop ;
				}
				
				E2 = E3 ;
			}
			Ci ++ ;
		}
	}
	
	int precNbr(int C, int x)
	// test method !
	{
		int count = 0 ;
		int E = part.first(C) ;
		while (E != - 1)
		{
			if (invTab[E][x].length != 0)
				count ++ ;
			E = part.next(E) ;
		}
		return count ;
	}
	
	
//========================== Printing methods =====================
	

	
	
	char chr(int x)
	{
		return (char)('a' + x) ;
	}
	
	void printInvTab() 
	{
			int i = 1 ;
			while (i != invTab.length)
			{
				int[][] invTabi = invTab[i] ;
				int j = 0 ;
				while (j !=  invTabi.length)
				{
					System.out.print("invTab[E" + i + "][" + ((char)('a' + j)) + "] = ") ;
					int k = 0 ;
					int[] invTabij = invTabi[j] ;
					while (k != invTabij.length)
					{
						System.out.print("E" + invTabij[k] + ", ") ;
						k ++ ;
					}
					System.out.println() ;
					j ++ ;
				}
				i ++ ;
			}
	}
	
	void printI()
	{
		int x = 0 ;
		while (x != nl)
		{
			System.out.print("I(" + chr(x) + ") = ") ;
			//I[x].print() ;
			int C = I[x].first() ;
			while (C != - 1)
			{
				
				System.out.print("C" + C + " ") ;
				C = I[x].next(C) ;			
			}
			System.out.println() ;
			x ++ ;
		}
	}
	
  
  void showPreds(int Ci, int a, int[] preds)
  {
  	
  	System.out.print("preds : ") ;
  	int k = 0 ;
  	while (k != preds.length)
  	{
  		int pred = preds[k] ;
  		System.out.print("E" + pred + "[C" + classNumber[pred] + "] ") ;
  		k ++ ;
  	}
  	System.out.println() ;
  }
	
	
	void printClasses()
	{
		int i = 0 ;
		while (i != m)
		{
			int state = part.first(i) ;
			System.out.print("C" + i + " [" + size[i] + "] = E" + state + "[C" + classNumber[state] + "]") ;
			state = part.next(state) ;

			while (state != - 1)
      {
      	
      	System.out.print(", E" + state + "[C" + classNumber[state] + "]") ;
      	state = part.next(state) ;
      }
      System.out.println() ;
			i ++ ;
		}
	}
	
	void printDFA()
	{
		printDFA(tabE, tTabD) ;
	}
	
	void printDFA(int[] tabE, int[][] tTabD)
	{
		
		System.out.println("------------------------------------------") ;
		
		int i = 0 ;
		
		while (i != tabE.length)
		{
			int[] tabD = tTabD[i] ;
			System.out.print("E" + tabE[i] + " = " + tabD[0] + " ") ;
			
			int x = 1 ;
			while (x != tabD.length)
			{
				System.out.print(" \t +  " + chr(x - 1) + ".E" + tabD[x]) ;
				x ++ ;			
			}	
			
			if (actualId != null)
			{
				System.out.print(" [" + actualId[tabE[i]]) ;
		
		    if (hasNoDFA != null)
				  System.out.println(", " + hasNoDFA[tabE[i]]  + "]") ;
			  else 
			    System.out.println("]") ;
			}
		  else
		  	System.out.println() ;
		  
			i ++ ;
		}
		
		System.out.println("------------------------------------------") ;	
	}
	
	int readDFA()
	{
		java.util.Scanner in = new java.util.Scanner(System.in) ;
		
		int nl = in.nextInt() ;
		int nbrLines = in.nextInt() ;
		tabE = new int[nbrLines] ;
		tTabD = new int[nbrLines][nl + 1] ;
		int i = 0 ;
		while (i != tabE.length)
		{
			tabE[i] = i + 1 ;
			int x = 0 ;
			while (x != nl + 1)
			{
				tTabD[i][x] = in.nextInt() ;
				x ++ ;
			}
			i ++ ;
		}
		this.nl = nl ;
		return nl ;
	}
	
	// Demo methods
	public static void main(String[] args) throws GCException
	{
		
		MinimizeNew minm = new MinimizeNew() ;
		
		int nl = minm.readDFA() ;
 		//minm.printDFA() ;
 		
 		if (args[0].equals("B"))
 		  minm.invertEquations(nl) ;
 		
 		//minm.printInvTab() ;
 		
 		minm.initPartition(minm.tabE.length) ;
 		
 		//minm.printClasses() ;
 		
 		long t0 = System.nanoTime() ;
 		if (args[0].equals("F"))
 		minm.browseForward(minm.tabE.length) ;
 		else
 		if (args[0].equals("B"))
 		minm.browseBackward(minm.tabE.length) ;
 		else throw new Error("First arg must be F or B") ;
 		
 		
 		
 		long totalTime = System.nanoTime() - t0 ;
 		System.out.println("Time = " + util.Time.toString(totalTime)
	  	+ " sec") ; 


 		System.out.println("m = " + minm.m) ;
 		
 		
 		
 		//minm.printClasses() ;
 		//minm.mergeClasses() ;

	}
	
	
	public static void main1(String[] args) throws GCException
	{
		
		Expressions exprs = new Background(1000000, 2) ;
		MakeDFA mDFA = new MakeDFA(exprs) ;
		MinimizeNew minm = new MinimizeNew(exprs) ;
		
		Term term = RegExprReader.toTerm(args[0]) ;
		int iExpr = exprs.toExpression(term) ;
 		
 		mDFA.computeAllDeriv(iExpr) ;
 		mDFA.computeEquations(iExpr) ;
 		minm.getDFA(mDFA.leftParts(), mDFA.rightParts()) ;
 		
 		int nbrGood = minm.normalizeEquations() ;
 		exprs.printEquations(minm.tabE, minm.tTabD) ;
 		
 		if (args[1].equals("B"))
 		minm.invertEquations(exprs.nbrLetters()) ;
 		
 		
 		minm.initPartition(nbrGood) ;
 		
 		long t0 = System.nanoTime() ;
 		if (args[1].equals("F"))
 		minm.browseForward(nbrGood) ;
 		else
 		if (args[1].equals("B"))
 		minm.browseBackward(nbrGood) ;
 		else throw new Error("Second arg must be F or B") ;
 		//minm.printClasses() ;
 		long totalTime = System.nanoTime() - t0 ;
 		System.out.println("Time = " + util.Time.toString(totalTime)
	  	+ " sec") ; 

 		System.out.println("m = " + minm.m) ;
 		mDFA.computeEquations(iExpr) ;
 		
 		
 		minm.mergeClasses() ;
 		System.out.println(exprs.toString(exprs.bestExpr(iExpr))) ;
 		iExpr = exprs.bestExpr(iExpr) ;
 		 System.out.println(exprs.size((iExpr))) ;
   
		mDFA.computeEquations(iExpr) ;
 		exprs.printEquations(mDFA.leftParts(), mDFA.rightParts()) ;
 		
	}
}












