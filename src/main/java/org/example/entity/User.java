package org.example.entity;


import lombok.Getter;
import lombok.Setter;


import java.sql.Blob;
import java.util.Date;
import java.util.List;
@Getter
@Setter
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private Boolean isAdmin;
    private List<Recipe> createdRecipes;
    private List<Recipe> favoriteRecipes;
    private List<Comment> comments;
    private String createdAt;



}

