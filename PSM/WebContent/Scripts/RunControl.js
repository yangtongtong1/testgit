var tn;

Ext.define('RunControlGrid', {
    requires: [
        'Ext.data.Model',
        'Ext.grid.Panel'
    ],
    toolbar: null,
    selRecs: [],
    createGrid: function (config) {
    	tn = config.table;
        var me = this;
        var title = config.title;                //标题，模块名
        var tableID = config.tableID;                //表格的ID号，通过ID号来确认所在Tap页和Grid
        var psize = config.pageSize;             //pagesize
        var container = config.container;        //存grid的panel的容器            
        var findStr = null;	//查询关键字
        var fileName = null;
        var style = null;
        var user = config.user;	//用户类，包含name, role, identity, unit , rank
        var gridDT;
        var createForm;
        var dataStore;
    	var column;
    	var queryURL;
        var forms = [];
        var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';	//必填项红色星号*
        var selRecs = [];
        var param;		//存放gridDT选择的行
        var projectNo = config.projectNo;
        var type;
        
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
        
         /**
         * 获得选择
         */
        var   getSel = function (grid) 
        {
            selRecs = [];  //清空数组
            keyIDs = [];
            selRecs = grid.getSelectionModel().getSelection();
            for (var i = 0; i < selRecs.length; i++)
            {
//            	if(title == '项目部安全生产投入计划'||title == '项目部安全费用使用台账'||title == '项目部安全费用使用检查' || title == '分包方安全生产投入计划' || title == '分包方安全费用使用台账' || title == '分包方安全费用使用检查'||title=="安全费用统计分析")
            		keyIDs.push(selRecs[i].data.ID);  //当为修改是，selRecs中应该只有条数据
            }
            if (selRecs.length === 0)
            {
                Ext.Msg.alert('警告', '没有选中任何记录！');
                return false;
            }
            return true;
        };
        
        //去掉字符串的左右空格
        String.prototype.trim = function () 
        {
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
		

		
		
//jianglf---------------------------------------------------------------------		
		//在uploadPanel中添加已有文件列表
		var insertFileToList = function()
      	{
        	var info_url = '';
        	var delete_url = '';
        	if(title == '现场作业人员动态管理'||title == '作业人员职业健康体检')
        	{
        		info_url = 'RunControlAction!getFileInfo';
        		delete_url = 'RunControlAction!deleteOneFile';
        	}
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
       	var getFileName = function(){
       		var name = null;
            uploadPanel.store.each(function(record){
            if(name == null)
            	name = record.get('name') + "*"
            else
                name += record.get('name') + "*";
            })
            return name;
       	}
       	
       	//删除上传文件
       	var deleteFile = function(style, fileName, ppid){
       		var deleteAllUrl = "";
       		if(title == '现场作业人员动态管理'||title == '作业人员职业健康体检')
       		{
       			deleteAllUrl = "RunControlAction!deleteAllFile";
       		}
       		$.getJSON(deleteAllUrl,
    		{	style: style, fileName: fileName,id:ppid},	//Ajax参数
                function (res) {
    				if (res.success) {}
                    else {
                    	Ext.Msg.alert("信息", res.msg);
            	}
            });
       	}
//end----------------------------------------------------------------------------       	
       	var DeleteFile = function(action)
       	{
       		var ppid = "";
       		var fileName = null;
  	        fileName = getFileName();
  	        if(action == "addSaftycheck"||action == "editSaftycheck" )
  	        {
				if(action == "editSaftycheck")
				{               			
					ppid = selRecs[0].data.ID;			
				}		
				deleteFile(style, fileName, ppid);
				uploadPanel.store.removeAll();
  	        }
       	}
        
       	
       	
     
        
    	
		//按钮函数
		var searchH = function (){
        	var keyword = tbar.getComponent("keyword").getValue().trim();
        	findStr = keyword;
        	if(queryURL.indexOf("?") > 0 ){
        		dataStore.getProxy().url = encodeURI(queryURL + '&findStr=' + findStr);
        	}else{
        		dataStore.getProxy().url = encodeURI(queryURL + '?findStr=' + findStr);
        	}
        	btnSearchR.enable();
        	dataStore.load({params: { start: 0, limit: psize }});
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
		
		var importH = function() {
        	var importUploadPanel = Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                addFileBtnText: '选择文件...',
                uploadBtnText: '上传',
                removeBtnText: '移除所有',
                cancelBtnText: '取消上传',
                file_upload_limit:1,
                file_size_limit: 1024,//MB
                upload_url: 'UploadAction!execute',
                anchor: '100%'
    		});
        	showWin({ 
        		winId: 'importProject', 
        		title: '导入文件', 
        		items: [importUploadPanel],
        		buttonAlign: 'center',
        		buttons: [{        	
	            	text: '确定',
	            	handler: function(){
	            		if (importUploadPanel.store.getCount() == 0) {
	            			Ext.Msg.alert('提示', '请先导入Excel文件');
	            			return;
	            		}	            			
	            		var fileName = importUploadPanel.store.getAt(0).get('name');
	            		var win = this;
	            		$.getJSON('OperaConAction!importExcel', { fileName: fileName, type : title, projectName: projectName },
	            				function(data) {
	            					if (data.result == 'success')
	            						Ext.Msg.alert('提示', '成功导入' + data.total + "条记录！");
	            					else
	            						Ext.Msg.alert('提示', '导入失败');
	        	            		win.up('window').close();
	            				}
	            		)
	            	}
        		},{        	
	            	text: '取消',
	            	handler: function(){
	            		this.up('window').close();
	            	}
        		}],
        		listeners: {
                    beforeclose: function (me) {
                    	if (importUploadPanel.store.getCount() != 0) {
                    		var fileName = importUploadPanel.store.getAt(0).get('name');
                       		$.getJSON("FileSystemAction!deleteAllFile", { fileName: fileName } );
                       		importUploadPanel.store.removeAll();
                    	}
                    	bbar.moveFirst();
                    }
                }
            });
        }
       	
       	var btnImport = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '文件导入',
            icon: "Images/ims/toolbar/extexcel.png",
            handler: importH
        })
        
        function scanH (item){
        	//被选择的那一行
        	var selRecs = gridDT.getSelectionModel().getSelection();
			var accessory = selRecs[0].data.Accessory; //选中行的上传文件，附录
			var array = accessory.split("*");
			var foldname = array[0]+"\\"+array[1];
			fileMenu.removeAll();
			
			//若从数据库中取出为空，则表示没有附件
			if(array.length == 1 || array.length==0 )
			{
				var menuItem = Ext.create('Ext.menu.Item', {
                	text: '暂无文件'
                	
                });
                fileMenu.add(menuItem);
			}//数据库中有附件，添加到文件下拉菜单中
			else{
				for( var i = 2; i < array.length; i++){
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
                		listeners:
						{
							'click': function (item, e, eOpts) {
                        		var fileName = item.text;    
                        		if(fileName.indexOf('.doc')>0||fileName.indexOf('.docx')>0 || fileName.indexOf('.xls')>0||fileName.indexOf('.xlsx')>0)
                				{               			               				
	                        		if(e.getX()-item.getX()>20)
	                        		{
	                        			window.open("upload\\" + foldname + "\\" + fileName);
	                        		}
	                        		else
	                        		{
	                        			if(fileName.indexOf('.docx')>0 || fileName.indexOf('.xlsx')>0 )
	                        				window.open("upload\\" + foldname + "\\" + fileName.substr(0,fileName.length-5)+".pdf");                    			
	                        			else 	                      
	                        				window.open("upload\\" + foldname + "\\" + fileName.substr(0,fileName.length-4)+".pdf");
	                        		}
	                			}
                        		else
                        		{
                        			window.open("upload\\" + foldname + "\\" + fileName);
                        		}
                      		}
					 	}
                	});
             		fileMenu.add(menuItem);  
				}
		 	}
        }
        
        
 
        
        var addH = function(){
        	var actionURL;
        	var uploadURL;
        	var items;
        	//*********************
        	problemNum = 1;
        	
        	if(title == '现场作业人员动态管理')
        	{	        	
        		actionURL = 'RunControlAction!addXiandongtai?userName=' + user.name + "&userRole=" + user.role ;  
        		uploadURL = "UploadAction!execute";
        		items = items_xiandongtai;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addXiandongtai',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addXiandongtai', title: '新增项目', items: [forms]});
        	}    
//jianglf-------------------------------------------------------------------------------------------        	
        	if(title == '作业人员职业健康体检')
        	{	        	
        		actionURL = 'RunControlAction!addGongtitai?userName=' + user.name + "&userRole=" + user.role ;  
        		uploadURL = "UploadAction!execute";
        		items = items_gongtitai;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addGongtitai',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addGongtitai', title: '新增项目', items: [forms]});
        	} 
        }
 //end---------------------------------------------------------------------------------------------       
        var editH = function() 
        {
        	var formItems;
        	var itemURL; 
        	var actionURL;
        	var uploadURL;
        	var items;
        	if(getSel(gridDT))
        	{
        		if(selRecs.length == 1)
        		{
        				
        			if(title == '现场作业人员动态管理')
                	{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'RunControlAction!editXiandongtai?userName=' + user.name + "&userRole=" + user.role ;  
        				items = items_xiandongtai;
              
        			createForm({
            			autoScroll: true,
            			action: 'editXiandongtai',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editXiandongtai', title: '修改项目', items: [forms]});
        			}
        			
 //jianglf----------------------------------------------------------------------------------------------------------       			
        			if(title == '作业人员职业健康体检')
                	{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'RunControlAction!editGongtitai?userName=' + user.name + "&userRole=" + user.role ;  
        				items = items_gongtitai;
              
        			createForm({
            			autoScroll: true,
            			action: 'editGongtitai',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editGongtitai', title: '修改项目', items: [forms]});
        			}
        		
        		}
        		else
				{
					 Ext.Msg.alert('警告', '只能选中一条记录！');
				}
        		
	        }
        }
//end-------------------------------------------------------------------------------------------------------------------        
        var deleteH = function() 
        {
        	
        	if(getSel(gridDT))
        	{
        		Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) 
        		{
    				if (buttonID === 'yes') 
    				{
    					if(title == '现场作业人员动态管理')
                    	{$.getJSON(encodeURI("RunControlAction!deleteXiandongtai?userName=" + user.name + "&userRole=" + user.role),
    							{id: keyIDs.toString()},	//Ajax参数
                                function (res) 
                                {
    								if (res.success)
    								{
                                        //重新加载store
    									dataStore.load({ params: { start: 0, limit: psize } });
    									bbar.moveFirst();	//状态栏回到第一页
                                    }
                                    else 
                                    {
                                    	Ext.Msg.alert("信息", res.msg);
                                    }
                                });
    					}
//jianglf-----------------------------------------------------------------------------------------------------------    					
    					if(title == '作业人员职业健康体检')
                    	{$.getJSON(encodeURI("RunControlAction!deleteGongtitai?userName=" + user.name + "&userRole=" + user.role),
    							{id: keyIDs.toString()},	//Ajax参数
                                function (res) 
                                {
    								if (res.success)
    								{
                                        //重新加载store
    									dataStore.load({ params: { start: 0, limit: psize } });
    									bbar.moveFirst();	//状态栏回到第一页
                                    }
                                    else 
                                    {
                                    	Ext.Msg.alert("信息", res.msg);
                                    }
                                });
    					}
    					

                     }
    			})
        	}
        
        }
//end--------------------------------------------------------------------------------------------------------------------
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

        
        var combosex = new Ext.data.ArrayStore({
            fields: ['id', 'sex'],
            data: [[1, '男'], 
            	[2, '女']]
          });
        
        var combogang = new Ext.data.ArrayStore({
            fields: ['id', 'data'],
            data: [[1, '岗前'], 
            	[2, '岗中'], 
            	[3, '岗后']]
          });
      
      
        var items_xiandongtai =[{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: 'hbox',
	        items:[{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: 'ID',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    name: 'ID',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'textfield',
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
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
                },
                {
	            	xtype:'textfield',
                    fieldLabel: '分包单位名称',
//                    labelWidth: 120,
                    afterLabelTextTpl: required,
                    allowBlank: false,
                    labelAlign: 'right',
                    anchor:'90%',
                    name: 'repoter'
                },
                {
	            	xtype:'textfield',
                    fieldLabel: '岗位/工种',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    allowBlank: false,
                    anchor:'90%',
                    name: 'gangwei'
                },
                {
	            	xtype:'textfield',
                    fieldLabel: '姓名',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    allowBlank: false,
                    anchor:'90%',
                    name: 'name'
                },
                {
                	xtype:'combobox',
                	fieldLabel: '性别',
                    store: combosex,
                    afterLabelTextTpl: required,
                    allowBlank: false,
                    valueField: 'sex',
                    displayField: 'sex',
	                triggerAction: 'all',
//	                 emptyText: '请选择...',
//	                 allowBlank: false,
//	                 blankText: '请选择政治面貌',
	                editable: false,
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    name: 'sex'
                },{
                	xtype:'textfield',
                    fieldLabel: '身份证号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    allowBlank: false,
                    anchor:'90%',
                    name: 'sfz',
                    regex : /^(\d{18,18}|\d{15,15}|\d{17,17}x)$/,
                	regexText : '输入正确的身份号码'
                },{
              	  xtype: 'datefield',
                  fieldLabel: '进场计划时间',
                  format : 'Y-m-d',
                  afterLabelTextTpl: required,
                  allowBlank: false,
                  anchor:'90%',
                  labelAlign: 'right',
                  name: 'intimeplan'
               },{
            	    xtype: 'datefield',
                    fieldLabel: '进场实际时间',
                    format : 'Y-m-d',
                    anchor:'90%',
                    labelAlign: 'right',
                    name: 'intimereal'
               },{
          	        xtype: 'datefield',
                    fieldLabel: '离场计划时间',
                    format : 'Y-m-d',
                    anchor:'90%',
                    afterLabelTextTpl: required,
                    allowBlank: false,
                    labelAlign: 'right',
                    name: 'lvetimeplan'
               },{
        	        xtype: 'datefield',
                    fieldLabel: '离场实际时间',
                    format : 'Y-m-d',
                    anchor:'90%', 
                    labelAlign: 'right',
                    name: 'lvetimereal'
                },{
                	xtype:'textfield',
                    fieldLabel: '联系电话',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    allowBlank: false,
                    anchor:'90%',
                    name: 'phone'
                },{
                	xtype:'textfield',
                    fieldLabel: '是否参加体检',
                    //labelWidth: 120,
//                    maxLength:2,
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    allowBlank: false,
                    anchor:'90%',
                    name: 'istijian'
                },{
                	xtype:'textfield',
                    fieldLabel: '是否参加工伤保险',
                    //labelWidth: 120,
//                    maxLength:2,
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    allowBlank: false,
                    anchor:'90%',
                    name: 'isgsbx'
                }]
	        }]
	    },uploadPanel
        ]
        
//jianglf------------------------------------------------------
        var items_gongtitai =[{
        	xtype: 'container',
	        anchor: '100%',
	        layout: 'hbox',
	        items:[{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: 'ID',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    name: 'ID',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'textfield',
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
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
                },
                {
	            	xtype:'textfield',
                    fieldLabel: '分包单位名称',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    name: 'repoter'
                },
                {
	            	xtype:'textfield',
                    fieldLabel: '岗位/工种',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    name: 'gangwei'
                },
                {
	            	xtype:'textfield',
                    fieldLabel: '姓名',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    name: 'name'
                },
                {
                	xtype:'combobox',
                	fieldLabel: '性别',
                    store: combosex,
                    valueField: 'sex',
                    displayField: 'sex',
	                triggerAction: 'all',
//	                 emptyText: '请选择...',
//	                 allowBlank: false,
//	                 blankText: '请选择政治面貌',
	                editable: false,
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    name: 'sex'
                },{
              	  xtype: 'datefield',
                  fieldLabel: '体检时间',
                  format : 'Y-m-d',
                  anchor:'90%',
                  labelAlign: 'right',
                  name: 'tijiantime'
               },
               {
	            	xtype:'textfield',
                   fieldLabel: '体检医院',
//                   labelWidth: 120,                   labelAlign: 'right',
                   anchor:'90%',
                   name: 'tijianplace'
               },
               {
	            	xtype:'textfield',
                   fieldLabel: '体检结果',
//                   labelWidth: 120,
                   labelAlign: 'right',
                   anchor:'90%',
                   name: 'tijianresult'
               },{
            	   xtype:'combobox',
            	   fieldLabel: '岗前/岗中/岗后',
                   store: combogang,
                   valueField: 'data',
                   displayField: 'data',
	                triggerAction: 'all',
//	                 emptyText: '请选择...',
//	                 allowBlank: false,
//	                 blankText: '请选择政治面貌',
	                editable: false,
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    name: 'type'
                }]
	        }]
	    },uploadPanel
        ]
 //end------------------------------------------------------------------------------------------          
            
        
        
        
      

        
        
       	/**
       	 * saftyCheck 计划
       	 * 计划
       	 */
		var store_Xiandongtai = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'repoter'},
                     { name: 'gangwei'},
                     { name: 'sfz'},
                     { name: 'Peixun'},
                     { name: 'Peixunnr'},
                     { name: 'name'},
                     { name: 'sex'},
                     { name: 'intimeplan'},
                     { name: 'intimereal'},
                     { name: 'lvetimeplan'},
                     { name: 'lvetimereal'},
                     { name: 'phone'},
                     { name: 'istijian'},
                     { name: 'isgsbx'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('RunControlAction!getXiandongtaiListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        
        
       
        /**
       	 * Gongtitai 计划
       	 * 计划
       	 */
		var store_Gongtitai = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
   	                 { name: 'repoter'},
                     { name: 'gangwei'},
                     { name: 'name'},
                     { name: 'sex'},
        	         { name: 'tijiantime'},
                     { name: 'tijianplace'},
                     { name: 'tijianresult'},
                     { name: 'type'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('RunControlAction!getGongtitaiListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        
        

      

        if(title == '现场作业人员动态管理')
        {	
        	
        	dataStore = store_Xiandongtai;
        	queryURL = 'RunControlAction!getXiandongtaiListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '分包单位名称', dataIndex: 'repoter', align: 'center', width: 100},	
        		    { text: '岗位/工种', dataIndex: 'gangwei', align: 'center', width: 100},	
        		    { text: '姓名', dataIndex: 'name', align: 'center', width: 100},	
        		    { text: '性别', dataIndex: 'sex', align: 'center', width: 100},
        		    { text: '身份证号', dataIndex: 'sfz', align: 'center', width: 100},
        		    { text: '进场计划时间', dataIndex: 'intimeplan', align: 'center', width: 100},
        		    { text: '进场实际时间', dataIndex: 'intimereal', align: 'center', width: 100},
        		    { text: '离场计划时间', dataIndex: 'lvetimeplan', align: 'center', width: 100},	
        		    { text: '离场实际时间', dataIndex: 'lvetimereal', align: 'center', width: 100},	
        		    { text: '联系电话', dataIndex: 'phone', align: 'center', width: 100},	
        		    { text: '是否参加体检', dataIndex: 'istijian', align: 'center', width: 100},	
        		    { text: '是否参加培训', dataIndex: 'Peixun', align: 'center', width: 100},
        		    { text: '是否参加工伤保险', dataIndex: 'isgsbx', align: 'center', width: 100}	
        		    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add(btnImport);
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add(btnScan);
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        			tbar.remove(btnImport);
        		}
        }
        
//jianglf-----------------------------------------------------------------------------------------------------------       
        if(title=='作业人员职业健康体检')
        {
//        	store_Taizhang.load();
        	dataStore = store_Gongtitai;
        	queryURL = 'RunControlAction!getGongtitaiListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '分包单位名称', dataIndex: 'repoter', align: 'center', width: 100},	
        		    { text: '岗位/工种', dataIndex: 'gangwei', align: 'center', width: 100},	
        		    { text: '姓名', dataIndex: 'name', align: 'center', width: 100},	
        		    { text: '性别', dataIndex: 'sex', align: 'center', width: 100},
        		    { text: '体检时间', dataIndex: 'tijiantime', align: 'center', width: 200},	
        		    { text: '体检医院', dataIndex: 'tijianplace', align: 'center', width: 200},	
        		    { text: '体检结果', dataIndex: 'tijianresult', align: 'center', width: 200},	
        		    { text: '岗前/岗中/岗后', dataIndex: 'type', align: 'center', width: 200}
        		    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add(btnScan);
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        }
        
 //end---------------------------------------------------------------------------------------------------------------       
        

        
      //状态栏
        bbar = Ext.create('Ext.PagingToolbar',{
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
                	handler: function() {
                		if(forms.form.isValid()){
                			switch (config.action){	
                			     
                			    case "addXiandongtai":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "addGongtitai":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    
                			    case "editXiandongtai":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    case "editGongtitai":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
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
          	                		
          	                		var actionId = -1;
          	                		
          	                		//actionid就是那个文件里面标黄记录对应的序号
          	                		if (title == '现场作业人员动态管理') actionId = 26;
          	                		else if(title == '环境保护费用管理') actionId = 20;
          	                		
          	                		
          	                		if (actionId != -1) {
          	                			Ext.Ajax.request({
              	          	   				async: false, 
              	          	                method: 'GET',
              	          	                params: {
              	          	                	project: projectName,
              	          	                	actionId: actionId    	            	                	
              	          	                },
              	          	                url: encodeURI("MissionAction!updateMissionState")
              	          	            });
          	                		}
          	                		
          	                		/************************* end *********************/

          	                		
          	                		
          	                	},
          	                	failure: function(form, action){
          	                		//alert(action.result.msg);
//          	                		Ext.Msg.alert('警告',action.result.msg);
          	                	}
      	                	})
      	                	this.up('window').close();  	
      	                }
      	                else{//forms.form.isValid() == false
      	                	if(config.action == "addProject" || config.action == "editProject")
                    		{
                    			var brief = forms.getForm().findField('BuildContent');
                    			var brieftext = brief.getValue();
                    			if(brieftext.length>500)
                    			{
                    				Ext.Msg.alert('警告','专利简介不能超过500个字符！');
                    			}
                    			else
                    			{
                    				Ext.Msg.alert('警告','请完善信息！');
                    			}
                    		}
      	                	else if(config.action == "addProjectperson" || config.action == "editProjectperson")
                    		{
                    			var brief = forms.getForm().findField('Duty');
                    			var brieftext = brief.getValue();
                    			if(brieftext.length>500)
                    			{
                    				Ext.Msg.alert('警告','职责不能超过500个字符！');
                    			}
                    			else
                    			{
                    				Ext.Msg.alert('警告','请完善信息！');
                    			}
                    		}
      	                	else
      	                	{
      	                		Ext.Msg.alert('警告','请完善信息！');
      	                	}
      	                }
                	}
                },{
                	text: '重置',
                	handler: function(){         	
                		DeleteFile(config.action);
                		uploadPanel.store.removeAll();
                		insertFileToList();
                		forms.form.reset();
                		if (config.action == "editXiandongtai") 
                        {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		if (config.action == "editGongtitai") 
                        {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		
                		 
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
        	if (config.action == "editXiandongtai") 
            {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
    		if (config.action == "editGongtitai") 
            {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
        	
        };       
        
        //创建装载formPanel的窗体，由工具栏按钮点击显示
        var showWin = function (config) {
        	var width = 420;
        	var height = 320;
 //jianglf--------------------------------------------------------------       	
//        	if(title == '上级检查问题整改及回复'||title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查')
        	if(title == '现场作业人员动态管理'||title == '作业人员职业健康体检')
        	{//编辑提案信息框
        		width = 800;
        		height = 600;
        	}
//end-------------------------------------------------------------------------        	
        
        	

        	
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
        
      var showAdd = function (config) {
        	var width = 320;
        	var height = 220;
        	      	
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
                constrain: true
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
            listeners: 
            {
            	//itemclick: onRowClick,
            	selectionchange: function(me, selected, eOpts)
            	{
            		var selRecs = gridDT.getSelectionModel().getSelection();
            		//只能选一个的按钮
        			if(selRecs.length == 1)
        			{
        				btnEdit.enable();       			
        				btnScan.enable();
        			}
        			else
        			{
        				btnEdit.disable();
        				btnScan.disable();
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
        			
            	},
            	//添加双击显示详细信息事件
                'celldblclick': function (self, td, cellIndex, record, tr, rowIndex)
        	    {
                	if(title == '现场作业人员动态管理')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '现场作业人员动态管理')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">分包单位名称</td><td width=\"40%\">" + record.get("repoter") + 
             		                    "</td></tr><tr><td style=\"padding:5px;\">岗位/工种</td><td>" + record.get('gangwei') + 
                                        "</td><td style=\"padding:5px;\">姓名</td><td>" + record.get('name') + 
                                        "</td></tr><tr><td style=\"padding:5px;\">身份证号<td>" + record.get('sfz') + 
                                        "</td><td style=\"padding:5px;\">是否参加培训</td><td>" + record.get('Peixun') + 
                                        "</tr><tr><td style=\"padding:5px;\">培训详情</td><td style=\"padding:5px;text-align:left;\" colspan=\"3\">" + record.get('Peixunnr') + "</td></tr><tr></td><td style=\"padding:5px;\">性别</td><td>" + record.get('sex') + 
                                        "</td><td style=\"padding:5px;\"> 进场计划时间</td><td>" +  record.get('intimeplan')+
                                        "</tr><tr></td><td style=\"padding:5px;\">进场实际时间</td><td>" + record.get('intimereal') + 
                                        "</td><td style=\"padding:5px;\">离场计划时间</td><td>" + record.get('lvetimeplan')+
                                        "</tr><tr></td><td style=\"padding:5px;\">离场实际时间</td><td>" + record.get('lvetimereal') + 
                                        "</td><td style=\"padding:5px;\">联系电话</td><td>" + record.get('phone')+
                                        "</tr><tr></td><td style=\"padding:5px;\">是否参加体检</td><td>" + record.get('istijian') + 
                                        "</td><td style=\"padding:5px;\">是否参加工伤保险</td><td>" + record.get('isgsbx')+ "</td></tr>";
             		         
             		        html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
             		        
           		         var misfile = record.get('Accessory').split('*');
//           		        var foldermis;
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
                		
                        Ext.create('Ext.window.Window', 
                        {
                           title: '查看详情',
                           titleAlign: 'center',
                           height: 350,
                           width: 700,
                           closeAction: 'destroy',
                           layout: 'fit',
                           autoScroll: true,
                           tools: 
                           [{
                               type: 'print',
                               handler: function() 
                               {
                            	   	 var oPop = window.open('','oPop');                                
	                              	 bdhtml = html_str;                              
	                              	 oPop.document.body.innerHTML = bdhtml;                        
	                              	 oPop.print();  
                               }
                           }],
                           html: html_str
                       }).show();
                	}
                	
                	
                	
//                	[
//           		     {name:'ID'},
//           	         { name: 'no'},
//                        { name: 'checkId'},
//                        { name: 'problem'},
//                        { name: 'solveTime'},
//                        { name: 'Accessory'},
//               ],
                	
                	
  //jianglf--------------------------------------------------------------------------------------------              	
                	if(title == '作业人员职业健康体检')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '作业人员职业健康体检')
                		{
                			var record = dataStore.getAt(rowIndex);
            		         var Num = rowIndex+1;
            		         //alert(record.get('pName'));
            		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
            		                    "详细信息</center></h1>"+
            		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
            		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
            		                    "</td><td width=\"15%\" style=\"padding:5px;\">分包单位名称</td><td width=\"40%\">" + record.get("repoter") + 
            		                    "</td></tr><tr><td style=\"padding:5px;\">岗位/工种</td><td>" + record.get('gangwei') + 
                                       "</td><td style=\"padding:5px;\">姓名</td><td>" + record.get('name') + 
                                       "</tr><tr></td><td style=\"padding:5px;\">性别</td><td>" + record.get('sex') + 
                                       "</td><td style=\"padding:5px;\"> 体检时间</td><td>" +  record.get('tijiantime')+
                                       "</tr><tr></td><td style=\"padding:5px;\">体检医院</td><td>" + record.get('tijianplace') + 
                                       "</td><td style=\"padding:5px;\">体检结果</td><td>" + record.get('tijianresult')+
                                       "</tr><tr></td><td style=\"padding:5px;\">岗前/岗中/岗后</td><td colspan=\"3\">" + record.get('type') + "</td></tr>";
            		         html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
              		        
            		         var misfile = record.get('Accessory').split('*');
//            		        var foldermis;
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
                        Ext.create('Ext.window.Window', 
                        {
                           title: '查看详情',
                           titleAlign: 'center',
                           height: 350,
                           width: 700,
                           closeAction: 'destroy',
                           layout: 'fit',
                           autoScroll: true,
                           tools: 
                           [{
                               type: 'print',
                               handler: function() 
                               {
                            	   	 var oPop = window.open('','oPop');                                
	                              	 bdhtml = html_str;                              
	                              	 oPop.document.body.innerHTML = bdhtml;                        
	                              	 oPop.print();  
                               }
                           }],
                           html: html_str
                       }).show();
                	}
                	
//end-------------------------------------------------------------------------------------------------------------------------------                	
       	        }
        	}
        });
        panel.add(gridDT);
        container.add(panel).show();
    }

});