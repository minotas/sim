var sim = { 
    roles : { guest: 'guest', user: 'user', admin : 'admin'},
    message_types : { information : 1, error: 2},
    rest_uri : 'http://localhost:8080/SpesaInMano/ws/',
    sections : {home: 1, alerts: 2, traffic: 3},
    timer : {value: 0}
    };

sim.load_home = function (){
        $('#sim_form_login').html(tpl_form_login);
    /*$.ajax({
        url: 'home.html', 
        success: function (data){
            $('#sim_content_content').html(data);
        }
        });*/
};

sim.authentication = function(){
    var data_params = {};
    data_params.email = $('#login_username').val();
    var login_password = calcMD5($('#login_password').val());
    data_params.password = login_password;
    $.ajax({
        url: sim.rest_uri + 'login',
        contentType: 'application/json',
       // dataType: "json",
        data: JSON.stringify(data_params),
        type: 'POST',
        success : function(data) {
            if(data.username){
                sim.timer.value = setTimeout('sim.logout()', 600*1000);
                //sim.current_rol = data.entity.rol;
                //sim.change_auth();
                var html = 'You are authenticated as <i>'+data.username + '</i> ';
                html += '<a href="javascript:;" onclick="sim.logout()" class="ui-button-text sim_login">(logout)</a>';
                $('#sim_form_login').html(html);
                sim.load_home();
            }
            else if(data.response){
                sim.print_message(data.response, sim.message_types.error);
            }
            else{
                sim.print_message(data, sim.message_types.error);
            }
        }
    });
};

sim.print_message = function (text, type) {
    var style = 'ui-state-highlight';
    
    if(type == sim.message_types.error) {
        style = 'ui-state-error';
        $('#imi_messages').dialog({title: 'Error'});
    }
    else{
        $('#imi_messages').dialog({title: 'Information'});
    }
    
    $('#imi_messages').html('<div class=' + style + '>' + text + '</div>');
    $('#imi_messages').dialog('open');
    
};


/***************************** TEMPLATES **************************************/
var tpl_form_login = '<dl><dd><span>Username: </span><input class="ui-widget input ui-corner-all" type="text" id="login_username"></dd>';
tpl_form_login += '<dd><span>Password: </span><input class="ui-widget input ui-corner-all" type="password" id="login_password" style="weight:15px;">';
tpl_form_login += '<button class="ui-button ui-button-text-only ui-widget ui-state-default ui-corner-all" onclick="sim.authentication()">';
tpl_form_login += '<span class="ui-button-text">Go</span></button></dd>';
tpl_form_login += '<dd><span class="ui-button-text"><a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Do you want to register?</a></span></dd></dl>';

var tpl_button_login = '<nav class="ui-button-text">';
tpl_button_login += '<a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Account access</a>|';
tpl_button_login += '<a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Do you want to register?</a>|';
tpl_button_login += '<a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">FAQ</a>';
tpl_button_login += '</nav>';