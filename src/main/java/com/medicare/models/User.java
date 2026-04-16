package com.medicare.models;

import java.time.LocalDate;
import java.io.Serializable;

public class User implements Serializable {

    public enum Role { Admin, Doctor, Receptionist, Technician }

    private int       userId;
    private String    username;
    private String    passwordHash;
    private String    firstName;
    private String    lastName;
    private Role      role;
    private String    email;
    private String    phone;
    private LocalDate dateOfEmployment;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public User() { }

    public User(int userId, String username, String passwordHash, String firstName,
                String lastName, Role role, String email, String phone,
                LocalDate dateOfEmployment, LocalDate createdAt, LocalDate updatedAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.dateOfEmployment = dateOfEmployment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int       getUserId()            { return userId;            }
    public String    getUsername()          { return username;          }
    public String    getPasswordHash()      { return passwordHash;      }
    public String    getFirstName()         { return firstName;         }
    public String    getLastName()          { return lastName;          }
    public Role      getRole()              { return role;              }
    public String    getEmail()             { return email;             }
    public String    getPhone()             { return phone;             }
    public LocalDate getDateOfEmployment()  { return dateOfEmployment;  }
    public LocalDate getCreatedAt()         { return createdAt;         }
    public LocalDate getUpdatedAt()         { return updatedAt;         }

    /** Convenience method for EL comparisons: ${user.roleName == 'Admin'} */
    public String getRoleName() { return role != null ? role.name() : ""; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setUserId(int userId)                    { this.userId = userId;                    }
    public void setUsername(String username)             { this.username = username;                }
    public void setPasswordHash(String passwordHash)     { this.passwordHash = passwordHash;        }
    public void setFirstName(String firstName)           { this.firstName = firstName;              }
    public void setLastName(String lastName)             { this.lastName = lastName;                }
    public void setRole(Role role)                       { this.role = role;                        }
    public void setEmail(String email)                   { this.email = email;                      }
    public void setPhone(String phone)                   { this.phone = phone;                      }
    public void setDateOfEmployment(LocalDate dateOfEmployment) { this.dateOfEmployment = dateOfEmployment; }
    public void setCreatedAt(LocalDate createdAt)       { this.createdAt = createdAt;              }
    public void setUpdatedAt(LocalDate updatedAt)       { this.updatedAt = updatedAt;              }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "', firstName='" + firstName + "', lastName='" + lastName + "', role=" + role + "}";
    }
}
