<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Users" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h5 class="mb-1 fw-bold"><i class="bi bi-person-gear text-primary me-2"></i>System Users</h5>
                <p class="text-muted mb-0 small">Manage staff accounts and access roles.</p>
            </div>
            <a href="${pageContext.request.contextPath}/users/new" class="btn btn-primary">
                <i class="bi bi-person-plus me-1"></i>Add User
            </a>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty users}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-person-x fs-1 d-block mb-2 opacity-25"></i>
                            <p>No users found.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="px-4 py-3">ID</th>
                                        <th class="py-3">Full Name</th>
                                        <th class="py-3">Username</th>
                                        <th class="py-3">Role</th>
                                        <th class="py-3 text-end px-4">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="u" items="${users}">
                                        <tr>
                                            <td class="px-4 text-muted small font-monospace">#${u.userId}</td>
                                            <td class="fw-medium">${u.firstName} ${u.lastName}</td>
                                            <td>
                                                <span class="badge bg-light text-dark border font-monospace fw-normal">
                                                    ${u.username}
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${u.roleName == 'Admin'}">
                                                        <span class="badge bg-danger-subtle text-danger border border-danger-subtle">Admin</span>
                                                    </c:when>
                                                    <c:when test="${u.roleName == 'Doctor'}">
                                                        <span class="badge bg-primary-subtle text-primary border border-primary-subtle">Doctor</span>
                                                    </c:when>
                                                    <c:when test="${u.roleName == 'Technician'}">
                                                        <span class="badge bg-warning-subtle text-warning border border-warning-subtle">Technician</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle">Receptionist</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-end px-4">
                                                <a href="${pageContext.request.contextPath}/users/${u.userId}"
                                                   class="btn btn-sm btn-outline-secondary">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
