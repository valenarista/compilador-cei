package semantic.entity;

import exceptions.SemanticException;
import lexical.Token;
import semantic.declarable.Attribute;
import semantic.declarable.Constructor;
import semantic.declarable.Method;

import java.util.HashMap;
import java.util.HashSet;

public class ConcreteClass implements EntityClass {
    Token idToken;
    Token herencia;
    HashMap<String,Attribute> attributes;
    HashMap<String,Method> methods;
    Constructor constructor;

    public ConcreteClass(Token idToken) {
        this.idToken = idToken;
        this.attributes = new HashMap<>();
        this.methods = new HashMap<>();
        this.constructor = null;
    }
    public void estaBienDeclarado(){

        for(Attribute a : attributes.values()){
            a.estaBienDeclarado();
        }
        for(Method m : methods.values()) {
            m.estaBienDeclarado();
        }
        if(constructor!=null){
            constructor.estaBienDeclarado();
        } else{
            //Constructor por defecto
            this.constructor = new Constructor(this.idToken);
        }

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
        if(attributes.get(attribute.getName())==null){
            attributes.put(attribute.getName(),attribute);
        } else {
            throw new SemanticException("El atributo "+attribute.getName()+" ya fue declarado en la clase "+this.getName(),attribute.getName() ,attribute.getLine());
        }
    }
    public void addMethod(Method method) {
        if(methods.get(method.getName())==null){
            methods.put(method.getName(),method);
        } else {
            throw new SemanticException("El metodo "+method.getName()+" ya fue declarado en la clase "+this.getName(),method.getName() ,method.getLine());
        }

    }
    public void addConstructor(Constructor constructor) {
        if(this.constructor==null){
            this.constructor = constructor;
        } else {
            throw new SemanticException("El constructor ya fue declarado en la clase "+this.getName(),constructor.getName() ,constructor.getLine());
        }
    }
    public void addInheritance(Token herencia) {
        this.herencia = herencia;
    }
}
