package regexpr;
import syntax.* ;
import util.* ;

public class EquivUF extends Equiv implements IEquiv{
	
	private UF uf ;
	//IExpressions exprs ;
	
	public EquivUF(IExpressions exprs)
	{
		super(exprs, false) ;
		this.exprs = exprs ;
		uf = new UF(exprs.memorySize()) ;
	}	
	
	void initMagicNumber() 
	{
	}
	
	void addToRtod(int iExpr1, int iExpr2)  throws GCException
	{
		int iPair = exprs.pair(iExpr1, iExpr2) ;
		toDerive[++ topM] = iPair ;
		uf.union(iExpr1, iExpr2) ;
	}
  	
	boolean checkAndUpdateRelation(int iExpr1, int iExpr2) throws GCException
	{
		iBest1 = iExpr1 ;
  	iBest2 = iExpr2 ;
  	
  	if (uf.equiv(iBest1, iBest2))
  		return true ;
  	
  	return false ;  			  
	}	
	
	public boolean eq(int iExpr1, int iExpr2) throws GCException
  {
  	if (uf.memorySize() < exprs.memorySize())
  		uf = new UF(exprs.memorySize()) ;
  	 		
  	boolean eq = check(iExpr1, iExpr2) ;
  	
  	uf.reinit() ;
  	
  	return eq ;
  }
}