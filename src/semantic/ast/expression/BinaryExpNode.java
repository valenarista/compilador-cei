package semantic.ast.expression;

import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.types.BooleanType;
import semantic.types.IntType;
import semantic.types.Type;

public class BinaryExpNode extends CompExpNode{
    private Token operator;
    private ExpressionNode leftSide;
    private ExpressionNode rightSide;
    public BinaryExpNode(ExpressionNode leftSide, Token operator, ExpressionNode rightSide) {
        this.leftSide = leftSide;
        this.operator = operator;
        this.rightSide = rightSide;
    }
    public Token getOperator() {
        return operator;
    }
    public ExpressionNode getLeftSide() {
        return leftSide;
    }
    public ExpressionNode getRightSide() {
        return rightSide;
    }
    @Override
    public Type check() {
        Type leftType = leftSide.check();
        Type rightType = rightSide.check();
        Type expectedTypes = getExpectedTypes(operator);
        Type resultType = getResultType(operator);
        if(leftType.isPrimitive() && rightType.isPrimitive()) {
            if (!leftType.getName().equals(rightType.getName())) {
                throw new SemanticException("Error semantico en linea " + operator.getLineNumber() + ": Los tipos de los operandos no son compatibles. Se obtuvo " + leftType + " y " + rightType, operator.getLexeme(), operator.getLineNumber());
            }
            if (expectedTypes != null && (!leftType.getName().equals(expectedTypes.getName()))) {
                throw new SemanticException("Error semantico en linea " + operator.getLineNumber() + ": El tipo de los operandos no es compatible con el operador " + operator.getLexeme() + ". Se esperaba " + expectedTypes + " pero se obtuvo " + leftType, operator.getLexeme(), operator.getLineNumber());
            }

        }
        if(expectedTypes == null && operator.getType() == TokenType.equalOp || operator.getType() == TokenType.notEqualOp) {
            if(!areConformantTypes(leftType,rightType)) {
                throw new SemanticException("Error semantico en linea " + operator.getLineNumber() + ": Los tipos no son conformantes para el operador " + operator.getLexeme() + ". . Se obtuvo " + leftType + " y " + rightType, operator.getLexeme(), operator.getLineNumber());
            }

        }

        return resultType;
    }
    public boolean areConformantTypes(Type leftType, Type rightType) {
        if(leftType.equals(rightType)) return true;
            return leftType.isSubtypeOf(rightType) || rightType.isSubtypeOf(leftType);
    }
    public Type getExpectedTypes(Token operator) {
        switch (operator.getType()) {
            case addOp, subOp, mulOp, divOp ,modOp,greaterOp,greaterEqualOp,lessEqualOp,lessOp -> {
                return new IntType();
            }
            case andOp, orOp -> {
                return new BooleanType();
            }
            default -> {
                return null;
            }
        }
    }
    public Type getResultType(Token operator) {
        switch (operator.getType()) {
            case addOp, subOp, mulOp, divOp ,modOp -> {
                return new IntType();
            }
            case andOp, orOp,greaterOp,greaterEqualOp,lessEqualOp,lessOp,equalOp, notEqualOp -> {
                return new BooleanType();
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public int getLine() {
        return operator.getLineNumber();
    }

    @Override
    public String getLexeme() {
        return operator.getLexeme();
    }

    @Override
    public boolean isAssign() {
        return false;
    }

    @Override
    public boolean isOperandWithCall() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
