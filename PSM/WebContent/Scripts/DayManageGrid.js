var tn;

Ext.define('DayManageGrid', {
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
        var IPAddress;
        var port;
        
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
            upload_url: '',
            anchor: '100%'
		})
		
		//在uploadPanel中添加已有文件列表
		var insertFileToList = function()
      	{
        	var info_url = '';
        	var delete_url = '';
        		info_url = 'DayManageAction!getFileInfo';
        		delete_url = 'DayManageAction!deleteOneFile';
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
       			deleteAllUrl = "DayManageAction!deleteAllFile";
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
  	        if( action == "addMeeting" || action == "editMeeting"  )
  	        {
				if( action == "editMeeting" )
				{               			
					ppid = selRecs[0].data.ID;			
				}		
				deleteFile(style, fileName, ppid);
				uploadPanel.store.removeAll();
  	        }
       	}
       	
       	var store_Periodreport = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'Year'},
					 { name: 'Month'},
					 { name: 'Week'},
					 { name: 'Time'},
					 { name: 'Accessory'},
					 { name: 'Type'},
					 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('DayManageAction!getPeriodreportListDef?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title+ "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			},
			autoLoad: true //即时加载数据
		});
       	
       	var store_Fingerprint = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'Name'},
					 { name: 'Ip'},
					 { name: 'Portno'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('DayManageAction!getFingerprintListDef?userName=' + user.name + "&userRole=" + user.role  ),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			},
			autoLoad: true //即时加载数据
		});
		
		var store_Daibanplanmade = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'holiday'},
                     { name: 'planname'},
                     { name: 'bianzhitime'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('DayManageAction!getDaibanplanmadeListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        
        var store_Daibanrecord = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'holiday'},
                     { name: 'ondutytime'},
                     { name: 'ondutyperson'},
                     { name: 'nextperson'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('DayManageAction!getDaibanrecordListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        
        
        var store_Saftyworkrizhi = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'rizhitime'},
                     { name: 'weekday'},
                     { name: 'qixiday'},
                     { name: 'qixinight'},
                     { name: 'degreeday'},
                     { name: 'degreenight'},
                     { name: 'windday'},
                     { name: 'windnight'},
                     { name: 'rizhi'},
                     { name: 'workplan'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('DayManageAction!getSaftyworkrizhiListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
        var store_Meeting = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Time'},
                     { name: 'Place'},
                     { name: 'Topic'},
                     { name: 'Host'},
                     { name: 'Record'},
                     { name: 'Participants'},
                     { name: 'Accessory'},
                     { name: 'Type'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('DayManageAction!getMeetingListDef?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        

		
        var store_PrescribedAction = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'action_id'},
                     { name: 'project'},
                     { name: 'prescribed_action'},
                     { name: 'related_menu'},
                     { name: 'prompt_role'},
                     { name: 'prompt_cycle'},
                     { name: 'completed_date'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('MissionAction!getPrescribedAction?userName=' + user.name + "&userRole=" + user.role + "&project=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        

     var items_Periodreport = [{
			xtype: 'container',
			anchor: '95%',
			layout: 'hbox',
			items:[{
				xtype:'textfield',
				fieldLabel: 'ID',
				labelAlign: 'right',
				anchor:'95%',
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
				anchor:'95%',
				name: 'Accessory',
				hidden: true,
				hiddenLabel: true
			},{
				xtype:'textfield',
				fieldLabel: 'Type',
				labelAlign: 'right',
				anchor:'95%',
				name: 'Type',
				hidden: true,
				hiddenLabel: true
			},{
				xtype:'textfield',
				fieldLabel: '年份',
				labelAlign: 'right',
				anchor:'95%',
				name: 'Year'
			},{
				xtype:'textfield',
				fieldLabel: '月份',
				labelAlign: 'right',
				anchor:'95%',
				name: 'Month'
			},{
				xtype:'textfield',
				fieldLabel: '周次',
				labelAlign: 'right',
				anchor:'95%',
				name: 'Week'
			}]
		},
			uploadPanel
		]
     
     var storemonth = new Ext.data.ArrayStore({
         fields: ['id', 'month'],
         data: [[1, '1'], 
         	[2, '2'], 
         	[3, '3'], 
         	[4, '4'], 
         	[5, '5'], 
         	[6, '6'], 
         	[7, '7'], 
         	[8, '8'], 
         	[9, '9'], 
         	[10, '10'],
             [11, '11'],
     	    [12, '12']]
       });
        	
     var items_Periodreport932933 = [{
			xtype: 'container',
			anchor: '95%',
			layout: 'hbox',
			items:[{
				xtype:'textfield',
				fieldLabel: 'ID',
				labelAlign: 'right',
				anchor:'95%',
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
				anchor:'95%',
				name: 'Accessory',
				hidden: true,
				hiddenLabel: true
			},{
				xtype:'textfield',
				fieldLabel: 'Type',
				labelAlign: 'right',
				anchor:'95%',
				name: 'Type',
				hidden: true,
				hiddenLabel: true
			},{
				xtype:'textfield',
				fieldLabel: '年份',
				labelAlign: 'right',
				anchor:'95%',
				name: 'Year'
			},
//			{
//				xtype:'textfield',
//				fieldLabel: '月份',
//				labelAlign: 'right',
//				anchor:'95%',
//				name: 'Month'
//			},
			{
            	xtype:'combobox',
                fieldLabel: '月份',
                store: storemonth,
                valueField: 'month',
                displayField: 'month',
//                labelWidth: 120,
                editable: false,
                labelAlign: 'right',
                anchor:'95%',
                allowBlank: false,
                name: 'Month'
            }/*,{
				xtype:'textfield',
				fieldLabel: 'Time',
				labelAlign: 'right',
				anchor:'95%',
				name: '报送日期',
				hidden: true,
				hiddenLabel: true
			}*/]
		},{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: {
	        	type:'hbox'
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
	                name: 'tipimg'
	            },
	        	{
	            	xtype:'tbtext',
	                text: '请点击上传文件',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	                listeners: {
						'afterRender': function (item, e, eOpts) {
							if(title=='安全生产管理信息月报表'){
								item.setText('请点击上传安全生产管理信息月报表');
							}else if(title=='事故隐患排查治理台账'){
								item.setText('请点击上传事故隐患排查治理台账');
							}else if(title=='企业职工伤亡事故月报表'){
								item.setText('请点击上传企业职工伤亡事故月报表');
							}
						}
					}
	            }
	        ]},
			uploadPanel
		]
        
          var items_Meeting = [{
	    	xtype: 'container',
	        anchor: '95%',
	        layout: 'hbox',
	        items:[{
	        	xtype: 'container',
	        	//id:'s1',
	        	//itemId:'s11',
	        	//name:'c1',
	        	anchor:'95%',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: 'ID',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ID',
                   	hidden: true,
                    hiddenLabel: true
                },{
	            	xtype:'textfield',
                    fieldLabel: 'Type',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Type',
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
                    anchor:'95%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:"datefield",
	                fieldLabel: '会议时间',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'95%',
	                allowBlank: false
                },{
                	xtype:'textfield',
                    fieldLabel: '会议地点',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Place'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            anchor:'95%',
	            //id:'s2',
	            //itemId:'s22',
	            //name:'c2',
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '主持人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Host'
                },{
                	xtype:'textfield',
                    fieldLabel: '记录人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Record'
//                    listeners: {
//                    	'focus':AddZB
//                    }
                }]
	    },{
	        	xtype: 'container',
	            flex: 1,
	            //id:'s3',
	            //itemId:'s33',            
	            //name:'c3',
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '参加人员',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Participants'
                
	        },{
                	xtype:'textfield',
                    fieldLabel: '会议主题',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Topic'
                }]}]
	    },{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: {
	        	type:'hbox'
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
	                name: 'tipimg'
	            },
	        	{
	            	xtype:'tbtext',
	                text: '请点击上传文件',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	                listeners: {
						'afterRender': function (item, e, eOpts) {
							if(title=='安委会会议'){
								item.setText('请点击上传安委会会议纪要');
							}else if(title=='安全生产工作会议'){
								item.setText('请点击上传安全生产工作会会议纪要');
							}else if(title=='安全生产分析会议'){
								item.setText('请点击上传安全生产分析会议会议纪要');
							}else if(title=='安全监督例会'){
								item.setText('请点击上传安全监督例会会给予');
							}else if(title=='其他会议'){
								item.setText('请点击上传安全会议会议记录');
							}
						}
					}
	            }
	        ]},
        	uploadPanel]
        	
        	
     var MonitorName_store = new Ext.data.JsonStore({
            proxy: {
                type: 'ajax',
                url: 'MapAction!getMonitorNameList'
            },
            reader: {
                type: 'json'
            },
            fields: ['MonitorName'],
            autoLoad: true
        });
        
        
     var teststore = new Array();
     var testdata = MonitorName_store.getRange();
     for(var i=0;i<MonitorName_store.getCount();i++) {
     	teststore.push(MonitorName_store.getAt(i));
     }
          
     var items_Fingerprint = [{
  	   xtype: 'container',
  	   anchor: '100%',
  	   layout: 'hbox',
  	   items:[{
  		   xtype: 'container',
  		   flex: 1,
  		   layout: 'anchor',
  		   items:[{
  			   xtype:'textfield',
  			   fieldLabel: 'ID',
  			   labelWidth: 120,
  			   labelAlign: 'right',
  			   anchor:'95%',
  			   name: 'ID',
  			   hidden: true,
  			   hiddenLabel: true
  		   },{
  			   xtype: 'combo',
                    fieldLabel: '监控点名称',
                    labelWidth: 100,
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    allowBlank:false,
                    anchor:'95%',
                    name: 'Name',
                    mode:'local',
                    triggerAction: 'all',
                    editable:false,
                    store: MonitorName_store,
                    displayField:'MonitorName'
  		   },{
	            		xtype:'textfield',
	            		fieldLabel: 'IP',
	            		labelAlign: 'right',
	            		allowBlank:false,
	            		anchor:'95%',
	            		name: 'Ip'
	            	},{
	            		xtype:'textfield',
	            		fieldLabel: '端口号',
	            		labelAlign: 'right',
	            		allowBlank:false,
	            		anchor:'95%',
	            		name: 'Portno'
	            	}]
  	   }]
     }]  
          
          
          var items_Daibanplanmade = [{
	    	xtype: 'container',
	        anchor: '95%',
	        layout: 'hbox',
	        items:[{
	        	xtype: 'container',
	        	//id:'s1',
	        	//itemId:'s11',
	        	//name:'c1',
	        	anchor:'95%',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: 'ID',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
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
                    fieldLabel: 'Type',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Type',
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
                	xtype:'combo',
  					fieldLabel: '节假日',
  					labelAlign: 'right',
  					anchor:'95%',
  					store:["元旦","春节","清明节","劳动节","端午节","中秋节","国庆节"],
  				   // afterLabelTextTpl: required,	//红色星号
  		  
  					//value :'5',
  					name: 'holiday'
                },{
                	xtype:'datefield',
                    fieldLabel: '编制时间',
                    format : 'Y-m-d',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'bianzhitime'
                }]
	        },{
  	        	xtype: 'container',
  	        	anchor:'95%',
  	            flex: 1,
  	            layout: 'anchor',
  	            items: [{
                	xtype:'textfield',
                    fieldLabel: '计划名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'planname'
                }]
  	        }]
	    }, {
	    	xtype: 'container',
	        anchor: '100%',
	        layout: {
	        	type:'hbox'
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
	                name: 'tipimg'
	            },
	        	{
	            	xtype:'tbtext',
	                text: '请点击上传领导带班值班计划',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip'
	            }
	        ]},
        	uploadPanel]
          
          
          
          var items_Daibanrecord = [{
  	    	xtype: 'container',
  	        anchor: '95%',
  	        layout: 'hbox',
  	        items:[{
  	        	xtype: 'container',
  	        	//id:'s1',
  	        	//itemId:'s11',
  	        	//name:'c1',
  	        	anchor:'95%',
  	            flex: 1,
  	            layout: 'anchor',
  	            items: [{
  	            	xtype:'textfield',
                      fieldLabel: 'ID',
                      //labelWidth: 120,
                      labelAlign: 'right',
                      anchor:'95%',
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
                      anchor:'95%',
                      name: 'Accessory',
                     	hidden: true,
                      hiddenLabel: true
                  },{
    					xtype:'combo',
      					fieldLabel: '节假日',
      					labelAlign: 'right',
      					anchor:'95%',
      					store:["元旦","春节","清明节","劳动节","端午节","中秋节","国庆节"],
      				   // afterLabelTextTpl: required,	//红色星号
      		  
      					//value :'5',
      					name: 'holiday'
      				},{
                    	xtype:'datefield',
                        fieldLabel: '值班日期',
                        //labelWidth: 120,
                        labelAlign: 'right',
                        format : 'Y-m-d',
                        anchor:'95%',
                        allowBlank: false,
                        name: 'ondutytime'
                    }]
  	        },{
  	        	xtype: 'container',
  	        	anchor:'95%',
  	            flex: 1,
  	            layout: 'anchor',
  	            items: [{
                  	xtype:'textfield',
                    fieldLabel: '值班人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'ondutyperson'
                },{
                  	xtype:'textfield',
                      fieldLabel: '接班人',
                      //labelWidth: 120,
                      labelAlign: 'right',
                      anchor:'95%',
                      allowBlank: false,
                      name: 'nextperson'
                  }]
  	        }]
  	    }, {
	    	xtype: 'container',
	        anchor: '100%',
	        layout: {
	        	type:'hbox'
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
	                name: 'tipimg'
	            },
	        	{
	            	xtype:'tbtext',
	                text: '请点击上传领导带班值班记录',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip'
	            }
	        ]},
          	uploadPanel]
          
          

          
          
          var weekcombo = new Ext.data.ArrayStore({
              fields: ['id', 'week'],
              data: [[1, '星期一'], 
              	     [2, '星期二'], 
              	     [3, '星期三'], 
              	     [4, '星期四'], 
              	     [5, '星期五'], 
              	     [6, '星期六'], 
              	     [7, '星期日']]
            });
          
 

          var qixicombo = new Ext.data.ArrayStore({
              fields: ['id', 'qixi'],
              data: [
            	     [1, '晴'], 
              	     [2, '阴'], 
              	     [3, '雾'], 
              	     [4, '小雨'], 
              	     [5, '大雨'], 
              	     [6, '雷阵雨'], 
              	     [7, '冰雹'],
            	     [8, '冻雨'], 
              	     [9, '雨夹雪'], 
              	     [10, '小雪'], 
              	     [11, '中雪'], 
              	     [12, '大雪'], 
              	     [13, '霜冻'], 
              	     [14, '低压槽和高压脊'],
            	     [15, '冷锋和暖锋'], 
              	     [16, '大风']]
            });
          
          var windcombo = new Ext.data.ArrayStore({
              fields: ['id', 'wind'],
              data: [
            	     [1, '1级风'], 
              	     [2, '2级风'], 
              	     [3, '3级风'], 
              	     [4, '4级风'], 
              	     [5, '5级风'], 
              	     [6, '6级风'], 
              	     [7, '7级风'],
            	     [8, '8级风'], 
              	     [9, '9级风'], 
              	     [10, '10级风'], 
              	     [11, '11级风'], 
              	     [12, '12级风'],
              	     [13, '13级风'], 
              	     [14, '14级风'], 
              	     [15, '15级风'], 
                   	 [16, '16级风'], 
                   	 [17, '17级风']]
            });

          
        
        
//          var date = new Date();
          var items_Saftyworkrizhi = [{
  	    	xtype: 'container',
  	        anchor: '95%',
  	        layout: 'hbox',
  	        items:[{
  	        	xtype: 'container',
  	        	anchor:'95%',
  	            flex: 1,
  	            layout: 'anchor',
  	            items: [{
	            	xtype:'textfield',
                    fieldLabel: 'ID',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
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
                    anchor:'95%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'datefield',
                    fieldLabel: '日期',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    format : 'Y-m-d',
                    anchor:'95%',
                    allowBlank: false,
                    value:new Date(),
                    editable: false,
                    listeners: {
	                    change: function(datefield,record) {
	                  　　                          datefield.setValue(new Date());
	                    }
	                },
                    name: 'rizhitime'
                },{
  	            	xtype:'combobox',
                    fieldLabel: '星期',
                    store: weekcombo,
                    valueField: 'week',
                    displayField: 'week',
//                    labelWidth: 120,
                    editable: false,
                    triggerAction: 'all',
                    allowBlank: false,
                    mode: 'local',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'weekday'
                }]
  	        },{
  	        	xtype: 'container',
  	            flex: 1,
  	            anchor:'95%',
  	            layout: 'anchor',
  	            items: [{
  	            	xtype:'combobox',
                    fieldLabel: '气象白天',
                    store: qixicombo,
                    valueField: 'qixi',
                    displayField: 'qixi',
//                    labelWidth: 120,
                    editable: false,
                    triggerAction: 'all',
                    allowBlank: false,
                    mode: 'local',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'qixiday'
                },{
  	            	xtype:'combobox',
                    fieldLabel: '气象夜间',
                    store: qixicombo,
                    valueField: 'qixi',
                    displayField: 'qixi',
//                    labelWidth: 120,
                    editable: false,
                    triggerAction: 'all',
                    allowBlank: false,
                    mode: 'local',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'qixinight'
                }]
  	       },{
  	        	xtype: 'container',
  	            flex: 1,
  	            layout: 'anchor',
  	            items: [{
                	xtype:'numberfield',
                    fieldLabel: '气温最高(℃)',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'degreeday'
                },{
                	xtype:'numberfield',
                    fieldLabel: '气温最低(℃)',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'degreenight'
                }]
  	        },{
  	        	xtype: 'container',
  	            flex: 1,
  	            layout: 'anchor',
  	            items: [{
  	            	xtype:'combobox',
                    fieldLabel: '风力白天',
                    store: windcombo,
                    valueField: 'wind',
                    displayField: 'wind',
//                    labelWidth: 120,
                    editable: false,
                    triggerAction: 'all',
                    allowBlank: false,
                    mode: 'local',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'windday'
                },{
    	            xtype:'combobox',
                    fieldLabel: '风力夜间',
                    store: windcombo,
                    valueField: 'wind',
                   displayField: 'wind',
//                        labelWidth: 120,
                  editable: false,
                    triggerAction: 'all',
                    allowBlank: false,
                    mode: 'local',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'windnight'
                }]
  	        }]
  	    },{ 
            xtype:'textarea',
            fieldLabel: '日志记录',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过1000个字符',
            maxLength:500,
            anchor:'100%',
            allowBlank: false,
            name: 'rizhi'
	    },{ 
            xtype:'textarea',
            fieldLabel: '工作计划/重点',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            allowBlank: false,
            name: 'workplan'
	    },
          	uploadPanel]
            
          //分配任务
          Ext.define('Item', {
  			extend: 'Ext.data.Model',
  			fields: ['text']
  		})
  		
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
  	
  	Ext.Ajax.request({
  		async: true, 
  		method : 'POST',
  		url: encodeURI('MissionAction!getPersonNode'),
  		success: function(response){
  			personList = Ext.decode(response.responseText); //返回监控点列表
  		}
  	});
  	
  	for ( var p in personList )
  	{  
  		 var newnode= Ext.create('Ext.data.NodeInterface',{leaf:false});             
  		 pnode = storeAllperson.getRootNode(); 		
  		 var newnode = pnode.createNode(newnode);            
  		 newnode.set("text",p); 
  		 var allperson = personList[p].split(",");
  		 for(var i = 0;i<allperson.length;i++)
  		 {
  			 var newde=[{text:allperson[i],leaf:true}];  //新节点信息   
  			 newnode.appendChild(newde);
  		 }
  		
  		 pnode.appendChild(newnode); //添加子节点  
  		 pnode.set('leaf',false);  
  		 pnode.expand(); 
  	
  	}
  		
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
              // afterLabelTextTpl: required,   //红色星号
     
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
                            for(var i = nodeList.length-1;i>=0;i--) {
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
              // anchor:'80%',
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
        	
         if(title == '安委会会议' || title == '安全生产工作会议' || title == '安全生产分析会议' || title == '安全监督例会' || title == '其他会议')
        	{	        	
        		
        		actionURL = 'DayManageAction!addMeeting?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_Meeting;
        		//items2 = items_securityPerson;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addMeeting',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addMeeting', title: '新增项目', items: [forms]});
        	}
         
         
         else if(title == '计划制定' )
     	{	        	
     		
     		actionURL = 'DayManageAction!addDaibanplanmade?userName=' + user.name + "&userRole=" + user.role;  
     		uploadURL = "UploadAction!execute";
     		items = items_Daibanplanmade;
     		//items2 = items_securityPerson;
         	createForm({
     			autoScroll: true,
 	        	bodyPadding: 5,
 	        	action: 'addDaibanplanmade',
 	        	url: actionURL,
 	        	items: items
 	        });
         	uploadPanel.upload_url = uploadURL;
         	bbar.moveFirst();	//状态栏回到第一页
 	        showWin({ winId: 'addDaibanplanmade', title: '新增项目', items: [forms]});
     	}
         else  if(title=='指纹考勤'){
     		actionURL = 'DayManageAction!addFingerprint?userName='+ user.name + "&userRole=" + user.role;
     		items = items_Fingerprint;
     		createForm({
     			autoScroll: true,
 	        	bodyPadding: 5,
 	        	action: 'addFingerprint',
 	        	url: actionURL,
 	        	items: items
     		});
     		bbar.moveFirst();	//状态栏回到第一页
     		showWin({ winId: 'addFingerprint', title: '新增项目', items: [forms]});
     	}
         
        else  if(title == '带班值班记录' )
      	{	        	
      		
      		actionURL = 'DayManageAction!addDaibanrecord?userName=' + user.name + "&userRole=" + user.role;  
      		uploadURL = "UploadAction!execute";
      		items = items_Daibanrecord;
      		//items2 = items_securityPerson;
          	createForm({
      			autoScroll: true,
  	        	bodyPadding: 5,
  	        	action: 'addDaibanrecord',
  	        	url: actionURL,
  	        	items: items
  	        });
          	uploadPanel.upload_url = uploadURL;
          	bbar.moveFirst();	//状态栏回到第一页
  	        showWin({ winId: 'addDaibanrecord', title: '新增项目', items: [forms]});
      	}
       else   if(title == '安全工作日志' )
       	{	        	
       		
       		actionURL = 'DayManageAction!addSaftyworkrizhi?userName=' + user.name + "&userRole=" + user.role;  
       		uploadURL = "UploadAction!execute";
       		items = items_Saftyworkrizhi;
       		//items2 = items_securityPerson;
           	createForm({
       			autoScroll: true,
   	        	bodyPadding: 5,
   	        	action: 'addSaftyworkrizhi',
   	        	url: actionURL,
   	        	items: items
   	        });
           	uploadPanel.upload_url = uploadURL;
           	bbar.moveFirst();	//状态栏回到第一页
   	        showWin({ winId: 'addSaftyworkrizhi', title: '新增项目', items: [forms]});
       	}
         
       else if (title == '安全周报' ) {
			actionURL = 'DayManageAction!addPeriodreport?userName=' + user.name + "&userRole=" + user.role + "&type=" + title; 
			items = items_Periodreport;
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
    
   else if (title == '安全生产管理信息月报表' || title == '事故隐患排查治理台账'|| title == '企业职工伤亡事故月报表') {
		actionURL = 'DayManageAction!addPeriodreport?userName=' + user.name + "&userRole=" + user.role + "&type=" + title; 
		items = items_Periodreport932933;
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
        			
					if(title == '安委会会议' || title == '安全生产工作会议' || title == '安全生产分析会议' || title == '安全监督例会' || title == '其他会议')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'DayManageAction!editMeeting?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
        				items = items_Meeting;
              
        			createForm({
            			autoScroll: true,
            			action: 'editMeeting',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editMeeting', title: '修改项目', items: [forms]});
        			}
					
					else if(title=='指纹考勤'){
     					actionURL = 'DayManageAction!editFingerprint?userName='+ user.name + "&userRole=" + user.role;
     					items = items_Fingerprint;
     					createForm({
                			autoScroll: true,
                			action: 'editFingerprint',
            	        	bodyPadding: 5,
            	        	url: actionURL,
            	        	items: items
            	        });
            			
                    	bbar.moveFirst();	//状态栏回到第一页
            	        showWin({ winId: 'editFingerprint', title: '修改项目', items: [forms]});
     				}
					else if(title == '计划制定')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'DayManageAction!editDaibanplanmade?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Daibanplanmade;
              
        			createForm({
            			autoScroll: true,
            			action: 'editDaibanplanmade',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editDaibanplanmade', title: '修改项目', items: [forms]});
        			}
					
				else	if(title == '带班值班记录')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'DayManageAction!editDaibanrecord?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Daibanrecord;
              
        			createForm({
            			autoScroll: true,
            			action: 'editDaibanrecord',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editDaibanrecord', title: '修改项目', items: [forms]});
        			}
					
					
					
				else	if(title == '安全工作日志')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'DayManageAction!editSaftyworkrizhi?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Saftyworkrizhi;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftyworkrizhi',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftyworkrizhi', title: '修改项目', items: [forms]});
        			}
        			
				else	if (title == '安全周报' ) {
					insertFileToList();
					actionURL = 'DayManageAction!editPeriodreport?userName=' + user.name + "&userRole=" + user.role + "&type=" + title; 
					items = items_Periodreport;
					
					createForm({
					autoScroll: true,
					action: 'editProject',
					bodyPadding: 5,
					url: actionURL,
					items: items
				});
				uploadPanel.upload_url = "UploadAction!execute";
//            	bbar.moveFirst();	//状态栏回到第一页
				showWin({ winId: 'editProject', title: '修改文件', items: [forms]});
				}
    		else	if (title == '安全生产管理信息月报表' || title == '事故隐患排查治理台账'|| title == '企业职工伤亡事故月报表') {
    			insertFileToList();
    			
    			actionURL = 'DayManageAction!editPeriodreport?userName=' + user.name + "&userRole=" + user.role + "&type=" + title; 
				items = items_Periodreport932933;
				
				createForm({
				autoScroll: true,
				action: 'editProject',
				bodyPadding: 5,
				url: actionURL,
				items: items
			});
				uploadPanel.upload_url = "UploadAction!execute";
	//        	bbar.moveFirst();	//状态栏回到第一页
				showWin({ winId: 'editProject', title: '修改文件', items: [forms]});
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
    					
    					
    					if(title == '安委会会议' || title == '安全生产工作会议' || title == '月度安全生产分析会议' || title == '安全监督例会' || title == '其他会议')
    					{$.getJSON(encodeURI("DayManageAction!deleteMeeting?userName=" + user.name + "&userRole=" + user.role + "&type=" + title),
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
    					
    					else if(title == '计划制定')
    					{$.getJSON(encodeURI("DayManageAction!deleteDaibanplanmade?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					else if(title=='指纹考勤')
			 			{$.getJSON(encodeURI("DayManageAction!deleteFingerprint?userName=" + user.name + "&userRole=" + user.role),
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
	                                });	//Ajax参数
			 			}
    					
    					else if(title == '带班值班记录')
    					{$.getJSON(encodeURI("DayManageAction!deleteDaibanrecord?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					else if (title == '安全周报' || title == '安全生产管理信息月报表' || title == '事故隐患排查治理台账'|| title == '企业职工伤亡事故月报表') {
							delete_url = 'DayManageAction!deletePeriodreport?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;
							$.getJSON(encodeURI(delete_url),
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
    					
    					
    					else if(title == '安全工作日志')
    					{$.getJSON(encodeURI("DayManageAction!deleteSaftyworkrizhi?userName=" + user.name + "&userRole=" + user.role),
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
        var btnAllotask = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '任务分配',
            icon: "Images/ims/toolbar/view.png",
           	disabled: true,
            handler: Allotask
           	
        //----------LZZ---------------//
        })
        
        //liuchi
        
        
            
	  var getSelKQ = function (grid) {
          selRecs = [];  //清空数组
          keyIDs = [];
          keyNames = [];
           
          selRecs = grid.getSelectionModel().getSelection();
          IPAddress = selRecs[0].data.Ip;
          port = selRecs[0].data.Portno;
          //Ext.Msg.alert('a',IPAddress +",,," +port);
          if (selRecs.length === 0 ) {
              Ext.Msg.alert('警告', '没有选中任何记录！');
              return false;
          }
          if (selRecs.length > 1 ) {
              Ext.Msg.alert('警告', '只能选一条记录！');
              return false;
          }
          return true;
      };
      
      
      
      
	  createFormKQ = function (config) {
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
              renderTo: Ext.getBody()
      	});
      };
      
    
	
	
	var kaoQingH = function() {
		alert(MonitorName_store.data.items[0].data['MonitorName']);
		if(getSelKQ(gridDT)) {
			
			var kaoQingStore = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'EnrollNumber'},
					 
					 { name: 'VerifyMode'},
					 { name: 'InOutMode'},
					 { name: 'Time'},
					 //{ name: 'WorkCode'},
					 { name: 'Name'}
			],
			//pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('DayManageAction!getAttLogListSearch?IPAddress=' + IPAddress + "&port=" + port),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			},
			autoLoad: true //即时加载数据
		});
            
            var gridKaoQing = Ext.create('Ext.grid.Panel', {       
		selModel: { selType: 'checkboxmodel'},   //选择框
		store: kaoQingStore,
		stripeRows: true,
		columnLines: true,
		//multiSelect : false,
		columns:  [
    		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},		
    		    { text: '用户编号', dataIndex: 'EnrollNumber', align: 'center', width: 150},
  	            { text: '用户姓名', dataIndex: 'Name', align: 'center', width: 100},
  	            { text: '验证方式', dataIndex: 'VerifyMode', align: 'center', width: 100},
  	            { text: '考勤状态', dataIndex: 'InOutMode', align: 'center', width: 100},
  	            { text: '考勤时间', dataIndex: 'Time', align: 'center', width: 200}
  	            //{ text: 'WorkCode', dataIndex: 'WorkCode', align: 'center', width: 100}
        ],
        viewConfig: {
	    	loadMask: false,
            loadMask: {                       //IE8不兼容loadMask
            	msg: '正在加载数据中……'
            }
        }
    
    });
		
    		createFormKQ({
    			autoScroll: true,
    			action: 'kaoQing',
    			bodyPadding: 5,
     	       	items: gridKaoQing
    		});	
    		//bbar.moveFirst();	//状态栏回到第一页
	        showWinKQ({ winId: 'kaoQing', title: '考勤记录', items: [forms]});
		}
	}
	
	var showWinKQ = function (config) {
    	var width = 720;
    	var height = 720;
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
        
        
        
        
        
        
        var btnKQ = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '考勤',
            icon: "Images/ims/toolbar/search.png",
            disabled: true,
            handler: kaoQingH
        })
        
        
        var userH = function() {
		if(getSelKQ(gridDT)) {
			
			var userStore = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'EnrollNumber'},
					 { name: 'Name'},
					 { name: 'Password'},
					 { name: 'Privilege'},
					 { name: 'Enabled'}
			],
			//pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('DayManageAction!getUserInfoListSearch?IPAddress=' + IPAddress + "&port=" + port),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			},
			autoLoad: true //即时加载数据
		});
            
            var gridUser = Ext.create('Ext.grid.Panel', {       
		selModel: { selType: 'checkboxmodel'},   //选择框
		store: userStore,
		stripeRows: true,
		columnLines: true,
		//multiSelect : false,
		columns:  [
    		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},		
    		    { text: '用户编号', dataIndex: 'EnrollNumber', align: 'center', width: 150},
  	            { text: '用户姓名', dataIndex: 'Name', align: 'center', width: 100},
  	            { text: '用户密码', dataIndex: 'Password', align: 'center', width: 100},
  	            { text: '用户权限', dataIndex: 'Privilege', align: 'center', width: 150},
  	            { text: '用户状态', dataIndex: 'Enabled', align: 'center', width: 100}
        ],
        viewConfig: {
	    	loadMask: false,
            loadMask: {                       //IE8不兼容loadMask
            	msg: '正在加载数据中……'
            }
        }
    
    });
		
    		createFormKQ({
    			autoScroll: true,
    			action: 'user',
    			bodyPadding: 5,
     	       	items: gridUser
    		});	
    		//bbar.moveFirst();	//状态栏回到第一页
	        showWinKQ({ winId: 'user', title: '用户信息', items: [forms]});
		}
	}
        
        
        var btnUser = Ext.create('Ext.Button', {
        	width: 120,
        	height: 32,
        	text: '用户信息',
            icon: "Images/ims/toolbar/search.png",
            disabled: true,
            handler: userH
        })
        
        var synH = function() {
			if(getSelKQ(gridDT)) {
				Ext.Msg.confirm('同步', '确定需要同步吗？', function (buttonID) {
    				if (buttonID === 'yes') {
    					
    					var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在同步中..."});
    					myMask.show();
    					$.getJSON(encodeURI('DayManageAction!synZhiWen?IPAddress=' + IPAddress + "&port=" + port),
    							//{id: keyIDs.toString()},	//Ajax参数
    					
                                function (res) 
                                {
                                	myMask.destroy();
    								if (res.success) 
    								{
    									
                                        Ext.Msg.alert("信息", "同步成功");
                                    }
                                    else 
                                    {
                                    	Ext.Msg.alert("信息", res.msg);
                                    }
                                });
    				}
        		});
			}
		}
			
			
        
        var btnSyn = Ext.create('Ext.Button', {
        	width: 120,
        	height: 32,
        	text: '同步信息',
            icon: "Images/ims/toolbar/search.png",
            disabled: true,
            handler: synH
        })
        
	
	
	
	
        //liuchi
        
        //建立工具栏
        var tbar = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        })

        
		if(title == '安委会会议' || title == '安全生产工作会议' || title == '安全生产分析会议' || title == '安全监督例会' || title == '其他会议')
        {	
        	dataStore = store_Meeting;
        	
        	queryURL = 'DayManageAction!getMeetingListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '会议时间', dataIndex: 'Time', align: 'center', width: 150},
            	    { text: '会议地点', dataIndex: 'Place', align: 'center', width: 200},
            	    { text: '会议主题', dataIndex: 'Topic', align: 'center', width: 150},
            	    { text: '主持人', dataIndex: 'Host', align: 'center', width: 150},
            	    { text: '记录人', dataIndex: 'Record', align: 'center', width: 150},
            	    { text: '参加人员', dataIndex: 'Participants', align: 'center', width: 150},
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
            	    { text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
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
		else  if(title=='指纹考勤')
    	{
    	dataStore = store_Fingerprint;
    	queryURL = 'DayManageAction!getFingerprintListSearch?userName=' + user.name + "&userRole=" + user.role;
    	column = [
    	          { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
    	          { text: '监控点名称', dataIndex: 'Name', align: 'center', width: 250},
    	          { text: 'IP', dataIndex: 'Ip', align: 'center', width: 250},
    	          { text: '端口号', dataIndex: 'Portno', align: 'center', width: 250}
    	          ]
    	tbar.add(textSearch);
 		tbar.add(btnSearch);
 		tbar.add(btnSearchR);
 		tbar.add("-");
 		tbar.add(btnAdd);
 		tbar.add(btnEdit);
 		tbar.add(btnDel);
 		tbar.add(btnKQ);
 		tbar.add(btnUser);
 		tbar.add(btnSyn);
    	}
        
		else if (title == '规定动作') {
			dataStore = store_PrescribedAction;	
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '项目部', dataIndex: 'project', align: 'center', width: 400},
					{ text: '规定动作', dataIndex: 'prescribed_action', align: 'center', width: 400},
					{ text: '完成时间', dataIndex: 'completed_date', align: 'center', width: 250},
			]
		}
        
		else if(title == '安全周报' ) {	
			dataStore = store_Periodreport;	
			queryURL = 'DayManageAction!getPeriodreportListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=" + projectName;
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '年份', dataIndex: 'Year', align: 'center', width: 150},
					{ text: '月份', dataIndex: 'Month', align: 'center', width: 200},
					{ text: '周次', dataIndex: 'Week', align: 'center', width: 150},
					//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
					{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
				]
				tbar.add(textSearch);
				tbar.add(btnSearch);
				tbar.add(btnSearchR);
				tbar.add(btnScan);
				tbar.add("-");
				tbar.add(btnAdd);
				tbar.add(btnEdit);
				tbar.add(btnDel);
				tbar.add(btnAllotask);
				if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        		
        		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'DayManageAction!getPeriodreportListDef?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title+ "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 200}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			
        		}

        		
        		if(user.role === '项目部人员')
      				tbar.remove(btnAllotask);
		}
        
        else if(title == '安全生产管理信息月报表' || title == '事故隐患排查治理台账'|| title == '企业职工伤亡事故月报表') {	
			dataStore = store_Periodreport;	
			queryURL = 'DayManageAction!getPeriodreportListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=" + projectName;
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '年份', dataIndex: 'Year', align: 'center', width: 150},
					{ text: '月份', dataIndex: 'Month', align: 'center', width: 200},
					{ text: '报送日期', dataIndex: 'Time', align: 'center', width: 150},
					//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
					{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
				]
				tbar.add(textSearch);
				tbar.add(btnSearch);
				tbar.add(btnSearchR);
				tbar.add(btnScan);
				tbar.add("-");
				tbar.add(btnAdd);
				tbar.add(btnEdit);
				tbar.add(btnDel);
				tbar.add(btnAllotask);
				
				if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'DayManageAction!getPeriodreportListDef?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title+ "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 200}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			
        		}
				
				if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        		if(user.role === '项目部人员')
      				tbar.remove(btnAllotask);
		}
        
        
        
    
        
        else if(title == '计划制定')
        {	
        	dataStore = store_Daibanplanmade;
        	
        	queryURL = 'DayManageAction!getDaibanplanmadeListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '节假日', dataIndex: 'holiday', align: 'center', width: 150},
            	    { text: '计划名称', dataIndex: 'planname', align: 'center', width: 200},
            	    { text: '编制时间', dataIndex: 'bianzhitime', align: 'center', width: 150},
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
            	    { text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
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

        
        else if(title == '带班值班记录')
        {	
        	dataStore = store_Daibanrecord;
        	
        	queryURL = 'DayManageAction!getSaftyworkrizhiListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '节假日', dataIndex: 'holiday', align: 'center', width: 150},
            	    { text: '值班日期', dataIndex: 'ondutytime', align: 'center', width: 200},
            	    { text: '值班人', dataIndex: 'ondutyperson', align: 'center', width: 100},
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
            	    { text: '接班人', dataIndex: 'nextperson', align: 'center', width: 100}
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
        
        
      
        
        else if(title == '安全工作日志')
        {	
        	dataStore = store_Saftyworkrizhi;
        	
        	queryURL = 'DayManageAction!getDaibanrecordListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '日期', dataIndex: 'rizhitime', align: 'center', width: 150},
            	    { text: '星期', dataIndex: 'weekday', align: 'center', width: 100},
            	    { text: '气象白天', dataIndex: 'qixiday', align: 'center', width: 100},
            	    { text: '气象夜间', dataIndex: 'qixinight', align: 'center', width: 100},
            	    { text: '气温最高(℃)', dataIndex: 'degreeday', align: 'center', width: 100},
            	    { text: '气温最低(℃)', dataIndex: 'degreenight', align: 'center', width: 100},
            	    { text: '风力白天', dataIndex: 'windday', align: 'center', width: 100},
            	    { text: '风力夜间', dataIndex: 'windnight', align: 'center', width: 100},
            	    { text: '日志记录', dataIndex: 'rizhi', align: 'center', width: 200},
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
            	    { text: '工作计划/重点', dataIndex: 'workplan', align: 'center', width: 200}
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

                				case "addMeeting":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					
                				}
                				
                				case "editMeeting": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					break;
                				}
                				
                				case "editProject": {
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									break;
								}
								case "addProject":{
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									uploadPanel.store.removeAll();
								} 
                				
                				case "addDaibanplanmade":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					
                				}
                				
                				case "editDaibanplanmade": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					break;
                				}
                				
                				
                				
                				case "addDaibanrecord":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					
                				}
                				
                				case "editDaibanrecord": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					break;
                				}
                				
                				
                				
                				case "addSaftyworkrizhi":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					
                				}
                				
                				case "editSaftyworkrizhi": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
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
          	                		if (title == '安全生产管理信息月报表'||title == '事故隐患排查治理台账'||title == '企业职工伤亡事故月报表') actionId = 32;
          	                		else if(title == '安委会会议') actionId = 39;
          	                		else if(title == '安全生产工作会议') actionId = 40;
          	                		else if(title == '安全监督例会') actionId = 41;
          	                		
          	                		
          	                		
          	                		
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
      	                	if(config.action == "addSaftyworkrizhi" || config.action == "editSaftyworkrizhi")
                    		{
                    			var rizhi = forms.getForm().findField('rizhi');
                    			var rizhitext = brief.getValue();
                    			var workplan = forms.getForm().findField('workplan');
                    			var workplantext = brief.getValue();
                    			if(workplantext.length>500||rizhitext.length>100)
                    			{
                    				Ext.Msg.alert('警告','专利简介不能超过500个字符！');
                    			}
                    			else
                    			{
                    				Ext.Msg.alert('警告','请完善信息！');
                    			}
                    		}
//      	                	else if(config.action == "addProjectperson" || config.action == "editProjectperson")
//                    		{
//                    			var brief = forms.getForm().findField('Duty');
//                    			var brieftext = brief.getValue();
//                    			if(brieftext.length>500)
//                    			{
//                    				Ext.Msg.alert('警告','职责不能超过500个字符！');
//                    			}
//                    			else
//                    			{
//                    				Ext.Msg.alert('警告','请完善信息！');
//                    			}
//                    		}
      	                	else
      	                	{
      	                		Ext.Msg.alert('警告','请完善信息！');
      	                	}
      	                }
                	}
                },{
                	text: '重置',
                	handler: function(){  
                		
                		if (tableID != 127) {
                		DeleteFile(config.action);
                		uploadPanel.store.removeAll();
                		insertFileToList();
                		}
                		forms.form.reset();
                	
                        if (config.action == "editMeeting") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        else if (config.action == "editDaibanplanmade") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        else if (config.action == "editDaibanrecord") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        else if (config.action == "editSaftyworkrizhi") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        
                        else if (config.action == "editProject") {
							forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
						}
                        else if(config.action == 'alloTask')
                    	{
                           var selRecs = gridDT.getSelectionModel().getSelection();
                		   // 设置表单初始值 		   
                		   forms.getForm().findField('missionname').setValue(selRecs[0].data.No);
                    	}
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
            

            if (config.action == "editMeeting") 
            {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            else if (config.action == "editDaibanplanmade")
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            else if (config.action == "editDaibanrecord") 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            else if (config.action == "editSaftyworkrizhi") 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
			
            else if (config.action == 'editProject') {
            	selRecs = [];  //清空数组
                selRecs = gridDT.getSelectionModel().getSelection();
                console.log(selRecs[0]);
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据
            }
            if (config.action == 'editFingerprint') {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据
            }
            
        };       
        
        //创建装载formPanel的窗体，由工具栏按钮点击显示
        var showWin = function (config) {
        	var width = 420;
        	var height = 320;
        	
  
        	
           
        		width = 1000;
        		height = 500;
        		
        		if(title == '计划制定')
            	{//编辑提案信息框
            		width = 800;
            		height = 500;
            	}
        		else if(title == '指纹考勤')
            	{
            		width = 500;
            		height = 250;
            	}

        	else if (tableID == 128) {
				width = 300;
				height = 200;
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
        				btnAllotask.enable();
        				btnEdit.enable();        			
        				btnScan.enable();
        				btnKQ.enable();
        				btnUser.enable();
        				btnSyn.enable();
        			}
        			else
        			{
        				btnAllotask.disable();
        				btnEdit.disable();
        				btnScan.disable();
        				btnKQ.disable();
        				btnUser.disable();
        				btnSyn.disable();
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
                	if(title == '项目概况')
                	{
                		var html_str = "";
                		if(title == '项目概况')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">项目名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">编号</td><td>" + record.get('No') + "</td><td style=\"padding:5px;\">规模</td><td>" + record.get('Scale') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">建设单位</td><td>" + record.get('BuildUnit') + "</td><td style=\"padding:5px;\">地理位置</td><td>" + record.get('Place') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">进度</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Progress') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">建设内容</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('BuildContent') + "</td></tr>";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
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
                	
                	if(title == '指纹考勤')
                 	{
                 		var html_str = "";
                 		if(title == '指纹考勤')
                 		{
                 			var record = dataStore.getAt(rowIndex);
              		         var Num = rowIndex+1;
              		         //alert(record.get('pName'));
              		        html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">监控点名称</td><td width=\"40%\">" + record.get('Name') + "</td></tr>\
              		        	\<tr><td style=\"padding:5px;\">IP</td><td>" + record.get('Ip') + "</td><td style=\"padding:5px;\">端口号</td><td>" + record.get('Portno') + "</td></tr>"
                               
                             
                           //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
              		         html_str += "</table>";
                 		}  		
                         Ext.create('Ext.window.Window', 
                         {
                            title: '查看详情',
                            titleAlign: 'center',
                            height: 250,
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
                	
                	if(title == '安委会会议' || title == '安全生产工作会议' || title == '安全生产分析会议' || title == '安全监督例会'|| title == '其他会议')
                	{
                		var html_str = "";
                		if(title == '安委会会议' || title == '安全生产工作会议' || title == '安全生产分析会议' || title == '安全监督例会'|| title == '其他会议')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+"安全会议"+"</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">会议时间</td ><td>" + record.get('Time') + "</td><td width=\"15%\" style=\"padding:5px;\">会议地点</td><td width=\"40%\">" + record.get("Place") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">会议主题</td><td>" + record.get('Topic') + "</td><td style=\"padding:5px;\">主持人</td><td>" + record.get('Host') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">记录人</td><td>" + record.get('Record') + "</td><td style=\"padding:5px;\">参加人员</td><td>" + record.get('Participants') + "</td></tr>";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
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
                	
                
                	
                	if(title == '计划制定')
                	{
                		var html_str = "";
                		if(title == '计划制定')
                		{
                			var record = dataStore.getAt(rowIndex);
            		         var Num = rowIndex+1;
            		         //alert(record.get('pName'));
            		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
            		                    "详细信息</center></h1>"+
            		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
            		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
            		                    "</td><td width=\"15%\" style=\"padding:5px;\">节假日</td><td width=\"40%\">" + record.get("holiday") + 
            		                    "</td></tr><tr><td style=\"padding:5px;\">计划名称</td><td>" + record.get('planname') + 
                                       "</td><td style=\"padding:5px;\">编制时间</td><td>" + record.get('bianzhitime')  +  "</td></tr>";
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
                	
                	
                	
                	if(title == '带班值班记录')
                	{
                		var html_str = "";
                		if(title == '带班值班记录')
                		{
                			var record = dataStore.getAt(rowIndex);
            		         var Num = rowIndex+1;
            		         //alert(record.get('pName'));
            		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
            		                    "详细信息</center></h1>"+
            		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
            		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
            		                    "</td><td width=\"15%\" style=\"padding:5px;\">节假日</td><td width=\"40%\">" + record.get("holiday") + 
            		                    "</td></tr><tr><td style=\"padding:5px;\">值班日期</td><td>" + record.get('ondutytime') + 
                                       "</td><td style=\"padding:5px;\">值班人</td><td>" + record.get('ondutyperson')   + 
            		                  "</tr><tr></td><td style=\"padding:5px;\">接班人</td><td  align=\"left\"  colspan=\"3\">" + record.get('nextperson')+  "</td></tr>";
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
                	
                	
                	
                
                	if(title == '安全工作日志')
                	{
                		var html_str = "";
                		if(title == '安全工作日志')
                		{
                			var record = dataStore.getAt(rowIndex);
            		         var Num = rowIndex+1;
            		         var datestring = record.get("rizhitime").substring(0,4)+"年"+record.get("rizhitime").substring(5,7)+"月"+record.get("rizhitime").substring(8,10)+"日";
            		         //alert(record.get('pName'));
            		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
            		                    "详细信息</center></h1>"+
            		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:left;\">"+
            		                    "<tr><td  style=\"padding:10px;\">" + datestring + "<br/>星期："+record.get('weekday')+
            		                    "</td><td style=\"padding:10px;\">" +"气象：白天："+ record.get('qixiday') +  "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;夜间："+record.get('qixinight')+
                                       "</td><td style=\"padding:10px;\">" +"气温：最高："+ record.get('degreeday') +"℃"+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;最低："+record.get('degreenight')+"℃"+
                                       "</td><td style=\"padding:10px;\">" +"风力：白天："+ record.get('windday')+  "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;夜间："+ record.get('windnight')+
                                       "</tr><tr></td><td style=\"padding:5px;\"  align=\"left\"  colspan=\"4\">" +"日志记录：<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+ record.get('rizhi')+  "</td></tr>"+
            		                  "</tr><tr></td><td style=\"padding:5px;\" align=\"left\"  colspan=\"4\">" +"工作计划/重点：<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+ record.get('workplan')+  "</td></tr>";
            		        html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
            		        
            		         var misfile = record.get('Accessory').split('*');
//            		        var foldermis;
          				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
            		         
          				    height = 800;
              		        width = 800;
            		        for(var i = 2;i<misfile.length;i++){
             		        	
             		        	scanfileName = getScanfileName(misfile[i]); 
             		        	displayfileName = misfile[i];
                        		/*if(getBLen(scanfileName)>10)
                        			displayfileName = displayfileName.substring(0,7)+"···";*/
             		        	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
             		        }
            		        
            		        
            		        
            		         html_str += "</table>";
            		         html_str+="<body style=\"padding:15px;\"><font  face=\"黑体\">&nbsp;&nbsp;&nbsp;填写说明：1、填写内容：安规、文件制度学习、安全工作记录和思考内容；2、本表由项目安全总监在每天下班前填写，项目完工后存档；3、要求填写内容完整、真实，力求详细。</font></body>"
            		         
               		    }  		
                        Ext.create('Ext.window.Window', 
                        {
                           title: '查看详情',
                           titleAlign: 'center',
                           height: 500,
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