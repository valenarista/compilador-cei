package exceptions;

public class SemanticException extends RuntimeException {
    public SemanticException(String description,String lexeme,int line) {
        super(description+"\n"+"[Error:"+lexeme+"|"+line+"]");

    }
}
