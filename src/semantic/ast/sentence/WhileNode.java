package semantic.ast.sentence;


import exceptions.SemanticException;
import semantic.ast.expression.ExpressionNode;
import semantic.types.BooleanType;
import semantic.types.Type;

public class WhileNode extends SentenceNode{
    private ExpressionNode condition;
    private SentenceNode body;
    public WhileNode(ExpressionNode condition, SentenceNode body) {
        this.condition = condition;
        this.body = body;
    }
    @Override
    public void check() {
        Type condType = condition.check();
        if (condType == null || !condType.getName().equals(new BooleanType().getName())) {
            throw new SemanticException("Error semantico en linea " + condition.getLine() + ": La condicion del while debe ser de tipo booleano",condition.getLexeme(),condition.getLine());
        }
        body.check();
    }

    public void setBody(SentenceNode body) {
        this.body = body;
    }
    public SentenceNode getBody() {
        return body;
    }
    public ExpressionNode getCondition() {
        return condition;
    }
}
