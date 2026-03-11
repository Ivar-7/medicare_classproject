<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Treatment Notes" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h5 class="mb-1 fw-bold"><i class="bi bi-journal-medical text-primary me-2"></i>Treatment Notes</h5>
                <p class="text-muted mb-0 small">Clinical observations and follow-up records.</p>
            </div>
            <a href="${pageContext.request.contextPath}/notes/new" class="btn btn-primary">
                <i class="bi bi-plus-circle me-1"></i>Add Note
            </a>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty notes}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-journal-x fs-1 d-block mb-2 opacity-25"></i>
                            <p>No treatment notes found.</p>
                            <a href="${pageContext.request.contextPath}/notes/new" class="btn btn-sm btn-primary">
                                Add First Note
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="px-4 py-3">ID</th>
                                        <th class="py-3">Visit ID</th>
                                        <th class="py-3">Clinical Notes</th>
                                        <th class="py-3">Follow-Up Date</th>
                                        <th class="py-3 text-end px-4">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="n" items="${notes}">
                                        <tr>
                                            <td class="px-4 text-muted font-monospace small">#${n.noteId}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/visits/${n.visitId}"
                                                   class="text-decoration-none">
                                                    Visit #${n.visitId}
                                                </a>
                                            </td>
                                            <td>
                                                <span class="text-truncate d-inline-block" style="max-width:280px"
                                                      title="${n.clinicalNotes}">${n.clinicalNotes}</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty n.followUpDate}">
                                                        <span class="badge bg-warning-subtle text-warning border border-warning-subtle">
                                                            <i class="bi bi-calendar-event me-1"></i>${n.followUpDate}
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted small">—</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-end px-4">
                                                <a href="${pageContext.request.contextPath}/notes/${n.noteId}"
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
