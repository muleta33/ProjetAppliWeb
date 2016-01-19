package models;

import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class User extends Model {
 
    public String email;
    public String login;
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
 
}
