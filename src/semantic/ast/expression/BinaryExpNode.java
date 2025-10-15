package semantic.ast.expression;

import lexical.Token;
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

        return null;
    }

    @Override
    public String getType() {
        return "";
    }
}
