Ext.define('OperaConGrid', {
    requires: [
        'Ext.data.Model',
        'Ext.grid.Panel'
    ],
    toolbar: null,
    selRecs: [],
    createGrid: function (config) {
        var me = this;
        var title = config.title;                //标题，模块名
        var tableID = config.tableID;            //表格的ID号，通过ID号来确认所在Tap页和Grid
        var psize = config.pageSize;             //pagesize
        var container = config.container;        //存grid的panel的容器            
        var findStr = null;						 //查询关键字
        var fileName = null;
        var style = null;
        var user = config.user;					 //用户类，包含name, role, identity, unit , rank
        var dataStore;
    	var column;
    	var queryURL;
        var forms = [];
        var selRecs = [];
        var param;								 //存放gridDT选择的行       
        
        var getScanfileName = function(fileName){
        	if(fileName.indexOf('.doc')>0||fileName.indexOf('.docx')>0 || fileName.indexOf('.xls')>0||fileName.indexOf('.xlsx')>0)
			{               			               					                     		
    			if(fileName.indexOf('.docx')>0 || fileName.indexOf('.xlsx')>0 )
    				fileName = fileName.substr(0,fileName.length-5)+".pdf";                    			
    			else 	                      
    				fileName = fileName.substr(0,fileName.length-4)+".pdf";	                        		
			}
			return fileName;
        }
                
        var getSel = function (grid) 
        {
            selRecs = [];  //清空数组
            keyIDs = [];
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
        
        //去掉字符串的左右空格
        String.prototype.trim = function () {
            return this.replace(/(^\s*)|(\s*$)/g, '');
        };
        
        var panel = Ext.create('Ext.panel.Panel', {
            itemId: tableID,
            title: title,
            layout: 'fit',
            closable: true,
            autoScroll: true
        });
        
        //上传控件及相关函数
        var uploadPanel = Ext.create('Ext.ux.uploadPanel.UploadPanel', {
            addFileBtnText: '选择文件...',
            uploadBtnText: '上传',
            removeBtnText: '移除所有',
            cancelBtnText: '取消上传',
            file_upload_limit:10,
            file_size_limit: 1024,//MB
            upload_url: '',
            anchor: '100%'
		})
		
		//在uploadPanel中添加已有文件列表
		var insertFileToList = function() {
        	var info_url = 'OperaConAction!getFileInfo';
        	var delete_url = 'OperaConAction!deleteOneFile';
			var existFile = selRecs[0].data.Accessory.split('*');
			for(var i = 2;i<existFile.length;i++)
			{        							
				var size;
				var fileName = existFile[i];
				// 获取文件大小
				Ext.Ajax.request({
	   				async: false, 
	                method : 'POST',
	                params:{
	                	path:existFile[0]+"\\"+existFile[1],
	                	name:fileName    	            	                	
	                },
	                url: encodeURI(info_url),
	                success: function(response){
	                	size = response.responseText;
	                }
	             });
				var extensionArray = fileName.split(".");   
				var extension = "."+extensionArray[extensionArray.length-1];
    			uploadPanel.store.add({
                    id: i+1,		            
                    name: fileName,		               
                    level: '普通',
                    size: size,
                    type: extension,
                    status: '-9',
                    percent: 100,
                    ppid:selRecs[0].data.ID,
                    deleteurl:delete_url
                }); 
			}
      	}
		
        //获取上传文件名
       	var getFileName = function() {
       		var name = null;
            uploadPanel.store.each(function(record) {
            if(name == null)
            	name = record.get('name') + "*";
            else
                name += record.get('name') + "*";
            })
            return name;
       	}
       	
       	//删除上传文件
       	var deleteFile = function(fileName, ppid) {
       		var deleteAllUrl = "OperaConAction!deleteAllFile";
       		$.getJSON(deleteAllUrl,
    		   {fileName: fileName, id: ppid},	//Ajax参数
                function (res) {
    				if (!res.success)             
                    	Ext.Msg.alert("信息", res.msg);
            });
       	}
       	
       	var DeleteFile = function(action)
       	{
       		var ppid = "";
       		var fileName = null;
  	        fileName = getFileName();
  	        if(action == "addProject" || action == "editProject") {
				if(action == "editProject")	{               			
					ppid = selRecs[0].data.ID;
				}		
				deleteFile(fileName, ppid);
				uploadPanel.store.removeAll();
  	        }
       	}        
		
		var store_Safecheckrec = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'Name'},
                     { name: 'Type'},
                     { name: 'Num'},
                     { name: 'ThisTime'},
                     { name: 'LastTime'},
                     { name: 'Agent'},
                     { name: 'Registrant'},
                     { name: 'Other'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperaConAction!getSafecheckrecListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });

		var items_Safecheckrec221 = [{
        	xtype:'textfield',
            fieldLabel: '分包单位名称',
            labelAlign: 'right',
            labelWidth: 100,
            anchor:'100%',
            name: 'Name'
        },{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: 'hbox',
	        items:[{
            	xtype:'textfield',
                fieldLabel: 'ID',
                name: 'ID',
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'Accessory',
                name: 'Accessory',
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'ProjectName',
                //labelWidth: 120,
                labelAlign: 'right',
                anchor:'95%',
                name: 'ProjectName',
                value : projectName,
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'tableID',
                emptyText: tableID,
                name: 'TableID',
                hidden: true,
                hiddenLabel: true
            },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '本次报备时间',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'ThisTime'
                },{
        	    	xtype:'textfield',
                    fieldLabel: '经办人',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Agent'
        	    }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '上次报备时间',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'LastTime'
                },{
                	xtype:'textfield',
                    fieldLabel: '登记人',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Registrant'
                }]
	        }]
	    },{
        	xtype:'textarea',
            fieldLabel: '备注',
            labelAlign: 'right',
            labelWidth: 100,
            anchor:'100%',
            name: 'Other'
        },
        	uploadPanel
        ];
		var items_Safecheckrec222 = [{
        	xtype:'textfield',
            fieldLabel: '分包单位名称',
            labelAlign: 'right',
            labelWidth: 100,
            anchor:'100%',
            name: 'Name'
        },{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: 'hbox',
	        items:[{
            	xtype:'textfield',
                fieldLabel: 'ID',
                name: 'ID',
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'Accessory',
                name: 'Accessory',
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'ProjectName',
                //labelWidth: 120,
                labelAlign: 'right',
                anchor:'95%',
                name: 'ProjectName',
                value : projectName,
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'tableID',
                emptyText: tableID,
                name: 'TableID',
                hidden: true,
                hiddenLabel: true
            },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '安全检查类型',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Type'
                },{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '本次报备时间',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'ThisTime'
                },{
        	    	xtype:'textfield',
                    fieldLabel: '经办人',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Agent'
        	    }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '安全检查次数',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Num'
                },{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '上次报备时间',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'LastTime'
                },{
                	xtype:'textfield',
                    fieldLabel: '登记人',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Registrant'
                }]
	        }]
	    },{
        	xtype:'textarea',
            fieldLabel: '备注',
            labelAlign: 'right',
            labelWidth: 100,
            anchor:'100%',
            name: 'Other'
        },
        	uploadPanel
        ]
		var items_Safecheckrec223 = [{
        	xtype:'textfield',
            fieldLabel: '分包单位名称',
            labelAlign: 'right',
            labelWidth: 100,
            anchor:'100%',
            name: 'Name'
        },{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: 'hbox',
	        items:[{
            	xtype:'textfield',
                fieldLabel: 'ID',
                name: 'ID',
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'Accessory',
                name: 'Accessory',
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'ProjectName',
                //labelWidth: 120,
                labelAlign: 'right',
                anchor:'95%',
                name: 'ProjectName',
                value : projectName,
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'tableID',
                emptyText: tableID,
                name: 'TableID',
                hidden: true,
                hiddenLabel: true
            },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '安全会议类型',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Type'
                },{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '本次报备时间',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'ThisTime'
                },{
        	    	xtype:'textfield',
                    fieldLabel: '经办人',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Agent'
        	    }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '安全会议次数',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Num'
                },{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '上次报备时间',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'LastTime'
                },{
                	xtype:'textfield',
                    fieldLabel: '登记人',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Registrant'
                }]
	        }]
	    },{
        	xtype:'textarea',
            fieldLabel: '备注',
            labelAlign: 'right',
            labelWidth: 100,
            anchor:'100%',
            name: 'Other'
        },
        	uploadPanel
        ]
        
        var items_Safecheckrec137 = [{
        	xtype:'textfield',
            fieldLabel: '分包单位名称',
            labelAlign: 'right',
            labelWidth: 100,
            anchor:'100%',
            name: 'Name'
        },{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: 'hbox',
	        items:[{
            	xtype:'textfield',
                fieldLabel: 'ID',
                name: 'ID',
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'Accessory',
                name: 'Accessory',
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype:'textfield',
                fieldLabel: 'tableID',
                emptyText: tableID,
                name: 'TableID',
                hidden: true,
                hiddenLabel: true
            },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '安全会议类型',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Type'
                },{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '本次报备时间',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'ThisTime'
                },{
        	    	xtype:'textfield',
                    fieldLabel: '经办人',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Agent'
        	    }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '安全会议次数',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Num'
                },{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '上次报备时间',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'LastTime'
                },{
                	xtype:'textfield',
                    fieldLabel: '登记人',
                    labelAlign: 'right',
                    labelWidth: 100,
                    anchor:'100%',
                    name: 'Registrant'
                }]
	        }]
	    },{
        	xtype:'textarea',
            fieldLabel: '备注',
            labelAlign: 'right',
            labelWidth: 100,
            anchor:'100%',
            name: 'Other'
        },
        	uploadPanel
        ]
		
		var column_221 = [
			{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
			{ text: '分包单位名称', dataIndex: 'Name', align: 'center', width: 250},
			{ text: '本次报备时间', dataIndex: 'ThisTime', align: 'center', width: 125},
			{ text: '上次报备时间', dataIndex: 'LastTime', align: 'center', width: 180},
			{ text: '经办人', dataIndex: 'Agent', align: 'center', width: 100},
			{ text: '登记人', dataIndex: 'Registrant', align: 'center', width: 100},
			{ text: '备注', dataIndex: 'Other', align: 'center', width: 100},
			//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
			{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
		];
		var column_222 = [
       	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
    	    { text: '分包单位名称', dataIndex: 'Name', align: 'center', width: 250},
    	    { text: '安全检查类型', dataIndex: 'Type', align: 'center', width: 100},
    	    { text: '安全检查次数', dataIndex: 'Num', align: 'center', width: 125},
    	    { text: '本次报备时间', dataIndex: 'ThisTime', align: 'center', width: 125},
    	    { text: '上次报备时间', dataIndex: 'LastTime', align: 'center', width: 180},
    	    { text: '经办人', dataIndex: 'Agent', align: 'center', width: 100},
    	    { text: '登记人', dataIndex: 'Registrant', align: 'center', width: 100},
    	    { text: '备注', dataIndex: 'Other', align: 'center', width: 100},
    	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
    	    { text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
        ];
		var column_223 = [
         	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
      	    { text: '分包单位名称', dataIndex: 'Name', align: 'center', width: 250},
      	    { text: '安全会议类型', dataIndex: 'Type', align: 'center', width: 100},
      	    { text: '安全会议次数', dataIndex: 'Num', align: 'center', width: 125},
      	    { text: '本次报备时间', dataIndex: 'ThisTime', align: 'center', width: 125},
      	    { text: '上次报备时间', dataIndex: 'LastTime', align: 'center', width: 180},
      	    { text: '经办人', dataIndex: 'Agent', align: 'center', width: 100},
      	    { text: '登记人', dataIndex: 'Registrant', align: 'center', width: 100},
      	    { text: '备注', dataIndex: 'Other', align: 'center', width: 100},
      	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
      	    { text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
        ];
        var column_137 = [
         	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
      	    { text: '分包单位名称', dataIndex: 'Name', align: 'center', width: 250},
      	    { text: '安全会议类型', dataIndex: 'Type', align: 'center', width: 100},
      	    { text: '安全会议次数', dataIndex: 'Num', align: 'center', width: 125},
      	    { text: '本次报备时间', dataIndex: 'ThisTime', align: 'center', width: 125},
      	    { text: '上次报备时间', dataIndex: 'LastTime', align: 'center', width: 180},
      	    { text: '经办人', dataIndex: 'Agent', align: 'center', width: 100},
      	    { text: '登记人', dataIndex: 'Registrant', align: 'center', width: 100},
      	    { text: '备注', dataIndex: 'Other', align: 'center', width: 100},
      	    { text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
        ];
		
		//按钮函数
		var searchH = function (){
        	var keyword = tbar.getComponent("keyword").getValue().trim();
        	findStr = keyword;
        	if(queryURL.indexOf("?") > 0 ){
        		dataStore.getProxy().url = encodeURI(queryURL + '&findStr=' + findStr);
        	} else{
        		dataStore.getProxy().url = encodeURI(queryURL + '?findStr=' + findStr);
        	}
        	btnSearchR.enable();
        	dataStore.load({params: { start:0, limit:psize}});
        	bbar.moveFirst();
        }
		var searchR = function(){
        	var keyword = tbar.getComponent("keyword").getValue().trim();
        	findStr = findStr + "," + keyword;
        	if(queryURL.indexOf("?") > 0 ){
        		dataStore.getProxy().url = encodeURI(queryURL + '&findStr=' + findStr);
        	}else{
        		dataStore.getProxy().url = encodeURI(queryURL + '?findStr=' + findStr);
        	}
        	//btnSearchR.disable();
        	dataStore.load({params: { start: 0, limit: psize }});
        	bbar.moveFirst();
        }
        
        //附件浏览的下拉菜单
        var fileMenu = new Ext.menu.Menu({
			shadow: "drop",
			allowOtherMenus: true,
			items: [
				new Ext.menu.Item({
					text: '暂无文件'
				})
			]
		})
        
        var scanH = function (item){
        	var selRecs = gridDT.getSelectionModel().getSelection();
			var accessory = selRecs[0].data.Accessory;
			var array = accessory.split("*");
			var foldname = array[0]+"\\"+array[1];
			fileMenu.removeAll();
			
			//若从数据库中取出为空，则表示没有附件
			if(array.length == 1 || array.length==0 ) {
				var menuItem = Ext.create('Ext.menu.Item', {
                	text: '暂无文件'                	
                });
                fileMenu.add(menuItem);
			}//数据库中有附件，添加到文件下拉菜单中
			else{
				for( var i = 2; i < array.length; i++) {
					var icon = "";
					if(array[i].indexOf('.doc')>0)
						icon = "Images/ims/toolbar/report_word.png";
					else if(array[i].indexOf('.rar')>0||array[i].indexOf('.zip')>0)
						icon = "Images/ims/toolbar/report_rar.png";
					else if(array[i].indexOf('.xls')>0||array[i].indexOf('.xlsx')>0)
						icon = "Images/ims/toolbar/report_excel.png";
					else if(array[i].indexOf('.png')>0||array[i].indexOf('.jpg')>0||array[i].indexOf('.bmp')>0)
						icon = "Images/ims/toolbar/report_picture.png";
					else
						icon = "Images/ims/toolbar/report_other.png";
					var menuItem = Ext.create('Ext.menu.Item', {
                		text: array[i],
                		icon: icon,
                		listeners: {
							'click': function (item, e, eOpts) {
								window.open("upload\\" + foldname + "\\" + item.text);								
                      		}
					 	}
                	});
             		fileMenu.add(menuItem);  
				}
		 	}
        }
        
        var addH = function() {
        	var actionURL;
        	var items;
        	if(tableID == 221 || tableID == 222 || tableID == 223 || tableID == 137) {
        		actionURL = 'OperaConAction!addSafecheckrec?userName=' + user.name + "&userRole=" + user.role;  
        		if (tableID == 221) items = items_Safecheckrec221;
        		else if (tableID == 222) items = items_Safecheckrec222;
        		else if (tableID == 223) items = items_Safecheckrec223;
        		else if (tableID == 137) items = items_Safecheckrec137;
        	} 
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addProject',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = "UploadAction!execute";
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addProject', title: '新增文件', items: [forms]});
        }

        var editH = function() {
        	var formItems;
        	var itemURL; 
        	var actionURL;
        	var uploadURL;
        	var items;
        	if(getSel(gridDT)) {
        		if(selRecs.length == 1 ) {
        			insertFileToList();	
        			if(tableID == 221 || tableID == 222 || tableID == 223 || tableID == 137) {
        				actionURL = 'OperaConAction!editSafecheckrec?userName=' + user.name + "&userRole=" + user.role; 
                		if (tableID == 221) items = items_Safecheckrec221;
                		else if (tableID == 222) items = items_Safecheckrec222;
                		else if (tableID == 223) items = items_Safecheckrec223;
                		else if (tableID == 137) items = items_Safecheckrec137;
                	} 
        			createForm({
            			autoScroll: true,
            			action: 'editProject',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = "UploadAction!execute";
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProject', title: '修改文件', items: [forms]});
        		}
        		else {
					 Ext.Msg.alert('警告', '只能选中一条记录！');
				}        		
	        }
        }
        
        var deleteH = function() {        	
        	if(getSel(gridDT)) {
        		Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) {
    				if (buttonID === 'yes') {
    					var delete_url = '';
    					if (tableID == 221 || tableID == 222 || tableID == 223 || tableID == 137) {
    						delete_url = 'OperaConAction!deleteSafecheckrec';
                    	}
    					$.getJSON(encodeURI(delete_url + "?userName=" + user.name + "&userRole=" + user.role),
    							{id: keyIDs.toString()},	//Ajax参数
                                function (res) {
    								if (res.success) {
                                        //重新加载store
    									dataStore.load({ params: { start: 0, limit: psize } });
    									bbar.moveFirst();	//状态栏回到第一页
                                    }
                                    else {
                                    	Ext.Msg.alert("信息", res.msg);
                                    }
                                });
                     }
    			})
        	}        
        }
              
        var btnAdd = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '添加',
            icon: "Images/ims/toolbar/add.gif",
            handler: addH
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
        
        var textSearch = Ext.create('Ext.form.TextField', {
         	itemId: 'keyword',
            emptyText: '请输入查询内容',
            width: 150,
            height: 25,
            listeners: {  
                specialkey: function(field,e) {    
                    if (e.getKey()==Ext.EventObject.ENTER)
                    	searchH();                
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
        var btnScan = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '附件浏览',
            icon: "Images/ims/toolbar/view.png",
           	disabled: true,
            menu: fileMenu,
            handler: scanH
        })
        //建立工具栏
        var tbar = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        })

        var addBtn = function() {
    		tbar.add(textSearch);
    		tbar.add(btnSearch);
    		tbar.add(btnSearchR);
    		tbar.add(btnScan);
    		tbar.add("-");
    		tbar.add(btnAdd);
    		tbar.add(btnEdit);
    		tbar.add(btnDel);
		}
        
        //221:劳动防护用品发放记录      222:分包方安全检查   223:分包方安全会议
        if (tableID == 221 || tableID == 222 || tableID == 223 || tableID == 137) {	
        	dataStore = store_Safecheckrec;
        	queryURL = 'OperaConAction!getSafecheckrecListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID+ "&projectName=" + projectName;
        	if (tableID == 221) column = column_221;
        	else if (tableID == 222) column = column_222;
        	else if (tableID == 223) column = column_223;
        	else if (tableID == 137) column = column_137;
        	addBtn();
        } 
      
        //状态栏
        var bbar = Ext.create('Ext.PagingToolbar',{
            displayInfo: true,
            emptyMsg: "没有数据要显示！",
            displayMsg: "当前为第{0}--{1}条，共{2}条数据", //参数是固定的，分别是起始和结束记录数、总记录数
            store: dataStore,
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
                        Ext.apply( dataStore, { pageSize: psize });
                        dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store,start会自动增加，增加为psize
                        this.ownerCt.moveFirst();	//选择了psize后，跳回第一页
                    }
                }
            }]
        });
        
        //建立formPanel，由工具栏按钮点击显示
        var createForm = function (config) {
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
                	handler: function() {
                		if(forms.form.isValid()){
                			switch (config.action){		
                				case "editProject": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addProject":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}                				
                				default:
                					break;
                			}
                			forms.form.submit({
                				clientValidation: true,
          	                	url: encodeURI(config.url),
          	                	success: function(form, action){
          	                		dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store	
          	                		if(action.result.msg != null)
          	                		{
          	                			Ext.Msg.alert('提示',action.result.msg);
          	                		}
          	                	},
          	                	failure: function(form, action){
          	                		//alert(action.result.msg);
          	                		Ext.Msg.alert('警告',action.result.msg);
          	                	}
      	                	})
      	                	this.up('window').close();  	
      	                }
      	                else{//forms.form.isValid() == false
      	                	Ext.Msg.alert('警告','请完善信息！');      	                	
      	                }
                	}
                },{
                	text: '重置',
                	handler: function(){         	
                		DeleteFile(config.action);
                		uploadPanel.store.removeAll();
                		insertFileToList();
                		forms.form.reset();
                		if (config.action == "editProject") {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
            if (config.action == 'editProject') {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据
            }
        };       
        
        //创建装载formPanel的窗体，由工具栏按钮点击显示
        var showWin = function (config) {
        	var width = 800;
        	var height = 500;
        	
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
                		DeleteFile(config.winId);
                        uploadPanel.store.removeAll();
                    }
                }
            };
            config = $.extend(defaultCng, config);
            var win = new Ext.Window(config);
            win.show();
            return win;
        };
        
        var gridDT = Ext.create('Ext.grid.Panel', {       
    		selModel: new Ext.selection.CheckboxModel({ selType: 'checkboxmodel' }),   //选择框
    		store: dataStore,
    		stripeRows: true,
    		columnLines: true,
    		columns: column,
            tbar: tbar,
            bbar: bbar,
            viewConfig: {
    	    	loadMask: false,
                loadMask: {                       //IE8不兼容loadMask
                	msg: '正在加载数据中……'
                }
            },
            listeners: {
            	//itemclick: onRowClick,
            	selectionchange: function(me, selected, eOpts) {
            		var selRecs = gridDT.getSelectionModel().getSelection();
            		//只能选一个的按钮
        			if(selRecs.length == 1) {
        				btnEdit.enable();        			
        				btnScan.enable();
        			} else {
        				btnEdit.disable();
        				btnScan.disable();
        			}
        			//多选的按钮
        			if(selRecs.length >= 1) {
        				btnDel.enable();
        			} else {
        				btnDel.disable();
        			}        			
            	},
                'celldblclick': function(self, td, cellIndex, record, tr, rowIndex){
                	var html_str = "";
            		var record = dataStore.getAt(rowIndex);
            		var Num = rowIndex + 1;
                	if (tableID == 221) {
                		html_str = "<h1 style=\"padding: 5px;\"><center>劳动防护用品发放记录详细信息</center></h1>"
            				+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
            				+ "<tr height=\"20px;\"><td style=\"padding:5px;\">分包单位名称</td><td colspan=\"3\">" + record.get("Name") + "</td></tr>"
            				+ "<tr><td style=\"padding:5px;\">本次报备时间</td><td>" + record.get('ThisTime') + "</td>"
            				+ "<td style=\"padding:5px;\">上次报备时间</td><td>" + record.get('LastTime') + "</td></tr>"
            				+ "<tr><td width=\"15%\ style=\"padding:5px;\">经办人</td><td>" + record.get('Agent') + "</td>"
            				+ "<td style=\"padding:5px;\">登记人</td><td>" + record.get('Registrant') + "</td></tr>"
            				+ "<tr style=\"height:100px;\"><td style=\"padding:5px;\">备注</td><td colspan=\"3\">" + record.get('Other') + "</td></tr>";
                		
                		html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
         		        
       		         var misfile = record.get('Accessory').split('*');
//       		        var foldermis;
     				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
       		         
     				    height = 500;
         		        width = 800;
         		        
         		        for(var i = 2;i<misfile.length;i++){
        		        	
        		        	scanfileName = getScanfileName(misfile[i]); 
        		        	displayfileName = misfile[i];
                   		/*if(getBLen(scanfileName)>10)
                   			displayfileName = displayfileName.substring(0,7)+"···";*/
        		        	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
        		        }
         		        
       		        /*for(var i = 2;i<misfile.length;i++){
       		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
       		        }*/
       		        
        		         html_str += "</table>";
                    } else if (tableID == 222) {
                    	html_str = "<h1 style=\"padding: 5px;\"><center>分包方安全检查报备详细信息</center></h1>"
            				+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
            				+ "<tr height=\"20px;\"><td style=\"padding:5px;\">分包单位名称</td><td colspan=\"3\">" + record.get("Name") + "</td></tr>"
            				+ "<tr><td style=\"padding:5px;\">安全检查类型</td><td>" + record.get('Type') + "</td>"
            				+ "<td style=\"padding:5px;\">安全检查次数</td><td>" + record.get('Num') + "</td></tr>"
            				+ "<tr><td style=\"padding:5px;\">本次报备时间</td><td>" + record.get('ThisTime') + "</td>"
            				+ "<td style=\"padding:5px;\">上次报备时间</td><td>" + record.get('LastTime') + "</td></tr>"
            				+ "<tr><td width=\"15%\ style=\"padding:5px;\">经办人</td><td>" + record.get('Agent') + "</td>"
            				+ "<td style=\"padding:5px;\">登记人</td><td>" + record.get('Registrant') + "</td></tr>"
            				+ "<tr style=\"height:100px;\"><td style=\"padding:5px;\">备注</td><td colspan=\"3\">" + record.get('Other') + "</td></tr>";
                    	
                    	html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
         		        
       		         var misfile = record.get('Accessory').split('*');
//       		        var foldermis;
     				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
       		         
     				    height = 500;
         		        width = 800;
         		        
         		        for(var i = 2;i<misfile.length;i++){
        		        	
        		        	scanfileName = getScanfileName(misfile[i]); 
        		        	displayfileName = misfile[i];
                   		/*if(getBLen(scanfileName)>10)
                   			displayfileName = displayfileName.substring(0,7)+"···";*/
        		        	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
        		        }
         		        
       		        /*for(var i = 2;i<misfile.length;i++){
       		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
       		        }*/
       		        
        		         html_str += "</table>";
                    	
                    	
                    } else if (tableID == 223) {
                    	html_str = "<h1 style=\"padding: 5px;\"><center>分包方安全会议报备详细信息</center></h1>"
            				+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
            				+ "<tr height=\"20px;\"><td style=\"padding:5px;\">分包单位名称</td><td colspan=\"3\">" + record.get("Name") + "</td></tr>"
            				+ "<tr><td style=\"padding:5px;\">安全会议类型</td><td>" + record.get('Type') + "</td>"
            				+ "<td style=\"padding:5px;\">安全会议次数</td><td>" + record.get('Num') + "</td></tr>"
            				+ "<tr><td style=\"padding:5px;\">本次报备时间</td><td>" + record.get('ThisTime') + "</td>"
            				+ "<td style=\"padding:5px;\">上次报备时间</td><td>" + record.get('LastTime') + "</td></tr>"
            				+ "<tr><td width=\"15%\ style=\"padding:5px;\">经办人</td><td>" + record.get('Agent') + "</td>"
            				+ "<td style=\"padding:5px;\">登记人</td><td>" + record.get('Registrant') + "</td></tr>"
            				+ "<tr style=\"height:100px;\"><td style=\"padding:5px;\">备注</td><td colspan=\"3\">" + record.get('Other') + "</td></tr>";
                    	
                    	html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
         		        
       		         var misfile = record.get('Accessory').split('*');
//       		        var foldermis;
     				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
       		         
     				    height = 500;
         		        width = 800;
         		        
         		        for(var i = 2;i<misfile.length;i++){
        		        	
        		        	scanfileName = getScanfileName(misfile[i]); 
        		        	displayfileName = misfile[i];
                   		/*if(getBLen(scanfileName)>10)
                   			displayfileName = displayfileName.substring(0,7)+"···";*/
        		        	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
        		        }
         		        
       		        /*for(var i = 2;i<misfile.length;i++){
       		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
       		        }*/
       		        
        		         html_str += "</table>";
                    }
                    else if (tableID == 137) {
                    	html_str = "<h1 style=\"padding: 5px;\"><center>分包方安全会议报备详细信息</center></h1>"
            				+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
            				+ "<tr height=\"20px;\"><td style=\"padding:5px;\">分包单位名称</td><td colspan=\"3\">" + record.get("Name") + "</td></tr>"
            				+ "<tr><td style=\"padding:5px;\">安全会议类型</td><td>" + record.get('Type') + "</td>"
            				+ "<td style=\"padding:5px;\">安全会议次数</td><td>" + record.get('Num') + "</td></tr>"
            				+ "<tr><td style=\"padding:5px;\">本次报备时间</td><td>" + record.get('ThisTime') + "</td>"
            				+ "<td style=\"padding:5px;\">上次报备时间</td><td>" + record.get('LastTime') + "</td></tr>"
            				+ "<tr><td width=\"15%\ style=\"padding:5px;\">经办人</td><td>" + record.get('Agent') + "</td>"
            				+ "<td style=\"padding:5px;\">登记人</td><td>" + record.get('Registrant') + "</td></tr>"
            				+ "<tr style=\"height:100px;\"><td style=\"padding:5px;\">备注</td><td colspan=\"3\">" + record.get('Other') + "</td></tr>";
                    	
                    	html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
         		        
       		         var misfile = record.get('Accessory').split('*');
//       		        var foldermis;
     				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
       		         
     				    height = 500;
         		        width = 800;
         		        
         		        for(var i = 2;i<misfile.length;i++){
        		        	
        		        	scanfileName = getScanfileName(misfile[i]); 
        		        	displayfileName = misfile[i];
                   		/*if(getBLen(scanfileName)>10)
                   			displayfileName = displayfileName.substring(0,7)+"···";*/
        		        	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
        		        }
         		        
       		        /*for(var i = 2;i<misfile.length;i++){
       		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
       		        }*/
       		        
        		         html_str += "</table>";
                    }
                	Ext.create('Ext.window.Window', {
                        title: '查看详情',
                        titleAlign: 'center',
                        height: 350,
                        width: 700,
                        closeAction: 'destroy',
                        layout: 'fit',
                        autoScroll: true,
                        tools: [{
                            type: 'print',
                            handler: function() {
                         	   	 var oPop = window.open('','oPop');                                
	                              	 bdhtml = html_str;
	                              	 oPop.document.body.innerHTML = bdhtml;
	                              	 oPop.print();
                            }
                        }],
                        html: html_str
                    }).show();
                }
        	}
        });
        panel.add(gridDT);
        container.add(panel).show();
    }
});