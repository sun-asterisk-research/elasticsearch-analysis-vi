package org.apache.lucene.analysis.vi;

import java.io.IOException;

import com.coccoc.Tokenizer.Mode;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;

public class VietnameseAnalyzer extends StopwordAnalyzerBase {
    private final Mode mode;

    public VietnameseAnalyzer(Mode mode, CharArraySet stopwords) {
        super(stopwords);
        this.mode = mode;
    }

    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        static {
            try {
                DEFAULT_STOP_SET = loadStopwordSet(true, VietnameseAnalyzer.class, "/stopwords.txt", "#");
            } catch (IOException e) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new VietnameseTokenizer(mode);
        TokenStream stream = new LowerCaseFilter(tokenizer);
        stream = new StopFilter(stream, stopwords);

        return new TokenStreamComponents(tokenizer, stream);
    }
}
