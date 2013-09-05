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
	$( "#sim_validate_button" ).click(function() {
		sim.open_send_cart();
	});
    $('#sim_form_register').dialog({  title: "Register",
                                   autoOpen: false,
                                   height: 380,
                                   width: 550,
                                   modal: true,
                                   buttons: { "send": function() { sim.clean_register(); var flag = sim.valida_form_register(); if(!flag){sim.new_user();} },
                                           "cancel": function() { $(this).dialog("close"); } } });
	$( "#selectable" ).selectable({
		stop:function(event, ui){
			$(event.target).children('.ui-selected').not(':first').removeClass('ui-selected');
		}
	});
	sim.getuser();
});    


var sim = { 
    roles : { guest: 'guest', user: 'user'},
    message_types : { information : 1, error: 2},
    rest_uri : 'http://localhost:8080/SpesaInMano/ws/',
    sections : {home: 1},
    timer : {value: 0},
	pagination: {records_by_page : 4, max_before_pagination : 8, current_pages : []},
	current_subcategory : null,
	monitored_supermarket : 0,
	db : {products : [], carts : []}
    };

sim.current_rol = sim.roles.guest;
sim.current_section = sim.sections.home;
sim.current_category = '';
sim.current_subcategory_name = '';

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
            if(data.id_user){
            	$.cookie('name', ''+data.name);
            	$.cookie('lastname', ''+data.lastname);
            	$.cookie('id_user', data.id_user);
                sim.current_rol = sim.roles.user;
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
		$("#sim_section").hide();
		$('#sim_section-2').show();
    	$('#sim_form_login').html(tpl_form_login);
    	$('#sim_cart').hide();
    	$("#sim_categories").hide();
		$("#sim_body_offers_external").hide();
    	$('#sim_body_body').hide();
    	$('#sim_body_menu').hide();
    	$('#sim_nav').hide();
		$("#sim_categories").empty();
		break;
    case sim.roles.user:
		sim.loading(false);
		$('#sim_section-2').hide();
		$("#sim_section").show();
		$('#sim_cart').show();
		$("#sim_categories").show();
		$("#sim_body_offers_external").show();
		sim.get_categories();
		//sim.get_offers();
        break; 
    }
};

/********************** PRODUCTS ***********************************/
sim.get_offers = function(position){
	sim.latitude = position.coords.latitude;
	sim.longitude = position.coords.longitude;
	var data_params = {"latitude" : sim.latitude,
			"longitude" : sim.longitude
	};
	//console.log(data_params);
	if(sim.current_rol != sim.roles.guest){
		$.ajax({
			url: sim.rest_uri + 'price/offers',
			type: 'GET',
			data: data_params,
			success : function(data) {
				//console.log(data);
				var offers_array = Object.prototype.toString.call( data.price ) === '[object Array]' ? data.price : [data.price];
				if(offers_array.length > 0){
					$("#sim_nav").hide();
					$("#sim_body_menu").hide();
					$("#sim_body_body").hide();
					$("#sim_body_offers").empty();
					for(var i = 1; i < offers_array.length; i++){
						$("#sim_body_offers").append(tpl_product_offer(offers_array[i]));
					}
					$('#sim_body_offers').css("width", 10*192);
					$("#sim_body_offers_external").show();
				}
				else{
					sim.print_message(data, sim.message_types.error);
				}
			},
			error:function(data){
				//alert("error");
				sim.print_message(data.responseText, sim.message_types.error);
			}
		});
	}
	
	//$("#sim_body_products").show();
};


/********************** CATEGORIES *******************************/
sim.get_categories = function(){
	$("#sim_categories").html('');
	$("#sim_categories").append('<div class="sim_subcategory" onclick="sim.getuser();">Offers<div id="uno" style="display:none;">uno hola</div></div>');
    $.ajax({
        url: sim.rest_uri + 'category',
        type: 'GET',
        success : function(data) {
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

sim.get_subcategory = function(id, name){
	sim.current_subcategory_name = name;
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
	sim.current_subcategory = id;
	$(".sim_dropdown_toggle").toggle(false);
	$('#sim_nav').html('<span>'+sim.current_subcategory_name+'	|	 '+name+'</span>');
	$("#sim_body_offers_external").hide();
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
            if(data != null){
				sim.db.products = Object.prototype.toString.call( data.product ) === '[object Array]' ? data.product : [data.product];
            	sim.load_products();
            }
            else{
            	$("#sim_body_products").html('<h>Products not found</h>').css("color" , "#2e90bd");
                //sim.print_message(data, sim.message_types.error);
            }
        },
        error:function(data){
        	sim.print_message(data.responseText, sim.message_types.error);
        }
    });
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




sim.load_products = function(){
	var data = sim.db.products;
	$("#sim_body_products").html('');
	if(!sim.pagination.current_pages[sim.current_subcategory]){
		sim.pagination.current_pages[sim.current_subcategory] = 1;
	}
	var current_page = sim.pagination.current_pages[sim.current_subcategory];
	var ini_page = data.length > ((current_page - 1) * sim.pagination.records_by_page) ? (current_page - 1) * sim.pagination.records_by_page : 0;
	var end_page = (ini_page + sim.pagination.records_by_page) > data.length ? data.length : ini_page + sim.pagination.records_by_page;
	if(data.length > sim.pagination.records_by_page && data.length > sim.pagination.max_before_pagination){
		sim.build_pagination(function() {sim.load_products()},  data.length, current_page);
		$('.sim_pagination').show();
	}
	else{
		end_page = data.length;
		$('.sim_pagination').hide();
	}
	for(var i = ini_page; i < end_page; i++){
		
		$("#sim_body_products").append(tpl_product(data[i]));
	}
};

sim.build_pagination = function(print_page, records_size, current_page){
	var html = $('.sim_pagination');
	html.empty();
	var number_page = Math.ceil(records_size / sim.pagination.records_by_page);
	for(var i = 1; i <= number_page; i++){
		html.append($('<span page="'+i+'" class="'+(i == current_page ? 'current' : '')+'">'+i+'</span>').click(function(){
			sim.pagination.current_pages[sim.current_subcategory] = $(this).attr('page');
			print_page();
		}));
	}	
};

sim.logout = function(){
	$.removeCookie('name');
	$.removeCookie('lastname');
	sim.current_rol = sim.roles.guest;
    sim.change_auth();
    sim.load_home();
};

sim.get_monitored_supermarket = function(){
	$.ajax({
        url: sim.rest_uri + 'monitoredSupermarket/number',
        type: 'GET',
        success : function(data) {
			
			if(data){
				sim.monitored_sumpermarket = data.number;
				console.log(sim.monitored_sumpermarket);
			
            }
            else{
                sim.print_message(data, sim.message_types.error);
            }
        },
        error:function(data){
			//alert("error");
        	sim.print_message(data.responseText, sim.message_types.error);
        }
    });
};

sim.getuser = function(){
	//window.localStorage["buttons_state"] = null;
	sim.getLocation();
	//window.localStorage = null;
	//sim.db.carts = JSON.stringify(eval(window.localStorage["carts"]));
	//console.log(sim.db.carts);
	//alert("user");
	if($.cookie('name') != null){
		sim.get_monitored_supermarket();
		sim.current_name = $.cookie('name');
		sim.current_lastname = $.cookie('lastname');
		sim.current_id_user = $.cookie('id_user');
		sim.current_rol = sim.roles.user;
		var html = '<button data-toggle="dropdown" class="btn btn-user dropdown-toggle">'+sim.current_name+'<span class="caret"></span></button>';
		html += '<ul class="dropdown-menu"><li><a href="javascript:;" onclick="sim.show_cart_lists()" class="ui-button-text sim_login"><strong>My cart lists</strong></a></li><li><a href="javascript:;" onclick="sim.monitoring_supermarket()" class="ui-button-text sim_login"><strong>My supermarkets</strong></a></li>';
		html += '<li><a href="javascript:;" onclick="sim.show_gifts()" class="ui-button-text sim_login"><strong>My Gifts</strong></a></li>';
		html += '<li class="divider"></li><li><a href="javascript:;" onclick="sim.logout()" class="ui-button-text sim_login">Logout</a></li></ul>';
        $('#sim_form_login').html(html);
		try {
			if (localStorage.getItem) {
				if(Object.prototype.toString.call( window.localStorage.carts ) === '[object Undefined]'){
					
					sim.db.carts = new Array;
					sim.db.carts[sim.current_id_user] = {array_position : 0, number_products : 0, listItem : [] };
					window.localStorage.carts = JSON.stringify(sim.db.carts);
					console.log(window.localStorage.carts);
				}
				else{
					
					sim.db.carts =  JSON.parse(window.localStorage.carts);
					console.log(sim.db.carts[sim.current_id_user]);
					if(sim.db.carts[sim.current_id_user] == null){
						sim.db.carts[sim.current_id_user] = {array_position : 0, number_products : 0, listItem : [] };
						window.localStorage.carts = JSON.stringify(sim.db.carts);
					}
					//console.log(sim.db.carts);
					
				}
			}
		} catch(e) {
			sim.db.carts = new Array;
			sim.db.carts[sim.current_id_user] = {array_position : 0, number_products : 0, listItem : [] };
		}
		
		/*if(window.localStorage.buttons_state){
			sim.buttons_state =  window.localStorage.buttons_state ;
			sim.buttons_state = jQuery.parseJSON( window.localStorage["buttons_state"] );
		}
		else{
			sim.buttons_state = [];
			window.localStorage["buttons_state"] = JSON.stringify(sim.buttons_state = []);
		}*/
		//console.log(sim.current_id_user);
		$('#sim_label_number_products').html('<a href="javascript:;" onclick="sim.open_products_detail()" class="sim_ahref"><p><strong>'+sim.db.carts[sim.current_id_user].number_products+' PRODUCTS</strong></p></a>');
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
	$("#sim_products_detail_window ul").empty();
	//var listItem = $.cookie('myCart');
	//var obj_listItem = jQuery.parseJSON(''+listItem+'');
	if(sim.db.carts[sim.current_id_user].listItem.length == null){
		$('#sim_products_detail_window').show();
		$('#sim_products_detail_window').html('<p>There is not products in the cart<p>');
		$("#sim_products_detail_window").dialog({
		  width: 420,
		  modal: true
		});
	}
	else{
		for(var i = 0; i < sim.db.carts[sim.current_id_user].listItem.length; i ++){
			//var html = '<li class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span>'+obj_listItem.listItem[i]+'</li>';
			$("#sim_products_detail_window ul").append(tpl_product_detail(sim.db.carts[sim.current_id_user].listItem[i]));
		}
		//$("#sim_products_detail_window ul").append('<li class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s"></span>Item 1</li>');
		$('#sim_products_detail_window').show();
		$("#sim_products_detail_window").dialog({
		  width: 420,
		  modal: true
		});
	}
};

sim.open_send_cart = function(){
	$('#sim_send_cart').dialog({  title: "confirm",
							   height: 300,
							   width: 500,
							   modal: true,
							   buttons: { "confrim": function() { sim.send_cart();},
									   "cancel": function() { $(this).dialog("close"); } } });
};

sim.send_cart = function(){
	$('#sim_send_cart').dialog('close');
	var data = {"listItem":sim.db.carts[sim.current_id_user].listItem};
	sim.loading(true);
	$.ajax({
		url: sim.rest_uri + 'marketList',
		data: JSON.stringify(data),
		type: 'POST',
		contentType: 'application/json',
		success : function(data) {
			//console.log(data);
			sim.loading(false);
			if(data.id){
				sim.validate_cart();
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
};

sim.validate_cart = function(){
	//window.localStorage["carts"] = '';
	//sim.getuser();
	
	if(sim.monitored_sumpermarket != 0){
		$.ajax({
			url: sim.rest_uri + 'marketList',
			type: 'GET',
			success : function(data) {
				sim.loading(false);
				if(data.supermarketListPrice){
					sim.loading(true);
					var supermarket_array = Object.prototype.toString.call( data.supermarketListPrice ) === '[object Array]' ? data.supermarketListPrice : [data.supermarketListPrice];
					$('#sim_body_menu').hide();
					$('#sim_body_body').hide();
					$('#sim_body_offers').html($('#sim_accordion'));
					$( "#sim_accordion" ).empty();
					for(var i = 0; i < supermarket_array.length; i++){
						$( "#sim_accordion" ).append(tpl_supermarket_list(supermarket_array[i]));
					}
					$('#sim_body_offers').show();
					$( "#sim_accordion" ).show();
					$( "#sim_accordion" ).accordion({
					  collapsible: true,
					  active: false,
					  heightStyle: "content",
					  activate: function( event, ui ) { 
						  //sim.load_supermarketlist_detail($(this).attr("id")); 
						  var currentHeaderID = ui.newHeader.attr("id");
						  //console.log(currentHeaderID);
						  sim.load_supermarketlist_detail(currentHeaderID); 
					  }
					});
					sim.loading(false);
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
	else{
		sim.get_supermarket();
		$('#sim_monitored_sumpermarket').dialog({  
			title: "Selecting supermarket",
			height: 380,
			width: 550,
			modal: true,
			buttons: { "send": function() { 
								
								var supermarkets = Array();
								$( "#selectable .ui-selected").each(function() {
									supermarkets.push($( this ).attr('id'));
								});
								sim.send_supermarket(supermarkets);
								$(this).dialog("close");},
                       "cancel": function() { $(this).dialog("close"); } }
		});
	}
	
};

sim.get_supermarket = function(){
	var data_params = {"latitude" : sim.latitude,
			"longitude" : sim.longitude
	};
	$.ajax({
			url: sim.rest_uri + 'monitoredSupermarket',
			type: 'GET',
			data: data_params,
			success : function(data) {
				console.log(data);
				if(data.monitoredSupermarket){
					var supermarket_array = Object.prototype.toString.call( data.monitoredSupermarket ) === '[object Array]' ? data.monitoredSupermarket : [data.monitoredSupermarket];
					for(var i = 0; i < supermarket_array.length; i++){
						var supermarket = supermarket_array[i];
						$('#selectable').append('<li class="ui-widget-content" id="'+supermarket.id_supermarket.id_supermarket+'" >'+supermarket.id_supermarket.name+'</li>');
					}
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
};

sim.send_supermarket = function(supermarkets){

	data_params = {
		"monitoredSupermarket" : []
	};
	for(var i = 0; i < supermarkets.length; i++){
		var id_supermaket = {
			"id_supermarket" : {"id_supermarket" : supermarkets[i]}
		};
		data_params.monitoredSupermarket.push(id_supermaket);
	}
	$.ajax({
		url: sim.rest_uri + 'monitoredSupermarket',
		data: JSON.stringify(data_params),
		type: 'POST',
		contentType: 'application/json',
		success : function(data) {
			console.log(data);
			sim.get_monitored_supermarket();
			sim.validate_cart();
			/*sim.loading(false);
			if(data.id){
				sim.validate_cart();
			}
			else if(data.response){
				sim.print_message(data.response, sim.message_types.error);
			}
			else{
				sim.print_message(data, sim.message_types.error);
			}*/
		},
		error:function(data){
			sim.print_message(data.responseText, sim.message_types.error);
		}
	});
	console.log(data_params);

};

sim.load_supermarketlist_detail = function(id){
	$.ajax({
		url: sim.rest_uri + 'marketList/details',
		type: 'GET',
		data: {"id_supermarket" : id},
		success : function(data) {
			sim.loading(false);
			if(data.marketListDetails){
				sim.loading(true);
				var details_array = Object.prototype.toString.call( data.marketListDetails ) === '[object Array]' ? data.marketListDetails : [data.marketListDetails];
					//$( "#sim_accordion" ).append(tpl_supermarket_list_detail(supermarket_array[i]));
					$( "#sim_accordion #div_"+id).empty();
					$( "#sim_accordion #div_"+id).html(tpl_supermarket_list_detail(details_array));
				sim.loading(false);
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
    var filter=/^[A-Za-zàèìòùáéíóú]+$/;
    if (filter.test(letters)){
        return true;
    }
    return false;
};

sim.validal = function(e) { // 1
    tecla = (document.all) ? e.keyCode : e.which; // 2
    if (tecla==8) return true; // 3
    patron =/[A-Za-z\s'àèìòùáéíóú\t]/; // 4
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

sim.showLocation = function(position) {
  sim.latitude = position.coords.latitude;
  sim.longitude = position.coords.longitude;
};

sim.errorHandler = function(err) {
  if(err.code == 1) {
    alert("Error: Access is denied!");
  }else if( err.code == 2) {
    alert("Error: Position is unavailable!");
  }
};

sim.getLocation = function(){
	
   if(navigator.geolocation){
      // timeout at 60000 milliseconds (60 seconds)
      var options = {timeout:10000};
      navigator.geolocation.getCurrentPosition(sim.get_offers, 
                                               sim.errorHandler,
                                               options);
   }else{
      sim.print_message("Sorry, browser does not support geolocation!", sim.message_types.error);
   }
};

sim.key_authentication = function(event){
	if(event.keyCode == 13){
		sim.loading(true);
		sim.authentication();
	}
};

sim.plus_quantity = function(id, detail){
	//console.log(sim.db.carts[sim.current_id_user].listItem);
	//console.log(id);
	//console.log(detail);
	if(detail){
		var band = 1;
		for(var i = 0; i < sim.db.carts[sim.current_id_user].listItem.length; i++){
			if(sim.db.carts[sim.current_id_user].listItem[i].id_product.id_product == id){
				band = 0;
				var pos = i;
				//console.log(pos);
			}
		}
		//band = 1 The product is not in details table
		if(!band){
			var plus = parseInt(sim.db.carts[sim.current_id_user].listItem[pos].quantity);
			//sim.db.carts[sim.current_id_user].listItem[pos].quantity++;
			plus++;
			$('#sim_label_quantity_detail_'+id).html(plus);
			sim.add_product_to_cart(id, '', true, true);
		}
	}
	else{
		var plus = $('#sim_input_quantity_'+id).val();
		plus++;
		$('#sim_input_quantity_'+id).val(plus);
		$('#sim_label_quantity_'+id).html(plus);
	}
};

sim.minus_quantity = function(id, detail){
	
	if(detail){
		var band = 1;
		for(var i = 0; i < sim.db.carts[sim.current_id_user].listItem.length; i++){
			if(sim.db.carts[sim.current_id_user].listItem[i].id_product.id_product == id){
				band = 0;
				var pos = i;
			}
		}
		//band = 1 The product is not in details table
		if(!band){
			var minus = parseInt(sim.db.carts[sim.current_id_user].listItem[pos].quantity);
			if(minus >= 1){
				minus--;
				$('#sim_label_quantity_detail_'+id).html(minus);
				sim.add_product_to_cart(id, '', true, false);
			}
		}
	}
	else{
		var minus = $('#sim_input_quantity_'+id).val();
		if(minus >= 1){
			minus--;
			$('#sim_input_quantity_'+id).val(minus);
			$('#sim_label_quantity_'+id).html(minus);
		}
	}
};

sim.add_product_to_cart = function(id, name, detail, plus){
	sim.loading(true);
	//sim.buttons_state[id] = 1;
	var quantity = $('#sim_input_quantity_'+id).val();
	if(quantity != '0'){
		var object_cart = {};
		if(sim.db.carts[sim.current_id_user] == null){
			sim.db.carts[sim.current_id_user] = {};
			sim.db.carts[sim.current_id_user] = new Array();
			sim.db.carts[sim.current_id_user].listItem[0] = {};
			sim.db.carts[sim.current_id_user].listItem[0].id_product = {};
			sim.db.carts[sim.current_id_user].listItem[0].id_product.id_product = id;
			sim.db.carts[sim.current_id_user].listItem[0].id_product.name = name;
			sim.db.carts[sim.current_id_user].listItem[0].quantity = $('#sim_input_quantity_'+id).val();
			sim.db.carts[sim.current_id_user].number_products = 1;
			sim.db.carts[sim.current_id_user].array_position = 1;
			window.localStorage["carts"] = JSON.stringify(sim.db.carts);
			window.localStorage["buttons_state"] = JSON.stringify(sim.buttons_state);
			$('#sim_label_number_products').html('<a href="javascript:;" onclick="sim.open_products_detail()" class="sim_ahref"><p><strong>1 PRODUCTS');
		}
		else{
			var band = 1;
			//var pos;
			for(var i = 0; i < sim.db.carts[sim.current_id_user].listItem.length; i++){
				if(sim.db.carts[sim.current_id_user].listItem[i].id_product.id_product == id){
					band = 0;
					var pos = i;
				}
			}
			//band = 1 The product is not in details table
			if(band){
				var pos = parseInt(sim.db.carts[sim.current_id_user].array_position);
				sim.db.carts[sim.current_id_user].listItem[parseInt(sim.db.carts[sim.current_id_user].array_position)] = {};
				sim.db.carts[sim.current_id_user].listItem[parseInt(sim.db.carts[sim.current_id_user].array_position)].id_product = {};
				sim.db.carts[sim.current_id_user].listItem[parseInt(sim.db.carts[sim.current_id_user].array_position)].id_product.id_product = id;
				sim.db.carts[sim.current_id_user].listItem[parseInt(sim.db.carts[sim.current_id_user].array_position)].id_product.name = name;
				sim.db.carts[sim.current_id_user].listItem[parseInt(sim.db.carts[sim.current_id_user].array_position)].quantity = $('#sim_input_quantity_'+id).val();
				parseInt(sim.db.carts[sim.current_id_user].number_products);
				sim.db.carts[sim.current_id_user].number_products++;
				$('#sim_label_number_products').html('<a href="javascript:;" onclick="sim.open_products_detail()" class="sim_ahref"><p><strong>'+sim.db.carts[sim.current_id_user].number_products+' PRODUCTS</strong></p></a>');
				parseInt(sim.db.carts[sim.current_id_user].array_position)
				sim.db.carts[sim.current_id_user].array_position++;
			}
			else{
				if(detail){
					if(plus){
						parseInt(sim.db.carts[sim.current_id_user].listItem[pos].quantity);
						sim.db.carts[sim.current_id_user].listItem[pos].quantity++;
					}
					else{
						parseInt(sim.db.carts[sim.current_id_user].listItem[pos].quantity);
						sim.db.carts[sim.current_id_user].listItem[pos].quantity--;
						if(sim.db.carts[sim.current_id_user].listItem[pos].quantity == 0 ){
							$('#sim_product_detail_'+id).remove();
						}
					}
				}
				else{
					//sim.db.carts[sim.current_id_user].listItem[pos].quantity = $('#sim_input_quantity_'+id).val();
					parseInt(sim.db.carts[sim.current_id_user].listItem[pos].quantity);
					sim.db.carts[sim.current_id_user].listItem[pos].quantity = parseInt(sim.db.carts[sim.current_id_user].listItem[pos].quantity) + parseInt($('#sim_input_quantity_'+id).val());
				}
				
			}
			window.localStorage["carts"] = JSON.stringify(sim.db.carts);
			window.localStorage["buttons_state"] = JSON.stringify(sim.buttons_state);
		}
		sim.loading(false);
	}
	else{
		sim.print_message("Enter the desired product quantity", sim.message_types.error);
	}
	
};

sim.delete_from_cart = function(id){
	sim.buttons_state[id] = 0;
	var quantity = $('#sim_input_quantity_'+id).val();
	if(quantity != '0'){
		var object_cart = {};
		if(sim.db.carts[sim.current_id_user] != null){
			var band = 1;
			//var pos;
			for(var i = 0; i < sim.db.carts[sim.current_id_user].listItem.length; i++){
				if(sim.db.carts[sim.current_id_user].listItem[i].id_product.id_product == id){
					band = 0;
					var pos = i;
				}
			}
			
			if(band){
				sim.print_message("The product to delete from cart is not available any more", sim.message_types.error);
			}
			else{
				sim.db.carts[sim.current_id_user].listItem.splice(pos, 1);
				$('#sim_product_detail_'+id).remove();
				parseInt(sim.db.carts[sim.current_id_user].number_products);
				sim.db.carts[sim.current_id_user].number_products--;
				$('#sim_label_number_products').html('<a href="javascript:;" onclick="sim.open_products_detail()" class="sim_ahref"><p><strong>'+sim.db.carts[sim.current_id_user].number_products+' PRODUCTS</strong></p></a>');
				parseInt(sim.db.carts[sim.current_id_user].array_position)
				sim.db.carts[sim.current_id_user].array_position--;
			}
			window.localStorage["carts"] = JSON.stringify(sim.db.carts);
			window.localStorage["buttons_state"] = JSON.stringify(sim.buttons_state);
			sim.getuser();
		}
	}
	else{
		sim.print_message("Enter the desired product quantity", sim.message_types.information);
	}
};

sim.empty_cart = function(){
	window.localStorage["carts"] = null;
	window.localStorage["buttons_state"] = null;
	sim.getuser();
	alert("cart empty");
};

sim.monitoring_product = function(id_product, name){
	sim.get_supermarket();
	$('#sim_monitored_sumpermarket').dialog({  
		title: "Selecting supermarket",
		height: 380,
		width: 550,
		modal: true,
		buttons: { "send": function() { 
							
							var supermarket = {};
							$( "#selectable .ui-selected").each(function() {
								supermarket.id_supermarket = ($( this ).attr('id'));
							});
							sim.send_monitoring_product(supermarket, id_product, name);
							$(this).dialog("close");},
				   "cancel": function() { $(this).dialog("close"); } }
	});
};

sim.send_monitoring_product = function(supermarket, id_product, name){
	sim.loading(true);
	var data_param = {};
	data_param.id_supermarket = supermarket.id_supermarket;
	data_param.id_product = id_product;
	$.ajax({
		url: sim.rest_uri + 'statistic',
		data : data_param,
		type: 'GET',
		success : function(data) {
			console.log(data);
			//sim.get_monitored_supermarket();
			//sim.validate_cart();
			sim.loading(false);
			if(data.statistic){
				var statistics = Object.prototype.toString.call( data.statistic ) === '[object Array]' ? data.statistic : [data.statistic];
				sim.print_statistics(statistics, name);
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
	
};

sim.print_statistics = function(statistics, name){
	console.log(statistics);
	var data_statistic = Array();
	data_statistic[0] = Array();
	data_statistic[0][0] = 'Data';
	data_statistic[0][1] = 'Price';
	for(var i = 0; i < statistics.length; i++){
		data_statistic[i+1] = Array();
		data_statistic[i+1][0] = statistics[i].month;
		data_statistic[i+1][1] = parseFloat(statistics[i].average);
	}
	
	console.log(data_statistic);
	sim.data = google.visualization.arrayToDataTable(data_statistic);

        sim.options = {
          title: ''+name,
          hAxis: {title: 'Year',  titleTextStyle: {color: '#333'}},
          vAxis: {minValue: 0}
        };
	$("#sim_monitored_product").dialog({  
		title: "Monitoring product",
		height: 540,
		width: 690,
		modal: false,
		buttons: { "OK": function() { $(this).dialog("close"); } }
	});
	var chart = new google.visualization.LineChart(document.getElementById('sim_monitored_product'));
        chart.draw(sim.data, sim.options);
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
		html += '<div class="sim_subcategory" onclick="sim.get_subcategory('+category_obj.id_category+',\''+category_obj.name+'\');">';
		html += '<div class="sim_subcategory_label">'+category_obj.name+'</div></dvi>';
		html += '<div id="toggle_'+category_obj.id_category+'" class="sim_dropdown_toggle" data-toggle="dropdown" style="display:none;"></div>';
    }
    return html;
};

var tpl_subcategory = function(subcategory, option){
	var html = '';
	if(option == 1){
		if(subcategory.productType.length <= 7){
			$('#sim_categories div .sim_dropdown_toggle').css("width", "220px");
			html += '<table border="0"><tr><th>';
			html += '<ul>';
			for(var i = 0; i < subcategory.productType.length; i++){
				var obj_subcategory = subcategory.productType[i];
				html +='<li><a href="#" onclick="sim.get_subcategory_products('+obj_subcategory.id_product_type+', \''+obj_subcategory.name+'\'); return false;">'+obj_subcategory.name+'</a></li>';
				
			}
			html +='</ul>';
			html += '</th></tr></table>';
		}
		else{
			$('#sim_categories div .sim_dropdown_toggle').css("width", "420px");
			html += '<table border="0"><tr><th class="first_colunm">';
			html += '<ul>';
			for(var i = 0; i < 7; i++){
				var obj_subcategory = subcategory.productType[i];
				html +='<li><a href="#" onclick="sim.get_subcategory_products('+obj_subcategory.id_product_type+', \''+obj_subcategory.name+'\'); return false;">'+obj_subcategory.name+'</a></li>';
				
			}
			html +='</ul>';
			html += '</th><th class="second_colunm">';
			html += '<ul>';
			for(var i = 7; i < subcategory.productType.length; i++){
				var obj_subcategory = subcategory.productType[i];
				html +='<li><a href="#" onclick="sim.get_subcategory_products('+obj_subcategory.id_product_type+', \''+obj_subcategory.name+'\'); return false;">'+obj_subcategory.name+'</a></li>';
				
			}
			html +='</ul>';
			html += '</th></tr></table>';
		}
	}
	else{
		html += '<ul>';
		for(var i = 0; i < subcategory.productType.length; i++){
			var obj_subcategory = subcategory.productType[i];
			html +='<li><a href="#" onclick="sim.get_subcategory_products('+obj_subcategory.id_product_type+', \''+obj_subcategory.name+'\'); return false;">'+obj_subcategory.name+'</a></li>';
			
		}
		html +='</ul>';
	}
	return html;
};

var tpl_product = function(product_obj){
    var html ='<div class="ui-corner-tr sim_products" id="sim_static_block_product_'+product_obj.id_product+'">';
    html += '<div class="ui-widget-header">'+product_obj.name+' '+product_obj.brand+'</div><div>';
    html +='<div class="sim_image_div"><img src="./product_images/'+product_obj.id_product+'.jpg" class="sim_image_product"/></div>';
    html += '<hr></hr><div><span class = "sim_static_block_label">'+product_obj.quantity+' '+product_obj.measure_unit+'</span></div>';
	html += '<div class="sim_product_add_button"><span id="sim_product_add_button_'+product_obj.id_product+'" class="ui-button ui-state-default ui-corner-all sim_product_add_button" onclick="sim.add_product_to_cart('+product_obj.id_product+', \''+product_obj.name+'\' , '+false+', '+false+');">Add to Cart</span></div></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.minus_quantity('+product_obj.id_product+', '+false+');"><span class="ui-icon ui-icon-circle-minus"></span></span></div>';
    html +='<div class="sim_product_quantity"><span id="sim_label_quantity_'+product_obj.id_product+'">1</span>';
	html += '<input id="sim_input_quantity_'+product_obj.id_product+'" type="hidden" value="1"/></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.plus_quantity('+product_obj.id_product+', '+false+');"><span class="ui-icon ui-icon-circle-plus"></span></span></div>';
	html += '<div class="sim_product_monitoring" id="sim_product_monitoring"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.monitoring_product('+product_obj.id_product+', \''+product_obj.name+'\');"><span class="ui-icon ui-icon-image"></span></span></div>';
    html += '</div>';
    return html;
};

var tpl_product_offer = function(product_obj){
     var html ='<div class="ui-corner-tr sim_products_offer" id="sim_static_block_product_'+product_obj.id_product.id_product+'">';
    html += '<div class="ui-widget-header">'+product_obj.id_product.name+' '+product_obj.id_product.brand+'</div>';
    html +='<div class="sim_image_div"><img src="./product_images/'+product_obj.id_product.id_product+'.jpg" class="sim_image_product"/></div>';
    html += '<hr></hr><div class="sim_header_offer">OFFER: '+product_obj.price+' €</div><div>';
	html +='<div><span class = "sim_static_block_label">'+product_obj.id_product.quantity+' '+product_obj.id_product.measure_unit+'</span></div>';
	html += '<div class="sim_product_add_button"><span id="sim_product_add_button_'+product_obj.id_product.id_product+'" class="ui-button ui-state-default ui-corner-all sim_product_add_button" onclick="sim.add_product_to_cart('+product_obj.id_product.id_product+', \''+product_obj.id_product.name+'\' , '+false+', '+false+');">Add to Cart</span></div></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.minus_quantity('+product_obj.id_product.id_product+', '+false+');"><span class="ui-icon ui-icon-circle-minus"></span></span></div>';
    html +='<div class="sim_product_quantity"><span id="sim_label_quantity_'+product_obj.id_product.id_product+'">1</span>';
	html += '<input id="sim_input_quantity_'+product_obj.id_product.id_product+'" type="hidden" value="1"/></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.plus_quantity('+product_obj.id_product.id_product+', '+false+');"><span class="ui-icon ui-icon-circle-plus"></span></span></div>';
	html += '<div class="sim_product_monitoring" id="sim_product_monitoring"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.monitoring_product('+product_obj.id_product.id_product+', \''+product_obj.id_product.name+'\');"><span class="ui-icon ui-icon-image"></span></span></div>';
    html += '</div>';
    return html;
};

var tpl_product_detail = function(product_obj){

	var html = '<div class="sim_product_detail" id="sim_product_detail_'+product_obj.id_product.id_product+'"><div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.minus_quantity('+product_obj.id_product.id_product+', '+true+');"><span class="ui-icon ui-icon-circle-minus"></span></span></div>';
    html +='<div class="sim_product_quantity"><span id="sim_label_quantity_detail_'+product_obj.id_product.id_product+'">'+product_obj.quantity+'</span>';
    html += '<input id="sim_input_quantity_'+product_obj.id_product.id_product+'" type="hidden" value="'+product_obj.quantity+'"/></div>';
    html += '<div class="sim_product_quantity"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.plus_quantity('+product_obj.id_product.id_product+', '+true+');"><span class="ui-icon ui-icon-circle-plus"></span></span></div>';
	html += '<div class="sim_product_quantity" >'+product_obj.id_product.name+'</div>';
	html += '<div class="sim_product_quantity" id="sim_product_detail_trash"><span class="ui-button ui-state-default ui-corner-all" onclick="sim.delete_from_cart('+product_obj.id_product.id_product+');"><span class="ui-icon ui-icon-trash"></span></div> </div>';
    
    
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

var tpl_supermarket_list = function(obj_supermarket){
	var id = obj_supermarket.id_supermarket;
	var html = '<h3 id="'+id+'">Supermarket: '+obj_supermarket.name+' ('+obj_supermarket.products_found+' Products found) Total: '+obj_supermarket.total+'</h3><div id="div_'+id+'"><p>Detail not available'+obj_supermarket.name+'</p></div>';
	return html;
};

var tpl_supermarket_list_detail = function(supermarket_detail){
	
	var html = '<table border="0"><tr><td>Name Product</td><td>Quantity</td><td>Price</td></tr>';
	//html += '<ul>';
	for(var i = 0; i < supermarket_detail.length; i++){
		var obj_detail = supermarket_detail[i];
		var name = ''+obj_detail.name+' '+obj_detail.brand+' '+obj_detail.measure;
		html +='<tr><td>'+name+'</td><td>'+obj_detail.quantity+'</td><td>'+obj_detail.price+'</td></tr>';
		
	}
	html += '</tr></table>';
	//console.log(html);
	return html;
};