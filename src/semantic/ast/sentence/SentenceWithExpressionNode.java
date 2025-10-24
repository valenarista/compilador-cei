package semantic.ast.sentence;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.expression.ExpressionNode;

public class SentenceWithExpressionNode extends SentenceNode {
    private ExpressionNode expressionNode;
    private Token finalToken;
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
            if(finalToken == null){
                throw new SemanticException("Error semantico en linea " + expressionNode.getLine() + ": La expresion no es una asignacion ni una llamada a funcion",expressionNode.getLexeme(),expressionNode.getLine());
            }
            throw new SemanticException("Error semantico en linea " + expressionNode.getLine() + ": La expresion no es una asignacion ni una llamada a funcion",finalToken.getLexeme(),finalToken.getLineNumber());
        }
    }
    @Override
    public boolean isAssign(){
        return expressionNode.isAssign();
    }

    public int getLine() {
        return expressionNode.getLine();
    }
    public String getLexeme() {
        return expressionNode.getLexeme();
    }

    public void setFinalToken(Token finalToken) {
        this.finalToken = finalToken;
    }
    public Token getFinalToken() {
        return finalToken;
    }
}
