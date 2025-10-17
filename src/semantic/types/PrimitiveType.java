package semantic.types;

abstract public class PrimitiveType implements Type {
    private String name;
    //null void int char boolean
    public PrimitiveType(String name) {
        this.name = name;
    }
    public boolean isPrimitive() {
        return true;
    }
    public String getName() {
        return name;
    }
    public int getLine() {
        return 0;
    }
}
