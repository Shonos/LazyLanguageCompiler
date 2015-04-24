
package lazylanguagecompiler;

public class Token {
    String tokenType;
    String dataType="";
    String strvalue;
    int intvalue; 
    double doublevalue;
    boolean boolvalue;
    char charvalue;
    
    Token (){
    
    }
    
    Token(String tokenT,String dtype, char value )
    {
        tokenType= tokenT;
        charvalue = value; 
        dataType ="char";
    }

    Token(String tokenT, String dtype, String value)
    {
        tokenType= tokenT;
        strvalue = value; 
        dataType = dtype;
    }
    
    Token(String tokenT, String dtype, int value)
    {
        tokenType= tokenT;
        intvalue = value;
        dataType = dtype;
    }
    
    Token(String tokenT, String type, double value)
    {
        tokenType = tokenT;
        doublevalue = value;
        dataType ="double";
    }
    
    Token(String tokenT, String type, boolean value)
    {
        tokenType = tokenT;
        boolvalue = value;
        dataType ="boolean";
    }
    public String toString(){
        return strvalue;
    }
    
    public void setvalue(String identifier) {
        strvalue = identifier;
    }
    public void setvalue(int identifier) {
        intvalue = identifier;
    }
    public void setvalue(double identifier) {
        doublevalue = identifier;
    }
    public void setvalue(boolean identifier) {
        boolvalue = identifier;
    }
}
