package syntax;
import java.util.Scanner ;

public class Line{

	//static String[] tScmd = new String[]{
	//	{},
	
	static final public byte CMD = 0 ; // a command
	static final public byte BL = CMD + 1 ; // blank line or commentary
	static final public byte EXPR = BL + 1 ; // a correct expression
	static final public byte EOT = EXPR + 1 ; // end of text
	static final public byte ERROR = EOT + 1 ; // bad line
	static final public byte S = ERROR + 1 ; // simplify 5
	static final public byte E = S + 1 ; // check equivalence 6
	static final public byte I = E + 1 ; // check inclusion
	static final public byte M = I + 1 ; // check or change memory
	static final public byte D = M + 1 ; // show MDFA
	static RegExprReader reader ;
	
	byte type ;
	byte typeOfCommand ;
	
  String line ;
  char[] tabC ;
  Term term ;
  String cmd ;
  int memorySize ;
  int nbrLetters ;
  
  boolean interactive;
  
  void nextLine(Scanner in)
  {
  	if (interactive)
  		System.out.print(">") ;
  	
  	try{
  	  line = in.nextLine() ;
  	}
  	catch(java.util.NoSuchElementException e)
  	{
  		line = ";" ;
  	}  	
  }
  
  public Line(boolean interactive)
  {
  	this.interactive = interactive ;
  }
  
  public Line()
  {
  	this(false) ;
  }
 
  
  public byte type()
  {
  	return type ;
  }

    
  public byte typeOfCommand()
  {
  	return typeOfCommand ;
  }

  
  public String getString()
  {
  	return line ;
  }

  
  public Term getTerm()
  {
  	return term ;
  }

    
  public Term getTerm(Term previous)
  {
  	return term ;
  }

  
  public String stringCommand()
  {
  	return cmd ;
  }

  
  public int memorySize()
  {
  	return memorySize ;
  }
  
  public int nbrLetters()
  {
  	return nbrLetters ;
  }

 
  int i ;

  public byte readLine(Scanner in)
  {
 	
  	nextLine(in) ;
  	tabC = line.toCharArray() ;
  	
  	while (isComment())
  	{
  		if (! interactive)
  		System.out.println(line) ;
  	   	
  		nextLine(in) ;
  		tabC = line.toCharArray() ;
  	}
  	
  	if (! interactive)
  	return parseLine() ;
    else 
    {
    	byte type = parseLine() ;
    	while (type == ERROR)
    		type = readLine(in) ;
    	return type ;
    }
  }
  
  public byte parseLine(String line)
  {		
		this.line = line ;
		tabC = line.toCharArray() ;
		return parseLine() ;
  }
   
  public byte parseLine()
  {		
		term = null ;
		cmd = null ;
		memorySize  = - 1;
    nbrLetters = - 1 ;
    
		i = 0 ;
		type = type0() ;
		
		return type ;
  }
  
  String nextToken()
  {
  	while (i != tabC.length && tabC[i] == ' ')
  		i ++ ;
  	
  	int lft = 0 ;
  	
  	while (i + lft != tabC.length && tabC[i + lft] != ' ')
  	{
  		lft ++ ;
  	}
  	
  	char[] tC = new char[lft] ;
  	lft = 0 ;
  	
  	while (i != tabC.length && tabC[i] != ' ')
  	{
  		tC[lft ++] = tabC[i ++] ;
  	}  	
  	  	
  	return String.valueOf(tC) ;
  }
  
  byte type0()
  {
  	String ftok = nextToken() ;
  	
  	if (ftok.equals(";"))
  		return EOT ;
  	
  	if (isCmd(ftok))
  		return CMD ;
  	 	 	
  	term = reader.toTerm(line) ;
  	if (term != null)
  		return EXPR ;
  	
  	return ERROR ;
  }
  
  void parseTail()
  {
  	while (i != tabC.length)
  		{
  			String tok = nextToken() ;
  			if (tok.equals(""))
  				break ;
  			try{
  				int x = Integer.parseInt(tok) ;
  				if (x > 26)
  					memorySize = x ;
  				else
  					nbrLetters = x ;
  		  }
  		  catch(NumberFormatException e){}
  		}
  }
  
  boolean isComment()
  {
  	int i = 0 ;
  	while (i != tabC.length && tabC[i] == ' ')
  		i ++ ;
  	
  	if (i == tabC.length)
  		return true ;
  	
  	if ('2' <= tabC[i] && tabC[i] <= '9') 
  		return true ;
  	
  	if (i + 1 <= tabC.length && tabC[i] == '/'
  		        && tabC[i + 1] == '/')
  	  return true ;
  	  
  	return false ;
  	
  }
  
  boolean isCmd(String cmd)
  {
  	char[] tC = cmd.toCharArray() ;  	
  	this.cmd = cmd ;
  	
  	if (isSimplify(tC))
  	{
  		parseTail() ;
  		typeOfCommand = S ;
  		return true ;  
  	}
  	
  	if (isEq(cmd))
  	{
  		parseTail() ;
  		typeOfCommand = E ;
  		return true ;  
  	}  	
  	
  	if (isInfEq(cmd))
  	{
  		parseTail() ;
  		typeOfCommand = I ;
  		return true ;  
  	}  	
  	
  	if (isDFA(cmd))
  	{
  		parseTail() ;
  		typeOfCommand = D ;
  		return true ;  
  	}  	
  	
  	if (isMemoryCmd(cmd))
  	{
  		parseTail() ;  		
  		typeOfCommand = M ; 		
  		return true ;  
  	}
  	
  	cmd = null ;
  	return false ;
  }
  
  boolean isSimplify(char[] tC)
  {
  	boolean ok = false ;
  	
  	int i = 0 ;
  	while (i != tC.length)
  	{
  		switch(tC[i])
  		{
  	  	case 'S' :
  	  	case 'R' :
  	  	case 'O' : 
  	  	case 'B' : 
  	  	case 'M' : 
  	  	case 'N' : 
  	  	case 'C' : 
  	  		ok = true ;
  	  	case 'I' :
  	  		break ;
  	  		
  	    default : return false ;		
  		}
  		i ++ ;
  	} 	
  	return ok ;
  }
  
  boolean isEq(String cmd)
  {
  	return cmd.equals("E") ||
  	       cmd.equals("EA") ||
  	       cmd.equals("EP") ||
  	       cmd.equals("EUF") ||
  	       cmd.equals("EBLC") ; 	
  }
  
  
  boolean isInfEq(String cmd)
  {
  	return cmd.equals("I") ||
  	       cmd.equals("IA") ; 	
  }
    
  boolean isDFA(String cmd)
  {
  	return cmd.equals("DFA") ; 	
  }
  
    
  boolean isMemoryCmd(String cmd)
  {
  	return cmd.equals("CM") ||
  	       cmd.equals("IM") ; 	
  }
  
  

}