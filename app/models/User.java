package models;

import java.util.*;
import javax.persistence.*;

import org.h2.store.WriterThread;

import groovy.ui.Console;
import play.db.jpa.*;
import play.data.validation.*;
import play.libs.Crypto;
 
@Entity
public class User extends Model {
 
    @Email
    @Required
    public String email;
    
    @Required
    @Column(unique = true)
    public String login;
    
    @Required
    public String password;
    
    public boolean isAdmin;
    
    public User(String email, String login, String password) {
        this.email = email;
        this.login = login;
        this.password = Crypto.passwordHash(password);
        this.isAdmin = false;
    }
    
    public static User connect(String login, String password) {
        User user = find("byLoginAndPassword", login, password).first();
        return user;
    }
    
    public String toString() {
        return login;
    }
 
}
