package models;

import play.db.jpa.Model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class DishCategory extends Model {

    @Required
    public String name;
    
    @Required
    @OneToMany
    public List<DishCategory> subCategories;
    
    @Required
    public boolean hasAFather;
    
    public DishCategory(String name, List<DishCategory> subCategories, boolean hasAFather) {
        this.name = name;
        this.subCategories = new ArrayList<DishCategory>(subCategories);
        this.hasAFather = hasAFather;
    }
    
    public String toString() {
        return name;
    }
    
    public static DishCategory findOrCreateByName(String name) {
        DishCategory dishCategory = DishCategory.find("byName", name).first();
        if(dishCategory == null) {
            dishCategory = new DishCategory(name, new ArrayList<DishCategory>(), false);
        }
        return dishCategory;
    }
    
}