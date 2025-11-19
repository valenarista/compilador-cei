package semantic.ast.expression;

import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.types.BooleanType;
import semantic.types.IntType;
import semantic.types.Type;
import static compiler.Main.symbolTable;

public class UnaryExpNode extends CompExpNode{
    private Token operator;
    private OperandNode operand;
    private Type type;

    public UnaryExpNode(Token operator, OperandNode operand) {
        this.operator = operator;
        this.operand = operand;
    }
    public UnaryExpNode(OperandNode operand) {
        this.operand = operand;
    }

    @Override
    public Type check() {
        Type type = operand.check();
        this.type = type;
        if(operator!=null){
            if(operatorIsNegation() && operandIsBoolean(type)){
                return type;
            }
            else if(operatorIsInt() && operandIsInt(type)){
                return type;
            }
            else{
                throw new SemanticException("Error semantico en linea "+operator.getLexeme()+" El tipo del operador "+operator.getLexeme()+" no es compatible con el tipo "+type.getName()+" del operando ",operator.getLexeme(),operator.getLineNumber());
            }
        }
        return type;
    }
    public OperandNode getOperand() {
        return operand;
    }

    @Override
    public int getLine() {
        return operand.getLine();
    }

    public Type getType(){
        return type;
    }

    @Override
    public String getLexeme() {
        return operand.getLexeme();
    }

    @Override
    public boolean isAssign() {
        return operand.isAssign();
    }

    @Override
    public boolean isOperandWithCall() {
        return operand.isOperandWithCall();
    }

    private boolean operandIsBoolean(Type type){
        return type.getName().equals(new BooleanType().getName());
    }
    private boolean operandIsInt(Type type){
        return type.getName().equals(new IntType().getName());
    }
    private boolean operatorIsNegation() {
        return operator.getType().equals(TokenType.notOp);
    }
    private boolean operatorIsInt(){
        return operator.getType().equals(TokenType.addOp) || operator.getType().equals(TokenType.subOp) || operator.getType().equals(TokenType.postDecrement) || operator.getType().equals(TokenType.postIncrement);
    }

    @Override
    public boolean isVariable() {
        return operand.isVariable();
    }

    @Override
    public void generateCode() {
        generateCode(false);
    }

    @Override
    public void generateCode(boolean isLeftSide) {
        operand.generateCode(isLeftSide);
        generateOperator();
    }

    private void generateOperator(){
        if(operator!=null){
            switch (operator.getType()){
                case notOp:
                    symbolTable.instructionList.add("    NOT");
                    break;
                case addOp:
                    //no operation needed
                    break;
                case subOp:
                    symbolTable.instructionList.add("    NEG");
                    break;
                case postIncrement:
                    symbolTable.instructionList.add("PUSH 1");
                    symbolTable.instructionList.add("    ADD");
                    break;
                case postDecrement:
                    symbolTable.instructionList.add("PUSH 1");
                    symbolTable.instructionList.add("    SUB");
                    break;
                default:
                    //no operation needed
                    break;
            }
        }
    }
}
