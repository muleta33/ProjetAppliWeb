package controllers;

import play.*;
import play.mvc.*;
import play.Play;

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
    
    public static void postComment(Long recipeId, String login, String content, int rating) {
        Recipe recipe = Recipe.findById(recipeId);
        // TODO: vérifier que l'utilisateur est connecté pour poster un commentaire
        User user = User.find("byLogin", login).first();
        recipe.addComment(user, content, rating);
        show(recipeId);
    }

}