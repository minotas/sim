$(function() {
	$( document ).on( "mouseenter", "#sim_categories div", function() {
		$(this).addClass('sim_menu_over');
	});
	$(document).on( "mouseleave", "#sim_categories div", function() {
		$(this).removeClass('sim_menu_over');
	});
	$(document).on("mouseleave", "div[id^='toggle_']", function(){
		
		$(".sim_dropdown_toggle").toggle(false);
	});
	$(document).on("mouseenter", "#sim_header", function(){
		
		$(".sim_dropdown_toggle").toggle(false);
	});
	$(document).on("mouseenter", "#sim_categories div .sim_dropdown_toggle a", function(){
		
		$(this).css("text-decoration", "underline");
	});
	$(document).on("mouseleave", "#sim_categories div .sim_dropdown_toggle a", function(){
		
		$(this).css("text-decoration", "none");
	});
    $('#sim_messages').dialog({  title: "Information",
        autoOpen: false,
        height: 380,
        width: 550,
        modal: true,
        buttons: { "ok": function() { $(this).dialog("close"); sim.loading(false);} } });
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
    rest_uri : 'http://localhost:8080/SpesaInMano/ws/',
    sections : {home: 1},
    timer : {value: 0}
    };

sim.current_rol = sim.roles.guest;
sim.current_section = sim.sections.home;
sim.current_name_section = '';
sim.current_cart = {};
sim.current_cart.listitem = [];

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
                //console.log(data);
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
        	//console.log(data);
            if(data.id_user){
            	$.cookie('name', ''+data.name);
            	$.cookie('lastname', ''+data.lastname);
            	$.cookie('id_user', data.id_user);
                sim.current_rol = sim.roles.user;
                //sim.change_auth();
            	/*var html = 'Welcome Mr./Ms.: '+data.name + ' '+data.lastname+'</i> ';
                html += '<a href="javascript:;" onclick="sim.logout()" class="ui-button-text sim_login">(logout)</a>';
                $('#sim_form_login').html(html);*/
				sim.getuser();
                sim.load_home();
            }
            else{
                sim.print_message(data, sim.message_types.error);
            }
        },
        error:function(data){
        	sim.print_message(data.responseText, sim.message_types.error);
        }
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
    	$('#sim_cart').hide();
    	$("#sim_categories").hide();
		$("#sim_body_offers").hide();
    	$('#sim_body_body').hide();
    	$('#sim_body_menu').hide();
    	$('#sim_nav').hide();
		$("#sim_categories").html('');
		break;
    case sim.roles.user:
		sim.loading(false);
		$('#sim_cart').show();
		$("#sim_categories").show();
		$("#sim_body_offers").show();
		sim.get_categories();
		sim.get_offers();
        break; 
    }
};

sim.get_offers = function(){
	$("#sim_body_offers").html('');
	$("#sim_body_offers").append(tpl_product);
	$("#sim_body_offers").append(tpl_product);
	//$("#sim_body_products").show();
};

sim.get_categories = function(){
	$("#sim_categories").html('');
	$("#sim_categories").append('<div onclick="">Offers<div id="uno" style="display:none;">uno hola</div></div>');
    $.ajax({
        url: sim.rest_uri + 'category',
        type: 'GET',
        success : function(data) {
        	//console.log(data.category[1]);
            if(data.category.length > 0){
            	sim.load_categories(data);
            }
            else{
                sim.print_message(data, sim.message_types.error);
            }
        },
        error:function(data){
        	sim.print_message(data.responseText, sim.message_types.error);
        }
    });
};

sim.load_categories = function (data){
    var category_obj;
    for(var i = 0; i < data.category.length; i++){
        category_obj = data.category[i];
        $('#sim_categories').append(tpl_categories(category_obj));
    }
};

sim.get_subcategory = function(id){
	sim.current_subcategory = id;
	//console.log(id);
	$.ajax({
        url: sim.rest_uri + 'category/' + id + '/productType/',
        type: 'GET',
        success : function(data) {
        	
            if(data.productType.length > 0){
            	sim.load_subcategories(data, id);
            	sim.load_subcategories_menu(data);
            }
            else{
                sim.print_message(data, sim.message_types.error);
            }
        },
        error:function(data){
        	sim.print_message(data.responseText, sim.message_types.error);
        }
    });
};

sim.load_subcategories = function(data, id){
	$(".sim_dropdown_toggle").toggle(false);
	$('#toggle_'+id).html('');
	$('#toggle_'+id).append(tpl_subcategory(data, 1));
	$("#toggle_"+id).toggle();
};

sim.load_subcategories_menu = function(data){
	$("#sim_body_menu").html('');
	$('#sim_body_menu').append('<span>Categories</span>');
	$('#sim_body_menu').append(tpl_subcategory(data, 2));
};

sim.get_subcategory_products = function(id, name){
	$(".sim_dropdown_toggle").toggle(false);
	$('#sim_nav').html('<span>'+name+'</span>');
	$("#sim_body_offers").hide();
	$('#sim_body_body').show();
	$('#sim_nav').show();
	$("#sim_body_menu").show();
	//$("#sim_body_products").append(tpl_product);
	$("#sim_body_products").show();
	var data_param = {};
	data_param.productType = id;
	$.ajax({
        url: sim.rest_uri + 'product',
        type: 'GET',
        data: data_param,
        success : function(data) {
        	console.log(data);
            /*if(data.productType.length > 0){
            	sim.load_subcategories(data, id);
            }
            else{
            	//$('#sim_categories').html(data);
                sim.print_message(data, sim.message_types.error);
            }*/
        }/*,
        error:function(data){
        	sim.print_message(data.responseText, sim.message_types.error);
        }*/
    });
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
		var html = 'Welcome Mr./Ms.: <i>'+name + ' '+lastname+'</i> ';
        html += '<a href="javascript:;" onclick="sim.logout()" class="ui-button-text sim_login">(logout)</a>';
        $('#sim_form_login').html(html);
		var number = $.cookie('number_products');
		if(number != null){
			$('#sim_label_number_products').html('<a href="javascript:;" onclick="sim.open_products_detail()" class="sim_ahref"><p><strong>'+number+' PRODUCTS</strong></p></a>');
		}
		else{
			$('#sim_label_number_products').html('<a href="javascript:;" onclick="sim.open_products_detail()" class="sim_ahref"><p><strong>0 PRODUCTS</strong></p></a>');
		}
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


/************************ PRODUCTS ******************************************/
sim.open_products_detail = function(){
	$("#sim_products_detail_window ul").html('');
	var listitem = $.cookie('myCart');
	var obj_listitem = jQuery.parseJSON(''+listitem+'');
	console.log(obj_listitem);
	for(var i = 0; i < obj_listitem.listitem.length; i ++){
		//var html = '<li class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span>'+obj_listitem.listitem[i]+'</li>';
		$("#sim_products_detail_window ul").append(tpl_product_detail(obj_listitem.listitem[i]));
	}
	//$("#sim_products_detail_window ul").append('<li class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span>Item 1</li>');
	$('#sim_products_detail_window').show();
	$("#sim_products_detail_window").dialog();
}

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
    if($('#register_email').val() == '' || !sim.validaEmail($('#register_email').val())){
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
    if(sim.validaEmail($('#register_email').val()) && $('#register_email').val() != ''){
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

sim.key_authentication = function(event){
	if(event.keyCode == 13){
		sim.loading(true);
		sim.authentication();
	}
};

sim.plus_quantity = function(id){
	var plus = $('#sim_input_quantity_'+id).val();
	plus++;
	$('#sim_input_quantity_'+id).val(plus);
	$('#sim_label_quantity_'+id).html(plus);
};

sim.minus_quantity = function(id){
	var minus = $('#sim_input_quantity_'+id).val();
	if(minus > 0){
		minus--;
		$('#sim_input_quantity_'+id).val(minus);
		$('#sim_label_quantity_'+id).html(minus);
	}	
};

sim.add_product_to_cart = function(id, name){
	//$.removeCookie('cart_position');
	//$.removeCookie('myCart');
	//alert(name);
	
	var object_cart = {};
	sim.current_cart = $.cookie('myCart');
	if(sim.current_cart == null){
		sim.current_cart = {};
		sim.current_cart.listitem = new Array();
		sim.current_cart.listitem[0] = {};
		sim.current_cart.listitem[0].id_product = {};
		sim.current_cart.listitem[0].id_product.id_product = id;
		sim.current_cart.listitem[0].id_product.name_product = name;
		sim.current_cart.listitem[0].quantity = $('#sim_input_quantity_'+id).val();
		sim.current_cart.number_products = 1;
		$.removeCookie('number_products');
		$.cookie('number_products', sim.current_cart.number_products);
		//console.log(sim.current_cart);
		var pos = 1;
		//$('#sim_label_number_products').html(''+sim.current_cart.number_products+' PRODUCTS</strong></p>');
		$.cookie('cart_position', pos);
		$.cookie('myCart', JSON.stringify(sim.current_cart));
		$('#sim_label_number_products').html('<a href="javascript:;" onclick="sim.open_products_detail()" class="sim_ahref"><p><strong>1 PRODUCTS');
	}
	else{
		var pos = $.cookie('cart_position');
		parseInt(pos);
		console.log(pos);
		$.removeCookie('cart_position');
		$.removeCookie('myCart');
		
		object_cart = jQuery.parseJSON(''+sim.current_cart+'');
		console.log(object_cart);
		//sim.current_cart.listitem = new Array();
		object_cart.listitem[pos] = {};
		object_cart.listitem[pos].id_product = {};
		object_cart.listitem[pos].id_product.id_product = id;
		object_cart.listitem[pos].id_product.name_product = name;
		object_cart.listitem[pos].quantity = $('#sim_input_quantity_'+id).val();
		parseInt(object_cart.number_products);
		object_cart.number_products++;
		$.removeCookie('number_products');
		$.cookie('number_products', object_cart.number_products);
		$('#sim_label_number_products').html('<a href="javascript:;" onclick="sim.open_products_detail()" class="sim_ahref"><p><strong>'+object_cart.number_products+' PRODUCTS</strong></p></a>');
		pos++;
		//console.log(sim.current_cart.listitem[pos]);
		//var pos = 1;
		$.cookie('cart_position', pos);
		$.cookie('myCart', JSON.stringify(object_cart));
	}
	
};

/***************************** TEMPLATES **************************************/
var tpl_form_login = '<table border="0"><tr><td align="center"><span>email:</td><td></span><input class="ui-widget input ui-corner-all" type="text" id="login_username"></td></tr>';
tpl_form_login += '<tr><td align="center"><span>Password:</td><td></span><input class="ui-widget input ui-corner-all" type="password" onkeypress="sim.key_authentication(event)" id="login_password" style="weight:15px;">';
tpl_form_login += '<button class="ui-button ui-button-text-only ui-widget ui-state-default ui-corner-all" onclick="sim.authentication()"><span class="ui-button-text">Go</span></button></td></tr>';
tpl_form_login += '<tr><td align="right"><span class="ui-button-text"><a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Do you want to register?</a></span></td></tr></table>';
//tpl_form_login += '<span class="ui-button-text">Go</span></button></dd>';
//tpl_form_login += '<dd><span class="ui-button-text"><a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Do you want to register?</a></span></dd></dl>';
var tpl_button_login = '<nav class="ui-button-text">';
tpl_button_login += '<a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Account access</a>|';
tpl_button_login += '<a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">Do you want to register?</a>|';
tpl_button_login += '<a href="javascript:;" onclick="sim.open_form_register()" class="ui-button-text sim_login">FAQ</a>';
tpl_button_login += '</nav>';

var tpl_categories = function(category_obj){
    var html = '';
    if(sim.current_rol == sim.roles.user){
		html += '<div id="sim_subcategory" onclick="sim.get_subcategory('+category_obj.id_category+');"><span >'+category_obj.name+'</span></dvi>';
		html += '<div id="toggle_'+category_obj.id_category+'" class="sim_dropdown_toggle" data-toggle="dropdown" style="display:none;"></div>';
    }
    return html;
};

var tpl_subcategory = function(subcategory, option){
	var html = '';
	if(option == 1){
		if(subcategory.productType.length <= 7){
			$('#sim_categories div .sim_dropdown_toggle').css("width", "220px");
			html += '<ul>';
			for(var i = 0; i < subcategory.productType.length; i++){
				var obj_subcategory = subcategory.productType[i];
				//console.log(obj_subcategory);
				//alert(subcategory.productType[i].id);
				html +='<li><a href="#" onclick="sim.get_subcategory_products('+obj_subcategory.id_product_type+', \''+obj_subcategory.name+'\'); return false;">'+obj_subcategory.name+'</a></li>';
				
			}
			html +='</ul>';
		}
		else{
			$('#sim_categories div .sim_dropdown_toggle').css("width", "420px");
			html += '<table border="0"><th>';
			html += '<ul>';
			for(var i = 0; i < 7; i++){
				var obj_subcategory = subcategory.productType[i];
				//console.log(obj_subcategory);
				//alert(subcategory.productType[i].id);
				html +='<li><a href="#" onclick="sim.get_subcategory_products('+obj_subcategory.id_product_type+', \''+obj_subcategory.name+'\'); return false;">'+obj_subcategory.name+'</a></li>';
				
			}
			html +='</ul>';
			html += '</th><th>';
			html += '<ul>';
			for(var i = 7; i < subcategory.productType.length; i++){
				var obj_subcategory = subcategory.productType[i];
				//console.log(obj_subcategory);
				//alert(subcategory.productType[i].id);
				html +='<li><a href="#" onclick="sim.get_subcategory_products('+obj_subcategory.id_product_type+', \''+obj_subcategory.name+'\'); return false;">'+obj_subcategory.name+'</a></li>';
				
			}
			html +='</ul>';
			html += '</th></table>';
		}
	}
	else{
		html += '<ul>';
		for(var i = 0; i < subcategory.productType.length; i++){
			var obj_subcategory = subcategory.productType[i];
			//console.log(obj_subcategory);
			//alert(subcategory.productType[i].id);
			html +='<li><a href="#" onclick="sim.get_subcategory_products('+obj_subcategory.id_product_type+', \''+obj_subcategory.name+'\'); return false;">'+obj_subcategory.name+'</a></li>';
			
		}
		html +='</ul>';
	}
	return html;
};

var tpl_product = function(){
	var product_obj = {};
	product_obj.name = 'Coca-Cola';
	product_obj.id = 1;
	product_obj.brand = 'light';
	product_obj.quantity = '250ml';
	product_obj.measure_unit = '300ml';
    var html ='<div class="ui-corner-tr sim_products" id="sim_static_block_product_'+product_obj.id+'">';
    html += '<div class="ui-widget-header">'+product_obj.name+', '+product_obj.brand+'</div><div>';
    html +='<div ><img src="./theme/default/imgs/coca-cola.png" class="sim_image_product"/></div>';
    html += '<hr></hr><div><span class = "sim_static_block_label">'+product_obj.quantity+', '+product_obj.measure_unit+'</span></div>';
    html += '<div class="sim_product_add_button"><span class="ui-button ui-state-default ui-corner-all sim_product_add_button" onclick="sim.add_product_to_cart('+product_obj.id+', \''+product_obj.name+'\');">Add to Cart</span></div></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.minus_quantity('+product_obj.id+');"><span class="ui-icon ui-icon-circle-minus"></span></span></div>';
    html +='<div class="sim_product_quantity"><span id="sim_label_quantity_'+product_obj.id+'">0</span>';
	html += '<input id="sim_input_quantity_'+product_obj.id+'" type="hidden" value="0"/></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.plus_quantity('+product_obj.id+');"><span class="ui-icon ui-icon-circle-plus"></span></span></div>';
    html += '</div>';
    return html;
};

var tpl_product_detail = function(product_obj){

	var html ='<div><div id="sim_img_product_detail"></div><div id="sim_description_product_detail">'+product_obj.id_product.name_product+'</div>';
    html += '<div><div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.minus_quantity('+product_obj.id_product.id_product+');"><span class="ui-icon ui-icon-circle-minus"></span></div>';
    html +='<div class="sim_product_quantity"><span id="sim_label_quantity_'+product_obj.id_product.id_product+'">'+product_obj.quantity+'</span>';
    html += '<input id="sim_input_quantity_'+product_obj.id_product.id_product+'" type="hidden" value="'+product_obj.quantity+'"/></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.plus_quantity('+product_obj.id_product.id_product+');"><span class="ui-icon ui-icon-circle-plus"></span></div> </div></div>';
    
	return html;

};

var tpl_supermarket_validate = function(){
	var product_obj = {};
	product_obj.name = 'Coca-Cola';
	product_obj.id = 1;
	product_obj.brand = 'light';
	product_obj.quantity = '250ml';
	product_obj.measure_unit = '300ml';
    var html ='<div class="ui-corner-tr sim_products" id="sim_static_block_product_'+product_obj.id+'">';
    html += '<div class="ui-widget-header">'+product_obj.name+', '+product_obj.brand+'</div><div>';
    html +='<div ><img src="./theme/default/imgs/coca-cola.png" alt="Smiley face" class="sim_image_product"/></div>';
    html += '<hr></hr><div><span class = "sim_static_block_label">'+product_obj.quantity+', '+product_obj.measure_unit+'</span></div>';
    html += '<div class="sim_product_add_button"><span class="ui-button ui-state-default ui-corner-all sim_product_add_button" onclick="sim.add_product_to_cart('+product_obj.id+');">Add to Cart</span></div></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.minus_quantity('+product_obj.id+');"><span class="ui-icon ui-icon-circle-minus"></span></div>';
    html +='<div class="sim_product_quantity"><span id="sim_label_quantity_'+product_obj.id+'">0</span><input id="sim_input_quantity_'+product_obj.id+'" type="hidden" value="0"/></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.plus_quantity('+product_obj.id+');"><span class="ui-icon ui-icon-circle-plus"></span></div>';
    html += '</div>';
    return html;
};