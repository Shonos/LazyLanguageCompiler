
package lazylanguagecompiler;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Parser   {
    Token tokToParse = new Token("","", "");
    Token pasTok = new Token("","","");
    public static Scanner sc;
    
    
    ParseTreeNode START_NODE = new ParseTreeNode(new Token("VARIABLE","","START"),null,null);
    ParseTreeNode DECLARATIONS_NODE= new ParseTreeNode(new Token("VARIABLE","","DECLARATIONS"),null,null);  
    ParseTreeNode BODY_NODE= new ParseTreeNode(new Token("VARIABLE","","BODY"),null,null); 
    ParseTreeNode currNode;
    ParseTree theTree = new ParseTree(START_NODE);
    
    Boolean error = false;

    public Parser() throws IOException{
        sc = new Scanner();
        ADVANCE();
        START();
        
    }

    
    public void ADVANCE(){
        pasTok = tokToParse;
        tokToParse = sc.DFA(pasTok);
        if(tokToParse.tokenType.equals("End of Line"))
            ADVANCE();
         
    }
    
    public void MATCH(Token expectedToken){
        if(tokToParse == expectedToken){
            //System.out.println("MATCHED :" + expectedToken);
            ADVANCE();
           // System.out.println("next token: " + tokToParse.toString());
        }
        else {
            ParseError("Expected " + expectedToken.toString() + "; Token is:  " + tokToParse.toString() + "; Line: " + sc.getcurrentline());
            ADVANCE();
            error = true;
        }
    }
    
    public void MATCH_ID(){
        if(tokToParse.tokenType.equals("ID")){
            //System.out.println("MATCHED ID: " + tokToParse.toString());
            ADVANCE();
            //System.out.println("next token: " + tokToParse.toString());
        }
        else {
            ParseError("Expected ID ; Token is:  " + tokToParse.toString() + "; Line: " + sc.getcurrentline());
            ADVANCE();
            error = true;
        }
    }
    
    public void MATCH_C(){
        if(tokToParse.tokenType.contains("CONSTANT") || tokToParse.dataType.equals("[BOOLEAN_VALUE]")){
            //System.out.println("MATCHED CONSTANT : " + tokToParse.toString());
            ADVANCE();
            //System.out.println("next token: " + tokToParse.toString());
        }
        else {
            ParseError("Expected CONSTANT ; Token is:  " + tokToParse.toString() + "; Line: " + sc.getcurrentline());
            ADVANCE();
            error = true;System.exit(0);
        }
    }
    
    
    
    

    public void START(){
        
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("<init_start>"));
        START_NODE.setFirstChild(currNode); START_NODE = START_NODE.firstChild;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("@"));
        START_NODE.setNextSibling(currNode); START_NODE = START_NODE.nextSibling;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH_ID();
        START_NODE.setNextSibling(currNode); START_NODE = START_NODE.nextSibling;
        //dec
        START_NODE.setNextSibling(DECLARATIONS_NODE); 
        
        //System.out.println(theTree.printParseTree());
        DECLARATIONS();  

    }
    
    public void DECLARATIONS(){
        while(!tokToParse.toString().equals("[VARIABLE_END]")){
            currNode = new ParseTreeNode(tokToParse,null,null);
            if(tokToParse.toString().equals("[DATA_TYPE_INTEGER]")){
                MATCH(sc.TokenTable.get("#"));
            }
            else if(tokToParse.toString().equals("[DATA_TYPE_DOUBLE]")){
                MATCH(sc.TokenTable.get("#d"));
            }
            else if(tokToParse.toString().equals("[DATA_TYPE_CHARACTER]")){
                MATCH(sc.TokenTable.get("$c"));
            }
            else if(tokToParse.toString().equals("[DATA_TYPE_STRING]")){
                MATCH(sc.TokenTable.get("$"));
            }
            else if(tokToParse.toString().equals("[DATA_TYPE_BOOLEAN]")){
                MATCH(sc.TokenTable.get("?"));
            }
            else {
                ParseError("Expecting DATA_TYPE on Line: " + sc.getcurrentline());
                System.exit(0);
                
            }

            DECLARATIONS_NODE.setFirstChild(currNode); DECLARATIONS_NODE = DECLARATIONS_NODE.firstChild; 

            currNode = new ParseTreeNode(tokToParse,null,null);
            MATCH_ID();
            DECLARATIONS_NODE.setNextSibling(currNode); DECLARATIONS_NODE = DECLARATIONS_NODE.nextSibling;

            currNode = new ParseTreeNode (tokToParse,null,null);
            MATCH(sc.TokenTable.get(":="));
            DECLARATIONS_NODE.setNextSibling(currNode); DECLARATIONS_NODE = DECLARATIONS_NODE.nextSibling;

            currNode = new ParseTreeNode (tokToParse,null,null);
            MATCH_C();
            DECLARATIONS_NODE.setNextSibling(currNode); DECLARATIONS_NODE = DECLARATIONS_NODE.nextSibling;
            
            if(tokToParse.toString().equals("[DATA_TYPE_FINAL]")){
                currNode = new ParseTreeNode (tokToParse,null,null);
                MATCH(sc.TokenTable.get("FINAL"));
                DECLARATIONS_NODE.setNextSibling(currNode); DECLARATIONS_NODE = DECLARATIONS_NODE.nextSibling;
            }
            
            DECLARATIONS_NODE.setNextSibling(new ParseTreeNode(new Token("VARIABLE","","DECLARATIONS"),null,null)); DECLARATIONS_NODE = DECLARATIONS_NODE.nextSibling;
         }  
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("<init_end>"));
        DECLARATIONS_NODE.setNextSibling(currNode);  DECLARATIONS_NODE = DECLARATIONS_NODE.nextSibling;
        

        DECLARATIONS_NODE.setNextSibling(BODY_NODE);
        
        BODY();
        
        
    }
    
    public void BODY(){
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("<lazy_wake>"));
        BODY_NODE.setFirstChild(currNode); BODY_NODE = BODY_NODE.firstChild;
        
        BODY_NODE.setNextSibling(STATEMENTS("[BODY_END]").root); BODY_NODE = BODY_NODE.nextSibling;
          
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("<lazy_sleep>"));
        BODY_NODE.setNextSibling(currNode); BODY_NODE = BODY_NODE.nextSibling;
    }
    
    public ParseTree STATEMENTS(String stopper){
        ParseTree STATEMENTS_TREE = new ParseTree();
        ParseTreeNode STATEMENTS_NODE = new ParseTreeNode(new Token("VARIABLE", "", "STATEMENTS"),null,null);
        STATEMENTS_TREE.setRoot(STATEMENTS_NODE);
        while(!tokToParse.toString().equals(stopper)){
            if(tokToParse.toString().equals("[IF_START]")){
                STATEMENTS_NODE.setFirstChild(IF_ELSE().root); STATEMENTS_NODE = STATEMENTS_NODE.firstChild;    
            }
            else if(tokToParse.toString().equals("[OUTPUT_VALUE]")){
                STATEMENTS_NODE.setFirstChild(OUTPUT().root); STATEMENTS_NODE = STATEMENTS_NODE.firstChild;
            }
            else if(tokToParse.toString().equals("[LOOP_START]")){
                STATEMENTS_NODE.setFirstChild(LOOP().root); STATEMENTS_NODE = STATEMENTS_NODE.firstChild;
            }
            else if(tokToParse.tokenType.equals("ID")){
                STATEMENTS_NODE.setFirstChild(ASSIGNMENT().root); STATEMENTS_NODE = STATEMENTS_NODE.firstChild;
            }
            else{
                ParseError("Start of statement not allowed on Line: " + sc.getcurrentline());
                ADVANCE();
                error = true;  
            }
            STATEMENTS_NODE.setNextSibling(new ParseTreeNode(new Token("VARIABLE","","STATEMENTS"),null,null)); STATEMENTS_NODE = STATEMENTS_NODE.nextSibling;
        }
        
        return STATEMENTS_TREE;
    }
    public ParseTree ASSIGNMENT(){
        ParseTreeNode ASSIGNMENT_NODE = new ParseTreeNode(new Token("VARIABLE","","ASSIGNMENT"),null,null);
        ParseTree ASSIGNMENT_TREE = new ParseTree();
        ASSIGNMENT_TREE.setRoot(ASSIGNMENT_NODE);
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH_ID();
        ASSIGNMENT_NODE.setFirstChild(currNode); ASSIGNMENT_NODE = ASSIGNMENT_NODE.firstChild;
        
        
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        
        if(tokToParse.toString().equals("[ARITHMETIC_OPERATOR_INCREMENT]")){
            MATCH(sc.TokenTable.get("++"));
            ASSIGNMENT_NODE.setNextSibling(currNode); ASSIGNMENT_NODE = ASSIGNMENT_NODE.nextSibling;
            return ASSIGNMENT_TREE;
        }

        if(tokToParse.toString().equals("[ARITHMETIC_OPERATOR_DECREMENT]")){
            MATCH(sc.TokenTable.get("--"));
            ASSIGNMENT_NODE.setNextSibling(currNode); ASSIGNMENT_NODE = ASSIGNMENT_NODE.nextSibling;
            return ASSIGNMENT_TREE;
        }
        
        MATCH(sc.TokenTable.get("="));
        ASSIGNMENT_NODE.setNextSibling(currNode); ASSIGNMENT_NODE = ASSIGNMENT_NODE.nextSibling;
        
        if(tokToParse.toString().equals("[CONVERTER]")){
            currNode = new ParseTreeNode(tokToParse,null,null);
            MATCH(sc.TokenTable.get("convert"));
            ASSIGNMENT_NODE.setNextSibling(currNode); ASSIGNMENT_NODE = ASSIGNMENT_NODE.nextSibling;
            
            currNode = new ParseTreeNode(tokToParse,null,null);
            MATCH_ID();
            ASSIGNMENT_NODE.setNextSibling(currNode); ASSIGNMENT_NODE = ASSIGNMENT_NODE.nextSibling;
            
            if(tokToParse.toString().equals("[NOISE_WORD]")){
                //currNode = new ParseTreeNode(tokToParse,null,null);
                MATCH(sc.TokenTable.get("to"));
                //ASSIGNMENT_NODE.setNextSibling(currNode); ASSIGNMENT_NODE = ASSIGNMENT_NODE.nextSibling;
            }
            currNode = new ParseTreeNode(tokToParse,null,null);
            if(tokToParse.toString().equals("[DATA_TYPE_INTEGER]")){
                MATCH(sc.TokenTable.get("#"));
            }
            else if(tokToParse.toString().equals("[DATA_TYPE_DOUBLE]")){
                MATCH(sc.TokenTable.get("#d"));
            }
            else if(tokToParse.toString().equals("[DATA_TYPE_CHARACTER]")){
                MATCH(sc.TokenTable.get("$c"));
            }
            else if(tokToParse.toString().equals("[DATA_TYPE_STRING]")){
                MATCH(sc.TokenTable.get("$"));
            }
            else if(tokToParse.toString().equals("[DATA_TYPE_BOOLEAN]")){
                MATCH(sc.TokenTable.get("?"));
            }
            else {
                ParseError("Expecting DATA_TYPE on Line: " + sc.getcurrentline());
                ADVANCE();
                error = true;
            }
            ASSIGNMENT_NODE.setNextSibling(currNode); ASSIGNMENT_NODE = ASSIGNMENT_NODE.nextSibling;
            return ASSIGNMENT_TREE;
        }
        
        ASSIGNMENT_NODE.setNextSibling(CONSTANTS().root); ASSIGNMENT_NODE = ASSIGNMENT_NODE.nextSibling;
        
        return ASSIGNMENT_TREE;
    }
    
    public ParseTree CONSTANTS(){
        ParseTreeNode CONSTANTS_NODE = new ParseTreeNode(new Token("VARIABLE","","CONSTANTS"),null,null);
        ParseTree CONSTANTS_TREE = new ParseTree();
        CONSTANTS_TREE.setRoot(CONSTANTS_NODE);
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        if(tokToParse.dataType.equals("char")){
            MATCH_C();
            CONSTANTS_NODE.setFirstChild(currNode); CONSTANTS_NODE = CONSTANTS_NODE.firstChild;
        }
        else if(tokToParse.dataType.equals("String")){
            MATCH_C();
            CONSTANTS_NODE.setFirstChild(currNode); CONSTANTS_NODE = CONSTANTS_NODE.firstChild;
        } 
        else if(tokToParse.dataType.equals("[BOOLEAN_VALUE]") || tokToParse.toString().contains("ID") || 
                tokToParse.toString().contains("CONSTANT") || tokToParse.toString().equals("[NEGATIVE_VALUE]") ||
                tokToParse.toString().equals("[LOGICAL_OPERATOR_NOT]") ||
                tokToParse.toString().equals("[OPEN_PARENTHESIS]") ){
            
            CONSTANTS_NODE.setFirstChild(EXPR().root); CONSTANTS_NODE = CONSTANTS_NODE.firstChild;
        }
        else if(tokToParse.toString().equals("[SCAN_VALUE]")){
            MATCH(sc.TokenTable.get("take"));
            CONSTANTS_NODE.setFirstChild(currNode); CONSTANTS_NODE = CONSTANTS_NODE.firstChild;
        }
        return CONSTANTS_TREE;
    }
    
    public ParseTree EXPR(){
        ParseTreeNode EXPR_NODE = new ParseTreeNode(new Token("Variable","","EXPR"),null,null);
        ParseTree EXPR_TREE = new ParseTree();
        EXPR_TREE.setRoot(EXPR_NODE);
        
        EXPR_NODE.setFirstChild(T().root); EXPR_NODE = EXPR_NODE.firstChild;
        
        EXPR_NODE.setNextSibling(EXPR_PRIME().root); EXPR_NODE = EXPR_NODE.nextSibling;
        
        return EXPR_TREE;
    }
    
    public ParseTree T(){
        ParseTreeNode T_NODE = new ParseTreeNode(new Token("Variable","","T_EXPR"),null,null);
        ParseTree T_TREE = new ParseTree();
        T_TREE.setRoot(T_NODE);
        
        T_NODE.setFirstChild(F().root); T_NODE = T_NODE.firstChild;
        
        T_NODE.setNextSibling(T_PRIME().root); T_NODE = T_NODE.nextSibling;
        
        return T_TREE;
    }
    
    public ParseTree EXPR_PRIME(){
        ParseTreeNode EXPR_PRIME_NODE = new ParseTreeNode(new Token("Variable","","EXPR_PRIME"),null,null);
        ParseTree EXPR_PRIME_TREE = new ParseTree();
        EXPR_PRIME_TREE.setRoot(EXPR_PRIME_NODE);
        
        currNode =  new ParseTreeNode(tokToParse,null,null);
        if(tokToParse.toString().equals("[ARITHMETIC_OPERATOR_ADD]")){
            MATCH(sc.TokenTable.get("+"));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        else if(tokToParse.toString().equals("[ARITHMETIC_OPERATOR_SUB]")){
            MATCH(sc.TokenTable.get("-"));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_GREATER]")){
            MATCH(sc.TokenTable.get(">"));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        
        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_LESS]")){
            MATCH(sc.TokenTable.get("<"));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_EQUAL]")){
            MATCH(sc.TokenTable.get("=="));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_GREATER_EQUAL]")){
            MATCH(sc.TokenTable.get(">="));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_LESS_EQUAL]")){
            MATCH(sc.TokenTable.get("<="));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_NOT_EQUAL]")){
            MATCH(sc.TokenTable.get("!="));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        else if(tokToParse.toString().equals("[LOGICAL_OPERATOR_AND]")){
            MATCH(sc.TokenTable.get("&&"));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        else if(tokToParse.toString().equals("[LOGICAL_OPERATOR_OR]")){
            MATCH(sc.TokenTable.get("||"));
            EXPR_PRIME_NODE.setFirstChild(currNode); EXPR_PRIME_NODE = EXPR_PRIME_NODE.firstChild;
            
            EXPR_PRIME_NODE.setNextSibling(T().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
            
            EXPR_PRIME_NODE.setNextSibling(EXPR_PRIME().root); EXPR_PRIME_NODE = EXPR_PRIME_NODE.nextSibling;
        }
        
        
        
        return EXPR_PRIME_TREE;
        
    }
    
    public ParseTree T_PRIME(){
        ParseTreeNode T_PRIME_NODE = new ParseTreeNode(new Token("Variable","","T_PRIME"),null,null);
        ParseTree T_PRIME_TREE = new ParseTree();
        T_PRIME_TREE.setRoot(T_PRIME_NODE);
        
        currNode =  new ParseTreeNode(tokToParse,null,null);
        if(tokToParse.toString().equals("[ARITHMETIC_OPERATOR_MULTIPLY]")){
            MATCH(sc.TokenTable.get("*"));
            T_PRIME_NODE.setFirstChild(currNode); T_PRIME_NODE = T_PRIME_NODE.firstChild;
            
            T_PRIME_NODE.setNextSibling(F().root); T_PRIME_NODE = T_PRIME_NODE.nextSibling;
            
            T_PRIME_NODE.setNextSibling(T_PRIME().root); T_PRIME_NODE = T_PRIME_NODE.nextSibling;
        }
        else if(tokToParse.toString().equals("[ARITHMETIC_OPERATOR_DIVIDE]")){
            MATCH(sc.TokenTable.get("/"));
            T_PRIME_NODE.setFirstChild(currNode); T_PRIME_NODE = T_PRIME_NODE.firstChild;
            
            T_PRIME_NODE.setNextSibling(F().root); T_PRIME_NODE = T_PRIME_NODE.nextSibling;
            
            T_PRIME_NODE.setNextSibling(T().root); T_PRIME_NODE = T_PRIME_NODE.nextSibling;
        }
        
        return T_PRIME_TREE;
        
    }
    
    public ParseTree F(){
        ParseTreeNode F_NODE = new ParseTreeNode(new Token("Variable","","T_PRIME"),null,null);
        ParseTree F_TREE = new ParseTree();
        F_TREE.setRoot(F_NODE);
        
         if(tokToParse.toString().equals("[NEGATIVE_VALUE]") || tokToParse.toString().equals("[LOGICAL_OPERATOR_NOT]") ){
             currNode =  new ParseTreeNode(tokToParse,null,null);
             if(tokToParse.toString().equals("[NEGATIVE_VALUE]"))
                MATCH(sc.TokenTable.get("-n"));
             else
                MATCH(sc.TokenTable.get("!"));
             F_NODE.setFirstChild(currNode); F_NODE = F_NODE.firstChild;
             if(tokToParse.dataType.equals("[BOOLEAN_VALUE]") || tokToParse.tokenType.equals("ID") || tokToParse.tokenType.contains("CONSTANT")){
                 F_NODE.setNextSibling(ID_EXPR().root); F_NODE = F_NODE.nextSibling;
             }
             else if(tokToParse.toString().equals("[OPEN_PARENTHESIS]")){
                 currNode =  new ParseTreeNode(tokToParse,null,null);
                 MATCH(sc.TokenTable.get("("));
                 F_NODE.setNextSibling(currNode); F_NODE = F_NODE.nextSibling;
                 
                 F_NODE.setNextSibling(EXPR().root); F_NODE = F_NODE.nextSibling;

                 currNode =  new ParseTreeNode(tokToParse,null,null);
                 MATCH(sc.TokenTable.get(")"));
                 F_NODE.setNextSibling(currNode); F_NODE = F_NODE.nextSibling;
             }
             
         }
         
         if(tokToParse.dataType.equals("[BOOLEAN_VALUE]") || tokToParse.tokenType.equals("ID") || tokToParse.tokenType.contains("CONSTANT")){
                 F_NODE.setFirstChild(ID_EXPR().root); F_NODE = F_NODE.firstChild;
             }
             else if(tokToParse.toString().equals("[OPEN_PARENTHESIS]")){
                 currNode =  new ParseTreeNode(tokToParse,null,null);
                 F_NODE.setFirstChild(currNode); F_NODE = F_NODE.firstChild;
                 MATCH(sc.TokenTable.get("("));
                 
                 F_NODE.setNextSibling(EXPR().root); F_NODE = F_NODE.nextSibling;

                 currNode =  new ParseTreeNode(tokToParse,null,null);
                 MATCH(sc.TokenTable.get(")"));
                 F_NODE.setNextSibling(currNode); F_NODE = F_NODE.nextSibling;
             }
             
 
        return F_TREE;
        
    }
    
    public ParseTree ID_EXPR(){
        ParseTreeNode ID_EXPR_NODE = new ParseTreeNode(new Token("Variable","","ID_EXPR"),null,null);
        ParseTree ID_EXPR_TREE = new ParseTree();
        ID_EXPR_TREE.setRoot(ID_EXPR_NODE);
        
//        if(tokToParse.toString().equals("[NEGATIVE_VALUE]")){
//            currNode = new ParseTreeNode(tokToParse,null,null);
//            MATCH(sc.TokenTable.get("-n"));
//            ID_EXPR_NODE.setFirstChild(currNode); ID_EXPR_NODE = ID_EXPR_NODE.firstChild;
//            
//            currNode = new ParseTreeNode(tokToParse,null,null);
//            if(tokToParse.tokenType.equals("ID")){
//               MATCH_ID();
//               ID_EXPR_NODE.setNextSibling(currNode); ID_EXPR_NODE = ID_EXPR_NODE.nextSibling;
//            }
//            else if(tokToParse.tokenType.contains("CONSTANT") || tokToParse.dataType.equals("[BOOLEAN_VALUE]")){
//               MATCH_C();
//               ID_EXPR_NODE.setNextSibling(currNode); ID_EXPR_NODE = ID_EXPR_NODE.nextSibling;
//            }
//            return ID_EXPR_TREE;
//        }
//        
        currNode = new ParseTreeNode(tokToParse,null,null);
        if(tokToParse.tokenType.equals("ID")){
            MATCH_ID();
            ID_EXPR_NODE.setFirstChild(currNode); ID_EXPR_NODE = ID_EXPR_NODE.firstChild;
        }
        else if(tokToParse.tokenType.contains("CONSTANT") || tokToParse.dataType.equals("[BOOLEAN_VALUE]")){
            MATCH_C();
            ID_EXPR_NODE.setFirstChild(currNode); ID_EXPR_NODE = ID_EXPR_NODE.firstChild;
        }
        return ID_EXPR_TREE;
    }
    
    
    
    
    public ParseTree LOOP(){
        ParseTreeNode LOOP_NODE = new ParseTreeNode(new Token("VARIABLE","","LOOP"),null,null);
        ParseTree LOOP_TREE = new ParseTree();
        LOOP_TREE.setRoot(LOOP_NODE);
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("do"));
        LOOP_NODE.setFirstChild(currNode); LOOP_NODE = LOOP_NODE.firstChild;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("{"));
        LOOP_NODE.setNextSibling(currNode); LOOP_NODE = LOOP_NODE.nextSibling;
        
        LOOP_NODE.setNextSibling(STATEMENTS("[BLOCK_END]").root); LOOP_NODE = LOOP_NODE.nextSibling;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("}"));
        LOOP_NODE.setNextSibling(currNode); LOOP_NODE = LOOP_NODE.nextSibling;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("while"));
        LOOP_NODE.setNextSibling(currNode); LOOP_NODE = LOOP_NODE.nextSibling;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("("));
        LOOP_NODE.setNextSibling(currNode); LOOP_NODE = LOOP_NODE.nextSibling;
        
        LOOP_NODE.setNextSibling(CONSTANTS().root); LOOP_NODE = LOOP_NODE.nextSibling;
        
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get(")"));
        LOOP_NODE.setNextSibling(currNode); LOOP_NODE = LOOP_NODE.nextSibling;
        
        return LOOP_TREE;
        
    }
    
    public ParseTree OUTPUT(){
        ParseTreeNode OUTPUT_NODE = new ParseTreeNode(new Token("VARIABLE","","OUTPUT"),null,null);
        ParseTree OUTPUT_TREE = new ParseTree();
        OUTPUT_TREE.setRoot(OUTPUT_NODE);
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("out"));
        OUTPUT_NODE.setFirstChild(currNode); OUTPUT_NODE = OUTPUT_NODE.firstChild;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("("));
        OUTPUT_NODE.setNextSibling(currNode); OUTPUT_NODE = OUTPUT_NODE.nextSibling;
        
        
        
//        currNode = new ParseTreeNode(tokToParse,null,null);
//        if(tokToParse.tokenType.equals("ID")){
//            MATCH_ID();
//        }
//        else if(tokToParse.tokenType.contains("CONSTANT")){
//            MATCH_C();
//        }
//        else{
//            ParseError("Can only output CONSTANTS and IDs on Line: " + sc.getcurrentline());
//            System.exit(0);
//        }
        OUTPUT_NODE.setNextSibling(CONSTANTS().root); OUTPUT_NODE = OUTPUT_NODE.nextSibling;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get(")"));
        OUTPUT_NODE.setNextSibling(currNode); //OUTPUT_NODE = OUTPUT_NODE.nextSibling;
        return OUTPUT_TREE;
        
        
    }
    
    public ParseTree IF_ELSE(){
        ParseTreeNode IF_ELSE_NODE = new ParseTreeNode(new Token("VARIABLE","","IF_ELSE"),null,null);
        ParseTree IF_ELSE_TREE = new ParseTree();
        IF_ELSE_TREE.setRoot(IF_ELSE_NODE);
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("if"));
        IF_ELSE_NODE.setFirstChild(currNode); IF_ELSE_NODE = IF_ELSE_NODE.firstChild;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("("));
        IF_ELSE_NODE.setNextSibling(currNode); IF_ELSE_NODE = IF_ELSE_NODE.nextSibling;
        
        
        IF_ELSE_NODE.setNextSibling(CONSTANTS().root); IF_ELSE_NODE = IF_ELSE_NODE.nextSibling;
        
       
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get(")"));
        IF_ELSE_NODE.setNextSibling(currNode); IF_ELSE_NODE = IF_ELSE_NODE.nextSibling;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("{"));
        IF_ELSE_NODE.setNextSibling(currNode); IF_ELSE_NODE = IF_ELSE_NODE.nextSibling;

        IF_ELSE_NODE.setNextSibling(STATEMENTS("[BLOCK_END]").root); IF_ELSE_NODE = IF_ELSE_NODE.nextSibling;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("}"));
        IF_ELSE_NODE.setNextSibling(currNode) ;  IF_ELSE_NODE = IF_ELSE_NODE.nextSibling;
        
        IF_ELSE_NODE.setNextSibling(ELSES().root); IF_ELSE_NODE = IF_ELSE_NODE.nextSibling;
        
        return IF_ELSE_TREE;
    }
    public ParseTree ELSES(){
        ParseTree ELSES_TREE= new ParseTree();
        ParseTreeNode ELSES_NODE = new ParseTreeNode(new Token("VARIABLE","","ELSES"),null,null);
        ELSES_TREE.setRoot(ELSES_NODE);
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        if(tokToParse.toString().equals("[ELSE_START]")){
            MATCH(sc.TokenTable.get("else"));
            ELSES_NODE.setFirstChild(currNode); ELSES_NODE = ELSES_NODE.firstChild;
            if(tokToParse.toString().equals("[BLOCK_START]")){
                ELSES_NODE.setNextSibling(ELSE().root); ELSES_NODE = ELSES_NODE.nextSibling;
            }
            else if(tokToParse.toString().equals("[IF_START]")){
                ELSES_NODE.setNextSibling(IF_ELSE().root); ELSES_NODE = ELSES_NODE.nextSibling;
            }
        }
        
        return ELSES_TREE;
    } 
    
    public ParseTree ELSE(){
        ParseTree ELSE_TREE = new ParseTree();
        ParseTreeNode ELSE_NODE = new ParseTreeNode(new Token("VARIABLE","","ELSE"),null,null);
        ELSE_TREE.setRoot(ELSE_NODE);
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("{"));
        ELSE_NODE.setFirstChild(currNode); ELSE_NODE = ELSE_NODE.firstChild;
        
        ELSE_NODE.setNextSibling(STATEMENTS("[BLOCK_END]").root); ELSE_NODE = ELSE_NODE.nextSibling;
        
        currNode = new ParseTreeNode(tokToParse,null,null);
        MATCH(sc.TokenTable.get("}"));
        ELSE_NODE.setNextSibling(currNode) ;  ELSE_NODE = ELSE_NODE.nextSibling;
        
        return ELSE_TREE;

        
    }
    
    
//    public ParseTree CONDITIONS(){
//        ParseTree CONDITIONS_TREE = new ParseTree();
//        ParseTreeNode CONDITIONS_NODE = new ParseTreeNode(new Token("VARIABLE","","CONDITIONS"),null,null);
//        CONDITIONS_TREE.setRoot(CONDITIONS_NODE);
//        currNode = new ParseTreeNode(tokToParse,null,null);
//        if(tokToParse.tokenType.equals("ID")){
//            MATCH_ID();
//        }
//        else if(tokToParse.tokenType.contains("CONSTANT")){
//            MATCH_C();
//        }
//        CONDITIONS_NODE.setFirstChild(currNode); CONDITIONS_NODE = CONDITIONS_NODE.firstChild;
//        
//        currNode = new ParseTreeNode(tokToParse,null,null);
//        if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_GREATER]")){
//            MATCH(sc.TokenTable.get(">"));
//        }
//        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_LESS]")){
//            MATCH(sc.TokenTable.get("<"));
//        }
//        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_EQUAL]")){
//            MATCH(sc.TokenTable.get("=="));
//        }
//        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_NOT_EQUAL]")){
//            MATCH(sc.TokenTable.get("!="));
//        }
//        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_GREATER_EQUAL]")){
//            MATCH(sc.TokenTable.get(">="));
//        }
//        else if(tokToParse.toString().equals("[RELATIONAL_OPERATOR_LESS_EQUAL]")){
//            MATCH(sc.TokenTable.get("<="));
//        }
//        CONDITIONS_NODE.setNextSibling(currNode); CONDITIONS_NODE = CONDITIONS_NODE.nextSibling;
//        
//        currNode = new ParseTreeNode(tokToParse,null,null);
//        if(tokToParse.tokenType.equals("ID")){
//            MATCH_ID();
//        }
//        else if(tokToParse.tokenType.contains("CONSTANT") || tokToParse.dataType.equals("[BOOLEAN_VALUE]")){
//            MATCH_C();
//        }
//        CONDITIONS_NODE.setNextSibling(currNode); CONDITIONS_NODE = CONDITIONS_NODE.nextSibling;
//        
//        currNode = new ParseTreeNode(tokToParse,null,null);
//        if(tokToParse.toString().equals("[LOGICAL_OPERATOR_OR]")){
//            MATCH(sc.TokenTable.get("||"));           
//            CONDITIONS_NODE.setNextSibling(currNode); CONDITIONS_NODE = CONDITIONS_NODE.nextSibling;
//            
//            
//            CONDITIONS_NODE.setNextSibling(CONDITIONS().root); CONDITIONS_NODE = CONDITIONS_NODE.nextSibling;
//        } 
//        
//        if(tokToParse.toString().equals("[LOGICAL_OPERATOR_AND]")){
//            MATCH(sc.TokenTable.get("&&"));  
//            CONDITIONS_NODE.setNextSibling(currNode); CONDITIONS_NODE = CONDITIONS_NODE.nextSibling;
//             
//            CONDITIONS_NODE.setNextSibling(CONDITIONS().root); CONDITIONS_NODE = CONDITIONS_NODE.nextSibling;
//        }
//        
//        return CONDITIONS_TREE;   
//    }
   

    public void ParseError(String err){
        System.out.println("Error in Parsing, message: " + err);
    }
      
      public void printTree() {
          System.out.println(theTree.printParseTree());
      }

    public static void main(String[] args) throws IOException{
        Parser pp = new Parser();
        
        if(!pp.error){
           System.out.println("Parse success!");
           System.out.println("###########################  Lazy Parse Tree ####################################"); 
           pp.printTree();
        }
        
        
        
    }
       
}