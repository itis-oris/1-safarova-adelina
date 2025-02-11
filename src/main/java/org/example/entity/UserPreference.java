package org.example.entity;




public class UserPreference {
    private Long id;
    private User user;
    private Preference preference;

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Preference getPreference() {
        return preference;
    }
    public void setPreference(Preference preference) {
        this.preference = preference;
    }

}
