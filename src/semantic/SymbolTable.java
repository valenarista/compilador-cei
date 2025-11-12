package semantic;

import compiler.Main;
import exceptions.SemanticException;
import lexical.Token;
import lexical.TokenType;
import semantic.ast.sentence.BlockNode;
import semantic.ast.sentence.NullBlockNode;
import semantic.ast.sentence.VarLocalNode;
import semantic.declarable.*;
import semantic.entity.ConcreteClass;
import semantic.entity.EntityClass;
import semantic.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTable {
    public EntityClass claseActual;
    Attribute attributeActual;
    Invocable currentInvocable;
    Constructor constructorActual;
    Method methodActual;
    public HashMap<String,EntityClass> clases;
    BlockNode currentBlock;
    public List<String> instructionList;


    public SymbolTable(){
        clases = new HashMap<>();
        instructionList = new ArrayList<>();
    }

    public void createPredefinedClasses() {
        //Object class
        EntityClass objectClass = new ConcreteClass(new Token(TokenType.classID,"Object",0),null);
        Method debugPrint = new Method(new Token(TokenType.metVarID,"debugPrint",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        debugPrint.setBlock(new NullBlockNode());
        debugPrint.addParameter(new Parameter(new Token(TokenType.metVarID,"i",0),new IntType()));
        objectClass.addMethod(debugPrint);
        objectClass.addConstructor(new Constructor(new Token(TokenType.classID,"Object",0),null));
        objectClass.setPredefined(true);
        clases.put("Object",objectClass);
        debugPrint.setOwnerClass(objectClass);


        //String class
        EntityClass stringClass = new ConcreteClass(new Token(TokenType.classID,"String",0),null);
        stringClass.addInheritance(new Token(TokenType.classID,"Object",0));
        stringClass.addConstructor(new Constructor(new Token(TokenType.classID,"String",0),null));
        stringClass.setPredefined(true);
        clases.put("String",stringClass);

        //System class
        EntityClass systemClass = new ConcreteClass(new Token(TokenType.classID,"System",0),null);
        systemClass.addInheritance(new Token(TokenType.classID,"Object",0));
        systemClass.addConstructor(new Constructor(new Token(TokenType.classID,"System",0),null));
        systemClass.setPredefined(true);
        clases.put("System",systemClass);

        Method read = new Method(new Token(TokenType.metVarID,"read",0),new IntType(),new Token(TokenType.sw_static,"static",0));
        read.setBlock(new NullBlockNode());
        systemClass.addMethod(read);
        read.setOwnerClass(systemClass);

        Method printB = new Method(new Token(TokenType.metVarID,"printB",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printB.setBlock(new NullBlockNode());
        printB.addParameter(new Parameter(new Token(TokenType.metVarID,"b",0),new BooleanType()));
        systemClass.addMethod(printB);
        printB.setOwnerClass(systemClass);

        Method printC = new Method(new Token(TokenType.metVarID,"printC",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printC.setBlock(new NullBlockNode());
        printC.addParameter(new Parameter(new Token(TokenType.metVarID,"c",0),new CharType()));
        systemClass.addMethod(printC);
        printC.setOwnerClass(systemClass);

        Method printI = new Method(new Token(TokenType.metVarID,"printI",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printI.setBlock(new NullBlockNode());
        printI.addParameter(new Parameter(new Token(TokenType.metVarID,"i",0),new IntType()));
        systemClass.addMethod(printI);
        printI.setOwnerClass(systemClass);

        Method printS = new Method(new Token(TokenType.metVarID,"printS",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printS.setBlock(new NullBlockNode());
        printS.addParameter(new Parameter(new Token(TokenType.metVarID,"s",0),new ReferenceType(new Token(TokenType.classID,"String",0))));
        systemClass.addMethod(printS);
        printS.setOwnerClass(systemClass);

        Method println = new Method(new Token(TokenType.metVarID,"println",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        println.setBlock(new NullBlockNode());
        systemClass.addMethod(println);
        println.setOwnerClass(systemClass);

        Method printBln = new Method(new Token(TokenType.metVarID,"printBln",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printBln.setBlock(new NullBlockNode());
        printBln.addParameter(new Parameter(new Token(TokenType.metVarID,"b",0),new BooleanType()));
        systemClass.addMethod(printBln);
        printBln.setOwnerClass(systemClass);

        Method printCln = new Method(new Token(TokenType.metVarID,"printCln",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printCln.setBlock(new NullBlockNode());
        printCln.addParameter(new Parameter(new Token(TokenType.metVarID,"c",0),new CharType()));
        systemClass.addMethod(printCln);
        printCln.setOwnerClass(systemClass);

        Method printIln = new Method(new Token(TokenType.metVarID,"printIln",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printIln.setBlock(new NullBlockNode());
        printIln.addParameter(new Parameter(new Token(TokenType.metVarID,"i",0),new IntType()));
        systemClass.addMethod(printIln);
        printIln.setOwnerClass(systemClass);

        Method printSln = new Method(new Token(TokenType.metVarID,"printSln",0),new VoidType(),new Token(TokenType.sw_static,"static",0));
        printSln.setBlock(new NullBlockNode());
        printSln.addParameter(
                new Parameter(
                        new Token(TokenType.metVarID,"s",0),
                        new ReferenceType(new Token(TokenType.classID,"String",0))));
        systemClass.addMethod(printSln);
        printSln.setOwnerClass(systemClass);

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
        boolean found = false;
        if(currentBlock != null){
            if(currentBlock.getParentBlock() != null){
                found = currentBlock.getVarLocalMap().get(id) != null;
                if(!found){
                    BlockNode parentBlock = currentBlock.getParentBlock();
                    while(parentBlock != null && !found){
                        found = parentBlock.getVarLocalMap().get(id) != null;
                        parentBlock = parentBlock.getParentBlock();
                    }
                    return found;
                } else{
                    return true;
                }
            }
            found = currentBlock.getVarLocalMap().get(id) != null;
        }
        return found;
    }
    public Type getLocalVarType(String id) {
        Type type = new NullType(new Token(TokenType.sw_null,"null",0));
        VarLocalNode var = null;
        if(currentBlock != null){
            if(currentBlock.getParentBlock() != null){
                var = currentBlock.getVarLocalMap().get(id);
                if(var == null){
                    BlockNode parentBlock = currentBlock.getParentBlock();
                    while(var == null && parentBlock != null){
                        var = parentBlock.getVarLocalMap().get(id);
                        parentBlock = parentBlock.getParentBlock();
                    }
                    if(var != null)
                        return var.getType();
                } else{
                    return var.getType();
                }
            }
            var = currentBlock.getVarLocalMap().get(id);
            type = var.getType();
        }
        return type;
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
    public boolean checkCompatibility(ReferenceType leftType, ReferenceType rightType) {
        if (leftType.getName().equals(rightType.getName())) {
            return true;
        }
        EntityClass parentClass;
        parentClass = clases.get(leftType.getName());
        while (parentClass != null) {
            if (parentClass.getName().equals(rightType.getName())) {
                return true;
            }
            if (parentClass.getHerencia() != null) {
                parentClass = clases.get(parentClass.getHerencia().getLexeme());
            } else {
                parentClass = null;
            }
        }
        return false;
    }

    public void generateCode(){
        setOffsets();
        initGenerator();
        heapRoutinesGenerator();
        defaultClassesGenerator();

        for(EntityClass clase : clases.values()){
            if(!clase.isPredefined())
                clase.generateCode();
        }
    }
    private void initGenerator(){
        instructionList.add(".CODE");
        instructionList.add("PUSH simple_heap_init");
        instructionList.add("CALL");

        instructionList.add("PUSH main");
        instructionList.add("CALL");
        instructionList.add("HALT");
    }

    private void heapRoutinesGenerator(){
        instructionList.add("simple_heap_init:");
        instructionList.add("RET 0");

        instructionList.add("simple_malloc:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOADHL");
        instructionList.add("DUP");
        instructionList.add("PUSH 1");
        instructionList.add("ADD");
        instructionList.add("STORE 4");
        instructionList.add("LOAD 3");
        instructionList.add("ADD");
        instructionList.add("STOREHL");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");
    }
    private void defaultClassesGenerator() {
        //Object class
        //static void debugPrint(int i)
        instructionList.add("; Clase Object");
        instructionList.add("Object_debugPrint:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOAD 3");
        instructionList.add("IPRINT");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");

        //System class
        //static int read()
        instructionList.add("; Clase System");
        instructionList.add("System_read:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("READ");
        instructionList.add("STORE 3");
        instructionList.add("STOREFP");
        instructionList.add("RET 0");

        //static void printB(boolean b)
        instructionList.add("System_printB:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOAD 3");
        instructionList.add("BPRINT");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");

        //static void printC(char c)
        instructionList.add("System_printC:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOAD 3");
        instructionList.add("CPRINT");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");

        //static void printI(int i)
        instructionList.add("System_printI:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOAD 3");
        instructionList.add("IPRINT");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");

        //static void printS(String s)
        instructionList.add("System_printS:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOAD 3");
        instructionList.add("SPRINT");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");

        //static void println()
        instructionList.add("System_println:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("PRNLN");
        instructionList.add("STOREFP");
        instructionList.add("RET 0");

        //static void printBln(boolean b)
        instructionList.add("System_printBln:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOAD 3");
        instructionList.add("BPRINT");
        instructionList.add("PRNLN");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");

        //static void printCln(char c)
        instructionList.add("System_printCln:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOAD 3");
        instructionList.add("CPRINT");
        instructionList.add("PRNLN");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");

        //static void printIln(int i)
        instructionList.add("System_printIln:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOAD 3");
        instructionList.add("IPRINT");
        instructionList.add("PRNLN");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");

        //static void printSln(String s)
        instructionList.add("System_printSln:");
        instructionList.add("LOADFP");
        instructionList.add("LOADSP");
        instructionList.add("STOREFP");
        instructionList.add("LOAD 3");
        instructionList.add("SPRINT");
        instructionList.add("PRNLN");
        instructionList.add("STOREFP");
        instructionList.add("RET 1");
    }
    private void setOffsets(){
        for(EntityClass clase : clases.values()){
            clase.setOffsets();
        }
    }

    public int getLocalVarOffset(String varName) {
        System.out.println("DEBUG getLocalVarOffset: Buscando variable '" + varName + "'");

        VarLocalNode var = null;
        if (currentBlock != null) {
            System.out.println("  -> Bloque actual tiene " + currentBlock.getVarLocalMap().size() + " variables");
            var = currentBlock.getVarLocalMap().get(varName);
            if (var == null) {
                BlockNode parentBlock = currentBlock.getParentBlock();
                while (var == null && parentBlock != null) {
                    System.out.println("  -> Buscando en bloque padre...");
                    var = parentBlock.getVarLocalMap().get(varName);
                    parentBlock = parentBlock.getParentBlock();
                }
            }
        }
        if (var != null) {
            System.out.println("  -> Variable encontrada con offset: " + var.getOffset());
            return var.getOffset();
        } else {
            throw new SemanticException("Error semantico: La variable local '" + varName + "' no existe en el bloque actual.", varName, 0);
        }
    }
}