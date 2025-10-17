package semantic.types;

public class VoidType extends PrimitiveType{
    public VoidType() {
        super("void");
    }

    @Override
    public boolean isSubtypeOf(Type rightType) {
        return rightType instanceof VoidType;
    }
}
