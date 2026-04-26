<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>
<% User _u = (User) session.getAttribute("loggedUser"); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Upload Product &middot; Marketly</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css" />
</head>
<body>

<jsp:include page="header.jsp" />

<main class="page" style="max-width:720px;">
  <div class="dashboard-header">
    <div>
      <h1>Upload a digital product</h1>
      <p class="section-sub">Add a new item to your Marketly shop. Buyers can preview &amp; download instantly after purchase.</p>
    </div>
  </div>

  <% String err = (String) request.getAttribute("error"); %>
  <% String ok  = (String) request.getAttribute("success"); %>
  <% if (err != null) { %><div class="alert alert-error"><%= err %></div><% } %>
  <% if (ok  != null) { %><div class="alert alert-success"><%= ok %></div><% } %>

  <div class="auth-card">
    <form action="<%= request.getContextPath() %>/UploadServlet" method="post" enctype="multipart/form-data">
      <div class="form-group">
        <label for="productName">Product name</label>
        <input type="text" id="productName" name="productName" required placeholder="e.g. Modern Resume Template" />
      </div>

      <div class="form-group">
        <label for="description">Description</label>
        <textarea id="description" name="description" required placeholder="Describe what's inside, format, page count, what buyers get..."></textarea>
      </div>

      <div class="form-group">
        <label for="price">Price (&#8377;)</label>
        <input type="number" id="price" name="price" min="0" step="0.01" required placeholder="0.00" />
        <div class="hint">Set 0 for a free download.</div>
      </div>

      <div class="form-group">
        <label>Product file</label>
        <label class="dropzone" id="dz">
          <div class="icon">&#128206;</div>
          <p id="dz-text"><strong>Click to choose a file</strong> or drag &amp; drop</p>
          <small>PDF, DOCX, ZIP, PNG, JPG &middot; up to 50&nbsp;MB</small>
          <input type="file" id="productFile" name="productFile"
                 accept=".pdf,.docx,.zip,.png,.jpg,.jpeg" required />
        </label>
      </div>

      <div style="display:flex; gap:10px;">
        <button type="submit" class="btn btn-orange" style="flex:1;">Publish product</button>
        <a href="<%= request.getContextPath() %>/ViewProductsServlet" class="btn btn-outline">Cancel</a>
      </div>
    </form>
  </div>
</main>

<jsp:include page="footer.jsp" />

<script>
  // Lightweight dropzone visual feedback (no logic change — same multipart POST)
  (function () {
    var input = document.getElementById('productFile');
    var dz    = document.getElementById('dz');
    var txt   = document.getElementById('dz-text');
    if (!input || !dz) return;
    input.addEventListener('change', function () {
      if (input.files && input.files[0]) {
        dz.classList.add('has-file');
        txt.innerHTML = '<strong>' + input.files[0].name + '</strong> selected';
      }
    });
  })();
</script>
</body>
</html>
