package app.teststruct;

import java.util.ArrayList;

public class Question{
    String name = "";
    ArrayList<Answer> variants;
    
    public Question(String _name){
        name = _name;
        variants = new ArrayList<Answer>();
    }
}