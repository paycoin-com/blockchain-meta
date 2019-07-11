<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<header class="app-header navbar">
  <button class="navbar-toggler sidebar-toggler d-lg-none mr-auto" type="button" data-toggle="sidebar-show">
    <span class="navbar-toggler-icon"></span>
  </button>
  <a class="navbar-brand" href="#">
    <i class="icon-wallet"></i>&nbsp;&nbsp;Block-Explorer
  </a>
  <button class="navbar-toggler sidebar-toggler d-md-down-none" type="button" data-toggle="sidebar-lg-show">
    <span class="navbar-toggler-icon"></span>
  </button>
  <!-- ---------------- -->
  <c:url value="/logout" var="logoutUrl"/>
  <form id="formHeaderMenu" action="${logoutUrl}" method="POST">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <!--  --------------- -->
  <ul class="nav navbar-nav ml-auto">
    <li class="nav-item dropdown">
      <a class="nav-link" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
        <img class="img-avatar" src="<c:url value = "/assets/img/avatar.png"/>" alt="user@wallet.com">
        <sec:authentication property="principal.username" />
      </a>
      <div class="dropdown-menu dropdown-menu-right">
        <div class="dropdown-header text-center">
          <strong>Account</strong>
        </div>
        <a class="dropdown-item" href="#">
          <i class="fa fa-user"></i> Profile</a>
        <div class="divider"></div>
        <a class="dropdown-item" href="#" onclick="submitLogoutForm();">
          <i class="fa fa-lock"></i> Logout</a>
      </div>
    </li>
  </ul>

  </form>
  <!--  
  <button class="navbar-toggler aside-menu-toggler d-md-down-none" type="button" data-toggle="aside-menu-lg-show">
    <span class="navbar-toggler-icon"></span>
  </button>
  <button class="navbar-toggler aside-menu-toggler d-lg-none" type="button" data-toggle="aside-menu-show">
    <span class="navbar-toggler-icon"></span>
  </button>
  -->
</header>

<script>
function submitLogoutForm()
{
    $('#formHeaderMenu').submit();
}
</script>