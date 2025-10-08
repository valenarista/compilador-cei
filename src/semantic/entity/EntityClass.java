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
    Token getModificador();
    Token getHerencia();
    boolean isAbstract();
    boolean isStatic();
    boolean isFinal();
    boolean isClass();
    boolean isInterface();
    boolean consolidated();

    void addAttribute(Attribute attribute);
    void addMethod(Method method);
    void addConstructor(Constructor constructor);
    void addInheritance(Token herencia);
}
