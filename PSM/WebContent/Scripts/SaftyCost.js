var tn;

Ext.define('SaftyCostGrid', {
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
        	if(title == '项目部安全生产投入计划'||title == '项目部安全费用使用台账' || title == '项目部安全费用使用检查' || title == '分包方安全生产投入计划'|| title == '分包方安全费用使用台账'||title=='项目部安全费用统计')
        	{
        		info_url = 'SaftyCostAction!getFileInfo';
        		delete_url = 'SaftyCostAction!deleteOneFile';
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
       		if(title == '项目部安全生产投入计划'||title == '项目部安全费用使用台账' || title == '项目部安全费用使用检查'|| title == '分包方安全生产投入计划'|| title == '分包方安全费用使用台账'||title =='项目部安全费用统计')
       		{
       			deleteAllUrl = "SaftyCostAction!deleteAllFile";
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
  	        if(action == "addProject"||action == "editProject" || action == "addfb" || action == "editfb"  )
  	        {
				if(action == "editProject" ||  action == "editfb" )
				{               			
					ppid = selRecs[0].data.ID;			
				}		
				deleteFile(style, fileName, ppid);
				uploadPanel.store.removeAll();
  	        }
       	}
        
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
		async: false, 
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
		bbar.moveFirst();	//状态栏回到第一页
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
        	
        	if(title == '项目部安全生产投入计划')
        	{	        	
        		actionURL = 'SaftyCostAction!addSaftycostplan';  
        		uploadURL = "UploadAction!execute";
        		items = items_saftycostplan;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addSaftycostplan',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addSaftycostplan', title: '新增项目', items: [forms]});
        	}        	
        	if(title=='安全费用统计分析')
        	{
        		actionURL = 'SaftyCostAction!addSaftycosttj';  
        		uploadURL = "UploadAction!execute";
        		items = items_saftycosttj;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addSaftycosttj',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addSaftycosttj', title: '新增项目', items: [forms]});
        	}
        	
        	//*****************************
        	if(title=='项目部安全费用使用台账')
        	{
        		actionURL = 'SaftyCostAction!addSaftyaccounts';  
        		uploadURL = "UploadAction!execute";
        		items = items_saftyaccounts;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addSaftyaccounts',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addSaftyaccounts', title: '新增项目', items: [forms]});
        	}
        	//*****************************addSaftyaccountscheck
        	if(title == '项目部安全费用使用检查')
        	{
        		actionURL = 'SaftyCostAction!addSaftyjiancha';  
        		uploadURL = "UploadAction!execute";
        		items = items_saftyjiancha;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addSaftyjiancha',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addSaftyjiancha', title: '新增项目', items: [forms]});
        	}
        	
        	//***************************addFenbaosaftyaccounts
        	if(title == '分包方安全生产投入计划')
        	{
        		actionURL = 'SaftyCostAction!addFenbaoplan';  
        		uploadURL = "UploadAction!execute";
        		items = items_fenbaoplan;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addFenbaoplan',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addFenbaoplan', title: '新增项目', items: [forms]});
        	}
        	
        	//***************************addFenbaosaftyaccounts
        	if(title == '分包方安全费用使用台账')
        	{
        		actionURL = 'SaftyCostAction!addFenbaosaftyaccounts';  
        		uploadURL = "UploadAction!execute";
        		items = items_fenbaosaftyaccounts;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addFenbaosaftyaccounts',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addFenbaosaftyaccounts', title: '新增项目', items: [forms]});
        	}
        	//*************************items_fenbaosaftyaccountscheck
        	if(title == '分包方安全费用使用检查')
        	{
        		actionURL = 'SaftyCostAction!addFenbaosaftyjiancha';  
        		uploadURL = "UploadAction!execute";
        		items = items_fenbaosaftyjiancha;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addFenbaosaftyjiancha',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addFenbaosaftyjiancha', title: '新增项目', items: [forms]});
        	}
        	//***********************items_fenbaosaftycostsum
        	if(title == '分包方安全费用统计')
        	{
        		actionURL = 'SaftyCostAction!addFenbaosaftycostsum';  
        		uploadURL = "UploadAction!execute";
        		items = items_fenbaosaftycostsum;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addFenbaosaftycostsum',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addFenbaosaftycostsum', title: '新增项目', items: [forms]});
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
        				
        			if(title == '项目部安全生产投入计划')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCostAction!editSaftycostplan?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_saftycostplan;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftycostplan',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftycostplan', title: '修改项目', items: [forms]});
        			}
        			
        			//*******有上传附件
        			if(title == '项目部安全费用使用台账')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCostAction!editSaftyaccounts?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_saftyaccounts;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftyaccounts',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftyaccounts', title: '修改', items: [forms]});
        			}
        			
        			//*****有上传附件
        			if(title == '项目部安全费用使用检查')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCostAction!editSaftyjiancha?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_saftyjiancha;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftyjiancha',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftyjiancha', title: '修改', items: [forms]});
        			}
        			
        			//************************************
        			
        			if(title == '分包方安全生产投入计划')
        			{
        				insertFileToList();
        				actionURL = 'SaftyCostAction!editFenbaoplan?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_fenbaoplan;
              
        			createForm({
            			autoScroll: true,
            			action: 'editFenbaoplan',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editFenbaoplan', title: '修改', items: [forms]});
        			}
        			
        			if(title == '分包方安全费用使用台账')
        			{
//        				insertFileToList();
        				actionURL = 'SaftyCostAction!editFenbaosaftyaccounts?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_fenbaosaftyaccounts;
              
        			createForm({
            			autoScroll: true,
            			action: 'editFenbaosaftyaccounts',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editFenbaosaftyaccounts', title: '修改', items: [forms]});
        			}
        			
        			if(title == '分包方安全费用使用检查')
        			{
//        				insertFileToList();
        				actionURL = 'SaftyCostAction!editFenbaosaftyjiancha?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_fenbaosaftyjiancha;
              
        			createForm({
            			autoScroll: true,
            			action: 'editFenbaosaftyjiancha',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editFenbaosaftyjiancha', title: '修改', items: [forms]});
        			}
        			
        			
        			if(title == '项目部安全费用统计')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = actionURL = 'SaftyCostAction!editSaftycosttj?userName=' + user.name + "&userRole=" + user.role; 
        				items = items_saftycosttj;
                
        			createForm({
            			autoScroll: true,
            			action: 'editSaftycosttj',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftycosttj', title: '修改项目', items: [forms]});
        			}
        			
        			if(title == '分包方安全费用统计')
        			{
//        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = actionURL = 'SaftyCostAction!editFenbaosaftycostsum?userName=' + user.name + "&userRole=" + user.role; 
        				items = items_fenbaosaftycostsum;
                
        			createForm({
            			autoScroll: true,
            			action: 'editFenbaosaftycostsum',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editFenbaosaftycostsum', title: '修改项目', items: [forms]});
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
    					if(title == '项目部安全生产投入计划')
    					{$.getJSON(encodeURI("SaftyCostAction!deleteSaftycostplan?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '项目部安全费用统计')
    					{$.getJSON(encodeURI("SaftyCostAction!deleteSaftycosttj?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '项目部安全费用使用台账' )
    					{$.getJSON(encodeURI("SaftyCostAction!deleteSaftyaccounts?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '项目部安全费用使用检查')
    					{$.getJSON(encodeURI("SaftyCostAction!deleteSaftyjiancha?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '分包方安全生产投入计划')
    					{$.getJSON(encodeURI("SaftyCostAction!deleteFenbaoplan?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '分包方安全费用使用台账')
    					{$.getJSON(encodeURI("SaftyCostAction!deleteFenbaosaftyaccounts?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '分包方安全费用使用检查')
    					{$.getJSON(encodeURI("SaftyCostAction!deleteFenbaosaftyjiancha?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					if(title == '分包方安全费用统计')
    					{$.getJSON(encodeURI("SaftyCostAction!deleteFenbaosaftycostsum?userName=" + user.name + "&userRole=" + user.role),
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
						$.getJSON('SaftyCostAction!importExcel', { fileName: fileName, type : title, projectName: projectName },
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
       
        //建立工具栏
        var tbar = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        })
        
        
        
        //添加显示的框
        var items_saftycost = [{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: 'hbox',
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
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '科目编码',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'subjectnum'
                },{
                	xtype:'textfield',
                    fieldLabel: '费用分类',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'costkind'
                },{
                	xtype:'textfield',
                    fieldLabel: '费用明细',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'costetails'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '一月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'jan'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '二月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'feb'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '三月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'mar'
                
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '四月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'apr'
                },{
                	xtype:'textfield',
                    fieldLabel: '五月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'may'
                },{
                	xtype:'textfield',
                    fieldLabel: '六月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'june'
                },{
                	xtype:'textfield',
                    fieldLabel: '七月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'july'
                },{
                	xtype:'textfield',
                    fieldLabel: '八月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'aug'
                },{
                	xtype:'textfield',
                    fieldLabel: '九月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'sept'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '十月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'oct'
                },{
                	xtype:'textfield',
                    fieldLabel: '十一月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'nov'
                },{
                	xtype:'textfield',
                    fieldLabel: '十二月',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'dec'
                },{
                	xtype:'textfield',
                    fieldLabel: '总计',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'sumcost'
                },{
	            	xtype:'textfield',
                    fieldLabel: 'ID',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'planorcost',
                    value:'0',
                   	hidden: true,
                    hiddenLabel: true
                }]
	        }]
	    }]
        
        
        
        
        
        
        
        function gettitle(grid){
        	editSel = [];  //清空数组
      	    editSel = grid.getSelectionModel().getSelection();
      	    yearorfull = editSel[0].data.yearorfull;
      	    if(yearorfull=="年度投入计划"){
      	    	return editSel[0].data.year+"年";
      	    }else{
      	    	return yearorfull
      	    }
        }
        //添加显示的框'项目部安全费用使用台帐'******************
        //***项目部安全生产投入计划
        var items_saftycostplan =[{
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
                    labelWidth: 350,
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
                },
                 {
                	xtype:'textfield',
                    fieldLabel: '附件',
                    labelWidth: 350,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'combo',
                	queryMode:'local',
                	fieldLabel: '计划类型',
                	editable:false,
                	labelAlign: 'right',
                	labelWidth: 560,
                    anchor:'95%',
                	store:new Ext.data.ArrayStore({
                	   fields : ['id','name'],
                	   data : [[1,'年度投入计划'],
                		       [2,'项目整体投入计划']]
                	}),
                	valueField:'name',
                	displayField:'name',
                	triggerAction:'all',
                	autoSelect:true,
                    allowBlank: false,
                    listeners:
                	{
                    	change :function(combo){
                		  var kind = combo.value;
              		    if(kind=="项目整体投入计划"){
              			  var year = forms.getForm().findField('year');
              			  year.setVisible(false);
              			  year.hidden = true;
              			  year.hiddenLabel=true;
              			  year.allowBlank = true;
              			   
              		    }
              		    else{
              		    	 var year = forms.getForm().findField('year');
             			    year.setVisible(true);
             			     year.hidden = false;
               			     year.hiddenLabel=false;
               			     year.allowBlank = false;
              		    }
                	  }
                    },
                    name:'yearorfull'
                },{
                	xtype:'combo',
                	queryMode:'local',
                	fieldLabel: '年份',
                	editable:false,
                	labelAlign: 'right',
                	labelWidth: 560,
                    anchor:'95%',
                	store:new Ext.data.ArrayStore({
                	   fields : ['id','name'],
                	   data : []
                	}),
                	valueField:'name',
                	displayField:'id',
                	triggerAction:'all',
                	autoSelect:true,
                	listeners:
                	{
                	  beforerender :function(){
                	    var newyear = Ext.Date.format(new Date(),'Y');//这是为了取现在的年份数
                	    var yearlist = [];
                	    var first = newyear;
                	    for(var i = -1;i<=1;i++){
                	      yearlist.push([ Number(i)+Number(newyear),Number(i)+Number(newyear)]);
                	    }
                	    this.store.loadData(yearlist);
                	  }
                    },
                    allowBlank: false,
                    name:'year'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）总包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind0'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind01'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'costkind02',
                    allowBlank: false,
                    listeners:{
                        'focus': countSumitem
                    }
                },{
	            	xtype:'numberfield',
                    fieldLabel: '配备、 维护、 保养应急救援器材、 设备支出和应急演练支出 总包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind1'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind11'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind12',
                    listeners:{
                        'focus': countSumitem
                    }
                },{
	            	xtype:'numberfield',
                    fieldLabel: '开展重大危险源和事故隐患评估、监控和整改支出 总包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind2'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind21'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind22',
                    listeners:{
                        'focus': countSumitem
                    }
                },{
	            	xtype:'numberfield',
                    fieldLabel: '安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出 总包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind3'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind31'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind32',
                    listeners:{
                        'focus': countSumitem
                    }
                },{
	            	xtype:'numberfield',
                    fieldLabel: '配备和更新现场作业人员安全防护用品支出 总包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind4'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind41'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind42',
                    listeners:{
                        'focus': countSumitem
                    }
                },{
	            	xtype:'numberfield',
                    fieldLabel: '安全生产宣传、教育、培训支出 总包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind5'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind51'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind52',
                    listeners:{
                        'focus': countSumitem
                    }
                },{
	            	xtype:'numberfield',
                    fieldLabel: '安全生产 生产适用的新技术、 新标准、 新工艺、 新准备的推广应用 总包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind6'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind61'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind62',
                    listeners:{
                        'focus': countSumitem
                    }
                },{
	            	xtype:'numberfield',
                    fieldLabel: '安全设施及特种设备检测检验支出 总包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind7'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind71'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind72',
                    listeners:{
                        'focus': countSumitem
                    }
                },{
	            	xtype:'numberfield',
                    fieldLabel: '野外应急食品、应急器械、应急药品支出 总包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind8'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind81'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind82',
                    listeners:{
                        'focus': countSumitem
                    }
                },{
	            	xtype:'numberfield',
                    fieldLabel: '其它与安全生产直接相关的支出 总包方',
                    labelWidth:560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind9'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '分包方',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind91'
                },{
	            	xtype:'numberfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind92',
                    listeners:{
                        'focus': countSumitem
                    }
                },{
                	xtype:'textfield',
                    fieldLabel: '总包方合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costplansum',
                    listeners:{
                      'focus': countSum
                    }
                } ,{
                	xtype:'textfield',
                    fieldLabel: '分包方合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costplansum1',
                    listeners:{
                      'focus': countSum1
                    }
                },{
                	xtype:'textfield',
                    fieldLabel: '项目合计',
                    labelWidth: 560,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costplansum2',
                    listeners:{
                      'focus': countSum2
                    }
                }]
	        }]
	    }
	    , {
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
	                text: '请点击上传项目部安全生产投入计划',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,uploadPanel
        ]
        
        
//    {padding-left:10px;
//	    	xtype: 'container',
//	        anchor: '100%',
//	        layout: {
//	        	type:'hbox',
////	        	align: 'middle ',
////	            pack: 'left',
//	        },
//	        width: 150,
//	        items:[
//	        	{
//	            	xtype:'tbtext',
//	                text: '请点击上传项目部安全生产投入计划',
////	                width: 100,
//	                height:10,
////	                labelAlign: 'right',
////	                anchor:'95%',margin-bottom:10px
//	                style:'color:#FF0000;margin-left:5px;margin-top:15px',
//	                name: 'tip',
//	            }
//	        	,{
//	            	xtype:'box',
//	                width:20,
//	                height:50,
////	                labelAlign: 'right',
////	                anchor:'95%',
//	                allowBlank: false,
//	                style:'padding-left:2px', 
//	                html:'<img src="./Images/ims/icon/提示图片.png">',
//	                name: 'tipimg',
//	            }
//	        ]}
    
        
        function  countSumitem(numberfield)
        {
        	var sum = 0;
        	var t1 = 0;
        	var t2 = 0;
        	
        	var num = numberfield.name.substring(8,9);
        	textgf = forms.getForm().findField('costkind'+num+'2');
        	num1 = forms.getForm().findField('costkind'+num);
        	num2 = forms.getForm().findField('costkind'+num+'1');
    		t1 = num1.getValue();
    		if(t1==""){ t1 = 0;}
    		t2 = num2.getValue();
    		if(t2==""){ t2 = 0;}
    		sum = Number(t1)+Number(t2);
    		textgf.setValue(sum);
        }
        
        function countSum()
        {
        	var sum = 0;
        	var t = 0;
        	var textgf;
        	for(var i = 0;i<10;i++)
        	{
        	
        		textgf = forms.getForm().findField('costkind'+i);
        		t = textgf.getValue();
        		if(t==""){ t = 0;}
        		sum = Number(sum)+Number(t);
        	}
        	
        	
        	var sumtext = forms.getForm().findField('costplansum');
        	
        	sumtext.setValue(sum);
        }
        
        function countSum1()
        {
        	var sum = 0;
        	var t = 0;
        	var textgf;
        	for(var i = 0;i<10;i++)
        	{
        		textgf = forms.getForm().findField('costkind'+i+'1');
        		t = textgf.getValue();
        		if(t==""){ t = 0;}
        		sum = Number(sum)+Number(t);
        	}
        	
        	
        	var sumtext = forms.getForm().findField('costplansum1');
        	
        	sumtext.setValue(sum);
        }
        
        
        function countSum2()
        {
        	var sum = 0;
        	var t = 0;
        	var textgf;
        	for(var i = 0;i<10;i++)
        	{
        		
        		textgf = forms.getForm().findField('costkind'+i+'2');
        		t = textgf.getValue();
        		if(t==""){ t = 0;}
        		sum = Number(sum)+Number(t);
        	}
        	
        	
        	var sumtext = forms.getForm().findField('costplansum2');
        	
        	sumtext.setValue(sum);
        }
//        ,{
//        	xtype: 'container',
//            flex: 1,
//            layout: 'column',
//            anchor : '100%',
//            name:'父控件',
//            items:[{
//            	xtype:'textfield',
//                fieldLabel: '整改问题1',
////                labelWidth: 500,
//                labelAlign: 'right',
//                anchor:'90%',
//                name: 'problem1',
////                width:685
//                columnWidth : .9
//            },{
//            	xtype:'button',
//                text: '+',
//                labelWidth: 5,
//                columnWidth : .05,
//                labelAlign: 'right',
//                anchor:'5%',
//                name: 'addbtn',
//                margin:'0 0 0 10',
//                listeners:{
//                	'click':AddZB
//                }
//            }]
//        }
        
        
//        var combostore = new Ext.data.ArrayStore({
//        	                 fields: ['id', 'name'],
//        	                 data: [[1, '团员'], [2, '党员'], [3, '其他']]
//        	             });
//        	             //创建Combobox
//        	             var combobox = new Ext.form.ComboBox({
//        	                 fieldLabel: '政治面貌',
//        	                 store: combostore,
//        	                 displayField: 'name',
//        	                 valueField: 'id',
//        	                 triggerAction: 'all',
//        	                 emptyText: '请选择...',
//        	                 allowBlank: false,
//        	                 blankText: '请选择政治面貌',
//        	                 editable: false,
//        	                 mode: 'local'
//        	             });
        
        
//      var combostore = new Ext.data.ArrayStore({
//        fields: ['id', 'name'],
//        data: [[1, '团员'], [2, '党员'], [3, '其他']]
//    });
        
        
      var combostore = new Ext.data.ArrayStore({
        fields: ['id', 'kind'],
        data: [[1, '完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）'], 
        	[2, '配备、 维护、 保养应急救援器材、 设备支出和应急演练支出'], 
        	[3, '开展重大危险源和事故隐患评估、监控和整改支出'], 
        	[4, '安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出'], 
        	[5, '配备和更新现场作业人员安全防护用品支出'], 
        	[6, '安全生产宣传、教育、培训支出'], 
        	[7, '安全生产 生产适用的新技术、 新标准、 新工艺、 新准备的推广应用'], 
        	[8, '安全设施及特种设备检测检验支出'], 
        	[9, '野外应急食品、应急器械、应急药品支出'], 
        	[10, '其它与安全生产直接相关的支出']]
      });
      
      
      var bianmacombo = new Ext.data.ArrayStore({
          fields: ['kind', 'bianma'],
          queryMode: 'local',
          data: [
        	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）', '4301040101'], 
          	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）', '4301040102'], 
          	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）', '4301040103'], 
          	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）', '4301040104'], 
          	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）', '4301040105'], 
          	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）', '4301040106'], 
          	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）', '4301040107'], 
          	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）', '4301040108'], 
          	
          	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '4301040201'], 
          	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '4301040202'],
          	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '4301040203'],
          	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '4301040204'],
          	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '4301040205'],
          	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '4301040206'],
          	
          	['开展重大危险源和事故隐患评估、监控和整改支出', '4301040301'],
          	['开展重大危险源和事故隐患评估、监控和整改支出', '4301040302'],
          	['开展重大危险源和事故隐患评估、监控和整改支出', '4301040303'],
          	['开展重大危险源和事故隐患评估、监控和整改支出', '4301040304'],
          	
          	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '4301040401'],
          	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '4301040402'],
          	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '4301040403'],
          	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '4301040404'],
          	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '4301040405'],
          	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '4301040406'],
          	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '4301040407'],
          	
          	['配备和更新现场作业人员安全防护用品支出', '4301040501'],
          	['配备和更新现场作业人员安全防护用品支出', '4301040502'],
          	['配备和更新现场作业人员安全防护用品支出', '4301040503'],
          	['配备和更新现场作业人员安全防护用品支出', '4301040504'],
          	['配备和更新现场作业人员安全防护用品支出', '4301040505'],
          	['配备和更新现场作业人员安全防护用品支出', '4301040506'],
          	['配备和更新现场作业人员安全防护用品支出', '4301040507'],
          	['配备和更新现场作业人员安全防护用品支出', '4301040508'],
          	
          	['安全生产宣传、教育、培训支出', '4301040601'],
          	['安全生产宣传、教育、培训支出', '4301040602'],
          	['安全生产宣传、教育、培训支出', '4301040603'],
          	['安全生产宣传、教育、培训支出', '4301040604'],
          	['安全生产宣传、教育、培训支出', '4301040605'],
          	['安全生产宣传、教育、培训支出', '4301040606'],
          	['安全生产宣传、教育、培训支出', '4301040607'],
          	
          	['安全生产 生产适用的新技术、 新标准、 新工艺、 新准备的推广应用', '43010407'],
          	
          	['安全设施及特种设备检测检验支出', '4301040801'],
          	['安全设施及特种设备检测检验支出', '4301040801'],
          	
          	['野外应急食品、应急器械、应急药品支出', '43010409'],
          	
          	['其它与安全生产直接相关的支出', '4301041601'],
          	['其它与安全生产直接相关的支出', '4301041602'],
          	['其它与安全生产直接相关的支出', '4301041603'],
          	['其它与安全生产直接相关的支出', '4301041604'],
          	['其它与安全生产直接相关的支出', '4301041605'],
          	['其它与安全生产直接相关的支出', '4301041606'],
          	['其它与安全生产直接相关的支出', '4301041607']]
        });
      
        var detailscombo = new Ext.data.ArrayStore({
            fields: ['kind', 'details'],
            queryMode: 'local',
            data: [
            	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）','1、安全防护设施设备支出1）各类机电设备安全装置；2）施工供配电及用电安全防护设施（漏电保护、接地保护、触电保护等装置，变压器、配电盘周边防护设施，电器防爆设施，防水电缆及备用电源等）；3）机械设备（起重机械、提升设备、锅炉、压力器、压缩机等）上的各种保护、保险装置及安全防护措施；4）防治边帮滑坡设施。'], 
            	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）','2、临时安全防护设施设备支出1）“洞口”（楼梯口、电梯井口、预留洞口、通道口等）、“临边”（未安装栏杆的平台临边、无外架防护的层面临边、升降口临边、基坑沟槽临边、上下斜道临边等）、挖井、挖孔、沉井、泥浆池等防护、防滑设施；2）高处作业中防止物体、人员坠落设置的安全带、棚、护栏等防护设施；3）爆破及交叉作业（穿越村镇、公路、河流、地下管线进行施工、运输等作业）所增设的防护、隔离、栏挡等措施；4）施工场地安全围挡设施。'],
            	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）','3、有毒、 有害、 危险源等监控、 检测设备支出1）隧道及孔洞开挖过程中有毒有害气体监测、通风设备设施，隧道内粉尘监测设备设施。 '],
            	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）','4、防雷、 防台等异常气候、 防地质灾害设施设备支出1）防火、防爆、防尘、防毒、防雷、防台风等设备设施及备品；2）地质灾害监控防护设备设施。'],
            	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）','5、安全警示、标志、器材支出1）各种安全告示、警告标志；2）航道、车道临时防护及警示标识设置等。'],
            	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）','6、大型起重机械安装安全监控管理系统支出1）安全防护通讯设备'],
            	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）','7、安全报警系统支出'],
            	['完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）','8、其它（其他临时安全防护设备、设施）'],
            		
            	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '1、应急预案编写、评审支出'],
            	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '2、应急救援设施、设备、 物资支出（应急电源、照明、通风、抽水、提升设备及锹镐铲、千斤顶等）'],
            	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '3、应急信息、 通讯系统建设支出'],
            	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '4、应急医疗器材、药品支出'],
            	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '5、应急救援培训、演练支出'],
            	['配备、 维护、 保养应急救援器材、 设备支出和应急演练支出', '6、消防器材及系统支出'],
            	
            	['开展重大危险源和事故隐患评估、监控和整改支出', '1、重大危险源和事故隐患辨识、评估支出'], 
            	['开展重大危险源和事故隐患评估、监控和整改支出', '2、重大危险源和事故隐患监控支出'], 
            	['开展重大危险源和事故隐患评估、监控和整改支出', '3、 重大危险源和事故隐患整改支出'], 
            	['开展重大危险源和事故隐患评估、监控和整改支出', '4、危险性较大的分部分项工程作业评估、监控和整改支出'], 
            	
            	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '1、安全检查用具、器材支出'], 
            	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '2、日常安全检查支出'], 
            	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '3、专项、综合安全检查支出'], 
            	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '4、安全考核支出'], 
            	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '5、专项安全评价支出'], 
            	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '6、安全咨询费用支出'], 
            	['安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出', '7、安全标准化建设支出'], 
            	
            	['配备和更新现场作业人员安全防护用品支出', '1、安全帽'], 
            	['配备和更新现场作业人员安全防护用品支出', '2、防护服'], 
            	['配备和更新现场作业人员安全防护用品支出', '3、防护手套'], 
            	['配备和更新现场作业人员安全防护用品支出', '4、呼吸、防尘用具'], 
            	['配备和更新现场作业人员安全防护用品支出', '5、眼防护用具'], 
            	['配备和更新现场作业人员安全防护用品支出', '6、防护鞋'], 
            	['配备和更新现场作业人员安全防护用品支出', '7、防暑 降温物品的支出'], 
            	['配备和更新现场作业人员安全防护用品支出', '8、其它个人防护用具'], 
            	
            	['安全生产宣传、教育、培训支出', '1、安全生产月、安康杯等安全活动支出'], 
            	['安全生产宣传、教育、培训支出', '2、其它专项安全活动支出（ 安全竞赛等）'], 
            	['安全生产宣传、教育、培训支出', '3、安全宣传：宣传标语、 书籍、 报刊、 音响材料、 宣传器材支出'], 
            	['安全生产宣传、教育、培训支出', '4、安全培训器材支出'], 
            	['安全生产宣传、教育、培训支出', '5、安全培训教材支出'], 
            	['安全生产宣传、教育、培训支出', '6、安全培训场地费用支出'], 
            	['安全生产宣传、教育、培训支出', '7、培训人员及受训人员（专职安检人员、生产管理人员、从业人员及特种作业人员）的工资等相关费用支出'], 
            	
            	['安全生产 生产适用的新技术、 新标准、 新工艺、 新准备的推广应用', ''], 
            	
            	['安全设施及特种设备检测检验支出', '1、安全设施的检测、检验支出'], 
            	['安全设施及特种设备检测检验支出', '2、特种设备的检测、检验支出（特种机械设备、压力容器、避雷设施）'], 
            	
            	['野外应急食品、应急器械、应急药品支出', ''], 
            	
            	['其它与安全生产直接相关的支出', '1、结算给分包队伍的安全生产费用'],
            	['其它与安全生产直接相关的支出', '2、安全管理体系运行维护支出'],
            	['其它与安全生产直接相关的支出', '3、安全会议支出'],
            	['其它与安全生产直接相关的支出', '4、安全奖励、表彰支出'],
            	['其它与安全生产直接相关的支出', '5、特种作业人员（从事高空、井下、尘毒作业的人员及炊管人员等）体检费用'],
            	['其它与安全生产直接相关的支出', '6、办理安全施工许可证'],
            	['其它与安全生产直接相关的支出', '7、办公、生活区的防腐、防毒、防四害、防触电、防煤气、防火患等支出。']]
          });
        
        
        
        var items_saftycosttj =[{
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
                    fieldLabel: '年份',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'year'
                },{
	            	xtype:'textfield',
                    fieldLabel: '科目编码',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'subjectnum'
                },{
                	xtype:'combobox',
                    fieldLabel: '费用分类',
                    store: combostore,
                    valueField: 'value',
                    displayField: 'value',
	                 triggerAction: 'all',
//	                 emptyText: '请选择...',
//	                 allowBlank: false,
//	                 blankText: '请选择政治面貌',
	                 editable: false,
	                 mode: 'local',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind'
                },{
                	xtype:'textfield',
                    fieldLabel: '支出',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'cost'
                },{
                	xtype:'numberfield',
                    fieldLabel: '实时整体统计',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'costrealtime',
                	hidden: true,
                    hiddenLabel: true
                }]
	        }]
	    },
        	uploadPanel
        ]
        
        
        function liandong()
        {
//        	alert("change");
            var kind = forms.getForm().findField('costkind').value;
            var detailscombo = forms.getForm().findField("costetails").getStore(); 
            detailscombo.clearFilter();  
            detailscombo.filter('kind',kind);
            detailscombo.load();
            
            
            var subject = forms.getForm().findField("subjectnum");
            if(subject!=null)
            {
            	var bianmacombo = subject.getStore(); 
                bianmacombo.clearFilter();  
                bianmacombo.filter('kind',kind);
                bianmacombo.load();
            }
        }
        
        
        function detailschange(){
        	var detailstr = forms.getForm().findField("costetails").value; 
        	var index = -1;
        	var detailscombo = forms.getForm().findField("costetails").getStore();
        	for(var i = 0;i<detailscombo.getCount();i++){
        		var tmp = detailscombo.getAt(i).get('details');
        		if(tmp==detailstr){
        		    index = i;
        		}
        	}
        	
        	
        	if(index!=-1){
        		var subject = forms.getForm().findField("subjectnum");
                if(subject!=null)
                {
                	var bianmacombo = subject.getStore(); 
                	subject.setValue(bianmacombo.getAt(index).get('bianma'));
                }
        	}
        }
        
       
        
        var items_saftyaccounts =[{
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
                	xtype:'combobox',
                    fieldLabel: '费用分类',
                    store: combostore,
                    valueField: 'kind',
                    displayField: 'kind',
//	                 triggerAction: 'all',
	                 editable: false,
	                 mode: 'local',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costkind',
                    
                    listeners: {  
                    	change:liandong 
                    }  
//                    renderTo:'costkind'
                },{
                	xtype:'combobox',
                    fieldLabel: '费用明细',
                    store: detailscombo,
                    valueField: 'details',
                    displayField: 'details',
//	                 triggerAction: 'all',
	                 editable: false,
//	                 mode: 'local',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    listeners: {  
                    	change:detailschange 
                    },
                    name: 'costetails'
                },{
	            	xtype:'combobox',
                    fieldLabel: '科目编码',
                    store: bianmacombo,
                    valueField: 'bianma',
                    displayField: 'bianma',
//                    labelWidth: 120,
                    editable: false,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'subjectnum'
                },{
                	xtype:'textfield',
                    fieldLabel: '申请部门',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'applysector'
                },{
                	xtype:'textfield',
                    fieldLabel: '费用用途',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'costuse'
                
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'numberfield',
                    fieldLabel: '金额(元)',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'amount'
                },{
                	xtype:'textfield',
                    fieldLabel: '经办人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'manager'
                },{
                	xtype:'textfield',
                    fieldLabel: '登记人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'registerperson'
                },{
//                	xtype:'textfield',
//                    fieldLabel: '审批时间',
//                    //labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    name: 'approtime'
                	 xtype: 'datefield',
                     fieldLabel: '审批时间',
                     name: 'approtime',
                     format : 'Y-m-d',
                     anchor:'95%',
                     allowBlank: false,
                     labelAlign: 'right'
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
            name: 'remarks'
	    },
	    {
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
	                text: '请点击上传安全费用使用凭证',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]


        
        var combocheck = new Ext.data.ArrayStore({
            fields: ['id', 'value'],
            data: [[1, '符合'], 
            	[2, '不符合']]
          });
        
        //添加显示的框'项目部安全费用使用检查'******************
        var items_saftyjiancha =[{
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
                    fieldLabel: '检查人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'jcperson'
                },{
                  xtype: 'datefield',
                  fieldLabel: '检查时间',
                  name: 'jctime',
                  format : 'Y-m-d',
                  anchor:'95%',
                  allowBlank: false,
                  labelAlign: 'right'
                }]
	        },{
  	        	xtype: 'container',
  	            flex: 1,
  	            anchor:'95%',
  	            layout: 'anchor',
  	            items: [{
                	xtype:'combobox',
                    fieldLabel: '检查结果',
                    store: combocheck,
                    valueField: 'value',
                    displayField: 'value',
	                 triggerAction: 'all',
	                 editable: false,
	                 mode: 'local',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'jcresult'
                }]
  	       }]
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
	                text: '请点击上传安全费用检查记录',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,uploadPanel
        ]
        
        
       var items_fenbaoplan = [{
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
                    fieldLabel: '分包单位名称',
                    labelWidth: 160,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'fenbaoname'
                },{
                	xtype:'textfield',
                    fieldLabel: '报备安全生产投入计划名称',
                    labelWidth: 160,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'planname'
                }]
	        },{
  	        	xtype: 'container',
  	            flex: 1,
  	            anchor:'95%',
  	            layout: 'anchor',
  	            items: [{
                    xtype: 'datefield',
                    fieldLabel: '报备时间',
                    name: 'time',
                    format : 'Y-m-d',
                    labelWidth: 160,
                    anchor:'95%',
                    allowBlank: false,
                    labelAlign: 'right'
                  }]
  	       }]
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
	                text: '请点击上传分包方安全生产投入计划',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,uploadPanel
        ]
        
        
//        xtype: 'datefield',
//        fieldLabel: '创建结束时间',
//        name: 'creatEndTime',
//        format : 'Y-m-d',
//        anchor:'-10',
//        readOnly:true,
//        hideTrigger:true,
//        listeners: {
//        //光标聚焦时触发的事件
//        "focus": function () { 
//        this.onTriggerClick(); //显示日期选择框
//        },
//        //失去焦点时触发的事件
//        "blur":function(){
//        this.onMenuHide();//隐藏日期选择框
//        }}
	
        //添加显示的框'分包方安全费用使用台账'******************
        var items_fenbaosaftyaccounts =  [{
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
                    fieldLabel: '分包单位名称',
                    labelWidth: 160,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'subcontractor'
                },{
                	xtype:'textfield',
                    fieldLabel: '报备安全费用使用台账',
                    labelWidth: 160,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'taizhang'
                }]
	        },{
  	        	xtype: 'container',
  	            flex: 1,
  	            anchor:'95%',
  	            layout: 'anchor',
  	            items: [{
                    xtype: 'datefield',
                    fieldLabel: '报备时间',
                    name: 'checktime',
                    format : 'Y-m-d',
                    anchor:'95%',
                    allowBlank: false,
                    labelAlign: 'right'
                  }]
  	       }]
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
	                text: '请点击上传分包方安全费用使用凭证',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,uploadPanel
        ]
        
        
        var items_fenbaosaftyjiancha = [{
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
                    fieldLabel: '分包单位名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'fenbaoname'
                },{
                	xtype:'textfield',
                    fieldLabel: '检查人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'jcperson'
                }]
	        },{
  	        	xtype: 'container',
  	            flex: 1,
  	            anchor:'95%',
  	            layout: 'anchor',
  	            items: [{
                    xtype: 'datefield',
                    fieldLabel: '检查时间',
                    name: 'jctime',
                    format : 'Y-m-d',
                    anchor:'95%',
                    allowBlank: false,
                    labelAlign: 'right'
                  },{
                  	xtype:'combobox',
                      fieldLabel: '检查结果',
                      store: combocheck,
                      valueField: 'value',
                      displayField: 'value',
  	                 triggerAction: 'all',
  	                 editable: false,
  	                 mode: 'local',
                      //labelWidth: 120,
                      labelAlign: 'right',
                      anchor:'95%',
                      allowBlank: false,
                      name: 'jcresult'
                  }]
  	       }]
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
	                text: '请点击上传分包方安全费用使用检查记录',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,uploadPanel
        ]
        
        
        
        var items_fenbaosaftycostsum = [{
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
//                	xtype:'textfield',
//                  fieldLabel: '登记时间',
//                  //labelWidth: 120,
//                  labelAlign: 'right',
//                  anchor:'95%',
//                  name: 'registertime'
              	 xtype: 'datefield',
                   fieldLabel: '审批登记日期',
                   format : 'Y-m-d',
                   anchor:'95%',
                   labelAlign: 'right',
                   allowBlank: false,
                   name: 'regtime'
              },{
                	xtype:'textfield',
                    fieldLabel: '分包单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'repoter'
                },{
                	xtype:'numberfield',
                    fieldLabel: '结算金额',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'cost'
                
                }]
	        }]
	    }]
        
        
        
 
        
       	/**
       	 * saftycostplan 计划
       	 * 计划
       	 */
		var store_SaftyCostPlan = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'subjectnum'},
                     { name: 'costkind0'},
                     { name: 'costkind1'},
                     { name: 'costkind2'},
                     { name: 'costkind3'},
                     { name: 'costkind4'},
                     { name: 'costkind5'},
                     { name: 'costkind6'},
                     { name: 'costkind7'},
                     { name: 'costkind8'},
                     { name: 'costkind9'},
                     { name: 'costkind01'},
                     { name: 'costkind11'},
                     { name: 'costkind21'},
                     { name: 'costkind31'},
                     { name: 'costkind41'},
                     { name: 'costkind51'},
                     { name: 'costkind61'},
                     { name: 'costkind71'},
                     { name: 'costkind81'},
                     { name: 'costkind91'},
                     { name: 'costkind02'},
                     { name: 'costkind12'},
                     { name: 'costkind22'},
                     { name: 'costkind32'},
                     { name: 'costkind42'},
                     { name: 'costkind52'},
                     { name: 'costkind62'},
                     { name: 'costkind72'},
                     { name: 'costkind82'},
                     { name: 'costkind92'},
                     { name: 'year'},
                     { name: 'costplan'},
                     { name: 'costplansum'},
                     { name: 'costplansum1'},
                     { name: 'costplansum2'},
                     { name: 'ProjectName'},
                     { name: 'yearorfull'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getSaftycostplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
    	/**
       	 * SaftyCosttj 项目部安全费用统计
       	 * 计划
       	 */
		var store_SaftyCosttj = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'subjectnum'},
                     { name: 'costkind'},
                     { name: 'year'},
                     { name: 'cost'},
                     { name: 'costrealtime'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getSaftycosttjListDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		/**
       	 * Saftyjiancha 
       	 * 计划
       	 */
		var store_Saftyjiancha = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'jcperson'},
                     { name: 'jctime'},
                     { name: 'jcresult'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getSaftyjianchaListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
    	/**
       	 * saftycost 统计
       	 * 统计
       	 */
		var store_SaftyCostCost = Ext.create('Ext.data.Store', {
        	fields: [
        	         {name:'ID'},
        	         { name: 'subjectnum'},
                     { name: 'costkind'},
                     { name: 'costetails'},
                     { name: 'jan'},
                     { name: 'feb'},
                     { name: 'mar'},
                     { name: 'apr'},
                     { name: 'may'},
                     { name: 'june'},
                     { name: 'july'},
                     { name: 'aug'},
                     { name: 'sept'},
                     { name: 'oct'},
                     { name: 'nov'},
                     { name: 'dec'},
                     { name: 'sumcost'},
                     { name: 'planorcost'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getAllSaftycostcost'),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		/**
       	 * Saftyaccounts数据源
       	 * 统计
       	 */
		var store_Saftyaccounts = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'subjectnum'},
                     { name: 'costkind'},
                     { name: 'costetails'},
                     { name: 'applysector'},
                     { name: 'costuse'},
                     { name: 'amount'},
                     { name: 'manager'},
                     { name: 'registerperson'},
                     { name: 'approtime'},
                     { name: 'Accessory'},
                     { name: 'remarks'},
                     { name: 'checksituation'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getSaftyaccountsListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		
		/**
       	 * Fenbaoplan 
       	 * 计划
       	 */
		var store_Fenbaoplan = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'fenbaoname'},
                     { name: 'planname'},
                     { name: 'time'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getFenbaoplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		
		/**
       	 * Fenbaosaftyaccounts数据源
       	 * 统计
       	 */
		var store_Fenbaosaftyaccounts = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'subcontractor'},
                     { name: 'taizhang'},
                     { name: 'checktime'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getFenbaosaftyaccountsListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		/**
       	 * Fenbaosaftyjiancha 
       	 * 计划
       	 */
		var store_Fenbaosaftyjiancha = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        		     { name: 'fenbaoname'},
        	         { name: 'jcperson'},
                     { name: 'jctime'},
                     { name: 'jcresult'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getFenbaosaftyjianchaListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		/**
       	 * Fenbaosaftycostsum数据源
       	 * 统计
       	 */
		var store_Fenbaosaftycostsum = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'regtime'},
                     { name: 'costkind'},
                     { name: 'repoter'},
                     { name: 'cost'},
//                     { name: 'reportor'},
                     { name: 'sumcost'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getFenbaosaftycostsumDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		
		  
    
		var store_Saftycosttj1 = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        		     { name: 'year'},
                     { name: 'costkind0'},
                     { name: 'costkind1'},
                     { name: 'costkind2'},
                     { name: 'costkind3'},
                     { name: 'costkind4'},
                     { name: 'costkind5'},
                     { name: 'costkind6'},
                     { name: 'costkind7'},
                     { name: 'costkind8'},
                     { name: 'costkind9'},
                     
                     { name: 'costkind01'},
                     { name: 'costkind11'},
                     { name: 'costkind21'},
                     { name: 'costkind31'},
                     { name: 'costkind41'},
                     { name: 'costkind51'},
                     { name: 'costkind61'},
                     { name: 'costkind71'},
                     { name: 'costkind81'},
                     { name: 'costkind91'},
                     
                     { name: 'costkind02'},
                     { name: 'costkind12'},
                     { name: 'costkind22'},
                     { name: 'costkind32'},
                     { name: 'costkind42'},
                     { name: 'costkind52'},
                     { name: 'costkind62'},
                     { name: 'costkind72'},
                     { name: 'costkind82'},
                     { name: 'costkind92'},
                 
                     { name: 'costkind03'},
                     { name: 'costkind13'},
                     { name: 'costkind23'},
                     { name: 'costkind33'},
                     { name: 'costkind43'},
                     { name: 'costkind53'},
                     { name: 'costkind63'},
                     { name: 'costkind73'},
                     { name: 'costkind83'},
                     { name: 'costkind93'},
                     { name: 'sum1'},
                     { name: 'sum2'},
                     { name: 'sum3'},
                     { name: 'sum4'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getSaftycosttj1ListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
      
		

		var store_Saftycosttj2 = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        		     { name: 'year'},
                     { name: 'costkind0'},
                     { name: 'costkind1'},
                     { name: 'costkind2'},
                     { name: 'costkind3'},
                     { name: 'costkind4'},
                     { name: 'costkind5'},
                     { name: 'costkind6'},
                     { name: 'costkind7'},
                     { name: 'costkind8'},
                     { name: 'costkind9'},
                     
                     { name: 'costkind01'},
                     { name: 'costkind11'},
                     { name: 'costkind21'},
                     { name: 'costkind31'},
                     { name: 'costkind41'},
                     { name: 'costkind51'},
                     { name: 'costkind61'},
                     { name: 'costkind71'},
                     { name: 'costkind81'},
                     { name: 'costkind91'},
                     
                     { name: 'costkind02'},
                     { name: 'costkind12'},
                     { name: 'costkind22'},
                     { name: 'costkind32'},
                     { name: 'costkind42'},
                     { name: 'costkind52'},
                     { name: 'costkind62'},
                     { name: 'costkind72'},
                     { name: 'costkind82'},
                     { name: 'costkind92'},
                 
                     { name: 'costkind03'},
                     { name: 'costkind13'},
                     { name: 'costkind23'},
                     { name: 'costkind33'},
                     { name: 'costkind43'},
                     { name: 'costkind53'},
                     { name: 'costkind63'},
                     { name: 'costkind73'},
                     { name: 'costkind83'},
                     { name: 'costkind93'},
                     { name: 'sum1'},
                     { name: 'sum2'},
                     { name: 'sum3'},
                     { name: 'sum4'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getSaftycosttj2ListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		

		var store_Saftycosttj3 = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        		     { name: 'year'},
                     { name: 'costkind0'},
                     { name: 'costkind1'},
                     { name: 'costkind2'},
                     { name: 'costkind3'},
                     { name: 'costkind4'},
                     { name: 'costkind5'},
                     { name: 'costkind6'},
                     { name: 'costkind7'},
                     { name: 'costkind8'},
                     { name: 'costkind9'},
                     
                     { name: 'costkind01'},
                     { name: 'costkind11'},
                     { name: 'costkind21'},
                     { name: 'costkind31'},
                     { name: 'costkind41'},
                     { name: 'costkind51'},
                     { name: 'costkind61'},
                     { name: 'costkind71'},
                     { name: 'costkind81'},
                     { name: 'costkind91'},
                     
                     { name: 'costkind02'},
                     { name: 'costkind12'},
                     { name: 'costkind22'},
                     { name: 'costkind32'},
                     { name: 'costkind42'},
                     { name: 'costkind52'},
                     { name: 'costkind62'},
                     { name: 'costkind72'},
                     { name: 'costkind82'},
                     { name: 'costkind92'},
                 
                     { name: 'costkind03'},
                     { name: 'costkind13'},
                     { name: 'costkind23'},
                     { name: 'costkind33'},
                     { name: 'costkind43'},
                     { name: 'costkind53'},
                     { name: 'costkind63'},
                     { name: 'costkind73'},
                     { name: 'costkind83'},
                     { name: 'costkind93'},
                     { name: 'sum1'},
                     { name: 'sum2'},
                     { name: 'sum3'},
                     { name: 'sum4'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getSaftycosttj3ListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });

		
        if(title == '项目部安全生产投入计划')
        {	
        	
        	dataStore = store_SaftyCostPlan;
        	queryURL = 'SaftyCostAction!getSaftycostplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '计划类型', dataIndex: 'yearorfull', align: 'center', width: 120},	
        		    { text: '年份', dataIndex: 'year', align: 'center', width: 100},	
//            	    { text: '科目编码', dataIndex: 'subjectnum', align: 'center', width: 100},	
//            	    { text: '费用分类', dataIndex: 'costkind', align: 'center', width: 550},
            	    { text: '总包方预算总金额(元)', dataIndex: 'costplansum', align: 'center', width: 150},
            	    { text: '分包方预算总金额(元)', dataIndex: 'costplansum1', align: 'center', width: 150},
            	    { text: '项目合计总金额(元)', dataIndex: 'costplansum2', align: 'center', width: 150}
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
        	tbar.add("-");
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
    			dataStore.getProxy().url = 'SaftyCostAction!getSaftycostplanListDef?userName=' + user.name 
                      + "&userRole=" +user.role + "&projectName=" + "";
    			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
    			
    			//给第一列添加所属项目，dataindex自己根据实际字段改
    			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 300}];
    			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
    			
    			
    			queryURL = 'SaftyCostAction!getSaftycostplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=";
    		}
        		
        }
        
        
        
        if(title == '项目部安全费用使用台账')
        {	
        	dataStore = store_Saftyaccounts;
        	
        	queryURL = 'SaftyCostAction!getSaftyaccountsListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
            	    { text: '科目编码', dataIndex: 'subjectnum', align: 'center', width: 100},	
            	    { text: '费用分类', dataIndex: 'costkind', align: 'center', width: 150},
            	    { text: '费用细目', dataIndex: 'costetails', align: 'center', width: 200},
            	    { text: '申请部门', dataIndex: 'applysector', align: 'center', width: 80},
            	    { text: '费用用途', dataIndex: 'costuse', align: 'center', width: 80},
            	    { text: '金额(元)', dataIndex: 'amount', align: 'center', width: 80},
            	    { text: '经办人', dataIndex: 'manager', align: 'center', width: 80},
            	    { text: '登记人', dataIndex: 'registerperson', align: 'center', width: 80},
            	    { text: '审批时间', dataIndex: 'approtime', align: 'center', width: 120},
//            	    { text: '发票/收据复印件', dataIndex: 'receiptcopy', align: 'center', width: 80},
            	    { text: '备注', dataIndex: 'remarks', align: 'center', width: 80}
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
        	tbar.add("-");
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
        
        
        if(title == '项目部安全费用使用检查'){
            dataStore = store_Saftyjiancha;
        	
        	queryURL = 'SaftyCostAction!getSaftyjianchaListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
            	    { text: '检查人', dataIndex: 'jcperson', align: 'center', width: 80},
            	    { text: '检查时间', dataIndex: 'jctime', align: 'center', width: 120},
            	    { text: '检查结果', dataIndex: 'jcresult', align: 'center', width: 80}
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	tbar.add("-");
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
        
        
        if(title == '分包方安全生产投入计划'){
            dataStore = store_Fenbaoplan;
        	queryURL = 'SaftyCostAction!getFenbaoplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
            	    { text: '分包单位名称', dataIndex: 'fenbaoname', align: 'center', width: 100},
            	    { text: '报备安全生产投入计划名称', dataIndex: 'planname', align: 'center', width: 170},
            	    { text: '报备时间', dataIndex: 'time', align: 'center', width: 100}
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
            	tbar.add("-");
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
        
        
        if(title=='分包方安全费用使用台账')
        {
        	dataStore = store_Fenbaosaftyaccounts;
          	
          	queryURL = 'SaftyCostAction!getFenbaosaftyaccountsListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
          	//dataStore.getProxy().url = encodeURI(queryURL);
          	//style = "write";
          	column = [
          		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
              	    { text: '分包单位名称', dataIndex: 'subcontractor', align: 'center', width: 100},
              	    { text: '报备安全费用使用台账', dataIndex: 'taizhang', align: 'center', width: 150},
              	    { text: '报备时间', dataIndex: 'checktime', align: 'center', width: 100}
              	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
              	]
              	tbar.add("-");
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
        
        if(title=='分包方安全费用使用检查')
        {
            dataStore = store_Fenbaosaftyjiancha;
          	
          	queryURL = 'SaftyCostAction!getFenbaosaftyjianchaListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
          	//dataStore.getProxy().url = encodeURI(queryURL);
          	//style = "write";
          	column = [
          		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
          		    { text: '分包单位名称', dataIndex: 'fenbaoname', align: 'center', width: 100},
          		    { text: '检查人', dataIndex: 'jcperson', align: 'center', width: 80},
          	        { text: '检查时间', dataIndex: 'jctime', align: 'center', width: 120},
          	        { text: '检查结果', dataIndex: 'jcresult', align: 'center', width: 80}
          	        //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
              	]
              	tbar.add("-");
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
        
        
        if(title=='总包方安全费用汇总统计')
        {
        	 dataStore = store_Saftycosttj1;
           	
           	queryURL = 'SaftyCostAction!getSaftycosttj1ListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
           	//dataStore.getProxy().url = encodeURI(queryURL);
           	//style = "write";
           	column = [
           		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
           		    { text: '年份', dataIndex: 'year', align: 'center', width: 150},
           		    { text: '年度投入计划总计(元)', dataIndex: 'sum1', align: 'center', width: 200},
           		    { text: '年度实际使用总计(元)', dataIndex: 'sum2', align: 'center', width: 200},
           		    { text: '项目整体投入计划总计(元)', dataIndex: 'sum3', align: 'center', width: 200},
           		    { text: '项目整体实际使用总计(元)', dataIndex: 'sum4', align: 'center', width: 200}
           		    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
               	]
               	tbar.add("-");
           	    tbar.add(textSearch);
       		    tbar.add(btnSearch);
       		    tbar.add(btnSearchR);
           		tbar.add("-");
//           		tbar.add(btnAdd);
//           		tbar.add(btnEdit);
//           		tbar.add(btnDel);
//           		tbar.add(btnScan);
        }
        
        
        if(title=='分包方安全费用汇总统计')
        {
        	 dataStore = store_Saftycosttj2;
           	
           	queryURL = 'SaftyCostAction!getSaftycosttj2ListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
           	//dataStore.getProxy().url = encodeURI(queryURL);
           	//style = "write";
           	column = [
           		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
           		    { text: '年份', dataIndex: 'year', align: 'center', width: 150},
           		    { text: '年度投入计划总计(元)', dataIndex: 'sum1', align: 'center', width: 200},
        		    { text: '年度实际使用总计(元)', dataIndex: 'sum2', align: 'center', width: 200},
        		    { text: '项目整体投入计划总计(元)', dataIndex: 'sum3', align: 'center', width: 200},
        		    { text: '项目整体实际使用总计(元)', dataIndex: 'sum4', align: 'center', width: 200}
        		    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
               	]
               	tbar.add("-");
           	    tbar.add(textSearch);
       		    tbar.add(btnSearch);
       		    tbar.add(btnSearchR);
           		tbar.add("-");
//           		tbar.add(btnAdd);
//           		tbar.add(btnEdit);
//           		tbar.add(btnDel);
//           		tbar.add(btnScan);
        }
        
        if(title=='项目安全费用汇总统计')
        {
        	 dataStore = store_Saftycosttj3;
           	
           	queryURL = 'SaftyCostAction!getSaftycosttj3ListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
           	//dataStore.getProxy().url = encodeURI(queryURL);
           	//style = "write";
           	column = [
           		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
           		    { text: '年份', dataIndex: 'year', align: 'center', width: 150},
           		    { text: '年度投入计划总计(元)', dataIndex: 'sum1', align: 'center', width: 200},
        		    { text: '年度实际使用总计(元)', dataIndex: 'sum2', align: 'center', width: 200},
        		    { text: '项目整体投入计划总计(元)', dataIndex: 'sum3', align: 'center', width: 200},
        		    { text: '项目整体实际使用总计(元)', dataIndex: 'sum4', align: 'center', width: 200}
        		    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
               	]
               	tbar.add("-");
           	    tbar.add(textSearch);
       		    tbar.add(btnSearch);
       		    tbar.add(btnSearchR);
           		tbar.add("-");
//           		tbar.add(btnAdd);
//           		tbar.add(btnEdit);
//           		tbar.add(btnDel);
//           		tbar.add(btnScan);
           		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'SaftyCostAction!getSaftycosttj3ListDef?userName=' + user.name 
                                                + "&userRole=" +user.role + "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 80}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			queryURL = 'SaftyCostAction!getSaftycosttj3ListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=";
        		}
        }
        
        

		var store_Saftycosttjrenwu1 = Ext.create('Ext.data.Store', {
        	fields: [
//        		     {name:'ID'},
        		     { name: 'year'},
                     { name: 'sum1'},
                     { name: 'sum2'},
                     { name: 'sum3'},
                     { name: 'sum4'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCostAction!getSaftycosttjrenwu1ListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
        if(title=='安全生产费用统计')
        {
        	 dataStore = store_Saftycosttjrenwu1;
           	
           	queryURL = 'SaftyCostAction!getSaftycosttj1ListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
           	//dataStore.getProxy().url = encodeURI(queryURL);
           	//style = "write";
           	column = [
           		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
           		    { text: '年份', dataIndex: 'year', align: 'center', width: 150},
           		    { text: '年度投入计划总计(元)', dataIndex: 'sum1', align: 'center', width: 200},
           		    { text: '年度实际使用总计(元)', dataIndex: 'sum2', align: 'center', width: 200},
           		    { text: '项目整体投入计划总计(元)', dataIndex: 'sum3', align: 'center', width: 200},
           		    { text: '项目整体实际使用总计(元)', dataIndex: 'sum4', align: 'center', width: 200}
           		    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
               	]
               	tbar.add("-");
           	    tbar.add(textSearch);
       		    tbar.add(btnSearch);
       		    tbar.add(btnSearchR);
           		tbar.add("-");
//           		tbar.add(btnAdd);
//           		tbar.add(btnEdit);
//           		tbar.add(btnDel);
//           		tbar.add(btnScan);
           		
           		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'SaftyCostAction!getSaftycosttj3ListDef?userName=' + user.name 
                                                + "&userRole=" +user.role + "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 80}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			queryURL = 'SaftyCostAction!getSaftycosttj1ListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=";
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
                				case "addSaftycostplan": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "?fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "editSaftycostplan":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                				}
                				
                				case "addSaftycosttj":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "?fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				case "editSaftycosttj":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
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
                				
                				
                				case "addSaftycostCost":{
//                					var fileName = getFileName();
//                					if(fileName == null)
//                						fileName = "";
//                					config.url += "&fileName=" + fileName;
//                					uploadPanel.store.removeAll();
                					break;
                				}
                				//***********************
                				case "addSaftyaccounts":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "?fileName=" + fileName;
//                					alert(config.url);
                					//config.url = encodeURI(config.url);
                					uploadPanel.store.removeAll();
                					break;
                				}
                				//************************
                				case "addSaftyjiancha":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "?fileName=" + fileName;
//                					alert(config.url);
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				case "editSaftyjiancha":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                				}
                				
                				//************************
                				case "addFenbaoplan":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "?fileName=" + fileName;
//                					alert(config.url);
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				case "editFenbaoplan":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                				}
                				
                				//************************
                				case "addFenbaosaftyaccounts":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "?fileName=" + fileName;
//                					alert(config.url);
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "editFenbaosaftyaccounts":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                				}
                				//************************
                				case "addFenbaosaftyjiancha":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "?fileName=" + fileName;
//                					alert(config.url);
                					uploadPanel.store.removeAll();
                					break;
                				}
                				case "editFenbaosaftyjiancha":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                				}
                				//************************
                				case "addFenbaosaftyaccountscheck":{
                					break;
                				}
                				
                				case"addFenbaosaftycostsum":{
                					break;
                				}
                				
                				
                				case "editSaftyaccounts": {
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
          	                	},
          	                	failure: function(form, action){
          	                		//alert(action.result.msg);
//          	                		Ext.Msg.alert('警告',action.result.msg);
          	                	}
      	                	})
      	                	this.up('window').close();  	
      	                }
      	                else{//forms.form.isValid() == false
      	                	if(config.action == "addSaftyaccounts" || config.action == "editSaftyaccounts")
                    		{
                    			var brief = forms.getForm().findField('remarks');
                    			var brieftext = brief.getValue();
                    			if(brieftext.length>500)
                    			{
                    				Ext.Msg.alert('警告','备注不能超过500个字符！');
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
                		if (config.action == "editSaftycostplan") 
                        {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		if (config.action == "editSaftycosttj") 
                        {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		 if (config.action == "editSaftycost") 
                         {
                              forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                         }
                		 if (config.action == 'editSaftyaccounts') 
                         {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                         }
                		 if (config.action == 'editFenbaosaftyaccounts') 
                         {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                         }
                		 if (config.action == 'editFenbaosaftycostsum') 
                         {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                         }
                		 if (config.action == 'editSaftyjiancha') 
                         {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                         }
                		 if (config.action == 'editFenbaoplan') 
                         {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                         }
                		 if (config.action == 'editFenbaosaftyaccounts') 
                         {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                         }
                		 if (config.action == 'editFenbaosaftyjiancha') 
                         {
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                         }
                		 if(config.action == 'alloTask')
                     	{
                            var selRecs = gridDT.getSelectionModel().getSelection();
                 		   // 设置表单初始值 		   
                 		   forms.getForm().findField('missionname').setValue(selRecs[0].data.No);
                     	}
                		 
                		 
                		 
                		 
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
        
        	
        	if (config.action == "editSaftycostplan") 
            {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
        	if (config.action == "editSaftycosttj") 
            {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == "editSaftycost") 
            {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == 'editSaftyaccounts') 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            
            if (config.action == 'editFenbaosaftyaccounts') 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == 'editFenbaosaftycostsum') 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == 'editSaftyjiancha') 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == 'editFenbaoplan') 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == 'editFenbaosaftyaccounts') 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == 'editFenbaosaftyjiancha') 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
        };       
        
        //创建装载formPanel的窗体，由工具栏按钮点击显示
        var showWin = function (config) {
        	
        	//清除store的filter
   		    detailscombo.clearFilter(); 
   		    bianmacombo.clearFilter(); 
        	
        	var width = 800;
        	var height = 550;
        	
        	if(title == '项目部安全生产投入计划')
        	{//编辑提案信息框
        		width = 800;
        		height = 600;
        	}
        	
        	if(title == '项目部安全费用使用台账')
        	{//编辑提案信息框
        		width = 800;
        		height = 600;
        	}
        	
        	if(title == '项目部安全费用使用检查')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	
        	

        	if(title == '分包方安全生产投入计划')
        	{//编辑提案信息框
//        		alert('分包方安全生产投入计划');
        		width = 800;
        		height = 500;
        	}
        	
        	if(title == '分包方安全费用使用台账')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	
        	if(title == '分包方安全费用使用检查')
        	{
        		width = 800;
        		heigth = 600;
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
        			}
        			else
        			{
        				btnAllotask.disable();
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
                	if(title == '项目部安全生产投入计划')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '项目部安全生产投入计划')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+gettitle(gridDT)+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">年份</td><td colspan=\"2\" width=\"40%\">" + record.get("year") + "</td>"+
             		                  
             		                    
             		                    "<tr><th width=\"15%\" style=\"padding:5px;\" rowspan=\"2\">科目编码</th>"+
             		                    "<th width=\"40%\" style=\"padding:5px;\" rowspan=\"2\">费用分类</th>"+
             		                    "<th width=\"15%\" style=\"padding:5px;\" colspan=\"3\">预算金额(元)</th></tr>"+
             		                  
             		                   "<tr><th width=\"15%\" style=\"padding:5px;\">总包方</th>"+
             		                  "<th width=\"15%\" style=\"padding:5px;\">分包方</th>"+
             		                  "<th width=\"15%\" style=\"padding:5px;\">项目合计</th></tr>"+
             		                 
              		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010401</td>"+
              		                 "<td width=\"15%\" style=\"padding:5px;\">完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）</td>"+
              		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind0')+
              		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind01')+
              		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind02')+"</td></tr>"+
              		                 
              		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010402</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">配备、 维护、 保养应急救援器材、 设备支出和应急演练支出</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind1')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind11')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind12')+"</td></tr>"+
            		                 
            		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010403</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">开展重大危险源和事故隐患评估、监控和整改支出</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind2')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind21')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind22')+"</td></tr>"+
             		                  
            		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010404</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind3')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind31')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind32')+"</td></tr>"+
            		                 
            		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010405</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">配备和更新现场作业人员安全防护用品支出</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind4')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind41')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind42')+"</td></tr>"+
            		                 
            		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010406</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">安全生产宣传、教育、培训支出</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind5')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind51')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind52')+"</td></tr>"+
            		                 
            		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010407</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">安全生产 生产适用的新技术、 新标准、 新工艺、 新准备的推广应用</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind6')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind61')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind62')+"</td></tr>"+
            		                 
            		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010408</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">安全设施及特种设备检测检验支出</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind7')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind71')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind72')+"</td></tr>"+
            		                 
            		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010409</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">野外应急食品、应急器械、应急药品支出</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind8')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind81')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind82')+"</td></tr>"+
            		                 
            		                 "<tr><td width=\"15%\" style=\"padding:5px;\">43010416</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">其它与安全生产直接相关的支出</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind9')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costkind91')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costkind92')+"</td></tr>"+
            		                 
            		                 "<tr><td width=\"15%\" style=\"padding:5px;\" align=\"center\" colspan=\"2\">预算总金额</td>"+
            		                 "<td width=\"15%\" style=\"padding:5px;\">"+record.get('costplansum')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+record.get('costplansum1')+
            		                 "</td><td width=\"15%\" style=\"padding:5px;\">"+ record.get('costplansum2')+"</td></tr>";
//                                        "</td></tr><tr><td style=\"padding:5px;\">进度</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Progress') + 
//                                        "</td></tr><tr><td style=\"padding:5px;\">建设内容</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('BuildContent') + "</td></tr>";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
             		        html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"4\">";
             		        
            		         var misfile = record.get('Accessory').split('*');
//            		        var foldermis;
          				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
            		         
          				    height = 500;
              		        width = 800;
            		        for(var i = 2;i<misfile.length;i++){
            		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
            		        }
            		        
             		         html_str += "</table>";
                		}  		
                        Ext.create('Ext.window.Window', 
                        {
                           title: '查看详情',
                           titleAlign: 'center',
                           height: 750,
                           width: 800,
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
                	
                	if(title == '项目部安全费用使用台账')
                	{
                		var html_str = "";
                		if(title == '项目部安全费用使用台账')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		        html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('subjectnum')+
 		                               "详细信息</center></h1>"+
 		                               "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
 		                               "<tr><td width=\"15%\" style=\"padding:5px;\">费用分类</td ><td>" + record.get("costkind") + 
 		                               "</td><td width=\"15%\" style=\"padding:5px;\">费用细目</td><td width=\"40%\">" + record.get("costetails") + 
 		                               "</td></tr><tr><td style=\"padding:5px;\">申请部门</td><td>" + record.get('applysector') + 
 		                               "</td><td style=\"padding:5px;\">费用用途</td><td>" + record.get('costuse') + 
 		                               "</tr><tr></td><td style=\"padding:5px;\">金额</td><td>" + record.get('amount') + 
 		                               "</td><td style=\"padding:5px;\">经办人</td><td>" + record.get('manager') + 
 		                               "</tr><tr></td><td style=\"padding:5px;\">登记人</td><td>" + record.get('registerperson') + 
 		                               "</td><td style=\"padding:5px;\">审批时间</td><td>" + record.get('approtime') + 
 		                               "</tr><tr></td><td style=\"padding:5px;\">备注</td><td  align=\"left\"  colspan=\"3\">" + record.get('remarks') +  "</td></tr>";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
             		       html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
            		        
           		         var misfile = record.get('Accessory').split('*');
//           		        var foldermis;
         				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
           		         
         				    height = 500;
             		        width = 800;
           		        for(var i = 2;i<misfile.length;i++){
           		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
           		        }
           		        
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
                	
                	
                	if(title == '项目部安全费用使用检查')
                	{
                		var html_str = "";
                		if(title == '项目部安全费用使用检查')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		        html_str = "<h1 style=\"padding: 5px;\"><center>"+
 		                               "详细信息</center></h1>"+
 		                               "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
 		                               "<tr><td width=\"15%\" style=\"padding:5px;\">检查人</td ><td>" + record.get('jcperson') + 
 		                               "</td><td style=\"padding:5px;\">检查时间</td><td>" + record.get('jctime') + 
 		                              "</tr><tr></td><td style=\"padding:5px;\">检查情况</td><td  align=\"left\"  colspan=\"3\">" + record.get('jcresult') +  "</td></tr>";
             		       html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
            		        
           		         var misfile = record.get('Accessory').split('*');
//           		        var foldermis;
         				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
           		         
         				    height = 500;
             		        width = 800;
           		        for(var i = 2;i<misfile.length;i++){
           		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
           		        }
           		        
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
                	
                	
                	
                	
                	if(title == '分包方安全生产投入计划')
                	{
                		var html_str = "";
                		if(title == '分包方安全生产投入计划')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		        html_str = "<h1 style=\"padding: 5px;\"><center>"+
 		                               "详细信息</center></h1>"+
 		                               "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
 		                               "<tr><td width=\"15%\" style=\"padding:5px;\">分包单位名称</td ><td>" + record.get('fenbaoname') + 
 		                               "</td><td style=\"padding:5px;\">报备安全生产投入计划名称</td><td>" + record.get('fenbaoname') + 
 		                              "</tr><tr></td><td style=\"padding:5px;\">报备时间</td><td  align=\"left\"  colspan=\"3\">" + record.get('time') +  "</td></tr>";
             		       html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
            		        
           		         var misfile = record.get('Accessory').split('*');
//           		        var foldermis;
         				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
           		         
         				    height = 500;
             		        width = 800;
           		        for(var i = 2;i<misfile.length;i++){
           		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
           		        }
           		        
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
                	
                	
                	if(title == '分包方安全费用使用台账')
                	{
                		var html_str = "";
                		if(title == '分包方安全费用使用台账')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		        html_str = "<h1 style=\"padding: 5px;\"><center>"+
                                       "详细信息</center></h1>"+
                                       "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
                                       "<tr><td width=\"15%\" style=\"padding:5px;\">分包单位名称</td ><td>" + record.get('subcontractor') + 
                                       "</td><td style=\"padding:5px;\">报备安全费用使用台账</td><td>" + record.get('taizhang') + 
                                       "</tr><tr></td><td style=\"padding:5px;\">报备时间</td><td  align=\"left\"  colspan=\"3\">" + record.get('checktime') +  "</td></tr>";
             		       html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
            		        
           		         var misfile = record.get('Accessory').split('*');
//           		        var foldermis;
         				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
           		         
         				    height = 500;
             		        width = 800;
           		        for(var i = 2;i<misfile.length;i++){
           		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
           		        }
           		        
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
                	
                	
                	
                	if(title == '分包方安全费用使用检查')
                	{
                		var html_str = "";
                		if(title == '分包方安全费用使用检查')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		        html_str = "<h1 style=\"padding: 5px;\"><center>"+
                             "详细信息</center></h1>"+
                             "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
                             "<tr><td width=\"15%\" style=\"padding:5px;\">分包单位名称</td ><td>" + record.get('fenbaoname') + 
                             "</td><td style=\"padding:5px;\">检查人</td><td>" + record.get('jcperson') + 
                            "</tr><tr></td><td style=\"padding:5px;\">检查时间</td><td>" + record.get('jctime') + 
                            "</td><td style=\"padding:5px;\">检查情况</td><td>" + record.get('jcresult') + "</td></tr>";
             		       html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
            		        
           		         var misfile = record.get('Accessory').split('*');
//           		        var foldermis;
         				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
           		         
         				    height = 500;
             		        width = 800;
           		        for(var i = 2;i<misfile.length;i++){
           		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
           		        }
           		        
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
                	
                	
                	if(title == '总包方安全费用汇总统计')
                	{
                		var html_str = "";
                		if(title == '总包方安全费用汇总统计')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		    html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('year')+
 		                    "年详细信息</center></h1>"+
 		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
 		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
 		                    "</td><td width=\"15%\" style=\"padding:5px;\">年份</td><td width=\"40%\">" + record.get("year") + 
 		                    "</td></tr><tr><td style=\"padding:5px;\">完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）</td><td>" +record.get('year')+"计划:"+ record.get('costkind0') + "<br/>"+record.get('year')+"实际:" + record.get('costkind01')+ "<br/>总包方整体投入计划:"+ record.get('costkind02') +"<br/>总包方整体实际使用:"+ record.get('costkind03')+
                            "</td><td style=\"padding:5px;\">配备、 维护、 保养应急救援器材、 设备支出和应急演练支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind1') + "<br/>"+record.get('year')+"实际:" + record.get('costkind11')+ "<br/>总包方整体投入计划:"+ record.get('costkind12') +"<br/>总包方整体实际使用:"+ record.get('costkind13')+
                            "</td></tr><tr><td style=\"padding:5px;\">开展重大危险源和事故隐患评估、监控和整改支出</td><td>" +record.get('year')+"计划:"+ record.get('costkind2') + "<br/>"+record.get('year')+"实际:" + record.get('costkind21')+ "<br/>总包方整体投入计划:"+ record.get('costkind22') +"<br/>总包方整体实际使用:"+ record.get('costkind23')+
                            "</td><td style=\"padding:5px;\">安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind3') + "<br/>"+record.get('year')+"实际:" + record.get('costkind31')+ "<br/>总包方整体投入计划:"+ record.get('costkind32') +"<br/>总包方整体实际使用:"+ record.get('costkind33')+
                            "</td></tr><tr><td style=\"padding:5px;\">配备和更新现场作业人员安全防护用品支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind4') + "<br/>"+record.get('year')+"实际:" + record.get('costkind41')+ "<br/>总包方整体投入计划:"+ record.get('costkind42') +"<br/>总包方整体实际使用:"+ record.get('costkind43')+
                            "</td><td style=\"padding:5px;\">安全生产宣传、教育、培训支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind5') + "<br/>"+record.get('year')+"实际:" + record.get('costkind51')+ "<br/>总包方整体投入计划:"+ record.get('costkind52') +"<br/>总包方整体实际使用:"+ record.get('costkind53')+
                            "</td></tr><tr><td style=\"padding:5px;\">安全生产 生产适用的新技术、 新标准、 新工艺、 新准备的推广应用</td><td>"+record.get('year')+"计划:"+ record.get('costkind6') + "<br/>"+record.get('year')+"实际:" + record.get('costkind61')+ "<br/>总包方整体投入计划:"+ record.get('costkind62') +"<br/>总包方整体实际使用:"+ record.get('costkind63')+
                            "</td><td style=\"padding:5px;\">安全设施及特种设备检测检验支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind7') + "<br/>"+record.get('year')+"实际:" + record.get('costkind71')+ "<br/>总包方整体投入计划:"+ record.get('costkind72') +"<br/>总包方整体实际使用:"+ record.get('costkind73')+
                            "</td></tr><tr><td style=\"padding:5px;\">野外应急食品、应急器械、应急药品支出</td><td>" +record.get('year')+"计划:"+ record.get('costkind8') + "<br/>"+record.get('year')+"实际:" + record.get('costkind81')+ "<br/>总包方整体投入计划:"+ record.get('costkind82') +"<br/>总包方整体投入实际:"+ record.get('costkind83')+
                            "</td><td style=\"padding:5px;\">其它与安全生产直接相关的支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind9') + "<br/>"+record.get('year')+"实际:" + record.get('costkind91')+ "<br/>总包方整体投入计划:"+ record.get('costkind92') +"<br/>总包方整体实际使用:"+ record.get('costkind93')+
                            "</tr><tr></td><td style=\"padding:5px;\">"+record.get('year')+"投入计划总计"+"</td><td colspan=\"3\">" + record.get('sum1') + "</td></tr>"+
 		                    "</tr><tr></td><td style=\"padding:5px;\">"+record.get('year')+"实际使用总计"+"</td><td colspan=\"3\">" + record.get('sum2') + "</td></tr>"+
 		                    "</tr><tr></td><td style=\"padding:5px;\">总包方整体投入计划总计</td><td colspan=\"3\">" + record.get('sum3') + "</td></tr>"+
 		                    "</tr><tr></td><td style=\"padding:5px;\">总包方整体实际使用总计</td><td colspan=\"3\">" + record.get('sum4') + "</td></tr>";
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
                	
                	
                	if(title == '分包方安全费用汇总统计')
                	{
                		var html_str = "";
                		if(title == '分包方安全费用汇总统计')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		    html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('year')+
 		                    "年详细信息</center></h1>"+
 		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
 		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
 		                    "</td><td width=\"15%\" style=\"padding:5px;\">年份</td><td width=\"40%\">" + record.get("year") + 
 		                    "</td></tr><tr><td style=\"padding:5px;\">完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）</td><td>" +record.get('year')+"计划:"+ record.get('costkind0') + "<br/>"+record.get('year')+"实际:" + record.get('costkind01')+ "<br/>分包方整体投入计划:"+ record.get('costkind02') +"<br/>分包方整体投入实际:"+ record.get('costkind03')+
                            "</td><td style=\"padding:5px;\">配备、 维护、 保养应急救援器材、 设备支出和应急演练支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind1') + "<br/>"+record.get('year')+"实际:" + record.get('costkind11')+ "<br/>分包方整体投入计划:"+ record.get('costkind12') +"<br/>分包方整体投入实际:"+ record.get('costkind13')+
                            "</td></tr><tr><td style=\"padding:5px;\">开展重大危险源和事故隐患评估、监控和整改支出</td><td>" +record.get('year')+"计划:"+ record.get('costkind2') + "<br/>"+record.get('year')+"实际:" + record.get('costkind21')+ "<br/>分包方整体投入计划:"+ record.get('costkind22') +"<br/>分包方整体投入实际:"+ record.get('costkind23')+
                            "</td><td style=\"padding:5px;\">安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind3') + "<br/>"+record.get('year')+"实际:" + record.get('costkind31')+ "<br/>分包方整体投入计划:"+ record.get('costkind32') +"<br/>分包方整体投入实际:"+ record.get('costkind33')+
                            "</td></tr><tr><td style=\"padding:5px;\">配备和更新现场作业人员安全防护用品支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind4') + "<br/>"+record.get('year')+"实际:" + record.get('costkind41')+ "<br/>分包方整体投入计划:"+ record.get('costkind42') +"<br/>分包方整体投入实际:"+ record.get('costkind43')+
                            "</td><td style=\"padding:5px;\">安全生产宣传、教育、培训支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind5') + "<br/>"+record.get('year')+"实际:" + record.get('costkind51')+ "<br/>分包方整体投入计划:"+ record.get('costkind52') +"<br/>分包方整体投入实际:"+ record.get('costkind53')+
                            "</td></tr><tr><td style=\"padding:5px;\">安全生产 生产适用的新技术、 新标准、 新工艺、 新准备的推广应用</td><td>"+record.get('year')+"计划:"+ record.get('costkind6') + "<br/>"+record.get('year')+"实际:" + record.get('costkind61')+ "<br/>分包方整体投入计划:"+ record.get('costkind62') +"<br/>分包方整体投入实际:"+ record.get('costkind63')+
                            "</td><td style=\"padding:5px;\">安全设施及特种设备检测检验支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind7') + "<br/>"+record.get('year')+"实际:" + record.get('costkind71')+ "<br/>分包方整体投入计划:"+ record.get('costkind72') +"<br/>分包方整体投入实际:"+ record.get('costkind73')+
                            "</td></tr><tr><td style=\"padding:5px;\">野外应急食品、应急器械、应急药品支出</td><td>" +record.get('year')+"计划:"+ record.get('costkind8') + "<br/>"+record.get('year')+"实际:" + record.get('costkind81')+ "<br/>分包方整体投入计划:"+ record.get('costkind82') +"<br/>分包方整体投入实际:"+ record.get('costkind83')+
                            "</td><td style=\"padding:5px;\">其它与安全生产直接相关的支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind9') + "<br/>"+record.get('year')+"实际:" + record.get('costkind91')+ "<br/>分包方整体投入计划:"+ record.get('costkind92') +"<br/>分包方整体投入实际:"+ record.get('costkind93')+
                            "</tr><tr></td><td style=\"padding:5px;\">"+record.get('year')+"投入计划总计"+"</td><td colspan=\"3\">" + record.get('sum1') + "</td></tr>"+
 		                    "</tr><tr></td><td style=\"padding:5px;\">"+record.get('year')+"投入实际总计"+"</td><td colspan=\"3\">" + record.get('sum2') + "</td></tr>"+
 		                    "</tr><tr></td><td style=\"padding:5px;\">分包方整体投入计划总计</td><td colspan=\"3\">" + record.get('sum3') + "</td></tr>"+
 		                    "</tr><tr></td><td style=\"padding:5px;\">分包方整体投入实际总计</td><td colspan=\"3\">" + record.get('sum4') + "</td></tr>";
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
                	
                	
                	if(title == '项目安全费用汇总统计')
                	{
                		var html_str = "";
                		if(title == '项目安全费用汇总统计')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		    html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('year')+
 		                    "年详细信息</center></h1>"+
 		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
 		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
 		                    "</td><td width=\"15%\" style=\"padding:5px;\">年份</td><td width=\"40%\">" + record.get("year") + 
 		                    "</td></tr><tr><td style=\"padding:5px;\">完善、改造和维护安全防护设施设备支出（不含“三同时” 要求初期投入的安全设施）</td><td>" +record.get('year')+"计划:"+ record.get('costkind0') + "<br/>"+record.get('year')+"实际:" + record.get('costkind01')+ "<br/>项目整体投入计划:"+ record.get('costkind02') +"<br/>项目整体投入实际:"+ record.get('costkind03')+
                            "</td><td style=\"padding:5px;\">配备、 维护、 保养应急救援器材、 设备支出和应急演练支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind1') + "<br/>"+record.get('year')+"实际:" + record.get('costkind11')+ "<br/>项目整体投入计划:"+ record.get('costkind12') +"<br/>项目整体投入实际:"+ record.get('costkind13')+
                            "</td></tr><tr><td style=\"padding:5px;\">开展重大危险源和事故隐患评估、监控和整改支出</td><td>" +record.get('year')+"计划:"+ record.get('costkind2') + "<br/>"+record.get('year')+"实际:" + record.get('costkind21')+ "<br/>项目整体投入计划:"+ record.get('costkind22') +"<br/>项目整体投入实际:"+ record.get('costkind23')+
                            "</td><td style=\"padding:5px;\">安全生产检查、评价（不包括新建、改建、扩建项目 安全评价）、咨询和标准化建设支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind3') + "<br/>"+record.get('year')+"实际:" + record.get('costkind31')+ "<br/>项目整体投入计划:"+ record.get('costkind32') +"<br/>项目整体投入实际:"+ record.get('costkind33')+
                            "</td></tr><tr><td style=\"padding:5px;\">配备和更新现场作业人员安全防护用品支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind4') + "<br/>"+record.get('year')+"实际:" + record.get('costkind41')+ "<br/>项目整体投入计划:"+ record.get('costkind42') +"<br/>项目整体投入实际:"+ record.get('costkind43')+
                            "</td><td style=\"padding:5px;\">安全生产宣传、教育、培训支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind5') + "<br/>"+record.get('year')+"实际:" + record.get('costkind51')+ "<br/>项目整体投入计划:"+ record.get('costkind52') +"<br/>项目整体投入实际:"+ record.get('costkind53')+
                            "</td></tr><tr><td style=\"padding:5px;\">安全生产 生产适用的新技术、 新标准、 新工艺、 新准备的推广应用</td><td>"+record.get('year')+"计划:"+ record.get('costkind6') + "<br/>"+record.get('year')+"实际:" + record.get('costkind61')+ "<br/>项目整体投入计划:"+ record.get('costkind62') +"<br/>项目整体投入实际:"+ record.get('costkind63')+
                            "</td><td style=\"padding:5px;\">安全设施及特种设备检测检验支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind7') + "<br/>"+record.get('year')+"实际:" + record.get('costkind71')+ "<br/>项目整体投入计划:"+ record.get('costkind72') +"<br/>项目整体投入实际:"+ record.get('costkind73')+
                            "</td></tr><tr><td style=\"padding:5px;\">野外应急食品、应急器械、应急药品支出</td><td>" +record.get('year')+"计划:"+ record.get('costkind8') + "<br/>"+record.get('year')+"实际:" + record.get('costkind81')+ "<br/>项目整体投入计划:"+ record.get('costkind82') +"<br/>项目整体投入实际:"+ record.get('costkind83')+
                            "</td><td style=\"padding:5px;\">其它与安全生产直接相关的支出</td><td>"+record.get('year')+"计划:"+ record.get('costkind9') + "<br/>"+record.get('year')+"实际:" + record.get('costkind91')+ "<br/>项目整体投入计划:"+ record.get('costkind92') +"<br/>项目整体投入实际:"+ record.get('costkind93')+
                            "</tr><tr></td><td style=\"padding:5px;\">"+record.get('year')+"投入计划总计"+"</td><td colspan=\"3\">" + record.get('sum1') + "</td></tr>"+
 		                    "</tr><tr></td><td style=\"padding:5px;\">"+record.get('year')+"投入实际总计"+"</td><td colspan=\"3\">" + record.get('sum2') + "</td></tr>"+
 		                    "</tr><tr></td><td style=\"padding:5px;\">项目整体投入计划总计</td><td colspan=\"3\">" + record.get('sum3') + "</td></tr>"+
 		                    "</tr><tr></td><td style=\"padding:5px;\">项目整体投入实际总计</td><td colspan=\"3\">" + record.get('sum4') + "</td></tr>";
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
                	
                	
       	        }
        	}
        });
        panel.add(gridDT);
        container.add(panel).show();
    }

});