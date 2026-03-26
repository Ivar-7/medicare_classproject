<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 Server Error | Medicare HMS</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light d-flex align-items-center justify-content-center min-vh-100">
    <div class="text-center p-4">
        <i class="bi bi-exclamation-triangle text-warning display-1 opacity-50"></i>
        <h1 class="fw-bold text-dark mt-3">500</h1>
        <h5 class="text-muted mb-4">Internal Server Error</h5>
        <p class="text-muted mb-4">
            Something went wrong on our end. The error has been logged.<br>
            Please try again or contact your system administrator.
        </p>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
            <i class="bi bi-house me-2"></i>Back to Dashboard
        </a>
    </div>
</body>
</html>
