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
                <i class="bi bi-capsule text-primary me-2"></i>
                ${empty prescription ? 'Write New Prescription' : 'Edit Prescription'}
            </h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

<div class="card border-0 shadow-sm">
    <div class="card-body p-4">
        <h6 class="fw-bold mb-3">Prescription Details</h6>
        
        <form action="${pageContext.request.contextPath}/prescriptions" method="POST">
    
            <input type="hidden" name="prescriptionId" value="${prescription.prescriptionId}">

            <div class="mb-3">
                <label class="form-label">Visit ID</label>
                <input type="number" name="visitId" class="form-control" 
                       value="${prescription.visitId}" required>
                <div class="form-text">Enter the numeric ID from the medical visit.</div>
            </div>

            <div class="mb-3">
                <label class="form-label">Student ID (Reg Number)</label>
                <input type="text" name="studentRegNumber" class="form-control"
                       value="${not empty prescription.studentRegNumber ? prescription.studentRegNumber : studentRegNumber}"
                       placeholder="e.g. STU-001" required>
                <div class="form-text">Must match the student linked to the selected visit.</div>
            </div>

            <div class="mb-3">
                <label class="form-label">Medicine Name</label>
                <input type="text" name="medicineName" class="form-control" 
                       value="${prescription.medicineName}" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Disease</label>
                <input type="text" name="disease" class="form-control"
                       value="${prescription.disease}" placeholder="e.g. Hypertension" required>
                <div class="form-text">Enter the disease or diagnosis.</div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Dosage</label>
                    <input type="text" name="dosage" class="form-control" placeholder="e.g. 500mg" 
                              value="${prescription.dosage}" required>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Duration</label>
                    <input type="text" name="duration" class="form-control" placeholder="e.g. 7 days" 
                              value="${prescription.duration}" required>
                </div>
            </div>

            <hr class="my-4">

            <div class="d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/prescriptions" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left me-1"></i>Cancel
                </a>
                <button type="submit" class="btn btn-primary px-4">
                    <i class="bi bi-check-lg me-1"></i>Save Prescription
                </button>
            </div>
        </form>
    </div>
</div>

      <!--<a href="${pageContext.request.contextPath}/prescriptions" class="btn btn-sm btn-outline-secondary mt-3">
    <i class="bi bi-arrow-left me-1"></i>Back to Prescriptions
</a>
            </div>
        </div>-->

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
