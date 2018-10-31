<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
<base href="./">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
<title>Main</title>
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
            <li class="breadcrumb-item">Home</li>
            <li class="breadcrumb-item active">Dashboard</li>
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
                    <div class="card-body">
                        <div class="row">
                            <div class="col-sm-5">
                                <jsp:useBean id="now" class="java.util.Date" />
                                <fmt:formatDate var="year" value="${now}" pattern="MM-yyyy" />
                                <h4 class="card-title mb-0">E-Currency Rates</h4>
                                <div class="small text-muted">${year}</div>
                            </div>
                            <!-- /.col-->
                            <div class="col-sm-7 d-none d-md-block">
                                <div class="btn-group btn-group-toggle float-right mr-3" data-toggle="buttons">
                                    <label class="btn btn-outline-secondary"> <input id="option1" type="radio"
                                        name="options" autocomplete="off"> Day
                                    </label> <label class="btn btn-outline-secondary active"> <input id="option2"
                                        type="radio" name="options" autocomplete="off" checked=""> Month
                                    </label> <label class="btn btn-outline-secondary"> <input id="option3" type="radio"
                                        name="options" autocomplete="off"> Year
                                    </label>
                                </div>
                            </div>
                            <!-- /.col-->
                        </div>
                        <!-- /.row-->
                        <div class="chart-wrapper" style="height: 300px; margin-top: 40px;">
                            <canvas class="chart" id="main-chart" height="300"></canvas>
                        </div>
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
    <script src="<c:url value = "/assets/lib/chart.js/js/Chart.min.js"/>"></script>
    <script src="<c:url value = "/assets/lib/@coreui/coreui-chartjs-tooltips/js/custom-tooltips.min.js"/>"></script>
    <script src="<c:url value = "/assets/js/main.js"/>"></script>
    <script type="text/javascript">
    
    /*
    function drawLineChart() 
    {
    	  function formatDate( dateInp ) 
    	  {
    		  var day = dateInp.dayOfMonth;
    		  var month = dateInp.monthValue - 1; // Month is 0-indexed
    		  var year = dateInp.year;

    		  return (month + 1) + "/" +  day+ "/" +  year;
    	  }

    	  var jsonData = $.ajax(
    	  {
    	      url: 'api/v1/currency/exchangerates?period=m',
    	      dataType: 'json',
    	      
    	  }).done(function (results) 
    	  {
    	    var labels = [], data=[];
    	    results.forEach(function(result) 
    	    {
    	      labels.push(formatDate(result.date));
    	      data.push(parseFloat(result.rate));
    	    });

    	    // Create the chart.js data structure using 'labels' and 'data'
    	    var tempData = {
    	      labels : labels,
    	      datasets : [{
    	    	  label: 'BTC',
    	    	  backgroundColor: hexToRgba(getStyle('--info'), 10),
    	          borderColor: getStyle('--info'),    	          
    	          data       : data
    	      }]
    	    };

    	    // Get the context of the canvas element we want to select
    	    var ctx = document.getElementById("main-chart").getContext("2d");

    	    var myLineChart = new Chart(ctx , {
                type: "line",
                data: tempData, 
            });
    	  });
    	}

    	drawLineChart();
    */
    
    </script>
</body>
</html>