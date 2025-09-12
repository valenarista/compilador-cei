package compiler;

import exceptions.LexicalException;
import exceptions.SyntacticException;
import lexical.LexicalAnalyzerMultiDetect;
import lexical.Token;
import lexical.TokenType;
import sourcemanager.SourceManager;
import sourcemanager.SourceManagerCharImpl;
import syntactic.SyntacticAnalyzer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        String filePath;
        SourceManager sourceManager = new SourceManagerCharImpl();
        LexicalAnalyzerMultiDetect lexicalAnalyzer;
        SyntacticAnalyzer syntacticAnalyzer;

        if (args.length > 0) {
            try{
                filePath = args[0];
                sourceManager.open(filePath);
                lexicalAnalyzer = new LexicalAnalyzerMultiDetect(sourceManager);
                syntacticAnalyzer = new SyntacticAnalyzer(lexicalAnalyzer);
                /*
                Token token;
                do {
                    token = lexicalAnalyzer.getNextToken();
                    System.out.println(token.toString());
                } while (token.getType() != TokenType.eof);
                for(LexicalException errors : lexicalAnalyzer.getErrors()){
                    System.out.println(errors.getDetailedErrorMessage());
                }
                if(lexicalAnalyzer.getErrors().isEmpty())
                    System.out.println("[SinErrores]");

                 */
                syntacticAnalyzer.inicial();
                System.out.println("[SinErrores]");
            } catch (SyntacticException e) {
                System.out.println( e.getMessage() );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}