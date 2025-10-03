package exceptions;

public class SemanticException extends RuntimeException {
    public SemanticException(String name,int line) {
        super("[Error:"+name+"|"+line+"]");
    }
}
