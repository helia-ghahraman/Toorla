package comp;
import gen.ToorlaListener;
import gen.ToorlaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolTableCreator implements ToorlaListener {
    ArrayList<SymbolTable>symbolTables=new ArrayList<>();
    SymbolTable globalScope;
    SymbolTable currSymbolTable;
    SymbolTableItem currItem;
    boolean entry=false;
    String currentClassName=null;
    boolean isInClass=false;
    boolean isInMethod=false;
    String currenClassParent=null;
    ErrorFinder errFndr;
    int scopeCount=0;
    List<ParseTree> list;

    //it takes the name of the class instance and returns class name
    public String getClassName(SymbolTable givenSymbolTable,String classInstanceName){
        HashMap<String,String>parameterNames=new HashMap();
        ArrayList<SymbolTable>symbolTables=new ArrayList<>();
        SymbolTable symbolTableToBeChecked=givenSymbolTable;
        while (symbolTableToBeChecked!=null){
            symbolTables.add(symbolTableToBeChecked);
            symbolTableToBeChecked=symbolTableToBeChecked.getParent();
        }
        for(int i=0;i<symbolTables.size();i++){
            HashMap<String,String> currentSymbolTableClassNames=symbolTables.get(i).classNames;
            parameterNames.putAll(currentSymbolTableClassNames);
        }
        System.out.println("instance"+parameterNames.get(classInstanceName));;
        return parameterNames.get(classInstanceName);
    }

    public static boolean isNumeric(String string) {
        int intValue;

        if(string == null || string.equals("")) {
            return false;
        }

        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }
    @Override
    public void enterProgram(ToorlaParser.ProgramContext ctx) {
        globalScope = new SymbolTable("program", ctx.start.getLine());
        currSymbolTable = globalScope;
        symbolTables.add(currSymbolTable);
        errFndr = new ErrorFinder(globalScope);
    }

    @Override
    public void exitProgram(ToorlaParser.ProgramContext ctx) {
        System.out.println(errFndr);
    }

    @Override
    public void enterClassDeclaration(ToorlaParser.ClassDeclarationContext ctx) {
        isInClass=true;
        currentClassName=ctx.className.getText();
        String keyName="Class_"+ctx.className.getText();
        String name=ctx.className.getText();
        String parent=null;
        if(ctx.classParent!=null) {
            parent = ctx.classParent.getText();
            currenClassParent=ctx.classParent.getText();
        }
        String type="Class";
        SymbolTableItem symbolTableItem=new SymbolTableItem();
        symbolTableItem.name=name;
        symbolTableItem.type=type;
        symbolTableItem.itemType=type;
        symbolTableItem.parent=parent;
        symbolTableItem.isEntry=entry;
        currSymbolTable.add_item(keyName, ctx.start.getLine(), symbolTableItem,ctx.start.getCharPositionInLine());
        int lineNumber = ctx.start.getLine();
        Object[] args = {ctx.start.getCharPositionInLine(),ctx.start.getLine()};

        errFndr.findDefinitionDuplicate(currSymbolTable, args,symbolTableItem);
        errFndr.inheritanceError(ctx);
        SymbolTable tmp = new SymbolTable(name, lineNumber);
        tmp.parent=currSymbolTable;
        currSymbolTable.addSubSymbolTable(tmp);
        currSymbolTable = tmp;
        symbolTables.add(currSymbolTable);
    }

    @Override
    public void exitClassDeclaration(ToorlaParser.ClassDeclarationContext ctx) {
        isInClass=false;
        currenClassParent=null;
        currSymbolTable = currSymbolTable.getParent();
        currentClassName=null;
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
            String keyName="Field_"+newList.get(i);
            String type=null;
            if(isInClass&&isInMethod){
                type="MethodField";
            }
            else if(isInClass){
                type="ClassField";
            }

            SymbolTableItem symbolTableItem=new SymbolTableItem();
            symbolTableItem.name=newList.get(i);
            symbolTableItem.type=type;
            symbolTableItem.itemType=type;
            symbolTableItem.fieldType=newList.get(newList.size()-2);
            currSymbolTable.add_item(keyName, ctx.start.getLine(), symbolTableItem,ctx.start.getCharPositionInLine());
            Object[] args = {ctx.start.getCharPositionInLine(),ctx.start.getLine()};
            boolean result=errFndr.findDefinitionDuplicate(currSymbolTable, args, symbolTableItem);
//            }
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
        ArrayList<SymbolTableItem>symbolTableItemss=new ArrayList<>();
        ArrayList<String>symbolTableItemsKeys=new ArrayList<>();
        isInMethod=true;
        ArrayList toBeAdded=new ArrayList();
        toBeAdded.add(currentClassName);
        if(ctx.methodAccessModifier!=null)
            toBeAdded.add(ctx.methodAccessModifier.getText());
        else toBeAdded.add("public");
        globalScope.methodNamesInClasses.put(ctx.methodName.getText(),toBeAdded);
        String keyName;
        String type;
        if(ctx.methodName.getText().equals(currentClassName)){
            keyName = "Constructor_" + ctx.methodName.getText();
            type="ConstructorMethod";

        }else {
            keyName = "Method_" + ctx.methodName.getText();
            type = "Method";
        }
        String name=ctx.methodName.getText();
        String returnType="["+ctx.t.getText()+"]";
        String params="[]";
        int count=1;

        if(ctx.param1!=null){
            StringBuilder stringBuilder=new StringBuilder();
            list=ctx.children;
            ArrayList<String> newList=new ArrayList<>();
            for(int i=0;i<list.size();i++){
                newList.add(list.get(i).getText());
            }
            int first=newList.indexOf("(");
            int last=newList.indexOf(")");
            for(int j=first+1;j<last;j+=4){
                if(j!=first+1){
                    stringBuilder.append(", ");
                }
                stringBuilder.append("[");
                stringBuilder.append("name: "+newList.get(j)+", type: "+newList.get(j+2)+", index: "+count);
                count++;
                stringBuilder.append("]");
                    SymbolTableItem symbolTableItem2 = new SymbolTableItem();
                    String keyName2 = "Field_" + newList.get(j);
                    symbolTableItem2.name = newList.get(j);
                    symbolTableItem2.type = "ParamField";
                    symbolTableItem2.itemType = "Var";
                    symbolTableItem2.fieldType = newList.get(j + 2);
                    String toBeChecked=newList.get(j + 2);
                    if(newList.get(j+2).contains("[")){
                        int start=newList.get(j+2).indexOf("[");
                        toBeChecked=newList.get(j+2).substring(0,start);
                    }
                    boolean isDefined=true;
                    if(!toBeChecked.equals("int")&&!toBeChecked.equals("string")&&!toBeChecked.equals("bool")){
                        isDefined=false;
                        ArrayList<String> keys = new ArrayList<>(globalScope.items.keySet());
                        for(int i=0;i<keys.size();i++){
                            if(globalScope.items.get(keys.get(i)).type.equals("Class")){
                                if(globalScope.items.get(keys.get(i)).name.equals(toBeChecked)){
                                    isDefined=true;
                                    break;
                                }
                            }
                        }
                        if(!isDefined){
                            if(currenClassParent!=null) {
                                if (currenClassParent.equals(toBeChecked)) {
                                    isDefined = true;
                                }
                            }
                        }
                    }
                    symbolTableItemss.add(symbolTableItem2);
                    symbolTableItemsKeys.add(keyName2);
            }
            params=stringBuilder.toString();
        }
        SymbolTableItem symbolTableItem=new SymbolTableItem();
        symbolTableItem.name=name;
        symbolTableItem.type=type;
        symbolTableItem.returnType=returnType;
        symbolTableItem.parList=params;
        symbolTableItem.itemType="Method";

        currSymbolTable.add_item(keyName, ctx.start.getLine(), symbolTableItem,ctx.start.getCharPositionInLine());
        int lineNumber = ctx.start.getLine();
        Object[] args = {ctx.start.getCharPositionInLine(),ctx.start.getLine()};
        errFndr.findDefinitionDuplicate(currSymbolTable, args,symbolTableItem);
        errFndr.findReturnTypeMismatch(currSymbolTable,ctx, args);
        SymbolTable tmp = new SymbolTable(name, lineNumber);
        tmp.parent=currSymbolTable;
        currSymbolTable.addSubSymbolTable(tmp);
        currSymbolTable = tmp;
        symbolTables.add(currSymbolTable);
        for(int i=0;i<symbolTableItemss.size();i++){
            currSymbolTable.add_item(symbolTableItemsKeys.get(i),ctx.start.getLine(),symbolTableItemss.get(i),ctx.start.getCharPositionInLine());


        }
    }

    @Override
    public void exitMethodDeclaration(ToorlaParser.MethodDeclarationContext ctx) {
        isInMethod = false;
        currSymbolTable = currSymbolTable.getParent();

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
        SymbolTable if_statement;
        if(scopeCount >= 2){
            if_statement = new SymbolTable("nested",ctx.start.getLine());
        }
        else {
            if_statement = new SymbolTable("if",ctx.start.getLine());
        }
        if_statement.parent=currSymbolTable;
        currSymbolTable.addSubSymbolTable(if_statement);
        currSymbolTable = if_statement;
        symbolTables.add(currSymbolTable);
    }

    @Override
    public void exitClosedConditional(ToorlaParser.ClosedConditionalContext ctx) {
        scopeCount --;
        currSymbolTable = currSymbolTable.getParent();
    }

    @Override
    public void enterOpenConditional(ToorlaParser.OpenConditionalContext ctx) {
        scopeCount ++;
        SymbolTable if_statement;
        if(scopeCount >= 2){
            if_statement = new SymbolTable("nested",ctx.start.getLine());
        }
        else {
            if_statement = new SymbolTable("if",ctx.start.getLine());
        }
        if_statement.parent=currSymbolTable;
        currSymbolTable.addSubSymbolTable(if_statement);
        currSymbolTable = if_statement;
        symbolTables.add(currSymbolTable);
    }

    @Override
    public void exitOpenConditional(ToorlaParser.OpenConditionalContext ctx) {
        scopeCount --;
        currSymbolTable = currSymbolTable.getParent();
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
        List<ParseTree> list2=ctx.children;
//        System.out.println(ctx.getText());
        ArrayList<String> newList=new ArrayList<>();
        for(int i=0;i<list2.size();i++){
            newList.add(list2.get(i).getText());
//            System.out.println("hi+"+list.get(i).getText());
        }
        int index=newList.indexOf("var");
        for (int i=index+1;i<newList.size()-1;i+=4){
            String type=null;
            if(isInClass&&isInMethod){
                type="MethodField";
            }
            else if(isInClass){
                type="ClassField";
            }
            String keyName="Field_"+newList.get(i);
            SymbolTableItem symbolTableItem=new SymbolTableItem();
            symbolTableItem.name=newList.get(i);
            symbolTableItem.type=type;
            symbolTableItem.itemType="Var";
            String fieldType=null;


            if(isNumeric(newList.get(i+2))){
                fieldType="int";
            }
            else if(newList.get(i+2).equals("true")||newList.get(i+2).equals("false")){
                fieldType="bool";
            }
            else if(newList.get(i+2).contains("\"")){
                fieldType="string";
            }
            else if(newList.get(i+2).contains("[")){
                int start=newList.get(i+2).indexOf("[");
                fieldType=newList.get(i+2).substring(0,start)+"[]";
            }
            else if(newList.get(i+2).contains("(")){
                int start=newList.get(i+2).indexOf("(");
                fieldType=newList.get(i+2).substring(0,start);
            }
            symbolTableItem.fieldType=fieldType;
            currSymbolTable.add_item(keyName, ctx.start.getLine(), symbolTableItem,ctx.start.getCharPositionInLine());
            Object[] args = {ctx.start.getCharPositionInLine(),ctx.start.getLine()};
            errFndr.findDefinitionDuplicate(currSymbolTable, args,symbolTableItem);

//            ArrayList<String> newList2=new ArrayList<>();
//            StringBuilder stringBuilder=new StringBuilder();
//            for(int j=0;j<list.size();j++){
//                newList2.add(list.get(j).getText());
//            }
//            int first=newList2.indexOf("(");
//            int last=newList2.indexOf(")");
//            for(int j=first+1;j<last;j+=4){
//                if(j!=first+1){
//                    stringBuilder.append(", ");
//                }
//                stringBuilder.append("[");
//                stringBuilder.append("]");
//
//                SymbolTableItem symbolTableItem2=new SymbolTableItem();
//                String keyName2="Field_"+newList2.get(j);
//                symbolTableItem2.name=newList2.get(j);
//                symbolTableItem2.type="ParamField";
//                symbolTableItem2.itemType="Var";
//                symbolTableItem2.fieldType=newList.get(j+2);
//                currSymbolTable.add_item(keyName2, ctx.start.getLine(), symbolTableItem2,ctx.start.getCharPositionInLine());
//                Object[] args2 = {ctx.start.getCharPositionInLine(),ctx.start.getLine()};
//                errFndr.findDefinitionDuplicate(currSymbolTable, args2,symbolTableItem2);
//            }
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
        SymbolTable while_statement;
        if(scopeCount >= 2){
            while_statement = new SymbolTable("nested",ctx.start.getLine());
        }
        else {
            while_statement = new SymbolTable("while",ctx.start.getLine());
        }
        while_statement.parent=currSymbolTable;
        currSymbolTable.addSubSymbolTable(while_statement);
        currSymbolTable = while_statement;
        symbolTables.add(currSymbolTable);
    }

    @Override
    public void exitStatementClosedLoop(ToorlaParser.StatementClosedLoopContext ctx) {
        scopeCount --;
        currSymbolTable = currSymbolTable.getParent();
    }

    @Override
    public void enterStatementOpenLoop(ToorlaParser.StatementOpenLoopContext ctx) {
        scopeCount ++;
        SymbolTable while_statement;
        if(scopeCount >= 2){
            while_statement = new SymbolTable("nested",ctx.start.getLine());
        }
        else {
            while_statement = new SymbolTable("while",ctx.start.getLine());
        }
        while_statement.parent=currSymbolTable;
        currSymbolTable.addSubSymbolTable(while_statement);
        currSymbolTable = while_statement;
        symbolTables.add(currSymbolTable);
    }

    @Override
    public void exitStatementOpenLoop(ToorlaParser.StatementOpenLoopContext ctx) {
        scopeCount --;
        currSymbolTable = currSymbolTable.getParent();
    }

    @Override
    public void enterStatementWrite(ToorlaParser.StatementWriteContext ctx) {

    }

    @Override
    public void exitStatementWrite(ToorlaParser.StatementWriteContext ctx) {

    }

    @Override
    public void enterStatementAssignment(ToorlaParser.StatementAssignmentContext ctx) {
        List<ParseTree> list=ctx.children;
        if(ctx.right!=null){

            if(ctx.right.e!=null){

                if(ctx.right.e.a!=null){

                    if(ctx.right.e.a.e!=null){

                        if(ctx.right.e.a.e.c!=null){

                            if(ctx.right.e.a.e.c.a!=null){

                                if(ctx.right.e.a.e.c.a.m!=null){

                                    if(ctx.right.e.a.e.c.a.m.u!=null){

                                        if(ctx.right.e.a.e.c.a.m.u.m!=null){

                                            if(ctx.right.e.a.e.c.a.m.u.m.o!=null) {

                                                if(ctx.right.e.a.e.c.a.m.u.m.o.newModifier!=null){

//                                                    System.out.println("field: "+ ctx.left.getText() + "/ type: local var..."+ctx.right.e.a.e.c.a.m.u.m.o.newModifier.getText());
//                                                    System.out.println(ctx.right.e.getText());
                                                    String newString=ctx.right.e.getText();
                                                    int start = ctx.right.e.getText().indexOf("[");
                                                    String type=null;
                                                    if(isInClass&&isInMethod){
                                                        type="MethodVar";
                                                    }
                                                    else if(isInClass){
                                                        type="ClassVar";
                                                    }
                                                    if(start>=0){
                                                        newString=newString.substring(3,start)+"[]";
                                                        String keyName="Field_"+ctx.left.getText();
                                                        SymbolTableItem symbolTableItem=new SymbolTableItem();
                                                        symbolTableItem.name=ctx.left.getText();
                                                        symbolTableItem.type=type;
                                                        symbolTableItem.itemType="Var";
                                                        symbolTableItem.fieldType=newString;
                                                        currSymbolTable.add_item(keyName, ctx.start.getLine(), symbolTableItem,ctx.start.getCharPositionInLine());
                                                        Object[] args = {ctx.start.getCharPositionInLine(),ctx.start.getLine()};
                                                        //errFndr.findDefinitionDuplicate(currSymbolTable, args,symbolTableItem);
                                                    }
                                                    else {

                                                        start = ctx.right.e.getText().indexOf("(");
                                                        newString=newString.substring(3,start);
                                                        String keyName="Field_"+ctx.left.getText();
                                                        currSymbolTable.classNames.put(ctx.left.getText(),newString);
                                                        SymbolTableItem symbolTableItem=new SymbolTableItem();
                                                        symbolTableItem.name=ctx.left.getText();
                                                        symbolTableItem.type=type;
                                                        symbolTableItem.itemType="Var";
                                                        symbolTableItem.fieldType="Classtype:["+newString+"]";
                                                        boolean isDefined=false;
                                                        ArrayList<String> keys = new ArrayList<>(globalScope.items.keySet());
                                                        for(int i=0;i<keys.size();i++){
                                                            if(globalScope.items.get(keys.get(i)).type.equals("Class")){

                                                                if(globalScope.items.get(keys.get(i)).name.equals(newString)){
                                                                    isDefined=true;
                                                                    break;

                                                                }

                                                            }

                                                        }
                                                        if(!isDefined){
                                                            if(currenClassParent!=null) {
                                                                if (currenClassParent.equals(ctx.left.getText())) {

                                                                    isDefined = true;
                                                                }
                                                            }
                                                        }
                                                        symbolTableItem.isDefined=isDefined;

                                                        currSymbolTable.add_item(keyName, ctx.start.getLine(), symbolTableItem,ctx.start.getCharPositionInLine());
                                                        Object[] args = {ctx.start.getCharPositionInLine(),ctx.start.getLine()};
                                                        //errFndr.findDefinitionDuplicate(currSymbolTable, args,symbolTableItem);
                                                    }

//                                                    System.out.println(newString);



                                                }


                                            }


                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

        }
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
        if(ctx.mt.dotOp!=null) {
//            errFndr.privateClasses(globalScope,currentClassName,ctx.mt.);
            int start = ctx.mt.getText().indexOf("(");
            if (start != -1 && !ctx.o.getText().equals("self")) {
//                System.out.println("bryh" + ctx.o.getText());
                String newString = ctx.mt.getText().substring(1, start);
//                System.out.println("hi" + newString);
                int lineNumber = ctx.start.getLine();
//                Object[] args = {ctx.start.getCharPositionInLine()};
//                String className=getClassName(currSymbolTable,ctx.o.getText());
//                System.out.println("please "+className+" "+ctx.o.getText()+" "+newString);
                errFndr.privateClasses(globalScope,currentClassName,newString,ctx.start.getCharPositionInLine(),lineNumber);
            }
        }


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
    public String toString() {
        SymbolTable curr = globalScope;
        String str = globalScope.toString() + "\n";
        str += parseChildren(curr);
        return str;
    }
    private String parseChildren(SymbolTable curr) {
        String str = "";
        ArrayList<SymbolTable> children = curr.getInnerSymbolTables();
        for (SymbolTable table : children) {
            str += table.toString() + "\n";
            str += parseChildren(table);
        }
        return str;
    }
}
