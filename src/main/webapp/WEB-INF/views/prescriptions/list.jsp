<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Prescriptions" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h5 class="mb-1 fw-bold"><i class="bi bi-capsule text-primary me-2"></i>Prescriptions</h5>
                <p class="text-muted mb-0 small">Medication prescribed during visits.</p>
            </div>
            <a href="${pageContext.request.contextPath}/prescriptions/new" class="btn btn-primary">
                <i class="bi bi-plus-circle me-1"></i>Write Prescription
            </a>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty prescriptions}">
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-capsule fs-1 d-block mb-2 opacity-25"></i>
                            <p>No prescriptions found.</p>
                            <a href="${pageContext.request.contextPath}/prescriptions/new" class="btn btn-sm btn-primary">
                                Write First Prescription
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
                                        <th class="py-3">Student ID</th>
                                        <th class="py-3">Medicine</th>
                                        <th class="py-3">Dosage</th>
                                        <th class="py-3">Duration</th>
                                        <th class="py-3 text-end px-4">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
    <c:forEach var="p" items="${prescriptions}">
        <tr>
            <td class="px-4 text-muted font-monospace small">#${p.prescriptionId}</td>
            <td>
                <a href="${pageContext.request.contextPath}/visits/${p.visitId}"
                   class="text-decoration-none">
                    Visit #${p.visitId}
                </a>
            </td>
            <td class="text-muted font-monospace small">${p.studentRegNumber}</td>
            <td class="fw-medium">${p.medicineName}</td>
            <td class="text-muted">${p.dosage}</td>
            <td class="text-muted">${p.duration}</td>
            <td class="text-end px-4">
                <!--Edit Button-->
                <a href="${pageContext.request.contextPath}/prescriptions/${p.prescriptionId}"
                   class="btn btn-sm btn-outline-secondary">
                    <i class="bi bi-pencil"></i>
                </a>

                <!--Delete Button-->
                <a href="${pageContext.request.contextPath}/prescriptions/delete/${p.prescriptionId}" 
                   class="btn btn-sm btn-outline-danger ms-1"
                   onclick="return confirm('Are you sure you want to delete this prescription?')">
                    <i class="bi bi-trash"></i>
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
