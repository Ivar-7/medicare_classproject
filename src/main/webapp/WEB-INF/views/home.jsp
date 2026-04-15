<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"      %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"       %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Dashboard" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-start mb-4">
            <div>
                <h4 class="mb-1 fw-bold text-dark">
                    Welcome back, <span class="text-primary">${sessionScope.currentUser.firstName} ${sessionScope.currentUser.lastName}</span>
                </h4>
                <p class="text-muted mb-0">
                    <i class="bi bi-calendar3 me-1"></i>
                    <jsp:useBean id="now" class="java.util.Date" />
                    <fmt:formatDate value="${now}" pattern="EEEE, dd MMMM yyyy" />
                    &nbsp;&bull;&nbsp;
                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle">
                        ${sessionScope.currentUser.roleName}
                    </span>
                </p>
            </div>
            <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/students/new" class="btn btn-sm btn-outline-primary">
                    <i class="bi bi-person-plus me-1"></i>New Student
                </a>
                <a href="${pageContext.request.contextPath}/visits/new" class="btn btn-sm btn-primary">
                    <i class="bi bi-plus-circle me-1"></i>New Visit
                </a>
            </div>
        </div>

        <!-- Stats Cards -->
        <div class="row g-4 mb-4">

            <div class="col-12 col-sm-6 col-xl-3">
                <div class="card stat-card border-0 shadow-sm h-100">
                    <div class="card-body d-flex align-items-center gap-3 p-4">
                        <div class="stat-icon bg-primary-subtle rounded-3 p-3">
                            <i class="bi bi-people-fill fs-3 text-primary"></i>
                        </div>
                        <div>
                            <div class="stat-value fw-bold fs-2 lh-1">${totalStudents}</div>
                            <div class="stat-label text-muted small mt-1">Total Students</div>
                        </div>
                    </div>
                    <div class="card-footer bg-transparent border-0 px-4 pb-3">
                        <a href="${pageContext.request.contextPath}/students" class="text-primary small text-decoration-none">
                            View all <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-12 col-sm-6 col-xl-3">
                <div class="card stat-card border-0 shadow-sm h-100">
                    <div class="card-body d-flex align-items-center gap-3 p-4">
                        <div class="stat-icon bg-success-subtle rounded-3 p-3">
                            <i class="bi bi-calendar2-check-fill fs-3 text-success"></i>
                        </div>
                        <div>
                            <div class="stat-value fw-bold fs-2 lh-1">${todayVisits}</div>
                            <div class="stat-label text-muted small mt-1">Visits Today</div>
                        </div>
                    </div>
                    <div class="card-footer bg-transparent border-0 px-4 pb-3">
                        <a href="${pageContext.request.contextPath}/visits" class="text-success small text-decoration-none">
                            View today <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-12 col-sm-6 col-xl-3">
                <div class="card stat-card border-0 shadow-sm h-100">
                    <div class="card-body d-flex align-items-center gap-3 p-4">
                        <div class="stat-icon bg-info-subtle rounded-3 p-3">
                            <i class="bi bi-clipboard2-pulse-fill fs-3 text-info"></i>
                        </div>
                        <div>
                            <div class="stat-value fw-bold fs-2 lh-1">${totalVisits}</div>
                            <div class="stat-label text-muted small mt-1">Total Visits</div>
                        </div>
                    </div>
                    <div class="card-footer bg-transparent border-0 px-4 pb-3">
                        <a href="${pageContext.request.contextPath}/visits" class="text-info small text-decoration-none">
                            View all <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-12 col-sm-6 col-xl-3">
                <div class="card stat-card border-0 shadow-sm h-100">
                    <div class="card-body d-flex align-items-center gap-3 p-4">
                        <div class="stat-icon bg-warning-subtle rounded-3 p-3">
                            <i class="bi bi-person-badge-fill fs-3 text-warning"></i>
                        </div>
                        <div>
                            <div class="stat-value fw-bold fs-2 lh-1">${totalStaff}</div>
                            <div class="stat-label text-muted small mt-1">Staff Members</div>
                        </div>
                    </div>
                    <div class="card-footer bg-transparent border-0 px-4 pb-3">
                        <c:if test="${sessionScope.currentUser.roleName == 'Admin'}">
                            <a href="${pageContext.request.contextPath}/users" class="text-warning small text-decoration-none">
                                Manage <i class="bi bi-arrow-right"></i>
                            </a>
                        </c:if>
                        <c:if test="${sessionScope.currentUser.roleName != 'Admin'}">
                            <span class="text-muted small">Clinic staff</span>
                        </c:if>
                    </div>
                </div>
            </div>

        </div><!-- /Stats Cards -->

        <div class="row g-4">

            <!-- Recent Visits Table -->
            <div class="col-12 col-xl-8">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-header bg-white border-bottom d-flex justify-content-between align-items-center py-3 px-4">
                        <h6 class="mb-0 fw-semibold">
                            <i class="bi bi-clock-history text-primary me-2"></i>Recent Visits
                        </h6>
                        <a href="${pageContext.request.contextPath}/visits" class="btn btn-sm btn-outline-secondary">
                            View All
                        </a>
                    </div>
                    <div class="card-body p-0">
                        <c:choose>
                            <c:when test="${empty recentVisits}">
                                <div class="text-center py-5 text-muted">
                                    <i class="bi bi-calendar2-x fs-1 d-block mb-2 opacity-25"></i>
                                    <p class="mb-0">No visits recorded yet.</p>
                                    <a href="${pageContext.request.contextPath}/visits/new"
                                       class="btn btn-sm btn-primary mt-3">
                                        <i class="bi bi-plus-circle me-1"></i>Record First Visit
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th class="px-4 py-3 text-muted fw-semibold small text-uppercase">#</th>
                                                <th class="py-3 text-muted fw-semibold small text-uppercase">Student</th>
                                                <th class="py-3 text-muted fw-semibold small text-uppercase">Doctor</th>
                                                <th class="py-3 text-muted fw-semibold small text-uppercase">Date</th>
                                                <th class="py-3 text-muted fw-semibold small text-uppercase">Symptoms</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="v" items="${recentVisits}" varStatus="loop">
                                                <tr>
                                                    <td class="px-4 text-muted small">${loop.count}</td>
                                                    <td>
                                                        <div class="d-flex align-items-center gap-2">
                                                            <div class="avatar-circle bg-primary-subtle text-primary">
                                                                ${fn:substring(v.studentName,0,1)}
                                                            </div>
                                                            <div>
                                                                <div class="fw-medium">${v.studentName}</div>
                                                                <div class="text-muted small">${v.regNumber}</div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td class="text-muted small">${v.doctorName}</td>
                                                    <td class="text-muted small">${v.visitDate}</td>
                                                    <td>
                                                                                                                <span class="text-truncate d-inline-block truncate-160"
                                                              title="${v.symptoms}">
                                                            ${v.symptoms}
                                                        </span>
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

            <!-- Quick Actions -->
            <div class="col-12 col-xl-4">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-header bg-white border-bottom py-3 px-4">
                        <h6 class="mb-0 fw-semibold">
                            <i class="bi bi-lightning-charge text-warning me-2"></i>Quick Actions
                        </h6>
                    </div>
                    <div class="card-body d-flex flex-column gap-2 p-4">

                        <a href="${pageContext.request.contextPath}/students/new"
                           class="btn btn-outline-primary d-flex align-items-center gap-2 text-start">
                            <i class="bi bi-person-plus-fill fs-5"></i>
                            <div>
                                <div class="fw-medium">Register Student</div>
                                <div class="small text-muted">Add a new patient profile</div>
                            </div>
                        </a>

                        <a href="${pageContext.request.contextPath}/visits/new"
                           class="btn btn-outline-success d-flex align-items-center gap-2 text-start">
                            <i class="bi bi-clipboard2-plus-fill fs-5"></i>
                            <div>
                                <div class="fw-medium">Record Visit</div>
                                <div class="small text-muted">Create a new consultation</div>
                            </div>
                        </a>

                        <a href="${pageContext.request.contextPath}/prescriptions/new"
                           class="btn btn-outline-info d-flex align-items-center gap-2 text-start">
                            <i class="bi bi-capsule-pill fs-5"></i>
                            <div>
                                <div class="fw-medium">Write Prescription</div>
                                <div class="small text-muted">Issue medication for a visit</div>
                            </div>
                        </a>

                        <a href="${pageContext.request.contextPath}/notes/new"
                           class="btn btn-outline-secondary d-flex align-items-center gap-2 text-start">
                            <i class="bi bi-journal-plus fs-5"></i>
                            <div>
                                <div class="fw-medium">Add Treatment Note</div>
                                <div class="small text-muted">Record clinical observations</div>
                            </div>
                        </a>

                        <c:if test="${sessionScope.currentUser.roleName == 'Admin'}">
                            <a href="${pageContext.request.contextPath}/users/new"
                               class="btn btn-outline-warning d-flex align-items-center gap-2 text-start">
                                <i class="bi bi-person-check-fill fs-5"></i>
                                <div>
                                    <div class="fw-medium">Add Staff Member</div>
                                    <div class="small text-muted">Create user account</div>
                                </div>
                            </a>
                        </c:if>

                    </div>
                </div>
            </div>

        </div><!-- /row -->

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
