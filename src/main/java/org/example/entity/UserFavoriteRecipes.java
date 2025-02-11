package org.example.entity;



public class UserFavoriteRecipes {
    private Long id;
    private User user;
    private Recipe recipe;

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    public Recipe getRecipe() {
        return recipe;
    }
}
