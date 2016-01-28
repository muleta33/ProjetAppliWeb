package controllers;

import models.*;
import play.libs.Crypto;

public class Security extends controllers.Secure.Security {

    static boolean authenticate(String username, String password) {
        return User.connect(username, Crypto.passwordHash(password)) != null;
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
        else if ("connected".equals(profile)) {
            return !User.find("byLogin", connected()).<User>fetch().isEmpty();
        }
        return false;
    }
    
}
