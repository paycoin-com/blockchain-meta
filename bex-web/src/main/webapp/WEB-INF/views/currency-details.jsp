<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" 
         import="io.hs.bex.blocknode.model.NodeState,io.hs.bex.blocknode.model.OperationType" %>
                  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
<base href="./">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
<title>Currency details</title>
<!-- Icons-->
<link href="<c:url value = "/assets/lib/@coreui/icons/css/coreui-icons.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/flag-icon-css/css/flag-icon.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/font-awesome/css/font-awesome.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/simple-line-icons/css/simple-line-icons.css"/>" rel="stylesheet">
<!-- Main styles for this application-->
<link href="<c:url value = "/assets/css/style.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/pace-progress/css/pace.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/datatables/datatables-1.10.18/css/jquery.dataTables.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/datatables/fixedheader-3.1.4/css/fixedHeader.dataTables.min.css"/>" rel="stylesheet">

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
            <li class="breadcrumb-item">Currency</li>
            <li class="breadcrumb-item active">Currency-Details</li>
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
                <div class="row">
                    <div class="col-lg-10">
                        <div class="card">
                            <div class="card-header">
                            </div>
                            <form class="form-horizontal" action="<c:url value = "/currency-update"/>" method="get">
                            <input type="hidden" name="code" value="${currency.code }">
                            <div class="card-body">
                                <div class="row">
                                <table class="table table-responsive-sm table-bordered table-striped table-sm">
                                    <tbody>
                                        <tr>
                                            <th>Code:</th>
                                            <td>${currency.code }</td>
                                        </tr>
                                        <tr>
                                            <th>Name:</th>
                                            <td>${currency.displayName }</td>
                                        </tr>
                                        <tr>
                                            <th>Symbol:</th>
                                            <td>${currency.symbolUnicode}&nbsp;&nbsp;-&nbsp;&nbsp;${currency.symbol}</td>
                                        </tr>
                                        <tr>
                                            <th>Type:</th>
                                            <td>${currency.type}</td>
                                        </tr>
                                        <tr>
                                            <th>Details:</th>
                                            <td>
                                                <div class="form-group">
                                                  <label for="details">Comment:</label>
                                                  <textarea class="form-control" name="details"
                                                        rows="8" id="details">${currency.details}</textarea>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th></th>
                                            <td><button class="btn-sm btn-primary" type="submit">
                                                <i class="fa fa-edit"></i> Update Info</button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                                </div>
                            </div>
                            </form>
                            <br>
                            <div class="card-footer"></div>
                        </div>
                    </div>
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
    <script src="<c:url value = "/assets/lib/pace-progress/js/pace.min.js"/>"></script>
    <script src="<c:url value = "/assets/lib/perfect-scrollbar/js/perfect-scrollbar.min.js"/>"></script>
    <script src="<c:url value = "/assets/lib/@coreui/coreui/js/coreui.min.js"/>"></script>
    
    <script src="<c:url value = "/assets/lib/datatables/datatables-1.10.18/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value = "/assets/lib/datatables/fixedheader-3.1.4/js/dataTables.fixedHeader.min.js"/>"></script>
    
    <!-- Plugins and scripts required by this view-->
    <script type="text/javascript">
    
    /* $(document).ready(function() 
    {
        $('#tbTransaction').DataTable( {
            fixedHeader: true
        } );
    } ); */
    
    </script>
    </body>
</html>