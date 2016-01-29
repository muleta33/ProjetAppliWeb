package controllers;

import java.lang.reflect.Constructor;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import controllers.CRUD.ObjectType;
import models.DishCategory;
import models.Ingredient;
import models.Recipe;
import models.Tag;
import models.User;
import play.*;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.*;

@Check("connected")
@With(Secure.class)
@CRUD.For(Recipe.class)
public class MyRecipes extends CRUD {
    
    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        User user = User.find("byLogin", Security.connected()).first();
        String where = "author.login = '" + user.login + "'" ;
        if (!request.args.isEmpty())
            where += " AND " + (String) request.args.get("where");
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }
    
    public static void create(String tags, String ingredients) throws Exception {
        User user = User.find("byLogin", Security.connected()).first();
        Set<Ingredient> ingredientsList = new TreeSet<Ingredient>();
        Set<Tag> tagsList = new TreeSet<Tag>();
        // Set tags list
        for(String tag : tags.split("\\s+")) {
            if(tag.trim().length() > 0) {
                tagsList.add(Tag.findOrCreateByName(tag));
            }
        }
        // Set ingredients list
        for(String ingredient : ingredients.split("\\n+")) {
            if(ingredient.trim().length() > 0) {
                ingredientsList.add(Ingredient.findOrCreateByName(ingredient));
            }
        }
        Recipe recipe = new Recipe(user, "", null, 0, 0, 0, ingredientsList, tagsList, "");
        Binder.bindBean(params.getRootParamNode(), "object", recipe);
        validation.valid(recipe);
        if (validation.hasErrors()) {
            /*for(play.data.validation.Error error : validation.errors()) {
                 System.out.println(error.getKey());
             }*/
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", recipe);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", recipe);
            }
        }
        recipe._save();
        flash.success(play.i18n.Messages.get("crud.created", "recipe"));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", recipe._key());
    }
    
    public static void save(Long id, String tags, String ingredients) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id.toString());
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        // Trick to create and add new tags/ingredients
        Recipe recipe = (Recipe)object;
        // Set tags list
        for(String tag : tags.split("\\s+")) {
            if(tag.trim().length() > 0) {
                recipe.tags.add(Tag.findOrCreateByName(tag));
            }
        }
        // Set ingredients list
        for(String ingredient : ingredients.split("\\n+")) {
            if(ingredient.trim().length() > 0) {
                recipe.ingredients.add(Ingredient.findOrCreateByName(ingredient));
            }
        }
        // Save the recipe
        object._save();
        // Redirecting
        flash.success(play.i18n.Messages.get("crud.saved", "recipe"));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }
}
