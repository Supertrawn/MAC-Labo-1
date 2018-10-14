package ch.heigvd.iict.dmg.labo1.queries;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Comparator;

public class QueriesPerformer {

    private Analyzer analyzer = null;
    private IndexReader indexReader = null;
    private IndexSearcher indexSearcher = null;

    public QueriesPerformer(Analyzer analyzer, Similarity similarity) {
        this.analyzer = analyzer;
        Path path = FileSystems.getDefault().getPath("index");
        Directory dir;
        try {
            dir = FSDirectory.open(path);
            this.indexReader = DirectoryReader.open(dir);
            this.indexSearcher = new IndexSearcher(indexReader);
            if (similarity != null)
                this.indexSearcher.setSimilarity(similarity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void close() {
        if (this.indexReader != null)
            try {
                this.indexReader.close();
            } catch (IOException e) { /* BEST EFFORT */ }
    }
}
