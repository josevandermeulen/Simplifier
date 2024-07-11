package util ;

public class Flags{
	
	static int[] bitAtPos = new int[32] ;
	// bitAtPos[pos] = P 1 S where S consists of pos 0 and P of (31 - pos) 0
	static int[] zeroAtPos = new int[32] ;
	// zeroAtPos[pos] = P 0 S where S consists of pos 1 and P of (31 - pos) 
	static {
		int pos = 0 ;
		int val = 1 ;
		int sumVals = 0 ;
		while (pos != 32)
		{
			sumVals += val ;
			bitAtPos[pos] = val ;
			
		  //System.out.println(pos + " " + bitAtPos[pos]) ;
		  //System.out.println(sumVals + " " + sumVals) ;
			
			val *= 2 ;
			pos ++ ;
		}
		
		pos = 0 ;
		val = 0 ;
	  while (pos != 32)
		{
			zeroAtPos[pos] = sumVals ^ bitAtPos[pos] ;
			
		  //System.out.println(pos + " " + zeroAtPos[pos]) ;
  		pos ++ ;
		}
	}
	
	private int[] bits ;
	
	public Flags(final int memorySize)
	{
		bits = new int[memorySize] ;
	}
	
	public void setFlag(int iExpr, int pos)
	{
		bits[iExpr] |= bitAtPos[pos] ;	
	}
	
	public void reSetFlag(int iExpr, int pos)
	{
		bits[iExpr] &= zeroAtPos[pos] ;
	}
	
	public void unMark(int iExpr)
	{
		bits[iExpr] = 0 ;
	}

	public boolean hasFlag(int iExpr, int pos)
	{
		return (bits[iExpr] & bitAtPos[pos]) != 0 ;
	}

	public static int HASDFA = 1 ;
	static int HASMDFA = 2 ;
	static int SOLVED = 3 ;
	static int SIMPLIFIED = 4 ;
	static int STACKED = 5 ;
	
	public void joinFlags(int iExpr1, int iExpr2)
	{
		int join = bits[iExpr1] | bits[iExpr2] ;
		bits[iExpr1] = join ;
		bits[iExpr2] = join ;
	}
	
	public void setHasDFA(int iExpr)
	{
		setFlag(iExpr, HASDFA) ;
	}
	
	public boolean hasDFA(int iExpr)
	{
		return hasFlag(iExpr, HASDFA) ;
	}	
	
	public void setHasMDFA(int iExpr)
	{
		setFlag(iExpr, HASMDFA) ;
	}
	
	public boolean hasMDFA(int iExpr)
	{
		return hasFlag(iExpr, HASMDFA) ;
	}
	
	public void setSimplified(int iExpr)
	{
		setFlag(iExpr, SIMPLIFIED) ;
	}
	
	public boolean isSimplified(int iExpr)
	{
		return hasFlag(iExpr, SIMPLIFIED) ;
	}
	
	
	public void setSolved(int iExpr)
	{
		setFlag(iExpr, SOLVED) ;
	}
	
	public boolean isSolved(int iExpr)
	{
		return hasFlag(iExpr, SOLVED) ;
	}
	
	public void putOnStack(int iExpr)
	{
		setFlag(iExpr, STACKED) ;
	}
	
	public boolean wasOnStack(int iExpr)
	{
		return hasFlag(iExpr, STACKED) ;
	}
	
	public static void main(String[] toto)
	{
		
	}

}






