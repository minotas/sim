$(function() {
    $('#sim_content_menu div').mouseover(function(){
        $(this).addClass('sim_menu_over');
    });
    $('#sim_content_menu div').mouseout(function(){
        $(this).removeClass('sim_menu_over');
    });
    $('#sim_messages').dialog({  title: "Information",
        autoOpen: false,
        height: 380,
        width: 550,
        modal: true,
        buttons: { "ok": function() { $(this).dialog("close"); } } });
    $('#sim_form_register').dialog({  title: "Register",
                                   autoOpen: false,
                                   height: 380,
                                   width: 550,
                                   modal: true,
                                   buttons: { "send": function() { sim.clean_register(); var flag = sim.valida_form_register(); if(!flag){sim.new_user();} },
                                           "cancel": function() { $(this).dialog("close"); } } });
    sim.getuser();
});    


var sim = { 
    roles : { guest: 'guest', user: 'user'},
    message_types : { information : 1, error: 2},
    rest_uri : 'http://192.168.1.130:8080/SpesaInMano/ws/',
    sections : {home: 1},
    timer : {value: 0}
    };

sim.current_rol = sim.roles.guest;
sim.current_section = sim.sections.home;

/******************** USER FUNCTIONS *******************************/
sim.new_user = function () {
    sim.loading(true);
    var password = $('#register_password').val();
    var repeat_password = $('#register_repeat_password').val();
    if(password != repeat_password){
        sim.print_message('The passwords do not match');
    }
    else{
        var data_params = {};
        data_params.name = $('#register_firstname').val();
        data_params.lastname = $('#register_lastname').val();
        data_params.email = $('#register_email').val();
        data_params.username = $('#register_username').val();
        data_params.password = calcMD5($('#register_password').val());
       
        $.ajax({
            url: sim.rest_uri + 'user',
            data: JSON.stringify(data_params),
            type: 'POST',
            contentType: 'application/json',
            success : function(data) {
                console.log(data);
            	sim.loading(false);
                if(data.id_user){
                    $('#sim_form_register').dialog('close');
                    sim.print_message('The user was registered successful');
                }
                else if(data.response){
                    sim.print_message(data.response, sim.message_types.error);
                }
                else{
                    sim.print_message(data, sim.message_types.error);
                }
            },
            error:function(data){
            	sim.print_message(data.responseText, sim.message_types.error);
            }
        });
    }
};


/*************************** INITIALIZE ****************************/
sim.load_home = function (){
	
    sim.current_section = sim.sections.home;
    if(sim.current_rol == sim.roles.guest){
        $('#pri_form_login').html(tpl_form_login);
    }
    /*$.ajax({
        url: 'home.html', 
        success: function (data){
            $('#pri_content_content').html(data);
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
        data: JSON.stringify(data_params),
        type: 'POST',
       contentType: 'application/json',
        success : function(data) {
        	console.log(data);
            if(data.id_user){
            	$.cookie('name', ''+data.name);
            	var cookieValue = $.cookie("name");
            	$.cookie('lastname', ''+data.lastname);
                sim.current_rol = sim.roles.user;
                sim.change_auth();
            	var html = 'You are authenticated as <i>'+data.name + ' '+data.lastname+'</i> ';
                html += '<a href="javascript:;" onclick="sim.logout()" class="ui-button-text sim_login">(logout)</a>';
                $('#sim_form_login').html(html);
               
                sim.load_home();
            }
            else{
                sim.print_message(data, sim.message_types.error);
            }
        },
        error:function(data){
        	sim.print_message(data.responseText, sim.message_types.error);
        }
       /* statusCode : {
            404 : function(){sim.print_message("user not found", sim.message_types.error);},
            400: function(){sim.print_message("empty fields", sim.message_types.error);},
            501: function(){sim.print_message("Internal error", sim.message_types.error);}
        }*/
    });
};

sim.change_auth = function(){
	if($.cookie('name') != null){
		sim.current_rol = sim.roles.user;
	}
	else{
		sim.current_rol = sim.roles.guest;
	}
    switch(sim.current_rol){
    case sim.roles.guest:
    	$('#sim_form_login').html(tpl_form_login);
		break;
    case sim.roles.user:
        break; 
    }
};

sim.logout = function(){
	$.removeCookie('name');
	$.removeCookie('lastname');
	sim.current_rol = sim.roles.guest;
    sim.change_auth();
    sim.load_home();
};

sim.getuser = function(){
	
	if($.cookie('name') != null){
		var name = $.cookie('name');
		var lastname = $.cookie('lastname');
		sim.current_rol = sim.roles.user;
		var html = 'You are authenticated as <i>'+name + ' '+lastname+'</i> ';
        html += '<a href="javascript:;" onclick="sim.logout()" class="ui-button-text sim_login">(logout)</a>';
        $('#sim_form_login').html(html);
	}
	else{
        sim.current_rol = sim.roles.guest;
        sim.load_home();
    }
    sim.change_auth();
};

sim.open_form_register = function() {
    $('#register_firstname').val('');
    $('#register_lastname').val('');
    $('#register_email').val('');
    $('#register_password').val('');
    $('#register_repeat_password').val('');
    $('.sim_input_form #firstname_label').text('*');
    $('.sim_input_form #lastname_label').text('*');
    $('.sim_input_form #email_label').text('*');
    $('.sim_input_form #password_label').text('*');
    $('.sim_input_form #rpassword_label').text('*');
    $('#sim_form_register').dialog('open');
};


/************************ OTHERS FUNCTIONS **********************************/
sim.print_message = function (text, type) {
    var style = 'ui-state-highlight';
    
    if(type == sim.message_types.error) {
        style = 'ui-state-error';
        $('#sim_messages').dialog({title: 'Error'});
    }
    else{
        $('#sim_messages').dialog({title: 'Information'});
    }
    
    $('#sim_messages').html('<div class=' + style + '>' + text + '</div>');
    $('#sim_messages').dialog('open');
    
};

sim.valida_form_register = function(){
    var flag = false;
    
    if($('#register_firstname').val() == '' || !sim.validaletters($('#register_firstname').val())){
        $('.sim_input_form #firstname_label').text('field alfanumeric and obligatory');
        flag = true;
    }
    if($('#register_lastname').val() == '' || !sim.validaletters($('#register_lastname').val())){
        $('.sim_input_form #lastname_label').text('field alfanumeric and obligatory');
        flag = true;
    }
    if($('#register_email').val() != '' && !sim.validaEmail($('#register_email').val())){
        $('.sim_input_form #email_label').text('Please enter a valid email');
        flag = true;
    }
    if($('#register_password').val() == '' || $('#register_repeat_password').val().length <= 5){
        $('.sim_input_form #password_label').text('password too short or empty');
        flag = true;
    }
    if($('#register_repeat_password').val() == '' || $('#register_repeat_password').val().length <= 5){
        $('.sim_input_form #rpassword_label').text('password too short or empty');
        flag = true;
    }
    return flag;
};

sim.validanum = function(e) { // 1
    tecla = (document.all) ? e.keyCode : e.which; // 2
    if (tecla==8) return true; // 3
    patron =/[0-9-.\t]/; // 4
    te = String.fromCharCode(tecla); // 5
    return patron.test(te); // 6
};

sim.validanumber = function (number){
    var filter=/^[0-9.]+$/;
    if (filter.test(number)){
        return true;
    }
    return false;
};

sim.validaletters = function(letters){
    var filter=/^[A-Za-zאטלעשביםףת]+$/;
    if (filter.test(letters)){
        return true;
    }
    return false;
};

sim.validal = function(e) { // 1
    tecla = (document.all) ? e.keyCode : e.which; // 2
    if (tecla==8) return true; // 3
    patron =/[A-Za-z\s'אטלעשביםףת\t]/; // 4
    te = String.fromCharCode(tecla); // 5
    return patron.test(te); // 6
};

sim.validaEmail = function(e) {
    var filter=/^[A-Za-z][A-Za-z0-9_]*@[A-Za-z0-9_]+\.[A-Za-z0-9_.]+[A-za-z]$/;
    if (filter.test(e)){
        return true;
    }
    return false;
};

sim.clean_register = function(){
    
    if($('#register_firstname').val() != '' && sim.validaletters($('#register_firstname').val())){
        $('.sim_input_form #firstname_label').text('*');
    }
    if($('#register_lastname').val() != '' && sim.validaletters($('#register_lastname').val())){
        $('.sim_input_form #lastname_label').text('*');
    }
    if(sim.validaEmail($('#register_email').val())){
        $('.sim_input_form #email_label').text('');
    }
    if($('#register_username').val() != ''){
        $('.sim_input_form #username_label').text('*');
    }
    if($('#register_password').val() != '' && $('#register_repeat_password').val().length >= 5){
        $('.sim_input_form #password_label').text('*');
    }
    if($('#register_repeat_password').val() != '' && $('#register_repeat_password').val().length >= 5){
        $('.sim_input_form #rpassword_label').text('*');
    }
    
};

sim.loading = function(state){
    if(state){
        $('#sim_loading').show();
    }
    else{
        $('#sim_loading').hide();
    }
};

/***************************** TEMPLATES **************************************/
var tpl_form_login = '<dl><dd><span>email: </span><input class="ui-widget input ui-corner-all" type="text" id="login_username"></dd>';
tpl_form_login += '<dd><span>Password: </span><input class="ui-widget input ui-corner-all" type="password" id="login_password" style="weight:15px;">';
tpl_form_login += '<button class="ui-button ui-button-text-only ui-widget ui-state-default ui-corner-all" onclick="sim.authentication()">';
tpl_form_login += '<span class="ui-button-text">Go</span></button></dd>';
tpl_form_login += '<dd><span class="ui-button-text"><a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Do you want to register?</a></span></dd></dl>';

var tpl_button_login = '<nav class="ui-button-text">';
tpl_button_login += '<a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Account access</a>|';
tpl_button_login += '<a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Do you want to register?</a>|';
tpl_button_login += '<a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">FAQ</a>';
tpl_button_login += '</nav>';