var ws;
var loggedIn = false;

$("#text-input").keypress(function(e){
	if(e.which==13){ //enter
		var target = $(e.target);
		var msg = target.val().trim();
		target.val("");
		if(msg) {
			if(loggedIn){
				send("CHAT",msg);
			} else{
				login(msg);
			}
		}
	}
});

if (WebSocket){
	// Let us open a web socket
	ws = new WebSocket("ws://localhost:80/socket");
	ws.onopen = function() {
		announce("Connected to chat server.");
		var username = $.cookie("username");
		if(username){
			login(username);
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
}
else {
	alert("Please upgrade your browser to one that supports WebSockets.");
}

function login(username){
	send("LOGIN", username);
	$("#text-input").prop("placeholder", "Chat");
	$("#text-input").animate({"width":500});
	announce("Logged in as '"+username+"'");
	loggedIn=true;
	$.cookie("username",username,{expires: 7});
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
	var args = msg.args;
	
	if(command=="CHAT"){
		append(args[0], args[1], false);
	} else if(command=="PLAYERS"){
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

function append(from, msg, isPrivate){
	var div = $("<div class='row'>");
	var fromDiv = $("<span class='msg-author'>").text("<"+from+">").addClass("sender");
	var msgDiv =  $("<span class='msg-text'>").text(msg).addClass("message");
	
	if(from=="$narrator"){
		msgDiv.addClass("private");
		div.append(msgDiv);
	} else{
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
