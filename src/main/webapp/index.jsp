<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.medicare.features.users.services.UserService" %>
<%
    Object currentUser = session != null ? session.getAttribute("currentUser") : null;

    if (currentUser != null) {
        response.sendRedirect(request.getContextPath() + "/home");
    } else {
        try {
            int totalUsers = new UserService().countUsers();
            if (totalUsers <= 1) {
                response.sendRedirect(request.getContextPath() + "/register");
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
        } catch (Exception ignored) {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
%>
