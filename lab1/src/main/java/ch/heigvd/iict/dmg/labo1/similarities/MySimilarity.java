package ch.heigvd.iict.dmg.labo1.similarities;

import org.apache.lucene.search.similarities.ClassicSimilarity;

public class MySimilarity extends ClassicSimilarity {
    @Override
    public float tf(float freq) {
        double result = 1 + Math.log(freq);
        return (float) result;
    }

    @Override
    public float idf(long docFreq, long numDocs) {
        double result = Math.log((numDocs / docFreq + 1) + 1);
        return (float) result;
    }

    @Override
    public float coord(int overlap, int maxOverlap) {
        double result = Math.sqrt(overlap / maxOverlap);
        return (float) result;
    }
}
