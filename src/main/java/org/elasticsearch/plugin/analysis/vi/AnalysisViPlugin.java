package org.elasticsearch.plugin.analysis.vi;

import java.util.Collections;
import java.util.Map;

import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.index.analysis.VietnameseTokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

public class AnalysisViPlugin extends Plugin implements AnalysisPlugin {
    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return Collections.singletonMap("vi_tokenizer", VietnameseTokenizerFactory::new);
    }
}
