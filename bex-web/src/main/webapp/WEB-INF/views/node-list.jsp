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
<title>Nodes</title>
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
            <li class="breadcrumb-item">Nodes</li>
            <li class="breadcrumb-item active">Node-List</li>
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
                        <i class="fa fa-align-justify"></i> Node list</div>
                    <div class="card-body">
                        <table class="table table-responsive-sm table-bordered table-striped table-sm">
                            <thead>
                                <tr>
                                    <th>No</th>
                                    <th>Node name</th>
                                    <th>Host</th>
                                    <th>Network-Type</th>
                                    <th>Full mode</th>
                                    <th>State</th>
                                    <th>Status</th>
                                    <th>Status-Message</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- ************************ -->
                                <c:forEach var="node" items="${nodes}" varStatus="loopCounter">
                                <tr>
                                    <td>${loopCounter.count}</td>
                                    <td><a href="<c:url value = "/node-details?node_id=${node.id }"/>">
                                        ${node.name }</a>
                                    </td>
                                    <td>${node.network.host}</td>
                                    <td>${node.network.type}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${node.fullVerificationMode }">
                                                <span class="badge badge-success">True</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning">False</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${node.state == NodeState.UNDEFINED }">
                                                <span class="badge badge-warning">Undefined</span>
                                            </c:when>
                                            <c:when test="${node.state == NodeState.FAILED }">
                                                <span class="badge badge-error">Failed</span>
                                            </c:when>
                                            <c:when test="${node.state == NodeState.INACTIVE }">
                                                <span class="badge badge-warning">INACTIVE</span>
                                            </c:when>
                                            <c:when test="${node.state == NodeState.ACTIVE_NOTSYNC }">
                                                <span class="badge badge-primary">Active-NotSynced</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-success">Active-Synced</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${node.status.operationType == OperationType.HALT }">
                                                <span class="badge badge-error">Undefined</span>
                                            </c:when>
                                            <c:when test="${node.status.operationType == OperationType.INIT }">
                                                <span class="badge badge-warning">Init</span>
                                            </c:when>
                                            <c:when test="${node.status.operationType == OperationType.SYNC_CORE }">
                                                <span class="badge badge-primary">Sync</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-success">IDLE</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${node.status.message}</td>
                                </tr>
                                </c:forEach>
                                <!-- ************************ -->
                            </tbody>
                        </table>                    
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