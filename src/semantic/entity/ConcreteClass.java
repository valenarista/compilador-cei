package semantic.entity;

import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.ast.sentence.*;
import semantic.declarable.Attribute;
import semantic.declarable.Constructor;
import semantic.declarable.Method;
import semantic.declarable.Parameter;
import semantic.types.BooleanType;

import java.util.HashMap;

import static compiler.Main.symbolTable;

public class ConcreteClass implements EntityClass {
    Token idToken;
    Token herencia;
    Token implementation;
    Token modificador;
    HashMap<String,Attribute> attributes;
    HashMap<String,Attribute> shadowedAttributes;
    HashMap<String,Method> methods;
    HashMap<String,Method> inheritedMethods;
    HashMap<String,Attribute> inheritedAtts;
    Constructor constructor;
    boolean consolidated;
    boolean predefined = false;

    private String VTLabel;

    public ConcreteClass(Token idToken,Token modificador) {
        this.idToken = idToken;
        this.modificador = modificador;
        this.implementation = null;
        this.attributes = new HashMap<>();
        this.shadowedAttributes = new HashMap<>();
        this.inheritedAtts = new HashMap<>();
        this.methods = new HashMap<>();
        this.inheritedMethods = new HashMap<>();
        this.constructor = null;
        consolidated = false;
        this.VTLabel = "VT_" + idToken.getLexeme();
    }
    public boolean consolidated(){
        return consolidated;
    }

    public void estaBienDeclarado(){
        if(modificador!=null && modificador.getType().equals(TokenType.sw_static))
            throw new SemanticException("Error semantico en linea "+idToken.getLineNumber()+" Una clase no anidada no puede ser static.",idToken.getLexeme(), idToken.getLineNumber());

        for(Attribute a : attributes.values()){
            a.estaBienDeclarado();
        }
        for(Method m : methods.values()) {
            m.estaBienDeclarado();
            if((m.getModifier()==null || !m.getModifier().getType().equals(TokenType.sw_abstract)) && !m.hasBody()){
                throw new SemanticException("Error semantico en linea "+m.getLine()+" Los metodos no abstractos deben tener cuerpo.",m.getName(), m.getLine());
            }
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

    @Override
    public boolean isPredefined() {
        return predefined;
    }
    @Override
    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
    }

    public void chequeoSentencias(){
        symbolTable.setCurrentClass(idToken.getLexeme(), this);
        symbolTable.setCurrentInvocable(null);
        symbolTable.setCurrentBlock(null);

        for(Attribute a : attributes.values()){
            a.chequeoSentencias();
        }
        for(Method m : methods.values()) {
            if(inheritedMethods.get(m.getName())==null) {
                m.chequeoSentencias();
                boolean completa = checkDeadCode(m.getBlock(),true);
                if(!(m.getBlock() instanceof NullBlockNode)) {
                    if (completa && !m.getReturnType().getName().equals("void")) {
                        throw new SemanticException("Error semantico en linea " + m.getLine() + " El metodo " + m.getName() + " no retorna en todos los caminos posibles.", m.getName(), m.getLine());
                    }
                }
            }
        }
        if(constructor!=null){
            constructor.chequeoSentencias();
        }
    }

    boolean checkDeadCode(SentenceNode sentence, boolean reachable){
        if(!reachable){
            throw new SemanticException("Error semantico en linea "+sentence.getLine()+" Codigo inalcanzable detectado.",sentence.getLexeme(), sentence.getLine());
        }
        if(sentence instanceof BlockNode){
            BlockNode block = (BlockNode) sentence;
            boolean currentReachable = reachable;
            for(SentenceNode s : block.getSentences()){
                currentReachable = checkDeadCode(s, currentReachable);
            }
            return currentReachable;
        }

        if(sentence instanceof IfNode){
            IfNode ifNode = (IfNode) sentence;
            boolean thenReachable = checkDeadCode(ifNode.getBody(), true);

            if(!(ifNode.getElseBody() instanceof EmptySentenceNode)){
                boolean elseReachable = checkDeadCode(ifNode.getElseBody(), true);
                return thenReachable || elseReachable;
            }

            return true;
        }

        if(sentence instanceof WhileNode){
            WhileNode whileNode = (WhileNode) sentence;
            boolean whileReachable = checkDeadCode(whileNode.getBody(), true);
            if(whileNode.getCondition().getLexeme().equals("true")){
                return whileReachable;
            }
            return true;
        }

        if(sentence instanceof ReturnNode){
            return false;
        }

        return true;
    }


    public void checkInheritance(){
        if(herencia!=null && symbolTable.getClass(herencia.getLexeme())==null){
            throw new SemanticException("Error semantico en linea "+herencia.getLineNumber()+" La clase padre "+herencia.getLexeme()+" no existe.",herencia.getLexeme(), herencia.getLineNumber());
        }
        if(herencia!=null && symbolTable.getClass(herencia.getLexeme()).isInterface()){
            throw new SemanticException("Error semantico en linea "+herencia.getLineNumber()+" Una clase no puede heredar de una interfaz.",herencia.getLexeme(), herencia.getLineNumber());
        }
        if(herencia!=null && isAbstract() && !(symbolTable.getClass(herencia.getLexeme()).isAbstract())){
            if(!herencia.getLexeme().equals("Object"))
                throw new SemanticException("Error semantico en linea "+herencia.getLineNumber()+" Una clase concreta no puede heredar de una clase abstracta.",herencia.getLexeme(), herencia.getLineNumber());
        }

        if(herencia!=null && symbolTable.getClass(herencia.getLexeme()).isStatic())
            throw new SemanticException("Error semantico en linea "+herencia.getLineNumber()+" Una clase no puede heredar de una clase static.",herencia.getLexeme(), herencia.getLineNumber());
        if(herencia!=null && symbolTable.getClass(herencia.getLexeme()).isFinal())
            throw new SemanticException("Error semantico en linea "+herencia.getLineNumber()+" Una clase no puede heredar de una clase final.",herencia.getLexeme(), herencia.getLineNumber());
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
            throw new SemanticException("Error semantico en linea "+method.getLine()+" El metodo "+method.getName()+"no esta siendo implementado por la clase "+getName(),method.getName(), method.getLine());
        }

        Method implementedMethod = methods.get(method.getName());
        if (!(implementedMethod.getReturnType().getName().equals(method.getReturnType().getName())) || (implementedMethod.getParamList().size() != method.getParamList().size())) {
            throw new SemanticException("Error semantico en linea "+ implementedMethod.getLine()+" El metodo "+method.getName()+" esta siendo implementado en la clase "+this.getName()+" pero con una firma diferente.",implementedMethod.getName() ,implementedMethod.getLine());
        }
        for (int i = 0; i < implementedMethod.getParamList().size(); i++) {
            Parameter existingParam = implementedMethod.getParamList().get(i);
            Parameter newParam = method.getParamList().get(i);
            if (!(existingParam.getType().getName().equals(newParam.getType().getName()))) {
                throw new SemanticException("Error semantico en linea "+ implementedMethod.getLine()+" El metodo "+method.getName()+" esta siendo implementado en la clase "+this.getName()+" pero con una firma diferente.",implementedMethod.getName() ,implementedMethod.getLine());
            }
        }
        checkMethodModifier(method, implementedMethod);
        checkVisibilityModifier(method, implementedMethod);

    }
    private void inheriteAttribute(Attribute attribute) {
        if(attributes.get(attribute.getName())==null){
            attributes.put(attribute.getName(),attribute);
            inheritedAtts.put(attribute.getName(),attribute);
        } else {
            shadowedAttributes.put(attribute.getName(),attribute);
        }

    }
    private void inheriteMethod(Method method) {
        if(methods.get(method.getName())==null){
            if(method.getModifier()!=null)
                checkAbstractModifier(method);
            methods.put(method.getName(),method);
            inheritedMethods.put(method.getName(),method);

        } else {
            Method existingMethod = methods.get(method.getName());
            if (!(existingMethod.getReturnType().getName().equals(method.getReturnType().getName())) || (existingMethod.getParamList().size() != method.getParamList().size())) {
                throw new SemanticException("Error semantico en linea "+existingMethod.getLine()+" El metodo "+method.getName()+" ya fue declarado en la clase "+this.getName()+" con una firma diferente.",existingMethod.getName() ,existingMethod.getLine());
            }
            for (int i = 0; i < existingMethod.getParamList().size(); i++) {
                Parameter existingParam = existingMethod.getParamList().get(i);
                Parameter newParam = method.getParamList().get(i);
                if (!(existingParam.getType().getName().equals(newParam.getType().getName()))) {
                    throw new SemanticException("Error semantico en linea "+existingMethod.getLine()+" El metodo "+method.getName()+" ya fue declarado en la clase "+this.getName()+" con una firma diferente.",existingMethod.getName() ,existingMethod.getLine());
                }
            }

            checkMethodModifier(method, existingMethod);
            checkVisibilityModifier(method, existingMethod);
            //Sobreescritura exitosa
        }
    }
    private void checkVisibilityModifier(Method method, Method existingMethod) {
        if(method.getVisibility().getType().equals(TokenType.sw_public)&& existingMethod.getVisibility().getType().equals(TokenType.sw_private)) {
            throw new SemanticException("Error semantico en linea "+methods.get(method.getName()).getLine()+" El metodo " + method.getName() + " no puede reducir su visibilidad en la clase " + this.getName() + ".", methods.get(method.getName()).getName(), methods.get(method.getName()).getLine());

        }
    }
    private void checkAbstractModifier(Method method) {
        if(method.getModifier().getType().equals(TokenType.sw_abstract) && (this.modificador==null || !(this.modificador.getType().equals(TokenType.sw_abstract)))) {
            throw new SemanticException("Error semantico en linea "+method.getLine()+" El metodo " + method.getName() + " no puede ser heredado en la clase " + this.getName() + " por ser abstracto y la clase concreta.", method.getName(), method.getLine());
        }
    }
    private void checkMethodModifier(Method method, Method existingMethod) {
        if(method.getModifier()!=null){
            if(method.getModifier().getType().equals(TokenType.sw_final)||method.getModifier().getType().equals(TokenType.sw_static)) {
                throw new SemanticException("Error semantico en linea "+methods.get(method.getName()).getLine()+" El metodo " + method.getName() + " no puede ser sobreescrito en la clase " + this.getName() + " por ser final o static.", methods.get(method.getName()).getName(), methods.get(method.getName()).getLine());
            } else if (method.getModifier().getType().equals(TokenType.sw_abstract) && (this.modificador==null || !(this.modificador.getType().equals(TokenType.sw_abstract)))) {
                if(existingMethod.getModifier()!=null && existingMethod.getModifier().getType().equals(TokenType.sw_abstract))
                    throw new SemanticException("Error semantico en linea "+methods.get(method.getName()).getLine()+" El metodo " + method.getName() + " tiene que ser implementado en la clase " + this.getName() + " sin ser abstracto.", methods.get(method.getName()).getName(), methods.get(method.getName()).getLine());
            }
            if(method.getModifier().getType().equals(TokenType.sw_abstract) && !existingMethod.hasBody() && (this.modificador==null || !(this.modificador.getType().equals(TokenType.sw_abstract)))) {
                throw new SemanticException("Error semantico en linea "+methods.get(method.getName()).getLine()+" El metodo " + method.getName() + " tiene que ser implementado y tener bloque en la clase " + this.getName(), methods.get(method.getName()).getName(), methods.get(method.getName()).getLine());
            }


            if(!(method.getModifier().getType().equals(TokenType.sw_static)) && existingMethod.getModifier()!=null && existingMethod.getModifier().getType().equals(TokenType.sw_static)) {
                throw new SemanticException("Error semantico en linea "+methods.get(method.getName()).getLine()+" El metodo " + method.getName() + " no puede ser sobreescrito en la clase " + this.getName() + " por ser static.", existingMethod.getName(), methods.get(method.getName()).getLine());
            }
        } else{
            if(existingMethod.getModifier()!=null && existingMethod.getModifier().getType().equals(TokenType.sw_static)){
                throw new SemanticException("Error semantico en linea "+methods.get(method.getName()).getLine()+" El metodo " + method.getName() + " no puede cambiar de naturaleza en la clase " + this.getName() + " para ser static.", existingMethod.getName(), methods.get(method.getName()).getLine());
            }
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

    @Override
    public HashMap<String, Method> getInheritedMethods() {
        return inheritedMethods;
    }

    @Override
    public HashMap<String, Attribute> getInheritedAttributes() {
        return inheritedAtts;
    }


    public Constructor getConstructor() {
        return constructor;
    }
    public Token getHerencia() {
        return herencia;
    }

    public void addAttribute(Attribute attribute) {
        if(attributes.get(attribute.getName())==null){
            attributes.put(attribute.getName(),attribute);
        } else {
            throw new SemanticException("Error semantico en linea "+attribute.getLine()+" El atributo "+attribute.getName()+" ya fue declarado en la clase "+this.getName(),attribute.getName() ,attribute.getLine());
        }
    }
    public void addMethod(Method method) {
        if(methods.get(method.getName())==null){
            if(method.getModifier()!=null && method.getModifier().getType().equals(TokenType.sw_abstract) &&
                    (this.modificador==null || !(this.modificador.getType().equals(TokenType.sw_abstract)))){
                throw new SemanticException("Error semantico en linea "+method.getLine()+" Una clase concreta no puede tener metodos abstractos.",method.getName() ,method.getLine());
            }
            methods.put(method.getName(),method);
        } else {
            throw new SemanticException("Error semantico en linea "+method.getLine()+" El metodo "+method.getName()+" ya fue declarado en la clase "+this.getName(),method.getName() ,method.getLine());
        }

    }
    public void addConstructor(Constructor constructor) {
        if(this.modificador!=null && this.modificador.getType().equals(TokenType.sw_abstract)){
            throw new SemanticException("Error semantico en linea "+constructor.getLine()+" Una clase abstracta no puede tener constructor.",constructor.getName() ,constructor.getLine());
        }

        if(!constructor.getName().equals(getName()))
            throw new SemanticException("Error semantico en linea "+constructor.getLine()+" El constructor debe tener el mismo nombre que la clase "+this.getName(),constructor.getName() ,constructor.getLine());

        if(this.constructor==null){
            this.constructor = constructor;
        } else {
            throw new SemanticException("Error semantico en linea "+constructor.getLine()+" El constructor ya fue declarado en la clase "+this.getName(),constructor.getName() ,constructor.getLine());
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
            throw new SemanticException("Error semantico en linea "+implementation.getLineNumber()+" La interfaz "+implementation.getLexeme()+" no existe.",implementation.getLexeme(), implementation.getLineNumber());
        }
        if(!symbolTable.getClass(implementation.getLexeme()).isInterface()){
            throw new SemanticException("Error semantico en linea "+implementation.getLineNumber()+" Una clase concreta no puede implementar una clase.",implementation.getLexeme(), implementation.getLineNumber());
        }

        if(symbolTable.getClass(implementation.getLexeme()).isFinal())
            throw new SemanticException("Error semantico en linea "+implementation.getLineNumber()+" No se puede implementar una interfaz final.",implementation.getLexeme(), implementation.getLineNumber());
        if(symbolTable.getClass(implementation.getLexeme()).isStatic())
            throw new SemanticException("Error semantico en linea "+implementation.getLineNumber()+" No se puede implementar una interfaz static.",implementation.getLexeme(), implementation.getLineNumber());

    }

    private void checkCircularInheritance(Token herencia) {
        String currentClassName = this.getName();
        Token current = herencia;
        while (current != null) {
            String parentName = current.getLexeme();
            if (parentName.equals(currentClassName)) {
                throw new SemanticException("Error semantico en linea "+current.getLineNumber()+" Herencia circular detectada en la clase " + currentClassName, parentName, current.getLineNumber());
            }
            EntityClass parentClass = symbolTable.getClass(parentName);
            if (parentClass != null) {
                current = parentClass.getHerencia();
            } else {
                break;
            }
        }
    }

    @Override
    public String getVTLabel() {
        return VTLabel;
    }

    public void generateCode(){
        generateVirtualTable();

        for(Method method : methods.values()){
            if(!inheritedMethods.containsKey(method.getName())){
                method.generateCode();
            }
        }

        constructor.generateCode();

    }
    private void generateVirtualTable() {
        symbolTable.instructionList.add(".DATA");

        String className = getName();
        String label = "VT_" + className;

        symbolTable.instructionList.add(label + ": NOP");

        for (Method method : methods.values()) {
            symbolTable.instructionList.add("DW " + method.getLabel());
        }
    }


    public void setOffsets(){
        //setAttributeOffsets();
        setMethodOffsets();
    }
    private void setAttributeOffsets(){
        int offset = 0;
        for(Attribute attribute : attributes.values()){
        //    attribute.setOffset(offset);
          //  offset += attribute.getType().getSize();
        }
    }
    private void setMethodOffsets() {
        int offset = 0;
        for (Method method : methods.values()) {
            method.setOffset(offset);
            offset += 1;
        }
    }





}
