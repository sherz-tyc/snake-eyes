$(document).ready(function () {
	
	$('#playButton').attr('disabled','disabled');
    $('#stakeValue').keyup(function(){
        if($(this).val().length !=0)
            $('#playButton').removeAttr('disabled');          
        else
            $('#playButton').attr('disabled','disabled');
    })

    $("#playButton").click(function (event) {
    	 //stop submit the form, we will post it manually.
        event.preventDefault();
        var attr = $(this).attr('disabled');
        
        if (attr === undefined) {
        	fire_ajax_submit();
        }
    });

});

function fire_ajax_submit() {

    var stake = $("#stakeValue").val();
    var playerId = $("#playerId").val();

    $("#playButton").attr('disabled','disabled');

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/snakeeyes/play?stake=" + stake + "&playerId=" + playerId,
        cache: false,
        timeout: 600000,
        success: function (data) {
        	
        	var dice1 = data.dice1;
        	var dice2 = data.dice2;
        	var currentPlayerId = data.player_id;

            var result = "<h2 style=\"color:blue\">" + data.payout_name + "</h2>"
                + "<h3> You have won £" + data.winnings + "</h3>"
                + "<h3> Your new balance is £" + data.updated_balance + "</h3>";
            $('#dice1').html(dice1);
            $('#dice2').html(dice2);
            $('#feedback').html(result);
            $('#currentPlayerId').html(currentPlayerId);
            $("#playerId").val(currentPlayerId);

            get_game_log(currentPlayerId);
            $("#playButton").removeAttr('disabled');
            

        },
        error: function (e) {
            var json = JSON.parse(e.responseText);

            var msg = "<h3>Error Encountered</h3><h4 style=\"color:red\">"
                + json.message + "</h4>";
            $('#feedback').html(msg);

            console.log("ERROR : ", e);
            $("#playButton").removeAttr('disabled');

        }
    });

}

function get_game_log(playerId) {

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/snakeeyes/log?playerId=" + playerId,
        cache: false,
        timeout: 600000,
        success: function (data) {
        	
        	data.reverse();
        	var gamelog_table = '<table class="table table-striped"><tr><td>Date</td><td>Dice 1</td><td>Dice 2</td><td>Stake</td><td>'
            + 'Result</td><td>Winnings</td><td>Balace History</td></tr>';
            $.each(data, function (i, item) {
            	gamelog_table += '<tr><td>' + item.attempted_date + '</td><td>' + item.dice1 + 
                '</td><td>' + item.dice2 + '</td><td>' + item.stake + '</td><td>' + item.payout_name
                + '</td><td>' + item.winnings + '</td><td>' + item.updated_balance+ '</td></tr>';
            });
            gamelog_table += '</table>';
        	$('#gamelog_feedback').html(gamelog_table);

        },
        error: function (e) {
            var json = JSON.parse(e.responseText);

            var msg = "<h3>Error Encountered</h3><h4 style=\"color:red\">"
                + json.message + "</h4>";
            $('#gamelog_feedback').html(msg);

        }
    });

}