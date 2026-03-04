package com.ia.ia_base.models;

public class Tag {
    private int id;
    private String tagName;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tag) {
        this.tagName = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tag(String tag) {
        this.tagName = tag;
    }

    @Override
    public String toString() {
        return tagName;
    }
}
