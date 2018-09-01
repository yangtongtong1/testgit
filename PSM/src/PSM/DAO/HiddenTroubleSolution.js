var tn;

Ext.define('HiddenTroubleSolutionGrid', {
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
        var projectName = config.projectName;
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
						$.getJSON('HiddenTroubleSolutionAction!importExcel', { fileName: fileName, type : title, projectName: projectName },
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
		
		
//jianglf---------------------------------------------------------------------		
		//在uploadPanel中添加已有文件列表
		var insertFileToList = function()
      	{
        	var info_url = '';
        	var delete_url = '';
        	if(title == '项目危险源辨识与风险管控动态公示')
        	{
        		info_url = 'HiddenTroubleSolutionAction!getFileInfo';
        		delete_url = 'HiddenTroubleSolutionAction!deleteOneFile';
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
       		if(title == '项目危险源辨识与风险管控动态公示')
       		{
       			deleteAllUrl = "HiddenTroubleSolutionAction!deleteAllFile";
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
        	
        	if(title == '项目危险源辨识与风险管控动态公示')
        	{	        	
        		actionURL = 'HiddenTroubleSolutionAction!addWeixianyuan?userName=' + user.name + "&userRole=" + user.role ;  
        		uploadURL = "UploadAction!execute";
        		items = items_Weixianyuan;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addWeixianyuan',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addWeixianyuan', title: '新增项目', items: [forms]});
        	}    

        }
        
        
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
        				
        			if(title == '项目危险源辨识与风险管控动态公示')
                	{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'HiddenTroubleSolutionAction!editWeixianyuan?userName=' + user.name + "&userRole=" + user.role ;  
        				items = items_Weixianyuan;
              
        			createForm({
            			autoScroll: true,
            			action: 'editWeixianyuan',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editWeixianyuan', title: '修改项目', items: [forms]});
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
    					if(title == '项目危险源辨识与风险管控动态公示')
                    	{$.getJSON(encodeURI("HiddenTroubleSolutionAction!deleteWeixianyuan?userName=" + user.name + "&userRole=" + user.role),
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

        var combo2 = new Ext.data.ArrayStore({
            fields: ['title', 'showkind'],
            data: [['1', '蓝色（1级）'], 
            	['2', '蓝色（2级）'], 
            	['3', '黄色（3级）'], 
            	['4', '橙色（4级）'], 
            	['5', '红色（5级）']]
          });

		var items_Weixianyuan = [{
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
                    anchor:'100%',
                    name: 'ID',
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
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype: 'datefield',
                    fieldLabel: '作业时间',
                    format : 'Y-m-d',
                    anchor:'100%',
                    labelAlign: 'right',
                    allowBlank: true,
                    name: 'jobTime'
                },{
                	xtype:'textfield',
                    fieldLabel: '作业地点',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'jobLocation'
                },{
                	xtype:'textfield',
                    fieldLabel: '作业内容',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'jobContent'
                },{
                	xtype:'textfield',
                    fieldLabel: '主要风险',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'mainRisk'
                
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'combobox',
                    fieldLabel: '风险等级（预警颜色）',
                    store: combo2,
                    valueField: 'showkind',
                    displayField: 'showkind',
//                    labelWidth: 120,
                    editable: false,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'riskRank'
                },{
                	xtype:'textfield',
                    fieldLabel: '工作负责人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'jobMan'
                },{
                	xtype:'textfield',
                    fieldLabel: '现场监督人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'jobJiandu'
                }]
	        }]
	    },{
            xtype:'textarea',
            fieldLabel: '预控措施',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'preAction'
	    },{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: {
	        	type:'hbox',
	        },
	        width: 150,
	        items:[
	        	{
	            	xtype:'box',
	                width:20,
	                height:50,
	                allowBlank: false,
	                style:'margin-left:5px;', 
	                html:'<img src="./Images/ims/icon/提示图片.png">',
	                name: 'tipimg',
	            },
	        	{
	            	xtype:'tbtext',
	                text: '请点击上传项目危险源辨识与风险管控动态公示牌',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]            
        
        
        
        
        var store_Weixianyuan = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'jobTime'},
                     { name: 'jobLocation'},
                     { name: 'jobContent'},
                     { name: 'mainRisk'},
                     { name: 'riskRank'},
                     { name: 'preAction'},
                     { name: 'jobMan'},
                     { name: 'jobJiandu'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('HiddenTroubleSolutionAction!getWeixianyuanListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + title+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       
        if(title == '项目危险源辨识与风险管控动态公示')
        {	
        	dataStore = store_Weixianyuan;
        	queryURL = 'HiddenTroubleSolutionAction!getWeixianyuanListSearch?userName=' + user.name + "&userRole=" + user.role + "&type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '作业时间', dataIndex: 'jobTime', align: 'center', width: 150},
            	    { text: '作业地点', dataIndex: 'jobLocation', align: 'center', width: 200},
            	    { text: '作业内容', dataIndex: 'jobContent', align: 'center', width: 150},
            	    { text: '主要风险', dataIndex: 'mainRisk', align: 'center', width: 150},
            	    { text: '风险等级（预警颜色）', dataIndex: 'riskRank', align: 'center', width: 150},
            	    { text: '预控措施', dataIndex: 'preAction', align: 'center', width: 150},
            	    { text: '工作负责人', dataIndex: 'jobMan', align: 'center', width: 150},
            	    { text: '现场监督人', dataIndex: 'jobJiandu', align: 'center', width: 150},
            	    { text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	//tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add(btnScan);
        		tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add(btnImport);
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        			tbar.remove(btnImport);
        		}
        }
      

     
        
        

        
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
                			 
                			    
                			    case "addWeixianyuan":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                			    case "editWeixianyuan": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
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
          	                	},
          	                	failure: function(form, action){
          	                		//alert(action.result.msg);
//          	                		Ext.Msg.alert('警告',action.result.msg);
          	                	}
      	                	})
      	                	this.up('window').close();  	
      	                }
      	                else{//forms.form.isValid() == false
      	                	if(config.action == "addWeixianyuan" || config.action == "editWeixianyuan")
                    		{
                    			var brief = forms.getForm().findField('Yijian');
                    			var brieftext = brief.getValue();
                    			if(brieftext.length>500)
                    			{
                    				Ext.Msg.alert('警告','不能超过500个字符！');
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
                		 if (config.action == 'editWeixianyuan') 
                         {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                         }
                		
                		 
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
        	 if (config.action == 'editWeixianyuan') 
             {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
             }
        	
        };       
        
        //创建装载formPanel的窗体，由工具栏按钮点击显示
        var showWin = function (config) {
        	var width = 800;
        	var height = 600;
 //jianglf--------------------------------------------------------------       	
//        	if(title == '上级检查问题整改及回复'||title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查')
        	if(title == '项目危险源辨识与风险管控动态公示')
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
                	if(title == '项目危险源辨识与风险管控动态公示')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '项目危险源辨识与风险管控动态公示')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">作业时间</td><td width=\"40%\">" + record.get("jobTime") + 
             		                    "</td></tr><tr><td style=\"padding:5px;\">作业地点</td><td>" + record.get('jobLocation') + 
                                        "</td><td style=\"padding:5px;\">作业内容</td><td>" + record.get('jobContent') + 
                                        "</tr><tr></td><td style=\"padding:5px;\">主要风险</td><td>" + record.get('mainRisk') + 
                                        "</td><td style=\"padding:5px;\"> 风险等级（预警颜色）</td><td>" +  record.get('riskRank')+ "</td></tr>"+
                                        
                                        "</tr><tr></td><td style=\"padding:5px;\">工作负责人</td><td>" + record.get('jobMan') + 
                                        "</td><td style=\"padding:5px;\"> 现场监督人</td><td>" +  record.get('jobJiandu')+ "</td></tr>"+
                                        "</tr><tr></td><td style=\"padding:5px;\">预控措施</td><td  align=\"left\"  colspan=\"3\">" + record.get('preAction') +  "</td></tr>";
             		         
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
                	
                	
                	
       	        }
        	}
        });
        panel.add(gridDT);
        container.add(panel).show();
    }

});