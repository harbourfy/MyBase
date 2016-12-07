function addMenu(){
	var url = ctx + "/inner/menu/edit";
	layer.open({
		type: 2,
		title: '添加菜单权限信息',
		shadeClose: true,
		shade: 0.8,
		area: ['550px', '400px'],
		content: url
	});
}


function modifyConfig(id){
	var url = ctx + "/inner/config/edit?id=" + id;
	layer.open({
		type: 2,
		title: '编辑基础配置信息',
		shadeClose: true,
		shade: 0.8,
		area: ['550px', '400px'],
		content: url
	});
}

function addSameConfig(id){
	var url = ctx + "/inner/config/addSame?id=" + id;
	layer.open({
		type: 2,
		title: '新增基础配置信息',
		shadeClose: true,
		shade: 0.8,
		area: ['550px', '400px'],
		content: url
	});
}
function viewConfig(id){
	var url = ctx + "/inner/config/view?id=" + id;
	layer.open({
		type: 2,
		title: '查看基础配置详细信息',
		shadeClose: true,
		shade: 0.8,
		area: ['550px', '400px'],
		content: url
	});
}

function deleteMenu(id){
	layer.confirm("确定删除该菜单信息？", function(){
		var url = ctx + "/inner/menu/delete?id=" + id;
		$.ajax({
	        type: "get",
	        url: url,
	        success: function() {
	        	layer.alert('删除成功', function(index){
	      		  window.location.reload();
	    		});
	        }
	    });
	})
	
}

function addSuccess(){
	window.location.href = ctx + "/inner/menu/list";
}

function queryMenu(page){
	var url = ctx + "/inner/menu/list?page=" + page;
	var menuName = $("#txtmenuName").val();
	if(isNotBlank(menuName)){
		url += "&menuName=" + menuName;
	}
	
	var menuTypeName = $("#menuTypeSelect").find("option:selected").text();
	if(menuTypeName != "请选择菜单类型"){
		var menuType = $("#menuTypeSelect").find("option:selected").val();
		url += "&menuType=" + menuType;
	}
	
	
	var menuUrl = $("#txtmenuUrl").val();
	if(isNotBlank(menuUrl)){
		url += "&menuUrl=" + menuUrl;
	}
	
	window.location.href = url;
}
function isNotBlank(args){
	var result = false;
	if(args != "" && args != null && args != undefined){
		result = true;
	}
	
	return result;
}
   
$(function(){
	initTree();
})

function initTree() {
   $.ajax({
	   type : 'get',
	   url : ctx + "/inner/menu/treeData",
	   success: function(data){
		   $('#tree').treeview({data: data});
	   }
   })  
   
   
   
   $("#btnView").click(function (e) {
	   var arr = $('#tree').treeview('getSelected');
	   var menuId = "";
	   for (var key in arr) {
		   menuId = arr[key].id;
	   }
	   
	   if(isNotBlank(menuId)){
		   var url = ctx + "/inner/menu/view?id=" + menuId;
			layer.open({
				type: 2,
				title: '查看菜单详细信息',
				shadeClose: true,
				shade: 0.8,
				area: ['550px', '400px'],
				content: url
			});
	   }else{
		   layer.alert("请选择菜单"); 
	   }
   });
   
   $("#btnAddSub").click(function (e) {
	   var arr = $('#tree').treeview('getSelected');
	   var menuId = "";
	   for (var key in arr) {
		   menuId = arr[key].id;
	   }
	   
	   if(isNotBlank(menuId)){
		   var url = ctx + "/inner/menu/isSystem?id=" + menuId;
		   $.ajax({
			   type: "get",
			   url: url,
			   success: function(isSystem){
				   alert(isSystem)
			   }
			   
		   });
	   }else{
		   layer.alert("请选择父菜单"); 
	   }
   });
        
}
 

