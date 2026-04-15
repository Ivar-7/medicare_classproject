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
</head>
<body class="bg-body-tertiary">

<div class="d-flex align-items-center justify-content-center min-vh-100 p-3">
    <div class="container">
    <div class="row justify-content-center">
    <div class="col-12 col-sm-10 col-md-8 col-lg-6 col-xl-5">
    <div class="card border-0 shadow-sm w-100">

        <!-- Card Header -->
        <div class="card-body p-5">
            <div class="text-center mb-4">
                <h4 class="fw-bold mb-1 text-dark">Medicare HMS</h4>
                <p class="text-muted small">University Health Management System</p>
            </div>

            <!-- Success Alert -->
            <c:if test="${not empty requestScope.success}">
                <div class="alert alert-success d-flex align-items-center gap-2 py-2 mb-3" role="alert">
                    <i class="bi bi-check-circle-fill flex-shrink-0"></i>
                    <small>${requestScope.success}</small>
                </div>
            </c:if>

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
                    <input type="text" class="form-control"
                           id="username" name="username"
                           value="${username}"
                           placeholder="Enter your username"
                           autocomplete="username" required autofocus>
                </div>

                <div class="mb-4">
                    <label for="password" class="form-label fw-medium small">Password</label>
                    <input type="password" class="form-control"
                           id="password" name="password"
                           placeholder="Enter your password"
                           autocomplete="current-password" required>
                </div>

                <div class="form-check mb-4">
                    <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe">
                    <label class="form-check-label small text-muted" for="rememberMe">
                        Keep me signed in on this device
                    </label>
                </div>

                <button type="submit" class="btn btn-primary w-100">Sign In</button>

            </form>
        </div>

        <div class="card-footer text-center bg-light border-0 py-3">
            <!-- <small class="text-muted d-block mb-2">
                Default credentials: <strong>admin</strong> / <strong>admin123</strong>
            </small> -->
            <hr class="my-2">
            <small class="text-muted">
                Don't have an account? 
                <a href="${pageContext.request.contextPath}/register" class="text-primary fw-semibold text-decoration-none">
                    Sign up as staff
                </a>
            </small>
        </div>

    </div>
    </div>
    </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
