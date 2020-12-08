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

    public int answerQuestion(int[] answ, int size){
        int k = 0, res = 0;
        for(int i = 0; i < variants.size(); ++i){
            if(k < size && answ[k] == i)
                res += variants.get(i).chose(true);
            else
                res += variants.get(i).chose(false);;
        }
        return res;
    }
}