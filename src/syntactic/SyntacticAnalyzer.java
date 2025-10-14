package syntactic;

import exceptions.SyntacticException;
import lexical.LexicalAnalyzerMultiDetect;
import lexical.Token;
import lexical.TokenType;
import semantic.ast.sentence.*;
import semantic.declarable.Attribute;
import semantic.declarable.Constructor;
import semantic.declarable.Method;
import semantic.declarable.Parameter;
import semantic.entity.ConcreteClass;
import semantic.entity.EntityClass;
import semantic.entity.Interface;
import semantic.types.*;

import static compiler.Main.symbolTable;


import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class SyntacticAnalyzer {
    LexicalAnalyzerMultiDetect lexicalAnalyzer;
    Token currentToken;

    public SyntacticAnalyzer(LexicalAnalyzerMultiDetect lexicalAnalyzer) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        try {
            this.currentToken = lexicalAnalyzer.getNextToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void inicial() throws SyntacticException{
        listaClases();
        match(TokenType.eof);
    }

    void listaClases(){
        if(primerosClase(currentToken)||primerosInterface(currentToken)){
            Token modificador = modificadorOpcional();
            conjuntoClaseOInterfaz(modificador);
        }
        else {
            //Epsilon
        }
    }
    void conjuntoClaseOInterfaz(Token modificador){
        if (primerosClase(currentToken)) {
            clase(modificador);
            listaClases();
        }
        else if(primerosInterface(currentToken)){
            interfaz(modificador);
            listaClases();
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token: "+TokenType.sw_class+" o un token: "+TokenType.sw_interface);
        }
    }
    void interfaz(Token modificador){
        match(TokenType.sw_interface);
        Token nombre = currentToken;
        match(TokenType.classID);
        EntityClass nuevaInterfaz = new Interface(nombre,modificador);
        symbolTable.setCurrentClass(nuevaInterfaz.getName(),nuevaInterfaz);
        tipoParametricoOpcional();
        Token herencia = herenciaOpcionalInterfaz();
        nuevaInterfaz.addInheritance(herencia);
        match(TokenType.openCurly);
        listaMiembrosInterfaz();
        match(TokenType.closeCurly);
        symbolTable.addCurrentClass();
    }
    Token herenciaOpcionalInterfaz(){
        if (currentToken.getType().equals(TokenType.sw_extends)){
            match(TokenType.sw_extends);
            Token nombre = currentToken;
            match(TokenType.classID);
            tipoParametricoOpcional();
            return nombre;
        }
        else{
            //epsilon
            return null;
        }
    }

    void listaMiembrosInterfaz(){
        if(primerosMiembroInterfaz(currentToken)){
            miembroInterfaz();
            listaMiembrosInterfaz();
        }
        else {
            //Epsilon
        }
    }

    void miembroInterfaz(){
        Token visibilidad = visibilidadOpcional();
        cuerpoMiembroInterfaz(visibilidad);
    }
    void cuerpoMiembroInterfaz(Token visibility){
        if(primerosModificador(currentToken)){
            Token modificador = modificador();
            metodoConMod(modificador,visibility);
        }
        else if(primerosMiembroMetVarInterfaz(currentToken)){
            miembroMetVarIntefaz(visibility);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token: [public-abstract-final-static void] o un tipo valido");
        }
    }
    void miembroMetVarIntefaz(Token visibility){
        if(primerosTipo(currentToken)){
            Type type = tipo();
            Token nombre = currentToken;
            match(TokenType.metVarID);
            miembroTail(nombre,type,visibility);
        }
        else if(currentToken.getType().equals(TokenType.sw_void)){
            match(TokenType.sw_void);
            Type type = new VoidType();
            Token nombre = currentToken;
            match(TokenType.metVarID);
            Method nuevoMetodo = new Method(nombre,type,null,visibility);
            List<Parameter> paramList = metodoTail(nuevoMetodo);
            for(Parameter p : paramList){
                nuevoMetodo.addParameter(p);
            }
            symbolTable.setCurrentMethod(nuevoMetodo);

        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un  tipo o un token: "+TokenType.sw_void);
        }
    }


    void clase(Token modificador){
        match(TokenType.sw_class);
        Token nombre = currentToken;
        match(TokenType.classID);
        ConcreteClass nuevaClase = new ConcreteClass(nombre,modificador);
        symbolTable.setCurrentClass(nuevaClase.getName(),nuevaClase);
        tipoParametricoOpcional();
        Token herencia = herenciaImplementacionOpcional(nuevaClase);
        nuevaClase.addInheritance(herencia);
        match(TokenType.openCurly);
        listaMiembros();
        match(TokenType.closeCurly);
        symbolTable.addCurrentClass();
    }

    Token herenciaImplementacionOpcional(ConcreteClass clase){
        if(currentToken.getType().equals(TokenType.sw_extends)){
            return herenciaOpcional();
        }
        else if(currentToken.getType().equals(TokenType.sw_implements)){
            match(TokenType.sw_implements);
            Token nombre = currentToken;
            match(TokenType.classID);
            clase.addImplementation(nombre);
            tipoParametricoOpcional();
            return new Token(TokenType.classID,"Object",0);
        }
        else{
            //epsilon
            return new Token(TokenType.classID,"Object",0);
        }
    }
    void tipoParametricoOpcional(){
        if(currentToken.getType().equals(TokenType.lessOp)){
            match(TokenType.lessOp);
            match(TokenType.classID);
            match(TokenType.greaterOp);
        }
        else{
            //epsilon
        }
    }
    Token modificadorOpcional(){
        if (currentToken.getType().equals(TokenType.sw_final)){
            Token modificador = currentToken;
            match(TokenType.sw_final);
            return modificador;
        }
        else if (currentToken.getType().equals(TokenType.sw_abstract)){
            Token modificador = currentToken;
            match(TokenType.sw_abstract);
            return modificador;
        }
        else if (currentToken.getType().equals(TokenType.sw_static)){
            Token modificador = currentToken;
            match(TokenType.sw_static);
            return modificador;
        }
        else{
            //epsilon
            return null;
        }
    }
    Token herenciaOpcional(){
        if (currentToken.getType().equals(TokenType.sw_extends)){
            match(TokenType.sw_extends);
            Token nombre = currentToken;
            match(TokenType.classID);
            tipoParametricoOpcional();
            return nombre;
        }
        else{
            //epsilon
            return new Token(TokenType.classID,"Object",0);
        }
    }
    void listaMiembros(){
        if(primerosVisibilidadMiembro(currentToken)){
            visibilidadMiembro();
            listaMiembros();
        }
        else {
            //Epsilon
        }
    }
    void visibilidadMiembro(){
        Token visibilidad = visibilidadOpcional();
        miembro(visibilidad);
    }
    Token visibilidadOpcional(){
        if(currentToken.getType().equals(TokenType.sw_public)){
            Token visibility = currentToken;
            match(TokenType.sw_public);
            return visibility;
        }
        else if(currentToken.getType().equals(TokenType.sw_private)){
            Token visibility = currentToken;
            match(TokenType.sw_private);
            return visibility;
        }
        else{
            //epsilon
            //Visibilidad por defecto es public
            return new Token(TokenType.sw_public,"public",0);
        }
    }
    void miembro(Token visibility){
        if(currentToken.getType().equals(TokenType.classID)){
            Token tipoOEncabezado = currentToken;
            match(TokenType.classID);
            constructorOMetVar(tipoOEncabezado,visibility);
        } else if (primerosModificador(currentToken)){
            Token modificador = modificador();
            metodoConMod(modificador,visibility);
        } else if (primerosMiembroMetVar(currentToken)){
            miembroMetVar(visibility);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token: [public-abstract-final-static void] o un tipo valido");
        }
    }
    void constructorOMetVar(Token tipoOEncabezado,Token visibilidad){
        if(primerosConstructor(currentToken)){
            constructor(tipoOEncabezado,visibilidad);
        }
        else if(currentToken.getType().equals(TokenType.metVarID)||currentToken.getType().equals(TokenType.lessOp)){
            tipoParametricoOpcional();
            Token nombre = currentToken;
            match(TokenType.metVarID);
            Type tipo = new ReferenceType(tipoOEncabezado);
            miembroTail(nombre, tipo,visibilidad);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token: "+TokenType.openBracket+" o un token: "+TokenType.semicolon);
        }
    }
    void constructor(Token nombre,Token visibilidad){
        Constructor nuevoConstructor = new Constructor(nombre,visibilidad);
        List<Parameter> paramList = argsFormales();
        for(Parameter p : paramList){
            nuevoConstructor.addParameter(p);
        }
        symbolTable.addCurrentConstructor(nuevoConstructor);
        nuevoConstructor.setBlock(bloque());
    }

    List<Parameter> argsFormales(){
        match(TokenType.openBracket);
        List<Parameter> paramList = listaArgsFormalesOpcional();
        match(TokenType.closeBracket);
        return paramList;
    }
    List<Parameter> listaArgsFormalesOpcional(){
        if(primerosListaArgsFormales(currentToken)){
            return listaArgsFormales();
        }
        else{
            //epsilon
            return new java.util.ArrayList<>();
        }
    }
    List<Parameter> listaArgsFormales(){
        Parameter parametro = argFormal();
        List<Parameter> head = new java.util.ArrayList<>();
        head.add(parametro);
        List<Parameter> tail = listaArgFormales_Recursivo();
        head.addAll(tail);
        return head;
    }
    List<Parameter> listaArgFormales_Recursivo(){
        if(currentToken.getType().equals(TokenType.comma)){
            match(TokenType.comma);
            Parameter parametro = argFormal();
            List<Parameter> head = new java.util.ArrayList<>();
            head.add(parametro);
            List<Parameter> tail = listaArgFormales_Recursivo();
            head.addAll(tail);
            return head;
        }
        else{
            //epsilon
            return new java.util.ArrayList<>();
        }
    }
    Parameter argFormal(){
        Type argType =  tipo();
        Token nombre = currentToken;
        match(TokenType.metVarID);
        return new Parameter(nombre,argType);
    }
    Type tipo(){
        if(primerosTipoPrimitivo(currentToken)){
            return tipoPrimitivo();
        }
        else {
            Type argType = new ReferenceType(currentToken);
            match(TokenType.classID);
            tipoParametricoOpcional();
            return argType;
        }
    }
    PrimitiveType tipoPrimitivo(){
        if(currentToken.getType().equals(TokenType.sw_int)){
            PrimitiveType tipo = new IntType();
            match(TokenType.sw_int);
            return tipo;
        }
        else if(currentToken.getType().equals(TokenType.sw_char)){
            PrimitiveType tipo = new CharType();
            match(TokenType.sw_char);
            return tipo;
        }
        else if(currentToken.getType().equals(TokenType.sw_boolean)){
            PrimitiveType tipo = new BooleanType();
            match(TokenType.sw_boolean);
            return tipo;
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear un tipo primitivo" );
        }
    }

    BlockNode bloque(){
        match(TokenType.openCurly);
        BlockNode nuevoBloque = new BlockNode();
        List<SentenceNode> sentenceNodeList = new java.util.ArrayList<>();
        listaSentencias(sentenceNodeList);
        for(SentenceNode s : sentenceNodeList){
            nuevoBloque.addSentence(s);
        }
        match(TokenType.closeCurly);
        return nuevoBloque;
    }
    void listaSentencias(List<SentenceNode> sentenceNodeList){
        if(primerosSentencia(currentToken)){
            sentenceNodeList.add(sentencia());
            listaSentencias(sentenceNodeList);
        }
        else{
            //epsilon
        }
    }
    Token modificador(){
        if(currentToken.getType().equals(TokenType.sw_static)){
            Token modificador = currentToken;
            match(TokenType.sw_static);
            return modificador;
        }
        else if(currentToken.getType().equals(TokenType.sw_final)){
            Token modificador = currentToken;
            match(TokenType.sw_final);
            return modificador;
        }
        else if(currentToken.getType().equals(TokenType.sw_abstract)){
            Token modificador = currentToken;
            match(TokenType.sw_abstract);
            return modificador;
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un modificador [static-abstract-final]" );
        }
    }
    void metodoConMod(Token modificador,Token visibility){
        Type tipo = tipoMetodo();
        Token nombre = currentToken;
        match(TokenType.metVarID);
        Method nuevoMetodo = new Method(nombre,tipo,modificador,visibility);
        List<Parameter> paramList = metodoTail(nuevoMetodo);
        for(Parameter p : paramList){
            nuevoMetodo.addParameter(p);
        }
        symbolTable.setCurrentMethod(nuevoMetodo);
    }
    Type tipoMetodo(){
        if(currentToken.getType().equals(TokenType.sw_void)){
            match(TokenType.sw_void);
            return new VoidType();
           }
        else{
            return tipo();
        }
    }
    List<Parameter> metodoTail(Method method){
        List<Parameter> paramList = argsFormales();
        bloqueOpcional(method);
        return paramList;
    }
    void bloqueOpcional(Method method){
        if(primerosBloque(currentToken)){
            method.setHasBody(true);
            method.setBlock(bloque());
        }else if(currentToken.getType().equals(TokenType.semicolon)){
            method.setHasBody(false);
            match(TokenType.semicolon);
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un primero de bloque o un token: "+TokenType.semicolon);

        }
    }
    void miembroMetVar(Token visibility){
        if(primerosTipoPrimitivo(currentToken)){
            Type type = tipoPrimitivo();
            Token nombre = currentToken;
            match(TokenType.metVarID);
            miembroTail(nombre, type,visibility);
        }
        else if(currentToken.getType().equals(TokenType.sw_void)){
            match(TokenType.sw_void);
            Type type = new VoidType();
            Token nombre = currentToken;
            match(TokenType.metVarID);
            Method nuevoMetodo = new Method(nombre,type,null,visibility);
            List<Parameter> paramList = metodoTail(nuevoMetodo);
            for(Parameter p : paramList){
                nuevoMetodo.addParameter(p);
            }
            symbolTable.setCurrentMethod(nuevoMetodo);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un  tipo o un token: "+TokenType.sw_void);
        }
    }
    void miembroTail(Token nombre, Type tipo,Token visibility){
        if(primerosMetodoTail(currentToken)){
            Method nuevoMetodo = new Method(nombre,tipo,null,visibility);
            List<Parameter> paramList = metodoTail(nuevoMetodo);
            symbolTable.setCurrentMethod(nuevoMetodo);
            for(Parameter p : paramList){
                nuevoMetodo.addParameter(p);
            }
        }
        else if(currentToken.getType().equals(TokenType.semicolon) || primerosAtributoTail(currentToken)){
            Attribute nuevoAtributo = new Attribute(nombre,tipo,visibility);
            symbolTable.setCurrentAttribute(nuevoAtributo);
            atributoTail();
            match(TokenType.semicolon);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token:"+TokenType.openCurly+" o un token: "+TokenType.semicolon);
        }
    }

    void atributoTail(){
        if(currentToken.getType().equals(TokenType.assignOp)){
            match(TokenType.assignOp);
            expresionCompuesta();
        } else{
            //epsilon
        }

    }
    SentenceNode sentencia(){
        if(currentToken.getType().equals(TokenType.semicolon)){
            match(TokenType.semicolon);
            return new EmptySentenceNode();
        }
        else if(primerosExpresion(currentToken)){
            expresion();
            match(TokenType.semicolon);
            return new EmptySentenceNode(); //TODO ESTA BIEN?
        }
        else if(primerosFor(currentToken)){
            forSentencia();
            return new ForStandardNode();
        }
        else if(primerosBloque(currentToken)){
            symbolTable.getCurrentInvocable().setBlock(bloque());
            return symbolTable.getCurrentBlock(); //TODO ESTA BIEN?
        }
        else if(primerosIf(currentToken)){
            ifSentencia();
            return new IfNode();
        }
        else if(primerosWhile(currentToken)){
            whileSentencia();
            return new WhileNode();
        }
        else if(primerosReturn(currentToken)) {
            returnSentencia();
            match(TokenType.semicolon);
            return new ReturnNode();
        }
        else if(primerosVarLocal(currentToken)){
            varLocal();
            match(TokenType.semicolon);
            return new VarLocalNode();
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con una sentencia valida" );
        }
    }
    void forSentencia(){
        match(TokenType.sw_for);
        match(TokenType.openBracket);
        contenidoFor();
        match(TokenType.closeBracket);
        sentencia();
    }
    void contenidoFor(){
        if(currentToken.getType().equals(TokenType.sw_var)){
            match(TokenType.sw_var);
            match(TokenType.metVarID);
            forTipo();
        }
        else if(primerosExpresion(currentToken)){
            expresion();
            forStandard();
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un tipo o una expresion");
        }
    }
    void forTipo(){
        if(primerosForStandard(currentToken)){
            match(TokenType.assignOp);
            expresionCompuesta();
            forStandard();
        } else if (primerosForIterador(currentToken)) {
            forIterador();
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con el simbolo [:] o con el simbolo [=]");
        }
    }
    void forStandard(){
        match(TokenType.semicolon);
        expresion();
        match(TokenType.semicolon);
        expresion();
    }
    void forIterador(){
        match(TokenType.colon);
        expresion();
    }
    void ifSentencia(){
        match(TokenType.sw_if);
        match(TokenType.openBracket);
        expresion();
        match(TokenType.closeBracket);
        sentencia();
        ifSentencia_Recursivo();
    }
    void ifSentencia_Recursivo(){
        if(currentToken.getType().equals(TokenType.sw_else)){
            match(TokenType.sw_else);
            sentencia();
        }
        else{
            //epsilon
        }
    }
    void whileSentencia(){
        match(TokenType.sw_while);
        match(TokenType.openBracket);
        expresion();
        match(TokenType.closeBracket);
        sentencia();
    }
    void returnSentencia(){
        match(TokenType.sw_return);
        expresionOpcional();
    }
    void varLocal(){
        match(TokenType.sw_var);
        match(TokenType.metVarID);
        match(TokenType.assignOp);
        expresionCompuesta();
    }
    void expresion(){
        expresionCompuesta();
        expresion_Recursiva();
    }
    void expresion_Recursiva(){
        if(primerosOperadorAsignacion(currentToken)){
            operadorAsignacion();
            expresionCompuesta();
        } else{
            //epsilon
        }
    }
    void operadorAsignacion(){
        if(currentToken.getType().equals(TokenType.assignOp)){
            match(TokenType.assignOp);
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba un operador de asignacion");
        }
    }
    void expresionCompuesta(){
        expresionBasica();
        operacionTernariaOpcional();
        expresionCompuesta_Recursiva();
    }

    void operacionTernariaOpcional(){
        if(currentToken.getType().equals(TokenType.questionMark)){
            match(TokenType.questionMark);
            expresionCompuesta();
            match(TokenType.colon);
            expresionCompuesta();
        } else{
            //epsilon
        }
    }

    void expresionCompuesta_Recursiva(){
        if(primerosOperadorBinario(currentToken)) {
            operadorBinario();
            expresionBasica();
            operacionTernariaOpcional();
            expresionCompuesta_Recursiva();
        }else{
            //epsilon
        }
    }

    void expresionBasica(){
        if(primerosOperadorUnario(currentToken)) {
            operadorUnario();
            operando();
        } else if(primerosOperando(currentToken)){
            operando();
        } else {
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un operador unario o un operando valido" );
        }
    }
    void operadorUnario(){
        if(currentToken.getType().equals(TokenType.addOp)){
            match(TokenType.addOp);
        } else if(currentToken.getType().equals(TokenType.subOp)){
            match(TokenType.subOp);
        } else if(currentToken.getType().equals(TokenType.postIncrement)){
            match(TokenType.postIncrement);
        } else if(currentToken.getType().equals(TokenType.postDecrement)){
            match(TokenType.postDecrement);
        } else if(currentToken.getType().equals(TokenType.notOp)){
            match(TokenType.notOp);
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType()
                    ,"Se esperaba matchear con un operador unario ["+TokenType.addOp+TokenType.subOp+TokenType.postDecrement+TokenType.postIncrement+TokenType.notOp+"]");
        }
    }
    void operadorBinario(){
        if(currentToken.getType().equals(TokenType.orOp)){
            match(TokenType.orOp);
        } else if(currentToken.getType().equals(TokenType.andOp)){
            match(TokenType.andOp);
        } else if(currentToken.getType().equals(TokenType.equalOp)){
            match(TokenType.equalOp);
        } else if(currentToken.getType().equals(TokenType.notEqualOp)){
            match(TokenType.notEqualOp);
        } else if(currentToken.getType().equals(TokenType.greaterOp)){
            match(TokenType.greaterOp);
        } else if(currentToken.getType().equals(TokenType.lessOp)){
            match(TokenType.lessOp);
        } else if(currentToken.getType().equals(TokenType.greaterEqualOp)){
            match(TokenType.greaterEqualOp);
        } else if(currentToken.getType().equals(TokenType.lessEqualOp)){
            match(TokenType.lessEqualOp);
        } else if(currentToken.getType().equals(TokenType.addOp)){
            match(TokenType.addOp);
        } else if(currentToken.getType().equals(TokenType.subOp)){
            match(TokenType.subOp);
        } else if(currentToken.getType().equals(TokenType.mulOp)){
            match(TokenType.mulOp);
        } else if(currentToken.getType().equals(TokenType.divOp)){
            match(TokenType.divOp);
        } else if(currentToken.getType().equals(TokenType.modOp)){
            match(TokenType.modOp);
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un operador binario");
        }
    }
    void operando(){
        if(primerosPrimitivo(currentToken)){
            primitivo();
        } else if(primerosReferencia(currentToken)){
            referencia();
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un operando valido" );
        }
    }
    void primitivo(){
        if(currentToken.getType().equals(TokenType.intLiteral)){
            match(TokenType.intLiteral);
        } else if(currentToken.getType().equals(TokenType.charLiteral)){
            match(TokenType.charLiteral);
        } else if(currentToken.getType().equals(TokenType.sw_true)){
            match(TokenType.sw_true);
        } else if(currentToken.getType().equals(TokenType.sw_false)){
            match(TokenType.sw_false);
        } else if(currentToken.getType().equals(TokenType.sw_null)){
            match(TokenType.sw_null);
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un primitivo [literalInt-literalChar-true-false-null]" );
        }
    }
    void referencia(){
        primario();
        referencia_Recursiva();
    }
    void referencia_Recursiva(){
        if(currentToken.getType().equals(TokenType.dot)){
            match(TokenType.dot);
            match(TokenType.metVarID);
            argsActualesOpcional();
            referencia_Recursiva();
        } else{
            //epsilon
        }
    }
    void argsActualesOpcional(){
        if(primerosArgsActuales(currentToken)){
            argsActuales();
        } else{
            //epsilon
        }
    }
    void argsActuales(){
        match(TokenType.openBracket);
        listaExpresionesOpcional();
        match(TokenType.closeBracket);
    }
    void listaExpresionesOpcional(){
        if(primerosListaExpresiones(currentToken)){
            listaExpresiones();
        } else{
            //epsilon
        }
    }
    void listaExpresiones(){
        expresion();
        listaExpresiones_Recursiva();
    }
    void listaExpresiones_Recursiva(){
        if(currentToken.getType().equals(TokenType.comma)){
            match(TokenType.comma);
            expresion();
            listaExpresiones_Recursiva();
        } else{
            //epsilon
        }
    }
    void expresionOpcional(){
        if(primerosExpresion(currentToken)){
            expresion();
        } else{
            //epsilon
        }
    }
    void primario(){
        if(currentToken.getType().equals(TokenType.stringLiteral)){
            match(TokenType.stringLiteral);
        } else if(currentToken.getType().equals(TokenType.sw_this)){
            match(TokenType.sw_this);
        } else if(currentToken.getType().equals(TokenType.metVarID)){
            match(TokenType.metVarID);
            llamadaTail();
        } else if(primerosLlamadaConstructor(currentToken)){
            llamadaConstructor();
        } else if(primerosExpresionParentizada(currentToken)){
            expresionParentizada();
        } else if(primerosLlamadaMetodoEstatico(currentToken)){
            llamadaMetodoEstatico();
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con una llamada a constructor, expresion parentizada, llamada a metodo estatico o [literalString-this-idMetodoVariable]" );
        }
    }
    void llamadaTail(){
        if(primerosArgsActuales(currentToken)){
            argsActuales();
        } else{
            //epsilon
        }
    }
    void llamadaConstructor(){
        match(TokenType.sw_new);
        match(TokenType.classID);
        tipoParametricoDiamanteOpcional();
        argsActuales();
    }
    void tipoParametricoDiamanteOpcional(){
        if(currentToken.getType().equals(TokenType.lessOp)){
            match(TokenType.lessOp);
            diamanteVacioOTipo();
            match(TokenType.greaterOp);
        } else{
            //epsilon
        }
    }
    void diamanteVacioOTipo(){
        if(currentToken.getType().equals(TokenType.classID)){
            match(TokenType.classID);
        } else {
            //epsilon
        }
    }
    void expresionParentizada(){
        match(TokenType.openBracket);
        expresion();
        match(TokenType.closeBracket);
    }
    void llamadaMetodoEstatico(){
        match(TokenType.classID);
        match(TokenType.dot);
        match(TokenType.metVarID);
        argsActuales();
    }


    void match(TokenType tokenName) {
        if(tokenName.equals(currentToken.getType())){
            try {
                currentToken = lexicalAnalyzer.getNextToken();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            if(currentToken.getType().equals(TokenType.eof))
                throw new SyntacticException("",currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear el token: "+tokenName);
            
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear el token: "+tokenName);
        }
    }
    boolean primerosMiembroInterfaz(Token token){
        return primerosModificador(token) || primerosMiembroMetVar(token) || token.getType().equals(TokenType.sw_public) || token.getType().equals(TokenType.sw_private);
    }

    boolean primerosVisibilidadMiembro(Token token){
        return token.getType().equals(TokenType.sw_public) || token.getType().equals(TokenType.sw_private) || primerosMiembro(token);
    }
    boolean primerosInterface(Token token){
        return token.getType().equals(TokenType.sw_interface) || primerosModificador(token);
    }
    boolean primerosAtributoTail(Token token){
        return token.getType().equals(TokenType.assignOp);
    }
    boolean primerosForStandard(Token token){
        return token.getType().equals(TokenType.assignOp);
    }
    boolean primerosForIterador(Token token){
        return token.getType().equals(TokenType.colon);
    }
    boolean primerosClase(Token token){
        return token.getType().equals(TokenType.sw_class)||primerosModificador(token);
    }
    boolean primerosFor(Token token){
        return token.getType().equals(TokenType.sw_for);
    }
    boolean primerosListaArgsFormales(Token token){
        return primerosArgFormal(token);
    }
    boolean primerosArgFormal(Token token){
        return primerosTipo(token);
    }
    boolean primerosOperadorAsignacion(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.assignOp
        ));
        return primeros.contains(token.getType());
    }
    boolean primerosListaExpresiones(Token token){
        return primerosExpresion(token);
    }
    boolean primerosArgsActuales(Token token){
        return token.getType() == TokenType.openBracket;
    }


    boolean primerosMiembro(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.classID
        ));
        return primeros.contains(token.getType()) || primerosModificador(token) || primerosMiembroMetVar(token);
    }
    boolean primerosConstructor(Token token){
        return primerosArgsFormales(token);
    }
    boolean primerosModificador(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.sw_static,
                TokenType.sw_final,
                TokenType.sw_abstract
        ));
        return primeros.contains(token.getType());
    }
    boolean primerosMiembroMetVarInterfaz(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.sw_void
        ));
        return primeros.contains(token.getType())||primerosTipo(token);
    }

    boolean primerosMiembroMetVar(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.sw_void
        ));
        return primeros.contains(token.getType())||primerosTipoPrimitivo(token);
    }
    boolean primerosTipo(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.classID
        ));
        return primeros.contains(token.getType()) || primerosTipoPrimitivo(token);
    }
    boolean primerosTipoPrimitivo(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.sw_int,
                TokenType.sw_boolean,
                TokenType.sw_char
        ));
        return primeros.contains(token.getType());
    }
    boolean primerosSentencia(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.semicolon
        ));
        return primeros.contains(token.getType()) || primerosExpresion(token) || primerosFor(token) || primerosBloque(token) || primerosIf(token) || primerosWhile(token) || primerosReturn(token) || primerosVarLocal(token);
    }
    boolean primerosBloque(Token token){
        return token.getType() == TokenType.openCurly;
    }
    boolean primerosIf(Token token){
        return token.getType() == TokenType.sw_if;
    }
    boolean primerosWhile(Token token) {
        return token.getType() == TokenType.sw_while;
    }
    boolean primerosReturn(Token token){
        return token.getType() == TokenType.sw_return;
    }
    boolean primerosVarLocal(Token token) {
        return token.getType() == TokenType.sw_var;
    }
    boolean primerosExpresion(Token token){
        return primerosExpresionCompuesta(token);
    }
    boolean primerosExpresionCompuesta(Token token){
        return primerosExpresionBasica(token);
    }
    boolean primerosExpresionBasica(Token token){
        return primerosOperadorUnario(token) || primerosOperando(token);
    }
    boolean primerosOperadorUnario(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.subOp,
                TokenType.notOp,
                TokenType.postDecrement,
                TokenType.postIncrement,
                TokenType.addOp
        ));
        return primeros.contains(token.getType());
    }
    boolean primerosOperando(Token token){
        return primerosPrimitivo(token) || primerosReferencia(token);
    }

    boolean primerosPrimitivo(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.intLiteral,
                TokenType.charLiteral,
                TokenType.sw_true,
                TokenType.sw_false,
                TokenType.sw_null
        ));
        return primeros.contains(token.getType());
    }
    boolean primerosReferencia(Token token){
        return primerosPrimario(token);
    }
    boolean primerosPrimario(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.stringLiteral,
                TokenType.sw_this,
                TokenType.metVarID
        ));
        return primeros.contains(token.getType()) || primerosLlamadaConstructor(token) ||
                primerosExpresionParentizada(token) || primerosLlamadaMetodoEstatico(token) ;
    }
    boolean primerosLlamadaConstructor(Token token){
        return token.getType() == TokenType.sw_new;
    }
    boolean primerosExpresionParentizada(Token token) {
        return token.getType() == TokenType.openBracket;
    }
    boolean primerosLlamadaMetodoEstatico(Token token){
        return token.getType() == TokenType.classID;
    }
    boolean primerosMetodoTail(Token token){
        return primerosArgsFormales(token);
    }
    boolean primerosArgsFormales(Token token){
        return token.getType() == TokenType.openBracket;
    }
    boolean primerosOperadorBinario(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.andOp,
                TokenType.orOp,
                TokenType.equalOp,
                TokenType.notEqualOp,
                TokenType.greaterOp,
                TokenType.greaterEqualOp,
                TokenType.lessOp,
                TokenType.lessEqualOp,
                TokenType.subOp,
                TokenType.addOp,
                TokenType.mulOp,
                TokenType.divOp,
                TokenType.modOp
        ));
        return primeros.contains(token.getType());
    }
}
