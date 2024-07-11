package regexpr;

public class TwoList{
	
	private MultiList list ;
	
	// liste 0 : réservoir
	// liste 1 : utilisés
	private int size = 0 ; // nombre d'éléments utilisés
	private int maxSize = 0 ; // nombre max d'éléments utilisés
	public int size(){ return size ; }
	public int maxSize(){ return maxSize ; }
	
	public TwoList(final int nbrIds)
	{
		list = new MultiList(2, nbrIds) ;
		
		int i = nbrIds - 1 ;
		while (i != 0)
			list.add(0, i --) ;
	}
	
	public int choose()
	{
		int v = list.first(0) ;
		if (v != - 1)
		  add(v) ;
		return v ;
	}
	
	public void add(int v)
	{
		if (v == 0)
			throw new Error("add(" + v + ")") ;
		
		//if (! list.isInAList(v))
		//	throw new Error("! list.isInAList(" + v + ")") ;
		list.remove(0, v) ;
		
		//if (list.isInAList(v))
		//	throw new Error("list.isInAList(" + v + ")") ;		
		list.add(1, v) ;			
		size ++ ;
		if (size > maxSize)
			maxSize = size ;
	}
	
	public void remove(int v)
	{
		list.remove(1, v) ;
		list.add(0, v) ;	

		size -- ;
	}
	
	public int first() 
	{
		return list.first(1) ;
	}
	
	public int val(int v)
	{
		return v ;
	}
	
	public int next(int v) 
	{
		return list.next(v) ;
	}
	
	public void print()
	{
		list.print(1) ;
	}
}