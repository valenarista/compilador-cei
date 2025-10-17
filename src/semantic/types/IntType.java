package semantic.types;

public class IntType extends PrimitiveType{
    public IntType() {
        super("int");
    }

    @Override
    public boolean isSubtypeOf(Type rightType) {
        return rightType instanceof IntType;
    }
}
