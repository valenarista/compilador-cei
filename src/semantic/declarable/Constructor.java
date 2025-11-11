package semantic.declarable;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.sentence.BlockNode;
import semantic.types.Type;

import java.util.HashMap;
import java.util.List;
import static compiler.Main.symbolTable;

public class Constructor implements Invocable{
    private Token idToken;
    private Token visibility;
    private HashMap<String,Parameter> parameters;
    private List<Parameter> paramList;
    private BlockNode block;

    private String label;

    public Constructor(Token idToken, Token visibility) {
        this.idToken = idToken;
        this.visibility = visibility;
        parameters = new HashMap<>();
        paramList = new java.util.ArrayList<>();
        label = "ctor_" + idToken.getLexeme();
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

    @Override
    public boolean isPublic() {
        return visibility == null || visibility.getLexeme().equals("public");
    }


    @Override
    public boolean isStaticMethod() {
        return false;
    }

    @Override
    public boolean isVoid() {
        return false;
    }

    public void estaBienDeclarado() throws SemanticException{
        for(Parameter p : paramList){
            p.estaBienDeclarado();
        }
    }
    public void chequeoSentencias() throws SemanticException{
        if(block != null){
            block.check();
        }
    }

    public String getLabel() {
        return label;
    }

    public void generateCode() {
        symbolTable.instructionList.add(".CODE");
        symbolTable.instructionList.add(label + ":");
        symbolTable.instructionList.add("LOADFP");
        symbolTable.instructionList.add("LOADSP");
        symbolTable.instructionList.add("STOREFP");

        if(block != null){
            block.generateCode();
        }

        int memoryToFree = paramList.size() + 1;

        symbolTable.instructionList.add("STOREFP");
        symbolTable.instructionList.add("RET "+ memoryToFree);
    }
}
