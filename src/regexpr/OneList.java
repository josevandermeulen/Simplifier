package regexpr;

public class OneList{
	
	// Liste d'identifiants (il y en a nbrIds)
	private OneListPlain l ;
	
	public OneList(final int nbrIds)
	{
		l = new OneListPlain(nbrIds + 1) ;
	}
	
	public boolean inList(int v)
	{
		return l.inList(v + 1) ;
	}
	
	public boolean add(int v)
	{
		return l.add(v + 1) ;
	}
	
	public boolean remove(int v)
	{
	  return l.remove(v + 1) ;
	}
	
	public boolean isEmpty()
	{
		return l.isEmpty() ;
	}
	
	public int first()
	{
		int v = l.first() ;
		if (v == - 1)
			return v ;
		else
			return v - 1 ;
	}
	
	public int choose()
	{
		
		return l.choose() - 1  ;
	}
	
	public int next(int v)
	{
		v = l.next(v + 1) ;
		if (v != - 1)
			v -- ;
		return v ;
	}
	
	public void print()
	{
		int v = first() ;
		System.out.print("[ ") ;
		
		while (v != - 1)
		{
			System.out.print((v) + " ") ;
			v = next(v) ;
			
		  if (v == 0)
			throw new Error("v == 0") ;

		}
		
		System.out.println("]") ;
	}
	
	public static void main(String[] toto)
	{
		OneList l = new OneList(100) ;
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