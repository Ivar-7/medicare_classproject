package com.medicare.shared.filters;

import com.medicare.models.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String path = requestURI.substring(contextPath.length());

        // Public routes (no auth required)
        boolean isPublic = isPublicPath(path);

        if (isPublic) {
            chain.doFilter(req, res);
            return;
        }

        // Protected routes - require authentication
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        // Role-based access control
        if (!isAuthorizedForPath(path, currentUser)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") || path.equals("") ||
               path.equals("/login") ||
               path.equals("/register") ||
               path.startsWith("/assets/") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/WEB-INF/");
    }

    private boolean isAuthorizedForPath(String path, User currentUser) {
        // Doctor-only paths
        if (path.startsWith("/prescriptions") && currentUser.getRole() != User.Role.Doctor) {
            return false;
        }

        // Receptionist-only paths
        if (path.startsWith("/students/new") || path.contains("students/register")) {
            if (currentUser.getRole() != User.Role.Receptionist) {
                return false;
            }
        }

        // Notes are available to Doctor and Technician
        if (path.startsWith("/notes")
            && currentUser.getRole() != User.Role.Doctor
            && currentUser.getRole() != User.Role.Technician) {
            return false;
        }

        // Visits are available to Receptionist and Doctor
        if (path.startsWith("/visits")
            && currentUser.getRole() != User.Role.Receptionist
            && currentUser.getRole() != User.Role.Doctor
            && currentUser.getRole() != User.Role.Admin) {
            return false;
        }

        // Medical history available to Doctor and Receptionist
        if (path.startsWith("/medical-history")
            && currentUser.getRole() != User.Role.Doctor
            && currentUser.getRole() != User.Role.Receptionist) {
            return false;
        }

        // Admin-only paths
        if (path.startsWith("/audit") || path.startsWith("/users")) {
            if (currentUser.getRole() != User.Role.Admin) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void destroy() { }
}

