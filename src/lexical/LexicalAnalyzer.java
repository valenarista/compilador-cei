package lexical;

import exceptions.LexicalException;
import sourcemanager.SourceManager;
import sourcemanager.SourceManagerImpl;
import java.io.IOException;
import java.util.HashMap;

import static lexical.TokenType.*;

import static sourcemanager.SourceManager.END_OF_FILE;

public class LexicalAnalyzer{

        private SourceManager sourceManager;
        private char currentChar;
        private String lexeme;
        private HashMap<String, TokenType> reservedWords;

        public LexicalAnalyzer (SourceManager sourceManager)  {
            this.sourceManager = sourceManager;
            try {
                this.currentChar = sourceManager.getNextChar();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Token getNextToken() throws LexicalException, IOException {
            lexeme = "";
            return e0();
        }

        private void addChar(){
            lexeme += currentChar;
        }

        private void getChar() throws IOException {
            currentChar = sourceManager.getNextChar();
        }

        private Token e0() throws LexicalException, IOException {
            if(currentChar==' ' || currentChar=='\t' || currentChar == '\n'){
                addChar();
                getChar();
                return getNextToken();
            }
            else if(currentChar == END_OF_FILE) {
                return e1();
            } else if (currentChar == '/') {
                addChar();
                getChar();
                return e2();
            } else if(currentChar == '('){
                addChar();
                getChar();
                return e6();
            }else if(currentChar == ')'){
                addChar();
                getChar();
                return e7();
            } else if (Character.isDigit(currentChar)) {
                addChar();
                getChar();
                return e8();
            } else if (currentChar == '<') {
                addChar();
                getChar();
                return e9();
            } else if (currentChar == '>'){
                addChar();
                getChar();
                return e11();
            } else if (currentChar == '='){
                addChar();
                getChar();
                return e13();
            } else if (currentChar == '!'){
                addChar();
                getChar();
                return e15();
            }



            else if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '%' || currentChar == ';' || currentChar == ',' || currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}') {
                addChar();
                getChar();
                return e();
            } else {
                throw new LexicalException(lexeme, sourceManager.getLineNumber());
            }
        }

        private Token e1(){
            addChar();
            return new Token(eof, lexeme, sourceManager.getLineNumber());
        }
        private Token e2() throws LexicalException, IOException {
            if(currentChar == '/'){
                addChar();
                getChar();
                return e3();
            } else if (currentChar == '*') {
                addChar();
                getChar();
                return e4();
            } else {
                return new Token(divOp, lexeme, sourceManager.getLineNumber());
            }
        }
        private Token e3() throws IOException {
            if(currentChar == '\n' || currentChar == END_OF_FILE){
                addChar();
                getChar();
                return getNextToken();
            } else {
                addChar();
                getChar();
                return e3();
            }
        }
        private Token e4() throws LexicalException, IOException {
            if(currentChar == END_OF_FILE){
                throw new LexicalException(lexeme, sourceManager.getLineNumber());
            } else if (currentChar == '*') {
                addChar();
                getChar();
                return e5();
            } else {
                addChar();
                getChar();
                return e4();
            }
        }
        private Token e5() throws LexicalException, IOException {
            if(currentChar == END_OF_FILE){
                throw new LexicalException(lexeme, sourceManager.getLineNumber());
            } else if (currentChar == '/') {
                addChar();
                getChar();
                return getNextToken();
            } else {
                addChar();
                getChar();
                return e4();
            }
        }

        private Token e6() throws IOException {
            addChar();
            getChar();
            return new Token(openBracket, lexeme, sourceManager.getLineNumber());
        }

        private Token e7() throws IOException {
            addChar();
            getChar();
            return new Token(closeBracket, lexeme, sourceManager.getLineNumber());
        }
        private Token e8() throws LexicalException, IOException {
            if(Character.isDigit(currentChar)){
                addChar();
                getChar();
                return e8();
            } else if(lexeme.length()<=9){
                return new Token(intType, lexeme, sourceManager.getLineNumber());
            } else {
                throw new LexicalException(lexeme, sourceManager.getLineNumber());
            }
        }
        private Token e9() throws LexicalException, IOException {
            if(currentChar == '='){
                addChar();
                getChar();
                return e10();
            } else {
                return new Token(lessOp, lexeme, sourceManager.getLineNumber());
            }
        }
        private Token e10() {
            return new Token(lessEqualOp, lexeme, sourceManager.getLineNumber());
        }
        private Token e11() throws LexicalException, IOException {
            if(currentChar == '='){
                addChar();
                getChar();
                return e12();
            } else {
                return new Token(greaterOp, lexeme, sourceManager.getLineNumber());
            }
        }
        private Token e12() {
            return new Token(greaterEqualOp, lexeme, sourceManager.getLineNumber());
        }
        private Token e13() throws LexicalException, IOException {
            if(currentChar == '='){
                addChar();
                getChar();
                return e14();
            } else {
                return new Token(assignOp, lexeme, sourceManager.getLineNumber());
            }
        }
        private Token e14() {
            return new Token(equalOp, lexeme, sourceManager.getLineNumber());
        }
        private Token e15() throws LexicalException, IOException {
            if(currentChar == '='){
                addChar();
                getChar();
                return e16();
            } else {
                return new Token(notOp, lexeme, sourceManager.getLineNumber());
            }
        }
        private Token e16() {
            return new Token(notEqualOp, lexeme, sourceManager.getLineNumber());
        }



        private void buildWordMap(){
            reservedWords = new HashMap<>();
            reservedWords.put("class", sw_class);
            reservedWords.put("public", sw_public);
            reservedWords.put("static", sw_static);
            reservedWords.put("void", sw_void);
            reservedWords.put("extends", sw_extends);
            reservedWords.put("implements", sw_implements);
            reservedWords.put("return", sw_return);
            reservedWords.put("int", sw_int);
            reservedWords.put("boolean", sw_boolean);
            reservedWords.put("if", sw_if);
            reservedWords.put("else", sw_else);
            reservedWords.put("while", sw_while);
            reservedWords.put("var", sw_var);
            reservedWords.put("char", sw_char);
            reservedWords.put("null", sw_null);
            reservedWords.put("this", sw_this);
            reservedWords.put("new", sw_new);
            reservedWords.put("true", sw_true);
            reservedWords.put("false", sw_false);
            reservedWords.put("interface", sw_interface);
            reservedWords.put("private", sw_private);
            reservedWords.put("String", stringType);
        }
}