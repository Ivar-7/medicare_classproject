<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty user ? 'Add User' : 'Edit User'}" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="mb-4">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/users" class="text-decoration-none">Users</a>
                    </li>
                    <li class="breadcrumb-item active">${empty user ? 'Add User' : 'Edit User'}</li>
                </ol>
            </nav>
            <h5 class="fw-bold mb-0">
                <i class="bi bi-person-${empty user ? 'plus' : 'gear'} text-primary me-2"></i>
                ${empty user ? 'Add New User' : 'Edit User Account'}
            </h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <!-- TODO: Implement user create/edit form -->
        <div class="card border-0 shadow-sm">
            <div class="card-body p-5 text-center text-muted">
                <i class="bi bi-person-gear fs-1 d-block mb-3 opacity-25"></i>
                <h6>User Form</h6>
                <p class="mb-0 small">
                    Implement the user management form here.<br>
                    Fields: Username, Password, Full Name, Role (Admin / Doctor / Receptionist).
                </p>
                <a href="${pageContext.request.contextPath}/users" class="btn btn-sm btn-outline-secondary mt-3">
                    <i class="bi bi-arrow-left me-1"></i>Back to Users
                </a>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
