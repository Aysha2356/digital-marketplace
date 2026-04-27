<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Product, model.User" %>
<%!
    private String thumbFor(String path) {
        if (path == null) return "&#128196;";
        String lp = path.toLowerCase();
        if (lp.endsWith(".docx") || lp.endsWith(".doc")) return "&#128221;";
        if (lp.endsWith(".zip"))  return "&#128230;";
        return "&#128196;";
    }
    private boolean isPdf(String path) {
        return path != null && path.toLowerCase().endsWith(".pdf");
    }
    private boolean isImage(String path) {
        if (path == null) return false;
        String lp = path.toLowerCase();
        return lp.endsWith(".png") || lp.endsWith(".jpg") || lp.endsWith(".jpeg");
    }
%>
<%
    User    _user     = (User) session.getAttribute("loggedUser");
    String  _role     = (_user != null) ? _user.getRole() : "buyer";
    boolean _isSeller = "seller".equalsIgnoreCase(_role);

    @SuppressWarnings("unchecked")
    List<Product> _products = (List<Product>) request.getAttribute("products");
    int _count = (_products != null) ? _products.size() : 0;

    String  _searchQuery = (String)  request.getAttribute("searchQuery");
    Integer _searchCount = (Integer) request.getAttribute("searchCount");
    boolean _isSearching = (_searchQuery != null && !_searchQuery.isEmpty());
    String  _allActiveClass = _isSearching ? "" : "active";

    double _totalValue = 0.0;
    if (_isSeller && _products != null) {
        for (Product p : _products) _totalValue += p.getPrice();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title><%= _isSeller ? "My Shop" : "Browse Products" %> &middot; Marketly</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css" />

  <script src="https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.min.js"></script>
  <script>
    pdfjsLib.GlobalWorkerOptions.workerSrc =
      'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js';
  </script>

  <style>
    /* ── PDF / Image fill — absolute cover inside .card-thumb ── */
    .pdf-canvas {
      position: absolute;
      top: 0; left: 0;
      width: 100%;
      height: 100%;
      display: block;
    }
    .img-preview {
      position: absolute;
      top: 0; left: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }
    .thumb-icon {
      position: absolute;
      top: 50%; left: 50%;
      transform: translate(-50%, -50%);
      font-size: 64px;
      opacity: 0.45;
    }
    .pdf-loading {
      position: absolute;
      bottom: 10px; left: 50%;
      transform: translateX(-50%);
      font-size: 11px;
      color: #999;
      white-space: nowrap;
      z-index: 2;
    }

    /* ── Search styles ── */
    .search-section { width:100%; max-width:680px; margin:0 auto 12px; }
    .search-form {
      display:flex; align-items:center; background:#fff;
      border:2px solid #e0e0e0; border-radius:50px; overflow:hidden;
      box-shadow:0 2px 12px rgba(0,0,0,.08);
    }
    .search-form:focus-within {
      border-color:var(--orange);
      box-shadow:0 4px 20px rgba(241,100,30,.15);
    }
    .search-form input[type="text"] {
      flex:1; border:none; outline:none; padding:14px 22px;
      font-size:.97rem; background:transparent; color:#222; font-family:inherit;
    }
    .search-form input::placeholder { color:#aaa; }
    .search-form button {
      background:var(--orange); border:none; padding:0 22px;
      height:52px; cursor:pointer; font-size:1.2rem;
      color:#fff; min-width:56px;
    }
    .search-form button:hover { background:var(--orange-dark); }
    .search-clear {
      display:inline-flex; align-items:center; gap:6px;
      background:#f5f5f5; border:1px solid #ddd; color:#555;
      padding:7px 16px; border-radius:20px;
      font-size:.83rem; text-decoration:none; margin-top:10px;
    }
    .search-clear:hover { background:#ebebeb; text-decoration:none; }
    .search-info {
      background:#fff8f3; border:1px solid #fde2cc; border-radius:10px;
      padding:12px 18px; margin-bottom:24px; font-size:.9rem; color:#7a4010;
      display:flex; align-items:center; justify-content:space-between;
      flex-wrap:wrap; gap:8px;
    }
    .search-info strong { color:var(--orange); }

    /* ── Hero + category ── */
    .hero-search-wrap { text-align:center; padding:48px 20px 32px; }
    .hero-search-wrap h1 {
      font-family:'Georgia',serif; font-size:2.4rem;
      font-weight:700; margin-bottom:12px; color:var(--ink);
    }
    .hero-search-wrap p { color:var(--muted); font-size:1rem; margin-bottom:28px; }
    .inline-cat-nav {
      display:flex; gap:8px; flex-wrap:wrap;
      justify-content:center; margin-top:20px;
    }
    .inline-cat-nav a {
      padding:8px 18px; border-radius:20px; font-size:.85rem;
      font-weight:500; text-decoration:none; border:1px solid var(--line);
      color:var(--ink-soft); background:#fff; transition:all .18s;
    }
    .inline-cat-nav a:hover,
    .inline-cat-nav a.active {
      background:var(--orange); border-color:var(--orange);
      color:#fff; text-decoration:none;
    }
  </style>
</head>
<body>

<jsp:include page="header.jsp" />

<main class="page">

<% if (_isSeller) { %>
  <%-- ===== SELLER DASHBOARD ===== --%>
  <div class="dashboard-header">
    <div>
      <h1>Welcome back, <%= _user.getName() %></h1>
      <p class="section-sub">Here's what's happening in your shop today.</p>
    </div>
    <a href="<%= request.getContextPath() %>/UploadServlet" class="btn btn-orange">
      + Upload new product
    </a>
  </div>
  <div class="stats-row">
    <div class="stat-card">
      <div class="label">Listings</div>
      <div class="value"><%= _count %></div>
      <div class="delta">Active in your shop</div>
    </div>
    <div class="stat-card">
      <div class="label">Catalog value</div>
      <div class="value">&#8377;<%= String.format("%.2f", _totalValue) %></div>
      <div class="delta">Sum of listed prices</div>
    </div>
    <div class="stat-card">
      <div class="label">Shop status</div>
      <div class="value" style="color:var(--green);">Live</div>
      <div class="delta">Visible to buyers</div>
    </div>
    <div class="stat-card">
      <div class="label">Seller ID</div>
      <div class="value">#<%= _user.getUserId() %></div>
      <div class="delta">Your unique handle</div>
    </div>
  </div>
  <h2 class="section-title">My uploaded products</h2>
  <p class="section-sub">Manage everything you've published on Marketly.</p>

<% } else { %>
  <%-- ===== BUYER BROWSE ===== --%>
  <div class="hero-search-wrap">
    <h1>Discover handpicked digital goods</h1>
    <p>E-books, templates, study notes, design files &amp; more &mdash; instantly downloadable.</p>
    <div class="search-section">
      <form class="search-form"
            action="<%= request.getContextPath() %>/ViewProductsServlet"
            method="get">
        <input type="text" name="search"
               placeholder="Search for digital products, templates, notes..."
               value="<%= _isSearching ? _searchQuery : "" %>"
               autocomplete="off" />
        <button type="submit">&#128269;</button>
      </form>
      <% if (_isSearching) { %>
        <a class="search-clear"
           href="<%= request.getContextPath() %>/ViewProductsServlet">
          &#10005; Clear search
        </a>
      <% } %>
    </div>
    <div class="inline-cat-nav">
      <a href="<%= request.getContextPath() %>/ViewProductsServlet"
         class="<%= _allActiveClass %>">All Products</a>
      <a href="<%= request.getContextPath() %>/ViewProductsServlet?search=ebook">E-books</a>
      <a href="<%= request.getContextPath() %>/ViewProductsServlet?search=template">Templates</a>
      <a href="<%= request.getContextPath() %>/ViewProductsServlet?search=notes">Study Notes</a>
      <a href="<%= request.getContextPath() %>/ViewProductsServlet?search=design">Design Files</a>
      <a href="<%= request.getContextPath() %>/ViewProductsServlet?search=wallpaper">Wallpapers</a>
      <a href="<%= request.getContextPath() %>/ViewProductsServlet?search=zip">Archives</a>
    </div>
  </div>

  <% if (_isSearching) { %>
    <div class="search-info">
      <span>
        Showing <strong><%= _searchCount %>
        result<%= _searchCount == 1 ? "" : "s" %></strong>
        for &ldquo;<strong><%= _searchQuery %></strong>&rdquo;
      </span>
      <a class="search-clear"
         href="<%= request.getContextPath() %>/ViewProductsServlet">
        &#10005; Clear
      </a>
    </div>
  <% } else { %>
    <h2 class="section-title">
      Hi <%= _user != null ? _user.getName() : "there" %>, here's what's new
    </h2>
    <p class="section-sub">
      <%= _count %> product<%= _count == 1 ? "" : "s" %> available right now
    </p>
  <% } %>
<% } %>

  <%-- ===== PRODUCT GRID ===== --%>
  <% if (_products == null || _products.isEmpty()) { %>
    <div class="empty">
      <div class="icon"><%= _isSearching ? "&#128270;" : "&#128230;" %></div>
      <% if (_isSearching) { %>
        <h3>No results found</h3>
        <p>No products matched &ldquo;<strong><%= _searchQuery %></strong>&rdquo;.</p>
        <p style="margin-top:16px;">
          <a href="<%= request.getContextPath() %>/ViewProductsServlet"
             class="btn btn-orange">Browse all products</a>
        </p>
      <% } else if (_isSeller) { %>
        <h3>No products yet</h3>
        <p>Upload your first digital product to start selling.</p>
        <p style="margin-top:16px;">
          <a href="<%= request.getContextPath() %>/UploadServlet"
             class="btn btn-orange">+ Upload your first product</a>
        </p>
      <% } else { %>
        <h3>No products available</h3>
        <p>Check back soon — sellers are uploading new items.</p>
      <% } %>
    </div>
  <% } else { %>
    <div class="grid">
      <% for (Product p : _products) {
           String _fp  = p.getFilePath();
           String _pid = "thumb_" + p.getProductId();
      %>
        <article class="card">

          <%-- Smart Thumbnail --%>
          <div class="card-thumb" id="wrap_<%= _pid %>">
            <% if (isPdf(_fp)) { %>
              <canvas class="pdf-canvas"
                      id="<%= _pid %>"
                      data-pdf="<%= request.getContextPath() %>/<%= _fp %>">
              </canvas>
              <span class="pdf-loading" id="loading_<%= _pid %>">Loading...</span>
            <% } else if (isImage(_fp)) { %>
              <img class="img-preview"
                   src="<%= request.getContextPath() %>/<%= _fp %>"
                   alt="<%= p.getProductName() %>"
                   onerror="this.style.display='none'" />
            <% } else { %>
              <span class="thumb-icon"><%= thumbFor(_fp) %></span>
            <% } %>
            <% if (_isSeller) { %>
              <span class="badge">Yours</span>
            <% } %>
          </div>

          <div class="card-body">
            <h3 class="card-title"><%= p.getProductName() %></h3>
            <p class="card-desc">
              <%= p.getDescription() != null ? p.getDescription() : "" %>
            </p>
            <div class="card-price">
              <% if (p.getPrice() == 0.0) { %>
                Free
              <% } else { %>
                &#8377;<%= String.format("%.2f", p.getPrice()) %>
              <% } %>
            </div>
            <div class="card-actions">
              <% if (_isSeller) { %>
                <a class="btn btn-outline btn-sm btn-block"
                   href="<%= request.getContextPath() %>/DownloadServlet?productId=<%= p.getProductId() %>">
                   Preview file
                </a>
              <% } else { %>
                <a class="btn btn-orange btn-sm btn-block"
                   href="<%= request.getContextPath() %>/DownloadServlet?productId=<%= p.getProductId() %>">
                   &#11015; Download
                </a>
              <% } %>
            </div>
          </div>
        </article>
      <% } %>
    </div>
  <% } %>

</main>

<jsp:include page="footer.jsp" />

<%-- PDF.js — cover-mode fill --%>
<script>
// ── Live search filter ──────────────────────────────
var searchInput = document.querySelector('.search-form input[name="search"]');
var searchForm  = document.querySelector('.search-form');

if (searchInput && searchForm) {

  // Submit form on Enter or button click — let server handle it
  // BUT also do instant live filter while typing
  searchInput.addEventListener('input', function () {
    var query = this.value.toLowerCase().trim();
    var cards  = document.querySelectorAll('.card');
    var count  = 0;

    cards.forEach(function (card) {
      var title = card.querySelector('.card-title');
      var desc  = card.querySelector('.card-desc');
      var tText = title ? title.textContent.toLowerCase() : '';
      var dText = desc  ? desc.textContent.toLowerCase()  : '';

      if (query === '' || tText.includes(query) || dText.includes(query)) {
        card.style.display = 'flex';
        count++;
      } else {
        card.style.display = 'none';
      }
    });

    // Update count text
    var sub = document.querySelector('.section-sub');
    if (sub) {
      sub.textContent = count + ' product' + (count === 1 ? '' : 's') + ' available right now';
    }
  });
}
</script>

</body>
</html>
