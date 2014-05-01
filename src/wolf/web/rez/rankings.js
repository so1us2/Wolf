$("#rankings-button").click(function(e){
	var container = $("#rankings-modal tbody");
	
	container.empty();
	container.text("Loading...");
	
	$.getJSON("/rankings", function(data){
		container.empty();
		for(var i = 0; i < data.length; i++){
			var player = data[i];
			var tr = $("<tr>");
			tr.append($("<td>").text((i+1)+""));
			
			var playerTD = $("<td>");
			starize(playerTD, player.name);
			tr.append(playerTD);
			
//			tr.append($("<td>").text(player.name));
			
			tr.append($("<td>").text(player.wins));
			tr.append($("<td>").text(player.losses));
			tr.append($("<td>").text(player.win_percentage));
			container.append(tr);
			
			tr.data("player", player.name);
			tr.click(rankingsListener);
		}
	});
});

function rankingsListener(e){
	var player = $(e.target).parent().data("player");
	if(!player){
		return;
	}
	
	$("#game-history .modal-title").text(player+"'s Game History")
	
	var container = $("#game-history tbody");
	
	container.empty();
	container.text("Loading...");
	
	$.getJSON("/player/"+player+"/history", function(data){
		container.empty();
		for(var i = 0; i < data.length; i++){
			var game = data[i];
			var tr = $("<tr>");
			tr.append($("<td>").text(game.start_date));
			tr.append($("<td>").text(game.num_players));
			tr.append($("<td>").text(game.role));
			tr.append($("<td>").text(game.winner ? "Won" : "Lost"));
			tr.append($("<td>").text(game.alive ? "Alive" : "Dead"));
			container.append(tr);
		}
	});
	
	$("#rankings-modal").modal("hide");
	setTimeout(function(){
		$("#game-history").modal();
	}, 500);
};

$("#back-to-rankings-button").click(function(){
	$("#game-history").modal("hide");
	setTimeout(function(){
		$("#rankings-modal").modal();
	}, 500);
});

