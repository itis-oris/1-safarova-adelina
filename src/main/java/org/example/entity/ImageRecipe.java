package org.example.entity;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ImageRecipe {
    private Long id;

    private String filePath;


    private Recipe recipe;

}

