package app.teststruct;

import java.util.ArrayList;

public class Question{
    String name = "";
    ArrayList<Answer> variants;
    
    public Question(String name){
        this.name = name;
        variants = new ArrayList<Answer>();
    }

    public void add(Answer ans){
        variants.add(ans);
    }

    public final String toString(){
        return name + variants;
    }
}