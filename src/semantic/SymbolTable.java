package semantic;

import exceptions.SemanticException;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public EntityClass claseActual;
    Attribute attributeActual;
    Method methodActual;
    Constructor constructorActual;
    HashMap<String,EntityClass> clases;


    public SymbolTable(){
        clases = new HashMap<>();
    }

    public void chequeoDeclaraciones() {
        clases.forEach((name,clase) -> clase.estaBienDeclarado() );
        clases.forEach((name,clase) -> clase.consolidar() );
    }

    void estaBienDeclarado(){}
    void consolidar(){}

    public void setCurrentClass(String lexeme, EntityClass nuevaClase) {
        if(clases.get(lexeme) == null){
            claseActual = nuevaClase;
        } else{
            throw new SemanticException(nuevaClase.getName(), nuevaClase.getLine());
        }

    }
    public void addCurrentClass() {
        clases.put(claseActual.getName(),claseActual);
    }
    public void setCurrentConstructor(Constructor constructor) {
        this.constructorActual = constructor;
    }
    public void setCurrentMethod(Method method) {
        this.methodActual = method;
    }
    public void setCurrentAttribute(Attribute attribute) {
        claseActual.addAttribute(attribute);
        this.attributeActual = attribute;
    }
    public void addCurrentMethod(Method method) {
        claseActual.addMethod(method);
    }
    public void addCurrentConstructor(Constructor constructor) {
        claseActual.addConstructor(constructor);
    }

}