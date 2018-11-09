package com.riddler.usr.bean;

/**
 * Created by daniel.luo on 2017/11/22.
 */
public class OneQuestion {
    private String question;
    private String answer;
    private double score;
    private int greedy;
    private int fear;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getGreedy() {
        return greedy;
    }

    public void setGreedy(int greedy) {
        this.greedy = greedy;
    }

    public int getFear() {
        return fear;
    }

    public void setFear(int fear) {
        this.fear = fear;
    }
}
