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
</head>
<body class="bg-body-tertiary">
    <div class="d-flex align-items-center justify-content-center min-vh-100 p-3">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-12 col-sm-10 col-md-8 col-lg-7 col-xl-6">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body p-4 p-md-5">
                            <div class="text-center mb-4">
                                <h4 class="fw-bold mb-1">Create Staff Account</h4>
                                <p class="text-muted small mb-0">Register a new team member for the medical unit</p>
                            </div>

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center gap-2" role="alert">
                                    <i class="bi bi-exclamation-triangle-fill flex-shrink-0"></i>
                                    <div>${error}</div>
                                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                </div>
                            </c:if>

                            <form method="post" action="${pageContext.request.contextPath}/register" class="row g-3">
                                <div class="col-md-6">
                                    <label for="firstName" class="form-label">First Name</label>
                                    <input id="firstName" name="firstName" type="text" class="form-control"
                                           value="${firstName}" placeholder="Enter first name" maxlength="80"
                                           pattern="[A-Za-z][A-Za-z\s'.-]*"
                                           title="Use letters, spaces, apostrophes, dots, and hyphens only."
                                           oninput="this.value=this.value.replace(/[0-9]/g,'')" required>
                                </div>

                                <div class="col-md-6">
                                    <label for="lastName" class="form-label">Last Name</label>
                                    <input id="lastName" name="lastName" type="text" class="form-control"
                                           value="${lastName}" placeholder="Enter last name" maxlength="80"
                                           pattern="[A-Za-z][A-Za-z\s'.-]*"
                                           title="Use letters, spaces, apostrophes, dots, and hyphens only."
                                           oninput="this.value=this.value.replace(/[0-9]/g,'')" required>
                                </div>

                                <div class="col-12">
                                    <label for="username" class="form-label">Username</label>
                                    <input id="username" name="username" type="text" class="form-control"
                                           value="${username}" placeholder="Create a unique username" maxlength="80" required>
                                </div>

                                <div class="col-md-6">
                                    <label for="email" class="form-label">Email</label>
                                    <input id="email" name="email" type="email" class="form-control"
                                           value="${email}" placeholder="name@example.com" maxlength="120">
                                </div>

                                <div class="col-md-6">
                                    <label for="phone" class="form-label">Phone</label>
                                    <input id="phone" name="phone" type="text" class="form-control"
                                           value="${phone}" placeholder="e.g. +260..." maxlength="30">
                                </div>

                                <div class="col-12">
                                    <label for="password" class="form-label">Password</label>
                                    <input id="password" name="password" type="password" class="form-control"
                                           placeholder="Enter a strong password" required>
                                    <div class="form-text">Password should be strong and secure.</div>
                                </div>

                                <div class="col-12">
                                    <label for="role" class="form-label">Role</label>
                                    <select id="role" name="role" class="form-select" required>
                                        <option value="" ${empty role ? 'selected' : ''}>Choose role</option>
                                        <option value="Receptionist" ${role == 'Receptionist' ? 'selected' : ''}>Receptionist</option>
                                        <option value="Doctor" ${role == 'Doctor' ? 'selected' : ''}>Doctor</option>
                                        <option value="Technician" ${role == 'Technician' ? 'selected' : ''}>Technician</option>
                                    </select>
                                    <div class="form-text">Choose the role that matches the staff position.</div>
                                </div>

                                <div class="col-12 pt-1">
                                    <button type="submit" class="btn btn-primary w-100">Create Account</button>
                                </div>
                            </form>

                            <div class="text-center mt-4 small text-muted">
                                Already have an account?
                                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none fw-semibold">Sign in here</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
