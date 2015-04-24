package lazylanguagecompiler;



public class ParseTreeNode {
    protected Token token;
    protected ParseTreeNode firstChild;
    protected ParseTreeNode nextSibling;
    protected ParseTreeNode lastSibling;
    
    public ParseTreeNode() {
        this(new Token(), null, null);
        this.lastSibling = nextSibling;
    }
    
    public ParseTreeNode(Token token, ParseTreeNode child, ParseTreeNode sibling) {
        this.token = token;
        this.firstChild = child;
        this.nextSibling = sibling;
        this.lastSibling = nextSibling;
    }
    
    public void createParseTreeNode(Token token, ParseTreeNode child, ParseTreeNode sibling) {
        this.token = token;
        this.firstChild = child;
        this.nextSibling = sibling;
    }
    
    public void setToken(Token token) {
        this.token = token;
    }
    
    public void setFirstChild(ParseTreeNode child) {
        this.firstChild = child;
    }
    
    public void setNextSibling(ParseTreeNode sibling) {
        this.nextSibling = sibling;
    }
    
    
    public void setChildAndSibling(ParseTreeNode child, ParseTreeNode sibling) {
        this.firstChild = child;
        this.nextSibling = sibling;
    }
    
    public Token getToken() {
        return this.token;
    }
    
    public Boolean siblingIsNull() {
        if(nextSibling==null){
            return true;
        }
        else
            return false;
    }
    public Boolean firstChildIsNull() {
        if(firstChild==null){
            return true;
        }
        else
            return false;
    }
    
    
}