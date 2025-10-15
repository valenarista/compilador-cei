package semantic.ast.literal;

import lexical.Token;
import semantic.types.CharType;
import semantic.types.Type;

public class CharLiteralNode extends LiteralNode{
    private Token token;
    public CharLiteralNode(Token token) {
        this.token = token;
    }
    public Token getToken() {
        return token;
    }
    @Override
    public Type getType() {
        return new CharType();
    }

    @Override
    public Type check() {
        return new CharType();
    }
}
