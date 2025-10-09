package semantic.declarable;

import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.types.Type;

import java.util.HashMap;
import java.util.List;

import static compiler.Main.symbolTable;

public class Method {
    private HashMap<String,Parameter> parameters;
    private List<Parameter> paramList;
    private Token idToken;
    private Type returnType;
    private Token modifier;
    private Token visibility;
    private boolean hasBody;

    public Method(Token idToken, Type returnType, Token modifier, Token visibility) {
        this.visibility = visibility;
        this.returnType = returnType;
        this.idToken = idToken;
        this.modifier = modifier;
        hasBody = true;
        parameters = new HashMap<>();
        paramList = new java.util.ArrayList<>();
    }
    public Method(Token idToken, Type returnType, Token modifier) {
        this.returnType = returnType;
        this.idToken = idToken;
        this.modifier = modifier;
        hasBody = true;
        this.visibility = new Token(TokenType.sw_public,"public",idToken.getLineNumber());
        parameters = new HashMap<>();
        paramList = new java.util.ArrayList<>();
    }

    public void addParameter(Parameter parameter) throws SemanticException {
        if (parameters.get(parameter.getName()) == null) {
            parameters.put(parameter.getName(), parameter);
            paramList.add(parameter);
        } else {
            throw new SemanticException("Ya fue declarado un parametro con el nombre "+ parameter.getName()+" en el metodo "+this.getName(),parameter.getName(), parameter.getLine());
        }
    }
    public boolean hasBody() {
        return hasBody;
    }
    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }
    public Token getVisibility() {
        return visibility;
    }
    public String getName() {
        return idToken.getLexeme();
    }
    public int getLine() {
        return idToken.getLineNumber();
    }
    public List<Parameter> getParamList() {
        return paramList;
    }
    public Type getReturnType() {
        return returnType;
    }
    public Token getModifier() {
        return modifier;
    }
    public void estaBienDeclarado() {
        if (!returnType.isPrimitive() && (symbolTable.getClass(returnType.getName()) == null)) {
            throw new SemanticException("El metodo fue declarado como tipo de clase inexistente. ", returnType.getName(), returnType.getLine());
        }

        if(!paramList.isEmpty()) {
            for (Parameter param : paramList) {
                param.estaBienDeclarado();
            }
        }

    }
}


