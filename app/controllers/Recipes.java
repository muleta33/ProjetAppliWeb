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
        Recipe recipe = (Recipe)object;
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
        object._save();
        // Redirecting
        flash.success(play.i18n.Messages.get("crud.saved", "recipe"));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }

}
