package semantic.ast.reference;

import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.ast.expression.ExpressionNode;
import semantic.types.Type;

public class ParentizedExpressionNode extends ReferenceNode{
    private ExpressionNode expression;
    private ChainingNode optionalChaining;
    private Token token;
    public ParentizedExpressionNode(ExpressionNode expression,Token token){
        this.expression = expression;
        this.token = token;
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
        return expression.isAssign();
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
        return token;
    }

    @Override
    public boolean isVariable() {
        if(optionalChaining != null) {
            return optionalChaining.isVariable();
        }
        return false;
    }
}
