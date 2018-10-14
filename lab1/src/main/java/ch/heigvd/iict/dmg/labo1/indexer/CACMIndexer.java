package ch.heigvd.iict.dmg.labo1.indexer;

import ch.heigvd.iict.dmg.labo1.parsers.ParserListener;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Objects;

public class CACMIndexer implements ParserListener {

    private Directory dir = null;
    private IndexWriter indexWriter = null;

    private Analyzer analyzer = null;
    private Similarity similarity = null;

    public CACMIndexer(Analyzer analyzer, Similarity similarity) {
        this.analyzer = analyzer;
        this.similarity = similarity;
    }

    public void openIndex() {
        // 1.2. create an index writer config
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(OpenMode.CREATE); // create and replace existing index
        iwc.setUseCompoundFile(false); // not pack newly written segments in a compound file:
        //keep all segments of index separately on disk
        if (similarity != null)
            iwc.setSimilarity(similarity);
        // 1.3. create index writer
        Path path = FileSystems.getDefault().getPath("index");
        try {
            this.dir = FSDirectory.open(path);
            this.indexWriter = new IndexWriter(dir, iwc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewDocument(Long id, String authors, String title, String summary) {
        Document doc = new Document();

        FieldType fieldType = new FieldType();

        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        fieldType.setTokenized(true);
        fieldType.setStored(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorPositions(true);
        fieldType.setStoreTermVectorOffsets(true);
        fieldType.freeze();

        String[] authorsList = authors.split(";");

        // Create the fields
        Field idField = new Field("id", id.toString(), fieldType);
        Field titleField = new Field("title", title, fieldType);
        Field authorField;
        Field summaryField;

        // Add the fields to the document
        doc.add(idField);
        doc.add(titleField);

        if (Objects.isNull(authors)) {
            authorField = new StringField("authors", "Unknown", Field.Store.YES);
            doc.add(authorField);
        } else {
            // Add all the authors
            for (String authorName : authorsList) {
                authorField = new StringField("authors", authorName, Field.Store.YES);
                doc.add(authorField);
            }
        }

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

    public void finalizeIndex() {
        if (this.indexWriter != null)
            try {
                this.indexWriter.close();
            } catch (IOException e) { /* BEST EFFORT */ }
        if (this.dir != null)
            try {
                this.dir.close();
            } catch (IOException e) { /* BEST EFFORT */ }
    }

}
