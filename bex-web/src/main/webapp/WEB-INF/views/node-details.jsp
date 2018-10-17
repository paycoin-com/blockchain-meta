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
<title>Node details</title>
<!-- Icons-->
<link href="<c:url value = "/assets/lib/@coreui/icons/css/coreui-icons.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/flag-icon-css/css/flag-icon.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/font-awesome/css/font-awesome.min.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/simple-line-icons/css/simple-line-icons.css"/>" rel="stylesheet">
<!-- Main styles for this application-->
<link href="<c:url value = "/assets/css/style.css"/>" rel="stylesheet">
<link href="<c:url value = "/assets/lib/pace-progress/css/pace.min.css"/>" rel="stylesheet">
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
            <li class="breadcrumb-item active">Node-Details</li>
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
                    <div class="col-lg-6">
                        <div class="card">
                            <div class="card-header">
                                <a id="btnNodeStart" class="btn btn-sm btn-primary 
                                ${(node.state == NodeState.ACTIVE_NOTSYNC || node.state == NodeState.ACTIVE_SYNC)?'disabled':'' }" 
                                    role="button" href="<c:url value = "/node-action?action_type=1&node_id=${node.id }"/>">
                                    <i class="fa fa-play-circle"></i>&nbsp;&nbsp;Start</a>
                                <a id="btnNodeStopt" class="btn btn-sm btn-warning
                                ${(node.state != NodeState.ACTIVE_NOTSYNC && node.state != NodeState.ACTIVE_SYNC)?'disabled':'' }" 
                                   role="button" href="<c:url value = "/node-action?action_type=2&node_id=${node.id }"/>">
                                   <i class="fa fa-ban"></i>&nbsp;&nbsp;Stop</a>
                                <a id="btnNodeStartSync" class="btn btn-sm btn-success" role="button" 
                                    href="<c:url value = "/node-action?action_type=3&node_id=${node.id }"/>">Sync-With-Core</a>
                                <a id="btnNodeStartLocalSync" class="btn btn-sm btn-success" role="button" 
                                    href="<c:url value = "/node-action?action_type=4&node_id=${node.id }"/>">Sync-With-LocalStore</a>
                            </div>
                            <div class="card-body">
                                <table class="table table-responsive-sm table-bordered table-striped table-sm">
                                    <tbody>
                                        <tr>
                                            <th>Node name:</th>
                                            <td>${node.name }</td>
                                        </tr>
                                        <tr>
                                            <th>Host:</th>
                                            <td>${node.network.host }</td>
                                        </tr>
                                        <tr>
                                            <th>Network Type:</th>
                                            <td>${node.network.type }</td>
                                        </tr>
                                        <tr>
                                            <th>Full Mode:</th>
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
                                        </tr>
                                        <tr>
                                            <th>State:</th>
                                            <td>
                                                <div id="dvNodeState">
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
                                                </div>                                            
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Status:</th>
                                            <td>
                                                <div id="dvNodeStatus">
                                                    <c:choose>
                                                        <c:when test="${node.status.operationType == OperationType.HALT }">
                                                            <span class="badge badge-error">Undefined</span>
                                                        </c:when>
                                                        <c:when test="${node.status.operationType == OperationType.INIT }">
                                                            <span class="badge badge-warning">Init</span>
                                                        </c:when>
                                                        <c:when test="${node.status.operationType == OperationType.SYNC_CORE }">
                                                            <span class="badge badge-primary">Sync-Core</span>
                                                        </c:when>
                                                        <c:when test="${node.status.operationType == OperationType.SYNC_LOCAL_STORE }">
                                                            <span class="badge badge-primary">Sync-Local-Store</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-success">IDLE</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Status-Message:</th>
                                            <td>
                                                <div id="dvNodeStatusMessage">${node.status.message }</div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Operation-Progress:</th>
                                            <td>
                                                <div class="clearfix">
                                                    <div class="float-left">
                                                        <strong id="dvOperationPg">0%</strong>
                                                    </div>
                                                    <div class="float-right">
                                                        <small id="dvBlocksLeft" class="text-muted">
                                                            Blocks left:</small>
                                                    </div>
                                                </div>
                                                <div class="progress progress-xs">
                                                    <div id="pbOperation" class="progress-bar bg-success" 
                                                            role="progressbar" style="width: 0%" aria-valuenow="50" 
                                                            aria-valuemin="0" aria-valuemax="100">
                                                   </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
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
    <!-- Plugins and scripts required by this view-->
    <script type="text/javascript">
    
    var operationType = "${node.status.operationType }"; 
    
    $(document).ready(function() 
    {
    	if( operationType == "SYNC_CORE" || operationType == "SYNC_LOCAL_STORE")
    	{
            $('#dvOperationPg').attr("style","width:0%;");   
            $('#dvOperationPg').html("0%");   
            
            checkOperationStatus( ${node.id});
        }
    });
    
    function checkOperationStatus( nodeId ) 
    {
        var pollUrl = '<c:url value = "/node/status/"/>'+nodeId;

        setTimeout(function() 
        {
            $.ajax({
                url: pollUrl,
                type: "GET",
                dataType: "json",
                success: function(data) 
                {
                	$('#pbOperation').attr("style","width:" + data.operationProgress.completePercentage + "%;");   
                    $('#dvOperationPg').html( data.operationProgress.completePercentage.toFixed(1) + " %");   
                    $('#dvBlocksLeft').html( data.operationProgress.blocksLeft);   
                    
                    if( data.operationProgress.completeBlocksCount == 0)
                    {
                        $('#dvNodeStatus').html( "<span class='badge badge-success'>"+ data.operationType +"</span>");
                        $('#dvNodeStatusMessage').html( "Download completed");
                        operationType = data.operationType;
                    }   
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) 
                {
                    if (XMLHttpRequest.readyState == 4) 
                    {
                        // HTTP error (can be checked by XMLHttpRequest.status and XMLHttpRequest.statusText)
                    }
                    else if (XMLHttpRequest.readyState == 0) 
                    {
                        // Network error (i.e. connection refused, access denied due to CORS, etc.)
                        active = 2;
                    }
                    else 
                    {
                        // something weird is happening
                    }
                },
                complete: checkOperationStatus( nodeId ),
                timeout: 8000
            })
        }, 5000);
    }
    </script>
    </body>
</html>