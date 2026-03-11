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
                <i class="bi bi-clipboard2-plus text-primary me-2"></i>
                ${empty visit ? 'Record New Visit' : 'Edit Medical Visit'}
            </h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <!-- TODO: Implement visit create/edit form -->
        <div class="card border-0 shadow-sm">
            <div class="card-body p-5 text-center text-muted">
                <i class="bi bi-clipboard2-plus fs-1 d-block mb-3 opacity-25"></i>
                <h6>Medical Visit Form</h6>
                <p class="mb-0 small">
                    Implement the visit form here.<br>
                    Fields: Student (reg number), Doctor, Visit Date, Symptoms, Diagnosis.
                </p>
                <a href="${pageContext.request.contextPath}/visits" class="btn btn-sm btn-outline-secondary mt-3">
                    <i class="bi bi-arrow-left me-1"></i>Back to Visits
                </a>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
