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
    
    public DishCategory(String name, List<DishCategory> subCategories) {
        this.name = name;
        this.subCategories = new ArrayList<DishCategory>(subCategories);
    }
    
    public String toString() {
        return name;
    }
    
}