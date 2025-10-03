package semantic;

import lexical.Token;

import java.util.HashSet;

public class ConcreteClass implements EntityClass {
    Token idToken;
    Token herencia;
    HashSet<Attribute> attributes;
    HashSet<Method> methods;
    HashSet<Constructor> constructores;

    public ConcreteClass(Token idToken) {
        this.idToken = idToken;
        this.attributes = new HashSet<>();
        this.methods = new HashSet<>();
        this.constructores = new HashSet<>();
    }
    public void estaBienDeclarado(){
    }
    public void consolidar(){
    }
    public String getName() {
        return idToken.getLexeme();
    }
    public int getLine() {
        return idToken.getLineNumber();
    }
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }
    public void addMethod(Method method) {
        methods.add(method);
    }
    public void addConstructor(Constructor constructor) {
        constructores.add(constructor);
    }
    public void addInheritance(Token herencia) {
        this.herencia = herencia;
    }
}
