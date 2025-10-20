package semantic.ast.reference;

import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.ast.expression.ExpressionNode;
import semantic.types.Type;

public class ParentizedExpressionNode extends ReferenceNode{
    private ExpressionNode expression;
    private ChainingNode optionalChaining;
    public ParentizedExpressionNode(ExpressionNode expression){
        this.expression = expression;
    }

    @Override
    public Type check() {
        Type type = expression.check();
        if(optionalChaining != null){
            return optionalChaining.check(type);
        }
        return type;
    }

    @Override
    public void setOptChaining(ChainingNode chainingNode) {
        optionalChaining = chainingNode;
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
        if(optionalChaining != null) {
            return optionalChaining.isOperandWithCall();
        }
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
