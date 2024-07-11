package util ;

public class Time{

  static long[] t0 = new long[100] ;
  static int i = 0 ;

	public static void sett0()
	{
		t0[i ++]  = System.nanoTime() ;
	}
	
	public static String stime()
	{
		if (i == 0)
			i = 1 ;
		long t = System.nanoTime() - t0[-- i] ;
		t /= 1000000 ;
		return "" + t / 1000d + "[" + i + "]" ;
	}
	
	public static String toString(long t)
	{
		t /= 1000000 ;
		return "" + t / 1000d ;
	}
		
	
	public static String toStringD(long t)
	{
		if (t >= 1000000000l)
		{
			int tm = (int) (t / 1000000000l) ;
			
			int td = (int) (t % 1000000000l) ;
			
			if (tm >= 100)
				td = 0 ;
			else if (tm >= 10)
			  td = (int) (td / 100000000) ;
			else
				td = (int) (td / 10000000) ;

			if (td == 0)
				return "" + tm + "\\,s" ;
			if (td >= 10 || tm >= 10)
				return "" + tm + "." + td + "\\,s" ;
			else
				return "" + tm + ".0" + td + "\\,s" ;			  
		}
		
		if (t >= 1000000)
		{
			int tm = (int) (t / 1000000l) ;
			
			int td = (int) (t % 1000000l) ;
			
			if (tm >= 100)
				td = 0 ;
			else if (tm >= 10)
			  td = (int) (td / 100000) ;
			else
				td = (int) (td / 10000) ;

			if (td == 0)
				return "" + tm + "\\,m" ;
			if (td >= 10 || tm >= 10)
				return "" + tm + "." + td + "\\,m" ;
			else
				return "" + tm + ".0" + td + "\\,m" ;			  
		}
		
		{
			int tm = (int) (t / 1000l) ;
			
			int td = (int) (t % 1000l) ;
			
			if (tm >= 100)
				td = 0 ;
			else if (tm >= 10)
			  td = (int) (td / 100) ;
			else
				td = (int) (td / 10) ;

			if (td == 0)
				return "" + tm + "\\,\\mu" ;
			if (td >= 10 || tm >= 10)
				return "" + tm + "." + td + "\\,\\mu" ;
			else
				return "" + tm + ".0" + td + "\\,\\mu" ;			  
		}
		
	}
	
	public static String toStringN(double x)
	/* 
	   Afficher x sous une des formes 
	      y  x < 1000
	      y K x < 1000,000
	      y M x < 1000,000,000
	      y G x < 1000,000,000,000	
	      avec 3 chiffres pour y (sauf si y < 100)
	*/
	{
		if (x < 10)
		{
			int ix = (int) (x * 100) ;
			int y = ix / 100 ;
			int z = ix % 100 ;
			
			if (z == 0)
				return "" + y ;
			
			if (z < 10)
				return "" + y + ".0" + z ;
			
			return "" + y + "." + z ;
		}
		
		if (x < 100)
		{
			int ix = (int) (x * 10) ;
			int y = ix / 10 ;
			int z = ix % 10 ;
			
			if (z == 0)
				return "" + y ;
			
			return "" + y + "." + z ;
		}
		
		
		if (x < 1000)
			return "" + ((int)(x + 0.5));
		//---------------------------------
		if (x < 10000)
		{
			int y = (int) (x / 1000) ;
			int z = ((int)(x % 1000)) / 10 ;
			
			if (z == 0)
			return "" + y  + "\\,K" ;
		
			if (z < 10)
			return "" + y  + ".0" + z + "\\,K" ;
		
		  return "" + y  + "." + z + "\\,K" ;			
		}
		
		if (x < 100000)
		{
			int y = (int) (x / 1000) ;
			int z = ((int)(x % 1000)) / 100 ;
		
		  if (z == 0)
			return "" + y  + "\\,K" ;
	
		  return "" + y  + "." + z + "\\,K" ;			
		}
		
		if (x < 1000000)
		{
			int y = (int) (x / 1000) ;
			
			return "" + y  + "\\,K" ;
		}
		//---------------------------------
		
		
		if (x < 10000000)
		{
			int y = (int) (x / 1000000) ;
			int z = ((int)(x % 1000000)) / 10000 ;
			
			if (z == 0)
			return "" + y  + "\\,M" ;
		
			if (z < 10)
			return "" + y  + ".0" + z + "\\,M" ;
		
		  return "" + y  + "." + z + "\\,M" ;			
		}
		
		if (x < 100000000)
		{
			int y = (int) (x / 1000000) ;
			int z = ((int)(x % 1000000)) / 100000 ;
		
		  if (z == 0)
			return "" + y  + "\\,M" ;
	
		  return "" + y  + "." + z + "\\,M" ;			
		}
		
		if (x < 1000000000)
		{
			int y = (int) (x / 1000000) ;
			
			return "" + y  + "\\,M" ;
		}
		
			//---------------------------------
		
		
		if (x < 10000000000l)
		{
			int y = (int) (x / 1000000000) ;
			int z = ((int)(x % 1000000000)) / 10000000 ;
			
			if (z == 0)
			return "" + y  + "\\,G" ;
		
			if (z < 10)
			return "" + y  + ".0" + z + "\\,G" ;
		
		  return "" + y  + "." + z + "\\,G" ;			
		}
		
		if (x < 100000000000l)
		{
			int y = (int) (x / 1000000000) ;
			int z = ((int)(x % 1000000000)) / 100000000 ;
		
		  if (z == 0)
			return "" + y  + "\\,G" ;
	
		  return "" + y  + "." + z + "\\,G" ;			
		}
		
		if (x < 1000000000000l)
		{
			int y = (int) (x / 1000000000) ;
			
			return "" + y  + "\\,G" ;
		}
		return "" + x ;
	}
	
	
	
	
	
	public static void main(String[] arg)
	{
		System.out.println(toStringN(Long.parseLong(arg[0]))) ;
	}
	
}