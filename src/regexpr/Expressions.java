package regexpr;
import syntax.* ;

public class Expressions implements IExpressions{
	
	/*
	
	Une instance de cette classe représente un ensemble d'expressions
	régulières normalisées.
	
	Une expression est identifiée par un entier iExpr
	
	*/
	
	Fold fold ;
	MultiList idList ;

	
	int memorySize ;
	int[][] tabNexpr ; // Sous-expressions directes
	int nbrLetters = 2 ;
	byte[] type ;
	int[] tabCode ; // tableau des hash-codes
	int[][] tabEq ;
	long[] dejaVu ;
	int[] toDerive ;
	int[] pTab ;
	boolean[] hasDFA ;
	boolean[] hasMDFA ;
	long magicNumber = 0 ;
	
	
	public void setMemorySize(int x)
	{
		memorySize  = x ;
	}
		
	
	public void setNbrLetters(int x)
	{
		nbrLetters  = x ;
	}
	
	public int nbrLetters()
	{
		return nbrLetters ;
	}
	
	public void setToDerive()
	{
		if (toDerive == null)
			toDerive = new int[memorySize] ;
	}	
	
	public int[] getToDerive()
	{
		setToDerive() ;
		return toDerive ;
	}
		
	public void setDejaVu()
	{
		if (dejaVu == null)
			dejaVu = new long[memorySize] ;
	}	
	
	public long[] getDejaVu()
	{
		setDejaVu() ;
		return dejaVu ;
	}
		public void setPTab()
	{
		if (pTab == null)
			pTab = new int[memorySize] ;
	}	
	
	public int[] getPTab()
	{
		setPTab() ;
		return pTab ;
	}
	
	public void setHasDFA()
	{
		if (hasDFA == null)
			hasDFA = new boolean[memorySize] ;
	}	
	
	public boolean[] getHasDFA()
	{
		setHasDFA() ;
		return hasDFA ;
	}
		
	
	
	public long magicNumber()
	{
		return magicNumber ;
	}	
	
	public long newMagicNumber()
	{	
		++ magicNumber ;
		if (magicNumber < 0)
			throw new Error("Catastrophe !") ;
		return magicNumber ;
	}
	
	MultiList hashTable ; // bucket et hashTable
	int firstPosInHashTable ; // ???

	//short[] tags ; plus tard...
	// let x an integer, written b_l ... b_0
	// we write x{i} to denote b_i (l >= i >= 0)
	// tags[iExpr]{0} = 1 if 1 belongs to L(iExpr)
	//                = 0 otherwise
	
  public TwoList iExprList ; // Liste des identifiants utilisés
		
	
  char[] actualLetters ;
  public void setActualLetters(char[] a) 
  {
  	actualLetters = a ;
  }
  
  static public char[] normalLetters ;  
  static {
  	normalLetters = new char[256];
		{
	  	int c =  1 ;
		  while (c != 256)
			{
				normalLetters[c] = (char) (c ++) ;
		  }
	  }	
  }
  
  
  public Expressions(final int memorySize, int nbrLetters)
  {
  	this(memorySize, normalLetters, nbrLetters) ;
	}
  
  
	public Expressions(final int memorySize, char[] actualLetters,
		 int nbrLetters)
	{
		this.memorySize = memorySize ;
	  this.actualLetters = actualLetters ;
	  this.nbrLetters = nbrLetters ;

		reinit() ;
	
	  fold = new Fold(this) ;
	}
	
	public void reinit()
	{
		
		tabNexpr = new int[memorySize][] ;
		type = new byte[memorySize] ;
		tabCode = new int[memorySize] ;
		iExprList = new TwoList(memorySize) ;
		hashTable = new MultiList(memorySize, memorySize);
		idList = new MultiList(NBRTYPES, memorySize);
		tabEq = null ;
		dejaVu = null ;
		toDerive = null ;
		
		
		
		type[ZERO] = ZERO ;
	  tabCode[ZERO] = ZERO ;
	  tabNexpr[ZERO] = new int[]{} ;
	  // Very special value : never used as a subexpr...
	  // Not true (because of set operations)
	  

		type[ONE] = ONE ;
	  tabNexpr[ONE] = new int[]{} ;
	  tabCode[ONE] = ONE ;
    iExprList.add(ONE) ;
		
		{
			int l = 'a' ;
			while (l != 'z' + 1)
			{
				int iExpr = l - 'a' + 2 ;
				tabNexpr[iExpr] = new int[]{} ;
	      tabCode[iExpr] = iExpr ;
				type[iExpr] = LETTER ;
				iExprList.add(iExpr) ;
				l ++ ;
			}				
			firstPosInHashTable = l - 'a' + 2;
		}
		
		//iExprList.print() ;
		hasDFA = new boolean[memorySize] ;
	}

	
  public int fold(int[] tabE) throws GCException
  {
  	return fold.fold(tabE) ;
  }
  
  public int fold(int iExpr) throws GCException
  {
  	return fold.fold(iExpr) ;
  }

  public int[] foldTabD(int[] tabD) throws GCException
  {
  	int[] tabDF = new int[tabD.length] ;
  	tabDF[0] = tabD[0] ;
  	int x = 1 ;
  	while (x != tabD.length)
  	{
  		tabDF[x] = fold(tabD[x]) ;
  		x ++ ;
  	}
  	
  	return tabDF ;
  }
 
  public int leftDistribute(int iLeft, int iRight) throws GCException
 	{
 		int[] tabE = tabE(iRight) ;
 		int[] tabDist = new int[tabE.length] ;
 		
 		int i = 0 ;
 		while (i != tabE.length)
 		{
 			tabDist[i] = concat(iLeft, tabE[i]) ;
 			i ++ ;
 		}
 		
 		return union(tabDist) ; 		
 	}
  
  public void unify(int iExpr1, int iExpr2)  throws GCException
  {
  	
  }
  
  public int[] tabE(int iExpr)
  // Let E = E1 + ... + En (n >= 0)
  // we return {E1, ..., En}
  {
  	if (type(iExpr) == UNION)
  		return tabNexpr[iExpr] ;
  	
  	if (type(iExpr) == ZERO)
  	  return new int[]{} ;
  	  
  	return new int[]{iExpr} ; 	
  }
  
  public int[] tabS(int iExpr)
  // Subexpressions of Expr
  {
  	return tabNexpr[iExpr] ;
  }

	public boolean atom(int iExpr)
	{
		return iExpr < firstPosInHashTable ;
	}
	
	public int memorySize()
	{
		return memorySize ;
	}
	
	int newIExpr() throws GCException
	{
		int iExpr = iExprList.choose() ;
		
		if (iExpr == - 1)
		{
			System.out.println("It's time to call GC.") ;	
			throw new GCException() ;
		}
		
		return iExpr ;
	}
	
	public int bestExpr(int iExpr)
	{
		return iExpr ;
	}
	
		
	final int BUFFERLENGTH = 10 ; // for instance
	public int newBuffer() throws GCException 
	{
		int iBuf = newIExpr() ;
		
		type[iBuf] = BUFFER ;
		int[] buf = new int[BUFFERLENGTH] ;
		tabNexpr[iBuf] = buf ;
		buf[0] = 1 ;
		
		return iBuf ;
	}
	
	public void addToBuffer(int iBuf, int v) 
	{
		int[] buf = tabNexpr[iBuf] ;
		
		if (buf[0] == buf.length)
		{
			int[] newBuf = new int[buf.length * 3 / 2] ;
			newBuf[0] = buf.length ;
			int i = 1 ;
			while (i != buf.length)
			{
				newBuf[i] = buf[i] ;
				i ++ ;
			}
			buf = newBuf ;
			tabNexpr[iBuf] = buf ;
		}
		
		//System.out.println("addToBuffer " +toString(iExpr)) ;
		
		buf[buf[0]] = v ;
		buf[0] ++ ;
	}
	
	public int[] bufferToTabE(int iBuf) 
	{
		int[] buf = tabNexpr[iBuf] ;
		int[] tabE = new int[buf[0] - 1] ;
		int i = 0 ;
		int j = 1 ;
		while (j != buf[0])
		{
			//System.out.println("bufj " +toString(buf[j])) ;
			tabE[i] = buf[j] ;
			i ++ ; j ++ ;
		}
				
		free(iBuf) ;
		
		return tabE ;
		
	}	
	
	public void printBuffer(int iBuf) 
	{
		int[] buf = tabNexpr[iBuf] ;
		System.out.print("buf : ") ;
		int j = 1 ;
		while (j != buf[0])
		{
			System.out.print(toString(buf[j]) + " ; ") ;
			j ++ ;
		}
		System.out.println() ;
		
	}	
	
	public void free(int iBuf)
	{
		tabNexpr[iBuf] = null ;
		iExprList.remove(iBuf) ;
	}
	
	public int[] bufferToTabEAndReinit(int iBuf) 
	{
		int[] buf = tabNexpr[iBuf] ;
		int[] tabE = new int[buf[0] - 1] ;
		int i = 0 ;
		int j = 1 ;
		while (i != tabE.length)
		{
			//System.out.println("bufj " +toString(buf[j])) ;
			tabE[i] = buf[j] ;
			i ++ ; j ++ ;
		}
		
		buf[0] = 1 ;
		
		return tabE ;
		
	}


	
	//-----------------------------------------------------------------
	
	
	public boolean hasOne(int iExpr) //throws GCException
  {
  	 	
  	if (type(iExpr) == ONE)
  		return true ;
  	
  	if (atom(iExpr))
  		return false ;
  	 	
    if (type(iExpr) == STAR)
  		return true ;
	
  	if (type(iExpr) == UNION)
  	{
  		
  		int[] tabE = tabNexpr[iExpr] ;
  		int i = 0 ;
  		boolean hasOne = false ;
  		while (i != tabE.length && ! hasOne)
  		{
  			hasOne = hasOne(tabE[i])  ;
  			i ++ ;
  		}
  		
  		return hasOne ;
  	}  	
  	
  	if (type(iExpr) == CONCAT)
  	{
  		
  		int iA = tabNexpr[iExpr][0] ;
  		int iB = tabNexpr[iExpr][1] ;
  		boolean hasOne = hasOne(iA)  ;
  		
  		while (hasOne && type(iB) == CONCAT)
  		{
  			hasOne = hasOne(tabNexpr[iB][0])  ;
  			iB = tabNexpr[iB][1] ;
  		}
  		
  		if (hasOne)
  			hasOne = hasOne(iB)  ;  		
  		return hasOne ;
  	}
  	
  	if (type(iExpr) == DIFF)
  	{
  		int iF = tabNexpr[iExpr][0] ;
  		int iE = tabNexpr[iExpr][1] ; 
  		return hasOne(iF)
  		  && ! hasOne(iE) ;
  	}
  	
  	if (type(iExpr) == ZERO)
  		return false ;
 
  	
  	throw new Error("hasOne(int iExpr)") ;
  }
  

 

  public byte type(int iExpr)
  {
  	return type[iExpr] ;
  }

  int hashCode(int iExpr)
	{
		return tabCode[iExpr] ;			
	}
  // new
  int hashCode(byte type, int[] tabE)
  {
  	int i = 0 ;
		int hash = type ; //* 35569 ;
		while (i !=  tabE.length)
		{
			hash = hash  * 35569 + tabE[i] ; // 46999 2237
			i ++ ;
		}
		return hash ;
  }
  
  static boolean equals(int[] a, int[] b)
  {
  	if (a.length != b.length)
  		return false ;
  	
  	int i = 0 ;
  	boolean equals = true ;
  	while (i != a.length && i != b.length && equals)
  	{
  		equals = a[i] == b[i] ;
  		i ++ ;
  	}	
  	return equals ;
  }
  
  int pos(byte type, int[] iE)
  // Position of type(iE) in the bucket
  {
  	int pos = hashCode(type, iE) % memorySize() ;
		if (pos < 0)
			pos = - pos ;
		//if (pos == 0)
		//	pos = 1 ;
		
		return pos ;
  }
  
  int pos(int iExpr)
  // Position of type(iE) in the bucket
  {
  	int pos = tabCode[iExpr] % memorySize() ;
		if (pos < 0)
			pos = - pos ;
		//if (pos == 0)
		//	pos = 1 ;
		
		return pos ;
  }
  
  
  int findId(int pos, byte type, int[] iE)
  // if type(iE) exists, returns its identifier
  // return - 1, otherwise
	{
		int ind = hashTable.first(pos) ;
		
		while (ind != - 1)
		{
			if (type(ind) == type && equals(iE, tabNexpr[ind]))
				return ind ;
			ind = hashTable.next(ind) ;
		}		
		return ind ;		
	}
	

 	int[] merge(int[] a, int[] b)
	{
		int count = 0 ;
		int i = 0 ; int j = 0 ;
		while (i != a.length && j != b.length)
		{
			count ++ ;
			
			if (a[i] < b[j])
				i ++ ;
			else if (a[i] > b[j])
				j ++ ;
			else
			{ i ++;
				j ++;
			}
		}
		
		count += a.length - i + b.length - j ;
		
		if (count == a.length)
			return a ;
		
		
		if (count == b.length)
			return b ;
		
		
		int[] c = new int[count] ;
		int k = 0 ;
		i = j = 0 ;
		while (i != a.length && j != b.length)
		{
			if (a[i] < b[j])
			{	
				c[k ++] = a[i ++] ;
				
			}
			else if (a[i] > b[j])
			{	
				c[k ++] = b[j ++] ;				
			}
			else
			{ 
				c[k++] = a[i ++];
				j ++;
			}
		}
		
		while (i != a.length)
		{
			c[k ++] = a[i ++] ;
		}		
		
		while (j != b.length)
		{
			c[k ++] = b[j ++] ;
		}
		
		return c ;
	}
	
	
	int[] insert(int[] a, int x)
	{
		int pos = 0 ;
		while (pos != a.length && a[pos] < x) pos ++ ;
		
		if (pos != a.length && a[pos] == x)
			return a ;
		
		int[] b = new int[a.length + 1] ;
		int i = 0 ;
		int j = 0 ;
		while (j != b.length)
		{
			if (j == pos)
				b[j ++] = x ;
			else
				b[j ++] = a[i ++] ;
		}
		
		return b ;
	}
	
	
	public int union(final int[] iE) throws GCException
	// return iE[1] union ... union iE[n - 1]
	// but efficiently : in s log s (s : size of the whole thing.
	{
		int[][] iF = new int[iE.length][] ;
		int i = 0 ;
		while (i != iE.length)
		{
			iF[i] = tabE(iE[i]) ;
			i ++ ;
		}
		
		int[] iEF = merge(iF) ;
		
		if (iEF.length > 1)
			return makeExpr(UNION, iEF) ;
		else if (iEF.length == 1)
			return iEF[0] ;
		else 
			return zero() ;
		
	}
	
	public int union(final int iE0, final int iF0) throws GCException
	// compute an identifier for E union F
	{		
		int iE = iE0 ; int iF = iF0 ;
		
		if (type(iE) == ZERO || iE == iF)
			return iF ;
		
		if (type(iF) == ZERO)
			return iE ;
		
		if (type(iF) == UNION)
			{ int t = iE ; iE = iF ; iF = t ; }
		
		int[] tUnion ;
		
		if (type(iE) == UNION)
		{
			int[] tE = tabNexpr[iE] ;
			
			if (type(iF) == UNION)
			{
				int[] tF = tabNexpr[iF] ;
				tUnion = merge(tE, tF) ;
				
				if (tUnion == tE)
					return iE ;				
				
				if (tUnion == tF)
					return iF ;
			}
			else
			{
				tUnion = insert(tE, iF) ;
				if (tUnion == tE)
					return iE ;				
			}
		}
		else
		{
			if (iF < iE)
				{ int t = iE ; iE = iF ; iF = t ; }
			// iE < iF
			
			tUnion = new int[]{iE, iF} ;
		}
		
	  int uExpr = makeExpr(UNION, tUnion) ;

	  return uExpr ;
	} 
  
	public boolean existsExpr(byte typeE, int[] tabE)
	{
		int pos = pos(typeE, tabE) ;		
		int iExpr = findId(pos, typeE, tabE) ;
		
		return iExpr != - 1 ;
		
	}
	
	public int makeExpr(byte typeE, int[] tabE) throws GCException
	{
		int pos = pos(typeE, tabE) ;	
		
		int iExpr = findId(pos, typeE, tabE) ;
				
		if (iExpr != - 1)
		{
			return 	iExpr ;
		}
				
		iExpr = newIExpr() ;
			
		type[iExpr] = typeE ;
		hashTable.add(pos, iExpr) ;
		tabNexpr[iExpr] = tabE ;	
		tabCode[iExpr] = hashCode(typeE, tabE) ;		
		return iExpr ;
	}
	
	int[] merge(int[][] a)
	// merge subarrays of a
	{
		if (a.length == 0)
			return new int[]{} ;
		
		while (a.length != 1)
		{
			int[][] b = new int[(a.length + 1) / 2][] ;
			
			int i = 0 ;
			int j = 0 ;
			if (a.length % 2 == 1)
			{
				b[0] = a[0] ;
				i = 1 ;
				j = 1 ;
			}
			
			while (i != a.length)
			{
				b[j] = merge(a[i], a[i + 1]) ;
				i += 2 ;
				j ++ ;
			}	
			
			a = b ;
		}
		
		return a[0] ;	
	}
  
  
  public int zero(){ return ZERO ; }
	public int one(){ return ONE ; }
	
	public int iLetter(char l){ return (l - 'a' + 2) ; }
	// identifier of l	
	public char letter(int iExpr){ return (char)(iExpr - 2 + 'a') ; }
	// letter whose identifier is iExpr

  public boolean notZero(int iExpr)
  // iExpr is guaranteed not to be equivalent to 0
  // but in fact, not !
  {
  	return iExpr != zero() ;
  }
	
  
  public int op(final byte OP, final int iP, final int iS) throws GCException
  {
  	switch(OP)
  	{
  	  case DELTA : return delta(iP, iS) ;
  	  case DIFF : return diff(iP, iS) ;
  	  case INTER : return inter(iP, iS) ;
  	  
  	  default : throw new Error("Error in op : " + OP) ;
  	}
  }
  
  boolean op(final byte OP, final boolean iP, final boolean iS)
  {
  	switch(OP)
  	{
  	  case DELTA : return iP != iS ;
  	  case DIFF : return iP && ! iS ;
  	  case INTER : return iP && iS ;
  	  
  	  default : throw new Error("Error in op : " + OP) ;
  	}
  }
	
	public int delta(final int iP, final int iS) throws GCException
	// compute an identifier for the symetric difference of iP and iS
	{		
		if (iP == zero())
			return iS ;
		
		if (iS == zero())
			return iP ;
		
		if (bestExpr(iP) == bestExpr(iS))
			return 0 ;
		
		return weakOP(DELTA, iP, iS) ;			
	}	
	
	public int diff(final int iP, final int iS) throws GCException
	// compute an identifier for P\S
	{		
		if (iP == zero() || bestExpr(iP) == bestExpr(iS))
			return zero() ;
		
		if (iS == zero())
			return iP ;
		
		return weakOP(DIFF, iP, iS) ;			
	}
	
	public int inter(final int iP, final int iS) throws GCException
	// compute an identifier for P inter S
	{		
		if (bestExpr(iP) == bestExpr(iS))
			return bestExpr(iP) ;
		
		if (iP == zero() || iS == zero())
			return zero() ;
		
		return weakOP(INTER, iP, iS) ;			
	}
	
	public int weakOP(byte OP, final int iP, final int iS) throws GCException
	// 
	{						  
		final int[] tabE = new int[]{iP, iS} ;		
		return makeExpr(OP, tabE) ;
	}			
		
	public int not(final int iExpr) throws GCException
	// compute an identifier for ! E
	{	
		return makeExpr(NOT, new int[]{iExpr}) ;					
	}
	
	public int weakDiff(final int iP, final int iS) throws GCException
	// 
	{						  
		return weakOP(DIFF, iP, iS) ;
	}			
	
	public boolean existsPair(int iExpr1, int iExpr2)
	{	
		return existsExpr(PAIR, new int[]{iExpr1, iExpr2}) ;		
	}
		
	public int pair(final int iP, final int iS) throws GCException
	// 
	{						  
		final int[] tabE = new int[]{iP, iS} ;		
		int iPair = makeExpr(PAIR, tabE) ;
		idList.add(PAIR, iPair) ;
		return iPair ;				
  }
  
  public void removePair(int iPair)
  {
  	idList.remove(PAIR, iPair) ;
  	
  	iExprList.remove(iPair) ;
			
		int pos = pos(iPair) ;		
		hashTable.remove(pos, iPair) ;		  
		tabNexpr[iPair] = null ;			
  }
	
	public void collectPairs()
	{
		while (! idList.isEmpty(PAIR))
		{
			int iExpr = idList.choose(PAIR) ;			
			removePair(iExpr) ;
		}
	}
	
	public void collectPairs(boolean check) throws GCException
	{
		while (! idList.isEmpty(PAIR))
		{
			int iExpr = idList.choose(PAIR) ;
			
			int iExpr0 = tabNexpr[iExpr][0] ;
			int iExpr1 = tabNexpr[iExpr][1] ;
			//hasDFA[iExpr0] = true ;
			//hasDFA[iExpr1] = true ;
			
			if (check)
			{				
				unify(tabNexpr[iExpr][0], tabNexpr[iExpr][1]) ;
				//System.out.println("size0 = " + toString(iExpr0) + " " + size(iExpr0)) ;
				//System.out.println("size1 = " + toString(iExpr1) + " " + size(iExpr1)) ;
				//System.out.println("best  = " + toString(bestExpr(iExpr1)) + " " + size(bestExpr(iExpr1))) ;
			}
		
			removePair(iExpr) ;
		}
	}
	
	
	public int[] unfoldConcat(int iE)
	// Pre : E is of the form F1 . (F2 . ... (Fn-1 . Fn)...)) (n >= 0)
	// where none of the Fi are concatenation nor 0 nor 1
  // E = 1 ==> n = 0
  // we return {F1, ..., Fn} ;
	{
		if (iE == one())
			return new int[]{} ;
		
		int n = 1 ; 
		int iS = iE ;
		while (type(iS) == CONCAT)
		{
			n ++ ;
			iS = tabNexpr[iS][1] ;
		}
		
		int[] tabF = new int[n] ;
		int j = 0 ;
		iS = iE ;
		while (type(iS) == CONCAT)
		{
			tabF[j] = tabNexpr[iS][0] ;
			iS = tabNexpr[iS][1] ;
			j ++ ;
		}
		tabF[j] = iS ;
		
		return tabF ;
	}
	
	public int concat(final int[] iE, final int iS) throws GCException
	// return iE[1] concat ... concat iE[n - 1] concat iS
	// no iE[i] is a concatenation
	{
		if (iE.length == 0)
			return iS ;
		
		int iC = iS ;
		int i = iE.length ;
		while (i != 0)
		{
			-- i ;
		  iC = simpleConcat(iE[i], iC) ;
		}
		return iC ;
	}
	
	public int concat(final int[] iE) throws GCException
	// return (iE[1]) concat ... concat (iE[n - 1])
	// but efficiently : in O(s) (s : size of the whole thing).
	{
		if (iE.length == 0)
			return one() ;
		
		int iC = iE[iE.length - 1] ;
		int i = iE.length - 1 ;
		while (i != 0)
		{
			-- i ;			
		  iC = concat(unfoldConcat(iE[i]), iC) ;			
		}
		return iC ;
	}
	

	
	int simpleConcat(final int iP, final int iS) throws GCException
	// compute an identifier for P.S
	// iP is not a concatenation nor 0 nor 1
	{		
		
		if (iP == one())
			return iS ;
		
		if (iS == one())
			return iP ;
		
		final int[] tabE =  new int[]{iP, iS} ;				
		return makeExpr(CONCAT, tabE) ;							
	}
	
  public int concat(final int iP, final int iS) throws GCException
	// compute an identifier for P.S
	{	
		if (iP == zero() || iS == zero())
			return zero() ;
		
		if (type(iP) == CONCAT)
		  return concat(unfoldConcat(iP), iS) ;	
		else
			return simpleConcat(iP, iS) ;
	}
	
	public int[][] removeExtremeLetters(final int iExpr) throws GCException
	// Pre : E is of the form F1 . (F2 . ... (Fn-1 . Fn)...)) (n >= 0)
	// where none of the Fi are concatenation nor 0 nor 1
	// return Fi. (F{i + 1}. (... Fj) )
	// where F1, ..., F{i - 1} and F{J + 1} ... F n are all letters.
	{
		int[] tabF = unfoldConcat(iExpr) ;
		
		int i = 0 ;
		while (i != tabF.length && type(tabF[i]) == Expressions.LETTER)
		  i ++ ;
		
		int j = tabF.length ;
		while (j != i && type(tabF[j - 1]) == Expressions.LETTER)
		  j -- ;
		
		int[] tabP = new int[i] ;
		int p = 0 ;
		while (p != i)
		{
			tabP[p] = tabF[p] ;
		  p ++ ;
		}
		
		int le = j - i ;
		int[] tabG = new int[le] ;
		int k = 0 ;
		while (k != le)
			tabG[k ++] = tabF[i ++] ;
		
		int[] tabS = new int[tabF.length - j] ;
		int s = tabS.length ;
		    j = tabF.length ;
		while (s != 0)
		{
			tabS[-- s] = tabF[-- j] ;
		}	
		
		return new int[][]{tabP, tabG, tabS} ;
		
		
		
	  //return concat(tabG, one()) ;
		
	}


  public int star(final int iExpr) throws GCException
	// compute an identifier for E*
	{
		if (iExpr == zero() || iExpr == one())
			return one() ;
			
		if (type(iExpr) == STAR)
			return iExpr ;
		
		int r = makeExpr(STAR, new int[]{iExpr}) ;		
		return r ;
	}
	
	public int[] addEquation(int iExpr, int[] tabD) throws GCException 
	{
		if (tabEq == null)
			tabEq = new int[memorySize()][] ;
		
		tabEq[iExpr] = tabD ;
		return tabD ;
	}
	
	public int[] exprToTabD(int iExpr)
	{
		if (tabEq == null)
			tabEq = new int[memorySize()][] ;
			
		return tabEq[iExpr] ;
	}	
	
	public int[] exprToTabDF(int iExpr)
	{
		if (tabEq == null)
			tabEq = new int[memorySize()][] ;
			
		return tabEq[iExpr] ;
	}
	
	
	
	public int[] derivByUnfold(int iExpr) throws GCException
  {		             		
  	int[] tabD = new int[nbrLetters + 1] ;
  	
  	boolean one = derivByUnfold(tabD, iExpr, one(), 0) ;
  	
  	if (one) 
  		tabD[0] = one() ;
  	else
  		tabD[0] = zero() ;
  		  	    
  	return tabD ;
  }
  
  boolean derivByUnfold(int[] tabD, int iExpr, int iR, int level) throws GCException
  // Ajouter à tabD concat ((D_x iExpr), iR) tout x
  // renvoyer o(E)
  { 	
  	
  	switch (type(iExpr))
  	{
  	  case Expressions.ZERO :
  	  	return  false ;
  	  	
   	  case Expressions.ONE :  	  	
  	  	return true ;
  	  	
     	case Expressions.LETTER : 
     		{
     			int i = iExpr - 1 ;
     			
 		  	  tabD[i] = union(tabD[i], iR) ;
 		  	}
 		  	return false ;
 		  	
 		  case Expressions.UNION :	
 		  	{
 		  		int[] tabE = tabNexpr[iExpr] ;
 		  	  boolean one = false ;
 		  		int i = 0 ;
 		  		while (i != tabE.length)
 		  		{
 		  			one |= derivByUnfold(tabD, tabE[i], iR, level + 1) ;
 		  			i ++ ;
 		  		}
 		  		return one ;
 		  	}
 		  	
   		case Expressions.CONCAT :	
   			{
   				int iP = tabNexpr[iExpr][0] ;
 		  	  int iS = tabNexpr[iExpr][1] ;		
 		  	  boolean one ;		  	  
 		  	  one = derivByUnfold(tabD, iP, concat(iS, iR), level + 1) ;
 		  	  if (one)
 		  	  one = derivByUnfold(tabD, iS, iR, level + 1) ;
 		  	  return one ;
 		  	}
  		 		  	
   		case Expressions.STAR :	
 		  	int iSub = tabNexpr[iExpr][0] ; 		  	
 		  	derivByUnfold(tabD, iSub, concat(iExpr, iR), level + 1) ;
 		  	return true ;
 		  	
 		  case Expressions.DELTA :
 		  case Expressions.DIFF :
 		  case Expressions.INTER :
 		  	{
 		  		byte OP = type(iExpr) ;
 		  		
 		  		int iE = tabNexpr[iExpr][0] ;
 		  		int iF = tabNexpr[iExpr][1] ;
 		  		
 		  		int[] tabE = new int[tabD.length] ; 		  		
 		  		boolean oneE = derivByUnfold(tabE, iE, one(), level + 1) ;
 		  		
 		  		int[] tabF = new int[tabD.length] ;
 		  		boolean oneF = derivByUnfold(tabF, iF, one(), level + 1) ;
 		  		 		  		
 		  		{
 		  			int i = 0 ;
 		  		  while (i != tabD.length)
 		  		  {
 		  			  tabD[i] = fold(union(tabD[i],
 		  			  	concat(op(OP, tabE[i], tabF[i]), iR))) ;		  			  
 		  			  i ++ ;
 		  		  }		  		  
 		  		}
 		  		
		  		return op(OP, oneE, oneF) && hasOne(iR);		  		
 		  	}
 		  	
 		  case Expressions.NOT :
 		  	{
 		  		
 		  		int iE = tabNexpr[iExpr][0] ;
 		  		
 		  		int[] tabE = new int[tabD.length] ; 		  		
 		  		boolean oneE = derivByUnfold(tabE, iE, one(), level + 1) ;
 		  		
 		  		{
 		  			int i = 0 ;
 		  		  while (i != tabD.length)
 		  		  {
 		  			  tabD[i] = fold(union(tabD[i],
 		  			  	concat(not(tabE[i]), iR))) ;		  			  
 		  			  i ++ ;
 		  		  }		  		  
 		  		} 		  		
		  		return ! oneE ;		 		  		
 		  	}
 		  	
 		  default : throw new Error("derivByUnfold " + toString(iExpr)) ;
  		
  	}  	
  }
  
  //boolean existsTabD ;
  //public boolean existsTabD()
  //{
  //	return existsTabD ;
  //}
  
  final public int[] tabD(int iExpr) throws GCException
	{
		int[] tabD = exprToTabD(iExpr) ; //exprToTabDF
		if (tabD == null)
		{
			//System.out.println("tabD") ;
			tabD = (computeTabDeriv(iExpr)) ;
		}		
		return tabD ;
	}  
	
	//public boolean tabDHasExpr(int[] tabD)
	//{
	//	return false ;
	//} 
	
	int[] bestify(int[] tabD)
	{
		return tabD ;
	}
	
	public int tabDToIExpr(int[] tabD) throws GCException
	// On convertit tabD en l'expression correspondante ;
	{
		int[] tE = new int[tabD.length] ;
		tE[0] = tabD[0] ;
		
		int x = 0 ;
		while (x != tabD.length)
		{
			tE[x] = concat(iLetter((char)('a' + x - 1)), tabD[x]) ;
			x ++ ;
		}
		
		return bestExpr(fold(tE)) ;
	}
	
	int nbrPartialDerivatives ;
	public int nbrPartialDerivatives() 
	{
		return nbrPartialDerivatives ;
	}
	

	public int[] computeTabDeriv(int iExpr) throws GCException
	{
		int[] tabD = new int[nbrLetters + 1] ;    
		tabD[0] = zero() ;
    int[] tabE = tabE(iExpr) ;
    int[][] tabDE = new int[tabE.length][] ;
    {
    		int i = 0 ;
    		while (i != tabE.length)                                                       
    		{
    			int[] tabDEi = exprToTabD(tabE[i]) ; //exprToTabDF
    			if (tabDEi == null || tabDEi.length != nbrLetters + 1)   			  	
    			{	  
    			   tabDEi = derivByUnfold(tabE[i]) ;
    			   nbrPartialDerivatives ++ ;
    				 tabDEi = addEquation(tabE[i], tabDEi) ;
    			}
    			
    			tabDE[i] = tabDEi ;//exprToTabD(tabE[i]) ; //tabDEi ;
    				  
    			if (tabDEi[0] == one())
    				tabD[0] = one() ;
    			i ++ ;
    		}  			
     }

    {               
    	  int x = 1 ;
    		while (x != tabD.length)
    		{
    			int[] iDx = new int[tabE.length] ;
    			int i = 0 ;
    			
    			while (i != tabE.length)
    			{
            iDx[i] = tabDE[i][x] ;  	
    				i ++ ;
    			}
   			  tabD[x] = (union(iDx)) ;
    			x ++ ;
    		}
    } 
    
     tabD = addEquation(iExpr, tabD) ;      
     return tabD ; // exprToTabD(iExpr) ;
	}
	
	public int elimOne(int iExpr)  throws GCException
	{
		if (type(iExpr) != Expressions.UNION 
			|| ! hasOne(iExpr))
			return iExpr ;
		
		int[] tabE = tabE(iExpr) ;
		
		int[] tabR ;
		int j ;
		if (tabE[0] == one())
		{
			j = 1 ;
			tabR = new int[tabE.length - 1] ;
		}
		else
		{
			j = 0 ;
			tabR = new int[tabE.length] ;
		}
			
		
		boolean bingo = false ;
		{
			int i = 0 ;
			while (j != tabE.length)
			{
				int iExprj = tabE[j] ;
				tabR[i] = iExprj ;
				if (type(iExprj) == Expressions.CONCAT)
				{
					int iA = tabS(iExprj)[0] ;
					int iAS = tabS(iExprj)[1] ;
					if (iAS == star(iA))
					{
						bingo = true ;
						tabR[i] = iAS ;
					}
				}			
				i ++ ;
				j ++ ;
			}
		}
		
		if (bingo)
			return union(tabR) ;
		else
			return iExpr ;
	}
	
	
  
  public int subsumeTest(int iExpr1, int iExpr2)
  // E1 = E11 + ... + E1n1 (n1 >= 0)
  // E2 = E21 + ... + E2n2 (n2 >= 0)
  // On renvoie n1 + n2
  // n1 = 1 si {E11, ... , E1n1} subsetof {E21, ... , E2n2}
  // n1 = 0 sinon
  // n2 = 2 si {E21, ... , E2n2} subsetof {E11, ... , E1n1}
  // n2 = 0 sinon
  { 
     if (iExpr1 == iExpr2)
     	 return 3 ;
     
     if (iExpr1 == zero())
     	 return 1 ;
     
     if (iExpr2 == zero())
     	 return 2 ;
     
  	 int[] tab1 = tabE(iExpr1) ;
  	 int[] tab2 = tabE(iExpr2) ;
  	 
  	 int n1 = 1 ;
  	 int n2 = 2 ;
  	 int i1 = 0 ;
  	 int i2 = 0 ;
  	 while (i1 != tab1.length && i2 != tab2.length
  	 	      && !(n1 == 0 && n2 == 0))
  	 {
  	 	 if (tab1[i1] < tab2[i2])
  	 	 {
  	 	 	 n1 = 0 ; i1 ++ ;
  	 	 }
  	 	 else if (tab1[i1] > tab2[i2])
  	 	 {
  	 	 	 n2 = 0 ; i2 ++ ;  	 	 	
  	 	 }
  	 	 else
  	 	 {
  	 	 	 i1 ++ ; i2 ++ ;
  	 	 }
  	 }
  	 
  	 if (n1 == 1 && i1 != tab1.length)
  	 	 n1 = 0 ;  	 
  	 
  	 if (n2 == 2 && i2 != tab2.length)
  	 	 n2 = 0 ;
  	 
  	 return n1 + n2 ; 	 
  }
  
  public boolean isSubsumed(int iExpr1, int iExpr2)
  {
  	boolean isSubsumed ;
  	if (iExpr1 == iExpr2)
  		isSubsumed = true ;
  	else
  	if (tabE(iExpr1).length >= tabE(iExpr2).length)
  		isSubsumed = false ;
  	else
  	  isSubsumed = subsumeTest(iExpr1, iExpr2) == 1 ;
 	
  	return isSubsumed ;
  }
  
  int diffInfEq(int iExpr1, int iExpr2) throws GCException
  // E1 = E11 + ... + E1n1 (n1 >= 0)
  // E2 = E21 + ... + E2n2 (n2 >= 0)
  // {E11, ..., E1n1} \subsetEq {E21, ..., E2n}
  // On renvoie union({E21, ..., E2n2}\{E11, ..., E1n1})
  {
  	if (iExpr1 == zero())
  		return iExpr2 ;
  	
  	 int[] tab1 = tabE(iExpr1) ;
  	 int[] tab2 = tabE(iExpr2) ;
  	 
  	 if (tab1.length == tab2.length)
  	 	 return zero() ;
  	 
  	 int[] tabDiff = new int[tab2.length - tab1.length] ;
  	 
  	 { 
  	 	 int i1 = 0 ;
  	   int i2 = 0 ;
  	   int i = 0 ;
  	   while (i1 != tab1.length && i2 != tab2.length)
  	   {
  	 	   if (tab1[i1] == tab2[i2])
  	 	   {  	 	   	 
  	 	 	   i1 ++ ; i2 ++ ;
  	 	   }
  	 	   else 
  	 	   {
  	 	   	 tabDiff[i] = tab2[i2] ;
  	 	 	   i ++ ; i2 ++ ;  	 	 	
  	 	   }
  	 	 }
  	 	 
  	 	 while (i2 != tab2.length)
  	   {
  	 	   	 tabDiff[i] = tab2[i2] ;
  	 	 	   i ++ ; i2 ++ ;  	 	 	
  	 	 }
  	 } 	 
  	 
  	 if (tabDiff.length == 1)
  	 	 return tabDiff[0] ;
  	 
  	 return makeExpr(UNION, tabDiff) ; 
  }
  
  
  
  int diffUnion(int iExpr1, int iExpr2) throws GCException
  // E1 = E11 + ... + E1n1 (n1 >= 0)
  // E2 = E21 + ... + E2n2 (n2 >= 0)
  // On renvoie union({E11, ..., E1n1}\{E21, ..., E2n2})
  {
  	 if (iExpr2 == zero())
  		return iExpr1 ;
  	
  	 int[] tab1 = tabE(iExpr1) ;
  	 int[] tab2 = tabE(iExpr2) ;
  	 

  	 int n = 0 ;
  	 { int i1 = 0 ;
  	   int i2 = 0 ;
  	   while (i1 != tab1.length && i2 != tab2.length)
  	   {
  	 	   if (tab1[i1] < tab2[i2])
  	 	   {
  	 	   	 n ++ ;
  	 	 	   i1 ++ ;
  	 	   }
  	 	   else if (tab1[i1] > tab2[i2])
  	 	   {
  	 	 	   i2 ++ ;  	 	 	
  	 	   }
  	 	   else
  	 	   {
  	 	 	 i1 ++ ; i2 ++ ;
  	 	   }
  	 	 }
  	 
  	   while (i1 != tab1.length)
  	   {
  	 	 	 n ++ ;
  	 	 	 i1 ++ ; 	
  	 	 }
  	 }
  	 
  	 if (n == 0)
  	 	 return zero() ;
  	 
  	 int[] tabD = new int[n] ;
  	 int i = 0 ;
  	 
  	 { 
  	 	 int i1 = 0 ;
  	   int i2 = 0 ;
  	   while (i1 != tab1.length && i2 != tab2.length)
  	   {
  	 	   if (tab1[i1] < tab2[i2])
  	 	   {
  	 	   	 tabD[i ++] = tab1[i1] ;
  	 	 	   i1 ++ ;
  	 	   }
  	 	   else if (tab1[i1] > tab2[i2])
  	 	   {
  	 	 	   i2 ++ ;  	 	 	
  	 	   }
  	 	   else
  	 	   {
  	 	 	 i1 ++ ; i2 ++ ;
  	 	   }
  	 	 }
  	 
  	 
  	   while (i1 != tab1.length)
  	   {
  	 	 	  tabD[i ++] = tab1[i1] ;
  	 	 	  i1 ++ ;
  	   }
  	 }
  	 
  	 if (n == 1)
  	 	 return tabD[0] ;
  	 
  	 int iDiff = makeExpr(UNION, tabD) ; 	
  	 
  	 return iDiff ; 	 
  }
	  
  int[] diffUnionNew(int iExpr1, int iExpr2) throws GCException
  // E1 = E11 + ... + E1n1 (n1 >= 0)
  // E2 = E21 + ... + E2n2 (n2 >= 0)
  // On renvoie {E11, ..., E1n1}\{E21, ..., E2n2}
  {
  	
  	 int[] tab1 = tabE(iExpr1) ;
  	 int[] tab2 = tabE(iExpr2) ;
  	 

  	 int n = 0 ;
  	 { int i1 = 0 ;
  	   int i2 = 0 ;
  	   while (i1 != tab1.length && i2 != tab2.length)
  	   {
  	 	   if (tab1[i1] < tab2[i2])
  	 	   {
  	 	   	 n ++ ;
  	 	 	   i1 ++ ;
  	 	   }
  	 	   else if (tab1[i1] > tab2[i2])
  	 	   {
  	 	 	   i2 ++ ;  	 	 	
  	 	   }
  	 	   else
  	 	   {
  	 	 	 i1 ++ ; i2 ++ ;
  	 	   }
  	 	 }
  	 
  	   while (i1 != tab1.length)
  	   {
  	 	 	 n ++ ;
  	 	 	 i1 ++ ; 	
  	 	 }
  	 }
  	 
  	 
  	 int[] tabD = new int[n] ;
  	 int i = 0 ;
  	 
  	 { 
  	 	 int i1 = 0 ;
  	   int i2 = 0 ;
  	   while (i1 != tab1.length && i2 != tab2.length)
  	   {
  	 	   if (tab1[i1] < tab2[i2])
  	 	   {
  	 	   	 tabD[i ++] = tab1[i1] ;
  	 	 	   i1 ++ ;
  	 	   }
  	 	   else if (tab1[i1] > tab2[i2])
  	 	   {
  	 	 	   i2 ++ ;  	 	 	
  	 	   }
  	 	   else
  	 	   {
  	 	 	 i1 ++ ; i2 ++ ;
  	 	   }
  	 	 }
  	 
  	 
  	   while (i1 != tab1.length)
  	   {
  	 	 	  tabD[i ++] = tab1[i1] ;
  	 	 	  i1 ++ ;
  	   }
  	 }
  	   	 
  	 return tabD ;
  }
  
  public String toIdList(int iE)
	// 
	{
		  	  
		switch (type(iE))
		{
			case LETTER : return "" + actualLetters[letter(iE)] ;
	    case ZERO : return "0" ;
	    case ONE : return "1" ;
	    case UNION : {
	    		int[] tIE = tabNexpr[iE] ;
		      String SUnion = "" + tIE[0] ;
		      int i = 1 ;
		      while (i != tIE.length)
		      {
			     SUnion += " + " + tIE[i] ;
			     i ++ ;
		      }
		       return SUnion ;
       	}
	    case CONCAT : 
	    case DELTA : 
	    case DIFF : 
	    case INTER : 
	    case NOT : 
      case STAR : return "" + iE ;
		}
		return "[Identifiant incorrect]" ;
	}  
	
	
	public long size(int iExpr)
	// The length of iExpr
	{
		switch (type(iExpr))
		{
		  case ZERO : 
		  case ONE : return 0 ;
		  case LETTER : return 1 ;
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
		  		return length ;
		  	}
			case CONCAT :
				{
					int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					long length = size(iP) + size(iS) + 1 ;
					
					return length ;
				}
				
		  case STAR :
			  {
					int iSub = tabNexpr[iExpr][0] ;	
					long length = size(iSub) + 1 ;
					return length ;
				}
				
			case NOT :
			  {
					int iSub = tabNexpr[iExpr][0] ;	
					long length = size(iSub) + BEAUCOUP ;
					return length ;
				}	
				
			case DIFF :
			case INTER :
			case DELTA :
				{
					int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					return stringSize(iP) + stringSize(iS) + BEAUCOUP ;
		    }  
				
			case BOX :
				{
					int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					return stringSize(iP) + stringSize(iS) + 1 ;
		    }  
		  default : throw new Error("unkown type " + iExpr + " [size]") ;
		}
	}	
	
	
	public long stringSize(int iExpr)
	// The length of a string representing iExpr
	{
		switch (type(iExpr))
		{
		  case ZERO : 
		  case ONE : 
		  case LETTER : return 1 ;
		  case UNION :
		  	{
		  		int[] tabE = tabE(iExpr) ;
		  		int i = 0 ;
		  		long length = 3 * (tabE.length - 1) ;
		  		while (i != tabE.length)
		  		{
		  			length += stringSize(tabE[i]) ;
		  			i ++ ;
		  		}
		  		return length ;
		  	}
			case CONCAT :
				{
					int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					long length = stringSize(iP) + stringSize(iS) ;
					if (type(iP) == UNION)
						length += 2 ;
					if (type(iS) == UNION)
						length += 2 ;
					return length ;
				}
				
		  case STAR :
			  {
					int iSub = tabNexpr[iExpr][0] ;	
					long length = stringSize(iSub) + 1 ;
					if (type(iSub) == UNION || type(iSub) == CONCAT)
						length += 2 ;
					return length ;
				}
				
			case NOT :
			  {
					int iSub = tabNexpr[iExpr][0] ;	
					long length = stringSize(iSub) + 1 ;
					
					if (type(iSub) != LETTER &&
						  type(iSub) != ONE && 
						  type(iSub) != ZERO)
						length += 2 ;
					
					return length ;

				}	
				
			case DIFF :
			case INTER :
			case DELTA :
				{
				  int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					return stringSize(iP) + stringSize(iS) + 5 ;
		    }  
				
				
			case BOX :
				{
				  int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					return stringSize(iP) + stringSize(iS) + 4 ;
		    }  
		  default : throw new Error("unkown type " + iExpr + " [stringSize]") ;
		}
	}
	
	char[] toStringNew(int iExpr)
	{
		int length = (int) stringSize(iExpr) ;
		char[] tabC = new char[length] ;
		toString(tabC, 0, iExpr) ;
		return tabC ;
	}
	
	public String toString(int iExpr)
	{
		return String.valueOf(toStringNew(iExpr)) ;
	}
	
	int toString(char[] tabC, int i, int iExpr)
	{
		switch (type(iExpr))
		{
		  case ZERO : tabC[i] = '0' ; return i + 1 ;
		  case ONE  : tabC[i] = '1' ; return i + 1 ; 
		  case LETTER : tabC[i] = actualLetters[letter(iExpr)] ; 
		  	  return i + 1 ;
		  	
		  case UNION :
		  	{
		  		int[] tabE = tabE(iExpr) ;
		  		
		  		i = toString(tabC, i, tabE[0]) ;
		  		
		  		int j = 1 ;		  		
		  		while (j != tabE.length)
		  		{
		  			tabC[i ++] = ' ' ;
		  			tabC[i ++] = '+' ;
		  			tabC[i ++] = ' ' ;
		  			
		  			i = toString(tabC, i, tabE[j]) ;
		  			j ++ ;
		  		}
		  		return i ;
		  	}
		  	
			case CONCAT :
				{
					int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					
					if (type(iP) == UNION)
						tabC[i ++] = '(' ;
					
					i = toString(tabC, i, iP) ;
					
					if (type(iP) == UNION)
						tabC[i ++] = ')' ;
					
					if (type(iS) == UNION)
						tabC[i ++] = '(' ;
					
					i = toString(tabC, i, iS) ;
					
					if (type(iS) == UNION)
						tabC[i ++] = ')' ;

					return i ;
				}
				
		  case STAR :
			  {
					int iSub = tabNexpr[iExpr][0] ;	
					
					if (type(iSub) == UNION || type(iSub) == CONCAT)
						tabC[i ++] = '(' ;
					
					i = toString(tabC, i, iSub) ;
					
					if (type(iSub) == UNION || type(iSub) == CONCAT)
						tabC[i ++] = ')' ;
					
					tabC[i ++] = '*' ;
					
					return i ;

				}
				
					
		  case NOT :
			  {
					int iSub = tabNexpr[iExpr][0] ;	
					
					tabC[i ++] = '!' ;
					
					if (type(iSub) != LETTER &&
						  type(iSub) != ONE && 
						  type(iSub) != ZERO)
						tabC[i ++] = '(' ;
					
					  i = toString(tabC, i, iSub) ;
					
					if (type(iSub) != LETTER &&
						  type(iSub) != ONE && 
						  type(iSub) != ZERO)
						tabC[i ++] = ')' ;
										
					return i ;

				}
				
			case DIFF :
			case INTER :
			case DELTA :
				{
					int iP = tabNexpr[iExpr][0] ;
					int iS = tabNexpr[iExpr][1] ;
					
					tabC[i ++] = '(' ;
					
          i = toString(tabC, i, iP) ;
          
          tabC[i ++] = ' ' ;
          switch (type(iExpr))
          { 
            case DIFF : tabC[i ++] = '\\' ; break ;
			      case INTER : tabC[i ++] = '&' ; break ;
			      case DELTA : tabC[i ++] = '^' ; break ;
			      default : 
			    }
          
          tabC[i ++] = ' ' ;
          
          i = toString(tabC, i, iS) ;
          
          tabC[i ++] = ')' ;
          
          return i ;
          
				}
				
			case BOX :
				{
				  int iSub = tabNexpr[iExpr][0] ;
				  
					tabC[i ++] = 'b' ;
          tabC[i ++] = 'o' ;
          tabC[i ++] = 'x' ;
          tabC[i ++] = '(' ;
          
          i = toString(tabC, i, iSub) ;
          
          tabC[i ++] = ')' ;

          return i ;         
		    }  
		  default : throw new Error("unkown type " + iExpr + " [toString]") ;
		}
	}
    
	public String typeToString(int type)
	{
		switch (type)
		{
		  case ZERO : return "ZERO" ;
		  case ONE : return "ONE" ;
		  case LETTER : return "LETTER" ;
		  case UNION : return "UNION" ;
		  case CONCAT : return "CONCAT" ;
		  case STAR : return "STAR" ;
		  case DIFF : return "DIFF" ;
		  case DELTA : return "DELTA" ;
		  case INTER : return "INTER" ;
		  case NOT : return "NOT" ;
		  case BOX : return "BOX" ;
		}
		
		throw new Error("[Error in typeToString] : " + type) ;
	}
	
	public String toStringType(int iExpr)
	{
		return typeToString(type(iExpr)) ;
	}
	

  public void printStatsHashTable()
  {
  	int[] stats = new int[memorySize] ;
  	
  	int ind = 0 ;
  	while (ind != memorySize)
  	{
  		stats[hashTable.length(ind)] ++ ;
  		ind ++ ;
  	}
  	
  	int i = 0 ;
  	while (i != stats.length)
  	{
  		if (stats[i] != 0)
  		System.out.println("stats[" + i + "] = " + stats[i]) ;
  		i ++ ;
  	}
  }


	public void printAll() 
	{
		System.out.println("tabNexpr    -     tabCode") ;
		System.out.println("=========================") ;
		int i = iExprList.first() ;
		while (i != - 1)
		{
			int iExpr = iExprList.val(i) ;
			System.out.println(iExpr  + " " +
				tabCode[iExpr] + " " +
				toString(iExpr)) ;
			i = iExprList.next(i) ;
		}	
		
	}

	public void printAllExprs()
	{
		int iExpr = iExprList.first() ;
		while (iExpr != - 1)
		{
			int nextIExpr = iExprList.next(iExpr) ;
			
		  System.out.println(iExpr + " " + toString(iExpr)) ;
		  
		  iExpr = nextIExpr ;			
		}
	}
	
	public void checkHashCode(String msg)
	{
		int iExpr = iExprList.first() ;
		while (iExpr != - 1)
		{
			
			if (!atom(iExpr) && hashCode(iExpr) != 
				hashCode(type(iExpr), tabNexpr[iExpr]))
			throw new Error(iExpr + " " + toString(iExpr) + " " + msg) ;
			
			iExpr = iExprList.next(iExpr) ;
		}
	}
	
	public void checkExpressions(String msg)
	{
		boolean[] dejaVu = new boolean[tabNexpr.length] ;
		
		int iExpr = iExprList.first() ;
		while (iExpr != - 1)
		{
			
			int[] tabE = tabNexpr[iExpr] ;
			
			switch (type(iExpr))
			{
					case STAR :
						if (tabE.length != 1 || type(tabE[0]) == STAR)
							throw new Error("checkExpressions STAR " + toString(iExpr)) ;
						break ;
						
					case CONCAT :						
						if (tabE.length != 2 || type(tabE[0]) == CONCAT)
							throw new Error("checkExpressions CONCAT " + toString(iExpr)) ;
						break ;
						
				  case DIFF :						
						if (tabE.length != 2 || type(tabE[0]) == ZERO || 
							type(tabE[1]) == ZERO)
							throw new Error("checkExpressions DIFF " + toString(iExpr)) ;
						break ;
						
					case UNION :
						if (tabE.length < 2)
							throw new Error("checkExpressions UNION " + toString(iExpr)) ;
						int i = 0 ;
						while (i != tabE.length)
						{
							if (type(tabE[i]) == UNION)
							throw new Error("checkExpressions UNION " + toString(iExpr)) ;
						  i ++ ;
						}
						break ;
						
				default : ;
												
			}
			
			//checkLoop(iExpr, dejaVu) ;
			
			iExpr = iExprList.next(iExpr) ;
		}
	}
		
	public int toExpression(Term t) throws GCException
	{
		return toExpression(new Term[]{t})[0] ;
	}	
	
		
	public int[] toExpression(Term[] t) throws GCException
	{
		Term.computeActualLetters(t, (char)('a' + nbrLetters() - 1)) ;
		setActualLetters(Term.actualLetters()) ;
		
		int[] TIExpr = new int[t.length] ; 
		
		int i = 0 ;
		while (i != t.length)
		{
			TIExpr[i] = t[i].toExpression(this) ;
			i ++ ;
		}
		
		return TIExpr ;
	}	
	

	public static int[] sort(int[] a)
	{
		//int[] r = new int[a.length] ;
		
		int i = 0 ;
		// trié jusque i - 1
		while (i != a.length)
		{
			int x = a[i] ; 
			int j = i - 1 ;
			while (j != - 1 && a[j] > x)
			{
				a[j + 1] = a[j] ;
				j -- ;
			}
			a[j + 1] = x ;
			
			i ++ ;
		}
		return a ;
	}
	
	public static int[] sort(int[] a, long[] size)
	{
		//int[] r = new int[a.length] ;
		
		int i = 0 ;
		// trié jusque i - 1
		while (i != a.length)
		{
			int x = a[i] ; 
			int j = i - 1 ;
			while (j != - 1 && size[a[j]] > size[x])
			{
				a[j + 1] = a[j] ;
				j -- ;
			}
			a[j + 1] = x ;
			
			i ++ ;
		}
		return a ;
	}
	
		
	//-------- Stoughton ----------------------------
	
  boolean[] tf = null ; 
  
  public boolean hasL(int iExpr, int ix) //throws GCException
  // x is a letter
  {
  	 	
  	if (type(iExpr) == ONE)
  		return false ;
  	
  	if (type(iExpr) == LETTER)
  		return ix == iExpr ;
  	 	
    if (type(iExpr) == STAR)
  		return hasL(tabNexpr[iExpr][0], ix) ;
	
  	if (type(iExpr) == UNION)
  	{
  		
  		int[] tabE = tabNexpr[iExpr] ;
  		int i = 0 ;
  		boolean hasL = false ;
  		while (i != tabE.length && ! hasL)
  		{
  			hasL = hasL(tabE[i], ix)  ;
  			i ++ ;
  		}
  		
  		return hasL ;
  	}  	
  	
  	if (type(iExpr) == CONCAT)
  	{		
  		int iF = tabNexpr[iExpr][0] ;
  		int iE = tabNexpr[iExpr][1] ; 
 
  		return hasL(iF, ix) && hasOne(iE)
  		    || hasL(iE, ix) && hasOne(iF);
  	}
  	
  	if (type(iExpr) == DIFF)
  	{
  		int iF = tabNexpr[iExpr][0] ;
  		int iE = tabNexpr[iExpr][1] ; 
  		return hasL(iF, ix)
  		  && ! hasL(iE, ix) ;
  	}
  	
  	if (type(iExpr) == ZERO)
  		return false ;
 
  	
  	throw new Error("hasL(int iExpr, int ix)") ;
  }
  
 
  public boolean infEq(int iExpr1, int iExpr2, 
  	final boolean memoization) throws GCException
  {
  	magicNumber ++ ;
  	if (memoization && tf == null)
  		tf = new boolean[tabNexpr.length] ;
  	if (memoization && dejaVu == null)
      setDejaVu() ;  	
  	return infEq0(iExpr1, iExpr2, memoization) ;
  }
  
  public boolean infEq0(int iExpr1, int iExpr2, final  boolean memoization) throws GCException
  {
  	int iDiff = diff(iExpr1, iExpr2) ;
  	if (memoization && dejaVu[iDiff] == magicNumber)
  	{
  		return tf[iDiff] ; 	  	
  	}
  	
  	if (memoization)
  	{ 
  		tf[iDiff] = infEq1(iExpr1, iExpr2, memoization) ;
  	  dejaVu[iDiff] = magicNumber ;
  	   return tf[iDiff] ;
  	}
  	else
  		return infEq1(iExpr1, iExpr2, memoization) ;
  	
  }
  public boolean infEq1(int iExpr1, int iExpr2, final boolean memoization) throws GCException
  {
  	//System.err.println(s + "infEq" + " " + toString(iExpr1) + " " + toString(iExpr2)) ;
  	
  	if (iExpr1 == zero())
  		return true ;
  	
   	if (iExpr1 == one())
  		return hasOne(iExpr2) ;
  	
  	if (iExpr2 == one())
  		return false ;
  	
  	if (type(iExpr1) == LETTER)
  		return hasL(iExpr2, iExpr1)  ;
  	
  	if (type(iExpr2) == LETTER)
  		return false  ;
  	
  	if (type(iExpr1) == UNION)
  	{  		
  		int[] tabE = tabNexpr[iExpr1] ;
  		int i = 0 ;
  		boolean infEq = true ;
  		while (i != tabE.length && infEq)
  		{
  			infEq = infEq0(tabE[i], iExpr2, memoization)  ;
  			i ++ ;
  		}  		
  		return infEq ;
  	}  	
  	   	
  	if (type(iExpr2) == UNION)
  	{  		
  		int[] tabE = tabNexpr[iExpr2] ;
  		int i = 0 ;
  		boolean infEq = false ;
  		while (i != tabE.length && ! infEq)
  		{
  			infEq = infEq0(iExpr1, tabE[i], memoization)  ;
  			i ++ ;
  		}  		
  		return infEq ;
  	}  	
  	 	  	   	
  	if (type(iExpr1) == STAR)
  	{  		
  		int iE1 = tabNexpr[iExpr1][0] ;
  		
  		if (type(iExpr2) == STAR)
  			return infEq0(iE1, iExpr2, memoization) ;
  		
  		if (type(iExpr2) == CONCAT)
  	  {
  		  int iF1 = tabNexpr[iExpr2][0] ;
  		  int iF2 = tabNexpr[iExpr2][1] ;
  		  if (hasOne(iF1) 
  		  	   && infEq0(iExpr1, iF2, memoization)
  		      || hasOne(iF2) 
  		        && infEq0(iExpr1, iF1, memoization))
  		    return true ;
  	  }  	  
  	  return false ; 		
  	}  	
  	 		
  	if (type(iExpr1) == CONCAT)
  	{
  		  int iE1 = tabNexpr[iExpr1][0] ;
  		  int iE2 = tabNexpr[iExpr1][1] ;
  		  
  		  if (type(iExpr2) == STAR)
  		  {
  		  	int iF = tabNexpr[iExpr2][0] ;
  		  	return infEq0(iExpr1, iF, memoization) 
  		  	  || infEq0(iE1, iExpr2, memoization) && infEq0(iE2, iExpr2, memoization) ;
  		  }
  		    		  
  		  if (type(iExpr2) == CONCAT)  
  		  {
  		  	int iF1 = tabNexpr[iExpr2][0] ;
  		    int iF2 = tabNexpr[iExpr2][1] ;
  		    
  		    if (infEq0(iE1, iF1, memoization) && infEq0(iE2, iF2, memoization))
  		    	return true ;
  		    
  		    if (hasOne(iF1) 
  		      	&& infEq0(iExpr1, iF2, memoization)
  		      || hasOne(iF2) 
  		        && infEq0(iExpr1, iF1, memoization))
  		    return true ;
  		    
  		    if (type(iF1) == STAR && type(iF2) != STAR)
  		    	return infEq0(iE1, iF1, memoization) && infEq0(iE2, iExpr2, memoization) ;
  		    
  		    if (type(iF2) == STAR && type(iF1) != STAR)
  		    	return infEq0(iE2, iF2, memoization) && infEq0(iE1, iExpr2, memoization)  ;
  		    
  		    if (type(iF2) == STAR && type(iF1) == STAR)
  		    	return  infEq0(iE1, iF1, memoization) && infEq0(iE2, iExpr2, memoization)
  		          || infEq0(iE2, iF2, memoization) && infEq0(iE1, iExpr2, memoization) ; 		          
  		   }  	
  		  
  		  return false ;
  	}  	
  	
    throw new Error("infEq(int iExpr1, int iExpr2) " 
    	+ typeToString(type(iExpr1)) 
    	+ " " + typeToString(type(iExpr2))) ;
  }
  
  //----------------------------------------
  
	public void printEquations(int[] left, int[][] right)
	{
		int i = 0 ;
		while (i != left.length)
		{
			System.out.println(
				"E" + left[i] + " \t ="
				+ toString(right[i])) ;
				
			i ++ ;
		}
	}
	
	public String toString(int[] tabD)
	{
		String r = "\t" ;
		
		boolean noSymbolBefore = true ;
		if (tabD[0] == one())
		{
			r += " 1" ;
			noSymbolBefore = false ;
		}
		else
			r += "  " ;
		
		int x = 1 ;
		char l = 'a' ;
		while (x != tabD.length)
		{
			r += "\t" ;
			
			if (tabD[x] != zero())
			{
				if(! noSymbolBefore)
				r +=  " + " ;
			  else
			  r +=  "   " ;
			
			  r += actualLetters[l] + ".E" + tabD[x] ; 
				noSymbolBefore = false ;
			}
			
			l += 1 ;
			x ++ ;
		}		
		return r ;
	}
	
	public boolean checkHasDFA(int iExpr0)
	{
		long[] dejaVu = getDejaVu() ;
		long magicNumber = newMagicNumber() ;
		int[] toDerive = getToDerive() ;
		int topE = 0 ;
		int topF = 1 ;
		toDerive[0] = iExpr0 ;
		dejaVu[iExpr0] = magicNumber ;
		while (topE != topF)
		{
			int iExpr = toDerive[topE ++ ] ;
			int[] tabD = exprToTabD(iExpr) ;
			
			if (tabD == null)
				return false ;
			
			{
				int x = 1 ;
				while (x != tabD.length)
				{
					int iExprx = tabD[x] ;
					if (dejaVu[iExprx] != magicNumber)
				  {
				  	dejaVu[iExprx] = magicNumber ;
				  	toDerive[topF ++] = iExprx ;
				  }
				  x ++ ;
				}
			}
		}
		
		return true ;
	}
	

}




















