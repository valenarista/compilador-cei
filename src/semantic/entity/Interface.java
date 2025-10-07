package semantic.entity;

import lexical.Token;
import lexical.TokenType;
import semantic.declarable.Attribute;
import semantic.declarable.Constructor;
import semantic.declarable.Method;

import java.util.HashSet;

public class Interface implements EntityClass {
    private Token idToken;
    private Token modificador;
    HashSet<Method> methods;

    public Interface(Token idToken,Token modificador) {
        this.idToken = idToken;
        this.modificador = modificador;
        this.methods = new HashSet<>();
    }
    public void estaBienDeclarado(){
    }
    public void consolidar(){
    }
    public Token getModificador() { return modificador; }
    public String getName() {
        return idToken.getLexeme();
    }
    public int getLine() {
        return idToken.getLineNumber();
    }
    public void addMethod(Method method) {
        methods.add(method);
    }
    public void addInheritance(Token herencia) {
        // Las interfaces no pueden heredar en este diseño
        throw new UnsupportedOperationException("Las interfaces no pueden heredar.");
    }
    public void addAttribute(Attribute attribute) {
        // Las interfaces no tienen atributos
        throw new UnsupportedOperationException("Las interfaces no pueden tener atributos.");
    }
    public void addConstructor(Constructor constructor) {
        // Las interfaces no tienen constructores
        throw new UnsupportedOperationException("Las interfaces no pueden tener constructores.");
    }
    public boolean isAbstract() {
        return true;
    }
    public boolean isStatic() {
        if(modificador!=null)
            return this.modificador.getType().equals(TokenType.sw_static);
        return false;
    }
    public boolean isFinal() {
        if(modificador!=null)
            return this.modificador.getType().equals(TokenType.sw_final);
        return false;
    }
}
