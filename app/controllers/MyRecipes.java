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
    
    public static void create(String tagName, String ingredientName) throws Exception {
        User user = User.find("byLogin", Security.connected()).first();
        DishCategory category = (DishCategory) DishCategory.findAll().get(0);
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        Set<Tag> tags = new TreeSet<Tag>();
        Recipe recipe = new Recipe(user, "", category, 0, 0, 0, ingredients, tags, "");
        Binder.bindBean(params.getRootParamNode(), "object", recipe);
        /*validation.valid(recipe);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", recipe);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", recipe);
            }
        }*/
        if (tagName != "") {
            Tag tag = new Tag(tagName).save();
            recipe.tags.add(tag);
        }
        if (ingredientName != "") {
            Ingredient ingredient = new Ingredient(ingredientName).save();
            recipe.ingredients.add(ingredient);
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
}
