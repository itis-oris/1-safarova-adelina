package org.example.entity;

import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Getter
@Setter

public class Recipe {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Integer preparationTime;
    private Integer servings;
    private String ingredients;
    private String steps;
    private String createdAt;
    private String coverImagePath;
    private User user;
    private List<Comment> comments;
    private List<Rating> ratings;
    private List<ImageRecipe> images = new ArrayList<>();

    public void addImage(ImageRecipe image) {
        images.add(image);
        image.setRecipe(this);
    }

    public void removeImage(ImageRecipe image) {
        images.remove(image);
        image.setRecipe(null);
    }

}

