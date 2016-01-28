package controllers;

import play.*;
import play.mvc.*;
import play.Play;
import play.cache.Cache;
import play.data.validation.*;
import play.libs.Codec;
import play.libs.Images;

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
        User user = User.find("byLogin", Security.connected()).first();
        render(frontRecipe, olderRecipes, user);
    }
    
    public static void show(Long id) {
        Recipe recipe = Recipe.findById(id);
        String randomID = Codec.UUID();
        render(recipe, randomID);
    }

    public static void postComment(Long recipeId, String login, 
            @Required(message="A message is required") String content, int rating, 
            @Required(message="Please type the code") String code, String randomID) {
        Recipe recipe = Recipe.findById(recipeId);
        validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
        // Retrieve and check if the user is connected user
        User user = User.find("byLogin", Security.connected()).first();
        validation.required(user).message("You have to be connected to post a comment");
        if (validation.hasErrors()) {
            render("Application/show.html", recipe, randomID);
        }
        recipe.addComment(user, content, rating);
        flash.success("Thanks for posting!");
        Cache.delete(randomID);
        show(recipeId);
    }
    
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#E4EAFD");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }
    
    public static void listTagged(String tag) {
        List<Recipe> recipes = Recipe.findTaggedWith(tag);
        render(tag, recipes);
    }

}