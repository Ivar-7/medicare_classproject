<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 Not Found | Medicare HMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light d-flex align-items-center justify-content-center min-vh-100">
    <div class="text-center p-4">
        <i class="bi bi-map text-muted display-1 opacity-25"></i>
        <h1 class="fw-bold text-dark mt-3">404</h1>
        <h5 class="text-muted mb-4">Page Not Found</h5>
        <p class="text-muted mb-4">The page you are looking for doesn't exist or has been moved.</p>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
            <i class="bi bi-house me-2"></i>Back to Dashboard
        </a>
    </div>
</body>
</html>
