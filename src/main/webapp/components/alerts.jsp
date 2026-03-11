<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty requestScope.success}">
    <div class="alert alert-success alert-dismissible fade show d-flex align-items-center gap-2 mb-0" role="alert">
        <i class="bi bi-check-circle-fill flex-shrink-0"></i>
        <div>${requestScope.success}</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty requestScope.error}">
    <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center gap-2 mb-0" role="alert">
        <i class="bi bi-exclamation-triangle-fill flex-shrink-0"></i>
        <div>${requestScope.error}</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty requestScope.warning}">
    <div class="alert alert-warning alert-dismissible fade show d-flex align-items-center gap-2 mb-0" role="alert">
        <i class="bi bi-exclamation-circle-fill flex-shrink-0"></i>
        <div>${requestScope.warning}</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
