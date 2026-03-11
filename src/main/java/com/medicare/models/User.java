package com.medicare.models;

public class User {

    public enum Role { Admin, Doctor, Receptionist }

    private int    userId;
    private String username;
    private String password;
    private String fullName;
    private Role   role;

    public User() { }

    public User(int userId, String username, String password, String fullName, Role role) {
        this.userId   = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role     = role;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getUserId()  { return userId;   }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public Role   getRole()     { return role;     }

    /** Convenience method for EL comparisons: ${user.roleName == 'Admin'} */
    public String getRoleName() { return role != null ? role.name() : ""; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setUserId(int userId)      { this.userId   = userId;   }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(Role role)           { this.role     = role;     }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "', role=" + role + "}";
    }
}
