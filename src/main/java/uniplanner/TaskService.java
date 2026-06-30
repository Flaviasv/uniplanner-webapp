package uniplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    
	//Validation method
	public boolean validateTask(Task task) {
		if (task == null) {
			return false;
		}
		if (isBlank(task.getTitle())) {
			return false;
		}
		if (task.getDueDate() == null) {
			return false;
		}
		if (isBlank(task.getCourse())) {
			return false;
		}
		if (task.getUserId() <= 0) {
			return false;
		}
		if(!isValidPriority(task.getPriority())) {
			return false;
		}
		if(!isValidStatus(task.getStatus()))
		{return false;
		}
		return true;
	}
	
	//isBlank helper method
	public boolean isBlank(String input) {
return input == null || input.trim().isEmpty();}
	
	
	// priority validation helper method
	public boolean isValidPriority(String priority) {
		return priority != null && (priority.equals("High") || priority.equals("Medium") || priority.equals("Low"));}
	
	//another for status validation
	public boolean isValidStatus(String status) {
		return status != null && (status.equals("To Do") || status.equals("In Progress") || status.equals("Completed") );
				
	}
	
	
    // Add a new task
    public boolean addTask(Task task) {
    	
    	//added validation to addTask
    	if (!validateTask(task)) {System.out.println("Task validation failed, task was not added");
    	return false;
    	}
    	
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
        //checks for valid user ID
        if(userId <=0) {
        	System.out.println("User ID invalid, no tasks were loaded");
        	return tasks;
        }
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
    	
    	//validation for task again
    	if(!validateTask(task)) {
    		System.out.println("Task validation failed, task was not updated");
    		return false;
    	}
    	//additional validation to make sure the task already exists to be updated
    	if(task.getId()<=0) {
    		System.out.println("Task must already exist to be updated, no matching task found");
    		return false;
    	}
    	
    	
        String sql = "UPDATE tasks SET title = ?, dueDate = ?, priority = ?, status = ?, course = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, task.getTitle());
            pstmt.setDate(2, java.sql.Date.valueOf(task.getDueDate()));
            pstmt.setString(3, task.getPriority());
            pstmt.setString(4, task.getStatus());
            pstmt.setString(5, task.getCourse());
            pstmt.setInt(6, task.getId());
            //added userID based security so only the same user can edit, changed the return to be based on anything changing
            pstmt.setInt(7, task.getUserId());
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated >0;
            
        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
            return false;
        }
    }
    
    // Delete a task
    public boolean deleteTask(int taskId, int userId) {
    	
    	//checking user and task exist
    	if (taskId <= 0) {
    		System.out.println("Task ID is invalid, task was not deleted");
    		return false;
    	}
    	if(userId <= 0) {
    		System.out.println("Invalid user ID, task was not deleted");
    		return false;
    	}
    	
    	//added userID validation as well so users can only affect their own tasks
        String sql = "DELETE FROM tasks WHERE id = ? and userId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, taskId);
            pstmt.setInt(2, userId);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted> 0;
            
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