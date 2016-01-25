package models;

import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import play.data.validation.*;
 
@Entity
public class User extends Model {
 
    @Email
    @Required
    public String email;
    
    public String login;
    
    @Required
    public String password;
    
    public boolean isAdmin;
    
    public User(String email, String login, String password) {
        this.email = email;
        this.login = login;
        this.password = password;
    }
    
    public static User connect(String login, String password) {
        return find("byLoginAndPassword", login, password).first();
    }
    
    public String toString() {
        return email;
    }
 
}
