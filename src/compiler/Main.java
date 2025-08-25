package compiler;

import exceptions.LexicalException;
import lexical.LexicalAnalyzer;
import lexical.Token;
import lexical.TokenType;
import sourcemanager.SourceManager;
import sourcemanager.SourceManagerImpl;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String filePath;
        SourceManager sourceManager = new SourceManagerImpl();
        LexicalAnalyzer lexicalAnalyzer;

        if (args.length > 0) {
            try{
                filePath = args[0];
                sourceManager.open(filePath);
                lexicalAnalyzer = new LexicalAnalyzer(sourceManager);
                Token token;
                do {
                    token = lexicalAnalyzer.getNextToken();
                    System.out.println(token.toString());
                } while (token.getType() != TokenType.eof);

                System.out.println("[SinErrores]");
            } catch (LexicalException | IOException e) {
                System.out.println(e.getMessage());
            }

        }





    }
}