package fichiers ;


import java.io.* ;

//import ordinateur.clavier ;
//import ordinateur.ecran ;

public class text
{

/* Cette classe est suppos�e (wishfull thinking) impl�menter un �quivalent
des fichiers de texte du Pascal standard. Nous allons donc impl�menter les 
fonctionnalit�s suivantes :

1. Un fichier de texte a un *contenu* qui est une suite de caract�res ASCII
d�coup�es en lignes par adjonction d'un caract�re eln (s�parateur de lignes).

2. Un fichier de texte poss�de un *nom externe* qui est le *chemin d'acc�s*
permettant de retrouver son contenu sur le disque dur ou la disquette.

3. Un fichier peut �tre *ferm�* ou *ouvert en lecture* ou *ouvert en �criture*

3.1. Nous allons distinguer deux notions de fichiers :

   + Le *fichier externe* ou *vu de l'exterieur du programme". 
		 Il poss�de un nom externe (voir plus haut). 
		 Il peut �tre ferm� (non utilis� par aucun programme).
		 Il peut �tre utilis� (par un et un seul programme). 
			
		 Le contenu du fichier externe est d�fini uniquement lorsque 
		 le fichier externe est ferm�.
			
	 + Le *fichier interne* ou *vu de l'interieur du programme". 
		 Il poss�de un nom externe (...)
		 Il peut �tre *ferm�* ou *ouvert en lecture* ou *ouvert en �criture*
		 S'il est ferm�, son contenu est ind�fini.
		 S'il est ouvert, ... le blabla habituel.
		
		 Un objet de type text est un fichier interne (et r�ciproquement).
			

4. Les op�rations suivantes sont d�finies sur un fichier de texte :

  // Le d�but et la fin : cr�ation et fermeture 

   
		
*/




private String nom ;

private String etat ; // F, IN ou OUT.
public static final String F   = "F"   ; 
public static final String IN  = "IN"  ; 
public static final String OUT = "OUT" ; 
public static final char TAB = 9 ;

private             int errCode    = 0  ; // Pas d'erreur lors d'une op�ration d'IO.
public static final int CLOSEERR   = 10 ; // Impossible de fermer le fichier externe.
public static final int FERR       = 20 ; // Le fichier interne n'est pas ferm�.
public static final int INERR      = 23 ; // Le fichier interne n'est pas ouvert en lecture.
public static final int RESETERR   = 21 ; // Le fichier externe n'est pas ferm� (est utilis�) ou n'existe pas.
public static final int READERR    = 22 ; // "erreur de lecture"
public static final int EOFERR     = 51 ; // condition eof hors de propos
public static final int EOLNERR    = 52 ; // condition eoln hors de propos
public static final int NUMERR     = 70 ; // suite de caract�re ne repr�sentant pas un nombre.
public static final int REWRITEERR = 110 ;// Le fichier externe ne peut �tre r��crit.
public static final int WRITEERR   = 111 ;// "erreur d'�criture"
public static final int OUTERR     = 112 ;// le fichier n'est pas ouvert en �criture.

public  int    ioError(){ return errCode ; }

public text(String nomExterne) 
// Pr�  : 
// Post : un objet de type *fichier de texte* est cr�� et sa ref est renvoy�e.
//        il est ferm�.
// Codes d'erreur :
{ 
  nom = nomExterne ;
	etat = F ;
}

private BufferedReader input ;
private BufferedWriter output ;


public void close () 
// Pr�  : le fichier est ouvert.
// Post : le fichier interne est ferm� ;
//        le fichier externe de m�me nom est ferm� et a pour contenu
//        celui du fichier interne (au moment de l'appel).
// Codes d'erreur : FERR : le fichier n'est pas ouvert au d�part.
//                  CLOSEERR : impossible de fermer le fichier externe.
{
  errCode = 0 ;
	
	if (etat==F) errCode = FERR ;

 if (etat==IN)
	try { input.close() ;  } catch(Exception e) { errCode = CLOSEERR ; }
	
	if (etat==OUT)
	try { output.close() ; } catch(Exception e) { errCode = CLOSEERR ; }
	
	etat = F ;
	
}

		
//  LECTURE

private boolean eof ;
private char[] buf ;
private int i ;

public void reset() 
// Pr�  : le fichier est ferm�.
// Post : si un fichier externe ferm� existait, ce fichier est utilis�
//        le fichier interne est ouvert en lecture
//        son contenu accessible est le contenu initial du fichier externe.
// Codes d'erreur :
//   INERR    : le fichier n'est pas ferm� au d�part.
//   RESETERR : le fichier est d�j� ouvert (utilis� par un autre programme ou celui-ci)
//              ou il n'existe pas
//   READERR  : pas possible de lire la premi�re ligne du fichier.
{
  errCode = 0 ;

  if (etat==F)
	{
	  try
		{ 
		  input = new BufferedReader(new FileReader(nom)) ; 
			etat  = IN ;
			try
		  { 
		    String line = input.readLine() ;
				if (line==null) eof = true ;
				else { eof = false ;
				       buf = line.toCharArray() ;
							 i   = 0 ;
				     }
		  }
		  catch(Exception e)
		  { eof = true ; errCode = READERR ; }
		}
		catch(Exception e)
		{ errCode = RESETERR ; }
	}
	else errCode = FERR	;
		
}


public boolean eof() 
// Pr�  : le fichier est ouvert en lecture.
// R�sultat : (boolean)("le contenu accessible est vide.")
// Codes d'erreur : INERR : le fichier n'est pas ouvert en lecture.
{
  errCode = 0 ;
	
  if (etat==IN) return eof ;
	else { errCode = INERR ; return true ; }
}


public boolean eoln() 
// Pr� :  le fichier est ouvert en lecture ;
//        le contenu accessible n'est pas vide.
// R�sultat : (boolean)("le caract�re courant vaut eln.")
// Codes d'erreur : 
//   INERR  : le fichier n'est pas ouvert en lecture.
//   EOFERR : le contenu accessible est vide.
{
  errCode = 0 ;
	
  if (etat==IN)
	   if (!eof) return (i==buf.length) ;
		 else { errCode = EOFERR ; return true ; }
	else { errCode = INERR ; return true ; }
}


public boolean tab() 
// Pr� :  le fichier est ouvert en lecture ;
//        le contenu accessible n'est pas vide.
// R�sultat : (boolean)("le caract�re courant vaut TAB.")
// Codes d'erreur : 
//   INERR  : le fichier n'est pas ouvert en lecture.
//   EOFERR : le contenu accessible est vide.
{
  errCode = 0 ;
	
  if (etat==IN)
	   if (!eof) {if (i!=buf.length) 
			             return buf[i]==TAB ;
							  else return false ; }
		 else { errCode = EOFERR ; return true ; }
	else { errCode = INERR ; return true ; }
}



public char read_char()  
// Pr� :  le fichier est ouvert en lecture.
//        le contenu accessible n'est pas vide.
//        le caract�re courant n'est pas eln.
//        
// R�sultat : le caract�re courant au moment de l'appel
// Post : le contenu accessible est amput� de son premier caract�re.
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : le contenu accessible est vide.
//   EOLNERR : le caract�re courant est eln.
{
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    if (!eof)
			{
			    if (i!=buf.length) return buf[i++] ;
					else { errCode = EOLNERR ; return ' ' ; }
			}
			else { errCode = EOFERR ; return ' ' ; }
	}
	else { errCode = INERR ; return ' ' ; }
}		


public String readStringUntilTab()  
// Pr� :  le fichier est ouvert en lecture.
//        le contenu accessible n'est pas vide.
//        le caract�re courant n'est pas eln.
//        
// R�sultat : le string form� de tous les caract�res suivants de la ligne courante
//        jusqu'au premier TAB (exclu) ou jusqu'� la fin de ligne.
// Post : le contenu accessible est amput� des caract�res lu (le TAB inclus, mais pas le eln).
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : le contenu accessible est vide.
//   EOLNERR : le caract�re courant est eln.
{
  String s = "" ;
	boolean fin = false ;
	char c = read_char() ;
	if (c==TAB) fin = true ;
  while (!fin)
	{ 
	  s += c ; 
		if (eof()||eoln()) fin = true ;
		else { c = read_char() ;
		       if (c==TAB) fin = true ;
				 }
	}
	
	return s ;
}		


public String readStringUntilSep()  
// Pr� :  le fichier est ouvert en lecture.
//        le contenu accessible n'est pas vide.
//        le caract�re courant n'est pas eln.
//        
// R�sultat : le string form� de tous les caract�res suivants de la ligne courante
//        jusqu'au premier TAB (exclu) ou jusqu'� la fin de ligne.
// Post : le contenu accessible est amput� des caract�res lu (le TAB inclus, mais pas le eln).
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : le contenu accessible est vide.
//   EOLNERR : le caract�re courant est eln.
{
  String s = "" ;
	boolean fin = false ;
	char c = read_char() ;
	if ((c==TAB)|(c==' ')) fin = true ;
  while (!fin)
	{ 
	  s += c ; 
		if (eof()||eoln()) fin = true ;
		else { c = read_char() ;
		       if ((c==TAB)|(c==' ')) fin = true ;
				 }
	}
	
	return s ;
}		


	
public char first_char()  // "lookup" du premier caract�re � lire.
// Pr� :  le fichier est ouvert en lecture.
//        
// R�sultat : le caract�re courant au moment de l'appel ou
//            le caract�re blanc ' ' si le caract�re courant est eln
//                                ou si le contenu accessible est vide.
// Post : le contenu accessible n'est *pas* modifi�.
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
{
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    if (!eof)
			{
			    if (i!=buf.length) return buf[i] ;
					else  return ' ' ; 
			}
			else  return ' ' ; 
	}
	else { errCode = INERR ; return ' ' ; }
}		
	
public String readString()  
// Pr� :  le fichier est ouvert en lecture.
//        
// R�sultat : le String constitu� du pr�fixe du contenu accessible 
//        au moment de l'appel allant jusqu'au premier caract�re eln
//        exclu (s'il existe) ou jusqu'� la fin du fichier.
// Post : le contenu accessible est amput� de tous les caract�res du string
//        mais pas du premier eln qui le suit (s'il existe)
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
{
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    if (!eof)
			{
			    char[] suf = new char[buf.length - i] ;
					int j = 0 ;
					while (i!=buf.length)
					{ suf[j] = buf[i] ; j++ ; i++ ; }
					
					return String.valueOf(suf) ;
			}
			else  return "" ; 
	}
	else { errCode = INERR ; return "" ; }
}				


public void skipSeparators()
// Pr�  : le fichier est ouvert en lecture.
//        le contenu accessible est de la forme :
//
//        alpha beta
//
//        o� alpha ne contient que des espaces et des eln
//
// Post : le contenu accessible vaut beta.
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
{
  if (etat==IN)
     while(!eof&&((i==buf.length)||buf[i]==' '||buf[i]==TAB))
	      if (i==buf.length)
			      readln() ;
		    else i ++  ;
	else { errCode = INERR ; }
}



public byte  read_byte()  { return (byte) read_long() ;  }
public short read_short() { return (short) read_long() ; }
public int   read_int()   { return (int) read_long() ;   }
public long  read_long()  
// Pr� :  le fichier est ouvert en lecture.
//        le contenu accessible est de la forme
//        
//        alpha num beta
//
//        o� alpha est une suite d'espaces et de eln
//           num est une suite de chiffres d�cimaux pr�c�d�e �ventuellement d'un "-"
//           beta ne commence pas par un chiffre.
//
// R�sultat : la valeur de type long (byte, short, int) repr�sent�e par num
// Post : le contenu accessible == beta
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : alpha n'est suivi d'aucun caract�re.
//   NUMERR  : alpha n'est pas suivi d'une suite de chiffres d�cimaux pr�c�d�e �ventuellement d'un "-" ;
//             dans ce cas le contenu accessible est amput� seulement de alpha.
{
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    skipSeparators() ;
			
	    if (!eof)
			{
			    int j = i ;
					if (buf[j]=='-') j++ ;
					while ( j!=buf.length && (buf[j]>='0' & buf[j]<='9') ) j++ ;
					
			    char[] num = new char[j - i] ;
					
					int savei = i ;
					int k = 0 ;
					while (i!=j)
					{ num[k] = buf[i] ; k++ ; i++ ; }
					
					try{ return Long.parseLong(String.valueOf(num)) ; }
					catch(Exception e)
					{ errCode = NUMERR ; i = savei ; return -5 ; }
			}
			else { errCode = EOFERR ; return 237 ; }
	}
	else { errCode = INERR ; return 567 ; }
	
}		

public float  read_float()   { return (float) read_double() ;   } 	
public double read_double()  
// Pr� :  le fichier est ouvert en lecture.
//        le contenu accessible est de la forme
//        
//        alpha num beta
//
//        o� alpha est une suite d'espaces et de eln
//           num est un litt�ral de type double (sans d ou D final)
//           beta commence par un espace ou un eln ou est vide.
//
// R�sultat : la valeur de type double repr�sent�e par num
// Post : le contenu accessible == beta
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : alpha n'est suivi d'aucun caract�re.
//   NUMERR  : alpha n'est pas un litt�ral de type double (sans d ou D final) ;
//             dans ce cas le contenu accessible est amput� seulement de alpha.
//
// Note : cette sp�cification suppose qu'on utilise une version de Java qui respecte
// la sp�cification JLS (ISBN 0-201-63451-1). Mais dans l'impl�mentation du JDK 1.6
// que j'utilise, la sp�cification n'est pas correctement impl�ment�e et il est permis
// que le litt�ral se termine par f ou F ou d ou D.
{
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    skipSeparators() ;
			
	    if (!eof)
			{
			    int j = i ;
					while ( j!=buf.length && (buf[j]!=' ') ) j++ ;
					
			    char[] num = new char[j - i] ;
					
					int savei = i ;
					int k = 0 ;
					while (i!=j)
					{ num[k] = buf[i] ; k++ ; i++ ; }
					
					try{ return Double.valueOf(String.valueOf(num)).doubleValue() ; }
					catch(Exception e)
					{ errCode = NUMERR ; i = savei ; return -5 ; }
			}
			else { errCode = EOFERR ; return 237 ; }
	}
	else { errCode = INERR ; return 567 ; }
}		


public void readln()  
// Pr� :  le fichier est ouvert en lecture.
//        le contenu accessible n'est pas vide.
//        
// Post : le contenu accessible est amput� d'un pr�fixe allant
//        jusqu'au premier eln (inclus) ou jusqu'� la fin du fichier
//        s'il n'y a plus de eln dans le contenu accessible.
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : le contenu accessible est vide.
//   READERR : impossible de lire la ligne suivante.
{
  errCode = 0 ;

  if (etat==IN) 
	{
	   if (!eof)
	       try
		     { 
		        String line = input.readLine() ;
				    if (line==null) eof = true ;
				    else { eof = false ;
				           buf = line.toCharArray() ;
							     i   = 0 ;
				         }
				  }
		      catch(Exception e)
		     { eof = true ; errCode = READERR ; }
		 else errCode = EOFERR ;	
	}
	else errCode = INERR	;
		
}

public char   readln_char()   { char   x = read_char()   ; readln() ; return x ; }
public byte   readln_byte()   { byte   x = read_byte()   ; readln() ; return x ; }
public short  readln_short()  { short  x = read_short()  ; readln() ; return x ; }
public int    readln_int()    { int    x = read_int()    ; readln() ; return x ; }
public long   readln_long()   { long   x = read_long()   ; readln() ; return x ; }
public float  readln_float()  { float  x = read_float()  ; readln() ; return x ; }
public double readln_double() { double x = read_double() ; readln() ; return x ; }
public String readlnString()  { String x = readString()  ; readln() ; return x ; }
		

//  ECRITURE -----------------------------------------------------------------



public void rewrite() 
// Pr�  : le fichier est ferm�.
// Post : le fichier interne est ouvert en �criture
//        son contenu  est vide.
// Codes d'erreur :
//   FERR       : le fichier n'est pas ferm�.
//   REWRITEERR : le fichier externe est d�j� utilis� (ou est bloqu�, verrouill�, etc.)
{
  errCode = 0 ;

  if (etat==F)
	{
	  try
		{ 
		  output = new BufferedWriter(new FileWriter(nom)) ; 
			etat  = OUT ;
		}
		catch(Exception e)
		{ errCode = REWRITEERR ; }
	}
	else errCode = FERR	;
		
}
		

public void write(char c)  
// Pr� :  le fichier est ouvert en �criture.
//        
// Post : c est ajout� � la fin du contenu du fichier
// Codes d'erreur :
//   OUTERR   : le fichier n'est pas ouvert en �criture.
//   WRITEERR : impossible d'�crire (va savoir pourquoi !)
{

  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.write(c) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}		
	
public void write(byte x)     {  write("" + x) ; }	
public void write(short x)    {  write("" + x) ; }	
public void write(int x)      {  write("" + x) ; }	
public void write(long x)     {  write("" + x) ; }	
public void write(float x)    {  write("" + x) ; }	
public void write(double x)   {  write("" + x) ; }	
public void write(boolean x)  {  write("" + x) ; }	

public void write(String s)  
// Pr� :  le fichier est ouvert en �criture.
//        
// Post : s est ajout� � la fin du contenu du fichier
// Codes d'erreur :
//   OUTERR   : le fichier n'est pas ouvert en �criture.
//   WRITEERR : impossible d'�crire (va savoir pourquoi !)
{
  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.write(s, 0, s.length()) ; 		
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
	
}		

public void writeln(char x)     {  write(x) ; writeln() ; }	
public void writeln(byte x)     {  write(x) ; writeln() ; }	
public void writeln(short x)    {  write(x) ; writeln() ; }	
public void writeln(int x)      {  write(x) ; writeln() ; }	
public void writeln(long x)     {  write(x) ; writeln() ; }	
public void writeln(float x)    {  write(x) ; writeln() ; }	
public void writeln(double x)   {  write(x) ; writeln() ; }	
public void writeln(boolean x)  {  write(x) ; writeln() ; }	
public void writeln(String x)   {  write(x) ; writeln() ; }	

	
public void writeln()  
// Pr� :  le fichier est ouvert en �criture.
//        
// Post : eln est ajout� � la fin du contenu du fichier
// Codes d'erreur :
//   OUTERR   : le fichier n'est pas ouvert en �criture.
//   WRITEERR : impossible d'�crire (va savoir pourquoi !)
{
  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.newLine(); 		
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}			

public void writeTab() {  write(TAB) ; }


}

