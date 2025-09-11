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

    void inicial(){
        listaClases();
        match(TokenType.eof);
    }

    void listaClases(){
        if (currentToken.getType().equals(TokenType.sw_class)){
            clase();
            listaClases();
        }
        else if (currentToken.getType().equals(TokenType.eof)) {
            //epsilon
        }
        else{
            throw new SyntacticException(currentToken, TokenType.sw_class, TokenType.sw_interface, TokenType.eof);
        }
    }
    void clase(){
        modificadorOpcional();
        match(TokenType.sw_class);
        match(TokenType.classID);
        herenciaOpcional();
        match(TokenType.openCurly);
        listaMiembros();
        match(TokenType.closeCurly);
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
        }
        else{
            //epsilon
        }
    }
    void listaMiembros(){
        if(primerosMiembro(currentToken)){
            miembro();
            listaMiembros();
        }
        else {
            //Epsilon
        }
    }
    void miembro(){
        if(primerosConstructor(currentToken)){
            constructor();
        } else if (primerosModificador(currentToken)){
            modificador();
            metodoConMod();
        } else if (primerosMiembroMetVar(currentToken)){
            miembroMetVar();
        }
        else{
            throw new SyntacticException(currentToken,);
        }
    }
    void constructor(){
        match(TokenType.sw_public);
        match(TokenType.classID);
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
            throw new SyntacticException(currentToken, TokenType.sw_int, TokenType.sw_char, TokenType.sw_boolean);
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
            throw new SyntacticException(currentToken, TokenType.sw_static, TokenType.sw_final, TokenType.sw_abstract);
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
        }else{
            //epsilon
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
            throw new SyntacticException(currentToken, TokenType.sw_void, TokenType.classID, TokenType.sw_int, TokenType.sw_char, TokenType.sw_boolean);
        }
    }
    void miembroTail(){
        if(primerosMetodoTail(currentToken)){
            metodoTail();
        }
        else if(currentToken.getType().equals(TokenType.semicolon)){
            match(TokenType.semicolon);
        }
        else{
            throw new SyntacticException(currentToken, TokenType.openBracket, TokenType.semicolon);
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
            throw new SyntacticException(currentToken, TokenType.semicolon /* agregar los primeros de las otras producciones */);
        }
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
        } else if(currentToken.getType().equals(TokenType.subOp)){
            match(TokenType.subOp);
            if(currentToken.getType().equals(TokenType.assignOp)){
                match(TokenType.assignOp);
            }
        } else if(currentToken.getType().equals(TokenType.addOp)){
            match(TokenType.addOp);
            if(currentToken.getType().equals(TokenType.assignOp))
                match(TokenType.assignOp);
        } else{
            throw new SyntacticException(currentToken,);
        }
    }
    void expresionCompuesta(){
        expresionBasica();
        expresionCompuesta_Recursiva();
    }
    void expresionCompuesta_Recursiva(){
        if(primerosOperadorBinario(currentToken)) {
            operadorBinario();
            expresionBasica();
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
        }
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
            throw new SyntacticException(currentToken,tokenName);
        }
    }
    boolean primerosMiembro(Token token){
        return primerosConstructor(token) | primerosModificador(token) | primerosMiembroMetVar(token);
    }
    boolean primerosConstructor(Token token){
        return token.getType() == TokenType.sw_public;
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
        return primeros.contains(token.getType()) || primerosExpresion(token) || primerosBloque(token) || primerosIf(token) || primerosWhile(token) || primerosReturn(token) || primerosVarLocal(token);
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
        HashSet<TokenType> primeros = new HashSet<>(List.of(

        ));
        return primerosExpresionCompuesta(token);
    }
    boolean primerosExpresionCompuesta(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(

        ));
        return primerosExpresionBasica(token);
    }
    boolean primerosExpresionBasica(Token token){
        HashSet<TokenType> primeros = new HashSet<>(List.of(

        ));
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
