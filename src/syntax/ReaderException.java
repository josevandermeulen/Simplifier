package syntax;

class ReaderException extends Exception{
	
	String msg1 ;
	int pos ;
	//String msg2 ;
	
	ReaderException(String msg1, int pos)
	{
		this.msg1 = msg1 ;
		this.pos = pos ;
		//this.msg2 = msg2 ;
	}

}