package lexical;

import exceptions.LexicalException;
import sourcemanager.SourceManager;
import sourcemanager.SourceManagerImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static lexical.TokenType.*;

import static sourcemanager.SourceManager.END_OF_FILE;

public class LexicalAnalyzer {

    private final SourceManager sourceManager;
    private char currentChar;
    private String lexeme;
    private HashMap<String, TokenType> reservedWords;

    public LexicalAnalyzer(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
        buildWordMap();
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

    private void addChar() {
        lexeme += currentChar;
    }

    private void getChar() throws IOException {
        currentChar = sourceManager.getNextChar();
    }

    private Token e0() throws LexicalException, IOException {
        if (currentChar == ' ' || currentChar == '\t' || currentChar == '\n') {
            addChar();
            getChar();
            return getNextToken();
        } else if (currentChar == END_OF_FILE) {
            return e1EOF();
        } else if (currentChar == '/') {
            addChar();
            getChar();
            return e2Comment();
        } else if (currentChar == '(') {
            addChar();
            getChar();
            return e6();
        } else if (currentChar == ')') {
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
        } else if (currentChar == '>') {
            addChar();
            getChar();
            return e11();
        } else if (currentChar == '=') {
            addChar();
            getChar();
            return e13();
        } else if (currentChar == '!') {
            addChar();
            getChar();
            return e15();
        } else if (currentChar == '&') {
            addChar();
            getChar();
            return e17();
        } else if (currentChar == '|') {
            addChar();
            getChar();
            return e19();
        } else if (currentChar == '+') {
            addChar();
            getChar();
            return e21();
        } else if (currentChar == '-') {
            addChar();
            getChar();
            return e23();
        } else if (currentChar == '*') {
            addChar();
            getChar();
            return e25();
        } else if (currentChar == '%') {
            addChar();
            getChar();
            return e26();
        } else if (currentChar == ';') {
            addChar();
            getChar();
            return e27();
        } else if (currentChar == ',') {
            addChar();
            getChar();
            return e28();

        } else if (currentChar == '.') {
            addChar();
            getChar();
            return e29();
        } else if (currentChar == ':') {
            addChar();
            getChar();
            return e30();
        } else if (currentChar == '{') {
            addChar();
            getChar();
            return e31();
        } else if (currentChar == '}') {
            addChar();
            getChar();
            return e32();
        } else if (Character.isUpperCase(currentChar)) {
            addChar();
            getChar();
            return e33();
        } else if (Character.isLowerCase(currentChar)) {
            addChar();
            getChar();
            return e34();
        } else if (currentChar == '"') {
            addChar();
            getChar();
            return e35();
        } else if (currentChar == '\'') {
            addChar();
            getChar();
            return e37();
        } else {
            addChar();
            getChar();
            throw new LexicalException(lexeme, sourceManager.getLineNumber(), sourceManager.getColumnNumber(), sourceManager.getCurrentLineText(), "símbolo válido");
        }
    }

    private Token e1EOF() {
        addChar();
        return new Token(eof, lexeme, sourceManager.getLineNumber());
    }

    private Token e2Comment() throws LexicalException, IOException {
        if (currentChar == '/') {
            addChar();
            getChar();
            return e3SimpleComment();
        } else if (currentChar == '*') {
            addChar();
            getChar();
            return e4MultiComment();
        } else {
            return new Token(divOp, lexeme, sourceManager.getLineNumber());
        }
    }

    private Token e3SimpleComment() throws IOException {
        if (currentChar == '\n' || currentChar == END_OF_FILE) {
            addChar();
            getChar();
            return getNextToken();
        } else {
            addChar();
            getChar();
            return e3SimpleComment();
        }
    }

    private Token e4MultiComment() throws LexicalException, IOException {
        if (currentChar == END_OF_FILE) {
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "comentario multilínea no cerrado"
            );


        } else if (currentChar == '*') {
            addChar();
            getChar();
            return e5MultiComment();
        } else {
            addChar();
            getChar();
            return e4MultiComment();
        }
    }

    private Token e5MultiComment() throws LexicalException, IOException {
        if (currentChar == END_OF_FILE) {
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "comentario multilínea no cerrado"
            );


        } else if (currentChar == '/') {
            addChar();
            getChar();
            return getNextToken();
        } else {
            addChar();
            getChar();
            return e4MultiComment();
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
        if (Character.isDigit(currentChar)) {
            addChar();
            getChar();
            return e8();
        } else if (lexeme.length() <= 9) {
            return new Token(intLiteral, lexeme, sourceManager.getLineNumber());
        } else {
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "entero de longitud invalida"
            );
        }
    }

    private Token e9() throws LexicalException, IOException {
        if (currentChar == '=') {
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
        if (currentChar == '=') {
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
        if (currentChar == '=') {
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
        if (currentChar == '=') {
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

    private Token e17() throws LexicalException, IOException {
        if (currentChar == '&') {
            addChar();
            getChar();
            return e18();
        } else {
            if (currentChar != sourceManager.END_OF_FILE && currentChar != (char)26) {
                addChar();
            }
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "Operador AND incompleto"
            );


        }
    }

    private Token e18() {
        return new Token(andOp, lexeme, sourceManager.getLineNumber());
    }

    private Token e19() throws LexicalException, IOException {
        if (currentChar == '|') {
            addChar();
            getChar();
            return e20();
        } else {
            if (currentChar != sourceManager.END_OF_FILE && currentChar != (char)26) {
                addChar();
            }
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "Operador OR incompleto"
            );


        }
    }

    private Token e20() {
        return new Token(orOp, lexeme, sourceManager.getLineNumber());
    }

    private Token e21() throws LexicalException, IOException {
        if (currentChar == '+') {
            addChar();
            getChar();
            return e22();
        } else {
            return new Token(addOp, lexeme, sourceManager.getLineNumber());
        }
    }

    private Token e22() {
        return new Token(postIncrement, lexeme, sourceManager.getLineNumber());
    }

    private Token e23() throws LexicalException, IOException {
        if (currentChar == '-') {
            addChar();
            getChar();
            return e24();
        } else {
            return new Token(subOp, lexeme, sourceManager.getLineNumber());
        }
    }

    private Token e24() {
        return new Token(postDecrement, lexeme, sourceManager.getLineNumber());
    }

    private Token e25() {
        return new Token(mulOp, lexeme, sourceManager.getLineNumber());
    }

    private Token e26() {
        return new Token(modOp, lexeme, sourceManager.getLineNumber());
    }

    private Token e27() {
        return new Token(semicolon, lexeme, sourceManager.getLineNumber());
    }

    private Token e28() {
        return new Token(comma, lexeme, sourceManager.getLineNumber());
    }

    private Token e29() {
        return new Token(dot, lexeme, sourceManager.getLineNumber());
    }

    private Token e30() {
        return new Token(colon, lexeme, sourceManager.getLineNumber());
    }

    private Token e31() throws IOException {
        addChar();
        getChar();
        return new Token(openCurly, lexeme, sourceManager.getLineNumber());
    }

    private Token e32() throws IOException {
        addChar();
        getChar();
        return new Token(closeCurly, lexeme, sourceManager.getLineNumber());
    }

    private Token e33() throws LexicalException, IOException {
        if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            addChar();
            getChar();
            return e33();
        } else {
            return new Token(classID, lexeme, sourceManager.getLineNumber());
        }
    }

    private Token e34() throws LexicalException, IOException {
        if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            addChar();
            getChar();
            return e34();
        } else {
            TokenType type = reservedWords.get(lexeme);
            return new Token(Objects.requireNonNullElse(type, metVarID), lexeme, sourceManager.getLineNumber());
        }

    }

    private Token e35() throws LexicalException, IOException {
        if (currentChar == END_OF_FILE || currentChar == '\n') {
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "string mal cerrado"
            );


        } else if (currentChar == '"') {
            addChar();
            getChar();
            return new Token(stringLiteral, lexeme, sourceManager.getLineNumber());
        } else if (currentChar == '\\') {
            addChar();
            getChar();
            return e36();
            //TODO PREGUNTAR COMO REPRESENTAR EL \"
        } else {
            addChar();
            getChar();
            return e35();
        }
    }

    private Token e36() throws LexicalException, IOException {
        if (currentChar == '"') {
            addChar();
            getChar();
            return e35();
        } else if (currentChar == 'u') {
            return e39Unicode();
        } else if (currentChar == END_OF_FILE || currentChar == '\n' || Character.isWhitespace(currentChar)) {
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "" //TODO COMPLETAR
            );
        }
        addChar();
        getChar();
        return e35();
    }

    private Token e37() throws LexicalException, IOException {
        //TODO REVISAR
        if (currentChar == END_OF_FILE || currentChar == '\n') {
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "literal de carácter mal cerrado"
            );


        }
        addChar();
        getChar();

        //UNICODE CASE
        if (lexeme.length() == 2 && lexeme.charAt(1) == '\\' && currentChar == 'u') {
            return e38Unicode();
        }

        if (lexeme.length() == 2 && lexeme.charAt(1) == '\\') {
            addChar();
            getChar();
            if (lexeme.length() == 3 && (lexeme.charAt(2) == '\\' || lexeme.charAt(2) == '\'')) {
                if (currentChar != '\'') {
                    throw new LexicalException(
                            lexeme,
                            sourceManager.getLineNumber(),
                            sourceManager.getColumnNumber(),
                            sourceManager.getCurrentLineText(),
                            "literal de carácter invalido"
                    );


                }
                addChar();
                getChar();
                return new Token(charLiteral, lexeme, sourceManager.getLineNumber());
            } else {
                throw new LexicalException(
                        lexeme,
                        sourceManager.getLineNumber(),
                        sourceManager.getColumnNumber(),
                        sourceManager.getCurrentLineText(),
                        "literal de carácter invalido"
                );


            }
        } else if (lexeme.length() == 2 && currentChar == '\'') {
            char c = lexeme.charAt(1);
            if (c == '\\' || c == '\'') {
                throw new LexicalException(
                        lexeme,
                        sourceManager.getLineNumber(),
                        sourceManager.getColumnNumber(),
                        sourceManager.getCurrentLineText(),
                        "literal de carácter invalido"
                );


            }
            addChar();
            getChar();
            return new Token(charLiteral, lexeme, sourceManager.getLineNumber());
        } else {
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "literal de carácter invalido"
            );

        }
    }

    private Token e38Unicode() throws LexicalException, IOException {
        addChar(); // add 'u'

        StringBuilder unicodeSeq = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            getChar();
            if (isHexDigit(currentChar)) {
                unicodeSeq.append(currentChar);
                addChar();
            } else {
                throw new LexicalException(
                        lexeme,
                        sourceManager.getLineNumber(),
                        sourceManager.getColumnNumber(),
                        sourceManager.getCurrentLineText(),
                        "caracter unicode con caracter no hexadecimal"
                );


            }
        }
        getChar();
        if (currentChar != '\'') {
            throw new LexicalException(
                    lexeme,
                    sourceManager.getLineNumber(),
                    sourceManager.getColumnNumber(),
                    sourceManager.getCurrentLineText(),
                    "caracter unicode de longitud invalida"
            );


        }
        addChar();
        getChar();
        return new Token(charLiteral, lexeme, sourceManager.getLineNumber());
    }

    private Token e39Unicode() throws LexicalException, IOException {
        addChar(); // add 'u'

        StringBuilder unicodeSeq = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            getChar();
            if (isHexDigit(currentChar)) {
                unicodeSeq.append(currentChar);
                addChar();
            } else {
                throw new LexicalException(
                        lexeme,
                        sourceManager.getLineNumber(),
                        sourceManager.getColumnNumber(),
                        sourceManager.getCurrentLineText(),
                        "string unicode con caracter no hexadecimal"
                );
            }
        }
        getChar();

        return e35();
    }


    private boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9') ||
                (c >= 'a' && c <= 'f') ||
                (c >= 'A' && c <= 'F');
    }


    private void buildWordMap() {
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
        reservedWords.put("abstract", sw_abstract);
        reservedWords.put("final", sw_final);
    }
}