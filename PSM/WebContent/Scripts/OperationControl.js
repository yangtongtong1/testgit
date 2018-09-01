var tn;

Ext.define('OperationControlGrid', {
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
        var projectName = config.projectName;
        var param;		//存放gridDT选择的行
        
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
            for (var i = 0; i < selRecs.length; i++) 
            {
//            	if(title == '方案实施')
//            	    keyIDs.push(selRecs[i].data.ID);
//            	if(title == '安全施工作业票')
//            		keyIDs.push(selRecs[i].data.ID);
//            	if(title == '交通安全')
//            		keyIDs.push(selRecs[i].data.ID);
//            	if(title == '消防安全')
//            		keyIDs.push(selRecs[i].data.ID);
//            	if(title == '个人职业病防护用品')
//            		keyIDs.push(selRecs[i].data.ID);
//            	if(title == '职业健康防护设施')
//            		keyIDs.push(selRecs[i].data.ID);
//            	if(title == '环境保护专项措施')
//            		keyIDs.push(selRecs[i].data.ID);
//            	if(title == '节能减排专项措施')
//            		keyIDs.push(selRecs[i].data.ID);
//            	if(title == '安全标志、标识')
//            		keyIDs.push(selRecs[i].data.ID);
//            	if(title == '特种作业人员')
            		keyIDs.push(selRecs[i].data.ID);
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
            upload_url: 'UploadAction!execute',
            anchor: '100%'
		})
		
		//在uploadPanel中添加已有文件列表
		var insertFileToList = function()
      	{
        	var info_url = '';
        	var delete_url = '';
    		info_url = 'OperationControlAction!getFileInfo';
    		delete_url = 'OperationControlAction!deleteOneFile';
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
       		if(title == '方案实施' || title == '安全施工作业票' ||title == '环境保护专项措施' || title == '职业危害现状分析'|| title == '职业危害因素监测'|| title == '安全文明施工总体策划编审批'|| title == '安全文明施工总体策划交底'|| title == '特种设备作业人员'
       			|| title == '节能减排专项措施'|| title == '特种作业人员'|| title=='安全生产协议'|| title=='建筑机械、施工机具管理台账'|| title=='施工组织设计'|| title=='专项方案编审批'|| title=='分包方考核与评价'|| title=='环境保护费用管理'|| title=='节能减排费用管理'|| title=='其他作业')
       		{
       			deleteAllUrl = "OperationControlAction!deleteAllFile";
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
       	var DeleteFile = function(action)
       	{
       		var ppid = "";
       		var fileName = null;
  	        fileName = getFileName();
  	        if(action == "addSecureJobSlip"||action == "editSecureJobSlip"||action == "addSchemeimple"||action == "editSchemeimple"||action == "addEnviromentPro"||action=="addFeemanagement"||action == "editFeemanagement"
  	        	||action == "editEnviromentPro" ||action=="addSaveEnergy"||action == "editSaveEnergy" ||action == "editTezhongpeople" || action=="addTezhongpeople"
  	        	||action=="addSafetyprotocal"||action == "editSafetyprotocal"||action=="addConstructionelec"||action == "editConstructionelec"||action=="addConstructdesign"||action == "editConstructdesign"
  	        	||action=="addProapproval"||action == "editProapproval"||action=="addOccuevaluation"||action == "editOccuevaluation"||action=="addOccuOccumonitor"||action == "editOccuOccumonitor"
  	        	||action=="addOccuSafepromonitor"||action == "editOccuSafepromonitor"||action=="addOccuSafeprojd"||action == "editOccuSafeprojd"||action=="addOccuTezhongsbpeople"||action == "editOccuTezhongsbpeople"
  	        	||action=="addOccuFbtestjudge"||action == "editOccuFbtestjudge"	||action=="addOtherjob"||action == "editOtherjob")
  	        {
				if(action == "editSecureJobSlip" || action == "editSchemeimple"||action == "editEnviromentPro" || action =="editSaveEnergy"||action == "editTezhongpeople"|| action =="editSafetyprotocal"
					|| action =="editConstructionelec"||action == "editConstructdesign"|| action == "editProapproval"|| action == "editOccuevaluation"|| action == "editOccuOccumonitor"
					||action == "editOccuSafepromonitor"||action == "editOccuSafeprojd"||action == "editOccuTezhongsbpeople"||action == "editOccuFbtestjudge"||action == "editFeemanagement"||action == "editOtherjob")
				{               			
					ppid = selRecs[0].data.ID;			
				}		
				deleteFile(style, fileName, ppid);
				uploadPanel.store.removeAll();
  	        }
       	}
       	
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
       	
       	var store_Feemanagement = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Typei'},
                     { name: 'Unit'},
                     { name: 'Fee'},
                     { name: 'Usea'},
                     { name: 'Jperson'},
                     { name: 'Dperson'},
                     { name: 'Time'},
                     { name: 'Content'},
                     { name: 'Title'},
                     { name: 'Accessory'},
                     { name : 'Year'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getFeemanagementListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Feemanagement = [{
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
                    fieldLabel: '《安全用电技术措施》',
                    labelWidth: 500,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype : 'combo',  
                    fieldLabel : '费用分类',
                    labelAlign: 'right',
                    name : 'Typei',  
                    anchor : '100%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['培训费用', '培训费用'],  
                                        ['宣传费用', '宣传费用'],  
                                        ['考核、表彰奖励费用', '考核、表彰奖励费用'],  
                                        ['其他管理费用', '其他管理费用'],  
                                        ['技术创新科研费用', '技术创新科研费用'],  
                                        ['设备更新及改造费用', '设备更新及改造费用'],  
                                        ['其他环评水保措施落实费用', '其他环评水保措施落实费用']]  
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype:'textfield',
                    fieldLabel: '使用单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Unit'
                },{
                	xtype:'textfield',
                    fieldLabel: '登记人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Dperson'
                },{
                	xtype:"datefield",
                	fieldLabel: '审批时间',
                	afterLabelTextTpl: required,
                	//labelWidth: 200,
                	labelAlign: 'right',
                	format:"Y-m-d",
                	name: 'Time',
                	anchor:'100%',
                	allowBlank: false 
                   }
                ]},{
	            	xtype: 'container',
		            flex: 1,
		            layout: 'anchor',
		          items:[{
	            
                	xtype:'textfield',
                    fieldLabel: '金额（元）',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Fee'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '费用用途',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Usea'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '经办人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Jperson'
                }]
                }]
                },{
                	xtype:'textarea',
                    fieldLabel: '备注',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Content'
                },
                uploadPanel
                ]
                
	     
        	
        
       	
       	var store_Constructionelec = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Type'},
                     { name: 'Name'},
                     { name: 'Num'},
                     { name: 'Singlepower'},
                     { name: 'Sumpower'},
                     { name: 'Factory'},
                     { name: 'Shiyong'},
                     { name: 'Outtime'},
                     { name: 'Intime'},
                     { name: 'Plantime'},
                     { name: 'Realtime'},
                     { name: 'Status'},
                     { name: 'Approve'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getConstructionelecListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Constructionelec = [{
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
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
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
                    fieldLabel: '名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '规格型号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Type'
                },{
                	xtype:'textfield',
                    fieldLabel: '台数',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Num'
                },{
                	xtype:'textfield',
                    fieldLabel: '单机功率',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Singlepower'
                },{
                	xtype:'textfield',
                    fieldLabel: '总功率',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Sumpower'
                },{
                	xtype:'textfield',
                    fieldLabel: '生产厂家',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Factory'
                },{
                	xtype : 'combo',  
                    fieldLabel : '设备使用方式',
                    labelAlign: 'right',
                    name : 'Shiyong',  
                    anchor : '100%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['租赁', '租赁'],  
                                        ['分包商自带', '分包商自带'],  
                                        ['甲供', '甲供']]  
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'datefield',
                    fieldLabel: '出场时间',
                    format:'Y-m-d',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Outtime'
                },{
                	xtype:'datefield',
                    fieldLabel: '进场时间',
                    format:'Y-m-d',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Intime'
                },{
                	xtype:'datefield',
                    fieldLabel: '计划出场时间',
                    format:'Y-m-d',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Plantime'
                },{
                	xtype:'datefield',
                    fieldLabel: '实际出场时间',
                    format:'Y-m-d',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Realtime'
                },{
                     xtype:'textfield',
                     fieldLabel: '使用状态',
                      //labelWidth: 120,
                      labelAlign: 'right',
                      anchor:'100%',
                      name: 'Status'
                },{
                	xtype : 'combo',  
                    fieldLabel : '是否组织进场检查验收',
                    labelAlign: 'right',
                    name : 'Approve',  
                    anchor : '100%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['是', '是'],  
                                        ['否', '否']]
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                }]
	        }]
	    },
        	uploadPanel
        ]
       	
       	var store_Safetyprotocal = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Type'},
                     { name: 'Fbname'},
                     { name: 'Name'},
                     { name: 'Date'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getSafetyprotocalListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
    	
       	var items_Safetyprotocal = [{
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
                    anchor:'95%',
                    name: 'ID',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'textfield',
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
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
                	xtype : 'combo',  
                    fieldLabel : '协议类型',
                    labelAlign: 'right',
                    name : 'Type',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['安健环管理协议', '安健环管理协议'],  
                                        ['调试运行安全协议', '调试运行安全协议'],  
                                        ['安全生产告知函', '安全生产告知函']]  
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '签订时间',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Date'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '分包单位名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Fbname'
                },{
                	xtype:'textfield',
                    fieldLabel: '协议名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Name'
                }]
	        }]
	    },
        	uploadPanel
	    ]
  
       	var store_SecureJobSlip = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'WorkPlace'},
                     { name: 'WorkContent'},
                     { name: 'WorkTime'},
                     { name: 'DangerSource'},
                     { name: 'Principle'},
                     { name: 'WorkerNum'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getSecureJobSlipListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_secureJobSlip = [{
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
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
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
                    fieldLabel: '作业地点、范围',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'WorkPlace'
                },{
                	xtype:'textfield',
                    fieldLabel: '作业内容',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'WorkContent'
                },{
                	xtype:'textfield',
                    fieldLabel: '作业时间',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'WorkTime'
                
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '危险源',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'DangerSource'
                },{
                	xtype:'textfield',
                    fieldLabel: '单项工作负责人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Principle'
                },{
                	
                        xtype:'textfield',
                        fieldLabel: '施工人数',
                        //labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'100%',
                        name: 'WorkerNum'
                }]
	        }]
	    },
        	uploadPanel
        ]
//jianglf--------------------------------------------------------------------------------------------------------------------       	
       	var store_Schemeimple = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'SchemeName'},
                     { name: 'Supervise'},
                     { name: 'Acceptance'},
                     { name: 'Jd'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getSchemeimpleListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Schemeimple = [{
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
                	xtype:'textfield',
                    fieldLabel: '专业施工方案名称',
                    labelWidth: 120,
                    readOnly: true,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'SchemeName'
                },{
                	xtype:'textfield',
                    fieldLabel: '旁站监督',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Supervise'
                },{
                	xtype:'textfield',
                    fieldLabel: '方案验收',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Acceptance'
                },{
                	xtype : 'combo',  
                    fieldLabel : '方案交底',
                    labelWidth: 120,
                    labelAlign: 'right',
                    name : 'Jd',  
                    anchor : '100%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['已交底', '已交底'],  
                                        ['未交底', '未交底']]  
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                
                }]
	        }]
	    },
        	uploadPanel
        ]
//end--------------------------------------------------------------------------------------------------------------       	
       	var store_FireSafety = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'Model'},
                     { name: 'Num'},
                     { name: 'Department'},
                     { name: 'AcqDate'},
                     { name: 'ChangeDate'},
                     { name: 'Place'},
                     { name: 'ChargePerson'},
                     { name: 'CheckPeriodically'},
                     { name: 'ProjectName'},
                     { name: 'CheckResult'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getFireSafetyListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var store_TransportSafety = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'CarNum'},
                     { name: 'CarName'},
                     { name: 'Department'},
                     { name: 'License'},
                     { name: 'Driver'},
                     { name: 'DriverNum'},
                     { name: 'ProjectName'},
                     { name: 'Maintenance'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getTransportSafetyListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_fireSafety = [{
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
                    fieldLabel: '消防设备名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '型号及规格',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Model'
                },{
                	xtype:'textfield',
                    fieldLabel: '数量',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Num'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '配置地点',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Place'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '是否定期检查',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'CheckPeriodically'
                
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Department'
                },{
                	xtype:'datefield',
                    fieldLabel: '购置日期',
                    format:'Y-m-d',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'AcqDate'
                },{
                	
                        xtype:'datefield',
                        fieldLabel: '更换日期',
                        //labelWidth: 120,
                        labelAlign: 'right',
                        format:'Y-m-d',
                        anchor:'100%',
                        name: 'ChangeDate'
                },{
                	xtype:'textfield',
                    fieldLabel: '责任人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'ChargePerson'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '检查结果',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'CheckResult'
                
                }]
	        }]
	    }
        ]
       	
       	var items_transportSafety = [{
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
                    fieldLabel: '车辆编号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'CarNum'
                },{
                	xtype:'textfield',
                    fieldLabel: '机动车车辆',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'CarName'
                },{
                	xtype:'textfield',
                    fieldLabel: '机动车所属单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Department'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '机动车行驶证',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'License'
                
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '指定驾驶人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Driver'
                },{
                	xtype:'textfield',
                    fieldLabel: '驾驶人驾驶证号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'DriverNum'
                },{
                	
                        xtype:'textfield',
                        fieldLabel: '是否定期检查维保',
                        //labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'100%',
                        name: 'Maintenance'
                }]
	        }]
	    }
        ]
       	
       	var store_ODHequipment = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'Model'},
                     { name: 'Department'},
                     { name: 'Num'},
                     { name: 'BuyTime'},
                     { name: 'SerialNumber'},
                     { name: 'Place'},
                     { name: 'ProjectName'},
                     { name: 'Responsibility'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getODHequipmentListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var store_PODgoods = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'DeliveryTime'},
                     { name: 'Num'},
                     { name: 'PersonName'},
                     { name: 'Department'},
                     { name: 'TimeLimit'},
                     { name: 'Autograph'},
                     { name: 'ProjectName'},
                     { name: 'Comment'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getPODgoodsListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var store_EnviromentPro = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'Department'},
                     { name: 'Time'},
                     { name: 'Approval'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getEnviromentProListDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var store_SaveEnergy = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'Department'},
                     { name: 'Time'},
                     { name: 'Approval'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getSaveEnergyListDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
//jianglf--------------------------------------
       	var store_Constructdesign = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Type'},
                     { name: 'Unit'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getConstructdesignListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Constructdesign = [{
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
                    fieldLabel: '编审批记录',
                    labelWidth: 500,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype : 'combo',  
                    fieldLabel : '施工组织设计类型',
                    labelWidth: 120,
                    labelAlign: 'right',
                    name : 'Type',  
                    anchor : '100%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['施工组织总设计', '施工组织总设计'],  
                                        ['分包单位施工组织设计', '分包单位施工组织设计']]  
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype:'textfield',
                    fieldLabel: '编制单位',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Unit'
                }]
	        }]
	    },
        	uploadPanel
        ]
       	
       	var store_Proapproval = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'Type'},
                     { name: 'Unit'},
                     { name: 'Time'},
                     { name: 'Approval'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getProapprovalListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Proapproval = [{
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
                    anchor:'95%',
                    name: 'ID',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'textfield',
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
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
                	xtype : 'combo',  
                    fieldLabel : '是否按要求审批',
                    labelAlign: 'right',
                    name : 'Approval',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['分包单位审批', '分包单位审批'],  
                                        ['总包项目部审批', '总包项目部审批'],  
                                        ['监理单位审批', '监理单位审批'],  
                                        ['否', '否']]  
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                
                },{
                	xtype:"datefield",
	                fieldLabel: '编制时间',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'95%',
	                allowBlank: false 
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '专项方案名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Name'
                },{
                	xtype : 'combo',  
                    fieldLabel : '工程类别',
                    labelAlign: 'right',
                    name : 'Type',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['超过一定规模的危险性较大的分部分项工程', '超过一定规模的危险性较大的分部分项工程'],  
                                        ['危险性较大的分部分项工程', '危险性较大的分部分项工程'],  
                                        ['其他工程', '其他工程']]  
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                }]
	        }]
	    },{
	    	xtype:'textfield',
            fieldLabel: '编制单位',
            //labelWidth: 120,
            labelAlign: 'right',
            anchor:'95%',
            name: 'Unit'
	    },
        	uploadPanel
	    ]
       	
    	var store_Occuevaluation = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'No'},
                     { name: 'Date'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getOccuevaluationListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Occuevaluation = [{
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
                    fieldLabel: '附件',
                    labelWidth: 500,
                    labelAlign: 'right',
                    anchor:'100%',
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
                	xtype:"datefield",
	                fieldLabel: '评价时间',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Date',
	                anchor:'100%',
	                allowBlank: false 
                },{
                	xtype:'textfield',
                    fieldLabel: '评价序号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'No'
                
                }]
	        }]
	    },
        	uploadPanel
        ]
       	
       	var store_Occumonitor = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'No'},
                     { name: 'Factor'},
                     { name: 'Time'},
                     { name: 'Result'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getOccumonitorListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Occumonitor = [{
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
                    fieldLabel: '附件',
                    labelWidth: 500,
                    labelAlign: 'right',
                    anchor:'100%',
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
                    fieldLabel: '监测序号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'No'
                },{
                	xtype:'textfield',
                    fieldLabel: '职业危害因素',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Factor'
                },{
                	xtype:"datefield",
	                fieldLabel: '监测时间',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'100%',
	                allowBlank: false 
                
                },{
                	xtype:'textfield',
                    fieldLabel: '监测结果',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Result'
                
                }
                ]
	        }]
	    },
        	uploadPanel
        ]
       	
       	var store_Otherjob = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'Time'},
                     { name: 'Unit'},
                     { name: 'Record'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getOtherjobListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Otherjob = [{
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
                    fieldLabel: '附件',
                    labelWidth: 500,
                    labelAlign: 'right',
                    anchor:'100%',
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
                    fieldLabel: '作业名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:"datefield",
	                fieldLabel: '作业时间',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'100%',
	                allowBlank: false 
                },{
                	xtype:'textfield',
                    fieldLabel: '作业单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Unit'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '作业安全记录',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Record'
                
                }
                ]
	        }]
	    },
        	uploadPanel
        ]
       	
       	var store_Safepromonitor = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Bzperson'},
                     { name: 'Bztime'},
                     { name: 'Shperson'},
                     { name: 'Shtime'},
                     { name: 'Pzperson'},
                     { name: 'Pztime'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getSafepromonitorListDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Safepromonitor = [{
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
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
	            	xtype:'textfield',
                    fieldLabel: '编制人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Bzperson'
                },{
                	xtype:'textfield',
                    fieldLabel: '审核人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Shperson'
                },{
                	xtype:'textfield',
                    fieldLabel: '批注人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Pzperson'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'datefield',
                    fieldLabel: '编制时间',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Bztime',
	                format:"Y-m-d",
	                allowBlank: false
                },{
                	xtype:'datefield',
                    fieldLabel: '审核时间',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Shtime',
	                format:"Y-m-d",
	                allowBlank: false
                },{
                	
                	xtype:'datefield',
                    fieldLabel: '批准时间',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Pztime',
	                format:"Y-m-d",
	                allowBlank: false
                }]
	        }]
	    },
        	uploadPanel
        ]
       	
    	var store_Safeprojd = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Person'},
                     { name: 'Jdperson'},
                     { name: 'Time'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getSafeprojdListDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Safeprojd = [{
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
                    fieldLabel: '附件',
                    labelWidth: 500,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'textfield',
                    fieldLabel: '交底人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Person'
                },{
                	xtype:'textfield',
                    fieldLabel: '交底对象',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Jdperson'
                },{
                	xtype:"datefield",
	                fieldLabel: '交底时间',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'100%',
	                allowBlank: false 
                
                }
                ]
	        }]
	    },
        	uploadPanel
        ]
//end------------------------------------------
       	
//jianglf---------------------------------------------------------
       	var store_Tezhongsbpeople = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'FbName'},
                     { name: 'Type'},
                     { name: 'Project'},
                     { name: 'Name'},
                     { name: 'Gender'},
                     { name: 'CardNo'},
                     { name: 'BeginTime'},
                     { name: 'ValidTime'},
                     { name: 'Unit'},
                     { name: 'Peixun'},
                     { name: 'Peixunnr'},
                     { name: 'Ps'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getTezhongsbpeopleListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Tezhongsbpeople = [{
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
                    fieldLabel: '附件',
                    labelWidth: 500,
                    labelAlign: 'right',
                    anchor:'100%',
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
                    fieldLabel: '分包单位名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'FbName'
                },{
                	xtype:'textfield',
                    fieldLabel: '作业种类',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Type'
                },{
                	xtype:'textfield',
                    fieldLabel: '作业项目',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Project'
                },{
                	xtype:'textfield',
                    fieldLabel: '姓名',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype : 'combo',  
                    fieldLabel : '性别',
                    labelAlign: 'right',
                    name : 'Gender',  
                    anchor : '100%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['男', '男'],  
                                        ['女', '女']]  
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype:'textfield',
                    fieldLabel: '特种设备作业人员证编号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'CardNo'
                },{
                	xtype:"datefield",
	                fieldLabel: '发证日期',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'BeginTime',
	                anchor:'100%',
	                allowBlank: false 
                
                },{
                	xtype:"datefield",
	                fieldLabel: '有效期至',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'ValidTime',
	                anchor:'100%',
	                allowBlank: false 
                
                },{
                	xtype:'textfield',
                    fieldLabel: '发证机关',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Unit'
                },{
                	xtype:'textarea',
                    fieldLabel: '备注',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    height: 75,
                    anchor:'100%',
                    name: 'Ps'
                }
                ]
	        }]
	    },
        	uploadPanel
        ]
       	
       	var store_Fbtestjudge = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'No'},
                     { name: 'Tester'},
                     { name: 'Time'},
                     { name: 'Result'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getFbtestjudgeListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var items_Fbtestjudge = [{
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
                    fieldLabel: '附件',
                    labelWidth: 500,
                    labelAlign: 'right',
                    anchor:'100%',
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
                    fieldLabel: '考核序号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'No'
                },{
                	xtype:'textfield',
                    fieldLabel: '考核分包方',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Tester'
                },{
                	xtype:"datefield",
	                fieldLabel: '考核时间',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'100%',
	                allowBlank: false 
                
                },{
                	xtype:'textfield',
                    fieldLabel: '考核结果',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Result'
                }
                ]
	        }]
	    },
        	uploadPanel
        ]
//end-------------------------------------------------------------       	
       	var store_SecuritySymbol = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'Num'},
                     { name: 'Department'},
                     { name: 'InstallTime'},
                     { name: 'InstallPlace'},
                     { name: 'Responsibility'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getSecuritySymbolListDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
//jianglf------------------------------------------------------------------------------------        
        var store_Tezhongpeople = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'FbName'},
                     { name: 'Type'},
                     { name: 'Name'},
                     { name: 'Sex'},
                     { name: 'CardName'},
                     { name: 'CardTime'},
                     { name: 'UseTime'},
                     { name: 'CardNo'},
                     { name: 'CardPlace'},
                     { name: 'Ps'},
                     { name: 'Peixun'},
                     { name: 'Peixunnr'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('OperationControlAction!getTezhongpeopleListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        var items_Tezhongpeople = [{
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
                    anchor:'95%',
                    name: 'ID',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'textfield',
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
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
	            	fieldLabel: '分包单位名称',//labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'FbName' 
                },{
	 	            xtype:'textfield',
                    fieldLabel: '作业类别',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Type'                            
	 	         },
                	{
                	xtype:'textfield',
                    fieldLabel: '姓名',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '性别',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Sex'
                },{
                	xtype:'textfield',
                    fieldLabel: '准操项目',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'CardName'
                },{
                	xtype:"datefield",
	                fieldLabel: '发证日期',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'CardTime',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:"datefield",
	                fieldLabel: '有效期至',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'UseTime',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:'textfield',
                    fieldLabel: '特种作业操作证编号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'CardNo'
                },{
                	xtype:'textfield',
                    fieldLabel: '发证机关',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'CardPlace'
                },{
                	xtype:'textfield',
                    fieldLabel: '是否参加培训',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Peixun',
                    hidden:true
                },{
                	xtype:'textarea',
                    fieldLabel: '备注',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    height:75,
                    name: 'Ps'
                }]
	        }]
	    },
        	uploadPanel
        ]
//end------------------------------------------------------------------------------       	
       	var items_enviromentPro = [{
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
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'textfield',
                    fieldLabel: '环境影响专项措施名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '编制时间',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Time'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '编制单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Department'
                },{
                	xtype:'textfield',
                    fieldLabel: '是否要求审批',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Approval'
                }]
	        }]
	    },
        	uploadPanel
        ]
       	
       	var items_saveEnergy = [{
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
                    fieldLabel: '附件',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'textfield',
                    fieldLabel: '节能减排专项措施名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '编制时间',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Time'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '编制单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Department'
                },{
                	xtype:'textfield',
                    fieldLabel: '是否要求审批',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Approval'
                }]
	        }]
	    },
        	uploadPanel
        ]
       	
    	var items_oDHequipment = [{
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
                    fieldLabel: '防护设施名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '制造单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Department'
                },{
                	xtype:'textfield',
                    fieldLabel: '购买时间',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'BuyTime'
                },{
                	xtype:'textfield',
                    fieldLabel: '使用地点',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Place'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '型号及规格',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Model'
                },{
                	xtype:'textfield',
                    fieldLabel: '数量',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Num'
                },{
                	xtype:'textfield',
                    fieldLabel: '编号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'SerialNumber'
                },{
                	xtype:'textfield',
                    fieldLabel: '责任人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Responsibility'
                }]
	        }]
	    }
        ]
       	
       	var items_securitySymbol = [{
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
                    fieldLabel: '安全标志、标识名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Department'
                },{
                	xtype:'textfield',
                    fieldLabel: '安装地点',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'InstallPlace'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '数量',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Num'
                },{
                	xtype:'textfield',
                    fieldLabel: '安装时间',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'InstallTime'
                },{
                	xtype:'textfield',
                    fieldLabel: '责任人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Responsibility'
                }]
	        }]
	    },
        	uploadPanel
        ]
       	
       	var items_pODgoods = [{
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
                    fieldLabel: '职业病防护用品名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '数量',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Num'
                },{
                	xtype:'textfield',
                    fieldLabel: '所属单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Department'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'datefield',
                    fieldLabel: '发放时间',
                    format:"Y-m-d",
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'DeliveryTime'
                },{
                	xtype:'textfield',
                    fieldLabel: '领取人姓名',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'PersonName'
                },{
                	xtype:'textfield',
                    fieldLabel: '使用期限',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'TimeLimit'
                }]
	        }]
	    },{
            xtype:'textarea',
            fieldLabel: '备注',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Comment'
	    }
        ]
       	
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
        	var selRecs = gridDT.getSelectionModel().getSelection();
			var accessory = selRecs[0].data.Accessory;
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
        	if(title == '方案实施')
        	{	        	
        		actionURL = 'OperationControlAction!addSchemeimple?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Schemeimple;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addSchemeimple',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addSchemeimple', title: '新增项目', items: [forms]});
        	}
        	
        	if(((title == '节能减排费用管理')||(title == '环境保护费用管理')))
        	{	        	
        		actionURL = 'OperationControlAction!addFeemanagement?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Feemanagement;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addFeemanagement',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addFeemanagement', title: '新增项目', items: [forms]});
        	}
        	
        	if(title == '安全生产协议')
        	{	        	
        		actionURL = 'OperationControlAction!addSafetyprotocal?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Safetyprotocal;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addSafetyprotocal',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addSafetyprotocal', title: '新增协议', items: [forms]});
        	}
        	
        	if(title == '建筑机械、施工机具管理台账')
        	{	        	
        		actionURL = 'OperationControlAction!addConstructionelec?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Constructionelec;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addConstructionelec',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addConstructionelec', title: '新增协议', items: [forms]});
        	}
        	
        	if(title == '安全施工作业票')
        	{	        	
        		actionURL = 'OperationControlAction!addSecureJobSlip?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_secureJobSlip;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addSecureJobSlip',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addSecureJobSlip', title: '新增安全施工作业票', items: [forms]});
        	}
        	if(title == '消防设备设施管理台账')
        	{	        	
        		actionURL = 'OperationControlAction!addFireSafety?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_fireSafety;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addFireSafety',
	        	url: actionURL,
	        	items: items
	        });
        	//uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addFireSafety', title: '新增消防安全条目', items: [forms]});
        	}
        	if(title == '交通车辆管理台账')
        	{	        	
        		actionURL = 'OperationControlAction!addTransportSafety?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_transportSafety;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addTransportSafety',
	        	url: actionURL,
	        	items: items
	        });
        	//uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addTransportSafety', title: '新增交通安全条目', items: [forms]});
        	}
        	
        	if(title == '环境保护专项措施')
        	{	        	
        		actionURL = 'OperationControlAction!addEnviromentPro?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_enviromentPro;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addEnviromentPro',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addEnviromentPro', title: '新增环境保护专项措施', items: [forms]});
        	}
        	if(title == '节能减排专项措施')
        	{	        	
        		actionURL = 'OperationControlAction!addSaveEnergy?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_saveEnergy;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addSaveEnergy',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addSaveEnergy', title: '新增节能减排专项措施', items: [forms]});
        	}
        	
//jianglf-----------------------------------------------------
        	if(title == '施工组织设计')
        	{	        	
        		actionURL = 'OperationControlAction!addConstructdesign?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Constructdesign;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addConstructdesign',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addConstructdesign', title: '新增施工组织设计', items: [forms]});
        	}
        	
        	if(title == '专项方案编审批')
        	{	        	
        		actionURL = 'OperationControlAction!addProapproval?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Proapproval;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addProapproval',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addProapproval', title: '新增编审批', items: [forms]});
        	}
        	
        	if(title == '职业危害现状评价')
        	{	        	
        		actionURL = 'OperationControlAction!addOccuevaluation?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Occuevaluation;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addOccuevaluation',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addOccuevaluation', title: '新增评价', items: [forms]});
        	}
        	
        	if(title == '职业危害因素监测')
        	{	        	
        		actionURL = 'OperationControlAction!addOccumonitor?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Occumonitor;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addOccumonitor',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addOccumonitor', title: '新增监测', items: [forms]});
        	}
        	
        	if(title == '其他作业')
        	{	        	
        		actionURL = 'OperationControlAction!addOtherjob?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Otherjob;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addOtherjob',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addOtherjob', title: '新增作业', items: [forms]});
        	}
        	
        	if(title == '安全文明施工总体策划编审批')
        	{	        	
        		actionURL = 'OperationControlAction!addSafepromonitor?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Safepromonitor;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addSafepromonitor',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addSafepromonitor', title: '新增编审批', items: [forms]});
        	}
        	
        	if(title == '安全文明施工总体策划交底')
        	{	        	
        		actionURL = 'OperationControlAction!addSafeprojd?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Safeprojd;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addSafeprojd',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addSafeprojd', title: '新增交底', items: [forms]});
        	}
//end---------------------------------------------------------
        	
//jianglf-------------------------------------------------------------
        	if(title == '特种设备作业人员')
        	{	        	
        		actionURL = 'OperationControlAction!addTezhongsbpeople?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Tezhongsbpeople;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addTezhongsbpeople',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addSafeprojd', title: '新增人员', items: [forms]});
        	}
        	
        	if(title == '分包方考核与评价')
        	{	        	
        		actionURL = 'OperationControlAction!addFbtestjudge?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Fbtestjudge;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addFbtestjudge',
	        	url: actionURL,
	        	items: items
	        });
        	uploadPanel.upload_url = uploadURL;
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addFbtestjudge', title: '新增考核与评价', items: [forms]});
        	}
//end----------------------------------------------------------------        	
        	if(title == '职业健康防护设施')
        	{	        	
        		actionURL = 'OperationControlAction!addODHequipment?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_oDHequipment;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addODHequipment',
	        	url: actionURL,
	        	items: items
	        });
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addODHequipment', title: '新增职业健康防护设施', items: [forms]});
        	}
        	if(title == '安全标志、标识')
        	{	        	
        		actionURL = 'OperationControlAction!addSecuritySymbol?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_securitySymbol;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addSecuritySymbol',
	        	url: actionURL,
	        	items: items
	        });
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addSecuritySymbol', title: '新增安全标志、标识', items: [forms]});
        	}
        	if(title == '个人职业病防护用品')
        	{	        	
        		actionURL = 'OperationControlAction!addPODgoods?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_pODgoods;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addPODgoods',
	        	url: actionURL,
	        	items: items
	        });
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addPODgoods', title: '新增个人职业病防护用品', items: [forms]});
        	}
        	
        	if(title == '特种作业人员')
        	{	        	
        		actionURL = 'OperationControlAction!addTezhongpeople?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Tezhongpeople;
        	
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'addTezhongpeople',
	        	url: actionURL,
	        	items: items
	        });
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'addTezhongpeople', title: '新增特种作业人员', items: [forms]});
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
        		if(selRecs.length == 1 )
        		{
        			
        			if(((title == '节能减排费用管理')||(title == '环境保护费用管理')))
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editFeemanagement?userName=' + user.name + "&userRole=" + user.role ;  
        				items = items_Feemanagement;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editFeemanagement',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editFeemanagement', title: '修改项目', items: [forms]});
        			}
        			
        			if(title == '方案实施')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editSchemeimple?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Schemeimple;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editSchemeimple',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSchemeimple', title: '修改方案实施', items: [forms]});
        			}
        			if(title == '安全施工作业票')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editSecureJobSlip?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_secureJobSlip;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editSecureJobSlip',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSecureJobSlip', title: '修改安全施工作业票', items: [forms]});
        			}
        			
        			if(title == '安全生产协议')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editSafetyprotocal?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Safetyprotocal;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSafetyprotocal',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSafetyprotocal', title: '修改协议', items: [forms]});
        			}
        			
        			if(title == '建筑机械、施工机具管理台账')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editConstructionelec?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Constructionelec;
              
        			createForm({
            			autoScroll: true,
            			action: 'editConstructionelec',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editConstructionelec', title: '修改台账', items: [forms]});
        			}
        			
        			if(title == '交通车辆管理台账')
        			{
        				//insertFileToList();
        				actionURL = 'OperationControlAction!editTransportSafety?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_transportSafety;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editTransportSafety',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			//uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editTransportSafety', title: '修改交通安全条目', items: [forms]});
        			}
        			if(title == '消防设备设施管理台账')
        			{
        				//insertFileToList();
        				actionURL = 'OperationControlAction!editFireSafety?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_fireSafety;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editFireSafety',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			//uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editFireSafety', title: '修改台账', items: [forms]});
        			}
        			if(title == '环境保护专项措施')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editEnviromentPro?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_enviromentPro;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editEnviromentPro',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editEnviromentPro', title: '修改环境保护专项措施', items: [forms]});
        			}
        			if(title == '节能减排专项措施')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editSaveEnergy?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_saveEnergy;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editSaveEnergy',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaveEnergy', title: '修改节能减排专项措施', items: [forms]});
        			}
        			
//jianglf----------------------------------------------------
        			if(title == '施工组织设计')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editConstructdesign?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Constructdesign;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editConstructdesign',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editConstructdesign', title: '修改施工组织设计', items: [forms]});
        			}
        			
        			if(title == '专项方案编审批')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				var selRec = gridDT.getSelectionModel().getSelection();
        				var pName = selRec[0].data.Name;
        				actionURL = 'OperationControlAction!editProapproval?userName=' + user.name + "&userRole=" + user.role + "&pName=" +pName ;  
        				items = items_Proapproval;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editProapproval',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProapproval', title: '修改编审批', items: [forms]});
        			}
        			
        			if(title == '职业危害现状评价')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editOccuevaluation?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Occuevaluation;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editOccuevaluation',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editOccuevaluation', title: '修改评价', items: [forms]});
        			}
        			
        			if(title == '职业危害因素监测')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editOccumonitor?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Occumonitor;
              
        			createForm({
            			autoScroll: true,
            			action: 'editOccumonitor',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editOccumonitor', title: '修改监测', items: [forms]});
        			}
        			
        			if(title == '其他作业')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editOtherjob?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Otherjob;
              
        			createForm({
            			autoScroll: true,
            			action: 'editOtherjob',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editOtherjob', title: '修改作业', items: [forms]});
        			}
        			
        			if(title == '安全文明施工总体策划编审批')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editSafepromonitor?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Safepromonitor;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSafepromonitor',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSafepromonitor', title: '修改编审批', items: [forms]});
        			}
        			
        			if(title == '安全文明施工总体策划交底')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editSafeprojd?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Safeprojd;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSafeprojd',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSafeprojd', title: '修改交底', items: [forms]});
        			}
//end--------------------------------------------------------
        			
//jianglf-----------------------------------------------------------------
        			if(title == '特种设备作业人员')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editTezhongsbpeople?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Tezhongsbpeople;
              
        			createForm({
            			autoScroll: true,
            			action: 'editTezhongsbpeople',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editTezhongsbpeople', title: '修改人员', items: [forms]});
        			}
        			
        			if(title == '分包方考核与评价')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'OperationControlAction!editFbtestjudge?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Fbtestjudge;
              
        			createForm({
            			autoScroll: true,
            			action: 'editFbtestjudge',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editFbtestjudge', title: '修改考核与评价', items: [forms]});
        			}
//end----------------------------------------------------------------------        			
        			
        			if(title == '职业健康防护设施')
        			{
        				actionURL = 'OperationControlAction!editODHequipment?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_oDHequipment;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editODHequipment',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editODHequipment', title: '修改职业健康防护设施', items: [forms]});
        			}
        			if(title == '安全标志、标识')
        			{
                		insertFileToList();
        				actionURL = 'OperationControlAction!editSecuritySymbol?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_securitySymbol;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editSecuritySymbol',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSecuritySymbol', title: '修改安全标志、标识', items: [forms]});
        			}
        			if(title == '个人职业病防护用品')
        			{
        				insertFileToList();
        				actionURL = 'OperationControlAction!editPODgoods?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_pODgoods;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editPODgoods',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editPODgoods', title: '修改个人职业病防护用品', items: [forms]});
        			}
        			
        			if(title == '特种作业人员')
        			{
        				insertFileToList();
        				actionURL = 'OperationControlAction!editTezhongpeople?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Tezhongpeople;
                		
        			
        			createForm({
            			autoScroll: true,
            			action: 'editTezhongpeople',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editTezhongpeople', title: '修改特种作业人员', items: [forms]});
        			}
        			
        		}
        		
        		else
				{
					 Ext.Msg.alert('警告', '只能选中一条记录！');
				}
        		
	        }
        }
        
        var deleteH = function() 
        {
        	
        	if(getSel(gridDT))
        	{
        		Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) 
        		{
    				if (buttonID === 'yes') 
    				{
    					if(title=='节能减排费用管理'||title=='环境保护费用管理')
    					{$.getJSON(encodeURI("OperationControlAction!deleteFeemanagement?userName=" + user.name + "&userRole=" + user.role + "&Type=" + title),
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
    					
    					if(title=='方案实施')
    					{$.getJSON(encodeURI("OperationControlAction!deleteSchemeimple?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '安全生产协议')
    					{$.getJSON(encodeURI("OperationControlAction!deleteSafetyprotocal?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '建筑机械、施工机具管理台账')
    					{$.getJSON(encodeURI("OperationControlAction!deleteConstructionelec?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					
    					if(title=='安全施工作业票')
    					{$.getJSON(encodeURI("OperationControlAction!deleteSecureJobSlip?userName=" + user.name + "&userRole=" + user.role),
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
    					if(title=='交通车辆管理台账')
    					{$.getJSON(encodeURI("OperationControlAction!deleteTransportSafety?userName=" + user.name + "&userRole=" + user.role),
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
    					if(title=='消防设备设施管理台账')
    					{$.getJSON(encodeURI("OperationControlAction!deleteFireSafety?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title=='个人职业病防护用品')
    					{$.getJSON(encodeURI("OperationControlAction!deletePODgoods?userName=" + user.name + "&userRole=" + user.role),
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
    					if(title=='职业健康防护设施')
    					{$.getJSON(encodeURI("OperationControlAction!deleteODHequipment?userName=" + user.name + "&userRole=" + user.role),
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
    					if(title=='环境保护专项措施')
    					{$.getJSON(encodeURI("OperationControlAction!deleteEnviromentPro?userName=" + user.name + "&userRole=" + user.role),
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
    					if(title=='节能减排专项措施')
    					{$.getJSON(encodeURI("OperationControlAction!deleteSaveEnergy?userName=" + user.name + "&userRole=" + user.role),
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
    					
//jianglf-------------------------------------------------------------
    					if(title=='施工组织设计')
    					{$.getJSON(encodeURI("OperationControlAction!deleteConstructdesign?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title=='专项方案编审批')
    					{$.getJSON(encodeURI("OperationControlAction!deleteProapproval?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title=='职业危害现状评价')
    					{$.getJSON(encodeURI("OperationControlAction!deleteOccuevaluation?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '职业危害因素监测')
    					{$.getJSON(encodeURI("OperationControlAction!deleteOccumonitor?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '其他作业')
    					{$.getJSON(encodeURI("OperationControlAction!deleteOtherjob?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '安全文明施工总体策划编审批')
    					{$.getJSON(encodeURI("OperationControlAction!deleteSafepromonitor?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '安全文明施工总体策划交底')
    					{$.getJSON(encodeURI("OperationControlAction!deleteSafeprojd?userName=" + user.name + "&userRole=" + user.role),
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
//end-----------------------------------------------------------------
    					
//jianglf-------------------------------------------------------------
    					if(title == '特种设备作业人员')
    					{$.getJSON(encodeURI("OperationControlAction!deleteTezhongsbpeople?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '分包方考核与评价')
    					{$.getJSON(encodeURI("OperationControlAction!deleteFbtestjudge?userName=" + user.name + "&userRole=" + user.role),
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
//end-----------------------------------------------------------------    					
    					if(title=='特种作业人员')
    					{$.getJSON(encodeURI("OperationControlAction!deleteTezhongpeople?userName=" + user.name + "&userRole=" + user.role),
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
    					if(title=='安全标志、标识')
    					{$.getJSON(encodeURI("OperationControlAction!deleteSecuritySymbol?userName=" + user.name + "&userRole=" + user.role),
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
        
        Ext.define('Item', {
			extend: 'Ext.data.Model',
			fields: ['text']
		})
	
	
	//------------5-1-1任务分配--------------//
	var storeSelperson = new Ext.data.TreeStore({
		model: 'Item',
		root: {
			text: 'Root',
			expanded: true,   
			children:[]
		}
	})
	var storeAllperson = Ext.create('Ext.data.TreeStore',{  		   					 		
		model: 'Item',
		root: {
			text: 'Root 2',
			expanded: true,
			children: []
		}
	});
	var personList;
			
	var addMissionEditor = [{
		xtype: 'container',
		anchor: '100%',
		layout: 'hbox',
		items:[{
			xtype: 'container',
			flex: 1,
			layout: 'anchor',
			items: [{
				xtype:'textfield',
				fieldLabel: '文件号',
				labelAlign: 'left',
				anchor:'95%',
				name: 'No',
				hidden: true,
				hiddenLabel: true
			},{
				xtype:'textfield',
				fieldLabel: '任务名称',
				labelAlign: 'left',
				// afterLabelTextTpl: required, //红色星号
				anchor:'95%',
				name: 'missionname'
			},{
				xtype:'combo',
				fieldLabel: '分数',
				labelAlign: 'left',
				anchor:'95%',
				store:["1","2","3","4","5"],
			  // afterLabelTextTpl: required,  //红色星号
	  
				value :'5',
				name: 'score'
			}]
		},{
			xtype: 'container',
			flex: 1,
			layout: 'anchor',
			items: [{
				xtype:"datefield",
				fieldLabel: '要求时间',
				afterLabelTextTpl: required,
				labelAlign: 'left',
				format:"Y-m-d",
				name: 'fintime',
				anchor:'100%',
				allowBlank: false                                        
			},{
				xtype:'textfield',
				fieldLabel: '任务类型',
				labelAlign: 'left',
				// afterLabelTextTpl: required, //红色星号
				anchor:'95%',
				name: 'missionfield',
				value:title
				}]
			}]
		},{
			xtype: 'container',
			hidden: user.role === '项目部人员',
			anchor: '100%',
			layout: 'column',
			items:[{
				xtype: 'container',
				flex: 1,
				layout: 'column',
				items:[{
				xtype: "checkbox",
				boxLabel: '所有项目经理',
				colspan: 1,
				height: 30,
				width: 120,
				listeners: {
					change: function(field) {
						if(field.checked){
							snode = storeSelperson.getRootNode();    
							pnode = storeAllperson.getRootNode(); 
							var nodeList = [];
							snode.cascadeBy(function(nod){                         
							if(nod.get('text').indexOf("项目经理")>-1)
								nodeList.push(snode.indexOf(nod));                 
							}) 
							for(var i = nodeList.length-1;i>=0;i--) {
								snode.getChildAt(i).remove();
							}              
							pnode.cascadeBy(function(node){ 
								if(node.get('leaf')&&node.get('text').indexOf('项目经理')>1){
									var text = node.get('text')+'-'+node.parentNode.get('text');
									var newnode=[{text:text,leaf:true}];  //新节点信息                   
									snode.appendChild(newnode); //添加子节点  
									snode.set('leaf',false);  
									snode.expand();
								}
							});
						} else {
							var nodeList = [];                      
							snode = storeSelperson.getRootNode();    
							snode.cascadeBy(function(nod){                     
								if(nod.get('text').indexOf("项目经理")>-1)
									nodeList.push(snode.indexOf(nod));                 
							})    
							for(var i = nodeList.length-1;i>=0;i--) {
								snode.getChildAt(nodeList[i]).remove();
							}
						}
					}
				}
			},{
				xtype: "checkbox",
				boxLabel: '所有项目安全总监',
				colspan: 1,
				height: 30,
				width: 140,
				listeners: {
					change: function(field) {
						if(field.checked){
							var nodeList = [];
							snode = storeSelperson.getRootNode();    
							pnode = storeAllperson.getRootNode(); 
							snode.cascadeBy(function(nod){  //删除已存在的项目
								if(nod.get('text').indexOf("项目安全总监")>-1)
									nodeList.push(snode.indexOf(nod));                 
								}) 
								for(var i = nodeList.length-1;i>=0;i--)
								{
									snode.getChildAt(i).remove();
								}
								pnode.cascadeBy(function(node){
									if(node.get('leaf')&&node.get('text').indexOf('项目安全总监')>1){
										var text = node.get('text')+'-'+node.parentNode.get('text');
										var newnode=[{text:text,leaf:true}];  //新节点信息                      
										snode.appendChild(newnode); //添加子节点  
										snode.set('leaf',false);
										snode.expand();
									}  
								});
						} else {
							var nodeList = [];//删除所有项目安全节点
							snode = storeSelperson.getRootNode();    
							snode.cascadeBy(function(nod){  
								if(nod.get('text').indexOf("项目安全总监")>-1) {
									nodeList.push(snode.indexOf(nod));    
								}
							})
							for(var i = nodeList.length-1;i>=0;i--) {
								snode.getChildAt(nodeList[i]).remove();
							}
						}
					}
				}
			},{
				xtype: "checkbox",
				boxLabel: '所有院领导',
				colspan: 1,
				height: 30,
				width: 120,
				listeners: {
					change: function(field) {
						if(field.checked){
							snode = storeSelperson.getRootNode();    
							pnode = storeAllperson.getRootNode(); 
							var nodeList = [];
							snode.cascadeBy(function(nod){                         
							if(nod.get('text').indexOf("院领导")>-1)
								nodeList.push(snode.indexOf(nod));                 
							})                       
							for(var i = nodeList.length-1;i>=0;i--) {
								snode.getChildAt(i).remove();
							}              
							pnode.cascadeBy(function(node){                                          
								if(node.get('leaf')&&node.get('text').indexOf('院领导')>1){
									var text = node.get('text')+'-'+node.parentNode.get('text');                         
									var newnode=[{text:text,leaf:true}];  //新节点信息                   
									snode.appendChild(newnode); //添加子节点  
									snode.set('leaf',false);  
									snode.expand();                         
								}
							});
						} else {
							var nodeList = [];                      
							snode = storeSelperson.getRootNode();                             
							snode.cascadeBy(function(nod){                     
							if(nod.get('text').indexOf("院领导")>-1)
								nodeList.push(snode.indexOf(nod));                 
							})                          
							for(var i = nodeList.length-1;i>=0;i--) {
								snode.getChildAt(nodeList[i]).remove();
							}
						}
					}
				}
			},{
				xtype: "checkbox",
				boxLabel: '所有质安部管理员',
				colspan: 1,
				height: 30,
				width: 150,
				listeners: {
					change: function(field) {
						if(field.checked){
							snode = storeSelperson.getRootNode();    
							pnode = storeAllperson.getRootNode(); 
							var nodeList = [];
							snode.cascadeBy(function(nod){                         
							if(nod.get('text').indexOf("质安部管理员")>-1)
								nodeList.push(snode.indexOf(nod));                 
							})                       
							for(var i = nodeList.length-1;i>=0;i--) {
								snode.getChildAt(i).remove();
							}              
							pnode.cascadeBy(function(node){                                          
								if(node.get('leaf')&&node.get('text').indexOf('质安部管理员')>1){
									var text = node.get('text')+'-'+node.parentNode.get('text');                         
									var newnode=[{text:text,leaf:true}];  //新节点信息                   
									snode.appendChild(newnode); //添加子节点  
									snode.set('leaf',false);  
									snode.expand();                         
								}
							});
						} else {
							var nodeList = [];                      
							snode = storeSelperson.getRootNode();                             
							snode.cascadeBy(function(nod){                     
							if(nod.get('text').indexOf("质安部管理员")>-1)
								nodeList.push(snode.indexOf(nod));                 
							})                          
							for(var i = nodeList.length-1;i>=0;i--) {
								snode.getChildAt(nodeList[i]).remove();
							}
						}
					}
				}
			},{
				xtype: "checkbox",
				boxLabel: '所有其他管理员',
				colspan: 1,
				height: 30,
				width: 120,
				listeners: {
					change: function(field) {
						if(field.checked){
							snode = storeSelperson.getRootNode();    
							pnode = storeAllperson.getRootNode(); 
							var nodeList = [];
							snode.cascadeBy(function(nod){                         
							if(nod.get('text').indexOf("其他管理员")>-1)
								nodeList.push(snode.indexOf(nod));                 
							})                       
							for(var i = nodeList.length-1;i>=0;i--) {
								snode.getChildAt(i).remove();
							}              
							pnode.cascadeBy(function(node){                                          
								if(node.get('leaf')&&node.get('text').indexOf('其他管理员')>1){
									var text = node.get('text')+'-'+node.parentNode.get('text');                         
									var newnode=[{text:text,leaf:true}];  //新节点信息                   
									snode.appendChild(newnode); //添加子节点  
									snode.set('leaf',false);  
									snode.expand();                         
								}
							});
						} else {
							var nodeList = [];                      
							snode = storeSelperson.getRootNode();                             
							snode.cascadeBy(function(nod){                     
							if(nod.get('text').indexOf("其他管理员")>-1)
								nodeList.push(snode.indexOf(nod));                 
							})                          
							for(var i = nodeList.length-1;i>=0;i--) {
								snode.getChildAt(nodeList[i]).remove();
							}
						}
					}
				}
			}]
		}]
	},{
		xtype: 'container',
		anchor: '100%',
		layout: 'hbox',
		items:[{
			xtype: 'container',
			flex: 1,
			layout: 'anchor',
			items:[{
				title: '待选择',
				xtype:'treepanel',
				height:200,
				store: storeAllperson, 
				anchor:'95%',
				rootVisible: false,
				viewConfig: {
					plugins: {
					 ptype: 'treeviewdragdrop',
					 enableDrag: true,
					 enableDrop: false        
					}
				},
				listeners: {
					itemclick:function(view,record,item,index,e,opts){               
						var model = view.getRecord(view.findTargetByEvent(e));//父节点名                             
						var parentName = model.parentNode.get('text');  
						var newnode=[{text:record.get('text')+'-'+parentName,leaf:true}];  //新节点信息                                             
						pnode = storeSelperson.getRootNode();
						if(record.get('leaf')){
							pnode.appendChild(newnode); //添加子节点  
							pnode.set('leaf',false);  
							pnode.expand();     
						};
					}
				}
			}]
		},{
			xtype: 'container',
			flex: 1,
			layout: 'anchor',
			items: [{
				title: '已选择',
				xtype:'treepanel',
				height:200,
				store: storeSelperson,
				lines:false,
				rootVisible: false,
				viewConfig: {
					plugins: {                      
					 ptype: 'treeviewdragdrop',
					 enableDrag: false,
					 enableDrop: true,
					 appendOnly: true
					}
				
				},
				listeners: {
					itemclick:function(view,record,item,index,e,opts){
						var model = view.getRecord(view.findTargetByEvent(e));
						model.remove();    
					}
				}
			}]
		}]
	},
		uploadPanel
	]

	var Allotask = function(){
		Ext.Ajax.request({
			async: false, 
			method : 'POST',
			url: encodeURI('MissionAction!getPersonNode?role='+user.role + "&projectName="+config.projectName),
			success: function(response){
				personList = Ext.decode(response.responseText); //返回监控点列表
			}
		});
		storeAllperson.getRootNode().removeAll();
		for ( var p in personList ) {  
			var newnode= Ext.create('Ext.data.NodeInterface',{leaf:false});             
			pnode = storeAllperson.getRootNode();
			var newnode = pnode.createNode(newnode);            
			newnode.set("text",p); 
			var allperson = personList[p].split(",");
			for(var i = 0;i<allperson.length;i++) {
				var newde=[{text:allperson[i],leaf:true}];  //新节点信息   
				newnode.appendChild(newde);
			}               
			pnode.appendChild(newnode); //添加子节点  
			pnode.set('leaf',false);  
			pnode.expand();         
		}
		if(getSel(gridDT)) insertFileToList();
		var actionURL;
		var uploadURL;
		var items;      
		var folder;
		folder = selRecs[0].data.Accessory;
		actionURL = 'MissionAction!alloTask?title=分配任务&missionexp=文件要求&folder='+folder; 
		uploadURL = "UploadAction!execute";
		items = addMissionEditor;
		createForm({
			autoScroll: true,
			bodyPadding: 5,
			action: 'alloTask',
			url: actionURL,
			items: items
		});
		uploadPanel.upload_url = uploadURL;
		bbar.moveFirst();   //状态栏回到第一页
		showWin({ winId: 'alloTask', title: '任务分配', items: [forms]});     
	}
	
	var btnAllotask = Ext.create('Ext.Button', {
    	width: 100,
    	height: 32,
    	text: '任务分配',
        icon: "Images/ims/toolbar/view.png",
       	disabled: true,
        handler: Allotask
    })
	//------------5-1-1任务分配--------------//
        
        
        if(((title == '节能减排费用管理')||(title == '环境保护费用管理')))
        {	
        	dataStore = store_Feemanagement;
        	
        	queryURL = 'OperationControlAction!getFeemanagementListSearch?userName=' + user.name + "&userRole=" + user.role +"&Type=" + title;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '费用分类', dataIndex: 'Typei', align: 'center', width: 120},
            	    { text: '使用单位', dataIndex: 'Unit', align: 'center', width: 120},
            	    { text: '金额（元）', dataIndex: 'Fee', align: 'center', width: 120},
            	    { text: '费用用途', dataIndex: 'Usea', align: 'center', width: 120},
            	    { text: '经办人', dataIndex: 'Jperson', align: 'center', width: 120},
            	    { text: '登记人', dataIndex: 'Dperson', align: 'center', width: 120},
            	    { text: '审批时间', dataIndex: 'Year', align: 'center', width: 120},
            	    { text: '备注', dataIndex: 'Content', align: 'center', width: 120}
            	   // { text: '附件', dataIndex: 'Accessory', align: 'center', width: 200,hidden:true}
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
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        } 
        
        if(title == '安全生产协议')
        {	
        	dataStore = store_Safetyprotocal;
        	
        	queryURL = 'OperationControlAction!getSafetyprotocalListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '协议类型', dataIndex: 'Type', align: 'center', width: 120},
            	    { text: '分包单位名称', dataIndex: 'Fbname', align: 'center', width: 120},
            	    { text: '协议名称', dataIndex: 'Name', align: 'center', width: 120},
            	    { text: '签订时间', dataIndex: 'Date', align: 'center', width: 120}
            	   // { text: '附件', dataIndex: 'Accessory', align: 'center', width: 700}
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
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        } 
        
        if(title == '建筑机械、施工机具管理台账')
        {	
        	dataStore = store_Constructionelec;
        	
        	queryURL = 'OperationControlAction!getConstructionelecListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '名称', dataIndex: 'Name', align: 'center', width: 120},
            	    { text: '规格型号', dataIndex: 'Type', align: 'center', width: 120},
            	    { text: '台数', dataIndex: 'Num', align: 'center', width: 120},
            	    { text: '单台功率', dataIndex: 'Singlepower', align: 'center', width: 120},
            	    { text: '总功率', dataIndex: 'Sumpower', align: 'center', width: 120},
            	    { text: '生产厂家', dataIndex: 'Factory', align: 'center', width: 120},
            	    { text: '设备使用方式', dataIndex: 'Usage', align: 'center', width: 120},
            	    { text: '出场时间', dataIndex: 'Outtime', align: 'center', width: 120},
            	    { text: '进场时间', dataIndex: 'Intime', align: 'center', width: 120},
            	    { text: '计划出场时间', dataIndex: 'Plantime', align: 'center', width: 120},
            	    { text: '实际出场时间', dataIndex: 'Realtime', align: 'center', width: 120},
            	    { text: '使用状态', dataIndex: 'Status', align: 'center', width: 120},
            	    { text: '是否组织进场检查验收', dataIndex: 'Approve', align: 'center', width: 150}
            	   // { text: '《安全用电技术措施》', dataIndex: 'Accessory', align: 'center', width: 600}
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
        		}
        } 
        
//jianglf----------------------------------------------------------------------------------------------------        
        if(title == '方案实施')
        {	
        	dataStore = store_Schemeimple;
        	queryURL = 'OperationControlAction!getSchemeimpleListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
            	    { text: '专项施工方案名称', dataIndex: 'SchemeName', align: 'center', width: 250},
            	    { text: '方案交底', dataIndex: 'Jd', align: 'center', width: 250},
            	    { text: '旁站监督', dataIndex: 'Supervise', align: 'center', width: 250},
            	    { text: '方案验收', dataIndex: 'Acceptance', align: 'center', width: 250}
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	    
            	]
            	//tbar.add("-");
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
        
//end---------------------------------------------------------------------------------------------------------        
        if(title == '安全施工作业票')
        {	
        	dataStore = store_SecureJobSlip;
        	queryURL = 'OperationControlAction!getSecureJobSlipListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '作业地点、范围', dataIndex: 'WorkPlace', align: 'center', width: 150},
        	          	{ text: '作业内容', dataIndex: 'WorkContent', align: 'center', width: 200},
        	          	{ text: '作业时间', dataIndex: 'WorkTime', align: 'center', width: 150},
        	          	{ text: '危险源', dataIndex: 'DangerSource', align: 'center', width: 150},
        	          	{ text: '单项工作负责人', dataIndex: 'Principle', align: 'center', width: 150},
        	          	{ text: '施工人数', dataIndex: 'WorkerNum', align: 'center', width: 150}
        	          	//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
        	          //	{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
            	]
            	//tbar.add("-");
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
        if(title == '交通车辆管理台账')
        {	
        	dataStore = store_TransportSafety;
        	queryURL = 'OperationControlAction!getTransportSafetyListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '车辆编号', dataIndex: 'CarNum', align: 'center', width: 150},
        	          	{ text: '机动车车辆', dataIndex: 'CarName', align: 'center', width: 200},
        	          	{ text: '机动车所属单位', dataIndex: 'Department', align: 'center', width: 150},
        	          	{ text: '机动车行驶证', dataIndex: 'License', align: 'center', width: 150},
        	          	{ text: '指定驾驶人', dataIndex: 'Driver', align: 'center', width: 150},
        	          	{ text: '驾驶人驾驶证号', dataIndex: 'DriverNum', align: 'center', width: 150},
        	          	{ text: '是否定期检查维保', dataIndex: 'Maintenance', align: 'center', width: 80}
        	          	//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	//tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		//tbar.add(btnScan);
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		} else {
            		tbar.add(btnImport);
        		}
        }
        if(title == '消防设备设施管理台账')
        {	
        	dataStore = store_FireSafety;
        	queryURL = 'OperationControlAction!getFireSafetyListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '消防设备名称', dataIndex: 'Name', align: 'center', width: 150},
        	          	{ text: '型号及规格', dataIndex: 'Model', align: 'center', width: 200},
        	          	{ text: '数量', dataIndex: 'Num', align: 'center', width: 150},
        	          	{ text: '单位', dataIndex: 'Department', align: 'center', width: 150},
        	          	{ text: '购置日期', dataIndex: 'AcqDate', align: 'center', width: 150},
        	          	{ text: '更换日期', dataIndex: 'ChangeDate', align: 'center', width: 150},
        	          	{ text: '配置地点', dataIndex: 'Place', align: 'center', width: 80},
        	          	{ text: '责任人', dataIndex: 'ChargePerson', align: 'center', width: 80},
        	          	{ text: '是否定期检查', dataIndex: 'CheckPeriodically', align: 'center', width: 80},
        	          	{ text: '检查结果', dataIndex: 'CheckResult', align: 'center', width: 80}
        	          	//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	//tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add(btnImport);
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		//tbar.add(btnScan);
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        }
//jianglf--------------------------------------------------------------------------------        
        if(title == '特种作业人员')
        {	
        	dataStore = store_Tezhongpeople;
        	queryURL = 'OperationControlAction!getTezhongpeopleListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '分包单位名称', dataIndex: 'FbName', align: 'center', width: 150},
        	          	{ text: '作业类别', dataIndex: 'Type', align: 'center', width: 200},
        	          	{ text: '姓名', dataIndex: 'Name', align: 'center', width: 150},
        	          	{ text: '性别', dataIndex: 'Sex', align: 'center', width: 150},
        	          	{ text: '准操项目', dataIndex: 'CardName', align: 'center', width: 150},
        	          	{ text: '发证日期', dataIndex: 'CardTime', align: 'center', width: 150},
        	          	{ text: '有效期至', dataIndex: 'UseTime', align: 'center', width: 150},
        	          	{ text: '特种作业操作证编号', dataIndex: 'CardNo', align: 'center', width: 180},
        	          	{ text: '发证机关', dataIndex: 'CardPlace', align: 'center', width: 150},
        	          	{ text: '是否参加培训', dataIndex: 'Peixun', align: 'center', width: 150},
        	          	{ text: '备注', dataIndex: 'Ps', align: 'center', width: 150}
        	          //	{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 150}
        	          	//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	//tbar.add("-");
            	tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add(btnImport);
        		tbar.add("-");
        		
        		tbar.add(btnScan);
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        			tbar.remove(btnImport);
        		}
        }
//end------------------------------------------------------------------------------------------------       
        if(title == '个人职业病防护用品')
        {	
        	dataStore = store_PODgoods;
        	queryURL = 'OperationControlAction!getPODgoodsListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '职业病防护用品名称', dataIndex: 'Name', align: 'center', width: 150},
        	          	{ text: '发放时间', dataIndex: 'DeliveryTime', align: 'center', width: 80},
        	          	{ text: '数量', dataIndex: 'Num', align: 'center', width: 80},
        	          	{ text: '领取人姓名', dataIndex: 'PersonName', align: 'center', width: 150},
        	          	{ text: '所属单位', dataIndex: 'Department', align: 'center', width: 150},
        	          	{ text: '使用期限', dataIndex: 'TimeLimit', align: 'center', width: 150},
        	        
        	          	{ text: '备注', dataIndex: 'Comment', align: 'center', width: 300}
        	          	//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	//tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add(btnImport);
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        			tbar.remove(btnImport);
        		}
        }
        if(title == '职业健康防护设施')
        {	
        	dataStore = store_ODHequipment;
        	queryURL = 'OperationControlAction!getODHequipmentListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '防护设施名称', dataIndex: 'Name', align: 'center', width: 150},
        	          	{ text: '型号及规格', dataIndex: 'Model', align: 'center', width: 80},
        	          	{ text: '制造单位', dataIndex: 'Department', align: 'center', width: 80},
        	          	{ text: '数量', dataIndex: 'Num', align: 'center', width: 150},
        	          	{ text: '购买时间', dataIndex: 'BuyTime', align: 'center', width: 150},
        	          	{ text: '编号', dataIndex: 'SerialNumber', align: 'center', width: 150},
        	          	{ text: '使用地点', dataIndex: 'Place', align: 'center', width: 80},
        	          	{ text: '责任人', dataIndex: 'Responsibility', align: 'center', width: 300}
        	          	//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	//tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add(btnImport);
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        			tbar.remove(btnImport);
        		}
        }
        if(title == '安全标志、标识')
        {	
        	dataStore = store_SecuritySymbol;
        	queryURL = 'OperationControlAction!getSecuritySymbolListSearch?userName=' + user.name + "&userRole=" + user.role;
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '安全标志、标识名称', dataIndex: 'Name', align: 'center', width: 150},
        	          	{ text: '数量', dataIndex: 'Num', align: 'center', width: 80},
        	          	{ text: '单位', dataIndex: 'Department', align: 'center', width: 80},
        	          	{ text: '安装时间', dataIndex: 'InstallTime', align: 'center', width: 150},
        	          	{ text: '安装地点', dataIndex: 'InstallPlace', align: 'center', width: 150},
        	          	{ text: '责任人', dataIndex: 'Responsibility', align: 'center', width: 300},
        				{ text: '安全标志、标识预览', align: 'center', width: 200, renderer: function (value, meta, record) {
	    						var accessory = record.data.Accessory;
	    						var array = accessory.split("*");
	    						var foldname = array[0]+"\\"+array[1];
	    						var html = "";
	    						//若从数据库中取出为空，则表示没有附件
	    						if( array.length==0 ) {
	    							return "";
	    						}//数据库中有附件，添加到文件下拉菜单中
	    						else{
	    							for( var i = 2; i < array.length; i++) {
	    								if(array[i].indexOf('.png')>0 || array[i].indexOf('.jpg')>0 || array[i].indexOf('.bmp')>0)
	    									html += "<a href='upload/"+foldname+"/"+array[i]+"' target='_blank' style='margin:auto 5px;'/>图片预览</a>";
	    								else
	    									html += "<a href='upload/"+foldname+"/"+array[i]+"' target='_blank' style='margin:auto 5px;'/>文件预览</a>";
	    							} 
	    						}
	    						return html == '' ? '暂无文件' : html;
	    					}
	    				},
            	]
            	//tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        }
        if(title == '环境保护专项措施')
        {	
        	dataStore = store_EnviromentPro;
        	queryURL = 'OperationControlAction!getEnviromentProListSearch?userName=' + user.name + "&userRole=" + user.role;
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '环境影响专项措施名称', dataIndex: 'Name', align: 'center', width: 150},
        	          	{ text: '编制单位', dataIndex: 'Department', align: 'center', width: 200},
        	          	{ text: '编制时间', dataIndex: 'Time', align: 'center', width: 150},
        	          	{ text: '是否要求审批', dataIndex: 'Approval', align: 'center', width: 150}
        	          	//{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
            	]
            	//tbar.add("-");
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
        if(title == '节能减排专项措施')
        {	
        	dataStore = store_SaveEnergy;
        	queryURL = 'OperationControlAction!getSaveEnergyListSearch?userName=' + user.name + "&userRole=" + user.role;
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '节能减排专项措施名称', dataIndex: 'Name', align: 'center', width: 150},
        	          	{ text: '编制单位', dataIndex: 'Department', align: 'center', width: 200},        	          	{ text: '编制时间', dataIndex: 'Time', align: 'center', width: 150},
        	          	{ text: '是否要求审批', dataIndex: 'Approval', align: 'center', width: 150}
        	         // 	{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
            	]
            	//tbar.add("-");
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
//jianglf------------------------------------------------------------------
        if(title == '施工组织设计')
        {	
        	dataStore = store_Constructdesign;
        	queryURL = 'OperationControlAction!getConstructdesignListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        /*	 var misfile = record.get('Accessory').split('*');
		        var fileName;
		       for(var i = 2;i<misfile.length;i++){
	        	
	        	fileName = misfile[i];
		       }*/
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        	          	{ text: '施工组织设计类型', dataIndex: 'Type', align: 'center', width: 250},
        	          	{ text: '编制单位', dataIndex: 'Unit', align: 'center', width: 250}
        	          	//{ text: '编审批记录', dataIndex: 'Accessory', align: 'center', width: 250}
        	          	//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	//tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add(btnScan);
        		tbar.add(btnAllotask);
        		
        		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'OperationControlAction!getConstructdesignListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 250}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			
        		}
        		
        }
        
        if(title == '专项方案编审批')
        {	
        	dataStore = store_Proapproval;
        	queryURL = 'OperationControlAction!getProapprovalListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '专项方案名称', dataIndex: 'Name', align: 'center', width: 150},
        	          	{ text: '工程类别', dataIndex: 'Type', align: 'center', width: 200},
        	          	{ text: '编制单位', dataIndex: 'Unit', align: 'center', width: 150},
        	          	{ text: '编制时间', dataIndex: 'Time', align: 'center', width: 150},
        	          	{ text: '是否按要求审批', dataIndex: 'Approval', align: 'center', width: 150}
        	          	//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
        	          	//{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
            	]
            	//tbar.add("-");
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
        
        if(title == '职业危害现状评价')
        {	
        	dataStore = store_Occuevaluation;
        	queryURL = 'OperationControlAction!getOccuevaluationListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	column = [
        	          	{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
        	          	{ text: '评价序号', dataIndex: 'No', align: 'center', width: 150},
        	          	{ text: '评价时间', dataIndex: 'Date', align: 'center', width: 200}
        	          	//{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 200}
        	          	//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	//tbar.add("-");
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
        
        if(title == '职业危害因素监测')
        {	
        	dataStore = store_Occumonitor;
        	
        	queryURL = 'OperationControlAction!getOccumonitorListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '监测序号', dataIndex: 'No', align: 'center', width: 120},
            	    { text: '职业危害因素', dataIndex: 'Factor', align: 'center', width: 120},
            	    { text: '监测时间', dataIndex: 'Time', align: 'center', width: 120},
            	    { text: '监测结果', dataIndex: 'Result', align: 'center', width: 150}
            	   // { text: '附件', dataIndex: 'Accessory', align: 'center', width: 600}
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
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        } 
        
        if(title == '其他作业')
        {	
        	dataStore = store_Otherjob;
        	
        	queryURL = 'OperationControlAction!getOtherjobListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '作业名称', dataIndex: 'Name', align: 'center', width: 120},
            	    { text: '作业时间', dataIndex: 'Time', align: 'center', width: 120},
            	    { text: '作业单位', dataIndex: 'Unit', align: 'center', width: 120},
            	    { text: '作业安全记录', dataIndex: 'Record', align: 'center', width: 150}
            	   // { text: '附件', dataIndex: 'Accessory', align: 'center', width: 600}
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
        } 
        
        if(title == '安全文明施工总体策划编审批')
        {	
        	dataStore = store_Safepromonitor;
        	
        	queryURL = 'OperationControlAction!getSafepromonitorListSearch?userName=' + user.name + "&userRole=" + user.role;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '编制人', dataIndex: 'Bzperson', align: 'center', width: 120},
            	    { text: '编制时间', dataIndex: 'Bztime', align: 'center', width: 120},
            	    { text: '审核人', dataIndex: 'Shperson', align: 'center', width: 120},
            	    { text: '审核时间', dataIndex: 'Shtime', align: 'center', width: 120},
            	    { text: '批准人', dataIndex: 'Pzperson', align: 'center', width: 120},
            	    { text: '批准时间', dataIndex: 'Pztime', align: 'center', width: 120}
            	  //  { text: '附件', dataIndex: 'Accessory', align: 'center', width: 600}
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
        } 
        
        if(title == '安全文明施工总体策划交底')
        {	
        	dataStore = store_Safeprojd;
        	
        	queryURL = 'OperationControlAction!getSafeprojdListSearch?userName=' + user.name + "&userRole=" + user.role;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '交底人', dataIndex: 'Person', align: 'center', width: 120},
            	    { text: '交底对象', dataIndex: 'Jdperson', align: 'center', width: 120},
            	    { text: '交底时间', dataIndex: 'Time', align: 'center', width: 120}
            	  // { text: '附件', dataIndex: 'Accessory', align: 'center', width: 600}
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
        }
//end----------------------------------------------------------------------
        
//jianglf------------------------------------------------------------------
        if(title == '特种设备作业人员')
        {	
        	dataStore = store_Tezhongsbpeople;
        	
        	queryURL = 'OperationControlAction!getTezhongsbpeopleListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '分包单位名称', dataIndex: 'FbName', align: 'center', width: 120},
            	    { text: '作业种类', dataIndex: 'Type', align: 'center', width: 120},
            	    { text: '作业项目', dataIndex: 'Project', align: 'center', width: 120},
            	    { text: '姓名', dataIndex: 'Name', align: 'center', width: 120},
            	    { text: '性别', dataIndex: 'Gender', align: 'center', width: 120},
            	    { text: '特种设备作业人员证编号', dataIndex: 'CardNo', align: 'center', width: 180},
            	    { text: '发证日期', dataIndex: 'BeginTime', align: 'center', width: 120},
            	    { text: '有效期至', dataIndex: 'ValidTime', align: 'center', width: 120},
            	    { text: '发证机关', dataIndex: 'Unit', align: 'center', width: 120},
            	    { text: '是否参加培训', dataIndex: 'Peixun', align: 'center', width: 120},
            	    { text: '备注', dataIndex: 'Ps', align: 'center', width: 120}
            	   // { text: '附件', dataIndex: 'Accessory', align: 'center', width: 600}
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
        			tbar.remvoe(btnImport);
        		}
        }
        
        if(title == '分包方考核与评价')
        {	
        	dataStore = store_Fbtestjudge;
        	
        	queryURL = 'OperationControlAction!getFbtestjudgeListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '考核序号', dataIndex: 'No', align: 'center', width: 120},
            	    { text: '考核分包方', dataIndex: 'Tester', align: 'center', width: 120},
            	    { text: '考核时间', dataIndex: 'Time', align: 'center', width: 120},
            	    { text: '考核结果', dataIndex: 'Result', align: 'center', width: 120}
            	   // { text: '附件', dataIndex: 'Accessory', align: 'center', width: 600}
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
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        }
//end-----------------------------------------------------------------------        
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
            					case "addSecuritySymbol":
            					case "editSecuritySymbol":
                				case "editSecureJobSlip":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addSecureJobSlip":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editSchemeimple":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "addSchemeimple":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "addEnviromentPro":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editEnviromentPro":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editSaveEnergy":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "addSaveEnergy":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
//jianglf------------------------------------------
                				case "editFeemanagement":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "addFeemanagement":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editConstructdesign":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "addConstructdesign":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editProapproval":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "addProapproval":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editOccuevaluation":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "addOccuevaluation":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editOccumonitor": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addOccumonitor":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editOtherjob": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addOtherjob":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editSafepromonitor": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addSafepromonitor":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editSafeprojd": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addSafeprojd":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
//end---------------------------------------------
                				
//jianglf---------------------------------------------------
                				case "editTezhongsbpeople": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addTezhongsbpeople":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				
                				case "editFbtestjudge": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addFbtestjudge":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
//end------------------------------------------------------                				
                				case "editTezhongpeople":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "addTezhongpeople":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				
                				case "editSafetyprotocal": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addSafetyprotocal":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                				}
                				case "editConstructionelec": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "addConstructionelec":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}                				
                				case "alloTask":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					var properson="";
                					var tnode = storeSelperson.getRootNode(); 
     		       	            	tnode.cascadeBy(function(node){ //遍历节点                      
	     		       	            	properson = properson+node.get('text')+',';
	     		       	        	}); 
     		       	            	config.url = config.url+'&properson='+properson;
                					config.url += "&fileName=" + fileName + "&user="+user.name;
                					
                					if (uploadPanel.store.count() == 0) {
                						Ext.Msg.alert('提示', '请上传文件！');
                						return;
                					}
                					//uploadPanel.store.removeAll();
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
          	                		if (title == '施工组织设计') actionId = 7;
          	                		else if(title == '环境保护费用管理') actionId = 20;
          	                		else if(title == '节能减排费用管理') actionId = 21;
          	                		else if(title == '特种作业人员') actionId = 24;
          	                		else if(title == '特种设备作业人员') actionId = 25;
          	                		
          	                		
          	                		
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
                		if (config.action == "editSecureJobSlip") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		if(config.action=="editSchemeimple")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editFireSafety")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editTransportSafety")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editPODgoods")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editODHequipment")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editEnviromentPro")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editSaveEnergy")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
//jianglf--------------------------------------------
                		if(config.action=="editFeemanagement")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editConstructdesign")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editProapproval")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editOccuevaluation")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editOccumonitor")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editOtherjob")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editSafepromonitor")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editSafeprojd")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
//end------------------------------------------------
                		
//jianglf-------------------------------------------------------------
                		if(config.action=="editTezhongsbpeople")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		
                		if(config.action=="editFbtestjudge")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
//end------------------------------------------------------------------                		
                		if(config.action=="editSecuritySymbol")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editTezhongpeople")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editSafetyprotocal")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                		if(config.action=="editConstructionelec")
                		{
                			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                		}
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
            if (config.action == 'editSecureJobSlip') 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if(config.action=="editSchemeimple")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
            if(config.action=="editFireSafety")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
            if(config.action=="editTransportSafety")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
            if(config.action=="editPODgoods")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editODHequipment")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editEnviromentPro")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editSaveEnergy")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
//jianglf--------------------------------------------
    		if(config.action=="editFeemanagement")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editConstructdesign")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editProapproval")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editOccuevaluation")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editOccumonitor")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editOtherjob")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editSafepromonitor")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editSafeprojd")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
//end------------------------------------------------
    		
//jianglf---------------------------------------------------
    		if(config.action=="editTezhongsbpeople")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		
    		if(config.action=="editFbtestjudge")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
//end-----------------------------------------------------------    		
    		if(config.action=="editSecuritySymbol")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editTezhongpeople")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editSafetyprotocal")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
    		if(config.action=="editConstructionelec")
    		{
    			forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
    		}
        }; 
        
      //创建装载formPanel的窗体，由工具栏按钮点击显示
        var showWin = function (config) {
        	var width = 420;
        	var height = 320;
        	
        	if(title == '方案实施')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	if(title == '安全施工作业票')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	if(title == '消防设备设施管理台账')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	if(title == '交通车辆管理台账')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	if(title == '个人职业病防护用品')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	if(title == '职业健康防护设施')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	if(title == '环境保护专项措施')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	if(title == '节能减排专项措施')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
//jianglf-----------------------------------
        	if(title == '环境保护费用管理'||title=='节能减排费用管理')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	
        	if(title == '施工组织设计')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	if(title == '专项方案编审批')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	if(title == '职业危害现状评价')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	if(title == '职业危害因素监测')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	if(title == '安全文明施工总体策划编审批')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	if(title == '安全文明施工总体策划交底')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
//end---------------------------------------
        	
//jianglf------------------------------------------
        	if(title == '特种设备作业人员')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	if(title == '其他作业')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	
        	if(title == '分包方考核与评价')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}

//end---------------------------------------------        	
        	if(title == '安全标志、标识')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	if(title == '特种作业人员')
        	{//编辑提案信息框
        		width = 800;
        		height = 600;
        	}
        	
        	if(title == '安全生产协议')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	if(title == '建筑机械、施工机具管理台账')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	
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
        				btnAllotask.enable();
        			}
        			else
        			{
        				btnEdit.disable();
        				btnScan.disable();
        				btnAllotask.disable();
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
                'celldblclick': function (self, td, cellIndex, record, tr, rowIndex)
        	    {
                	if(title == '安全施工作业票')
                	{
                		var html_str = "";
                		if(title == '安全施工作业票')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('WorkContent')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">作业地点、范围</td><td width=\"40%\">" + record.get("WorkPlace") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">作业内容</td><td>" + record.get('WorkContent') + "</td><td style=\"padding:5px;\">作业时间</td><td>" + record.get('WorkTime') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">危险源</td><td>" + record.get('DangerSource') + "</td><td style=\"padding:5px;\">单项工作负责人</td><td>" + record.get('Principle') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">施工人数</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('WorkerNum') + "</td></tr>";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
             		         
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
                	
                	if(title == '环境保护费用管理'||title=='节能减排费用管理')
                	{
                		var html_str = "";
                		if(title == '环境保护费用管理'||title=='节能减排费用管理')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">费用分类</td><td width=\"40%\">" + record.get("Typei") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">使用单位</td><td>" + record.get('Unit') + "</td><td style=\"padding:5px;\">金额（元）</td><td>" + record.get('Fee') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">费用用途</td><td>" + record.get('Usea') + "</td><td style=\"padding:5px;\">经办人</td><td>" + record.get('Jperson') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">登记人</td><td>" + record.get('Dperson') + "</td><td style=\"padding:5px;\">审批时间</td><td>" + record.get('Time') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">备注</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Content') + "</td></tr>";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
             		         
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
                	
                	if(title == '安全生产协议')
                	{
                		var html_str = "";
                		if(title == '安全生产协议')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">协议类型</td><td width=\"40%\">" + record.get("Type") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">协议名称</td><td>" + record.get('Name') + "</td><td style=\"padding:5px;\">签订时间</td><td>" + record.get('Date') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">分包单位名称</td><td colspan=\"3\">" + record.get('Fbname') + "</td><tr>";
             		         
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
 //jianglf-------------------------------------------------------------------------------------------
                	if(title == '施工组织设计')
                	{
                		var html_str = "";
                		if(title == '施工组织设计')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		        var misfile = record.get('Accessory').split('*');
             		        var fileName;
             		       for(var i = 2;i<misfile.length;i++){
           		        	
           		        	fileName = misfile[i];
             		       }
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">编制单位</td><td width=\"40%\">" + record.get("Unit") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">施工组织设计类型</td><td colspan=\"3\">" + record.get('Type')  + "</td></tr>\
                            \<tr><td style=\"padding:5px;\">编审批记录</td><td colspan=\"3\">" + fileName + "</td><tr>";
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
                	
                	if(title == '专项方案编审批')
                	{
                		var html_str = "";
                		if(title == '专项方案编审批')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">" +
             		         		"<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td colspan=\"3\">" + Num + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">专项方案名称</td><td>" + record.get('Name') + "</td><td style=\"padding:5px;\">编制单位</td><td>" + record.get('Unit') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">工程类别</td><td>" + record.get('Type') + "</td><td style=\"padding:5px;\">编制时间</td><td>" + record.get('Time') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">是否按要求审批</td><td colspan=\"3\">" + record.get('Approval') + "</td><tr>";
//                              \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('Accessory') + "</td><tr>";
             		        html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
             		        
              		         var misfile = record.get('Accessory').split('*');
//              		        var foldermis;
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
                	
                	if(title == '方案实施')
                	{
                		var html_str = "";
                		if(title == '方案实施')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">专项施工方案名称</td><td width=\"40%\">" + record.get("SchemeName") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">旁站监督</td><td colspan=\"3\">" + record.get('Supervise') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">方案验收</td><td colspan=\"3\">" + record.get('Acceptance') + "</td><tr>\
                               \<tr><td style=\"padding:5px;\">方案交底</td><td colspan=\"3\">" + record.get('Jd') + "</td><tr>";
             		         
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
                	
                	if(title == '职业危害现状评价')
                	{
                		var html_str = "";
                		if(title == '职业危害现状评价')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('No')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">评价序号</td><td width=\"40%\">" + record.get("No") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">评价时间</td><td colspan=\"3\">" + record.get('Date') + "</td><tr>";
//                            \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('Accessory') + "</td><tr>";
             		         
             		         
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
                	
                	if(title == '职业危害因素监测')
                	{
                		var html_str = "";
                		if(title == '职业危害因素监测')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('No')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">监测序号</td><td width=\"40%\">" + record.get("No") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">职业危害因素</td><td>" + record.get('Factor') + "</td><td style=\"padding:5px;\">监测时间</td><td>" + record.get('Time') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">监测结果</td><td colspan=\"3\">" + record.get('Result') + "</td></tr>";
//                            \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('Accessory') + "</td><tr>";
             		         
             		         
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
                	
                	if(title == '其他作业')
                	{
                		var html_str = "";
                		if(title == '其他作业')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">作业名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">作业时间</td><td>" + record.get('Time') + "</td><td style=\"padding:5px;\">作业单位</td><td>" + record.get('Unit') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">作业安全记录</td><td colspan=\"3\">" + record.get('Record') + "</td></tr>";
//                            \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('Accessory') + "</td><tr>";
             		         
             		         
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
                	
                	if(title == '安全文明施工总体策划编审批')
                	{
                		var html_str = "";
                		if(title == '安全文明施工总体策划编审批')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Bzperson')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">编制人</td><td width=\"40%\">" + record.get("Bzperson") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">编制时间</td><td>" + record.get('Bztime') + "</td><td style=\"padding:5px;\">审核人</td><td>" + record.get('Shperson') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">审核时间</td><td>" + record.get('Shtime') + "</td><td style=\"padding:5px;\">批准人</td><td>" + record.get('Pzperson') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">批准时间</td><td colspan=\"3\">" + record.get('Pztime') + "</td></tr>";
                            
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
                	
                	if(title == '安全文明施工总体策划交底')
                	{
                		var html_str = "";
                		if(title == '安全文明施工总体策划交底')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Person')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">交底人</td><td width=\"40%\">" + record.get("Person") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">交底对象</td><td>" + record.get('Jdperson') + "</td><td style=\"padding:5px;\">交底时间</td><td>" + record.get('Time') + "</td></tr>";
//                              \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('Accessory') + "</td><tr>";
             		         
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
//end------------------------------------------------------------------------------------------------   
                	
//jianglf----------------------------------------------------------------------------------------
                	if(title == '特种设备作业人员')
                	{
                		var html_str = "";
                		if(title == '特种设备作业人员')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td  colspan=\"3\">" + Num + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">分包单位名称</td><td>" + record.get('FbName') + "</td><td style=\"padding:5px;\">作业种类</td><td>" + record.get('Type') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">作业项目</td><td>" + record.get('Project') + "</td><td style=\"padding:5px;\">姓名</td><td>" + record.get('Name') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">性别</td><td>" + record.get('Gender') + "</td><td style=\"padding:5px;\">特种设备作业人员证编号</td><td>" + record.get('CardNo') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">发证日期</td><td>" + record.get('BeginTime') + "</td><td style=\"padding:5px;\">有效期至</td><td>" + record.get('ValidTime') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">是否参加培训</td><td colspan=\"3\">" + record.get('Peixun') + "</td><tr>\
                               \<tr><td style=\"padding:5px;\">培训详情</td><td style=\"padding:5px;text-align:left;\" colspan=\"3\">" +record.get('Peixunnr') + "</td><tr>\
                              \<tr><td style=\"padding:5px;\">发证机关</td><td>" + record.get('Unit') + "</td><td style=\"padding:5px;\">备注</td><td>" + record.get('Ps') + "</td></tr>";
//                              \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('Accessory') + "</td><tr>";
             		         
             		        html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
             		        
              		         var misfile = record.get('Accessory').split('*');
//              		        var foldermis;
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
                	
                	if(title == '分包方考核与评价')
                	{
                		var html_str = "";
                		if(title == '分包方考核与评价')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">考核序号</td><td width=\"40%\">" + record.get("No") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">考核分包方</td><td colspan=\"3\">" + record.get('Tester') + "</td><tr>\
                              \<tr><td style=\"padding:5px;\">考核时间</td><td colspan=\"3\">" + record.get('Time') + "</td><tr>\
                              \<tr><td style=\"padding:5px;\">考核结果</td><td colspan=\"3\">" + record.get('Result') + "</td><tr>";
//                              \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('Accessory') + "</td><tr>";
             		         
             		        
             		        html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
             		        
              		         var misfile = record.get('Accessory').split('*');
//              		        var foldermis;
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
                	
                	if(title == '特种作业人员')
                	{
                		var html_str = "";
                		if(title == '特种作业人员')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td colspan=\"3\">" + Num + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">分包单位名称</td><td>" + record.get('FbName') + "</td><td style=\"padding:5px;\">作业类别</td><td>" + record.get('Type') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">准操项目</td><td>" + record.get('CardName') + "</td><td style=\"padding:5px;\">姓名</td><td>" + record.get('Name') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">性别</td><td>" + record.get('Sex') + "</td><td style=\"padding:5px;\">特种作业操作证编号</td><td>" + record.get('CardNo') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">发证日期</td><td>" + record.get('CardTime') + "</td><td style=\"padding:5px;\">有效期至</td><td>" + record.get('UseTime') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">是否参加培训</td><td colspan=\"3\">" + record.get('Peixun') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">培训详情</td><td style=\"padding:5px;text-align:left;\" colspan=\"3\">" + record.get('Peixunnr') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">发证机关</td><td>" + record.get('CardPlace') + "</td><td style=\"padding:5px;\">备注</td><td>" + record.get('Ps') + "</td></tr>";
//                              \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('Accessory') + "</td><tr>";
             		         
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
//end-------------------------------------------------------------------------------------------                	
                	if(title == '建筑机械、施工机具管理台账')
                	{
                		var html_str = "";
                		if(title == '建筑机械、施工机具管理台账')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         var misfile = record.get('Accessory').split('*');
             		         var fileName;
             		         for(var i = 2;i<misfile.length;i++){
             		        	fileName = misfile[i];
             		         }
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">规格型号</td><td>" + record.get('Type') + "</td><td style=\"padding:5px;\">台数</td><td>" + record.get('Num') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">单机功率</td><td>" + record.get('Singlepower') + "</td><td style=\"padding:5px;\">总功率</td><td>" + record.get('Sumpower') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">生产厂家</td><td>" + record.get('Factory') + "</td><td style=\"padding:5px;\">设备使用方式</td><td>" + record.get('Shiyong') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">出场日期</td><td>" + record.get('Outtime') + "</td><td style=\"padding:5px;\">进场日期</td><td>" + record.get('Intime') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">计划出场时间</td><td>" + record.get('Plantime') + "</td><td style=\"padding:5px;\">实际出场时间</td><td>" + record.get('Realtime') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">是否组织进场检查验收</td><td>" + record.get('Approve') + "</td><td style=\"padding:5px;\">使用状态</td><td>" + record.get('Status') + "</td></tr>";
             		         
             		        html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
             		        
           		         
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
                	
                	
                	if(title == '消防设备设施管理台账')
                	{
                		var html_str = "";
                		if(title == '消防设备设施管理台账')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">消防设备名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">型号及规格</td><td>" + record.get('Model') + "</td><td style=\"padding:5px;\">数量</td><td>" + record.get('Num') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">单位</td><td>" + record.get('Department') + "</td><td style=\"padding:5px;\">购置日期</td><td>" + record.get('AcqDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">更换日期</td><td>" + record.get('ChangeDate') + "</td><td style=\"padding:5px;\">配置地点</td><td>" + record.get('Place') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">责任人</td><td>" + record.get('ChargePerson') + "</td><td style=\"padding:5px;\">是否定期检查</td><td>" + record.get('CheckPeriodically') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">检查结果</td><td>" + record.get('CheckResult') + "</td></tr>";
                              
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
                	if(title == '交通车辆管理台账')
                	{
                		var html_str = "";
                		if(title == '交通车辆管理台账')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('CarName')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">车辆编号</td><td width=\"40%\">" + record.get("CarNum") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">机动车车辆</td><td>" + record.get('CarName') + "</td><td style=\"padding:5px;\">机动车所属单位</td><td>" + record.get('Department') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">机动车行驶证</td><td>" + record.get('License') + "</td><td style=\"padding:5px;\">指定驾驶人</td><td>" + record.get('Driver') + "</td></tr>\
             		         \<tr><td style=\"padding:5px;\">驾驶人驾驶证号</td><td>" + record.get('DriverNum') + "</td><td style=\"padding:5px;\">是否定期检查维保</td><td>" + record.get('Maintenance') + "</td></tr>";
                          
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
                	
                	if(title == '个人职业病防护用品')
                	{
                		var html_str = "";
                		if(title == '个人职业病防护用品')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">职业病防护用品名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">发放时间</td><td>" + record.get('DeliveryTime') + "</td><td style=\"padding:5px;\">数量</td><td>" + record.get('Num') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">领取人姓名</td><td>" + record.get('PersonName') + "</td><td style=\"padding:5px;\">所属单位</td><td>" + record.get('Department') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">使用期限</td><td>" + record.get('TimeLimit') + "</td></tr>\
                              		\<tr><td style=\"padding:5px;\">备注</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Comment') + "</td></tr>";
                          
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
                	if(title == '职业健康防护设施')
                	{
                		var html_str = "";
                		if(title == '职业健康防护设施')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">防护设施名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">型号及规格</td><td>" + record.get('Model') + "</td><td style=\"padding:5px;\">制造单位</td><td>" + record.get('Department') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">数量</td><td>" + record.get('Num') + "</td><td style=\"padding:5px;\">购买时间</td><td>" + record.get('BuyTime') + "</td></tr>\
             		         \<tr><td style=\"padding:5px;\">编号</td><td>" + record.get('SerialNumber') + "</td><td style=\"padding:5px;\">使用地点</td><td>" + record.get('Place') + "</td></tr>\
             		         \<tr><td style=\"padding:5px;\">责任人</td><td>" + record.get('Responsibility') + "</td></tr>";
                          
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
                	if(title == '安全标志、标识')
                	{
                		var html_str = "";
                		if(title == '安全标志、标识')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">安全标志、标识名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">数量</td><td>" + record.get('Num') + "</td><td style=\"padding:5px;\">单位</td><td>" + record.get('Department') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">安装时间</td><td>" + record.get('InstallTime') + "</td><td style=\"padding:5px;\">安装地点</td><td>" + record.get('InstallPlace') + "</td></tr>\
             		         \<tr><td style=\"padding:5px;\">责任人</td><td>" + record.get('Responsibility') + "</td></tr>";
                          
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