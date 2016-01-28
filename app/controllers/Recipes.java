package controllers;

import controllers.CRUD.ObjectType;
import models.Ingredient;
import models.Recipe;
import models.Tag;
import models.User;
import play.*;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.*;

@Check("admin")
@With(Secure.class)
public class Recipes extends CRUD {
    
    public static void save(Long id, String tagName, String ingredientName) throws Exception {
        // Retrieve recipe
        Recipe recipe = Recipe.findById(id);
        notFoundIfNull(recipe);
        // Bind other elements
        Binder.bindBean(params.getRootParamNode(), "object", recipe);
        validation.valid(recipe);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", recipe);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", recipe);
            }
        }
        flash.success(play.i18n.Messages.get("crud.saved", "recipe"));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        // Create and add tag if necessary
        if (!tagName.equals("")) {
            Tag tag = Tag.findOrCreateByName(tagName);
            tag.save();
            recipe.tags.add(tag);
        }
        // Create and add ingredient if necessary
        if (!ingredientName.equals("")) {
            Ingredient ingredient = Ingredient.findOrCreateByName(ingredientName);
            ingredient.save();
            recipe.ingredients.add(ingredient);
        }
        // Save the recipe
        recipe.save();
        redirect(request.controller + ".show", recipe._key());
    }

}
