package org.example.entity;



public class Preference {
    private Long id;
    private String preferenceName;

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getPreferenceName(){
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }
}
