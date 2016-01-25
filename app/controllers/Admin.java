package controllers;

import play.*;
import play.mvc.*;
 
import java.util.*;
 
import models.*;

import play.mvc.Controller;

@With(Secure.class)
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byLogin", Security.connected()).first();
            List<Recipe> userRecipes = Recipe.find("select distinct r from Recipe r join r.author as a where a.login = ?", user.login).fetch();
            renderArgs.put("user", user.login);
            // order by postedAt desc
            renderArgs.put("userRecipes", userRecipes);
        }
    }
    
    
 
    public static void index() {
        render();
    }
}
