package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class Recipe extends Model {

    @Required
	public String title;
    
    @Required
    public Date postedAt;
    
    @Required
    @ManyToOne
    public DishCategory category;
    
    public float rating;
    
    @Required
    public int preparationTime;
    
    @Required
    public int cookingTime;
    
    @Required
    public int numberOfPersons;
    
    @Required
    @ManyToMany
    public List<Ingredient> ingredients;
    
    @OneToMany(mappedBy="recipe", cascade=CascadeType.ALL)
    public List<Comment> comments;

    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @Required
    @ManyToOne
    public User author;
    
    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Tag> tags;
    
    public Recipe(User author, String title, DishCategory category, int preparationTime, int cookingTime, int numberOfPersons, 
                  List<Ingredient> ingredients, String content) {
        this.author = author;
        this.title = title;
        this.category = category;
        this.rating = 0;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.numberOfPersons = numberOfPersons;
        this.ingredients = new ArrayList<Ingredient>(ingredients);
        this.comments = new ArrayList<Comment>();
        this.tags = new TreeSet<Tag>();
        this.content = content;
        this.postedAt = new Date();
    }
    
    public Recipe addComment(User author, String content, int rating) {
        Comment newComment = new Comment(this, author, content, rating).save();
        this.comments.add(newComment);
        this.save();
        return this;
    }
    
    public float computeRating() {
        rating = 0;
        for (Comment comment : comments) {
            rating += comment.rating;
        }
        rating /= comments.size();
        return rating;
    }
    
    public Recipe previous() {
        return Recipe.find("postedAt < ? order by postedAt desc", postedAt).first();
    }

    public Recipe next() {
        return Recipe.find("postedAt > ? order by postedAt asc", postedAt).first();
    }
    
    public Recipe tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }
    
    public static List<Recipe> findTaggedWith(String tag) {
        return Recipe.find("select distinct r from Recipe r join r.tags as t where t.name = ?", tag).fetch();
    }
    
    public static List<Recipe> findTaggedWith(String... tags) {
        return Recipe.find(
                "select distinct r from Recipe r join r.tags as t where t.name in (:tags) group by r.id having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }
    
    public String toString() {
        return title;
    }
    
}
