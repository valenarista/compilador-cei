package semantic.entity;

import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.declarable.Attribute;
import semantic.declarable.Constructor;
import semantic.declarable.Method;
import semantic.declarable.Parameter;

import java.util.HashMap;
import static compiler.Main.symbolTable;

public class Interface implements EntityClass {
    private Token idToken;
    private Token herencia;
    private Token modificador;
    HashMap<String,Method> methods;
    HashMap<String,Attribute> attributes;
    private boolean consolidated;

    public Interface(Token idToken,Token modificador) {
        this.idToken = idToken;
        this.modificador = modificador;
        this.methods = new HashMap<>();
        this.attributes = new HashMap<>();
    }
    public void estaBienDeclarado(){
        if(modificador!=null && modificador.getType().equals(TokenType.sw_static))
            throw new SemanticException("Una Interface no anidada no puede ser static.",idToken.getLexeme(), idToken.getLineNumber());
        if(modificador!=null && modificador.getType().equals(TokenType.sw_final))
            throw new SemanticException("Una Interface no puede ser final.",idToken.getLexeme(), idToken.getLineNumber());

        for(Attribute a : attributes.values()){
            a.estaBienDeclarado();
        }
        for(Method m : methods.values()) {
            m.estaBienDeclarado();
        }

        checkInheritance();
        checkCircularInheritance(herencia);

    }
    public boolean consolidated(){
        return consolidated;
    }
    public void consolidar(){
        if(consolidated)
            return ;
        if(herencia!=null) {
            consolidatedParent();
            EntityClass parent = symbolTable.getClass(herencia.getLexeme());
            for(Attribute attribute : parent.getAttributes().values()){
                inheritanceAttribute(attribute);
            }
            for(Method method : parent.getMethods().values()){
                inheritanceMethod(method);
            }

        }

        consolidated = true;

    }
    private void consolidatedParent(){
        EntityClass parent = symbolTable.getClass(herencia.getLexeme());
        if(!parent.consolidated())
            parent.consolidar();
    }
    private void inheritanceAttribute(Attribute attribute){
        if(attributes.get(attribute.getName())==null){
            attributes.put(attribute.getName(),attribute);
        } else if (!attributes.get(attribute.getName()).getType().equals(attribute.getType())) {
            throw new SemanticException("No se puede heredar el atributo "+attribute.getName()+" porque ya fue declarado en la clase "+this.getName(),attribute.getName() ,attributes.get(attribute.getName()).getLine());
        }
    }
    private void inheritanceMethod(Method m) {
        if (methods.get(m.getName()) == null) {
            methods.put(m.getName(), m);
        } else {
            Method existingMethod = methods.get(m.getName());
            if (!(existingMethod.getReturnType().getName().equals(m.getReturnType().getName()))) {
                throw new SemanticException("El metodo "+m.getName()+" ya fue declarado en la interface "+this.getName()+" con una firma diferente. (Error en tipo de retorno)",existingMethod.getName() ,existingMethod.getLine());
            }
            if((existingMethod.getParamList().size() != m.getParamList().size())){
                throw new SemanticException("El metodo "+m.getName()+" ya fue declarado en la interface "+this.getName()+" con una firma diferente. (Error en cantidad de parametros)",existingMethod.getName() ,existingMethod.getLine());
            }
            for (int i = 0; i < existingMethod.getParamList().size(); i++) {
                Parameter existingParam = existingMethod.getParamList().get(i);
                Parameter newParam = m.getParamList().get(i);
                if (!(existingParam.getType().getName().equals(newParam.getType().getName()))) {
                    throw new SemanticException("El metodo "+m.getName()+" ya fue declarado en la interface "+this.getName()+" con una firma diferente. (Error en tipo de parametros)",existingMethod.getName() ,existingMethod.getLine());
                }
            }

        }
    }

    public boolean isInterface() {
        return true;
    }
    public Token getHerencia() { return herencia; }
    public Token getModificador() { return modificador; }
    public String getName() {
        return idToken.getLexeme();
    }

    public HashMap<String, Method> getMethods() {
        return methods;
    }

    public int getLine() {
        return idToken.getLineNumber();
    }
    public HashMap<String,Attribute> getAttributes(){
        return attributes;
    }
    public Constructor getConstructor() {
        // Las interfaces no tienen constructores
        return null;
    }
    public void addMethod(Method method) {
        methods.put(method.getName(),method);
    }
    public void addInheritance(Token herencia) {
        this.herencia = herencia;
    }
    private void checkInheritance() {
        //Una interface puede heredar de otra interface, pero no de una clase

        if (herencia != null) {
            if (symbolTable.getClass(herencia.getLexeme()) == null)
                throw new SemanticException("La interfaz heredada no existe.", herencia.getLexeme(), herencia.getLineNumber());
            if (symbolTable.getClass(herencia.getLexeme()).isClass())
                throw new SemanticException("Una interfaz no puede heredar de una clase.", herencia.getLexeme(), herencia.getLineNumber());

            if (symbolTable.getClass(herencia.getLexeme()).isFinal())
                throw new SemanticException("No se puede heredar de una interfaz final.", herencia.getLexeme(), herencia.getLineNumber());
            if (symbolTable.getClass(herencia.getLexeme()).isStatic())
                throw new SemanticException("No se puede heredar de una interfaz static.", herencia.getLexeme(), herencia.getLineNumber());
        }
    }
    private void checkCircularInheritance(Token herencia) {
        String currentInterfaceName = this.getName();
        Token current = herencia;
        while (current != null) {
            String parentName = current.getLexeme();
            if (parentName.equals(currentInterfaceName)) {
                throw new SemanticException("Herencia circular detectada en la interface " + currentInterfaceName, parentName, current.getLineNumber());
            }
            EntityClass parentClass = symbolTable.getClass(parentName);
            if (parentClass != null) {
                current = parentClass.getHerencia();
            } else {
                break;
            }
        }
    }

    public void addAttribute(Attribute attribute) {
        if(attributes.get(attribute.getName())==null){
            attributes.put(attribute.getName(),attribute);
        } else {
            throw new SemanticException("El atributo "+attribute.getName()+" ya fue declarado en la interface "+this.getName(),attribute.getName() ,attribute.getLine());
        }
    }
    public void addConstructor(Constructor constructor) {
        // Las interfaces no tienen constructores
        throw new SemanticException("Las interfaces no pueden tener constructores.",constructor.getName(),constructor.getLine());
    }
    public boolean isAbstract() {
        return true;
    }
    public boolean isClass() {
        return false;
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
