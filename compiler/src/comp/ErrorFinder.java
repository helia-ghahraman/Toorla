package comp;

import gen.ToorlaParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ErrorFinder {
    private SymbolTable table;
    private ArrayList<Error> errors;
    Map<String, String> map = new HashMap<String, String>();

    public ErrorFinder(SymbolTable table){
        this.table = table;
        errors = new ArrayList<Error>();
    }

    boolean findDefinitionDuplicate(SymbolTable table, Object[] args, SymbolTableItem entry2){
        String type = " duplicate definition";

        for (Map.Entry<String,SymbolTableItem> entry1 : table.getItems().entrySet()) {
            if(entry1.getValue().equals(entry2)) continue;
            boolean typeEq = entry1.getValue().type.equals(entry2.type);
            boolean nameEq = entry1.getValue().getName().equals(entry2.getName());
            if(typeEq && nameEq){
                int lineNumber=(Integer) args[1];
                String text = entry1.getValue().itemType + " " + entry1.getValue().getName() + " has been defined already";
                int column = (Integer) args[0];
                this.errors.add(new Error(type, lineNumber, column, text));
                return true;
            }
        }
        return false;
    }

    void inheritanceError(ToorlaParser.ClassDeclarationContext ctx){
        String type = " inheritance loop";

        if (ctx.classParent!=null) {
            map.put(ctx.className.getText(), ctx.classParent.getText());
            ArrayList<String> inherit = new ArrayList<>();
            inherit.add(ctx.className.getText());
            inherit.add(ctx.classParent.getText());
            String current = ctx.classParent.getText();
            for (int i =0;i< map.size();i++){
                if (map.get(current) == null) { //has no parent
                    break;
                } else if (map.get(current).equals(ctx.className.getText())) {
                    String text;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < inherit.size(); j++) {
                        stringBuilder.append("[" + inherit.get(j) + "]=>");
                    }
                    stringBuilder.append("[" + map.get(current) + "]");
                    text = stringBuilder.toString();
                    this.errors.add(new Error(type, text));
                }
                current = map.get(current);
                inherit.add(current);
            }
        }
    }

     void findReturnTypeMismatch(SymbolTable table, ToorlaParser.MethodDeclarationContext ctx, Object[] args) {
        int line_num = (Integer) args[1];
        int column = (Integer) args[0];
        String type = " Return Type Mismatch";
        String text = "Return type of this method must be ";
        int i =0;
//         for (Map.Entry<String,SymbolTableItem> entry1 : table.getItems().entrySet()) {
//             System.out.println(i+" : "+entry1.getKey()+" : "+entry1.getValue().fieldType+" : "+ctx.s.closedStatement().statementReturn().expression().getText()+"....");
//             i++;
//         }
        boolean sw=false;
        for (Map.Entry<String,SymbolTableItem> entry1 : table.getItems().entrySet()) {
            if (entry1.getKey().startsWith("Method_")||entry1.getKey().startsWith("Constructor_")) {
                if (ctx.t != null && ctx.s.closedStatement().statementReturn().e != null) {
                    if (ctx.s.closedStatement().statementReturn().e.getText().equals(entry1.getValue().name) && (ctx.t.getText().equals(entry1.getValue().fieldType) || ctx.t.getText().equals("self.".concat(entry1.getValue().fieldType)))) ;
                    for (Map.Entry<String,SymbolTableItem> entry2 : table.getItems().entrySet()) {
                       // System.out.println(ctx.t.getText()+" : "+entry2.getValue().fieldType+" : "+ctx.s.closedStatement().statementReturn().e.getText()+" : "+entry2.getValue().name+"....");
                        if (!(entry2.getKey().startsWith("Method_")||entry2.getKey().startsWith("Constructor_"))){
                            if (ctx.t.getText().equals(entry2.getValue().fieldType) && (ctx.s.closedStatement().statementReturn().e.getText().equals(entry2.getValue().name)))sw=true;
                        }
                    }
                }
                if (!sw){
                    String completeText = text + ctx.t.getText();
                    this.errors.add(new Error(type, line_num, column, completeText));
                }
                sw=false;
            }
        }

    }

    void privateClasses(SymbolTable mainST,String currentClassName, String currentMethodName, int column,int lineNumber){
        ArrayList<SymbolTable> innerSymbolTables=table.getInnerSymbolTables();
        String type=" private method";
        String text="private methods are not accessible outside of class";
        if(mainST.methodNamesInClasses.containsKey(currentMethodName)) {

            ArrayList classAssosiatedWithCurrentMethod = mainST.methodNamesInClasses.get(currentMethodName);
            if(classAssosiatedWithCurrentMethod.get(0).toString().equals(currentClassName)) {
                return;
            }
            if(classAssosiatedWithCurrentMethod.get(1).equals("private")){
                this.errors.add(new Error(type, lineNumber, column, text));
            }
        }
    }


    private void cleanseDupErrors(){

        for (int i = 0; i < this.errors.size() - 1; i++) {
            for( int j = i + 1 ; j < this.errors.size(); j++){
                String t1 = this.errors.get(i).getType();
                String t2 = this.errors.get(j).getType();
                int l1 = this.errors.get(i).getLine();
                int l2 = this.errors.get(j).getLine();
                boolean eqType = this.errors.get(i).getType().equals(this.errors.get(j).getType());
                boolean eqLine = this.errors.get(i).getLine() == this.errors.get(j).getLine();
                boolean eqColumn = this.errors.get(i).getColumn() == this.errors.get(j).getColumn();
                boolean eqText = this.errors.get(i).getText().equals(this.errors.get(j).getText());
                if (eqText && eqLine && eqType && eqColumn){
                    this.errors.remove(j);
                }
            }
        }
    }

    @Override
    public String toString() {
        this.cleanseDupErrors();
        String str = "";
        for (Error err: this.errors){
            str +=  err + "\n" ;
        }
        return str;
    }
}

class Error {
    private String type;
    private int line;
    private int column;
    private String text;

    public Error(String type, int line, int column, String text){
        this.type = type;
        this.line = line;
        this.column = column;
        this.text = text;
    }
    public Error(String type, String text){
        this.type = type;
        this.text = text;
    }

    public int getLine() {
        return this.line;
    }

    public String getType() {
        return this.type;
    }

    public int getColumn() {
        return this.column;
    }

    public String getText() {
        return this.text;
    }

    public String toString(){
        if (Objects.equals(this.type, " duplicate definition") || Objects.equals(this.type, " private method"))
            return "Error" + this.type + " : in line " + this.line + ":" + this.column + " , " + this.text;
        else if(Objects.equals(this.type, " inheritance loop")){
            return "Error 410: invalid inheritance"+ this.text;
        }
        else{
            return "Error210 : in line "+this.line + ":" + this.column + " , " + this.text;
        }
    }
}
