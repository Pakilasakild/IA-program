package com.ia.ia_base.models;

public class StudentUser extends User {

    public StudentUser() {
        super();
    }

    public StudentUser(int id, String passwordHash, String email, Role role) {
        super(id, passwordHash, email, role);
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public boolean canManageCategories() {
        return false;
    }

    public boolean isStudent(){
        return true;
    }
}
