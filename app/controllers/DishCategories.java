package controllers;

import models.DishCategory;
import play.*;
import play.mvc.*;

//@Check("admin")
//@With(Secure.class)
@CRUD.For(DishCategory.class)
public class DishCategories extends CRUD {
    
}
