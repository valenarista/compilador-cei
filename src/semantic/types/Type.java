package semantic.types;

public interface Type {
    boolean isPrimitive();
    String getName();
    int getLine();
}
