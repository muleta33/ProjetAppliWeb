package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;

@Entity
public class Recipe extends Model {

	public String title;
    public Date postedAt;
    public String category;
    public float rating;
    public int preparationTime;
    public int cookingTime;
    public int numberOfPersons;
    
    @ManyToMany
    public List<Ingredient> ingredients;

    @Lob
    public String content;
    
    @ManyToOne
    public User author;
    
    public Recipe(User author, String title, String category, int preparationTime, int cookingTime, int numberOfPersons, 
                  List<Ingredient> ingredients, String content) {
        this.author = author;
        this.title = title;
        this.category = category;
        this.rating = computeRating();
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.numberOfPersons = numberOfPersons;
        this.ingredients = new ArrayList<Ingredient>(ingredients);
        this.content = content;
        this.postedAt = new Date();
    }
    
    private float computeRating() {
        return 0;
    }
    
}
