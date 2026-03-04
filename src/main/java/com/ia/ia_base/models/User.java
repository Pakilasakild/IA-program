package com.ia.ia_base.models;

public abstract class User {
    private int id;
    private String email;
    private String passwordHash;
    private Role role;
    private boolean isBlocked = false;
    private boolean mustChangePassword = false;

    public User() {
    }

    public User(int id, String passwordHash, String email, Role role) {
        this.id = id;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract boolean canManageUsers();

    public abstract boolean canManageCategories();
    public abstract boolean isStudent();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return email + " (" + (role != null ? role.getName() : "No role") + ")";
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}
