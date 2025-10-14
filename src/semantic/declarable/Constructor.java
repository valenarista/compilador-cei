package semantic.declarable;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.sentence.BlockNode;
import java.util.HashMap;
import java.util.List;

public class Constructor implements Invocable{
    private Token idToken;
    private Token visibility;
    private HashMap<String,Parameter> parameters;
    private List<Parameter> paramList;
    private BlockNode block;

    public Constructor(Token idToken, Token visibility) {
        this.idToken = idToken;
        this.visibility = visibility;
        parameters = new HashMap<>();
        paramList = new java.util.ArrayList<>();
    }

    public void setBlock(BlockNode block) { this.block = block; }
    public BlockNode getBlock() { return block; }
    public String getName(){
        return idToken.getLexeme();
    }
    public Token getVisibility() {
        return visibility;
    }
    public List<Parameter> getParamList() {
        return paramList;
    }
    public int getLine(){
        return idToken.getLineNumber();
    }
    public void addParameter(Parameter parameter){
        if (parameters.get(parameter.getName()) == null) {
            parameters.put(parameter.getName(), parameter);
            paramList.add(parameter);
        } else {
            throw new SemanticException("Error semantico en linea "+parameter.getLine()+" Ya fue declarado un parametro con el nombre "+ parameter.getName()+" en el constructor "+this.getName(),parameter.getName(), parameter.getLine());
        }
    }
    public void estaBienDeclarado() throws SemanticException{
        for(Parameter p : paramList){
            p.estaBienDeclarado();
        }
    }
}
