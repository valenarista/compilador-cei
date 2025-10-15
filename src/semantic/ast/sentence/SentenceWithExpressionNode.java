package semantic.ast.sentence;

import semantic.ast.expression.ExpressionNode;

public class SentenceWithExpressionNode extends SentenceNode {
    private ExpressionNode expressionNode;
    public SentenceWithExpressionNode(ExpressionNode expressionNode) {
        this.expressionNode = expressionNode;
    }
    public ExpressionNode getExpressionNode() {
        return expressionNode;
    }
    @Override
    public void check() {
        expressionNode.check();
    }
}
