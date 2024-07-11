package regexpr ;
import util.* ;

public class Background extends SizedExpressions{
	
	/* 
	
	expressions + equations 
	
	*/
	
	
	int[] tabIExpr ; // tableauX des 
	int[] tabITabD ; // équations : iExpr = iTabD
	
	boolean[] hasMDFA ;
	boolean[] simplified ;
	
	MultiList hashEq ; // liste des identifiants d'équations à cette position
	// (hash table des équations)	
	public TwoList nextIEq ;  // liste des identifiants
	// d'équations, utilisés et non utilisés (2 listes)
	
	int[][] ttabD ; // Tableau des tableaux de dérivées
	// L'indice est l'identifiant du tableau dans la table de hash
	TwoList nextITabD  ;
	// liste des identifiants de tableaux utilisés (+ réservoir)		
	MultiList hashTabD ; // Table de hash des tableaux de dérivées
	// L'indice est donné par le hash code du tableau.
	// La liste correspondant à un indice est celle des indices
	// des tableaux avec ce hash code.
	
	MultiList listIEqForITabD ; 
	// listIEqForITabD(iTab) est la liste des
	// iEq identifiants d'une équation iExpr = iTabD 
	
	MultiList listIEqForIExpr ;
  // listIEqForIExpr(iExpr) est la liste des iEq identifiants
	// d'une équation iExpr = iTabD
		
	MultiList[] listIEqHasIExprAtX ; // listIEqHasIExprAtX[i](iExpr) est la liste des
	// identifiants des équations qui ont l'identifiant iExpr_x
	// à la position x (si i > 0).

	OneList listITabDWithTwoIExpr ;
	// Liste des iTabD tels qu'il existe deux équations
	// iExpr = iTabD et iExpr' = iTabD	
	
	OneList listIExprWithTwoITabD ;
	// Liste des iExpr tels qu'il existe deux équations
	// iExpr = iTabD et iExpr = iTabD'
	
	ListITabs toUnify ;
	// Liste de paires d'identifiants d'expressions à unifier
	int iBuf = 0 ;
	
	public void getEquations(int[] tabE, int[][] tTabD) throws GCException
	{
		merge() ;
		
		int i = 0 ;
		int iEq = nextIEq.first() ;
		while (i != tabE.length)
		{
			tabE[i] = tabIExpr[iEq] ;
			tTabD[i] = ttabD[tabITabD[iEq]] ;
			i ++ ;
			iEq = nextIEq.next(iEq) ;
		}		
		if (iEq != - 1)
			throw new Error("iEq != - 1") ; 
	}


		
	public Background(final int memorySize, int nbrLetters)
	{
		super(memorySize, nbrLetters) ;
		reinit() ;
	}
	
	public void reinit()
	{
		super.reinit() ;
		
		tabIExpr = null ;
		tabITabD =  null ;
		hashEq   =  null ;		
		ttabD =  null ;
		hashTabD =  null ;		
		listIEqForITabD =  null ;
		listIEqForIExpr =  null ;		
		listIEqHasIExprAtX = null ;					
		listITabDWithTwoIExpr = null ;
		listIExprWithTwoITabD = null ;
		nextITabD = null ;
		nextIEq = null ;		
		toUnify = null ;
		
		System.gc() ;		
		
		
		tabIExpr = new int[tree.length] ;
		tabITabD = new int[tree.length] ;
		hashEq   = new MultiList(tree.length, tree.length) ;
		
		ttabD = new int[tree.length][] ;
		hashTabD = new MultiList(tree.length, tree.length);	
		
		listIEqForITabD = new MultiList(tree.length, tree.length);
		listIEqForIExpr = new MultiList(tree.length, tree.length);
		
		listIEqHasIExprAtX = new MultiList[nbrLetters + 1] ;
		
		{
			int i = 1 ;
			while (i != listIEqHasIExprAtX.length)
			{
				listIEqHasIExprAtX[i] = new MultiList(tree.length, tree.length) ;
				i ++ ;
			}
		}
		
		listITabDWithTwoIExpr = new OneList(tree.length) ;
		listIExprWithTwoITabD = new OneList(tree.length) ;
		nextITabD = new TwoList(tree.length) ;
		nextIEq   = new TwoList(tree.length) ;			
		toUnify   = new ListITabs() ;
		hasMDFA   = new boolean[tree.length] ;	
		//simplified   = new boolean[tree.length] ;
		//System.out.println("background : " + simplified) ;
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
	
	void removeEq(int iEq)
	//
	// iEq est l'identifiant d'une équation 
	// iExpr = iTabD
	// qu'on veut retirer du background...
	
	// On suppose que iExpr et iTabD ne sont utilisés
	// que dans cette équation. !!!! ????
	//
	// On enlève iExpr de rien du tout...
	// On enlève iTabD de hashTabD, 
	// Soit tabD = ttabD[iTabD],
	//   on peut faire ttabD[iTabD] = null
	// On remet iTabD dans nextITabD	
	// On retire iEq de toutes les listes
	//   listIEqHasIExprAtX[x](Ex) où tabD = ... + x.Ex + ... (Ex != 0)
	//   Ex = tabD[x]
	// On retire iEq de hashEq et de la table de hash des
	//   équations, de listIEqForITabD(idTab), de listITab(iExpr)
	{
		
		int iExpr = tabIExpr[iEq] ;			
		int iTabD = tabITabD[iEq] ;
		int[] tabD = ttabD[iTabD] ;		

		// On enlève iEq de hashEq :
		{
			int pos = findKey(iExpr, iTabD) ;
			hashEq.remove(pos, iEq) ;
			nextIEq.remove(iEq) ;
		}
		
		// On enlève iExpr et iTabD de listIEqForIExpr et listIEqForITabD
		listIEqForIExpr.remove(iExpr, iEq) ;
		listIEqForITabD.remove(iTabD, iEq) ;
		
		// If listIEqForITabD(iTabD) is Empty
		// we remove iTabD from hashTabD and more...
		if (listIEqForITabD.isEmpty(iTabD))
		{
			int pos = findKey(tabD) ;
		  hashTabD.remove(pos, iTabD) ;		  		
		  ttabD[iTabD] = null ; // pour GC, uniquement
		  nextITabD.remove(iTabD) ;
		}

		// If listIEqForIExpr(iExpr) has less than two elements,
		// we remove iExpr from listIExprWithTwoITabD 
		if (! listIEqForIExpr.hasAtLeastTwo(iExpr))
			listIExprWithTwoITabD.remove(iExpr) ;
		
			// If listIEqForITabD(iTabD) has less than two elements,
		// we remove iTabD from listITabDWithTwoIExpr 
		if (! listIEqForITabD.hasAtLeastTwo(iTabD))
			listITabDWithTwoIExpr.remove(iTabD) ;
			
		{
			int x = 1 ;
			while (x != listIEqHasIExprAtX.length)
			{
				listIEqHasIExprAtX[x].remove(tabD[x], iEq) ;
				x ++ ;
			}
		}		
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
	
	
	public int iExprToTabD(int iExpr)
	{
		int bExpr = bestExpr(iExpr) ;
		int iEq = listIEqForIExpr.first(bExpr) ;
		if (iEq == - 1)
		{
			return - 1 ;
		}
		return tabITabD[iEq] ;
	}
	
	public int[] exprToTabD(int iExpr)
	{
		
		int idTabD = iExprToTabD(iExpr) ;
		if (idTabD == - 1)
		{
			return null ;
		}
		return ttabD[idTabD] ;
	}
	
	//public boolean tabDHasExpr(int[] tabD)
	//{
	//	return  findITabD(tabD) != - 1 ;
	//} 
	
	
	
	public int tabDToIExpr(int[] tabD) throws GCException
	// S'il existe une équation
	// iExpr = tabD, on renvoie iExpr,
	// sinon, on renvoie super.tabDToIExpr(tabD) 
	{
		int idTab = findITabD(tabD) ;	
		if (idTab == - 1)
			return super.tabDToIExpr(tabD) ;
		
		int iEq = listIEqForITabD.first(idTab) ;
		if (iEq != - 1)
			return tabIExpr[iEq] ;
		else
			return super.tabDToIExpr(tabD) ;
	}
	
	int hashCode(int[] tabD)
	{
		int x = tabD[0] ;
		int i = 1 ;
		while (i != tabD.length)
		{
			x = x * 35569 + tabD[i] ;
			i ++ ;
		}		
		return x  ;
	}
	
	int hashCode(int iExpr, int idTab)
	// hash code pour les équations.
	{
		return iExpr * 35569 + idTab ;
	}
	
	int findKey(int[] tabD)
	// position d'un tableau hashTab
	{
		int pos = hashCode(tabD) % tree.length;
		if (pos < 0)
			pos = - pos ;
		return pos ;
	}
		
	int findKey(int iExpr, int iTab)
	// position d'une équation dans la table hashEq
	{
		int pos = hashCode(iExpr, iTab) % tree.length;
		if (pos < 0)
			pos = - pos ;
		return pos ;
	}
	
	int findIEq(int iExpr, int iTab)
	// If an equation iExpr = idTab exists
	// return its iEq or - 1
	{
		int pos = findKey(iExpr, iTab) ;
		
		int iEq = hashEq.first(pos) ;		
		while (iEq != - 1)
		{
			if (tabIExpr[iEq] == iExpr && tabITabD[iEq] == iTab)
				return iEq ;
			iEq = hashEq.next(iEq) ;
		}		
		return iEq ;
	}
	
		
	int findITabD(int[] tabD)
	// Renvoyer l'identifiant de tabD 
	// if it exists or - 1 otherwise
	{
		int pos = findKey(tabD) ;
				
		int idTab = hashTabD.first(pos) ;
		while (idTab != - 1)
		{			
			if (equals(ttabD[idTab], tabD))
			return idTab ;			
			idTab = hashTabD.next(idTab) ;
		}			

		return idTab ;
	}

	
	int[] bestify(int[] tabD)
	{
		int[] res = new int[tabD.length] ;
		res[0] = tabD[0]  ;
		int x = 1 ;
		while (x != tabD.length)
		{
			res[x] = bestExpr(tabD[x]) ;
			x ++ ;
		}
		return res ;
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
	
	public int[] addEquationNew(int iExpr, int[] tabD)  throws GCException
	{		
		int iEq = addEq(iExpr, tabD) ;
		
		addToBuffer(iBuf, iEq) ;
		
		return tabD ;
	}	
	
	boolean isBest(int iExpr, int[] tabD)
	{
		if (iExpr != bestExpr(iExpr))
			return false ;
		int x = 1 ;
		while (x != tabD.length)
		{
			if (tabD[x] != bestExpr(tabD[x]))
				return false ;
			x ++ ;
		}
		
		return true ;
	}
	
	void bestifyEquations()
	{
		int[] tabIEq = bufferToTabEAndReinit(iBuf) ;
		int i = 0 ;
		while (i != tabIEq.length)
		{
			int iEq = tabIEq[i ++] ;
			int iExpr = tabIExpr[iEq] ;
			int[] tabD = ttabD[tabITabD[iEq]] ;
			if (tabD != null && ! isBest(iExpr, tabD))
			{
				removeEq(iEq) ;
			  addEq(bestExpr(iExpr), bestify(tabD)) ;
			}
		}
	}
	
	public int[] addEquation(int iExpr, int[] tabD)  throws GCException
	{
		super.addEquation(iExpr, tabD) ;
		
		//System.out.println("addEquation") ;
		iExpr = bestExpr(iExpr) ;
		int[] tabDB = bestify(tabD) ;
		
		addEq(iExpr, tabDB) ;
		merge() ;	
		
		salomaaRule(bestExpr(iExpr), bestify(tabDB)) ;

		return tabD ;
	}
	
	
	int addEq(int iExpr, int[] tabD)
	// Pré : iExpr est l'identifiant d'une expression
	// tabD est un tableau de dérivées pour Expr
	// On crée, le cas échéant un identifiant pour tabD
	// Ensuite, on ajoute l'équation iExpr = iTabD
	{		
		int iTabD = findITabD(tabD) ;	
		if (iTabD != - 1)
		{
		  int iEq = findIEq(iExpr, iTabD) ;
		  if (iEq != - 1)
			return iEq;
		}
		else
		{
			iTabD = nextITabD.choose() ;
			ttabD[iTabD] = tabD ;
		  hashTabD.add(findKey(tabD), iTabD) ;		
		}
		
		int iEq = findIEq(iExpr, iTabD) ;
		if (iEq == - 1)
		{				
		  iEq = nextIEq.choose() ;		   
		  hashEq.add(findKey(iExpr, iTabD), iEq) ;
		  tabIExpr[iEq] = iExpr ;
		  tabITabD[iEq] = iTabD ;
		}
			
  	listIEqForITabD.add(iTabD, iEq) ;
		listIEqForIExpr.add(iExpr, iEq) ;
			
		if (listIEqForITabD.hasAtLeastTwo(iTabD))
			listITabDWithTwoIExpr.add(iTabD) ;
		
	  if (listIEqForIExpr.hasAtLeastTwo(iExpr))
			listIExprWithTwoITabD.add(iExpr) ;
				
		int x = 1 ;
		while (x != tabD.length)
		{
			listIEqHasIExprAtX[x].add(tabD[x], iEq) ;
			x ++ ;
		}			
		return iEq ;
	}

	void checkAtLeastTwo()
	{
		//"check listITabDWithTwoIExpr") ;
		listITabDWithTwoIExpr.print() ;
		int iExpr = listITabDWithTwoIExpr.first() ;
		while (iExpr != - 1)
		{
			listIEqForITabD.print(iExpr) ;
			iExpr = listITabDWithTwoIExpr.next(iExpr) ;
		}
	}
	
	public long nbrUnif = 0 ;
	void newUnif()
	{
		nbrUnif ++ ;
	}
	
	public void unify(int iExpr1, int iExpr2)  throws GCException
	{		
		//System.out.println("unify") ;
		toUnify.add(iExpr1, iExpr2) ;				
		merge() ;
	}
	
	public void merge() throws GCException
	{
    long[] dejaVu = getDejaVu() ;
    long developped = magicNumber() ;
		
		
		while (!(toUnify.isEmpty() && listITabDWithTwoIExpr.isEmpty() && listIExprWithTwoITabD.isEmpty()))
		{
			//System.out.println("toUnify.isEmpty() = " + toUnify.isEmpty()) ;
			//System.out.println("listITabDWithTwoIExpr.isEmpty()) = " + listITabDWithTwoIExpr.isEmpty()) ;
			//System.out.println("listIExprWithTwoITabD.isEmpty() = " + listIExprWithTwoITabD.isEmpty()) ;
			//System.out.println("-------------------------------------") ;
			
			
			while (! toUnify.isEmpty())
			{ 
				int[] two = toUnify.remove() ;
							  
			  int iExpr1 = bestExpr(two[0]) ; int iExpr2 = bestExpr(two[1]) ;
			  		  
			  //System.out.println(iExpr1 + " --- " + iExpr2) ;
			  if (iExpr1 == iExpr2)
			  	continue ;
			  
			  two = unionFind(iExpr1, iExpr2) ;
			  iExpr1 = two[0] ; iExpr2 = two[1] ;
			  substitute(iExpr1, iExpr2) ;	
			  
			  /*int iExprU = union(iExpr1, iExpr2) ;
			  
			  //System.out.println(iExpr1 + " %%% " + iExpr2) ;
			  if (iExpr1 != iExprU)
			  {	
			  	tree[iExprU] += tree[iExpr1] ;
			  	tree[iExpr1] = iExprU ;
		      substitute(iExprU, iExpr1) ;	      
		    }			  
		    
		    if (iExpr2 != iExprU)
			  {	
			  	tree[iExprU] += tree[iExpr2] ;
			  	tree[iExpr2] = iExprU ;
		      substitute(iExprU, iExpr2) ;	      
		    }*/
		    
		    
		    
		    if (dejaVu[iExpr2] == developped)
		    {	
		    	dejaVu[iExpr1] = developped ;
		    }
		    
		    /*if (simplified[iExpr2] || simplified[iExpr1])
		    {	
		    	simplified[iExpr1] = true ;
		    	simplified[iExpr2] = true ;
		    }*/
		    
			    
		    /*if (dejaVu[iExpr1] == developped)
		    {	
		    	dejaVu[iExprU] = developped ;
		    }*/
		    
		    if (hasDFA[iExpr1] || hasDFA[iExpr2] )
		    {	
		    	hasDFA[iExpr1] = true ;
		    	hasDFA[iExpr2] = true ;
		    }
		    
        if (hasMDFA[iExpr2])
		    {	
		    	hasMDFA[iExpr1] = true ;
		    }

		  }
		
		
		
		if (! listIExprWithTwoITabD.isEmpty())
		{
			
			int iExpr = listIExprWithTwoITabD.first() ;
			
			//listIEqForIExpr.print(iExpr) ;

			int[] iEqs   = listIEqForIExpr.firstTwo(iExpr) ;
			
		  int iTab1 = tabITabD[iEqs[0]] ;
			int iTab2 = tabITabD[iEqs[1]] ;
			
			//System.out.println(iEqs[0] + " $$$ " + iEqs[1]) ;

			//System.out.println(iTab1 + " $$$ " + iTab2) ;
			//System.out.println(tabIExpr[iEqs[0]] + " $$$ " + tabIExpr[iEqs[1]]) ;
			
			
			int[] tabD1 = ttabD[iTab1] ;
			int[] tabD2 = ttabD[iTab2] ;
			
			if (tabD1[0] != tabD2[0])
			{
				System.out.println("tabD1[0] = " + tabD1[0]) ;
				System.out.println("tabD2[0] = " + tabD2[0]) ;
				throw new Error("tabD1[0] != tabD2[0]") ;
			}
			
			int x = 1 ;
			while (x != tabD1.length)
			{
				if (tabD1[x] != tabD2[x])
		  	toUnify.add(tabD1[x], tabD2[x]) ;
		  	x ++ ;
		  }						
			continue ;
		}		
		
		if (! listITabDWithTwoIExpr.isEmpty())
		{
			int iTabD = listITabDWithTwoIExpr.first() ;
			
			//listIEqForITabD.print(iTabD) ;

			
			int[] iEqs = listIEqForITabD.firstTwo(iTabD) ;
			
			//System.out.println(iEqs[0] + " === " + iEqs[1]) ;

						
		  int iExpr1 = tabIExpr[iEqs[0]] ;
		  int iExpr2 = tabIExpr[iEqs[1]] ;
		  
		  //System.out.println(iExpr1 + " === " + iExpr2) ;

		  
			toUnify.add(iExpr1, iExpr2) ;						
		}		
	 } 
   
	}
	
	int[] substitute(int[] tabD, int iExpr1, int iExpr2)
	{
		int[] tabN = new int[tabD.length] ;
		tabN[0] = tabD[0] ;
		int x = 1 ;
		while (x != tabD.length)
		{
			if (tabD[x] == iExpr2)
				tabN[x] = iExpr1 ;
			else
				tabN[x] = tabD[x] ;
			x ++ ;
		}
		return tabN ;
	}
	
	void substitute(int iExpr1, int iExpr2)
	// We substitute iExpr2 by iExpr1 "everywhere"
	// i.e. in every equation.
	// The identifiers of the old equations are removed from
	// hashEq, nextIEq, listIexpr, listIEqForIExpr, listIEqHasIExprAtX
	// The identifiers of the new equations are added to
	// hashEq, nextIEq, listIexpr, listIEqForIExpr, listIEqHasIExprAtX
	// The lists listITabDWithTwoIExpr, listIExprWithTwoITabD
	// are also updated accordingly
	{
		// We substitute in equations iExpr2 = ...
		{
		  while (! listIEqForIExpr.isEmpty(iExpr2))
		  {
		  	int iEq = listIEqForIExpr.choose(iExpr2) ;
		  	int iTabD = tabITabD[iEq] ;
			  int[] tabD = ttabD[iTabD] ;
			  removeEq(iEq) ;
			  int[] tabN = substitute(tabD, iExpr1, iExpr2) ;
        addEq(iExpr1, tabN) ;
		  }
		}
		
		int x = 1 ;
		while (x != listIEqHasIExprAtX.length)
		{
		  while (! listIEqHasIExprAtX[x].isEmpty(iExpr2))
		  {
		  	int iEq = listIEqHasIExprAtX[x].choose(iExpr2) ;
		  	int iTabD = tabITabD[iEq] ;
		  	int iExpr = tabIExpr[iEq] ;
			  int[] tabD = ttabD[iTabD] ;
			  removeEq(iEq) ;
			  int[] tabN = substitute(tabD, iExpr1, iExpr2) ;
			  addEq(iExpr, tabN) ;
		  }
		  x ++ ;
		}	
	}
	

	
}