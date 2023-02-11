package comp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {
    LinkedHashMap<String, SymbolTableItem> items;
    String name;
    int scopeLineNumber;
    ArrayList<SymbolTable> innerSymbolTables;
    SymbolTable parent;
    HashMap<String,ArrayList>methodNamesInClasses=new HashMap();
    HashMap<String,String>classNames=new HashMap<>();
    String type;

    public SymbolTable(String name, int scopeLineNumber){
        this.name = name;
        this.scopeLineNumber = scopeLineNumber;
        this.items = new LinkedHashMap<String, SymbolTableItem>();
        this.innerSymbolTables = new ArrayList<SymbolTable>();
    }
    public void addSubSymbolTable(SymbolTable st){
        innerSymbolTables.add(st);
        st.parent = this;
    }
    public void add_item(String name, int lineNum, SymbolTableItem item, int column){
        if(items.containsKey(name)){
            name = name + "_" + lineNum+"_"+column;
        }
        items.put(name, item);
    }
    public void addField(String name, int lineNum, SymbolTableItem item, ArrayList<SymbolTable> symbolTables,int column){
        boolean isAdded=false;
        for (SymbolTable globalTable : symbolTables) {
            if (globalTable.items.containsKey(name)) {
                name = name + "_" + lineNum + "_" + column;
                items.put(name, item);
                isAdded=true;
                break;
            }
        }
        if(!isAdded){
            items.put(name, item);
        }

    }


    public SymbolTable getParent(){
        return parent;
    }

    public ArrayList<SymbolTable> getInnerSymbolTables() {
        return innerSymbolTables;
    }

    public HashMap<String, SymbolTableItem> getItems() {
        return items;
    }

    public String toString() {
        return "------------- " + name + " : " + scopeLineNumber + " -------------\n"
                + printItems() + "-----------------------------------------\n";
    }
    private String printItems(){
        String itemsStr = "";
        for (Map.Entry<String,SymbolTableItem> entry : items.entrySet()) {
            itemsStr += "Key = " + entry.getKey() + " | Value = " + entry.getValue() + "\n";
        }
        return itemsStr;
    }
}
class SymbolTableItem {
    String itemType;
    boolean isEntry=false;
    String name;
    String parent;
    String type;
    String returnType;
    boolean isClassField = false;
    String fieldType;
    boolean isDefined = true;
    boolean isClassReturn = false;
    String parList;
    int line_number;

    public SymbolTableItem(){
    }

    public String getName() {
        return name;
    }

    public int getLine_number() {
        return line_number;
    }

    public String toString() {
        String str = "";
        if(this.type.equals("Class")){
            str += "Class (name: " + this.name + ") (parent: " + parent + ")"+" (isEntry: "+isEntry+")";
        }
        if(this.type.equals("MethodField") || this.type.equals("ClassField")){
            if(this.type.equals("ClassField")){
                str += "ClassField";
            }
            else {
                str += "MethodField";
            }
            str += " (name : " + this.name + ") (type : ";
            str += this.fieldType +", isDefiend: " + this.isDefined + ")";
        }
        if(this.type.equals("MethodVar") || this.type.equals("ClassVar")){
            if(this.type.equals("ClassVar")){
                str += "ClassVar";
            }
            else {
                str += "MethodVar";
            }
            str += " (name : " + this.name + ") (type : [local var=";

            str += this.fieldType +", isDefiend: " + this.isDefined + ")";

        }
        if(this.type.equals("ConstructorMethod")){
            str+=this.itemType;
            str += " (name : " + this.name + ") (return type:"+this.returnType+") (parameter list" + ": "+this.parList+")";
        }

        if(this.type.equals("Method")){
            str+=this.itemType;
            str += " (name : " + this.name + ") (return type:"+this.returnType+") (parameter list" +": "+this.parList+")";
        }
        if(this.type.equals("ParamField")){
            str+=this.type;
            str += " (name : " + this.name + ") (type :"+this.fieldType+", isDefiend: " + this.isDefined + ")";

        }
        return str;
    }
}

class MyParameter {
    private String type;
    private int index;
    public MyParameter(String type, int index){
        this.type = type;
        this.index = index;
    }
    public String toString() {
        String str = "[type: " + this.type + ", index: " + this.index + "]";
        return str;
    }
}
enum ItemType {
    IMPORT,
    CLASS,
    Field,
    ArrayField,
    Constructor,
    Method,
    ParField
}