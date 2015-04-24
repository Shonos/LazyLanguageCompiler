package LazyLanguageCompiler;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jezjez
 */
public class tree {
    Token data;
    tree left;
    tree sibling;
    
    tree()
    {
        data=null;
        left=null;
        sibling=null;
    }
    tree(String data, tree left, tree right) {
     
      this.left = left;
      this.sibling = right;
    }
    tree(String data)
    {
        this.data = new Token(data);
    }
    tree(Token datum)
    {
        this.data=datum;
    }
    public void setleft(tree left)
    {
        this.left= left;
    }
      public String printNode(){
        String s = "{Parent: " + data + " Child: ";
        if (this.left==null)
            s = s+"\\ Sibling: ";
        else
            s = s+left.data+" Sibling: ";
        if(this.sibling==null)
            s = s+"\\ Sibling: ";
        else
            s = s+sibling.data+" Sibling: ";
        return s;
    }
       public String treeOut(){
        String s = "[Token: \'" + data.tokenname + "\'";
        if (!(this.left==null) && !(this.sibling==null))
            s = s + " Child: \n"+left.treeOut() + " Sibling: " + sibling.treeOut() + " ] ";
        if (!(this.left==null) && (this.sibling==null))
            s = s + " Child: \n"+left.treeOut() + " ]";
        if ((this.left==null) && !(this.sibling==null))
            s = s + " Sibling: " + sibling.treeOut() + " ] ";
        return s;
    }
}
