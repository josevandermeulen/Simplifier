package fichiers ;

// date : fait � la mer appart. de Ren� avril 2001.

import java.io.* ;
//import ordinateur.clavier ;
//import ordinateur.ecran ;

public class binaryFile
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

private             int errCode    = 0  ; // Pas d'erreur lors d'une op�ration d'IO.
public static final int CLOSEERR   = 10 ; // Impossible de fermer le fichier externe.
public static final int FERR       = 20 ; // Le fichier interne n'est pas ferm�.
public static final int INERR      = 23 ; // Le fichier interne n'est pas ouvert en lecture.
public static final int RESETERR   = 21 ; // Le fichier externe n'est pas ferm� (est utilis�) ou n'existe pas.
public static final int READERR    = 22 ; // "erreur de lecture"
public static final int EOFERR     = 51 ; // condition eof hors de propos
public static final int NODATERR   = 70 ; // le nombre de bytes restant � lire est plus petit
                                          // que la taille de la donn�e � lire.        
public static final int REWRITEERR = 110 ;// Le fichier externe ne peut �tre r��crit.
public static final int WRITEERR   = 111 ;// "erreur d'�criture"
public static final int OUTERR     = 112 ;// le fichier n'est pas ouvert en �criture.

public  int    ioError(){ return errCode ; }

public binaryFile(String nomExterne) 
// Pr�  : 
// Post : un objet de type *fichier de texte* est cr�� et sa ref est renvoy�e.
//        il est ferm�.
// Codes d'erreur :
{ 
  nom = nomExterne ;
	etat = F ;
}

private DataInputStream  input  ;
private DataOutputStream output ;


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

public void reset() 
// Pr�  : le fichier est ferm�.
// Post : si un fichier externe ferm� existait, ce fichier est utilis�
//        le fichier interne est ouvert en lecture
//        son contenu accessible est le contenu initial du fichier externe.
// Codes d'erreur :
//   INERR    : le fichier n'est pas ferm� au d�part.
//   RESETERR : le fichier est d�j� ouvert (utilis� par un autre programme ou celui-ci)
//              ou il n'existe pas.
{
  errCode = 0 ;

  if (etat==F)
	{
	  try
		{ 
		  input = new DataInputStream(new BufferedInputStream(new FileInputStream(nom))) ; 
			etat  = IN ;
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
	
  if (etat==IN) 
	{  input.mark(1) ; 
	   int by ;
	   try{by = input.read() ; }catch(Exception e){ return true ; }
	   if (by==-1) return true ;
		 else try{input.reset(); return false ; }
			    catch(Exception e)
					{return true ; }    
	}
	else { errCode = INERR ; return true ; }
}

public byte read_byte()
{  return private_read_byte() ; }

private byte private_read_byte()  
// Pr� :  le fichier est ouvert en lecture.
//        le contenu accessible n'est pas vide.
//        
// R�sultat : le byte courant au moment de l'appel
// Post     : le contenu accessible est amput� de son premier byte.
// Codes d'erreur :
//   INERR   : le fichier n'est pas ouvert en lecture.
//   EOFERR  : le contenu accessible est vide.
{
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    if (!eof())
			{
	        try{return input.readByte() ; }
					catch(Exception e)
					{ return 0 ; }
		       
			}
			else { errCode = EOFERR ; return 0 ; }
	}
	else { errCode = INERR ; return 0 ; }
}		

public char read_char()
{
  int i = private_read_byte() ;
	if (i>=0) return (char) i ;
	else return (char)(256 + i) ;
	
}

public char  readUnicode_char()  
{ 
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    if (!eof())
			{
	        input.mark(2) ;
					try
					{  return input.readChar() ; }
					catch(Exception e)
					{ 
		         try
						 { input.reset();
							 errCode = NODATERR ;
							 return 0 ; 
						 }
			       catch(Exception e1)
					   { errCode = READERR ;
							 return 0 ; 
						 }
					}
			}
			else { errCode = EOFERR ; return 0 ; }
	}
	else { errCode = INERR ; return 0 ; }
}


public short read_short() 
{ 
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    if (!eof())
			{
	        input.mark(2) ;
					try
					{  return input.readShort() ; }
					catch(Exception e)
					{ 
		         try
						 { input.reset();
							 errCode = NODATERR ;
							 return 0 ; 
						 }
			       catch(Exception e1)
					   { errCode = READERR ;
							 return 0 ; 
						 }
					}
			}
			else { errCode = EOFERR ; return 0 ; }
	}
	else { errCode = INERR ; return 0 ; }
}

public int   read_int()
{ 
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    if (!eof())
			{
	        input.mark(4) ;
					try
					{  return input.readInt() ; }
					catch(Exception e)
					{ 
		         try
						 { input.reset();
							 errCode = NODATERR ;
							 return 0 ; 
						 }
			       catch(Exception e1)
					   { errCode = READERR ;
							 return 0 ; 
						 }
					}
			}
			else { errCode = EOFERR ; return 0 ; }
	}
	else { errCode = INERR ; return 0 ; }
}

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
	    if (!eof())
			{
	        input.mark(8) ;
					try
					{  return input.readLong() ; }
					catch(Exception e)
					{ 
		         try
						 { input.reset();
							 errCode = NODATERR ;
							 return 0 ; 
						 }
			       catch(Exception e1)
					   { errCode = READERR ;
							 return 0 ; 
						 }
					}
			}
			else { errCode = EOFERR ; return 0 ; }
	}
	else { errCode = INERR ; return 0 ; }
}

public float  read_float()
{ 
  errCode = 0 ;
	
  if (etat==IN) 
	{
	    if (!eof())
			{
	        input.mark(4) ;
					try
					{  return input.readFloat()  ; }
					catch(Exception e)
					{ 
		         try
						 { input.reset();
							 errCode = NODATERR ;
							 return 0 ; 
						 }
			       catch(Exception e1)
					   { errCode = READERR ;
							 return 0 ; 
						 }
					}
			}
			else { errCode = EOFERR ; return 0 ; }
	}
	else { errCode = INERR ; return 0 ; }
}

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
	    if (!eof())
			{
	        input.mark(8) ;
					try
					{  return input.readDouble() ; }
					catch(Exception e)
					{ 
		         try
						 { input.reset();
							 errCode = NODATERR ;
							 return 0 ; 
						 }
			       catch(Exception e1)
					   { errCode = READERR ;
							 return 0 ; 
						 }
					}
			}
			else { errCode = EOFERR ; return 0 ; }
	}
	else { errCode = INERR ; return 0 ; }
}

		

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
		  output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(nom))) ; 
			etat  = OUT ;
		}
		catch(Exception e)
		{ errCode = REWRITEERR ; }
	}
	else errCode = FERR	;
		
}
		

public void write(byte b)  
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
		  output.writeByte(b) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	


public void write(char c)  
// Pr� :  le fichier est ouvert en �criture.
//        c est un caract�re ASCII
//        
// Post : c est ajout� � la fin du contenu du fichier sur un byte
// Codes d'erreur :
//   OUTERR   : le fichier n'est pas ouvert en �criture.
//   WRITEERR : impossible d'�crire (va savoir pourquoi !)
{

  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.writeByte((byte)c) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	


	
public void writeUnicode(char x)     
{

  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.writeChar(x) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	


public void write(short x)    
{

  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.writeShort(x) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	

	
public void write(int x)
{

  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.writeInt(x) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	

	
public void write(long x)     {

  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.writeLong(x) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	

	
public void write(float x)    {

  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.writeFloat(x) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	

	
public void write(double x)   {

  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.writeDouble(x) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	

	
public void write(boolean x)  {

  errCode = 0 ;

  if (etat==OUT)
	{
	  try
		{ 
		  output.writeBoolean(x) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	

	

public void writeUnicode(String s)  
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
		  output.writeChars(s) ; 
			
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	

	

public void write(String s)  
// Pr� :  le fichier est ouvert en �criture.
//        s est un string de caract�res ASCII.
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
		  char[] c = s.toCharArray() ;
			int i = 0 ;
			while (i!=c.length)
			{  output.writeByte((byte)c[i]) ; i++ ; }
		}
		catch(Exception e)
		{ errCode = WRITEERR ; }
	}
	else errCode = OUTERR	;
}	

}

