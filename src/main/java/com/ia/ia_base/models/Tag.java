package com.ia.ia_base.models;

public class Tag {
    private int id;
    private String tagName;

    public Tag(String tag) {
        this.tagName = tag;
    }

    public String getTagName() {
        return tagName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return tagName;
    }
}
