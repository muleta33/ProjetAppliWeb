package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;
 
@Entity
public class Comment extends Model {
 
    @Required
    public Date postedAt;
    
    @Required
    public int rating;
    
    @Lob
    @Required
    public String content;
    
    @Required
    @OneToOne
    public User author;
    
    @ManyToOne
    @Required
    public Recipe recipe;
    
    public Comment(Recipe recipe, User author, String content, int rating) {
        this.recipe = recipe;
        this.author = author;
        this.content = content;
        this.rating = rating;
        this.postedAt = new Date();
    }
    
    public String toString() {
        return content;
    }
 
}
