package models;

import play.db.jpa.Model;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class Ingredient extends Model implements Comparable<Ingredient> {

    @Required
    public String description;
    
    public Ingredient(String description) {
        this.description = description;
    }
    
    public String toString() {
        return description;
    }
    
    public int compareTo(Ingredient otherIngredient) {
        return description.compareTo(otherIngredient.description);
    }
    
    public static Ingredient findOrCreateByName(String description) {
        Ingredient ingredient = Ingredient.find("byDescription", description).first();
        if(ingredient == null) {
            ingredient = new Ingredient(description).save();
        }
        return ingredient;
    }
    
}
