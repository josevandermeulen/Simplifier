package syntax;

class AxiomWriter{
	
	TermWriter tw ;
	
	AxiomWriter(TermWriter tw)
	{
		this.tw = tw ;
	}
	
	public 	String toString(Axiom a) 
	{
	  return tw.toString(a.head.term) + 
	  (a.half == Axiom.HALF ? " =: " : " = ")
	  + tw.toString(a.tail.term) + " " + a.tail.vars() ;
  }
  
  
  
}