<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty prescription ? 'New Prescription' : 'Edit Prescription'}" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="mb-4">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/prescriptions" class="text-decoration-none">Prescriptions</a>
                    </li>
                    <li class="breadcrumb-item active">
                        ${empty prescription ? 'New Prescription' : 'Edit Prescription'}
                    </li>
                </ol>
            </nav>
            <h5 class="fw-bold mb-0">
                ${empty prescription ? 'Write New Prescription' : 'Edit Prescription'}
            </h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-4 p-md-5">
                <h6 class="fw-bold mb-3">Prescription Details</h6>

                <form method="post" action="${pageContext.request.contextPath}/prescriptions" class="row g-3">

                    <input type="hidden" name="prescriptionId" value="${prescription.prescriptionId}">

                    <div class="col-md-6">
                        <label for="visitId" class="form-label">Visit ID</label>
                        <input id="visitId" type="number" name="visitId" class="form-control"
                               value="${prescription.visitId}" min="1" step="1" required>
                        <div class="form-text">Enter the numeric ID from the medical visit.</div>
                    </div>

                    <div class="col-md-6">
                        <label for="studentRegNumber" class="form-label">Student ID (Reg Number)</label>
                        <input id="studentRegNumber" type="text" name="studentRegNumber" class="form-control"
                               value="${prescription.studentRegNumber gt 0 ? prescription.studentRegNumber : studentRegNumber}"
                               placeholder="e.g. STU-001" required>
                        <div class="form-text">Must match the student linked to the selected visit.</div>
                    </div>

                    <div class="col-md-12">
                        <label for="medicineName" class="form-label">Medicine Name</label>
                        <input id="medicineName" type="text" name="medicineName" class="form-control"
                               value="${prescription.medicineName}" required>
                    </div>

                    <div class="col-md-12">
                           <label for="instructions" class="form-label">Instructions</label>
                           <input id="instructions" type="text" name="instructions" class="form-control"
                               value="${prescription.instructions}" placeholder="e.g. Take after meals" required>
                    </div>

                    <div class="col-md-6">
                        <label for="dosage" class="form-label">Dosage</label>
                        <input id="dosage" type="text" name="dosage" class="form-control"
                               placeholder="e.g. 500mg" value="${prescription.dosage}" required>
                    </div>

                    <div class="col-md-6">
                        <label for="duration" class="form-label">Duration</label>
                        <input id="duration" type="text" name="duration" class="form-control"
                               placeholder="e.g. 7 days" value="${prescription.duration}" required>
                    </div>

                    <div class="col-12 d-flex justify-content-between pt-2">
                        <a href="${pageContext.request.contextPath}/prescriptions" class="btn btn-outline-secondary">
                            Cancel
                        </a>
                        <button type="submit" class="btn btn-primary px-4">
                            Save Prescription
                        </button>
                    </div>
                </form>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
