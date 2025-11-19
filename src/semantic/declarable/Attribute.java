package semantic.declarable;

import exceptions.SemanticException;
import lexical.Token;
import semantic.ast.expression.UnaryExpNode;
import semantic.entity.ConcreteClass;
import semantic.ast.expression.ExpressionNode;
import semantic.ast.reference.ConstructorCallNode;
import semantic.ast.reference.VarCallNode;
import semantic.entity.EntityClass;
import semantic.types.ReferenceType;
import semantic.types.Type;
import java.util.Objects;

import static compiler.Main.symbolTable;

public class Attribute {
    private Token idToken;
    private Type type;
    private Token visibility;
    private ExpressionNode value;
    private int offset;

    public Attribute(Token idToken, Type type,Token visibility){
        this.idToken = idToken;
        this.type = type;
        this.visibility = visibility;
    }

    public String getName(){
        return idToken.getLexeme();
    }
    public int getLine(){
        return idToken.getLineNumber();
    }
    public Type getType(){
        return type;
    }
    public boolean isPublic(){
        return Objects.equals(visibility.getLexeme(), "public");
    }
    public Token getVisibility(){
        return visibility;
    }
    public void setValue(ExpressionNode value){
        if(value != null){
            ExpressionNode actualNode = value;
            if(value instanceof UnaryExpNode) {
                UnaryExpNode unaryNode = (UnaryExpNode) value;
                actualNode = unaryNode.getOperand();
            }

            if(actualNode instanceof VarCallNode) {
                VarCallNode varCallNode = (VarCallNode) actualNode;
                String varName = varCallNode.getToken().getLexeme();

                EntityClass currentClass = symbolTable.getCurrentClass();
                if(currentClass != null){
                    Attribute referencedAttr = currentClass.getAttributes().get(varName);
                    if(referencedAttr == null){
                        throw new SemanticException("Error semantico en linea " + varCallNode.getLine() + ": el atributo " + varName + " no esta declarado.", varName, varCallNode.getLine());
                    }
                }
            }

            if(value.isAssign())
                throw new SemanticException("Error semantico en linea "+value.getLine()+" La expresion asignada al atributo de nombre "+ idToken.getLexeme() +" no puede ser una asignacion. ", value.getLexeme(), value.getLine());
        }
        this.value = value;
    }


    public ExpressionNode getValue(){
        return value;
    }

    public void estaBienDeclarado(){
        if(!type.isPrimitive() && (symbolTable.getClass(type.getName())==null)){
            throw new SemanticException("Error semantico en linea "+type.getLine()+" El atributo de nombre "+ idToken.getLexeme() +" fue declarado como tipo de clase inexistente. ",type.getName(), type.getLine());
        }
    }
    public void chequeoSentencias(){
        if(value != null){
            Type valueType;
            if(value instanceof ConstructorCallNode){
                ConstructorCallNode constructorCallNode = (ConstructorCallNode) value;
                valueType = new ReferenceType(constructorCallNode.getToken());
            }else{
                valueType = value.check();
            }
            if(!valueType.isSubtypeOf(type)){
                if(type.isPrimitive() && valueType.getName().equals("null")){
                    throw new SemanticException("Error semantico en linea "+value.getLine()+" El atributo de nombre "+ idToken.getLexeme() +" fue inicializado con null pero es de tipo primitivo. ", value.getLexeme(), value.getLine());
                }
                if(!valueType.getName().equals("null")) {
                    throw new SemanticException("Error semantico en linea " + value.getLine() + " El atributo de nombre " + idToken.getLexeme() + " fue inicializado con un tipo incompatible. ", value.getLexeme(), value.getLine());
                }
            }
        }
    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
