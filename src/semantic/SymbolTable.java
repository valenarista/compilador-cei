package semantic;

import compiler.Main;
import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.ast.sentence.BlockNode;
import semantic.declarable.*;
import semantic.entity.ConcreteClass;
import semantic.entity.EntityClass;
import semantic.types.*;

import java.util.HashMap;

public class SymbolTable {
    public EntityClass claseActual;
    Attribute attributeActual;
    Invocable currentInvocable;
    Constructor constructorActual;
    Method methodActual;
    public HashMap<String,EntityClass> clases;
    BlockNode currentBlock;


    public SymbolTable(){
        clases = new HashMap<>();
    }

    public void createPredefinedClasses() {
        //Object class
        EntityClass objectClass = new ConcreteClass(new Token(TokenType.classID,"Object",0),null);
        Method debugPrint = new Method(new Token(TokenType.metVarID,"debugPrint",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        debugPrint.addParameter(new Parameter(new Token(TokenType.metVarID,"i",0),new IntType()));
        objectClass.addMethod(debugPrint);
        objectClass.addConstructor(new Constructor(new Token(TokenType.classID,"Object",0),null));
        clases.put("Object",objectClass);

        //String class
        EntityClass stringClass = new ConcreteClass(new Token(TokenType.classID,"String",0),null);
        stringClass.addInheritance(new Token(TokenType.classID,"Object",0));
        stringClass.addConstructor(new Constructor(new Token(TokenType.classID,"String",0),null));
        clases.put("String",stringClass);

        //System class
        EntityClass systemClass = new ConcreteClass(new Token(TokenType.classID,"System",0),null);
        systemClass.addInheritance(new Token(TokenType.classID,"Object",0));
        systemClass.addConstructor(new Constructor(new Token(TokenType.classID,"System",0),null));
        clases.put("System",systemClass);

        Method read = new Method(new Token(TokenType.metVarID,"read",0),new IntType(),new Token(TokenType.sw_static,"static",0));
        systemClass.addMethod(read);

        Method printB = new Method(new Token(TokenType.metVarID,"printB",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printB.addParameter(new Parameter(new Token(TokenType.metVarID,"b",0),new BooleanType()));
        systemClass.addMethod(printB);

        Method printC = new Method(new Token(TokenType.metVarID,"printC",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printC.addParameter(new Parameter(new Token(TokenType.metVarID,"c",0),new CharType()));
        systemClass.addMethod(printC);

        Method printI = new Method(new Token(TokenType.metVarID,"printI",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printI.addParameter(new Parameter(new Token(TokenType.metVarID,"i",0),new IntType()));
        systemClass.addMethod(printI);

        Method printS = new Method(new Token(TokenType.metVarID,"printS",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printS.addParameter(new Parameter(new Token(TokenType.metVarID,"s",0),new ReferenceType(new Token(TokenType.classID,"String",0))));
        systemClass.addMethod(printS);

        Method println = new Method(new Token(TokenType.metVarID,"println",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        systemClass.addMethod(println);

        Method printBln = new Method(new Token(TokenType.metVarID,"printBln",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printBln.addParameter(new Parameter(new Token(TokenType.metVarID,"b",0),new BooleanType()));
        systemClass.addMethod(printBln);

        Method printCln = new Method(new Token(TokenType.metVarID,"printCln",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printCln.addParameter(new Parameter(new Token(TokenType.metVarID,"c",0),new CharType()));
        systemClass.addMethod(printCln);

        Method printIln = new Method(new Token(TokenType.metVarID,"printIln",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printIln.addParameter(new Parameter(new Token(TokenType.metVarID,"i",0),new IntType()));
        systemClass.addMethod(printIln);

        Method printSln = new Method(new Token(TokenType.metVarID,"printSln",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printSln.addParameter(
                new Parameter(
                        new Token(TokenType.metVarID,"s",0),
                        new ReferenceType(new Token(TokenType.classID,"String",0))));
        systemClass.addMethod(printSln);

    }

    public void chequeoDeclaraciones() {
        clases.forEach((name,clase) -> clase.estaBienDeclarado() );
        clases.forEach((name,clase) -> clase.consolidar() );
    }
    public void chequeoSentencias()  {
        clases.forEach((name,clase) -> {
                clase.chequeoSentencias();
        } );
    }


    public void setCurrentClass(String lexeme, EntityClass nuevaClase) {
        claseActual = nuevaClase;
    }

    public void addCurrentClass() {
        if(clases.get(claseActual.getName()) == null){
            clases.put(claseActual.getName(),claseActual);
        } else{
            throw new SemanticException("Error semantico en linea "+claseActual.getLine()+" Se intento crear una clase ya existente ",claseActual.getName(), claseActual.getLine());
        }


    }
    public void setCurrentInvocable(Invocable invocable) {
        this.currentInvocable = invocable;
    }
    public Invocable getCurrentInvocable() {
        return this.currentInvocable;
    }
    public void setCurrentMethod(Method method) {
        this.methodActual = method;
        setCurrentInvocable(method);
    }
    public void addCurrentMethod() {
        claseActual.addMethod(methodActual);
    }
    public void setCurrentConstructor(Constructor constructor) {
        this.constructorActual = constructor;
        setCurrentInvocable(constructor);
    }
    public void addCurrentConstructor() {
        claseActual.addConstructor(constructorActual);
    }
    public void setCurrentBlock(BlockNode block) {
        this.currentBlock = block;
    }

    public Method getCurrentMethod() {
        return this.methodActual;
    }
    public EntityClass getCurrentClass() {
        return this.claseActual;
    }
    public BlockNode getCurrentBlock() {
        return this.currentBlock;
    }

    public Constructor getCurrentConstructor() {
        return this.constructorActual;
    }


    public Attribute getCurrentAttribute() {
        return this.attributeActual;
    }
    public void setCurrentAttribute(Attribute attribute) {
        claseActual.addAttribute(attribute);
        this.attributeActual = attribute;
    }

    public EntityClass getClass(String lexeme) {
        return clases.get(lexeme);
    }
    public boolean classExists(String lexeme) {
        return clases.get(lexeme) != null;
    }
    public boolean isParameter(String id) {
        if(currentInvocable != null){
            return currentInvocable.getParamList().stream().anyMatch(p -> p.getName().equals(id));
        }
        return false;
    }
    public boolean isLocalVar(String id) {
        if(currentBlock != null){
            return currentBlock.getVarLocalMap().get(id) != null;
        }
        return false;
    }
    public boolean isAttribute(String id) {
        if(claseActual != null){
            return claseActual.getAttributes().get(id) != null;
        }
        return false;
    }

    public Parameter getParameter(String id) {
        if (currentInvocable != null) {
            return currentInvocable.getParamList().stream().filter(p -> p.getName().equals(id)).findFirst().orElse(null);
        }
        return null;
    }



}