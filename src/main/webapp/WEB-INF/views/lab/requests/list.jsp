<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Lab Requests" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h5 class="mb-1 fw-bold"><i class="bi bi-clipboard2-pulse text-primary me-2"></i>Lab Requests</h5>
                <p class="text-muted mb-0 small">Track all lab requests raised from visits.</p>
            </div>
            <c:if test="${sessionScope.currentUser.roleName == 'Doctor' || sessionScope.currentUser.roleName == 'Admin'}">
                <a href="${pageContext.request.contextPath}/lab/requests/new" class="btn btn-primary">
                    <i class="bi bi-plus-circle me-1"></i>New Lab Request
                </a>
            </c:if>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty labRequests}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-clipboard-x fs-1 d-block mb-2 opacity-25"></i>
                            <p>No lab requests found.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="px-4 py-3">ID</th>
                                        <th class="py-3">Visit</th>
                                        <th class="py-3">Student</th>
                                        <th class="py-3">Test</th>
                                        <th class="py-3">Priority</th>
                                        <th class="py-3">Status</th>
                                        <th class="py-3">Requested By</th>
                                        <th class="py-3">Date</th>
                                        <th class="py-3 text-end px-4">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="r" items="${labRequests}">
                                        <tr>
                                            <td class="px-4 text-muted font-monospace small">#${r.requestId}</td>
                                            <td class="text-muted">#${r.visitId}</td>
                                            <td>
                                                <div class="fw-medium">
                                                    <c:choose>
                                                        <c:when test="${not empty r.studentName}">${r.studentName}</c:when>
                                                        <c:otherwise>Unknown Student</c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="text-muted small">${r.studentRegNumber}</div>
                                            </td>
                                            <td>
                                                <div class="fw-medium">${r.testName}</div>
                                                <div class="text-muted small">${r.testDescription}</div>
                                            </td>
                                            <td>
                                                <span class="badge ${r.priorityName eq 'Urgent' ? 'bg-danger-subtle text-danger border border-danger-subtle' : 'bg-info-subtle text-info border border-info-subtle'}">
                                                    ${r.priorityName}
                                                </span>
                                            </td>
                                            <td>
                                                <span class="badge ${r.statusName eq 'Completed' ? 'bg-success-subtle text-success border border-success-subtle' : 'bg-warning-subtle text-warning border border-warning-subtle'}">
                                                    ${r.statusName}
                                                </span>
                                            </td>
                                            <td class="text-muted small">#${r.requestedBy}</td>
                                            <td class="text-muted small">${r.requestDate}</td>
                                            <td class="text-end px-4">
                                                <a href="${pageContext.request.contextPath}/lab/requests/${r.requestId}"
                                                   class="btn btn-sm btn-outline-secondary">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <c:if test="${sessionScope.currentUser.roleName == 'Doctor' || sessionScope.currentUser.roleName == 'Admin'}">
                                                    <a href="${pageContext.request.contextPath}/lab/requests/delete/${r.requestId}"
                                                       class="btn btn-sm btn-outline-danger ms-1"
                                                       onclick="return confirm('Delete this lab request?');">
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
