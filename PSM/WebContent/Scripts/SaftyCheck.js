var tn;
var forms = [];
var store_saftyproblem = Ext.create('Ext.data.Store', {
	fields: [
             { name: 'showkind'}
    ],
    proxy: {
        type: 'ajax',
        url: encodeURI('BasicInfoAction!getSaftyproblemListDef?'),
        reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
            type: 'json', //返回数据类型为json格式
            root: 'rows',  //数据
            totalProperty: 'total' //数据总条数
        }
    },
    autoLoad: true //即时加载数据
});


Ext.define('SaftyCheckGrid', {
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
		

		
		
		
		//在uploadPanel中添加已有文件列表
		var insertFileToList = function()
      	{
        	var info_url = '';
        	var delete_url = '';
        	if(title == '上级检查问题整改及回复'||title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查')
        	{
        		info_url = 'SaftyCkeckAction!getFileInfo';
        		delete_url = 'SaftyCkeckAction!deleteOneFile';
        	}
        	else if(title=='项目部安全检查计划'||title=='项目部年度安全检查计划'||title=='分包方年度安全检查计划'||title=='隐患排查治理年度工作方案'){
        		info_url = 'SaftyCkeckAction!getFileInfo';
        		delete_url = 'SaftyCkeckAction!deleteOneFile';
        	}
        	else if(title=='分包方隐患排查治理台账'||title=='分包方隐患排查治理工作方案'||title=='安全生产目标检查与纠偏'||title=='分包方目标管理'){
        		info_url = 'SaftyCkeckAction!getFileInfo';
        		delete_url = 'SaftyCkeckAction!deleteOneFile';
        	}
			var existFile = selRecs[0].data.Accessory.split('*');
			for(var i = 2;i<existFile.length;i++)
			{        							
				var size;
				var fileName = existFile[i];
				var info_url = 'SaftyCkeckAction!getFileInfo';
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
       		if(title == '上级检查问题整改及回复'||title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查'||title=='对分包方的检查考核'||title=='项目部安全检查计划'||title=='项目部年度安全检查计划'||title=='安全生产目标检查与纠偏'||title=='分包方目标管理')
       		{
       			deleteAllUrl = "SaftyCkeckAction!deleteAllFile";
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
        
      //----------------任务分配-----------------//
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
		
		var btnAllotask = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '任务分配',
            icon: "Images/ims/toolbar/view.png",
           	disabled: true,
            handler: Allotask
        })
		//------------任务分配--------------//
        
        
    var items_dispatch = [{
	    	xtype: 'container',
	        anchor: '100%',
	        layout: 'hbox',
	        items:[{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	             xtype:'textfield',
                 fieldLabel: '编号',
                 labelAlign: 'left',
                 anchor:'95%',
                 name: 'ID',
                 hidden: true,
                 hiddenLabel: true
                },{
	            	xtype:'textfield',
	                fieldLabel: '检查时间',
//	                labelWidth: 120,
	                labelAlign: 'right',
	                anchor:'90%',
	                name: 'checktime'
	            },{
	            	xtype:'textfield',
	                fieldLabel: '检查类型',
//	                labelWidth: 120,
	                labelAlign: 'right',
	                anchor:'90%',
	                name: 'checktype'
	            },{
	            	xtype:'textfield',
	                fieldLabel: '计划完成时间',
//	                labelWidth: 120,
	                labelAlign: 'right',
	                anchor:'90%',
	                name: 'timeline'
	            }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'textfield',
	                fieldLabel: '检查单位',
//	                labelWidth: 120,
	                labelAlign: 'right',
	                anchor:'90%',
	                name: 'checkunit'
	            },{
	            	xtype:'textfield',
	                fieldLabel: '检查人员',
	                //labelWidth: 120,
	                labelAlign: 'right',
	                anchor:'90%',
	                name: 'checkperson'
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
                height:300,
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
                     pnode.cascadeBy(function(node){ //遍历节点,删除已添加的节点                                              
                    	 node.remove();                   
                	 });  		    
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
                height:300,
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
	    }]  
        
        
        
        var chkdispatch = function(){
        	var actionURL;
        	var items;      	
        	actionURL = 'SaftyCkeckAction!chkdispatch?userRole=' + user.role;  
        	items = items_dispatch;
        	createForm({
    			autoScroll: true,
	        	bodyPadding: 5,
	        	action: 'dispatchChk',
	        	url: actionURL,
	        	items:items            
	        });
        	bbar.moveFirst();	//状态栏回到第一页
	        showWin({ winId: 'dispatchChk', title: '分配任务', items: [forms]});
        }
        
        
        
        
        
    function scanH (item){
	 	var hasfinish = 1;
	 	var foldname;
    	var selRecs = gridDT.getSelectionModel().getSelection();
    	var accessory;
    	var finishacc;
    	var finisharry = [];						
		fileMenu.removeAll();
		 if(tableID == 160 )
		{
			hasfinish = 2;
			accessory = selRecs[0].data.Accessory;;
			finishacc = selRecs[0].data.solveAcc;
			finisharry = finishacc.split("*");
		}
		else
		{
			accessory = selRecs[0].data.Accessory;;
		}
		var array = accessory.split("*");
		
		//若从数据库中取出为空，则表示没有附件
		if((array.length == 1 || array.length==0)&(finisharry.length==1||finisharry.length == 0) )
		{
			var menuItem = Ext.create('Ext.menu.Item', {
            	text: '暂无文件'
            	
            });
            fileMenu.add(menuItem);
		}//数据库中有附件，添加到文件下拉菜单中
		else{
			for(var j = 0; j<hasfinish; j++){
				if(j == 1)
				{						
					fileMenu.add('-');
					array = finisharry;
				}
				foldname = array[0]+"\\"+array[1];
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
    }
        
        
 
        
        var addH = function(){
        	var actionURL;
        	var uploadURL;
        	var items;
        	//*********************
        	problemNum = 1;
        	
        	if(title == '上级检查问题整改及回复'||title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查'||title=='安全生产目标检查与纠偏'||title=='分包方目标管理')
        	{	   
        		//***************
        		 store_saftyproblem.load();
        		 store_readily_shoot.load();
//        		 alert(store_saftyproblem.getCount());
        		//***************
        		actionURL = 'SaftyCkeckAction!addSaftycheck?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
//        		uploadURL = "UploadAction!execute";
        		uploadURL = encodeURI("UploadAction!execute?table=安全检查记录表&tableitem=checktype|checktime&item=检查类型|检查时间&end=检查时间|Tab");
        		
        		if(title=='上级检查问题整改及回复'){
        			items = items_saftycheck;
        		}else{
        			items = items_saftycheckwhthouttype;
        		}
        		
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addSaftycheck',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addSaftycheck', title: '新增项目', items: [forms]});
        	}  
        	
        	
        	if(title=='分包方隐患排查治理台账')
        	{
        		actionURL = 'SaftyCkeckAction!addTaizhangfb?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Taizhangfb;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addTaizhangfb',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addTaizhangfb', title: '新增项目', items: [forms]});
        	}
        	
        	
        	if(title=='项目安全检查计划')
        	{
        		actionURL = 'SaftyCkeckAction!addSaftycheckplan?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Saftycheckplan;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addSaftycheckplan',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addSaftycheckplan', title: '新增项目', items: [forms]});
        	}
        	
        	if(title=='项目部年度安全检查计划')
        	{
        		actionURL = 'SaftyCkeckAction!addSaftycheckyearplan?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Saftycheckyearplan;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addSaftycheckyearplan',
    	        	url: actionURL,
    	        	items: items
    	        });
//            	alert(projectName);
//            	alert(forms.getForm().findField('ProjectName'));
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addSaftycheckyearplan', title: '新增项目', items: [forms]});
        	}
        	
        	
        	if(title=='分包方年度安全检查计划')
        	{
        		actionURL = 'SaftyCkeckAction!addSaftycheckyearplanfb?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Saftycheckyearplanfb;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addSaftycheckyearplanfb',
    	        	url: actionURL,
    	        	items: items
    	        });
//            	alert(forms.getForm().findField('ProjectName'));
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addSaftycheckyearplanfb', title: '新增项目', items: [forms]});
        	}
        	
        	if(title=='隐患排查治理年度工作方案')
        	{
        		actionURL = 'SaftyCkeckAction!addSaftycheckyinhuanpc?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Saftycheckyinhuanpc;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addSaftycheckyinhuanpc',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addSaftycheckyinhuanpc', title: '新增项目', items: [forms]});
        	}
        	
        	
        	if(title=='分包方危险源管理')
        	{
        		actionURL = 'SaftyCkeckAction!addRiskfenbao?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Riskfenbao;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addRiskfenbao',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addRiskfenbao', title: '新增项目', items: [forms]});
        	}
        	
        	
        	if(title=='项目危险因素（危险源）辨识')
        	{
        		actionURL = 'SaftyCkeckAction!addRiskprodanger?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Riskprodanger;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addRiskprodanger',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addRiskprodanger', title: '新增项目', items: [forms]});
        	}
        	
        	
        	if(title=='安全评估')
        	{
        		actionURL = 'SaftyCkeckAction!addRisksafepg?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Risksafepg;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addRisksafepg',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addRisksafepg', title: '新增项目', items: [forms]});
        	}
        	
        	if(title=='分包方隐患排查治理工作方案')
        	{
        		actionURL = 'SaftyCkeckAction!addFenbaoyinhuanpczlgzfa?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Fenbaoyinhuanpczlgzfa;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addFenbaoyinhuanpczlgzfa',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addFenbaoyinhuanpczlgzfa', title: '新增项目', items: [forms]});
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
        				
        			if(title == '上级检查问题整改及回复'||title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查'||title=='安全生产目标检查与纠偏'||title=='分包方目标管理')
                	{
        				//***************
               		    store_saftyproblem.load();
               		 store_readily_shoot.load();
               		   //***************
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editSaftycheck?userName=' + user.name + "&userRole=" + user.role + "&type=" + title+"&proNum"+problemNum;  
        				if(title=='上级检查问题整改及回复'){
                			items = items_saftycheck;
                		}else{
                			items = items_saftycheckwhthouttype;
                		}
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftycheck',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			
        			if(title=='上级检查问题整改及回复'){
        				EditShow({action: 'editSaftycheck'});
            			EditShowadv({action: 'editSaftycheck'});
            		}else{
            			EditShow({action: 'editSaftycheck'});
            		}
        			
        			
//        			forms.getForm().findField('problem1').setValue('456');
        			
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftycheck', title: '修改项目', items: [forms]});
        			}
        			
        			
        			if(title=='分包方隐患排查治理台账')
        			{
        				//***************
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editTaizhangfb?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Taizhangfb;
              
        			createForm({
            			autoScroll: true,
            			action: 'editTaizhangfb',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editTaizhangfb', title: '修改项目', items: [forms]});
        			}
        			
        			if(title == '项目安全检查计划')
        			{
        				//***************
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editSaftycheckplan?userName=' + user.name + "&userRole=" + user.role;  
        				
        				items = items_Saftycheckplan;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftycheckplan',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftycheckplan', title: '修改项目', items: [forms]});
        			}
        			
        			
        			if(title == '项目部年度安全检查计划')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editSaftycheckyearplan?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Saftycheckplan;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftycheckyearplan',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftycheckyearplan', title: '修改项目', items: [forms]});
        			}
        			
        			if(title == '分包方年度安全检查计划')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editSaftycheckyearplanfb?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Saftycheckyearplanfb;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftycheckyearplanfb',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftycheckyearplanfb', title: '修改项目', items: [forms]});
        			}
        			
        			
        			if(title == '隐患排查治理年度工作方案')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editSaftycheckyinhuanpc?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Saftycheckyinhuanpc;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftycheckyinhuanpc',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftycheckyinhuanpc', title: '修改项目', items: [forms]});
        			}
        			
        			
        			if(title == '分包方危险源管理')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editRiskfenbao?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Riskfenbao;
              
        			createForm({
            			autoScroll: true,
            			action: 'editRiskfenbao',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editRiskfenbao', title: '修改项目', items: [forms]});
        			}
        			
        			
        			if(title == '项目危险因素（危险源）辨识')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editRiskprodanger?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Riskprodanger;
              
        			createForm({
            			autoScroll: true,
            			action: 'editRiskprodanger',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editRiskprodanger', title: '修改项目', items: [forms]});
        			}
        			
        			
        			if(title == '安全评估')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editRisksafepg?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Risksafepg;
              
        			createForm({
            			autoScroll: true,
            			action: 'editRisksafepg',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editRisksafepg', title: '修改项目', items: [forms]});
        			}
        			
        			
        			if(title == '分包方隐患排查治理工作方案')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'SaftyCkeckAction!editFenbaoyinhuanpczlgzfa?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Fenbaoyinhuanpczlgzfa;
              
        			createForm({
            			autoScroll: true,
            			action: 'editFenbaoyinhuanpczlgzfa',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editFenbaoyinhuanpczlgzfa', title: '修改项目', items: [forms]});
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
    					if(title == '上级检查问题整改及回复'||title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查'||title=='对分包方的检查考核'||title=='安全生产目标检查与纠偏'||title=='分包方目标管理')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteSaftycheck?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
    					
    					
    					if(title=='分包方隐患排查治理台账')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteTaizhangfb?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
    					if(title == '项目安全检查计划')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteSaftycheckplan?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
    					
    					if(title == '项目部年度安全检查计划')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteSaftycheckyearplan?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
    					
    					if(title == '分包方年度安全检查计划')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteSaftycheckyearplanfb?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
    					
    					if(title == '隐患排查治理年度工作方案')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteSaftycheckyinhuanpc?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
    					
    					if(title == '分包方危险源管理')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteRiskfenbao?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
    					
    					if(title == '项目危险因素（危险源）辨识')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteRiskfenbao?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
    					
    					if(title == '安全评估')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteRisksafepg?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
    					
    					if(title == '分包方隐患排查治理工作方案')
                    	{$.getJSON(encodeURI("SaftyCkeckAction!deleteFenbaoyinhuanpczlgzfa?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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

         var btnchkdis = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '任务分配',
            icon: "Images/ims/toolbar/view.png",
           	disabled: true,
            handler: chkdispatch
        })
        //建立工具栏
        var tbar = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        })

        
//************************整改问题动态添加代码********************************************
        var store_readily_shoot = Ext.create('Ext.data.Store', {
        	fields: [
        			 { name: 'ID'},
        			 { name: 'url'},
        			 { name: 'prefix'},
        			 { name: 'comment'},
        			 { name: 'project'}
        	],
        	pageSize: psize,  //页容量20条数据
        	proxy: {
        		type: 'ajax',
        		url: encodeURI('HiddenTroubleSolutionAction!getReadilyShootListDef2?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&ProjectName=" + projectName),
        		reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
        			type: 'json', //返回数据类型为json格式
        			root: 'rows',  //数据
        			totalProperty: 'total' //数据总条数
        		}
        	}
        });
        
        
        
        
        var editSel;
        var editProblems;
        var editProblemsNum;
        var problemNum = 1;
        
        //问题添加按钮函数
        function AddZB(e, eOpts) {
        	var fm1 = this.up('container');

//        	problemNum = problemNum+1;
        	problemNum = checkNum();
        	
        	if(problemNum>9){
        		alert("问题数过多!");
        	}else{
//        		var cmp1 = fm1.add({
//                	xtype:'textfield',
//                    fieldLabel: '整改问题'+problemNum,
//                    ID:'整改问题'+problemNum,
//                    labelAlign: 'right',
//                    width:685,
//                    columnWidth : .57,
//                    margin:'6 0 0 0',
//                    anchor:'90%',
//                    allowBlank: false,
//                    name: 'problem'+problemNum
//                });
        		var cmp1 = fm1.add({
                	xtype:'combo',
                    fieldLabel: '整改问题'+problemNum,
                    ID:'整改问题'+problemNum,
                    store: store_readily_shoot,
                    valueField: 'comment',
                    displayField: 'comment',
	                 triggerAction: 'all',
	                 emptyText: '',
//	                 blankText: '请选择政治面貌',
	                 editable: true,
	                 mode: 'remote',
                    labelAlign: 'right',
                    width:685,
                    columnWidth : .57,
                    margin:'6 0 0 0',
                    anchor:'90%',
                    allowBlank: false,
                    name: 'problem'+problemNum
                });
        		
//        		margin: 20px 40px 60px 80px;（上20px；右40px；下60px；左80px。）
        		//************************************
        		var cmp3 = fm1.add({
                	xtype:'combo',
                    fieldLabel: '类别',
                    labelWidth: 30,
                    store: store_saftyproblem,
                    valueField: 'showkind',
                    displayField: 'showkind',
	                 triggerAction: 'all',
	                 emptyText: '请选择',
	                 allowBlank: false,
//	                 blankText: '请选择政治面貌',
	                 editable: true,
	                 mode: 'remote',
//                    labelWidth: 120,
                    labelAlign: 'right',
//                    anchor:'20%',
                    margin:'6 0 0 1',
                    columnWidth : .25,
                    minListWidth:400,
                    name: 'kind'+problemNum
                });
        		
        		var cmp4 = fm1.add({
        	            	xtype:'combo',
        	            	queryMode:'local',
        	            	editable:false,
        	            	labelAlign: 'right',
        	                anchor:'95%',
        	            	store:new Ext.data.ArrayStore({
        	            	   fields : ['id','name'],
        	            	   data : [['一般', '一般'], ['重大', '重大']]
        	            	}),
        	            	valueField:'name',
        	            	displayField:'id',
        	            	triggerAction:'all',
        	            	autoSelect:true,
        	            	 allowBlank: false,
        	            	 margin:'6 0 0 1',
        	                columnWidth : .08,
        	                listeners: {
        	                    afterRender: function(combo,record) {
        	                    	var firstValue  = this.store.getAt(0).get('id');
        	                  　　                                         combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示
        	                    }
        	                },
        	                name:'degree'+problemNum
        	            });
        		//****************************************
        		var cmp2 = fm1.add({
                	xtype:'button',
                    text: '-',
                    ID:problemNum,
                    labelAlign: 'left',
                    labelWidth: 5,
                    anchor:'5%',
                    margin:'6 0 0 10',
                    columnWidth : .05,
                    name: 'delproblem'+problemNum,
                    listeners: { click: function (btn) {
                    	
                        	var textgf = forms.getForm().findField('problem1');
                      	    var fm1 = textgf.up('container');
                      	    fm1.remove(forms.getForm().findField('problem'+btn.name.substring(10,11)));
                      	    fm1.remove(forms.getForm().findField('kind'+btn.name.substring(10,11)));
                      	    fm1.remove(forms.getForm().findField('degree'+btn.name.substring(10,11)));
                      	    fm1.remove(btn);
//                      	    refresh();
                      	    refresh2();
                     }
                    }
                });
        		//******************************
        		//此时可能已经存在的name很大为8，然而新加的name为2，即此时name8在name2的上面，
        		//那么refresh遍历的时候是根据name顺序为先2后8，那么就会先把2的fieldlabel改为比8的fieldlabel小，但是2却在8的下面
        		refresh2();
        		
        		
        	}
        }
        
        
        //查看1-10中的数字还有哪个没有用
        function checkNum()
        {
        	var array = new Array(0,0,0,0,0,0,0,0,0,0,0,0,0,0);
        	
        	for(var i = 1;i<=9;i++)
     	   {
     		  tem = forms.getForm().findField('problem'+i);
     		  if(tem!=null)
     		  {
     			  array[i] = 1;
     		  }
     	   }
           var flag = 11;
           for(var k = 1;k<=9;k++)
           {
        	   if(array[k]==0){
        		   return k;
        	   }
           }
           return flag;
        }
        
        
       function refresh(){
    	   var newNum = 2;
    	   for(var i = 2;i<=9;i++)
    	   {
    		  tem = forms.getForm().findField('problem'+i);
    		  if(tem!=null)
    		  {
    			  tem.setFieldLabel("整改问题"+newNum);
    			  newNum++;
    		  }
    	   }
//    	   problenm = newNum;
       }
       
       function refresh2(){
    	   var newNum = 2;
    	   var textgf = forms.getForm().findField('problem1');
    	   var tem = textgf.nextSibling();
    	   while(tem!=null)
    	   {
    		   if(tem.name.substring(0,7)=="problem")
    		   {
//    			   alert(tem.name);
    			   tem.setFieldLabel("整改问题"+newNum);
    			   newNum++;
    		   }
    		  tem = tem.nextSibling();
    	   }
       }
        
      function  EditShow(config)
      {
//    	  alert("进来了！！");
    	  if(config.action=="editSaftycheck")
    	  {
    		  getEditSel(gridDT);
    	  }
      }
      
      //delproblem,problem
      function clearall()
      {
    	  var textgf = forms.getForm().findField('degree1');
    	  var parent = textgf.up('container');
    	  var tmp = textgf.nextSibling();
//    	 alert(parent.name+tmp.name);
    	  while(tmp!=null)
    	  {
    		  var next = tmp.nextSibling();
//    		  alert(tmp.name.substring(0,7));
    		  if(tmp.name.substring(0,7)=="delprob"||tmp.name.substring(0,7)=="problem"||tmp.name.substring(0,4)=="kind"||tmp.name.substring(0,6)=="degree"){
    			  parent.remove(tmp);
    		  }
    		  tmp = next;
    	  }
      }
        
     
      /**
       * 获得选择
       */
      var   getEditSel = function (grid) 
      {
    	  editSel = [];  //清空数组
    	  editSel = grid.getSelectionModel().getSelection();
//    	  editProblems = editSel[0].data.problem.split("*");
//    	  editProblemsNum = editSel[0].data.pronum;
    	  editProblems = selRecs[0].data.problem.split("*");
    	  editProblemsNum = selRecs[0].data.pronum;
//    	  alert(editProblemsNum);
    	  editProKinds = selRecs[0].data.prokind.split("*");
    	  editProdegrees = selRecs[0].data.prodegree.split("*");
    	  if(editProblemsNum==0)
    	  {
    		  
    	  }
    	  else if(editProblemsNum==1)
    	  {
    		  forms.getForm().findField('problem1').setValue(editProblems[0]);
    		  forms.getForm().findField('kind1').setValue(editProKinds[0]);
    		  forms.getForm().findField('degree1').setValue(editProdegrees[0]);
    	  }
    	  else
    	  {
    		  forms.getForm().findField('problem1').setValue(editProblems[0]);
    		  forms.getForm().findField('kind1').setValue(editProKinds[0]);
    		  forms.getForm().findField('degree1').setValue(editProdegrees[0]);
    		  problemNum = 1;
    		  for(var j = 2;j<=editProblemsNum;j++)
    		  {
    			  addEdit();
    			  forms.getForm().findField('problem'+j).setValue(editProblems[j-1]);
    			  forms.getForm().findField('kind'+j).setValue(editProKinds[j-1]);
    			  forms.getForm().findField('degree'+j).setValue(editProdegrees[0]);
//    			  alert(j);
    		  }
    	  }
      };
      
      
      function addEdit()
      {
    	  var textgf = forms.getForm().findField('problem1');
    	  var fm1 = textgf.up('container');
        	
//        	problemNum = problemNum+1;
    	  problemNum = checkNum();
        	
        	if(problemNum>10){
        		alert("问题数过多!");
        	}else{
//        		var cmp1 = fm1.add({
//                	xtype:'textfield',
//                    //labelWidth: 120,
//                    fieldLabel: '整改问题'+problemNum,
//                    labelAlign: 'right',
////                    width:685,
//                    margin:'6 0 0 0',
//                    columnWidth : .57,
//                    anchor:'90%',
//                    allowBlank: false,
//                    name: 'problem'+problemNum
//                });
        		
        		var cmp1 = fm1.add({
                	xtype:'combo',
                    fieldLabel: '整改问题'+problemNum,
                    ID:'整改问题'+problemNum,
                    store: store_readily_shoot,
                    valueField: 'comment',
                    displayField: 'comment',
	                 triggerAction: 'all',
	                 emptyText: '',
//	                 blankText: '请选择政治面貌',
	                 editable: true,
	                 mode: 'remote',
                    labelAlign: 'right',
                    width:685,
                    columnWidth : .57,
                    margin:'6 0 0 0',
                    anchor:'90%',
                    allowBlank: false,
                    name: 'problem'+problemNum
                });
        		//************************************
        		var cmp3 = fm1.add({
                	xtype:'combo',
                    fieldLabel: '类别',
                    labelWidth: 30,
                    store: store_saftyproblem,
                    valueField: 'showkind',
                    displayField: 'showkind',
	                 triggerAction: 'all',
	                 emptyText: '请选择',
	                 allowBlank: false,
//	                 blankText: '请选择政治面貌',
	                 editable: true,
	                 mode: 'remote',
//                    labelWidth: 120,
                    labelAlign: 'right',
//                    anchor:'20%',
                    margin:'6 0 0 1',
                    columnWidth : .25,
                    minListWidth:400,
                    name: 'kind'+problemNum
                });
        		var cmp4 = fm1.add({
	            	xtype:'combo',
	            	queryMode:'local',
	            	editable:false,
	            	labelAlign: 'right',
	                anchor:'95%',
	            	store:new Ext.data.ArrayStore({
	            	   fields : ['id','name'],
	            	   data : [['一般', '一般'], ['重大', '重大']]
	            	}),
	            	valueField:'name',
	            	displayField:'id',
	            	triggerAction:'all',
	            	autoSelect:true,
	            	 allowBlank: false,
	            	 margin:'6 0 0 1',
	                columnWidth : .08,
	                name:'degree'+problemNum
	            });
        		//****************************************
        		var cmp2 = fm1.add({
                	xtype:'button',
                    text: '-',
                    ID:problemNum,
                    labelAlign: 'right',
                    anchor:'5%',
                    columnWidth : .05,
                    margin:'6 0 0 10',
//                    columnWidth:0.1,
                    name: 'delproblem'+problemNum,
                    listeners: { click: function (btn) {
                        	var textgf = forms.getForm().findField('problem1');
                      	    var fm1 = textgf.up('container');
                      	    fm1.remove(forms.getForm().findField('problem'+btn.name.substring(10,11)));
                      	  fm1.remove(forms.getForm().findField('kind'+btn.name.substring(10,11)));
                      	    fm1.remove(btn);
//                      	    refresh();
                      	    refresh2();
                     }
                    }
                });
        		//******************************
        		//此时可能已经存在的name很大为8，然而新加的name为2，即此时name8在name2的上面，
        		//那么refresh遍历的时候是根据name顺序为先2后8，那么就会先把2的fieldlabel改为比8的fieldlabel小，但是2却在8的下面
        		refresh2();
        	}
      }
      
      

      
      
      function strEdit(str,grid)
      {
    	  editSel = [];  //清空数组
    	  editSel = grid.getSelectionModel().getSelection();
    	  editProblemsNum = editSel[0].data.pronum;
      
    	 var str1 = str.split("*");
    	 var str2;
    	 if(str=="无")
    	 {
    		 return str+"<br/>";
    	 }
    	 if(editProblemsNum==0)
    	 {
    		 
    	 }
    	 else if(editProblemsNum==1)
    	 {
    		 str2 = "1、"+str1+"<br/>";
    	 }
    	 else{
    		 var j;
    		 for(var i = 0;i<editProblemsNum;i++)
        	 {
        		if(i==0)
        		{
        			j = i+1;
        			str2 = j+"、"+str1[i]+"<br/>";
        		}
        		else
        		{
        			j = i+1;
        			str2 = str2+j+"、"+str1[i]+"<br/>";
        		}
        	 }
    	 }
    	 return str2;
    	 
      }

      
      
//**********************************************************************************
      
      
//建议的动态添加************************************************************************
      var editSeladv;
      var editAdvices;
      var editAdvicesNum;
      var AdviceNum = 1;
      
      //问题添加按钮函数
      function AddADV(e, eOpts) {
      	var fm1 = this.up('container');

//      	problemNum = problemNum+1;
      	AdviceNum = checkNumadv();
      	
      	if(AdviceNum>9){
      		alert("建议数过多!");
      	}else{
      		var cmp1 = fm1.add({
              	xtype:'textfield',
                  fieldLabel: '建议'+AdviceNum,
                  ID:'建议'+AdviceNum,
                  labelAlign: 'right',
                  width:685,
                  columnWidth : .9,
                  margin:'6 0 0 0',
                  anchor:'90%',
                  allowBlank: false,
                  name: 'advice'+AdviceNum
              });
      		
//      		margin: 20px 40px 60px 80px;（上20px；右40px；下60px；左80px。）
      		
      		var cmp2 = fm1.add({
              	xtype:'button',
                  text: '-',
                  ID:AdviceNum,
                  labelAlign: 'left',
                  labelWidth: 5,
                  anchor:'5%',
                  margin:'6 0 0 10',
                  columnWidth : .05,
                  name: 'deladvice'+AdviceNum,
                  listeners: { click: function (btn) {
                      	var textgf = forms.getForm().findField('advice1');
                    	    var fm1 = textgf.up('container');
                    	    fm1.remove(forms.getForm().findField('advice'+btn.name.substring(9,10)));
                    	    fm1.remove(btn);
                    	    refresh2adv();
                   }
                  }
              });
      		//******************************
      		//此时可能已经存在的name很大为8，然而新加的name为2，即此时name8在name2的上面，
      		//那么refresh遍历的时候是根据name顺序为先2后8，那么就会先把2的fieldlabel改为比8的fieldlabel小，但是2却在8的下面
      		refresh2adv();
      		
      		
      	}
      }
      
      
      //查看1-10中的数字还有哪个没有用
      function checkNumadv()
      {
      	var array = new Array(0,0,0,0,0,0,0,0,0,0,0,0,0,0);
      	
      	for(var i = 1;i<=9;i++)
   	   {
   		  tem = forms.getForm().findField('advice'+i);
   		  if(tem!=null)
   		  {
   			  array[i] = 1;
   		  }
   	   }
         var flag = 11;
         for(var k = 1;k<=9;k++)
         {
      	   if(array[k]==0){
      		   return k;
      	   }
         }
         return flag;
      }
      
      
     function refresh(){
  	   var newNum = 2;
  	   for(var i = 2;i<=9;i++)
  	   {
  		  tem = forms.getForm().findField('advice'+i);
  		  if(tem!=null)
  		  {
  			  tem.setFieldLabel("建议"+newNum);
  			  newNum++;
  		  }
  	   }
//  	   problenm = newNum;
     }
     
     function refresh2adv(){
  	   var newNum = 2;
  	   var textgf = forms.getForm().findField('advice1');
  	   var tem = textgf.nextSibling();
  	   while(tem!=null)
  	   {  
  		   if(tem.name.substring(0,6)=="advice")
  		   {
//  			   alert(tem.name);
  			   tem.setFieldLabel("建议"+newNum);
  			   newNum++;
  		   }
  		  tem = tem.nextSibling();
  	   }
     }
      
    function  EditShowadv(config)
    {
//  	  alert("进来了！！");
  	  if(config.action=="editSaftycheck")
  	  {
  		getEditSeladv(gridDT);
  	  }
    }
    
    //deladvice,advice
    function clearalladv()
    {
  	  var textgf = forms.getForm().findField('advice1');
  	  var parent = textgf.up('container');
  	  var tmp = textgf.nextSibling();
//  	 alert(parent.name+tmp.name);
  	  while(tmp!=null)
  	  {
  		  var next = tmp.nextSibling();
//  		  alert(tmp.name.substring(0,7));
  		  if(tmp.name.substring(0,6)=="deladv"||tmp.name.substring(0,6)=="advice"){
  			  parent.remove(tmp);
  		  }
  		  tmp = next;
  	  }
    }
      
   
    /**
     * 获得选择
     */
    var   getEditSeladv = function (grid) 
    {
    	editSeladv = [];  //清空数组
    	editSeladv = grid.getSelectionModel().getSelection();
    	editAdvices = selRecs[0].data.advice.split("*");
    	editAdvicesNum = selRecs[0].data.advicenum;
//  	  alert(editProblemsNum);
//    	alert(editAdvices);
  	  if(editAdvicesNum==0)
  	  {
  		  
  	  }
  	  else if(editAdvicesNum==1)
  	  {
  		  forms.getForm().findField('advice1').setValue(editAdvices[0]);
  	  }
  	  else
  	  {
  		  forms.getForm().findField('advice1').setValue(editAdvices[0]);
  		  AdviceNum = 1;
  		  for(var j = 2;j<=editAdvicesNum;j++)
  		  {
  			  addEditadv();
//  			  alert(forms.getForm().findField('advice'+j));
  			  forms.getForm().findField('advice'+j).setValue(editAdvices[j-1]);
//  			  alert(j);
  		  }
  	  }
    };
    
    
    function addEditadv()
    {
  	  var textgf = forms.getForm().findField('advice1');
  	  var fm1 = textgf.up('container');
      	
//      	problemNum = problemNum+1;
  	  AdviceNum = checkNumadv();
      	
      	if(AdviceNum>10){
      		alert("建议过多!");
      	}else{
      		var cmp1 = fm1.add({
              	xtype:'textfield',
                  //labelWidth: 120,
                  fieldLabel: '建议'+AdviceNum,
                  labelAlign: 'right',
//                  width:685,
                  margin:'6 0 0 0',
                  columnWidth : .9,
                  anchor:'90%',
                  name: 'advice'+AdviceNum
              });
      		var cmp2 = fm1.add({
              	xtype:'button',
                  text: '-',
                  ID:AdviceNum,
                  labelAlign: 'right',
                  anchor:'5%',
                  columnWidth : .05,
                  margin:'6 0 0 10',
//                  columnWidth:0.1,
                  name: 'deladvice'+AdviceNum,
                  listeners: { click: function (btn) {
                      	var textgf = forms.getForm().findField('advice1');
                    	    var fm1 = textgf.up('container');
                    	    fm1.remove(forms.getForm().findField('advice'+btn.name.substring(9,10)));
                    	    fm1.remove(btn);
//                    	    refresh();
                    	    refresh2adv();
                   }
                  }
              });
      		//******************************
      		//此时可能已经存在的name很大为8，然而新加的name为2，即此时name8在name2的上面，
      		//那么refresh遍历的时候是根据name顺序为先2后8，那么就会先把2的fieldlabel改为比8的fieldlabel小，但是2却在8的下面
      		refresh2adv();
      	}
    }
    
    

    
    
    function strEditadv(str,grid)
    {
    	editSeladv = [];  //清空数组
    	editSeladv = grid.getSelectionModel().getSelection();
    	editAdvicesNum = editSel[0].data.advicenum;
    
    	if(str=="无")
   	 {
   		 return str+"<br/>";
   	 }
    	
  	 var str1 = str.split("*");
  	 var str2;
  	 if(editAdvicesNum==0)
  	 {
  		 
  	 }
  	 else if(editAdvicesNum==1)
  	 {
  		 str2 = "1、"+str1+"<br/>";
  	 }
  	 else{
  		 var j;
  		 for(var i = 0;i<editAdvicesNum;i++)
      	 {
      		if(i==0)
      		{
      			j = i+1;
      			str2 = j+"、"+str1[i]+"<br/>";
      		}
      		else
      		{
      			j = i+1;
      			str2 = str2+j+"、"+str1[i]+"<br/>";
      		}
      	 }
  	 }
  	 return str2;
  	 
    }
      
//**********************************************************************************
      

    
//    margin: 20px 40px 60px 80px;（上20px；右40px；下60px；左80px。）
    

//    var store_saftyproblem = Ext.create('Ext.data.Store', {
//    	fields: [
//                 { name: 'showkind'}
//        ],
//        proxy: {
//            type: 'ajax',
//            url: encodeURI('BasicInfoAction!getSaftyproblemListDef?'),
//            reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
//                type: 'json', //返回数据类型为json格式
//                root: 'rows',  //数据
//                totalProperty: 'total' //数据总条数
//            }
//        },
//        autoLoad: true //即时加载数据
//    });
     
    
    var checktypecombo = new Ext.data.ArrayStore({
        fields: ['id', 'checktype'],
        data: [[1, '定期安全检查'], 
        	[2, '专项安全检查'], 
        	[3, '春季安全检查'], 
        	[4, '秋季安全检查'], 
        	[5, '节假日安全检查'], 
        	[6, '节前安全检查'], 
        	[7, '复工安全检查'], 
        	[8, '安全目标专项检查'], 
        	[9, '其他']]
      });
    var checkunitcombo = new Ext.data.ArrayStore({
        fields: ['id', 'checkunit'],
        data: [[1, '集团公司'], 
        	[2, '湖北公司'], 
        	[3, '设计院'], 
        	[4, '建设单位'], 
        	[5, '其他']]
      });
    
    
        var items_saftycheck =[{
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
                    anchor:'90%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },
                {
                	xtype: 'datefield',
                    fieldLabel: '检查时间', 
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'checktime'
                },{
	            	xtype:'combobox',
                    fieldLabel: '检查类型',
                    store: checktypecombo,
                    valueField: 'checktype',
                    displayField: 'checktype',
//                    labelWidth: 120,
                    editable: true,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'checktype'
                },{
	            	xtype:'combobox',
                    fieldLabel: '检查单位',
                    store: checkunitcombo,
                    valueField: 'checkunit',
                    displayField: 'checkunit',
//                    labelWidth: 120,
                    editable: true,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'checkunit'
                }]
	        },{
  	        	xtype: 'container',
  	            flex: 1,
  	            anchor:'95%',
  	            layout: 'anchor',
  	            items: [{
	            	xtype:'combobox',
                    fieldLabel: '受检单位',
                    store:new Ext.data.ArrayStore({
                 	   fields : ['id','shoujianunit'],
                 	   data : [['1', '总包项目部'], ['2', '各分包单位']]
                 	}),
                    valueField: 'shoujianunit',
                    displayField: 'shoujianunit',
//                    labelWidth: 120,
                    editable: true,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'shoujianunit'
                },{
                	xtype:'textfield',
                    fieldLabel: '检查人员',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'checkperson'
                },{
                	xtype:'textfield',
                    fieldLabel: '安全检查记录表/整改通知单编号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'noticeandnum'
                }]
  	       },{
  	        	xtype: 'container',
  	            flex: 1,
  	            anchor:'95%',
  	            layout: 'anchor',
  	            items: [{
                	xtype: 'datefield',
                    fieldLabel: '整改回复时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: true,
                    name: 'replytime'
                },{
                	xtype:'datefield',
                    fieldLabel: '整改时限',
                    format : 'Y-m-d',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'timeline'
                },{
                	xtype:'textfield',
                    fieldLabel: '整改回复单编号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: true,
                    name: 'replyandnum'
                }]
  	       }]
	    },{
        	xtype: 'container',
            flex: 1,
            layout: 'column',
            anchor : '100%',
            name:'父控件',
            items:[{
            	xtype:'combo',
                fieldLabel: '整改问题1',
//                labelWidth: 500,
                store: store_readily_shoot,
                valueField: 'comment',
                displayField: 'comment',
                 triggerAction: 'all',
                 emptyText: '',
//                 blankText: '请选择政治面貌',
                 editable: true,
                 mode: 'remote',
                labelAlign: 'right',
                anchor:'90%',
//                width:685
                columnWidth : .57,
                listeners : {
                    change : function(field,newValue){
//                    	alert(newValue);
                    var kind =	forms.getForm().findField('kind1');
                    var degree = forms.getForm().findField('degree1');
                    	if(newValue==""){
                    		kind.allowBlank = true; 
                    		degree.allowBlank = true; 
                    	}
                    	else{
                    		kind.allowBlank = false; 
                    		degree.allowBlank = false; 
                    	}
                    }
                },
                name: 'problem1'
            },{
            	xtype:'combo',
                fieldLabel: '类别',
                labelWidth: 30,
                store: store_saftyproblem,
                valueField: 'showkind',
                displayField: 'showkind',
                 triggerAction: 'all',
                 emptyText: '请选择',
                 allowBlank: true,
//                 blankText: '请选择政治面貌',
                 editable: true,
                 mode: 'remote',
//                labelWidth: 120,
                labelAlign: 'right',
//                anchor:'20%',
                margin:'0,0,0,1',
                columnWidth : .25,
                minListWidth:400,
                name: 'kind1'
            },{
            	xtype:'combo',
            	queryMode:'local',
            	editable:false,
            	labelAlign: 'right',
                anchor:'95%',
                margin:'0,0,0,1',
            	store:new Ext.data.ArrayStore({
            	   fields : ['id','name'],
            	   data : [['一般', '一般'], ['重大', '重大']]
            	}),
            	valueField:'name',
            	displayField:'id',
            	triggerAction:'all',
            	autoSelect:true,
            	 allowBlank: true,
                columnWidth : .08,
                listeners: {
                    afterRender: function(combo,record) {
                    	var firstValue  = this.store.getAt(0).get('id');
                  　　                                         combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示
                    }
                },
                name:'degree1'
            },{
            	xtype:'button',
                text: '+',
                labelWidth: 5,
                columnWidth : .1,
                labelAlign: 'right',
                anchor:'5%',
                name: 'addbtn',
                margin:'0 0 0 10',
                listeners:{
                	'click':AddZB
                }
            }]
        },{
        	xtype: 'container',
            flex: 1,
            layout: 'column',
            anchor : '100%',
            name:'父控件2',
            items:[{
            	xtype:'textfield',
                fieldLabel: '建议1',
//                labelWidth: 500,
                labelAlign: 'right',
                anchor:'90%',
                name: 'advice1',
                margin:'6 0 0 0',
//                width:685
                columnWidth : .9
            },{
            	xtype:'button',
                text: '+',
                labelWidth: 5,
                columnWidth : .1,
                labelAlign: 'right',
                anchor:'5%',
                name: 'addadv',
                margin:'6 0 0 10',
                listeners:{
                	'click':AddADV
                }
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
	                text: '请点击上传安全检查记录表（整改通知单）或隐患照片',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
	    uploadPanel]
        
        
        
        
        var checktypecombo2 = new Ext.data.ArrayStore({
            fields: ['title', 'checktype'],
            data: [['季节性安全检查', '春季检查'], 
            	['季节性安全检查', '秋季检查'], 
            	['季节性安全检查', '冬季检查'], 
            	['专项安全检查', '安全目标专项检查'], 
            	['专项安全检查', '总包目标专项检查'], 
            	['专项安全检查', '分包目标专项检查'], 
            	['专项安全检查', '安全费用专项检查'], 
            	['专项安全检查', '交通安全专项检查'],  
            	['专项安全检查', '施工用电专项检查'], 
            	['专项安全检查', '三项业务专项检查'],
            	['专项安全检查', '应急物资专项检查'],
            	['专项安全检查', '防洪度汛专项检查'],
            	['专项安全检查', '其他专项检查']]
          });
        
        function checktypeChange(combo,record){
        	 var checktype  = this;
//        	 alert(checktype);
        	 if(title=='日常安全检查'||title=='定期安全检查'||title=='复工检查'){
        		 checktype.hidden=true;
        		 checktype.hiddenLabel=true;
        		 checktype.allowBlank=true;
        	 }else{
        		 checktype.hidden=false;
        		 checktype.hiddenLabel=false;
        		 checktype.allowBlank=false;
	        	 var detailscombo = checktype.getStore();
	        	 detailscombo.clearFilter();
	        	 detailscombo.filter('title',title);
	             detailscombo.load();
        	 }
        }
        
        
        var items_saftycheckwhthouttype =[{
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
                    anchor:'90%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
                },
                {
                	xtype: 'datefield',
                    fieldLabel: '检查时间', 
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'checktime'
                },{
                	xtype:'textfield',
                    fieldLabel: '检查内容',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'content'
                },{
	            	xtype:'combobox',
                    fieldLabel: '受检单位',
                    store:new Ext.data.ArrayStore({
                 	   fields : ['id','shoujianunit'],
                 	   data : [['1', '总包项目部'], ['2', '各分包单位']]
                 	}),
                    valueField: 'shoujianunit',
                    displayField: 'shoujianunit',
//                    labelWidth: 120,
                    editable: true,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'shoujianunit'
                },{
	            	xtype:'combobox',
                    fieldLabel: '检查类型',
                    store: checktypecombo2,
                    valueField: 'checktype',
                    displayField: 'checktype',
//                    labelWidth: 120,
                    editable: true,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    listeners:
                	{
                	   afterRender: checktypeChange
                    },
                    name: 'checktype'
                }]
	        },{
  	        	xtype: 'container',
  	            flex: 1,
  	            anchor:'95%',
  	            layout: 'anchor',
  	            items: [{
                	xtype:'textfield',
                    fieldLabel: '检查人员',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'checkperson'
                },{
                	xtype:'textfield',
                    fieldLabel: '安全检查记录表/整改通知单编号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'noticeandnum'
                },{
                	xtype:'datefield',
                    fieldLabel: '整改时限',
                    format : 'Y-m-d',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'timeline'
                }]
  	       },{
  	        	xtype: 'container',
  	            flex: 1,
  	            anchor:'95%',
  	            layout: 'anchor',
  	            items: [{
                	xtype: 'datefield',
                    fieldLabel: '整改回复时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: true,
                    name: 'replytime'
                },{
                	xtype:'textfield',
                    fieldLabel: '整改回复单编号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: true,
                    name: 'replyandnum'
                },{
                	xtype:'textfield',
                    fieldLabel: '整改复查情况',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: true,
                    name: 'last'
                }]
  	       }]
	    },{
        	xtype: 'container',
            flex: 1,
            layout: 'column',
            anchor : '100%',
            name:'父控件',
            items:[{
            	xtype:'combo',
                fieldLabel: '整改问题1',
//              labelWidth: 500,
                store: store_readily_shoot,
                valueField: 'comment',
                displayField: 'comment',
                 triggerAction: 'all',
                 emptyText: '',
//                 blankText: '请选择政治面貌',
                 editable: true,
                 mode: 'remote',
                labelAlign: 'right',
                anchor:'90%',
//                width:685
                columnWidth : .57,
                listeners : {
                    change : function(field,newValue){
//                    	alert(newValue);
                    var kind =	forms.getForm().findField('kind1');
                    	if(newValue==""){
                    		kind.allowBlank = true;  
                    	}
                    	else{
                    		kind.allowBlank = false; 
                    	}
                    }
                },
                name: 'problem1'
            },{
            	xtype:'combo',
                fieldLabel: '类别',
                labelWidth: 30,
                store: store_saftyproblem,
                valueField: 'showkind',
                displayField: 'showkind',
                 triggerAction: 'all',
                 emptyText: '请选择',
                 allowBlank: true,
//                 blankText: '请选择政治面貌',
                 editable: true,
                 mode: 'remote',
//                labelWidth: 120,
                labelAlign: 'right',
//                anchor:'20%',
                margin:'0,0,0,1',
                columnWidth : .25,
                minListWidth:400,
                name: 'kind1'
            },{
            	xtype:'combo',
            	queryMode:'local',
            	editable:false,
            	labelAlign: 'right',
                anchor:'95%',
            	store:new Ext.data.ArrayStore({
            	   fields : ['id','name'],
            	   data : [['一般', '一般'], ['重大', '重大']]
            	}),
            	valueField:'name',
            	displayField:'id',
            	triggerAction:'all',
            	autoSelect:true,
            	 allowBlank: true,
                columnWidth : .08,
                listeners: {
                    afterRender: function(combo,record) {
                    	var firstValue  = this.store.getAt(0).get('id');
                  　　                                         combo.setValue(firstValue);//同时下拉框会将与name为firstValue值对应的 text显示
                    }
                },
                name:'degree1'
            },{
            	xtype:'button',
                text: '+',
                labelWidth: 5,
                columnWidth : .1,
                labelAlign: 'right',
                anchor:'5%',
                name: 'addbtn',
                margin:'0 0 0 10',
                listeners:{
                	'click':AddZB
                }
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
	                text: '请点击上传安全检查记录表（整改通知单）或隐患照片',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
	    uploadPanel]
        
        
        var storemonth = new Ext.data.ArrayStore({
            fields: ['id', 'month'],
            data: [[1, '1月'], 
            	[2, '2月'], 
            	[3, '3月'], 
            	[4, '4月'], 
            	[5, '5月'], 
            	[6, '6月'], 
            	[7, '7月'], 
            	[8, '8月'], 
            	[9, '9月'], 
            	[10, '10月'],
                [11, '11月'],
        	    [12, '12月']]
          });
        
        var items_Taizhangfb =[{
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
                    name: 'fbname'
                },{
                	xtype:'combo',
                	queryMode:'local',
                	fieldLabel: '年份',
                	editable:false,
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    store:new Ext.data.ArrayStore({
                 	   fields : ['id','name'],
                 	   data : []
                 	}),
                 	valueField:'name',
                 	displayField:'id',
                 	triggerAction:'all',
                 	autoSelect:true,
                    allowBlank: false,
                    listeners:
                	{
                	  beforerender :function(){
                	    var newyear = Ext.Date.format(new Date(),'Y');//这是为了取现在的年份数
                	    var yearlist = [];
                	    var first = newyear;
                	    for(var i = -1;i<2;i++){
                	      yearlist.push([ Number(i)+Number(newyear),Number(i)+Number(newyear)]);
                	    }
                	    this.store.loadData(yearlist);
                	  }
                    },
                    name: 'year'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:'combobox',
                    fieldLabel: '台账月份',
                    store: storemonth,
                    valueField: 'month',
                    displayField: 'month',
//                    labelWidth: 120,
                    editable: false,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'month'
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
	                text: '请点击上传分包方隐患排查治理台账',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]
        
        
        var items_Saftycheckplan =[{
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
                    fieldLabel: '编制人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'bzren'
                },{
                	xtype: 'datefield',
                    fieldLabel: '编制时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'bztime'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '审批人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'spren'
                },{
                	xtype: 'datefield',
                    fieldLabel: '审批时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'sptime'
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
	                text: '请点击上传项目部安全检查计划',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]

        
//        var items_Saftycheckyearplan =[{
//	    	xtype: 'container',
//	        anchor: '100%',
//	        layout: 'hbox',
//	        items:[{
//	        	xtype: 'container',
//	            flex: 1,
//	            layout: 'anchor',
//	            items: [{
//	            	xtype:'textfield',
//                    fieldLabel: 'ID',
//                    labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    name: 'ID',
//                   	hidden: true,
//                    hiddenLabel: true
//                },{
//	            	xtype:'textfield',
//                    fieldLabel: 'ProjectName',
//                    //labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    name: 'ProjectName',
//                    value : projectName,
//                   	hidden: true,
//                    hiddenLabel: true
//                },{
//                	xtype:'textfield',
//                    fieldLabel: '附件',
//                    labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    name: 'Accessory',
//                   	hidden: true,
//                    hiddenLabel: true
//                },{
//                	xtype:'textfield',
//                    fieldLabel: '编制人',
//                    //labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    allowBlank: false,
//                    name: 'bzren'
//                },{
//                	xtype: 'datefield',
//                    fieldLabel: '编制时间',
//                    format : 'Y-m-d',
//                    anchor:'95%',
//                    labelAlign: 'right',
//                    allowBlank: false,
//                    name: 'bztime',
//                }]
//	        },{
//	        	xtype: 'container',
//	            flex: 1,
//	            layout: 'anchor',
//	            items: [{
//                	xtype:'textfield',
//                    fieldLabel: '审批人',
//                    //labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    allowBlank: false,
//                    name: 'spren'
//                },{
//                	xtype: 'datefield',
//                    fieldLabel: '审批时间',
//                    format : 'Y-m-d',
//                    anchor:'95%',
//                    labelAlign: 'right',
//                    allowBlank: false,
//                    name: 'sptime',
//                }]
//	        }]
//	    },
//        	uploadPanel
//        ]

        var items_Saftycheckyearplan=[{
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
                    fieldLabel: '编制人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'bzren'
                },{
                	xtype: 'datefield',
                    fieldLabel: '编制时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'bztime'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '审批人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'spren'
                },{
                	xtype: 'datefield',
                    fieldLabel: '审批时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'sptime'
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
	                text: '请点击上传项目部年度安全检查计划',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]
        
       var items_Saftycheckyearplanfb=[{
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
                    fieldLabel: '编制单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'bzunit'
                },{
                	xtype: 'datefield',
                    fieldLabel: '编制时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'bztime'
                },{
                	xtype:'textfield',
                    fieldLabel: '报备人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'bbren'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype: 'datefield',
                    fieldLabel: '报备时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'bbtime'
                },{
                	xtype:'textfield',
                    fieldLabel: '审批人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'spren'
                },{
                	xtype: 'datefield',
                    fieldLabel: '审批时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'sptime'
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
	                text: '请点击上传分包方年度安全检查计划',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]
        
        
//        var items_Saftycheckyinhuanpc =[{
//	    	xtype: 'container',
//	        anchor: '100%',
//	        layout: 'hbox',
//	        items:[{
//	        	xtype: 'container',
//	            flex: 1,
//	            layout: 'anchor',
//	            items: [{
//	            	xtype:'textfield',
//                    fieldLabel: 'ID',
//                    labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    name: 'ID',
//                   	hidden: true,
//                    hiddenLabel: true
//                },{
//	            	xtype:'textfield',
//                    fieldLabel: 'ProjectName',
//                    //labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    name: 'ProjectName',
//                    value : projectName,
//                   	hidden: true,
//                    hiddenLabel: true
//                },{
//                	xtype:'textfield',
//                    fieldLabel: '附件',
//                    labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    name: 'Accessory',
//                   	hidden: true,
//                    hiddenLabel: true
//                },{
//                	xtype:'combo',
//                	queryMode:'local',
//                	 fieldLabel: '年份',
//                	editable:false,
//                	labelAlign: 'right',
//                    anchor:'95%',
//                	store:new Ext.data.ArrayStore({
//                	   fields : ['id','name'],
//                	   data : []
//                	}),
//                	valueField:'name',
//                	displayField:'id',
//                	triggerAction:'all',
//                	autoSelect:true,
//                	listeners:
//                	{
//                	  beforerender :function(){
//                	    var newyear = Ext.Date.format(new Date(),'Y');//这是为了取现在的年份数
//                	    var yearlist = [];
//                	    var first = newyear;
//                	    for(var i = -1;i<=1;i++){
//                	      yearlist.push([ Number(i)+Number(newyear),Number(i)+Number(newyear)]);
//                	    }
//                	    this.store.loadData(yearlist);
//                	  }
//                    },
//                    allowBlank: false,
//                    name:'year'
//                },{
//                	xtype:'textfield',
//                    fieldLabel: '上传用户',
//                    //labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    allowBlank: false,
//                    name: 'uploaduser'
//                }]
//	        },{
//	        	xtype: 'container',
//	            flex: 1,
//	            layout: 'anchor',
//	            items: [{
//                	xtype: 'datefield',
//                    fieldLabel: '上传时间',
//                    format : 'Y-m-d',
//                    anchor:'95%',
//                    labelAlign: 'right',
//                    allowBlank: false,
//                    name: 'uploadtime'
//                }]
//	        }]
//	    },
//        	uploadPanel
//        ]
        
        
        
        var items_Saftycheckyinhuanpc =[{
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
                }]
	        }]
	    }, {
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
	                text: '请点击上传隐患排查治理年度工作方案',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]
        
        

     
        
       
        var items_Riskfenbao =[{
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
                    fieldLabel: '分包方名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'fenbaoname'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype: 'datefield',
                    fieldLabel: '报备时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'bbtime'
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
	                text: '请点击上传分包方危险源管理资料',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]
        
        
        
        var storebstype = new Ext.data.ArrayStore({
            fields: ['id', 'bstype'],
            data: [[1, '开工前'], 
            	[2, '工序施工前'], 
            	[3, '每月初']]
          });
        
        
        
        var items_Riskprodanger =[{
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
                    fieldLabel: '辨识类型',
                    store: storebstype,
                    valueField: 'bstype',
                    displayField: 'bstype',
//                    labelWidth: 120,
                    editable: false,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'bstype'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype: 'datefield',
                    fieldLabel: '辨识时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'bstime'
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
	                text: '请点击上传项目危险因素（危险源）辨识清单',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]
        
        var storeshigstage = new Ext.data.ArrayStore({
            fields: ['id', 'shigstage'],
            data: [[1, '开工准备阶段'], 
            	[2, '土建施工阶段'], 
            	[3, '安装调试阶段'],
            	[4, '试运行阶段']]
          });
      
        var items_Risksafepg =[{
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
                    fieldLabel: '施工阶段',
                    store: storeshigstage,
                    valueField: 'shigstage',
                    displayField: 'shigstage',
//                    labelWidth: 120,
                    editable: false,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'shigstage'
                },{
                	xtype: 'datefield',
                    fieldLabel: '安全评估时间',
                    format : 'Y-m-d',
                    anchor:'95%',
                    labelAlign: 'right',
                    allowBlank: false,
                    name: 'safeptime'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '编制人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'bzperson'
                },{
                	xtype:'textfield',
                    fieldLabel: '审核人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'shperson'
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
	                text: '请点击上传安全评估报告表',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]
      
        
    
        var items_Fenbaoyinhuanpczlgzfa =[{
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
                	xtype:'combo',
                	queryMode:'local',
                	fieldLabel: '年份',
                	editable:false,
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    store:new Ext.data.ArrayStore({
                 	   fields : ['id','name'],
                 	   data : []
                 	}),
                 	valueField:'name',
                 	displayField:'id',
                 	triggerAction:'all',
                 	autoSelect:true,
                    allowBlank: false,
                    listeners:
                	{
                	  beforerender :function(){
                	    var newyear = Ext.Date.format(new Date(),'Y');//这是为了取现在的年份数
                	    var yearlist = [];
                	    var first = newyear;
                	    for(var i = -1;i<2;i++){
                	      yearlist.push([ Number(i)+Number(newyear),Number(i)+Number(newyear)]);
                	    }
                	    this.store.loadData(yearlist);
                	  }
                    },
                    name: 'year'
                },{
                	xtype:'textfield',
                    fieldLabel: '分包单位名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'fenbaoname'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '工作方案名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'workname'
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
	                text: '请点击上传分包方隐患排查治理年度工作方案',
	                height:10,
	                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
	                name: 'tip',
	            }
	        ]}
        	,
        	uploadPanel
        ]
        
        
        var store_SaftyCheck326 = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'checktime'},
                     { name: 'checktype'},
                     { name: 'checkunit'},
                     { name: 'shoujianunit'},
                     { name: 'checkperson'},
                     { name: 'problem'},
                     { name: 'prokind'},
                     { name: 'prodegree'},
                     { name: 'pronum'},
                     { name: 'advice'},
                     { name: 'advicenum'},
                     { name: 'timeline'},
                     { name: 'replytime'},
                     { name: 'noticeandnum'},
                     { name: 'replyandnum'},
                     { name: 'content'},
                     { name: 'last'},
                     { name: 'Accessory'},
                     { name: 'type'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getSaftycheck326ListDef?userName=' + user.name + "&userRole=" + user.role+"&type=" + title),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        var store_SaftyCheck327 = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'checktime'},
                     { name: 'checktype'},
                     { name: 'checkunit'},
                     { name: 'shoujianunit'},
                     { name: 'checkperson'},
                     { name: 'problem'},
                     { name: 'prokind'},
                     { name: 'prodegree'},
                     { name: 'pronum'},
                     { name: 'advice'},
                     { name: 'advicenum'},
                     { name: 'timeline'},
                     { name: 'replytime'},
                     { name: 'noticeandnum'},
                     { name: 'replyandnum'},
                     { name: 'content'},
                     { name: 'last'},
                     { name: 'Accessory'},
                     { name: 'type'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getSaftycheck327ListDef?userName=' + user.name + "&userRole=" + user.role+"&type=" + title),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
       	/**
       	 * saftyCheck 计划
       	 * 计划
       	 */
		var store_SaftyCheck = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'checktime'},
                     { name: 'checktype'},
                     { name: 'checkunit'},
                     { name: 'shoujianunit'},
                     { name: 'checkperson'},
                     { name: 'problem'},
                     { name: 'prokind'},
                     { name: 'prodegree'},
                     { name: 'pronum'},
                     { name: 'advice'},
                     { name: 'advicenum'},
                     { name: 'timeline'},
                     { name: 'replytime'},
                     { name: 'noticeandnum'},
                     { name: 'replyandnum'},
                     { name: 'content'},
                     { name: 'last'},
                     { name: 'Accessory'},
                     { name: 'type'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getSaftycheckListDef?userName=' + user.name + "&userRole=" + user.role+"&type=" + title+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        
        /**
       	 * Taizhang
       	 */
		var store_Taizhang = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'no'},
                     { name: 'checkId'},
                     { name: 'problem'},
                     { name: 'location'},
                     { name: 'checkperson'},
                     { name: 'prolevel'},
                     { name: 'correction'},
                     { name: 'solvedep'},
                     { name: 'solvePerson'},
                     { name: 'expTime'},
                     { name: 'correctionfee'},
                     { name: 'solveExp'},
                     { name: 'solveTime'},
                     { name: 'supperson'},
                     { name: 'prevent'},
                     { name: 'solveAcc'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getTaizhangListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		/**
       	 * Taizhangfb
       	 */
		var store_Taizhangfb = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'fbname'},
        	         { name: 'year'},
                     { name: 'month'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getTaizhangfbListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
	
		/**
       	 * Saftycheckplan
       	 */
		var store_Saftycheckplan = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'bzren'},
                     { name: 'bztime'},
                     { name: 'spren'},
                     { name: 'sptime'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getSaftycheckplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		/**
       	 * Saftycheckyearplan
       	 */
		var store_Saftycheckyearplan = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'bzren'},
                     { name: 'bztime'},
                     { name: 'spren'},
                     { name: 'sptime'},
                     { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getSaftycheckyearplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		
		/**
       	 * Saftycheckyearplanfb
       	 */
		var store_Saftycheckyearplanfb = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'bzunit'},
                     { name: 'bztime'},
                     { name: 'bbren'},
                     { name: 'bbtime'},
                     { name: 'spren'},
                     { name: 'sptime'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getSaftycheckyearplanfbListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		
		/**
       	 * Saftycheckyinhuanpc
       	 */
		var store_Saftycheckyinhuanpc = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'year'},
                     { name: 'uploaduser'},
                     { name: 'uploadtime'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getSaftycheckyinhuanpcListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
	
		/**
       	 * Riskfenbao
       	 */
		var store_Riskfenbao = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'fenbaoname'},
                     { name: 'bbtime'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getRiskfenbaoListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		
		
		/**
       	 * Riskprodanger
       	 */
		var store_Riskprodanger = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'bstype'},
                     { name: 'bstime'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getRiskprodangerListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
		
		
		/**
       	 * Risksafepg
       	 */
		var store_Risksafepg = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'shigstage'},
                     { name: 'safeptime'},
                     { name: 'bzperson'},
                     { name: 'shperson'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getRisksafepgListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		
		/**
       	 * Fenbaoyinhuanpczlgzfa
       	 */
		var store_Fenbaoyinhuanpczlgzfa = Ext.create('Ext.data.Store', {
        	fields: [
        		     {name:'ID'},
        	         { name: 'year'},
                     { name: 'fenbaoname'},
                     { name: 'workname'},
                     { name: 'filename'},
                     { name: 'uploadtime'},
                     { name: 'Accessory'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('SaftyCkeckAction!getFenbaoyinhuanpczlgzfaListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        
        

		

		    if(title=='项目安全检查计划')
	        {
	        	store_Saftycheckplan.load();
	        	dataStore = store_Saftycheckplan;
	        	queryURL = 'SaftyCkeckAction!getSaftycheckplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
	        	//dataStore.getProxy().url = encodeURI(queryURL);
	        	//style = "write";
	        	column = [
	        		      { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
	        		      { text: '编制人', dataIndex: 'bzren', align: 'center', width: 110},
		    	          { text: '编制时间', dataIndex: 'bztime', align: 'center', width: 110},
		    	          { text: '审批人', dataIndex: 'spren', align: 'center', width: 110},
		    	          { text: '审批时间', dataIndex: 'sptime', align: 'center', width: 110}
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
	    			
	    			if(user.role == '全部项目') {
	        			dataStore.getProxy().url = 'SaftyCkeckAction!getSaftycheckplanListDef?userName=' + user.name 
                                                 + "&userRole=" +user.role + "&projectName=" + "";
	        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
	        			
	        			//给第一列添加所属项目，dataindex自己根据实际字段改
	        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 300}];
	        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
	        			
	        			queryURL = 'SaftyCkeckAction!getSaftycheckplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=";
	        		}
	        }
	        
	        else  if(title=='项目部年度安全检查计划')
	        {
	        	store_Saftycheckyearplan.load();
	        	dataStore = store_Saftycheckyearplan;
	        	queryURL = 'SaftyCkeckAction!getSaftycheckyearplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
	        	//dataStore.getProxy().url = encodeURI(queryURL);
	        	//style = "write";
	        	column = [
	        		        { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
	   		                { text: '编制人', dataIndex: 'bzren', align: 'center', width: 110},
	   	                    { text: '编制时间', dataIndex: 'bztime', align: 'center', width: 110},
	   	                    { text: '审批人', dataIndex: 'spren', align: 'center', width: 110},
	   	                    { text: '审批时间', dataIndex: 'sptime', align: 'center', width: 110}
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
	        		
	        		if(user.role == '全部项目') {
	        			dataStore.getProxy().url = 'SaftyCkeckAction!getSaftycheckyearplanListDef?userName=' + user.name 
                                                 + "&userRole=" +user.role + "&projectName=" + "";
	        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
	        			
	        			//给第一列添加所属项目，dataindex自己根据实际字段改
	        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}];
	        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
	        			
	        			queryURL = 'SaftyCkeckAction!getSaftycheckyearplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=";
	        		}
	        }
		    
	        else   if(title=='分包方年度安全检查计划')
	        {
	        	store_Saftycheckyearplanfb.load();
	        	dataStore = store_Saftycheckyearplanfb;
	        	queryURL = 'SaftyCkeckAction!getSaftycheckyearplanfbListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
	        	//dataStore.getProxy().url = encodeURI(queryURL);
	        	//style = "write";
	        	column = [
	        		        { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
	   		                { text: '编制单位', dataIndex: 'bzunit', align: 'center', width: 110},
	   	                    { text: '编制时间', dataIndex: 'bztime', align: 'center', width: 110},
	   	                    { text: '报备人', dataIndex: 'bbren', align: 'center', width: 110},
		                    { text: '报备时间', dataIndex: 'bbtime', align: 'center', width: 110},
	   	                    { text: '审批人', dataIndex: 'spren', align: 'center', width: 110},
	   	                    { text: '审批时间', dataIndex: 'sptime', align: 'center', width: 110} 
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
	        

	      else  if(title == '上级检查问题整改及回复')
          {	
        	
        	dataStore = store_SaftyCheck;
        	queryURL = 'SaftyCkeckAction!getSaftycheckListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '检查时间', dataIndex: 'checktime', align: 'center', width: 120},	
        		    { text: '检查类型', dataIndex: 'checktype', align: 'center', width: 150},	
        		    { text: '检查单位', dataIndex: 'checkunit', align: 'center', width: 150},	
        		    { text: '受检单位', dataIndex: 'shoujianunit', align: 'center', width: 150},	
        		    { text: '检查人员', dataIndex: 'checkperson', align: 'center', width: 150},	
        		    { text: '安全检查记录表/整改通知单编号', dataIndex: 'noticeandnum', align: 'center', width: 200},
        		    { text: '整改时限', dataIndex: 'timeline', align: 'center', width: 120},	
        		    { text: '整改回复时间', dataIndex: 'replytime', align: 'center', width: 120},	
        		    { text: '整改回复单编号', dataIndex: 'replyandnum', align: 'center', width: 200}
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
        		tbar.add(btnchkdis);
        		
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        		
        		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'SaftyCkeckAction!getSaftycheckListDef?userName=' + user.name 
                                             + "&userRole=" +user.role + "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			
        			queryURL = 'SaftyCkeckAction!getSaftycheckListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=";
        		}
        }
        
        else if(title == '季节性安全检查'||title == '专项安全检查')
        {	
        	
        	dataStore = store_SaftyCheck;
        	queryURL = 'SaftyCkeckAction!getSaftycheckListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '检查时间', dataIndex: 'checktime', align: 'center', width: 120},	
        		    { text: '检查内容', dataIndex: 'content', align: 'center', width: 150},	
        		    { text: '检查类型', dataIndex: 'checktype', align: 'center', width: 150},	
        		    { text: '受检单位', dataIndex: 'shoujianunit', align: 'center', width: 150},	
        		    { text: '检查人员', dataIndex: 'checkperson', align: 'center', width: 150},	
        		    { text: '安全检查记录表/整改通知单编号', dataIndex: 'noticeandnum', align: 'center', width: 200},
        		    { text: '整改时限', dataIndex: 'timeline', align: 'center', width: 120},	
        		    { text: '整改回复时间', dataIndex: 'replytime', align: 'center', width: 120},	
        		    { text: '整改回复单编号', dataIndex: 'replyandnum', align: 'center', width: 200},
        		    { text: '整改复查情况', dataIndex: 'replyandnum', align: 'center', width: 150}
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
        		tbar.add(btnchkdis);
        		
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        }
        
        else if(title == '日常安全检查'||title == '定期安全检查'||title == '复工检查')
        {	
        	
        	dataStore = store_SaftyCheck;
        	queryURL = 'SaftyCkeckAction!getSaftycheckListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '检查时间', dataIndex: 'checktime', align: 'center', width: 120},	
        		    { text: '检查内容', dataIndex: 'content', align: 'center', width: 150},	
        		    { text: '受检单位', dataIndex: 'shoujianunit', align: 'center', width: 150},	
        		    { text: '检查人员', dataIndex: 'checkperson', align: 'center', width: 150},	
        		    { text: '安全检查记录表/整改通知单编号', dataIndex: 'noticeandnum', align: 'center', width: 200},
        		    { text: '整改时限', dataIndex: 'timeline', align: 'center', width: 120},	
        		    { text: '整改回复时间', dataIndex: 'replytime', align: 'center', width: 120},	
        		    { text: '整改回复单编号', dataIndex: 'replyandnum', align: 'center', width: 200},
        		    { text: '整改复查情况', dataIndex: 'replyandnum', align: 'center', width: 150}
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
        		tbar.add(btnchkdis);
        		
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        }
        
        else  if(title=='隐患排查治理年度工作方案')
        {
        	store_Saftycheckyinhuanpc.load();
        	dataStore = store_Saftycheckyinhuanpc;
        	queryURL = 'SaftyCkeckAction!getSaftycheckyinhuanpcListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		        { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
   		                { text: '年份', dataIndex: 'year', align: 'center', width: 110},
   	                    { text: '文件名', dataIndex: 'uploaduser', align: 'center', width: 200},
   	                    { text: '上传时间', dataIndex: 'uploadtime', align: 'center', width: 110}
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
        			dataStore.getProxy().url = 'SaftyCkeckAction!getSaftycheckyinhuanpcListDef?userName=' + user.name 
                                             + "&userRole=" +user.role + "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			
        			queryURL = 'SaftyCkeckAction!getSaftycheckyinhuanpcListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=";
        			
        		}
        }
        
        else  if(title=='隐患排查治理台账')
        {
        	store_Taizhang.load();
        	dataStore = store_Taizhang;
        	queryURL = 'SaftyCkeckAction!getTaizhangListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '存在的安全隐患', dataIndex: 'problem', align: 'center', width: 110},
	    	          { text: '隐患存在地点', dataIndex: 'location', align: 'center', width: 110},
	    	          { text: '发现人(部门)', dataIndex: 'checkperson', align: 'center', width: 110},
	    	          { text: '隐患定级', dataIndex: 'prolevel', align: 'center', width: 110},
	    	          { text: '责任部门/单位', dataIndex: 'solvedep', align: 'center', width: 110},
	    	          { text: '责任人', dataIndex: 'solvePerson', align: 'center', width: 110},
	            	  { text: '计划完成整改时间', dataIndex: 'expTime', align: 'center', width: 120},
	            	  { text: '整改费用(元)', dataIndex: 'correctionfee', align: 'center', width: 110},
	            	  { text: '完成时间', dataIndex: 'solveTime', align: 'center', width: 110},	       	  
	            	  { text: '监督人', dataIndex: 'supperson', align: 'center', width: 110},
	            	  { text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	]
//            	tbar.add("-");
//        		tbar.add(btnAdd);
//        		tbar.add(btnEdit);
//        		tbar.add(btnDel);
        		tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add(btnScan);
        		
        		
        }
        
        else   if(title=='分包方隐患排查治理工作方案')
        {
        	store_Fenbaoyinhuanpczlgzfa.load();
        	dataStore = store_Fenbaoyinhuanpczlgzfa;
        	queryURL = 'SaftyCkeckAction!getFenbaoyinhuanpczlgzfaListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		        { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
   		                { text: '年份', dataIndex: 'year', align: 'center', width: 110},
   	                    { text: '分包单位名称', dataIndex: 'fenbaoname', align: 'center', width: 110},
   	                    { text: '工作方案名称', dataIndex: 'workname', align: 'center', width: 110},
	                    { text: '文件名', dataIndex: 'filename', align: 'center', width: 350} ,
	                    { text: '上传时间', dataIndex: 'uploadtime', align: 'center', width: 110} 
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
        
        
        else  if(title=='分包方隐患排查治理台账')
        {
        	store_Taizhangfb.load();
        	dataStore = store_Taizhangfb;
        	queryURL = 'SaftyCkeckAction!getTaizhangfbListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '分包单位名称', dataIndex: 'fbname', align: 'center', width: 110},
        		    { text: '台账年份', dataIndex: 'year', align: 'center', width: 110},
	    	        { text: '台账月份', dataIndex: 'month', align: 'center', width: 110}
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
        
      
        else   if(title=='项目危险因素（危险源）辨识')
        {
        	store_Riskprodanger.load();
        	dataStore = store_Riskprodanger;
        	queryURL = 'SaftyCkeckAction!getRiskprodangerListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		        { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
   		                { text: '辨识类型', dataIndex: 'bstype', align: 'center', width: 110},
   	                    { text: '辨识时间', dataIndex: 'bstime', align: 'center', width: 110} 
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
        }
        
        else   if(title=='安全评估')
        {
        	store_Risksafepg.load();
        	dataStore = store_Risksafepg;
        	queryURL = 'SaftyCkeckAction!getRisksafepgListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		        { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
   		                { text: '施工阶段', dataIndex: 'shigstage', align: 'center', width: 110},
   	                    { text: '安全评估时间', dataIndex: 'safeptime', align: 'center', width: 110},
   	                    { text: '编制人', dataIndex: 'bzperson', align: 'center', width: 110},
	                    { text: '审核人', dataIndex: 'shperson', align: 'center', width: 110}
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
        			dataStore.getProxy().url = 'SaftyCkeckAction!getRisksafepgListDef?userName=' + user.name 
                                             + "&userRole=" +user.role + "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			
        			queryURL = 'SaftyCkeckAction!getRisksafepgListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=";
        		}
        }
		    
        else   if(title=='分包方危险源管理')
        {
        	store_Riskfenbao.load();
        	dataStore = store_Riskfenbao;
        	queryURL = 'SaftyCkeckAction!getRiskfenbaoListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		        { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
   		                { text: '分包方名称', dataIndex: 'fenbaoname', align: 'center', width: 110},
   	                    { text: '报备时间', dataIndex: 'bbtime', align: 'center', width: 110} 
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
		    
		    
        else if(title=='安全生产目标检查与纠偏')
        {	
        	dataStore = store_SaftyCheck326;
        	queryURL = 'SaftyCkeckAction!getSaftycheck326ListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '检查时间', dataIndex: 'checktime', align: 'center', width: 150},	
        		    { text: '检查内容', dataIndex: 'content', align: 'center', width: 150},	
        		    { text: '检查类型', dataIndex: 'checktype', align: 'center', width: 150},	
        		    { text: '受检单位', dataIndex: 'shoujianunit', align: 'center', width: 150},	
        		    { text: '检查人员', dataIndex: 'checkperson', align: 'center', width: 150},	
        		    { text: '安全检查记录表/整改通知单编号', dataIndex: 'noticeandnum', align: 'center', width: 200},
        		    { text: '整改时限', dataIndex: 'timeline', align: 'center', width: 150},	
        		    { text: '整改回复时间', dataIndex: 'replytime', align: 'center', width: 150},	
        		    { text: '整改回复单编号', dataIndex: 'replyandnum', align: 'center', width: 200},
        		    { text: '整改复查情况', dataIndex: 'replyandnum', align: 'center', width: 150}
            	]
            	tbar.add("-");
        	    tbar.add(textSearch);
    		    tbar.add(btnSearch);
    		    tbar.add(btnSearchR);
    		    tbar.add(btnScan);
        		tbar.add("-");
        		//tbar.add(btnAdd);
        		//tbar.add(btnEdit);
        		//tbar.add(btnDel);
        		//tbar.add(btnchkdis);
        		
        		if(user.role=='其他管理员'){
        			tbar.remove(btnAdd);
            		tbar.remove(btnEdit);
            		tbar.remove(btnDel);
        		}
        }
        
        else if(title=='分包方目标管理')
        {	
        	dataStore = store_SaftyCheck327;
        	queryURL = 'SaftyCkeckAction!getSaftycheck327ListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '检查时间', dataIndex: 'checktime', align: 'center', width: 150},	
        		    { text: '检查内容', dataIndex: 'content', align: 'center', width: 150},	
        		    { text: '检查类型', dataIndex: 'checktype', align: 'center', width: 150},	
        		    { text: '受检单位', dataIndex: 'shoujianunit', align: 'center', width: 150},	
        		    { text: '检查人员', dataIndex: 'checkperson', align: 'center', width: 150},	
        		    { text: '安全检查记录表/整改通知单编号', dataIndex: 'noticeandnum', align: 'center', width: 200},
        		    { text: '整改时限', dataIndex: 'timeline', align: 'center', width: 150},	
        		    { text: '整改回复时间', dataIndex: 'replytime', align: 'center', width: 150},	
        		    { text: '整改回复单编号', dataIndex: 'replyandnum', align: 'center', width: 200},
        		    { text: '整改复查情况', dataIndex: 'replyandnum', align: 'center', width: 150}
            	]
            	tbar.add("-");
        	    tbar.add(textSearch);
    		    tbar.add(btnSearch);
    		    tbar.add(btnSearchR);
    		    tbar.add(btnScan);
        		tbar.add("-");
        		//tbar.add(btnAdd);
        		//tbar.add(btnEdit);
        		//tbar.add(btnDel);
        		//tbar.add(btnchkdis);
        		
        		if(user.role=='其他管理员'){
        			tbar.remove(btnAdd);
            		tbar.remove(btnEdit);
            		tbar.remove(btnDel);
        		}
        }
		    
		    
//        else if(title=='安全生产目标检查与纠偏'||title=='分包方目标管理')
//        {	
////        	alert('fdjsfakldsjafdklsa');
//        	dataStore = store_SaftyCheck326;
//        	queryURL = 'SaftyCkeckAction!getSaftycheck326ListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title;
//        	//dataStore.getProxy().url = encodeURI(queryURL);
//        	//style = "write";
//        	column = [
//        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
//        		    { text: '检查时间', dataIndex: 'checktime', align: 'center', width: 150},	
//        		    { text: '检查内容', dataIndex: 'content', align: 'center', width: 150},	
//        		    { text: '检查类型', dataIndex: 'checktype', align: 'center', width: 150},	
//        		    { text: '受检单位', dataIndex: 'shoujianunit', align: 'center', width: 150},	
//        		    { text: '检查人员', dataIndex: 'checkperson', align: 'center', width: 150},	
//        		    { text: '安全检查记录表/整改通知单编号', dataIndex: 'noticeandnum', align: 'center', width: 200},
//        		    { text: '整改时限', dataIndex: 'timeline', align: 'center', width: 150},	
//        		    { text: '整改回复时间', dataIndex: 'replytime', align: 'center', width: 150},	
//        		    { text: '整改回复单编号', dataIndex: 'replyandnum', align: 'center', width: 200},
//        		    { text: '整改复查情况', dataIndex: 'replyandnum', align: 'center', width: 150}
//            	]
//            	tbar.add("-");
//        	    tbar.add(textSearch);
//    		    tbar.add(btnSearch);
//    		    tbar.add(btnSearchR);
//    		    tbar.add(btnScan);
//        		tbar.add("-");
//        		//tbar.add(btnAdd);
//        		//tbar.add(btnEdit);
//        		//tbar.add(btnDel);
//        		//tbar.add(btnchkdis);
//        		
//        		if(user.role=='其他管理员'){
//        			tbar.remove(btnAdd);
//            		tbar.remove(btnEdit);
//            		tbar.remove(btnDel);
//        		}
//        }
		    
		    
		    var store_faxianwenti = Ext.create('Ext.data.Store', {
	        	fields: [
	        		     {name:'ID'},
	        	         { name: 'checktime'},
	        	         {name:'ProjectName'},
	                     { name: 'problem'},
	                     { name: 'prokind'},
	                     { name: 'prodegree'}
	            ],
	            pageSize: psize,  //页容量20条数据
	            proxy: {
	                type: 'ajax',
	                url: encodeURI('SaftyCkeckAction!getfaxianwentiListDef?userName=' + user.name + "&userRole=" + user.role+"&type=上级检查问题整改及回复"+ "&projectName=" + projectName),
	                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
	                    type: 'json', //返回数据类型为json格式
	                    root: 'rows',  //数据
	                    totalProperty: 'total' //数据总条数
	                }
	            },
	            autoLoad: true //即时加载数据
	        });
		    
       if(title=='安全检查发现问题统计')
        {	
        	
        	dataStore = store_faxianwenti;
        	queryURL = 'SaftyCkeckAction!getfaxianwentiListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=上级检查问题整改及回复"+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '检查时间', dataIndex: 'checktime', align: 'center', width: 150},	
        		    { text: '项目部名称', dataIndex: 'ProjectName', align: 'center', width: 150},	
        		    { text: '发现安全隐患', dataIndex: 'problem', align: 'center', width: 150},	
        		    { text: '隐患类别', dataIndex: 'prokind', align: 'center', width: 150},	
        		    { text: '隐患定级(一般/重大)', dataIndex: 'prodegree', align: 'center', width: 150}
            	]
            	tbar.add("-");
        	    tbar.add(textSearch);
    		    tbar.add(btnSearch);
    		    tbar.add(btnSearchR);
    		    tbar.add(btnScan);
        		tbar.add("-");
        		//tbar.add(btnAdd);
        		//tbar.add(btnEdit);
        		//tbar.add(btnDel);
        		//tbar.add(btnchkdis);
        		
        		if(user.role=='其他管理员'){
        			tbar.remove(btnAdd);
            		tbar.remove(btnEdit);
            		tbar.remove(btnDel);
        		}
        }
        
       
       
       
       /**
      	 * Taizhang
      	 */
		var store_yinhuanpaicha = Ext.create('Ext.data.Store', {
       	fields: [
       		     {name:'ID'},
       	         { name: 'no'},
                    { name: 'checkId'},
                    { name: 'problem'},
                    { name: 'location'},
                    { name: 'checkperson'},
                    { name: 'prolevel'},
                    { name: 'correction'},
                    { name: 'solvedep'},
                    { name: 'solvePerson'},
                    { name: 'expTime'},
                    { name: 'correctionfee'},
                    { name: 'solveExp'},
                    { name: 'solveTime'},
                    { name: 'supperson'},
                    { name: 'prevent'},
                    { name: 'solveAcc'},
                    { name: 'Accessory'},
                    { name: 'ProjectName'}
           ],
           pageSize: psize,  //页容量20条数据
           proxy: {
               type: 'ajax',
               url: encodeURI('SaftyCkeckAction!getyinhuanpaichaListDef?userName=' + user.name + "&userRole=" + user.role+ "&type=上级检查问题整改及回复"+ "&projectName=" + projectName),
               reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                   type: 'json', //返回数据类型为json格式
                   root: 'rows',  //数据
                   totalProperty: 'total' //数据总条数
               }
           },
           autoLoad: true //即时加载数据
       });
        
        
		
        if(title=='隐患排查治理台账统计')
        {	
        	dataStore = store_yinhuanpaicha;
        	queryURL = 'SaftyCkeckAction!getfaxianwentiListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=上级检查问题整改及回复"+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
        		    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
        		    { text: '存在的安全隐患', dataIndex: 'problem', align: 'center', width: 150},	
        		    { text: '项目部名称', dataIndex: 'ProjectName', align: 'center', width: 110},	
        		    { text: '隐患存在地点', dataIndex: 'location', align: 'center', width: 110},	
        		    { text: '发现人（部门）', dataIndex: 'checkperson', align: 'center', width: 110},	
        		    { text: '隐患定级（一般/重大）', dataIndex: 'prolevel', align: 'center', width: 120},
        		    { text: '责任部门/单位', dataIndex: 'solvedep', align: 'center', width: 110},
        		    { text: '责任人', dataIndex: 'solvePerson', align: 'center', width: 110},
        		    { text: '计划完成整改时间', dataIndex: 'expTime', align: 'center', width: 120},
        		    { text: '整改费用（元）', dataIndex: 'correctionfee', align: 'center', width: 110},
        		    { text: '完成时间', dataIndex: 'solveTime', align: 'center', width: 110},
        		    { text: '监督人', dataIndex: 'supperson', align: 'center', width: 110},
            	]
            	tbar.add("-");
        	    tbar.add(textSearch);
    		    tbar.add(btnSearch);
    		    tbar.add(btnSearchR);
    		    tbar.add(btnScan);
        		tbar.add("-");
        		//tbar.add(btnAdd);
        		//tbar.add(btnEdit);
        		//tbar.add(btnDel);
        		//tbar.add(btnchkdis);
        		
        		if(user.role=='其他管理员'){
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
//                		//********************************
//                		var kind1 = forms.getForm().findField('kind1');
//            			var problem1 = forms.getForm().findField('problem1');
//            			if(problem1.value!=null&&kind1.value==null)
//            			{
//            				Ext.Msg.alert('警告','请完选择类别！');
//            			}
//            			//**********************************
                		if(forms.form.isValid()){
                			switch (config.action){	
                			     
                			    case "addSaftycheck":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                			    }
                			    case "editSaftycheck":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    case "dispatchChk":{
                			    	var properson="";
                				    var tnode = storeSelperson.getRootNode(); 
                			        tnode.cascadeBy(function(node){ //遍历节点                      
                			            properson = properson+node.get('text');
                			        }); 
                			        properson = properson.substring(4,properson.length);
                			        config.url += "&userName=" + properson;
                			    }
                			    
                			    
                			    
                			    case "addSaftycheckplan":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editSaftycheckplan":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    
                			    case "addSaftycheckyearplan":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(forms.getForm().findField('ProjectName').value);
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editSaftycheckyearplan":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    
                			    case "addSaftycheckyearplanfb":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editSaftycheckyearplanfb":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    
                			    case "addSaftycheckyinhuanpc":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editSaftycheckyinhuanpc":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    
                			    
                			    case "addTaizhangfb":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editTaizhangfb":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    
                			    case "addRiskfenbao":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editRiskfenbao":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    
                			    case "addRiskprodanger":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editRiskprodanger":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    
                			    case "addRisksafepg":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editRisksafepg":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(config.url);
                					break;
                			    }
                			    
                			    
                			    case "addFenbaoyinhuanpczlgzfa":{
                			    	var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
//                					alert(fileName+"**"+config.url);
                					break;
                			    }
                			    case "editFenbaoyinhuanpczlgzfa":{
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
	                                config.url += "&fileName=" + fileName + "&user=" + user.name;
	                                if (uploadPanel.store.count() == 0) {
	                                    Ext.Msg.alert('提示', '请上传文件！');
	                                    return;
	                                }
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
      	                	if(config.action == "editFenbaoyinhuanpczlgzfa")
                    		{
                    			var fileName = getFileName();
            					if(fileName == null)
                    			{
                    				Ext.Msg.alert('警告','请上传文件！');
                    			}
                    		}
      	                	else
      	                	{
      	                		Ext.Msg.alert('警告','请完善信息！');
//      	                		Ext.Msg.alert('警告','请上传文件！');
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
//                		if (config.action == "editSaftycostplan") 
//                        {
//                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
//                        }
                		if (config.action == "editSaftycheck"|| config.action == "dispatchChk") 
                        {
                			
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                             clearall();
                             EditShow({action: 'editSaftycheck'});
                             clearalladv();
                             EditShowadv({action: 'editSaftycheck'});
                        }
                		else if (config.action == "editSaftycheckplan") 
                        {
                    		var selRecs = gridDT.getSelectionModel().getSelection();
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		else if (config.action == "editSaftycheckyearplan") 
                        {
                    		var selRecs = gridDT.getSelectionModel().getSelection();
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		else if (config.action == "editSaftycheckyearplanfb") 
                        {
                    		var selRecs = gridDT.getSelectionModel().getSelection();
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		else if (config.action == "editSaftycheckyinhuanpc") 
                        {
                    		var selRecs = gridDT.getSelectionModel().getSelection();
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		else if (config.action == "editTaizhangfb") 
                        {
                    		var selRecs = gridDT.getSelectionModel().getSelection();
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		else if (config.action == "editRiskfenbao") 
                        {
                    		var selRecs = gridDT.getSelectionModel().getSelection();
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        else if (config.action == "editRiskprodanger") 
                        {
                    		var selRecs = gridDT.getSelectionModel().getSelection();
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        else if (config.action == "editRisksafepg") 
                        {
                    		var selRecs = gridDT.getSelectionModel().getSelection();
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        else if (config.action == "editFenbaoyinhuanpczlgzfa") 
                        {
                    		var selRecs = gridDT.getSelectionModel().getSelection();
                             forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		
                		 
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
//        	if (config.action == "editSaftycostplan") 
//            {
//                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
//            }
        	if (config.action == "editSaftycheck"|| config.action == "dispatchChk") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
        	else if (config.action == "editSaftycheckplan") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
        	else if (config.action == "editSaftycheckyearplan") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
        	else if (config.action == "editSaftycheckyearplanfb") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
        	else if (config.action == "editSaftycheckyinhuanpc") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
        	else if (config.action == "editTaizhangfb") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }else if (config.action == "editRiskfenbao") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            else if (config.action == "editRiskprodanger") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            else if (config.action == "editRisksafepg") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            else if (config.action == "editFenbaoyinhuanpczlgzfa") 
            {
        		var selRecs = gridDT.getSelectionModel().getSelection();
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
    		
        	
        };       
        
        //创建装载formPanel的窗体，由工具栏按钮点击显示
        var showWin = function (config) {
        	var width = 800;
        	var height = 500;
        	
        	if(title == '上级检查问题整改及回复'||title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查'||title=='安全生产目标检查与纠偏'||title=='分包方目标管理')
        	{//编辑提案信息框
        		width = 900;
        		height = 600;
        	}
        	
        	else if(title == '项目部安全检查计划')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	else if(title == '项目部年度安全检查计划')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	else if(title == '分包方年度安全检查计划')
        	{//编辑提案信息框
        		width = 800;
        		height = 500;
        	}
        	else if(title == '隐患排查治理年度工作方案')
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
        				btnchkdis.enable();
        				btnEdit.enable();       			
        				btnScan.enable();
        				btnAllotask.enable();
        			}
        			else
        			{
        				btnchkdis.disable();
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
            	//添加双击显示详细信息事件
                'celldblclick': function (self, td, cellIndex, record, tr, rowIndex)
        	    {
                	
                	
                	if(title == '上级检查问题整改及回复')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '上级检查问题整改及回复')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         var datestring = record.get("checktime").substring(0,4)+"年"+record.get("checktime").substring(5,7)+"月"+record.get("checktime").substring(8,10)+"日";
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+"详细信息</center></h1>"+ 
             		                   "<body><font  face=\"黑体\">&nbsp;&nbsp;&nbsp;编号：</font></body>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                   "<tr><td width=\"15%\" style=\"padding:5px;\">受检单位</td ><td colspan=\"3\">" +  record.get('shoujianunit') + 
             		                    "</tr><tr><td width=\"15%\" style=\"padding:5px;\">检查类型</td ><td>" + record.get("checktype") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">检查时间</td><td width=\"40%\">" +datestring + 
             		                   "</tr><tr><td width=\"15%\" style=\"padding:5px;\">检查人员</td ><td colspan=\"3\">" +  record.get('checkperson') + 
             		                  " </tr><tr><td width=\"15%\" style=\"padding:5px;\" align=\"left\" colspan=\"4\">检查情况记录及存在的主要问题与整改建议：<br/>"+"一、整改问题<br/>"+
             		                  strEdit(record.get('problem'),gridDT)+"二、建议<br/>"+strEditadv(record.get('advice'),gridDT)+"</td ></tr>"+
             		                 " <tr><td width=\"15%\" style=\"padding:5px;\" align=\"left\" colspan=\"4\">整改要求：<br/>"+"&nbsp;&nbsp;&nbsp;以上问题，请&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;项目部高度重视，认真整改，经总承包项目经理签批后，于&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;日前将整改情况（附验证资料）报院质量安全部。"+
             		                 "<br/><br/>"+"检查负责人（签字）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 受检单位负责人（签字）：<br/>检查人员（签字）："+"</td ></tr>";
//                                        "</td></tr><tr><td style=\"padding:5px;\">进度</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Progress') + 
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
               		      html_str+="&nbsp;&nbsp;&nbsp;本表一式两份，检查单位和被检查单位各存一份。"
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
                	
                	
                	
                	if(title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查'||title=='安全生产目标检查与纠偏'||title=='分包方目标管理')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '日常安全检查'||title == '定期安全检查'||title == '季节性安全检查'||title == '复工检查'||title == '专项安全检查'||title=='安全生产目标检查与纠偏'||title=='分包方目标管理')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		        var datestring = record.get("checktime").substring(0,4)+"年"+record.get("checktime").substring(5,7)+"月"+record.get("checktime").substring(8,10)+"日";
             		         //alert(record.get('pName'));
             		         html_str =  "<h1 style=\"padding: 5px;\"><center>"+"详细信息</center></h1>"+ 
   		                   "<body><font  face=\"黑体\">&nbsp;&nbsp;&nbsp;编号：</font></body>"+
		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
		                   "<tr><td width=\"15%\" style=\"padding:5px;\">受检单位</td ><td colspan=\"3\">" +  record.get('shoujianunit') + 
		                    "</tr><tr><td width=\"15%\" style=\"padding:5px;\">检查类型</td ><td>" + record.get("checktype") + 
		                    "</td><td width=\"15%\" style=\"padding:5px;\">检查时间</td><td width=\"40%\">" +datestring + 
		                   "</tr><tr><td width=\"15%\" style=\"padding:5px;\">检查人员</td ><td colspan=\"3\">" +  record.get('checkperson') + 
		                  " </tr><tr><td width=\"15%\" style=\"padding:5px;\" align=\"left\" colspan=\"4\">检查情况记录及存在的主要问题与整改建议：<br/>"+"一、整改问题<br/>"+
		                  strEdit(record.get('problem'),gridDT)+"二、建议<br/>"+strEditadv(record.get('advice'),gridDT)+"</td ></tr>"+
		                 " <tr><td width=\"15%\" style=\"padding:5px;\" align=\"left\" colspan=\"4\">整改要求：<br/>"+"&nbsp;&nbsp;&nbsp;以上问题，请&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（单位）高度重视，认真整改，经项目经理签批后，于&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;日前将整改情况（附验证资料）报总承包项目部。"+
		                 "<br/><br/>"+"检查负责人（签字）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 受检单位负责人（签字）：<br/>检查人员（签字）："+"</td ></tr>";
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
               		      html_str+="&nbsp;&nbsp;&nbsp;本表一式两份，检查单位和被检查单位各存一份。"
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
                	
                	
                	
                	
             
                	
                	
                	
                	if(title == '隐患排查治理台账')
                	{
                		var html_str = "";
                		if(title == '隐患排查治理台账')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		        var misfile = record.get('Accessory').split('*');
              		        
            				if(misfile.length>2)
              		        	var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
            				height = 500;
                		    width = 800;
            				html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td style=\"padding:5px;\">问题名称</td><td>" + record.get('problem') + "</td><td style=\"padding:5px;\">隐患存在地点</td><td>" +record.get('location')  + "</td></tr>" +
              		        "<tr><td style=\"padding:5px;\">要求时间</td><td>" + record.get('expTime') + "</td><td style=\"padding:5px;\">完成时间</td><td>" + record.get('solveTime') + "</td></tr>"+
              		        "<tr><td style=\"padding:5px;\">完成人</td><td>" + "张三" + "</td><td style=\"padding:5px;\">发现人（部门）</td><td>" + record.get('checkperson') + "</td></tr>"+
              		        "<tr><td style=\"padding:5px;\">隐患定级</td><td>" + record.get('prolevel') + "</td><td style=\"padding:5px;\">责任部门/单位</td><td>" + record.get('solvedep') + "</td></tr>"+
              		        "<tr><td style=\"padding:5px;\">整改费用(元)</td><td>" + record.get('correctionfee') + "</td><td style=\"padding:5px;\">监督人</td><td>" + record.get('supperson') + "</td></tr>"+
              		        "<tr><td style=\"padding:5px;\">整改完成情况</td><td colspan=\"3\" align=\"left\">"+record.get('solveExp')+"</td></tr>"+
              		        "<tr><td style=\"padding:5px;\">整改措施</td><td colspan=\"3\" align=\"left\">"+record.get('correction')+"</td></tr>"+
              		        "<tr><td style=\"padding:5px;\">未完成整改的预防措施</td><td colspan=\"3\" align=\"left\">"+record.get('prevent')+"</td></tr>";             		       
        				    var approview = record.get('solveAcc').split("*");
        				    if(approview.length>2)
              		        	var folderapp = 'upload\\'+approview[0]+'\\'+approview[1]+'\\';
              		        html_str+="<tr><td style=\"padding:5px;\">任务附件</td><td colspan=\"3\">";
              		      for(var i = 2;i<misfile.length;i++){
             		    	   html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
             		        }
             		        html_str += "</td></tr><tr><td style=\"padding:5px;\">完成附件</td><td colspan=\"3\">";
            		        for(var i = 2;i<approview.length;i++){
          		        	    html_str = html_str + approview[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+folderapp+approview[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
            		        }  
             		         
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
                	
                	
                	
                	if(title == '分包方隐患排查治理台账')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '分包方隐患排查治理台账')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">分包方名称</td ><td>" + record.get("fbname") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">台账年份</td><td width=\"40%\">" + record.get("year") +"</td></tr>";
                                        "</td></tr><tr><td style=\"padding:5px;\">台账月份</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('month') + "</td></tr>";
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
                	
                	
                	if(title == '项目安全检查计划')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '项目安全检查计划')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">编制人</td ><td>" + record.get("bzren") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">编制时间</td><td width=\"40%\">" + record.get("bztime") + 
             		                    "</td></tr><tr><td style=\"padding:5px;\">审批人</td><td>" + record.get('spren') + 
                                        "</td><td style=\"padding:5px;\">审批时间</td><td>" + record.get('sptime') + "</td></tr>";
//                                        "</td></tr><tr><td style=\"padding:5px;\">进度</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Progress') + 
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
                	
                	
                	
                	if(title == '项目部年度安全检查计划')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '项目部年度安全检查计划')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">编制人</td ><td>" + record.get("bzren") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">编制时间</td><td width=\"40%\">" + record.get("bztime") + 
             		                    "</td></tr><tr><td style=\"padding:5px;\">审批人</td><td>" + record.get('spren') + 
                                        "</td><td style=\"padding:5px;\">审批时间</td><td>" + record.get('sptime') + "</td></tr>";
//                                        "</td></tr><tr><td style=\"padding:5px;\">进度</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Progress') + 
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
                	
                	
                	if(title == '分包方年度安全检查计划')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '分包方年度安全检查计划')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">编制单位</td ><td>" + record.get("bzunit") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">编制时间</td><td width=\"40%\">" + record.get("bztime") + 
             		                   "<tr><td width=\"15%\" style=\"padding:5px;\">报备人</td ><td>" + record.get("bbren") + 
            		                    "</td><td width=\"15%\" style=\"padding:5px;\">报备时间</td><td width=\"40%\">" + record.get("bbtime") + 
             		                    "</td></tr><tr><td style=\"padding:5px;\">审批人</td><td>" + record.get('spren') + 
                                        "</td><td style=\"padding:5px;\">审批时间</td><td>" + record.get('sptime') + "</td></tr>";
//                                        "</td></tr><tr><td style=\"padding:5px;\">进度</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Progress') + 
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
                	
                	
                	
                	if(title == '隐患排查治理年度工作方案')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '隐患排查治理年度工作方案')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">年份</td ><td>" + record.get("year") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">文件名</td><td width=\"40%\">" + record.get("uploaduser") + 
                                        "</td></tr><tr><td style=\"padding:5px;\">上传时间</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('uploadtime')  + "</td></tr>";
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
                	
                	
                	
                	
                	if(title == '分包方危险源管理')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '分包方危险源管理')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">分包方名称</td ><td>" + record.get("fenbaoname") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">报备时间</td><td width=\"40%\">" + record.get("bbtime") + "</td></tr>";
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
                	
                	
                	if(title == '项目危险因素（危险源）辨识')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '项目危险因素（危险源）辨识')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">辨识类型</td ><td>" + record.get("bstype") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">辨识时间</td><td width=\"40%\">" + record.get("bstime") + "</td></tr>";
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
                	
                	
                	
                	if(title == '安全评估')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '安全评估')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">施工阶段</td ><td>" + record.get("shigstage") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">安全评估时间</td><td width=\"40%\">" + record.get("safeptime")+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">编制人</td ><td>" + record.get("bzperson") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">审核人</td><td width=\"40%\">" + record.get("shperson")+ "</td></tr>";
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
                	

                	if(title == '分包方隐患排查治理工作方案')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '分包方隐患排查治理工作方案')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">年份</td ><td>" + record.get("year") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">分包单位名称</td><td width=\"40%\">" + record.get("fenbaoname")+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">工作方案名称</td ><td>" + record.get("workname") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">文件名</td><td width=\"40%\">" + record.get("filename")+ 
                                        "</td></tr><tr><td style=\"padding:5px;\">上传时间</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('uploadtime') + "</td></tr>";
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