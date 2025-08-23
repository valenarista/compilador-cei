package lexical;

public class Token {
    private TokenType type;
    private String lexeme;
    private int lineNumber;

    public Token(TokenType type, String lexeme, int lineNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
