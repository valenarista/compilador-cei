package semantic.ast.literal;

import lexical.Token;
import semantic.types.IntType;
import semantic.types.Type;

public class IntLiteralNode extends LiteralNode{
    private Token token;
    public IntLiteralNode(Token token) {
        this.token = token;
    }
    public Token getToken() {
        return token;
    }
    @Override
    public Type getType() {
        return new IntType();
    }

    @Override
    public Type check() {
        return new IntType();
    }
    @Override
    public int getLine() {
        return token.getLineNumber();
    }
    @Override
    public String getLexeme() {
        return token.getLexeme();
    }
}
