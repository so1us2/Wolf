var endTime;

$(function(){
	setInterval(updateTime, 1000);
});


function setTimer(end){
	endTime = end;
	if(end == -1){
		$("#clock").addClass("hidden");
	} else{
		$("#clock").removeClass("hidden");
	}
	updateTime();
}

function updateTime(){
	var now = new Date().getTime();
	var s = Math.floor((endTime - now) / 1000);
	
	var minutes = Math.floor(s / 60);
	var seconds = s % 60;
	
	if(seconds < 10){
		seconds = "0"+seconds;
	}
	
	$("#clock-time").text(minutes + ":" + seconds);
}