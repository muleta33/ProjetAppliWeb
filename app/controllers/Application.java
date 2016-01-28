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
        render(frontRecipe, olderRecipes);
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
        if (validation.hasErrors()) {
            render("Application/show.html", recipe, randomID);
        }
        // TODO: vérifier que l'utilisateur est connecté pour poster un commentaire
        User user = User.find("byLogin", login).first();
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
    
    public static void form() {
        List<DishCategory> dishCategories = DishCategory.find("order by name asc").fetch();
        render(dishCategories);
    }
 
    public static void save(String dishCategories) {
        List<DishCategory> dishCategoriesList = new ArrayList<DishCategory>();
        // Fill dishCategories list with each selected dishCategory
        for(String dishCategory : dishCategories.split("\\s+")) {
            if(dishCategory.trim().length() > 0) {
                dishCategoriesList.add(DishCategory.findOrCreateByName(dishCategory));
            }
        }
        // A changer
        listCategorized(dishCategoriesList.get(0).name);
    }
    
    public static void listCategorized(String dishCategory) {
        List<Recipe> recipes = Recipe.findCategorizedWith(dishCategory);
        render(dishCategory, recipes);
    }


}