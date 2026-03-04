# Model Creation, Inheritance and Composition

This document explains how to create models (Entity classes), use inheritance and composition in IB HL Computer Science IA projects.

## 1. Model (Entity) Creation

A Model (Entity) is a class that reflects the database table structure. Each table has its own Entity class.

### Simplest Example

Suppose we have a `students` table:
```sql
CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    age INT
);
```

**StudentEntity.java:**
```java
package com.ia.ia_base.database.models;

/**
 * Student model (Entity class)
 * Reflects 'students' table structure
 */
public class StudentEntity {
    // Private fields - correspond to table columns
    private int id;
    private String name;
    private String email;
    private int age;
    
    // Constructors
    public StudentEntity() {
        // Empty constructor - required for DAO
    }
    
    public StudentEntity(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
    
    // Getters and Setters (required for all fields)
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    // toString() method - useful for debugging
    @Override
    public String toString() {
        return "StudentEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
```

### StudentDAO.java (for working with database)

```java
package com.ia.ia_base.database;

import com.ia.ia_base.database.dao.BaseDAO;
import com.ia.ia_base.database.models.StudentEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StudentDAO extends BaseDAO<StudentEntity> {

    public List<StudentEntity> findAll() throws SQLException {
        return executeQuery("SELECT * FROM students");
    }

    public StudentEntity findById(int id) throws SQLException {
        List<StudentEntity> results = executeQuery("SELECT * FROM students WHERE id = ?", id);
        return results.isEmpty() ? null : results.get(0);
    }

    public int create(StudentEntity student) throws SQLException {
        String sql = "INSERT INTO students (name, email, age) VALUES (?, ?, ?)";
        return executeUpdate(sql, student.getName(), student.getEmail(), student.getAge());
    }

    @Override
    protected StudentEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        StudentEntity student = new StudentEntity();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setAge(rs.getInt("age"));
        return student;
    }
}
```

---

## 2. Inheritance

Inheritance is used when we have related classes that share common properties.

### Example: People System

**BaseEntity.java** (base class):
```java
package com.ia.ia_base.database.models;

/**
 * Base class for all person classes
 * Has common properties that other classes inherit
 */
public abstract class BasePersonEntity {
    protected int id;
    protected String name;
    protected String email;
    protected String phone;
    
    // Constructors
    public BasePersonEntity() {}
    
    public BasePersonEntity(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
```

**StudentEntity.java** (extends BasePersonEntity):
```java
package com.ia.ia_base.database.models;

/**
 * Student class - extends BasePersonEntity
 * Has additional student-specific properties
 */
public class StudentEntity extends BasePersonEntity {
    private String studentNumber;
    private int year;
    private double gpa;
    
    public StudentEntity() {
        super(); // Calls parent class constructor
    }
    
    public StudentEntity(String name, String email, String phone, 
                         String studentNumber, int year, double gpa) {
        super(name, email, phone); // Calls parent class constructor
        this.studentNumber = studentNumber;
        this.year = year;
        this.gpa = gpa;
    }
    
    // Only StudentEntity specific getters and setters
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    
    @Override
    public String toString() {
        return "StudentEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", year=" + year +
                ", gpa=" + gpa +
                '}';
    }
}
```

**TeacherEntity.java** (also extends BasePersonEntity):
```java
package com.ia.ia_base.database.models;

/**
 * Teacher class - extends BasePersonEntity
 * Has teacher-specific properties
 */
public class TeacherEntity extends BasePersonEntity {
    private String employeeId;
    private String department;
    private double salary;
    
    public TeacherEntity() {
        super();
    }
    
    public TeacherEntity(String name, String email, String phone,
                        String employeeId, String department, double salary) {
        super(name, email, phone);
        this.employeeId = employeeId;
        this.department = department;
        this.salary = salary;
    }
    
    // Only TeacherEntity specific getters and setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
```

### Database Structure with Inheritance

**students table:**
```sql
CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    student_number VARCHAR(50) UNIQUE,
    year INT,
    gpa DECIMAL(3,2)
);
```

**teachers table:**
```sql
CREATE TABLE teachers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    employee_id VARCHAR(50) UNIQUE,
    department VARCHAR(100),
    salary DECIMAL(10,2)
);
```

---

## 3. Composition

Composition is used when one class has another class's object as its part.

### Example: Order System

**AddressEntity.java** (composition part):
```java
package com.ia.ia_base.database.models;

/**
 * Address class - used in composition
 */
public class AddressEntity {
    private String street;
    private String city;
    private String postalCode;
    private String country;
    
    public AddressEntity() {}
    
    public AddressEntity(String street, String city, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }
    
    // Getters and Setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    @Override
    public String toString() {
        return street + ", " + city + ", " + postalCode + ", " + country;
    }
}
```

**CustomerEntity.java** (has AddressEntity as composition):
```java
package com.ia.ia_base.database.models;

/**
 * Customer class - uses composition with AddressEntity
 */
public class CustomerEntity {
    private int id;
    private String name;
    private String email;
    private AddressEntity address; // Composition - Customer has Address
    
    public CustomerEntity() {
        this.address = new AddressEntity(); // Create empty address
    }
    
    public CustomerEntity(String name, String email, AddressEntity address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // Composition getter and setter
    public AddressEntity getAddress() { return address; }
    public void setAddress(AddressEntity address) { this.address = address; }
    
    @Override
    public String toString() {
        return "CustomerEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address=" + address +
                '}';
    }
}
```

### Database Structure with Composition

**customers table:**
```sql
CREATE TABLE customers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    street VARCHAR(100),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100)
);
```

**CustomerDAO.java** (with composition):

```java
package com.ia.ia_base.database;

import com.ia.ia_base.database.dao.BaseDAO;
import com.ia.ia_base.database.models.CustomerEntity;
import com.ia.ia_base.database.models.AddressEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomerDAO extends BaseDAO<CustomerEntity> {

    public List<CustomerEntity> findAll() throws SQLException {
        return executeQuery("SELECT * FROM customers");
    }

    public int create(CustomerEntity customer) throws SQLException {
        String sql = "INSERT INTO customers (name, email, street, city, postal_code, country) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        AddressEntity addr = customer.getAddress();
        return executeUpdate(sql,
                customer.getName(),
                customer.getEmail(),
                addr.getStreet(),
                addr.getCity(),
                addr.getPostalCode(),
                addr.getCountry()
        );
    }

    @Override
    protected CustomerEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(rs.getInt("id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));

        // Create AddressEntity object from database
        AddressEntity address = new AddressEntity();
        address.setStreet(rs.getString("street"));
        address.setCity(rs.getString("city"));
        address.setPostalCode(rs.getString("postal_code"));
        address.setCountry(rs.getString("country"));

        customer.setAddress(address);
        return customer;
    }
}
```

---

## 4. More Complex Example: Inheritance + Composition

**ProductEntity.java** (base class):
```java
package com.ia.ia_base.database.models;

public abstract class ProductEntity {
    protected int id;
    protected String name;
    protected double price;
    protected String description;
    
    // Getters, setters, constructors...
}
```

**BookEntity.java** (extends ProductEntity + has composition):
```java
package com.ia.ia_base.database.models;

public class BookEntity extends ProductEntity {
    private String isbn;
    private String author;
    private PublisherEntity publisher; // Composition
    
    // Constructors, getters, setters...
}
```

**PublisherEntity.java**:
```java
package com.ia.ia_base.database.models;

public class PublisherEntity {
    private String name;
    private String country;
    // ...
}
```

---

## 5. Usage in Controller

```java
package com.ia.ia_base.controllers;

import com.ia.ia_base.database.StudentDAO;
import com.ia.ia_base.database.models.StudentEntity;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class StudentController extends BaseController {
    private StudentDAO studentDAO = new StudentDAO();
    
    @FXML
    private TableView<StudentEntity> tableView;
    
    @FXML
    private void loadStudents() {
        try {
            List<StudentEntity> students = studentDAO.findAll();
            // Load into TableView
            tableView.getItems().clear();
            tableView.getItems().addAll(students);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

---

## Main Rules

1. **Model = Table**: Each table has its own Entity class
2. **Inheritance**: Use when classes have common properties
3. **Composition**: Use when one class has another class's object
4. **Getters/Setters**: Always create for all fields
5. **toString()**: Useful for debugging
6. **DAO class**: Each Entity must have corresponding DAO class

## When to Use What?

- **Inheritance**: When you have "is a" relationship (Student IS A Person)
- **Composition**: When you have "has a" relationship (Customer HAS AN Address)
- **Simple model**: When neither inheritance nor composition is needed

