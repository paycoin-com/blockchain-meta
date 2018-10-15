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
<title>Block-Chain</title>
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
            <li class="breadcrumb-item">BLocks</li>
            <li class="breadcrumb-item active">Block-Chain</li>
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
                        <i class="fa fa-align-justify"></i> Recent Blocks</div>
                        <div class="card-body">
                        <form class="form-horizontal" action="<c:url value = "/block-list"/>" method="get">
                            <div class="row">
                                <div class="form-group col-sm-2">
                                    <select class="form-control" id="currencyType" name="provider">
                                        <option selected="selected" value="btc-testnet">BTC-Testnet</option>
                                        <option value="btc-regtest">BTC-Regtest</option>
                                        <option value="btc">BTC-Mainnet</option>
                                        <option>BCH</option>
                                        <option>ETH</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <input class="form-control" id="input1-group2" type="text" name="hash" 
                                        placeholder="Block Hash">
                                        <span class="input-group-prepend">
                                            <button class="btn btn-primary" type="submit">
                                                <i class="fa fa-search"></i> Search</button>
                                        </span>
                                    </div>
                                </div>                            
                            </div>
                        </form>
                        <table class="table table-responsive-sm table-bordered table-striped table-sm">
                            <thead>
                                <tr>
                                    <th>No</th>
                                    <th>Height</th>
                                    <th>Nonce</th>
                                    <th>Version</th>
                                    <th>Time</th>
                                    <th>Hash</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- ************************ -->
                                <c:forEach var="block" items="${blocks}" varStatus="loopCounter">
                                <tr>
                                    <td>${loopCounter.count}</td>
                                    <td><a href="<c:url value = "/blocks/${block.hash }?provider=${provider }"/>">
                                        ${block.height }</a>
                                    </td>
                                    <td>${block.nonce}</td>
                                    <td>${block.version}</td>
                                    <td>${block.getTimeAsString()}</td>
                                    <td><a href="<c:url value = "/blocks/${block.hash }?provider=${provider }"/>">
                                        ${block.hash }</a>
                                    </td>
                                </tr>
                                </c:forEach>
                                <!-- ************************ -->
                            </tbody>
                        </table>
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