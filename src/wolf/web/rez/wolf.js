var testing = false;

var ws;
var currentRoom = "Main Room";

$("#input-wrapper").on('keydown', '#text-input', function(e){
	if(e.which == 13){ //enter
		var target = $(e.target);
		var msg = target.val().trim();
		target.val("");
		if(msg) {
		   send("CHAT", msg);
		}
	} else if(e.which == 9){ //tab
		e.preventDefault();
		autocomplete($(e.target));
	}
});

//$("#text-input").keypress(function(e){
//	if(e.which == 13){ //enter
//		var target = $(e.target);
//		var msg = target.val().trim();
//		target.val("");
//		if(msg) {
//		   send("CHAT", msg);
//		}
//	} else if(e.which == 9){ //tab
//		autocomplete($e.target);
//	}
//});

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
		
		loadRoomMenu();
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

function loadRoomMenu(){
	$.getJSON("/rooms", function(data){
		var roomMenu = $("#room-menu");
		roomMenu.empty();
		
		for(var i = 0; i < data.length; i++){
			var room = data[i];
			var li = $("<li>").append($("<a href='#' data-room='"+room+"'>").text(room).click(roomListener));
			roomMenu.append(li);
		}
		
		roomMenu.append($("<li class='divider'>"));
		roomMenu.append($("<li>").append($("<a href='#'>").text("New Room").click(newRoom)));
	});
}

function roomListener(e){
	var room = $(e.target).data("room");
	if(room == currentRoom){
		return;
	}
	send("SWITCH_ROOM", room);
	setRoom(room);
}

function newRoom(){
     var roomName = window.prompt("Enter room name:");
     if(!roomName){
    	 return;
     }
     $.post("/rooms/" + roomName).success(function(){
    	 send("SWITCH_ROOM", roomName);
    	 setRoom(roomName);
     });
}

function setRoom(room){
	if(currentRoom == room){
		return;
	}
	console.log("Switching rooms: "+room);
	currentRoom = room;
	 $("#room-name").text(room + " ");
	// $("#chat-text").empty();
	 append("$narrator", "Joined room: "+room);
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
	else if(command=="LOAD_ROOMS"){
		loadRoomMenu();
	}
	else if(command=="LOGIN_SUCCESS"){
		loginSuccess(msg);
	} 
	else if(command == "PROMPT_NAME"){
	   var username = window.prompt("Enter your username:");
	   if(username){
		   send("USERNAME", username);
	   }
	}
	else if(command=="LOGIN_FAILED"){
		announce("LOGIN FAILED: "+msg.reason);
	}
	else if(command=="PLAYERS"){
		$(".list-group-item").remove();
		
		var n = msg.num_not_signed_in;
		if(!n){
			n = 0;
		}
		updatePlayers(msg.players, n);
	} else if(command == "MUSIC"){
		playSound(msg.url);
	}
}

function updatePlayers(players, numNotSignedIn){
	var watchers = $("#list-watching");
	watchers.empty();
	
	var numWatchers = numNotSignedIn;
	var numPlayers = 0;
	var watchingList = $("#list-watching");
	
	for(var i = 0; i < players.length; i++){
		var player = players[i];
		var name = player.name;
		
		if("in_game" in player){
			numPlayers++;
			
			if("voted" in player){
				name += "<image class='voted' data-toggle='tooltip' title='Voted!' src='pics/checkbox.png'>";
			}
			
			var li = $("<li class='list-group-item player-item'>").html(name);
			
			if("alive" in player){
				li.addClass("alive");
			} else if("in_game" in player){
				li.addClass("dead");
			}
			
			li.data("name", player.name);
			
			li.click(clickPlayerHandler);
			
			$("#list-players").append(li);
		} else{
			numWatchers++;
			watchingList.append(name+", ");
		}
	}
	
	for(var i = 0; i < numNotSignedIn; i++){
		watchingList.append("Anon " + (i+1) + ", ");
	}
	
	var tt = watchingList.text();
	if(tt.length >= 2){
		tt = tt.substring(0,tt.length-2);
		watchingList.text(tt);
	}
	
	
	$("#players-count").text(numPlayers);
	$("#viewers-count").text(numWatchers);
}

function append(from, msg, isSpectator){
	var fromNarrator = from=="$narrator";
	
	var div = $("<div class='row msg'>");

	var authorDiv = $("<span class='msg-author'>");
	starize(authorDiv, from, true);
	authorDiv.addClass("sender");
	
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
			authorDiv.addClass("spectator");
			msgDiv.addClass("spectator");
		}
		div.append(authorDiv).append(msgDiv);
	}
	
	$("#chat-text").append(div);
	scrollToBottom();
	
	authorDiv.click(authorFilter);
	
	if(fromNarrator){
		if(msg.indexOf("A new game is forming")==0){
			playSound("new_game.mp3");
		}
		if(msg.indexOf("Assigning roles...")==0){
			playSound("game_started.mp3");
		}
	}
}

function starize(div, player, addColon){
	var addStar = player.toUpperCase() === "AMATOMATO";
	if(addColon){
		player += ":";
	}
	if(addStar) {
		div.append("<img src='pics/star.png' title='Player Of The Month' class='star'>" + player)
	} else{
		div.text(player);
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

function scrollToBottom(force){
	var win = $(window);
	var doScroll = force || (win.scrollTop() + win.height() >= $(document).height()-100);
	if(doScroll){
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

$("#enable-sounds-checkbox").click(function(e){
	var checked = $(this).is(":checked");
	send("CHAT","/enable-sounds " + checked);
});

function clickPlayerHandler(e){
	var name = $(e.target).data("name")+":";
	filter(name);
}

var author;
function authorFilter(e){
	filter($(e.target).text());
}

function filter(clicked){
	if(!clicked){
		return;
	}
	
	if(clicked == author){
		author = null;
	} else{
		author = clicked;
	}
	
	$(".msg").removeClass("hidden");
	$(".msg-author").removeClass("highlight");
	if(author != null){
		console.log("Filtering on: "+author);
		$(".msg-author").each(function(){
			var t = $(this);
			if(author == t.text()){
				t.addClass("highlight");
			} else{
				t.parent().addClass("hidden");
			}
		});
	}
	
	scrollToBottom(true);
}
