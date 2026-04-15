<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty labResult or empty labResult.resultId ? 'New Lab Result' : 'Edit Lab Result'}" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="mb-4">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/lab/results" class="text-decoration-none">Lab Results</a>
                    </li>
                    <li class="breadcrumb-item active">${empty labResult or empty labResult.resultId ? 'New' : 'Edit'}</li>
                </ol>
            </nav>
            <h5 class="fw-bold mb-0">${empty labResult or empty labResult.resultId ? 'Create Lab Result' : 'Edit Lab Result'}</h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <c:if test="${sessionScope.currentUser.roleName != 'Technician' && sessionScope.currentUser.roleName != 'Admin'}">
            <div class="alert alert-warning">Only technicians and admins can create or edit lab results.</div>
        </c:if>

        <div class="card border-0 shadow-sm">
            <div class="card-body p-4 p-md-5">
                <form method="post" action="${pageContext.request.contextPath}/lab/results" class="row g-3">
                    <input type="hidden" name="resultId" value="${labResult.resultId}">

                    <div class="col-md-6">
                        <label for="requestId" class="form-label">Lab Request</label>
                        <select id="requestId" name="requestId" class="form-select" required>
                            <option value="">Select lab request</option>
                            <c:forEach var="req" items="${labRequests}">
                                <option value="${req.requestId}" ${labResult.requestId == req.requestId ? 'selected' : ''}>
                                    #${req.requestId} - ${req.testName} (Student ${req.studentRegNumber})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="patientId" class="form-label">Patient (Reg Number)</label>
                        <input id="patientId" name="patientId" type="number" class="form-control"
                               value="${labResult.patientId}" min="1" step="1"
                               placeholder="Enter patient registration number" required>
                        <div class="form-text">Use the student's registration number linked to the selected lab request.</div>
                    </div>

                    <div class="col-md-6">
                        <label for="technicianDisplay" class="form-label">Technician</label>
                        <input id="technicianDisplay" type="text" class="form-control"
                               value="#${sessionScope.currentUser.userId} - ${sessionScope.currentUser.firstName} ${sessionScope.currentUser.lastName}" readonly>
                    </div>

                    <div class="col-md-6">
                        <label for="resultStatus" class="form-label">Result Status</label>
                        <select id="resultStatus" name="resultStatus" class="form-select" required>
                            <option value="Pending" ${empty labResult.resultStatus or labResult.resultStatus eq 'Pending' ? 'selected' : ''}>Pending</option>
                            <option value="InProgress" ${labResult.resultStatus eq 'InProgress' ? 'selected' : ''}>In Progress</option>
                            <option value="Completed" ${labResult.resultStatus eq 'Completed' ? 'selected' : ''}>Completed</option>
                            <option value="Rejected" ${labResult.resultStatus eq 'Rejected' ? 'selected' : ''}>Rejected</option>
                        </select>
                    </div>

                    <div class="col-md-12">
                        <label for="resultValue" class="form-label">Result Value</label>
                        <input id="resultValue" name="resultValue" type="text" class="form-control"
                               value="${labResult.resultValue}" placeholder="e.g. 13.5 g/dL">
                    </div>

                    <div class="col-md-12">
                        <label for="resultDetails" class="form-label">Result Details</label>
                        <textarea id="resultDetails" name="resultDetails" class="form-control" rows="3"
                                  placeholder="Detailed findings" required>${labResult.resultDetails}</textarea>
                    </div>

                    <div class="col-md-12">
                        <label for="remarks" class="form-label">Remarks</label>
                        <textarea id="remarks" name="remarks" class="form-control" rows="2"
                                  placeholder="Optional notes">${labResult.remarks}</textarea>
                    </div>

                    <div class="col-12 d-flex justify-content-between pt-2">
                        <a href="${pageContext.request.contextPath}/lab/results" class="btn btn-outline-secondary">Back</a>
                        <c:if test="${sessionScope.currentUser.roleName == 'Technician' || sessionScope.currentUser.roleName == 'Admin'}">
                            <button type="submit" class="btn btn-primary px-4">Save Lab Result</button>
                        </c:if>
                    </div>
                </form>

                <c:if test="${not empty labResult and labResult.resultId > 0 and (sessionScope.currentUser.roleName == 'Technician' || sessionScope.currentUser.roleName == 'Admin')}">
                    <hr class="my-4">
                    <form method="post" action="${pageContext.request.contextPath}/lab/results"
                          onsubmit="return confirm('Delete this lab result? This action cannot be undone.');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="resultId" value="${labResult.resultId}">
                        <button type="submit" class="btn btn-outline-danger">Delete Lab Result</button>
                    </form>
                </c:if>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
