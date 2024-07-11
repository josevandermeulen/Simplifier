package util ;


class GenDFASamples{




public static void main0(String[] args){

  int nl = Integer.parseInt(args[0]) ;
  //int nl  = Integer.parseInt(args[0]) ;
  // Generate DFA "bad case"
  
  System.out.println(2 + " " + nl) ;
  System.out.println(1 + " " + 1 + " " + 1) ;
  int i = 2 ;
  while (i != nl + 1)
  {
  	
  	System.out.println(0 + " " + (i - 1) + " " + i) ;
  	
  	i ++ ;
  }
}

public static void main1(String[] args){

  int n = Integer.parseInt(args[0]) ;
  //int nl  = Integer.parseInt(args[0]) ;
  // Generate DFA "bad case"
  
  System.out.println(2 + " " + n) ;
  int i = 1 ;
  while (i != n + 1)
  {
  	
  	if (i <= n / 2)
  		System.out.print("1 ") ;
  	else
  	  System.out.print("0 ") ;
  	
  	if (i <= n / 4)
  		System.out.print((n / 2 + 2 * i - 1) + " " + (n / 2 + 2 * i - 1)) ;
  	
  	else
  		
  	if (i <= n / 2)
  		System.out.print((2 * (i - n / 4) - 1) + " " + (2 * (i - n / 4) - 1)) ;
  	
  	else
  		System.out.print((2 * (i - n / 2) - 1) + " " + (2 * (i - n / 2) - 1)) ;
  	
  	System.out.println() ;
  	
  	i ++ ;
  }
}

public static void main(String[] args){

  int n = Integer.parseInt(args[0]) ;
  
  java.util.Random r = new java.util.Random() ;
  
  System.out.println(2 + " " + n) ;
  int i = 1 ;
  while (i != n + 1)
  {
  	double d = r.nextDouble() ;
  	if (d < 0.5)
  		System.out.print("1 ") ;
  	else
  	  System.out.print("0 ") ;
  	
  	d = r.nextDouble() ;
  	
  	int x = ((int)(d * n)) + 1 ;
  	System.out.print(x + " ") ;
  	
  	d = r.nextDouble() ;
  	
  	x = ((int)(d * n)) + 1 ;
  	System.out.println(x + " ") ;
  	 	
  	i ++ ;
  }
}

}















