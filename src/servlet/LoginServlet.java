package servlet;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String email    = req.getParameter("email").trim().toLowerCase();
        String password = req.getParameter("password");

        if (email.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Email and password are required.");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
            return;
        }

        String hashedPassword = PasswordUtil.hash(password);
        User user = userDAO.loginUser(email, hashedPassword);

        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedUser", user);
            session.setAttribute("userId",     user.getUserId());
            session.setAttribute("userRole",   user.getRole());
            session.setMaxInactiveInterval(30 * 60);

            if ("seller".equalsIgnoreCase(user.getRole())) {
                resp.sendRedirect(req.getContextPath() + "/UploadServlet");
            } else {
                resp.sendRedirect(req.getContextPath() + "/ViewProductsServlet");
            }
        } else {
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        }
    }
}