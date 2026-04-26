package servlet;

import dao.ProductDAO;
import model.Product;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;

@WebServlet("/UploadServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize       = 50 * 1024 * 1024,
    maxRequestSize    = 100 * 1024 * 1024
)
public class UploadServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads";
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isSellerLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }
        req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isSellerLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/LoginServlet");
            return;
        }

        HttpSession session = req.getSession(false);
        User seller         = (User) session.getAttribute("loggedUser");

        String productName = req.getParameter("productName").trim();
        String description = req.getParameter("description").trim();
        double price;
        try {
            price = Double.parseDouble(req.getParameter("price"));
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid price format.");
            req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
            return;
        }

        Part filePart = req.getPart("productFile");
        String fileName = Paths.get(filePart.getSubmittedFileName())
                               .getFileName().toString();

        if (fileName.isEmpty()) {
            req.setAttribute("error", "Please select a file to upload.");
            req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
            return;
        }

        if (!isAllowedFileType(fileName)) {
            req.setAttribute("error", "Only PDF, DOCX, ZIP, PNG, JPG files are allowed.");
            req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
            return;
        }

        String uploadPath = getServletContext().getRealPath("")
                          + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String uniqueName = System.currentTimeMillis() + "_" + fileName;
        String fullPath   = uploadPath + File.separator + uniqueName;

        try (InputStream is  = filePart.getInputStream();
             FileOutputStream fos = new FileOutputStream(fullPath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        Product product = new Product(
            seller.getUserId(), productName, description,
            price, UPLOAD_DIR + "/" + uniqueName
        );
        boolean saved = productDAO.uploadProduct(product);

        if (saved) {
            resp.sendRedirect(req.getContextPath() + "/ViewProductsServlet");
        } else {
            req.setAttribute("error", "Database error. Upload failed.");
            req.getRequestDispatcher("/jsp/upload.jsp").forward(req, resp);
        }
    }

    private boolean isSellerLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String role = (String) session.getAttribute("userRole");
        return "seller".equalsIgnoreCase(role);
    }

    private boolean isAllowedFileType(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".pdf")  || lower.endsWith(".docx") ||
               lower.endsWith(".zip")  || lower.endsWith(".png")  ||
               lower.endsWith(".jpg")  || lower.endsWith(".jpeg");
    }
}