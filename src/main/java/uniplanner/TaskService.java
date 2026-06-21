package uniplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    
    // Add a new task
    public boolean addTask(Task task) {
        String sql = "INSERT INTO tasks (id, title, dueDate, priority, status, course, userId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int nextId = getNextId();
            pstmt.setInt(1, nextId);
            pstmt.setString(2, task.getTitle());
            pstmt.setDate(3, java.sql.Date.valueOf(task.getDueDate()));
            pstmt.setString(4, task.getPriority());
            pstmt.setString(5, task.getStatus());
            pstmt.setString(6, task.getCourse());
            pstmt.setInt(7, task.getUserId());
            pstmt.executeUpdate();
            
            task.setId(nextId);
            return true;
            
        } catch (SQLException e) {
            System.out.println("Error adding task: " + e.getMessage());
            return false;
        }
    }
    
    // Get all tasks for a user
    public List<Task> getTasksByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE userId = ? ORDER BY dueDate ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Task task = new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getDate("dueDate").toLocalDate(),
                    rs.getString("priority"),
                    rs.getString("status"),
                    rs.getString("course"),
                    rs.getInt("userId")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error getting tasks: " + e.getMessage());
        }
        return tasks;
    }
    
    // Update a task
    public boolean updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, dueDate = ?, priority = ?, status = ?, course = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, task.getTitle());
            pstmt.setDate(2, java.sql.Date.valueOf(task.getDueDate()));
            pstmt.setString(3, task.getPriority());
            pstmt.setString(4, task.getStatus());
            pstmt.setString(5, task.getCourse());
            pstmt.setInt(6, task.getId());
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
            return false;
        }
    }
    
    // Delete a task
    public boolean deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, taskId);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
            return false;
        }
    }
    
    // Get next available ID
    private int getNextId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM tasks";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting next ID: " + e.getMessage());
        }
        return 1;
    }
}