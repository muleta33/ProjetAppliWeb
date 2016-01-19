package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Comment extends Model {
 
    public Date postedAt;
    public int rating;
     
    @Lob
    public String content;
    
    @OneToOne
    public User author;
    
    @ManyToOne
    public Recipe recipe;
    
    public Comment(Recipe recipe, User author, String content, int rating) {
        this.recipe = recipe;
        this.author = author;
        this.content = content;
        this.rating = rating;
        this.postedAt = new Date();
    }
 
}
