var ws;

$("#text-input").keypress(function(e){
	if(e.which==13){ //enter
		var target = $(e.target);
		var msg = target.val().trim();
		target.val("");
		if(msg) {
		   send("CHAT", msg);
		}
	}
});

//open websocket
if (WebSocket){
	ws = new WebSocket("ws://playwolf.net:80/socket");
	ws.onopen = function() {
		announce("Connected to chat server.");
		var userID = $.cookie("userID");
		if(userID){
			loginWithUserID(userID);
		}
	};
	ws.onmessage = function (evt) { 
		receive(evt.data);
	};
	ws.onclose = function() { 
		announce("Disconnected from chat server.");
		ws = null;
		$(".input-label").html("Enter your username:");
	};
} else {
	alert("Please upgrade your browser to one that supports WebSockets.");
}

function loginComplete(data){
	console.log(data);
	loginWithUserID(data.authResponse.userID);
}

function loginWithUserID(userID){
	$.cookie("userID", userID,{expires: 7});
	send("LOGIN", userID);
	
    $(".fb-login-button").addClass("hidden");
    $(".login-advertise").addClass("hidden");
    
    $("#text-input").removeClass("hidden");
}

function loginSuccess(username){
	announce("Logged in as '"+username+"'");
}

function send(command, arg){
	var jsonData = {
		command:command,
		args:[arg]
	};
	ws.send(JSON.stringify(jsonData));
}

function receive(msg){
	msg = JSON.parse(msg);
	console.log(msg);
	
	var command = msg.command;
	
	if(command=="CHAT"){
		append(msg.from, msg.msg, false);
	}
	else if(command == "S_CHAT"){
		append(msg.from, msg.msg, true);
	}
	else if(command=="LOGIN_SUCCESS"){
		loginSuccess(msg.username);
	} 
	else if(command == "PROMPT_NAME"){
	   var username = window.prompt("Enter your username:");
	   send("USERNAME", username);
	}
	else if(command=="LOGIN_FAILED"){
		announce("LOGIN FAILED: "+msg.reason);
	}
	else if(command=="PLAYERS"){
		$(".list-group-item").remove();
		
		$("#viewers-count").text(msg.num_viewers);
		for(var i=0; i<msg.alive.length; i++){
			var player = msg.alive[i];
			$("#list-players").append($("<li class='list-group-item alive'>").text(player));
		}
		for(var i=0; i<msg.dead.length; i++){
			var player = msg.dead[i];
			$("#list-players").append($("<li class='list-group-item dead'>").text(player));
		}
		for(var i=0; i<msg.watchers.length; i++){
			var player = msg.watchers[i];
			$("#list-watching").append($("<li class='list-group-item'>").text(player));
		}
	}
}

function append(from, msg, isSpectator){
	var div = $("<div class='row'>");
	var fromDiv = $("<span class='msg-author'>").text(from+":").addClass("sender");
	var msgDiv =  $("<span class='msg-text'>").text(msg).addClass("message");
	
	if(from=="$narrator"){
		msgDiv.addClass("private");
		div.append(msgDiv);
	} else{
		if(isSpectator){
			fromDiv.addClass("spectator");
			msgDiv.addClass("spectator");
		}
		div.append(fromDiv).append(msgDiv);
	}
	
	$("#chat-text").append(div);
	scrollToBottom();
}

function announce(msg){
	var div = $("<div class='row'>").text(msg);
	div.addClass("private");
	$("#chat-text").append(div);
	scrollToBottom();
}

function scrollToBottom(){
	var win = $(window);
	if(win.scrollTop() + win.height() >= $(document).height()-100){
		window.scrollTo(0, document.body.scrollHeight);
	}
}

$("#rankings-button").click(function(e){
	var container = $("#rankings-modal tbody");
	
	container.empty();
	container.text("Loading...");
	
	$.getJSON("/rankings", function(data){
		container.empty();
		for(var i = 0; i < data.length; i++){
			var player = data[i];
			console.log(player);
			var tr = $("<tr>");
			tr.append($("<td>").text((i+1)+""));
			tr.append($("<td>").text(player.name));
			tr.append($("<td>").text(player.wins));
			tr.append($("<td>").text(player.losses));
			tr.append($("<td>").text(player.win_percentage));
			container.append(tr);
		}
	});
});
