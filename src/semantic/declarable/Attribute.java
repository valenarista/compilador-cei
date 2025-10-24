package semantic.declarable;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.types.Type;
import java.util.Objects;

import static compiler.Main.symbolTable;

public class Attribute {
    private Token idToken;
    private Type type;
    private Token visibility;
    private ExpressionNode value;

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
    public Token getVisibility(){
        return visibility;
    }
    public void setValue(ExpressionNode value){
        this.value = value;
    }
    public ExpressionNode getValue(){
        return value;
    }

    public void estaBienDeclarado(){
        if(!type.isPrimitive() && (symbolTable.getClass(type.getName())==null)){
            throw new SemanticException("Error semantico en linea "+type.getLine()+" El atributo de nombre "+ idToken.getLexeme() +" fue declarado como tipo de clase inexistente. ",type.getName(), type.getLine());
        }
    }
    public void chequeoSentencias(){
        if(value != null){
            Type valueType = value.check();
            if(!valueType.isSubtypeOf(type)){
                if(type.isPrimitive() && valueType.getName().equals("null")){
                    throw new SemanticException("Error semantico en linea "+value.getLine()+" El atributo de nombre "+ idToken.getLexeme() +" fue inicializado con null pero es de tipo primitivo. ", value.getLexeme(), value.getLine());
                }
                if(!valueType.getName().equals("null")) {
                    throw new SemanticException("Error semantico en linea " + value.getLine() + " El atributo de nombre " + idToken.getLexeme() + " fue inicializado con un tipo incompatible. ", value.getLexeme(), value.getLine());
                }
            }
        }
    }

}
