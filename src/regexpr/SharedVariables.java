package regexpr;

public class SharedVariables{
	
	int[] dejaVu ;
	int magicNumber = 0 ;
	int[] toDerive ;
	public SharedVariables(int memorySize){
		
		dejaVu = new int[memorySize] ;
		toDerive = new int[memorySize] ;	
	}
	
	public int[] dejaVu()
	{
		return dejaVu ;
	}	
	
	public int[] toDerive()
	{
		return toDerive ;
	}
	
	public int developped()
	{
		return magicNumber ;
	}	
	
	public int getMagicNumber()
	{
		return ++ magicNumber ;
	}
}
