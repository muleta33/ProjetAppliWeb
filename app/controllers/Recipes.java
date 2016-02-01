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
    
    /* Redefinition pour empêcher l'admin de créer et d'ajouter
     * une recette en tant que n'importe quel utilisateur
     */
    public static void blank() throws Exception {
        redirect(request.controller + ".list");
    }

}
