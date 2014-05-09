var startTime;

$(function(){
	setInterval(updateTime, 1000);
});

function startTimer(){
	startTime = new Date().getTime();
	
	updateTime();
	
	$("#clock").removeClass("hidden");
}

function stopTimer(){
	$("#clock").addClass("hidden");
}

function updateTime(){
	var now = new Date().getTime();
	var s = Math.floor((now - startTime) / 1000);
	
	var minutes = Math.floor(s / 60);
	var seconds = s % 60;
	
	if(seconds < 10){
		seconds = "0"+seconds;
	}
	
	$("#clock-time").text(minutes + ":" + seconds);
}