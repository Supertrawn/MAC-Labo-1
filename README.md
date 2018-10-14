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
