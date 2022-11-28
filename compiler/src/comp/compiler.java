package comp;

import gen.ToorlaLexer;
import gen.ToorlaListener;
import gen.ToorlaParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


import java.io.IOException;

public class compiler {
    public static void main(String[]args) throws IOException{

        CharStream stream= CharStreams.fromFileName("code.trl");
        ToorlaLexer lexer=new ToorlaLexer(stream);
        TokenStream tokens=new CommonTokenStream(lexer);
        ToorlaParser parser=new ToorlaParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree=parser.program();
        ParseTreeWalker walker=new ParseTreeWalker();
        ToorlaListener listener=new ProgramPrinter();
        walker.walk(listener,tree);
    }
}
