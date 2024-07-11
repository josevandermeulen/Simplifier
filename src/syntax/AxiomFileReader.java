package syntax;

import fichiers.text ;


class AxiomFileReader{
	
	AxiomReader ar ;
	Lexer l ;
	int nbrAxioms ;
	String fileName ;
	
	AxiomFileReader(String fileName, TermReader tr)
	{
		this.fileName = fileName ;
		textFileToLexer(fileName) ;
		tr.putLexer(l) ;
		ar = new AxiomReader(tr) ;
	}
	
	
	void textFileToLexer(String fileName)
  {
  	nbrAxioms = 0 ;
  	text file = new text(fileName) ;
		file.reset() ;
		if (file.ioError() != 0)
			throw new Error("Impossible d'ouvrir le fichier " + fileName) ;
		// Compter les (demi-)axiomes
		// et les caractères significatifs (avant //)
		int nbrChars = 0 ;
		while (! file.eof())
		{
			String toto = file.readlnString() ;
			char[] a = toto.toCharArray() ;
			int i = 0 ;
			while (i != a.length && 
				!(i <= a.length - 2 && a[i] == '/' && a[i + 1] == '/'))
			{
				if (a[i] == ';')
					nbrAxioms ++ ;
				i ++ ;
			}
			nbrChars += i + 1 ;
		}
		file.close() ;
		
		// Construire un tableau de chars avec les caractères
		// significatifs en séparant les lignes par un espace		
		file.reset() ;		
		char[] line = new char[nbrChars] ;
		{int j = 0 ;
		while (! file.eof())
		{
			String toto = file.readlnString() ;
			char[] a = toto.toCharArray() ;
			int i = 0 ;
			while (i != a.length && 
				!(i <= a.length - 2 && a[i] == '/' && a[i + 1] == '/'))
			{
				line[j ++] = a[i ++] ;
			}
			line[j ++] = ' ' ;
		}}
		file.close() ;
		
		l = new Lexer(line) ;
		
  }
  
  static int numLine ;
  static int posInLine ;
  static String theLine ;
  static void findTheLine(int ici, String fileName)
  {
  	text file = new text(fileName) ;
		file.reset() ;
		// Compter les (demi-)axiomes
		// et les caractères significatifs (avant //)
		int nbrChars = 0 ;
		numLine = 0 ;
		while (! file.eof())
		{
			String toto = file.readlnString() ;
			
			System.err.println(numLine + " : " + toto) ;
			
			char[] a = toto.toCharArray() ;
			int i = 0 ;
			while (i != a.length && 
				!(i <= a.length - 2 && a[i] == '/' && a[i + 1] == '/'))
			{
				i ++ ;
			}
			
			int nbrCharsIncludingPreviousLine = nbrChars ;
			nbrChars += i + 1 ;
			if (nbrChars > ici)
			{
				posInLine = ici - nbrCharsIncludingPreviousLine ;
				theLine = toto ;
				break ;
			}							
			numLine ++ ;
			
		}
		file.close() ;
  }
  
  static String makePointer(int i)
  {
  	char[] a = new char[i + 1] ;
  	int j = 0 ;
  	while (j != i)
  		a[j ++] = '-' ;
  	a[i] = '^' ;
  	
  	return String.valueOf(a) ;
  }

	
	
	
	Axiom[] toAxiomArray()
	{		
		Axiom[] axiomArray = new Axiom[nbrAxioms] ;
		
		try{
		// Construire le tableau
		int j = 0 ;
		while (j != axiomArray.length)
		{
			Axiom a = ar.makeAxiom() ; 
			l.move() ;
			axiomArray[j ++] = a ;
		}}
		catch(ReaderException e)
		{
			findTheLine(e.pos, fileName) ;
			
			System.err.println(e.msg1 + " à la position " + 
				posInLine + " de la ligne " +
				numLine + ":") ;
			System.out.println(theLine) ;
			System.out.println(makePointer(posInLine)) ;
			
			return null ;
		}
		
		return axiomArray ;
	}
	
	public static void main(String[] args) //throws Exception
	{

		AxiomFileReader afr = 
		new AxiomFileReader("Axioms/Axioms" + args[0] + ".txt",
				new RegExprReader()) ;
		
		Axiom[] aA = afr.toAxiomArray() ;
		
		if (aA != null)
		{
			AxiomWriter aw = new AxiomWriter(new RegExprWriter()) ;
			
			int i = 0 ;
			while (i != aA.length)
			{
				Axiom a = aA[i ++] ;
				if (a == null)
					break ;
				System.out.println(aw.toString(a)) ;
			}
		}
	}
	
}

