package semantic.ast.sentence;

import exceptions.SemanticException;
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
        if(!expressionNode.isAssign() && !expressionNode.isOperandWithCall()){
            throw new SemanticException("Error semantico en linea " + expressionNode.getLine() + ": La expresion no es una asignacion ni una llamada a funcion",expressionNode.getLexeme(),expressionNode.getLine());
        }
    }
}
