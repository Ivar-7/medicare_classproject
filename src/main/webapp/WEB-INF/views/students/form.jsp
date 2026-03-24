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

        <div class="card border-0 shadow-sm">
            <div class="card-body p-4 p-md-5">
                <form method="post" action="${pageContext.request.contextPath}/students" class="row g-3">

                    <input type="hidden" name="originalRegNumber"
                           value="${not empty originalRegNumber ? originalRegNumber : student.regNumber}">

                    <div class="col-md-6">
                        <label for="regNumber" class="form-label">Registration Number</label>
                        <input id="regNumber" name="regNumber" type="text" class="form-control"
                               value="${student.regNumber}" maxlength="40" ${not empty student ? 'readonly' : ''} required>
                        <div class="form-text">
                            <c:choose>
                                <c:when test="${empty student}">Unique student registration number.</c:when>
                                <c:otherwise>Registration number is immutable after creation.</c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="fullName" class="form-label">Full Name</label>
                        <input id="fullName" name="fullName" type="text" class="form-control"
                               value="${student.fullName}" maxlength="120" required>
                    </div>

                    <div class="col-md-4">
                        <label for="dob" class="form-label">Date of Birth</label>
                        <input id="dob" name="dob" type="date" class="form-control"
                               value="${student.dob}" required>
                    </div>

                    <div class="col-md-4">
                        <label for="gender" class="form-label">Gender</label>
                        <select id="gender" name="gender" class="form-select" required>
                            <option value="" ${empty student.gender ? 'selected' : ''}>Select gender</option>
                            <option value="Male" ${student.gender == 'Male' ? 'selected' : ''}>Male</option>
                            <option value="Female" ${student.gender == 'Female' ? 'selected' : ''}>Female</option>
                            <option value="Other" ${student.gender == 'Other' ? 'selected' : ''}>Other</option>
                        </select>
                    </div>

                    <div class="col-md-4">
                        <label for="faculty" class="form-label">Faculty</label>
                        <input id="faculty" name="faculty" type="text" class="form-control"
                               value="${student.faculty}" maxlength="100" required>
                    </div>

                    <div class="col-12">
                        <label for="contact" class="form-label">Contact</label>
                        <input id="contact" name="contact" type="text" class="form-control"
                               value="${student.contact}" maxlength="100" required>
                    </div>

                    <div class="col-12 d-flex gap-2 pt-2">
                        <button type="submit" name="action" value="${empty student ? 'create' : 'update'}"
                                class="btn btn-primary">
                            <i class="bi bi-check2-circle me-1"></i>
                            ${empty student ? 'Register Student' : 'Update Student'}
                        </button>
                        <a href="${pageContext.request.contextPath}/students" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i>Back to Students
                        </a>
                    </div>
                </form>

                <c:if test="${not empty student and not empty student.regNumber}">
                    <hr class="my-4">
                    <form method="post" action="${pageContext.request.contextPath}/students"
                          onsubmit="return confirm('Delete this student record? This action cannot be undone.');">
                        <input type="hidden" name="regNumber" value="${student.regNumber}">
                        <button type="submit" name="action" value="delete" class="btn btn-outline-danger">
                            <i class="bi bi-trash me-1"></i>Delete Student
                        </button>
                    </form>
                </c:if>
            </div>
        </div>

    </div>
</main>

<jsp:include page="/components/footer.jsp" />
