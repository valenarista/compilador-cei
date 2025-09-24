package syntactic;

import exceptions.SyntacticException;
import lexical.LexicalAnalyzerMultiDetect;
import lexical.Token;
import lexical.TokenType;

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
            modificadorOpcional();
            conjuntoClaseOInterfaz();
        }
        else {
            //Epsilon
        }
    }
    void conjuntoClaseOInterfaz(){
        if (primerosClase(currentToken)) {
            clase();
            listaClases();
        }
        else if(primerosInterface(currentToken)){
            interfaz();
            listaClases();
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token: "+TokenType.sw_class+" o un token: "+TokenType.sw_interface);
        }
    }
    void interfaz(){
        match(TokenType.sw_interface);
        match(TokenType.classID);
        tipoParametricoOpcional();
        herenciaOpcional();
        match(TokenType.openCurly);
        listaMiembrosInterfaz();
        match(TokenType.closeCurly);
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
        visibilidadOpcional();
        cuerpoMiembroInterfaz();
    }
    void cuerpoMiembroInterfaz(){
        if(primerosModificador(currentToken)){
            modificador();
            metodoConMod();
        }
        else if(primerosMiembroMetVar(currentToken)){
            miembroMetVar();
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token: [public-abstract-final-static void] o un tipo valido");
        }
    }
    void clase(){
        match(TokenType.sw_class);
        match(TokenType.classID);
        tipoParametricoOpcional();
        herenciaImplementacionOpcional();
        match(TokenType.openCurly);
        listaMiembros();
        match(TokenType.closeCurly);
    }
    void herenciaImplementacionOpcional(){
        if(currentToken.getType().equals(TokenType.sw_extends))
            herenciaOpcional();
        else if(currentToken.getType().equals(TokenType.sw_implements)){
            match(TokenType.sw_implements);
            match(TokenType.classID);
            tipoParametricoOpcional();
        }
        else{
            //epsilon
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
    void modificadorOpcional(){
        if (currentToken.getType().equals(TokenType.sw_final)){
            match(TokenType.sw_final);
        }
        else if (currentToken.getType().equals(TokenType.sw_abstract)){
            match(TokenType.sw_abstract);
        }
        else if (currentToken.getType().equals(TokenType.sw_static)){
            match(TokenType.sw_static);
        }
        else{
            //epsilon
        }
    }
    void herenciaOpcional(){
        if (currentToken.getType().equals(TokenType.sw_extends)){
            match(TokenType.sw_extends);
            match(TokenType.classID);
            tipoParametricoOpcional();
        }
        else{
            //epsilon
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
        visibilidadOpcional();
        miembro();
    }
    void visibilidadOpcional(){
        if(currentToken.getType().equals(TokenType.sw_public)){
            match(TokenType.sw_public);
        }
        else if(currentToken.getType().equals(TokenType.sw_private)){
            match(TokenType.sw_private);
        }
        else{
            //epsilon
        }
    }
    void miembro(){
        if(currentToken.getType().equals(TokenType.classID)){
          match(TokenType.classID);
          constructorOMetVar();
        } else if (primerosModificador(currentToken)){
            modificador();
            metodoConMod();
        } else if (primerosMiembroMetVar(currentToken)){
            miembroMetVar();
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token: [public-abstract-final-static void] o un tipo valido");
        }
    }
    void constructorOMetVar(){
        if(primerosConstructor(currentToken)){
            constructor();
        }
        else if(currentToken.getType().equals(TokenType.metVarID)||currentToken.getType().equals(TokenType.lessOp)){
            tipoParametricoOpcional();
            match(TokenType.metVarID);
            miembroTail();
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un token: "+TokenType.openBracket+" o un token: "+TokenType.semicolon);
        }
    }
    void constructor(){
        argsFormales();
        bloque();
    }

    void argsFormales(){
        match(TokenType.openBracket);
        listaArgsFormalesOpcional();
        match(TokenType.closeBracket);

    }
    void listaArgsFormalesOpcional(){
        if(primerosListaArgsFormales(currentToken)){
            listaArgsFormales();
        }
        else{
            //epsilon
        }
    }
    void listaArgsFormales(){
        argFormal();
        listaArgFormales_Recursivo();
    }
    void listaArgFormales_Recursivo(){
        if(currentToken.getType().equals(TokenType.comma)){
            match(TokenType.comma);
            argFormal();
            listaArgFormales_Recursivo();
        }
        else{
            //epsilon
        }
    }
    void argFormal(){
        tipo();
        match(TokenType.metVarID);
    }
    void tipo(){
        if(primerosTipoPrimitivo(currentToken)){
            tipoPrimitivo();
        }
        else {
            match(TokenType.classID);
            tipoParametricoOpcional();
        }
    }
    void tipoPrimitivo(){
        if(currentToken.getType().equals(TokenType.sw_int)){
            match(TokenType.sw_int);
        }
        else if(currentToken.getType().equals(TokenType.sw_char)){
            match(TokenType.sw_char);
        }
        else if(currentToken.getType().equals(TokenType.sw_boolean)){
            match(TokenType.sw_boolean);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear un tipo primitivo" );
        }
    }

    void bloque(){
        match(TokenType.openCurly);
        listaSentencias();
        match(TokenType.closeCurly);
    }
    void listaSentencias(){
        if(primerosSentencia(currentToken)){
            sentencia();
            listaSentencias();
        }
        else{
            //epsilon
        }
    }
    void modificador(){
        if(currentToken.getType().equals(TokenType.sw_static)){
            match(TokenType.sw_static);
        }
        else if(currentToken.getType().equals(TokenType.sw_final)){
            match(TokenType.sw_final);
        }
        else if(currentToken.getType().equals(TokenType.sw_abstract)){
            match(TokenType.sw_abstract);
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un modificador [static-abstract-final]" );
        }
    }
    void metodoConMod(){
        tipoMetodo();
        match(TokenType.metVarID);
        metodoTail();
    }
    void tipoMetodo(){
        if(currentToken.getType().equals(TokenType.sw_void)){
            match(TokenType.sw_void);
           }
        else{
            tipo();
        }
    }
    void metodoTail(){
        argsFormales();
        bloqueOpcional();
    }
    void bloqueOpcional(){
        if(primerosBloque(currentToken)){
            bloque();
        }else if(currentToken.getType().equals(TokenType.semicolon)){
            match(TokenType.semicolon);
        } else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un primero de bloque o un token: "+TokenType.semicolon);

        }
    }
    void miembroMetVar(){
        if(primerosTipo(currentToken)){
            tipo();
            match(TokenType.metVarID);
            miembroTail();
        }
        else if(currentToken.getType().equals(TokenType.sw_void)){
            match(TokenType.sw_void);
            match(TokenType.metVarID);
            metodoTail();
        }
        else{
            throw new SyntacticException(currentToken.getLexeme(),currentToken.getLineNumber(),currentToken.getType(),"Se esperaba matchear con un  tipo o un token: "+TokenType.sw_void);
        }
    }
    void miembroTail(){
        if(primerosMetodoTail(currentToken)){
            metodoTail();
        }
        else if(currentToken.getType().equals(TokenType.semicolon) || primerosAtributoTail(currentToken)){
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
    void sentencia(){
        if(currentToken.getType().equals(TokenType.semicolon)){
            match(TokenType.semicolon);
        }
        else if(primerosExpresion(currentToken)){
            expresion();
            match(TokenType.semicolon);
        }
        else if(primerosFor(currentToken)){
            forSentencia();
        }
        else if(primerosBloque(currentToken)){
            bloque();
        }
        else if(primerosIf(currentToken)){
            ifSentencia();
        }
        else if(primerosWhile(currentToken)){
            whileSentencia();
        }
        else if(primerosReturn(currentToken)) {
            returnSentencia();
            match(TokenType.semicolon);
        }
        else if(primerosVarLocal(currentToken)){
            varLocal();
            match(TokenType.semicolon);
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
    boolean primerosMiembroMetVar(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(
                TokenType.sw_void
        ));
        return primeros.contains(token.getType())||primerosTipo(token);
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
