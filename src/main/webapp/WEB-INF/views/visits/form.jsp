<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty visit ? 'New Visit' : 'Edit Visit'}" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="mb-4">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/visits" class="text-decoration-none">Visits</a>
                    </li>
                    <li class="breadcrumb-item active">${empty visit ? 'New Visit' : 'Edit Visit'}</li>
                </ol>
            </nav>
            <h5 class="fw-bold mb-0">
                ${empty visit ? 'Record New Visit' : 'Edit Medical Visit'}
            </h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-4 p-md-5">
                <form method="post" action="${pageContext.request.contextPath}/visits" class="row g-3">

                    <input type="hidden" name="visitId" value="${visit.visitId}">

                    <div class="col-md-6">
                        <label for="regNumber" class="form-label">Student Reg Number</label>
                        <input id="regNumber" name="regNumber" type="text" class="form-control"
                               value="${visit.regNumber}" placeholder="Enter existing student reg number" required>
                    </div>

                    <div class="col-md-6">
                        <label for="doctorId" class="form-label">Doctor ID</label>
                        <input id="doctorId" name="doctorId" type="number" class="form-control"
                               value="${visit.doctorId}" min="1" step="1" placeholder="Enter existing doctor user ID" required>
                    </div>

                    <div class="col-md-6">
                        <label for="visitDateDisplay" class="form-label">Visit Date</label>
                           <input id="visitDateDisplay" type="text" class="form-control"
                               value="Today (set automatically when saved)" readonly>
                           <div class="form-text">Visit date is always recorded as today's date/time.</div>
                    </div>

                    <div class="col-12">
                        <label for="symptoms" class="form-label">Symptoms</label>
                        <textarea id="symptoms" name="symptoms" class="form-control" rows="3" maxlength="1000"
                                  required>${visit.symptoms}</textarea>
                    </div>

                    <div class="col-md-6">
                        <label for="completed" class="form-label">Visit Status</label>
                        <c:set var="visitCompleted" value="${visit.statusName eq 'Completed'}" />
                        <select id="completed" name="completed" class="form-select" required>
                            <option value="false" ${visitCompleted ? '' : 'selected'}>Pending</option>
                            <option value="true" ${visitCompleted ? 'selected' : ''}>Completed</option>
                        </select>
                        <div class="form-text">Completed visits are removed from doctor pending visits on dashboard.</div>
                    </div>

                    <div class="col-12 d-flex gap-2 pt-2">
                        <button type="submit" name="action" value="${empty visit ? 'create' : 'update'}" class="btn btn-primary">
                            ${empty visit ? 'Save Visit' : 'Update Visit'}
                        </button>
                        <a href="${pageContext.request.contextPath}/visits" class="btn btn-outline-secondary">
                            Back to Visits
                        </a>
                    </div>
                </form>

                <c:if test="${not empty visit and visit.visitId > 0}">
                    <hr class="my-4">
                    <form method="post" action="${pageContext.request.contextPath}/visits"
                          onsubmit="return confirm('Delete this medical visit? This action cannot be undone.');">
                        <input type="hidden" name="visitId" value="${visit.visitId}">
                        <button type="submit" name="action" value="delete" class="btn btn-outline-danger">
                            Delete Visit
                        </button>
                    </form>
                </c:if>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
