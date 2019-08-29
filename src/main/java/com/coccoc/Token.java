package com.coccoc;

public final class Token {
    public enum Type {
        WORD,
        NUMBER;

        private static Type[] values = null;

        static {
            Type.values = Type.values();
        }

        public static Type fromInt(int i) {
            return Type.values[i];
        }
    }

    private final String text;
    private final Type type;
    private final int originalStart;
    private final int originalEnd;

    public Token(String text, int type, int originalStart, int originalEnd) {
        this(text, Type.fromInt(type), originalStart, originalEnd);
    }

    public Token(String text, Type type, int originalStart, int originalEnd) {
        this.text = text;
        this.type = type;
        this.originalStart = originalStart;
        this.originalEnd = originalEnd;
    }

    public String getText() {
        return text;
    }

    public int getLength() {
        return text.length();
    }

    public Type getType() {
        return type;
    }

    public int getOriginalStart() {
        return originalStart;
    }

    public int getOriginalEnd() {
        return originalEnd;
    }

    public String toString() {
        return text;
    }

    public char[] toCharArray() {
        return text.toCharArray();
    }
}
