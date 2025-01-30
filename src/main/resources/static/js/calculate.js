function calculateTotalIncExp() {
	let amount = Number(document.getElementById("amount").value);
	let tax = Number(document.getElementById("tax").value);

	document.getElementById("totalAmount").value = amount + (amount * tax / 100);
}

function calculateNetAmount(){
	let amount = Number(document.getElementById("amount").value);
	let discount = Number(document.getElementById("discount").value);
	document.getElementById("netAmount").value = amount + discount;
}

function calculateSubscriptionAmount(){
	var price = 0;

	$("#channelPackage option:selected").each(function () {
		var $this = $(this);
		if ($this.length) {
			var selText = $this.text();
			var channelPackagePrice = selText.split("₹")[1];
			price = price + Number(channelPackagePrice);
		}
	});

	$("#bouquets option:selected").each(function () {
		var $this = $(this);
		if ($this.length) {
			var selText = $this.text();
			var bouquetPrice = selText.split("₹")[1];
			price = price + Number(bouquetPrice);
		}
	});

	$("#channels option:selected").each(function () {
		var $this = $(this);
		if ($this.length) {
			var selText = $this.text();
			var channelPrice = selText.split("₹")[1];
			price = price + Number(channelPrice);
		}
	});

	$('#subscriptionAmount').val(price);

}

function calculateSubscriptionBillAmount(){
	var amount = 0;

	$("#bills option:selected").each(function () {
		var $this = $(this);
		if ($this.length) {
			var selText = $this.text();
			var billAmount = selText.split("₹")[1];
			amount = amount + Number(billAmount);
		}
	});

	$('#amount').val(amount);
	calculateNetAmount();

}

function calculateOtherDueAmount(){
	var amount = 0;

	$("#dues option:selected").each(function () {
		var $this = $(this);
		if ($this.length) {
			var selText = $this.text();
			var dueAmount = selText.split("₹")[1];
			amount = amount + Number(dueAmount);
		}
	});
	
	$('#amount').val(amount);
	calculateNetAmount();

}

function collectionPaidAmountChange(){
	var paidAmount = Number($('#amount').val());
	var discount = Number($('#discount').val());
	var netAmount = paidAmount + discount;
	$('#netAmount').val(netAmount);
	
	$("#dueTable tr").each(function(index){
		if(index === 0) return;
		var currentRow=$(this);
		var dueAmount=currentRow.find("td:eq(4) input[name$=collectedAmount]").val();
		netAmount -= dueAmount;
	});
	if(netAmount < 0){
		$('#advanceAmount').val(0.0);
		$("#dueTable tr").each(function(index){
			if(index === 0) return;
			var currentRow=$(this);
			var dueAmount = Number(currentRow.find("td:eq(4) input[name$=collectedAmount]").val());
			
			if(dueAmount + netAmount > 0){
				currentRow.find("td:eq(4) input[name$=collectedAmount]").val(dueAmount + netAmount);
				return false;
			} else{
				currentRow.find("td:eq(4) input[name$=collectedAmount]").val(0.0);
				netAmount += dueAmount;
			}
		});
	} else{
		advanceAmount = netAmount;
		var tbody = $('#dueTable tbody');
		tbody.html($('tr',tbody).get().reverse());
		$("#dueTable tr").each(function(index){
			if(index === 0) return;
			var currentRow=$(this);
			var dueAmount = Number(currentRow.find("td:eq(4) input[name$=collectedAmount]").val());
			var maxAttr = Number(currentRow.find("td:eq(4) input[name$=collectedAmount]").attr('max'));
			var gap = maxAttr - dueAmount;
			if(gap > 0){
				if(gap < advanceAmount) {
					currentRow.find("td:eq(4) input[name$=collectedAmount]").val(dueAmount + gap);
					advanceAmount -= gap;
				} else{
					currentRow.find("td:eq(4) input[name$=collectedAmount]").val(dueAmount + advanceAmount);
					advanceAmount = 0.0;
					return false;
				}
			}
		});
		tbody = $('#dueTable tbody');
		tbody.html($('tr',tbody).get().reverse());
		$('#advanceAmount').val(advanceAmount);
	}
	
	adjustUpdatedPaidAmount();
	
}


function adjustUpdatedPaidAmount(){
	var toBePaid = 0;
	$("#dueTable tr").each(function(index){
		if(index === 0) return;
		var currentRow=$(this);
		var collectedAmount= Number(currentRow.find("td:eq(4) input[name$=collectedAmount]").val());
		var paidAmount=Number(currentRow.find("td:eq(3)").text());
		currentRow.find("td:eq(4) input[name$=paidAmount]").val(paidAmount + collectedAmount);
		currentRow.find("td:eq(5)").text(paidAmount + collectedAmount);
		toBePaid += collectedAmount;
	});
	var discount = Number($('#discount').val());
	$('#netAmount').val(toBePaid);
	$('#amount').val(toBePaid - discount);
	
}

function collectionCollectedAmountChange(){
	adjustUpdatedPaidAmount();
}

function calculateGstPrice(){
	let basePrice = Number(document.getElementById("basePrice").value);
	let gstNonGst = document.getElementById("gstNonGst").value;
	if(gstNonGst == 'GST') {
		document.getElementById("price").value = (basePrice*1.18).toFixed(2);
	} else {
		document.getElementById("price").value = basePrice;
	}
}

function updateExpiryDate(days){
	var startDate = document.querySelector("#dateOfConnStart").value;
	var expiryDateElement = document.querySelector("#dateOfConnExpiry");

	if (!isNaN(days) && startDate.length) {
		startDate = startDate.split("-");
		startDate = new Date(startDate[0], startDate[1] - 1, startDate[2]);
		startDate.setDate(startDate.getDate() + days);
		expiryDateElement.valueAsDate = null;
		expiryDateElement.valueAsDate = startDate;
	}
}

function test() {
	setTimeout(
			function() {
				var nextDayDate = $('#dateOfConnStart').datepicker('getDate', '+1d');
				alert($('#dateOfConnStart').datepicker('getDate'));
				nextDayDate.setDate(nextDayDate.getDate() + 1);
				$('#dateOfConnExpiry').datepicker('setDate', nextDayDate);
			}, 1000);
}

