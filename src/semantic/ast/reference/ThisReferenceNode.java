package semantic.ast.reference;

import lexical.Token;
import semantic.ast.chaining.ChainingNode;
import semantic.types.Type;

public class ThisReferenceNode extends ReferenceNode{
    private Token token;
    private String className;
    private ChainingNode optChaining;
    public ThisReferenceNode(Token token, String className) {
        this.token = token;
    }

    @Override
    public Type check() {
        return null;
    }
}
