<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Lab Technician Dashboard" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="row mb-4">
            <div class="col-12">
                <div>
                    <h3 class="fw-bold mb-1">
                        <i class="bi bi-beaker text-primary me-2"></i>Welcome, ${sessionScope.currentUser.firstName} ${sessionScope.currentUser.lastName}
                    </h3>
                    <p class="text-muted mb-0">Lab Technician Dashboard - Patient Test Notes</p>
                </div>
            </div>
        </div>

        <!-- Quick Stats -->
        <div class="row mb-4">
            <div class="col-md-6 col-lg-3">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <p class="text-muted small mb-2">Total Students</p>
                                <h4 class="fw-bold mb-0">${studentCount}</h4>
                            </div>
                            <div class="text-info fs-2">
                                <i class="bi bi-people-fill"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-lg-3">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <p class="text-muted small mb-2">Lab Notes Created</p>
                                <h4 class="fw-bold mb-0">${notesCount}</h4>
                            </div>
                            <div class="text-success fs-2">
                                <i class="bi bi-clipboard-check-fill"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-3">Quick Actions</h5>
                        <div class="row g-2">
                            <div class="col-sm-6 col-md-4">
                                <a href="${pageContext.request.contextPath}/students" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-people me-2"></i>View All Students
                                </a>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <a href="${pageContext.request.contextPath}/notes" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-journal-medical me-2"></i>Lab Notes
                                </a>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <a href="${pageContext.request.contextPath}/notes/new" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-plus-circle me-2"></i>Add Lab Note
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Students -->
        <div class="row">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-3">Student Directory</h5>
                        <c:choose>
                            <c:when test="${empty recentStudents}">
                                <div class="text-center text-muted py-4">
                                    <p><i class="bi bi-inbox fs-3 opacity-25 d-block mb-2"></i>No students registered yet</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th class="px-4">Reg Number</th>
                                                <th>Full Name</th>
                                                <th>Faculty</th>
                                                <th class="text-end px-4">Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="student" items="${recentStudents}" varStatus="status">
                                                <c:if test="${status.count <= 10}">
                                                <tr>
                                                    <td class="px-4 fw-medium">${student.regNumber}</td>
                                                    <td>${student.firstName} ${student.lastName}</td>
                                                    <td class="text-muted small">${student.faculty}</td>
                                                    <td class="text-end px-4">
                                                        <a href="${pageContext.request.contextPath}/notes/student/${student.regNumber}"
                                                           class="btn btn-sm btn-outline-secondary">
                                                            <i class="bi bi-pencil"></i>Add Note
                                                        </a>
                                                    </td>
                                                </tr>
                                                </c:if>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
