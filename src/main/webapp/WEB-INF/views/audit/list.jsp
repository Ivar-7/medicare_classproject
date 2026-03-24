<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Audit Logs" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h5 class="mb-1 fw-bold"><i class="bi bi-shield-check text-primary me-2"></i>Audit Logs</h5>
                <p class="text-muted mb-0 small">System activity and security monitoring.</p>
            </div>
            <c:if test="${not empty sessionScope.currentUser and sessionScope.currentUser.roleName == 'Admin'}">
                <form method="post" action="${pageContext.request.contextPath}/audit"
                      onsubmit="return confirm('Clear all audit logs? This action cannot be undone.');">
                    <button type="submit" name="action" value="clear" class="btn btn-outline-danger btn-sm">
                        <i class="bi bi-trash me-1"></i>Clear Logs
                    </button>
                </form>
            </c:if>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty logs}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-shield-x fs-1 d-block mb-2 opacity-25"></i>
                            <p>No audit logs recorded yet.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="px-4 py-3">ID</th>
                                        <th class="py-3">User</th>
                                        <th class="py-3">Action</th>
                                        <th class="py-3">Timestamp</th>
                                        <th class="py-3">IP Address</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="log" items="${logs}">
                                        <tr>
                                            <td class="px-4 text-muted small font-monospace">#${log.logId}</td>
                                            <td>
                                                <div class="fw-medium">${log.username}</div>
                                                <div class="text-muted small">ID: ${log.userId}</div>
                                            </td>
                                            <td>
                                                <span class="badge bg-info-subtle text-info border border-info-subtle">
                                                    ${log.action}
                                                </span>
                                            </td>
                                            <td class="text-muted small font-monospace">${log.timestamp}</td>
                                            <td class="text-muted small font-monospace">${log.ipAddress}</td>
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
