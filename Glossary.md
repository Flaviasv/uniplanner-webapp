# Database Layer Glossary - UniPlanner

**Package:** `uniplanner`

---

## DatabaseConnection.java

| Method | Description | Returns |
|--------|-------------|---------|
| `getConnection()` | Establishes connection to HSQLDB | `Connection` |
| `createTables()` | Creates `users` and `tasks` tables if they do not exist | `void` |

---

## User.java (Model)

### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | `int` | Unique user ID |
| `username` | `String` | User's login username |
| `password` | `String` | User's password |
| `email` | `String` | User's email address |

### Constructor

| Constructor | Parameters | Description |
|-------------|------------|-------------|
| `User()` | `id, username, password, email` | Creates a new User object |

### Getters and Setters

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getId()` | `int` | Returns user ID |
| `setId()` | `void` | Sets user ID |
| `getUsername()` | `String` | Returns username |
| `setUsername()` | `void` | Sets username |
| `getPassword()` | `String` | Returns password |
| `setPassword()` | `void` | Sets password |
| `getEmail()` | `String` | Returns email |
| `setEmail()` | `void` | Sets email |

---

## Task.java (Model)

### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | `int` | Unique task ID |
| `title` | `String` | Task title |
| `dueDate` | `LocalDate` | Due date of the task |
| `priority` | `String` | High, Medium, or Low |
| `status` | `String` | To Do, In Progress, or Completed |
| `course` | `String` | Course name |
| `userId` | `int` | ID of the user who owns the task |

### Constructor

| Constructor | Parameters | Description |
|-------------|------------|-------------|
| `Task()` | `id, title, dueDate, priority, status, course, userId` | Creates a new Task object |

### Getters and Setters

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getId()` | `int` | Returns task ID |
| `setId()` | `void` | Sets task ID |
| `getTitle()` | `String` | Returns task title |
| `setTitle()` | `void` | Sets task title |
| `getDueDate()` | `LocalDate` | Returns due date |
| `setDueDate()` | `void` | Sets due date |
| `getPriority()` | `String` | Returns priority |
| `setPriority()` | `void` | Sets priority |
| `getStatus()` | `String` | Returns status |
| `setStatus()` | `void` | Sets status |
| `getCourse()` | `String` | Returns course name |
| `setCourse()` | `void` | Sets course name |
| `getUserId()` | `int` | Returns user ID |
| `setUserId()` | `void` | Sets user ID |

---

## UserService.java

| Method | Parameters | Description | Returns |
|--------|------------|-------------|---------|
| `registerUser()` | `User user` | Inserts a new user into the database | `boolean` |
| `loginUser()` | `String username, String password` | Authenticates a user | `User` or `null` |
| `findByUsername()` | `String username` | Searches for a user by username | `User` or `null` |
| `findByEmail()` | `String email` | Searches for a user by email | `User` or `null` |
| `validateUser` | `User user` | Ensures all the fields in the user are correct and will not cause issues | `boolean` |
| `isValidEmail` | `String email` | checks to make sure the email has an appropriate format | `boolean` |
| `isBlank` | `String input` | checks for an empty string | `boolean` |
| `hashPassword` | `String password` | Creates a hash from the password, stores in database and checks against it, no plain text passwords are stored now uses SHA-256 | `String` |

---

## TaskService.java

| Method | Parameters | Description | Returns |
|--------|------------|-------------|---------|
| `addTask()` | `Task task` | Inserts a new task into the database | `boolean` |
| `getTasksByUserId()` | `int userId` | Retrieves all tasks for a user (ordered by due date) | `List<Task>` |
| `updateTask()` | `Task task` | Updates an existing task | `boolean` |
| `deleteTask()` | `int taskId` | Deletes a task by its ID | `boolean` |
| `validateTask` |`Task task` | checks the fields of task to make sure they are within bounds |`boolean` |
| `isValidStatus` |`String status` | ensures the status matches one of the presets allowed |`boolean` |
| `isValidPriority` |`String priority` | works like isValidStatus only for priorities |`boolean` |
| `isBlank` | `String input` | checks for an empty string | `boolean` |
---
