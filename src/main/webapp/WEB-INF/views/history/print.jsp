<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Medical History" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">
        <div class="mb-4">
            <h5 class="fw-bold mb-1"><i class="bi bi-file-earmark-medical text-primary me-2"></i>Print Medical History</h5>
            <p class="text-muted small mb-0">Search by student ID and print complete history.</p>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm mb-4">
            <div class="card-body p-4">
                <form method="get" action="${pageContext.request.contextPath}/medical-history" class="row g-3 align-items-end">
                    <div class="col-md-6">
                        <label for="regNumber" class="form-label">Student ID (Reg Number)</label>
                        <input id="regNumber" name="regNumber" type="text" class="form-control"
                               value="${regNumber}" placeholder="e.g. STU-001" required>
                    </div>
                    <div class="col-md-6 d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            Load History
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <c:if test="${not empty student}">
            <div class="card border-0 shadow-sm" id="historyPrintArea">
                <div class="card-body p-4 p-md-5">
                    <div class="d-flex justify-content-between align-items-start mb-4">
                        <div>
                            <h5 class="fw-bold mb-1">Student Medical History</h5>
                            <div class="text-muted small">Generated for: ${student.firstName} ${student.lastName} (${student.regNumber})</div>
                            <div class="text-muted small">Faculty: ${student.faculty}</div>
                        </div>
                        <button type="button" class="btn btn-outline-secondary" onclick="window.print()">
                            Print Medical History
                        </button>
                    </div>

                    <h6 class="fw-bold mt-4">Visit History</h6>
                    <c:choose>
                        <c:when test="${empty visits}">
                            <p class="text-muted small">No visits recorded.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive mb-3">
                                <table class="table table-sm table-striped">
                                    <thead>
                                        <tr>
                                            <th>Visit ID</th>
                                            <th>Date</th>
                                            <th>Doctor</th>
                                            <th>Symptoms</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="v" items="${visits}">
                                            <tr>
                                                <td>#${v.visitId}</td>
                                                <td>${v.visitDate}</td>
                                                <td>${v.doctorName}</td>
                                                <td>${v.symptoms}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <h6 class="fw-bold mt-4">Prescription History</h6>
                    <c:choose>
                        <c:when test="${empty prescriptions}">
                            <p class="text-muted small">No prescriptions recorded.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive mb-3">
                                <table class="table table-sm table-striped">
                                    <thead>
                                        <tr>
                                            <th>Prescription ID</th>
                                            <th>Visit ID</th>
                                            <th>Medicine</th>
                                            <th>Dosage</th>
                                            <th>Duration</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="p" items="${prescriptions}">
                                            <tr>
                                                <td>#${p.prescriptionId}</td>
                                                <td>#${p.visitId}</td>
                                                <td>${p.medicineName}</td>
                                                <td>${p.dosage}</td>
                                                <td>${p.duration}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <h6 class="fw-bold mt-4">Lab / Note History</h6>
                    <c:choose>
                        <c:when test="${empty labHistory}">
                            <p class="text-muted small">No lab or treatment notes recorded.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive mb-3">
                                <table class="table table-sm table-striped">
                                    <thead>
                                        <tr>
                                            <th>Note ID</th>
                                            <th>Visit ID</th>
                                            <th>Clinical/Lab Notes</th>
                                            <th>Follow-up Date</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="n" items="${labHistory}">
                                            <tr>
                                                <td>#${n.noteId}</td>
                                                <td>#${n.visitId}</td>
                                                <td>${n.clinicalNotes}</td>
                                                <td>${n.followUpDate}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:if>
    </div>
</main>

<jsp:include page="/components/footer.jsp" />
