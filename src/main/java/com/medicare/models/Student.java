package com.medicare.models;

import java.time.LocalDate;

public class Student {

    private String    regNumber;
    private String    fullName;
    private LocalDate dob;
    private String    gender;
    private String    faculty;
    private String    contact;

    public Student() { }

    public Student(String regNumber, String fullName, LocalDate dob,
                   String gender, String faculty, String contact) {
        this.regNumber = regNumber;
        this.fullName  = fullName;
        this.dob       = dob;
        this.gender    = gender;
        this.faculty   = faculty;
        this.contact   = contact;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String    getRegNumber() { return regNumber; }
    public String    getFullName()  { return fullName;  }
    public LocalDate getDob()       { return dob;       }
    public String    getGender()    { return gender;    }
    public String    getFaculty()   { return faculty;   }
    public String    getContact()   { return contact;   }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }
    public void setFullName(String fullName)   { this.fullName  = fullName;  }
    public void setDob(LocalDate dob)          { this.dob       = dob;       }
    public void setGender(String gender)       { this.gender    = gender;    }
    public void setFaculty(String faculty)     { this.faculty   = faculty;   }
    public void setContact(String contact)     { this.contact   = contact;   }

    @Override
    public String toString() {
        return "Student{regNumber='" + regNumber + "', fullName='" + fullName + "'}";
    }
}
