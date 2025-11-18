package semantic.ast.reference;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.ast.expression.ExpressionNode;
import semantic.declarable.Constructor;
import semantic.declarable.Method;
import semantic.declarable.Parameter;
import semantic.entity.ConcreteClass;
import semantic.entity.EntityClass;
import semantic.types.ReferenceType;
import semantic.types.Type;

import java.util.List;

import static compiler.Main.symbolTable;

public class ConstructorCallNode extends ReferenceNode{
    private Token token;
    private String className;
    private ChainingNode optChaining;
    private List<ExpressionNode> argList;


    public ConstructorCallNode(Token token, String className) {
        this.token = token;
        this.className = className;
    }
    public void setArgList(List<ExpressionNode> argList) {
        this.argList = argList;
    }
    public List<ExpressionNode> getArgList() {
        return argList;
    }

    public Token getToken() {
        return token;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public Type check(){
        if(symbolTable.getClass(className)==null){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": no se puede llamar al constructor de la clase " + className + " porque no existe.", token.getLexeme(), token.getLineNumber());
        }
        Constructor constructor = symbolTable.getClass(className).getConstructor();

        if(!constructor.isPublic())
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el constructor de la clase " + className + " es privado y no se puede acceder desde el contexto actual.", token.getLexeme(), token.getLineNumber());

        List<Parameter> origArgList = constructor.getParamList();
        int index = 0;
        if(argList.size() != origArgList.size()){
            throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el numero de argumentos de la llamada a constructor no coincide con el numero de parametros esperados.",token.getLexeme(), token.getLineNumber());
        }
        for(ExpressionNode arg : argList){
            Type type = arg.check();
            if(!type.isSubtypeOf(origArgList.get(index).getType())){
                throw new SemanticException("Error semantico en linea " + token.getLineNumber() + ": el tipo del argumento \"" + (arg.getLexeme()) + "\" no coincide con el tipo del parametro esperado en la llamada a constructor.",token.getLexeme(), token.getLineNumber());
            }
            index++;
        }

        if(optChaining != null) {
            return optChaining.check(new ReferenceType(token));
        }
        return new ReferenceType(token);
    }

    @Override
    public void setOptChaining(ChainingNode chainingNode) {
        optChaining = chainingNode;
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
        return true;
    }
    @Override
    public void generateCode(boolean isLeftSide){
        generateCode();
    }

    @Override
    public void generateCode(){
        symbolTable.instructionList.add("RMEM 1");

        EntityClass building = symbolTable.getClass(className);
        int cirSize = building.getCIRSize();

        symbolTable.instructionList.add("PUSH " + cirSize);
        symbolTable.instructionList.add("PUSH simple_malloc");
        symbolTable.instructionList.add("CALL");

        symbolTable.instructionList.add("DUP");
        symbolTable.instructionList.add("PUSH "+building.getVTLabel());
        symbolTable.instructionList.add("STOREREF 0");

        symbolTable.instructionList.add("DUP");

        for(ExpressionNode arg : argList){
            arg.generateCode();
            symbolTable.instructionList.add("SWAP");
        }
        String constructorLabel = building.getConstructor().getLabel();
        symbolTable.instructionList.add("PUSH " + constructorLabel);
        symbolTable.instructionList.add("CALL");

        if(optChaining != null){
            optChaining.generateCode();
        }

    }

    @Override
    public boolean isVariable() {
        if(optChaining != null) {
            return optChaining.isVariable();
        }
        return false;
    }
}
