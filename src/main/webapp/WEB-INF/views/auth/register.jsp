<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - School Medical System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .register-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            width: 100%;
            max-width: 500px;
            padding: 40px;
        }
        .register-header {
            text-align: center;
            margin-bottom: 40px;
        }
        .register-header h1 {
            font-size: 28px;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 8px;
        }
        .register-header p {
            color: #7f8c8d;
            font-size: 14px;
        }
        .form-group {
            margin-bottom: 24px;
        }
        .form-label {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 8px;
            display: block;
        }
        .form-control, .form-select {
            border: 2px solid #ecf0f1;
            border-radius: 8px;
            padding: 12px 16px;
            font-size: 15px;
            transition: all 0.3s ease;
        }
        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        .btn-register {
            width: 100%;
            padding: 12px;
            font-weight: 600;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            border-radius: 8px;
            cursor: pointer;
            margin-top: 8px;
            transition: transform 0.2s ease;
        }
        .btn-register:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
        }
        .register-footer {
            text-align: center;
            margin-top: 24px;
            font-size: 14px;
        }
        .register-footer a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
        }
    </style>
</head>
<body>
    <div class="register-container">
        <div class="register-header">
            <h1><i class="bi bi-hospital"></i> Medical System</h1>
            <p>Register as a Staff Member</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center gap-2" role="alert">
                <i class="bi bi-exclamation-triangle-fill flex-shrink-0"></i>
                <div>${error}</div>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <div class="form-group">
                <label for="fullName" class="form-label">Full Name</label>
                <input id="fullName" name="fullName" type="text" class="form-control"
                       value="${fullName}" placeholder="Enter your full name" maxlength="120" required>
            </div>

            <div class="form-group">
                <label for="username" class="form-label">Username</label>
                <input id="username" name="username" type="text" class="form-control"
                       value="${username}" placeholder="Create a unique username" maxlength="80" required>
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input id="password" name="password" type="password" class="form-control"
                       placeholder="Enter a strong password" required>
                <small class="form-text text-muted d-block mt-2">
                    Password must be strong and secure.
                </small>
            </div>

            <div class="form-group">
                <label for="role" class="form-label">Select Your Role</label>
                <select id="role" name="role" class="form-select" required>
                    <option value="">-- Choose your role --</option>
                    <option value="Receptionist">
                        <i class="bi bi-person-check"></i> Receptionist
                    </option>
                    <option value="Doctor">
                        <i class="bi bi-stethoscope"></i> Doctor
                    </option>
                    <option value="Technician">
                        <i class="bi bi-beaker"></i> Lab Technician
                    </option>
                </select>
                <small class="form-text text-muted d-block mt-2">
                    Choose the role that matches your position in the clinic.
                </small>
            </div>

            <button type="submit" class="btn btn-register">
                <i class="bi bi-person-plus me-2"></i>Create Account
            </button>
        </form>

        <div class="register-footer">
            Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in here</a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
