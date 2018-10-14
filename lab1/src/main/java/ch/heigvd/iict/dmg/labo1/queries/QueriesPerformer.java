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

        Comparator<TermStats> comparator = new HighFreqTerms.DocFreqComparator();

        TermStats[] terms = new TermStats[0];

        try {
            terms = HighFreqTerms.getHighFreqTerms(indexReader, numTerms, field, comparator);
        } catch (Exception e) { /* BEST EFFORT */ }

        for (TermStats term : terms) {
            String author = term.termtext.utf8ToString();
            Integer frequency = term.docFreq;

            System.out.println("Author: " + (author.equals("") ? "<Unknown>" : author) + ", frequencies: " + frequency);
        }

        System.out.println();
    }

    public void query(String q) {
        int nbDocuments = 5000;
        int currentHit = 1;
        int maxHitReturn = 10;

        System.out.println("Searching for [" + q + "]");

        QueryParser parser = new QueryParser("summary", analyzer);

        Query query = null;
        ScoreDoc hits[] = new ScoreDoc[0];

        try {
            query = parser.parse(q);
        } catch (ParseException e) { /* BEST EFFORT */ }

        try {
            hits = indexSearcher.search(query, nbDocuments).scoreDocs;
        } catch (IOException e) { /* BEST EFFORT */ }

        System.out.println("Results found: " + hits.length);


        for (ScoreDoc hit : hits) {
            Document doc = null;
            try {
                doc = indexSearcher.doc(hit.doc);
            } catch (IOException e) { /* BEST EFFORT */ }

            String id = doc.get("id");
            String title = doc.get("title");
            float score = hit.score;

            System.out.println(id + ": " + title + " (" + score + ")");

            if (currentHit != maxHitReturn) {
                currentHit++;
            } else {
                break;
            }
        }
    }

    public void close() {
        if (this.indexReader != null)
            try {
                this.indexReader.close();
            } catch (IOException e) { /* BEST EFFORT */ }
    }

}
