package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping; //to get info from browser

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication 

//start of application
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  //DO NOT REMOVE
  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/") //source index
  String index(Map<String, Object> model) {

    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      //need primary key ID FOR REMOVING AND ADDING 
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS Rectangle (id serial PRIMARY KEY, name varchar(20), color varchar(20),"
              + " width decimal, height decimal, borderWidth decimal, borderColor varchar(20), image integer);");

      ResultSet rs = stmt.executeQuery("SELECT * FROM Rectangle");

      //create string type of the id (default) which is to be removed (not displayed on screen), name is string and color 
      // as well
      ArrayList<String> ids = new ArrayList<String>();
      ArrayList<String> names = new ArrayList<String>();
      ArrayList<String> colors = new ArrayList<String>();
      while (rs.next()) {
       
        ids.add(String.valueOf(rs.getInt("id")));
        names.add(rs.getString("name"));
        colors.add(rs.getString("color"));

        
      }

    
      model.put("ids", ids);
      model.put("names", names);
      model.put("colors", colors);

     

      return "home"; //

    } 
    catch (Exception e) {
      model.put("message", e.getMessage());
      return "error"; //error 
    }

  }

  

  //gets the specific id for the rectangle request and sets it attributes using the getstring command (and other getters)
  @RequestMapping("/rectangle")
  String rectangle(@RequestParam(value = "id", required = false) String id, Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM Rectangle where id=" + id); //tag is the id number 
      int guineaPig;
      while (rs.next()) {
       

        model.put("id", String.valueOf(rs.getInt("id")));
        model.put("name", rs.getString("name"));
        model.put("color", rs.getString("color"));
        model.put("width", String.valueOf(rs.getDouble("width")));//px
        model.put("height", String.valueOf(rs.getDouble("height")));//px
        model.put("borderWidth", String.valueOf(rs.getDouble("borderWidth")));//px
        model.put("borderColor", rs.getString("borderColor"));

      }

      return "rectangle";

    } 
    
    //print error message if try fails
    catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }
  

  //creates a object at the url so that when post mapping is done we can add the values from the form into the attributes
  //of the rectangle object. 
  @RequestMapping("/add") 
  String addRectangle(Map<String, Object> model) {

    Rectangle rectangle = new Rectangle();
    model.put("rectangle", rectangle);
    return "add";

  }


  //takes the form and puts the values of the rectangle into the database. 
  @PostMapping(path = "/add", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String addRectangleToDB(Rectangle rectangle) throws Exception {

    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "INSERT INTO Rectangle(name, color, width, height, borderWidth, borderColor, image) VALUES ('" 
          + rectangle.getName() + "', '" + rectangle.getColor() + "', " + rectangle.getWidth() + ", " 
          + rectangle.getHeight() + ", " + rectangle.getBorderWidth() + ", '" + rectangle.getBorderColor() 
          + "', " + rectangle.getImage() + ");");
      return "add";
    } catch (Exception e) {
      return "error";
    }

  }

  //If the user decides not to add any rectangles go back to the main index localhosr:5000 page (Redirect back from
  //addition page)
  @PostMapping(path = "/add", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE }, params = "action=cancel")
  public String cancelAdd(Rectangle rectangle){

    return "redirect:/";

  }

  //at locahost5000;/delete we get message and take the id asking if want to delete the id (tag)
  @RequestMapping("/delete") 
  String deleteRectangle(@RequestParam(value = "id", required = false) String id, Map<String, Object> model) {

    String message = "Please confirm if you want to delete rectangle ID " + id + " ? If not you can go back or click delete"; //we took the id parameter

    Rectangle rectangle = new Rectangle();
    model.put("rectangle", rectangle);
    model.put("message", message);
    model.put("tag", id);
    System.out.println("The ID # deleted is " + id); //this prints out the deleted id number on the console 
    return "delete";

  }



@PostMapping(path = "/delete", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE }, params = "action=delete")
  public String deleteDelete(Map<String, Object> model, Rectangle rectangle, @RequestParam("id") int id) throws Exception{

    try (Connection connection = dataSource.getConnection()) {
          Statement stmt = connection.createStatement();
          stmt.executeUpdate("DELETE FROM Rectangle WHERE id = " + id + ";");
          String message = "Succesfully deleted " + id; 
           model.put("message", message);

           model.put("tag", Integer.toString(id));
          return "delete";
        } catch (Exception e) {
          return "error";
        }
    
      }


//If we press cancel before deleting we should go back to the root directory
@PostMapping(path = "/delete", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE }, params = "action=cancel")
  public String cancelDelete(Rectangle rectangle){

    return "redirect:/";

  }

  //-----------------------------------DO NOT TOUCH THIS PART------------------------------------------
  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
