package regexpr;

public class OneListPlain{
	
	// Liste d'identifiants (il y en a nbrIds)
	// 0 n'est plus réservé ???
	// soit i (1 <= i < succ.length - 1)
	// succ[i] == pred[i] == 0 <==> i pas dans la liste
	// succ[i] est l'élément suivant i dans la liste ou - 1, 
	// si pas de suivant
	// succ[0] est le premier élément ou - 1 si la liste est vide.
	
	int[] succ ;
	int[] pred ;
	int first ;

	
	public OneListPlain(final int nbrIds)
	{
		succ = new int[nbrIds] ;
		pred = new int[nbrIds] ;
		first = - 1 ;
	}
	
	public boolean inList(int v)
	{
		return succ[v] != 0 ;
	}
	
	public boolean add(int v)
	// on l'ajoute s'il n'y est pas
	// result == il n'y était pas
	{
		if (succ[v] != 0)
			return false ;
		
		pred[v] = - 1 ;
		succ[v] = first ;
		if (first != - 1)
			pred[first] = v ;
		first = v ;

		return true ;
	}
	
	
	public boolean remove(int v)
	{
	  if (succ[v] == 0)
			return false ;
		
		int s = succ[v] ;
		int p = pred[v] ;
		
		if (p == - 1)
		{
			first = s ;
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
	
	public boolean isEmpty()
	{
		return first == - 1 ;
	}
	
	public int first()
	{
		return first ;
	}
	
	public int choose()
	{
		int v = first() ;
		remove(v) ;
		return v  ;
	}
	
	public int next(int v)
	{
		
		  if (v == 0)
			throw new Error("v == 0") ;
		
		

		return succ[v] ;
	}
	
	public void print()
	{
		int v = first ;
		System.out.print("[ ") ;
		
		int count = 0 ;
		
		while (v != - 1 && count < 1000)
		{
			System.out.print((v ) + " ") ;
			v = succ[v] ;
			
			count ++ ;
		  if (v == 0)
			throw new Error("v == 0") ;

		}
		
		System.out.println("]") ;
	}
	
	public static void main(String[] toto)
	{
		OneListPlain l = new OneListPlain(100) ;
		System.out.println(l.add(4)) ;
		l.print() ;
		System.out.println(l.add(5)) ;
		l.print() ;
		System.out.println(l.add(4)) ;
		l.print() ;
		System.out.println(l.remove(4)) ;
		l.print() ;
		System.out.println(l.add(2)) ;
		l.print() ;
		System.out.println(l.add(4)) ;
		l.print() ;
		System.out.println(l.add(6)) ;
		l.print() ;
		System.out.println(l.remove(2)) ;		
		l.print() ;
		System.out.println(l.remove(2)) ;
		
		l.print() ;
		while (! l.isEmpty())
		{
			System.out.println(l.choose()) ;
		}
		
		l.print() ;
	}
	
}