var testing = $TESTING;
var loggedIn = false;

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

//open websocket
if (WebSocket){
	var domain = testing ? "localhost" : "playwolf.net";
	ws = new WebSocket("ws://"+domain+":80/socket");
	ws.onopen = function() {
		announce("Connected to chat server.");
		var userID = $.cookie("userID");
		var accessToken = $.cookie("accessToken");
		if(testing){
			userID = -1;
			accessToken = "TESTING";
		}
		if(userID && accessToken){
			loginWithFB(userID, accessToken);
		} else{
			$(".login-advertise").clone().appendTo($("#chat-text"));
		}
		
		switchToRoomInURL();
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
	loginWithFB(data.authResponse.userID, data.authResponse.accessToken);
}

function loginWithFB(userID, accessToken){
	if(!testing){
		var date = new Date();
		date.setTime(date.getTime() + 24 * 60 * 60 * 1000);
		$.cookie("userID", userID, {expires: date});
		$.cookie("accessToken", accessToken, {expires: date});
	}
	send2("LOGIN", userID, accessToken);
	
    $(".login-advertise").addClass("hidden");
    
    $("#text-input").removeClass("hidden");
    
    $("#create-game-button").click(newGameHandler);
    
    loggedIn = true;
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
		$('[data-toggle="dropdown"]').parent().removeClass('open');
		return false;
	}
	setRoom(room);
	
	hideNewRoomDropdown();
	return false;
}

function newRoom(){
	hideNewRoomDropdown();
	
     var roomName = window.prompt("Enter room name:");
     if(!roomName){
    	 return false;
     }
     
     $.post("/rooms/" + roomName).success(function(){
    	 setRoom(roomName);
     });
     
     return false;
}

function hideNewRoomDropdown(){
	$('[data-toggle="dropdown"]').parent().removeClass('open');
}

function switchToRoomInURL(){
	var path = window.location.pathname;
	
	if(path.indexOf('/room/') != 0){
		return;
	}
	
	var roomName = path.substring(6, path.length).split('_').join(' ');
	setRoom(roomName);
}

function setRoom(room){
	if(currentRoom == room){
		return;
	}
	currentRoom = room;
	
	send("SWITCH_ROOM", room);
	
	console.log("Switching rooms: "+room);
	
	$("#room-name").text(room + " ");
	append("$narrator", "Joined room: "+room);
	
	if(history && history.pushState){
		history.pushState(null, null, '/room/'+room.split(' ').join('_'));
	}
}

function send(command, arg){
	var jsonData = {
		command:command,
		args:[arg]
	};
	ws.send(JSON.stringify(jsonData));
}

function send2(command, arg1, arg2){
	var jsonData = {
		command:command,
		args:[arg1, arg2]
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
	  if(loggedIn){
		 append(msg.from, msg.msg, true);
	  }
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
	} else if(command == "SWITCH_ROOM"){
		announce(msg.msg);
		setRoom(msg.room);
	} else if(command == "TIMER"){
	   setTimer(msg.end);
	} else if(command == "STAGE"){
		setStage(msg.stage);
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
				name += "<image class='voted' data-toggle='tooltip' title='Voted!' src='/pics/checkbox.png'>";
			}
			
			var li = $("<li class='list-group-item player-item'>").html(name);
			
			if("alive" in player){
				if("disconnected" in player){
					li.addClass("disconnected");
				} else{
					li.addClass("alive");
				}
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
			playSound("/new_game.mp3");
		}
		if(msg.indexOf("Assigning roles...")==0){
			playSound("/game_started.mp3");
		}
	}
}

function starize(div, player, addColon){
	var addStar = player.toUpperCase() === "OSCAR";
	if(addColon){
		player += ":";
	}
	if(addStar) {
		div.append("<img src='/pics/star.png' title='Player Of The Month' class='star'>" + player)
	} else{
		div.text(player);
	}
}

function playSound(url){
	var enable_sounds = $("#enable-sounds-checkbox").is(":checked");
	console.log("playsound: "+url+" :: "+enable_sounds);
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
			var ahref = $("<a class='pointer'>").text(rule);
			var li = $("<li data-rule='"+rule+"'>").append(ahref);
			list.append(li);
			li.click(rulesListener);
			if(i == 0){
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

function newGameHandler(){
	$("#new-game-modal").modal("hide");
	
	var numPlayers = $("#num-players-chooser").val();
	var timeLimit = $("#time-limit-chooser").val();
	var rated = $("#rated-checkbox").is(":checked") ? "YES" : "NO";
	var priv = $("#private-checkbox").is(":checked");
	var silent = $("#silent-checkbox").is(":checked");
	
	send("CHAT", "/newgame");
	if(priv){
		send("CHAT", "/private");
	}
	send("CHAT", "/setplayers " + numPlayers);
	send("CHAT", "/setflag TIME_LIMIT " + timeLimit);
	send("CHAT", "/setflag RATED_GAME " + rated);
	
	if(silent){
		send("CHAT", "/setflag SILENT_GAME YES");
		send("CHAT", "/setflag ANNOUNCE_VOTES YES");
		send("CHAT", "/setflag WITHDRAW_VOTES YES");
		send("CHAT", "/setflag VOTING_METHOD END_ON_MAJORITY");
	}
}

function setStage(stage){
	if(stage == 0){
		$("#new-game-button").show();
	} else{
		$("#new-game-button").hide();
	}
}
