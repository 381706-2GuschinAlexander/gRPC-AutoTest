package app.teststruct;

public class Answer{
    String text = "";
    int pointTaken = 0;
    int pointSkip = 0;

    public Answer(String text, int pointTaken, int pointSkip){
        this.text = text;
        this.pointTaken = pointTaken;
        this.pointSkip = pointSkip;
    }

    public final String toString(){
        return text;
    }

    public int chose(boolean isChosen){
        return (isChosen == true ? pointTaken : pointSkip);
    }
}