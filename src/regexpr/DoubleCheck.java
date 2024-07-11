package regexpr;

public class DoubleCheck implements IInfEq{
	
	InfEqA infEqA ;
	InfEq infEq ;
	IExpressions exprs ;
	
	
	public DoubleCheck(IExpressions exprs)
	{
		infEqA = new InfEqA(exprs) ;
		infEq  = new InfEq(exprs) ;
		this.exprs = exprs ;
	}
	
	public boolean infEq(int iExpr1, int iExpr2)  throws GCException
	{
		boolean iEq = infEqA.infEq(iExpr1, iExpr2) ;
		
		if (! iEq)
		{
			if (infEq.infEq(iExpr1, iExpr2) != iEq)
			{
			
			  System.out.println("DoubleCheck : " + exprs.toString(iExpr1)) ;
			  System.out.println("DoubleCheck : " + exprs.toString(iExpr2)) ;
			}
		}
		return iEq ;
	}
	
	
}