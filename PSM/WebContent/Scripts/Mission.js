Ext.define('MissionGrid', {
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
		var user = config.user;
		var gridDT;
		var column;
		var store;
		var forms;
		var queryURL;
		var projectName = config.projectName;
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
			
		 var fileMenu = new Ext.menu.Menu({
		 	shadow: "drop",
		 	allowOtherMenus: true,
		 	items: [
		 		new Ext.menu.Item({
				text: '暂无文件'
				   })
				]
		})
		
		//liuchi 统计分析
		var store_ZiliaoTongJi = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'projectName'},
                     { name: 'num0'},
                     { name: 'num1'},
                     { name: 'num2'},
                     { name: 'num3'},
                     { name: 'num4'},
                     { name: 'num5'},
                     { name: 'num6'},
                     { name: 'num7'},
                     { name: 'num8'},
                     { name: 'num9'},
                     { name: 'num10'},
                     { name: 'num11'},
                     { name: 'num12'},
                     { name: 'num13'},
                     { name: 'num14'},
                     { name: 'num15'},
                     { name: 'num16'},
                     { name: 'num17'},
                     { name: 'num18'},
                     { name: 'num19'},
                     { name: 'num20'},
                     { name: 'num21'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('DayManageAction!getZiliaoTongJi?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
		 function scanH (item){
		 	var hasfinish = 1;
		 	var foldname;
        	var selRecs = gridDT.getSelectionModel().getSelection();
        	var accessory;
        	var finishacc;
        	var finisharry = [];						
			fileMenu.removeAll();
			if(tableID == 251)
			{
				hasfinish = 2;
				accessory = selRecs[0].data.accessory;
				finishacc = selRecs[0].data.solveAcc;
				finisharry = finishacc.split("*");
			}
			else if(tableID == 240)
			{
				hasfinish = 2;
				accessory = selRecs[0].data.missionfile;
				finishacc = selRecs[0].data.finishfile;
				finisharry = finishacc.split("*");
			}
			else if(tableID == 248 || tableID == 239)
			{
				accessory = selRecs[0].data.approfile;
			}
			else if (tableID == 247) 
			{
				accessory = selRecs[0].data.missionfile;
			}
			console.log(hasfinish);
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
				console.log(hasfinish);
				for(var j = 0; j < hasfinish; j++){
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
                     fieldLabel: '编号',
                     labelAlign: 'left',
                     anchor:'95%',
                     name: 'ID',
                     hidden: true,
                     hiddenLabel: true
                 },{
 	            	xtype:'textfield',
                     fieldLabel: '任务名称',
                     labelAlign: 'right',
                    
                    // afterLabelTextTpl: required,	//红色星号
                     anchor:'95%',
                     name: 'missionname'
                 },{
                	xtype:'combo',
                    fieldLabel: '分数',
                    labelAlign: 'right',
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
                     labelAlign: 'right',
                     format:"Y-m-d",
                     name: 'fintime',
                     anchor:'100%'
                                               
 	             },{
 	            	 xtype:'textfield',
                     fieldLabel: '任务类型',
                     labelAlign: 'right',
                    // afterLabelTextTpl: required,	//红色星号
                     anchor:'95%',
                     name: 'missionfield'                    
	                 }]
 	        }]
 	    },{
        	xtype:'textareafield',
        	name:'missionexp',
        	fieldLabel:'任务说明', 
        	labelAlign: 'right',
        	anchor:'100%',
        	height:80       
        },{
 	    	xtype: 'container',
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
                    	 for(var i = nodeList.length-1;i>=0;i--)
                    	 {
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
                        
                      //   };
                    }
                    else{
                    	 var nodeList = [];
                    	 
                    	 snode = storeSelperson.getRootNode();                             
                         snode.cascadeBy(function(nod){                     
                         if(nod.get('text').indexOf("项目经理")>-1)
                        	nodeList.push(snode.indexOf(nod));                 
                    	 })                        	
                    	 for(var i = nodeList.length-1;i>=0;i--)
                    	 {
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
                        
                      //   };
                    }
                    else{
                    	 var nodeList = [];//删除所有项目安全节点
                    	 snode = storeSelperson.getRootNode();                             
                         snode.cascadeBy(function(nod){  
                         	
                         if(nod.get('text').indexOf("项目安全总监")>-1)
                         {
                         	alert(snode.indexOf(nod));
                        	nodeList.push(snode.indexOf(nod));    
                         }
                    	 })                   	 
                    	 for(var i = nodeList.length-1;i>=0;i--)
                    	 {
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
                         pnode.cascadeBy(function(node){ //遍历节点,删除已添加的节点                              
                         if(node.get('text').indexOf(parentName)>-1)
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
 	    },uploadPanel]
        
        var Edit_addAppro = [{
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
                     labelAlign: 'right',
                     anchor:'95%',
                     name: 'ID',
                     hidden: true,
                     hiddenLabel: true
                 },{
 	            	xtype:'textfield',
                     fieldLabel: '审批名称',
                     labelAlign: 'right',
                     afterLabelTextTpl: required,	//红色星号
                     anchor:'95%',
                     name: 'approname'
                 }]
 	        },{
 	        	xtype: 'container',
 	            flex: 1,
 	            layout: 'anchor',
 	            items: [{
 	            	 xtype:'textfield',
                     fieldLabel: '审批类型',
                     afterLabelTextTpl: required,
                     labelAlign: 'right',                 
                     name: 'approtype',                  
                     anchor:'100%'                                              
 	             }]
 	        }]
 	    },{
        	xtype:'textareafield',
        	name:'approexp',
        	fieldLabel:'审批说明', 
        	labelAlign: 'right',
        	anchor:'100%',
        	height:70       
        },uploadPanel]
        
	    var finishEditor = [{
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
                     labelAlign: 'right',
                     anchor:'95%',
                     name: 'ID',
                     hidden: true,
                     hiddenLabel: true
                 },{
 	            	xtype:'textfield',
                     fieldLabel: '任务名称',
                     labelAlign: 'right',
                     id:'missionname',
                     afterLabelTextTpl: required,	//红色星号
                     anchor:'95%',
                     name: 'missionname'
                 },{
                 	xtype:'textfield',
                     fieldLabel: '任务类型',
                     labelAlign: 'right',
                     afterLabelTextTpl: required,
                     anchor:'95%',
                     name: 'type',
                     allowBlank: false
                 }]
 	        },{
 	        	xtype: 'container',
 	            flex: 1,
 	            layout: 'anchor',
 	            items: [{
 	            	 xtype:"datefield",
                     fieldLabel: '要求时间',
                     afterLabelTextTpl: required,
                     labelAlign: 'right',
                     readOnly : true,
                     format:"Y-m-d",
                     name: 'fintime',
                     anchor:'100%'
                                               
 	             },{
                	xtype:'datefield',
                    fieldLabel: '完成时间',
                    name:'truefistime',
                    afterLabelTextTpl: required,
                    labelAlign: 'right',
                    anchor:'100%',     
                    readOnly : true,
                    format:"Y-m-d",
	        		value: new Date()
                 }]
 	        }]
 	    },{
        	xtype:'textareafield',
        	name:'finishSit',
        	fieldLabel:'完成情况', 
        	labelAlign: 'right',
        	anchor:'100%',
        	height:70       
        },uploadPanel]
	    
        var finishDisEditor = [{
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
                     labelAlign: 'right',
                     anchor:'95%',
                     name: 'ID',
                     hidden: true,
                     hiddenLabel: true
                 },{
 	            	xtype:'textfield',
                     fieldLabel: '问题名称',
                     labelAlign: 'right',
                     afterLabelTextTpl: required,	//红色星号
                     anchor:'95%',
                     name: 'problem'
                 },{
                 	xtype:'textfield',
                     fieldLabel: '整改费用(元)：',
                     labelAlign: 'right',
                     afterLabelTextTpl: required,
                     anchor:'95%',
                     name: 'correctionfee',
                     allowBlank: false
                 }]
 	        },{
 	        	xtype: 'container',
 	            flex: 1,
 	            layout: 'anchor',
 	            items: [{
                 	xtype:'datefield',
                    fieldLabel: '完成时间',
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    anchor:'95%',
                    format:"Y-m-d",
                    value:new Date(),
                    name: 'solveTime',
                    allowBlank: false
                },{
                 	xtype:'textfield',
                     fieldLabel: '监督人',
                     labelAlign: 'right',                 
                     anchor:'95%',
                     name: 'supperson'              
                 }]
 	        }]
 	    },{
        	xtype:'textareafield',
        	name:'solveExp',
        	fieldLabel:'整改完成情况', 
        	labelAlign: 'right',
        	anchor:'100%',
        	height:70       
        },{
        	xtype:'textareafield',
        	name:'correction',
        	fieldLabel:'整改措施', 
        	labelAlign: 'right',
        	anchor:'100%',
        	height:70       
        },{
        	xtype:'textareafield',
        	name:'prevent',
        	fieldLabel:'未完成整改的预防措施', 
        	labelAlign: 'right',
        	anchor:'100%',
        	height:70       
        },uploadPanel]
        
        
        var Editor_checkAppro = [{
        	xtype:'textareafield',
        	name:'approview',
        	fieldLabel:'审查意见', 
        	labelAlign: 'right',
        	anchor:'100%',
        	height:100       
        }]
        
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
        
        var getBLen = function(str) {
  			if (str == null) return 0;
  			if (typeof str != "string"){
   			str += "";
 			}
 			return str.replace(/[^\x00-\xff]/g,"01").length;
		}
        
        
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
       	
	   var insertFileToList = function()
      	{
        	var info_url = '';
        	var delete_url = ''; 
        	var selRecs = gridDT.getSelectionModel().getSelection();
			info_url = 'MissionAction!getFileInfo';
			delete_url = 'MissionAction!deleteOneFile';
			var existFile = selRecs[0].data.accessory.split('*');
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
	        
	        
	    var finishMis = function(){
	    	var title = "完成任务";
	    	//var hint = "请添加完成文件！";
	    	var selRecs = gridDT.getSelectionModel().getSelection();
	    	if(selRecs[0].data.state == '1'){
	    		Ext.Msg.alert('提示','该任务已完成！');
	    	}
	    	else if(selRecs[0].data.state == '0'){
	    		Ext.Msg.alert('提示','该任务已超期！');
	    	}
	    	else{	    		
	    		var url = 'MissionAction!alloTask?title='+title+'&folder='+selRecs[0].data.missionfile+'&user='+user.name;
	    		createMissionForm(title,finishEditor,url);
	    	}
	    } 
	    
	    var finishDis = function(){
	    	var title = "完成排查";
	    	//var hint = "请添加完成文件！";
	    	var selRecs = gridDT.getSelectionModel().getSelection();
	    	if(selRecs[0].data.solveTime == '已过期'){
	    		Ext.Msg.alert('提示','该任务已过期！');
	    	}
	    	else if(selRecs[0].data.solveTime == '进行中'){		
	    		var url = 'MissionAction!finishDis?id='+selRecs[0].data.ID;
	    		createMissionForm(title,finishDisEditor,url);
	    	}
	    	else{
	    		Ext.Msg.alert('提示','该任务已完成！');
	    	}
	    }
	    
	    var editDis = function(){
	    	insertFileToList();
	        var title = "编辑任务";
	    	//var hint = "请添加完成文件！";
	    	var selRecs = gridDT.getSelectionModel().getSelection();
	    	if(selRecs[0].data.solveTime == '已过期'){
	    		Ext.Msg.alert('提示','该任务已过期！');
	    	}
	    	else if(selRecs[0].data.solveTime == '进行中'){		
	    		Ext.Msg.alert('提示','请先完成任务！');
	    	}
	    	else{
	    		var url = 'MissionAction!finishDis?id='+selRecs[0].data.ID;
	    		createMissionForm(title,finishDisEditor,url);
	    	}
	    }
	    
	    var addMis = function(){
	    	var title = "分配任务";
	    	
	    	var url = 'MissionAction!alloTask?title='+title+'&user='+user.name;
	    	createMissionForm(title,addMissionEditor,url);
	    }

	    var delMis = function(){
	    	if(getSel(gridDT)) {
        		Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) {
    				if (buttonID === 'yes') {
    					var delete_url = 'MissionAction!deleteTask';
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
    			})
        	}        
	    }
	    
	    
	    var addAppro = function(){
	    	var selRecs = gridDT.getSelectionModel().getSelection();
	    	var title = "请求审批";
	    	var url = 'MissionAction!addAppro?title='+title+'&user='+user.name;
	    	createMissionForm(title,Edit_addAppro,url);
	    }
	    
	    var rejectAppro = function(){
	    	var selRecs = gridDT.getSelectionModel().getSelection();
	    	var title = "驳回";
	    	var url = 'MissionAction!checkAppro?title='+title+'&id='+selRecs[0].data.ID+'&person=admin';
	    	createMissionForm(title,Editor_checkAppro,url);
	    }
	    var aggreeAppro = function(){
	    	var selRecs = gridDT.getSelectionModel().getSelection();
	    	var title = "同意";
	    	var url = 'MissionAction!checkAppro?title='+title+'&id='+selRecs[0].data.ID+'&person=admin';
	    	createMissionForm(title,Editor_checkAppro,url);
	    } 
	    var createMissionForm = function(title,items,url){		
			forms = Ext.create('Ext.form.Panel', {
 				minWidth: 200,
 				minHeight: 100,
 				bodyPadding: 5,
	 			anchor:'100%',	
 				buttonAlign: 'center',
 				layout: 'anchor',
 		        frame: false,
 		        defaultType:'textfield',
 		        items: items,
 		        buttons: [{
 		        	text: '确定',
 		        	handler: function() {
 		        		var fileName = getFileName(); 		        		 		        		
 		        		if(forms.form.isValid()){
 		        			if(title == "分配任务"){
 		        					var properson="";
                				    var tnode = storeSelperson.getRootNode(); 
                			        tnode.cascadeBy(function(node){ //遍历节点                      
                			            properson = properson+node.get('text')+',';
                			        }); 
                			        properson = properson.substring(4,properson.length);
 		        				url=url+'&properson='+properson+'&proName='+ forms.getForm().findField('missionname').getValue(); 		        				
 		        			}		        			
 		        			forms.form.submit({
 		        				clientValidation: true,
 		        				url:encodeURI(url+'&fileName='+fileName),
 		  	                	success: function(form, action){	  	                	
 		  	                		store.load({params:{start:0,limit:psize}});	
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
 		   var selRecs = gridDT.getSelectionModel().getSelection(); 
 		   if(title == "完成任务"){		  
 			  forms.getForm().findField('missionname').setValue(selRecs[0].data.missionname);
 			  forms.getForm().findField('fintime').setValue(selRecs[0].data.fintime);
 			  forms.getForm().findField('type').setValue(selRecs[0].data.type);
 			  forms.getForm().findField('ID').setValue(selRecs[0].data.ID);
 		   }
 		   else if(title == "完成排查"){			
 			  forms.getForm().findField('problem').setValue(selRecs[0].data.problem);
 			  forms.getForm().findField('ID').setValue(selRecs[0].data.ID);
 		   }
 		   else if(title == "编辑任务"){
 		       forms.getForm().loadRecord(selRecs[0]);
 		   }
			var height = 0;
			var width = 0;
			if(tableID == 239){
				height = 200;
				width = 500
			}
			else if(tableID == 240){
				height = 650;
				width = 850;
			}
			else{
				height = 500;
				width = 800;
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
                items:forms,
                listeners: {
                    'beforeclose': function (me) {              		
                        uploadPanel.store.removeAll();
                    }
                }
                }).show();
		}
	        
	    var btnFinishMis = Ext.create('Ext.Button', {
        	width: 80,
        	height: 32,
        	text: '完成任务',
            icon: "Images/ims/toolbar/restore.gif",
            handler: finishMis
        })
        
        var btnAddMis = Ext.create('Ext.Button', {
        	width: 80,
        	height: 32,
        	text: '临时任务',
            icon: "Images/ims/toolbar/add.gif",
            handler: addMis
        })
        
        var btnDelMis = Ext.create('Ext.Button', {
        	width: 80,
        	height: 32,
        	disabled: true,
        	text: '撤销任务',
            icon: "Images/ims/toolbar/delete.gif",
            handler: delMis
        })
	    
        var btnFinishDis = Ext.create('Ext.Button', {
        	width: 80,
        	height: 32,
        	text: '完成排查',
            icon: "Images/ims/toolbar/restore.gif",
            handler: finishDis
        })
        
        
        var menu = Ext.create('Ext.menu.Menu', {
        style: {
            overflow: 'visible'     // For the Combo popup
        },
        items: [
              {
                text: '所有',
                flag:'',
                listeners:
				{
					'click': function (item, e, eOpts) {
						menuMissionClick(item.flag);
						}
				}
           },'-',{   	  
               text: '进行中',  
               flag:'2',
               listeners:
				{
					'click': function (item, e, eOpts) {
						menuMissionClick(item.flag);
						}
				}
           },'-',{
               text: '已完成',
               flag:'1',
               listeners:
				{
					'click': function (item, e, eOpts) {
						menuMissionClick(item.flag);
						}
				}
           },'-',{
               text: '未完成',
               flag:'0',
               listeners:
				{
					'click': function (item, e, eOpts) {
						menuMissionClick(item.flag);
						}
				}
           }
        ]
    });
	    
	    var menuMissionClick = function(text){	    			 
	        	store.getProxy().url = encodeURI('MissionAction!mymission?data='+text+'&user='+user.name);
	        	store.load({params: { start: 0, limit: psize }});
	        	bbar.moveFirst();  					
	    }
        
        
	    var tbar = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        })
		var store_Mission = Ext.create('Ext.data.Store', { 
        	fields: [
        	         { name: 'ID'},
                     { name: 'progroup'},
                     { name: 'allpro'},
                     { name: 'isfinished'},
                     { name: 'underfinished'},
                     { name: 'unfinished',type:'int'},
                     { name: 'subscore'},
                     { name: 'totalscore'}
            ],
            sorters : [{
                property : 'unfineshed', // 指定要排序的列索引
                direction : 'DESC' // 降序，  ASC：升序
            }],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('MissionAction!totalmission?projectName=' + (user.role == '全部项目' ? '全部项目' : projectName )),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });	
	    
	    var store_myMission  = Ext.create('Ext.data.Store', {  
        	fields: [
        	         { name: 'ID'},
        	         { name: 'progroup'},
                     { name: 'missionname'},
                     { name: 'missionstate'},
                     { name: 'fintime'},
                     { name: 'properson'},
                     { name: 'missionfile'},
                     { name: 'type'},
                     { name: 'finishfile'},
                     { name: 'truefintime'},
                     { name: 'score'},
                     { name: 'finsit'},
                     { name: 'misexp'},
                     { name: 'state',type:'int'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('MissionAction!mymission?data=所有&user='+user.name),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });	
	    
	    var store_Approval  = Ext.create('Ext.data.Store', {  
        	fields: [
        	         { name: 'ID'},
                     { name: 'approname'},
                     { name: 'approtype'},
                     { name: 'approtime'},
                     { name: 'approexp'},
                     { name: 'approperson'},
                     { name: 'approfile'},
                     { name: 'approstate'}, 
                     { name: 'approview'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('MissionAction!myApproval?data=所有'),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });	
	    
	    var store_taizhang = Ext.create('Ext.data.Store', {  
        	fields: [
        	         { name: 'ID'},
                     { name: 'problem'},
                     { name: 'expTime'},
                     { name: 'solveTime'},
                     { name: 'solveAcc'},
                     { name: 'accessory'},
                     { name: 'correction'},
                     { name: 'correctionfee'},
                     { name: 'supperson'},
                     { name: 'prevent'},
                     { name: 'solveExp'},   
                     { name: 'location'},   
                     { name: 'checkperson'},   
                     { name: 'prolevel'},   
                     { name: 'solvedep'},
                     { name: 'solvePerson'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('MissionAction!mytaizhang?user='+user.name),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            }
        });	
	    
	    
	    
	    
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
             //       	searchH();                
                    }  
                } 
            }

        })
        
          var btnSearch = Ext.create('Ext.Button', {
	    	width: 55,
	    	height: 32,
	    	text: '查询',
	        icon: "Images/ims/toolbar/search.png"
//	        handler: searchH
        })
        var btnSearchR = Ext.create('Ext.Button', {
        	width: 105,
        	height: 32,
        	text: '在结果中查询',
            icon: "Images/ims/toolbar/search.png",
            disabled: true
        //    handler: searchR
        })
        var btnAddAppro = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '新增',
            icon: "Images/ims/toolbar/group.png",
            handler: addAppro
        })
	    var btnRejectAppro = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '驳回',
            icon: "Images/ims/toolbar/delete.gif",
            handler: rejectAppro
        })
        var btnAggreeAppro = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '同意',
            icon: "Images/ims/toolbar/restore.gif",
            handler: aggreeAppro
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
        
        var btnEdit = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '编辑',
        	disabled:true,
            //tooltip: '编辑一条记录',
           	icon: "Images/ims/toolbar/edit.png",
            handler: editDis
        })
        
        var comboProject = Ext.create('Ext.form.ComboBox', {
			store: Ext.create('Ext.data.Store', {
				fields: [
					 { name: 'ID'},
					 { name: 'Name'}
				],
				proxy: {
					type: 'ajax',
					url: encodeURI('EduTrainAction!getProjectNameList?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName),
					reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
						type: 'json', //返回数据类型为json格式
						root: 'rows',  //数据
						totalProperty: 'total' //数据总条数
					}
				}
			}),
			displayField: 'Name',
			valueField: "Name",
			editable: false,
			autoSelect: true,
			listeners: {   
				render: function(t, eOpts) {
					t.getStore().on("load", function(s, r, o) {
						t.setValue(r[0].get('Name'));
					});
					t.getStore().load();
				},
				change: function(combo, records, eOpts) {
					store.on('beforeload', function (store, options) {
						Ext.apply(store.proxy.extraParams, { group: combo.getValue() }); 
					});
					bbar.moveFirst();
				}
			}
		});
        
        
        // 任务管理的6个分类
        var stateGroup = Ext.create('Ext.form.ComboBox', {
        	store: ['全部阶段', '开工准备阶段','在建阶段','收尾移交阶段','完工总结阶段'],
        	listeners: {   
				render: function(t, eOpts) {
					t.setValue(t.getStore().data.get(0))
				},
				change: function(combo, records, eOpts) {
					// 首先全部隐藏
					for (var i = 3; i < gridDT.columns.length; i++)
						gridDT.columns[i].hide();
					// 显示符合要求的
					Ext.Ajax.request({
					    async: false, 
			            method : 'POST',
			            url: encodeURI('MissionAction!getsomeprojectname?type='+combo.getValue()),
			            success: function(response){
			            	var someprojectname = Ext.decode(response.responseText); //返回监控点列表
			            	for (var i = 3; i < gridDT.columns.length; i++)
								if (gridDT.columns[i].text in someprojectname)
									gridDT.columns[i].show();
			            }
		     		});        	
					
				}
			}
        })
        
	    if(tableID == 207){
			store = store_Mission;
			store.load();
			tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add(btnSearchR);
			column = [
	            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	         
	            	    { text: '项目部名称', dataIndex: 'progroup', align: 'center', width: 110},
	            	    { text: '下达任务', dataIndex: 'allpro', align: 'center', width: 110},
	            	    { text: '已完成', dataIndex: 'isfinished', align: 'center', width: 110},
	            	    { text: '进行中', dataIndex: 'underfinished', align: 'center', width: 110},
	            	    { text: '未完成', dataIndex: 'unfinished', align: 'center', width: 140},
	            	    { text: '扣分', dataIndex: 'subscore', align: 'center', width: 110},
	            	    { text: '总分', dataIndex: 'totalscore', align: 'center', width: 100}          
	            	]
	    }
	    else if(tableID == 240 || tableID == 247){
	    	store = store_myMission;
	    	
	    	if(tableID == 240)
	    	{
	    		store.getProxy().url = encodeURI('MissionAction!getGroupMis');
				if (user.role == '全部项目') {
					tbar.add(comboProject);				
				} else {
					Ext.apply(store.proxy.extraParams, { group: projectName }); 
				}
	    		tbar.add(textSearch);
				tbar.add(btnSearch);
				tbar.add(btnSearchR);
	    		tbar.add(btnAddMis);
	    		tbar.add(btnDelMis);
	    		tbar.add(btnScan);
	    	}
	    	else if(tableID == 247)
	    	{
	    		store.getProxy().url = encodeURI('MissionAction!mymission?data=所有&user='+user.name);
	    		tbar.add(textSearch);
				tbar.add(btnSearch);
				tbar.add(btnSearchR);
	    		tbar.add(btnFinishMis);
	    		tbar.add(btnScan);
//		    	tbar.add(btnAddMis);
		    
		    	tbar.add({
		                text:'选择分类',
		                width: 125,
		            	height: 28,
		                icon:"Images/ims/toolbar/view.png",         
		                menu: menu  // assign menu by instance
		            });
		        tbar.add(btnScan);
		    	store.load();
	    	}
	    	column = [
	            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	         
	            	    { text: '项目部名称', dataIndex: 'progroup', align: 'center', width: 110},
	            	    { text: '任务名称', dataIndex: 'missionname', align: 'center', width: 110},
	            	    { text: '任务状态', dataIndex: 'missionstate', align: 'center', width: 110},
	            	    { text: '任务类型', dataIndex: 'type', align: 'center', width: 110},
	            	    { text: '要求时间', dataIndex: 'fintime', align: 'center', width: 110},
	            	    { text: '完成时间', dataIndex: 'truefintime', align: 'center', width: 140},
	            	    { text: '扣分', dataIndex: 'score', align: 'center', width: 110}       
	            	 ]
	    }
	    else if(tableID == 248 || tableID == 239){
	    	store = store_Approval;
	    	tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add(btnSearchR);
			if(tableID == 248)
			{
				store.getProxy().url = 'MissionAction!myApproval?user='+user.name+'&data=个人';
				tbar.add(btnAddAppro);
			}
			else if(tableID == 239){
				tbar.add(btnAggreeAppro);
				tbar.add(btnRejectAppro);
			}
			//if(tableID ==)
	    	tbar.add(btnScan);
	    	store.load();
	    	column = [
	    	          { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	         
            	      { text: '审批名称', dataIndex: 'approname', align: 'center', width: 110},
            	      { text: '审批类型', dataIndex: 'approtype', align: 'center', width: 110},
            	      { text: '审批状态', dataIndex: 'approstate', align: 'center', width: 110},
            	      { text: '审批时间', dataIndex: 'approtime', align: 'center', width: 110}              
	    	         ]
	    }
	    
	    else if(tableID == 238 || tableID == 291){
	    	var allprojectname;
	    	var field = [{name:'proname'}];
    		column = [{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
    	              { text: '项目名称', dataIndex: 'proname', align: 'center', width: 110}];
	    	Ext.Ajax.request({
		    async: false, 
            method : 'POST',
            url: encodeURI('MissionAction!getallprojectname'),
            success: function(response){
        	allprojectname = Ext.decode(response.responseText); //返回监控点列表
       		 }
     		});        	
	        for ( var p in allprojectname )
	        {
	        	field.push({name:p});
	        	column.push({text:p,dataIndex:p,align:'center',width: 150});
	        }
	    	store = Ext.create('Ext.data.Store', {  
	    	fields: field,
	        pageSize: psize,  //页容量20条数据
	        proxy: {
	            type: 'ajax',
	            url: encodeURI('MissionAction!totalbymis'),
	            reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
	                type: 'json', //返回数据类型为json格式
	                root: 'rows',  //数据
	                totalProperty: 'total' //数据总条数
	            }
	        }
	 	   });	
	    	store.load();
	    	tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add(btnSearchR);
			tbar.add(stateGroup);
	    }
	    else if(tableID == 251){
	    	store = store_taizhang;
	    	store.load();
	    	tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add(btnSearchR);
			tbar.add(btnEdit);
			tbar.add(btnFinishDis);
			tbar.add(btnScan);
			
	    	column = [
	                  { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
	    	          { text: '存在的安全隐患', dataIndex: 'problem', align: 'center', width: 110},
	    	          { text: '隐患存在地点', dataIndex: 'location', align: 'center', width: 110},
	    	          { text: '发现人(部门)', dataIndex: 'checkperson', align: 'center', width: 110},
	    	          { text: '隐患定级', dataIndex: 'prolevel', align: 'center', width: 110},
	    	          { text: '责任部门/单位', dataIndex: 'solvedep', align: 'center', width: 110},
	    	          { text: '责任人', dataIndex: 'solvePerson', align: 'center', width: 110},
	            	  { text: '计划完成整改时间', dataIndex: 'expTime', align: 'center', width: 110},
	            	  { text: '整改费用(元)', dataIndex: 'correctionfee', align: 'center', width: 110},
	            	  { text: '完成时间', dataIndex: 'solveTime', align: 'center', width: 110},	       	  
	            	  { text: '监督人', dataIndex: 'supperson', align: 'center', width: 110}	            	
	    	         ]
	    }
	    else if(tableID == 290) {
	    	store = store_ZiliaoTongJi;
	    	store.load();
	    	column = [
	                  { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
	    	          { text: '项目部名称', dataIndex: 'projectName', align: 'center', width: 400},
	    	          { text: '安全生产工作策划', dataIndex: 'num0', align: 'center', width: 200},
	    	          { text: '准备阶段安全评估', dataIndex: 'num1', align: 'center', width: 200},
	    	          { text: '土建施工阶段安全评估', dataIndex: 'num2', align: 'center', width: 200},
	    	          { text: '安装调试阶段安全评估', dataIndex: 'num3', align: 'center', width: 200},
	    	          { text: '试运行阶段安全评估', dataIndex: 'num4', align: 'center', width: 200},
	            	  { text: '总体安全生产目标', dataIndex: 'num5', align: 'center', width: 200},
	            	  { text: '年度安全生产目标', dataIndex: 'num6', align: 'center', width: 200},
	            	  { text: '目标分解', dataIndex: 'num7', align: 'center', width: 110},	       	  
	            	  { text: '安全生产投入计划', dataIndex: 'num8', align: 'center', width: 200},
	            	  { text: '培训计划', dataIndex: 'num9', align: 'center', width: 110},
	    	          { text: '安全检查计划', dataIndex: 'num10', align: 'center', width: 200},
	    	          { text: '标准化创建方案', dataIndex: 'num11', align: 'center', width: 200},
	    	          { text: '标准化自评报告', dataIndex: 'num12', align: 'center', width: 200},
	    	          { text: '隐患排查治理工作方案', dataIndex: 'num13', align: 'center', width: 200},
	            	  { text: '安全工作日志', dataIndex: 'num14', align: 'center', width: 200},
	            	  { text: '安全周报', dataIndex: 'num15', align: 'center', width: 110},
	            	  { text: '安全生产管理信息月报表', dataIndex: 'num16', align: 'center', width: 200},
	            	  { text: '事故隐患排查治理台账', dataIndex: 'num17', align: 'center', width: 200},
	            	  { text: '节能减排统计监测报表', dataIndex: 'num18', align: 'center', width: 200},
	            	  { text: '半年度安全工作总结', dataIndex: 'num19', align: 'center', width: 200},
	            	  { text: '年度安全工作总结', dataIndex: 'num20', align: 'center', width: 200},
	            	  { text: '完工安全工作总结', dataIndex: 'num21', align: 'center', width: 200}
	    	         ]
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
	    
			gridDT = Ext.create('Ext.grid.Panel', {       
	    		selModel: new Ext.selection.CheckboxModel({ selType: 'checkboxmodel' }),   //选择框
	    		store: store,
	    		stripeRows: true,
	    		columnLines: true,
	    		tbar: tbar,
		        bbar: bbar,
	            columns: column,
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
	        				btnFinishDis.enable();	
	        				btnScan.enable();
	        				btnFinishMis.enable();	
	        				btnEdit.enable();
	        			}
	        			else
	        			{
	        				btnFinishDis.disable();
	        				btnScan.disable();
	        				btnFinishMis.disable();
	        				btnEdit.disable();
	        			}
	        			//多选的按钮
	        			if(selRecs.length >= 1)
	        			{
	        				btnDelMis.enable();
	        			}
	        			else
	        			{
	        				btnDelMis.disable();
	        			}
	        			
	            	},
	            	'celldblclick': function (self, td, cellIndex, record, tr, rowIndex)
	        	    {             	
	                		var html_str = "";    
	                		var height;
	                		var width;
	                		var scanfileName;
	                		var displayfileName;
                			var record = store.getAt(rowIndex); 
                			if(tableID == 247||tableID == 240){
                		    height = 400;
                		    width = 800;
             		        html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td style=\"padding:5px;\">任务名称</td><td>" + record.get('missionname') + "</td><td style=\"padding:5px;\">任务状态</td><td>" + record.get('missionstate') + "</td></tr>" +
             		        "<tr><td style=\"padding:5px;\">要求时间</td><td>" + record.get('fintime') + "</td><td style=\"padding:5px;\">完成时间</td><td>" + record.get('truefintime') + "</td></tr>"+
             		        "<tr><td style=\"padding:5px;\">任务类型</td><td>" + record.get('type') + "</td><td style=\"padding:5px;\">完成人</td><td>" + record.get('properson') + "</td></tr>"+
             		        "<tr><td style=\"padding:5px;\">任务说明</td><td colspan=\"3\" align=\"left\">"+record.get('misexp')+"</td></tr><tr><td style=\"padding:5px;\">完成情况</td><td colspan=\"3\" align=\"left\">"+record.get('finsit')+"</td></tr>"+
             		        "<tr><td style=\"padding:5px;\">任务附件</td><td colspan=\"3\">";
             		       // "<tr><td style=\"padding:5px;\"><a href=\"http://www.w3school.com.cn\"  target=\"_blank\">W3School</a><br><a href=\"http://www.w3school.com.cn\">W3School</a></td><td></td><td style=\"padding:5px;\">规模</td><td></td></tr>";
             		        var misfile = record.get('missionfile').split('*');
             		        
             		        var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
             		        
             		        var finishfile = record.get('finishfile').split('*');
             		        
             		        var folderfis = "";
             		        
             		        if(!(finishfile.length<2)){
             		        	folderfis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
             		        }        		       
             		        for(var i = 2;i<misfile.length;i++){
             		        	
             		        	scanfileName = getScanfileName(misfile[i]); 
             		        	displayfileName = misfile[i];
                        		if(getBLen(scanfileName)>10)
                        			displayfileName = displayfileName.substring(0,7)+"···";
             		        	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
             		        }
             		       html_str += "</td></tr><tr><td style=\"padding:5px;\">完成附件</td><td colspan=\"3\">";
             		        for(var i = 2;i<finishfile.length;i++){
             		        	scanfileName = getScanfileName(finishfile[i]); 
                        		displayfileName =finishfile[i];
                        		if(getBLen(scanfileName)>10)
                        			displayfileName = displayfileName.substring(0,7)+"···";   		
           		        	    html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+folderfis+finishfile[i]+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+folderfis+finishfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
             		        }
                          //  "<tr><td style=\"padding:5px;\">编号</td><td>" + record.get('No') + "</td><td style=\"padding:5px;\">规模</td><td>" + record.get('Scale') + "</td></tr>";
                                             
             		        html_str += "</td></tr></table>";
                			}else if(tableID == 207){ 
                				var mission;
                				var scanfileName;
                				height = 600;
                				width = 1000;
                		        html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\"\">任务名称</td><td width=\"10%\"\">负责人</td><td width=\"10%\">任务状态</td><td width=\"10%\">要求时间</td><td width=\"10%\">完成时间</td>" +
                 		        "<td width=\"20%\">任务附件</td><td width=\"20%\">完成附件</td>"
                 		        "<tr><td style=\"padding:5px;\">任务附件</td></tr>";
                		        Ext.Ajax.request({                           
                					async: false, 
                			        method : 'POST',
                			        url: encodeURI('MissionAction!getGroupMis'),
                			        params:{
                			        	group:record.get('progroup')
                			        },
                			        success: function(response){   
                			        	    var test = record.get('progroup');
                			             	mission = Ext.decode(response.responseText); 
                			        }
                			     });              
                		        for(var i = 0;i<mission.length;i++){            		     
                		        	html_str = html_str+"<tr><td width=\"10%\"\">"+mission[i].missionname+"</td><td width=\"10%\"\">"+mission[i].properson+"</td><td width=\"10%\">"+mission[i].missionstate+"</td><td width=\"10%\">"+mission[i].fintime+"</td><td width=\"10%\">"+mission[i].downtime+"</td>";
                 		        	var misfile = mission[i].missionfile.split('*');
                 		        	var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
                 		        	var finishfile = mission[i].finishfile.split('*');                    	
                 		        	var folderfis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
                 		        	html_str+="<td width=\"25%\"\">";              		        	
                      		        for(var j = 2;j<misfile.length;j++){
                      		        	scanfileName = getScanfileName(misfile[j]);
                      		            displayfileName =misfile[j];
                        		        if(getBLen(scanfileName)>10)
                        			   		displayfileName = displayfileName.substring(0,7)+"···";   
                      		        	html_str = html_str +displayfileName +"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[j]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
                      		        }
                      		        html_str+="</td>";
                      		        html_str+="<td width=\"25%\"\">";                     		        
                  		        	for(var j = 2;j<finishfile.length;j++){
                  		        		scanfileName = getScanfileName(finishfile[j]);
                  		        		displayfileName =finishfile[j];
                        		        if(getBLen(scanfileName)>10)
                        			   		displayfileName = displayfileName.substring(0,7)+"···"; 
                  		        		html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+folderfis+finishfile[j]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
                  		      		}
                    		        html_str+="</td></tr>";
                		        }
                		        html_str+="</table>";
                		       
                			}else if(tableID == 248||tableID == 239){
                				var misfile = record.get('approfile').split('*');
                  		        var scanfileName;
                  		        var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
                				height = 350;
                    		    width = 800;
                				html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td style=\"padding:5px;\">审批名称</td><td>" + record.get('approname') + "</td><td style=\"padding:5px;\">请求审批人</td><td>" + record.get('approperson') + "</td></tr>" +
                  		        "<tr><td style=\"padding:5px;\">审批类型</td><td>" + record.get('approtype') + "</td><td style=\"padding:5px;\">审批状态</td><td>" + record.get('approstate') + "</td></tr>"+
                  		        "<tr><td style=\"padding:5px;\">审批说明</td><td colspan=\"3\" align=\"left\">"+record.get('approexp')+"</td></tr>";
                  		       
            				    var approview = record.get('approview').split("**");
                 		        for(var i = 0;i<approview.length;i++){
                 		    	   html_str+="<tr><td style=\"padding:5px;\">审查意见</td><td colspan=\"3\" align=\"left\">"+approview[i]+"</td></tr>";
                 		        }
                 		        html_str += "</td></tr><tr><td style=\"padding:5px;\">任务附件</td><td colspan=\"3\">";
                		        for(var i = 2;i<misfile.length;i++){
                		        	scanfileName = getScanfileName(misfile[i]);
                		        	displayfileName =misfile[i];
                        		    if(getBLen(scanfileName)>10)
                        			   displayfileName = displayfileName.substring(0,7)+"···"; 
              		        	    html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
                		        }                       		 
                			}else if(tableID == 251){
                				var misfile = record.get('accessory').split('*');
                  		        
                				if(misfile.length>2)
                  		        	var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
                				height = 500;
                    		    width = 800;
                				html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td style=\"padding:5px;\">问题名称</td><td>" + record.get('problem') + "</td><td style=\"padding:5px;\">隐患存在地点</td><td>" +record.get('location')  + "</td></tr>" +
                  		        "<tr><td style=\"padding:5px;\">要求时间</td><td>" + record.get('expTime') + "</td><td style=\"padding:5px;\">完成时间</td><td>" + record.get('solveTime') + "</td></tr>"+
                  		        "<tr><td style=\"padding:5px;\">完成人</td><td>" + record.get('solvePerson') + "</td><td style=\"padding:5px;\">发现人（部门）</td><td>" + record.get('checkperson') + "</td></tr>"+
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
                 		           scanfileName = getScanfileName(misfile[i]);
                 		           displayfileName =misfile[i];
                        		   if(getBLen(scanfileName)>15)
                        			  displayfileName = displayfileName.substring(0,12)+"···"; 
                 		    	   html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
                 		        }
                 		        html_str += "</td></tr><tr><td style=\"padding:5px;\">完成附件</td><td colspan=\"3\">";
                		        for(var i = 2;i<approview.length;i++){
                		        	scanfileName = getScanfileName(approview[i]);
                		        	displayfileName =misfile[i];
                        		    if(getBLen(scanfileName)>10)
                        			   displayfileName = displayfileName.substring(0,7)+"···"; 
              		        	    html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+folderapp+approview[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
                		        }     
                			}
                			if(tableID>=206&&tableID<=207 || tableID>=247&&tableID<=248||tableID >=239&&tableID<=240||tableID == 251){
	                        Ext.create('Ext.window.Window', 
	                        {
	                           title: '查看详情',
	                           titleAlign: 'center',
	                           height: height,
	                           width: width,
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
	    var containerType = container.getXType();    
		 if (containerType === "tabpanel") {
	         panel.add(gridDT);
	         
	         container.add(panel).show();
	     } else {
	         container.add(gridDT);
	     }  
}
});