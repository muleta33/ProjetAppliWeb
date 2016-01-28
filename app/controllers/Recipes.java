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
    
    public static void save(Long id, String tags, String ingredients) throws Exception {
        /* Attention erreur si titre vide ??? */
        Recipe recipe = Recipe.findById(id);
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
        recipe._save();
        // Redirecting
        flash.success(play.i18n.Messages.get("crud.saved", "recipe"));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", recipe._key());
    }

}
