package semantic.ast.sentence;

import semantic.ast.expression.ExpressionNode;
import semantic.types.Type;

public class AssignNode extends ExpressionNode{
    private ExpressionNode left;
    private ExpressionNode right;

    public AssignNode(ExpressionNode left, ExpressionNode right) {
        this.left = left;
        this.right = right;
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
