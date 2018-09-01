var tn;

Ext.define('MultiMediaFileGrid', {
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
		var fileRecs = [];
        var projectName = config.projectName;
        var param;	//存放gridDT选择的行
        var fileUploadPanel;
        
        var getSel = function (grid) 
        {
            selRecs = [];  //清空数组
            keyIDs = [];
            selRecs = fileRecs.slice(0);
            for (var i = 0; i < selRecs.length; i++) {
            	keyIDs.push(selRecs[i].data.ID);
            }
            if (selRecs.length === 0) {
                Ext.Msg.alert('警告', '没有选中任何记录！');
                return false;
            }
            return true;
        };
        
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
        
        //去掉字符串的左右空格
        String.prototype.trim = function () {
            return this.replace(/(^\s*)|(\s*$)/g, '');
        };
        
        String.prototype.endWith=function(str){
			var reg=new RegExp(str+"$");
			return reg.test(this);
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
        		info_url = 'GoalDutyAction!getFileInfo';
        		delete_url = 'GoalDutyAction!deleteOneFile';

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
        		info_url = 'GoalDutyAction!getFileInfo';
        		delete_url = 'GoalDutyAction!deleteOneFile';

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
  	         if(action == "addProject"||action == "editProject" )
  	        {
				if(action == "editProject" )
				{             			
					ppid = selRecs[0].data.ID;
				}
				deleteFile(style, fileName, ppid);
				uploadPanel.store.removeAll();
  	        }
       	}
       	

		var store_multimediafile = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'FileType'},
					 { name: 'UploadDate'},
					 { name: 'Filename'},
					 { name: 'Foldname'},
					 { name: 'Accessory'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getMultimediafileListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&ProjectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		
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
				url: encodeURI('HiddenTroubleSolutionAction!getReadilyShootListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&ProjectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});

		var items_multimediafile = [{
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
		},
			uploadPanel
		]
		
		//按钮函数
		var searchH = function (){
			var keyword = tbar.getComponent("keyword").getValue().trim();
			findStr = keyword;
			if(queryURL.indexOf("?") > 0 ){
				dataStore.getProxy().url = encodeURI(queryURL + '&findStr=' + findStr);
			} else{
				dataStore.getProxy().url = encodeURI(queryURL + '?findStr=' + findStr);
			}
			dataStore.load({params: { start:0, limit:psize}});
			bbar.moveFirst();
		}
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
						//icon = "Images/ims/toolbar/report_rar.png";
						continue;
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
        
        var scanH2 = function (item){
        	var selRecs = gridDT.getSelectionModel().getSelection();
			var accessory = selRecs[0].data.Accessory;
			var foldname1 = selRecs[0].data.Foldname1;
			var foldname2 = selRecs[0].data.Foldname2;
			var foldname = foldname1+"\\"+foldname2; 
			fileMenu.removeAll();
			
					var icon = "";
					if(accessory.indexOf('.doc')>0)
						icon = "Images/ims/toolbar/report_word.png";
					else if(accessory.indexOf('.rar')>0||accessory.indexOf('.zip')>0)
						icon = "Images/ims/toolbar/report_rar.png";
					else if(accessory.indexOf('.xls')>0||accessory.indexOf('.xlsx')>0)
						icon = "Images/ims/toolbar/report_excel.png";
					else if(accessory.indexOf('.png')>0||accessory.indexOf('.jpg')>0||accessory.indexOf('.bmp')>0)
						icon = "Images/ims/toolbar/report_picture.png";
					else
						icon = "Images/ims/toolbar/report_other.png";
					var menuItem = Ext.create('Ext.menu.Item', {
                		text: accessory,
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
		 	
       
//jianglf-------------------------------------------------------------------------------       
        var addH = function(){
        	var actionURL = 'EduTrainAction!addMultimediafile?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&projectName=" + projectName;  ;
			var items = items_multimediafile;
			createForm({
				autoScroll: true,
				bodyPadding: 5,
				action: 'addProject',
				url: actionURL,
				items: items
			});
			uploadPanel.upload_url = "UploadAction!execute";
			bbar.moveFirst();	//状态栏回到第一页
			showWin({ 
				winId: 'addProject', 
				title: '新增文件', 
				items: [forms]
			});
        }

//jianglf--------------------------------------------------------------------------------------      
        var deleteH = function() {
        	if(getSel(gridDT)) {
				Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) {
					if (buttonID === 'yes') {
						var delete_url;
						if (title == '安全理念' || title == '安全警示语' || title == '安全承诺' || title == '安全行为激励'
							|| tableID >= 307 && tableID <= 312 || tableID == 147 || tableID == 149) delete_url = 'EduTrainAction!deleteMultimediafile';
						else if (title = '安全隐患随手拍') delete_url = 'HiddenTroubleSolutionAction!deleteReadilyShoot';
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
        var btnDel = Ext.create('Ext.Button', {
        	width: 55,
        	height: 32,
        	text: '删除',
        	disabled:true,
           	icon: "Images/ims/toolbar/delete.gif",
            handler: deleteH
        }) 
        
        
        //建立工具栏
        var tbar = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        })
//jianglf-----------------------------------------------------------------------------------------------------     
        if (title == '安全理念' || title == '安全警示语' || title == '安全承诺' || title == '安全行为激励'
        	|| tableID >= 307 && tableID <= 312 || tableID == 147 || tableID == 149) {	
        	dataStore = store_multimediafile;
			dataStore.load();
			queryURL = 'EduTrainAction!getMultimediafileListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID;
			column = [
				{ text: '序号',xtype: 'rownumberer',width: 50,sortable: false},	
				{ text: '文件类型', dataIndex: 'FileType', align: 'center', width: 200},
				{ text: '文件名', dataIndex: 'Filename', align: 'center', width: 350},
				{ text: '上传日期', dataIndex: 'UploadDate', align: 'center', width: 250},
				{ text: '项目部', align: 'center', width: 200, renderer: function (value, meta, record) {
						return	projectName;
					}
				}
			]
			tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add("-");
			tbar.add(btnAdd);
			tbar.add(btnDel);
			if(user.role === '项目部人员' && !(tableID >= 307 && tableID <= 312)) {
				tbar.remove(btnAdd);
				tbar.remove(btnDel);
    		}
        } else if (title == '安全隐患随手拍') {
        	dataStore = store_readily_shoot;
			dataStore.load();
			queryURL = 'HiddenTroubleSolutionAction!getReadilyShootListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID;
			column = [
				{ text: '序号',xtype: 'rownumberer',width: 50,sortable: false},	
				{ text: '文件类型', dataIndex: 'FileType', align: 'center', width: 200},
				{ text: '文件名', dataIndex: 'Filename', align: 'center', width: 350},
				{ text: '上传日期', dataIndex: 'UploadDate', align: 'center', width: 250},
				{ text: '项目部', align: 'center', width: 200, renderer: function (value, meta, record) {
						return	projectName;
					}
				}
			]
			tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add("-");
			tbar.add(btnDel);
			if(user.role === '项目部人员') {
				tbar.remove(btnAdd);
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
        };       
        
        //创建装载formPanel的窗体，由工具栏按钮点击显示
        var showWin = function (config) {
        	var width = 900;
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
        
		var viewPanel;
		var dataView;
		String.prototype.endWith=function(str){var reg=new RegExp(str + '$');return reg.test(this);}//测试ok，直接使用str.endWith("abc")方式调用即可String.prototype.endWith=function(str){var reg=new RegExp(str+"$");return reg.test(this);} 
		
		if (title == '安全理念' || title == '安全警示语' || title == '安全承诺' || title == '安全行为激励'
			|| tableID >= 307 && tableID <= 312 || tableID == 147 || tableID == 149) {
			var fileTpl = new Ext.XTemplate(
				'<tpl for=".">',
					'<div class="singleFile">',
						'<tpl if="FileType == &quot;图片&quot;">',
							'<img src="{Foldname}/{Filename}" />',
						'</tpl>',
						'<tpl if="FileType != &quot;图片&quot;">',
							'<img src="Images/ims/icon/{FileType}.jpg" />',
						'</tpl>',
						'<strong>{Filename}</strong>',
					'</div>',
				'</tpl>'
			);

			dataView = Ext.create('Ext.view.View', {
				store: store_multimediafile,
			    tpl: fileTpl,
			    deferInitialRefresh: false,
			    itemSelector: 'div.singleFile',
				multiSelect: true,
				autoScroll: true,
				listeners: {
					selectionchange: function(dataView, selectNodes) {
						fileRecs = selectNodes.slice(0);
						var selRecs = selectNodes.slice(0);
						//多选的按钮
						if(selRecs.length >= 1) {
							btnDel.enable();
						} else {
							btnDel.disable();
						}
					},
					itemcontextmenu: function (myself, record, items, index, e) {//Fires when an item is right clicked.
						//e.preventDefault(); //stopEvent()，停止一个事件，相当于调用preventDefault()和stopPropagation()两个函数。
						//e.stopEvent();
						dataView.getSelectionModel().select(index);
						var filetype = record.raw.FileType;
						var foldname = record.raw.Foldname;
						var filename = record.raw.Filename;
						var rightClickMenu = new Ext.menu.Menu({
							floating: true,
							items: [{
								text: "预览",//根目录添加文件夹功能已经可以正常运行
								icon: 'Images/ims/toolbar/view.png',
								tooltip: '预览',//点击了预览才会进入这个
								hidden: (filetype != '图片' && filetype != '视频' && filetype != 'PDF文档' && filetype != 'Word文档' && filetype != 'Excel文档'),								
								handler: function () {
									if (filetype == '图片') {
										var html = "<div style=\"overflow:auto;width:100%;height:100%;\"><img src=\""+foldname+"/"+filename+"\" style=\"width:100%;\" /><div>";
										showWin({
											title: "预览",
											html: html,
											closeAction: 'destroy'
										});
									} else if (filetype == '视频') {
										var html;
										if (filename.endWith('.wmv')) {
											if (!!window.ActiveXObject || "ActiveXObject" in window) {
												html = '<embed src="'+foldname+'/'+filename+'" type="video/x-ms-wmv" autostart="true" loop="true" showControls="true" showstatusbar="1" style="width:100%;height:100%;" />';
											} else {
												Ext.Msg.alert('提示', 'Chrome已经不支持播放WMV格式的视频， 若想观看请移步IE浏览器，或者将360浏览器切换到IE内核。');
												return;
											}
										} else {
											html = "<video src=\""+foldname+"/"+filename+"\" autoplay=\"autoplay\" controls=\"controls\" style=\"width:100%;height:100%;\" />";
										}
										showWin({
											html: html,
											closeAction: 'destroy'
										});
									} else if (filetype == 'PDF文档') {
										var html = "<iframe src=\""+foldname+"/"+filename+"\" style=\"width:100%;height:100%;\" />";
										showWin({
											html: html,
											maximizable: true,
											closeAction: 'destroy'
										});
									} else if (filetype == 'Word文档' || filetype == 'Excel文档') {
										
										var html = "<iframe src=\""+foldname+"/"+filename.substr(0, filename.lastIndexOf('.'))+".pdf\" style=\"width:100%;height:100%;\" />";
										showWin({
											html: html,
											maximizable: true,
											closeAction: 'destroy'
										});
									} else {
										Ext.Msg.alert('提示', '该文件暂时不支持预览！');
									}
								}
							},{
								text: "下载",//根目录的名字直接不让修改就行了
								icon: 'Images/ims/toolbar/download.png',
								tooltip: '下载',
								handler: function () {
									var a=document.createElement('a');
									a.setAttribute("href", foldname + '/' + filename);
								    a.setAttribute("download", filename);
									a.click();
								}
							},{
								text: "删除",//根目录的名字直接不让修改就行了
								icon: 'Images/ims/toolbar/remove1.png',
								handler: deleteH
							}]
						})
						rightClickMenu.showAt(e.getXY());
					},
					itemdblclick: function(myself, record, item, index, e, eOpts) {
						var filetype = record.raw.FileType;
						var foldname = record.raw.Foldname;
						var filename = record.raw.Filename;												
						if (filetype == '图片') {
							var html = "<div style=\"overflow:auto;width:100%;height:100%;\"><img src=\""+foldname+"/"+filename+"\" style=\"width:100%;\" /><div>";
							showWin({
								title: "预览",
								html: html,
								closeAction: 'destroy'
							});
						} else if (filetype == '视频') {
							var html;
							if (filename.endWith('.wmv')) {
								if (window.ActiveXObject || "ActiveXObject" in window) {
									html = '<embed src="'+foldname+'/'+filename+'" type="video/x-ms-wmv" autostart="true" loop="true" showControls="true" showstatusbar="1" style="width:100%;height:100%;" />';
								} else {
									Ext.Msg.alert('提示', 'Chrome已经不支持播放WMV格式的视频， 若想观看请移步IE浏览器，或者将360浏览器切换到IE内核。');
									return;
								}
							} else {
								html = "<video src=\""+foldname+"/"+filename+"\" autoplay=\"autoplay\" controls=\"controls\" style=\"width:100%;height:100%;\" />";
							}
							showWin({
								html: html,
								closeAction: 'destroy'
							});
						} else if (filetype == 'PDF文档') {
							var html = "<iframe src=\""+foldname+"/"+filename+"\" style=\"width:100%;height:100%;\" />";
							showWin({
								html: html,
								maximizable: true,
								closeAction: 'destroy'
							});
						} else if (filetype == 'Word文档' || filetype == 'Excel文档') {
							var html = "<iframe src=\""+foldname+"/"+filename.substr(0, filename.lastIndexOf('.'))+".pdf\" style=\"width:100%;height:100%;\" />";
							showWin({
								html: html,
								maximizable: true,
								closeAction: 'destroy'
							});
						} else {
							Ext.Msg.alert('提示', '该文件暂时不支持预览！');
						}
					}
				}					
			});
			
			viewPanel = Ext.create('Ext.panel.Panel', {
				title: "资料浏览",
				header: false,	//以图标方式查看的时候，设置为false就会隐藏标题，跟那条细线无关，这个在内部
				tbar: tbar,
				layout: 'fit',
				items: dataView,
				bodyStyle: 'border-color:rgb(200,200,200);border-width:1px;',
				listeners: {
					'afterrender': function () {
						viewPanel.add(dataView);
					}					
				}
			});
			panel.add(viewPanel);
		} else if (title == '安全隐患随手拍') {
			var fileTpl = new Ext.XTemplate(
					'<tpl for=".">',
						'<div class="singleFile readilyShoot">',
							'<div style="width:100%;height:65%;background-image:url(\'{url}\');background-repeat:no-repeat;background-position:center;background-size:contain;" /></div>',
							'<div style="width:100%;height:30%;margin-top:5%;">',	
								'<strong>{prefix}</strong>',
								'<strong>{comment}</strong>',
							'</div>',
						'</div>',
					'</tpl>'
				);

				dataView = Ext.create('Ext.view.View', {
					store: dataStore,
				    tpl: fileTpl,
				    deferInitialRefresh: false,
				    itemSelector: 'div.singleFile',
					multiSelect: true,
					autoScroll: true,
					listeners: {
						selectionchange: function(dataView, selectNodes) {
							fileRecs = selectNodes.slice(0);
							var selRecs = selectNodes.slice(0);
							//多选的按钮
							if(selRecs.length >= 1) {
								btnDel.enable();
							} else {
								btnDel.disable();
							}
						},
						itemcontextmenu: function (myself, record, items, index, e) {//Fires when an item is right clicked.
							//e.preventDefault(); //stopEvent()，停止一个事件，相当于调用preventDefault()和stopPropagation()两个函数。
							//e.stopEvent();
							dataView.getSelectionModel().select(index);
							var imageURL = record.raw.url;
							var prefix = record.raw.prefix;
							var comment = record.raw.comment;
							var html = "<div style=\"overflow:auto;width:100%;height:100%;margin-bottom:20px;background-image:url('"+imageURL+"');background-repeat:no-repeat;background-position:center;background-size:contain\"><div>";
							var rightClickMenu = new Ext.menu.Menu({
								floating: true,
								items: [{
									text: "预览",//根目录添加文件夹功能已经可以正常运行
									icon: 'Images/ims/toolbar/view.png',
									tooltip: '预览',//点击了预览才会进入这个
									hidden: false,								
									handler: function () {
										showWin({
											title: "预览",
											html: html,
											closeAction: 'destroy'
										});
									}
								},{
									text: "详情",//根目录添加文件夹功能已经可以正常运行
									icon: 'Images/ims/toolbar/resubmit.png',
									tooltip: '查看详情',//点击了预览才会进入这个
									hidden: false,								
									handler: function () {
										Ext.Msg.alert('详情', prefix + '<br/><br/>' + comment);
									}
								},{
									text: "删除",//根目录的名字直接不让修改就行了
									icon: 'Images/ims/toolbar/remove1.png',
									handler: deleteH
								}]
							})
							rightClickMenu.showAt(e.getXY());
						},
						itemdblclick: function(myself, record, item, index, e, eOpts) {
							var imageURL = record.raw.url;
							var prefix = record.raw.prefix;
							var comment = record.raw.comment;
							var html = "<div style=\"overflow:auto;width:100%;height:100%;margin-bottom:20px;background-image:url('"+imageURL+"');background-repeat:no-repeat;background-position:center;background-size:contain\"><div>";
							showWin({
								title: "预览",
								html: html,
								closeAction: 'destroy'
							});
						}
					}					
				});
				
				viewPanel = Ext.create('Ext.panel.Panel', {
					title: "资料浏览",
					header: false,	//以图标方式查看的时候，设置为false就会隐藏标题，跟那条细线无关，这个在内部
					tbar: tbar,
					layout: 'fit',
					items: dataView,
					bodyStyle: 'border-color:rgb(200,200,200);border-width:1px;',
					listeners: {
						'afterrender': function () {
							viewPanel.add(dataView);
						}					
					}
				});
				panel.add(viewPanel);
	    }
        container.add(panel).show();

    } 
});