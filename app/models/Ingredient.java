package models;

import play.db.jpa.Model;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Ingredient extends Model {

    public String description;
    
    public Ingredient(String description) {
        this.description = description;
    }
    
}
