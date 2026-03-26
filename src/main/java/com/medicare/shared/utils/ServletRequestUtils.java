package com.medicare.shared.utils;

import com.medicare.models.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class ServletRequestUtils {

    private ServletRequestUtils() {
    }

    public static String trim(String value) {
        return value == null ? null : value.trim();
    }

    public static void exposeAlertsFromQuery(HttpServletRequest request) {
        exposeQueryAlert(request, "success");
        exposeQueryAlert(request, "error");
        exposeQueryAlert(request, "warning");
    }

    public static void redirectWithMessage(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String path,
                                           String messageType,
                                           String message) throws IOException {
        response.sendRedirect(request.getContextPath() + path + "?" + messageType + "=" + encode(message));
    }

    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object currentUser = session.getAttribute("currentUser");
        return currentUser instanceof User ? (User) currentUser : null;
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static void exposeQueryAlert(HttpServletRequest request, String key) {
        String value = trim(request.getParameter(key));
        if (value != null && !value.isBlank()) {
            request.setAttribute(key, value);
        }
    }
}