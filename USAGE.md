# How to Use IA Base Application

## First Run

### Running WITHOUT Database (Recommended Initially)

**By default, the application starts WITHOUT a database!** This is perfect for UI development.

Simply run:
```bash
cd IA_base
mvn clean javafx:run
```

The application will start without any errors - you can create windows and UI without DB.

### Running WITH Database

When you're ready to use the database:

#### 1. Enable Database Usage

Open `AppConfig.java` file and change:
```java
private static boolean useDatabase = true;
```

Or programmatically in `MainApplication.java` file before `start()` method:
```java
AppConfig.setUseDatabase(true);
```

#### 2. Configure Database Connection

Open `DatabaseConnection.java` file and change:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/";
private static final String DB_NAME = "ia_database";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

#### 3. Create Database

Run `database_setup.sql` script on MySQL server:
```bash
mysql -u root -p < database_setup.sql
```

Or use MySQL Workbench or phpMyAdmin.

#### 4. Run Application

```bash
cd IA_base
mvn clean javafx:run
```

## How to Add a New Window

### 1. Create FXML File

Create file: `src/main/resources/com/ia/ia_base/views/YourWindow.fxml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<VBox xmlns="http://javafx.com/javafx"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="com.ia.ia_base.controllers.YourController">
    <Label text="Your Window"/>
    <Button text="Close" fx:id="closeButton"/>
</VBox>
```

### 2. Create Controller Class

Create file: `src/main/java/com/ia/ia_base/controllers/YourController.java`

```java
package com.ia.ia_base.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class YourController extends BaseController {
    @FXML
    private Button closeButton;
    
    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        closeButton.setOnAction(e -> closeWindow());
    }
}
```

### 3. Open Window

**From menu:**
Edit `MainController.java` and add:
```java
addMenuItem("Windows", "Your Window", () -> {
    openNewWindow("views/YourWindow.fxml", "Your Window Title");
});
```

**From another window:**
```java
openNewWindow("views/YourWindow.fxml", "Your Window Title");
```

## How to Work with Database

### 1. Create Entity Class

```java
package com.ia.ia_base.database;

public class UserEntity {
    private int id;
    private String name;
    private String email;
    
    // Constructors, getters, setters
}
```

### 2. Create DAO Class

```java
package com.ia.ia_base.database;

import com.ia.ia_base.database.dao.BaseDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAO extends BaseDAO<UserEntity> {

    public List<UserEntity> findAll() throws SQLException {
        return executeQuery("SELECT * FROM users");
    }

    public UserEntity findById(int id) throws SQLException {
        List<UserEntity> results = executeQuery("SELECT * FROM users WHERE id = ?", id);
        return results.isEmpty() ? null : results.get(0);
    }

    public int create(UserEntity user) throws SQLException {
        return executeUpdate("INSERT INTO users (name, email) VALUES (?, ?)",
                user.getName(), user.getEmail());
    }

    @Override
    protected UserEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        UserEntity user = new UserEntity();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}
```

### 3. Use DAO in Controller

```java
public class UserController extends BaseController {
    private UserDAO userDAO = new UserDAO();
    
    public void loadUsers() {
        try {
            List<UserEntity> users = userDAO.findAll();
            // Display data
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

## Useful Methods

### BaseController methods:
- `openNewWindow(fxmlPath, title)` - opens new window
- `openModalWindow(fxmlPath, title)` - opens modal window
- `closeWindow()` - closes current window

### WindowManager methods:
- `WindowManager.openWindow(fxmlPath, title)` - opens window
- `WindowManager.openWindow(fxmlPath, title, width, height)` - opens window with dimensions
- `WindowManager.closeWindow(stage)` - closes window

### DatabaseConnection methods:
- `DatabaseConnection.getInstance().getConnection()` - gets connection
- `DatabaseConnection.getInstance().closeConnection()` - closes connection
- `DatabaseConnection.getInstance().isConnected()` - checks if connected

## Tips

1. **Always use BaseController** - simplifies work
2. **Use BaseDAO** - automatically manages connections
3. **Handle errors** - use try-catch blocks with SQLException
4. **Close windows** - use `closeWindow()` method
5. **Test database** - check if connection works before creating DAO

## Database Management

**Enable DB:**
```java
AppConfig.setUseDatabase(true);
```

**Disable DB:**
```java
AppConfig.setUseDatabase(false);
```

**Check if DB is used:**
```java
if (AppConfig.isUseDatabase()) {
    // DB is used
}
```

## Troubleshooting

**Problem:** Cannot connect to database
- Check if DB is enabled: `AppConfig.isUseDatabase()`
- Check if MySQL server is running
- Check database settings in `DatabaseConnection.java`
- Make sure database is created
- If not ready to use DB yet, leave `useDatabase = false`

**Problem:** Window doesn't open
- Check if FXML file is in correct path
- Check if controller class is correctly specified in FXML file
- Check if controller class extends BaseController

**Problem:** Module errors
- Check `module-info.java` file
- Make sure all required modules are added

