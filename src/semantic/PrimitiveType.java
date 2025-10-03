package semantic;

public class PrimitiveType implements Type {
    private String name;
    //null void int char boolean
    public PrimitiveType(String name) {
        this.name = name;
    }
}
