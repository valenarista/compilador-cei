package semantic;

import lexical.Token;

import java.util.HashSet;

public class Interfaz implements EntidadClase{
    private Token idToken;
    HashSet<Metodo> metodos;

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
    public void addMetodo(Metodo metodo) {
        metodos.add(metodo);
    }
    public void addAtributo(Atributo atributo) {
        // Las interfaces no tienen atributos
        throw new UnsupportedOperationException("Las interfaces no pueden tener atributos.");
    }
    public void addConstructor(Constructor constructor) {
        // Las interfaces no tienen constructores
        throw new UnsupportedOperationException("Las interfaces no pueden tener constructores.");
    }
}
