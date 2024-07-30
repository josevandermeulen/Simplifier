This repository contains materials related to a project whose initial goal was to design and implement good methods to simplify regular expressions. The problem of minimizing the length of such expressions is PSPACE-complete but, nevertheless, most large expressions with a small number of letters can be enormously reduced, using very simple reasoning sometimes. Moreover regular languages can be specified (defined) in other ways, such as finite automata or regular equation systems, from which equivalent regular expressions can be constructed; simplifying these expressions can make them much more readable.

The development of methods to simplify regular expressions led us to define specific data structures to represent expressions: Classes of equivalent expressions modulo properties such as associativity, commutativity, and reflexivity of operators are represented by a unique identifier, i.e. an integer. Identifiers can also be viewed (used) as the states of a finite automaton. To represent transitions between states, arrays of integers are used. So our data structures provide an integrated representation of regular languages, i.e. expressions, finite automata, and regular equation systems are represented in the same framework.

Based on the data structures, efficient algorithms are provided not only to simplify expressions but also to pass from a given representation of a regular language to another. In fact, in our system, all representations may contribute to the simplification goal: For example, an expression can be simplified by first computing its minimal deterministic finite automaton and, then, solving the equations constituting this automaton with a specific algorithm. Other simplification rules use algorithms to compare expressions with respect to equivalence or inclusion of the languages they define. Our representation framework is adequate to implement several algorithms either original or based on ideas proposed in the literature. A user of the system can choose the one that suits him best.

On top of the fundamental data structures and algorithms, the system includes an additional layer specifically designed to address the issue of simplification. Each identifier has an associated size (the size of the identified expression) and the system provides a Union-Find data structure that maps each identifier to the identifier of a best expression defining the same regular language.

This ArteFact directory contains the following files:

- BLCTool.jar : a jar file containing a java executable of my system.

- Demo.java : is the source file of a class implementing a demo program
  for understanding and assessing the system. (For information: It can be
  modified and re-compiled using the jar file as a class path, but
  this is not necessary for executing the system: the jar file already contains
  a file Demo.class)

- Tutorial.pdf : explains how to use end check the system
  with the files in the directory testdata.

- commands, commands2 : command lines that you can execute to test the system
  (assuming you have few time or no imagination for inventing your own test cases).

- testdata : a directory containing test files to assess the system.

- papers : a directory containing papers related to the system.
  
  The papers in the directory 'papers' are not in a final version but they
  already provide useful information on how the system works:

  - ComparingExpressions.pdf describes in a generic way the algorithms I, IA, E, 
  EUF, EA, EP, EBLC available in the system.

  - Derivatives.pdf explains the notion of derivatives used in the system and the
  algorithms to compute set of equations (DFA) based on them.

  - EarlyDraft.pdf is an early attempt at describing the objective of the system
  and its architecture. It describes some algorithms not described elsewhere.

  - Tool.pdf is a previous attempt at describing practically what the system does.

  

