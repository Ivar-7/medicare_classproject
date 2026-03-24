<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.medicare.features.users.services.UserService" %>
<%
    HttpSession session = request.getSession(false);
    Object currentUser = session != null ? session.getAttribute("currentUser") : null;

    if (currentUser != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
    } else {
        try {
            if (new UserService().countUsers() > 0) {
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                response.sendRedirect(request.getContextPath() + "/register");
            }
        } catch (Exception ignored) {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
%>
