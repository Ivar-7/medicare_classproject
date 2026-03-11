<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | Medicare HMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="login-body">

<div class="login-wrapper d-flex align-items-center justify-content-center min-vh-100 p-3">
    <div class="login-card card border-0 shadow-lg" style="width: 100%; max-width: 430px;">

        <!-- Card Header -->
        <div class="card-body p-5">
            <div class="text-center mb-4">
                <div class="login-icon bg-primary rounded-circle d-inline-flex align-items-center justify-content-center mb-3"
                     style="width:64px; height:64px;">
                    <i class="bi bi-hospital-fill text-white fs-3"></i>
                </div>
                <h4 class="fw-bold mb-1 text-dark">Medicare HMS</h4>
                <p class="text-muted small">University Health Management System</p>
            </div>

            <!-- Error Alert -->
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger d-flex align-items-center gap-2 py-2" role="alert">
                    <i class="bi bi-exclamation-triangle-fill flex-shrink-0"></i>
                    <small>${requestScope.error}</small>
                </div>
            </c:if>

            <!-- Login Form -->
            <form action="${pageContext.request.contextPath}/login" method="post" novalidate>

                <div class="mb-3">
                    <label for="username" class="form-label fw-medium small">Username</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0">
                            <i class="bi bi-person text-muted"></i>
                        </span>
                        <input type="text" class="form-control border-start-0 bg-light"
                               id="username" name="username"
                               placeholder="Enter your username"
                               autocomplete="username" required autofocus>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="password" class="form-label fw-medium small">Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0">
                            <i class="bi bi-lock text-muted"></i>
                        </span>
                        <input type="password" class="form-control border-start-0 bg-light"
                               id="password" name="password"
                               placeholder="Enter your password"
                               autocomplete="current-password" required>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">
                    <i class="bi bi-box-arrow-in-right me-2"></i>Sign In
                </button>

            </form>
        </div>

        <div class="card-footer text-center bg-light border-0 py-3">
            <small class="text-muted">
                <i class="bi bi-shield-lock me-1"></i>
                Default credentials: <strong>admin</strong> / <strong>admin123</strong>
            </small>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
