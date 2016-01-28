package controllers;

import play.*;
import play.mvc.*;
 
import java.util.*;
 
import models.*;

import play.mvc.Controller;

@With(Secure.class)
public class Admin extends CRUD {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byLogin", Security.connected()).first();
            renderArgs.put("user", user);
        }
    }
    
    public static void index() {
        render();
    }
}
