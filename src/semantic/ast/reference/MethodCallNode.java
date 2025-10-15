package semantic.ast.reference;

import lexical.Token;
import semantic.types.Type;

public class MethodCallNode extends ReferenceNode{
    private Token token;
    private String methodName;
    public MethodCallNode(Token token, String methodName) {
        this.token = token;
        this.methodName = methodName;
    }

    @Override
    public Type check() {
        return null;
    }
}
