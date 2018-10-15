<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" 
         import="io.hs.bex.blockchain.model.tx.TransactionIOType" %>
                  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
<base href="./">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
<title>Transaction details</title>
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
            <li class="breadcrumb-item">Transactions</li>
            <li class="breadcrumb-item active">Transactions-Details</li>
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
                            <div class="card-body">
                                <div class="row">
                                <table class="table table-responsive-sm table-bordered table-striped table-sm">
                                    <c:set var="tx" value="${transaction}"/>
                                    <tbody>
                                        <tr>
                                            <th>Transactions Hash:</th>
                                            <td>${tx.hash }</td>
                                        </tr>
                                        <tr>
                                            <th>Version:</th>
                                            <td>${tx.version }</td>
                                        </tr>
                                        <tr>
                                            <th>Message Size:</th>
                                            <td>${tx.messageSize }</td>
                                        </tr>
                                        <tr>
                                            <th>Input Sum:</th>
                                            <td>${tx.getInputSum().valueAsString() }</td>
                                        </tr>
                                        <tr>
                                            <th>Output Sum:</th>
                                            <td>${tx.getOutputSum().valueAsString() }</td>
                                        </tr>
                                        <tr>
                                            <th>Fee Sum:</th>
                                            <td>${tx.getFeeSum().valueAsString() }</td>
                                        </tr>
                                    </tbody>
                                </table>
                                </div>
                                <hr>
                                <div class="row">
                                <table id="tbTransaction" class="table table-responsive-sm table-hover table-outline mb-0">
                                  <thead class="thead-light">
                                    <tr>
                                      <th>Index</th>
                                      <th>Address-From</th>
                                      <th>Address-To</th>
                                      <th>Amount</th>
                                      <th>Coinbase</th>
                                      <th>Type</th>
                                    </tr>
                                  </thead>
                                  <tbody>
                                  <!-- ************************ -->
                                  <c:forEach var="txInput" items="${tx.txInputs}" varStatus="loopCounter">
                                    <tr>
                                      <td>${txInput.index }</td>
                                      <td>${txInput.fromAddress.value }</td>
                                      <td>${txInput.fromAddress.value }</td>
                                      <td>${txInput.amount.valueAsString() }</td>
                                      <td>
                                          <c:choose>
                                            <c:when test="${txInput.coinBase }">
                                                <span class="badge badge-success">True</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning">False</span>
                                            </c:otherwise>
                                            </c:choose>
                                      </td>
                                      <td>
                                          <c:choose>
                                            <c:when test="${txInput.type == TransactionIOType.INPUT}">
                                                <i class="fa fa-arrow-left"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fa fa-arrow-right"></i>
                                            </c:otherwise>
                                            </c:choose>
                                      </td>
                                    </tr>
                                    </c:forEach>
                                    <c:forEach var="txOutput" items="${tx.txOutputs}" varStatus="loopCounter">
                                    <tr>
                                      <td>${txOutput.index }</td>
                                      <td><a href="<c:url value = "/address/${txOutput.toAddress.value}?provider=${provider }"/>">
                                            ${txOutput.toAddress.value }</a></td>
                                      <td>${txOutput.toAddress.value }</td>
                                      <td>${txOutput.amount.valueAsString() }</td>
                                      <td>
                                        <span class="badge badge-warning">False</span>
                                      </td>
                                      <td>
                                          <c:choose>
                                            <c:when test="${txOutput.type == TransactionIOType.INPUT }">
                                                <i class="fa fa-arrow-left"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fa fa-arrow-right"></i>
                                            </c:otherwise>
                                            </c:choose>
                                      </td>
                                    </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                                </div>
                            </div>
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
    
    $(document).ready(function() 
    {
        $('#tbTransaction').DataTable( {
            fixedHeader: true
        } );
    } );
    
    </script>
    </body>
</html>