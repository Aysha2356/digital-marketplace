package servlet;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String name     = req.getParameter("name").trim();
        String email    = req.getParameter("email").trim().toLowerCase();
        String password = req.getParameter("password");
        String role     = req.getParameter("role");

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            req.setAttribute("error", "All fields are required.");
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }

        if (password.length() < 6) {
            req.setAttribute("error", "Password must be at least 6 characters.");
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }

        if (userDAO.isEmailExists(email)) {
            req.setAttribute("error", "Email is already registered. Please login.");
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }

        String hashedPassword = PasswordUtil.hash(password);
        User newUser = new User(name, email, hashedPassword, role);
        boolean success = userDAO.registerUser(newUser);

        if (success) {
            req.setAttribute("success", "Registration successful! Please login.");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        } else {
            req.setAttribute("error", "Registration failed. Please try again.");
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
        }
    }
}