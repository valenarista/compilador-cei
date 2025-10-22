package semantic.types;

import lexical.Token;
import lexical.TokenType;

public class NullType extends ReferenceType{
    private Token token;
    public NullType(Token token) {
        super(token);
    }

    @Override
    public boolean isSubtypeOf(Type rightType) {
        return rightType instanceof NullType;
    }
}
