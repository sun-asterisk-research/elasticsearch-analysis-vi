package org.apache.lucene.analysis.vi;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import com.coccoc.Token;
import com.coccoc.Tokenizer.Mode;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class VietnameseTokenizer extends Tokenizer {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

    private Mode tokenizeMode;
    private com.coccoc.Tokenizer tokenizer;
    private Iterator<Token> tokens;

    private int currentOffset = 0;

    public VietnameseTokenizer(Mode tokenizeMode) {
        this.tokenizeMode = tokenizeMode;
        this.tokenizer = com.coccoc.Tokenizer.getInstance();
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();

        if (tokens.hasNext()) {
            final Token token = tokens.next();
            final int tokenLength = token.getLength();
            final int start = correctOffset(token.getOriginalStart());
            final int end = correctOffset(token.getOriginalEnd());

            termAtt.copyBuffer(token.toCharArray(), 0, tokenLength);
            typeAtt.setType(token.getType().name().toLowerCase(Locale.ROOT));
            offsetAtt.setOffset(start, end);
            currentOffset = end;

            return true;
        }

        return false;
    }

    @Override
    public void end() throws IOException {
        super.end();
        int finalOffset = correctOffset(currentOffset);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        currentOffset = 0;
        tokens = tokenizer.tokenize(input, tokenizeMode).iterator();
    }
}
