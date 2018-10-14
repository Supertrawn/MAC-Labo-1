# Labo-1 Indexing and Search with Apache Lecene
## Ludovic Delafontaine & Kevin Pradervand

## Réponse au question: 

### Does the command line demo use stopword removal? Explain how you find out the answer.
	oui,  la demo utilise le standard analyzer qui lui supprime les stopwords
### Does the command line demo use stemming? Explain how you find out the answer.
	non, le standard analyzer n'utilise pas le stemming, mais le english analyzer oui 
### Is the search of the command line demo case insensitive? How did you find out the answer?
	oui, le standard analyzer n'est pas sensible à la case
### Does it matter whether stemming occurs before or after stopword removal? Consider this as a general question.
	oui, attention avec les nom propre comme les nom d'entreprise par exemple, mais puisque pas sensible à la case il risque de passer tout ddroit ur des nom propre.
## Using Different Analyzers
### StandardAnalyzer
	a. 3203 documents et 27099 terms
	b. 19972 terms dans le summary field
	c. top 10 terms dans le summary field : which, system, papaer, computer, can, descibed, given, presented, time, from
	d. 19 élément à 1.44Mo
	e. 
### WhitespaceAnalyzer
	a. 3203 documents et 34827 terms
	b. 26821 terms dans le summary field
	c. top 10 terms dans le summary field : of, the, is, a, and, to, in, for, The, are
	d. 19 élément à 1.80Mo
	e.
### EnglishAnalyzer
	a. 3203 documents et 23010 terms
	b. 16724 terms dans le summary field
	c. top 10 terms dans le summary field : us, which, comput, program, system, present,describ, paper, method, can
	d. 19 élément à 1.32Mo
	e.
### ShingleAnalyzerWrapper (size 2)
	a. 3203 documents et 103070 terms
	b. 85610 terms dans le summary field
	c. top 10 terms dans le summary field : which, system, paper, computer, can, _paper, described, given, presented, time
	d. 16 élément à 3.92Mo
	e.
### ShingleAnalyzerWrapper (size 3)
	a. 3203 documents et 145746 terms
	b. 125776 terms dans le summary field
	c. top 10 terms dans le summary field : algorithm, which, system, computer, paper, can, descibed, time, given, presented
	d. 16 élément à 5.40Mo
	e.
### StopAnalyzer
	a. 3203 documents et 25116 terms
	b. 18658 terms dans le summary field
	c. top 10 terms dans le summary field : algorithm, which, system, computer, paper, described, can, presented, given, time
	d. 16 élément à 1.40Mo
	e.
