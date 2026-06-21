-- UniPlanner Database Schema

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS tasks (
    id INT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    dueDate DATE,
    priority VARCHAR(10) CHECK (priority IN ('High', 'Medium', 'Low')),
    status VARCHAR(20) CHECK (status IN ('To Do', 'In Progress', 'Completed')),
    course VARCHAR(50),
    userId INT NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);