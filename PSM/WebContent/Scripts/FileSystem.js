var tn;

Ext.define('FileSystemGrid', {
    requires: [
        'Ext.data.Model',
        'Ext.grid.Panel'
    ],
    toolbar: null,
    selRecs: [],
    createGrid: function (config) {
    	tn = config.table;
        var me = this;
        var projectName = config.projectName;    //所属的项目部
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
		// 项目部制度建设 不同项目部 用项目部名区分
    	var type = tableID == 82 ? projectName : title;
        
        var getSel = function (grid) {
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
        	uploadPanel.store.removeAll();
        	var info_url = 'FileSystemAction!getFileInfo';
        	var delete_url = 'FileSystemAction!deleteOneFile';
			var existFile = selRecs[0].data.Accessory.split('*');
			for(var i = 2; i < existFile.length; i++) {     							
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
       		var deleteAllUrl = "FileSystemAction!deleteAllFile";
       		if(tableID >= 68 && tableID <= 72) {
       			deleteAllUrl = "FileSystemAction!deleteAllFile";
        	} else if (tableID == 73 || tableID == 77 || tableID == 79 || tableID == 210) {
        		deleteAllUrl = "FileSystemAction!deleteAllFile";
        	} else if (tableID == 74 || tableID == 76 || tableID == 78) {
        		deleteAllUrl = "FileSystemAction!deleteAllFile";
        	} 
       		$.getJSON(deleteAllUrl,
    		{	style: style, fileName: fileName, id:ppid},	//Ajax参数
                function (res) {
    				if (!res.success)
    					Ext.Msg.alert("信息", res.msg); 
            });
       	}
       	
       	var DeleteFile = function(action) {
       		var ppid = "";
       		var fileName = null;
  	        fileName = getFileName();
  	        if(action == "addProject"||action == "editProject") {
				if(action == "editProject") {               			
					ppid = selRecs[0].data.ID;
				}		
				deleteFile(style, fileName, ppid);
				uploadPanel.store.removeAll();
  	        }
       	}        
		
		var store_Commonlaw = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'No'},
                     { name: 'Name'},
                     { name: 'FromUnit'},
                     { name: 'EnactDate'},
                     { name: 'ApplyDate'},
                     { name: 'Accessory'},
                     { name: 'Type'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('FileSystemAction!getCommonlawListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title + "&ProjectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });
		var store_Commonfile = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'No'},
                     { name: 'Name'},
                     { name: 'FromUnit'},
                     { name: 'HowFast'},
                     { name: 'FileRequire'},
                     { name: 'WriteOpinion'},
                     { name: 'Accessory'},
                     { name: 'Querypermission'},
                     { name: 'Type'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('FileSystemAction!getCommonfileListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + type + "&ProjectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });
		var store_Commonsystem = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'SendDate'},
                     { name: 'ReceiveDate'},
                     { name: 'No'},
                     { name: 'Name'},
                     { name: 'FromUnit'},
                     { name: 'Urgency'},
                     { name: 'Requirement'},
                     { name: 'Opinion'},
                     { name: 'Accessory'},
                     { name: 'Type'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('FileSystemAction!getCommonsystemListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&ProjectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });
		var store_Prodepartdoc = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'UploadDate'},
                     { name: 'Filename'},
                     { name: 'Accessory'},
                     { name: 'Type'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('FileSystemAction!getProdepartdocListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&ProjectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });		
		var store_Fbsystem = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'UploadDate'},
                     { name: 'Filename'},
                     { name: 'Accessory'},
                     { name: 'FbUnit'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('FileSystemAction!getFbsystemListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&ProjectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });
		
	   //************************yangtong***********************************
		var store_Standmodel = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'one'},
                     { name: 'two'},
                     { name: 'three'},
                     { name: 'modelname'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('FileSystemAction!getStandmodelListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&ProjectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });
		 //************************yangtong***********************************
		
		
		//----------------LZZ-----------------//
		Ext.define('Item', {
			extend: 'Ext.data.Model',
			fields: ['text']
		});
			
		var storeSelperson = new Ext.data.TreeStore({
			model: 'Item',
			root: {
				text: 'Root',
				expanded: true,   
				children:[]
			}
		});
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
					// afterLabelTextTpl: required,	//红色星号
					anchor:'95%',
					name: 'missionname'
				},{
					xtype:'combo',
					fieldLabel: '分数',
					labelAlign: 'left',
					anchor:'95%',
					store:["1","2","3","4","5"],
				  // afterLabelTextTpl: required,	//红色星号
		 
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
					// afterLabelTextTpl: required,	//红色星号
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
//							pnode.cascadeBy(function(node){ //遍历节点,删除已添加的节点                              
//							if(node.get('text').indexOf(parentName)>-1)
//								node.remove();                   
//							});
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
			if (tableID == 75 || tableID == 79 || tableID == 77 || tableID == 210) {
				actionURL = 'MissionAction!alloTask?title=分配任务&missionexp=文件要求&folder='+folder; 
				uploadURL = "UploadAction!execute";
				items = addMissionEditor;
			}
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
		//------------LZZ--------------//
		
		// commonfile 是制度一类的， commonsystem是文件处理一类的
		var items_commonlaw = [{
        	xtype:'textfield',
            fieldLabel: '文件名称',
            labelAlign: 'right',
            anchor:'100%',
            name: 'Name'
        },{
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
                    fieldLabel: '文件号',
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'No'
                },{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '颁布日期',
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'EnactDate'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '发文单位',
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'FromUnit'
                },{
                	xtype:'datefield',
                	format: 'Y-m-d',
                    fieldLabel: '实施日期',
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'ApplyDate'
                },{
                	xtype:'textfield',
                    fieldLabel: '类型',
                    emptyText: title,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Type',
                    hidden: true,
                    hiddenLabel: true
                }]
	        }]
	    },
        	uploadPanel
        ]
		var items_commonfile = [{
        	xtype:'combo',
            fieldLabel: '参考HSE制度',
        	store: Ext.create('Ext.data.Store', {
            	fields: [
       	         	{ name: 'ID'},
                    { name: 'Name'}
	           ],
	           pageSize: 1000,  //页容量1000条数据
	           proxy: {
	               type: 'ajax',
	               url: encodeURI('FileSystemAction!getCommonfileListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + '项目部参考HSE制度' + "&ProjectName=" + projectName),
	               reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
	                   type: 'json', //返回数据类型为json格式
	                   root: 'rows',  //数据
	                   totalProperty: 'total' //数据总条数
	               }
	           }
	       }),
    		displayField: 'Name',
    		valueField: "ID",
            labelAlign: 'right',
            anchor:'100%',
            listeners: {
            	change: function(field, newValue, oldValue, eOpts) {
             		forms.getForm().load({
        				url: 'FileSystemAction!getHSEinfo',
        				params: { ID: newValue },
        				failure: function() { alert('加载远程记录失败，请检查数据结构'); }
        			});
            	}
            }
        },{
        	xtype:'textfield',
            fieldLabel: '制度名称',
            labelAlign: 'right',
            anchor:'100%',
            name: 'Name'
        },{
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
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'ID',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype:'textfield',
                    fieldLabel: '附件',
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },{
	            	xtype:'textfield',
                    fieldLabel: '发文单位',
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'FromUnit'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '文件号',
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'No'
                }]
	        }]
	    },
        	uploadPanel
        ]
		var items_commonsystem = [{
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
                    fieldLabel: '文件名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                },{
                	xtype:'datefield',
					format:"Y-m-d",
                    fieldLabel: '发文日期',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'SendDate'
                },{
                	xtype:'textfield',
                    fieldLabel: '文件号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'No'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
                    fieldLabel: '发文单位',
                    value: title,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'FromUnit'
                },{
                	xtype:'datefield',
					format:"Y-m-d",
                    fieldLabel: '收文日期',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'ReceiveDate'
                },{
                	xtype:'combo',
                    fieldLabel: '紧急程度',
                    store: ["无","急","特急"],
                    value: "无",
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Urgency'
                },{
                	xtype:'textfield',
                    fieldLabel: '类型',
                    emptyText: title,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Type',
                    hidden: true,
                    hiddenLabel: true
                }]
	        }]
	    },{
        	xtype:'textarea',
            fieldLabel: '文件要求',
            labelAlign: 'right',
            anchor:'100%',
            name: 'Requirement'
        },{
        	xtype:'textarea',
            fieldLabel: '签批意见',
            labelAlign: 'right',
            anchor:'100%',
            name: 'Opinion'
        },
        	uploadPanel
        ]
		var items_prodepartdoc = [{
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
        	xtype:'combo',
        	store: ['规章制度','目标计划','会议纪要','重要通知','其他文件'],
            fieldLabel: '文件类型',
            labelWidth: 120,
            labelAlign: 'right',
            anchor:'100%',
            name: 'Type'
        },
        	uploadPanel
        ]
		var items_fbsystem = [{
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
            fieldLabel: '分包单位名称',
            labelWidth: 120,
            labelAlign: 'right',
            anchor:'100%',
            name: 'FbUnit'
        },
        	uploadPanel
        ]
		
		//************************yangtong***********************************
	    var items_Standmodel =[{
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
                    fieldLabel: '一级菜单',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'one'
                },{
                	xtype:'textfield',
                    fieldLabel: '二级菜单',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'two'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '三级菜单',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'three'
                },{
                	xtype:'textfield',
                    fieldLabel: '表单模板名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'modelname'
                }]
	        }]
	    },
        	uploadPanel
        ]
		//************************yangtong***********************************
		
		//按钮函数
		var searchH = function (){
        	var keyword = tbar.getComponent("keyword").getValue().trim();
        	findStr = keyword;
        	if(queryURL.indexOf("?") > 0 ){
        		dataStore.getProxy().url = encodeURI(queryURL + '&findStr=' + findStr + "&ProjectName=" + projectName);
        	}else{
        		dataStore.getProxy().url = encodeURI(queryURL + '?findStr=' + findStr + "&ProjectName=" + projectName);
        	}
        	btnSearchR.enable();
        	dataStore.load({params: { start:0, limit:psize}});
        	bbar.moveFirst();
        }
		var searchR = function(){
        	var keyword = tbar.getComponent("keyword").getValue().trim();
        	findStr = findStr + "," + keyword;
        	if(queryURL.indexOf("?") > 0 ){
        		dataStore.getProxy().url = encodeURI(queryURL + '&findStr=' + findStr + "&ProjectName=" + projectName);
        	}else{
        		dataStore.getProxy().url = encodeURI(queryURL + '?findStr=' + findStr + "&ProjectName=" + projectName);
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
                        		if(fileName.indexOf('.doc')>0||fileName.indexOf('.docx')>0)
                				{               			              				
	                        		if(e.getX()-item.getX()>20)
	                        		{
	                        			window.open("upload\\" + foldname + "\\" + fileName);
	                        		}
	                        		else
	                        		{
	                        			if(fileName.indexOf('.docx')>0)
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
        	 //************************yangtong***********************************
        	if(title=='系统标准表单模板下载'){
        		actionURL = 'FileSystemAction!addStandmodel?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Standmodel;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addStandmodel',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addStandmodel', title: '新增项目', items: [forms]});
        	}
        	 //************************yangtong***********************************
        	else{
		        	if(tableID >= 69 && tableID <= 73) {
		        		actionURL = 'FileSystemAction!addCommonlaw';  
		        		uploadURL = "UploadAction!execute";
		        		items = items_commonlaw;
		        	} else if (tableID == 74 || tableID == 76 || tableID == 78 || tableID == 252 || tableID == 82) {
		        		actionURL = 'FileSystemAction!addCommonfile';  
		        		uploadURL = "UploadAction!execute";
		        		// 需要参考HSE制度
		        		items = tableID == 82 ? items_commonfile : items_commonfile.slice(1);
		        	} else if (tableID == 75 || tableID == 77 || tableID == 79 || tableID == 210) {
		        		actionURL = 'FileSystemAction!addCommonsystem';  
		        		uploadURL = "UploadAction!execute";
		        		items = items_commonsystem;
		        	} else if (title == '项目部发文') {
		        		actionURL = 'FileSystemAction!addProdepartdoc';  
		        		uploadURL = "UploadAction!execute";
		        		items = items_prodepartdoc;
		        	} else if (title == '分包单位制度建设') {
		        		actionURL = 'FileSystemAction!addFbsystem';  
		        		uploadURL = "UploadAction!execute";
		        		items = items_fbsystem;
		        	}
		        	actionURL = actionURL + '?userName=' + user.name + "&userRole=" + user.role + "&ProjectName=" + projectName + "&Type=" + type;
		        	createForm({
		    			autoScroll: true,
			        	bodyPadding: 5,
			        	action: 'addProject',
			        	url: actionURL,
			        	items: items
			        });
		        	uploadPanel.upload_url = uploadURL;
		        	bbar.moveFirst();	//状态栏回到第一页
			        showWin({ winId: 'addProject', title: '新增文件', items: [forms]});
        	}
        }

        var editH = function() {
        	var formItems;
        	var itemURL; 
        	var actionURL;
        	var uploadURL;
        	var items;
        	if (getSel(gridDT)) {
        		if(selRecs.length == 1 ) {
        			
        			 //************************yangtong***********************************
        			 if(title=='系统标准表单模板下载'){
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'FileSystemAction!editStandmodel?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Standmodel;
              
	        			createForm({
	            			autoScroll: true,
	            			action: 'editStandmodel',
	        	        	bodyPadding: 5,
	        	        	url: actionURL,
	        	        	items: items
	        	        });
	        			uploadPanel.upload_url = uploadURL;
	                	bbar.moveFirst();	//状态栏回到第一页
	        	        showWin({ winId: 'editStandmodel', title: '修改项目', items: [forms]});
        			}
        			 //************************yangtong***********************************
	        		 else{
		        			insertFileToList();
		        			if(tableID >= 69 && tableID <= 73) {
		                		actionURL = 'FileSystemAction!editCommonlaw';  
		                		uploadURL = "UploadAction!execute";
		                		items = items_commonlaw;
		                	} else if (tableID == 74 || tableID == 76 || tableID == 78 || tableID == 252 || tableID == 82) {
		                		actionURL = 'FileSystemAction!editCommonfile';  
		                		uploadURL = "UploadAction!execute";
		                		// 需要参考HSE制度
		                		items = tableID == 82 ? items_commonfile : items_commonfile.slice(1);
		                	} else if (tableID == 75 || tableID == 77 || tableID == 79 || tableID == 210) {
		                		actionURL = 'FileSystemAction!editCommonsystem';  
		                		uploadURL = "UploadAction!execute";
		                		items = items_commonsystem;
		                	} else if (title == '项目部发文') {
		                		actionURL = 'FileSystemAction!editProdepartdoc';  
		                		uploadURL = "UploadAction!execute";
		                		items = items_prodepartdoc;
		                	} else if (title == '分包单位制度建设') {
		                		actionURL = 'FileSystemAction!editFbsystem';  
		                		uploadURL = "UploadAction!execute";
		                		items = items_fbsystem;
		                	}
		                	actionURL = actionURL + '?userName=' + user.name + "&userRole=" + user.role + "&ProjectName=" + projectName + "&Type=" + type;
		        			createForm({
		            			autoScroll: true,
		            			action: 'editProject',
		        	        	bodyPadding: 5,
		        	        	url: actionURL,
		        	        	items: items
		        	        });
		        			uploadPanel.upload_url = uploadURL;
		                	bbar.moveFirst();	//状态栏回到第一页
		        	        showWin({ winId: 'editProject', title: '修改文件', items: [forms]});
        			 }
        		}
        		else
        			Ext.Msg.alert('警告', '只能选中一条记录！');	
	        }
        }
        
        var deleteH = function() {        	
        	if(getSel(gridDT)) {
        		Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) {
    				if (buttonID === 'yes') {
    					//************************yangtong***********************************
           			    if(title=='系统标准表单模板下载'){
           			    	$.getJSON(encodeURI("FileSystemAction!deleteStandmodel?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
           			   //************************yangtong***********************************
    					else{
	    					var delete_url = '';
	    					if (tableID >= 69 && tableID <= 73) {
	    						delete_url = 'FileSystemAction!deleteCommonlaw';
	                    	} else if (tableID == 74 || tableID == 76 || tableID == 78 || tableID == 252 || tableID == 82) {
	                    		delete_url = 'FileSystemAction!deleteCommonfile';
	                    	} else if (tableID == 75 || tableID == 77 || tableID == 79 || tableID == 210) {
	                    		delete_url = 'FileSystemAction!deleteCommonsystem';
	                    	} else if (title == '项目部发文') {
	                    		delete_url = 'FileSystemAction!deleteProdepartdoc';
	                    	} else if (title == '分包单位制度建设') {
	                    		delete_url = 'FileSystemAction!deleteFbsystem';
	                    	}
	    					$.getJSON(encodeURI(delete_url + "?userName=" + user.name + "&userRole=" + user.role),
	    							{id: keyIDs.toString()},	//Ajax参数
	                                function (res) {
	    								if (res.success) {
	                                        //重新加载store
	    									dataStore.load({ params: { start: 0, limit: psize } });
	    									bbar.moveFirst();	//状态栏回到第一页
	                                    }
	                                    else 
	                                    	Ext.Msg.alert("信息", res.msg);
	                                });
    					}
                     }
    			})
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
	            		$.getJSON('FileSystemAction!importExcel', { fileName: fileName, type : type },
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
        var btnImport = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '文件导入',
            icon: "Images/ims/toolbar/extexcel.png",
            handler: importH
        })
        //----------LZZ------------------//
        var btnAllotask = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '任务分配',
            icon: "Images/ims/toolbar/view.png",
           	disabled: true,
            handler: Allotask
        })       
        //----------LZZ---------------//
        //建立工具栏
        var tbar = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        })

        //------------邹文俊修改---------------//
        if (tableID >= 69 && tableID <= 73) {	
        	dataStore = store_Commonlaw;
        	dataStore.load();
        	queryURL = 'FileSystemAction!getCommonlawListSearch?userName=' + user.name + "&userRole=" + user.role;
        	column = [
        		{ text: '序号', xtype: 'rownumberer',width: 40,sortable: false},	
        	    { text: title, dataIndex: 'Name', align: 'center', width: 300},
        	    { text: '颁发日期', dataIndex: 'EnactDate', align: 'center', width: 150},
        	    { text: '实施日期', dataIndex: 'ApplyDate', align: 'center', width: 150},
        	    { text: '发布单位', dataIndex: 'FromUnit', align: 'center', width: 200},
        	    { text: '文号', dataIndex: 'No', align: 'center', width: 200}
        	]
    		tbar.add(btnAdd);
    		tbar.add(btnEdit);
    		tbar.add(btnDel);
    		tbar.add("-");
    		tbar.add(textSearch);
    		tbar.add(btnSearch);
    		tbar.add(btnSearchR);
    		tbar.add(btnScan);
    		tbar.add(btnImport);
    		
    		if(user.role === '项目部人员') {
    			tbar.remove(btnAdd);
    			tbar.remove(btnEdit);
    			tbar.remove(btnDel);
    			tbar.remove(btnImport);
    		}
    		
    		
        } else if (tableID == 74 || tableID == 76 || tableID == 78 || tableID == 252 || tableID == 82) {
        	dataStore = store_Commonfile;
        	dataStore.load();
        	queryURL = 'FileSystemAction!getCommonfileListSearch?userName=' + user.name + "&userRole=" + user.role + "&type=" + type;
        	column = [
        		{ text: '序号', xtype: 'rownumberer',width: 40,sortable: false},	
        	    { text: '制度名称', dataIndex: 'Name', align: 'center', width: 300},
        	    { text: '发布单位', dataIndex: 'FromUnit', align: 'center', width: 200},
        	    { text: '文号', dataIndex: 'No', align: 'center', width: 200}
        	]
      		tbar.add(btnAdd);
      		tbar.add(btnEdit);
      		tbar.add(btnDel);
      		tbar.add("-");
      		tbar.add(textSearch);
      		tbar.add(btnSearch);
      		tbar.add(btnSearchR);
      		tbar.add(btnScan);
    		tbar.add(btnImport);
    		if(user.role === '项目部人员' && tableID != 82) {
    			tbar.remove(btnAdd);
    			tbar.remove(btnEdit);
    			tbar.remove(btnDel);
    			tbar.remove(btnImport);
    		}
        } else if (tableID == 75 || tableID == 77 || tableID == 79 || tableID == 210) {
        	dataStore = store_Commonsystem;
        	dataStore.load();
        	queryURL = 'FileSystemAction!getCommonsystemListSearch?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;
        	column = [
          	    { text: '序号',xtype: 'rownumberer',width: 50,sortable: false},	
          	    { text: '发文日期', dataIndex: 'SendDate', align: 'center', width: 100},
          	    { text: '收文日期', dataIndex: 'ReceiveDate', align: 'center', width: 100},
          	    { text: '文件号', dataIndex: 'No', align: 'center', width: 100},
          	    { text: '文件名称', dataIndex: 'Name', align: 'center', width: 150},
          	    { text: '发文单位', dataIndex: 'FromUnit', align: 'center', width: 150},
          	    { text: '紧急程度', dataIndex: 'Urgency', align: 'center', width: 150},
          	    { text: '文件要求', dataIndex: 'Requirement', align: 'center', width: 150},
          	    { text: '签批意见', dataIndex: 'Opinion', align: 'center', width: 150}
          	]
      		tbar.add(btnAdd);
      		tbar.add(btnEdit);
      		tbar.add(btnDel);
      		tbar.add("-");
      		tbar.add(textSearch);
      		tbar.add(btnSearch);
      		tbar.add(btnSearchR);
      		tbar.add(btnScan);
      		tbar.add(btnAllotask);
      		/*if(user.role === '项目部人员')
      			tbar.remove(btnAllotask);*/
      		if(user.role === '项目部人员') {
    			tbar.remove(btnAdd);
    			tbar.remove(btnEdit);
    			tbar.remove(btnDel);
    			tbar.remove(btnImport);
    		}
        } else if (title == '项目部发文') {
        	dataStore = store_Prodepartdoc;
        	dataStore.load();
        	queryURL = 'FileSystemAction!getProdepartdocListSearch?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;
        	column = [
          	    { text: '序号',xtype: 'rownumberer',width: 50,sortable: false},	
          	    { text: '类型', dataIndex: 'Type', align: 'center', width: 200},
          	    { text: '文件名', dataIndex: 'Filename', align: 'center', width: 350},
          	    { text: '上传日期', dataIndex: 'UploadDate', align: 'center', width: 250},
        	    { text: '所属项目', align: 'center', width: 200, renderer: function (value, meta, record) {
						return	projectName;
	        	    }
				}
          	]
      		tbar.add(btnAdd);
      		tbar.add(btnEdit);
      		tbar.add(btnDel);
      		tbar.add("-");
      		tbar.add(textSearch);
      		tbar.add(btnSearch);
      		tbar.add(btnSearchR);
      		tbar.add(btnScan);
      		
      		
        } else if (title == '分包单位制度建设') {
        	dataStore = store_Fbsystem;
        	dataStore.load();
        	queryURL = 'FileSystemAction!getFbsystemListSearch?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;
        	column = [
          	    { text: '序号',xtype: 'rownumberer',width: 50,sortable: false},	
          	    { text: '分包单位名称', dataIndex: 'FbUnit', align: 'center', width: 200},
          	    { text: '文件名', dataIndex: 'Filename', align: 'center', width: 350},
          	    { text: '上传日期', dataIndex: 'UploadDate', align: 'center', width: 250},
        	    { text: '所属项目', align: 'center', width: 200, renderer: function (value, meta, record) {
						return	projectName;
	        	    }
				}
          	]
      		tbar.add(btnAdd);
      		tbar.add(btnEdit);
      		tbar.add(btnDel);
      		tbar.add("-");
      		tbar.add(textSearch);
      		tbar.add(btnSearch);
      		tbar.add(btnSearchR);
      		tbar.add(btnScan);
        }
        //************************yangtong***********************************
        else if(title=='系统标准表单模板下载') 
        {
        	store_Standmodel.load();
        	dataStore = store_Standmodel;
        	queryURL = 'FileSystemAction!getStandmodelListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		        { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
   		                { text: '一级菜单', dataIndex: 'one', align: 'center', width: 200},
   	                    { text: '二级菜单', dataIndex: 'two', align: 'center', width: 200},
   	                    { text: '三级菜单', dataIndex: 'three', align: 'center', width: 200},
	                    { text: '表单模板名称', dataIndex: 'modelname', align: 'center', width: 300}
	                    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add(btnScan);
    			if (user.role != '项目部人员') {
            		tbar.add("-");
            		tbar.add(btnAdd);
            		tbar.add(btnEdit);
            		tbar.add(btnDel);
        			tbar.add(btnAllotask);
    			}
        }
        //************************yangtong***********************************
        //------------邹文俊修改---------------//
        
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
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				
                				
                				case "addStandmodel":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					alert(fileName);
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editStandmodel":{
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
          	                		Ext.Msg.alert('警告',action.result.msg);
          	                	}
      	                	})
      	                	this.up('window').close();  	
      	                }
      	                else{//forms.form.isValid() == false
      	                	if(config.action == "addProject" || config.action == "editProject") {
                    			var brief = forms.getForm().findField('WritetOpinion');
                    			var brieftext = brief.getValue();
                    			if(brieftext.length>500)
                    				Ext.Msg.alert('警告','专利简介不能超过500个字符！');
                    			else
                    				Ext.Msg.alert('警告','请完善信息！');
                    		}
      	                	else
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
                		if (config.action == "editStandmodel") {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据 
                        }
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
            if (config.action == 'editProject') {
            	selRecs = [];  //清空数组
                selRecs = gridDT.getSelectionModel().getSelection();
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据
            }
            if(config.action == 'alloTask') {
               var selRecs = gridDT.getSelectionModel().getSelection();
    		  // 设置表单初始值 		  
    		  forms.getForm().findField('missionname').setValue(selRecs[0].data.Name);
        	}
            
            if (config.action == 'editStandmodel') {
            	selRecs = [];  //清空数组
                selRecs = gridDT.getSelectionModel().getSelection();
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
        				btnAllotask.enable();
        				btnEdit.enable();        			
        				btnScan.enable();
        			} else {
        				btnAllotask.disable();
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
                'celldblclick': function (self, td, cellIndex, record, tr, rowIndex) {
                	if(tableID >= 68 && tableID <= 73) {
                		var record = dataStore.getAt(rowIndex);
             			var Num = rowIndex+1;
             			//alert(record.get('pName'));
             			var html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">文件名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                        \<tr><td style=\"padding:5px;\">文件号</td><td>" + record.get('No') + "</td><td style=\"padding:5px;\">发文单位</td><td>" + record.get('FromUnit') + "</td></tr>\
                        \<tr><td style=\"padding:5px;\">颁发日期</td><td>" + record.get('EnactDate') + "</td><td style=\"padding:5px;\">实施日期</td><td>" + record.get('ApplyDate') + "</td></tr>";
                    	html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
        		       
      		        var misfile = record.get('Accessory').split('*');
//      		       if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
      		        	height = 500;
      		        	width = 800;
        		       
        		       for(var i = 2;i<misfile.length;i++){
       		       	
       		       	scanfileName = getScanfileName(misfile[i]); 
       		       	displayfileName = misfile[i];
       		       	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
       		       }
      		       
       		        html_str += "</table>";                		
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
                	} else if (tableID == 75 || tableID == 77 || tableID == 79 || tableID == 210) {
                		var html_str = "";
                		var record = dataStore.getAt(rowIndex);
             		   var Num = rowIndex+1;
             		   html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">文件名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                        \<tr><td style=\"padding:5px;\">文件号</td><td>" + record.get('No') + "</td><td style=\"padding:5px;\">发文单位</td><td>" + record.get('FromUnit') + "</td></tr>";
						
                      //\<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
             		  html_str+="<tr><td style=\"padding:5px;\">文件要求</td><td  colspan=\"3\">" + record.get('Requirement') + "</td>";
             		  html_str+="<tr><td style=\"padding:5px;\">签批意见</td><td  colspan=\"3\">" + record.get('Opinion') + "</td>";
             		  html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
        		       
      		        var misfile = record.get('Accessory').split('*');
//      		       var foldermis;
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
                	} else if (tableID == 74 || tableID == 76 || tableID == 78 || tableID == 82 || tableID == 252) {
                		var html_str = "";
                		var record = dataStore.getAt(rowIndex);
             			var Num = rowIndex+1;
             		   //alert(record.get('pName'));
             			html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">文件名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                        \<tr><td style=\"padding:5px;\">文件号</td><td>" + record.get('No') + "</td><td style=\"padding:5px;\">发文单位</td><td>" + record.get('FromUnit') + "</td></tr>";
                    	html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
        		       
      		        var misfile = record.get('Accessory').split('*');
//      		       var foldermis;
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
                	} else if (title == '项目部发文' || title == '分包单位制度建设') {
                		var record = dataStore.getAt(rowIndex);
            			var accessory = record.get('Accessory');
            			var array = accessory.split("*");
            			if (array.length < 3) return;
            			var foldName = array[0]+"\\"+array[1];
            			var fileName = array[array.length-1];
            			window.open("upload\\" + foldName + "\\" + fileName);
       		       	return;       		       	
       		       }
                	
                	
                	 
                	if(title == '系统标准表单模板下载')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '系统标准表单模板下载')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">一级菜单</td ><td>" + record.get("one") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">二级菜单</td><td width=\"40%\">" + record.get("two")+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">三级菜单</td ><td>" + record.get("three") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">表单模板名称</td><td width=\"40%\">" + record.get("modelname")+ "</td></tr>";
//                                        "</td></tr><tr><td style=\"padding:5px;\">建设内容</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('BuildContent') + "</td></tr>";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
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
       	        }
        	}
        });
        panel.add(gridDT);
        container.add(panel).show();
    }
});