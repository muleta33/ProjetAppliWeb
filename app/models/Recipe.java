package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;

import org.h2.engine.Comment;

import play.db.jpa.*;

@Entity
public class Recipe extends Model {

	public String title;
    public Date postedAt;
    public DishCategory category;
    public float rating;
    public int preparationTime;
    public int cookingTime;
    public int numberOfPersons;
    
    @ManyToMany
    public List<Ingredient> ingredients;
    
    @OneToMany(mappedBy="recipe", cascade=CascadeType.ALL)
    public List<Comment> comments;

    @Lob
    public String content;
    
    @ManyToOne
    public User author;
    
    public Recipe(User author, String title, DishCategory category, int preparationTime, int cookingTime, int numberOfPersons, 
                  List<Ingredient> ingredients, String content) {
        this.author = author;
        this.title = title;
        this.category = category;
        this.rating = computeRating();
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.numberOfPersons = numberOfPersons;
        this.ingredients = new ArrayList<Ingredient>(ingredients);
        this.comments = new ArrayList<Comment>();
        this.content = content;
        this.postedAt = new Date();
    }
    
    public Recipe addComment(String author, String content, int rating) {
        Comment newComment = new Comment(this, author, content, rating).save();
        this.comments.add(newComment);
        this.save();
        return this;
    }
    
    private float computeRating() {
        rating = 0;
        for (Comment comment : comments) {
            rating += comment.rating;
        }
        rating /= comments.size();
        return rating;
    }
    
}
