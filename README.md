Here is the logical data model for your 6 tables. This defines the structure and purpose without the SQL syntax.

### 1. Users Table (System Staff)

This table manages access control for Admin, Doctor, and Receptionist roles.

| Column Name | Description | Data Type |
| --- | --- | --- |
| **UserID** | Unique ID for each staff member | Integer |
| **Username** | Login name | String |
| **Password** | Encrypted password | String |
| **Full_Name** | Actual name of the staff member | String |
| **Role** | Access level (Admin, Doctor, Receptionist) | Enum |

---

### 2. Students Table (Patient Profiles)

Stores the permanent biographical data of the students.

| Column Name | Description | Data Type |
| --- | --- | --- |
| **RegNumber** | Student’s University Registration ID | String (Primary Key) |
| **FullName** | Name as per University records | String |
| **DOB** | Date of Birth (for age calculation) | Date |
| **Gender** | Male / Female | String |
| **Faculty** | Engineering, Agriculture, Science, etc. | String |
| **Contact** | Phone number for emergency | String |

---

### 3. Medical Visits Table (Consultations)

The core record created every time a student visits the clinic.

| Column Name | Description | Data Type |
| --- | --- | --- |
| **VisitID** | Unique ID for the visit | Integer |
| **RegNumber** | Link to the Student table | String (Foreign Key) |
| **DoctorID** | Link to the UserID who treated them | Integer (Foreign Key) |
| **VisitDate** | Date and time of the visit | DateTime |
| **Symptoms** | Student's complaints | Text |
| **Diagnosis** | Doctor’s finding after checkup | Text |

---

### 4. Prescriptions Table

Detailed list of medicine provided during a specific visit.

| Column Name | Description | Data Type |
| --- | --- | --- |
| **PrescriptionID** | Unique ID for the line item | Integer |
| **VisitID** | Link to the specific Medical Visit | Integer (Foreign Key) |
| **MedicineName** | Name of the drug prescribed | String |
| **Dosage** | Strength and frequency (e.g., 500mg, 2x daily) | String |
| **Duration** | Number of days to take it | String |

---

### 5. Treatment Notes Table

Captures supplementary clinical observations that don't fit into a standard diagnosis.

| Column Name | Description | Data Type |
| --- | --- | --- |
| **NoteID** | Unique ID for the note | Integer |
| **VisitID** | Link to the specific Medical Visit | Integer (Foreign Key) |
| **ClinicalNotes** | Detailed doctor's observations | Text |
| **FollowUpDate** | When the student should return (if needed) | Date |

---

### 6. Audit Logs Table (Security)

To meet your requirement of monitoring system activity and confidentiality.

| Column Name | Description | Data Type |
| --- | --- | --- |
| **LogID** | Unique ID for the log entry | Integer |
| **UserID** | Who performed the action | Integer (Foreign Key) |
| **Action** | What they did (e.g., "Deleted Patient", "Viewed History") | String |
| **Timestamp** | Exact date and time of the action | DateTime |
| **IP_Address** | The computer used to access the system | String |

---

### Relationship Logic

* **Patients to Visits:** 1 to Many (One student has many visits).
* **Visits to Prescriptions:** 1 to Many (One visit can have multiple medicines).
* **Users to Visits:** 1 to Many (One doctor records many visits).
* **Users to Logs:** 1 to Many (One user generates many logs).

Would you like the Java "Entity" classes for these tables to use in your Servlet code?