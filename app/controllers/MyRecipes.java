package controllers;

import java.util.List;

import controllers.CRUD.ObjectType;
import models.DishCategory;
import models.Recipe;
import models.User;
import play.*;
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
}
