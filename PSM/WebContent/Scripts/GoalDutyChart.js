var tn;

Ext.Loader.setConfig({ enabled: true });
Ext.Loader.setPath('Ext.ux.DataView', 'explorerview');

Ext.define('GoalDutyChartGrid', {
    requires: [
        'Ext.data.Model',
        'Ext.grid.Panel',
        'Ext.window.MessageBox',
        'Ext.tip.*',
        'Ext.form.field.ComboBox',
        'Ext.form.FieldSet',
        'Ext.tip.QuickTipManager',
        'Ext.data.*',   
        'Ext.util.*',
        'Ext.view.View',
        'Ext.ux.DataView.Animated',
        'Ext.XTemplate',
        'Ext.panel.Panel',
        'Ext.toolbar.*',
        'Ext.slider.Multi'
    ],
    ischecklabel: false,
    ischecklabelleft:false,
    checklabel:"",
    projectidid: "",
    containereast: "",
    main_panel: "",
    approvalstep_panel: "",
    researchstep_panel: "",
    acceptstep_panel: "",
    toolbarmg: "",
    canedit: '',
    grid:null,
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
        var checklabel = "";
        var grid = null;
        var ischecklabel = false;
        var ischecklabelleft = false;
        
        var approvalTextArray = new Array("立项建议书及审查意见", "工作大纲及审查意见", "合同");
        var approvalXarray = new Array("100", "380", "620");
        var researchTextArray = new Array("中间成果", "初步成果", "专家意见", "研讨会纪要", "现场实验资料", "阶段性工作情况");
        var researchXarray = new Array("50", "180", "310", "450", "590", "730");
        var acceptTextArray = new Array("图片★", "PPT★", "会议报道★", "报告", "知识产权", "验收意见");
        var acceptXarray = new Array("50", "180", "310", "450", "590", "730");
        me.canedit = config.canedit;
        var containereast = config.containereast;
        var containerwest = config.containerwest;
        var xgbm = config.xgbm;                  //相关表名
        var xgmk = config.xgmk;                  //相关模块
        var gridId = config.gridId;              //grid的Id(这里是名字)
        me.containereast = containereast;
        var projectname = config.projectname;
        var projectid = config.projectid;
        var toolbarmg = config.toolbarmg;
        me.toolbarmg = toolbarmg;
        var centerwidth = 250 - 0;
        me.projectidid = projectid;
        
        var projectNo = config.projectNo;
     	var projectName = config.projectName;
     	
     	//alert(projectName);
        
        var nowhitebigstep;
        var nowStepNum = 0;
        var nowNodeID;
        var nowName;
        var nowAccessory;
        var nowPhone;
        var nowDuty;
        
        
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
        
        var nodename_store = new Ext.data.JsonStore({
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getNodeNameList')
            },
            reader: {
                type: 'json'
            },
            fields: ['NodeName'],
            autoLoad: true
        });
        
        var name_store = new Ext.data.JsonStore({
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getNameList')
            },
            reader: {
                type: 'json'
            },
            fields: ['Name'],
            autoLoad: false
        });
        
        function drawOneLine(x0,y0,x1,y1)
        {
        	var canvas=document.getElementById('canvas' + tableID);
            var ctx = canvas.getContext('2d');    
            var xmid = (main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 + approvalstep_panel.getWidth()/2;
            var mid = (y1-y0)/2;
            ctx.beginPath();
            ctx.moveTo(x0 ,y0);
            ctx.lineTo(x0,y0+mid);
            ctx.lineTo(x1,y0+mid);
            ctx.lineTo(x1,y1);
            
//            ctx.moveTo((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 ,35);
//            ctx.lineTo((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 ,230);
            ctx.stroke();
        }
        
        function drawTwoLine(x0,y0,x1,y1)
        {
        	var canvas=document.getElementById('canvas' + tableID);
            var ctx = canvas.getContext('2d');    
            var xmid = (main_panel.getWidth() - approvalstep_panel.getWidth()) / 2.0 + approvalstep_panel.getWidth()/2.0;
            //var xmid = main_panel.getWidth()/2;
            //Ext.Msg.alert('警告',xmid);
            //Ext.Msg.alert('警告',x1+'+'+xmid);
            //x1 = x1.toFixed(0);
            var mid = (y1-y0)/3;
            ctx.beginPath();
            ctx.moveTo(x0 ,y0);
            ctx.lineTo(x0,y0+mid);
            ctx.lineTo(xmid,y0+mid);
            ctx.lineTo(xmid,y0+mid+mid);
            ctx.lineTo(x1,y0+mid+mid);
            ctx.lineTo(x1,y1);
            
//            ctx.moveTo((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 ,35);
//            ctx.lineTo((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 ,230);
            ctx.stroke();
        }
        
        function drawAllLine()
        {
        	var canvas=document.getElementById('canvas' + tableID);
            var ctx = canvas.getContext('2d');
            ctx.clearRect(0,0,2000,2000);
            var ox1 = (main_panel.getWidth() - approvalstep_panel.getWidth()) / 2;
            var oy1 = 35;
            var oy2 = 230;
            var oy3 = 425;
            var width = 115;
            var height = 50;
            var pheight = 160;
            var pwidth = 1000;
            for (var i = 0; i < flow_store.getCount(); i++) 
			{
				var record = flow_store.getAt(i);
				console.log(record.get('StepNum'));
				if(record.get('StepNum') == 1)
				{
					var x0 = ox1 + record.get('NodeX')+width/2;
		            var y0 = oy1 + record.get('NodeY')+height;
		            for (var j = i+1; j < flow_store.getCount(); j++)
		            {
		            	var record1 = flow_store.getAt(j);
		            	if(record1.get('StepNum') == 2)
		            	{
		            		var x1 = ox1+ record1.get('NodeX')+width/2;
		                    var y1 = oy2 + record1.get('NodeY');
		                    drawOneLine(x0,y0,x1,y1);
		            	}
		            }

				}
				if(record.get('StepNum') == 2)
				{
					var x0 = ox1 + record.get('NodeX')+width/2;
		            var y0 = oy2 + record.get('NodeY')+height;
		            for (var j = i+1; j < flow_store.getCount(); j++)
		            {
		            	var record1 = flow_store.getAt(j);
		            	if(record1.get('StepNum') == 3)
		            	{
		            		var x1 = ox1+ record1.get('NodeX')+width/2;
		            		x1 = parseInt(x1);
		                    var y1 = oy3 + record1.get('NodeY');
		                    drawTwoLine(x0,y0,x1,y1);
		            	}
		            }

				}
		    }
            
            
           
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
        
        //获取store中的ID 附件等信息
        var setInfoFromStore = function()
        {
        	for (var i = 0; i < flow_store.getCount(); i++) 
			{
				var record = flow_store.getAt(i);
				if(record.get('NodeName') == checklabel && record.get('StepNum') == nowStepNum)
				{
					nowNodeID = record.get('ID');
					nowName = record.get('Name');
					nowAccessory = record.get('Accessory');
					nowPhone = record.get('Phone');
					nowDuty = record.get('Duty');
					break;
				}
		    }
        }
        
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
        	setInfoFromStore();
        	var info_url = '';
        	var delete_url = '';

        		info_url = 'GoalDutyAction!getFileInfo';
        		delete_url = 'GoalDutyAction!deleteOneFile';

			var existFile = nowAccessory.split('*');
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
                    ppid:nowNodeID,
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

       			deleteAllUrl = "GoalDutyAction!deleteAllFile";

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
  	        if(action == "edit")
			{
  	        	ppid = nowNodeID;			
			}
			deleteFile(style, fileName, ppid);
			uploadPanel.store.removeAll();
  	       
       	}
       	
       	

        
        


     	
     	
     	  createSaveculture = function(){
       		Ext.Ajax.request({
   				async: false, 
                method : 'POST',
                url: 'GoalDutyAction!Saveculture?type=' + title,
                success: function(response){
                	Sim = Ext.decode(response.responseText);
                }
               });
               
    	 forms = Ext.create('Ext.form.Panel', {
				minWidth: 200,
				minHeight: 100,
				bodyPadding: 5,
				closeAction:'destroy',
				buttonAlign: 'center', 
				layout: {
		            type: 'vbox',
		            align: 'stretch'  // Child items are stretched to full width
		        }, 
		        defaults: { anchor: '100%', height: 30, labelWidth: 100 },
		        autoScroll: true,
		        frame: false,
		        defaultType:'textfield',
		        items: [{
			    	fieldLabel: "编号",		//编号框用于绑定数据ID，隐藏不显示
			        name: "ID",
			        labelAlign: 'right',
			        hidden: true,
			       	hiddenLabel: true
		        },{
		        	xtype:'textareafield',
			       	fieldLabel: "简介",
			       	name: "Content",
			       	value:Sim[0].Content,
			       	labelAlign: 'right',
			       	flex:1,
			        allowBlank:false
			    }],
			    buttons: [{
		        	text: '确定',
		        	handler: function() {
		        		if(forms.form.isValid()){
		        			forms.form.submit({
		        				clientValidation: true,
		        				url:'GoalDutyAction!UpdateSaveculture?type=' + title,
		  	                	success: function(){
		  	                		Ext.Msg.alert('信息','更新成功！');
		  	                	//	var Schoolinformation = Ext.getCmp('info');
		  	                	    getSavecultureinfo();
		  	                	    test.body.update(html_str);
		  	                	    //this.up('window').close();
		  	                	},
		  	                	failure: function(form, action){
		  	                		Ext.Msg.alert('警告','更新失败');
		  	                		//this.up('window').close();
		  	                	}
		                	})
		                	//this.up('window').destroy();
		                }
		        		else{
		        			Ext.Msg.alert('警告','请完善信息！');
		        			//this.up('window').close();
		        		}
		        		//this.up('window').close();
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
    	 });
    };
    
    
           var UpdateSaveculture = function(){
        	createSaveculture();       
        	Ext.create('Ext.window.Window', {
                title: '编辑',
                height: 700,
                width: 700,
                modal: true,  //设定为模态窗口
                plain: true,
                layout: 'fit',
                titleAlign: 'center',
                closable: true,		//可关闭的
                closeAction: 'destroy',	//关闭动作，有hide、close、destory
                draggable: true,
                resizable: true,
                maximizable: true,
                constrain: true,
                items:forms
                }).show();
        }
        
       var btnUpdateSaveculture = Ext.create('Ext.Button', {
        	width: 120,
         	height: 32,
        	icon: "Images/ims/toolbar/save.png",
        	text: '修改信息',
            type: 'button',
            ItemId:'tbtn-dlll',   
            handler:UpdateSaveculture
        })
       
                //工具栏
        var tbr = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        }); 
        
        //获取学校信息
        var getSavecultureinfo = function(){
      	  Ext.Ajax.request({
   				async: false, 
                method : 'POST',
                url: 'GoalDutyAction!Saveculture?type=' + title,
                success: function(response){
                	Sim = Ext.decode(response.responseText);
                }
               });
      	  html_str = "<h1 style=\"padding: 5px;\"><center>"+ Sim[0].Type +"</center></h1>" +
      	  			"<p>" + Sim[0].Content + "</p>"
        }
     	
  
        
         
                //tableID >=103 && tableID <=106 
      if(title == '安全理念' || title == '安全警示语' || title == '安全承诺' || title == '安全行为激励')
      {	

    	 tbr.add(btnUpdateSaveculture);
    	 getSavecultureinfo();
    	 
       test = new Ext.Panel({
         	border:false,
         	html: html_str,
            tbar: tbr
         });
    	 
      var containerType = container.getXType();    
    	 if (containerType === "tabpanel") {
             panel.add(test);
             container.add(panel).show();
         } else {
             container.add(test);
         } 
      } 
    	 
 

    
    
       	
       	
        
       	var items_node = [{
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
                	xtype: 'combo',
                    fieldLabel: '职位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    allowBlank:false,
                    anchor:'100%',
                    name: 'NodeName',
                    mode:'local',
                    triggerAction: 'all',
                    //editable:false,
                    store: nodename_store,
                    displayField:'NodeName',
                    listeners:
                    {
                        select:function(combo,record,index)
                        {  
                                var nameField = forms.getForm().findField('Name');
                                var phoneField = forms.getForm().findField('Phone');
                                //console.log(phoneField);
                                //console.log(combo.getValue());
                                nameField.clearValue();
                                phoneField.setValue('');  
                                nameField.store.load(  
                                {  
                                         params:
                                         {  
                                        	 NodeName:combo.getValue()  
                                         }  
                                });  
                            
                                 
                        }  
                    }
                },{
                	xtype:'textfield',
                    fieldLabel: '手机',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    //editable:false,
                    //readOnly:true,
                    name: 'Phone'
                
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'combo',
                    fieldLabel: '姓名',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name',
                    mode:'local',
                    queryMode:'local',
                    triggerAction: 'all',
                    //editable:false,
                    store: name_store,
                    displayField:'Name',
                    listeners:
                    {
                        select:function(combo,record,index)
                        {  
                            try
                            {  
                                var nodenameField = forms.getForm().findField('NodeName');
                                var phoneField = forms.getForm().findField('Phone');
                               
                                Ext.Ajax.request({
                       				async: false, 
                                    method : 'POST',
                                    url: 'GoalDutyAction!getPhone',
                                    params:{
                                    	NodeName:nodenameField.getValue(),
                                    	Name:combo.getValue()  
                                    },
                                    success: function(response){
                                    	forms.getForm().findField('Phone').setValue(response.responseText);
                                    }
                                   });
                            }
                            catch(ex)
                            {  
                              //alert("数据加载失败！");  
                            }      
                        }  
                    }
	            },{
                	xtype:'textfield',
                    fieldLabel: '所属项目',
                    //labelWidth: 120,
                    readOnly:true,
                    labelAlign: 'right',
                    anchor:'100%',
                    value:projectName,
                    name: 'ProjectName'
                }]
	        }]
	    },{
            xtype:'textarea',
            fieldLabel: '职责',
            layout: 'anchor',
//            labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Duty'
	    },
        	uploadPanel
        ]
       	
       	var editH = function() 
        {
        	var formItems;
        	var itemURL; 
        	var actionURL;
        	var uploadURL;
        	var items;
        	
        	setInfoFromStore();
        	insertFileToList();	
        	if(tableID >=97 &&  tableID<=100 ||  tableID == 102 || tableID == 56)
        	{
        		uploadURL = "UploadAction!execute";
        		actionURL = 'GoalDutyAction!editNode?userName=' + user.name + "&userRole=" + user.role;  
        		items = items_node;                  		
            }  
        	createForm({
        		autoScroll: true,
            	action: 'edit',
        	    bodyPadding: 5,
        	    url: actionURL,
        	    items: items
        	    });
        	uploadPanel.upload_url = uploadURL;
        	showWin({ winId: 'edit', title: '修改信息', items: [forms]});
        }
       	
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
                	handler: function() 
                	{
                		if(forms.form.isValid())
                		{
                			switch (config.action)
                			{		
                				case "edit": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName +"&tableid="+ tableID;
                					uploadPanel.store.removeAll();
                					break;	
                				}
                				case "add":
                				{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName +"&tableid="+ tableID + "&stepNum="+ nowStepNum +"&lastNodeName="+ checklabel;
                					uploadPanel.store.removeAll();
                					break;	
                				}
                				default:
                					break;
                			}
                			var mynodename = forms.getForm().findField('NodeName').getValue();
                			if ((config.action == 'add' && CanAddNewNode(flow_store, mynodename, nowStepNum)) || (config.action == 'edit' && CanEditNewNode(flow_store, mynodename, nowStepNum)))
                			{
                				forms.form.submit({
                    				clientValidation: true,
              	                	url: encodeURI(config.url),
              	                	success: function(form, action){
              	                		flow_store.load();
                                        DrawflowOnapprovalstep_panel(approvalstep_panel, researchstep_panel, acceptstep_panel, 6, acceptTextArray, acceptXarray, detailform, flow_store);
//                                        ShownodeMessageOnEastpanel(checklabel);
                                        detailform.hide();
            	                        ShownodeMessageOnEastpanel("未选择节点");
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
                			else
                			{
                				Ext.Msg.alert('提示',"职位命名重复");
                			}
                			 	
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
                		if (config.action == "edit") 
                        {
                            //forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                			forms.getForm().findField('ID').setValue(nowNodeID);
                			forms.getForm().findField('NodeName').setValue(checklabel);
                			forms.getForm().findField('Name').setValue(nowName);
                			forms.getForm().findField('Phone').setValue(nowPhone);
                			forms.getForm().findField('Duty').setValue(nowDuty);
                        }
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
        	var nameField = forms.getForm().findField('Name');
            nameField.store.load(  
            {  
                     params:
                     {  
                    	 NodeName:''  
                     }  
            });
        	
            if (config.action == 'edit') 
            {
            	
            	var nameField = forms.getForm().findField('Name');
                nameField.store.load(  
                {  
                         params:
                         {  
                        	 NodeName:checklabel  
                         }  
                });
            	
            	forms.getForm().findField('ID').setValue(nowNodeID);
    			forms.getForm().findField('NodeName').setValue(checklabel);
    			forms.getForm().findField('Name').setValue(nowName); 
    			forms.getForm().findField('Phone').setValue(nowPhone);
    			forms.getForm().findField('Duty').setValue(nowDuty);
                //forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
        };       
       	
        var showWin = function (config) {
        	
        	if(tableID >=97 &&  tableID<=100 ||  tableID == 102 || tableID == 56)
        	{//编辑提案信息框
        		width = 780;
        		height = 530;
        	} else {
        		width = 780;
        		height = 530;
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
        
        var replaceProjectNamehChar = function(projectName)
        {
        	var str = projectName.replace("#", "@");
        	//alert(str);
        	return str;
        };
        		
		var flow_store = Ext.create('Ext.data.Store', {
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url:encodeURI('GoalDutyAction!getFlowNodeList?tableid='+tableID+'&projectName='+replaceProjectNamehChar(projectName)),
                reader: {
                    type: 'json'
                }
            },
            fields: [
        	         { name: 'ID'},
                     { name: 'TableID'},
                     { name: 'StepNum',type: 'int'},
                     { name: 'OrderNum',type: 'int'},
                     { name: 'NodeName'},
                     { name: 'NodeX',type: 'int'},
                     { name: 'NodeY',type: 'int'},
                     { name: 'Name'},
                     { name: 'Phone'},
                     { name: 'Duty'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ]
        });

   	
		var RequestStepmessage = function (projectid, node, formpanel) 
		{
			var nodename = "   " ;
		    var name = "   ";
		    var phone = "   ";
		    var projectname = "   ";
		    var duty = " "
			for (var i = 0; i < flow_store.getCount(); i++) 
			{
				var record = flow_store.getAt(i);
				if(record.get('NodeName') == node)
				{
					nodename = node;
					name = record.get('Name');
					phone = record.get('Phone');
					projectname = record.get('ProjectName');
					duty = record.get('Duty');
				}
		    }		    
		    var html = '<h3>职位：</h3><li class = li_li>' + nodename + '</li>';
		    html = html + '<h3>姓名：</h3><li class = li_li>' + name + '</li>'
		    html = html + '<h3>手机：</h3><li class = li_li>' + phone + '</li>'
		    html = html + '<h3>所属项目：</h3><li class = li_li>' + projectname + '</li>'
		    html = html + '<h3>职责：</h3><li class = li_li>' + duty + '</li>'
		    //html = html + '<h3>负责单位：</h3><li class = li_li>' + projectcompany + '</li>'
		   // html = html + '<h3>合同资金：</h3><li class = li_li>' + projectmoney + '</li>'
		    //html = html + '<h3>项目人员：</h3><li class = li_li>' + projectpeople + '</li>'
		    //html = html + '<h3>项目简介：</h3><li class = li_li>' + projectdescribe + '</li>'
		    var label = Ext.create('Ext.form.Label', {
		        margin: '0 0 0 10',
		        html: html
		    });
		    formpanel.removeAll();
		    formpanel.add(label);    
		}
		
		var ShownodeMessageOnEastpanel = function (clicknode) {
	        var me = this;
	        
	        var btnUpload = Ext.create('Ext.Button', {
	        	width: 55,
	        	//height: 32,
	        	text: '编辑',
	        	disabled:true,
	           	icon: "Images/ims/toolbar/edit.png",
	            handler: editH
	        });
	        
	        if(clicknode == '未选择节点')
	        {
	        	btnUpload.disable();
	        }
	        else
	        {
	        	btnUpload.enable();
	        }
	        
	        containereast.removeAll();
	        containereast.setTitle('简介');
	        var paneltest = Ext.create('Ext.panel.Panel', {
	            width: 250,
	           // height: 500,
	            //title: clicknode,
	            //titleAlign: 'center',
	            layout: {
	                type: 'vbox',
	                align:'stretch'
	            }
	        });
	        
	        var nodemessage_formpanel = Ext.create('Ext.form.Panel', {
	            height: 250,
	            layout: {
	                type: 'fit'
	            },
	            style: 'text-align:left',
	            items: []
	        });
	        RequestStepmessage('', clicknode, nodemessage_formpanel);
	        paneltest.add(nodemessage_formpanel);
	        function renderTopic(value, p, record) {
	        	
	        	var fold_array = record.data.FoldName.split('*');
	            return Ext.String.format(
	                '<a href="upload/{2}/{3}/{1}" target="_blank">{0}</a>',             
	                value,
	                encodeURIComponent(record.data.FileName),
	                encodeURIComponent(fold_array[0]),
	                encodeURIComponent(fold_array[1])
	            );
	        }
	        var grid = Ext.create('Ext.grid.Panel', {
	            //width: 250,
	            //height: 500,
	            //collapsible: true,
	            //title: '文件列表',
	            autoScroll : true,
	            tbar: [{
	                xtype: 'label',
	                
	                html: '<h4 align="center">' + clicknode + '</h4>'
	            },'->','-',btnUpload
	           ],
	            titleAlign:'center',
	            store: {
	                xtype: 'json',
	                proxy: {
	                    type: 'ajax',
	                    url:encodeURI('GoalDutyAction!getFileNameList?clicknode='+clicknode+'&tableid='+tableID+'&stepNum='+nowStepNum+'&projectName='+replaceProjectNamehChar(projectName)),
	                    //url: 'Ajax/connectDB/Getuploadfilename.ashx?&Project_id=' + me.projectidid + '&Project_step=' + clicknode + '&XGMK=KJCX_YJXM',
	                    reader: {
	                        type: 'json'
	                    }
	                    
	                },
	                fields: ['FoldName', 'FileName'],
	                autoLoad:true
	            },
	            loadMask: true,
	            selModel: {
	                pruneRemoved: false
	            },
	            multiSelect: true,
	            viewConfig: {
	                trackOver: false
	            },
	            features: [{
	                ftype: 'grouping',
	                hideGroupedHeader: false
	            }],
	        // grid columns
	            columns: [{
	                xtype: 'rownumberer',
	                width: 30,
	                sortable: false
	            }, {
	                tdCls: 'x-grid-cell-topic',
	                text: "文件名",
	                dataIndex: 'FileName',
	                flex: 1,
	                renderer: renderTopic,
	                sortable: true
	            }
	            ],
	            listeners: {
	                'itemdblclick': function (thisgrid, record, item, index, e, et) {
	                    var split_array = record.data.FileName.split(".");
	                    var fold_array = record.data.FoldName.split('*');
	                    var foldName = fold_array[0] + '/' +fold_array[1];
	                    var previewfilename;
	                    //var preview_filetype = new Array("doc", "docx", "txt", "jpg", "png", "pdf");
	                    var preview_filetype = new Array("doc", "docx");
	                    switch (split_array[1]) {
	                        case "doc":
	                        case "docx":
	                        case "xls":
	                            previewfilename = split_array[0] + '.pdf';
	                            break;
	                        default:
	                            previewfilename = record.data.FileName;
	                    }
	                    var saveUrl = 'upload/' + foldName+'/' + previewfilename;
	                    //window.open(saveUrl, "文档预览", "height=700,width=1200");
	                    window.open(saveUrl);
	                }
	            }
	        });
	        //me.grid = grid;
	        paneltest.add(grid);
	        containereast.add(paneltest).show();        
	    }
		
		var DrawflowOnapprovalstep_panel = function (approvalstep_panel, researchstep_panel, basepanel, num, approvalTextArray, approvalXarray, detailform, flow_store) 
		{
	        approvalstep_panel.removeAll();
	        researchstep_panel.removeAll();
	        basepanel.removeAll();     
	        //var aTextArray = new Array("项目(副)经理","设计经理", "物资经理", "施工经理", "调试经理","设计分包项目经理","土建分包项目经理","安装分包项目经理","调试分包项目经理");
	        //var aXarray = new Array(380,80,280,480,680,80,280,480,680);
	        var index1 = 1;
	        var index2 = 5;
	        var width = 115;
	        flow_store.load({
	            callback: function (records, options, success) 
	            {
	                for (var i = 0; i < records.length; i++) 
	                {
	                	var nodename1 =  records[i].data.NodeName;
	                	var name1 = records[i].data.Name;
	                	var showname = nodename1+"<br>"+"<B>("+name1+")</B>";
	                	
	                	var test_panel = Ext.create("Ext.form.Panel", {
	      	  	          x: records[i].data.NodeX,
	      	  	          y: records[i].data.NodeY,
	      	  	          width: width,
	      	  	          height: 50,
	      	  	          //frame: true,
	      	  	          style: 'text-align:center',
	      	  	          bodyStyle: {
	      	  	              background: '#E8E8E8'
	      	  	          },
	      	  	          layout: 'border',
	      	  	          items: [{
	      	  	              xtype: 'label',
	      	  	              frame: true,
	      	  	              html: showname,
	      	  	              region: 'center',
	      	  	              margin: '4 0 0 0',
	      	  	              //y:5,
	      	  	              //html: '<font color="' + records[i].data.backgroundpage + '">' + records[i].data.nodename + '</font>',
	      	  	             // style: 'text-align:center',
	      	  	              listeners: {
	      	  	                  contextmenu: {
	      	  	                      element: 'el', //bind to the underlying el property on the panel
	      	  	                      fn: function (e, t) {
	      	  	                    	  
	      	  	                    	  var clicknodename0 = Ext.get(t.id).dom.innerHTML;
	      	  	                          var clicknodename = clicknodename0.substring(0,clicknodename0.indexOf("<br>"));
	      	  	                          checklabel = clicknodename;
	      	  	                    	  
	      	  	                          ischecklabel = true;
	      	  	                      }
	      	  	                  },
	      	  	                  click: {
	      	  	                      element: 'el', //bind to the underlying el property on the panel          
	      	  	                      fn: function (e, t, eOpts){
	      	  	                    	        	  	                    	  
	      	  	                    	  var clicknodename0 = Ext.get(t.id).dom.innerHTML;
	      	  	                          var clicknodename = clicknodename0.substring(0,clicknodename0.indexOf("<br>"));
	      	  	                          checklabel = clicknodename;
	      	  	                          ShownodeMessageOnEastpanel(checklabel);
	      	  	                          ischecklabelleft = true;
	      	  	                      }
	      	  	                  }
	      	  	              }
	      	  	          }]
	      	  	      });
	      	          if(records[i].data.StepNum == 1)
	      	          {
	      	        	  approvalstep_panel.add(test_panel);
	      	          }
	      	          else if(records[i].data.StepNum == 2)
	      	          {
	      	        	  researchstep_panel.add(test_panel);
	      	          }
	      	          else
	      	          {
	      	        	  basepanel.add(test_panel);
	      	          }                
	                }
	                
	                drawAllLine();
	            }
	        });
	        
//	        flow_store.load({
//	            callback: function (records, options, success) {
//	                for (var i = 0; i < records.length; i++) {
//	                    var width = records[i].data.nodename.length * 15;                    
//	                    if (records[i].data.nodename.length < 6) {
//	                        width = 90;
//	                    }
//	                    var xxx = records[i].data.nodeY;
//	                    var yyy = 40;
//	                    if (xxx > 4) {
//	                        xxx = xxx - 4;
//	                        yyy = 140;
//	                    }
//	                    var test_panel = Ext.create("Ext.form.Panel", {
//	                        x: xxx* 200 - 150,
//	                        y: yyy,
//	                        width: width,
//	                        height: 30,
//	                        //frame: true,
//	                        style: 'text-align:center',
//	                        bodyStyle: records[i].data.backgroundpage,
//	                        layout: 'border',
//	                        items: [{
//	                            xtype: 'label',
//	                            frame: true,
//	                            text: records[i].data.nodename,
//	                            region: 'center',
//	                            margin: '4 0 0 0',
//	                            //y:5,
//	                            //html: '<font color="' + records[i].data.backgroundpage + '">' + records[i].data.nodename + '</font>',
//	                           // style: 'text-align:center',
//	                            listeners: {
//	                                contextmenu: {
//	                                    element: 'el', //bind to the underlying el property on the panel
//	                                    fn: function (e, t) {
//	                                        var hh = Ext.get(t.id).dom.innerHTML
//	                                        me.checklabel = hh;
//	                                        me.ischecklabel = true;
//	                                    }                                    
//	                                },
//	                                click: {
//	                                    element: 'el', //bind to the underlying el property on the panel          
//	                                    fn: function (e, t, eOpts){
//	                                        var clicknodename = Ext.get(t.id).dom.innerHTML
//	                                        me.ShownodeMessageOnEastpanel(clicknodename);
//	                                        me.ischecklabelleft = true;
//	                                    }
//	                                }
//	                            }
//	                        }]
//	                    });
//	                    switch (records[i].data.stepname) {
//	                        case "立项阶段":
//	                            approvalstep_panel.add(test_panel);
//	                            //approvalstep_panel.add(endtime_label);
//	                            //approvalstep_panel.add(file_label);
//	                           // approvalstep_panel.add(describe_label);
//	                            break;
//	                        case "研究阶段":
//	                            researchstep_panel.add(test_panel);
//	                            break;
//	                        case "验收阶段":
//	                            basepanel.add(test_panel);
//	                            break;
//	                    }
	//
//	                }
//	            }
//	        });              
	    }
			
		var StepClickHandler = function (e, t, detailform) {
            detailform.showAt(e.getX(), e.getY());
			if (ischecklabel) {
	            ischecklabel = false;
	        } else {
	        	checklabel = "未选择节点";
	        }
	    }
		
		function CanAddNewNode(store, newName, nowStep)
		{
			var can = true;
//			for (var i = 0; i < store.getCount(); i++) 
//			{
//				var record = store.getAt(i);
//				if(record.get('StepNum') == nowStep && record.get('NodeName') == newName)
//				{
//					can = false;
//					break;
//				}
//		    }
			return can;
		}
		
		function CanEditNewNode(store, newName, nowStep)
		{
			var can = true;
//			for (var i = 0; i < store.getCount(); i++) 
//			{
//				var record = store.getAt(i);
//				if(record.get('StepNum') == nowStep && record.get('NodeName') == newName && record.get('NodeName') != checklabel)
//				{
//					can = false;
//					break;
//				}
//		    }
			return can;
		}
		
		function haveUpNode(store, newName, nowStep)
		{
			if(nowStep == 1)
			{
				return true;
			}
			else
			{
				for (var i = 0; i < store.getCount(); i++) 
				{
					var record = store.getAt(i);
					if(record.get('StepNum') == nowStep-1)
					{
						return true;
					}
			    }
			}
			return false;
		}
		
		function CanDeleteNode(store, nodename, nowStep)
		{
			var can = true;
			var havenext = false;
			var nodecount = 0;
			for (var i = 0; i < store.getCount(); i++) 
			{
				var record = store.getAt(i);
				if(record.get('StepNum') == nowStep)
				{
					nodecount = nodecount+1;
				}
				if(record.get('StepNum') == nowStep+1)
				{
					havenext = true;
				}
		    }
			if(havenext == true && nodecount == 1)
			{
				can = false;
			}
			return can;
		}
		
		var main_panel = Ext.create("Ext.panel.Panel", {
	            layout: 'absolute',
	            region: 'center',
	            html : ['<canvas id="canvas' + tableID + '" width="2000" height="2000"></canvas>'],
	            listeners: {
//                    'beforedestroy': function (p , e) {
//                		detailform.hide();
//                		
//                    },
                    'resize': function (panel, width, height, oldWidth, oldHeight, eOpts) {
                        approvalstep_panel.setX((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 + main_panel.getX(), true);
                        researchstep_panel.setX((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 + main_panel.getX(), true);
                        acceptstep_panel.setX((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 + main_panel.getX(), true);
                        drawAllLine();
                    }
                }
	            
	    });
		
		var approvalstep_panel = Ext.create("Ext.form.Panel", {
			bodyStyle: 'background-color : transparent;',
			border:false,
			name:'1panel',
            layout: 'absolute',
            width: 1000,
            height: 160,
            x: 15,
            y: 35,
            //frame:true,
//            lbar: [
//                    {
//                        xtype: 'label',
//                        width: 40,
//                        style: 'text-align:center',
//                        html: '<br/><br/><font size="4"><strong>第</strong></font><br/><font size="4"><strong>一</strong></font><br/><font size="4"><strong>级</strong></font><br/>'
//                    }
//            ],
            listeners: {
                contextmenu: {
                    element: 'el', //bind to the underlying el property on the panel
                    fn: function (e, t) {
                    	nowStepNum = 1;
                    	//StepClickHandler(e, t, projectid, detailform);
//                        nowhitebigstep = '立项阶段';
                        detailform.getComponent("addNode").setText("添加(一级)");
                        detailform.getComponent("deleteNode").setText("删除(一级)");
                        detailform.getComponent("editNode").setText("编辑(一级)");
                        if(!ischecklabel)
                        {
                        	detailform.getComponent("deleteNode").disable();
                            detailform.getComponent("editNode").disable();
                        }
                        else
                        {
                        	detailform.getComponent("deleteNode").enable();
                            detailform.getComponent("editNode").enable();
                        }
                        StepClickHandler(e, t, detailform); 
                    }
                },
                click: {
                    element: 'el', //bind to the underlying el property on the panel          
                    fn: function (e, t, eOpts) {
                    	nowStepNum = 1;
                    	detailform.hide();
                    }
                }
            }
        });       
		
		var researchstep_panel = Ext.create("Ext.panel.Panel", {
			bodyStyle: 'background-color : transparent;',
			border:false,
			name:'2panel',
            layout: 'absolute',
            width: 1000,
            height: 160,
            x: 15,
            y: 230,
//            lbar: [
//                    {
//                        xtype: 'label',
//                        width: 40,
//                        style: 'text-align:center',
//                        html: '<br/><br/><font size="4"><strong>第</strong></font><br/><font size="4"><strong>二</strong></font><br/><font size="4"><strong>级</strong></font><br/>'
//                    }
//            ],
            listeners: {
                contextmenu: {
                    element: 'el', //bind to the underlying el property on the panel
                    fn: function (e, t) {
                    	nowStepNum = 2;
//                        nowhitebigstep = '研究阶段';
                    	detailform.getComponent("addNode").setText("添加(二级)");
                    	detailform.getComponent("deleteNode").setText("删除(二级)");
                        detailform.getComponent("editNode").setText("编辑(二级)");
                        if(!ischecklabel)
                        {
                        	detailform.getComponent("deleteNode").disable();
                            detailform.getComponent("editNode").disable();
                        }
                        else
                        {
                        	detailform.getComponent("deleteNode").enable();
                            detailform.getComponent("editNode").enable();
                        }
                        StepClickHandler(e, t, detailform);
                        
                    }
                },
                click: {
                    element: 'el', //bind to the underlying el property on the panel          
                    fn: function (e, t, eOpts) {
                    	nowStepNum = 2;
                    	detailform.hide();
                    }
                }
            }
        });
		
		var acceptstep_panel = Ext.create("Ext.panel.Panel", {
			bodyStyle: 'background-color : transparent;',
			border:false,
			name:'3panel',
            layout: 'absolute',
            width: 1000,
            height: 160,
            x: 15,
            y: 425,
//            lbar: [
//                    {
//                        xtype: 'label',
//                        width: 40,
//                        style: 'text-align:center',
//                        html: '<br/><br/><font size="4"><strong>第</strong></font><br/><font size="4"><strong>三</strong></font><br/><font size="4"><strong>级</strong></font><br/>'
//                    }
//            ],
            listeners: {
                contextmenu: {
                    element: 'el', //bind to the underlying el property on the panel
                    fn: function (e, t) {
                    	nowStepNum = 3;
//                        nowhitebigstep = '验收阶段';
                    	detailform.getComponent("addNode").setText("添加(三级)");
                    	detailform.getComponent("deleteNode").setText("删除(三级)");
                        detailform.getComponent("editNode").setText("编辑(三级)");
                        if(!ischecklabel)
                        {
                        	detailform.getComponent("deleteNode").disable();
                            detailform.getComponent("editNode").disable();
                        }
                        else
                        {
                        	detailform.getComponent("deleteNode").enable();
                            detailform.getComponent("editNode").enable();
                        }
                        StepClickHandler(e, t, detailform);                        
                    }
                },
                click: {
                    element: 'el', //bind to the underlying el property on the panel          
                    fn: function (e, t, eOpts) {
                    	nowStepNum = 3;
                    	detailform.hide();
                    }
                }
            }
        });
		
		function addNewNode()
		{
			if(haveUpNode(flow_store, "", nowStepNum))
			{ 
	        	var actionURL;
	        	var uploadURL;
	        	var items;
	        	
	        	if(tableID >=97 &&  tableID<=100 ||  tableID == 102 || tableID == 56)
	        	{
	        		uploadURL = "UploadAction!execute";
	        		actionURL = 'GoalDutyAction!addNode?userName=' + user.name + "&userRole=" + user.role;  
	        		items = items_node;                  		
	            }  
	        	createForm({
	        		autoScroll: true,
	            	action: 'add',
	        	    bodyPadding: 5,
	        	    url: actionURL,
	        	    items: items
	        	    });
	        	uploadPanel.upload_url = uploadURL;
	        	showWin({ winId: 'add', title: '新增信息', items: [forms]});
	        	
			}
			else
         	{
         		Ext.Msg.alert('提示',"请先添加上一级节点");
         	}
     	   
		}
		
		
		var detailform = new Ext.menu.Menu({
            //id: "" + projectid + "Ext.menu.Menu",
            height: 100,
            width: 100,
            floating: true,
            hidden: false,
            bodyStyle: {
                background: '#ffc'
            },
            items: [
//                {
//                    text: "上传文件",
//                    icon: 'Images/ims/toolbar/process.png',
//                    handler: function () {                      
//                        //me.uploadH(projectid, me.checklabel);
//                    }
//                },'-',
                {
                	itemId: 'addNode',
                    text: '添加',
//                    height: 30,
//                    width: 150,
                    icon: 'Images/ims/toolbar/add.gif',
                    handler: function () 
                    {
                    	
                    	addNewNode();
                    }
//                    menu: new Ext.menu.Menu
//                    ({
//                        ignoreParentClicks: true,
//                        bodyStyle:
//                        {
//                            background: '#ffc',
//                        },
//                        items: [
//                         {
//                        	itemId: 'newNodeName',
//                            //id: 'getstepdetailnew' + tableID,
//                            height: 30,
//                            width: 150,
//                            xtype: 'textareafield',
//                            border: false,
//                            emptyText:'节点名称',
//                            value: '',
//                            allowOnlyWhitespace: false,
//                            style: {
//                                background: '#ffc',
//                            },
//                            labelWidth: 30,
//                            labelStyle: 'normal small-caps bold 12px/1.2em "Times New Roman",Georgia,Serif',
//                        },
//                        '-',
//                       {
//                           text: '确定',
//                           height: 30,
//                           width: 150,
//                           handler: function () {
//                        	   var newNodeName = detailform.getComponent("addNode").menu.getComponent("newNodeName").value;
//                        	   //console.log(newNodeName);
//                        	   if(haveUpNode(flow_store, newNodeName, nowStepNum))
//                        	   {
//                        		   if (CanAddNewNode(flow_store, newNodeName, nowStepNum)) 
//                                   {
//                                	   //console.log('stepnum'+nowStepNum);
//       	  	                           //console.log('nodename'+checklabel);
//       	  	                           //DrawflowOnapprovalstep_panel(approvalstep_panel, researchstep_panel, acceptstep_panel, 6, acceptTextArray, acceptXarray, detailform, flow_store);
//       	  	                           //detailform.hide();
//                                       Ext.Ajax.request({
//                                           url: 'GoalDutyAction!addNode',
//                                           params:
//                                           {
//                                        	   tableid: tableID,
//                                        	   stepNum: nowStepNum,
//                                        	   lastNodeName: checklabel,
//                                        	   nowNodeName: newNodeName,
//                                        	   projectName:projectName
//                                           },
//                                           success: function (response, opts) 
//                                           {
//                                               flow_store.load();
//                                               DrawflowOnapprovalstep_panel(approvalstep_panel, researchstep_panel, acceptstep_panel, 6, acceptTextArray, acceptXarray, detailform, flow_store);
//                                               detailform.getComponent("addNode").menu.getComponent("newNodeName").setValue('');
//                                               detailform.hide();
//                                               ShownodeMessageOnEastpanel("未选择节点");
//                                           }
//                                       });
//                                       
//                                   } else {
////                                       alert("命名重复");
//                                       Ext.Msg.alert('提示',"命名重复");
//                                   }
//                        	   }
//                        	   else
//                        	   {
//                        		   Ext.Msg.alert('提示',"请先添加上一级节点");
//                        	   }
//                               
//                              
//                           }
//
//                       }]
//                    })
//                   
                },
                '-',
                {
                	itemId: 'deleteNode',
                    text: "删除",
                    icon: 'Images/ims/toolbar/delete.gif',
                    handler: function () {
                    	detailform.hide();
                    	if(CanDeleteNode(flow_store, checklabel, nowStepNum))
                    	{
                    		Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) 
                                    {
                                		if (buttonID === 'yes')
                                		{
                                			if (checklabel == '未选择节点') {
//                                				alert('未选择节点');
                                				Ext.Msg.alert('提示',"未选择节点");
                                				
                                				return;
                                			}
                                			Ext.Ajax.request({
                                                url: 'GoalDutyAction!deleteNode',
                                                params:
                                                {
                                                	tableid: tableID,
                                             	    stepNum: nowStepNum,
                                             	    nodeName: checklabel,
                                             	    projectName:projectName
                                                },
                                                success: function (response, opts) {
                                                	flow_store.load();
                                                    DrawflowOnapprovalstep_panel(approvalstep_panel, researchstep_panel, acceptstep_panel, 6, acceptTextArray, acceptXarray, detailform, flow_store);
                                                    ShownodeMessageOnEastpanel("未选择节点");
                                                }
                                            });		
                                		}
                                    })
                    	}
                    	else
                    	{
                    		//alert('请先删除下级节点！');
                    		Ext.Msg.alert('提示','请先删除下级节点！');
                    	}
                    	
                    }
                },
                '-',
                {
                	itemId: 'editNode',
                    text: "编辑",
                    icon: "Images/ims/toolbar/edit.png",
                    handler: function () 
                    {
                    	
                    	editH();
                    }
                }
            ]

        });
        
        var eastpanel = Ext.create("Ext.panel.Panel", {
            region: 'east',
            collapsible: false,
            collapsed: false,
            width: 250,
            titleAlign: 'center'
        });
        var big_panel = Ext.create("Ext.panel.Panel", {
            layout: 'border',
            itemId: tableID,
           // itemId:"gridId",
            title: title,
            //closable: true,
            items: [main_panel, eastpanel],
            closable: true,
            //closeAction: 'destroy',	//关闭动作，有hide、close、destroy
            autoScroll: true,
            listeners: 
            {
            afterrender: function (node, optd) 
            {
            	detailform.hide();
            	container.items.each(function (item) {
                //if (item.closable && item.itemId != gridId) {
                //    container.remove(item);
                //}
            });
           // container.add(node).show();
           // alert("dasd");
        }
        }
        });
		
		if(tableID >=97 &&  tableID<=100 ||  tableID == 102 || tableID == 56)
		{
		DrawflowOnapprovalstep_panel(approvalstep_panel, researchstep_panel, acceptstep_panel, 6, acceptTextArray, acceptXarray, detailform, flow_store);
		main_panel.add(approvalstep_panel);
        main_panel.add(researchstep_panel);
        main_panel.add(acceptstep_panel);
        containereast = eastpanel;
        container.add(big_panel).show();
        
        ShownodeMessageOnEastpanel("未选择节点");
        approvalstep_panel.setX((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 + main_panel.getX(), true);
        researchstep_panel.setX((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 + main_panel.getX(), true);
        acceptstep_panel.setX((main_panel.getWidth() - approvalstep_panel.getWidth()) / 2 + main_panel.getX(), true);
        drawAllLine();
    }}
        

   
});