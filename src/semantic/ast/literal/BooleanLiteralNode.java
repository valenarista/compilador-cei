package semantic.ast.literal;

import lexical.Token;
import semantic.types.BooleanType;
import semantic.types.Type;

public class BooleanLiteralNode extends LiteralNode{
    private Token token;
    public BooleanLiteralNode(Token token) {
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
        return new BooleanType();
    }
}
