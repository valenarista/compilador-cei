package exceptions;

import lexical.TokenType;

public class SyntacticException extends RuntimeException {
    public SyntacticException(String lexema,int nroLinea) {
        System.out.println("[Error:"+lexema+"|"+nroLinea+"]");
    }
}
