package semantic.ast.expression;

import semantic.types.Type;

public class AssignExpNode extends ExpressionNode{
    private String id;
    private ExpressionNode leftSide;
    private ExpressionNode rightSide;

    @Override
    public Type check() {
        return null;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    public ExpressionNode getLeftSide(){
        return leftSide;
    }
    public ExpressionNode getRightSide(){
        return rightSide;
    }
}
