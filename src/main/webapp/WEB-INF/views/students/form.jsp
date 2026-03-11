<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty student ? 'Add Student' : 'Edit Student'}" scope="request" />

<jsp:include page="/components/header.jsp" />
<jsp:include page="/components/navbar.jsp" />

<main class="main-content">
    <div class="container-fluid px-4 py-4">

        <div class="mb-4">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/students" class="text-decoration-none">Students</a>
                    </li>
                    <li class="breadcrumb-item active">${empty student ? 'Add Student' : 'Edit Student'}</li>
                </ol>
            </nav>
            <h5 class="fw-bold mb-0">
                <i class="bi bi-person-${empty student ? 'plus' : 'pencil'} text-primary me-2"></i>
                ${empty student ? 'Register New Student' : 'Edit Student Profile'}
            </h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <!-- TODO: Implement student create/edit form -->
        <div class="card border-0 shadow-sm">
            <div class="card-body p-5 text-center text-muted">
                <i class="bi bi-pencil-square fs-1 d-block mb-3 opacity-25"></i>
                <h6>Student Form</h6>
                <p class="mb-0 small">
                    Implement the student registration / edit form here.<br>
                    Fields: Reg Number, Full Name, Date of Birth, Gender, Faculty, Contact.
                </p>
                <a href="${pageContext.request.contextPath}/students" class="btn btn-sm btn-outline-secondary mt-3">
                    <i class="bi bi-arrow-left me-1"></i>Back to Students
                </a>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
