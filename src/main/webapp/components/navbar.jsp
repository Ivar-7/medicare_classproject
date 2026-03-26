<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container-fluid px-4">

        <a class="navbar-brand fw-bold d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/dashboard">
            <i class="bi bi-hospital-fill fs-5"></i>
            <span>School Medical System</span>
        </a>

        <button class="navbar-toggler border-0" type="button"
                data-bs-toggle="collapse" data-bs-target="#mainNav" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto gap-1">

                <li class="nav-item">
                    <a class="nav-link rounded px-3 ${requestScope['javax.servlet.forward.servlet_path'] eq '/dashboard' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/dashboard">
                        <i class="bi bi-speedometer2 me-1"></i>Dashboard
                    </a>
                </li>

                <!-- All roles except Doctor can view students -->
                <c:if test="${sessionScope.currentUser.roleName != 'Doctor'}">
                    <li class="nav-item">
                        <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/students">
                            <i class="bi bi-people me-1"></i>Students
                        </a>
                    </li>
                </c:if>

                <!-- Receptionist: Record visits; Doctor: View visits; Tech: View visits -->
                <c:if test="${sessionScope.currentUser.roleName == 'Receptionist' || sessionScope.currentUser.roleName == 'Doctor'}">
                    <li class="nav-item">
                        <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/visits">
                            <i class="bi bi-calendar2-check me-1"></i>Visits
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/medical-history">
                            <i class="bi bi-file-earmark-medical me-1"></i>Medical History
                        </a>
                    </li>
                </c:if>

                <!-- Doctor only: Prescriptions -->
                <c:if test="${sessionScope.currentUser.roleName == 'Doctor'}">
                    <li class="nav-item">
                        <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/prescriptions">
                            <i class="bi bi-capsule me-1"></i>Prescriptions
                        </a>
                    </li>
                </c:if>

                <!-- Notes visible to Doctor and Technician only -->
                <c:if test="${sessionScope.currentUser.roleName == 'Doctor' || sessionScope.currentUser.roleName == 'Technician'}">
                    <li class="nav-item">
                        <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/notes">
                            <i class="bi bi-journal-medical me-1"></i>
                            <c:choose>
                                <c:when test="${sessionScope.currentUser.roleName == 'Doctor'}">Clinical Notes</c:when>
                                <c:otherwise>Lab Notes</c:otherwise>
                            </c:choose>
                        </a>
                    </li>
                </c:if>

                <!-- Admin only: Users & Audit -->
                <c:if test="${sessionScope.currentUser.roleName == 'Admin'}">
                    <li class="nav-item">
                        <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/users">
                            <i class="bi bi-person-gear me-1"></i>Users
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/audit">
                            <i class="bi bi-shield-check me-1"></i>Audit
                        </a>
                    </li>
                </c:if>

            </ul>

            <ul class="navbar-nav ms-auto">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle d-flex align-items-center gap-2 px-3 rounded"
                       href="#" role="button" data-bs-toggle="dropdown" id="userDropdown">
                        <i class="bi bi-person-circle fs-5"></i>
                        <span>${sessionScope.currentUser.fullName}</span>
                        <c:choose>
                            <c:when test="${sessionScope.currentUser.roleName == 'Receptionist'}">
                                <span class="badge bg-success fw-normal ms-1">Reception</span>
                            </c:when>
                            <c:when test="${sessionScope.currentUser.roleName == 'Doctor'}">
                                <span class="badge bg-info fw-normal ms-1">Doctor</span>
                            </c:when>
                            <c:when test="${sessionScope.currentUser.roleName == 'Technician'}">
                                <span class="badge bg-warning fw-normal ms-1">Lab Tech</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-secondary fw-normal ms-1">${sessionScope.currentUser.roleName}</span>
                            </c:otherwise>
                        </c:choose>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                        <li>
                            <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                                <i class="bi bi-box-arrow-right me-2"></i>Logout
                            </a>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>

    </div>
</nav>
