package models;

import play.db.jpa.Model;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class Ingredient extends Model {

    @Required
    public String description;
    
    public Ingredient(String description) {
        this.description = description;
    }
    
    public String toString() {
        return description;
    }
    
}
