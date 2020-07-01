package com.sundaydavid.firestoreexampleproject;

import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.Map;

public class Note {
    private String documentId;
    private String title;
    private String description;
    private int priority;
    List<String> tags;
    Map<String, Boolean> tag;

    public Note(){
        //no-org constructor
    }

    public Note(String title, String description, int priority, List<String> tags, Map<String, Boolean> tag) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.tags = tags;
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getTags() {
        return tags;
    }

    public Map<String, Boolean> getTag() {
        return tag;
    }
}
