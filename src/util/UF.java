package util ;
import regexpr.* ;


public class UF{
	
  private OneList inTree ;
  private int[] tree ;
  private int memorySize ;
  
  public int memorySize()
  {
  	return memorySize ;
  }
  
	
  public UF(int memorySize)
  {
  	this.memorySize = memorySize ;
 	  inTree = new OneList(memorySize) ;
 	  tree = new int[memorySize] ;
  }
  
  public int find(final int iExpr0)
  {

  	
  	if (tree[iExpr0] == 0)
  	{
  		tree[iExpr0] = - 1 ;
  		inTree.add(iExpr0) ;
  		return iExpr0 ;
  	}
  	int iTop = iExpr0 ;
  	while (tree[iTop] >= 0)
  	{
  		
  		iTop = tree[iTop] ;
  	}

  	int iExpr = iExpr0 ;
  	while (iExpr != iTop)
  	{
  		int nExpr = tree[iExpr] ;
  		tree[iExpr] = iTop ;
  		iExpr = nExpr ;
  	}

  	return iTop ;
  }
  
  public boolean equiv(int iExpr1, int iExpr2)
  {
  	return find(iExpr1) == find(iExpr2) ;
  }
  
  public int union(int iExpr1, int iExpr2)
  {
  	iExpr1 = find(iExpr1) ;
  	iExpr2 = find(iExpr2) ;
  	
  	if (tree[iExpr1] > tree[iExpr2])
  		{ int t = iExpr1 ; iExpr1 = iExpr2 ; iExpr2 = t ; } 	
  	
  	// tree[iExpr1] <= tree[iExpr2]
  	
  	tree[iExpr1] += tree[iExpr2] ;
  	tree[iExpr2] = iExpr1 ;
  	
  	return iExpr1 ;
  }
 
	public void reinit()
	{
		while (! inTree.isEmpty())
		{
			int iExpr = inTree.choose() ;
			tree[iExpr] = 0 ;
		}
	}
}








