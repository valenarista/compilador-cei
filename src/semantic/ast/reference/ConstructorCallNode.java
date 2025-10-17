package semantic.ast.reference;

import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.types.Type;

import java.util.List;

public class ConstructorCallNode extends ReferenceNode{
    private Token token;
    private String className;
    private List<ExpressionNode> argList;

    public ConstructorCallNode(Token token, String className) {
        this.token = token;
        this.className = className;
    }
    public void setArgList(List<ExpressionNode> argList) {
        this.argList = argList;
    }
    public List<ExpressionNode> getArgList() {
        return argList;
    }

    public Token getToken() {
        return token;
    }


    public String getClassName() {
        return className;
    }
    @Override
    public Type check() {
        return null;
    }

    @Override
    public int getLine() {
        return token.getLineNumber();
    }

    @Override
    public String getLexeme() {
        return token.getLexeme();
    }

    @Override
    public boolean isAssign() {
        return false;
    }

    @Override
    public boolean isOperandWithCall() {
        return true;
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
