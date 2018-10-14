package ch.heigvd.iict.dmg.labo1.queries;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.IndexSearcher;
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

        TermStats[] terms = new TermStats[0];

        Comparator<TermStats> comparator = new HighFreqTerms.DocFreqComparator();

        try {
            terms = HighFreqTerms.getHighFreqTerms(indexReader, numTerms, field, comparator);
        } catch (Exception e) { /* BEST EFFORT */ }

        for (TermStats term : terms) {
            String author = term.termtext.utf8ToString();
            Integer frequency = term.docFreq;

            System.out.println("Author: " + (author.equals("") ? "Unknown" : author) + ", frequencies: " + frequency);
        }

        System.out.println();
    }

    public void query(String q) {
        // TODO student
        // See "Searching" section

        System.out.println("Searching for [" + q + "]");
    }

    public void close() {
        if (this.indexReader != null)
            try {
                this.indexReader.close();
            } catch (IOException e) { /* BEST EFFORT */ }
    }

}
