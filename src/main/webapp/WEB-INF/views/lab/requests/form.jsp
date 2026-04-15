<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty labRequest or empty labRequest.requestId ? 'New Lab Request' : 'Edit Lab Request'}" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="mb-4">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/lab/requests" class="text-decoration-none">Lab Requests</a>
                    </li>
                    <li class="breadcrumb-item active">${empty labRequest or empty labRequest.requestId ? 'New' : 'Edit'}</li>
                </ol>
            </nav>
            <h5 class="fw-bold mb-0">${empty labRequest or empty labRequest.requestId ? 'Create Lab Request' : 'Edit Lab Request'}</h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <c:if test="${sessionScope.currentUser.roleName == 'Technician'}">
            <div class="alert alert-warning">Technicians can view lab requests but cannot create or edit them.</div>
        </c:if>

        <div class="card border-0 shadow-sm">
            <div class="card-body p-4 p-md-5">
                <form method="post" action="${pageContext.request.contextPath}/lab/requests" class="row g-3">
                    <input type="hidden" name="requestId" value="${labRequest.requestId}">

                    <div class="col-md-6">
                        <label for="visitId" class="form-label">Visit</label>
                        <select id="visitId" name="visitId" class="form-select" required>
                            <option value="">Select a visit</option>
                            <c:forEach var="v" items="${visits}">
                                <option value="${v.visitId}" ${labRequest.visitId == v.visitId ? 'selected' : ''}>
                                    #${v.visitId} - Student ${v.regNumber} (${v.visitDate})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="requestedByDisplay" class="form-label">Requested By</label>
                        <input id="requestedByDisplay" type="text" class="form-control"
                               value="#${sessionScope.currentUser.userId} - ${sessionScope.currentUser.firstName} ${sessionScope.currentUser.lastName}" readonly>
                    </div>

                    <div class="col-md-8">
                        <label for="testName" class="form-label">Test Name</label>
                        <input id="testName" name="testName" type="text" class="form-control"
                               value="${labRequest.testName}" placeholder="e.g. Full Blood Count" required>
                    </div>

                    <div class="col-md-4">
                        <label for="priority" class="form-label">Priority</label>
                        <select id="priority" name="priority" class="form-select" required>
                            <option value="Routine" ${empty labRequest.priorityName or labRequest.priorityName eq 'Routine' ? 'selected' : ''}>Routine</option>
                            <option value="Urgent" ${labRequest.priorityName eq 'Urgent' ? 'selected' : ''}>Urgent</option>
                        </select>
                    </div>

                    <div class="col-12">
                        <label for="testDescription" class="form-label">Test Description</label>
                        <textarea id="testDescription" name="testDescription" class="form-control" rows="3"
                                  placeholder="Additional context for the lab team">${labRequest.testDescription}</textarea>
                    </div>

                    <div class="col-md-6">
                        <label for="status" class="form-label">Status</label>
                        <select id="status" name="status" class="form-select" required>
                            <option value="Pending" ${empty labRequest.statusName or labRequest.statusName eq 'Pending' ? 'selected' : ''}>Pending</option>
                            <option value="InProgress" ${labRequest.statusName eq 'InProgress' ? 'selected' : ''}>In Progress</option>
                            <option value="Completed" ${labRequest.statusName eq 'Completed' ? 'selected' : ''}>Completed</option>
                            <option value="Cancelled" ${labRequest.statusName eq 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="requestDateDisplay" class="form-label">Request Date</label>
                        <input id="requestDateDisplay" type="text" class="form-control"
                               value="${empty labRequest.requestDate ? 'Today' : labRequest.requestDate}" readonly>
                    </div>

                    <div class="col-12 d-flex justify-content-between pt-2">
                        <a href="${pageContext.request.contextPath}/lab/requests" class="btn btn-outline-secondary">Back</a>
                        <c:if test="${sessionScope.currentUser.roleName == 'Doctor' || sessionScope.currentUser.roleName == 'Admin'}">
                            <button type="submit" class="btn btn-primary px-4">Save Lab Request</button>
                        </c:if>
                    </div>
                </form>

                <c:if test="${not empty labRequest and labRequest.requestId > 0 and (sessionScope.currentUser.roleName == 'Doctor' || sessionScope.currentUser.roleName == 'Admin')}">
                    <hr class="my-4">
                    <form method="post" action="${pageContext.request.contextPath}/lab/requests"
                          onsubmit="return confirm('Delete this lab request? This action cannot be undone.');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="requestId" value="${labRequest.requestId}">
                        <button type="submit" class="btn btn-outline-danger">Delete Lab Request</button>
                    </form>
                </c:if>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
