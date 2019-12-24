package org.elasticsearch.index.analysis;

import java.util.Locale;

import com.coccoc.Tokenizer.Mode;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.vi.VietnameseTokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class VietnameseTokenizerFactory extends AbstractTokenizerFactory {
    private final Mode tokenizeMode;

    public VietnameseTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, settings, name);

        tokenizeMode = getTokenizeMode(settings);
    }

    public static Mode getTokenizeMode(Settings settings)
    {
        String modeSetting = settings.get("mode", "normal").toUpperCase(Locale.ROOT);
        return Mode.valueOf(modeSetting);
    }

    public Tokenizer create() {
        return new VietnameseTokenizer(tokenizeMode);
    }
}
