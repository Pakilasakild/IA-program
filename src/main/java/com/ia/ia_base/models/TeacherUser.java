package com.ia.ia_base.models;

public class TeacherUser extends User{
    public TeacherUser() {
        super();
    }

    public TeacherUser(int id, String passwordHash, String email, Role role) {
        super(id, passwordHash, email, role);
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }

    @Override
    public boolean canManageCategories() {
        return true;
    }

    @Override
    public boolean isStudent() {
        return false;
    }
}
