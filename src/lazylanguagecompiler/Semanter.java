package lazylanguagecompiler;

import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Shaun
 */
public class Semanter {
    
    public Semanter(ParseTreeNode root) { 
        ParseTreeNode currNode = new ParseTreeNode();
        currNode = root;
        
        
        printTokens(root);
    
    }
    
    public void printTokens(ParseTreeNode x){
        System.out.println(x.token.tokenType);
        if(x.firstChild != null)
            printTokens(x.firstChild);
        if(x.nextSibling != null)
            printTokens(x.nextSibling);
        
    }
    
    public static void main(String[] args) throws IOException{
        Parser pp = new Parser();
        
        if(!pp.error){
           //System.out.println("Parse success!");
           //System.out.println("###########################  Lazy Parse Tree ####################################"); 
           //pp.printTree();
           
        }
        Semanter p = new Semanter(pp.theTree.root);
        
        
    }
    
    
}
