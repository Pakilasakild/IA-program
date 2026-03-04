# IA Base Application

Base JavaFX application for IB HL Computer Science IA projects with MySQL database.

## Structure

### Main Classes

- **MainApplication.java** - Main application class, starts JavaFX application
- **DatabaseConnection.java** - Singleton class for database connection management
- **BaseDAO.java** - Base DAO class, simplifies database operations
- **BaseController.java** - Base Controller class, simplifies creating new windows
- **WindowManager.java** - Class for window management

### How to Create a New Window

1. **Create FXML file** `src/main/resources/com/ia/ia_base/views/YourView.fxml`
2. **Create Controller class** `src/main/java/com/ia/ia_base/controllers/YourController.java` that extends `BaseController`
3. **Add menu item** or use `WindowManager.openWindow()` method

#### Example:

**YourView.fxml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<VBox xmlns="http://javafx.com/javafx"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="com.ia.ia_base.controllers.YourController">
    <Label text="Your Window"/>
</VBox>
```

**YourController.java:**
```java
package com.ia.ia_base.controllers;

public class YourController extends BaseController {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Your initialization
    }
}
```

**Open window:**
```java
openNewWindow("views/YourView.fxml", "Your Window Title");
```

### How to Work with Database

1. **Create Entity class** (e.g., `UserEntity.java`)
2. **Create DAO class** that extends `BaseDAO<YourEntity>`
3. **Implement `mapResultSetToEntity()` method**

#### Example:

**UserEntity.java:**
```java
public class UserEntity {
    private int id;
    private String name;
    // getters, setters
}
```

**UserDAO.java:**
```java
public class UserDAO extends BaseDAO<UserEntity> {
    public List<UserEntity> findAll() throws SQLException {
        return executeQuery("SELECT * FROM users");
    }
    
    @Override
    protected UserEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        UserEntity user = new UserEntity();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        return user;
    }
}
```

### Database Settings

**By default, database is NOT USED.** Application starts without DB.

**Enable database:**
Edit `AppConfig.java` file and change:
```java
private static boolean useDatabase = true;
```

Or programmatically:
```java
AppConfig.setUseDatabase(true);
```

**Database connection settings:**
Edit `DatabaseConnection.java` class:
- `DB_URL` - database server address
- `DB_NAME` - database name
- `DB_USER` - username
- `DB_PASSWORD` - password

### Running

**Without database (default):**
```bash
mvn clean javafx:run
```
Application will start without DB - perfect for UI development!

**With database:**
1. Enable DB usage in `AppConfig.java` file: `useDatabase = true`
2. Make sure MySQL server is running
3. Create database: `CREATE DATABASE ia_database;`
4. Run application:
   ```bash
   mvn clean javafx:run
   ```

### Add New Menu Item

**MainController.java** file:
```java
addMenuItem("Windows", "New Window", () -> {
    openNewWindow("views/NewView.fxml", "New Window");
});
```

## Examples

Project includes example classes:
- `ExampleController` - example controller
- `ExampleDAO` - example DAO class
- `ExampleEntity` - example Entity class

## Model Creation

Detailed information about model creation, inheritance and composition:
- **[MODELS.md](MODELS.md)** - Full guide with examples

## Requirements

- Java 24
- Maven
- MySQL Server
- JavaFX 24
