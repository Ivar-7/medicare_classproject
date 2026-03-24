<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Medical Visits" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h5 class="mb-1 fw-bold"><i class="bi bi-calendar2-check text-primary me-2"></i>Medical Visits</h5>
                <p class="text-muted mb-0 small">All recorded patient consultations.</p>
            </div>
            <a href="${pageContext.request.contextPath}/visits/new" class="btn btn-primary">
                <i class="bi bi-plus-circle me-1"></i>New Visit
            </a>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty visits}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-calendar2-x fs-1 d-block mb-2 opacity-25"></i>
                            <p>No visits recorded yet.</p>
                            <a href="${pageContext.request.contextPath}/visits/new" class="btn btn-sm btn-primary">
                                Record First Visit
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="px-4 py-3">ID</th>
                                        <th class="py-3">Student</th>
                                        <th class="py-3">Doctor</th>
                                        <th class="py-3">Date</th>
                                        <th class="py-3">Symptoms</th>
                                        <th class="py-3 text-end px-4">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="v" items="${visits}">
                                        <tr>
                                            <td class="px-4 text-muted font-monospace small">#${v.visitId}</td>
                                            <td>
                                                <div class="fw-medium">${v.studentName}</div>
                                                <div class="text-muted small">${v.regNumber}</div>
                                            </td>
                                            <td class="text-muted small">${v.doctorName}</td>
                                            <td class="text-muted small">${v.visitDate}</td>
                                            <td>
                                                <span class="text-truncate d-inline-block" style="max-width:130px"
                                                      title="${v.symptoms}">${v.symptoms}</span>
                                            </td>
                                            <td class="text-end px-4">
                                                <a href="${pageContext.request.contextPath}/visits/${v.visitId}"
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
