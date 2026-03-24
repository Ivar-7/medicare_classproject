<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Receptionist Dashboard" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="row mb-4">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h3 class="fw-bold mb-1">
                            <i class="bi bi-house-door text-primary me-2"></i>Welcome, ${sessionScope.currentUser.fullName}
                        </h3>
                        <p class="text-muted mb-0">Receptionist Dashboard</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/students/new" class="btn btn-primary">
                        <i class="bi bi-person-plus me-1"></i>Register New Student
                    </a>
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
                            <div class="text-primary" style="font-size: 32px;">
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
                                <p class="text-muted small mb-2">Total Visits</p>
                                <h4 class="fw-bold mb-0">${visitCount}</h4>
                            </div>
                            <div class="text-success" style="font-size: 32px;">
                                <i class="bi bi-clipboard2-check-fill"></i>
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
                                <a href="${pageContext.request.contextPath}/students/new" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-person-plus me-2"></i>Register Student
                                </a>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <a href="${pageContext.request.contextPath}/visits" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-calendar-check me-2"></i>Record Visit
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Visits -->
        <div class="row">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-3">Recent Visits</h5>
                        <c:choose>
                            <c:when test="${empty recentVisits}">
                                <div class="text-center text-muted py-4">
                                    <p><i class="bi bi-inbox fs-3 opacity-25 d-block mb-2"></i>No visits recorded yet</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th class="px-4">Visit ID</th>
                                                <th>Student</th>
                                                <th>Date</th>
                                                <th>Symptoms</th>
                                                <th class="text-end px-4">Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="visit" items="${recentVisits}" varStatus="status">
                                                <c:if test="${status.count <= 5}">
                                                <tr>
                                                    <td class="px-4 fw-medium">#${visit.visitId}</td>
                                                    <td>${visit.regNumber}</td>
                                                    <td class="text-muted small">${visit.visitDate}</td>
                                                    <td>
                                                        <span class="text-truncate d-inline-block" style="max-width: 200px;"
                                                              title="${visit.symptoms}">${visit.symptoms}</span>
                                                    </td>
                                                    <td class="text-end px-4">
                                                        <a href="${pageContext.request.contextPath}/visits/${visit.visitId}"
                                                           class="btn btn-sm btn-outline-secondary">
                                                            <i class="bi bi-eye"></i>
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
