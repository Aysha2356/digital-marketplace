<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>
<%
    User _hUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;
    String _ctx = request.getContextPath();
    String _role = (_hUser != null) ? _hUser.getRole() : null;
%>
<header class="top-header">
  <div class="top-header-inner">

    <a href="<%= _ctx %>/ViewProductsServlet" class="brand">
      Marketly<span class="dot">.</span>
    </a>

    <nav class="user-nav">
      <% if (_hUser == null) { %>
        <a href="<%= _ctx %>/LoginServlet">
          <span class="icon">&#128100;</span>Sign in
        </a>
        <a href="<%= _ctx %>/RegisterServlet">
          <span class="icon">&#10133;</span>Register
        </a>
      <% } else { %>
        <span class="greeting">Hi, <strong><%= _hUser.getName() %></strong></span>
        <% if ("seller".equalsIgnoreCase(_role)) { %>
          <a href="<%= _ctx %>/UploadServlet">
            <span class="icon">&#11014;</span>Upload
          </a>
          <a href="<%= _ctx %>/ViewProductsServlet">
            <span class="icon">&#128722;</span>My Shop
          </a>
        <% } else { %>
          <a href="<%= _ctx %>/ViewProductsServlet">
            <span class="icon">&#128722;</span>Browse
          </a>
        <% } %>
        <a href="<%= _ctx %>/LogoutServlet">
          <span class="icon">&#128682;</span>Logout
        </a>
      <% } %>
    </nav>

  </div>
</header>
