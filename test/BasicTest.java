import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
        Fixtures.deleteDatabase();
    }
    
    @Test
    public void createAndRetrieveUser() {
        // Create a new user and save it
        new User("bob@gmail.com", "Bobby38", "secret").save();
        
        // Retrieve the user with e-mail address bob@gmail.com
        User bob = User.find("byEmail", "bob@gmail.com").first();
        
        // Test 
        assertNotNull(bob);
        assertEquals("Bobby38", bob.login);
    }
    
    @Test
    public void createRecipe() {
        // Create a new user and save it
        User bob = new User("bob@gmail.com", "Bob", "secret").save();
        
        // Create a new post
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        Ingredient poulet = new Ingredient("poulet").save();
        ingredients.add(poulet);
        Ingredient basquaise = new Ingredient("basquaise").save();
        ingredients.add(basquaise);
        new Recipe(bob, "Poulet basquaise", DishCategory.Plat, 30, 60, 4, ingredients, "Préparation complète").save();
        
        // Test that the recipe has been created
        assertEquals(1, Recipe.count());
        
        // Retrieve all recipes created by Bob
        List<Recipe> bobRecipes = Recipe.find("byAuthor", bob).fetch();
        
        // Tests
        assertEquals(1, bobRecipes.size());
        Recipe firstRecipe = bobRecipes.get(0);
        assertNotNull(firstRecipe);
        assertEquals(bob, firstRecipe.author);
        assertEquals("Poulet basquaise", firstRecipe.title);
        assertEquals(DishCategory.Plat, firstRecipe.category);
        assertEquals(30, firstRecipe.preparationTime);
        assertEquals(60, firstRecipe.cookingTime);
        assertEquals(4, firstRecipe.numberOfPersons);
        assertEquals("poulet", firstRecipe.ingredients.get(0).description);
        assertEquals("basquaise", firstRecipe.ingredients.get(1).description);
        assertEquals("Préparation complète", firstRecipe.content);
        assertNotNull(firstRecipe.postedAt);
    }
    
    @Test
    public void postComments() {
        // Create a new user and save it
        User bob = new User("bob@gmail.com", "Bobby38", "secret").save();
        
        // Create a new user and save it
        User jeff = new User("jeff@gmail.com", "Jeff33", "secret").save();
     
        // Create a new recipe
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        Ingredient poulet = new Ingredient("poulet").save();
        ingredients.add(poulet);
        Ingredient basquaise = new Ingredient("basquaise").save();
        ingredients.add(basquaise);
        Recipe bobRecipe = new Recipe(bob, "My first recipe", DishCategory.Plat, 12, 25, 2, ingredients, "Blabla").save();
     
        // Post a first comment
        new Comment(bobRecipe, jeff, "Nice recipe", 5).save();
        new Comment(bobRecipe, bob, "Thanks !", 5).save();
     
        // Retrieve all comments
        List<Comment> bobRecipeComments = Comment.find("byRecipe", bobRecipe).fetch();
     
        // Tests
        assertEquals(2, bobRecipeComments.size());
     
        Comment firstComment = bobRecipeComments.get(0);
        assertNotNull(firstComment);
        assertEquals("Jeff33", firstComment.author.login);
        assertEquals("Nice recipe", firstComment.content);
        assertNotNull(firstComment.postedAt);
     
        Comment secondComment = bobRecipeComments.get(1);
        assertNotNull(secondComment);
        assertEquals("Bobby38", secondComment.author.login);
        assertEquals("Thanks !", secondComment.content);
        assertNotNull(secondComment.postedAt);
    }
    
    @Test
    public void useTheCommentsRelation() {
        // Create a new user and save it
        User bob = new User("bob@gmail.com", "Bobby38", "secret").save();
        
        // Create a new user and save it
        User jeff = new User("jeff@gmail.com", "Jeff33", "secret").save();
        
        // Create a new user and save it
        User tom = new User("tom@gmail.com", "Tom33", "secret").save();
        
        // Create a new recipe
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        Ingredient poulet = new Ingredient("poulet").save();
        ingredients.add(poulet);
        Ingredient basquaise = new Ingredient("basquaise").save();
        ingredients.add(basquaise);
        Recipe bobRecipe = new Recipe(bob, "Poulet basquaise", DishCategory.Plat, 12, 25, 2, ingredients, "Blabla").save();
        
        // Post a first comment
        bobRecipe.addComment(jeff, "Tested", 4);
        bobRecipe.addComment(tom, "Tested", 4);
     
        // Count things
        assertEquals(3, User.count());
        assertEquals(1, Recipe.count());
        assertEquals(2, Comment.count());
     
        // Retrieve Bob's recipe
        bobRecipe = Recipe.find("byAuthor", bob).first();
        assertNotNull(bobRecipe);

        // Navigate to comments
        assertEquals(2, bobRecipe.comments.size());
        assertEquals("Jeff33", bobRecipe.comments.get(0).author.login);
        
        // Delete the recipe
        bobRecipe.delete();
        
        // Check that all comments have been deleted
        assertEquals(3, User.count());
        assertEquals(0, Recipe.count());
        assertEquals(0, Comment.count());
    }
    
    @Test
    public void fullTestFromData() {
        Fixtures.loadModels("test-data.yml");
     
        // Count things
        assertEquals(2, User.count());
        assertEquals(1, Recipe.count());
        assertEquals(2, Comment.count());
     
        // Try to connect as users
        assertNotNull(User.connect("Bobby38", "secret"));
        assertNotNull(User.connect("Jeff33", "secret"));
        assertNull(User.connect("Jeff33", "badpassword"));
        assertNull(User.connect("Tom", "secret"));
     
        // Find all of Bob's recipes
        List<Recipe> bobRecipe = Recipe.find("author.login", "Bobby38").fetch();
        assertEquals(1, bobRecipe.size());
        // Random test
        assertEquals(2, bobRecipe.get(0).numberOfPersons);
        // Check the compute of the rating
        bobRecipe.get(0).computeRating();
        assertEquals(4.5, bobRecipe.get(0).rating, 0.001);
     
        // Find all comments related to Bob's recipes
        List<Comment> bobRecipeComments = Comment.find("recipe.author.login", "Bobby38").fetch();
        assertEquals(2, bobRecipeComments.size());
    }

}
