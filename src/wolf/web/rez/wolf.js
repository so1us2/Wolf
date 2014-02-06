var ws;
var loggedIn = false;

$("#the-input").keypress(function(e){
	if(e.which==13){ //enter
		var target = $(e.target);
		var msg = target.val().trim();
		target.val("");
		if(msg) {
			if(loggedIn){
				send("CHAT "+msg);
			} else{
				login(msg);
			}
		}
	}
});

if ("WebSocket" in window){
	// Let us open a web socket
	ws = new WebSocket("ws://localhost:80/socket");
	ws.onopen = function() {
		appendToChat("Connected to chat server.");
		var username = $.cookie("username");
		if(username){
			login(username);
		}
	};
	ws.onmessage = function (evt) { 
		receive(evt.data);
	};
	ws.onclose = function() { 
		appendToChat("Disconnected from chat server.");
		ws = null;
		$(".input-label").html("Enter your username:");
	};
}
else {
	alert("Please upgrade your browser to one that supports WebSockets.");
}

function login(username){
	send("LOGIN "+username);
	$(".input-label").html("Chat");
	appendToChat("Logged in as '"+username+"'");
	loggedIn=true;
	$.cookie("username",username,{expires: 7});
}

function send(msg){
	ws.send(msg);
}

function receive(msg){
	var i = msg.indexOf(" ");
	var command = msg.substring(0,i);
	var rest = msg.substring(i+1);
	if(command=="CHAT"){
		appendToChat(rest);
	} else if(command=="CONNECTIONS"){
		$("#viewers-count").text(rest);
	}
}

function appendToChat(msg){
	var div = $("<div>").text(msg);
	$(".chat-text").append(div);
}