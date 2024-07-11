package syntax;

public class Lexer{

  private int i ; // cur pos
  final private char[] line ;
  
  Lexer(char[] line)
  {
  	this.line = line ;
  	reset() ;
  }
  
  char curChar()
  { 
  	char c ;
  	if (i == line.length)
  		c = 0 ;
  	else
  		c = line[i] ;
  	
  	//System.out.println(c) ;
  	return c ;
  }
  
  char nextChar()
  // Pré : i != line.length
  {
  	int j = i + 1 ;
  	while (j != line.length && line[j] == ' ') j ++ ;
  
  	if (j == line.length)
  		return 0 ;
  	else
  		return line[j] ;  		
  }
  
  void move()
  // Pré : i != line.length
  {
  	i ++ ;
  	while (i != line.length && line[i] == ' ') i ++ ;
  }
  
  void reset()
  {
  	i = - 1 ;
  	move() ;
  }
   
  void reset(int i)
  {
  	this.i = i ;
  }
  
  int pos()
  {
  	return i ;
  }
  
  
  
  

}