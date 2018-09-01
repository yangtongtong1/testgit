Ext.define('MapGrid', {
    requires: [
        'Ext.data.Model',
        'Ext.grid.Panel',
        'Ext.window.MessageBox',
        'Ext.tip.*',
        'Ext.form.field.ComboBox',
        'Ext.form.FieldSet',
        'Ext.tip.QuickTipManager',
        'Ext.data.*'
    ],
    createGrid: function (config) {  
		var container = config.container;
		var tableID = config.tableID; 
		var title = config.title;
		var projectName = config.user.role === '全部项目' ? config.user.role : config.projectName;
		console.log('projectName:', projectName);
		var gridDT;
		var column;
		var store;
		var forms;
		var queryURL;
		var findStr;
		var psize = config.pageSize;  
		var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';	//必填项红色星号*
		var panel = Ext.create('Ext.panel.Panel', {
	         itemId: tableID,
	         title: title,
	         layout: 'fit',
	         closable: true,
	         autoScroll: true
	     });  	
		
		  var getSel = function (grid) 
	        {
	            selRecs = [];  //清空数组
	            keyIDs = [];
	            selRecs = grid.getSelectionModel().getSelection();
	            for (var i = 0; i < selRecs.length; i++) 
	            {	            	
	            	keyIDs.push(selRecs[i].data.ID);	       	  
	            }
	            if (selRecs.length === 0) 
	            {
	                Ext.Msg.alert('警告', '没有选中任何记录！');
	                return false;
	            }
	            return true;
	        };
		
		  var Editor_monitor = [{
		    	fieldLabel: "编号",		//编号框用于绑定数据ID，隐藏不显示
		        name: "ID",
		        labelAlign: 'right',
		        hidden: true,
		       	hiddenLabel: true
	        },{
		        xtype:'combo',
		    	fieldLabel: "所属项目部",
		        name: "projectName",
	        	store: Ext.create('Ext.data.Store', {
	            	fields: [
	       	         	{ name: 'ID'},
	                    { name: 'Name'}
			           ],
			           pageSize: 1000,  //页容量1000条数据
			           proxy: {
			               type: 'ajax',
			               url: encodeURI('EduTrainAction!getProjectListDef'),
			               reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
			                   type: 'json', //返回数据类型为json格式
			                   root: 'rows',  //数据
			                   totalProperty: 'total' //数据总条数
			               }
			           }
			    }),
			    displayField: 'Name',
	    		valueField: "Name",
	            labelAlign: 'right',
	            anchor:'100%'
	        },{
	        	fieldLabel:"监控点名称:",
	        	name:"monitorName",   
	        	labelAlign: 'right',
	        	allowBlank:false,
	        	afterLabelTextTpl: required
		    },{
	        	fieldLabel:"监控点经度:",
	        	name:"longitude",   
	        	labelAlign: 'right',
	        	allowBlank:false,
	        	afterLabelTextTpl: required
		    },{       
		    	fieldLabel: "监控点纬度:",
		       	labelAlign: 'right',
		        name:"latitude",
		        allowBlank:false,
		    	afterLabelTextTpl: required
		    },{
		        fieldLabel:"登录用户名:",
		        name:"userName",
		       	labelAlign: 'right',
		       	allowBlank:false,
		       	afterLabelTextTpl: required
		    },{
		       	fieldLabel: "登录密码:",
		       	name: "userPwd",
		       	allowBlank:false,
		       	labelAlign: 'right',
		       	afterLabelTextTpl: required
		    },{
		       	fieldLabel: "摄像机IP:",
		       	name: "ipaddress",
		       	allowBlank:false,
		       	labelAlign: 'right',
		       	afterLabelTextTpl: required
		    },{
		       	fieldLabel: "PC端口号:",
		       	name: "port",
		       	allowBlank:false,
		       	labelAlign: 'right',
		       	afterLabelTextTpl: required
		    },{
		       	fieldLabel: "移动端端口号:",
		       	name: "mobilePort",
		       	allowBlank:false,
		       	labelAlign: 'right',
		       	afterLabelTextTpl: required
		    },  
		    {
		       	fieldLabel: "摄像机频道:",
		       	name: "channel",
		       	allowBlank:false,
		       	labelAlign: 'right',	
		       	afterLabelTextTpl: required
		    },{	
		    	fieldLabel: "备注:",
			    xtype: 'textareafield',
			    name:"remarks",
				labelAlign: 'right',
				style: 'margin:0',
				height:90,
			    flex:1
		    }]
		
	    var addMonitor = function(){
        	flag=false;
        	createform(flag,"新增监控点",Editor_monitor);	
        }
		  
		var setCenter = function(){
			 var selRecs = gridDT.getSelectionModel().getSelection();
			 Ext.Ajax.request({                            //获取中心点
					async: false, 
			        method : 'POST',
			        url: encodeURI('MapAction!setCenterPoint'),
			        params:{
			        	ID:selRecs[0].data.ID
			        },
			        success: function(response){
			        	Ext.Msg.alert('提示','设置成功！');
			        }
			     });
			 gridDT.store.reload();
		}
		  
		var editH = function(){
			flag = true;
			createform(flag,"编辑监控点",Editor_monitor);
		}
		var createform = function(flag,title,items){		
			var width;
			var height;
			var url;
			if(tableID == 202){
				width = 400;
				height = 500;
				if(flag == false){
					url = 'MapAction!addMonitor';
				}
				else{
					url = 'MapAction!editMonitor';
				}
			}
			forms = Ext.create('Ext.form.Panel', {
 				minWidth: 200,
 				minHeight: 100,
 				bodyPadding: 5,
 				buttonAlign: 'center',
 			    layout: 'anchor',
 		        defaults: { anchor: '100%', height: 30, labelWidth: 80 },
 		        autoScroll: true,
 		        frame: false,
 		        defaultType:'textfield',
 		        items: items,
 		        buttons: [{
 		        	text: '确定',
 		        	handler: function() {
 		        		if(forms.form.isValid()){
 		        			forms.form.submit({
 		        				clientValidation: true,
 		        				url:encodeURI(url),
 		  	                	success: function(form, action){
 		  	                		bbar.moveFirst();
 		  	                	},
 		  	                	failure: function(form, action){
 		  	                		alert("编辑失败！");
 		  	                	}
 		                	})
 		                	this.up('window').close();
 		                }
 		        		else{
 		        			Ext.Msg.alert('警告','请完善信息！');
 		        		}
 		                
 		        	}
 		        },{
 		        	text: '取消',
 		        	Align:'left',
 		        	handler: function(){
 		        		this.up('window').close();
 		        	}
 		        },{
 		        	text: '重置',
 		        	handler: function(){
 		        		forms.form.reset();
 		        	}
 		        }],
 		        renderTo: Ext.getBody()
 		   })
 		   if(flag == true){	
 			  var selRecs = gridDT.getSelectionModel().getSelection();
 	           forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据             
 		   }
			Ext.create('Ext.window.Window', {
                title: title,
                height: height,
                width: width,
                modal: true,  //设定为模态窗口
                plain: true,
                layout: 'fit',
                titleAlign: 'center',
                closable: true,		//可关闭的
                closeAction: 'close',	//关闭动作，有hide、close、destory
                draggable: true,
                resizable: true,
                maximizable: true,
                constrain: true,
                items:forms
                }).show();
		}
		 var searchH = function (){
	        	var keyword = tbar.getComponent("keyword").getValue().trim();
	        	findStr = keyword;
	        	store.getProxy().url = encodeURI(queryURL + '?findStr=' + findStr);  
	        	btnSearchR.enable();
	        	store.load({params: { start: 0, limit: psize }});
	        	bbar.moveFirst();
	        }
        var searchR = function(){
        	var keyword = tbar.getComponent("keyword").getValue().trim();
        	findStr = findStr + "," + keyword;
        	store.getProxy().url = encodeURI(queryURL + '?findStr=' + findStr);   
        	store.load({params: { start: 0, limit: psize }});
        	bbar.moveFirst();
        }
		
        var deleteH = function() 
        {
        	if(getSel(gridDT))
        	{
        		Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) 
        		{
    				if (buttonID === 'yes'){			
    					$.getJSON(encodeURI('MapAction!deleteMonitor'),
    							{id: keyIDs.toString()},	//Ajax参数
                                function (res) {
    								if (res.success) {
    									bbar.moveFirst();	//状态栏回到第一页
                                    } else {
                                    	Ext.Msg.alert("信息", res.msg);
                                    }
                                });				
                     }
    			})
        	}
        
        }
        
		var textSearch = Ext.create('Ext.form.TextField', {
         	itemId: 'keyword',
            emptyText: '请输入查询内容',
            width: 150,
            height: 25,
            listeners: 
            {  
                specialkey: function(field,e)
                {    
                    if (e.getKey()==Ext.EventObject.ENTER)
                    {   
                    	searchH();                
                    }  
                } 
            }

        })
		
 
	      var btnSearch = Ext.create('Ext.Button', {
	    	width: 55,
	    	height: 32,
	    	text: '查询',
	        icon: "Images/ims/toolbar/search.png",
	        handler: searchH
        })
        var btnSearchR = Ext.create('Ext.Button', {
        	width: 105,
        	height: 32,
        	text: '在结果中查询',
            icon: "Images/ims/toolbar/search.png",
            disabled: true,
            handler: searchR
        })
        
		var btnAddMonitor = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '新增',
            icon: "Images/ims/toolbar/group.png",
            handler: addMonitor
        })
         var btnEdit = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '编辑',
        	disabled:true,
            //tooltip: '编辑一条记录',
           	icon: "Images/ims/toolbar/edit.png",
            handler: editH
        })
        var btnDel = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '删除',
        	disabled:true,
           	icon: "Images/ims/toolbar/delete.gif",
            handler: deleteH
        }) 
        
         var btnSetCenterPoint = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '设为中心点',
        	disabled:true,
           	icon: "Images/ims/toolbar/upload.png",
            handler: setCenter
        }) 
        
		
	    var tbar = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        })
		var store_Monitor = Ext.create('Ext.data.Store', {  //监控列表store
        	fields: [
        	         { name: 'ID'},
                     { name: 'monitorName'},
                     { name: 'longitude'},
                     { name: 'latitude'},
                     { name: 'userName'},
                     { name: 'userPwd'},
                     { name: 'ipaddress'},
                     { name: 'port'},
                     { name: 'mobilePort'},
                     { name: 'channel'},
                     { name: 'remarks'},
                     { name : 'defaultpos'},
                     { name : 'projectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('MapAction!getMonitorListInPage?projectName=' + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });	
		if(tableID == 204){
			sessionStorage.projectName=projectName;
			gridDT = Ext.create('Ext.panel.Panel', {               //百度地图Grid
				html:' <iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="Map.html"> </iframe>'  
			})
		}
		
		else if(tableID == 202){
			if(tableID == 202){
				store = store_Monitor;
				store.load();
				tbar.add(textSearch);
				tbar.add(btnSearch);
				tbar.add(btnSearchR);
				tbar.add(btnAddMonitor);
				tbar.add(btnEdit);
				tbar.add(btnDel);
				tbar.add(btnSetCenterPoint);
				queryURL = 'MapAction!searchMonitor';
			}
			var bbar = Ext.create('Ext.PagingToolbar',{
	            displayInfo: true,
	            emptyMsg: "没有数据要显示！",
	            displayMsg: "当前为第{0}--{1}条，共{2}条数据", //参数是固定的，分别是起始和结束记录数、总记录数
	            store: store,
	            items: ['-', {
	                xtype: 'combo',
	                fieldLabel: '显示行数',
	                labelWidth: 65,
	                width: 130,
	                store: [10, 20, 50, 100, 200, '全部'],
	                value: psize,
	                forceSelection: true,
	                listeners: {
	                    'collapse': function (field) {
	                        var size = field.lastValue;
	                        psize = size === "全部" ? 100000 : size;
	                        Ext.apply( store, { pageSize: psize });
	                        //dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store,start会自动增加，增加为psize
	                        this.ownerCt.moveFirst();	//选择了psize后，跳回第一页
	                    }
	                }
	            }]
	        });	
			column = [
	            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
	            	    { text: '编号', dataIndex: 'ID', align: 'center', width: 60},
	            	    { text: '监控点名称', dataIndex: 'monitorName', align: 'left', width: 110},
	            	    { text: '监控点经度', dataIndex: 'longitude', align: 'center', width: 110},
	            	    { text: '监控点纬度', dataIndex: 'latitude', align: 'center', width: 110},
	            	    { text: '摄像机用户名', dataIndex: 'userName', align: 'center', width: 140},
	            	    { text: '摄像机密码', dataIndex: 'userPwd', align: 'center', width: 110},
	            	    { text: '摄像机IP', dataIndex: 'ipaddress', align: 'center', width: 100},
	            	    { text: 'PC端口号', dataIndex: 'port', align: 'center', width: 120},
	            	    { text: '移动端口号', dataIndex: 'mobilePort', align: 'center', width: 120},
	            	    { text: '摄像机频道', dataIndex: 'channel', align: 'center', width: 120},
	            	    { text: '所属项目部', dataIndex: 'projectName', align: 'center', width: 200}
	            	]
			gridDT = Ext.create('Ext.grid.Panel', {       
	    		selModel: new Ext.selection.CheckboxModel({ selType: 'checkboxmodel' }),   //选择框
	    		store: store,
	    		stripeRows: true,
	    		columnLines: true,
	            columns: column,
	            tbar: tbar,
	            bbar: bbar,
	            viewConfig: {
	            	getRowClass: function changeRowClass(record, rowIndex, rowParams, store) {
									    if (record.get("defaultpos") == "1") {
									        return 'x-grid-record-yellow';
									    }
									},
	    	    	loadMask: false,
	                loadMask: {                       //IE8不兼容loadMask
	                	msg: '正在加载数据中……'
	                }
	            },
	            listeners: 
	            {
	            	//itemclick: onRowClick,
	            	selectionchange: function(me, selected, eOpts)
	            	{
	            		var selRecs = gridDT.getSelectionModel().getSelection();
	            		//只能选一个的按钮
	        			if(selRecs.length == 1)
	        			{
	        				btnSetCenterPoint.enable();
	        				btnEdit.enable();        			       					
	        			}
	        			else
	        			{
	        				btnSetCenterPoint.disable();
	        				btnEdit.disable();
	        			}
	        			//多选的按钮
	        			if(selRecs.length >= 1)
	        			{
	        				btnDel.enable();
	        			}
	        			else
	        			{
	        				btnDel.disable();
	        			}
	        			
	            	}
	            }
	        });
		}
	    var containerType = container.getXType();    
		 if (containerType === "tabpanel") {
	         panel.add(gridDT);
	         
	         container.add(panel).show();
	     } else {
	         container.add(gridDT);
	     }  
}
});