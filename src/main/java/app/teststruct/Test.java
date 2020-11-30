package app.teststruct;

import java.util.ArrayList;

public class Test{
    String name = "";
    ArrayList<Integer> list_id;
    
    public Test(String _name){
        name = _name;
        list_id = new ArrayList<Integer>();
    }

    public void add(Integer value){
        list_id.add(value);
    }

    public final String toString(){
        return name + list_id;
    }
}
