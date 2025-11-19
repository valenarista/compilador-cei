package semantic.ast.reference;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.declarable.Attribute;
import semantic.declarable.Parameter;
import semantic.types.Type;

import static compiler.Main.symbolTable;

public class VarCallNode extends ReferenceNode{
    private Token token;
    private String varName;
    private ChainingNode optChaining;
    public VarCallNode(Token token, String varName) {
        this.token = token;
        this.varName = varName;
    }
    public Token getToken() {
        return token;
    }

    public String getVarName() {
        return varName;
    }
    public void setOptChaining(ChainingNode optChaining) {
        this.optChaining = optChaining;
    }


    @Override
    public Type check() {
        Type varType;
        if(isParameter()){
            varType = symbolTable.getCurrentInvocable().getParamList().stream().filter(p -> p.getName().equals(varName)).findFirst().get().getType();
        }else if(isLocalVar()){
            varType = symbolTable.getLocalVarType(varName);
        }else if(isAttribute()){
            if(symbolTable.getCurrentInvocable()!=null && symbolTable.getCurrentInvocable().isStaticMethod()){
                throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": no se puede acceder al atributo '" + varName + "' en un metodo estatico.",token.getLexeme(), token.getLineNumber());
            }else{
                Attribute attr = symbolTable.getCurrentClass().getAttributes().get(varName);
                if(!attr.isPublic() && symbolTable.getCurrentClass().getInheritedAttributes().get(attr.getName())!=null){
                    throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el atributo '" + varName + "' es privado y no se puede acceder desde la clase actual.",token.getLexeme(), token.getLineNumber());
                }
                varType = attr.getType();
            }
        }else{
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": la variable '" + varName + "' no ha sido declarada.",token.getLexeme(), token.getLineNumber());
        }

        if(optChaining != null){
            return optChaining.check(varType);
        }
        return varType;
    }

    @Override
    public int getLine() {
        return token.getLineNumber();
    }

    @Override
    public String getLexeme() {
        return token.getLexeme();
    }

    @Override
    public boolean isAssign() {
        return false;
    }

    @Override
    public boolean isOperandWithCall() {
        if(optChaining != null) {
            return optChaining.isOperandWithCall();
        }
        return false;
    }

    public boolean isParameter(){
        if(symbolTable.getCurrentInvocable() == null){
            return false;
        }
        for(Parameter p : symbolTable.getCurrentInvocable().getParamList()){
            if(p.getName().equals(varName)){
                return true;
            }
        }
        return false;
    }
    public boolean isLocalVar(){
        return symbolTable.isLocalVar(varName);
    }
    public boolean isAttribute(){
        return symbolTable.isAttribute(varName);
    }

    @Override
    public boolean isVariable() {
        if(optChaining != null) {
            return optChaining.isVariable();
        }
        return true;
    }

    @Override
    public void generateCode(){
        generateCode(false);
    }

    @Override
    public void generateCode(boolean isLeftSide) {
        System.out.println("DEBUG VarCallNode: Generando código para variable '" + varName + "'");
        System.out.println("  -> isParameter(): " + isParameter());
        System.out.println("  -> isLocalVar(): " + isLocalVar());
        System.out.println("  -> isAttribute(): " + isAttribute());
        System.out.println("  -> currentBlock es null? " + (symbolTable.getCurrentBlock() == null));
        if(symbolTable.getCurrentBlock() != null) {
            System.out.println("  -> Variables en currentBlock: " + symbolTable.getCurrentBlock().getVarLocalMap().keySet());
        }

        System.out.println("  -> Generando código para el lado " + (isLeftSide ? "izquierdo" : "derecho"));

        if(isLeftSide)
            generateLeftSideCode();
        else
            generateRightSideCode();


        if(optChaining != null) {
            optChaining.generateCode();
        }
    }

    private void generateParameterCode(){
        Parameter parameter = symbolTable.getCurrentInvocable().getParamList().stream().filter(p -> p.getName().equals(varName)).findFirst().get();
        int offset = parameter.getOffset();
        System.out.println("DEBUG generateParameterCode: parámetro lado der'" + varName + "' tiene offset " + offset);
        symbolTable.instructionList.add("LOAD " + offset);
    }
    private void generateLocalVarCode(){
        int localVarOffset = symbolTable.getLocalVarOffset(varName);
        symbolTable.instructionList.add("LOAD " + localVarOffset);
    }
    private void generateAttributeCode() {
        symbolTable.instructionList.add("LOAD 3");
        Attribute attr = symbolTable.getCurrentClass().getAttributes().get(varName);
        int attrOffset = attr.getOffset();
        symbolTable.instructionList.add("LOADREF " + attrOffset);
    }
    private int getParameterOffset(){
        int paramSize = symbolTable.getCurrentInvocable().getParamList().size();
        int paramIndex = 0;
        for(Parameter p : symbolTable.getCurrentInvocable().getParamList()){
            if(p.getName().equals(varName)){
                break;
            }
            paramIndex++;
        }
        int baseOffset = paramSize + 3;
        if(!symbolTable.getCurrentInvocable().isVoid()){
            baseOffset +=1;
        }
        return baseOffset - paramIndex;
    }
    private void generateLeftSideCode(){
        System.out.println("  -> Generando lado izquierdo");

        if(isParameter()) {
            System.out.println("  -> Generando como parámetro");
            Parameter parameter = symbolTable.getCurrentInvocable().getParamList().stream().filter(p -> p.getName().equals(varName)).findFirst().get();
            int paramOffset = parameter.getOffset();
            System.out.println("DEBUG generateParameterCode: parámetro lado izq'" + varName + "' tiene offset " + paramOffset);
            symbolTable.instructionList.add("STORE " + paramOffset);
        }
        else if(isLocalVar()) {
            System.out.println("  -> Generando como variable local");
            int localVarOffset = symbolTable.getLocalVarOffset(varName);
            symbolTable.instructionList.add("STORE " + localVarOffset);
        }
        else if(isAttribute()) {
            System.out.println("  -> Generando como atributo");
            symbolTable.instructionList.add("LOAD 3");
            symbolTable.instructionList.add("SWAP");
            Attribute attr = symbolTable.getCurrentClass().getAttributes().get(varName);
            int attrOffset = attr.getOffset();
            symbolTable.instructionList.add("STOREREF " + attrOffset);
        }
        else {
            System.err.println("  -> ERROR: No se pudo determinar el tipo de variable!");
        }
    }
    private void generateRightSideCode(){
        if(isParameter()) {
            System.out.println("  -> Generando como parámetro");
            generateParameterCode();
        }
        else if(isLocalVar()) {
            System.out.println("  -> Generando como variable local");
            generateLocalVarCode();
        }
        else if(isAttribute()) {
            System.out.println("  -> Generando como atributo");
            generateAttributeCode();
        }
        else {
            System.err.println("  -> ERROR: No se pudo determinar el tipo de variable!");
        }
    }

}
