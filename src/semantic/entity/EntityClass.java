package semantic.entity;

import lexical.Token;
import semantic.declarable.Attribute;
import semantic.declarable.Constructor;
import semantic.declarable.Method;

public interface EntityClass {
    void estaBienDeclarado();
    void consolidar();

    String getName();
    int getLine();

    void addAttribute(Attribute attribute);
    void addMethod(Method method);
    void addConstructor(Constructor constructor);
    void addInheritance(Token herencia);
}
