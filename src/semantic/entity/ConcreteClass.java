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

public class ConcreteClass implements EntityClass {
    Token idToken;
    Token herencia;
    Token implementation;
    Token modificador;
    HashMap<String,Attribute> attributes;
    HashMap<String,Method> methods;
    Constructor constructor;
    boolean consolidated;

    public ConcreteClass(Token idToken,Token modificador) {
        this.idToken = idToken;
        this.modificador = modificador;
        this.implementation = null;
        this.attributes = new HashMap<>();
        this.methods = new HashMap<>();
        this.constructor = null;
        consolidated = false;
    }
    public boolean consolidated(){
        return consolidated;
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
            this.constructor = new Constructor(this.idToken,new Token(TokenType.sw_public,"public",this.idToken.getLineNumber()));
        }

        checkInheritance();
        checkCircularInheritance(herencia);
        if(implementation!=null){
            checkImplementation();
        }
    }
    public void checkInheritance(){
        if(herencia!=null && symbolTable.getClass(herencia.getLexeme())==null){
            throw new SemanticException("La clase padre "+herencia.getLexeme()+" no existe.",herencia.getLexeme(), herencia.getLineNumber());
        }
        if(herencia!=null && symbolTable.getClass(herencia.getLexeme()).isInterface()){
            throw new SemanticException("Una clase no puede heredar de una interfaz.",herencia.getLexeme(), herencia.getLineNumber());
        }
        if(herencia!=null && isAbstract() && !(symbolTable.getClass(herencia.getLexeme()).isAbstract())){
            if(!herencia.getLexeme().equals("Object"))
                throw new SemanticException("Una clase concreta no puede heredar de una clase abstracta.",herencia.getLexeme(), herencia.getLineNumber());
        }

        if(herencia!=null && symbolTable.getClass(herencia.getLexeme()).isStatic())
            throw new SemanticException("Una clase no puede heredar de una clase static.",herencia.getLexeme(), herencia.getLineNumber());
        if(herencia!=null && symbolTable.getClass(herencia.getLexeme()).isFinal())
            throw new SemanticException("Una clase no puede heredar de una clase final.",herencia.getLexeme(), herencia.getLineNumber());
    }
    public boolean isAbstract() {
        if(modificador!=null)
            return this.modificador.getType().equals(TokenType.sw_abstract);
        return false;
    }
    public boolean isFinal() {
        if(modificador!=null)
            return this.modificador.getType().equals(TokenType.sw_final);
        return false;
    }
    public boolean isStatic() {
        if(modificador!=null)
            return this.modificador.getType().equals(TokenType.sw_static);
        return false;
    }
    private boolean imObject(){
        return getName().equals("Object");
    }

    public void consolidar(){
        if(imObject()) consolidated = true;
        if(consolidated)
            return ;
        consolidatedParent();
        if(herencia!=null){
            ConcreteClass parentClass = (ConcreteClass) symbolTable.getClass(herencia.getLexeme());
            if(parentClass != null){
                //Agregar atributos heredados
                for(Attribute a : parentClass.getAttributes().values()){
                    inheriteAttribute(a);
                }
                //Agregar metodos heredados
                for(Method m : parentClass.getMethods().values()){
                    inheriteMethod(m);
                }
                //Agregar constructor heredado si no tiene
                if(this.constructor==null && parentClass.constructor!=null){
                    this.constructor = parentClass.constructor;
                }
            }
        }
        if(implementation!=null){
            Interface toImplement = (Interface) symbolTable.getClass(implementation.getLexeme());
            for(Attribute attribute : toImplement.getAttributes().values()){
                inheriteAttribute(attribute);
            }
            for(Method method : toImplement.getMethods().values()){
                implementMethod(method);
            }
        }
        consolidated = true;
    }
    private void consolidatedParent(){
        if(herencia!=null){
            symbolTable.getClass(herencia.getLexeme()).consolidar();
        }
        if(implementation!=null){
            symbolTable.getClass(implementation.getLexeme()).consolidar();
        }
    }
    private void implementMethod(Method method){
        if(methods.get(method.getName())==null){
            throw new SemanticException("El metodo "+method.getName()+"no esta siendo implementado por la clase "+getName(),method.getName(), method.getLine());
        }
        //TODO CONSULTA POR LOS MODIFICADORES
        Method implementedMethod = methods.get(method.getName());
        if (!(implementedMethod.getReturnType().getName().equals(method.getReturnType().getName())) || (implementedMethod.getParamList().size() != method.getParamList().size())) {
            throw new SemanticException("El metodo "+method.getName()+" esta siendo implementado en la clase "+this.getName()+" pero con una firma diferente.",implementedMethod.getName() ,implementedMethod.getLine());
        }
        for (int i = 0; i < implementedMethod.getParamList().size(); i++) {
            Parameter existingParam = implementedMethod.getParamList().get(i);
            Parameter newParam = method.getParamList().get(i);
            if (!(existingParam.getType().getName().equals(newParam.getType().getName()))) {
                throw new SemanticException("El metodo "+method.getName()+" esta siendo implementado en la clase "+this.getName()+" pero con una firma diferente.",implementedMethod.getName() ,implementedMethod.getLine());
            }
        }

    }
    private void inheriteAttribute(Attribute attribute) {
        //TODO CAMBIAR PARA PODER SOBREESCRIBIR ATRIBUTOS
        if(attributes.get(attribute.getName())==null){
            attributes.put(attribute.getName(),attribute);
        } else if (!attributes.get(attribute.getName()).getType().equals(attribute.getType())) {
            throw new SemanticException("No se puede heredar el atributo "+attribute.getName()+" porque ya fue declarado en la clase "+this.getName(),attribute.getName() ,attributes.get(attribute.getName()).getLine());
        }

    }
    private void inheriteMethod(Method method) {
        if(methods.get(method.getName())==null){
            if(method.getModifier()!=null)
                checkAbstractModifier(method);
            methods.put(method.getName(),method);
        } else {
            Method existingMethod = methods.get(method.getName());
            if (!(existingMethod.getReturnType().getName().equals(method.getReturnType().getName())) || (existingMethod.getParamList().size() != method.getParamList().size())) {
                throw new SemanticException("El metodo "+method.getName()+" ya fue declarado en la clase "+this.getName()+" con una firma diferente.",existingMethod.getName() ,existingMethod.getLine());
            }
            for (int i = 0; i < existingMethod.getParamList().size(); i++) {
                Parameter existingParam = existingMethod.getParamList().get(i);
                Parameter newParam = method.getParamList().get(i);
                if (!(existingParam.getType().getName().equals(newParam.getType().getName()))) {
                    throw new SemanticException("El metodo "+method.getName()+" ya fue declarado en la clase "+this.getName()+" con una firma diferente.",existingMethod.getName() ,existingMethod.getLine());
                }
            }
            if(method.getModifier()!=null)
                checkMethodModifier(method, existingMethod);
        }
    }
    private void checkAbstractModifier(Method method) {
        if(method.getModifier().getType().equals(TokenType.sw_abstract) && (this.modificador==null || !(this.modificador.getType().equals(TokenType.sw_abstract)))) {
            throw new SemanticException("El metodo " + method.getName() + " no puede ser heredado en la clase " + this.getName() + " por ser abstracto y la clase concreta.", method.getName(), method.getLine());
        }
    }
    private void checkMethodModifier(Method method, Method existingMethod) {
        if(method.getModifier().getType().equals(TokenType.sw_final)||method.getModifier().getType().equals(TokenType.sw_static)) {
            throw new SemanticException("El metodo " + method.getName() + " no puede ser sobreescrito en la clase " + this.getName() + " por ser final o static.", methods.get(method.getName()).getName(), methods.get(method.getName()).getLine());
        } else if (method.getModifier().getType().equals(TokenType.sw_abstract) && (this.modificador==null || !(this.modificador.getType().equals(TokenType.sw_abstract)))) {
            if(existingMethod.getModifier()!=null && existingMethod.getModifier().getType().equals(TokenType.sw_abstract))
                throw new SemanticException("El metodo " + method.getName() + " tiene que ser implementado en la clase " + this.getName() + " sin ser abstracto.", methods.get(method.getName()).getName(), methods.get(method.getName()).getLine());
        }
    }
    public boolean isClass() {
        return true;
    }

    public boolean isInterface() {
        return false;
    }

    public String getName() {
        return idToken.getLexeme();
    }
    public int getLine() {
        return idToken.getLineNumber();
    }
    public Token getModificador() {
        return modificador;
    }
    public HashMap<String, Attribute> getAttributes() {
        return attributes;
    }
    public HashMap<String, Method> getMethods() {
        return methods;
    }
    public Token getHerencia() {
        return herencia;
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
            if(method.getModifier()!=null && method.getModifier().getType().equals(TokenType.sw_abstract) &&
                    (this.modificador==null || !(this.modificador.getType().equals(TokenType.sw_abstract)))){
                throw new SemanticException("Una clase concreta no puede tener metodos abstractos.",method.getName() ,method.getLine());
            }
            methods.put(method.getName(),method);
        } else {
            throw new SemanticException("El metodo "+method.getName()+" ya fue declarado en la clase "+this.getName(),method.getName() ,method.getLine());
        }

    }
    public void addConstructor(Constructor constructor) {
        if(this.modificador!=null && this.modificador.getType().equals(TokenType.sw_abstract)){
            throw new SemanticException("Una clase abstracta no puede tener constructor.",constructor.getName() ,constructor.getLine());
        }

        if(!constructor.getName().equals(getName()))
            throw new SemanticException("El constructor debe tener el mismo nombre que la clase "+this.getName(),constructor.getName() ,constructor.getLine());

        if(this.constructor==null){
            this.constructor = constructor;
        } else {
            throw new SemanticException("El constructor ya fue declarado en la clase "+this.getName(),constructor.getName() ,constructor.getLine());
        }
    }
    public void addInheritance(Token herencia) {
        this.herencia = herencia;
    }
    public void addImplementation(Token implementation) {
        this.implementation = implementation;
    }
    private void checkImplementation(){
        if(symbolTable.getClass(implementation.getLexeme())==null){
            throw new SemanticException("La interfaz "+implementation.getLexeme()+" no existe.",implementation.getLexeme(), implementation.getLineNumber());
        }
        if(!symbolTable.getClass(implementation.getLexeme()).isInterface()){
            throw new SemanticException("Una clase concreta no puede implementar una clase.",implementation.getLexeme(), implementation.getLineNumber());
        }

        //TODO CONSULTAR FINAL Y STATIC
        if(symbolTable.getClass(implementation.getLexeme()).isFinal())
            throw new SemanticException("No se puede implementar una interfaz final.",implementation.getLexeme(), implementation.getLineNumber());
        if(symbolTable.getClass(implementation.getLexeme()).isStatic())
            throw new SemanticException("No se puede implementar una interfaz static.",implementation.getLexeme(), implementation.getLineNumber());


        Interface interfaceToImplement = (Interface) symbolTable.getClass(implementation.getLexeme());
        for(Method m : interfaceToImplement.getMethods().values()) {
            if (methods.get(m.getName()) == null) {
                throw new SemanticException("La clase concreta " + this.getName() + " debe implementar el metodo " + m.getName() + " de la interfaz " + interfaceToImplement.getName(), m.getName(), m.getLine());
            } else {
                Method classMethod = methods.get(m.getName());
                if (!(classMethod.getReturnType().getName().equals(m.getReturnType().getName())) || (classMethod.getParamList().size() != m.getParamList().size())) {
                    throw new SemanticException("El metodo " + m.getName() + " de la clase concreta " + this.getName() + " no coincide con la firma del metodo de la interfaz " + interfaceToImplement.getName(), m.getName(), classMethod.getLine());
                }
                for (int i = 0; i < classMethod.getParamList().size(); i++) {
                    Parameter classParam = classMethod.getParamList().get(i);
                    Parameter interfaceParam = m.getParamList().get(i);
                    if (!(classParam.getType().getName().equals(interfaceParam.getType().getName()))) {
                        throw new SemanticException("El metodo " + m.getName() + " de la clase concreta " + this.getName() + " no coincide con la firma del metodo de la interfaz " + interfaceToImplement.getName(), m.getName(), classMethod.getLine());
                    }
                }

            }
        }
    }

    private void checkCircularInheritance(Token herencia) {
        String currentClassName = this.getName();
        Token current = herencia;
        while (current != null) {
            String parentName = current.getLexeme();
            if (parentName.equals(currentClassName)) {
                throw new SemanticException("Herencia circular detectada en la clase " + currentClassName, parentName, current.getLineNumber());
            }
            EntityClass parentClass = symbolTable.getClass(parentName);
            if (parentClass != null) {
                current = parentClass.getHerencia();
            } else {
                break;
            }
        }
    }


}
