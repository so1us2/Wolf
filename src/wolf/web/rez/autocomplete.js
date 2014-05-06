function autocomplete(inputField){
	var msg = inputField.val();
	var spaceIndex = msg.lastIndexOf(" ");
	
	var lastWord = msg.substring(spaceIndex+1);
	
	if(lastWord.length == 0){
		return;
	}
	
	lastWord = lastWord.toUpperCase();
	
	var matches = [];
	
	$("#list-players li").each(function(){
		var t = $(this);
		
		if(t.text().toUpperCase().indexOf(lastWord) === 0){
			matches.push(t.text());
		}
	});
	
	if(matches.length == 1){
		var messageBeforeSpace = "";
		if(spaceIndex != -1){
			messageBeforeSpace = msg.substring(0, spaceIndex+1);
		}
		inputField.val(messageBeforeSpace+matches[0]);
	}
}

