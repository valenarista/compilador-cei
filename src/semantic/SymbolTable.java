package semantic;

import java.util.HashMap;
import java.util.HashSet;

public class SymbolTable {
    EntidadClase claseActual;
    Atributo atributoActual;
    Metodo metodoActual;
    Constructor constructorActual;
    HashSet<EntidadClase> clases;


    public SymbolTable(){
        clases = new HashSet<>();
    }

    public void chequeoDeclaraciones() {
        for (EntidadClase entidadClase : clases) {
            entidadClase.estaBienDeclarado();
        }
        for (EntidadClase entidadClase : clases) {
            entidadClase.consolidar();
        }
    }

    void estaBienDeclarado(){}
    void consolidar(){}
}