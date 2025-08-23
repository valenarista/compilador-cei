package exceptions;

public class LexicalException extends RuntimeException {
    public LexicalException(String lexeme,int lineNumber) {
        super("[Error:"+lexeme+"|"+lineNumber+"]");
    }
}
