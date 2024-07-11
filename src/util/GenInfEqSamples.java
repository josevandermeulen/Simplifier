package util ;


class GenInfEqSamples{

public static void main0(String[] args){

  String s1 = "(a + b)*a" ;
  String s2 = "(a*b)*a" ;
  String s3 = "a*" ;

  System.out.println("2 1000 100") ;
  int i = 0 ;
  while (i != 50)
  {
  	s1 += "(a + b)" ;
  	s2 += "a"  ;
  	System.out.println(s2 + s3 + " + " + s1) ;
  	System.out.println(s1) ;
  	i ++ ;
  }
  
  System.out.println(";") ;

}

public static void main1(String[] args){

  String x1 = "(a*b)*" ;
  String x2 = "ab" ;
  String x3 = "a*" ;
  
  String y1 = "(a + b)*b" ;
  String y2 = "" ;
  String y3 = "ab*" ;

  System.out.println("2 1000 100") ;
  int i = 0 ;
  while (i != 50)
  {
  	System.out.println(x1 + x2 + x3) ;
  	System.out.println(y1 + y2 + "(" + y3 + ")*") ;
  	x2 += "abab" ;
  	y2 += "ab*" ;
  	y3 += "ab*" ;
  	i ++ ;
  }
  System.out.println(";") ;
}


public static void main2(String[] args){

  String s1 = "(a + b)*a" ;
  String s2 = "(a*b)*a" ;
  String s3 = "a*" ;

  System.out.println("2 1000 50") ;
  int i = 0 ;
  while (i != 50)
  {
  	s1 += "(a + b)" ;
  	s2 += "a"  ;
  	System.out.print("( " + s2 + s3 + " \\ ") ;
  	System.out.println(s1 + ")") ;
  	i ++ ;
  }

}


public static void main3(String[] args){

  String s1 = "(a + b)*a" ;
  String s2 = "(a*b)*a" ;
  String s3 = "a*" ;

  System.out.println("2 1000 50") ;
  int i = 0 ;
  while (i != 50)
  {
  	s1 += "(a + b)" ;
  	s2 += "a"  ;
  	System.out.print("( " + s1 + " \\ ") ;
  	System.out.println(s2 + s3 + ")") ;
  	i ++ ;
  }

}

public static void main4(String[] args){

  String x1 = "(b*a)*" ;
  String x2 = "ba" ;
  String x3 = "b*" ;
  
  String y1 = "(a + b)*b" ;
  String y2 = "" ;
  String y3 = "ab*" ;

  System.out.println("2 1000 100") ;
  int i = 0 ;
  while (i != 50)
  {
  	System.out.print("(" + x1 + x2 + x3 + " \\ ") ;
  	System.out.println(y1 + y2 + "(" + y3 + ")*" + ")") ;
  	x2 += "baba" ;
  	y2 += "ab*" ;
  	y3 += "ab*" ;
  	i ++ ;
  }
  System.out.println(";") ;
}


public static void main5(String[] args){

  String x1 = "(b*a)*" ;
  String x2 = "ba" ;
  String x3 = "b*" ;
  
  String y1 = "(a + b)*b" ;
  String y2 = "" ;
  String y3 = "ab*" ;

  System.out.println("2 1000 100") ;
  int i = 0 ;
  while (i != 50)
  {
  	System.out.print("(" + y1 + y2 + "(" + y3 + ")*" 
  		+ " \\ " + x1 + x2 + x3 ) ;
  	System.out.println(")") ;
  	x2 += "baba" ;
  	y2 += "ab*" ;
  	y3 += "ab*" ;
  	i ++ ;
  }
  System.out.println(";") ;
}
public static void main6(String[] args){

  
  
  String y1 = "(a + b)*b" ;
  String y2 = "" ;
  String y3 = "ab*" ;

  System.out.println("2 1000 50") ;
  int i = 0 ;
  while (i != 50)
  {
  	System.out.println( y1 + y2 + "(" + y3 + ")*" ) ;
  	y2 += "ab*" ;
  	y3 += "ab*" ;
  	i ++ ;
  }
  System.out.println(";") ;
}

public static void main7(String[] args){

  
  
  String y1 = "(a + b)*" ;
  String y2 = "" ;
  String[] y3 = new String[20] ;
  
  int i = 1 ;
  String s = "" ;
  y3[0] = "" ;
  while (i != y3.length)
  {
  	s += "(a + b)" ;
  	y3[i] = s ;
  	i ++ ;
  }

  //System.out.println("2 1000 50") ;
  int n = 20 ;
  while (n != 0)
  {
  	n -- ;
  	System.out.println(y1 + y3[n] ) ;
  }
  System.out.println(";") ;
}



public static void main8(String[] args){

//(a + b)*a(a + b)^n + (a + b)*b(a + b)^n)*
//(a + b)*(a + b)(a + b)^{n+1}
  
  String y1 = "(a + b)*a" ;
  String y2 = "(a + b)*b" ;
  String y3 = "(a + b)*(a + b)" ;
  
  String xn = "" ;

  //System.out.println("3 1000 50") ;
  System.out.println("2 100 100") ;
  int n = 0 ;
  while (n != 50)
  {
  	 
  	 System.out.print(y1  + xn  + " + ") ;
  	 System.out.println(y2 +  xn) ;
  	 System.out.println(y3 + xn) ;
  	 n ++ ;
  	 xn += "(a + b)" ;

  }
  System.out.println(";") ;
}

public static void main9(String[] args){

  //(a + b)*a(a + b)^n(a(a + b)^n)* + (a + b)*b(a + b)^n(c(a + b)^n)*
//(a + b)*(a + b)(a + b)^n((a+b)^n+1)*
  
  String y1 = "(a + b)*a" ;
  String y2 = "(a + b)*b" ;
  String y3 = "(a + b)*(a + b)" ;
  
  String xn = "" ;

  //System.out.println("3 1000 50") ;
  System.out.println("2 1000 50") ;
  int n = 0 ;
  while (n != 11)
  {
  	 System.out.println(y3 + "(a + b)" + xn + "(" + "(a + b)" + xn + ")*") ;
  	 System.out.print(y1  + xn + "(" + "a" + xn + ")*" + " + ") ;
  	 //System.out.println(y2 +  xn + "(" + "c" + xn + ")*") ;
  	 System.out.println(y2 +  xn + "(" + "b" + xn + ")*") ;
  	 xn += "(a + b)" ;
  	 n ++ ;
  }
  System.out.println(";") ;
}

static String truc(String[] t, int iSpecial, String expr)
{
	String r = t[iSpecial] + "(" ;
	
  int i  = 1 ;
	if (iSpecial == 0)
	{
		r += t[1] ;
		i = 2 ;
	}
	else
	{
		r += t[0] ;
  }
  
	while (i != t.length)
	{
		if (i != iSpecial)
		{
			r += " + " + t[i] ;
		}
		i ++ ;
	}
	return r + " + " + expr + ")" ;
}

public static void main10(String[] args){

 int n = Integer.parseInt(args[0]) ;

 String[] t = new String[]{"a", "b"} ;
 int i = 1 ;
 while (i != n)
 {
 	 String[] newt = new String[t.length * 2] ;

 	 {
 	 	 int j = 0 ;
 	 	 while (j != t.length)
 	 	 {
 	 	 	 newt[j] = "a" + t[j] ;
 	 	 	 j ++ ;
 	 	 }
 	 }
 	 
 	 {
 	 	 int j = 0 ;
 	 	 while (j != t.length)
 	 	 {
 	 	 	 newt[j + t.length] = "b" + t[j] ;
 	 	 	 j ++ ;
 	 	 }
 	 }	 
 	 t = newt ;
 	 i ++ ;
 }
  
 
 i = 1 ;
 String s1 = truc(t, 0, "(a + b)*") ;
 while (i != t.length)
 {
 		s1 += " + " + truc(t, i, "(a + b)*") ;
 		i ++ ;
 }
 
System.out.println("2 1000 50") ;

 System.out.println(s1) ;
 
 i = 0 ;
 String s2 = truc(t, t.length/2 , "(ab*a + ba*b)*(1 + ab* + ba*)") ;
 while (i != t.length)
 {
 	  if (i != t.length / 2)
 		s2 += " + " + truc(t, i, "(ab*a + ba*b)*(1 + ab* + ba*)") ;
 		i ++ ;
 }
 
  System.out.println(s2) ;
  System.out.println(";") ;
}


public static void main(String[] args){

  int nbr = Integer.parseInt(args[0]) ;
  

  System.out.println("2 1000 100") ;
  
  String[] s = new String[]{""} ;
  int i = 0 ;
  while (i != nbr)
  {
  	String[] s2 = new String[s. length * 2] ;
  	int j = 0 ;
  	while (j != s.length)
  	{
  		s2[j] = 'a' + s[j] ;
  		j ++ ;  		
  	}
  	
  	j = 0 ;
  	while (j != s.length)
  	{
  		s2[j + s.length] = 'b' + s[j] ;
  		j ++ ;  		
  	}  	
  	i ++ ;
  	
  	s = s2 ;
  	System.out.print(s[0]) ;
  	int k = 1 ;
  	while (k != s.length)
  	{
  		System.out.print(" + " + s[k]) ;
  		k ++ ;
  	}
  	System.out.println() ;
  }
  
  System.out.println(";") ;

}

}















