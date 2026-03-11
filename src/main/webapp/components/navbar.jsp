<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container-fluid px-4">

        <a class="navbar-brand fw-bold d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/home">
            <i class="bi bi-hospital-fill fs-5"></i>
            <span>Medicare HMS</span>
        </a>

        <button class="navbar-toggler border-0" type="button"
                data-bs-toggle="collapse" data-bs-target="#mainNav" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto gap-1">

                <li class="nav-item">
                    <a class="nav-link rounded px-3 ${requestScope['javax.servlet.forward.servlet_path'] eq '/home' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/home">
                        <i class="bi bi-speedometer2 me-1"></i>Dashboard
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/students">
                        <i class="bi bi-people me-1"></i>Students
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/visits">
                        <i class="bi bi-calendar2-check me-1"></i>Visits
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/prescriptions">
                        <i class="bi bi-capsule me-1"></i>Prescriptions
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link rounded px-3" href="${pageContext.request.contextPath}/notes">
                        <i class="bi bi-journal-medical me-1"></i>Tx Notes
                    </a>
                </li>

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
                        <span class="badge bg-white text-primary fw-normal ms-1">
                            ${sessionScope.currentUser.roleName}
                        </span>
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
