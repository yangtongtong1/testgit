<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>总承包项目安全管理系统</title>
<link href="Content/main_header.css" rel="stylesheet" />
<link href="Content/main_content.css" rel="stylesheet" />
<link href="Content/zwj.css" rel="stylesheet" />

<style type="text/css">
        a
        {
            text-decoration: none;
        }
        li
        {
            list-style-type: none;
        }
        .x-grid-record-red table{ 
   background: #FF1224; 
   
  
} 

 tr.x-grid-record-red .x-grid-td { color: RED; }
 tr.x-grid-record-yellow .x-grid-td { background: YELLOW; }
</style>

<link rel="stylesheet" href="Extjs_4.2/resources/css/ext-all-neptune.css"/>
<script type="text/javascript" src="Extjs_4.2/bootstrap.js"></script>
<script type="text/javascript" src="Extjs_4.2/locale/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="Scripts/md5.js"></script>
<script type="text/javascript" src="Scripts/BasicInfo.js"></script>
<script src="Scripts/jQuery/jquery-1.9.0.min.js"></script>
<script src="Scripts/Ext.ux.js"></script>
<script src="Scripts/swfupload.js"></script>
<script src="Scripts/Main.js"></script>
<script src="Scripts/UploadPanels.js"></script>
<script src="Scripts/BasicInfo.js"></script>
<script src="Scripts/GoalDutyChart.js"></script>
<script src="Scripts/FileSystem.js"></script>
<script src="Scripts/GoalDutyGrid.js"></script>
<script src="Scripts/EduTrain.js"></script>
<script src="Scripts/MapGrid.js"></script>
<script src="Scripts/EmeRescue.js"></script>
<script src="Scripts/OperationControl.js"></script>
<script src="Scripts/SaftyCost.js"></script>
<script src="Scripts/HiddenTroubleSolution.js"></script>
<script src="Scripts/DayManageGrid.js"></script>
<script src="Scripts/SaftyCheck.js"></script>
<script src="Scripts/Mission.js"></script>
<script src="Scripts/RunControl.js"></script>
<script src="Scripts/OperaCon.js"></script>
<script src="Scripts/chart2.js"></script>
<script src="Scripts/MultiMediaFileGrid.js"></script>

<script type="text/javascript">
	Ext.require('Ext.ux.TabCloseMenu');
	//Ext.require('gridModel');
	//定义用户类
	function createUser(name, role, identity, unit , phone,rank,projectName)
	{
		var tmp = new Object();
		tmp.name = name;
		tmp.role = role;
		tmp.identity = identity;
		tmp.unit = unit;
		tmp.rank = rank;
		tmp.phone = phone;
		tmp.projectName = projectName;
		return tmp;
	}
	
    var name = "<%= session.getAttribute("UserName")%>";	//角色名，若角色是专家则为专家自己的姓名，若角色为机构则，roleName为机构名称
	var role = "<%= session.getAttribute("UserType")%>";	//用户角色

	//设置session，存储项目的编号，以实现选择项目的按钮
	<% session.setAttribute("ProjectNo",""); %>
	projectNo = "<%= session.getAttribute("ProjectNo")%>";
    var unit = '<%= session.getAttribute("UserUnit")%>';	//用户单位或部门
	var identity = "<%= session.getAttribute("UserId")%>";  //申请用户身份号
    var phone = '<%= session.getAttribute("UserPhone")%>';	//用户电话
    
    var projectName = "<%= session.getAttribute("ProjectName")%>";
    
    //unit = "计算机学院";
    
    var rank = '<%= session.getAttribute("UserRank")%>';	//用户级别,如本科生 硕士生 博士生 教师等
    
    //rank = "硕士生";
    
    var user = createUser(name, role, identity, unit ,phone, rank,projectName);		//创建用户对象
	var menu_Root = <%=request.getAttribute("menu_Root")%>;	//根菜单数组
	var menu_Child = <%=request.getAttribute("menu_Child")%>;	//二级菜单数组
	var menu_Leaf = <%=request.getAttribute("menu_Leaf")%>;	//三级菜单数组
	var menuArray = new Array();	//菜单侧边栏数组
	var childArray= new Array();	//二级菜单panel数组
	var rootArray = new Array();	//根菜单panel数组,根菜单panel为菜单侧边栏的item,因此rootArray.length与menuArray.length相等
	var menuName;
	var initMenu;
	var panelCenter = null;
	var panelWest = null;
	var panelEast = null;
	var gridP;
	
	
	function showGrid(record, item) {
        var container = Ext.getCmp("center-panel");
        var isLeaf = item.raw.leaf;
        var title = item.raw.text;
        var tableID = item.raw.id;
        //判断是否叶节点
        if (isLeaf) {
        	//刷新右边区域
            //Ext.getCmp('east-form').removeAll(false);
            //判断container是否已经包含这个表
            if (container.getComponent(tableID)) {
            	container.setActiveTab(tableID);
                return;
            }
   		}
        
        //根据数据库中查询的菜单ID号进入相应的系统页面，ID号若不清楚请查看数据库
                if (tableID){
        	//var gridP;
           // switchT = 20;
           
           if(  (tableID >=97 &&  tableID<=100) ||  tableID == 102 || tableID == 56 || tableID == 103  )
           {
        	   gridP = Ext.create('GoalDutyChartGrid');  
           }
           else if(tableID >=104 &&  tableID<=107 || tableID==305 || tableID >=307 &&  tableID<=312 ||tableID == 147 || tableID == 149) {
        	   gridP = Ext.create('MultiMediaFileGrid');
           }
           else if( (tableID >= 69 && tableID <= 80) || tableID == 82 || tableID == 210 || tableID == 252|| tableID == 84 ||tableID==303)
           {
           	gridP = Ext.create('FileSystemGrid');
           }
           else if(tableID == 131 || tableID == 132 || tableID == 140 || tableID == 139 ||tableID ==226 ||tableID == 227 ||tableID == 230 ||tableID ==232||tableID==148||tableID==217 ||tableID==133||tableID==141
        		   || tableID ==129 || tableID ==130 ||  tableID == 225|| tableID ==228||tableID ==145||tableID ==146||tableID ==218||tableID ==137 || (tableID >= 265 && tableID <= 266) ||tableID ==281 || tableID ==233)
           {
        	   gridP = Ext.create('OperationControlGrid');
           }
           else if(tableID == 219 ||  tableID == 220)
           {
        	   gridP = Ext.create('RunControlGrid');
           }
       	else if(tableID >= 221 &&  tableID <= 223 || tableID == 137)
       {
    	   gridP = Ext.create('OperaConGrid');
       }
       	else if(tableID == 55 || (tableID >= 57 && tableID <= 68) || tableID == 235 || tableID == 255 || tableID == 280|| tableID == 284 || tableID == 292 || tableID == 297 ||tableID==304 ||tableID==313)
        {
     	   gridP = Ext.create('BasicInfoGrid');
        }
           else if((tableID >= 115 && tableID <= 128 && tableID != 126) || (tableID >= 211 && tableID <= 213) || tableID==253 || tableID==259 || (tableID>=261 && tableID <=264)|| tableID==293 || tableID==298|| tableID==299)
       	{
       		gridP = Ext.create('EduTraninGrid');
       	}
           else if(tableID == 173 || tableID == 176 || tableID == 183 || tableID == 267 || tableID == 268||tableID==171||tableID==172||tableID==174||tableID==177||tableID==181||tableID==182)
       	{
       	   gridP = Ext.create('EmeRescueGrid');
       	}


else if( tableID == 234  ||tableID == 256|| (tableID >= 108 && tableID <= 114) || tableID == 286)
       	   {
       	   gridP = Ext.create('SaftyCostGrid');
       	   
       	   }


else if(tableID==289){
	gridP = Ext.create('ChartGrid2');
}

else if(tableID >= 152 && tableID<=157 ||  tableID == 160 ||  tableID == 163 ||tableID == 150|| tableID== 151 ||tableID ==258 ||tableID ==159||tableID ==164||tableID ==167||tableID ==166||tableID == 162 || tableID ==  90 || tableID == 91||tableID==287||tableID==288)
   	   {
   	   gridP = Ext.create('SaftyCheckGrid');
   	   }


else if(tableID == 165)
       	   {
       	   gridP = Ext.create('HiddenTroubleSolutionGrid');
       	   }
else if(tableID == 202 || tableID==204 )
   {
   	   gridP = Ext.create('MapGrid');
   }
else if(tableID >= 184 && tableID<=188||tableID==194||tableID==195 || tableID >= 189 && tableID<=191 || tableID==257|| tableID==203 || tableID==301|| tableID == 302)
{
	   gridP = Ext.create('DayManageGrid');
}
           else if( (tableID >=  81 && tableID <= 84)  || (tableID >=  85 && tableID <= 96) || tableID == 101 || tableID == 103 || tableID == 171 || tableID == 175 || (tableID >= 178 && tableID <= 179) 
        		   || tableID == 150 || tableID == 151 || tableID == 159 ||  tableID == 161 ||  tableID == 162
        		   || tableID == 129 || tableID == 111 || (tableID >= 189 &&  tableID <= 193)|| (tableID >= 196 &&  tableID <= 201)
        		   || tableID == 164 || (tableID >=  166 && tableID <= 170)
        		   || tableID == 130 || (tableID >= 218 && tableID <= 232)
        		   || tableID == 135 || (tableID >= 145 && tableID <= 149)
        		   || tableID == 209  || (tableID >= 124 && tableID <= 126) || tableID == 111 || tableID == 282|| tableID == 283 || (tableID >= 294 && tableID <= 296)|| tableID == 300)
           {
        	   gridP = Ext.create('GoalDutyGrid');
           }
           else if(tableID >=206 && tableID<=207 ||tableID >= 238 && tableID<=240|| tableID >= 247 && tableID<=248|| tableID == 251 || tableID == 291 || tableID == 290)
       	   {
       		   gridP = Ext.create('MissionGrid');
       	   };
           ;
            gridP.createGrid({
            	id:'data-grid',
            	title: title,
             	tableID: tableID,
             	userName: name,
             	userRole: role,
             	user: user,
             	projectNo:projectNo,
             	projectName:projectName,
             	pageSize: 20,
             	//strM: switchT,
             	unVisables: ['FOREIGNID'],
             	container: container,
 			});
		}
    }
	
	//定义菜单类
	Ext.define('GXGL.ProMenu',{
		extend: 'Ext.data.Model',
		fields: [
			{name:'name',  type:'string'},
			{name:'items', type: 'array'}
		]
	});
	
	for(var j=0; j<menu_Root.length; j++){
		childArray = new Array();		
		for( var i=0, k=0; i<menu_Child.length; i++){
			if(menu_Child[i][1] == menu_Root[j]){
				
				var url = encodeURI('MenuAction!getleaf?name=' + menu_Child[i][0] + '&role=' + role);
			//	var tempurl	= encodeURI(url);			
				var child = Ext.create('Ext.panel.Panel', {
		        	title: menu_Child[i][0],
		         	layout: 'fit',
		         	items: {
		             	xtype: 'treepanel',
		             	rootVisible: false,
		             	autoScroll: true,
		             	lines: false,
		             	useArrows: true,
		             	store: Ext.create('Ext.data.TreeStore', {
		            		proxy: {
		            			type: 'ajax',
		            			url: url,
		            		},
		            		fields:[{
		            			name:'id',
		            			type:'int'
		            		},{
		            			name:'text',
		            			type:'string'
		            		}]
		             	}),
		             	listeners: {
		                	'itemclick': function (record, item) {
		                    	showGrid(record, item);
		                 	}
		             	}
		         	}
		     	})		     	
		     	childArray[k++] = child;
			}	
		}
		if(j == 0){      
			initMenu = childArray;
			menuName = menu_Root[j]
		}
		rootArray[j] = childArray;
	}

	
	//动态从数据库获取menu，来自定义菜单
	for(var i=0; i<menu_Root.length; i++){
		var customMenu = Ext.create('GXGL.ProMenu', {
			name: menu_Root[i],
			items: rootArray[i]
	    });
		menuArray[i] = customMenu;
	}
	
	Ext.onReady(function(){
		Ext.Loader.loadScript("Extjs_4.2/locale/ext-lang-zh_CN.js");	//解决日期空间中文bug
		Ext.QuickTips.init();	//启动tooltip效果
		Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));
		

		Ext.getDoc().on("contextmenu", function(e){
				    e.stopEvent();
				});

		function change(menu, title, order) {
             //刷新中央区域
        	panelCenter.removeAll(true);
             //刷新右边区域
           
         	//panelEast.removeAll(false);
//             panelEast.add(textSearch);
//             panelEast.add(btnSearch);
             //更新左边菜单
            panelWest.removeAll(false);
            panelWest.add(menu.get("items"));
            panelWest.setTitle(title);
            var firstMenu = menu.get("items")[order];
            firstMenu.expand(false);
            panelWest.expand(true);
         };
         //上Panel
         var panelNorth = Ext.create('Ext.Component', {
         	region: 'north',
           	height: 100,
            contentEl: 'main_header',
            listeners: {
            	afterrender: function () {
            		var btnColors = {
                            active: '#E0EEEE',
                            inactive: 'transparent'
                    };
            		//鼠标移动事件
                    $(".nav").hover(function () {
                        $(this).css("background-color", "#E0EEEE");
                    }, function () {
                        if ($(this).hasClass('active')) {
                            $(this).css("background-color", btnColors.active);
                        } else {
                            $(this).css("background-color", btnColors.inactive);
                        }
                    });
            		
            		//鼠标点击事件
            		$(".nav").click( function () {
                    	var clickName = $(this).text();
                    	for(i=0; i<menuArray.length; i++){
                        	if(menuArray[i].get("name") == clickName){
                        		change(menuArray[i], clickName, 0);	
                        	}
                        }   	              
                    });
            	}
            }
        });
        
         
         

         


         
//         var panelEast = Ext.create("Ext.FormPanel", {
//              region: 'east',
//              //stateId: 'navigation-panel',
//              id: 'east-form',     // see Ext.getCmp() below
//              title: '查看数据',
//              titleAlign: 'center',
//              split: true,
//              width: 200,
//              collapsible: true,//可以折叠
//              collapsed: false,//表格两格边框不能合二为一
//              animCollapse: true,
//              margins: '0 5 0 0',
//              bodyPadding: 5,
//              buttonAlign: 'center',
//              layout: 'fit',
//              defaults: {
//                  anchor: '100%',
//                  height: 30,
//                  labelWidth: 120
//              },
//              autoScroll: true,
//              defaultType: 'textfield',
//              items:[btnSearch,textSearch]
//          });
        
         //中间Panel
         panelCenter = Ext.create("Ext.TabPanel", {
         	xtype: 'tabpanel',
            id: 'center-panel',
            layout: 'fit',
            region: 'center',               
            deferredRender: false,
            plugins: Ext.create('Ext.ux.TabCloseMenu', {
            	closeTabText: '关闭当前页',
            	closeOthersTabsText: '关闭其他页',
            	closeAllTabsText: '关闭所有页'
             })
         });
         
         //左Panel
         panelWest = Ext.create("Ext.Panel", {
             region: 'west',
             stateId: 'navigation-panel',
             id: 'west-panel', 
             title: menuName,
             titleAlign: 'center',
             split: true,
             width: 200,
             autoScroll: true,
             collapsible: true,	//是否可以收缩
             collapsed:false,	//是否收缩
             animCollapse: true,
             margins: '0 0 0 5',
             layout: {
                 type: 'accordion',
                 titleCollapse: true,
                 animate: true,
                 activeOnTop: false,	//默认是false按序切换，值为true时，top会无序切换
             },
             items: initMenu
         });
         

          
         
         var viewport = Ext.create('Ext.Viewport', {
             id: 'jjxm-ywgl',
             layout: 'border',
             items: [
                 panelNorth, panelWest, panelCenter
             ]
         })
         
         //如果角色为院领导，showgrid
         var loginpeople = "<%= session.getAttribute("UserLoginType")%>";
         if(loginpeople == "院领导") {
        	 
        	 var container = Ext.getCmp("center-panel");
        	 
        	 gridP = Ext.create('MapGrid');
        	 
        	 gridP.createGrid({
             	id:'data-grid',
             	title: "现场监控",
              	tableID: 204,
              	userName: name,
              	userRole: role,
              	user: user,
              	projectNo:projectNo,
              	projectName:projectName,
              	pageSize: 20,
              	//strM: switchT,
              	unVisables: ['FOREIGNID'],
              	container: container,
  			});
         }
         
/* 下面这部分，新建一个项目管理的全局按钮，负责筛选项目id，过滤数据
	暂时将选择项写死，后期需要从数据库读取item生成选择项
*/
            
            var projectStore = Ext.create('Ext.data.Store', {
                model: Ext.define('ProjectList', {
                    extend: 'Ext.data.Model',
                    fields: [
                        {type: 'int', name: 'ID'},
                        {type: 'string', name: 'No'},
                        {type: 'string', name: 'Name'},
                        {type: 'string', name: 'Manager'},
                        {type: 'string', name: 'Progress'}
                    ]
                }),
                proxy: {
                    type: 'ajax',
                    url: encodeURI('BasicInfoAction!getProjectNameListDef?userName=' + name + "&userRole=" +role),
                    reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                        type: 'json', //返回数据类型为json格式
                        root: 'rows',  //数据
                        totalProperty: 'total' //数据总条数
                    }
                },
                autoLoad: true
            });
            
	  var getSel = function (grid) {
          selRecs = [];  //清空数组
          keyIDs = [];
          keyNames = [];
          selRecs = grid.getSelectionModel().getSelection();
          for (var i = 0; i < selRecs.length; i++) {
          	keyIDs.push(selRecs[i].data.ID);
          }
          if (selRecs.length === 0) {
              Ext.Msg.alert('警告', '没有选中任何记录！');
              return false;
          }
          return true;
      };
      
    //建立工具栏
      var tbar = new Ext.Toolbar({
          defaults: {
              scale: 'medium'
          }
      })
    
    
    dataStore = projectStore;
    
    var column = [
     		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},		
     		    { text: '项目编号', dataIndex: 'No', align: 'center', width: 100},
   	            { text: '项目名称', dataIndex: 'Name', align: 'center', width: 600},
   	            { text: '项目经理', dataIndex: 'Manager', align: 'center', width: 150},
   	          { text: '项目进度', dataIndex: 'Progress', align: 'center', width: 150},
         ]
    /* stateGroup = Ext.create('Ext.form.ComboBox', {
    	store: ['开工准备阶段','土建施工阶段','安装调试阶段','试运行阶段','收尾移交阶段','完工总结阶段'],
    	listeners: {   
			render: function(t, eOpts) {
				t.setValue(t.getStore().data.get(0))
			},
			change: function(combo, records, eOpts) {
				// 显示符合要求的
				//alert(combo.getValue());
				dataStore.clearFilter();
				dataStore.filter('Progress',combo.getValue());        	
				dataStore.load();
				
			}
		}
    }) */
    tbar.add({
		xtype: 'radiogroup',
		itemId: 'grid_type',
		width: 1000,
		items: [{
			xtype: 'radio',
			boxLabel: '全部',
			name: 'PType',
			inputValue: '',
			labelWidth: 30,
			checked: true
		},{
			xtype: 'radio',
			boxLabel: '开工准备阶段',
			name: 'PType',
			labelWidth: 280,
			inputValue: '开工准备阶段'
			
		},{
			xtype: 'radio',
			boxLabel: '在建阶段',
			name: 'PType',
			labelWidth: 280,
			inputValue: '在建阶段'
		},{
			xtype: 'radio',
			boxLabel: '收尾移交阶段',
			name: 'PType',
			labelWidth: 180,
			inputValue: '收尾移交阶段'
		},{
			xtype: 'radio',
			boxLabel: '完工总结阶段',
			name: 'PType',
			labelWidth: 180,
			inputValue: '完工总结阶段'
		}],
		listeners: {
        	'change' : function(obj) {
        		var PType = obj.lastValue['PType'];
        		dataStore.clearFilter();
				dataStore.filter('Progress',PType);        	
				dataStore.load();
        	}
        }
	});
      
      /*余明星修改   新修改
      *
      */
	  createForm = function (config) {
      	forms = Ext.create('Ext.form.Panel', {
      		minWidth: 200,
      		minHeight: 100,
      		bodyPadding: config.bodyPadding,
              buttonAlign: 'center',
              layout: config.layout,
              defaults: config.defaults,
              autoScroll: true,
              frame: false,
              html: config.html,
              defaultType: config.defaultType,
              items: config.items,
              buttons: [{
              	text: '取消',
              	handler: function(){
              		this.up('window').close();
              	}
              },{
              	
            	text: '确定',
            	listeners: 
            	{	'click':function() {
              		if(forms.form.isValid()){
              			switch (config.action){
              				
              				case "projectView":{
              					if(getSel(gridProject)){
              						var temp1 = gridProject.getSelectionModel().getSelection();
              						if(temp1.length > 1){
              							Ext.Msg.alert("提示信息", "只能选择一个项目进行查看！")
              						}else{
              							//alert(temp1[0]);
              							projectName = temp1[0].get('Name');
              							//config.url = 'PPAction!AppointAssess?ID=' + param + '&expert='+temp1[0].data.ExpertName;
              							var protext = Ext.getCmp("proLabel");
                                		protext.setText(projectName);
                                		
                                		this.up('window').close();
                                		//this.up('window').close();
                                		
                                		
                                		//判断角色 加载菜单 Ajax存session 然后改变location
                                		//MenuAction?
                                		
                                		if(role == "全部项目"){
                                		function createXmlHttp(){//创建XMLHTTP
                                			var xmlHttp;
                                			try{	//Firefox、Safari
                                				xmlHttp = new XMLHttpRequest();
                                			}
                                				catch(e){
                                					try{// IE
                                						xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
                                					}
                                						catch(e){
                                							try{
                                								xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
                                							}
                                								catch(e){ }
                                						}
                                				}
                                				return xmlHttp;
                                		}
                                		function savePname(){

                        					//1.创建异步交互对象
                        					var xhr = createXmlHttp();
                        					
                        					//2.设置监听
                        					xhr.onreadystatechange = function(){
                        						if(xhr.readyState == 4 && xhr.status == 200){
                        							/* var projectName = "${sessionScope.projectName}";  */
                        							
                        					}

                        				}
                        					
                        					//3.打开链接
                        					
                        					xhr.open("POST","${pageContext.request.contextPath}/MenuAction_savePname?time="+new Date().getTime()+"&projectName="+projectName,false);
                        					xhr.setRequestHeader("content-type","text/html;charset=UTF-8");
                        					//4.发送
                        					 xhr.send(null);
                        				} 
                                		savePname();	
                                		/* var pn = "${sessionScope.projectName}"; */
                                		
                                		location.href="MenuAction_login"; }	
              						}
              					}
              					break;
              				}
              				
              				
              				default:
              					break;
              			}
              			
              	}}}}
],
              renderTo: Ext.getBody()
      	});
      };
     
    
    
    
    
    

var showWin = function (config) {
	var width = 1100;
	var height = 700;
	var defaultCng = {
            modal: true,  //设定为模态窗口
            plain: true,
            width: width,
            height: height,
            layout: 'fit',
            titleAlign: 'center',
            closable: true,		//可关闭的
            closeAction: 'close',	//关闭动作，有hide、close、destroy
            draggable: true,
            resizable: true,
            maximizable: true,
            constrain: true,
            listeners: {
                'beforeclose': function (me) {
                	var centerPanel = Ext.getCmp("center-panel");
              		 centerPanel.removeAll(true);
            		/* DeleteFile(config.winId);
                    uploadPanel.store.removeAll(); */
                }
            }
        };
        config = $.extend(defaultCng, config);
        var win = new Ext.Window(config);
        win.show();
        return win;
    };
    
var projectViewH = function() {
		
		createForm({
			autoScroll: true,
			action: 'projectView',
			bodyPadding: 5,
 	       	items: gridProject
		});	
		//bbar.moveFirst();	//状态栏回到第一页
        showWin({ winId: 'projectView', title: '项目查看', items: [forms]});

}
    
      
      
	/*余明星修改     新修改
	*
	*/
    var gridProject = Ext.create('Ext.grid.Panel', {       
		selModel: { selType: 'checkboxmodel'},   //选择框
		store: dataStore,
		stripeRows: true,
		columnLines: true,
		tbar: tbar,
		//multiSelect : false,
		columns:  column,
        
        viewConfig: {
	    	loadMask: false,
            loadMask: {                       //IE8不兼容loadMask
            	msg: '正在加载数据中……'
            }
        },
        listeners: {
        	'celldblclick':function(self, td, cellIndex, record, tr, rowIndex) {
        		
        		//Ext.getCmp(btnid).fireEvent('click');
        		var record = projectStore.getAt(rowIndex);
        		projectName = record.get('Name');
					//config.url = 'PPAction!AppointAssess?ID=' + param + '&expert='+temp1[0].data.ExpertName;
				var protext = Ext.getCmp("proLabel");
        		protext.setText(projectName);
        		
        		this.up('window').close();
        		

        		//判断角色 加载菜单 Ajax存session 然后改变location
        		//MenuAction?
        		
        		
        		if(role == "全部项目"){
        		function createXmlHttp(){//创建XMLHTTP
        			var xmlHttp;
        			try{	//Firefox、Safari
        				xmlHttp = new XMLHttpRequest();
        			}
        				catch(e){
        					try{// IE
        						xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
        					}
        						catch(e){
        							try{
        								xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
        							}
        								catch(e){ }
        						}
        				}
        				return xmlHttp;
        		}
        		function savePname(){

					//1.创建异步交互对象
					var xhr = createXmlHttp();
					
					//2.设置监听
					xhr.onreadystatechange = function(){
						if(xhr.readyState == 4 && xhr.status == 200){
							/* var projectName = "${sessionScope.projectName}";  */
							
					}

				}
					
					//3.打开链接
					
					xhr.open("POST","${pageContext.request.contextPath}/MenuAction_savePname?time="+new Date().getTime()+"&projectName="+projectName,false);
					xhr.setRequestHeader("content-type","text/html;charset=UTF-8");
					//4.发送
					 xhr.send(null);
				} 
        		savePname();	
        		/* var pn = "${sessionScope.projectName}"; */
        		
        		location.href="MenuAction_login"; }	
        	}
        }	
    });
	
	var toolbar;
	var tblabel;
	
	
	  
	 /*余明星修改   新修改
    *
    */
    setTimeout(function() {
            
            tblabel = Ext.widget({
                xtype: 'toolbar',
                style: 'background-color: transparent;', 
                border: false,
                rtl: false,
                shadow: false,
                id: 'label-toolbar',
                floating: true,
                fixed: true,
                margin : 0,
                preventFocusOnActivate: true,
                items: [{
                	xtype: 'label',
                	id:'proLabel',
                    text:"${sessionScope.ProjectName}",
                    style:'border-width:0px;font-size:22px;color:#F8F8FF;font-family:"华文隶书,隶书";'             
                	}]
            });
            
            //隐藏projectName
                    
            tblabel.show();         //先显示 后隐藏
           
            if(role=="全部项目")
            	tblabel.hide();
            tblabel.alignTo(
                document.body,
                Ext.optionsToolbarAlign || 'tl-tl',
                [
                    460, 6-(document.body.scrollTop || document.documentElement.scrollTop)
                ]
            );
    	
        toolbar = Ext.widget({
            xtype: 'toolbar',
            style: 'background-color:transparent;', 
            border: false,
            rtl: false,
            id: 'options-toolbar',
            floating: true,
            fixed: true,
            shadow: false,
            margin: 1,
            preventFocusOnActivate: true,
            draggable: {
                constrain: true
            },
            items: [{
                xtype: 'button',
                id: 'AllBtn',
                text: '全部项目',
                renderTo: Ext.getBody(),
                handler: function(){ 
                	//获取现在的角色
                	//var nrole = "${sessionScope.UserType}";
                
					if(role != "全部项目")
                	location.href='MenuAction_all'; 	
                }
            },  {
                xtype: 'button',
                id: 'SettingBtn',
                text: '项目查看',
                renderTo: Ext.getBody(),
                handler: projectViewH
            }, {
                xtype: 'button',
                text: '退出系统',
                renderTo: Ext.getBody(),
                handler: function() {
                	Ext.MessageBox.confirm("提示", "确认退出？", function(btn) {  
                        if(btn == 'yes')
                       	 { 	
                       	 Ext.Ajax.request({
                             method : 'POST',
                             url: 'BasicInfoAction!DestroySession',
                             success: function(){
                        
                            	 location.href='login.html';
                           
                             }
                       	 });
                       	 }
                    }) } 
            }],

            // Extra constraint margins within default constrain region of parentNode
            constraintInsets: '0 -' + (Ext.getScrollbarSize().width + 2) + ' 0 0'
        });
       // toolbar.show();
        
        //“全部项目” 对项目部人员隐藏
         var logintype = "<%= session.getAttribute("UserLoginType")%>";   
        // var Allbtn = Ext.getDom("AllBtn");
         toolbar.show();         //先显示 后隐藏
         
         
         if(logintype=="项目部人员")
        	 
        	Ext.getCmp('AllBtn').hide();
         
        toolbar.alignTo(
            document.body,
            Ext.optionsToolbarAlign || 'tr-tr',
            [
                (Ext.getScrollbarSize().width - 15) * (Ext.rootHierarchyState.rtl ? 1 : -1),
                -(document.body.scrollTop || document.documentElement.scrollTop)
            ]
        );

        window.onresize = function(){
            toolbar.alignTo(
                document.body,
                Ext.optionsToolbarAlign || 'tr-tr',
                [
                    (Ext.getScrollbarSize().width - 15) * (Ext.rootHierarchyState.rtl ? 1 : -1),
                    -(document.body.scrollTop || document.documentElement.scrollTop)
                ]
            );
        }

        var constrainer = function() {
            toolbar.doConstrain();
        };

        Ext.EventManager.onWindowResize(constrainer);
        toolbar.on('destroy', function() { 
            Ext.EventManager.removeResizeListener(constrainer);
        });
    }, 100);
});
</script>
</head>
<body>
	<div id="main_header" class="x-hide-display">
        <div class="jjpro-header">
            <div class="jjpro-logos" style="background-color:transparent;">
                 <img src="Images/Main_header/logos_s.png" height="57" />
            </div>
        </div>
        
        <div id="outer-header">
            <header id="header">
                <nav id="nav">
                    <ul id="ul" class="views">
                    	<%=request.getAttribute("menu_li") %>
                    </ul>
                </nav>
                <div style="position:absolute; left:1020px; float:left; width:250px" id="combo"></div>
            </header>
        </div>
    </div>
</body>
</html>