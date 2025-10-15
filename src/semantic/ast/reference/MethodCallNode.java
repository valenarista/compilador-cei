package semantic.ast.reference;

import lexical.Token;
import semantic.ast.expression.ExpressionNode;
import semantic.types.Type;

import java.util.List;

public class MethodCallNode extends ReferenceNode{
    private Token token;
    private String methodName;
    private List<ExpressionNode> argList;

    public MethodCallNode(Token token, String methodName) {
        this.token = token;
        this.methodName = methodName;
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
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Type check() {
        return null;
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
