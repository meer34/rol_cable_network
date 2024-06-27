function calculateTotalIncExp() {
	let amount = Number(document.getElementById("amount").value);
	let tax = Number(document.getElementById("tax").value);

	document.getElementById("totalAmount").value = amount + (amount * tax / 100);
}

function calculateNetAmount(){
	let amount = Number(document.getElementById("amount").value);
	let discount = Number(document.getElementById("discount").value);
	document.getElementById("netAmount").value = amount - discount;
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

	$("#buckets option:selected").each(function () {
		var $this = $(this);
		if ($this.length) {
			var selText = $this.text();
			var bucketPrice = selText.split("₹")[1];
			price = price + Number(bucketPrice);
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

function calculateGstPrice(){
	let basePrice = Number(document.getElementById("basePrice").value);
	let gstNonGst = document.getElementById("gstNonGst").value;
	if(gstNonGst == 'GST') {
		document.getElementById("price").value = (basePrice*1.18).toFixed(2);
	} else {
		document.getElementById("price").value = basePrice;
	}
}

function updateExpiryDate(){
	var startDate = document.querySelector("#dateOfConnStart").value;
	var days = 30;
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

