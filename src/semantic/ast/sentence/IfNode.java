package semantic.ast.sentence;

import semantic.ast.expression.ExpressionNode;
import semantic.types.BooleanType;
import semantic.types.Type;

public class IfNode extends SentenceNode{
    private ExpressionNode condition;
    private SentenceNode body;
    private SentenceNode elseBody;
    public IfNode(ExpressionNode condition,SentenceNode body,SentenceNode elseBody){
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public void check() {
        condition.check().equals(new BooleanType());
        body.check();
        elseBody.check();
    }
}
