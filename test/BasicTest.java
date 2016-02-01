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
        
        // Create a new Recipe
        DishCategory category = new DishCategory("Plat", new ArrayList<DishCategory>(), false).save();
        Recipe recipe = new Recipe(bob, "Poulet basquaise", category, 30, 60, 4, "Préparation complète").save();
        recipe.addIngredient("poulet");
        recipe.addIngredient("basquaise");
        recipe.tagItWith("tag");
        
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
        assertEquals(category.name, firstRecipe.category.name);
        assertEquals(30, firstRecipe.preparationTime);
        assertEquals(60, firstRecipe.cookingTime);
        assertEquals(4, firstRecipe.numberOfPersons);
        assertEquals(true, firstRecipe.ingredients.contains(Ingredient.findOrCreateByName("poulet")));
        assertEquals(true, firstRecipe.ingredients.contains(Ingredient.findOrCreateByName("basquaise")));
        assertEquals("Préparation complète", firstRecipe.content);
        assertNotNull(firstRecipe.postedAt);
    }
    
    @Test
    public void RecipeComments() {
        // Create a new user and save it
        User bob = new User("bob@gmail.com", "Bobby38", "secret").save();
        
        // Create a new user and save it
        User jeff = new User("jeff@gmail.com", "Jeff33", "secret").save();
     
        // Create a new recipe
        DishCategory category = new DishCategory("Plat", new ArrayList<DishCategory>(), false).save();
        Recipe bobRecipe = new Recipe(bob, "My first recipe", category, 12, 25, 2, "Blabla").save();
        
        // Recipe a first comment
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
        DishCategory category = new DishCategory("Plat", new ArrayList<DishCategory>(), false).save();
        Recipe bobRecipe = new Recipe(bob, "Poulet basquaise", category, 12, 25, 2, "Blabla").save();
        
        // Recipe a first comment
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
        assertEquals(2, Ingredient.count());
        assertEquals(7, DishCategory.count());
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
    
    @Test
    public void testTags() {
        // Create a new user and save it
        User bob = new User("bob@gmail.com", "secret", "Bob").save();
     
        // Create a new recipe
        DishCategory category = new DishCategory("Plat", new ArrayList<DishCategory>(), false).save();
        Recipe bobRecipe = new Recipe(bob, "Poulet basquaise", category, 12, 25, 2, "Blabla").save();
        bobRecipe.addIngredient("poulet");
        bobRecipe.addIngredient("basquaise");
        
        // Create a new recipe
        Recipe anotherBobRecipe = new Recipe(bob, "Poulet basquaise épicé", category, 12, 25, 2, "Blabla").save();
        anotherBobRecipe.addIngredient("poulet");
        anotherBobRecipe.addIngredient("basquaise");
        anotherBobRecipe.addIngredient("épices");
        
        // Well
        assertEquals(0, Recipe.findTaggedWith("poulet").size());
     
        // Tag it now
        bobRecipe.tagItWith("poulet").tagItWith("other").save();
        anotherBobRecipe.tagItWith("poulet").tagItWith("épices").save();
     
        // Check
        assertEquals(2, Recipe.findTaggedWith("poulet").size());
        assertEquals(1, Recipe.findTaggedWith("épices").size());
        assertEquals(1, Recipe.findTaggedWith("poulet", "épices").size());
        assertEquals(1, Recipe.findTaggedWith("poulet", "other").size());
        assertEquals(0, Recipe.findTaggedWith("poulet", "other", "épices").size());
        assertEquals(0, Recipe.findTaggedWith("other", "épices").size());
        
        // Check tag cloud
        List<Map> cloud = Tag.getCloud();
        assertEquals(
            "[{pound=1, tag=other}, {pound=2, tag=poulet}, {pound=1, tag=épices}]",
            cloud.toString()
        );
    }
    
}
