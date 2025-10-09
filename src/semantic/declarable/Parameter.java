package semantic.declarable;

import exceptions.SemanticException;
import lexical.Token;
import semantic.types.Type;

import static compiler.Main.symbolTable;

public class Parameter {
    private Token idToken;
    private Type type;
    private int position;

    public Parameter(Token idToken, Type type) {
        this.idToken = idToken;
        this.type = type;
    }

    public String getName() {
        return idToken.getLexeme();
    }
    public int getLine() {
        return idToken.getLineNumber();
    }
    public Type getType(){
        return type;
    }
    public void estaBienDeclarado(){
        if(!type.isPrimitive() && (symbolTable.getClass(type.getName())==null)){
            throw new SemanticException("Error semantico en linea "+type.getLine()+" El parametro con nombre "+ idToken.getLexeme() +" fue declarado como tipo de clase inexistente. ",type.getName(), type.getLine());
        }
    }
}
