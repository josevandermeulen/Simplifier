package fichiers ;


import java.io.* ;

//import ordinateur.clavier ;
//import ordinateur.ecran ;

public class text
{

/* Cette classe est supposée (wishfull thinking) implémenter un équivalent
des fichiers de texte du Pascal standard. Nous allons donc implémenter les 
fonctionnalités suivantes :

1. Un fichier de texte a un *contenu* qui est une suite de caractères ASCII
découpées en lignes par adjonction d'un caractère eln (séparateur de lignes).

2. Un fichier de texte possède un *nom externe* qui est le *chemin d'accès*
permettant de retrouver son contenu sur le disque dur ou la disquette.

3. Un fichier peut être *fermé* ou *ouvert en lecture* ou *ouvert en écriture*

3.1. Nous allons distinguer deux notions de fichiers :

   + Le *fichier externe* ou *vu de l'exterieur du programme". 
		 Il possède un nom externe (voir plus haut). 
		 Il peut être fermé (non utilisé par aucun programme).
		 Il peut être utilisé (par un et un seul programme). 
			
		 Le contenu du fichier externe est défini uniquement lorsque 
		 le fichier externe est fermé.
			
	 + Le *fichier interne* ou *vu de l'interieur du programme". 
		 Il possède un nom externe (...)
		 Il peut être *fermé* ou *ouvert en lecture* ou *ouvert en écriture*
		 S'il est fermé, son contenu est indéfini.
		 S'il est ouvert, ... le blabla habituel.
		
		 Un objet de type text est un fichier interne (et réciproquement).
			

4. Les opérations suivantes sont définies sur un fichier de texte :

  // Le début et la fin : création et fermeture 

   
		
*/




private String nom ;

private String etat ; // F, IN ou OUT.
public static final String F   = "F"   ; 
public static final String IN  = "IN"  ; 
public static final String OUT = "OUT" ; 
public static final char TAB = 9 ;

private             int errCode    = 0  ; // Pas d'erreur lors d'une opération d'IO.
public static final int CLOSEERR   = 10 ; // Impossible de fermer le fichier externe.
public static final int FERR       = 20 ; // Le fichier interne n'est pas fermé.
public static final int INERR      = 23 ; // Le fichier interne n'est pas ouvert en lecture.
public static final int RESETERR   = 21 ; // Le fichier externe n'est pas fermé (est utilisé) ou n'existe pas.
public static final int READERR    = 22 ; // "erreur de lecture"
public static final int EOFERR     = 51 ; // condition eof hors de propos
public static final int EOLNERR    = 52 ; // condition eoln hors de propos
public static final int NUMERR     = 70 ; // suite de caractère ne représentant pas un nombre.
public static final int REWRITEERR = 110 ;// Le fichier externe ne peut être réécrit.
public static final int WRITEERR   = 111 ;// "erreur d'écriture"
public static final int OUTERR     = 112 ;// le fichier n'est pas ouvert en écriture.

public  int    ioError(){ return errCode ; }

public text(String nomExterne) 
// Pré  : 
// Post : un objet de type *fichier de texte* est créé et sa ref est renvoyée.
//        il est fermé.
// Codes d'erreur :
{ 
  nom = nomExterne ;
	etat = F ;
}

private BufferedReader input ;
private BufferedWriter output ;


public void close () 
// Pré  : le fichier est ouvert.
// Post : le fichier interne est fermé ;
//        le fichier externe de même nom est fermé et a pour contenu
//        celui du fichier interne (au moment de l'appel).
// Codes d'erreur : FERR : le fichier n'est pas ouvert au départ.
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
// Pré  : le fichier est fermé.
// Post : si un fichier externe fermé existait, ce fichier est utilisé
//        le fichier interne est ouvert en lecture
//        son contenu accessible est le contenu initial du fichier externe.
// Codes d'erreur :
//   INERR    : le fichier n'est pas fermé au départ.
//   RESETERR : le fichier est déjà ouvert (utilisé par un autre programme ou celui-ci)
//              ou il n'existe pas
//   READERR  : pas possible de lire la première ligne du fichier.
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
// Pré  : le fichier est ouvert en lecture.
// Résultat : (boolean)("le contenu accessible est vide.")
// Codes d'erreur : INERR : le fichier n'est pas ouvert en lecture.
{
  errCode = 0 ;
	
  if (etat==IN) return eof ;
	else { errCode = INERR ; return true ; }
}


public boolean eoln() 
// Pré :  le fichier est ouvert en lecture ;
//        le contenu accessible n'est pas vide.
// Résultat : (boolean)("le caractère courant vaut eln.")
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
// Pré :  le fichier est ouvert en lecture ;
//        le contenu accessible n'est pas vide.
// Résultat : (boolean)("le caractère courant vaut TAB.")
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
// Pré :  le fichier est ouvert en lecture.
//        le contenu accessible n'est pas vide.
//        le caractère courant n'est pas eln.
//        
// Résultat : le caractère courant au moment de l'appel
// Post : le contenu accessible est amputé de son premier caractère.
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : le contenu accessible est vide.
//   EOLNERR : le caractère courant est eln.
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
// Pré :  le fichier est ouvert en lecture.
//        le contenu accessible n'est pas vide.
//        le caractère courant n'est pas eln.
//        
// Résultat : le string formé de tous les caractères suivants de la ligne courante
//        jusqu'au premier TAB (exclu) ou jusqu'à la fin de ligne.
// Post : le contenu accessible est amputé des caractères lu (le TAB inclus, mais pas le eln).
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : le contenu accessible est vide.
//   EOLNERR : le caractère courant est eln.
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
// Pré :  le fichier est ouvert en lecture.
//        le contenu accessible n'est pas vide.
//        le caractère courant n'est pas eln.
//        
// Résultat : le string formé de tous les caractères suivants de la ligne courante
//        jusqu'au premier TAB (exclu) ou jusqu'à la fin de ligne.
// Post : le contenu accessible est amputé des caractères lu (le TAB inclus, mais pas le eln).
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : le contenu accessible est vide.
//   EOLNERR : le caractère courant est eln.
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


	
public char first_char()  // "lookup" du premier caractère à lire.
// Pré :  le fichier est ouvert en lecture.
//        
// Résultat : le caractère courant au moment de l'appel ou
//            le caractère blanc ' ' si le caractère courant est eln
//                                ou si le contenu accessible est vide.
// Post : le contenu accessible n'est *pas* modifié.
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
// Pré :  le fichier est ouvert en lecture.
//        
// Résultat : le String constitué du préfixe du contenu accessible 
//        au moment de l'appel allant jusqu'au premier caractère eln
//        exclu (s'il existe) ou jusqu'à la fin du fichier.
// Post : le contenu accessible est amputé de tous les caractères du string
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
// Pré  : le fichier est ouvert en lecture.
//        le contenu accessible est de la forme :
//
//        alpha beta
//
//        où alpha ne contient que des espaces et des eln
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
// Pré :  le fichier est ouvert en lecture.
//        le contenu accessible est de la forme
//        
//        alpha num beta
//
//        où alpha est une suite d'espaces et de eln
//           num est une suite de chiffres décimaux précédée éventuellement d'un "-"
//           beta ne commence pas par un chiffre.
//
// Résultat : la valeur de type long (byte, short, int) représentée par num
// Post : le contenu accessible == beta
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : alpha n'est suivi d'aucun caractère.
//   NUMERR  : alpha n'est pas suivi d'une suite de chiffres décimaux précédée éventuellement d'un "-" ;
//             dans ce cas le contenu accessible est amputé seulement de alpha.
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
// Pré :  le fichier est ouvert en lecture.
//        le contenu accessible est de la forme
//        
//        alpha num beta
//
//        où alpha est une suite d'espaces et de eln
//           num est un littéral de type double (sans d ou D final)
//           beta commence par un espace ou un eln ou est vide.
//
// Résultat : la valeur de type double représentée par num
// Post : le contenu accessible == beta
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : alpha n'est suivi d'aucun caractère.
//   NUMERR  : alpha n'est pas un littéral de type double (sans d ou D final) ;
//             dans ce cas le contenu accessible est amputé seulement de alpha.
//
// Note : cette spécification suppose qu'on utilise une version de Java qui respecte
// la spécification JLS (ISBN 0-201-63451-1). Mais dans l'implémentation du JDK 1.6
// que j'utilise, la spécification n'est pas correctement implémentée et il est permis
// que le littéral se termine par f ou F ou d ou D.
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
// Pré :  le fichier est ouvert en lecture.
//        le contenu accessible n'est pas vide.
//        
// Post : le contenu accessible est amputé d'un préfixe allant
//        jusqu'au premier eln (inclus) ou jusqu'à la fin du fichier
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
// Pré  : le fichier est fermé.
// Post : le fichier interne est ouvert en écriture
//        son contenu  est vide.
// Codes d'erreur :
//   FERR       : le fichier n'est pas fermé.
//   REWRITEERR : le fichier externe est déjà utilisé (ou est bloqué, verrouillé, etc.)
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
// Pré :  le fichier est ouvert en écriture.
//        
// Post : c est ajouté à la fin du contenu du fichier
// Codes d'erreur :
//   OUTERR   : le fichier n'est pas ouvert en écriture.
//   WRITEERR : impossible d'écrire (va savoir pourquoi !)
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
// Pré :  le fichier est ouvert en écriture.
//        
// Post : s est ajouté à la fin du contenu du fichier
// Codes d'erreur :
//   OUTERR   : le fichier n'est pas ouvert en écriture.
//   WRITEERR : impossible d'écrire (va savoir pourquoi !)
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
// Pré :  le fichier est ouvert en écriture.
//        
// Post : eln est ajouté à la fin du contenu du fichier
// Codes d'erreur :
//   OUTERR   : le fichier n'est pas ouvert en écriture.
//   WRITEERR : impossible d'écrire (va savoir pourquoi !)
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

