package semantic.ast.expression;

import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.types.BooleanType;
import semantic.types.IntType;
import semantic.types.Type;

public class UnaryExpNode extends CompExpNode{
    private Token operator;
    private OperandNode operand;

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
        if(operator!=null){
            if(operatorIsNegation() && operandIsBoolean(type)){
                return type;
            }
            else if(operatorIsInt() && operandIsInt(type)){
                return type;
            }
            else{
                throw new SemanticException("Error semantico en linea "+operator.getLexeme()+" El tipo del operador "+operator.getLexeme()+" no es compatible con el operando "+operand.getToken().getLexeme(),operator.getLexeme(),operator.getLineNumber());
            }
        }
        return type;
    }

    @Override
    public int getLine() {
        return operator.getLineNumber();
    }

    @Override
    public String getLexeme() {
        return operand.getLexeme();
    }

    private boolean operandIsBoolean(Type type){
        return type.equals(new BooleanType());
    }
    private boolean operandIsInt(Type type){
        return type.equals(new IntType());
    }
    private boolean operatorIsNegation() {
        return operator.getType().equals(TokenType.notOp);
    }
    private boolean operatorIsInt(){
        return operator.getType().equals(TokenType.addOp) || operator.getType().equals(TokenType.subOp) || operator.getType().equals(TokenType.postDecrement) || operator.getType().equals(TokenType.postIncrement);
    }


    @Override
    public boolean isVariable() {
        return false;
    }
}
