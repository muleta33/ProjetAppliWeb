package controllers;

import models.User;
import play.*;
import play.mvc.*;

@Check("admin")
public class Users extends CRUD {

    @Before
    static void addDefaults() {
        renderArgs.put("applicationTitle", "New user?");
        renderArgs.put("applicationBaseline", "Create your account and share your recipes!");
    }
    
    public static void form() {
        render();
    }
     
    public static void save(String login, String email, String password) {
        // Create post
        User newUser = new User(email, login, password);
        // Validate
        validation.valid(newUser);
        if(validation.hasErrors()) {
            render("@form", newUser);
        }
        // Save
        newUser.save();
        index();
    }
}
