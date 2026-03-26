<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty note ? 'New Note' : 'Edit Note'}" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="mb-4">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/notes" class="text-decoration-none">Treatment Notes</a>
                    </li>
                    <li class="breadcrumb-item active">${empty note ? 'New Note' : 'Edit Note'}</li>
                </ol>
            </nav>
            <h5 class="fw-bold mb-0">
                ${empty note ? 'Add Treatment Note' : 'Edit Treatment Note'}
            </h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-4 p-md-5">
                <form method="post" action="${pageContext.request.contextPath}/notes" class="row g-3">

                    <input type="hidden" name="noteId" value="${note.noteId}">

                    <div class="col-md-4">
                        <label for="visitId" class="form-label">Visit ID</label>
                        <input id="visitId" name="visitId" type="number" class="form-control"
                               value="${note.visitId}" min="1" step="1" required>
                    </div>

                    <div class="col-md-4">
                        <label for="followUpDate" class="form-label">Follow-Up Date</label>
                        <input id="followUpDate" name="followUpDate" type="date" class="form-control"
                               value="${note.followUpDate}">
                        <div class="form-text">Optional field for scheduling a follow-up review.</div>
                    </div>

                    <div class="col-12">
                        <label for="clinicalNotes" class="form-label">Clinical Notes</label>
                        <textarea id="clinicalNotes" name="clinicalNotes" class="form-control" rows="6"
                                  maxlength="4000" required>${note.clinicalNotes}</textarea>
                    </div>

                    <div class="col-12 d-flex gap-2 pt-2">
                        <button type="submit" name="action" value="${empty note ? 'create' : 'update'}"
                                class="btn btn-primary">
                            ${empty note ? 'Save Note' : 'Update Note'}
                        </button>
                        <a href="${pageContext.request.contextPath}/notes" class="btn btn-outline-secondary">
                            Back to Notes
                        </a>
                    </div>
                </form>

                <c:if test="${not empty note and note.noteId > 0}">
                    <hr class="my-4">
                    <form method="post" action="${pageContext.request.contextPath}/notes"
                          onsubmit="return confirm('Delete this treatment note? This action cannot be undone.');">
                        <input type="hidden" name="noteId" value="${note.noteId}">
                        <button type="submit" name="action" value="delete" class="btn btn-outline-danger">
                            Delete Note
                        </button>
                    </form>
                </c:if>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
