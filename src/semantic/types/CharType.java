package semantic.types;

public class CharType extends PrimitiveType{
    public CharType() {
        super("char");
    }

    @Override
    public boolean isSubtypeOf(Type rightType) {
        return rightType instanceof CharType;
    }
}
