<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Lab Results" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h5 class="mb-1 fw-bold"><i class="bi bi-file-earmark-medical text-primary me-2"></i>Lab Results</h5>
                <p class="text-muted mb-0 small">Capture and review investigation outcomes.</p>
            </div>
            <c:if test="${sessionScope.currentUser.roleName == 'Technician' || sessionScope.currentUser.roleName == 'Admin'}">
                <a href="${pageContext.request.contextPath}/lab/results/new" class="btn btn-primary">
                    <i class="bi bi-plus-circle me-1"></i>New Lab Result
                </a>
            </c:if>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty labResults}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-file-earmark-x fs-1 d-block mb-2 opacity-25"></i>
                            <p>No lab results found.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="px-4 py-3">Result ID</th>
                                        <th class="py-3">Request</th>
                                        <th class="py-3">Student</th>
                                        <th class="py-3">Test</th>
                                        <th class="py-3">Status</th>
                                        <th class="py-3">Value</th>
                                        <th class="py-3">Technician</th>
                                        <th class="py-3">Created</th>
                                        <th class="py-3 text-end px-4">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="r" items="${labResults}">
                                        <tr>
                                            <td class="px-4 text-muted font-monospace small">#${r.resultId}</td>
                                            <td class="text-muted">#${r.requestId}</td>
                                            <td>
                                                <div class="fw-medium">
                                                    <c:choose>
                                                        <c:when test="${not empty r.studentName}">${r.studentName}</c:when>
                                                        <c:otherwise>Student #${r.patientId}</c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                            <td class="text-muted">${r.testName}</td>
                                            <td>
                                                <span class="badge ${r.resultStatus eq 'Completed' ? 'bg-success-subtle text-success border border-success-subtle' : 'bg-warning-subtle text-warning border border-warning-subtle'}">
                                                    ${r.resultStatus}
                                                </span>
                                            </td>
                                            <td class="text-muted">${r.resultValue}</td>
                                            <td class="text-muted">${r.technicianName}</td>
                                            <td class="text-muted small">${r.createdAt}</td>
                                            <td class="text-end px-4">
                                                <a href="${pageContext.request.contextPath}/lab/results/${r.resultId}"
                                                   class="btn btn-sm btn-outline-secondary">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <c:if test="${sessionScope.currentUser.roleName == 'Technician' || sessionScope.currentUser.roleName == 'Admin'}">
                                                    <a href="${pageContext.request.contextPath}/lab/results/delete/${r.resultId}"
                                                       class="btn btn-sm btn-outline-danger ms-1"
                                                       onclick="return confirm('Delete this lab result?');">
                                                        <i class="bi bi-trash"></i>
                                                    </a>
                                                </c:if>
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
