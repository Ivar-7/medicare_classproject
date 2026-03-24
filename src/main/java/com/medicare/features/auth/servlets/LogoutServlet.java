package com.medicare.features.auth.servlets;

import com.medicare.features.auth.services.RememberMeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private final RememberMeService rememberMeService = new RememberMeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (RememberMeService.COOKIE_NAME.equals(cookie.getName())) {
                    try {
                        rememberMeService.revokeToken(cookie.getValue());
                    } catch (Exception ignored) { }
                }
            }
        }

        Cookie clearCookie = new Cookie(RememberMeService.COOKIE_NAME, "");
        clearCookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        clearCookie.setMaxAge(0);
        clearCookie.setHttpOnly(true);
        clearCookie.setSecure(request.isSecure());
        response.addCookie(clearCookie);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
