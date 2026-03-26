<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="isEdit" value="${not empty originalRegNumber}" />
<c:set var="pageTitle" value="${isEdit ? 'Edit Student' : 'Add Student'}" scope="request" />

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
                    <li class="breadcrumb-item active">${isEdit ? 'Edit Student' : 'Add Student'}</li>
                </ol>
            </nav>
            <h5 class="fw-bold mb-0">
                ${isEdit ? 'Edit Student Profile' : 'Register New Student'}
            </h5>
        </div>

        <jsp:include page="/components/alerts.jsp" />

        <div class="card border-0 shadow-sm">
            <div class="card-body p-4 p-md-5">
                <form method="post" action="${pageContext.request.contextPath}/students" class="row g-3">

                    <input type="hidden" name="originalRegNumber"
                           value="${originalRegNumber}">

                    <div class="col-md-6">
                        <label for="regNumber" class="form-label">Registration Number</label>
                        <input id="regNumber" name="regNumber" type="text" class="form-control"
                               value="${student.regNumber}" maxlength="40" ${isEdit ? 'readonly' : ''} required>
                        <div class="form-text">
                            <c:choose>
                                <c:when test="${not isEdit}">Unique student registration number.</c:when>
                                <c:otherwise>Registration number is immutable after creation.</c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="fullName" class="form-label">Full Name</label>
                        <input id="fullName" name="fullName" type="text" class="form-control"
                               value="${student.fullName}" maxlength="120"
                               pattern="[A-Za-z][A-Za-z\s'.-]*"
                               title="Use letters, spaces, apostrophes, dots, and hyphens only."
                               oninput="this.value=this.value.replace(/[0-9]/g,'')" required>
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
                        <button type="submit" name="action" value="${isEdit ? 'update' : 'create'}"
                                class="btn btn-primary">
                            ${isEdit ? 'Update Student' : 'Register Student'}
                        </button>
                        <a href="${pageContext.request.contextPath}/students" class="btn btn-outline-secondary">
                            Back to Students
                        </a>
                    </div>
                </form>

                <c:if test="${isEdit and not empty student and not empty student.regNumber}">
                    <hr class="my-4">
                    <form method="post" action="${pageContext.request.contextPath}/students"
                          onsubmit="return confirm('Delete this student record? This action cannot be undone.');">
                        <input type="hidden" name="regNumber" value="${student.regNumber}">
                        <button type="submit" name="action" value="delete" class="btn btn-outline-danger">
                            Delete Student
                        </button>
                    </form>
                </c:if>
            </div>
        </div>

    </div>
</main>

<script>
    (function() {
        const dobInput = document.getElementById('dob');
        if (!dobInput) {
            return;
        }

        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        dobInput.max = `${yyyy}-${mm}-${dd}`;
    })();
</script>

<jsp:include page="/components/footer.jsp" />
