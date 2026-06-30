package uniplanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserService {
	
	
	//main validation method for users
	public boolean validateUser(User user) {
		if (user == null) {
			return false;
		}
		if (isBlank(user.getUsername())) {
			return false;
		}
		if (isBlank(user.getPassword())) {
			return false;
		}
		if(!isValidEmail(user.getEmail())) {
			return false;
		}
		return true;
	}
	
	//some helper validation methods
	public boolean isValidEmail(String email) {
		if(isBlank(email)) {
			return false;
		}
		//checks for format blank @ blank. blank with at least 2 letters in the domain 
		return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
				
	}
	public boolean isBlank(String input) {
		return input == null || input.trim().isEmpty();
	}
	public User findByEmail(String email) {
		if(isBlank(email)) {
			return null;
		}
		String sql = "SELECT * FROM users WHERE email = ?";
		try (Connection conn = DatabaseConnection.getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return new User(rs.getInt("id"),rs.getString("username"),rs.getString("password"),rs.getString("email"));}}catch(SQLException e) {
System.out.println("Error finding user by email: " + e.getMessage());}
		return null;}
			
	public String hashPassword(String password) {
		if(isBlank(password)) {
			return null;
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = digest.digest(password.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashedBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');}
				hexString.append(hex);}
			return hexString.toString();
		}
		catch (NoSuchAlgorithmException e ) {
			System.out.println("Erorr hashing pasword: " + e.getMessage());
			return null;}}
	
    
    // Register a new user
    public boolean registerUser(User user) {
    	
    	//checks fields are all valid
    	if(!validateUser(user)) {
    		System.out.println("User validation failed, user not registered");
    		return false;
    	}
    	//ensures no duplicate users are created
    	if (findByUsername(user.getUsername()) != null) {
    		System.out.println("Username already exists, user not registered");
    		return false;
    	}
    	if (findByEmail(user.getEmail()) != null) {
    		System.out.println("Email already exists, user not registered");
    		return false;
    	}
        String sql = "INSERT INTO users (id, username, password, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int nextId = getNextId();
            pstmt.setInt(1, nextId);
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, hashPassword(user.getPassword()));
            pstmt.setString(4, user.getEmail());
            pstmt.executeUpdate();
            
            user.setId(nextId);
            return true;
            
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    // Login - find user by username and password
    public User loginUser(String username, String password) {
    	
    	if(isBlank(username)||isBlank(password)) {
    		System.out.println("Username and password required for login");
    		return null;
    	}
    	
    	String hashedPassword = hashPassword(password);
    	
    	
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
        return null;
    }
    
    // Find user by username
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null;
    }
    
    // Get next available ID
    private int getNextId() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 FROM users";
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