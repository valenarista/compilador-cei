package semantic.declarable;

import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.ast.sentence.BlockNode;
import semantic.entity.EntityClass;
import semantic.types.Type;

import java.sql.Blob;
import java.util.HashMap;
import java.util.List;

import static compiler.Main.symbolTable;

public class Method implements Invocable {
    private HashMap<String,Parameter> parameters;
    private List<Parameter> paramList;
    private Token idToken;
    private Type returnType;
    private Token modifier;
    private Token visibility;
    private boolean hasBody;
    private BlockNode block;
    private EntityClass ownerClass;

    private int offset;
    private String label;


    public Method(Token idToken, Type returnType, Token modifier, Token visibility) {
        this.visibility = visibility;
        this.returnType = returnType;
        this.idToken = idToken;
        this.modifier = modifier;
        hasBody = true;
        parameters = new HashMap<>();
        paramList = new java.util.ArrayList<>();
        this.ownerClass = symbolTable.getCurrentClass();
        if(this.ownerClass != null) {
            this.label = ownerClass.getName() + "_" + idToken.getLexeme();
        } else {
            this.label = null;
        }
    }
    public Method(Token idToken, Type returnType, Token modifier) {
        this.returnType = returnType;
        this.idToken = idToken;
        this.modifier = modifier;
        hasBody = true;
        this.visibility = new Token(TokenType.sw_public,"public",idToken.getLineNumber());
        parameters = new HashMap<>();
        paramList = new java.util.ArrayList<>();
        this.ownerClass = null;
        this.label = null;
    }

    public void addParameter(Parameter parameter) throws SemanticException {
        if (parameters.get(parameter.getName()) == null) {
            parameters.put(parameter.getName(), parameter);
            paramList.add(parameter);
        } else {
            throw new SemanticException("Ya fue declarado un parametro con el nombre "+ parameter.getName()+" en el metodo "+this.getName(),parameter.getName(), parameter.getLine());
        }
    }

    @Override
    public boolean isPublic() {
        return visibility == null || visibility.getType().equals(TokenType.sw_public);
    }

    @Override
    public boolean isStaticMethod() {
        return modifier!=null && modifier.getType().equals(TokenType.sw_static);
    }

    public void setBlock(BlockNode block) { this.block = block; }
    public BlockNode getBlock() { return block; }
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
    public EntityClass getOwnerClass() {
        return ownerClass;
    }


    public void estaBienDeclarado() {
        if (!returnType.isPrimitive() && (symbolTable.getClass(returnType.getName()) == null)) {
            throw new SemanticException("Error semantico en linea "+returnType.getLine()+" El metodo fue declarado como tipo de clase inexistente. ", returnType.getName(), returnType.getLine());
        }

        if(!paramList.isEmpty()) {
            for (Parameter param : paramList) {
                param.estaBienDeclarado();
            }
        }
    }
    public void chequeoSentencias() throws SemanticException {
        block.check();
    }
    public void generateCode(){
        if(getName().equals("main") && isStaticMethod()){
            label = "main";
        }
        symbolTable.instructionList.add(label + ":");
        symbolTable.instructionList.add("LOADFP");
        symbolTable.instructionList.add("LOADSP");
        symbolTable.instructionList.add("STOREFP");

        EntityClass previousClass = symbolTable.getCurrentClass();
        symbolTable.setCurrentClass(ownerClass.getName(), ownerClass);
        symbolTable.setCurrentInvocable(this);

        if(block != null && hasBody()) {
            block.generateCode();
        }

        if(isVoid()) {
            int localVarCount = 0;
            if(block != null && block.getVarLocalMap() != null) {
                localVarCount = block.getVarLocalMap().size();
            }

            if(localVarCount > 0) {
                symbolTable.instructionList.add("FMEM " + localVarCount);
            }

            symbolTable.instructionList.add("STOREFP");
            int memoryNeeded = isStaticMethod() ? paramList.size() : paramList.size() + 1;
            symbolTable.instructionList.add("RET " + memoryNeeded);
        } else {
            int localVarCount = 0;
            if(block != null && block.getVarLocalMap() != null) {
                localVarCount = block.getVarLocalMap().size();
            }
            if(localVarCount > 0) {
                symbolTable.instructionList.add("FMEM " + localVarCount);
            }
            symbolTable.instructionList.add("STOREFP");
            int memoryNeeded = isStaticMethod() ? paramList.size() : paramList.size() + 1;
            symbolTable.instructionList.add("RET " + memoryNeeded);
        }
        if(previousClass != null)
            symbolTable.setCurrentClass(previousClass.getName(), previousClass);
    }

    public String getLabel() {
        if(label == null && ownerClass != null) {
            label = ownerClass.getName() + "_" + idToken.getLexeme();
        }
        return label;
    }
    public void setOffset(int offset) {
        this.offset = offset;

    }
    public int getOffset() {
        return offset;
    }
    public boolean isVoid() {
        return returnType.getName().equals("void");
    }


    public void setOwnerClass(EntityClass objectClass) {
        this.ownerClass = objectClass;
        if(ownerClass != null) {
            label = ownerClass.getName() + "_" + idToken.getLexeme();
        }
    }

}


