# Task Management System - Authentication & Authorization Setup

## Overview
This document describes the authentication and authorization system implemented according to the strict role-based requirements.

## System Architecture

### Backend (Spring Boot 4.0.1)

#### 1. **Authentication Flow**
- **Login Only**: Public registration has been removed
- **JWT-based**: Stateless authentication using JSON Web Tokens
- **Default Admin**: System creates a default Manager account on first startup

#### 2. **User Roles**
```java
public enum Role {
    MANAGER,    // Can create users, tasks, manage everything
    DEVELOPER,  // Can work on assigned tasks
    REVIEWER    // Can review and approve/reject tasks
}
```

#### 3. **Security Configuration**
- **Method-level security**: `@PreAuthorize` annotations enforce role checks
- **Stateless sessions**: No server-side session storage
- **JWT Filter**: Validates tokens on every request
- **Public endpoints**: Only `/api/auth/login` is publicly accessible

#### 4. **Role-Based Permissions**

**MANAGER can:**
- Create new users (POST `/users`)
- Create tasks (POST `/tasks`)
- Update task details (PUT `/tasks/{id}`)
- View all tasks and audit logs
- Assign/reassign developers and reviewers

**DEVELOPER can:**
- View assigned tasks
- Move tasks: BACKLOG → IN_PROGRESS → REVIEW
- Cannot create tasks or users
- Cannot approve tasks

**REVIEWER can:**
- View tasks assigned for review
- Move tasks: REVIEW → DONE (approve)
- Move tasks: REVIEW → IN_PROGRESS (reject)
- Cannot create tasks or users

#### 5. **Key Backend Files**

**Authentication:**
- `AuthController.java` - Login endpoint only
- `AuthService.java` - Authentication logic
- `JwtUtil.java` - Token generation/validation
- `SecurityConfig.java` - Spring Security configuration
- `CustomUserDetailsService.java` - User loading for authentication

**Authorization:**
- `PermissionService.java` - Business logic for role checks
- `@PreAuthorize` annotations on controllers

**Data Seeding:**
- `DataSeeder.java` - Creates default admin account

**Default Credentials:**
```
Email: admin@taskapp.com
Password: password
```

### Frontend (React + Vite)

#### 1. **Authentication Context**
- `AuthContext.jsx` - Global auth state management
- Decodes JWT to extract user details (userId, role, email)
- Stores token in localStorage
- Provides `login()` and `logout()` functions

#### 2. **Protected Routes**
- `ProtectedRoute.jsx` - Wrapper for authenticated pages
- Redirects to `/login` if not authenticated

#### 3. **Role-Based UI**
The Dashboard conditionally renders based on user role:

```jsx
{user?.role === 'MANAGER' && (
    <Button onClick={() => setIsCreatingUser(true)}>
        New User
    </Button>
)}
```

**Manager sees:**
- "New User" button
- "New Task" button
- User creation form
- Task creation form
- Edit task functionality

**Developer/Reviewer sees:**
- Only their assigned tasks
- State transition buttons appropriate to their role
- Task details view

#### 4. **Key Frontend Files**
- `App.jsx` - Routing (no public register route)
- `Login.jsx` - Login page (no registration link)
- `Dashboard.jsx` - Main board with role-based features
- `TaskDetails.jsx` - Task details and history
- `AuthContext.jsx` - Authentication state management

## API Endpoints

### Public
- `POST /api/auth/login` - Login with email/password

### Protected (Requires Authentication)

**User Management (MANAGER only):**
- `POST /users` - Create new user
- `GET /users/{id}` - Get user by ID
- `GET /users/role/{role}` - Get users by role

**Task Management:**
- `POST /tasks` - Create task (MANAGER only)
- `GET /tasks` - Get all tasks
- `GET /tasks/{id}` - Get task by ID
- `PUT /tasks/{id}` - Update task details (MANAGER only)
- `PUT /tasks/{id}/state?state={STATE}` - Change task state (role-dependent)

**Audit Logs:**
- `GET /tasks/{id}/history` - Get task history

## Workflow Rules (Enforced by Backend)

### Task State Transitions
```
BACKLOG → IN_PROGRESS → REVIEW → DONE
```

**Transition Rules:**
1. **BACKLOG → IN_PROGRESS**: Developer only, no unresolved dependencies
2. **IN_PROGRESS → REVIEW**: Developer only
3. **REVIEW → DONE**: Reviewer only (approve)
4. **REVIEW → IN_PROGRESS**: Reviewer only (reject)

### Business Rules
1. Assignee must be a DEVELOPER
2. Reviewer must be a REVIEWER
3. Assignee and Reviewer must be different users
4. Only MANAGER can create/update tasks
5. Only MANAGER can create users
6. Users can only transition tasks they're assigned to

## Setup Instructions

### 1. Backend Setup
```bash
# Navigate to project root
cd TaskApp

# Build the project (if Maven is installed)
mvn clean install

# Run the application
mvn spring-boot:run
```

**Note:** The application will automatically create the default admin user on first startup.

### 2. Frontend Setup
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will be available at `http://localhost:5173`

### 3. First Login
1. Open `http://localhost:5173`
2. Login with default credentials:
   - Email: `admin@taskapp.com`
   - Password: `password`
3. Create additional users (Developers, Reviewers) using the "New User" button

## Security Features

1. **Password Hashing**: BCrypt with salt
2. **JWT Expiration**: 10 hours
3. **Token Validation**: On every request
4. **Role Enforcement**: Both frontend (UX) and backend (security)
5. **Audit Trail**: All important actions logged

## Database Schema

**Users Table:**
- userId (PK)
- displayName
- email (unique)
- password (hashed)
- role (MANAGER/DEVELOPER/REVIEWER)

**Tasks Table:**
- taskId (PK)
- taskName
- description
- state (BACKLOG/IN_PROGRESS/REVIEW/DONE)
- assigneeId (FK → Users)
- reviewerId (FK → Users)
- creationTime

**Audit Logs Table:**
- auditId (PK)
- taskId (FK → Tasks, nullable)
- actorUserId (FK → Users)
- actionType (TASK_CREATED/TASK_STATE_CHANGED)
- previousState
- newState
- comment
- createdAt

## Troubleshooting

### Backend won't start
- Check MySQL is running
- Verify database credentials in `application.properties`
- Ensure port 8080 is available

### Frontend can't connect to backend
- Verify backend is running on port 8080
- Check Vite proxy configuration in `vite.config.js`
- Check browser console for CORS errors

### Login fails
- Verify user exists in database
- Check password is correct
- Check JWT secret key is configured
- Review backend logs for authentication errors

## Next Steps

According to the original requirements, the following features should be implemented next:

1. **Task Dependencies** - Blocking relationships between tasks
2. **Deadlines** - START_BY and FINISH_BY constraints
3. **Escalation** - Automatic deadline violation handling
4. **Scheduled Jobs** - Background processing for deadline checks
5. **Enhanced Audit Logging** - More detailed action tracking

These features are already partially scaffolded in the codebase (e.g., `TaskDependencyService`, `AuditLogService`).
