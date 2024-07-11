package regexpr ;

public class GC{

	Background exprs ;
	MultiList minExprs ;
  public GC(Background exprs, MultiList minExprs)
  {
  	this.exprs = exprs ;
  	this.minExprs = minExprs ;
  }

  public void gc()
	{
		// We save the expressions and their types
		int[][] tabNexpr = exprs.tabNexpr ;
		byte[] type = exprs.type ;
		int[] tabCode = exprs.tabCode ;
		long[] sizeExprs = exprs.sizeExprs ;
		
		exprs.reinit() ;
		
		//System.out.println("check1 ") ;
		//System.out.println("GC check1 finished : iExprList.size " +
		//exprs.iExprList.size()) ;
		
		//check() ;
		
		//System.out.println(minExprs) ;
		
		{
			int iExpr = exprs.firstPosInHashTable ;
			while (iExpr != tabNexpr.length)
			{
				if (minExprs.isInAList(iExpr))
				{
					if (type[iExpr]== 0)
						throw new Error() ;
					exprs.type[iExpr] = type[iExpr] ;
					exprs.tabNexpr[iExpr] = tabNexpr[iExpr] ;
					exprs.tabCode[iExpr] = tabCode[iExpr] ;
					exprs.sizeExprs[iExpr] = sizeExprs[iExpr] ;
					int pos = exprs.pos(iExpr) ;
					exprs.hashTable.add(pos, iExpr) ;
					exprs.iExprList.add(iExpr) ;
				}			
				iExpr ++ ;
			}
		}
		
		System.out.println("GC finished : iExprList.size " +
			exprs.iExprList.size()) ;
		//System.out.println("check2 ") ;

		//check() ;
		//System.out.println("GC check2 finished : iExprList.size " +
		//	exprs.iExprList.size()) ;
	}
	
	public void gc(int iExpr)
	{
		if (minExprs.isInAList(iExpr))
			return ;
		int[] tabE = exprs.tabNexpr[iExpr] ;
		byte type = exprs.type(iExpr) ;
		int hcode = exprs.tabCode[iExpr] ;
		long size = exprs.sizeExprs[iExpr] ;
		
		gc() ;
		
		exprs.tabNexpr[iExpr] = tabE ;
		exprs.type[iExpr] = type ;
		exprs.tabCode[iExpr] = hcode ;
		exprs.sizeExprs[iExpr] = size ;
		int pos = exprs.pos(iExpr) ;
		exprs.hashTable.add(pos, iExpr) ;
		exprs.iExprList.add(iExpr) ;
		//System.out.print("gc : ") ;
		//System.out.println(exprs.toString(iExpr)) ;
	}
	
	public void check()
	{
		int iExpr = exprs.iExprList.first() ;
		
		System.out.println("check gc : " + iExpr + " "
			+ exprs.toString(iExpr)) ;
		/*while (iExpr != - 1)
		{
			//if (exprs.size(iExpr) <= 1)
			//System.out.println("gc : " + iExpr) ;
			int[] tabE = exprs.tabS(iExpr) ;
			try{
				
				int i = 0 ;
				while (i != tabE.length)
				{
					 if (!minExprs.isInAList(tabE[i]))
					 	 System.out.println("check gc : " + iExpr + " " + tabE[i]) ;
					i++ ;
				}
			}
			catch(Exception e)
			{		
				System.out.println("check gc : " + iExpr) ;
			}
			
			iExpr = exprs.iExprList.next(iExpr) ;
		}*/
	}
		
}