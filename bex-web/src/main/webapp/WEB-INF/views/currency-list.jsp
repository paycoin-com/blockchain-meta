<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" 
         import="io.hs.bex.blocknode.model.NodeState,io.hs.bex.blocknode.model.OperationType" %>
         
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
<base href="./">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
<title>Currencies</title>
<!-- Icons-->
<link href="<c:url value = "/assets/lib/@coreui/icons/css/coreui-icons.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/flag-icon-css/css/flag-icon.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/font-awesome/css/font-awesome.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/simple-line-icons/css/simple-line-icons.css"/>" rel="stylesheet">
<!-- Main styles for this application-->
<link href="<c:url value = "/assets/css/style.css"/>" rel="stylesheet">
</head>
<body class="app header-fixed sidebar-fixed aside-menu-fixed sidebar-lg-show">
    <!-- Header -->
    <jsp:include page="include/header.jsp"><jsp:param name="page" value="index" /></jsp:include>
    <!-- /Header -->

    <div class="app-body">
        <!-- SideBar -->
        <jsp:include page="include/sidebar.jsp"><jsp:param name="page" value="index" /></jsp:include>
        <!-- /SideBar -->

        <main class="main"> <!-- Breadcrumb-->
        <ol class="breadcrumb">
            <li class="breadcrumb-item">Currencies</li>
            <li class="breadcrumb-item active"></li>
            <!-- Breadcrumb Menu
          <li class="breadcrumb-menu d-md-down-none">
            <div class="btn-group" role="group" aria-label="Button group">
              <a class="btn" href="#">
                <i class="icon-speech"></i>
              </a>
              <a class="btn" href="./">
                <i class="icon-graph"></i> Â Dashboard</a>
            </div>
          </li>
          -->
        </ol>
        <div class="container-fluid">
            <div class="animated fadeIn">
                <div class="card">
                    <div class="card-header">
                        <i class="fa fa-align-justify"></i> Currencies </div>
                        <div class="card-body">
                        <form class="form-horizontal" action="<c:url value = "/currency-update"/>" method="POST">
                        <span class="input-group-prepend">
                            <button class="btn-sm btn-primary" type="submit">
                                <i class="fa fa-edit"></i> Update Supported</button>&nbsp;&nbsp;
                            <a href="<c:url value = "/currency-sync-start"/>" class="btn-sm btn-primary" role="button">
                                <i class="fa fa-clock"></i> Start Sync Job</a>&nbsp;&nbsp;
                            <a href="<c:url value = "/currency-stats-create"/>" class="btn-sm btn-primary" role="button">
                                <i class="fa fa-clock"></i> Create Stats Data</a>
                        </span><br>
                        
                        <table class="table table-responsive-sm table-bordered table-striped table-sm">
                            <thead>
                                <tr>
                                    <th>No</th>
                                    <th>Support</th>
                                    <th>Code</th>
                                    <th>Codenum</th>
                                    <th>Name</th>
                                    <th>Symbol</th>
                                    <th>Type</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- ************************ -->
                                <c:forEach var="currency" items="${currencies}" varStatus="loopCounter">
                                <tr>
                                    <td>${loopCounter.count}</td>
                                    <td>                                                
                                        <c:choose>
                                            <c:when test="${currency.supported }">
                                                <input value="${currency.code }" name="supported" type="checkbox" checked="checked">
                                            </c:when>
                                            <c:otherwise>
                                                <input value="${currency.code }" name="supported" type="checkbox">
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><a href="<c:url value = "/currency-details?code=${currency.code }"/>">${currency.code }</a></td>
                                    <td>${currency.codeNumeric}</td>
                                    <td>${currency.displayName}</td>
                                    <td>Unicode:${currency.symbolUnicode}&nbsp;&nbsp;-&nbsp;&nbsp;${currency.symbol}</td>
                                    <td>${currency.type}</td>
                                </tr>
                                </c:forEach>
                                <!-- ************************ -->
                            </tbody>
                        </table>
                        </form>
                        <ul class="pagination">
                          <li class="page-item">
                            <a class="page-link" href="#">Prev</a>
                          </li>
                          <li class="page-item active">
                            <a class="page-link" href="#">1</a>
                          </li>
                          <li class="page-item">
                            <a class="page-link" href="#">2</a>
                          </li>
                          <li class="page-item">
                            <a class="page-link" href="#">Next</a>
                          </li>
                        </ul>                    
                    </div>
                    <div class="card-footer"></div>
                </div>
            </div>
        </div>
        </main>
    </div>

    <!-- Footer -->
    <jsp:include page="include/footer.jsp"><jsp:param name="page" value="index" /></jsp:include>
    <!-- Footer -->

    <!-- CoreUI and necessary plugins-->
    <script src="<c:url value = "/assets/lib/jquery/js/jquery.min.js"/>"></script>
    <script src="<c:url value = "/assets/lib/popper.js/js/popper.min.js"/>"></script>
    <script src="<c:url value = "/assets/lib/bootstrap/js/bootstrap.min.js"/>"></script>
    <script src="<c:url value = "/assets/lib/perfect-scrollbar/js/perfect-scrollbar.min.js"/>"></script>
    <script src="<c:url value = "/assets/lib/@coreui/coreui/js/coreui.min.js"/>"></script>
    <!-- Plugins and scripts required by this view-->
    <script type="text/javascript">
    
    
    </script>
</body>
</html>