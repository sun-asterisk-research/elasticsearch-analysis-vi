package org.elasticsearch.index.analysis;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.vi.VietnameseAnalyzer;
import org.apache.lucene.search.suggest.analyzing.SuggestStopFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class VietnameseStopTokenFilterFactory extends AbstractTokenFilterFactory {
    private static final Map<String, Set<?>> NAMED_STOP_WORDS;
    private final CharArraySet stopwords;
    private final boolean ignoreCase;
    private final boolean removeTrailing;

    static {
        NAMED_STOP_WORDS = Collections.singletonMap("_vietnamese_", VietnameseAnalyzer.getDefaultStopSet());
    }

    public VietnameseStopTokenFilterFactory(
        IndexSettings indexSettings, Environment env, String name, Settings settings
    ) {
        super(indexSettings, name, settings);
        this.ignoreCase = settings.getAsBoolean("ignore_case", false);
        this.removeTrailing = settings.getAsBoolean("remove_trailing", true);
        this.stopwords = Analysis.parseWords(
            env, settings, "stopwords", VietnameseAnalyzer.getDefaultStopSet(), NAMED_STOP_WORDS, ignoreCase
        );
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return removeTrailing
            ? new StopFilter(tokenStream, stopwords)
            : new SuggestStopFilter(tokenStream, stopwords);
    }
}
