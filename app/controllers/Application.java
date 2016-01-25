package controllers;

import play.*;
import play.mvc.*;
import play.Play;
import play.data.validation.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    @Before
    static void addDefaults() {
        renderArgs.put("applicationTitle", Play.configuration.getProperty("application.title"));
        renderArgs.put("applicationBaseline", Play.configuration.getProperty("application.baseline"));
    }
    
    public static void index() {
        Recipe frontRecipe = Recipe.find("order by postedAt desc").first();
        List<Recipe> olderRecipes = Recipe.find("order by postedAt desc").from(1).fetch(10);
        render(frontRecipe, olderRecipes);
    }
    
    public static void show(Long id) {
        Recipe recipe = Recipe.findById(id);
        render(recipe);
    }

    public static void postComment(Long recipeId, String login, @Required String content, int rating) {
        Recipe recipe = Recipe.findById(recipeId);
        if (validation.hasErrors()) {
            render("Application/show.html", recipe);
        }
        // TODO: vérifier que l'utilisateur est connecté pour poster un commentaire
        User user = User.find("byLogin", login).first();
        recipe.addComment(user, content, rating);
        flash.success("Thanks for posting!");
        show(recipeId);
    }
    
    public static void listTagged(String tag) {
        List<Recipe> recipes = Recipe.findTaggedWith(tag);
        render(tag, recipes);
    }

}