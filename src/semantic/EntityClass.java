package semantic;

import lexical.Token;

public interface EntityClass {
    public void estaBienDeclarado();
    public void consolidar();

    String getName();
    int getLine();

    void addAttribute(Attribute attribute);
    void addMethod(Method method);
    void addConstructor(Constructor constructor);
    void addInheritance(Token herencia);
}
