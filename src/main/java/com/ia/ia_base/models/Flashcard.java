package com.ia.ia_base.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;

public class Flashcard {
    private int id;
    private String question;
    private String answer;
    private ArrayList<String> tags = new ArrayList<>();
    private BooleanProperty active = new SimpleBooleanProperty();

    public Flashcard() {
    }

    public Flashcard(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

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

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean isActive() {
        return active.getValue();
    }

    public void setActive(boolean isActive) {
        active.set(isActive);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BooleanProperty activeProperty() {
        return active;
    }
}
