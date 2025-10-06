package semantic;

import compiler.Main;
import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.declarable.Attribute;
import semantic.declarable.Constructor;
import semantic.declarable.Method;
import semantic.declarable.Parameter;
import semantic.entity.ConcreteClass;
import semantic.entity.EntityClass;
import semantic.types.*;

import java.util.HashMap;

public class SymbolTable {
    public EntityClass claseActual;
    Attribute attributeActual;
    Method methodActual;
    Constructor constructorActual;
    public HashMap<String,EntityClass> clases;


    public SymbolTable(){
        clases = new HashMap<>();
    }

    public void createPredefinedClasses() {
        //Object class
        EntityClass objectClass = new ConcreteClass(new Token(TokenType.classID,"Object",0));
        Method debugPrint = new Method(new Token(TokenType.metVarID,"debugPrint",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        debugPrint.addParameter(new Parameter(new Token(TokenType.metVarID,"i",0),new IntType()));
        objectClass.addMethod(debugPrint);
        clases.put("Object",objectClass);

        //String class
        EntityClass stringClass = new ConcreteClass(new Token(TokenType.classID,"String",0));
        stringClass.addInheritance(new Token(TokenType.classID,"Object",0));
        clases.put("String",stringClass);

        //System class
        EntityClass systemClass = new ConcreteClass(new Token(TokenType.classID,"System",0));
        systemClass.addInheritance(new Token(TokenType.classID,"Object",0));
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
        //clases.forEach((name,clase) -> clase.consolidar() );
    }


    public void setCurrentClass(String lexeme, EntityClass nuevaClase) {
        if(clases.get(lexeme) == null){
            claseActual = nuevaClase;
        } else{
            throw new SemanticException("Se intento crear una clase ya existente ",nuevaClase.getName(), nuevaClase.getLine());
        }

    }
    public void addCurrentClass() {
        clases.put(claseActual.getName(),claseActual);
    }
    public void setCurrentConstructor(Constructor constructor) {
        this.constructorActual = constructor;
    }
    public void setCurrentMethod(Method method) {
        claseActual.addMethod(method);
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
    public EntityClass getClass(String lexeme) {
        return clases.get(lexeme);
    }

}