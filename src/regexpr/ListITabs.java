package regexpr;

class ListITabs{

	/* Une liste de tuples d'entiers 
	
	*/
	
	int[][] val ;
	int[] nextPos ;
	int nextFreePos ;
	int firstPos ;
	int firstFreePos ;
	static int DEFAULTNBRELEM = 5000 ;
	
	ListITabs()
	{
		this(DEFAULTNBRELEM) ;
	}
	
	ListITabs(int nbrElem){
		
		val = new int[nbrElem][] ;
		nextPos = new int[nbrElem] ;
		nextFreePos = 0 ;
		firstPos = - 1 ;
		firstFreePos = - 1 ;
	}
	
	void add(int[] v)
	{
		int pos ;
		if (firstFreePos == - 1)
		{	if (nextFreePos == val.length)
			{
				int[][] newVal = new int[val.length * 3 / 2][] ;
				int[] newNextPos = new int[newVal.length] ;
				int i = 0 ;
				while (i != val.length)
				{
					newVal[i] = val[i] ;
					newNextPos[i] = nextPos[i] ;
					i ++ ;
				}
				val = newVal ;
				nextPos = newNextPos ;
			}
			pos = nextFreePos ++ ;
		}
		else
		{
			pos = firstFreePos ;
			firstFreePos = nextPos[firstFreePos] ;
		}
		
		val[pos] = v ;
		nextPos[pos] = firstPos ;
		firstPos = pos ;
	}
	
	void add(int v)
	{
		add(new int[]{v}) ;
	}
	
	void add(int v, int w)
	{
		
		//System.out.println("toUnify : <" + v + ", " + w + ">") ; 
		if (v == w) 
			return ;
		
		
		add(new int[]{v, w}) ;
	}

	
	int[] remove()
	// Pré : firstPos != - 1
	{
		int[] v = val[firstPos] ;
		val[firstPos] = null ;
		int pos = nextPos[firstPos] ;
		nextPos[firstPos] = firstFreePos ;
		firstFreePos = firstPos ;
		firstPos = pos ;
		return v ;
	}
	
	boolean isEmpty()
	{
		return firstPos == - 1 ;
	}


}