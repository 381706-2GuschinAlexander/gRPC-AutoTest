package app.teststruct;

import java.util.ArrayDeque;

public class ActiveTest{
    Integer score = 0;
    ArrayDeque<Question> question_que;
    
    public ActiveTest(){
        question_que = new ArrayDeque<Question>();
    }

    public void addQuestion(Question q){
        question_que.add(q);
    }

    public Question peekQuestion(){
        return question_que.peek();
    }

    public Question nextQuestion(){
        return question_que.poll();
    }

    public final String toString(){
        return question_que.peek().toString();
    }

    public void answerQuestion(int[] answ_arr, int size){
        Question corr = question_que.peek();
        score += corr.answerQuestion(answ_arr, size);
        nextQuestion();
    }
}
