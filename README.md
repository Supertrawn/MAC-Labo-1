# MAC - Labo 1 - Indexing and Search with Apache Lecene
Auteurs: Ludovic Delafontaine & Kevin Pradervand

## Réponses aux questions

### Understand the Lucene API
1. *`Does the command line demo use stopword removal? Explain how you find out the answer.`*

	Oui,  la demo utilise le `StandardAnalyzer` qui lui supprime les stopwords.

	"*Filters StandardTokenizer with StandardFilter, LowerCaseFilter and StopFilter, using a list of English stop words.*" - [Class `StandardAnalyzer`](https://lucene.apache.org/core/7_4_0/core/org/apache/lucene/analysis/standard/StandardAnalyzer.html).

2. *`Does the command line demo use stemming? Explain how you find out the answer.`*
   
	Non, le `StandardAnalyzer` n'utilise pas le stemming.

	"*Filters StandardTokenizer with StandardFilter, LowerCaseFilter and StopFilter, using a list of English stop words.*" - [Class `StandardAnalyzer`](https://lucene.apache.org/core/7_4_0/core/org/apache/lucene/analysis/standard/StandardAnalyzer.html). 

3. *`Is the search of the command line demo case insensitive? How did you find out the answer?`*

	Oui, le `StandardAnalyzer` n'est pas sensible à la case.

	"*Filters StandardTokenizer with StandardFilter, LowerCaseFilter and StopFilter, using a list of English stop words.*" - [Class `StandardAnalyzer`](https://lucene.apache.org/core/7_4_0/core/org/apache/lucene/analysis/standard/StandardAnalyzer.html). 
   
4. *`Does it matter whether stemming occurs before or after stopword removal? Consider this as a general question.`*
   
	Oui, par exemple avec les noms propres comme les noms d'entreprise qui pourraient être mal indexés si un stemming leur est appliqué ("*Coming from Hell*", un éventuel groupe de musique -> "*come hell*") et donc changer les résultats.

	TODO - pas le meilleur exemple..?

### Indexing and Searching the CACM collection - Indexing

- `id` & `authors` peuvent être de type `StringField` car nous n'avons pas besoin de faire d'opérations particulières dessus, telles que tokeniser.
- `title` & `summary` doivent être d'un type particulier (`FieldType`) car il est nécessaire d'effectuer des traitements particuliers dessus (voir le code ci-dessous).

**Notes**

- Il n'aurait pas été possible d'utiliser le type `TextField` car il ne contient pas les vecteurs de termes (*"A field that is indexed and tokenized, without term vectors."* - [Class `TextField`](https://lucene.apache.org/core/5_5_0/core/index.html?org/apache/lucene/document/TextField.html)).
- Le type `StringField` est utile pour les éléments qu'il n'est pas nécessaires de tokeniser, tels que les identifiants ou les champs uniques. C'est la raison pour laquelle il est utilisé pour l'identifiant de document et les auteurs ("*A field that is indexed but not tokenized: the entire String value is indexed as a single token. For example this might be used for a 'country' field or an 'id' field, or any field that you intend to use for sorting or access through the field cache.*" - [Class `StringField`](https://lucene.apache.org/core/5_5_0/core/index.html?org/apache/lucene/document/StringField.html)).

```java
@Override
public void onNewDocument(Long id, String authors, String title, String summary) {
	Document doc = new Document();

	// Define a new type of field (custom kind)
	FieldType fieldType = new FieldType();

	// Use documents, frequencies and positions to compare
	// all documents with the other ones.
	fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);

	// Tokenize the field
	fieldType.setTokenized(true);

	// The field must be stored (kept) with the document
	fieldType.setStored(true);

	// Enable the term vectors
	fieldType.setStoreTermVectors(true);

	// Enable the term vector of positions for the document
	fieldType.setStoreTermVectorPositions(true);

	// Enable the term vector of offsets for the document
	fieldType.setStoreTermVectorOffsets(true);

	// Prevents future changes to the field
	fieldType.freeze();

	// Create the fields
	Field idField = new StringField("id", id.toString(), Field.Store.YES);
	Field titleField = new Field("title", title, fieldType);
	Field authorField;
	Field summaryField;

	// Add the fields to the document
	doc.add(idField);
	doc.add(titleField);

	// Label the authors as "Unknown" if unknown
	if (Objects.isNull(authors)) {
		authorField = new StringField("authors", "Unknown", Field.Store.YES);
		doc.add(authorField);
	} else {
		// Add all the authors
		for (String authorName : authors.split(";")) {
			authorField = new StringField("authors", authorName, Field.Store.YES);
			doc.add(authorField);
		}
	}

	// Label the summary empty if empty
	if (Objects.isNull(summary)) {
		summaryField = new Field("summary", "", fieldType);
	} else {
		summaryField = new Field("summary", summary, fieldType);
	}

	doc.add(summaryField);

	try {
		this.indexWriter.addDocument(doc);
	} catch (IOException e) {
		e.printStackTrace();
	}
}
```

### Indexing and Searching the CACM collection - Using different Analyzers
Tous les résultats ont été obtenus avec:

```java
Similarity similarity = new ClassicSimilarity();
```

et:

```java
private static Analyzer getAnalyzer() {
	return new StandardAnalyzer();
}
```

#### StandardAnalyzer

```java
private static Analyzer getAnalyzer() {
	return new StandardAnalyzer();
}
```

1. Number of indexed documents: 3203
2. Number of indexed terms: 27099
3. Number of index terms in the summary field: 19972
4. The top 10 frequent terms of the summary field in the index: 
	1. *which*
	2. *system*
	3. *paper*
	4. *computer*
	5. *can*
	6. *descibed*
	7. *given*
	8. *presented*
	9. *time*
	10. *from*
5. The size of the index on disk: 19 éléments à 1.44Mo
6. The required time for indexing: TODO

#### WhitespaceAnalyzer

```java
private static Analyzer getAnalyzer() {
	return // TODO
}
```

1. Number of indexed documents: 3203
2. Number of indexed terms: 34827
3. Number of index terms in the summary field: 26821
4. The top 10 frequent terms of the summary field in the index: 
	1. *of*
	2. *the*
	3. *is*
	4. *a*
	5. *and*
	6. *to*
	7. *in*
	8. *for*
	9. *The*
	10. *are*
5. The size of the index on disk: 19 éléments à 1.80Mo
6. The required time for indexing: TODO

#### EnglishAnalyzer

```java
private static Analyzer getAnalyzer() {
	return new EnglishAnalyzer();
}
```

1. Number of indexed documents: 3203
2. Number of indexed terms: 23010
3. Number of index terms in the summary field: 16724
4. The top 10 frequent terms of the summary field in the index: 
	1. *us*
	2. *which*
	3. *comput*
	4. *program*
	5. *system*
	6. *present*
	7. *describ*
	8. *paper*
	9. *method*
	10. *can*
5. The size of the index on disk: 19 éléments à 1.32Mo
6. The required time for indexing: TODO

#### ShingleAnalyzerWrapper (size 2)

```java
private static Analyzer getAnalyzer() {
	return // TODO
}
```

1. Number of indexed documents: 3203
2. Number of indexed terms: 103070
3. Number of index terms in the summary field: 85610
4. The top 10 frequent terms of the summary field in the index: 
	1. *which*
	2. *system*
	3. *paper*
	4. *computer*
	5. *can*
	6. *paper*
	7. *described*
	8. *given*
	9. *presented*
	10. *time*
5. The size of the index on disk: 16 élément à 3.92Mo
6. The required time for indexing: TODO

#### ShingleAnalyzerWrapper (size 3)

```java
private static Analyzer getAnalyzer() {
	return // TODO
}
```

1. Number of indexed documents: 3203
2. Number of indexed terms: 145746
3. Number of index terms in the summary field: 125776
4. The top 10 frequent terms of the summary field in the index: 
	1. *algorithm*
	2. *which*
	3. *system*
	4. *computer*
	5. *paper*
	6. *can*
	7. *descibed*
	8. *time*
	9. *given*
	10. *presented*
5. The size of the index on disk: 16 éléments à 5.40Mo
6. The required time for indexing: TODO

#### StopAnalyzer

```java
private static Analyzer getAnalyzer() {
	return // TODO
}
```

1. Number of indexed documents: 3203
2. Number of indexed terms: 25116
3. Number of index terms in the summary field: 18658
4. The top 10 frequent terms of the summary field in the index: 
	1. *algorithm*
	2. *which*
	3. *system*
	4. *computer*
	5. *paper*
	6. *described*
	7. *can*
	8. *presented*
	9. *given*
	10. *time*
5. The size of the index on disk: 16 éléments à 1.40Mo
6. The required time for indexing: TODO

### Indexing and Searching the CACM collection - Reading Index

1. What is the author with the highest number of publications? How
many publications does he/she have?

	Comme nous avons décidé d'incorporer l'auteur "`Unknown`" dans l'index lorsque l'auteur est inconnu, "l'auteur" qui est le plus de publications est le regroupement de tous les auteurs inconnus.
	
	Au sens strict par contre, l'auteur qui a le plus de publications est `Thacher Jr., H. C.` avec 38 publications.

	```text
	Author: <Unknown>, frequencies: 84
	Author: Thacher Jr., H. C., frequencies: 38
	Author: Naur, P., frequencies: 19
	Author: Hill, I. D., frequencies: 16
	Author: Wirth, N., frequencies: 15
	Author: Pike, M. C., frequencies: 14
	Author: Herndon, J. R., frequencies: 14
	Author: Gautschi, W., frequencies: 14
	Author: Boothroyd, J., frequencies: 14
	Author: George, R., frequencies: 12
	```

2. List the top 10 terms in the title field with their frequency.
   
   ```text
	Top ranking terms for field [title] are: 
	Author: algorithm, frequencies: 961
	Author: computer, frequencies: 260
	Author: system, frequencies: 172
	Author: programming, frequencies: 154
	Author: method, frequencies: 124
	Author: data, frequencies: 110
	Author: systems, frequencies: 108
	Author: language, frequencies: 99
	Author: program, frequencies: 93
	Author: matrix, frequencies: 82
   ```

```java
public void printTopRankingTerms(String field, int numTerms) {
	System.out.println("Top ranking terms for field [" + field + "] are: ");

	// Create the comparator
	Comparator<TermStats> comparator = new HighFreqTerms.DocFreqComparator();

	TermStats[] terms = null;

	try {
		terms = HighFreqTerms.getHighFreqTerms(indexReader, numTerms, field, comparator);
	} catch (Exception e) {
		e.printStackTrace();
	}

	// Display all the terms
	for (TermStats term : terms) {
		String author = term.termtext.utf8ToString();
		author = author.equals("") ? "<Unknown>" : author;
		
		Integer frequency = term.docFreq;

		System.out.println("Author: " + author + ", frequencies: " + frequency);
	}

	System.out.println();
}
```

### Indexing and Searching the CACM collection - Searching
Les résultats ont été obtenus avec:

```java
private static Analyzer getAnalyzer() {
	return new EnglishAnalyzer();
}
```

1. Publications containing the term "Information Retrieval".
   
	```java
	queriesPerformer.query("Information Retrieval");
	```

	```text
   	TODO
   	```

2. Publications containing both "Information" and "Retrieval".
   
	```java
	queriesPerformer.query("Information AND Retrieval");
	```

	```text
   	TODO
   	```

3. Publications containing at least the term "Retrieval" and, possibly
"Information" but not "Database".
   
	```java
	queriesPerformer.query("Retrieval AND Information~ -Database");
	```

	```text
   	TODO
   	```

4. Publications containing a term starting with "Info".
   
	```java
	queriesPerformer.query("Info*");
	```

	```text
   	TODO
   	```

5. Publications containing the term "Information" close to "Retrieval"
(max distance 5).
   
	```java
	queriesPerformer.query("Information Retrieval~5");
	```

	```text
   	TODO
   	```

```java
public void query(String q) {
	// The maximum number of documents to consider.
	// As we have 3203 documents, we can consider
	// all of them.
	int nbDocuments = 5000;

	// Ending hit. This allow to limit the number
	// of return results.
	int endHit = 10;

	System.out.println("Searching for [" + q + "]");

	QueryParser parser = new QueryParser("summary", analyzer);

	Query query = null;

	try {
		query = parser.parse(q);
	} catch (ParseException e) {
		e.printStackTrace();
	}

	ScoreDoc hits[] = null;

	try {
		hits = indexSearcher.search(query, nbDocuments).scoreDocs;
	} catch (IOException e) {
		e.printStackTrace();
	}

	System.out.println("Results found: " + hits.length);


	// Starting hit
	int startHit = 1;
	for (ScoreDoc hit : hits) {
		Document doc = null;

		try {
			doc = indexSearcher.doc(hit.doc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String id = doc.get("id");
		String title = doc.get("title");
		float score = hit.score;

		System.out.println(id + ": " + title + " (" + score + ")");

		// Only show the wanted results
		if (startHit != endHit) {
			startHit++;
		} else {
			break;
		}
	}
        
	System.out.println();
}
```

### Indexing and Searching the CACM collection - Tuning the Lucene Score
Tous les résultats ont été obtenus avec:

```java
private static Analyzer getAnalyzer() {
	return new EnglishAnalyzer();
}
```

et:

```java
queriesPerformer.query("compiler program");
```

Les résultats diffèrent entre `ClassicSimilarity()` et `MySimilarity()` car les critères de similitudes entre les documents et la requête ont été redéfinis par ceux issus du cours. On peut remarquer que le nombre de résultats restent les mêmes mais que l'ordre (= la pertinance entre les documents et la requête) des résultats ne se correspondent plus du tout. Seuls quelques résultats persistent entre les deux classes.

#### `ClassicSimilarity()`

```text
Searching for [compiler program]
Results found: 578
3189: An Algebraic Compiler for the FORTRAN Assembly Program (1.0367663)
1459: Requirements for Real-Time Languages (0.9427518)
2652: Reduction of Compilation Costs Through Language Contraction (0.93779767)
1183: A Note on the Use of a Digital Computerfor Doing Tedious Algebra and Programming (0.8799802)
1465: Program Translation Viewed as a General Data Processing Problem (0.82941306)
1988: A Formalism for Translator Interactions (0.82941306)
1647: WATFOR-The University of Waterloo FORTRAN IV Compiler (0.8082413)
1237: Conversion of Decision Tables To Computer Programs (0.7542014)
2944: Shifting Garbage Collection Overhead to Compile Time (0.7542014)
637: A NELIAC-Generated 7090-1401 Compiler (0.74336267)
```

#### `MySimilarity()`

```text
Searching for [compiler program]
Results found: 578
2534: Design and Implementation of a Diagnostic Compiler for PL/I (7.6723757)
637: A NELIAC-Generated 7090-1401 Compiler (7.104498)
2923: High-Level Data Flow Analysis (6.930661)
2652: Reduction of Compilation Costs Through Language Contraction (6.682171)
1647: WATFOR-The University of Waterloo FORTRAN IV Compiler (6.6194205)
1465: Program Translation Viewed as a General Data Processing Problem (5.9500484)
1988: A Formalism for Translator Interactions (5.9500484)
3189: An Algebraic Compiler for the FORTRAN Assembly Program (5.9500484)
1135: A General Business-Oriented Language Based on Decision Expressions* (5.3405066)
1237: Conversion of Decision Tables To Computer Programs (5.3405066)
```

```java
public class MySimilarity extends ClassicSimilarity {
    @Override
    public float tf(float freq) {
        return (float) (1 + Math.log(freq));
    }

    @Override
    public float idf(long docFreq, long numDocs) {
        return (float) Math.log((numDocs / docFreq + 1) + 1);
    }

    @Override
    public float coord(int overlap, int maxOverlap) {
        return (float) Math.sqrt(overlap / maxOverlap);
    }

    @Override
    public float lengthNorm(FieldInvertState state) {
        return 1;
    }
}
```
