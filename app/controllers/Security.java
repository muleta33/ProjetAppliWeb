package controllers;

import models.*;

public class Security extends controllers.Secure.Security {

    static boolean authenticate(String username, String password) {
        return User.connect(username, password) != null;
    }
    
    static void onDisconnected() {
        Application.index();
    }
    
    static void onAuthenticated() {
        Admin.index();
    }
    
    static boolean check(String profile) {
        if("admin".equals(profile)) {
            return User.find("byLogin", connected()).<User>first().isAdmin;
        }
        return false;
    }
    
}
