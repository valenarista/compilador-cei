package exceptions;

import lexical.TokenType;

public class SyntacticException extends RuntimeException {
    private String lexema;
    private int nroLinea;
    private TokenType tokenType;
    private String descripcion;

    public SyntacticException(String lexema,int nroLinea,TokenType tokenType, String descripcion){
        this.lexema = lexema;
        this.nroLinea = nroLinea;
        this.tokenType = tokenType;
        this.descripcion = descripcion;
    }

    @Override
    public String getMessage() {
        return "Error Sintactico en linea "+nroLinea+ ": "+descripcion+" Se encontro: '"+lexema+"' Tipo: "+tokenType
                +"\n[Error:"+lexema+"|"+nroLinea+"]";
    }
}
