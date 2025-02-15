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
	printWindow.document.write('<title>ROL CABLE NETWORK</title>');
	printWindow.document.write('<meta charset="utf-8">');
	printWindow.document.write('<meta http-equiv="X-UA-Compatible" content="IE=edge">');
	printWindow.document.write('<meta name="viewport" content="width=device-width, initial-scale=1">');
	printWindow.document.write('<link rel="shortcut icon" type="image/icon" href="/images/favicon.png" />');
	printWindow.document.write('<link rel="stylesheet" href="/css/bootstrap.min.css">')
//	printWindow.document.write('<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">')
	printWindow.document.write('<link rel="stylesheet" type="text/css" href="/css/print_style.css">')
	printWindow.document.write('</head>');
	printWindow.document.write('<body onload="window.print()">');

	var divContents = document.getElementById("printTable").outerHTML;

	printWindow.document.write(divContents);
	printWindow.document.write('</body>');
	printWindow.document.write('</html>');
	printWindow.document.close();
}

function exportTableToExcel() {
    let table = document.getElementById("printTable"); // Get table
    let wb = XLSX.utils.book_new(); // Create a new Excel workbook
    let ws = XLSX.utils.aoa_to_sheet([]); // Create an empty worksheet

    let rows = table.querySelectorAll("tr"); // Get all table rows
    let data = [];

    // Loop through table rows
    rows.forEach((row, rowIndex) => {
        let rowData = [];
        let cells = row.querySelectorAll("th, td"); // Select both headers and cells

        cells.forEach((cell) => {
            // Ignore elements with class 'noPrint'
            if (!cell.classList.contains("noPrint")) {
                rowData.push(cell.innerText.trim()); // Add text content to array
            }
        });

        if (rowData.length > 0) {
            data.push(rowData); // Add filtered row data to main array
        }
    });

    // Convert the filtered data to a worksheet
    ws = XLSX.utils.aoa_to_sheet(data);
    XLSX.utils.book_append_sheet(wb, ws, "Data"); // Append sheet to workbook

    // Save file as Excel
    XLSX.writeFile(wb, "Excel_Export.xlsx");
}


function checkIfNumberExistForOthers() {
	let phone = document.getElementById("phone").value;
	let userId = document.getElementById("userId").value;

	var retVal = true;
	var url = '/checkIfNumberExistsForOtherAppUsers';
	if(userId == '') userId = 0;

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
$('.pageElement, .page-link-next, .page-link-prev, .pageSizeChange, .goTo')
    .on("click change keyup", function (event) {

    if ($(this).hasClass('pageSizeChange') && event.type === 'click') return;
    if ($(this).hasClass('goTo') && !(event.key === 'Enter' || event.keyCode === 13)) return;

    let url = new URL(window.location.href); // Get current URL
    let params = new URLSearchParams(url.search); // Get query parameters

    let pageSize = $("#pageSize").val(),
        pageNumber = $(".pageElement.active").first().text(),
        totalPages = $(".pageElement").length;

    // Handle special cases for Go To and Pagination
    if ($(this).hasClass('goTo')) {
        pageNumber = $("#goToPageNo").val();
        if (pageNumber > totalPages) {
            alert("Entered page number is greater than total pages: " + totalPages);
            return;
        }
    } else if ($(this).hasClass('pageElement')) {
        pageNumber = $(this).text();
    } else if ($(this).hasClass('page-link-next')) {
        pageNumber = (pageNumber >= totalPages) ? 1 : Number(pageNumber) + 1;
    } else if ($(this).hasClass('page-link-prev')) {
        pageNumber = (pageNumber == 1) ? totalPages : Number(pageNumber) - 1;
    }

    // Update size and page parameters
    params.set("size", pageSize);
    params.set("page", pageNumber);

    // Update any other filtering parameters dynamically
    $(".filter-input").each(function () {
        let key = $(this).attr("name");
        let value = $(this).val();
        if (value) params.set(key, value);
        else params.delete(key); // Remove if empty
    });

    // Redirect to the updated URL
    url.search = params.toString();
    window.open(url.toString(), "_self");
});


function confirmDelete() {
	return confirm('Sure you want to delete this record?');
}

function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
        }
    }
    return false;
};

function openRenew() {
	const renewConnection = document.getElementById("renewConnection");
    renewConnection.classList.remove("displayNone");
}


function performRenewal() {
	const dateRangeSelection = document.getElementById("dateRange");
	var connId = $("input[type='radio'][name='id']:checked").val();
	
	if(dateRangeSelection.value == "Today"){
		if(confirm('Sure you want to renew this connection for Today?')){
			window.open(btoa('/connection/renew') + '?id=' + connId + '&date=' + new Date().toISOString().split('T')[0],"_self");
			alert("Sent Request for " + new Date().toISOString().split('T')[0]);
		}
	} else{
		const specificDayRenew = document.getElementById("js_datePickerToggle");
		if(specificDayRenew.value == null || specificDayRenew.value == ''){
			alert("Please select a date!");
			return false;
		}
		if(confirm('Sure you want to renew this connection for ' + specificDayRenew.value + '?')){
			window.open(btoa('/connection/renew') + '?id=' + connId + '&date=' + specificDayRenew.value,"_self");
			alert("Sent Request for " + specificDayRenew.value);
		}
	}
}

function setStateChangeValue(){
	document.getElementById("specificDayRenew").value = document.getElementById("js_datePickerToggle").value;
}

function toDateInputValue(dateObject){
	const local = new Date(dateObject);
	local.setMinutes(dateObject.getMinutes() - dateObject.getTimezoneOffset());
	return local.toJSON().slice(0,10);
}
$(document).ready( function() {
	document.getElementById("defaultDate").valueAsDate = new Date();
});


/*
function encodeUrlParams(url) {
    let urlObj = new URL(url, window.location.origin);
    urlObj.searchParams.forEach((value, key) => {
        urlObj.searchParams.set(key, encodeURIComponent(value));
    });
    return urlObj.toString();
}

document.addEventListener("DOMContentLoaded", function () {
    function encodeBase64(url) {
        return btoa(unescape(encodeURIComponent(url)));
    }

    // Encode <a> tag href attributes
    document.querySelectorAll(".app-menu a[href]").forEach(function (link) {
        let originalUrl = link.getAttribute("href");
        if (originalUrl && !originalUrl.startsWith("data:text/plain;base64,")) {
            let [base, query] = originalUrl.split("?", 2); // Split URL into base and query
            let encodedBase = encodeBase64(base); // Encode only base
            let newUrl = query ? encodedBase + "?" + query : encodedBase; // Reconstruct URL
            link.setAttribute("href", newUrl);
        }
    });

    // Encode <form> action attributes
    document.querySelectorAll("form[action]").forEach(function (form) {
        let originalUrl = form.getAttribute("action");
        if (originalUrl) {
            let [base, query] = originalUrl.split("?", 2);
            let encodedBase = encodeBase64(base);
            let newUrl = query ? encodedBase + "?" + query : encodedBase;
            form.setAttribute("action", newUrl);
        }
    });

    // Encode <input> formaction attributes
    document.querySelectorAll("input[formaction]").forEach(function (input) {
        let originalUrl = input.getAttribute("formaction");
        if (originalUrl) {
            let [base, query] = originalUrl.split("?", 2);
            let encodedBase = encodeBase64(base);
            let newUrl = query ? encodedBase + "?" + query : encodedBase;
            input.setAttribute("formaction", newUrl);
        }
    });

    // Encode location.href in onclick attributes
    document.querySelectorAll("input[onclick]").forEach(function (input) {
        let onclickValue = input.getAttribute("onclick");
        let match = onclickValue.match(/location\.href\s*=\s*['"]([^'"]+)['"]/);
        if (match) {
            let originalUrl = match[1];
            let [base, query] = originalUrl.split("?", 2);
            let encodedBase = encodeBase64(base);
            let newUrl = query ? encodedBase + "?" + query : encodedBase;
            input.setAttribute("onclick", onclickValue.replace(originalUrl, newUrl));
        }
    });
});

*/
