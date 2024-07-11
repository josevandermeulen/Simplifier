package util ;

public class BigNumber{
	
	static double[] tenToPower = new double[309] ;
	static{
		int i = 0 ;
		double pow = 1.0 ;
		while (i != tenToPower.length)
		{
			tenToPower[i] = pow ;
			
			i ++ ;
			if (i != tenToPower.length)
				pow *= 10 ;
		}
	}
	
	double x ; //mantisse ;
	long e ; // exposant
	// inv : 1 <= |x| < 10   ou  x = 0 et e = 0 ;
	// représente le nombre x * 10^e
	
	int sign()
	{
		if (x > 0)
			return 1 ;
		if (x < 0)
			return - 1 ;
		return 0 ;
	}
	
	public double toDouble()
	{
		if (x == 0.0)
			return 0.0 ;
		
		if (e >= 0)
			if (e <tenToPower.length)
			return x * tenToPower[(int)e] ;
		  else throw new Error(this + " cannot be converted to double") ;
	  else	
	  	if (- e <tenToPower.length)
			return x / tenToPower[- (int)e] ;
		  else return 0 ;
	}
	
	public BigNumber() {}// 0
	
	public BigNumber(double x) 
	{
	  this.x = x ;
	  this.e = 0 ;
	  normalize() ;
	
	}// 0
	
	public BigNumber(double x, long e) 
	{
	  this.x = x ;
	  this.e = e ;
	  normalize() ;
	
	}// 0
	
	public String toString() 
	{
		return x + " * 10^" + e ;
	}
	
	void normalize()
	{
		if (x == 0)
		{
			e = 0 ;
			return ;
		}
		
		int s = sign() ;
		x *= s ;
		
		while (x >= 10)
		{
		  x /= 10 ; e ++ ;
		}
		
		while (x < 1)
		{
		  x *= 10 ; e -- ;
		}
		
		x *= s ;
	}
	
	void plus(double x1, long e1, double x2, long e2)
	{
		if (e1 > e2)
		{
		  double t = x1 ; x1 = x2 ; x2 = t ;
		  long u = e1 ; e1 = e2 ; e2 = u ;
		}
		// e1 <= e2
		while (e1 < e2)
		{
			e1 ++ ;
			x1 /= 10 ;			
		}
		
		x = x1 + x2 ;
		e = e1 ;
		normalize() ;
		
	}
	
	public void plus(BigNumber a, BigNumber b)
	{
		plus(a.x, a.e, b.x, b.e) ;
	}
	
		
	public void plus(BigNumber b)
	{
		plus(x, e, b.x, b.e) ;
	}
	
	public void times(BigNumber a, BigNumber b)
	{
		times(a.x, a.e, b.x, b.e) ;
	}
	
		
	public BigNumber times(BigNumber b)
	{
		BigNumber r = new BigNumber() ;
   	r.times(x, e, b.x, b.e) ;
   	return r ;
	}
	
	
	void times(double x1, long e1, double x2, long e2)
	{
		x = x1 * x2 ;
		e = e1 + e2 ;
		normalize() ;
	}
	
	public void plusTimes(BigNumber a, BigNumber b)
	{
		BigNumber ab = new BigNumber() ;
		ab.times(a, b) ;
		plus(ab) ;
	}
	
		
	void divide(double x1, long e1, double x2, long e2)
	{
		x = x1 / x2 ;
		e = e1 - e2 ;
		normalize() ;
	}

   public BigNumber divide(BigNumber b)
   {
   	 BigNumber r = new BigNumber() ;
   	 r.divide(x, e, b.x, b.e) ;
   	 return r ;
   }
	
	public static void main(String[] args){
		
		BigNumber a = new BigNumber(-370) ;
		BigNumber b = new BigNumber(1.0/-370) ;
		BigNumber c = new BigNumber(0) ;
		c.plus(a, b) ;
		System.out.println(a + " ; " + b + " ; "  + c + " ; ")  ;
		
		c.times(a, b) ;
		System.out.println(a + " ; " + b + " ; "  + c + " ; ")  ;
		
		System.out.println((new BigNumber(1.0)).divide(new BigNumber(-370)))  ;
		
		BigNumber d = new BigNumber(125, 300) ;
		BigNumber e = new BigNumber(125, - 400) ;
		BigNumber f = new BigNumber(3, 275) ;
		System.out.println(d.toDouble() + " " + e.toDouble() + " " + f.toDouble())  ;
		
		
		
	}
	
}