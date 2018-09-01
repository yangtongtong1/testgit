Ext.define('EmeRescueGrid', {
	requires: [
		'Ext.data.Model',
		'Ext.grid.Panel'
	],
	toolbar: null,
	selRecs: [],
	createGrid: function (config) {
		var me = this;
		var title = config.title;                //标题，模块名
		var tableID = config.tableID;            //表格的ID号，通过ID号来确认所在Tap页和Grid
		var psize = config.pageSize;             //pagesize
		var container = config.container;        //存grid的panel的容器            
		var findStr = null;						 //查询关键字
		var fileName = null;
		var style = null;
		var user = config.user;					 //用户类，包含name, role, identity, unit , rank
		var dataStore;
		var column;
		var queryURL;
		var forms = [];
		var selRecs = [];
		var param;								 //存放gridDT选择的行        
		var projectName = config.projectName;
				
		
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
			var info_url = 'EmeRescueAction!getFileInfo';
			var delete_url = 'EmeRescueAction!deleteOneFile';
			var existFile = selRecs[0].data.Accessory.split('*');
			for(var i = 2;i<existFile.length;i++)
			{        				
//				alert(i);
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
		var getFileName = function() {
			var name = null;
			uploadPanel.store.each(function(record) {
			if(name == null)
				name = record.get('name') + "*";
			else
				name += record.get('name') + "*";
			})
			return name;
		}
		
		//删除上传文件
		var deleteFile = function(fileName, ppid) {
			var deleteAllUrl = "EmeRescueAction!deleteAllFile";
			$.getJSON(deleteAllUrl,
			   {fileName: fileName, id: ppid},	//Ajax参数
				function (res) {
					if (!res.success)             
						Ext.Msg.alert("信息", res.msg);
			});
		}
		
		var DeleteFile = function(action)
		{
			var ppid = "";
			var fileName = null;
			fileName = getFileName();
			if(action == "addProject" || action == "editProject") {
				if(action == "editProject")	{               			
					ppid = selRecs[0].data.ID;
				}		
				deleteFile(fileName, ppid);
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
        
        var importH2 = function() {
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
	            		$.getJSON('EmeRescueAction!importExcel', { fileName: fileName, type : title, tableID: tableID, projectName: projectName },
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
       	
       	var btnImport2 = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '文件导入',
            icon: "Images/ims/toolbar/extexcel.png",
            handler: importH2
        })
		
		var store_Managepara = function() {
			return Ext.create('Ext.data.Store', {
				fields: [
						 { name: 'ID'},
						 { name: 'Content'},
						 { name: 'Type'},
						 { name: 'Quantity'},
						 { name: 'Unit'},
						 { name: 'Fbunit'},
						 { name: 'State'},
						 { name: 'Place'},
						 { name: 'Responsible'},
						 { name: 'Accessory'},
						 { name: 'ProjectName'}
				],
				pageSize: psize,  //页容量20条数据
				proxy: {
					type: 'ajax',
					url: encodeURI('EmeRescueAction!getManageparaListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID+ "&projectName=" + projectName),
					reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
						type: 'json', //返回数据类型为json格式
						root: 'rows',  //数据
						totalProperty: 'total' //数据总条数
					}
				},
				autoLoad: true //即时加载数据
			});
		}
		
		
		
		
		var store_SpEquip =  Ext.create('Ext.data.Store', {
			fields: [
				 { name: 'ID'},
				 { name: 'Name'},
				 { name: 'Type'},
				 { name: 'Purpose'},
				 { name: 'InDate'},
				 { name: 'OutDate'},
				 { name: 'RegistNo'},
				 { name: 'Kind'},
				 { name: 'ManuUnit'},
				 { name: 'PurchaseDate'},
				 { name: 'InstallUnit'},
				 { name: 'CheckStatus'},
				 { name: 'UseStatus'},
				 { name: 'MajorStatus'},
				 { name: 'OtherStatus'},
				 { name: 'ProjectName'},
				 { name: 'Accessory'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EmeRescueAction!getSpEquipListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID+ "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		var store_ElecEquip =  Ext.create('Ext.data.Store', {
			fields: [
				 { name: 'ID'},
				 { name: 'EquipNo'},
				 { name: 'Name'},
				 { name: 'Type'},
				 { name: 'ManuUnit'},
				 { name: 'Quantity'},
				 { name: 'Unit'},
				 { name: 'Purpose'},
				 { name: 'InDate'},
				 { name: 'RegistNo'},
				 { name: 'UsePlace'},
				 { name: 'Responser'},
				 { name: 'ProjectName'},
				 { name: 'Accessory'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EmeRescueAction!getElecEquipListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID+ "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		
		
	
		var store_Yingjijyzz =  Ext.create('Ext.data.Store', {
			fields: [
				 { name: 'ID'},
				 { name: 'zuizhiname'},
				 { name: 'clortz'},
				 { name: 'clortztime'},
				 { name: 'fuzeren'},
				 { name: 'chengyuan'},
				 { name: 'gongzuojg'},
				 { name: 'Accessory'},
				 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EmeRescueAction!getYingjijyzzListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID+ "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		
		var store_Yingjiyuan =  Ext.create('Ext.data.Store', {
			fields: [
				{ name: 'ID'},
				 { name: 'zuizhiname'},
				 { name: 'bianzhiren'},
				 { name: 'bianzhitime'},
				 { name: 'shenheren'},
				 { name: 'shenhetime'},
				 { name: 'pizhunren'},
				 { name: 'pizhuntime'},
				 { name: 'Accessory'},
				 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EmeRescueAction!getYingjiyuanListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID+ "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		
		var store_Yingjipxyl =  Ext.create('Ext.data.Store', {
			fields: [
				 { name: 'ID'},
				 { name: 'content'},
				 { name: 'peixuntime'},
				 { name: 'Accessory'},
				 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EmeRescueAction!getYingjipxylListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID+ "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		
		
		var store_Yingjifenbao =  Ext.create('Ext.data.Store', {
			fields: [
				 { name: 'ID'},
				 { name: 'fenbaoname'},
				 { name: 'uploadtime'},
				 { name: 'filename'},
				 { name: 'type'},
				 { name: 'Accessory'},
				 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EmeRescueAction!getYingjifenbaoListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID+"&type=" + title+ "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		
		var items_Managepara = [{
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
					fieldLabel: 'Accessory',
					labelWidth: 120,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Accessory',
					hidden: true,
					hiddenLabel: true
				},{
					xtype:'textfield',
					fieldLabel: 'tableID',
					emptyText: tableID,
					labelAlign: 'right',
					anchor:'100%',
					name: 'TableID',
					hidden: true,
					hiddenLabel: true
				},{
					xtype:'textfield',
					fieldLabel: '设施、装备、物资名称',
					labelWidth: 150,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Content'
				},{
					xtype:'textfield',
					fieldLabel: '数量',
					labelAlign: 'right',
					labelWidth: 150,
					anchor:'100%',
					name: 'Quantity'
				},{
					xtype:'textfield',
					fieldLabel: '设施、装备、物资状态',
					labelWidth: 150,
					labelAlign: 'right',
					anchor:'100%',
					name: 'State'
				},{
					xtype:'textfield',
					fieldLabel: '负责人',
					labelWidth: 150,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Responsible'
				}]
			},{
				xtype: 'container',
				flex: 1,
				layout: 'anchor',
				items: [{
					xtype:'textfield',
					fieldLabel: '所属类别',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Type'
				},{
					xtype:'textfield',
					fieldLabel: '单位',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Unit'
				},{
					xtype:'textfield',
					fieldLabel: '存放地点',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Place'
				}]
			}]
		},
			uploadPanel
		]
		
		var items_Manageparafb = [{
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
					fieldLabel: 'Accessory',
					labelWidth: 120,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Accessory',
					hidden: true,
					hiddenLabel: true
				},{
					xtype:'textfield',
					fieldLabel: 'tableID',
					emptyText: tableID,
					labelAlign: 'right',
					anchor:'100%',
					name: 'TableID',
					hidden: true,
					hiddenLabel: true
				},{
					xtype:'textfield',
					fieldLabel: '设施、装备、物资名称',
					labelWidth: 150,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Content'
				},{
					xtype:'textfield',
					fieldLabel: '数量',
					labelAlign: 'right',
					labelWidth: 150,
					anchor:'100%',
					name: 'Quantity'
				},{
					xtype:'textfield',
					fieldLabel: '设施、装备、物资状态',
					labelWidth: 150,
					labelAlign: 'right',
					anchor:'100%',
					name: 'State'
				},{
					xtype:'textfield',
					fieldLabel: '负责人',
					labelWidth: 150,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Responsible'
				}]
			},{
				xtype: 'container',
				flex: 1,
				layout: 'anchor',
				items: [{
					xtype:'textfield',
					fieldLabel: '所属类别',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Type'
				},{
					xtype:'textfield',
					fieldLabel: '单位',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Unit'
				},{
					xtype:'textfield',
					fieldLabel: '分包单位',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Fbunit'
				},{
					xtype:'textfield',
					fieldLabel: '存放地点',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Place'
				}]
			}]
		},
			uploadPanel
		]
		
		var items_SpEquip = [{
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
					fieldLabel: 'Accessory',
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
					labelAlign: 'right',
					anchor:'100%',
					name: 'Name'
				},{
					xtype:'datefield',
					format: 'Y-m-d',
					fieldLabel: '进场时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'InDate'
				},{
					xtype:'textfield',
					fieldLabel: '用途',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Purpose'
				},{
					xtype:'textfield',
					fieldLabel: '车牌号/使用登记号',
					labelAlign: 'right',
					anchor:'100%',
					name: 'RegistNo'
				},{
					xtype:'datefield',
					format: 'Y-m-d',
					fieldLabel: '购置时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'PurchaseDate'
				},{
					xtype:'textfield',
					fieldLabel: '检验时间及情况',
					labelAlign: 'right',
					anchor:'100%',
					name: 'CheckStatus'
				},{
					xtype:'textfield',
					fieldLabel: '重大维修情况',
					labelAlign: 'right',
					anchor:'100%',
					name: 'MajorStatus'
				}]
			},{
				xtype: 'container',
				flex: 1,
				layout: 'anchor',
				items: [{
					xtype:'textfield',
					fieldLabel: '型号及规格',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Type'
				},{
					xtype:'datefield',
					format: 'Y-m-d',
					fieldLabel: '出场时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'OutDate',
					allowBlank : true
				},{
					xtype:'textfield',
					fieldLabel: '设备种类',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Kind'
				},{
					xtype:'textfield',
					fieldLabel: '制造单位',
					labelAlign: 'right',
					anchor:'100%',
					name: 'ManuUnit'
				},{
					xtype:'textfield',
					fieldLabel: '安装单位',
					labelAlign: 'right',
					anchor:'100%',
					name: 'InstallUnit'
				},{
					xtype:'textfield',
					fieldLabel: '使用状态',
					labelAlign: 'right',
					anchor:'100%',
					name: 'UseStatus'
				},{
					xtype:'textfield',
					fieldLabel: '其他变更情况',
					labelAlign: 'right',
					anchor:'100%',
					name: 'OtherStatus'
				}]
			}]
		},
			uploadPanel
		]		
		var items_ElecEquip = [{
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
					fieldLabel: 'Accessory',
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
					fieldLabel: '设备编号',
					labelAlign: 'right',
					anchor:'100%',
					name: 'EquipNo'
				},{
					xtype:'textfield',
					fieldLabel: '型号及规格',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Type'
				},{
					xtype:'textfield',
					fieldLabel: '数量',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Quantity'
				},{
					xtype:'datefield',
					format: 'Y-m-d',
					fieldLabel: '进场时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'InDate'
				},{
					xtype:'textfield',
					fieldLabel: '使用地点',
					labelAlign: 'right',
					anchor:'100%',
					name: 'UsePlace'
				}]
			},{
				xtype: 'container',
				flex: 1,
				layout: 'anchor',
				items: [{
					xtype:'textfield',
					fieldLabel: '设备名称',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Name'
				},{
					xtype:'textfield',
					fieldLabel: '制造单位',
					labelAlign: 'right',
					anchor:'100%',
					name: 'ManuUnit'
				},{
					xtype:'textfield',
					fieldLabel: '单位',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Unit'
				},{
					xtype:'textfield',
					fieldLabel: '使用编号',
					labelAlign: 'right',
					anchor:'100%',
					name: 'RegistNo'
				},{
					xtype:'textfield',
					fieldLabel: '责任人',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Responser'
				}]
			}]
		},
			uploadPanel
		]
		
		
		
		var storezuzhi = new Ext.data.ArrayStore({
            fields: ['id', 'name'],
            data: [[1, '组织成立'], 
            	[2, '组织调整']]
          });
		  var items_Yingjijyzz =[{
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
	                    fieldLabel: '应急救援组织名称',
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    allowBlank: false,
	                    name: 'zuizhiname'
	                },{
	                	xtype:'combo',
	                	queryMode:'local',
	                	fieldLabel: '组织成立/调整',
	                	editable:false,
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    store:storezuzhi,
	                 	valueField:'name',
	                 	displayField:'name',
	                 	triggerAction:'all',
	                 	autoSelect:true,
	                    allowBlank: false,
	                    name: 'clortz'
	                },{
	                	xtype: 'datefield',
	                    fieldLabel: '成立/调整时间',
	                    format : 'Y-m-d',
	                    anchor:'95%',
	                    labelAlign: 'right',
	                    allowBlank: false,
	                    name: 'clortztime'
	                }]
		        },{
		        	xtype: 'container',
		            flex: 1,
		            layout: 'anchor',
		            items: [{
	                	xtype:'textfield',
	                    fieldLabel: '负责人',
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    allowBlank: false,
	                    name: 'fuzeren'
	                },{
	                	xtype:'textfield',
	                    fieldLabel: '成员组成',
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    allowBlank: false,
	                    name: 'chengyuan'
	                },{
	                	xtype:'textfield',
	                    fieldLabel: '工作机构',
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    allowBlank: false,
	                    name: 'gongzuojg'
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
		                text: '请点击上传应急救援组织成立正式文件',
		                height:10,
		                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
		                name: 'tip'
		            }
		        ]},
	        	uploadPanel
	        ]
		  
		
		 var items_Yingjiyuan =[{
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
	                    fieldLabel: '应急救预案/现场处置方案名称',
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    allowBlank: false,
	                    name: 'zuizhiname'
	                },{
	                	xtype:'textfield',
	                    fieldLabel: '编制人',
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    allowBlank: false,
	                    name: 'bianzhiren'
	                },{
	                	xtype: 'datefield',
	                    fieldLabel: '编制时间',
	                    format : 'Y-m-d',
	                    anchor:'95%',
	                    labelAlign: 'right',
	                    allowBlank: false,
	                    name: 'bianzhitime'
	                }]
		        },{
		        	xtype: 'container',
		            flex: 1,
		            layout: 'anchor',
		            items: [{
	                	xtype:'textfield',
	                    fieldLabel: '审核人',
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    allowBlank: false,
	                    name: 'shenheren'
	                },{
	                	xtype: 'datefield',
	                    fieldLabel: '审核时间',
	                    format : 'Y-m-d',
	                    anchor:'95%',
	                    labelAlign: 'right',
	                    allowBlank: false,
	                    name: 'shenhetime'
	                }]
		        },{
		        	xtype: 'container',
		            flex: 1,
		            layout: 'anchor',
		            items: [{
	                	xtype:'textfield',
	                    fieldLabel: '批准人',
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    allowBlank: false,
	                    name: 'pizhunren'
	                },{
	                	xtype: 'datefield',
	                    fieldLabel: '批准时间',
	                    format : 'Y-m-d',
	                    anchor:'95%',
	                    labelAlign: 'right',
	                    allowBlank: false,
	                    name: 'pizhuntime'
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
		                text: '请点击上传应急预案、现场处置方案',
		                height:10,
		                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
		                name: 'tip'
		            }
		        ]},
	        	uploadPanel
	        ]
		  
		  
		  
		  var items_Yingjipxyl =[{
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
	                    fieldLabel: '培训与演练内容',
	                    //labelWidth: 120,
	                    labelAlign: 'right',
	                    anchor:'95%',
	                    allowBlank: false,
	                    name: 'content'
	                }]
		        },{
		        	xtype: 'container',
		            flex: 1,
		            layout: 'anchor',
		            items: [{
	                	xtype: 'datefield',
	                    fieldLabel: '培训与演练时间',
	                    format : 'Y-m-d',
	                    anchor:'95%',
	                    labelAlign: 'right',
	                    allowBlank: false,
	                    name: 'peixuntime'
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
		                text: '请点击上传应急培训与演练方案、记录',
		                height:10,
		                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
		                name: 'tip'
		            }
		        ]},
	        	uploadPanel
	        ]
		  
		  
		
		  var items_Yingjifenbao =[{
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
		                text: '点击上传文件',
		                height:10,
		                style:'color:#FF0000;margin-top:23px;margin-left:45px;',
		                name: 'tip',
		                listeners: {
							'afterRender': function (item, e, eOpts) {
								if(title=='分包方防洪度汛技术要求与应急预案'){
									item.setText('请点击上传分包方防洪度汛技术要求与应急预案');
								}else if(title=='分包方应急预案及现场处置方案'){
									item.setText('请点击上传分包方应急预案及现场处置方案');
								}else if(title=='分包方应急培训演练'){
									item.setText('请点击上传分包方应急培训演练方案、记录');
								}
							}
						}
		            }
		        ]},
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
			btnSearchR.enable();
			dataStore.load({params: { start:0, limit:psize}});
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
				for( var i = 2; i < array.length; i++) {
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
						listeners: {
							'click': function (item, e, eOpts) {
								window.open("upload\\" + foldname + "\\" + item.text);								
							}
						}
					});
					fileMenu.add(menuItem);  
				}
			}
		}
		
		var addH = function() {
			var actionURL;
			var items;
			if(tableID == 173 || tableID == 176) {
				actionURL = 'EmeRescueAction!addManagepara?userName=' + user.name + "&userRole=" + user.role;  
				items = items_Managepara;
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
			else if(tableID == 183) {
				actionURL = 'EmeRescueAction!addManagepara?userName=' + user.name + "&userRole=" + user.role;  
				items = items_Manageparafb;
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
			} else if (title == '特种设备管理台账') {        	
				actionURL = 'EmeRescueAction!addSpEquip?userName=' + user.name + "&userRole=" + user.role;  
				items = items_SpEquip;
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
			} else if (title == '电气设备设施管理台账') { 
				actionURL = 'EmeRescueAction!addElecEquip?userName=' + user.name + "&userRole=" + user.role;  
				items = items_ElecEquip;
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
			
			else if(title=='应急救援组织')
        	{
        		actionURL = 'EmeRescueAction!addYingjijyzz?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Yingjijyzz;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addYingjijyzz',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addYingjijyzz', title: '新增项目', items: [forms]});
        	}
			
			
			else if(title=='应急预案、现场处置方案编审批')
        	{
        		actionURL = 'EmeRescueAction!addYingjiyuan?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Yingjiyuan;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addYingjiyuan',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addYingjiyuan', title: '新增项目', items: [forms]});
        	}
			
			else if(title=='应急培训与演练')
        	{
        		actionURL = 'EmeRescueAction!addYingjipxyl?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_Yingjipxyl;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addYingjipxyl',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addYingjipxyl', title: '新增项目', items: [forms]});
        	}
			
			if(title =='分包方防洪度汛技术要求与应急预案'||title =='分包方应急预案及现场处置方案'||title =='分包方应急培训演练')
        	{	   
        		actionURL = 'EmeRescueAction!addYingjifenbao?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
//        		uploadURL = "UploadAction!execute";
        		uploadURL = encodeURI("UploadAction!execute");
        		items = items_Yingjifenbao;
        		
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addYingjifenbao',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addYingjifenbao', title: '新增项目', items: [forms]});
        	}  
			
			
		}

		var editH = function() {
			var formItems;
			var itemURL; 
			var actionURL;
			var uploadURL;
			var items;
			if(getSel(gridDT)) {
				if(selRecs.length == 1 ) {
					
					if(tableID == 173 || tableID == 176) {
						insertFileToList();	
						actionURL = 'EmeRescueAction!editManagepara?userName=' + user.name + "&userRole=" + user.role; 
						items = items_Managepara;
						createForm({
							autoScroll: true,
							action: 'editProject',
							bodyPadding: 5,
							url: actionURL,
							items: items
						});
						uploadPanel.upload_url = "UploadAction!execute";
						bbar.moveFirst();	//状态栏回到第一页
						showWin({ winId: 'editProject', title: '修改文件', items: [forms]});
					} 
					else if(tableID == 183) {
						insertFileToList();	
						actionURL = 'EmeRescueAction!editManagepara?userName=' + user.name + "&userRole=" + user.role; 
						items = items_Manageparafb;
						createForm({
							autoScroll: true,
							action: 'editProject',
							bodyPadding: 5,
							url: actionURL,
							items: items
						});
						uploadPanel.upload_url = "UploadAction!execute";
						bbar.moveFirst();	//状态栏回到第一页
						showWin({ winId: 'editProject', title: '修改文件', items: [forms]});
					} else if (title == '特种设备管理台账') {   
						insertFileToList();	
						actionURL = 'EmeRescueAction!editSpEquip?userName=' + user.name + "&userRole=" + user.role;  
						items = items_SpEquip;
						createForm({
							autoScroll: true,
							action: 'editProject',
							bodyPadding: 5,
							url: actionURL,
							items: items
						});
						uploadPanel.upload_url = "UploadAction!execute";
						bbar.moveFirst();	//状态栏回到第一页
						showWin({ winId: 'editProject', title: '修改文件', items: [forms]});
					} else if (title == '电气设备设施管理台账') { 
						insertFileToList();	
						actionURL = 'EmeRescueAction!editElecEquip?userName=' + user.name + "&userRole=" + user.role;  
						items = items_ElecEquip;
						createForm({
							autoScroll: true,
							action: 'editProject',
							bodyPadding: 5,
							url: actionURL,
							items: items
						});
						uploadPanel.upload_url = "UploadAction!execute";
						bbar.moveFirst();	//状态栏回到第一页
						showWin({ winId: 'editProject', title: '修改文件', items: [forms]});
					}
					
					else if(title=='应急救援组织')
        			{
        				//***************
	        				insertFileToList();
	        				uploadURL = "UploadAction!execute";
	        				actionURL = 'EmeRescueAction!editYingjijyzz?userName=' + user.name + "&userRole=" + user.role;  
	        				items = items_Yingjijyzz;
	              
	        			createForm({
	            			autoScroll: true,
	            			action: 'editYingjijyzz',
	        	        	bodyPadding: 5,
	        	        	url: actionURL,
	        	        	items: items
	        	        });
	        			uploadPanel.upload_url = uploadURL;
	                	bbar.moveFirst();	//状态栏回到第一页
	        	        showWin({ winId: 'editYingjijyzz', title: '修改项目', items: [forms]});
        			}
					
					
					else if(title=='应急预案、现场处置方案编审批')
        			{
        				//***************
	        				insertFileToList();
	        				uploadURL = "UploadAction!execute";
	        				actionURL = 'EmeRescueAction!editYingjiyuan?userName=' + user.name + "&userRole=" + user.role;  
	        				items = items_Yingjiyuan;
	              
	        			createForm({
	            			autoScroll: true,
	            			action: 'editYingjiyuan',
	        	        	bodyPadding: 5,
	        	        	url: actionURL,
	        	        	items: items
	        	        });
	        			uploadPanel.upload_url = uploadURL;
	                	bbar.moveFirst();	//状态栏回到第一页
	        	        showWin({ winId: 'editYingjiyuan', title: '修改项目', items: [forms]});
        			}
					
					
					else if(title=='应急培训与演练')
        			{
        				//***************
	        				insertFileToList();
	        				uploadURL = "UploadAction!execute";
	        				actionURL = 'EmeRescueAction!editYingjipxyl?userName=' + user.name + "&userRole=" + user.role;  
	        				items = items_Yingjipxyl;
	              
	        			createForm({
	            			autoScroll: true,
	            			action: 'editYingjipxyl',
	        	        	bodyPadding: 5,
	        	        	url: actionURL,
	        	        	items: items
	        	        });
	        			uploadPanel.upload_url = uploadURL;
	                	bbar.moveFirst();	//状态栏回到第一页
	        	        showWin({ winId: 'editYingjipxyl', title: '修改项目', items: [forms]});
        			}
					
					else  if(title =='分包方防洪度汛技术要求与应急预案'||title =='分包方应急预案及现场处置方案'||title =='分包方应急培训演练')
	                	{
	        				insertFileToList();
	        				uploadURL = "UploadAction!execute";
	        				actionURL = 'EmeRescueAction!editYingjifenbao?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
	                		items = items_Yingjifenbao;
	              
	        			createForm({
	            			autoScroll: true,
	            			action: 'editYingjifenbao',
	        	        	bodyPadding: 5,
	        	        	url: actionURL,
	        	        	items: items
	        	        });
	        			uploadPanel.upload_url = uploadURL;
	                	bbar.moveFirst();	//状态栏回到第一页
	        	        showWin({ winId: 'editYingjifenbao', title: '修改项目', items: [forms]});
	        		}
					
				}
				else {
					 Ext.Msg.alert('警告', '只能选中一条记录！');
				}        		
			}
		}
		
		var deleteH = function() {        	
			if(getSel(gridDT)) {
				Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) {
					if (buttonID === 'yes') {
						var delete_url = '';
						if (tableID == 173 || tableID == 176 || tableID == 183) {
							delete_url = 'EmeRescueAction!deleteManagepara';
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
						} else if (title == '特种设备管理台账') {        	
							delete_url = 'EmeRescueAction!deleteSpEquip';  
							items = items_SpEquip;
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
						} else if (title == '电气设备设施管理台账') { 
							delete_url = 'EmeRescueAction!deleteElecEquip';  
							items = items_ElecEquip;
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
						else if(title=='应急救援组织')
//							alert('删除');
	                	{$.getJSON(encodeURI("EmeRescueAction!deleteYingjijyzz?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
								{id: keyIDs.toString()},	//Ajax参数
	                            function (res) 
	                            {
									if (res.success)
									{
//										alert('删除');
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
						else if(title=='应急预案、现场处置方案编审批')
//							alert('删除');
	                	{$.getJSON(encodeURI("EmeRescueAction!deleteYingjiyuan?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
								{id: keyIDs.toString()},	//Ajax参数
	                            function (res) 
	                            {
									if (res.success)
									{
//										alert('删除');
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
						
						
						else if(title=='应急培训与演练')
//							alert('删除');
	                	{$.getJSON(encodeURI("EmeRescueAction!deleteYingjipxyl?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
								{id: keyIDs.toString()},	//Ajax参数
	                            function (res) 
	                            {
									if (res.success)
									{
//										alert('删除');
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
						if(title =='分包方防洪度汛技术要求与应急预案'||title =='分包方应急预案及现场处置方案'||title =='分包方应急培训演练')
                    	{$.getJSON(encodeURI("EmeRescueAction!deleteYingjifenbao?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
		//建立工具栏
		var tbar = new Ext.Toolbar({
			defaults: {
				scale: 'medium'
			}
		})

		var addBtn = function() {
			tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add(btnSearchR);
			tbar.add(btnScan);
			tbar.add("-");
			tbar.add(btnAdd);
			tbar.add(btnEdit);
			tbar.add(btnDel);
		}
		
		
		 if(title =='应急救援组织'){        	
			dataStore = store_Yingjijyzz;
			dataStore.load();
			queryURL = 'EmeRescueAction!getYingjijyzzListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID+ "&projectName=" + projectName;
			column = [
				{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	        	    								
				{ text: '应急救援组织名称', dataIndex: 'zuizhiname', align: 'center', width: 175},
				{ text: '组织成立/调整', dataIndex: 'clortz', align: 'center', width: 175},
				{ text: '成立/调整时间', dataIndex: 'clortztime', align: 'center', width: 175},
				{ text: '负责人', dataIndex: 'fuzeren', align: 'center', width: 175},
				{ text: '成员组成', dataIndex: 'chengyuan', align: 'center', width: 175},
				{ text: '工作机构', dataIndex: 'gongzuojg', align: 'center', width: 175}
				//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
			]
			addBtn();
			if(user.role === '其他管理员' || user.role === '院领导') {
    			tbar.remove(btnAdd);
    			tbar.remove(btnEdit);
    			tbar.remove(btnDel);
    		}
		}
		 
		 else if(title =='应急预案、现场处置方案编审批'){        	
				dataStore = store_Yingjiyuan;
				dataStore.load();
				queryURL = 'EmeRescueAction!getYingjiyuanListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID+ "&projectName=" + projectName;
				column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	        	    								
					{ text: '应急救预案/现场处置方案名称', dataIndex: 'zuizhiname', align: 'center', width: 175},
					{ text: '编制人', dataIndex: 'bianzhiren', align: 'center', width: 175},
					{ text: '编制时间', dataIndex: 'bianzhitime', align: 'center', width: 175},
					{ text: '审核人', dataIndex: 'shenheren', align: 'center', width: 175},
					{ text: '审核时间', dataIndex: 'shenhetime', align: 'center', width: 175},
					{ text: '批准人', dataIndex: 'pizhunren', align: 'center', width: 175},
					{ text: '批准时间', dataIndex: 'pizhuntime', align: 'center', width: 175}
					//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
				]
				addBtn();
				if(user.role === '其他管理员' || user.role === '院领导') {
	    			tbar.remove(btnAdd);
	    			tbar.remove(btnEdit);
	    			tbar.remove(btnDel);
	    		}
			}
		 
		 
		//应急设施、装备、物资       项目部防洪度汛物资     183分包方应急设备设施、物资
		 else if (tableID == 173 || tableID == 176 ) {	
			dataStore = store_Managepara();
			queryURL = 'EmeRescueAction!getManageparaListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID+ "&projectName=" + projectName;
			column = [
				{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
				{ text: '设施、装备、物资名称', dataIndex: 'Content', align: 'center', width: 250},
				{ text: '所属类别', dataIndex: 'Type', align: 'center', width: 100},
				{ text: '数量', dataIndex: 'Quantity', align: 'center', width: 125},
				{ text: '单位', dataIndex: 'Unit', align: 'center', width: 125},
				{ text: '设施、装备、物资状态', dataIndex: 'State', align: 'center', width: 180},
				{ text: '存放地点', dataIndex: 'Place', align: 'center', width: 200},
				{ text: '责任人', dataIndex: 'Responsible', align: 'center', width: 100},
				{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
				//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
			]
			addBtn();
			tbar.add(btnImport2);
			if(user.role === '其他管理员' || user.role === '院领导') {
    			tbar.remove(btnAdd);
    			tbar.remove(btnEdit);
    			tbar.remove(btnDel);
    			tbar.remove(btnImport2);
    		}
		}

		 else if ( tableID == 183) {	
				dataStore = store_Managepara();
				queryURL = 'EmeRescueAction!getManageparaListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID+ "&projectName=" + projectName;
				column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '设施、装备、物资名称', dataIndex: 'Content', align: 'center', width: 250},
					{ text: '所属类别', dataIndex: 'Type', align: 'center', width: 100},
					{ text: '数量', dataIndex: 'Quantity', align: 'center', width: 125},
					{ text: '单位', dataIndex: 'Unit', align: 'center', width: 125},
					{ text: '分包单位', dataIndex: 'Fbunit', align: 'center', width: 125},
					{ text: '设施、装备、物资状态', dataIndex: 'State', align: 'center', width: 180},
					{ text: '存放地点', dataIndex: 'Place', align: 'center', width: 200},
					{ text: '责任人', dataIndex: 'Responsible', align: 'center', width: 100},
					{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
					//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
				]
				addBtn();
				tbar.add(btnImport2);
				if(user.role === '其他管理员' || user.role === '院领导') {
	    			tbar.remove(btnAdd);
	    			tbar.remove(btnEdit);
	    			tbar.remove(btnDel);
	    			tbar.remove(btnImport2);
	    		}
			}
		 
			else if(title =='应急培训与演练'){        	
				dataStore = store_Yingjipxyl;
				dataStore.load();
				queryURL = 'EmeRescueAction!getYingjipxylListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID+ "&projectName=" + projectName;
				column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	        	    								
					{ text: '培训与演练内容', dataIndex: 'content', align: 'center', width: 175},
					{ text: '培训与演练时间', dataIndex: 'peixuntime', align: 'center', width: 175}
					//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
				]
				addBtn();
				if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
			}
			
		
			else if(title =='分包方防洪度汛技术要求与应急预案'||title =='分包方应急预案及现场处置方案'||title =='分包方应急培训演练'){        	
				dataStore = store_Yingjifenbao;
				
				dataStore.load();
				queryURL = 'EmeRescueAction!getYingjifenbaoListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID+ "&projectName=" + projectName;
				column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	        	    								
					{ text: '分包单位名称', dataIndex: 'fenbaoname', align: 'center', width: 175},
					{ text: '文件名称', dataIndex: 'filename', align: 'center', width: 350},
					{ text: '上传时间', dataIndex: 'uploadtime', align: 'center', width: 175}
					//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
				]
				addBtn();
				if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
			}
		 
		 
		 //显示的时候没有以下两个子目录
		 else if (title == '特种设备管理台账') {        	
			dataStore = store_SpEquip;
			dataStore.load();
			queryURL = 'EmeRescueAction!getSpEquipListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID;
			column = [
				{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	        	    								
				{ text: '名称', dataIndex: 'Name', align: 'center', width: 175},
				{ text: '型号及规格', dataIndex: 'Type', align: 'center', width: 175},
				{ text: '用途', dataIndex: 'Purpose', align: 'center', width: 175},
				{ text: '进场时间', dataIndex: 'InDate', align: 'center', width: 175},
				{ text: '出场时间', dataIndex: 'OutDate', align: 'center', width: 175},
				{ text: '车牌号/使用登记号', dataIndex: 'RegistNo', align: 'center', width: 175},
				{ text: '设备种类', dataIndex: 'Kind', align: 'center', width: 175},
				{ text: '制造单位', dataIndex: 'ManuUnit', align: 'center', width: 175},
				{ text: '购置时间', dataIndex: 'PurchaseDate', align: 'center', width: 175},
				{ text: '安装单位', dataIndex: 'InstallUnit', align: 'center', width: 175},
				{ text: '检验时间及情况', dataIndex: 'CheckStatus', align: 'center', width: 175},
				{ text: '使用状态', dataIndex: 'UseStatus', align: 'center', width: 175},
				{ text: '重大维修情况', dataIndex: 'MajorStatus', align: 'center', width: 175},
				{ text: '其他变更情况', dataIndex: 'OtherStatus', align: 'center', width: 175}
				//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
			]
			addBtn();
			tbar.add(btnImport);
			if(user.role === '其他管理员' || user.role === '院领导') {
    			tbar.remove(btnAdd);
    			tbar.remove(btnEdit);
    			tbar.remove(btnDel);
    			tbar.remove(btnImport);
    		}
		} else if (title == '电气设备设施管理台账') {        	
			dataStore = store_ElecEquip;
			dataStore.load();
			queryURL = 'EmeRescueAction!getElecEquipListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID+ "&projectName=" + projectName;
			column = [
				{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	        	    								
				{ text: '设备编号', dataIndex: 'EquipNo', align: 'center', width: 175},
				{ text: '名称', dataIndex: 'Name', align: 'center', width: 175},
				{ text: '型号及规格', dataIndex: 'Type', align: 'center', width: 175},
				{ text: '制造单位', dataIndex: 'ManuUnit', align: 'center', width: 175},
				{ text: '数量', dataIndex: 'Quantity', align: 'center', width: 175},
				{ text: '单位', dataIndex: 'Unit', align: 'center', width: 175},
				{ text: '进场时间', dataIndex: 'InDate', align: 'center', width: 175},
				{ text: '使用编号', dataIndex: 'RegistNo', align: 'center', width: 175},
				{ text: '使用地点', dataIndex: 'UsePlace', align: 'center', width: 175},
				{ text: '责任人', dataIndex: 'Responser', align: 'center', width: 175}
				//{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
			]
			addBtn();
			tbar.add(btnImport);
			if(user.role === '其他管理员' || user.role === '院领导') {
    			tbar.remove(btnAdd);
    			tbar.remove(btnEdit);
    			tbar.remove(btnDel);
    			tbar.remove(btnImport);
    		}
		}
		
		
		
		
	  
		//状态栏
		var bbar = Ext.create('Ext.PagingToolbar',{
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
		var createForm = function (config) {
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
								
								case "editYingjijyzz": {
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									uploadPanel.store.removeAll();
									break;
								}
								case "addYingjijyzz":{
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									uploadPanel.store.removeAll();
								}  
								
								
								case "editYingjiyuan": {
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									uploadPanel.store.removeAll();
									break;
								}
								case "addYingjiyuan":{
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									uploadPanel.store.removeAll();
								}  
								
								case "editYingjipxyl": {
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									uploadPanel.store.removeAll();
									break;
								}
								case "addYingjipxyl":{
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									uploadPanel.store.removeAll();
								}  
								
								
								
								case "editYingjifenbao": {
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									uploadPanel.store.removeAll();
									break;
								}
								case "addYingjifenbao":{
									var fileName = getFileName();
									if(fileName == null)
										fileName = "";
									config.url += "&fileName=" + fileName;
									uploadPanel.store.removeAll();
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
          	                		if (title == '应急救援组织') actionId = 8;
          	                		else if(title == '应急设施、装备、物资') actionId = 29;
          	                		
          	                		
          	                		
          	                		
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
						if (config.action == "editProject") {
							forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
						}
						else if (config.action == "editYingjijyzz") {
							forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
						}
						else if (config.action == "editYingjipxyl") {
							forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
						}
						else if (config.action == "editYingjifenbao") {
							forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
						}else if (config.action == "editYingjiyuan") {
							forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
						}
					}
				}],
				renderTo: Ext.getBody()
			});// forms定义结束
			
			if (config.action == 'editProject') {
				forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据
			}
			else if (config.action == "editYingjijyzz") {
				forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
			}
			else if (config.action == "editYingjipxyl") {
				forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
			}
			else if (config.action == "editYingjifenbao") {
				forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
			}else if (config.action == "editYingjiyuan") {
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
			listeners: {
				//itemclick: onRowClick,
				selectionchange: function(me, selected, eOpts) {
					var selRecs = gridDT.getSelectionModel().getSelection();
					//只能选一个的按钮
					if(selRecs.length == 1) {
						btnEdit.enable();        			
						btnScan.enable();
					} else {
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
				'celldblclick': function(self, td, cellIndex, record, tr, rowIndex){
					var html_str = "";
					var record = dataStore.getAt(rowIndex);
					var Num = rowIndex + 1;
					if (tableID == 173 || tableID == 176) {
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Content') + "详细信息</center></h1>"
								+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								+ "<tr height=\"20px;\"><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td>"
								+ "<td width=\"15%\" style=\"padding:5px;\">设施、装备、物资名称</td><td width=\"40%\">" + record.get("Content") + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">所属类别</td><td>" + record.get('Type') + "</td>"
								+ "<td style=\"padding:5px;\">数量</td><td>" + record.get('Quantity') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">单位</td><td>" + record.get('Unit') + "</td>"
								+ "<td style=\"padding:5px;\">设施、装备、物资状态</td><td>" + record.get('State') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">存放地点</td><td>" + record.get('Place') + "</td>"
								+ "<td style=\"padding:5px;\">责任人</td><td>" + record.get('Responsible') + "</td></tr>";
						html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
						
						var misfile = record.get('Accessory').split('*');
							if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
					
						height = 500;
						width = 800;         		        
						for(var i = 2;i<misfile.length;i++){							
							scanfileName = getScanfileName(misfile[i]); 
							displayfileName = misfile[i];
							html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
						}
						html_str += "</table>";
					} 
					if (tableID == 183) {
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Content') + "详细信息</center></h1>"
								+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								+ "<tr height=\"20px;\"><td width=\"15%\" style=\"padding:5px;\">序号</td ><td colspan=\"3\">" + Num + "</td>"
								+ "<tr><td width=\"15%\" style=\"padding:5px;\">设施、装备、物资名称</td><td width=\"40%\">" + record.get("Content") + "</td>" 
							    +	"<td style=\"padding:5px;\">分包单位</td><td>" + record.get('Fbunit') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">所属类别</td><td>" + record.get('Type') + "</td>"
								+ "<td style=\"padding:5px;\">数量</td><td>" + record.get('Quantity') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">单位</td><td>" + record.get('Unit') + "</td>"
								+ "<td style=\"padding:5px;\">设施、装备、物资状态</td><td>" + record.get('State') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">存放地点</td><td>" + record.get('Place') + "</td>"
								+ "<td style=\"padding:5px;\">责任人</td><td>" + record.get('Responsible') + "</td></tr>";
						html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
						
						var misfile = record.get('Accessory').split('*');
							if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
					
						height = 500;
						width = 800;         		        
						for(var i = 2;i<misfile.length;i++){							
							scanfileName = getScanfileName(misfile[i]); 
							displayfileName = misfile[i];
							html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
						}
						html_str += "</table>";
					} 
					else if (title == '特种设备管理台账') {        	
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Name') + "详细信息</center></h1>"
						+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
						+ "<tr><td style=\"padding:5px;\">名称</td><td>" + record.get('Name') + "</td>"
						+ "<td style=\"padding:5px;\">型号和规格</td><td>" + record.get('Type') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">进场时间</td><td>" + record.get('InDate') + "</td>"
						+ "<td style=\"padding:5px;\">出场时间</td><td>" + record.get('OutDate') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">用途</td><td>" + record.get('Purpose') + "</td>"
						+ "<td style=\"padding:5px;\">设备种类</td><td>" + record.get('Kind') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">车牌号/使用登记号</td><td>" + record.get('RegistNo') + "</td>"
						+ "<td style=\"padding:5px;\">制造单位</td><td>" + record.get('ManuUnit') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">购置时间</td><td>" + record.get('PurchaseDate') + "</td>"
						+ "<td style=\"padding:5px;\">安装单位</td><td>" + record.get('InstallUnit') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">检测时间及情况</td><td>" + record.get('CheckStatus') + "</td>"
						+ "<td style=\"padding:5px;\">使用情况</td><td>" + record.get('UseStatus') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">重大维修情况</td><td>" + record.get('MajorStatus') + "</td>"
						+ "<td style=\"padding:5px;\">其他变更情况</td><td>" + record.get('OtherStatus') + "</td></tr>";
					} else if (title == '电气设备设施管理台账') { 
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Name') + "详细信息</center></h1>"
						+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
						+ "<tr><td style=\"padding:5px;\">设备编号</td><td>" + record.get('EquipNo') + "</td>"
						+ "<td style=\"padding:5px;\">设备名称</td><td>" + record.get('Name') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">型号和规格</td><td>" + record.get('Type') + "</td>"
						+ "<td style=\"padding:5px;\">制造单位</td><td>" + record.get('ManuUnit') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">数量</td><td>" + record.get('Quantity') + "</td>"
						+ "<td style=\"padding:5px;\">单位</td><td>" + record.get('Unit') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">进场时间</td><td>" + record.get('InDate') + "</td>"
						+ "<td style=\"padding:5px;\">使用编号</td><td>" + record.get('RegistNo') + "</td></tr>"
						+ "<tr><td style=\"padding:5px;\">使用地点</td><td>" + record.get('UsePlace') + "</td>"
						+ "<td style=\"padding:5px;\">责任人</td><td>" + record.get('Responser') + "</td></tr>";
					}
					else if (title == '应急救援组织') {
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Content') + "详细信息</center></h1>"
								+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								+ "<tr height=\"20px;\"><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td>"
								+ "<td width=\"15%\" style=\"padding:5px;\">应急救援组织名称</td><td width=\"40%\">" + record.get("zuizhiname") + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">组织成立/调整</td><td>" + record.get('clortz') + "</td>"
								+ "<td style=\"padding:5px;\">成立/调整时间</td><td>" + record.get('clortztime') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">负责人</td><td>" + record.get('fuzeren') + "</td>"
								+ "<td style=\"padding:5px;\">成员组成态</td><td>" + record.get('chengyuan') + "</td></tr>"
								"<tr><td style=\"padding:5px;\">工作机构</td><td colspan=\"3\" align=\"left\">"+record.get('gongzuojg')+"</td></tr>";   
						html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
						
						var misfile = record.get('Accessory').split('*');
							if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
					
						height = 500;
						width = 800;         		        
						for(var i = 2;i<misfile.length;i++){							
							scanfileName = getScanfileName(misfile[i]); 
							displayfileName = misfile[i];
							html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
						}
						html_str += "</table>";
					}
					else if (title == '应急预案、现场处置方案编审批') {
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Content') + "详细信息</center></h1>"
								+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								+ "<tr height=\"20px;\"><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td>"
								+ "<td width=\"15%\" style=\"padding:5px;\">应急救预案/现场处置方案名称</td><td width=\"40%\">" + record.get("zuizhiname") + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">编制人</td><td>" + record.get('bianzhiren') + "</td>"
								+ "<td style=\"padding:5px;\">编制时间</td><td>" + record.get('bianzhitime') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">审核人</td><td>" + record.get('shenheren') + "</td>"
								+ "<td style=\"padding:5px;\">审核时间</td><td>" + record.get('shenhetime') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">批准人</td><td>" + record.get('pizhunren') + "</td>"
								+ "<td style=\"padding:5px;\">批准时间</td><td>" + record.get('pizhuntime') + "</td></tr>";   
						html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
						
						var misfile = record.get('Accessory').split('*');
							if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
					
						height = 500;
						width = 800;         		        
						for(var i = 2;i<misfile.length;i++){							
							scanfileName = getScanfileName(misfile[i]); 
							displayfileName = misfile[i];
							html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
						}
						html_str += "</table>";
					}
					
					
					else if (title == '应急培训与演练') {
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Content') + "详细信息</center></h1>"
								+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								+ "<tr height=\"20px;\"><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td>"
								+ "<td width=\"15%\" style=\"padding:5px;\">培训与演练内容</td><td width=\"40%\">" + record.get("content") + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">培训与演练时间</td><td>" + record.get('peixuntime') + +"</td></tr>";   
						html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
						
						var misfile = record.get('Accessory').split('*');
							if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
					
						height = 500;
						width = 800;         		        
						for(var i = 2;i<misfile.length;i++){							
							scanfileName = getScanfileName(misfile[i]); 
							displayfileName = misfile[i];
							html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
						}
						html_str += "</table>";
					}
					else if (title =='分包方防洪度汛技术要求与应急预案'||title =='分包方应急预案及现场处置方案'||title =='分包方应急培训演练') {
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Content') + "详细信息</center></h1>"
								+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								+ "<tr height=\"20px;\"><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td>"
								+ "<td width=\"15%\" style=\"padding:5px;\">分包单位名称</td><td width=\"40%\">" + record.get("fenbaoname") + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">文件名称</td><td>" + record.get('filename') +"</td>"
								+ "<td width=\"15%\" style=\"padding:5px;\">上传时间</td><td width=\"40%\">" + record.get("uploadtime") + "</td></tr>"  
						html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
						
						var misfile = record.get('Accessory').split('*');
							if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
					
						height = 500;
						width = 800;         		        
						for(var i = 2;i<misfile.length;i++){							
							scanfileName = getScanfileName(misfile[i]); 
							displayfileName = misfile[i];
							html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
						}
						html_str += "</table>";
					}
					
					
				
						
						
					Ext.create('Ext.window.Window', {
						title: '查看详情',
						titleAlign: 'center',
						height: 350,
						width: 700,
						closeAction: 'destroy',
						layout: 'fit',
						autoScroll: true,
						tools: [{
							type: 'print',
							handler: function() {
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
		});
		panel.add(gridDT);
		container.add(panel).show();
	}
});