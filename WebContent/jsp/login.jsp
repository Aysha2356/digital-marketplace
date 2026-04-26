<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Sign in &middot; Marketly</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css" />
</head>
<body>

<jsp:include page="header.jsp" />

<main class="page-narrow">
  <div class="auth-card">
    <h2>Welcome back</h2>
    <p class="sub">Sign in to browse, buy, or sell digital products.</p>

    <% String err = (String) request.getAttribute("error"); %>
    <% String ok  = (String) request.getAttribute("success"); %>
    <% if (err != null) { %><div class="alert alert-error"><%= err %></div><% } %>
    <% if (ok  != null) { %><div class="alert alert-success"><%= ok %></div><% } %>

    <form action="<%= request.getContextPath() %>/LoginServlet" method="post">
      <div class="form-group">
        <label for="email">Email address</label>
        <input type="email" id="email" name="email" required placeholder="you@example.com" />
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required placeholder="Your password" />
      </div>
      <button type="submit" class="btn btn-orange btn-block">Sign in</button>
    </form>

    <div class="form-footer">
      Don't have an account? <a href="<%= request.getContextPath() %>/RegisterServlet">Register here</a>
    </div>
  </div>
</main>

<jsp:include page="footer.jsp" />
</body>
</html>
