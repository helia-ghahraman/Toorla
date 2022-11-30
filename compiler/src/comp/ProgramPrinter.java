package comp;

import gen.ToorlaListener;
import gen.ToorlaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public class ProgramPrinter implements ToorlaListener {
    int indent=0;
    boolean entry=false;
    String currentClassName=null;
    int scopeCount = 0;
    boolean printScope = false;
    @Override
    public void enterProgram(ToorlaParser.ProgramContext ctx) {

        System.out.println("program start {");
        indent+=4;
    }

    @Override
    public void exitProgram(ToorlaParser.ProgramContext ctx) {
        System.out.println("}");
    }

    @Override
    public void enterClassDeclaration(ToorlaParser.ClassDeclarationContext ctx) {
        currentClassName=ctx.className.getText();
        for(int i=0;i<indent;i++){
            System.out.print(" ");
        }
        System.out.print("class: "+ctx.className.getText()+"/ "+"class parent: ");
        if(ctx.classParent==null){
            System.out.print("none"+"/ "+"isEntry: ");
        }
        else{
            System.out.print(ctx.classParent.getText()+"/ "+"isEntry: ");
        }

        if(entry){
            System.out.print("true");
        }
        else{
            System.out.print("false");
        }
        System.out.println("{");
        indent+=4;


    }

    @Override
    public void exitClassDeclaration(ToorlaParser.ClassDeclarationContext ctx) {
        currentClassName=null;
        entry=false;
        indent-=4;
        for(int i=0;i<indent;i++){
            System.out.print(" ");
        }
        System.out.println("}");

    }

    @Override
    public void enterEntryClassDeclaration(ToorlaParser.EntryClassDeclarationContext ctx) {
        entry=true;
    }

    @Override
    public void exitEntryClassDeclaration(ToorlaParser.EntryClassDeclarationContext ctx) {
        entry=false;
    }

    @Override
    public void enterFieldDeclaration(ToorlaParser.FieldDeclarationContext ctx) {
        List<ParseTree> list=ctx.children;
        ArrayList<String> newList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            newList.add(list.get(i).getText());
        }
        int index=newList.indexOf("field");
        for (int i=index+1;i<newList.size()-1;i+=2){
            for(int j=0;j<indent;j++){
                System.out.print(" ");
            }
            System.out.println("field: "+newList.get(i)+ "/ type: "+newList.get(newList.size()-2));
        }
    }

    @Override
    public void exitFieldDeclaration(ToorlaParser.FieldDeclarationContext ctx) {

    }

    @Override
    public void enterAccess_modifier(ToorlaParser.Access_modifierContext ctx) {

    }

    @Override
    public void exitAccess_modifier(ToorlaParser.Access_modifierContext ctx) {

    }

    @Override
    public void enterMethodDeclaration(ToorlaParser.MethodDeclarationContext ctx) {
        for(int i=0;i<indent;i++){
            System.out.print(" ");
        }
        if(ctx.methodName.getText().equals(currentClassName)){
            if (ctx.methodAccessModifier.getText()==null){
                System.out.println("class method: "+ctx.methodName.getText()+" / "+"return type: "+ctx.t.getText()+" / "+"type: "+"public{");
            }
            else {
                System.out.println("class method: "+ctx.methodName.getText()+" / "+"return type: "+ctx.t.getText()+" / "+"type: "+ctx.methodAccessModifier.getText()+"{");
            }
            indent+=4;
            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            System.out.print("parameters list: ");
            if(ctx.param1==null){
                System.out.println("[]");
            }
            else{
                System.out.print("[");
                List<ParseTree> list=ctx.children;
                ArrayList<String> newList=new ArrayList<>();
                for(int i=0;i<list.size();i++){
                    newList.add(list.get(i).getText());
                }
                int first=newList.indexOf("(");
                int last=newList.indexOf(")");
                for(int j=first+1;j<last;j+=4){
                    if(j!=first+1){
                        System.out.print(",");
                    }
                    System.out.print("type: "+newList.get(j+2)+"/ name: "+newList.get(j));
                }
                System.out.println("]");
            }

        }
        else if(ctx.methodName.getText().equals("main")) {
            System.out.println("main method / "+"return type: "+ctx.t.getText()+" {");
            indent+=4;
            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            System.out.print("parameters list: ");
            if(ctx.param1==null){
                System.out.println("[]");
            }
            else{
                System.out.print("[");
                List<ParseTree> list=ctx.children;
                ArrayList<String> newList=new ArrayList<>();
                for(int i=0;i<list.size();i++){
                    newList.add(list.get(i).getText());
                }
                int first=newList.indexOf("(");
                int last=newList.indexOf(")");
                for(int j=first+1;j<last;j+=4){
                    if(j!=first+1){
                        System.out.print(",");
                    }
                    System.out.print("type: "+newList.get(j+2)+"/ name: "+newList.get(j));
                }
                System.out.println("]");
            }

        }
        else {
            if (ctx.methodAccessModifier.getText()==null){
                System.out.println("class method: "+ctx.methodName.getText()+" / "+"return type: "+ctx.t.getText()+" / "+"type: "+"public{");
            }
            else {
                System.out.println("class method: "+ctx.methodName.getText()+" / "+"return type: "+ctx.t.getText()+" / "+"type: "+ctx.methodAccessModifier.getText()+"{");
            }
            indent+=4;
            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            System.out.print("parameters list: ");
            if(ctx.param1==null){
                System.out.println("[]");
            }
            else{
                System.out.print("[");
                List<ParseTree> list=ctx.children;
                ArrayList<String> newList=new ArrayList<>();
                for(int i=0;i<list.size();i++){
                    newList.add(list.get(i).getText());
                }
                int first=newList.indexOf("(");
                int last=newList.indexOf(")");
                for(int j=first+1;j<last;j+=4){
                    if(j!=first+1){
                        System.out.print(",");
                    }
                    System.out.print("type: "+newList.get(j+2)+"/ name: "+newList.get(j));
                }
                System.out.println("]");
            }
        }
        }



    @Override
    public void exitMethodDeclaration(ToorlaParser.MethodDeclarationContext ctx) {
        indent-=4;
        for(int i=0;i<indent;i++){
            System.out.print(" ");
        }
        System.out.println("}");
    }

    @Override
    public void enterClosedStatement(ToorlaParser.ClosedStatementContext ctx) {

    }

    @Override
    public void exitClosedStatement(ToorlaParser.ClosedStatementContext ctx) {

    }

    @Override
    public void enterClosedConditional(ToorlaParser.ClosedConditionalContext ctx) {
        scopeCount ++;
        if(scopeCount >= 2) {

            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            if(!printScope){
                System.out.println( "nested {\n");
                printScope = true;
            }
            else {
                System.out.println( "nested {\n");
            }
            indent+=4;
        }
    }

    @Override
    public void exitClosedConditional(ToorlaParser.ClosedConditionalContext ctx) {
        scopeCount --;
        if( scopeCount!=0 &&printScope){
            indent-=4;
            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            System.out.println( "}");

        }
        if(scopeCount == 0){
            printScope = false;
        }
    }

    @Override
    public void enterOpenConditional(ToorlaParser.OpenConditionalContext ctx) {
        scopeCount ++;
        if(scopeCount >= 2) {

            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            if(!printScope){
                System.out.println( "nested {\n");
                printScope = true;
            }
            else {
                System.out.println( "nested {\n");
            }
            indent+=4;
        }

    }

    @Override
    public void exitOpenConditional(ToorlaParser.OpenConditionalContext ctx) {
        scopeCount --;
        if( scopeCount!=0 &&printScope){
            indent-=4;
            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            System.out.println( "}");

        }
        if(scopeCount == 0){
            printScope = false;
        }

    }

    @Override
    public void enterOpenStatement(ToorlaParser.OpenStatementContext ctx) {

    }

    @Override
    public void exitOpenStatement(ToorlaParser.OpenStatementContext ctx) {

    }

    @Override
    public void enterStatement(ToorlaParser.StatementContext ctx) {

    }

    @Override
    public void exitStatement(ToorlaParser.StatementContext ctx) {

    }

    @Override
    public void enterStatementVarDef(ToorlaParser.StatementVarDefContext ctx) {
        List<ParseTree> list=ctx.children;
        ArrayList<String> newList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            newList.add(list.get(i).getText());
        }
        int index=newList.indexOf("var");
        for (int i=index+1;i<newList.size()-1;i+=4){
            for(int j=0;j<indent;j++){
                System.out.print(" ");
            }
            System.out.println("field: "+newList.get(i)+ "/ type: local var");
        }
    }

    @Override
    public void exitStatementVarDef(ToorlaParser.StatementVarDefContext ctx) {

    }

    @Override
    public void enterStatementBlock(ToorlaParser.StatementBlockContext ctx) {

    }

    @Override
    public void exitStatementBlock(ToorlaParser.StatementBlockContext ctx) {

    }

    @Override
    public void enterStatementContinue(ToorlaParser.StatementContinueContext ctx) {

    }

    @Override
    public void exitStatementContinue(ToorlaParser.StatementContinueContext ctx) {

    }

    @Override
    public void enterStatementBreak(ToorlaParser.StatementBreakContext ctx) {

    }

    @Override
    public void exitStatementBreak(ToorlaParser.StatementBreakContext ctx) {

    }

    @Override
    public void enterStatementReturn(ToorlaParser.StatementReturnContext ctx) {

    }

    @Override
    public void exitStatementReturn(ToorlaParser.StatementReturnContext ctx) {

    }

    @Override
    public void enterStatementClosedLoop(ToorlaParser.StatementClosedLoopContext ctx) {
        scopeCount ++;
        if(scopeCount >= 2) {

            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            if(!printScope){
                System.out.println( "nested {\n");
                printScope = true;
            }
            else {
                System.out.println( "nested {\n");
            }
            indent+=4;
        }
    }

    @Override
    public void exitStatementClosedLoop(ToorlaParser.StatementClosedLoopContext ctx) {
        scopeCount --;
        if( scopeCount!=0 &&printScope){
            indent-=4;
            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            System.out.println( "}");

        }
        if(scopeCount == 0){
            printScope = false;
        }
    }

    @Override
    public void enterStatementOpenLoop(ToorlaParser.StatementOpenLoopContext ctx) {
        scopeCount ++;
        if(scopeCount >= 2) {

            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            if(!printScope){
                System.out.println( "nested {\n");
                printScope = true;
            }
            else {
                System.out.println( "nested {\n");
            }
            indent+=4;
        }
    }

    @Override
    public void exitStatementOpenLoop(ToorlaParser.StatementOpenLoopContext ctx) {
        scopeCount --;
        if( scopeCount!=0 &&printScope){
            indent-=4;
            for(int i=0;i<indent;i++){
                System.out.print(" ");
            }
            System.out.println( "}");

        }
        if(scopeCount == 0){
            printScope = false;
        }
    }

    @Override
    public void enterStatementWrite(ToorlaParser.StatementWriteContext ctx) {

    }

    @Override
    public void exitStatementWrite(ToorlaParser.StatementWriteContext ctx) {

    }

    @Override
    public void enterStatementAssignment(ToorlaParser.StatementAssignmentContext ctx) {

    }

    @Override
    public void exitStatementAssignment(ToorlaParser.StatementAssignmentContext ctx) {

    }

    @Override
    public void enterStatementInc(ToorlaParser.StatementIncContext ctx) {

    }

    @Override
    public void exitStatementInc(ToorlaParser.StatementIncContext ctx) {

    }

    @Override
    public void enterStatementDec(ToorlaParser.StatementDecContext ctx) {

    }

    @Override
    public void exitStatementDec(ToorlaParser.StatementDecContext ctx) {

    }

    @Override
    public void enterExpression(ToorlaParser.ExpressionContext ctx) {

    }

    @Override
    public void exitExpression(ToorlaParser.ExpressionContext ctx) {

    }

    @Override
    public void enterExpressionOr(ToorlaParser.ExpressionOrContext ctx) {

    }

    @Override
    public void exitExpressionOr(ToorlaParser.ExpressionOrContext ctx) {

    }

    @Override
    public void enterExpressionOrTemp(ToorlaParser.ExpressionOrTempContext ctx) {

    }

    @Override
    public void exitExpressionOrTemp(ToorlaParser.ExpressionOrTempContext ctx) {

    }

    @Override
    public void enterExpressionAnd(ToorlaParser.ExpressionAndContext ctx) {

    }

    @Override
    public void exitExpressionAnd(ToorlaParser.ExpressionAndContext ctx) {

    }

    @Override
    public void enterExpressionAndTemp(ToorlaParser.ExpressionAndTempContext ctx) {

    }

    @Override
    public void exitExpressionAndTemp(ToorlaParser.ExpressionAndTempContext ctx) {

    }

    @Override
    public void enterExpressionEq(ToorlaParser.ExpressionEqContext ctx) {

    }

    @Override
    public void exitExpressionEq(ToorlaParser.ExpressionEqContext ctx) {

    }

    @Override
    public void enterExpressionEqTemp(ToorlaParser.ExpressionEqTempContext ctx) {

    }

    @Override
    public void exitExpressionEqTemp(ToorlaParser.ExpressionEqTempContext ctx) {

    }

    @Override
    public void enterExpressionCmp(ToorlaParser.ExpressionCmpContext ctx) {

    }

    @Override
    public void exitExpressionCmp(ToorlaParser.ExpressionCmpContext ctx) {

    }

    @Override
    public void enterExpressionCmpTemp(ToorlaParser.ExpressionCmpTempContext ctx) {

    }

    @Override
    public void exitExpressionCmpTemp(ToorlaParser.ExpressionCmpTempContext ctx) {

    }

    @Override
    public void enterExpressionAdd(ToorlaParser.ExpressionAddContext ctx) {

    }

    @Override
    public void exitExpressionAdd(ToorlaParser.ExpressionAddContext ctx) {

    }

    @Override
    public void enterExpressionAddTemp(ToorlaParser.ExpressionAddTempContext ctx) {

    }

    @Override
    public void exitExpressionAddTemp(ToorlaParser.ExpressionAddTempContext ctx) {

    }

    @Override
    public void enterExpressionMultMod(ToorlaParser.ExpressionMultModContext ctx) {

    }

    @Override
    public void exitExpressionMultMod(ToorlaParser.ExpressionMultModContext ctx) {

    }

    @Override
    public void enterExpressionMultModTemp(ToorlaParser.ExpressionMultModTempContext ctx) {

    }

    @Override
    public void exitExpressionMultModTemp(ToorlaParser.ExpressionMultModTempContext ctx) {

    }

    @Override
    public void enterExpressionUnary(ToorlaParser.ExpressionUnaryContext ctx) {

    }

    @Override
    public void exitExpressionUnary(ToorlaParser.ExpressionUnaryContext ctx) {

    }

    @Override
    public void enterExpressionMethods(ToorlaParser.ExpressionMethodsContext ctx) {

    }

    @Override
    public void exitExpressionMethods(ToorlaParser.ExpressionMethodsContext ctx) {

    }

    @Override
    public void enterExpressionMethodsTemp(ToorlaParser.ExpressionMethodsTempContext ctx) {

    }

    @Override
    public void exitExpressionMethodsTemp(ToorlaParser.ExpressionMethodsTempContext ctx) {

    }

    @Override
    public void enterExpressionOther(ToorlaParser.ExpressionOtherContext ctx) {
        List<ParseTree> list=ctx.children;
        ArrayList<String> newList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            newList.add(list.get(i).getText());
        }
        int index=newList.indexOf("new");
        if(index>=0&&newList.get(index).equals("new")) {
            for (int i = index + 1; i < newList.size() - 1; i += 4) {
                for (int j = 0; j < indent; j++) {
                    System.out.print(" ");
                }
                System.out.println("field: " + newList.get(i) + "/ type: local var");
            }
        }
    }

    @Override
    public void exitExpressionOther(ToorlaParser.ExpressionOtherContext ctx) {

    }

    @Override
    public void enterToorlaType(ToorlaParser.ToorlaTypeContext ctx) {

    }

    @Override
    public void exitToorlaType(ToorlaParser.ToorlaTypeContext ctx) {

    }

    @Override
    public void enterSingleType(ToorlaParser.SingleTypeContext ctx) {

    }

    @Override
    public void exitSingleType(ToorlaParser.SingleTypeContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}