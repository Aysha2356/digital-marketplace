package servlet;

import dao.ProductDAO;
import model.Product;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ════════════════════════════════════════════════════
 *  MODULE 4 — VIEW PRODUCT LIST + SERVER-SIDE SEARCH
 *  URL Mapping : /ViewProductsServlet
 *  URL with search: /ViewProductsServlet?search=keyword
 * ════════════════════════════════════════════════════
 */
@WebServlet("/ViewProductsServlet")
public class ViewProductsServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ── Session Guard ─────────────────────────────────────────────────────
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        User loggedUser = (User) session.getAttribute("loggedUser");

        // ── Get search query from URL parameter ───────────────────────────────
        String searchQuery = req.getParameter("search");
        if (searchQuery != null) {
            searchQuery = searchQuery.trim();
        }

        List<Product> products;

        // ── Fetch Products Based on Role ──────────────────────────────────────
        if ("seller".equalsIgnoreCase(loggedUser.getRole())) {
            products = productDAO.getProductsBySeller(loggedUser.getUserId());
            req.setAttribute("pageTitle", "My Uploaded Products");
        } else {
            products = productDAO.getAllProducts();
            req.setAttribute("pageTitle", "Browse Products");
        }

        // ── Server-Side Search Filter ─────────────────────────────────────────
        if (searchQuery != null && !searchQuery.isEmpty()) {
            final String query = searchQuery.toLowerCase();
            products = products.stream()
                .filter(p ->
                    (p.getProductName() != null &&
                     p.getProductName().toLowerCase().contains(query))
                    ||
                    (p.getDescription() != null &&
                     p.getDescription().toLowerCase().contains(query))
                )
                .collect(Collectors.toList());

            req.setAttribute("searchQuery", searchQuery);
            req.setAttribute("searchCount", products.size());
        }

        // ── Pass Data to JSP ──────────────────────────────────────────────────
        req.setAttribute("products",   products);
        req.setAttribute("loggedUser", loggedUser);
        req.getRequestDispatcher("/jsp/productList.jsp").forward(req, resp);
    }

    // ── POST handles search form submission ───────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String search = req.getParameter("search");
        if (search != null && !search.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath()
                + "/ViewProductsServlet?search="
                + java.net.URLEncoder.encode(search.trim(), "UTF-8"));
        } else {
            resp.sendRedirect(req.getContextPath() + "/ViewProductsServlet");
        }
    }
}