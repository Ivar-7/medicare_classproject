<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Doctor Dashboard" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="row mb-4">
            <div class="col-12">
                <div>
                    <h3 class="fw-bold mb-1">
                        <i class="bi bi-stethoscope text-primary me-2"></i>Welcome, Dr. ${sessionScope.currentUser.firstName} ${sessionScope.currentUser.lastName}
                    </h3>
                    <p class="text-muted mb-0">Doctor Dashboard - Manage Patient Visits</p>
                    <p class="text-muted small mb-0">Your Doctor ID: #${sessionScope.currentUser.userId}</p>
                </div>
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-3">Doctor IDs</h5>
                        <c:choose>
                            <c:when test="${empty doctorUsers}">
                                <p class="text-muted mb-0">No doctors available.</p>
                            </c:when>
                            <c:otherwise>
                                <div class="d-flex flex-wrap gap-2">
                                    <c:forEach var="doctor" items="${doctorUsers}">
                                        <span class="badge bg-light text-dark border">#${doctor.userId} - Dr. ${doctor.firstName} ${doctor.lastName}</span>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
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
                                <p class="text-muted small mb-2">Pending Visits</p>
                                <h4 class="fw-bold mb-0">${pendingVisitsCount}</h4>
                            </div>
                            <div class="text-warning fs-2">
                                <i class="bi bi-exclamation-circle-fill"></i>
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
                                <p class="text-muted small mb-2">Total Visits Handled</p>
                                <h4 class="fw-bold mb-0">${totalVisitsCount}</h4>
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
                                <a href="${pageContext.request.contextPath}/visits" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-calendar-check me-2"></i>View All Visits
                                </a>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <a href="${pageContext.request.contextPath}/prescriptions" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-capsule me-2"></i>Prescriptions
                                </a>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <a href="${pageContext.request.contextPath}/notes" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-journal-medical me-2"></i>Clinical Notes
                                </a>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <a href="${pageContext.request.contextPath}/lab/requests" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-clipboard2-pulse me-2"></i>Lab Requests
                                </a>
                            </div>
                            <div class="col-sm-6 col-md-4">
                                <a href="${pageContext.request.contextPath}/lab/results" class="btn btn-outline-primary w-100">
                                    <i class="bi bi-file-earmark-medical me-2"></i>Lab Results
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Visits Requiring Action -->
        <div class="row">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-3">
                            <i class="bi bi-exclamation-circle me-2 text-warning"></i>Pending Visits
                        </h5>
                        <c:choose>
                            <c:when test="${empty pendingVisits}">
                                <div class="text-center text-muted py-4">
                                    <p><i class="bi bi-check-circle fs-3 opacity-25 d-block mb-2"></i>No pending visits</p>
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
                                            <c:forEach var="visit" items="${pendingVisits}" varStatus="status">
                                                <c:if test="${status.count <= 10}">
                                                <tr>
                                                    <td class="px-4 fw-medium">#${visit.visitId}</td>
                                                    <td>${visit.regNumber}</td>
                                                    <td class="text-muted small">${visit.visitDate}</td>
                                                    <td>
                                                          <span class="text-truncate d-inline-block truncate-200"
                                                              title="${visit.symptoms}">${visit.symptoms}</span>
                                                    </td>
                                                    <td class="text-end px-4">
                                                        <a href="${pageContext.request.contextPath}/visits/${visit.visitId}"
                                                           class="btn btn-sm btn-primary">
                                                            <i class="bi bi-pencil"></i>Manage
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
