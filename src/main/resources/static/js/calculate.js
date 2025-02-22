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

function getTotalCollectedAmount(){
	var amount = 0.0;
	$("#dueTable tr").each(function(index){
		if(index === 0) return;
		var currentRow=$(this);
		var collectedAmount=Number(currentRow.find("td:eq(5) input[name$=collectedAmount]").val());
		amount += collectedAmount;
	});
	return amount;
}

function collectionPaidAmountChange(){
	var payableAmount = Number($('#amount').val());
	var discount = Number($('#discount').val());
	var consumerAdvanceAmount = Number($('#consumerAdvanceAmount').val());
	var totalCollectedAmount = getTotalCollectedAmount();
	
	var have = payableAmount + discount + consumerAdvanceAmount - totalCollectedAmount;
	
	if(have < 0){
		//$('#advanceAmount').val(0.0);
		$("#dueTable tr").each(function(index){
			if(index === 0) return;
			var currentRow=$(this);
			var collectedAmount = Number(currentRow.find("td:eq(5) input[name$=collectedAmount]").val());
			
			if(collectedAmount + have > 0){
				currentRow.find("td:eq(5) input[name$=collectedAmount]").val(collectedAmount + have);
				return false;
			} else{
				currentRow.find("td:eq(5) input[name$=collectedAmount]").val(0.0);
				have += collectedAmount;
			}
		});
		$('#advanceAmount').val(consumerAdvanceAmount);
	} else{
		var tbody = $('#dueTable tbody');
		tbody.html($('tr',tbody).get().reverse());
		
		$("#dueTable tr").each(function(index){
			if(index === 0) return;
			var currentRow=$(this);
			var collectedAmount = Number(currentRow.find("td:eq(5) input[name$=collectedAmount]").val());
			var maxAttr = Number(currentRow.find("td:eq(5) input[name$=collectedAmount]").attr('max'));
			var gap = maxAttr - collectedAmount;
			
			if(gap > 0){
				if(have > gap) {
					currentRow.find("td:eq(5) input[name$=collectedAmount]").val(collectedAmount + gap);
					have -= gap;
				} else{
					currentRow.find("td:eq(5) input[name$=collectedAmount]").val(collectedAmount + have);
					have = 0.0;
					return false;
				}
			}
			
		});
		
		tbody = $('#dueTable tbody');
		tbody.html($('tr',tbody).get().reverse());
		$('#advanceAmount').val(consumerAdvanceAmount - have);
		
	}
	
	$('#netAmount').val(getTotalCollectedAmount());
	
	$("#dueTable tr").each(function(index){
			if(index === 0) return;
			var currentRow=$(this);
			var collectedAmount= Number(currentRow.find("td:eq(5) input[name$=collectedAmount]").val());
			var paidAmount=Number(currentRow.find("td:eq(3)").text());
			currentRow.find("td:eq(5) input[name$=paidAmount]").val(paidAmount + collectedAmount);
			currentRow.find("td:eq(6)").text(paidAmount + collectedAmount);
		});
	
}

function adjustUpdatedPaidAmount(){
	var toBePaid = 0;
	var takeFromAdvance = 0;
	
	$("#dueTable tr").each(function(index){
		if(index === 0) return;
		var currentRow=$(this);
		var collectedAmount= Number(currentRow.find("td:eq(5) input[name$=collectedAmount]").val());
		var paidAmount=Number(currentRow.find("td:eq(3)").text());
		currentRow.find("td:eq(5) input[name$=paidAmount]").val(paidAmount + collectedAmount);
		currentRow.find("td:eq(6)").text(paidAmount + collectedAmount);
		toBePaid += collectedAmount;
	});
	
	$('#netAmount').val(toBePaid);
	
	var discount = Number($('#discount').val());
	var consumerAdvanceAmount = Number($('#consumerAdvanceAmount').val());
	
	toBePaid -= discount;
	if(toBePaid >= consumerAdvanceAmount) {
		takeFromAdvance = consumerAdvanceAmount;
		toBePaid -= takeFromAdvance;
		
	} else {
		takeFromAdvance = toBePaid;
		toBePaid = 0.0;
	}
	
	$('#advanceAmount').val(takeFromAdvance);
	$('#amount').val(toBePaid);
	
}


function collectionCollectedAmountChange(elem){
	var collectedAmount= parseFloat(elem.value);
	var max = parseFloat(elem.max);
	
	if (!isNaN(collectedAmount) && !isNaN(max) && collectedAmount > max) {
		alert("Collection amount cannot be greater than due amount!");
        elem.value = max;
    }
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

function fixAllInputValues(decimals = 2) {
    document.querySelectorAll("input[type='number'], input[data-fix]").forEach(input => {
        let value = parseFloat(input.value);
        if (!isNaN(value)) {
            input.value = (Math.round((value + Number.EPSILON) * Math.pow(10, decimals)) / Math.pow(10, decimals)).toFixed(decimals);
        }
    });
}
