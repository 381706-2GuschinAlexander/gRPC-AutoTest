package app.teststruct;

import java.util.ArrayList;
import java.util.Collections;

public final class Question{
    String name = "";
    ArrayList<Answer> variants;
    
    public Question(String name){
        this.name = name;
        variants = new ArrayList<Answer>();
    }

    public ArrayList<Answer> getVariants(){
        return variants;
    }

    public void add(Answer ans){
        variants.add(ans);
    }

    public final String toString(){
        return name;
    }

    public int answerQuestion(ArrayList<Integer> tmp){
        int k = 0, res = 0;
        ArrayList<Integer> arr = new ArrayList<Integer>(tmp);
        
        Collections.sort(arr);
        
        for(int i = 0; i < variants.size(); ++i){
            if(k < arr.size() && arr.get(k) == i){
                res += variants.get(i).chose(true);
                k++;
            }
            else
                res += variants.get(i).chose(false);;
        }
        return res;
    }
}