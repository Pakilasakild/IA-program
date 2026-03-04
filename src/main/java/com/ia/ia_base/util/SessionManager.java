package com.ia.ia_base.util;

import com.ia.ia_base.models.TeacherUser;
import com.ia.ia_base.models.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager(){

    }

    public static synchronized SessionManager getInstance(){
        if (instance == null){
            instance = new SessionManager();
        }

        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void logout(){
        this.currentUser = null;
    }

    public boolean isLoggedIn(){
        return currentUser != null;
    }

    public boolean isTeacher(){
        return currentUser != null && currentUser instanceof TeacherUser;
    }
}
