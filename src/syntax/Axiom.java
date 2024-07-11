package syntax;

class Axiom{
	
	// Une instance de cette classe représente un axiome
	// lead = tail
	// ou un demi axiome
	// head =: tail
	// où head et tail sont des termes pouvant contenir des variables.
	
	final NonGroundTerm head ;
	final NonGroundTerm tail ;
	final boolean half ;
	final static boolean HALF = true ;
	final static boolean FULL = ! HALF ;
	
	Axiom(Term head, Term tail, boolean half)
	{
		this.head = new NonGroundTerm(head) ;
		this.tail = new NonGroundTerm(tail, this.head.vars) ;
		this.half = half ;
	}

}