package com.coccoc;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public enum Mode {
        NORMAL(0),
        HOST(1),
        URL(2);

        private int value;

        Mode(int value) {
            this.value = value;
        }
    }

    private static final String libPath;
    private static final String dictPath;

    private native ByteBuffer[] segment(String text, int tokenizeOption);
    private native void freeMemory(ByteBuffer p);
    private native void initialize(String dictPath) throws RuntimeException;

    private static Tokenizer instance;

    static {
        try {
            URI file = Tokenizer.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            libPath = file.resolve("lib/libcoccoc_tokenizer_jni.so").getPath();
            dictPath = file.resolve("dicts").getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not initialize Tokenizer");
        }
    }

    public static Tokenizer getInstance() {
        if (instance == null) {
            instance = new Tokenizer();
        }

        return instance;
    }

    private Tokenizer() {
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            System.load(libPath);
            return null;
        });
        initialize(dictPath);
    }

    public List<Token> tokenize(String text, Mode mode) {
        if (text == null) {
            throw new IllegalArgumentException("text is null");
        }

        ByteBuffer[] segmentResults = segment(text, mode.value);

        IntBuffer normalizedChars = segmentResults[0].order(ByteOrder.nativeOrder()).asIntBuffer();
        IntBuffer rawTokens = segmentResults[1].order(ByteOrder.nativeOrder()).asIntBuffer();
        ByteBuffer pointers = segmentResults[2];

        StringBuilder sb = new StringBuilder();

        while (normalizedChars.hasRemaining()) {
            sb.appendCodePoint(normalizedChars.get());
        }

        String normalizedText = sb.toString();

        int tokensCount = rawTokens.capacity() / 6;
        List<Token> tokens = new ArrayList<Token>(tokensCount);

        for (int i = 0; i < tokensCount; i++) {
            int offset = i * 6;
            int normalizedStart = rawTokens.get(offset);
            int normalizedEnd = rawTokens.get(offset + 1);
            int originalStart = rawTokens.get(offset + 2);
            int originalEnd = rawTokens.get(offset + 3);

            String tokenText = normalizedText.substring(normalizedStart, normalizedEnd);
            int tokenType = rawTokens.get(offset + 4);

            tokens.add(new Token(tokenText, tokenType, originalStart, originalEnd));
        }

        freeMemory(pointers);

        return tokens;
    }

    public List<Token> tokenize(Reader input, Mode mode) throws IOException {
        char[] buffer = new char[1024];
        StringBuilder sb = new StringBuilder();
        int numCharsRead;
        while ((numCharsRead = input.read(buffer, 0, buffer.length)) != -1) {
            sb.append(buffer, 0, numCharsRead);
        }

        return tokenize(sb.toString(), mode);
    }
}
