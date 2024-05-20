(function () {
	"use strict";

	var treeviewMenu = $('.app-menu');

	// Toggle Sidebar
	$('[data-toggle="sidebar"]').click(function(event) {
		event.preventDefault();
		$('.app').toggleClass('sidenav-toggled');
	});

	// Activate sidebar treeview toggle
	$("[data-toggle='treeview']").click(function(event) {
		event.preventDefault();
		if(!$(this).parent().hasClass('is-expanded')) {
			treeviewMenu.find("[data-toggle='treeview']").parent().removeClass('is-expanded');
		}
		$(this).parent().toggleClass('is-expanded');
	});

	// Set initial active toggle
	$("[data-toggle='treeview.'].is-expanded").parent().toggleClass('is-expanded');

})();

function printData() {
	var printWindow = window.open("Print");
	printWindow.document.write('<head>')
	printWindow.document.write('<meta name="description" content="">');
	printWindow.document.write('<title>Print Page</title>');
	printWindow.document.write('<meta charset="utf-8">');
	printWindow.document.write('<meta http-equiv="X-UA-Compatible" content="IE=edge">');
	printWindow.document.write('<meta name="viewport" content="width=device-width, initial-scale=1">');
	printWindow.document.write('<link rel="shortcut icon" type="image/icon" href="images/favicon.png" />');
	printWindow.document.write('<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">')
	printWindow.document.write('<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">')
	printWindow.document.write('<link rel="stylesheet" type="text/css" href="css/print_style.css">')
	printWindow.document.write('</head>');
	printWindow.document.write('<body onload="window.print()">');

	var divContents = document.getElementById("printTable").outerHTML;

	printWindow.document.write(divContents);
	printWindow.document.write('</body>');
	printWindow.document.write('</html>');
	printWindow.document.close();
}

function checkIfNumberExistForOthers(userType) {
	let phone = document.getElementById("phone").value;
	let userId = document.getElementById("userId").value;

	var retVal = true;
	var url;

	if(userType == 'ADMIN') {
		url = '/checkIfNumberExistsForOtherAdmins';
	} else if(userType == 'MODERATOR') {
		url = '/checkIfNumberExistsForOtherModerators';
	}

	$.ajax({
		url : url,
		data : { "phone" : phone, "id" : userId },
		async: false,
		success : function(result) {
			if(result == "Exist"){
				alert("Number is already registered with another user!");
				retVal = false;
			}
		}
	});

	return retVal;
}


function searchTableColumn() {

	var searchColumnIndex = document.getElementById("searchBy").value;
	var value = document.getElementById("searchKeyword").value;

	$("table tr").each(function(index) {
		if (index !== 0) {

			$row = $(this);
			var id = $row.find("td").eq(searchColumnIndex).text();

			if (id.indexOf(value) !== 0) {
				$row.hide();
			}
			else {
				$row.show();
			}
		}
	});
}


//Pagination
$('.pageElement, .page-link-next, .page-link-prev, .pageSizeChange, .goTo').on("click change keyup", function (event) {

	if(this.className == 'pageSizeChange' && event.type == 'click') return;
	if(this.className == 'goTo' && !(event.key === 'Enter' || event.keyCode === 13)) return;

	var pageSize = $("#pageSize").val(),
			pageNumber = $(".pageElement.active").first().text(),
			keyword = $("#keyWord").val(),
			fromDate = $("#fromDate").val(),
			toDate = $("#toDate").val(),
			totalPages = $(".pageElement").length,
			url;

	console.log("page-" + pageNumber + ' size-' + pageSize);

	if(this.className == 'goTo') {
		pageNumber = $("#goToPageNo").val();
		if(pageNumber > totalPages) {
			alert("Entered page number is greater than total number of page: " + totalPages);
			return;
		}

	} else if(this.className == 'pageElement') {
		pageNumber = this.text;

	} else if(this.className == 'page-link-next') {
		if(pageNumber >= totalPages) pageNumber = 1;
		else pageNumber = Number(pageNumber) + 1;

	} else if(this.className == 'page-link-prev') {
		if(pageNumber == 1) pageNumber = totalPages;
		else pageNumber = Number(pageNumber) - 1;

	}

	if(keyword != '' || fromDate != '' || toDate != '') {
		url = "?fromDate=" + fromDate + "&toDate=" + toDate + "&keyword=" + keyword + "&size=" + pageSize + "&page=" + pageNumber;
	} else {
		url = "?size=" + pageSize + "&page=" + pageNumber;
	}

	window.open(url,"_self");
});

function confirmDelete() {
	return confirm('Sure you want to delete this record?');
}

