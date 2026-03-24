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

        <div class="card border-0 shadow-sm">
            <div class="card-body p-4 p-md-5">
                <form method="post" action="${pageContext.request.contextPath}/users" class="row g-3">

                    <input type="hidden" name="userId" value="${user.userId}">

                    <div class="col-md-6">
                        <label for="fullName" class="form-label">Full Name</label>
                        <input id="fullName" name="fullName" type="text" class="form-control"
                               value="${user.fullName}" maxlength="120" required>
                    </div>

                    <div class="col-md-6">
                        <label for="username" class="form-label">Username</label>
                        <input id="username" name="username" type="text" class="form-control"
                               value="${user.username}" maxlength="80" required>
                    </div>

                    <div class="col-md-6">
                        <label for="role" class="form-label">Role</label>
                        <select id="role" name="role" class="form-select" required>
                            <option value="" ${empty user.roleName ? 'selected' : ''}>Select role</option>
                            <option value="Admin" ${user.roleName == 'Admin' ? 'selected' : ''}>Admin</option>
                            <option value="Doctor" ${user.roleName == 'Doctor' ? 'selected' : ''}>Doctor</option>
                            <option value="Receptionist" ${user.roleName == 'Receptionist' ? 'selected' : ''}>Receptionist</option>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="password" class="form-label">
                            ${empty user ? 'Password' : 'New Password (Optional)'}
                        </label>
                        <input id="password" name="password" type="password" class="form-control"
                               ${empty user ? 'required' : ''}>
                        <div class="form-text">
                            <c:choose>
                                <c:when test="${empty user}">Required for new user accounts.</c:when>
                                <c:otherwise>Leave empty to keep the current password unchanged.</c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="col-12 d-flex gap-2 pt-2">
                        <button type="submit" name="action" value="${empty user ? 'create' : 'update'}"
                                class="btn btn-primary">
                            <i class="bi bi-check2-circle me-1"></i>
                            ${empty user ? 'Create User' : 'Update User'}
                        </button>
                        <a href="${pageContext.request.contextPath}/users" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i>Back to Users
                        </a>
                    </div>
                </form>

                <c:if test="${not empty user and user.userId > 0}">
                    <hr class="my-4">
                    <form method="post" action="${pageContext.request.contextPath}/users"
                          onsubmit="return confirm('Delete this user account? This action cannot be undone.');">
                        <input type="hidden" name="userId" value="${user.userId}">
                        <button type="submit" name="action" value="delete" class="btn btn-outline-danger">
                            <i class="bi bi-trash me-1"></i>Delete User
                        </button>
                    </form>
                </c:if>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
