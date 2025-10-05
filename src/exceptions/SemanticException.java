package exceptions;

public class SemanticException extends RuntimeException {
    public SemanticException(String description,String lexeme,int line) {
        super("[Error:"+lexeme+"|"+line+"]");
        System.out.println(description);
    }
}
