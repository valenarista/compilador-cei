package semantic.ast.reference;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.chaining.ChainingNode;
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
        System.out.println("Chequeando VarCallNode: " + varName + " en linea " + token.getLineNumber());
        Type varType;
        if(isParameter()){
            varType = symbolTable.getCurrentInvocable().getParamList().stream().filter(p -> p.getName().equals(varName)).findFirst().get().getType();
        }else if(isLocalVar()){
            varType = symbolTable.getCurrentBlock().getVarLocalMap().get(varName).getType();
        }else if(isAttribute()){
            if(!symbolTable.getCurrentInvocable().isStaticMethod()){
                if(symbolTable.getCurrentClass().getAttributes().get(varName).isPublic()){
                    varType = symbolTable.getCurrentClass().getAttributes().get(varName).getType();
                }else{
                    throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el atributo '" + varName + "' es privado y no se puede acceder desde el metodo actual.",token.getLexeme(), token.getLineNumber());
                }
            }else{
                throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": no se puede acceder al atributo '" + varName + "' en un metodo estatico.",token.getLexeme(), token.getLineNumber());
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
        return false;
    }

    public boolean isParameter(){
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
        return true;
    }
}
