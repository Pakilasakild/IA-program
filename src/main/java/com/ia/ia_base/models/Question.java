package com.ia.ia_base.models;

public class Question {
    private int id;
    private String question;
    private String firstAnswer;
    private String secondAnswer;
    private String thirdAnswer;
    private String fourthAnswer;
    private int correctAnswer;

    public Question(String question) {
        this.question = question;
    }

    public void setFirstAnswer(String firstAnswer) {
        this.firstAnswer = firstAnswer;
    }

    public void setSecondAnswer(String secondAnswer) {
        this.secondAnswer = secondAnswer;
    }

    public void setThirdAnswer(String thirdAnswer) {
        this.thirdAnswer = thirdAnswer;
    }

    public void setFourthAnswer(String fourthAnswer) {
        this.fourthAnswer = fourthAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Question(String question, String firstAnswer, String secondAnswer, String thirdAnswer, String fourthAnswer, int correctAnswer) {
        this.question = question;
        this.firstAnswer = firstAnswer;
        this.secondAnswer = secondAnswer;
        this.thirdAnswer = thirdAnswer;
        this.fourthAnswer = fourthAnswer;
        this.correctAnswer = correctAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String questionName) {
        this.question = questionName;
    }

    public String getFirstAnswer() {
        return firstAnswer;
    }

    public String getSecondAnswer() {
        return secondAnswer;
    }

    public String getThirdAnswer() {
        return thirdAnswer;
    }

    public String getFourthAnswer() {
        return fourthAnswer;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public String toString() {
        return question;
    }
}
