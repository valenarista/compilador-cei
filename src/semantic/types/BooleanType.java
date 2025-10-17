package semantic.types;

public class BooleanType extends PrimitiveType{
    public BooleanType() {
        super("boolean");
    }

    @Override
    public boolean isSubtypeOf(Type rightType) {
        return rightType instanceof BooleanType;
    }
}
