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

        <!-- TODO: Implement prescription create/edit form -->
        <div class="card border-0 shadow-sm">
            <div class="card-body p-5 text-center text-muted">
                <i class="bi bi-capsule fs-1 d-block mb-3 opacity-25"></i>
                <h6>Prescription Form</h6>
                <p class="mb-0 small">
                    Implement the prescription form here.<br>
                    Fields: Visit ID, Medicine Name, Dosage, Duration.
                </p>
                <a href="${pageContext.request.contextPath}/prescriptions" class="btn btn-sm btn-outline-secondary mt-3">
                    <i class="bi bi-arrow-left me-1"></i>Back to Prescriptions
                </a>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
