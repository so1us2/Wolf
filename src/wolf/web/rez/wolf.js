var testing = false;

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
	var domain = testing ? "localhost" : "playwolf.net";
	ws = new WebSocket("ws://"+domain+":80/socket");
	ws.onopen = function() {
		announce("Connected to chat server.");
		var userID = $.cookie("userID");
		if(testing){
			userID = 1;
		}
		if(userID){
			loginWithUserID(userID);
		} else{
			$(".login-advertise").clone().appendTo($("#chat-text"));
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
	if(!testing){
		$.cookie("userID", userID,{expires: 7});
	}
	send("LOGIN", userID);
	
    $(".login-advertise").addClass("hidden");
    
    $("#text-input").removeClass("hidden");
}

function loginSuccess(msg){
	announce("Logged in as '"+msg.username+"'");
	
	$("#enable-sounds-checkbox").prop("checked", msg.enable_sounds);
	
	$("#settings-button").removeClass("hidden");
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
		loginSuccess(msg);
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
		
		if(msg.num_not_signed_in){
			$("#viewers-count").text(msg.num_not_signed_in + " not signed in");
		} else{
			$("#viewers-count").text("");
		}			
		
		
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
	} else if(command == "MUSIC"){
		playSound(msg.url);
	}
}

function append(from, msg, isSpectator){
	var fromNarrator = from=="$narrator";
	
	var div = $("<div class='row'>");
	var fromDiv = $("<span class='msg-author'>").text(from+":").addClass("sender");
	var msgDiv =  $("<span class='msg-text'>").addClass("message");
	
	if(fromNarrator){
		msgDiv.html(msg);
	} else{
		msgDiv.text(msg);
	}
	
	if(fromNarrator){
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
	
	if(fromNarrator){
		if(msg.indexOf("A new game is forming")==0){
			playSound("new_game.mp3");
		}
		if(msg.indexOf("Assigning roles...")==0){
			playSound("game_started.mp3");
		}
	}
}

function playSound(url){
	var enable_sounds = $("#enable-sounds-checkbox").is(":checked");
	if(enable_sounds){
		new Audio(url).play();
	}
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

rulesLoaded = false;
$("#rules-button").click(function(e){
	if(rulesLoaded){
		return;
	}
	rulesLoaded = true;
	
	$.getJSON("/rules", function(data){
		var list = $("#rules-list");
		
		for(var i = 0; i < data.length; i++){
			var rule = data[i];
			var ahref = $("<a href='#'>").text(rule);
			var li = $("<li data-rule='"+rule+"'>").append(ahref);
			list.append(li);
			li.click(rulesListener);
			if(i==0){
				ahref.click();
			}
		}
	});
});

var lastRule;
function rulesListener(e){
	if(lastRule){
		lastRule.removeClass("active");
	}
	
	var parent = $(e.target).parent();
    parent.addClass("active");
    
    lastRule = parent;
    
    var container = $("#rules-content");
    
    $.get("/rules/"+parent.data("rule"), function(data){
    	container.html(data);
    });
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

$("#enable-sounds-checkbox").click(function(e){
	var checked = $(this).is(":checked");
	send("CHAT","/enable-sounds " + checked);
});
