-- Database creation script
-- Run this script on MySQL server

-- Create database
CREATE DATABASE IF NOT EXISTS ia_database;
USE ia_database;

-- Example table (you can delete and create your own)
CREATE TABLE IF NOT EXISTS example_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Example data
INSERT INTO example_table (name, description) VALUES
('Example 1', 'This is the first example'),
('Example 2', 'This is the second example');

-- Check data
SELECT * FROM example_table;

