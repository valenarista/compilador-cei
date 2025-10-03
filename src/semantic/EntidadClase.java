package semantic;

public interface EntidadClase {
    public void estaBienDeclarado();
    public void consolidar();

    String getName();
    int getLine();

    void addAtributo(Atributo atributo);
    void addMetodo(Metodo metodo);
    void addConstructor(Constructor constructor);
}
