package semantic.types;

public class NullType extends PrimitiveType{
    public NullType() {
        super("null");
    }

    @Override
    public boolean isSubtypeOf(Type rightType) {
        return rightType instanceof NullType;
    }
}
