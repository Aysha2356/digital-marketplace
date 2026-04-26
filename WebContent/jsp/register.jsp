<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Create your account &middot; Marketly</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css" />
</head>
<body>

<jsp:include page="header.jsp" />

<main class="page-narrow">
  <div class="auth-card">
    <h2>Create your account</h2>
    <p class="sub">Join Marketly to discover digital goods or open your own shop.</p>

    <% String err = (String) request.getAttribute("error"); %>
    <% if (err != null) { %><div class="alert alert-error"><%= err %></div><% } %>

    <form action="<%= request.getContextPath() %>/RegisterServlet" method="post">
      <div class="form-group">
        <label for="name">Full name</label>
        <input type="text" id="name" name="name" required placeholder="Your name" />
      </div>
      <div class="form-group">
        <label for="email">Email address</label>
        <input type="email" id="email" name="email" required placeholder="you@example.com" />
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required minlength="6" placeholder="At least 6 characters" />
        <div class="hint">Use at least 6 characters. Stored securely as a SHA-256 hash.</div>
      </div>

      <div class="form-group">
        <label>I want to join as</label>
        <div class="role-toggle">
          <input type="radio" id="role-buyer" name="role" value="buyer" checked />
          <label for="role-buyer"><span class="icon">&#128722;</span>Buyer<small>Browse &amp; download</small></label>

          <input type="radio" id="role-seller" name="role" value="seller" />
          <label for="role-seller"><span class="icon">&#127979;</span>Seller<small>Upload &amp; sell</small></label>
        </div>
      </div>

      <button type="submit" class="btn btn-orange btn-block">Create account</button>
    </form>

    <div class="form-footer">
      Already have an account? <a href="<%= request.getContextPath() %>/LoginServlet">Sign in</a>
    </div>
  </div>
</main>

<jsp:include page="footer.jsp" />
</body>
</html>
