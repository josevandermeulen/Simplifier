package regexpr;

public interface IExpressions{
	
	static final byte ZERO = 0 ;
	static final byte ONE = ZERO + 1 ;
	static final byte LETTER = ONE + 1 ;

	static final byte UNION = LETTER + 1 ;
	static final byte CONCAT = UNION + 1 ;
  static final byte STAR = CONCAT + 1 ;
  static final byte PAIR = STAR + 1 ;
  static final byte DIFF = PAIR + 1 ;
  static final byte INTER = DIFF + 1 ;
  static final byte DELTA = INTER + 1 ;
  static final byte NOT = DELTA + 1 ;
  static final byte BOX = NOT + 1 ;
  static final byte BUFFER = BOX + 1 ;
  static final int  NBRTYPES = BUFFER + 1 ;
  
  static final long BEAUCOUP = 256l * 256 * 256 * 256 ;
  static final long EXPR_SIZE_LIMIT = 2560 ;
  static final long FIRST_POS_IN_HASH_TABLE = 28 ;

  int memorySize() ;
  void setNbrLetters(int x) ;
  void setMemorySize(int x) ;
	
  void setToDerive() ;	
	int[] getToDerive() ;	
	void setDejaVu() ;
	long[] getDejaVu() ;
	long magicNumber() ;
	long newMagicNumber() ;
	
  int zero() ;
	int one() ;	
	int iLetter(char l) ;
	// identifier of l	
	char letter(int iExpr) ;
	// letter whose identifier is iExpr
	int nbrLetters() ;
	
	byte type(int iExpr) ;
	
	int[] tabE(int iExpr) ;
	
	int[] tabS(int iExpr) ;
	
	int bestExpr(int iExpr) ;
	
	int newBuffer() throws GCException ;
	void addToBuffer(int iBuf, int iExpr) ;
	int[] bufferToTabE(int iBuf) ;
	int[] bufferToTabEAndReinit(int iBuf) ;
	void free(int iBuf) ;


  int union(int iE, int iF) throws GCException ;
  int union(int[] iExpr) throws GCException ;
  
  int concat(int iP, int iS) throws GCException ;
  int concat(int[] iE) throws GCException ;
	// return iE[1] concat ... concat iE[n - 1]
	// but efficiently : in O(s) (s : size of the whole thing).
  int concat(int[] iE, int iS) throws GCException  ;
	// return iE[1] concat ... concat iE[n - 1] concat iS
	// Pre : no iE[i] is a concatenation
  int[] unfoldConcat(int iExpr) ;
	// Pre : Expr is of the form F1 . (F2 . ... (Fn-1 . Fn)...)) (n >= 0)
	// where none of the Fi are concatenation nor 0 nor 1
  // E = 1 ==> n = 0
  // we return {F1, ..., Fn} ;
  
  int makeExpr(byte type, int[] tabS) throws GCException ;
  int star(int iExpr) throws GCException ;
  int diff(int iExpr1, int iExpr2) throws GCException ;
  int inter(int iExpr1, int iExpr2) throws GCException ;
  int delta(int iExpr1, int iExpr2) throws GCException ;
  int weakDiff(int iExpr1, int iExpr2) throws GCException ;
  int pair(int iExpr1, int iExpr2) throws GCException ;
  boolean existsPair(int iExpr1, int iExpr2) ;
  void removePair(int iPair) ;
  
  int not(int iExpr) throws GCException ;
  void collectPairs() ;

  void unify(int iExpr1, int iExpr2) throws GCException ;
  int fold(int iExpr) throws GCException ;
  int fold(int[] tabE) throws GCException ;
  int leftDistribute(int iLeft, int iRight) throws GCException ;
  
  int op(byte type, int iExpr1, int iExpr2)  throws GCException ;
  
  boolean hasOne(int iExpr) ;
  boolean notZero(int iExpr) ;
  // iExpr is guaranteed not to be equivalent to 0
  
	String toString(int iExpr) ;
	long size(int iExpr) ;
	long stringSize(int iExpr) ;
	void setActualLetters(char[] a) ;
	
	void reinit() ;
	
	int[] addEquation(int iExpr, int[] tabD) throws GCException ;	
  int[] exprToTabD(int iExpr) ;
	
	int nbrPartialDerivatives() ;

	int[] tabD(int iExpr) throws GCException ;
	//boolean existsTabD()  ;
	
	int elimOne(int iExpr) throws GCException ; 
	boolean isSubsumed(int iExpr1, int iExpr2) ;
	int  subsumeTest(int iExpr1, int iExpr2) ;

 
}