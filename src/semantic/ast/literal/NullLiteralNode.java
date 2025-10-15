package semantic.ast.literal;

import lexical.Token;
import semantic.types.Type;

public class NullLiteralNode extends LiteralNode{
    private Token token;
    public NullLiteralNode(Token token) {
        this.token = token;
    }
    public Token getToken() {
        return token;
    }
    @Override
    public String getType() {
        return "";
    }

    @Override
    public Type check() {
        return null;
    }
}
