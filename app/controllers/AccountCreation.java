package controllers;

import javax.persistence.PersistenceException;

import models.DishCategory;
import models.User;
import play.*;
import play.mvc.*;

public class AccountCreation extends CRUD {

    @Before
    static void addDefaults() {
        renderArgs.put("applicationTitle", Play.configuration.getProperty("application.title"));
        renderArgs.put("applicationBaseline", Play.configuration.getProperty("application.baseline"));
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
        try {
            newUser.save();
        } catch (PersistenceException e) {
            flash.error("This login already exist, please find another one.");
            form();
        }
        index();
    }
}