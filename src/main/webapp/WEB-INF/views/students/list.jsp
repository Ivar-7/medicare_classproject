<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Students" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h5 class="mb-1 fw-bold"><i class="bi bi-people text-primary me-2"></i>Students</h5>
                <p class="text-muted mb-0 small">Manage registered patient profiles.</p>
            </div>
            <a href="${pageContext.request.contextPath}/students/new" class="btn btn-primary">
                <i class="bi bi-person-plus me-1"></i>Add Student
            </a>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <!-- Search Bar -->
        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body py-3 px-4">
                <form action="${pageContext.request.contextPath}/students" method="get" class="d-flex gap-2">
                    <input type="text" name="q" class="form-control form-control-sm"
                           placeholder="Search by name or reg. number..." value="${query}">
                    <button type="submit" class="btn btn-sm btn-outline-primary px-3">
                        Search
                    </button>
                    <c:if test="${not empty query}">
                        <a href="${pageContext.request.contextPath}/students" class="btn btn-sm btn-outline-secondary px-3">
                            Clear
                        </a>
                    </c:if>
                </form>
            </div>
        </div>

        <!-- Table -->
        <div class="card border-0 shadow-sm">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty students}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-people fs-1 d-block mb-2 opacity-25"></i>
                            <p>No students found.</p>
                            <a href="${pageContext.request.contextPath}/students/new" class="btn btn-sm btn-primary">
                                Register First Student
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="px-4 py-3">Reg Number</th>
                                        <th class="py-3">Full Name</th>
                                        <th class="py-3">Faculty</th>
                                        <th class="py-3">Email</th>
                                        <th class="py-3">Phone</th>
                                        <th class="py-3 text-end px-4">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="s" items="${students}">
                                        <tr>
                                            <td class="px-4">
                                                <span class="badge bg-light text-dark border fw-normal font-monospace">
                                                    ${s.regNumber}
                                                </span>
                                            </td>
                                            <td class="fw-medium">${s.firstName} ${s.lastName}</td>
                                            <td class="text-muted">${s.faculty}</td>
                                            <td class="text-muted">${s.email}</td>
                                            <td class="text-muted">${s.phone}</td>
                                            <td class="text-end px-4">
                                                <a href="${pageContext.request.contextPath}/students/${s.regNumber}"
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
