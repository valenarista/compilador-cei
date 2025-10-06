package semantic.declarable;

import exceptions.SemanticException;
import lexical.Token;
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

    public Method(Token idToken, Type returnType, Token modifier) {
        this.returnType = returnType;
        this.idToken = idToken;
        this.modifier = modifier;
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


