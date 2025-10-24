package semantic.entity;

import lexical.Token;
import semantic.declarable.Attribute;
import semantic.declarable.Constructor;
import semantic.declarable.Method;

public interface EntityClass {
    void estaBienDeclarado();
    void consolidar();
    void chequeoSentencias();

    String getName();
    int getLine();
    Token getModificador();
    Token getHerencia();
    java.util.HashMap<String, Method> getMethods();
    java.util.HashMap<String, Method> getInheritedMethods();
    java.util.HashMap<String, Attribute> getAttributes();
    Constructor getConstructor();
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
