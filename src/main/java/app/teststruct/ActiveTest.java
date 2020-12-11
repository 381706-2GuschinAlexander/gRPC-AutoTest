package app.teststruct;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class ActiveTest{
    Integer score = 0;
    ArrayDeque<Question> question_que;
    
    public ActiveTest(){
        question_que = new ArrayDeque<Question>();
    }

    public void addQuestion(Question q){
        question_que.add(q);
    }

    public void EarlyFinish(){
        ArrayList<Integer> tmp = new ArrayList<Integer>();
        tmp.add(-1);
        while(question_que.isEmpty() == false){
            answerQuestion(tmp);
        }
    }

    public Question peekQuestion(){
        if (question_que.isEmpty() == false)
            return question_que.peek();
        return null;
    }

    public Question nextQuestion(){
        return question_que.poll();
    }

    public final String toString(){
        return question_que.peek().toString();
    }

    public void answerQuestion(ArrayList<Integer> arr){
        while(question_que.isEmpty() == false && question_que.peek().getVariants().size() == 0)
            nextQuestion();

        if (question_que.isEmpty() == false) {
            Question corr = question_que.peek();
            score += corr.answerQuestion(arr);
            nextQuestion();
        } else 
            System.out.println("Empty");
        System.out.println(score);
    }
}
