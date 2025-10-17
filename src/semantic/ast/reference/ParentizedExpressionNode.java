package semantic.ast.reference;

import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.types.Type;

public class ParentizedExpressionNode extends ReferenceNode{
    private ExpressionNode expression;
    public ParentizedExpressionNode(ExpressionNode expression){
        this.expression = expression;
    }

    @Override
    public Type check() {
        return expression.check();
    }

    @Override
    public int getLine() {
        return expression.getLine();
    }

    @Override
    public String getLexeme() {
        return expression.getLexeme();
    }

    @Override
    public boolean isAssign() {
        return false;
    }

    @Override
    public boolean isOperandWithCall() {
        return false;
    }

    @Override
    public Token getToken() {
        return null;
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
