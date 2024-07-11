package regexpr;

public abstract class TExpressions implements IExpressions{
	
  private IExpressions exprs ; 	
  public TExpressions(IExpressions exprs)	
  {
  	this.exprs = exprs ;
  }
  
  public int memorySize()
  {
  	return exprs.memorySize() ;
  }
  
  public void setNbrLetters(int x)
	{
		exprs.setNbrLetters(x) ;
	}
  
  long tZero = 0 ;
  public int zero()
  {
  	long t0 = System.nanoTime() ;
  	int res =  exprs.zero() ;
  	tZero += System.nanoTime() - t0 ;
  	return res ;
  }
  
  long tOne = 0 ;  
	public int one() 	
	{
  	long t0 = System.nanoTime() ;
  	int res =  exprs.one() ;
  	tOne += System.nanoTime() - t0 ;
  	return res ;
  }
	
  long tIletter = 0 ;
	public int iLetter(char l) 
	// identifier of l	
	{
  	long t0 = System.nanoTime() ;
  	int res =  exprs.iLetter(l) ;
  	tIletter += System.nanoTime() - t0 ;
  	return res ;
  }
  
  long tLetter = 0 ;
	public char letter(int iExpr) 
	// letter whose identifier is iExpr
	{
  	long t0 = System.nanoTime() ;
  	char res =  exprs.letter(iExpr) ;
  	tLetter += System.nanoTime() - t0 ;
  	return res ;
  }
  
  public byte type(int iExpr) 
  {
  	return exprs.type(iExpr) ;
  }

	public int[] tabE(int iExpr)
	{ 	
  	return exprs.tabE(iExpr) ; 	
  }
  
	public int[] tabS(int iExpr)
	{ 	
  	return exprs.tabS(iExpr) ; 	
  }
  
  public int bestExpr(int iExpr){
  	
  	return exprs.bestExpr(iExpr) ;
  	
  }
  
  public int newBuffer() throws GCException 
  {
  	return exprs.newBuffer() ;
  }
  
	public void addToBuffer(int iBuf, int iExpr) 
  {
  	exprs.addToBuffer(iBuf, iExpr) ;
  }
 	
	public int[] bufferToTabE(int iBuf) 
  {
  	return exprs.bufferToTabE(iBuf) ;
  }
 	
	public int[] bufferToTabEAndReinit(int iBuf) 
  {
  	return exprs.bufferToTabEAndReinit(iBuf) ;
  }
 
  public void free(int iBuf) 
  {
  	exprs.free(iBuf) ;
  }

  long tUnion = 0 ;
  public int union(int iE, int iF) throws GCException
  {
  	long t0 = System.nanoTime() ;
  	int res =  exprs.union(iE, iF) ;
  	tUnion += System.nanoTime() - t0 ;
  	return res ;
  }
  
  public int union(int[] iExpr) throws GCException
  {
  	long t0 = System.nanoTime() ;
  	int res =  exprs.union(iExpr) ;
  	tUnion += System.nanoTime() - t0 ;
  	return res ;
  }
  
  long tConcat = 0 ;
  public int concat(int iP, int iS) throws GCException
  {
  	long t0 = System.nanoTime() ;
  	int res =  exprs.concat(iP, iS) ; ;
  	tConcat += System.nanoTime() - t0 ;
  	return res ;
  }
  
  public int concat(int[] iE) throws GCException
	// return iE[1] concat ... concat iE[n - 1]
	// but efficiently : in O(s) (s : size of the whole thing).
	{
  	long t0 = System.nanoTime() ;
  	int res =  exprs.concat(iE) ;
  	tConcat += System.nanoTime() - t0 ;
  	return res ;
  }
  
  public int concat(int[] iE, int iS) throws GCException  
	{
  	long t0 = System.nanoTime() ;
  	int res =  exprs.concat(iE, iS) ;
  	tConcat += System.nanoTime() - t0 ;
  	return res ;
  }
  
  public int[] unfoldConcat(int iExpr) 
	{
  	long t0 = System.nanoTime() ;
  	int[] res =  exprs.unfoldConcat(iExpr) ;
  	tConcat += System.nanoTime() - t0 ;
  	return res ;
  }
  
   public int makeExpr(byte type, int[] tabS) throws GCException 
   {
   	 return exprs.makeExpr(type, tabS) ;
   }

  
  long tStar = 0 ;
  public int star(int iExpr) throws GCException
  {
  	long t0 = System.nanoTime() ;
  	int res =  exprs.star(iExpr) ;
  	tStar += System.nanoTime() - t0 ;
  	return res ;
  }
  
  long tDiff = 0 ;
  public int diff(int iExpr1, int iExpr2) throws GCException
  {
  	long t0 = System.nanoTime() ;
  	int res =  exprs.diff(iExpr1, iExpr2) ;
  	tDiff += System.nanoTime() - t0 ;
  	return res ;
  }  
  
  public int delta(int iExpr1, int iExpr2) throws GCException
  {
  	int res =  exprs.delta(iExpr1, iExpr2) ;
  	return res ;
  }  
  
  public int inter(int iExpr1, int iExpr2) throws GCException
  {
  	int res =  exprs.inter(iExpr1, iExpr2) ;
  	return res ;
  }  
  
  long tWeakDiff = 0 ;
  public int weakDiff(int iExpr1, int iExpr2) throws GCException
  {
  	long t0 = System.nanoTime() ;
  	int res =  exprs.weakDiff(iExpr1, iExpr2) ;
  	tWeakDiff += System.nanoTime() - t0 ;
  	return res ;
  }
  
  long tPair = 0 ;
  public int pair(int iExpr1, int iExpr2) throws GCException
  {
  	long t0 = System.nanoTime() ;
  	int res =  exprs.pair(iExpr1, iExpr2) ;
  	tPair += System.nanoTime() - t0 ;
  	return res ;
  }
  
  public boolean existsPair(int iExpr1, int iExpr2) 
  {
  	return exprs.existsPair(iExpr1, iExpr2) ;
  }
  
  public void removePair(int iPair)
  {
  	exprs.removePair(iPair) ;
  }
  
  public int not(int iExpr) throws GCException 
  {
  	return exprs.not(iExpr) ;
  }
  
  public  void unify(int iExpr1, int iExpr2)  throws GCException
  {
  	exprs.unify(iExpr1, iExpr2) ;
  }
  
  public int fold(int iExpr) throws GCException
  {
  	return exprs.fold(iExpr) ;
  }
    
  public int fold(int[] tabE) throws GCException
  {
  	return exprs.fold(tabE) ;
  }
  
  
  public int leftDistribute(int iLeft, int iRight) throws GCException
  {
  	return exprs.leftDistribute(iLeft, iRight) ;
  }
  
  
  public int op(byte type, int iExpr1, int iExpr2)  throws GCException
  {
  	return exprs.op(type, iExpr1, iExpr2) ;
  }
    
  long tHasOne = 0 ;
  public boolean hasOne(int iExpr) 
  {
  	long t0 = System.nanoTime() ;
  	boolean res =  exprs.hasOne(iExpr) ;
  	tHasOne += System.nanoTime() - t0 ;
  	return res ;
  }
  
  long tNotZero = 0 ;
  public boolean notZero(int iExpr) 
  {
  	long t0 = System.nanoTime() ;
  	boolean res =  exprs.notZero(iExpr) ;
  	tNotZero += System.nanoTime() - t0 ;
  	return res ;
  }
  
  long tSize = 0 ;
	public long size(int iExpr)
	{
  	long t0 = System.nanoTime() ;
  	long length =  exprs.size(iExpr) ;
  	tSize += System.nanoTime() - t0 ;
  	return length ;
  }
    
  long tString = 0 ;
	public String toString(int iExpr)
	{
  	long t0 = System.nanoTime() ;
  	String res =  exprs.toString(iExpr) ;
  	tString += System.nanoTime() - t0 ;
  	return res ;
  }
  
  long tsAL = 0 ;
	public void setActualLetters(char[] a) 
	{
  	long t0 = System.nanoTime() ;
  	exprs.setActualLetters(a) ;
  	tsAL += System.nanoTime() - t0 ;
  }
  
  long tReinit ;
  public void reinit() 
  {
  	long t0 = System.nanoTime() ;
  	exprs.reinit() ;
  	tReinit += System.nanoTime() - t0 ;
  }
  
  long tAddEquations ;	
	public int[] addEquation(int iExpr, int[] tabD)	throws GCException 
	{
		long t0 = System.nanoTime() ;
  	int[] res = exprs.addEquation(iExpr, tabD) ;
  	tAddEquations += System.nanoTime() - t0 ;
  	return res ;
	}
	
	long tExprToTabD ;
	public int[] exprToTabD(int iExpr)
	{
		long t0 = System.nanoTime() ;
  	int[] res = exprs.exprToTabD(iExpr) ;
  	tExprToTabD += System.nanoTime() - t0 ;
  	return res ;
	}
	
	public int nbrPartialDerivatives()
	{
		return exprs.nbrPartialDerivatives() ;
	}
	
	long tTabD ;
	public int[] tabD(int iExpr) throws GCException
  {
		long t0 = System.nanoTime() ;
  	int[] res = exprs.tabD(iExpr) ;
  	tTabD += System.nanoTime() - t0 ;
  	return res ;
	}
	
	long tIsSubsumed ;	
  public boolean isSubsumed(int iExpr1, int iExpr2)
  {
		long t0 = System.nanoTime() ;
  	boolean res = exprs.isSubsumed(iExpr1, iExpr2) ;
  	tIsSubsumed += System.nanoTime() - t0 ;
  	return res ;
	}
  
	long tSubsumeTest ;	
	public int subsumeTest(int iExpr1, int iExpr2) 
	{
		long t0 = System.nanoTime() ;
  	int res = exprs.subsumeTest(iExpr1, iExpr2) ;
  	tSubsumeTest  += System.nanoTime() - t0 ;
  	return res ;	
	}
	
  public void printTimes()
  {
  	System.out.println("tZero = " + util.Time.toString(tZero)) ;
  	System.out.println("tOne = " + util.Time.toString(tOne)) ;
  	System.out.println("tIletter = " + util.Time.toString(tIletter)) ;
  	System.out.println("tLetter = " + util.Time.toString(tLetter)) ;
  	System.out.println("tUnion = " + util.Time.toString(tUnion)) ;
  	System.out.println("tConcat = " + util.Time.toString(tConcat)) ;
  	System.out.println("tStar = " + util.Time.toString(tStar)) ;
  	System.out.println("tDiff = " + util.Time.toString(tDiff)) ;
  	System.out.println("tHasOne = " + util.Time.toString(tHasOne)) ;  	
  	System.out.println("tNotZero = " + util.Time.toString(tNotZero)) ;  	
  	System.out.println("tSize = " + util.Time.toString(tSize)) ;
  	System.out.println("tString = " + util.Time.toString(tString)) ;
  	System.out.println("tsAL = " + util.Time.toString(tsAL)) ;
  	System.out.println("tReinit = " + util.Time.toString(tReinit)) ;
  	System.out.println("tAddEquations = " + util.Time.toString(tAddEquations)) ;
  	System.out.println("tExprToTabD = " + util.Time.toString(tExprToTabD)) ;
  	System.out.println("tTabD = " + util.Time.toString(tTabD)) ;
  	System.out.println("tWeakDiff = " + util.Time.toString(tWeakDiff)) ;
  	System.out.println("tPair = " + util.Time.toString(tPair)) ;
  	System.out.println("tSubsumeTest = " + util.Time.toString(tSubsumeTest)) ;
  	  	
  }
}