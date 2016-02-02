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
    
    @ManyToMany
    public Set<Ingredient> ingredients;
    
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
    
    public Recipe(User author, String title, DishCategory category, int preparationTime, int cookingTime, 
                  int numberOfPersons, String content) {
        this.author = author;
        this.title = title;
        this.category = category;
        this.rating = 0;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.numberOfPersons = numberOfPersons;
        this.ingredients = new TreeSet<Ingredient>();
        this.comments = new ArrayList<Comment>();
        this.tags = new TreeSet<Tag>();
        this.content = content;
        this.postedAt = new Date();
    }
    
    public Recipe addComment(User author, String content, int rating) {
        Comment newComment = new Comment(this, author, content, rating).save();
        this.comments.add(newComment);
        this.save();
        computeRating();
        return this;
    }
    
    public float computeRating() {
        rating = 0;
        int notNullRatings = 0;
        for (Comment comment : comments) {
            if (comment.rating != 0) {
                rating += comment.rating;
                notNullRatings ++;
            }
        }
        if (notNullRatings != 0)
            rating /= notNullRatings;
        return rating;
    }
    
    public Recipe previous() {
        return Recipe.find("postedAt < ? order by postedAt desc", postedAt).first();
    }

    public Recipe next() {
        return Recipe.find("postedAt > ? order by postedAt asc", postedAt).first();
    }
    
    public Recipe addIngredient(String name) {
        ingredients.add(Ingredient.findOrCreateByName(name));
        return this;
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
    
    public static List<Recipe> findCategorizedWith(String dishCategory) {
        return Recipe.find("select distinct r from Recipe r join r.category as c where c.name = ?", dishCategory).fetch();
    }
    
    public static List<Recipe> findCategorizedWith(String... dishCategories) {
        return Recipe.find(
                "select distinct r from Recipe r join r.category as c where c.name in (:dishCategories)"
        ).bind("dishCategories", dishCategories).bind("size", dishCategories.length).fetch();
    }
    
    public String toString() {
        return title;
    }
    
}
