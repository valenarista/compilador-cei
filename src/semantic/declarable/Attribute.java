package semantic.declarable;

import exceptions.SemanticException;
import lexical.Token;
import semantic.types.Type;
import java.util.Objects;

import static compiler.Main.symbolTable;

public class Attribute {
    private Token idToken;
    private Type type;
    private Token visibility;

    public Attribute(Token idToken, Type type,Token visibility){
        this.idToken = idToken;
        this.type = type;
        this.visibility = visibility;
    }

    public String getName(){
        return idToken.getLexeme();
    }
    public int getLine(){
        return idToken.getLineNumber();
    }
    public Type getType(){
        return type;
    }
    public boolean isPublic(){
        return Objects.equals(visibility.getLexeme(), "public");
    }

    public void estaBienDeclarado(){
        if(!type.isPrimitive() && (symbolTable.getClass(type.getName())==null)){
            throw new SemanticException("El atributo fue declarado como tipo de clase inexistente. ",idToken.getLexeme(), idToken.getLineNumber());
        }
    }

}
