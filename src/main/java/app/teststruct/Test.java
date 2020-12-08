package app.teststruct;

import java.util.ArrayList;

public class Test{
    String name = "";
    ArrayList<Integer> list_id;
    
    public Test(String name){
        this.name = name;
        list_id = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getArray(){
        return list_id;
    }

    public void add(Integer value){
        list_id.add(value);
    }

    public final String toString(){
        return name + list_id;
    }

    public final String getName(){
        return name;
    }
}
