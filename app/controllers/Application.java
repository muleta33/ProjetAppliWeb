package controllers;

import play.*;
import play.mvc.*;
import play.Play;

import java.util.*;

import models.*;

public class Application extends Controller {

    @Before
    static void addDefaults() {
        renderArgs.put("blogTitle", Play.configuration.getProperty("application.title"));
        renderArgs.put("blogBaseline", Play.configuration.getProperty("application.baseline"));
    }
    
    public static void index() {
        Recipe frontRecipe = Recipe.find("order by postedAt desc").first();
        List<Recipe> olderRecipes = Recipe.find("order by postedAt desc").from(1).fetch(10);
        render(frontRecipe, olderRecipes);
    }

}