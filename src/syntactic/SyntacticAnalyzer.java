package syntactic;

import exceptions.SyntacticException;
import lexical.LexicalAnalyzerMultiDetect;
import lexical.Token;
import lexical.TokenType;
import semantic.ast.chaining.ChainedCallNode;
import semantic.ast.chaining.ChainedVarNode;
import semantic.ast.chaining.ChainingNode;
import semantic.ast.expression.*;
import semantic.ast.literal.*;
import semantic.ast.reference.*;
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
            symbolTable.setCurrentMethod(nuevoMetodo);
            symbolTable.addCurrentMethod();
            List<Parameter> paramList = metodoTail(nuevoMetodo);
            for(Parameter p : paramList){
                nuevoMetodo.addParameter(p);
            }


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
        symbolTable.setCurrentConstructor(nuevoConstructor);
        symbolTable.addCurrentConstructor();
        List<Parameter> paramList = argsFormales();
        for(Parameter p : paramList){
            nuevoConstructor.addParameter(p);
        }
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
        nuevoBloque.setSentencesList(sentenceNodeList);
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
        symbolTable.setCurrentMethod(nuevoMetodo);
        symbolTable.addCurrentMethod();
        List<Parameter> paramList = metodoTail(nuevoMetodo);
        for(Parameter p : paramList){
            nuevoMetodo.addParameter(p);
        }

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
            method.setBlock(new NullBlockNode());
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
            symbolTable.setCurrentMethod(nuevoMetodo);
            symbolTable.addCurrentMethod();
            List<Parameter> paramList = metodoTail(nuevoMetodo);
            for(Parameter p : paramList){
                nuevoMetodo.addParameter(p);
            }

        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un  tipo o un token: "+TokenType.sw_void);
        }
    }
    void miembroTail(Token nombre, Type tipo,Token visibility){
        if(primerosMetodoTail(currentToken)){
            Method nuevoMetodo = new Method(nombre,tipo,null,visibility);
            symbolTable.setCurrentMethod(nuevoMetodo);
            symbolTable.addCurrentMethod();
            List<Parameter> paramList = metodoTail(nuevoMetodo);
            for(Parameter p : paramList){
                nuevoMetodo.addParameter(p);
            }

        }
        else if(currentToken.getType().equals(TokenType.semicolon) || primerosAtributoTail(currentToken)){
            Attribute nuevoAtributo = new Attribute(nombre,tipo,visibility);
            symbolTable.setCurrentAttribute(nuevoAtributo);
            ExpressionNode optionalValue = atributoTail();
            nuevoAtributo.setValue(optionalValue);
            match(TokenType.semicolon);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token:"+TokenType.openCurly+" o un token: "+TokenType.semicolon);
        }
    }

    ExpressionNode atributoTail(){
        if(currentToken.getType().equals(TokenType.assignOp)){
            match(TokenType.assignOp);
            return expresionCompuesta();
        } else{
            //epsilon
            return null;
        }

    }
    SentenceNode sentencia(){
        SentenceNode nuevaSentencia = new EmptySentenceNode();
        if(currentToken.getType().equals(TokenType.semicolon)){
            Token current = currentToken;
            match(TokenType.semicolon);
            return new EmptySentenceNode(current);
        }
        else if(primerosExpresion(currentToken)){
            nuevaSentencia = new SentenceWithExpressionNode(expresion());
            Token finalToken = currentToken;
            ((SentenceWithExpressionNode) nuevaSentencia).setFinalToken(finalToken);
            match(TokenType.semicolon);
            return nuevaSentencia;
        }
        else if(primerosFor(currentToken)){
            nuevaSentencia = forSentencia();
        }
        else if(primerosBloque(currentToken)){
            nuevaSentencia = bloque();
        }
        else if(primerosIf(currentToken)){
            nuevaSentencia = ifSentencia();
        }
        else if(primerosWhile(currentToken)){
            nuevaSentencia = whileSentencia();
        }
        else if(primerosReturn(currentToken)) {
            nuevaSentencia = returnSentencia();
            Token finalToken = currentToken;
            if(nuevaSentencia instanceof ReturnNode)
                ((ReturnNode) nuevaSentencia).setFinalToken(finalToken);
            match(TokenType.semicolon);
        }
        else if(primerosVarLocal(currentToken)){
            nuevaSentencia = varLocal();
            match(TokenType.semicolon);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con una sentencia valida" );
        }
        return nuevaSentencia;
    }
    SentenceNode forSentencia(){
        match(TokenType.sw_for);
        match(TokenType.openBracket);
        SentenceNode sentenceNode = contenidoFor();
        match(TokenType.closeBracket);
        SentenceNode block = sentencia();
        if(sentenceNode instanceof ForStandardNode)
            ((ForStandardNode) sentenceNode).setBody(block);
        return sentenceNode;
    }
    SentenceNode contenidoFor(){
        if(currentToken.getType().equals(TokenType.sw_var)){
            match(TokenType.sw_var);
            Token nombre = currentToken;
            match(TokenType.metVarID);
            return forTipo(nombre);
        }
        else if(primerosExpresion(currentToken)){
            ExpressionNode expressionNode = expresion();
            SentenceNode initialization = new SentenceWithExpressionNode(expressionNode);
            return forStandard(initialization);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un tipo o una expresion");
        }
    }
    SentenceNode forTipo(Token nombreVar){
        if(primerosForStandard(currentToken)){
            Token assignOp = currentToken;
            match(TokenType.assignOp);
            VarLocalNode varLocalNode = new VarLocalNode(nombreVar, expresionCompuesta(),assignOp);
            return forStandard(varLocalNode);
        } else if (primerosForIterador(currentToken)) {
            forIterador();
            return new EmptySentenceNode();
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con el simbolo [:] o con el simbolo [=]");
        }
    }
    ForStandardNode forStandard(SentenceNode initialization){
        match(TokenType.semicolon);
        ExpressionNode condition = expresion();
        match(TokenType.semicolon);
        ExpressionNode lastAssign = expresion();
        return new ForStandardNode(initialization,condition,lastAssign);
    }
    void forIterador(){
        match(TokenType.colon);
        expresion();
    }
    IfNode ifSentencia(){
        IfNode ifNode;
        match(TokenType.sw_if);
        match(TokenType.openBracket);
        ExpressionNode cond = expresion();
        match(TokenType.closeBracket);
        SentenceNode body = sentencia();
        ifNode = new IfNode(cond,body,ifSentencia_Recursivo());
        return ifNode;
    }
    SentenceNode ifSentencia_Recursivo(){
        if(currentToken.getType().equals(TokenType.sw_else)){
            match(TokenType.sw_else);
            return sentencia();
        }
        else{
            //epsilon
            return new EmptySentenceNode();
        }
    }
    SentenceNode whileSentencia(){
        match(TokenType.sw_while);
        match(TokenType.openBracket);
        ExpressionNode condition = expresion();
        match(TokenType.closeBracket);
        SentenceNode body = sentencia();
        return new WhileNode(condition,body);
    }
    SentenceNode returnSentencia(){
        match(TokenType.sw_return);
        return new ReturnNode(expresionOpcional());
    }
    VarLocalNode varLocal(){
        VarLocalNode nuevaVarLocal;
        match(TokenType.sw_var);
        Token nombre = currentToken;
        match(TokenType.metVarID);
        Token assignOp = currentToken;
        match(TokenType.assignOp);
        ExpressionNode expressionNode = expresionCompuesta();
        nuevaVarLocal = new VarLocalNode(nombre,expressionNode,assignOp);
        return nuevaVarLocal;
    }
    ExpressionNode expresion(){
        ExpressionNode expresionCompuesta = expresionCompuesta();
        return expresion_Recursiva(expresionCompuesta);
    }
    ExpressionNode expresion_Recursiva(ExpressionNode expressionNode){
        if(primerosOperadorAsignacion(currentToken)){
            Token operador = currentToken;
            operadorAsignacion();
            return new AssignNode(expressionNode,expresionCompuesta(),operador);
        } else{
            //epsilon
            return expressionNode;
        }
    }
    void operadorAsignacion(){
        if(currentToken.getType().equals(TokenType.assignOp)){
            match(TokenType.assignOp);
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba un operador de asignacion");
        }
    }
    ExpressionNode expresionCompuesta(){
        ExpressionNode expressionNode = expresionBasica();
        operacionTernariaOpcional();
        return  expresionCompuesta_Recursiva(expressionNode);
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

    ExpressionNode expresionCompuesta_Recursiva(ExpressionNode left){
        if(primerosOperadorBinario(currentToken)) {
            Token operator = currentToken;
            operadorBinario();
            ExpressionNode expressionNode = expresionBasica();
            operacionTernariaOpcional();
            BinaryExpNode binaryExpNode = new BinaryExpNode(left,operator,expressionNode);
            ExpressionNode toRet = expresionCompuesta_Recursiva(binaryExpNode);
            return toRet;
        }else{
            //epsilon
            return left;
        }
    }

    ExpressionNode expresionBasica(){
        if(primerosOperadorUnario(currentToken)) {
            return new UnaryExpNode(operadorUnario(),operando());
        } else if(primerosOperando(currentToken)){
            return new UnaryExpNode(operando());
        } else {
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un operador unario o un operando valido" );
        }
    }
    Token operadorUnario(){
        Token operator = currentToken;
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
        return operator;
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
    OperandNode operando(){
        if(primerosPrimitivo(currentToken)){
            return primitivo();
        } else if(primerosReferencia(currentToken)){
            return referencia();
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un operando valido" );
        }
    }

    OperandNode primitivo(){
        LiteralNode literalNode;
        if(currentToken.getType().equals(TokenType.intLiteral)){
            literalNode = new IntLiteralNode(currentToken);
            match(TokenType.intLiteral);
        } else if(currentToken.getType().equals(TokenType.charLiteral)){
            literalNode = new CharLiteralNode(currentToken);
            match(TokenType.charLiteral);
        } else if(currentToken.getType().equals(TokenType.sw_true)){
            literalNode = new BooleanLiteralNode(currentToken);
            match(TokenType.sw_true);
        } else if(currentToken.getType().equals(TokenType.sw_false)){
            literalNode = new BooleanLiteralNode(currentToken);
            match(TokenType.sw_false);
        } else if(currentToken.getType().equals(TokenType.sw_null)){
            literalNode = new NullLiteralNode(currentToken);
            match(TokenType.sw_null);
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un primitivo [literalInt-literalChar-true-false-null]" );
        }
        return literalNode;
    }
    OperandNode referencia(){
        OperandNode referenceNode = primario();
        referenceNode.setOptChaining(referencia_Recursiva());
        return referenceNode;
    }
    ChainingNode referencia_Recursiva(){
        if(currentToken.getType().equals(TokenType.dot)){
            match(TokenType.dot);
            Token nombre = currentToken;
            match(TokenType.metVarID);
            List<ExpressionNode> optArgs = argsActualesOpcional();
            if(optArgs == null){
                ChainedVarNode varCallNode = new ChainedVarNode(nombre,nombre.getLexeme());
                varCallNode.setOptChaining(referencia_Recursiva());
                return varCallNode;
            } else{
                ChainedCallNode methodCallNode = new ChainedCallNode(nombre,nombre.getLexeme());
                methodCallNode.setArgList(optArgs);
                methodCallNode.setOptChaining(referencia_Recursiva());
                return methodCallNode;
            }
        } else{
            //epsilon
            return null;
        }
    }
    List<ExpressionNode> argsActualesOpcional(){
        List<ExpressionNode> argList;
        if(primerosArgsActuales(currentToken)){
            argList = argsActuales();
        } else{
            //epsilon
            argList = null;
        }
        return argList;
    }
    List<ExpressionNode> argsActuales(){
        match(TokenType.openBracket);
        List<ExpressionNode> argList;
        argList = listaExpresionesOpcional();
        match(TokenType.closeBracket);
        return argList;
    }
    List<ExpressionNode> listaExpresionesOpcional(){
        List<ExpressionNode> argList = new java.util.ArrayList<>();
        if(primerosListaExpresiones(currentToken)){
            argList = listaExpresiones();
        } else{
            //epsilon
        }
        return argList;
    }
    List<ExpressionNode> listaExpresiones(){
        List<ExpressionNode> head = new java.util.ArrayList<>();
        head.add(expresion());
        List<ExpressionNode> tail = listaExpresiones_Recursiva();
        head.addAll(tail);
        return head;
    }
    List<ExpressionNode> listaExpresiones_Recursiva(){
        List<ExpressionNode> head = new java.util.ArrayList<>();
        if(currentToken.getType().equals(TokenType.comma)){
            match(TokenType.comma);
            head.add(expresion());
            List<ExpressionNode> tail = listaExpresiones_Recursiva();
            head.addAll(tail);
        } else{
            //epsilon
        }
        return head;
    }
    ExpressionNode expresionOpcional(){
        if(primerosExpresion(currentToken)){
            return expresion();
        } else{
            //epsilon
            return new NullExpressionNode();
        }
    }
    OperandNode primario(){
        OperandNode operandNode;
        if(currentToken.getType().equals(TokenType.stringLiteral)){
            operandNode = new StringLiteralNode(currentToken);
            match(TokenType.stringLiteral);
        } else if(currentToken.getType().equals(TokenType.sw_this)){
            operandNode = new ThisReferenceNode(currentToken,symbolTable.getCurrentClass().getName());
            match(TokenType.sw_this);
        } else if(currentToken.getType().equals(TokenType.metVarID)){
            Token nombre = currentToken;
            match(TokenType.metVarID);
            operandNode = llamadaTail(nombre);
        } else if(primerosLlamadaConstructor(currentToken)){
            operandNode = llamadaConstructor();
        } else if(primerosExpresionParentizada(currentToken)){
            operandNode = expresionParentizada();
        } else if(primerosLlamadaMetodoEstatico(currentToken)){
            operandNode = llamadaMetodoEstatico();
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con una llamada a constructor, expresion parentizada, llamada a metodo estatico o [literalString-this-idMetodoVariable]" );
        }
        return operandNode;
    }
    ReferenceNode llamadaTail(Token nombre){
        if(primerosArgsActuales(currentToken)){
            //llamada a metodo
            MethodCallNode llamadaMetodo = new MethodCallNode(nombre,nombre.getLexeme());
            llamadaMetodo.setArgList(argsActuales());
            return llamadaMetodo;
        } else{
            //epsilon
            return new VarCallNode(nombre,nombre.getLexeme());
        }
    }
    ConstructorCallNode llamadaConstructor(){
        match(TokenType.sw_new);
        Token nombre = currentToken;
        match(TokenType.classID);
        ConstructorCallNode constructorCall = new ConstructorCallNode(nombre,nombre.getLexeme());
        tipoParametricoDiamanteOpcional();
        constructorCall.setArgList(argsActuales());
        return constructorCall;
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
    ParentizedExpressionNode expresionParentizada(){
        match(TokenType.openBracket);
        ExpressionNode expressionNode = expresion();
        match(TokenType.closeBracket);
        return new ParentizedExpressionNode(expressionNode);
    }
    StaticMethodCallNode llamadaMetodoEstatico(){
        Token clase = currentToken;
        match(TokenType.classID);
        match(TokenType.dot);
        Token nombreMetodo = currentToken;
        match(TokenType.metVarID);
        StaticMethodCallNode staticMethodCallNode = new StaticMethodCallNode(clase,nombreMetodo);
        staticMethodCallNode.setArgList(argsActuales());
        return staticMethodCallNode;
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
