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
                ${empty user ? 'Add New User' : 'Edit User Account'}
            </h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-4 p-md-5">
                <form method="post" action="${pageContext.request.contextPath}/users" class="row g-3">

                    <input type="hidden" name="userId" value="${user.userId}">

                    <div class="col-md-6">
                        <label for="firstName" class="form-label">First Name</label>
                        <input id="firstName" name="firstName" type="text" class="form-control"
                               value="${user.firstName}" maxlength="80"
                               pattern="[A-Za-z](?:[A-Za-z .']|-)*"
                               title="Use letters, spaces, apostrophes, dots, and hyphens only."
                               oninput="this.value=this.value.replace(/[0-9]/g,'')" required>
                    </div>

                    <div class="col-md-6">
                        <label for="lastName" class="form-label">Last Name</label>
                        <input id="lastName" name="lastName" type="text" class="form-control"
                               value="${user.lastName}" maxlength="80"
                               pattern="[A-Za-z](?:[A-Za-z .']|-)*"
                               title="Use letters, spaces, apostrophes, dots, and hyphens only."
                               oninput="this.value=this.value.replace(/[0-9]/g,'')" required>
                    </div>

                    <div class="col-md-6">
                        <label for="username" class="form-label">Username</label>
                        <input id="username" name="username" type="text" class="form-control"
                               value="${user.username}" maxlength="80" required>
                    </div>

                    <div class="col-md-6">
                        <label for="email" class="form-label">Email</label>
                        <input id="email" name="email" type="email" class="form-control"
                               value="${user.email}" maxlength="120">
                    </div>

                    <div class="col-md-6">
                        <label for="phone" class="form-label">Phone</label>
                        <input id="phone" name="phone" type="text" class="form-control"
                               value="${user.phone}" maxlength="30">
                    </div>

                    <div class="col-md-6">
                        <label for="dateOfEmployment" class="form-label">Date of Employment</label>
                        <input id="dateOfEmployment" name="dateOfEmployment" type="date" class="form-control"
                               value="${not empty dateOfEmployment ? dateOfEmployment : user.dateOfEmployment}">
                        <div class="form-text">Optional.</div>
                    </div>

                    <div class="col-md-6">
                        <label for="role" class="form-label">Role</label>
                        <select id="role" name="role" class="form-select" required>
                            <option value="" ${empty user.roleName ? 'selected' : ''}>Select role</option>
                            <option value="Admin" ${user.roleName == 'Admin' ? 'selected' : ''}>Admin</option>
                            <option value="Doctor" ${user.roleName == 'Doctor' ? 'selected' : ''}>Doctor</option>
                            <option value="Receptionist" ${user.roleName == 'Receptionist' ? 'selected' : ''}>Receptionist</option>
                            <option value="Technician" ${user.roleName == 'Technician' ? 'selected' : ''}>Technician</option>
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
                            ${empty user ? 'Create User' : 'Update User'}
                        </button>
                        <a href="${pageContext.request.contextPath}/users" class="btn btn-outline-secondary">
                            Back to Users
                        </a>
                    </div>
                </form>

                <c:if test="${not empty user and user.userId > 0}">
                    <hr class="my-4">
                    <form method="post" action="${pageContext.request.contextPath}/users"
                          onsubmit="return confirm('Delete this user account? This action cannot be undone.');">
                        <input type="hidden" name="userId" value="${user.userId}">
                        <button type="submit" name="action" value="delete" class="btn btn-outline-danger">
                            Delete User
                        </button>
                    </form>
                </c:if>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />

<script>
    (function () {
        var dateInput = document.getElementById('dateOfEmployment');
        if (!dateInput) {
            return;
        }
        var now = new Date();
        var month = String(now.getMonth() + 1).padStart(2, '0');
        var day = String(now.getDate()).padStart(2, '0');
        dateInput.max = now.getFullYear() + '-' + month + '-' + day;
    })();
</script>
