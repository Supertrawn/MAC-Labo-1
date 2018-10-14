package ch.heigvd.iict.dmg.labo1;

import ch.heigvd.iict.dmg.labo1.indexer.CACMIndexer;
import ch.heigvd.iict.dmg.labo1.parsers.CACMParser;
import ch.heigvd.iict.dmg.labo1.queries.QueriesPerformer;
import ch.heigvd.iict.dmg.labo1.similarities.MySimilarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class Main {

    public static void main(String[] args) {

        // 1.1. create an analyzer
        Analyzer analyser = getAnalyzer();

        // Section "Tuning the Lucene Score"
        Similarity similarity = new ClassicSimilarity();
        //Similarity similarity = new MySimilarity();

        CACMIndexer indexer = new CACMIndexer(analyser, similarity);
        indexer.openIndex();
        CACMParser parser = new CACMParser("documents/cacm.txt", indexer);
        parser.startParsing();
        indexer.finalizeIndex();

        QueriesPerformer queriesPerformer = new QueriesPerformer(analyser, similarity);

        // Section "Reading Index"
        readingIndex(queriesPerformer);

        // Section "Searching"
        searching(queriesPerformer);

        queriesPerformer.close();

    }

    private static void readingIndex(QueriesPerformer queriesPerformer) {
        queriesPerformer.printTopRankingTerms("authors", 10);
        queriesPerformer.printTopRankingTerms("title", 10);
    }

    private static void searching(QueriesPerformer queriesPerformer) {

        //queriesPerformer.query("compiler program");

        queriesPerformer.query("Information Retrieval");
        queriesPerformer.query("Information AND Retrieval");
        queriesPerformer.query("Retrieval AND Information~ -Database");
        queriesPerformer.query("Info*");
        queriesPerformer.query("Information Retrieval~5");

    }

    private static Analyzer getAnalyzer() {
        return new EnglishAnalyzer();
    }

}
