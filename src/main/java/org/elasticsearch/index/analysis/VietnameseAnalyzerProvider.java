package org.elasticsearch.index.analysis;

import com.coccoc.Tokenizer.Mode;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.vi.VietnameseAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class VietnameseAnalyzerProvider extends AbstractIndexAnalyzerProvider<VietnameseAnalyzer> {
    private final VietnameseAnalyzer analyzer;

    public VietnameseAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);

        final Mode tokenizeMode = VietnameseTokenizerFactory.getTokenizeMode(settings);
        final CharArraySet stopwords = Analysis.parseStopWords(env, settings, VietnameseAnalyzer.getDefaultStopSet());
        analyzer = new VietnameseAnalyzer(tokenizeMode, stopwords);
    }

    @Override
    public VietnameseAnalyzer get() {
        return this.analyzer;
    }
}
