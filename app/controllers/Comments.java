package controllers;

import play.*;
import play.exceptions.TemplateNotFoundException;
import play.mvc.*;

@Check("admin")
@With(Secure.class)
public class Comments extends CRUD {

    /* Redefinition pour empêcher l'admin de créer et d'ajouter
     * un commentaire en tant que n'importe quel utilisateur
     */
    public static void blank() throws Exception {
        redirect(request.controller + ".list");
    }
    
}
