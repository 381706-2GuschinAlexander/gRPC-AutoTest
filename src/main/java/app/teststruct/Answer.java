package app.teststruct;

public class Answer{
    String text = "";
    boolean isTrue = false;
    int pointTaken = 0;
    int pointSkip = 0;

    public Answer(String text, boolean isTrue, int pointTaken, int pointSkip){
        this.text = text;
        this.isTrue = isTrue;
        this.pointTaken = pointTaken;
        this.pointSkip = pointSkip;
    }

    public final String toString(){
        return text;
    }
}