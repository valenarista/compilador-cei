package semantic.ast.chaining;

import exceptions.SemanticException;
import lexical.Token;
import semantic.declarable.Attribute;
import semantic.types.Type;

import static compiler.Main.symbolTable;

public class ChainedVarNode extends ChainingNode{
    private Token token;
    private ChainingNode optionalChaining;
    private String varName;
    private Attribute attribute;


    public ChainedVarNode(Token token, String varName) {
        this.token = token;
        this.varName = varName;

    }

    @Override
    public boolean isOperandWithCall() {
        if(optionalChaining != null) {
            return optionalChaining.isOperandWithCall();
        }
        return false;
    }

    @Override
    public boolean isVariable() {
        if(optionalChaining != null) {
            return optionalChaining.isVariable();
        }
        return true;
    }

    @Override
    public void generateCode() {
        symbolTable.instructionList.add("LOADREF " + attribute.getOffset());
        if(optionalChaining != null) {
            optionalChaining.generateCode();
        }
    }

    public void setOptChaining(ChainingNode chainingNode) {
        optionalChaining = chainingNode;
    }
    public Type check(Type previousType) {
        notPrimitiveCalling(previousType);
        checkAttributeValidity(previousType);
        if(optionalChaining != null) {
            return optionalChaining.check(attribute.getType());
        }
        return attribute.getType();
    }

    private void notPrimitiveCalling(Type callingType){
        if(callingType.isPrimitive())
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": no se puede hacer una llamada a un atributo en un tipo primitivo.",token.getLexeme(), token.getLineNumber());
    }
    private void checkAttributeValidity(Type callingType){
        attribute = symbolTable.getClass(callingType.getName()).getAttributes().get(varName);
        if(attribute == null){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": la clase " + callingType.getName() + " no tiene un atributo llamado " + varName + ".",token.getLexeme(), token.getLineNumber());
        }
        if(!symbolTable.getCurrentClass().getName().equals(callingType.getName()) && !attribute.isPublic())
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el atributo " + varName + " no es accesible desde la clase actual.",token.getLexeme(), token.getLineNumber());
    }
}
