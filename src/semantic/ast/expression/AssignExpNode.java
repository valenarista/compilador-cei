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
    public String getType() {
        return null;
    }

}
