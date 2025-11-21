package compiler;

import exceptions.LexicalException;
import exceptions.SemanticException;
import exceptions.SyntacticException;
import lexical.LexicalAnalyzerMultiDetect;
import semantic.SymbolTable;
import sourcemanager.SourceManager;
import sourcemanager.SourceManagerCharImpl;
import syntactic.SyntacticAnalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Main {
    public static SymbolTable symbolTable = null;
    public static void main(String[] args) {
        String filePath;
        String outputPath;
        SourceManager sourceManager = new SourceManagerCharImpl();
        LexicalAnalyzerMultiDetect lexicalAnalyzer = null;
        SyntacticAnalyzer syntacticAnalyzer;

        //if (args.length == 1) {
        //CAMBIO PARA LOGROS DE ETAPA 4. DESCOMENTAR LINEA DE ARRIBA Y COMENTAR LINEA DE ABAJO
        if (args.length == 2) {
            try{

                //vaciar tabla de simbolos
                symbolTable = new SymbolTable();
                symbolTable.createPredefinedClasses();

                filePath = args[0];
                //CAMBIO PARA LOGROS DE ETAPA 4. COMENTAR LINEA DE ABAJO
                outputPath = args[1];

                sourceManager.open(filePath);
                lexicalAnalyzer = new LexicalAnalyzerMultiDetect(sourceManager);
                syntacticAnalyzer = new SyntacticAnalyzer(lexicalAnalyzer);
                syntacticAnalyzer.inicial();

                symbolTable.chequeoDeclaraciones();
                symbolTable.chequeoSentencias();

                //CAMBIO PARA LOGROS DE ETAPA 4. COMENTAR LINEA DE ABAJO
                generate(outputPath);

                if (!lexicalAnalyzer.getErrors().isEmpty()) {
                    for(LexicalException error : lexicalAnalyzer.getErrors()){
                        System.out.println(error.getDetailedErrorMessage());
                    }
                } else{
                    System.out.println("Compilacion exitosa.");
                    System.out.println("[SinErrores]");
                }

            } catch (SemanticException | SyntacticException e) {
                if(lexicalAnalyzer!=null && !lexicalAnalyzer.getErrors().isEmpty()){
                    for(LexicalException error : lexicalAnalyzer.getErrors()){
                        System.out.println(error.getDetailedErrorMessage());
                    }
                }
                System.out.println( e.getMessage() );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void generate(String outputPath){
        //Generacion de codigo intermedio o maquina virtual
        File outputFile;
        FileWriter writer;
        BufferedWriter bufferedWriter;
        symbolTable.generateCode();
        try{
            if(outputPath == null)
                outputFile = new File("output.txt");
            else
                outputFile = new File(outputPath);

            writer = new FileWriter(outputFile);
            BufferedWriter buffer = new BufferedWriter(writer);

            for(String instruction : symbolTable.instructionList){
                writer.write(instruction);
                writer.write("\n");
            }
            writer.close();
            buffer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}