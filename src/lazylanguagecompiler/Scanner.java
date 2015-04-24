/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lazylanguagecompiler;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author Shaun
 */
public class Scanner {
    private String currentLine, wholesrc = "";
    private String splitsrc[];
    private int ptr, state = 0, currentline = 0, srclength, splitsize;
    private String posTok= "";
    private Boolean initDone = false;
    FileReader fp = new FileReader("E:\\LazyLang\\START.txt");
    BufferedReader buff = new BufferedReader(fp);
    public static HashMap<String, Token> TokenTable;
    
    Scanner() throws IOException {
        TokenTable = new HashMap<>();
        
        TokenTable.put("||", new Token("RESERVED", "[LOGICAL_OPERATOR]", "[LOGICAL_OPERATOR_OR]"));
        TokenTable.put("&&", new Token("RESERVED", "[LOGICAL_OPERATOR]", "[LOGICAL_OPERATOR_AND]"));
        TokenTable.put("!", new Token("RESERVED", "[LOGICAL_OPERATOR]", "[LOGICAL_OPERATOR_NOT]"));
        TokenTable.put("+", new Token("RESERVED", "[ARITHMETIC_OPERATOR]", "[ARITHMETIC_OPERATOR_ADD]"));
        TokenTable.put("-", new Token("RESERVED", "[ARITHMETIC_OPERATOR]", "[ARITHMETIC_OPERATOR_SUB]"));
        TokenTable.put("/", new Token("RESERVED", "[ARITHMETIC_OPERATOR]", "[ARITHMETIC_OPERATOR_DIVIDE]"));
        TokenTable.put("*", new Token("RESERVED", "[ARITHMETIC_OPERATOR]", "[ARITHMETIC_OPERATOR_MULTIPLY]"));
        TokenTable.put("++", new Token("RESERVED", "[ARITHMETIC_OPERATOR]", "[ARITHMETIC_OPERATOR_INCREMENT]"));
        TokenTable.put("--", new Token("RESERVED", "[ARITHMETIC_OPERATOR]", "[ARITHMETIC_OPERATOR_DECREMENT]"));
        TokenTable.put("-n", new Token("RESERVED", "[ARITHMETIC_OPERATOR]", "[NEGATIVE_VALUE]"));
        TokenTable.put("#", new Token("RESERVED", "[DATA_TYPE]", "[DATA_TYPE_INTEGER]"));
        TokenTable.put("#d", new Token("RESERVED", "[DATA_TYPE]", "[DATA_TYPE_DOUBLE]"));
        TokenTable.put("$", new Token("RESERVED", "[DATA_TYPE]", "[DATA_TYPE_STRING]"));
        TokenTable.put("$c", new Token("RESERVED", "[DATA_TYPE]", "[DATA_TYPE_CHARACTER]"));
        TokenTable.put("?", new Token("RESERVED", "[DATA_TYPE]", "[DATA_TYPE_BOOLEAN]"));
        TokenTable.put("<init_start>", new Token("RESERVED", "[BLOCK]", "[VARIABLE_START]"));
        TokenTable.put("<init_end>", new Token("RESERVED", "[BLOCK]", "[VARIABLE_END]"));
        TokenTable.put("<lazy_wake>", new Token("RESERVED", "[BLOCK]", "[BODY_START]"));
        TokenTable.put("<lazy_sleep>", new Token("RESERVED", "[BLOCK]", "[BODY_END]"));
        TokenTable.put("{", new Token("RESERVED", "[BLOCK]", "[BLOCK_START]"));
        TokenTable.put("}", new Token("RESERVED", "[BLOCK]", "[BLOCK_END]"));
        TokenTable.put("(", new Token("RESERVED", "[OPEN_PARENTHESIS]", "[OPEN_PARENTHESIS]"));
        TokenTable.put(")", new Token("RESERVED", "[CLOSE_PARENTHESIS]", "[CLOSE_PARENTHESIS]"));
        TokenTable.put("if", new Token("RESERVED", "[RESERVED]", "[IF_START]"));
        TokenTable.put("else", new Token("RESERVED", "[RESERVED]", "[ELSE_START]"));
        TokenTable.put("take", new Token("RESERVED", "[RESERVED]", "[SCAN_VALUE]"));
        TokenTable.put("to", new Token("RESERVED", "[RESERVED]", "[NOISE_WORD]"));
        TokenTable.put("out", new Token("RESERVED", "[RESERVED]", "[OUTPUT_VALUE]"));
        TokenTable.put("do", new Token("RESERVED", "[RESERVED]", "[LOOP_START]"));
        TokenTable.put("while", new Token("RESERVED", "[RESERVED]", "[LOOP_CONDITION]"));
        TokenTable.put("T", new Token("RESERVED", "[BOOLEAN_VALUE]", "[BOOLEAN_VALUE_TRUE]"));
        TokenTable.put("F", new Token("RESERVED", "[BOOLEAN_VALUE]", "[BOOLEAN_VALUE_FALSE]"));
        TokenTable.put(":=", new Token("RESERVED", "[ASSIGNMENT_OPERATOR]", "[ASSIGNMENT_OPERATOR]"));
        TokenTable.put("=", new Token("RESERVED", "[ASSIGNMENT_OPERATOR]", "[ASSIGNMENT_OPERATOR_EXISTING]"));
        TokenTable.put("==", new Token("RESERVED", "[RELATIONAL_OPERATOR]", "[RELATIONAL_OPERATOR_EQUAL]"));
        TokenTable.put(">", new Token("RESERVED", "[RELATIONAL_OPERATOR]", "[RELATIONAL_OPERATOR_GREATER]"));
        TokenTable.put("<", new Token("RESERVED", "[RELATIONAL_OPERATOR]", "[RELATIONAL_OPERATOR_LESS]"));
        TokenTable.put(">=", new Token("RESERVED", "[RELATIONAL_OPERATOR]", "[RELATIONAL_OPERATOR_GREATER_EQUAL]"));
        TokenTable.put("<=", new Token("RESERVED", "[RELATIONAL_OPERATOR]", "[RELATIONAL_OPERATOR_LESS_EQUAL]"));
        TokenTable.put("!=", new Token("RESERVED", "[RELATIONAL_OPERATOR]", "[RELATIONAL_OPERATOR_NOT_EQUAL]"));
        TokenTable.put("convert", new Token("RESERVED", "[RESERVED]", "[CONVERTER]"));
        TokenTable.put("@", new Token("RESERVED", "[RESERVED]", "[FILE_NAME]"));
        TokenTable.put("FINAL", new Token("RESERVED", "[RESERVED]", "[DATA_TYPE_FINAL]"));
        while ((currentLine = buff.readLine()) != null) {
            wholesrc = wholesrc + currentLine + "™";
        }
        wholesrc = wholesrc.trim();
        srclength = wholesrc.length();
        splitsrc = wholesrc.split("™");
        splitsize = splitsrc.length;
        for (int y = 0; y < splitsize; y++) {
            splitsrc[y] = splitsrc[y].trim() + "%";
            //System.out.println(splitsrc[y]);
        }
        splitsize = splitsrc.length;
        
    }
    
    public void closeall() throws IOException {
        buff.close();
        fp.close();
    }
    
    public int srclength() {
        return splitsize;
    }

    public int getcurrentline() {
        return currentline+1;
    }
    
    
    public Token DFA(Token pTok) {
        Token currTok = new Token();
        String firstIdentChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String contIdentChar = "_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String numbers = "0123456789";
        //String arithmetic = "+-*/";
        if(pTok.toString().equals("[VARIABLE_END]")){
            initDone = true;
        }
        
        while(true){
            char c = (splitsrc[currentline]).charAt(ptr);
            char n = ' '; if(ptr!=splitsrc[currentline].length()-1){ n =(splitsrc[currentline]).charAt(ptr+1);}
            //System.out.print("DFA STATE = " + state); System.out.println("  Current Char = " + c + " posTok = " + posTok + " next = " + n);
            
            
            switch(state){
                case 0: 
                    if(c=='<'){
                       if(ptr==0) {
                           ptr++;
                           state = 10;  
                       }
                       else if(n=='='){
                            ptr++;
                            ptr++;
                            state = 0;
                            return TokenTable.get("<=");
                       } 
                       else if(n=='!'){
                            ptr++;
                            ptr++;
                            state = 6;              
                       }
                       else{
                           ptr++;
                           state = 0;
                           return TokenTable.get("<");
                       } 
                    }
                    
                    else if(c=='>'){
                        if(n=='='){
                            ptr++;
                            ptr++;
                            state = 0;
                            return TokenTable.get(">=");  
                        }
                        else{
                           ptr++;
                           state = 0;
                           return TokenTable.get(">");
                        }
                    }
                    else if(c=='+'){
                        if(n=='+'){
                            ptr++;
                            ptr++;
                            state =0;
                            return TokenTable.get("++");
                        }
                        ptr++;
                        state =0;
                        return TokenTable.get("+");
                    }
                    else if(c=='-'){
                        if(n=='-'){
                            ptr++;
                            ptr++;
                            state =0;
                            return TokenTable.get("--");
                        }
                        ptr++;
                        state =0;
                        if(pTok.dataType.equals("[ASSIGNMENT_OPERATOR]") || pTok.dataType.equals("[ASSIGNMENT_OPERATOR_EXISTING]") 
                                || pTok.dataType.equals("[ARITHMETIC_OPERATOR]")|| pTok.dataType.equals("[OPEN_PARENTHESIS]") ){
                            return TokenTable.get("-n");
                            
                        }
                        else {
                            return TokenTable.get("-");
                        }
                    }
                    else if(c=='*'){
                        ptr++;
                        state =0;
                        return TokenTable.get("*");
                    }
                    else if(c=='/'){
                        ptr++;
                        state =0;
                        return TokenTable.get("/");
                    }
                    else if(c=='$'){
                        ptr++;
                        state =0;
                        if(splitsrc[currentline].contains("\'"))
                            return TokenTable.get("$c");
                        else
                            return TokenTable.get("$");
                    }
                    else if(c=='?'){
                        ptr++;
                        state =0;
                        return TokenTable.get("?");
                    }
                    else if(c=='#'){
                        ptr++;
                        state =0;
                        if(splitsrc[currentline].contains("."))
                            return TokenTable.get("#d");
                        else
                            return TokenTable.get("#");
                    }
                    else if(c=='@'){
                        ptr++;
                        state =0;
                        return TokenTable.get("@");
                    }
                    else if(c==':'){
                        if(n=='='){
                            ptr++;
                            ptr++;
                            state = 0;
                            return TokenTable.get(":="); 
                        }
                        else{ state = 100;}
                    }
                    else if(c=='&'){
                        if(n=='&'){
                            ptr++;
                            ptr++;
                            state = 0;
                            return TokenTable.get("&&");
                        }
                        else{ state = 100;}
                    }
                    else if(c=='|'){
                        if(n=='|'){
                            ptr++;
                            ptr++;
                            state = 0;
                            return TokenTable.get("||");  
                        }
                        else{ state = 100;}
                    }
                    else if (c=='T'){
                        posTok = "";
                        posTok = posTok + c;
                        ptr++;
                        if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':' || n=='&'|| n=='|' ){
                            return TokenTable.get("T");
                        }
                        state = 1;
                        
                    }
                    else if (c=='F'){
                        posTok = "";
                        posTok = posTok + c;
                        ptr++;
                        if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':' || n=='&'| n=='|'  ){
                            return TokenTable.get("F");
                        }
                        state = 42;
                    }
                    else if (c=='\"'){
                        posTok = "";
                        ptr++;
                        state = 3;
                    }
                    else if (c=='\''){
                        posTok = "";
                        ptr++;
                        state = 4;
                    }
                    //FOR OTHER RESERVED WORDS POSSIBLE TO BE IDENTIFIERS
                    else if (c=='i'){
                        posTok = "";
                        posTok = posTok + c;
                        if(n=='f')
                            state = 38;
                        else
                            state = 1;
                        ptr++;
                        if(n=='%'||n==' ' || n==')'){
                            state = 0;
                            if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                            
                         }
                    }
                    
                    
                    else if (c=='e') {
                        posTok = "";
                        posTok = posTok + c;
                        state = 39;
                        ptr++;
                        if(n=='%'||n==' ' || n==')') {
                            state = 0;
                            if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                         }
                    }
                    else if (c=='t') {
                        posTok = "";
                        posTok = posTok + c;
                        state = 46;
                        ptr++;
                        if(n=='%'||n==' ' || n==')'){
                            state = 0;
                            if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                         }
                    }
                    else if (c=='d') {
                        posTok = "";
                        posTok = posTok + c;
                        state = 51;
                        ptr++;
                        if(n=='%'||n==' ' || n==')'){
                            state = 0;
                            if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                         }
                    }
                    else if (c=='o') {
                        posTok = "";
                        posTok = posTok + c;
                        state = 49;
                        ptr++;
                        if(n=='%'||n==' ' || n==')'){
                            state = 0;
                           if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                         }
                    }
                    else if (c=='w') {
                        posTok = "";
                        posTok = posTok + c;
                        state = 52;
                        ptr++;
                        if(n=='%'||n==' ' || n==')'){
                            state = 0;
                            if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                         }
                    }
                    else if (c=='c') {
                        posTok = "";
                        posTok = posTok + c;
                        state = 56;
                        ptr++;
                        if(n=='%'||n==' ' || n==')'){
                            state = 0;
                            if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                         }
                    }
                    else if (c=='('){
                        ptr++;
                        state = 0;
                        return TokenTable.get("(");
                    }
                    else if (c==')'){
                        ptr++;
                        state = 0;
                        return TokenTable.get(")");
                    }
                    else if(c=='{'){
                        ptr++;
                        state = 0;
                       return TokenTable.get("{");
                    }
                    else if(c=='}'){
                        ptr++;
                        state = 0;
                        return TokenTable.get("}");
                    }
                    else if (c=='='){
                        if(n=='='){
                            ptr++;
                            ptr++;
                            state = 0;
                            return TokenTable.get("==");
                        }
                        else{
                            ptr++;
                            state = 0;
                            return TokenTable.get("=");
                        }
                       
                    }
                    else if (c=='!'){
                        if(n=='='){
                            ptr++;
                            ptr++;
                            state = 0;
                            return TokenTable.get("!=");
                        }
                        else{
                             ptr++;
                             state = 0;
                             return TokenTable.get("!");
                        }
                        
                        
                    }
                    
                    else if (c==' ' || c== '\t'){
                        ptr++;
                        state = 0;
                    }
                    
                    else if(c=='%'){
                        ptr = 0;
                        state = 500;
                    }
                    else if(firstIdentChar.contains(""+c)){
                        posTok = "";
                        posTok = posTok +c;
                        ptr++;
                        if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ){
                            if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                        }
                        state = 1;
                    }
                    else if(numbers.contains(""+c)){
                        posTok = "";
                        posTok = posTok + c;
                        ptr++;
                        if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':' || n=='&'| n=='|' ){
                           return new Token("INTEGER_CONSTANT","","[INTEGER_CONSTANT VALUE = " + posTok + "]");
                        }
                        state = 2;        
                    }
                    else{
                        ptr++;
                        state = 100;
                    }
                    break;
                case 1:
                    if(contIdentChar.contains(""+c)){
                        posTok = posTok +c;
                        ptr++;
                        if(n=='%')
                            state = 0;
                        if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':' || n=='&'| n=='|' ){
                            state = 0;
                            if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                        }
                    }
                    else { state = 100;}
                    break;
                case 2:
                    if(numbers.contains(""+c)){
                        posTok = posTok + c;
                        ptr++;
                        if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':' || n=='&'| n=='|' ){
                           state= 0;
                           return new Token("INTEGER_CONSTANT","","[INTEGER_CONSTANT VALUE = " + posTok + "]");
                        }
                        state = 2; 
                    }
                    else if(c=='.'){
                            posTok = posTok +c;
                            ptr++;
                            state = 5;
                        }
                    else { state = 100;}
                    break;
                case 3:
                    if(c=='%'){
                        state = 0;
                        ptr=0;
                        currentline++;
                        return new Token("ERROR", "message", "Expected \" at Line " + (currentline+1) + " character " + ptr);
                    }
                    else {
                        if(c!='\"'){
                        posTok = posTok + c;
                        ptr++;  
                        }
                        else{
                        ptr++;
                        state = 0;
                        return new Token("CONSTANT","String","[LITERAL_STRING VALUE=" + posTok + "]");
                        }
                    }
                    break;
                case 4:
                    if(c=='%'){
                        currentline++;
                        state = 0;
                        ptr=0;
                        return new Token("ERROR", "message", "Expected \' at Line " + (currentline+1) + " character " + ptr);
                    }
                    else {
                        if(c!='\''){
                        posTok = posTok + c;
                        ptr++;  
                        }
                        else{
                        ptr++;
                        state = 0;
                        if (posTok.length()>1) {
                            state = 0;
                            return new Token ("ERROR", "message", "Invalid Character at Line " + (currentline+1) + " character " + ptr);
                        }
                        
                        return new Token("CONSTANT","char","[LITERAL_CHAR VALUE=" + posTok + "]");
                        }
                    }
                    break;
                case 5:
                    if(numbers.contains(""+c)){
                        posTok = posTok +c;
                        ptr++;
                        if(n=='%')
                            state =0;
                        if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|' ){
                            state = 0;
                            return new Token("CONSTANT","double","[DOUBLE_CONSTANT VALUE=" + posTok + "]");
                        }
                    }
                    else if(c=='.'){
                            ptr++;
                            state = 0;
                            return new Token ("ERROR", "message", "Invalid Float Value at Line " + (currentline+1) + " character " + ptr);
                        }
                    else { state = 100;}
                    break;
                case 6:
                    if(c=='!'){
                        state = 7;
                        ptr++;
                    }
                    else{
                        ptr=0;
                        currentline++;
                        state = 0;
                    }
                    break;
                case 7:
                    ptr++;
                    if(c=='%'){
                        currentline++;
                        ptr=0;
                    }
                    if(c=='!'){
                        if(n=='>'){
                            currentline++;
                            state=0;
                            ptr=0;
                        }
                    }
                    
                    break;
                case 10:
                    if(c=='i'){
                        ptr++;
                        state = 11;
                    }
                    else if (c=='l'){
                        ptr++;
                        state = 24;
                    }
                    else if (c=='!'){
                        ptr++;
                        state = 6;
                    }
                    else { state = 100;}
                    break;
                case 11:
                    if(c=='n'){
                        ptr++;
                        state = 12;
                    }
                    else { state = 100;}
                    break;
                case 12:
                    if(c=='i'){
                        ptr++;
                        state = 13;
                    }
                    else { state = 100;}
                    break;
                case 13:
                    if(c=='t'){
                        ptr++;
                        state = 14;
                    }
                    else { state = 100;}
                    break;
                case 14:
                    if(c=='_'){
                        ptr++;
                        state = 15;
                    }
                    else { state = 100;}
                    break;
                case 15: // INIT_START OR INIT_END
                    if(c=='s'){
                        ptr++;
                        state = 16;
                    }
                    else if (c=='e'){
                        ptr++;
                        state = 21;
                    }
                    else { state = 100;}
                    break;
                case 16: 
                    if(c=='t'){
                        ptr++;
                        state = 17;
                    }
                    else { state = 100;}
                    break;
                case 17:
                    if(c=='a'){
                        ptr++;
                        state = 18;
                    }
                    else { state = 100;}
                    break;
                case 18:
                    if(c=='r'){
                        ptr++;
                        state = 19;      
                    }
                    else { state = 100;}
                    break;
                case 19: 
                    if(c=='t'){
                        ptr++;
                        state = 20;
                    }
                    else { state = 100;}
                    break;
                case 20:
                    if(c=='>'){
                        ptr++;
                        state = 0;
                        //TokenTable.put(posTok,new Token("id","",""));
                        return TokenTable.get("<init_start>");
                        //return new Token("<init_start>","RESERVED_WORD","[VARIABLE_START]");
                    }
                    else { state = 100;}
                    break;
                    
                case 21: 
                    if(c=='n'){
                        ptr++;
                        state = 22;
                    }
                    else { state = 100;}
                    break;
                    
                case 22:
                    if(c=='d'){
                        ptr++;
                        state = 23;
                    }
                    else { state = 100;}
                    break;
                case 23:
                    if(c=='>'){
                        ptr++;
                        state = 0;
                        return TokenTable.get("<init_end>");
                    }
                    else { state = 100; }
                    break;
                 
                case 24:
                    if(c=='a'){
                        ptr++;
                        state = 25;        
                    }
                    else { state = 100;}
                    break;
                case 25:
                    if(c=='z'){
                        ptr++;
                        state = 26;        
                    }
                    else { state = 100;}
                    break;    
                case 26:
                    if(c=='y'){
                        ptr++;
                        state = 27;        
                    }
                    else { state = 100;}
                    break;
                case 27:
                    if(c=='_'){
                        ptr++;
                        state = 28;        
                    }
                    else { state = 100;}
                    break;
                case 28: //WAKE OR SLEEP
                    if(c=='w'){
                        ptr++;
                        state = 29;        
                    }
                    else if(c=='s'){
                        ptr++;
                        state = 33;
                    }
                    else { state = 100;}
                    break;
                case 29: 
                    if(c=='a'){
                        ptr++;
                        state = 30;        
                    }
                    else { state = 100;}
                    break;
                case 30:
                    if(c=='k'){
                        ptr++;
                        state = 31;        
                    }
                    else { state = 100;}
                    break;
                case 31:
                    if(c=='e'){
                        ptr++;
                        state = 32;        
                    }
                    else { state = 100;}
                    break;
                case 32:
                    if(c=='>'){
                        ptr++;
                        state = 0;   
                       return TokenTable.get("<lazy_wake>");
                    }
                    else { state = 100;}
                    break;  
                case 33:
                    if(c=='l'){
                        ptr++;
                        state = 34;        
                    }
                    else { state = 100;}
                    break;
                 case 34:
                    if(c=='e'){
                        ptr++;
                        state = 35;        
                    }
                    else { state = 100;}
                    break;
                 case 35:
                    if(c=='e'){
                        ptr++;
                        state = 36;        
                    }
                    else { state = 100;}
                    break;
                 case 36:
                    if(c=='p'){
                        ptr++;
                        state = 37;        
                    }
                    else { state = 100;}
                    break;
                 case 37:
                    if(c=='>'){
                        ptr++;
                        state = 0;
                        return TokenTable.get("<lazy_sleep>");
                    }
                    else { state = 100;}
                    break;
                case 38:
                    posTok = posTok + c;
                    if(c=='f'){
                       if(!contIdentChar.contains(""+n)){
                           state = 0;
                           ptr++;
                           posTok = "";
                           return TokenTable.get("if");
                       }
                       else{
                           state = 1;
                           ptr++;
                       }
                    }                 
                    else { state = 100;}
                    break;
                case 39:
                    posTok = posTok + c;
                    if(c=='l'){
                        state = 40;
                        ptr++;
                    }
                    else {state = 100;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ){
                        state = 0;
                            if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 40:
                    posTok = posTok + c;
                    if(c=='s'){
                        state = 41;
                        ptr++;
                    }
                    else { state = 100;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ){
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 41:
                    posTok = posTok + c;
                    if(c=='e'){
                       if(!contIdentChar.contains(""+n)){
                           state = 0;
                           ptr++;
                           posTok = "";
                           return TokenTable.get("else");
                       }
                       else{
                           state = 1;
                           ptr++;
                       }
                    }                 
                    else { state = 100;}
                    break;
                case 42:
                    posTok = posTok + c;
                    if(c=='I'){
                        state=43;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ){
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 43:
                    posTok = posTok + c;
                    if(c=='N'){
                        state=44;
                        ptr++;
                    }
                    else { state = 1;
                    ptr++; }
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ){
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 44:
                    posTok = posTok + c;
                    if(c=='A'){
                        state=45;
                        ptr++;
                    }
                    else { state = 1;
                    ptr++; }
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ){
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 45:
                    posTok = posTok + c;
                    if(c=='L'){
                       if(!contIdentChar.contains(""+n)){
                           state = 0;
                           ptr++;
                           posTok = "";
                           return TokenTable.get("FINAL");
                       }
                       else{
                           state = 1;
                           ptr++;
                       }
                       
                    }                 
                    else { state = 100;}
                    break;
                case 46:
                    posTok = posTok + c;
                    if(c=='a'){
                        state=47;
                        ptr++;
                    }
                    else if(c=='o'){
                       if(!contIdentChar.contains(""+n)){
                           state = 0;
                           ptr++;
                           posTok = "";
                           return TokenTable.get("to");
                       }
                       else{
                           state = 1;
                           ptr++;
                       }
                       
                    } 
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ){
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 47:
                    posTok = posTok + c;
                    if(c=='k'){
                        state=48;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ){
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 48:
                    posTok = posTok + c;
                    if(c=='e'){
                       if(!contIdentChar.contains(""+n)){
                           state = 0;
                           ptr++;
                           posTok = "";
                           return TokenTable.get("take");
                       }
                       else{
                           state = 1;
                           ptr++;
                       }
                       
                    }                 
                    else { state = 100;}
                    break;
                case 49:
                    posTok = posTok + c;
                    if(c=='u'){
                        state=50;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ) {
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 50:
                    posTok = posTok + c;
                    if(c=='t'){
                       if(!contIdentChar.contains(""+n)){
                           state = 0;
                           ptr++;
                           posTok = "";
                           return TokenTable.get("out");
                       }
                       else{
                           state = 1;
                           ptr++;
                       } 
                    }                 
                    else { state = 100;}
                    break;
                   
                case 51:
                    posTok = posTok + c;
                    if(c=='o'){
                       if(!contIdentChar.contains(""+n)){
                           state = 0;
                           ptr++;
                           posTok = "";
                           return TokenTable.get("do");
                       }
                       else{
                           state = 1;
                           ptr++;
                       } 
                    }                 
                    else { state = 100;}
                    break;
                case 52:
                    posTok = posTok + c;
                    if(c=='h'){
                        state=53;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ) {
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 53:
                    posTok = posTok + c;
                    if(c=='i'){
                        state=54;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ){
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 54:
                    posTok = posTok + c;
                    if(c=='l'){
                        state=55;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ) {
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 55:
                    posTok = posTok + c;
                    if(c=='e'){
                       if(!contIdentChar.contains(""+n)){
                           state = 0;
                           ptr++;
                           posTok = "";
                           return TokenTable.get("while");
                       }
                       else{
                           state = 1;
                           ptr++;
                       } 
                    }                 
                    else { state = 100;}
                    break;
                
                case 56:
                    posTok = posTok + c;
                    if(c=='o'){
                        state=57;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ) {
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 57:
                    posTok = posTok + c;
                    if(c=='n'){
                        state=58;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ) {
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 58:
                    posTok = posTok + c;
                    if(c=='v'){
                        state=59;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ) {
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 59:
                    posTok = posTok + c;
                    if(c=='e'){
                        state=60;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ) {
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 60:
                    posTok = posTok + c;
                    if(c=='r'){
                        state=61;
                        ptr++;
                    }
                    else {
                        ptr++;
                        state = 1;}
                    if(n==' '||n=='|'||n=='<'||n=='='||n=='+'||n=='-'||n=='*'||n=='/'||n=='>'||n=='!'|| n=='%' || n==')' || n=='('|| n==':'|| n=='&'| n=='|'  ) {
                        state = 0;
                        if(initDone==false){
                                TokenTable.put(posTok,new Token("ID",posTok,"[ID="+posTok+"]"));
                            }
                            if(TokenTable.containsKey(posTok)){
                                return TokenTable.get(posTok);
                            }
                            else{
                                return new Token("ERROR", "message", "Variable " + posTok + " was not declared!");
                            }
                    }
                    break;
                case 61:
                    posTok = posTok + c;
                    if(c=='t'){
                       if(!contIdentChar.contains(""+n)){
                           state = 0;
                           ptr++;
                           posTok = "";
                           return TokenTable.get("convert");
                       }
                       else{
                           state = 1;
                           ptr++;
                       } 
                    }                 
                    else if(contIdentChar.contains(""+n)){
                        state = 1;
                        ptr++;
                    }
                    else { state = 100;}
                    break;
                case 100: // ERROR
                    state = 0;
                    return new Token("ERROR", "message", "Syntax Error at Line " + (currentline+1) + " character " + ptr);
                    
                case 500:
                    if(currentline == splitsize-1){
                        System.out.println("");
                        return new Token("End Of File","message","EOF");
                    }
                    else {
                        state =0;
                        currentline++;
                        return new Token("End of Line","mesage","\n");
                    }
                default:
                    System.out.println("in default case");
                    return new Token("ERROR", "message", "Syntax Error at Line " + (currentline+1) + " character " + ptr);
            }
        }
        
       
    }
    
    public static void main(String[] args) throws IOException{
        // TODO code application logic here
        Scanner sc = new Scanner();
        String err [] = new String [100];
        int ctr = 0;
        Token tok = new Token("","","");
        Token pastTok = new Token("","","");
        System.out.println("SCANNING PHASE");
        System.out.println("################################################################################################");
        while(!tok.strvalue.equals("EOF")){
            tok = sc.DFA(pastTok);
            if(tok.tokenType.equals("ERROR")){
              err[ctr] = tok.strvalue;  
              ctr++;
            }
            if(tok.tokenType.equals("id")){
                System.out.print(tok.strvalue);
            }
            else
                System.out.print(tok.strvalue);
            
            //insert parser here
            pastTok = tok;
        }
        System.out.println("\n################################################################################################");
        System.out.println("ERROR MESSAGES: ");
        for(int x = 0; x< ctr;x++){
            System.out.println(err[x]);
        }
        
        System.out.println("\n################################################################################################");
//        System.out.println("TOKEN TABLE: ");
//        Set set = TokenTable.entrySet();
//        Iterator iterator = set.iterator();
//        while(iterator.hasNext()){
//            Map.Entry mentry = (Map.Entry)iterator.next();
//            System.out.print("key is: " + mentry.getKey() + "\t \t \t" + " &  Value is : ");
//            System.out.println(mentry.getValue().toString());
//            
//        }

        
        
    }
}
