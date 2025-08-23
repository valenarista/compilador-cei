package lexical;

import exceptions.LexicalException;
import sourcemanager.SourceManager;
import sourcemanager.SourceManagerImpl;


import java.io.IOException;
import java.util.ArrayList;

import static sourcemanager.SourceManager.END_OF_FILE;

public class LexicalAnalyzer{

        private SourceManager sourceManager;
        private char currentChar;
        private String lexeme;

        public LexicalAnalyzer (SourceManager sourceManager)  {
            this.sourceManager = sourceManager;
            try {
                this.currentChar = sourceManager.getNextChar();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Token getNextToken() throws LexicalException {
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
            } else if (Character.isLetter(currentChar)) {
                addChar();
                getChar();
                return e4();
            } else if (Character.isDigit(currentChar)) {
                addChar();
                getChar();
                return e7();
            } else if (currentChar == '<' || currentChar == '>' || currentChar == '=' || currentChar == '!') {
                addChar();
                getChar();
                return e10();
            } else if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '%' || currentChar == ';' || currentChar == ',' || currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}') {
                addChar();
                getChar();
                return e12();
            } else {
                throw new LexicalException(lexeme, sourceManager.getLineNumber());
            }
        }
}