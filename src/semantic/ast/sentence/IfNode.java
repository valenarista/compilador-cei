package semantic.ast.sentence;

import exceptions.SemanticException;
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
        if(!condition.check().getName().equals(new BooleanType().getName()))
            throw new SemanticException("Error semantico en linea "+condition.getLine()+" La condicion de un if debe ser de tipo booleano",condition.getLexeme(),condition.getLine());
        body.check();
        elseBody.check();
    }
    public void setBody(SentenceNode body) {
        this.body = body;
    }
    public SentenceNode getBody() {
        return body;
    }
    public SentenceNode getElseBody() {
        return elseBody;
    }

}
