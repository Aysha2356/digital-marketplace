package servlet;

import dao.ProductDAO;
import model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;


/**
 * ════════════════════════════════════════════════════
 *  MODULE 5 — DOWNLOAD PRODUCT
 *  URL Mapping : /DownloadServlet?productId=XX
 *  JSP Pages   : (streams file directly to browser)
 * ════════════════════════════════════════════════════
 *
 * Flow:
 *   Logged-in buyer clicks Download on productList.jsp
 *     → GET /DownloadServlet?productId=XX
 *     → Authenticate session
 *     → Fetch product record from DB via ProductDAO
 *     → Locate file on server disk
 *     → Stream file bytes to browser as attachment
 */
@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {

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

        // ── Parse Product ID ──────────────────────────────────────────────────
        String idParam = req.getParameter("productId");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing productId parameter.");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid productId.");
            return;
        }

        // ── Fetch Product Record ──────────────────────────────────────────────
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
            return;
        }

        // ── Resolve File Path ─────────────────────────────────────────────────
        String realPath = getServletContext().getRealPath("") + File.separator
                        + product.getFilePath().replace("/", File.separator);
        File file = new File(realPath);

        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found on server.");
            return;
        }

        // ── Stream File to Client ─────────────────────────────────────────────
        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) mimeType = "application/octet-stream";

        resp.setContentType(mimeType);
        resp.setContentLengthLong(file.length());
        // Force download — browser won't try to preview
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" + file.getName().replaceFirst("^\\d+_", "") + "\"");

        try (InputStream is = new BufferedInputStream(new FileInputStream(file));
             OutputStream os = new BufferedOutputStream(resp.getOutputStream())) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}
