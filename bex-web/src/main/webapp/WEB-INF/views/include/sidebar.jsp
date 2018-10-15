<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="sidebar">
    <nav class="sidebar-nav">
      <ul class="nav">
        <li class="nav-item">
          <a class="nav-link" href="<c:url value = "/index"/>">
            <i class="nav-icon icon-speedometer"></i> Dashboard
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="<c:url value = "/node-list"/>">
            <i class="nav-icon fa fa-database"></i> Node list</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="<c:url value = "/block-list"/>">
            <i class="nav-icon icon-graph"></i> Block Chain</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="<c:url value = "/currency-list"/>">
            <i class="nav-icon icon-graph"></i>Currencies</a>
        </li>
      </ul>
    </nav>
    <button class="sidebar-minimizer brand-minimizer" type="button"></button>
  </div>
  