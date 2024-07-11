package regexpr;

public class MultiList{

	/* Une liste de listes d'entiers MultiList(i)
	
	  Les listes sont disjointes... 
	  les valeurs sont comprises entre 1 et nbrIds (exclu)
	
	*/
	
	int[] firstPos ; // Première pos de List(i) ou  - 1 si vide
	int[] succ ; //
	int[] pred ; // comme OneList
	
	
	boolean check(int ind)
	{
		int pos = firstPos[ind] ;
		while (pos != - 1 && pos != 0)
			pos = succ[pos] ;
		
		return pos != 0 ;
	}	
	
	boolean check(int ind, int v)
	{
		int pos = firstPos[ind] ;
		while (pos != - 1 && pos != v)
			pos = succ[pos] ;
		
		return pos == v ;
	}
	
	public MultiList(int nbrInd, int nbrIds){
		
		succ = new int[nbrIds] ;
		pred = new int[nbrIds] ;
		firstPos = new int[nbrInd] ;
		{ int i = 0 ;
			while (i != firstPos.length)
				firstPos[i ++] = - 1 ;
		}		
	}
	
	public void checkCompletely(String name)
	{
		int i = 0 ;
		while (i != firstPos.length)
		{
			if (! check(i))
			{
				throw new Error("Error in " + name + " " + i) ;
			}
			i ++ ;
		}
	}
	
	public void swap(int ind1, int ind2)
	{
		int first1 = firstPos[ind1] ;
		int first2 = firstPos[ind2] ;
		firstPos[ind1] = first2 ;
		firstPos[ind2] = first1 ;
	}
	
	public boolean isInAList(int v)
	{
		return succ[v] != 0 ;
	}
	
	public boolean add(int ind, int v)
	{
		if (succ[v] != 0)
			return false ;
		
		pred[v] = - 1 ;
		succ[v] = firstPos[ind] ;
		if (firstPos[ind] != - 1)
			pred[firstPos[ind]] = v ;
		firstPos[ind] = v ;

		return true ;
	}
	
	
	public boolean hasAtLeastTwo(int ind)
	{

		int pos1 = firstPos[ind] ;
		if (pos1 == - 1)
			return false ; // 0 éléments
		
		int pos2 = succ[pos1] ;
		if (pos2 == - 1)
			return false ; // 1 élément
				
		return true ;		
	}
	
	public int[] firstTwo(int ind)
	{

		int v = firstPos[ind] ;
		
		return new int[]{v, succ[v]} ; 
	}
	
	public boolean remove(int ind, int v)
	// Pré : ou bien v n'est dans aucune liste
	// ou bien v est dans liste(ind).
	{

		if (succ[v] == 0)
			return false ;
		
		int s = succ[v] ;
		int p = pred[v] ;
		
		if (p == - 1)
		{
			firstPos[ind] = s ;
		}
		else
		{
			succ[p] = s ;
		}
		
		if (s != - 1)
		{
			pred[s] = p ;
		}
		
		succ[v] = pred[v] = 0 ;
		
		return true ;
	}
	
	public int removeFirst(int ind)
	// pré : list(ind) not empty
	{
		int v = firstPos[ind] ;	
		remove(ind, v) ;
		return v ;
	}
	
	public boolean isEmpty(int ind)
	{
		return firstPos[ind] == - 1 ;
	}
	
	public int first(int ind)
	{
		return firstPos[ind] ;
	}
	
	public int choose(int ind)
	{
		int v = first(ind) ;
		remove(ind, v) ;
		return v ;
	}
	
	
	public int val(int pos)
	{
		return pos ;
	}
	
	public int next(int pos)
	{
		return succ[pos] ;
	}
	
	public int length(int ind)
	{
		int length = 0 ;
		int v = firstPos[ind] ;
		
		while (v != - 1)
		{
			v = succ[v] ;
			//if (v == 0)
		  //throw new Error("print : succ[v] == 0") ;
      length ++ ;
		}		
		return length ;
	}
	
	public void print()
	{
		System.out.println("----------------------------------------") ;
		int i = 0 ;
		while (i != firstPos.length)
		{
			if (! isEmpty(i))
			{
				System.out.print("liste(" + i + ") = ") ;
				print(i) ;
			}
			i ++ ;
		}
		System.out.println("----------------------------------------") ;		
	}
	
	public void print(int ind)
	{
		int v = firstPos[ind] ;
		System.out.print("liste(" + ind + ") = [ ") ;
		
		while (v != - 1)
		{
			System.out.print(v + " ") ;
			v = succ[v] ;
			if (v == 0)
		  throw new Error("print : succ[v] == 0") ;

		}
		
		System.out.println("]") ;
	}
	

	public static void main(String[] toto)
	{
		MultiList l = new MultiList(10, 100) ;
		System.out.println(l.add(1, 4)) ;
		l.print() ;
		System.out.println(l.add(3, 5)) ;
		l.print() ;
		System.out.println(l.add(5, 4)) ;
		l.print() ;
		System.out.println(l.remove(1, 4)) ;
		l.print() ;
		System.out.println(l.add(3, 2)) ;
		l.print() ;
		System.out.println(l.add(5, 4)) ;
		l.print() ;
		System.out.println(l.add(3, 6)) ;
		l.print() ;
		System.out.println(l.remove(2, 2)) ;		
		l.print() ;
		System.out.println(l.remove(1, 2)) ;		
		l.print() ;
		
		{
			int i = 0 ;
      while (i != 10)
	   	{ 
	   		while (! l.isEmpty(i))
		    {
			    System.out.println(l.choose(i)) ;
		    }
		    i ++ ;
		  }
		}
		l.print() ;
	}
	
}