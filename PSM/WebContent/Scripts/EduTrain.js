Ext.define('EduTraninGrid', {
	requires: [
		'Ext.data.Model',
		'Ext.grid.Panel',
		'Ext.form.ComboBox'
	],
	toolbar: null,
	selRecs: [],
	createGrid: function(config) {
		var me = this;
		var projectName = config.projectName;
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
		var fileRecs = [];
		var param;								 //存放gridDT选择的行
		var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';	//必填项红色星号*
		var projectNo = config.projectNo;
		
		// 表示题库四种题型中的一种
		var QState = 'radioQuestion1';
		
		Date.prototype.Format = function (fmt) { //author: meizz 
			var o = {
				"M+": this.getMonth() + 1, //月份 
				"d+": this.getDate(), //日 
				"h+": this.getHours(), //小时 
				"m+": this.getMinutes(), //分 
				"s+": this.getSeconds(), //秒 
				"q+": Math.floor((this.getMonth() + 3) / 3), //季度 
				"S": this.getMilliseconds() //毫秒 
			};
			if (/(y+)/.test(fmt)) 
				fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
			for (var k in o)
				if (new RegExp("(" + k + ")").test(fmt)) 
					fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
			return fmt;
		}
		
		var getSel = function (grid) {
			selRecs = [];  //清空数组
			keyIDs = [];
			if (title == '领导讲话' || title == '制度宣传' || title == '安全技术' || title == '其他专题学习') 
				selRecs = fileRecs.slice(0);
			else
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
			var info_url = 'EduTrainAction!getFileInfo';
			var delete_url = 'EduTrainAction!deleteOneFile';
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
				name = record.get('name') + "*";
			else
				name += record.get('name') + "*";
			})
			return name;
		}
		
		//删除上传文件
		var deleteFile = function(fileName, ppid) {
			var deleteAllUrl = "EduTrainAction!deleteAllFile";
			$.getJSON(deleteAllUrl,
			{	fileName: fileName, id: ppid},	//Ajax参数
				function (res) {
					if (!res.success)             
						Ext.Msg.alert("信息", res.msg);
			});
		}
		
		var DeleteFile = function(action) {
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
		
		var store_Trainplan1 = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'Content'},
					 { name: 'Employee'},
					 { name: 'Method'},
					 { name: 'ClassDate'},
					 { name: 'ClassTime'},
					 { name: 'Budget'},
					 { name: 'Result'},
					 { name: 'ProjectName'},
					 { name: 'Accessory'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getTrainplan1ListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + (title == '年度安全教育培训汇总' ? '' : projectName)),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		var store_Trainplan2 = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'Content'},
					 { name: 'Method'},
					 { name: 'ActDate'},
					 { name: 'RegistDate'},
					 { name: 'Funding'},
					 { name: 'ProjectName'},
					 { name: 'Accessory'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getTrainplan2ListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + (title == '三项业务宣传培训汇总' ? '' : projectName)),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		
		var store_Trainplan1Sum = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'FileName'},
					 { name: 'Year'},
					 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '年度安全教育培训' + "&projectName=" + ""),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json'
				}
			}
		});	
		
		var store_Trainplan2Sum = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'FileName'},
					 { name: 'Year'},
					 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '三项业务宣传培训' + "&projectName=" + ""),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json'
				}
			}
		});	
		
		
		var store_Persondb = Ext.create('Ext.data.Store', {
			fields: [
				{ name: 'ID'},
				{ name: 'PType'},
				{ name: 'Name'},
				{ name: 'Sex'},
				{ name: 'IDCard'},
				{ name: 'Birthday'},
				{ name: 'Phone'},
				{ name: 'PhoneUrgent'},
				{ name: 'PapersType'},
				{ name: 'PapersNo'},
				{ name: 'PapersDate'},
				{ name: 'PapersTypeTwo'},
				{ name: 'PapersNoTwo'},
				{ name: 'PapersDateTwo'},
				{ name: 'UserPwd'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('BasicInfoAction!getPersondbListReflash?userName=' + user.name + "&userRole=" +user.role + '&projectName=' + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		var store_Traintable = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'Content'},
					 { name: 'Employee'},
					 { name: 'Method'},
					 { name: 'TrainDate'},
					 { name: 'RegistDate'},
					 { name: 'Funding'},
					 { name: 'Accessory'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getTraintableListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID + "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		var store_fbdailytrain = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'Fenbao'},
					 { name: 'StartDate'},
					 { name: 'EndDate'},
					 { name: 'Time'},
					 { name: 'RegistDate'},
					 { name: 'Record'},
					 { name: 'Accessory'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getFbdailytrainListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		var store_fbactivity = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'ActivityDate'},
					 { name: 'Organization'},
					 { name: 'Theme'},
					 { name: 'Accessory'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getFbactivityListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});		
		var store_question = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'BelongTo'},
					 { name: 'Type'},
					 { name: 'Question'},
					 { name: 'OptionA'},
					 { name: 'OptionB'},
					 { name: 'OptionC'},
					 { name: 'OptionD'},
					 { name: 'OptionE'},
					 { name: 'Answer'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getQuestionListDef?userName=' + user.name + "&userRole=" + user.role),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		var store_testrecord = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'Name'},
					 { name: 'BelongTo'},
					 { name: 'ActualNum'},
					 { name: 'AvgScore'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getTestrecordListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + (tableID == 293 ? '设计院题库' : projectName)),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		var store_testpaper = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'Name'},
					 { name: 'Project'},
					 { name: 'IDNO'},
					 { name: 'Score1'},
					 { name: 'Score2'},
					 { name: 'Score3'},
					 { name: 'Score4'},
					 { name: 'Score'},
					 { name: 'TestDate'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getTestpaperListDef?userName=' + user.name + "&userRole=" + user.role),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		var store_Project = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'ID'},
					 { name: 'Name'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('EduTrainAction!getProjectListDef?userName=' + user.name + "&userRole=" + user.role + "&tableID=" + tableID),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
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
				url: encodeURI('EduTrainAction!getMultimediafileListDef?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&ProjectName="),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json', //返回数据类型为json格式
					root: 'rows',  //数据
					totalProperty: 'total' //数据总条数
				}
			}
		});
		
		var items_Trainplan1 = [{
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
					fieldLabel: 'Accessory',
					labelWidth: 120,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Accessory',
					hidden: true,
					hiddenLabel: true
				},{
					xtype:'textfield',
					fieldLabel: '教育培训主题',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Content'
				},{
					xtype:"datefield",
					format:"Y-m-d",
					fieldLabel: '计划时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'ClassDate'
				},{
					xtype:'combo',
					fieldLabel: '培训方式',
					store: ['内培','外培'],
					value: '内培',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Method'
				}]
			},{
				xtype: 'container',
				flex: 1,
				layout: 'anchor',
				items: [{
					xtype:'textfield',
					fieldLabel: '培训对象',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Employee'
				},{
					xtype:'textfield',
					fieldLabel: '计划课时',
					labelAlign: 'right',
					anchor:'100%',
					name: 'ClassTime'
				},{
					xtype:'textfield',
					fieldLabel: '预算',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Budget'
				}]
			}]
		},{
			xtype:'textarea',
			fieldLabel: '落实情况',
			labelAlign: 'right',
			anchor:'100%',
			name: 'Result'
		},
			uploadPanel
		]
		var items_Trainplan2_add = [{
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
					fieldLabel: 'Accessory',
					labelWidth: 120,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Accessory',
					hidden: true,
					hiddenLabel: true
				},{
					xtype:'textfield',
					fieldLabel: '宣传培训主题',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Content'
				},{
					xtype:"datefield",
					format:"Y-m-d",
					fieldLabel: '活动时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'ActDate'
				},{
					xtype:'textfield',
					fieldLabel: '经费记录',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Funding'
				}]
			},{
				xtype: 'container',
				flex: 1,
				layout: 'anchor',
				items: [{
					xtype:'textfield',
					fieldLabel: '宣传培训形式',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Method'
				},{
					xtype:'textfield',
					fieldLabel: '落实情况',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Result'
				}]
			}]
		},
			uploadPanel
		]
		var items_Trainplan2_edit = [{
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
					fieldLabel: 'Accessory',
					labelWidth: 120,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Accessory',
					hidden: true,
					hiddenLabel: true
				},{
					xtype:'textfield',
					fieldLabel: '宣传培训主题',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Content'
				},{
					xtype:"datefield",
					format:"Y-m-d",
					fieldLabel: '活动时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'ActDate'
				},{
					xtype:'textfield',
					fieldLabel: '经费记录',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Funding'
				}]
			},{
				xtype: 'container',
				flex: 1,
				layout: 'anchor',
				items: [{
					xtype:'textfield',
					fieldLabel: '宣传培训形式',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Method'
				},{
					xtype:"datefield",
					format:"Y-m-d",
					fieldLabel: '登记时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'RegistDate'
				},{
					xtype:'textfield',
					fieldLabel: '落实情况',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Result'
				}]
			}]
		},
			uploadPanel
		]
		var items_Persondb = [{
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
						xtype:'combo',
						fieldLabel: '人员类型',
						allowBlank : false,
						labelAlign: 'right',
						anchor:'95%',
						store:["项目经理","项目副经理","项目总工","项目施工经理","项目安全总监"],
						value :'项目经理',
						name: 'PType'
					},{
						xtype:'textfield',
						fieldLabel: '身份证号',
						//labelWidth: 120,
						labelAlign: 'right',
						anchor:'95%',
						name: 'IDCard',
						allowBlank : false,
						maxLength : 18,
						minLength : 15,
						validator : function isCardNo(value)  
									{  
										// 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X  
									var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;  
									if(reg.test(value) === false)  
									{   
										return  "身份证输入不合法";  
									 }
									 return true;
									}
						}
						,{
						xtype:'textfield',
						fieldLabel: '联系电话',
						allowBlank : false,
						//labelWidth: 120,
						labelAlign: 'right',
						anchor:'95%',
						name: 'Phone'
					
					},{
						xtype:'combo',
						fieldLabel: '持证类型1',
						labelAlign: 'right',
						anchor:'95%',
						store:["项目经理证","一级建造师证","二级建造师证","注册安全工程师","安监局培训资格证","住建厅安全考核合格证（A证）","住建厅安全考核合格证（B证)","住建厅安全考核合格证（C证）","能源局安全资格证"],
						value :'项目经理证',
						name: 'PapersType'
					},{
						xtype:'textfield',
						fieldLabel: '持证编号1',
						//labelWidth: 120,
						labelAlign: 'right',
						anchor:'95%',
						name: 'PapersNo'
					},{
						xtype:"datefield",
						fieldLabel: '有效期1',
						afterLabelTextTpl: required,
						labelAlign: 'right',
						format:"Y-m-d",
						name: 'PapersDate',
						anchor:'95%',
						allowBlank: true 
					}]
				},{
					xtype: 'container',
					flex: 1,
					layout: 'anchor',
					items: [
						{
						xtype:'textfield',
						fieldLabel: '姓名',
						//labelWidth: 120,
						labelAlign: 'right',
						allowBlank : false,
						anchor:'95%',
						name: 'Name'
					},{
						xtype:'combo',
						fieldLabel: '性别',
						labelAlign: 'right',
						allowBlank : false,
						anchor:'95%',
						store:["男","女"],
						value :'男',
						name: 'Sex'
					},{
						xtype:'textfield',
						fieldLabel: '紧急电话',
						//labelWidth: 120,
						labelAlign: 'right',
						anchor:'95%',
						name: 'PhoneUrgent'
					},{
						xtype:'textfield',
						fieldLabel: '生日',
						//labelWidth: 120,
						labelAlign: 'right',
						anchor:'95%',
						name: 'Birthday',
						hidden: true,
						hiddenLabel: true
					},{
						xtype:'textfield',
						fieldLabel: '密码',
						//labelWidth: 120,
						labelAlign: 'right',
						anchor:'95%',
						name: 'UserPwd',
						hidden: true,
						hiddenLabel: true
					},{
						xtype:'combo',
						fieldLabel: '持证类型2',
						labelAlign: 'right',
						anchor:'95%',
						store:["项目经理证","一级建造师证","二级建造师证","注册安全工程师","安监局培训资格证","住建厅安全考核合格证（A证）","住建厅安全考核合格证（B证)","住建厅安全考核合格证（C证）","能源局安全资格证"],
						value :'项目经理证',
						name: 'PapersTypeTwo'
					},{
						xtype:'textfield',
						fieldLabel: '持证编号2',
						//labelWidth: 120,
						labelAlign: 'right',
						anchor:'95%',
						name: 'PapersNoTwo'
					},
						{
						xtype:"datefield",
						fieldLabel: '有效期2',
						afterLabelTextTpl: required,
						//labelWidth: 200,
						labelAlign: 'right',
						format:"Y-m-d",
						name: 'PapersDateTwo',
						anchor:'95%',
						allowBlank: true 
					}]
				}]
			}]
		var items_Traintable = [{
			xtype:'textfield',
			fieldLabel: '教育培训主题',
			labelAlign: 'right',
			anchor:'100%',
			name: 'Content'
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
					xtype:'combo',
					fieldLabel: '培训方式',
					store: ['内培','外培'],
					value: '内培',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Method'
				},{
					xtype:"datefield",
					format:"Y-m-d",
					fieldLabel: '登记时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'RegistDate',
					readOnly: true,
					emptyText: new Date().Format("yyyy-MM-dd")
				}]
			},{
				xtype: 'container',
				flex: 1,
				layout: 'anchor',
				items: [{
					xtype:"datefield",
					format:"Y-m-d",
					fieldLabel: '培训时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'TrainDate'
				},{
					xtype:'textfield',
					fieldLabel: '经费记录',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Funding'
				}]
			}]
		},{
			xtype:'textarea',
			fieldLabel: '培训对象',
			labelAlign: 'right',
			anchor:'100%',
			name: 'Employee',
			listeners: {
				'focus': function() {
					if (tableID != 120 && tableID != 213 && tableID != 122 )
						return;
					$.getJSON("EduTrainAction!getEmployees", {
							tableID: tableID
						}, function(response) {							
							var employees = [];
							for (var i in response.data) {
								employees.push(response.data[i].name);
							}
							var items = [];
							for (var i in employees) {
								items.push({
									xtype: "checkbox",
									boxLabel: employees[i],
									colspan: 1,
									height: 30,
									width: 120
								})
							}
							showWin({
								WinID: "Select",
								title: "培训对象",
								closeAction: 'destroy',
								items: [Ext.create('Ext.form.Panel', {
									buttonAlign: 'center',
									bodyPadding: 10,
									id: 'employees',
									layout: {
										type: 'column',
										columns: 6
									},
									items: items,
									buttons: [{
										text: '确定',
										handler: function() {
											var employees_items = Ext.getCmp('employees').items;
											var employees = "";
											for (var i = 0; i < employees_items.length; i++) {
												if (employees_items.get(i).checked) {
													employees += (employees_items.get(i).boxLabel + ",");
												}
											}
											employees = employees.substr(0, employees.length - 1);
											forms.getForm().findField('Employee').setValue(employees);
											this.up('window').close();
										}
									},{
										text: '全选',
										handler: function() {
											var employees_items = Ext.getCmp('employees').items;
											for (var i = 0; i < employees_items.length; i++) {
												employees_items.get(i).setValue(true);
											}
										}
									},{
										text: '重置',
										handler: function() {
											this.up('form').getForm().reset();
										}
									}]
								})]								
							});							
							var employees = forms.getForm().findField('Employee').getValue().split(',');
							for (var i in employees) {
								var employees_items = Ext.getCmp('employees').items;
								for (var j = 0; j < employees_items.length; j++) {
									if (employees[i] == employees_items.get(j).boxLabel)
										employees_items.get(j).setValue(true);
								}
							}
						}
					)
				}
			}
		},
			uploadPanel
		]
		var items_fbdailytrain = [{
			xtype:'textfield',
			fieldLabel: '报备分包方',
			labelAlign: 'right',
			anchor:'100%',
			name: 'Fenbao'
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
					fieldLabel: 'Accessory',
					labelWidth: 120,
					labelAlign: 'right',
					anchor:'100%',
					name: 'Accessory',
					hidden: true,
					hiddenLabel: true
				},{
					xtype:"datefield",
					format:"Y-m-d",
					fieldLabel: '开始时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'StartDate'
				},{
					xtype:"datefield",
					format:"Y-m-d",
					fieldLabel: '登记时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'RegistDate',
					readOnly: true,
					emptyText: new Date().Format("yyyy-MM-dd")
				}]
			},{
				xtype: 'container',
				flex: 1,
				layout: 'anchor',
				items: [{
					xtype:"datefield",
					format:"Y-m-d",
					fieldLabel: '结束时间',
					labelAlign: 'right',
					anchor:'100%',
					name: 'EndDate'
				},{
					xtype:'textfield',
					fieldLabel: '培训次数',
					labelAlign: 'right',
					anchor:'100%',
					name: 'Time',
					validator : function isNum(value) {
						return /^\d+$/.test(value);						
					} 
				}]
			}]
		},
			uploadPanel
		]
		var items_fbactivity1 = [{
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
			fieldLabel: 'Accessory',
			labelWidth: 120,
			labelAlign: 'right',
			anchor:'100%',
			name: 'Accessory',
			hidden: true,
			hiddenLabel: true
		},{
			xtype:"datefield",
			format:"Y-m-d",
			fieldLabel: '时间',
			labelAlign: 'right',
			anchor:'100%',
			name: 'ActivityDate'
		},{
			xtype:'textfield',
			fieldLabel: '分包单位名称',
			labelAlign: 'right',
			anchor:'100%',
			name: 'Organization'
		},
			uploadPanel
		]
		var items_fbactivity2 = [{
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
			fieldLabel: 'Accessory',
			labelWidth: 120,
			labelAlign: 'right',
			anchor:'100%',
			name: 'Accessory',
			hidden: true,
			hiddenLabel: true
		},{
			xtype:"datefield",
			format:"Y-m-d",
			fieldLabel: '时间',
			labelAlign: 'right',
			anchor:'100%',
			name: 'ActivityDate'
		},{
			xtype:'textfield',
			fieldLabel: '分包单位名称',
			labelAlign: 'right',
			anchor:'100%',
			name: 'Organization'
		},{
			xtype:'textfield',
			fieldLabel: '主题',
			labelAlign: 'right',
			anchor:'100%',
			name: 'Theme'
		},
			uploadPanel
		]
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
		
		var radioGroup_Quesiton = Ext.create('Ext.form.RadioGroup', {
			xtype:'radiogroup',
			fieldLabel: '类型',
			labelAlign: 'right',
			anchor:'100%',
			items:[
				   {boxLabel: '单选题', name: 'Type', inputValue: '单选题'},
				   {boxLabel: '多选题', name: 'Type', inputValue: '多选题'},
				   {boxLabel: '判断题', name: 'Type', inputValue: '判断题'},
				   {boxLabel: '简答题', name: 'Type', inputValue: '简答题'}
			],
			listeners: {		
				change: function(obj) {
					var Type = obj.lastValue['Type'];
					var list1 = ['A', 'B', 'C', 'D', 'E'];
					var list2 = ['1', '2', '3', '4'];
					var changeOption = function(ID, methed, list) {                    			
						if (methed == 'hide')
							for (var i in list)
								forms.getComponent(ID + list[i]).hide();
						else if (methed == 'show')
							for (var i in list)
								forms.getComponent(ID + list[i]).show();
					};
					changeOption('form_answer', 'hide', list2);	
					if (Type == '单选题') {
						changeOption('form_option', 'hide', list1);
						changeOption('form_option', 'show', ['A', 'B', 'C', 'D']);
						forms.getComponent('form_answer1').show();
					} else if( Type == "多选题") {
						changeOption('form_option', 'show', list1);
						forms.getComponent('form_answer2').show();
					} else if (Type == '判断题') {
						changeOption('form_option', 'hide', list1);
						forms.getComponent('form_answer3').show();
					} else if (Type == '简答题') {
						changeOption('form_option', 'hide', list1);
						forms.getComponent('form_answer4').show();
					}
				}
			}
		})
		var items_Question = [{
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
				},
				radioGroup_Quesiton]
			}]
		},{
			xtype:'textarea',
			fieldLabel: '题目',
			itemId: 'form_question',
			labelAlign: 'right',
			anchor:'100%',
			name: 'Question'
		},{
			xtype:'textfield',
			fieldLabel: '选项A',
			itemId: 'form_optionA',
			labelAlign: 'right',
			anchor:'100%',
			name: 'OptionA'
		},{
			xtype:'textfield',
			fieldLabel: '选项B',
			itemId: 'form_optionB',
			labelAlign: 'right',
			anchor:'100%',
			name: 'OptionB'
		},{
			xtype:'textfield',
			fieldLabel: '选项C',
			itemId: 'form_optionC',
			labelAlign: 'right',
			anchor:'100%',
			name: 'OptionC'
		},{
			xtype:'textfield',
			fieldLabel: '选项D',
			itemId: 'form_optionD',
			labelAlign: 'right',
			anchor:'100%',
			name: 'OptionD'
		},{
			xtype:'textfield',
			fieldLabel: '选项E',
			hidden: true,
			itemId: 'form_optionE',
			labelAlign: 'right',
			anchor:'100%',
			name: 'OptionE'
		},{
			xtype:'radiogroup',
			fieldLabel: '答案',
			itemId: 'form_answer1',
			labelAlign: 'right',
			anchor:'100%',
			items:[
				   {boxLabel: '选项A', name: 'Answer1', inputValue: 'A', checked: true},
				   {boxLabel: '选项B', name: 'Answer1', inputValue: 'B'},
				   {boxLabel: '选项C', name: 'Answer1', inputValue: 'C'},
				   {boxLabel: '选项D', name: 'Answer1', inputValue: 'D'}
			]
		},{
			xtype:'checkboxgroup',
			hidden: true,
			fieldLabel: '答案',
			itemId: 'form_answer2',
			labelAlign: 'right',
			anchor:'100%',
			items:[
				   {boxLabel: '选项A', name: 'Answer2', inputValue: 'A'},
				   {boxLabel: '选项B', name: 'Answer2', inputValue: 'B'},
				   {boxLabel: '选项C', name: 'Answer2', inputValue: 'C'},
				   {boxLabel: '选项D', name: 'Answer2', inputValue: 'D'},
				   {boxLabel: '选项E', name: 'Answer2', inputValue: 'E'}
			]
		},{
			xtype:'radiogroup',
			hidden: true,
			fieldLabel: '答案',
			itemId: 'form_answer3',
			labelAlign: 'right',
			anchor:'100%',
			items:[
				   {boxLabel: '正确', name: 'Answer3', inputValue: 'T', checked: true},
				   {boxLabel: '错误', name: 'Answer3', inputValue: 'F'}
			]
		},{
			xtype:'textarea',
			hidden: true,
			fieldLabel: '答案',
			itemId: 'form_answer4',
			emptyText: '答案关键字用中文逗号隔开，如：关键词1，关键词2，关键词3',
			labelAlign: 'right',
			anchor:'100%',
			name: 'Answer'
		}]
		var items_Testrecord = [{
			xtype:'textfield',
			fieldLabel: 'ID',
			labelWidth: 80,
			labelAlign: 'right',
			anchor:'100%',
			name: 'ID',
			hidden: true,
			hiddenLabel: true
		},{
			xtype:'textfield',
			fieldLabel: '考试名称',
			labelWidth: 80,
			labelAlign: 'right',
			anchor:'100%',
			name: 'Name'
		},{
			xtype:'combobox',
			fieldLabel: '试题来源',
			store: title == '设计院考试信息' ? ['设计院题库'] : ['设计院题库', '项目部题库', '全部题库'],
			labelWidth: 80,
			labelAlign: 'right',
			anchor:'100%',
			name: 'Source'
		},{
			xtype: 'container',
			anchor: '100%',
			layout: 'hbox',
			margin: '5 0 0 0',
			items: [{
				xtype:'numberfield',
				fieldLabel: '选择题数量',
                value: 20,
                allowDecimals: false, // 不允许输入小数
                regex:/^\d+$/,
                regexText :'只能为数字',
                nanText: "请输入有效的整数",
				labelWidth: 80,
				labelAlign: 'right',
				flex: 1,
				name: 'Question1Num'
			},{
				xtype:'numberfield',
				fieldLabel: '单题分数',
                value: 2,
                allowDecimals: false, // 不允许输入小数
                regex:/^\d+$/,
                regexText :'只能为数字',
				labelWidth: 80,
				labelAlign: 'right',
				flex: 1,
				name: 'QuestionScore1'
			}]
		},{
			xtype: 'container',
			anchor: '100%',
			layout: 'hbox',
			margin: '5 0 0 0',
			items: [{
				xtype:'numberfield',
				fieldLabel: '多选题数量',
				value: 10,
                allowDecimals: false, // 不允许输入小数
                regex:/^\d+$/,
                regexText :'只能为数字',
				labelWidth: 80,
				labelAlign: 'right',
				flex: 1,
				name: 'Question2Num'
			},{
				xtype:'numberfield',
				fieldLabel: '单题分数',
				value: 4,
                allowDecimals: false, // 不允许输入小数
                regex:/^\d+$/,
                regexText :'只能为数字',
				labelWidth: 80,
				labelAlign: 'right',
				flex: 1,
				name: 'QuestionScore2'
			}]
		},{
			xtype: 'container',
			anchor: '100%',
			layout: 'hbox',
			margin: '5 0 0 0',
			items: [{
				xtype:'numberfield',
				fieldLabel: '判断题数量',
				value: 20,
                allowDecimals: false, // 不允许输入小数
                regex:/^\d+$/,
                regexText :'只能为数字',
				labelWidth: 80,
				labelAlign: 'right',
				flex: 1,
				name: 'Question3Num'
			},{
				xtype:'numberfield',
				fieldLabel: '单题分数',
				value: 1,
                allowDecimals: false, // 不允许输入小数
                regex:/^\d+$/,
                regexText :'只能为数字',
				labelWidth: 80,
				labelAlign: 'right',
				flex: 1,
				name: 'QuestionScore3'
			}]
		},{
			xtype: 'container',
			anchor: '100%',
			layout: 'hbox',
			margin: '5 0 0 0',
			items: [{
				xtype:'numberfield',
				fieldLabel: '简答题数量',
				value: 0,
                allowDecimals: false, // 不允许输入小数
                regex:/^\d+$/,
                regexText :'只能为数字',
				labelWidth: 80,
				labelAlign: 'right',
				flex: 1,
				name: 'Question4Num'
			},{
				xtype:'numberfield',
				fieldLabel: '单题分数',
				value: 0,
                allowDecimals: false, // 不允许输入小数
                regex:/^\d+$/,
                regexText :'只能为数字',
				labelWidth: 80,
				labelAlign: 'right',
				flex: 1,
				name: 'QuestionScore4'
			}]
		}]
		
		var grid_Testrecord = Ext.create('Ext.grid.Panel', {
			store: store_testpaper,
			columns:  [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},		
					{ text: '姓名', dataIndex: 'Name', align: 'center', width: 100},
					{ text: '所属项目', dataIndex: 'Project', align: 'center', width: 250},
					{ text: '总分', dataIndex: 'Score', align: 'center', width: 75},
					{ text: '考试日期', dataIndex: 'TestDate', align: 'center', width: 150}
			],
			viewConfig: {
				loadMask: {						//IE8不兼容loadMask
					msg: '正在加载数据中……'
				}
			}
		});
		
		
		
		//liuchi 增加上传按钮
        //获取上传文件名
       	var getUploadFileName = function(){
       		var name = null;
            fileUploadPanel.store.each(function(record){
            if(name == null)
            	name = record.get('name') + "*"
            else
                name += record.get('name') + "*";
            })
            return name;
       	}
       	
       	
       	var FileUploadName_store = new Ext.data.JsonStore({
	            proxy: {
	                type: 'ajax',
	                url: 'GoalDutyAction!getFileUploadNameList?title=' + title
	            },
	            reader: {
	                type: 'json'
	            },
	            fields: ['FileName',
	            		'Year',
	            		'Month'
	            ],
	            autoLoad: true
	        });
        
         
        var fileUploadH = function() {
        	fileUploadPanel = Ext.create('Ext.ux.uploadPanel.UploadPanel', {
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
        		winId: 'fileUpload', 
        		title: '导入文件', 
        		items: [fileUploadPanel],
        		buttonAlign: 'center',
        		buttons: [{        	
	            	text: '确定',
	            	handler: function(){
	            		if (fileUploadPanel.store.getCount() == 0) {
	            			Ext.Msg.alert('提示', '请先上传文件');
	            			return;
	            		}	            			
	            		//var fileName = fileUploadPanel.store.getAt(0).get('name');
	            		var fileUploadName = getUploadFileName();
	            		//url += "&fileName=" + fileUploadName;
                		fileUploadPanel.store.removeAll();
	            		
	            		var win = this;
	            		$.getJSON('GoalDutyAction!addFileUpload', { fileName: fileUploadName, title : title },
	            				function(data) {
	            					if (data.success == 'true')
	            						Ext.Msg.alert('提示', '成功导入');
	            					else
	            						Ext.Msg.alert('提示', '导入失败');
	            					FileUploadName_store.reload();
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
                    	if (fileUploadPanel.store.getCount() != 0) {
                    		var fileName = fileUploadPanel.store.getAt(0).get('name');
                       		$.getJSON("GoalDutyAction!deleteAllFile", { fileName: fileName } );
                       		fileUploadPanel.store.removeAll();
                    	}
                    	bbar.moveFirst();
                    }
                }
            });
        }
        
        
        
        
        //附件浏览的下拉菜单
        var scanFileUploadMenu = new Ext.menu.Menu({
			shadow: "drop",
			allowOtherMenus: true,
			items: [
				new Ext.menu.Item({
					text: '暂无文件'
				})
			]
		})
        
	        
	        
	        
        var scanFileUploadH = function (item){
        	//var selRecs = gridDT.getSelectionModel().getSelection();
        	
	        
        	var fileArr = new Array();
        	
        	for(var i=0;i<FileUploadName_store.getCount();i++) {
        		fileArr.push(FileUploadName_store.data.items[i].data['FileName']); 
        	}
        	
			/*var accessory = selRecs[0].data.Accessory;
			var array = accessory.split("*");
			var foldname = array[0]+"\\"+array[1];*/
			scanFileUploadMenu.removeAll();
			
			//若从数据库中取出为空，则表示没有附件
			if( fileArr.length==0 )
			{
				var menuItem = Ext.create('Ext.menu.Item', {
                	text: '暂无文件'                	
                });
                scanFileUploadMenu.add(menuItem);
			}//数据库中有附件，添加到文件下拉菜单中
			else{
				for( var i = 0; i < fileArr.length; i++){
					var icon = "";
					if(fileArr[i].indexOf('.doc')>0)
						icon = "Images/ims/toolbar/report_word.png";
					else if(fileArr[i].indexOf('.xls')>0||fileArr[i].indexOf('.xlsx')>0)
						icon = "Images/ims/toolbar/report_excel.png";
					else if(fileArr[i].indexOf('.png')>0||fileArr[i].indexOf('.jpg')>0||fileArr[i].indexOf('.bmp')>0)
						icon = "Images/ims/toolbar/report_picture.png";
					else
						icon = "Images/ims/toolbar/report_other.png";
					var menuItem = Ext.create('Ext.menu.Item', {
                		text: fileArr[i],
                		icon: icon,
                		listeners:
						{
							'click': function (item, e, eOpts) {
                        		var fileName = item.text;    
                        		if(fileName.indexOf('.doc')>0||fileName.indexOf('.docx')>0)
                				{               			               				
	                        		if(e.getX()-item.getX()>20)
	                        		{
	                        			window.open("upload\\" + "fileUpload" + "\\" + title + "\\" + fileName);
	                        		}
	                        		else
	                        		{
	                        			if(fileName.indexOf('.docx')>0)
	                        				window.open("upload\\"+ "fileUpload" + "\\" + title + "\\" + fileName.substr(0,fileName.length-5)+".pdf");                    			
	                        			else	                      
	                        				window.open("upload\\"+ "fileUpload" + "\\" + title + "\\" + fileName.substr(0,fileName.length-4)+".pdf");
	                        		}
	                			}
                        		else
                        		{
                        			window.open("upload\\"+ "fileUpload" + "\\" + title + "\\" + fileName);
                        		}
                      		}
					 	}
                	});
             		scanFileUploadMenu.add(menuItem);  
				}
		 	}
        }
        
        
        
        
      
        
        
        var btnFileUpload = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '上传文件',
            icon: "Images/ims/toolbar/extexcel.png",
            handler: fileUploadH
        })
        
        var btnscanFileUpload = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '附件浏览',
            icon: "Images/ims/toolbar/view.png",
           	//disabled: true,
            menu: scanFileUploadMenu,
            handler: scanFileUploadH
        })
          //liuchi
		
		
		
		
		
		
		
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
			if (title == '年度安全教育培训' || title == '年度安全教育培训汇总') {
				actionURL = 'EduTrainAction!addTrainplan1?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName; 
				items = items_Trainplan1;
			} else if (title == '三项业务宣传培训' || title == '三项业务宣传培训汇总') {
				actionURL = 'EduTrainAction!addTrainplan2?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName; 
				items = items_Trainplan2_add;
			} else if (tableID == 116 || (tableID >= 118 && tableID <= 122) || tableID == 213) {
				actionURL = 'EduTrainAction!addTraintable?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName; 
				items = items_Traintable;
			} else if (title == '分包方人员日常教育培训') {
				actionURL = 'EduTrainAction!addFbdailytrain?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName;
				items = items_fbdailytrain;
			} else if(title == '项目负责人' || title == '安全生产管理人员') {
				actionURL = 'BasicInfoAction!addPersondb?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName;
				items = items_Persondb;
			} else if (title == '分包方安全生产班前会') {
				actionURL = 'EduTrainAction!addFbactivity?userName=' + user.name + "&userRole=" + user.role + "&Theme=" + title + "&projectName=" + projectName; 
				items = items_fbactivity1;
			} else if (title == '分包方安全生产周活动') {
				actionURL = 'EduTrainAction!addFbactivity?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName; 
				items = items_fbactivity2;
			} else if (title == '领导讲话' || title == '制度宣传' || title == '安全技术' || title == '其他专题学习' ) {
				actionURL = 'EduTrainAction!addMultimediafile?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&projectName=";  
				items = items_multimediafile;
			} else if(title == '设计院题库') {
				actionURL = 'EduTrainAction!addQuestion?userName=' + user.name + "&userRole=" + user.role + "&belongTo=" + title;  
				items = items_Question;				
			} else if(title == '项目部题库') {
				actionURL = 'EduTrainAction!addQuestion?userName=' + user.name + "&userRole=" + user.role + "&belongTo=" + projectName;  
				items = items_Question;
			} else if(tableID == 128) {
				actionURL = 'EduTrainAction!addTestrecord?userName=' + user.name + "&userRole=" + user.role + "&belongTo=" + projectName;
				items = items_Testrecord;
			} else if(tableID == 293) {
				actionURL = 'EduTrainAction!addTestrecord?userName=' + user.name + "&userRole=" + user.role + "&belongTo=" + '设计院题库';
				items = items_Testrecord;
			}
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
			if (title == '设计院题库' || title == '项目部题库') {
				var QType = '单选题';
				if (QState == 'radioQuestion1') 	 QType = '单选题';
				else if (QState == 'radioQuestion2') QType = '多选题';
				else if (QState == 'radioQuestion3') QType = '判断题';
				else if (QState == 'radioQuestion4') QType = '简答题';
				// 为了触发change时间，设置两次
				radioGroup_Quesiton.setValue({ Type: '单选题' });
				radioGroup_Quesiton.setValue({ Type: QType });
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
					if (title != '设计院题库' && title != '项目部题库' && tableID != 128 && title != '项目负责人' && title != '安全生产管理人员')
						insertFileToList();
					if (title == '年度安全教育培训' || title == '年度安全教育培训汇总') {
						actionURL = 'EduTrainAction!editTrainplan1?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName; 
						items = items_Trainplan1;
					} else if (title == '三项业务宣传培训' || title == '三项业务宣传培训汇总') {
						actionURL = 'EduTrainAction!editTrainplan2?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName; 
						items = items_Trainplan2_edit;
					} else if (tableID == 116 || (tableID >= 118 && tableID <= 122) || tableID == 213) {
						actionURL = 'EduTrainAction!editTraintable?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName; 
						items = items_Traintable;
					} else if (title == '分包方人员日常教育培训'){
						actionURL = 'EduTrainAction!editFbdailytrain?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName; 
						items = items_fbdailytrain;
					} else if(title == '项目负责人' || title == '安全生产管理人员') {
						actionURL = 'BasicInfoAction!editPersondb?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName;  
						items = items_Persondb;
					} else if (title == '分包方安全生产班前会') {
						actionURL = 'EduTrainAction!editFbactivity?userName=' + user.name + "&userRole=" + user.role + "&Theme=" + title + "&projectName=" + projectName; 
						items = items_fbactivity1;
					} else if (title == '分包方安全生产周活动') {
						actionURL = 'EduTrainAction!editFbactivity?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName; 
						items = items_fbactivity2;
					} else if (title == '领导讲话' || title == '制度宣传' || title == '安全技术' || title == '其他专题学习' ) {
						actionURL = 'EduTrainAction!editMultimediafile?userName=' + user.name + "&userRole=" + user.role + "&type=" + title + "&projectName=" + projectName;  
						items = items_multimediafile;
					} else if(title == '设计院题库') {
						actionURL = 'EduTrainAction!editQuestion?userName=' + user.name + "&userRole=" + user.role + "&belongTo=" + title; 
						items = items_Question;
					} else if(title == '项目部题库'){
						actionURL = 'EduTrainAction!editQuestion?userName=' + user.name + "&userRole=" + user.role + "&belongTo=" + projectName; 
						items = items_Question;
					}
					createForm({
						autoScroll: true,
						action: 'editProject',
						bodyPadding: 5,
						url: actionURL,
						items: items
					});
					uploadPanel.upload_url = "UploadAction!execute";
					showWin({ winId: 'editProject', title: '修改文件', items: [forms]});
				}
				else {
					 Ext.Msg.alert('警告', '只能选中一条记录！');
				}
				if (title == '设计院题库' || title == '项目部题库') {
					var QType = '单选题';
					if (QState == 'radioQuestion1') 	 QType = '单选题';
					else if (QState == 'radioQuestion2') QType = '多选题';
					else if (QState == 'radioQuestion3') QType = '判断题';
					else if (QState == 'radioQuestion4') QType = '简答题';
					// 为了触发change时间，设置两次
					radioGroup_Quesiton.setValue({ Type: '单选题' });
					radioGroup_Quesiton.setValue({ Type: QType });
				}
			}
		}
		
		var deleteH = function() {        	
			if(getSel(gridDT)) {
				Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) {
					if (buttonID === 'yes') {
						var delete_url = '';
						if (title == '年度安全教育培训' || title == '年度安全教育培训汇总') {
							delete_url = 'EduTrainAction!deleteTrainplan1';
						} else if (title == '三项业务宣传培训' || title == '三项业务宣传培训汇总') {
							delete_url = 'EduTrainAction!deleteTrainplan2';
						} else if (tableID == 116 || (tableID >= 118 && tableID <= 122) || tableID == 213){
							delete_url = 'EduTrainAction!deleteTraintable';
						} else if (title == '分包方人员日常教育培训'){
							delete_url = 'EduTrainAction!deleteFbdailytrain';
						} else if (title == '项目负责人' || title == '安全生产管理人员') {
							delete_url = 'BasicInfoAction!deletePersondb';
						} else if (title == '分包方安全生产班前会' || title == '分包方安全生产周活动') {
							delete_url = 'EduTrainAction!deleteFbactivity';
						}  else if (title == '领导讲话' || title == '制度宣传' || title == '安全技术' || title == '其他专题学习' ) {
							delete_url = 'EduTrainAction!deleteMultimediafile';
						} else if (title == '设计院题库' || title == '项目部题库') {
							delete_url = 'EduTrainAction!deleteQuestion';
						} else if (tableID == 128 || tableID == 293) {
							delete_url = 'EduTrainAction!deleteTestrecord';
						} else if (title == '考试记录') {
							delete_url = 'EduTrainAction!deleteTestpaper';
						}
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
		
		// 考试入口
		var btnTestEntrance = Ext.create('Ext.Button', {
			width: 100,
			height: 32,
			text: '考试入口',
			icon: "Images/ims/toolbar/process.png",
			handler: function() {
				window.open('TestEntrance');
			}
		})
		
		
		
		//liuchi 增加上传按钮
        //获取上传文件名
       	var getUploadFileName = function(){
       		var name = null;
            fileUploadPanel.store.each(function(record){
            if(name == null)
            	name = record.get('name') + "*"
            else
                name += record.get('name') + "*";
            })
            return name;
       	}
       	
       	
       	var FileUploadName_store = new Ext.data.JsonStore({
	            proxy: {
	                type: 'ajax',
	                url: 'GoalDutyAction!getFileUploadNameList?title=' + title + "&projectName=" + projectName
	            },
	            reader: {
	                type: 'json'
	            },
	            fields: ['FileName',
	            		'Year',
	            		'Month',
	            		'ProjectName'
	            ],
	            autoLoad: true
	        });
        
         
        var fileUploadH = function() {
        	fileUploadPanel = Ext.create('Ext.ux.uploadPanel.UploadPanel', {
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
        		winId: 'fileUpload', 
        		title: '导入文件', 
        		items: [fileUploadPanel],
        		buttonAlign: 'center',
        		buttons: [{        	
	            	text: '确定',
	            	handler: function(){
	            		if (fileUploadPanel.store.getCount() == 0) {
	            			Ext.Msg.alert('提示', '请先上传文件');
	            			return;
	            		}	            			
	            		var fileName = fileUploadPanel.store.getCount();
	            		
	            		//alert(fileName);
	            		
	            		
	            		
	            		var fileUploadName = getUploadFileName();
	            		//url += "&fileName=" + fileUploadName;
                		fileUploadPanel.store.removeAll();
	            		
	            		var win = this;
	            		$.getJSON('GoalDutyAction!addFileUpload', { fileName: fileUploadName, title : title , projectName : projectName},
	            				function(data) {
	            					if (data.success == 'true')
	            						Ext.Msg.alert('提示', '成功导入');
	            					else
	            						Ext.Msg.alert('提示', '导入失败');
	            					FileUploadName_store.reload();
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
                    	if (fileUploadPanel.store.getCount() != 0) {
                    		var fileName = fileUploadPanel.store.getAt(0).get('name');
                       		$.getJSON("GoalDutyAction!deleteAllFile", { fileName: fileName } );
                       		fileUploadPanel.store.removeAll();
                    	}
                    	bbar.moveFirst();
                    }
                }
            });
        }
        
        
        
        
        //附件浏览的下拉菜单
        var scanFileUploadMenu = new Ext.menu.Menu({
			shadow: "drop",
			allowOtherMenus: true,
			items: [
				new Ext.menu.Item({
					text: '暂无文件'
				})
			]
		})
		
		
         var deleteFileUploadMenu = new Ext.menu.Menu({
			shadow: "drop",
			allowOtherMenus: true,
			items: [
				new Ext.menu.Item({
					text: '暂无文件'
				})
			]
		})
	        
	        
	        
        var scanFileUploadH = function (item){
        	//var selRecs = gridDT.getSelectionModel().getSelection();
        	
	        
        	var fileArr = new Array();
        	
        	for(var i=0;i<FileUploadName_store.getCount();i++) {
        		fileArr.push(FileUploadName_store.data.items[i].data['FileName']); 
        	}
        	
			/*var accessory = selRecs[0].data.Accessory;
			var array = accessory.split("*");
			var foldname = array[0]+"\\"+array[1];*/
			scanFileUploadMenu.removeAll();
			
			//若从数据库中取出为空，则表示没有附件
			if( fileArr.length==0 )
			{
				var menuItem = Ext.create('Ext.menu.Item', {
                	text: '暂无文件'                	
                });
                scanFileUploadMenu.add(menuItem);
			}//数据库中有附件，添加到文件下拉菜单中
			else{
				for( var i = 0; i < fileArr.length; i++){
					var icon = "";
					if(fileArr[i].indexOf('.doc')>0)
						icon = "Images/ims/toolbar/report_word.png";
					else if(fileArr[i].indexOf('.xls')>0||fileArr[i].indexOf('.xlsx')>0)
						icon = "Images/ims/toolbar/report_excel.png";
					else if(fileArr[i].indexOf('.png')>0||fileArr[i].indexOf('.jpg')>0||fileArr[i].indexOf('.bmp')>0)
						icon = "Images/ims/toolbar/report_picture.png";
					else
						icon = "Images/ims/toolbar/report_other.png";
					var menuItem = Ext.create('Ext.menu.Item', {
                		text: fileArr[i],
                		icon: icon,
                		listeners:
						{
							'click': function (item, e, eOpts) {
                        		var fileName = item.text;    
                        		if(fileName.indexOf('.doc')>0||fileName.indexOf('.docx')>0)
                				{               			               				
	                        		if(e.getX()-item.getX()>20)
	                        		{
	                        			window.open("upload\\" + "fileUpload" + "\\" + title + "\\" + fileName);
	                        		}
	                        		else
	                        		{
	                        			if(fileName.indexOf('.docx')>0)
	                        				window.open("upload\\"+ "fileUpload" + "\\" + title + "\\" + fileName.substr(0,fileName.length-5)+".pdf");                    			
	                        			else	                      
	                        				window.open("upload\\"+ "fileUpload" + "\\" + title + "\\" + fileName.substr(0,fileName.length-4)+".pdf");
	                        		}
	                			}
                        		else
                        		{
                        			window.open("upload\\"+ "fileUpload" + "\\" + title + "\\" + fileName);
                        		}
                      		}
					 	}
                	});
             		scanFileUploadMenu.add(menuItem);  
				}
		 	}
        }
        
        var scanFileUploadSumH = function (item){
        	//var selRecs = gridDT.getSelectionModel().getSelection();
        	
	        
        	var fileArr = new Array();
        	var newtitle;
        	var storeSum;
        	
        	if(title == '年度安全教育培训汇总') {
				newtitle = '年度安全教育培训';
				storeSum = store_Trainplan1Sum;
        	}
			else if(title == '三项业务宣传培训汇总') {
				newtitle = '三项业务宣传培训';
				storeSum = store_Trainplan2Sum;
			}
        	
        	for(var i=0;i<storeSum.getCount();i++) {
        		fileArr.push(storeSum.data.items[i].data['FileName']); 
        	}
        	
			/*var accessory = selRecs[0].data.Accessory;
			var array = accessory.split("*");
			var foldname = array[0]+"\\"+array[1];*/
			scanFileUploadMenu.removeAll();
			
			var newtitle;
			
			
			
			//若从数据库中取出为空，则表示没有附件
			if( fileArr.length==0 )
			{
				var menuItem = Ext.create('Ext.menu.Item', {
                	text: '暂无文件'                	
                });
                scanFileUploadMenu.add(menuItem);
			}//数据库中有附件，添加到文件下拉菜单中
			else{
				for( var i = 0; i < fileArr.length; i++){
					var icon = "";
					if(fileArr[i].indexOf('.doc')>0)
						icon = "Images/ims/toolbar/report_word.png";
					else if(fileArr[i].indexOf('.xls')>0||fileArr[i].indexOf('.xlsx')>0)
						icon = "Images/ims/toolbar/report_excel.png";
					else if(fileArr[i].indexOf('.png')>0||fileArr[i].indexOf('.jpg')>0||fileArr[i].indexOf('.bmp')>0)
						icon = "Images/ims/toolbar/report_picture.png";
					else
						icon = "Images/ims/toolbar/report_other.png";
					var menuItem = Ext.create('Ext.menu.Item', {
                		text: fileArr[i],
                		icon: icon,
                		listeners:
						{
							'click': function (item, e, eOpts) {
                        		var fileName = item.text;    
                        		if(fileName.indexOf('.doc')>0||fileName.indexOf('.docx')>0)
                				{               			               				
	                        		if(e.getX()-item.getX()>20)
	                        		{
	                        			window.open("upload\\" + "fileUpload" + "\\" + newtitle + "\\" + fileName);
	                        		}
	                        		else
	                        		{
	                        			if(fileName.indexOf('.docx')>0)
	                        				window.open("upload\\"+ "fileUpload" + "\\" + newtitle + "\\" + fileName.substr(0,fileName.length-5)+".pdf");                    			
	                        			else	                      
	                        				window.open("upload\\"+ "fileUpload" + "\\" + newtitle + "\\" + fileName.substr(0,fileName.length-4)+".pdf");
	                        		}
	                			}
                        		else
                        		{
                        			window.open("upload\\"+ "fileUpload" + "\\" + newtitle + "\\" + fileName);
                        		}
                      		}
					 	}
                	});
             		scanFileUploadMenu.add(menuItem);  
				}
		 	}
        }
        
        var deleteFileUploadH = function (item){
        	//var selRecs = gridDT.getSelectionModel().getSelection();
        	
	        
        	var fileArr = new Array();
        	
        	for(var i=0;i<FileUploadName_store.getCount();i++) {
        		fileArr.push(FileUploadName_store.data.items[i].data['FileName']); 
        	}
        	
			/*var accessory = selRecs[0].data.Accessory;
			var array = accessory.split("*");
			var foldname = array[0]+"\\"+array[1];*/
			deleteFileUploadMenu.removeAll();
			
			//若从数据库中取出为空，则表示没有附件
			if( fileArr.length==0 )
			{
				var menuItem = Ext.create('Ext.menu.Item', {
                	text: '暂无文件'                	
                });
                deleteFileUploadMenu.add(menuItem);
			}//数据库中有附件，添加到文件下拉菜单中
			else{
				for( var i = 0; i < fileArr.length; i++){
					var icon = "";
					if(fileArr[i].indexOf('.doc')>0)
						icon = "Images/ims/toolbar/report_word.png";
					else if(fileArr[i].indexOf('.xls')>0||fileArr[i].indexOf('.xlsx')>0)
						icon = "Images/ims/toolbar/report_excel.png";
					else if(fileArr[i].indexOf('.png')>0||fileArr[i].indexOf('.jpg')>0||fileArr[i].indexOf('.bmp')>0)
						icon = "Images/ims/toolbar/report_picture.png";
					else
						icon = "Images/ims/toolbar/report_other.png";
					var menuItem = Ext.create('Ext.menu.Item', {
                		text: fileArr[i],
                		icon: icon,
                		listeners:
						{
							'click': function (item, e, eOpts) {
                        		var fileName = item.text;    
                        		
                        		Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) {
				    				if (buttonID === 'yes') 
				    				{
				    					//alert(item.text);
				    					$.getJSON(encodeURI("GoalDutyAction!deleteFileUpload?file=" + item.text + "&title=" + title+ "&projectName=" + projectName),
				    							//{id: keyIDs.toString()},	//Ajax参数
				                                function (res) 
				                                {
				    								if (res.success) 
				    								{
				                                        //重新加载store
				    									FileUploadName_store.reload();
				    									Ext.Msg.alert('提示','删除成功');
				                                    }
				                                    else 
				                                    {
				                                    	Ext.Msg.alert("信息", res.msg);
				                                    }
				                                }
				                         );
    								}
                				});
							}
						}
					});
             		deleteFileUploadMenu.add(menuItem);  
				}
		 	}
        }
        
        
      
        
        
        var btnFileUpload = Ext.create('Ext.Button', {
        	width: 90,
        	height: 32,
        	text: '上传文件',
            icon: "Images/ims/toolbar/extexcel.png",
            handler: fileUploadH
        })
        
        var btnscanFileUpload = Ext.create('Ext.Button', {
        	width: 90,
        	height: 32,
        	text: '文件下载',
            icon: "Images/ims/toolbar/view.png",
           	//disabled: true,
            menu: scanFileUploadMenu,
            handler: scanFileUploadH
        })
        
        var btnscanFileUploadSum = Ext.create('Ext.Button', {
        	width: 120,
        	height: 32,
        	text: '文件下载',
            icon: "Images/ims/toolbar/view.png",
           	//disabled: true,
            menu: scanFileUploadMenu,
            handler: scanFileUploadSumH
        })
        
        var btndeleteFileUpload = Ext.create('Ext.Button', {
        	width: 90,
        	height: 32,
        	text: '文件删除',
            icon: "Images/ims/toolbar/view.png",
           	//disabled: true,
            menu: deleteFileUploadMenu,
            handler: deleteFileUploadH
        })
          //liuchi
		
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
					dataStore.on('beforeload', function (store, options) {
						Ext.apply(dataStore.proxy.extraParams, { projectName: combo.getValue() }); 
					});
					bbar.moveFirst();
				}
			}
		});
		
		
		
		// 考试名称的多选框
		var comboTest = Ext.create('Ext.form.ComboBox', {
			store: Ext.create('Ext.data.Store', {
				fields: [
					 { name: 'ID'},
					 { name: 'Name'}
				],
				proxy: {
					type: 'ajax',
					url: encodeURI('EduTrainAction!getDoubleTestrecordListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName),
					reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
						type: 'json', //返回数据类型为json格式
						root: 'rows',  //数据
						totalProperty: 'total' //数据总条数
					}
				}
			}),
			displayField: 'Name',
			valueField: "ID",
			editable: false,
			autoSelect: true,
			listeners: {   
				render: function(t, eOpts) {
					t.getStore().on("load", function(s, r, o) {
						t.setValue(r[0].get('ID'));
					});
					t.getStore().load();
				},
				change: function(combo, records, eOpts) {
					dataStore.on('beforeload', function (store, options) {
						Ext.apply(dataStore.proxy.extraParams, { testID: combo.getValue() }); 
					});
					bbar.moveFirst();
				}
			}
		});
		//建立工具栏
		var tbar = new Ext.Toolbar({
			defaults: { scale: 'medium' }
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
		
		var removeBtn = function() {
			tbar.remove(btnAdd);
			tbar.remove(btnEdit);
			tbar.remove(btnDel);
		}
		
		showPreviewPaper = function(ID) {
			var height = 660;
			var width = 800;
			var html_str = "<iframe src=\"/PSM/EduTrainAction!testPeview?testID="+ID+"\" style=\"width:100%;height:100%;\">";
			Ext.create('Ext.window.Window', {
				title: '查看详情',
				titleAlign: 'center',
				height: height,
				width: width,
				closeAction: 'destroy',
				layout: 'fit',
				autoScroll: true,
				maximizable: true,
				html: html_str
			}).show();
		};
		
		showRreviewPaper = function(ID, name) {
			height = 660;
			width = 800;
			html_str = "<iframe src=\"/PSM/EduTrainAction!testReview?testpaperID="+ID+"&name="+name+"\" style=\"width:100%;height:100%;\">";
			Ext.create('Ext.window.Window', {
				title: '查看详情',
				titleAlign: 'center',
				height: height,
				width: width,
				closeAction: 'destroy',
				layout: 'fit',
				autoScroll: true,
				maximizable: true,
				html: html_str
			}).show();
		};
		
		//----------------5-1-1任务分配-----------------//
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
        	width: 90,
        	height: 32,
        	text: '任务分配',
            icon: "Images/ims/toolbar/view.png",
           	disabled: true,
            handler: Allotask
        })
		//------------5-1-1任务分配--------------//				
		if (title == '年度安全教育培训') {
			dataStore = store_Trainplan1;
			dataStore.load();
			queryURL = 'EduTrainAction!getTrainplan1ListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID + "&projectName=" + projectName;
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '教育培训主题', dataIndex: 'Content', align: 'center', width: 250},
					{ text: '培训对象', dataIndex: 'Employee', align: 'center', width: 100},
					{ text: '培训方式', dataIndex: 'Method', align: 'center', width: 100},
					{ text: '计划时间', dataIndex: 'ClassDate', align: 'center', width: 200},
					{ text: '计划课时', dataIndex: 'ClassTime', align: 'center', width: 100},
					{ text: '经费预算', dataIndex: 'Budget', align: 'center', width: 100},
					{ text: '落实情况', dataIndex: 'Result', align: 'center', width: 235},
					{ text: '所属项目', align: 'center', width: 200, renderer: function (value, meta, record) {
							return	projectName;
						}
					}
				]
			addBtn();
			tbar.add(btnAllotask);
			tbar.add(btnFileUpload);
        		tbar.add(btnscanFileUpload);
        		tbar.add(btndeleteFileUpload);
        		tbar.add({
        			xtype: 'combo',
        			fieldLabel: '年份选择',
        			store:['2017','2018','2019'],
        			editable: false,
        			width: 200,
        			labelWidth: 100,
        			labelAlign: 'right',
                    //format: 'Y-m',
        			listeners: {
        				'select' : function (combo, records, eOpts) {
							        
							        var year = combo.getValue().toString();
							        //alert(year);
							        //year = year.toString();
							        FileUploadName_store.clearFilter();
							        FileUploadName_store.filter('Year',year);
							        //FileUploadName_store.filter('Month',month);
							        FileUploadName_store.load();
							        
						}
        			}
        		});
        		
        		
        		
        		
        		
        		
		} else if (title == '三项业务宣传培训') {
			dataStore = store_Trainplan2;
			dataStore.load();
			queryURL = 'EduTrainAction!getTrainplan2ListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID + "&projectName=" + projectName;
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '宣传培训主题', dataIndex: 'Content', align: 'center', width: 250},
					{ text: '宣传培训形式', dataIndex: 'Method', align: 'center', width: 100},
					{ text: '活动时间', dataIndex: 'ActDate', align: 'center', width: 200},
					{ text: '登记时间', dataIndex: 'RegistDate', align: 'center', width: 100},
					{ text: '经费记录', dataIndex: 'Funding', align: 'center', width: 100},
					{ text: '所属项目', align: 'center', width: 200, renderer: function (value, meta, record) {
							return	projectName;
						}
					}
				]
			addBtn();
			tbar.add(btnFileUpload);
        		tbar.add(btnscanFileUpload);
        		tbar.add(btndeleteFileUpload);
        		tbar.add({
        			xtype: 'combo',
        			fieldLabel: '年份选择',
        			store:['2017','2018','2019'],
        			editable: false,
        			width: 250,
        			labelWidth: 150,
        			labelAlign: 'right',
                    //format: 'Y-m',
        			listeners: {
        				'select' : function (combo, records, eOpts) {
							        
							        var year = combo.getValue().toString();
							        //alert(year);
							        //year = year.toString();
							        FileUploadName_store.clearFilter();
							        FileUploadName_store.filter('Year',year);
							        //FileUploadName_store.filter('Month',month);
							        FileUploadName_store.load();
							        
						}
        			}
        		});
		} else if (title == '年度安全教育培训汇总') {
			dataStore = store_Trainplan1Sum;	
			dataStore.load();
			queryURL = 'GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '年度安全教育培训' + "&projectName=" + '';
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 400 },
					{ text: '年份', dataIndex: 'Year', align: 'center', width: 200 },
					{ text: '安全教育培训计划', dataIndex: 'FileName', align: 'center', width: 400 }
				]
			addBtn();
			removeBtn();
			//tbar.add(btnAllotask);
			//tbar.add(btnFileUpload);
        		tbar.add(btnscanFileUploadSum);
        		
		} else if (title == '三项业务宣传培训汇总') {
			dataStore = store_Trainplan2Sum;
			dataStore.load();
			queryURL = 'GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '三项业务宣传培训' + "&projectName=" + '';
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 400 },
					{ text: '年份', dataIndex: 'Year', align: 'center', width: 200 },
					{ text: '三项业务宣传培训计划', dataIndex: 'FileName', align: 'center', width: 400 }
				]
			addBtn();
			removeBtn();
			//tbar.add(btnAllotask);
			//tbar.add(btnFileUpload);
        		tbar.add(btnscanFileUploadSum);
		} else if (tableID == 116 || (tableID >= 118 && tableID <= 122) || tableID == 213) {	
			dataStore = store_Traintable;
			dataStore.load();
			queryURL = 'EduTrainAction!getTraintableListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID + "&projectName=" + projectName;
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '教育培训主题', dataIndex: 'Content', align: 'center', width: 250},
					{ text: '培训对象', dataIndex: 'Employee', align: 'center', width: 120},
					{ text: '培训方式', dataIndex: 'Method', align: 'center', width: 120},
					{ text: '培训时间', dataIndex: 'TrainDate', align: 'center', width: 120},
					{ text: '登记时间', dataIndex: 'RegistDate', align: 'center', width: 100},
					{ text: '经费记录', dataIndex: 'Funding', align: 'center', width: 100},
					{ text: '所属项目', align: 'center', width: 200, renderer: function (value, meta, record) {
							return	projectName;
						}
					}
				]
			addBtn();
		} else if (title == '项目负责人' || title == '安全生产管理人员') {	
			dataStore = store_Persondb;
			queryURL = 'BasicInfoAction!getPersondbListSearch?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName;
			var role = '';
			if (title == '项目负责人') role = '项目经理';
			else role = '项目安全总监';
			dataStore.on('beforeload', function (store, options) {
				Ext.apply(dataStore.proxy.extraParams, { findStr: role }); 
			});
			dataStore.load();
			column = [
				{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
				{ text: 'ID', dataIndex: 'ID', align: 'center', width: 200,hidden:true},
				{ text: '人员类型', dataIndex: 'PType', align: 'center', width: 100},
				{ text: '姓名', dataIndex: 'Name', align: 'center', width: 100},
				{ text: '性别', dataIndex: 'Sex', align: 'center', width: 100},
				{ text: '身份证号', dataIndex: 'IDCard', align: 'center', width: 200},
				{ text: '出生年月', dataIndex: 'Birthday', align: 'center', width: 150},
				{ text: '联系电话', dataIndex: 'Phone', align: 'center', width: 150},
				{ text: '紧急联系电话', dataIndex: 'PhoneUrgent', align: 'center', width: 150},
				{ text: '持证类型1', dataIndex: 'PapersType', align: 'center', width: 200},
				{ text: '证件编号1', dataIndex: 'PapersNo', align: 'center', width: 150},
				{ text: '有效期至1', dataIndex: 'PapersDate', align: 'center', width: 150},
				{ text: '持证类型2', dataIndex: 'PapersTypeTwo', align: 'center', width: 200},
				{ text: '证件编号2', dataIndex: 'PapersNoTwo', align: 'center', width: 150},
				{ text: '有效期至2', dataIndex: 'PapersDateTwo', align: 'center', width: 150}
			]
			tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add(btnSearchR);
			tbar.add(btnScan);
		} else if (title == '分包方人员日常教育培训') {	
			dataStore = store_fbdailytrain;
			queryURL = 'EduTrainAction!getFbdailytrainListSearch?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName;
			dataStore.load();
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: 'ID', dataIndex: 'ID', align: 'center', width: 200,hidden:true},
					{ text: '报备分包方', dataIndex: 'Fenbao', align: 'center', width: 100},
					{ text: '培训开始日期', dataIndex: 'StartDate', align: 'center', width: 100},
					{ text: '培训结束日期', dataIndex: 'EndDate', align: 'center', width: 100},
					{ text: '培训次数', dataIndex: 'Time', align: 'center', width: 200},
					{ text: '报备时间', dataIndex: 'RegistDate', align: 'center', width: 150},
					{ text: '所属项目', align: 'center', width: 200, renderer: function (value, meta, record) {
							return	projectName;
						}
					}
				]
			addBtn();
		} else if (title == '分包方安全生产班前会') {
			dataStore = store_fbactivity;
			queryURL = 'EduTrainAction!getFbactivityListSearch?userName=' + user.name + "&userRole=" + user.role + "&Theme=" + title + "&projectName=" + projectName;
			dataStore.getProxy().url +=  "&Theme=" + title;
			dataStore.load();
			column = [
				{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
				{ text: 'ID', dataIndex: 'ID', align: 'center', width: 200,hidden:true},
				{ text: '日期', dataIndex: 'ActivityDate', align: 'center', width: 200},
				{ text: '分包单位名称', dataIndex: 'Organization', align: 'center', width: 300},
				{ text: '班前会现场预览', align: 'center', width: 200, renderer: function (value, meta, record) {
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
				{ text: '所属项目', align: 'center', width: 200, renderer: function (value, meta, record) {
						return	projectName;
					}
				}
			 ]
			addBtn();
		} else if (title == '分包方安全生产周活动') {
			dataStore = store_fbactivity;
			queryURL = 'EduTrainAction!getFbactivityListSearch?userName=' + user.name + "&userRole=" + user.role + user.role + "&projectName=" + projectName;
			dataStore.load();
			column = [
				{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
				{ text: 'ID', dataIndex: 'ID', align: 'center', width: 200,hidden:true},
				{ text: '日期', dataIndex: 'ActivityDate', align: 'center', width: 200},
				{ text: '分包单位名称', dataIndex: 'Organization', align: 'center', width: 300},
				{ text: '主题', dataIndex: 'Theme', align: 'center', width: 300},
				{ text: '周活动现场预览', align: 'center', width: 200, renderer: function (value, meta, record) {
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
				{ text: '所属项目', align: 'center', width: 200, renderer: function (value, meta, record) {
						return	projectName;
					}
				}
			 ]
			addBtn();
		} else if (title == '设计院题库' || title == '项目部题库') {	
			dataStore = store_question;
			if (title == '设计院题库')
				dataStore.getProxy().url += ("&belongTo=" + title);
			else 
				dataStore.getProxy().url += ("&belongTo=" + projectName);
			queryURL = 'EduTrainAction!getQuestionListSearch?userName=' + user.name + '&userRole=' + user.role + "&belongTo=设计院";
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40, sortable: false},	
					{ text: '问题', itemId: 'grid_Question', dataIndex: 'Question', align: 'left', width: 300},
					{ text: '选项A', itemId: 'grid_OptionA', dataIndex: 'OptionA', align: 'left', width: 150},
					{ text: '选项B', itemId: 'grid_OptionB', dataIndex: 'OptionB', align: 'left', width: 150},
					{ text: '选项C', itemId: 'grid_OptionC', dataIndex: 'OptionC', align: 'left', width: 150},
					{ text: '选项D', itemId: 'grid_OptionD', dataIndex: 'OptionD', align: 'left', width: 150},
					{ text: '选项E', itemId: 'grid_OptionE', dataIndex: 'OptionE', align: 'left', width: 150, hidden: true},
					{ text: '答案', itemId: 'grid_Answer', dataIndex: 'Answer', align: 'left', width: 75}
				]
			tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add(btnSearchR);
			tbar.add("-");
			tbar.add(btnAdd);
			tbar.add(btnEdit);
			tbar.add(btnDel);
			tbar.add("-");
			tbar.add({
				xtype: 'radiogroup',
				itemId: 'grid_type',
				width: 270,
				items: [{
					xtype: 'radio',
					boxLabel: '单选题',
					name: 'questionType',
					itemId: 'radioQuestion1',
					inputValue: 'radioQuestion1',
					checked: true
				},{
					xtype: 'radio',
					boxLabel: '多选题',
					name: 'questionType',
					itemId: 'radioQuestion2',
					inputValue: 'radioQuestion2'
				},{
					xtype: 'radio',
					boxLabel: '判断题',
					name: 'questionType',
					itemId: 'radioQuestion3',
					inputValue: 'radioQuestion3'
				},{
					xtype: 'radio',
					boxLabel: '简答题',
					name: 'questionType',
					itemId: 'radioQuestion4',
					inputValue: 'radioQuestion4'
				}],
				listeners: {
					'change' : function(obj) {
						var questionType = obj.lastValue['questionType'];
						QState = questionType;
						if (questionType == 'radioQuestion1' || questionType == 'radioQuestion2') {
							gridDT.down('#grid_OptionA').show();
							gridDT.down('#grid_OptionB').show();
							gridDT.down('#grid_OptionC').show();
							gridDT.down('#grid_OptionD').show();
							if (questionType == 'radioQuestion1')
								gridDT.down('#grid_OptionE').hide();
							else 
								gridDT.down('#grid_OptionE').show();
							gridDT.down('#grid_Question').setWidth(300);
							gridDT.down('#grid_Answer').setWidth(75);
							if (questionType == 'radioQuestion1')
								dataStore.on('beforeload', function (store, options) {
									Ext.apply(dataStore.proxy.extraParams, { type: '单选题' }); 
								});
							else if (questionType == 'radioQuestion2')
								dataStore.on('beforeload', function (store, options) {
									Ext.apply(dataStore.proxy.extraParams, { type: '多选题' }); 
								});
						} else if (questionType == 'radioQuestion3') {
							gridDT.down('#grid_OptionA').hide();
							gridDT.down('#grid_OptionB').hide();
							gridDT.down('#grid_OptionC').hide();
							gridDT.down('#grid_OptionD').hide();
							gridDT.down('#grid_OptionE').hide();
							gridDT.down('#grid_Question').setWidth(600);
							gridDT.down('#grid_Answer').setWidth(75);
							dataStore.on('beforeload', function (store, options) {
								Ext.apply(dataStore.proxy.extraParams, { type: '判断题' }); 
							});
						} else if (questionType == 'radioQuestion4') {
							gridDT.down('#grid_OptionA').hide();
							gridDT.down('#grid_OptionB').hide();
							gridDT.down('#grid_OptionC').hide();
							gridDT.down('#grid_OptionD').hide();
							gridDT.down('#grid_OptionE').hide();
							gridDT.down('#grid_Question').setWidth(600);
							gridDT.down('#grid_Answer').setWidth(400);
							dataStore.on('beforeload', function (store, options) {
								Ext.apply(dataStore.proxy.extraParams, { type: '简答题' }); 
							})
						}
						dataStore.load();
					}
				}
			});
			dataStore.on('beforeload', function (store, options) {
				Ext.apply(dataStore.proxy.extraParams, { type: '单选题' }); 
			});
			if(user.role === '项目部人员' && title == '设计院题库') {
    			removeBtn();
    		}
			dataStore.load();
		} else if (tableID == 128 || tableID == 293) {	// 考试信息（设计院和项目部）
			dataStore = store_testrecord;
			dataStore.load();
			queryURL = 'EduTrainAction!getTestrecordListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID;
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '考试名称', dataIndex: 'Name', align: 'center', width: 250},
					{ text: '所属项目部', dataIndex: 'BelongTo', align: 'center', width: 250},
					{ text: '实考人数', dataIndex: 'ActualNum', align: 'center', width: 150},
					{ text: '平均分', dataIndex: 'AvgScore', align: 'center', width: 150, renderer: function(value, cellmeta, record, rowIndex, columnIndex, store){
							var AvgScore = record.get('AvgScore');
							if (AvgScore == -1) 
								return "无人考试";
							return AvgScore;
						}
					},
					{ text: '查看试卷', align: 'center', width: 125, renderer: function (value, meta, record) {
							var testID = record.get('ID');
							return	'<a><img src="Images/ims/toolbar/help.png" onclick="showPreviewPaper('+testID+')"></a>';
						}
					},
					{ text: '考试入口', align: 'center', width: 125, renderer: function (value, meta, record) {
							var testID = record.get('ID');
							var name = user.name;
							var idno = user.identity;
							var projectID = -1;
							$.ajax({
						        type: "GET",
						        url: "EduTrainAction!getProjectListDef?start=0&limit=1000",
						        dataType: "json",
						        async: false,
						        success: function(data) {
						        	var total = data.total;
						        	for (var i = 0; i < total; i++) {
						        		if (data.rows[i].Name === projectName) {
						        			projectID = data.rows[i].ID;
						        			break;
						        		}
						        	}
						        		
						        }
						    });
							return	`<a href="/PSM/TestEntrance?testID=${testID}&projectID=${projectID}&name=${name}&idno=${idno}" target="_blank"><img src="Images/ims/toolbar/process.png"></a>`;
						}
					}
				]
			tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add(btnSearchR);
			tbar.add("-");
			tbar.add(btnAdd);
			tbar.add(btnDel);
			tbar.add("-");
			tbar.add(btnTestEntrance);
			// 项目部人员不可编辑设计院考试信息
			if (tableID == 293 && user.role == '项目部人员') {
				tbar.remove(btnAdd);
				tbar.remove(btnDel);
			}
		} else if (tableID == 253) {	//考试记录
			dataStore = store_testpaper;
			queryURL = 'EduTrainAction!getTestpaperListSearch?userName=' + user.name + '&userRole=' + user.role + '&tableID=' + tableID + "&projectName=" + projectName;
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '姓名', dataIndex: 'Name', align: 'center', width: 80},
					{ text: '项目部', dataIndex: 'Project', align: 'center', width: 250},
					{ text: '身份证', dataIndex: 'IDNO', align: 'center', width: 180},
					{ text: '单选题', dataIndex: 'Score1', align: 'center', width: 60},
					{ text: '多选题', dataIndex: 'Score2', align: 'center', width: 60},
					{ text: '判断题', dataIndex: 'Score3', align: 'center', width: 60},
					{ text: '简答题', dataIndex: 'Score4', align: 'center', width: 60},
					{ text: '总分', dataIndex: 'Score', align: 'center', width: 60},
					{ text: '考试时间', dataIndex: 'TestDate', align: 'center', width: 150},
					{ text: '查看历史试卷', align: 'center', width: 60, renderer: function (value, meta, record) {
							var testpaperID = record.get('ID');
							var name = record.get('Name');
							return	'<a><img src="Images/ims/toolbar/help.png" onclick="showRreviewPaper('+testpaperID+',\''+name+'\')"></a>';
						}
					}
				];
			if (user.role == '全部项目') {
				tbar.add(comboProject);				
			} else {
				Ext.apply(dataStore.proxy.extraParams, { projectName: projectName }); 
			}
			tbar.add(comboTest);
			tbar.add(textSearch);
			tbar.add(btnSearch);
			tbar.add(btnSearchR);
			if(user.role !== '项目部人员') {
    			tbar.add(btnDel);
    		}
		} else if (title == '领导讲话' || title == '制度宣传' || title == '安全技术' || title == '其他专题学习' ) {	
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
			tbar.add(btnSearchR);
			tbar.add("-");
			tbar.add(btnAdd);
			tbar.add(btnEdit);
			tbar.add(btnDel);
			if(user.role === '项目部人员') {
    			removeBtn();
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
								},
								failure: function(form, action){
									Ext.Msg.alert('警告',action.result.msg);
								}
							})
							this.up('window').close();  	
						}
						else{//forms.form.isValid() == false
							Ext.Msg.alert('警告','请完善信息并确认输入格式！');      	                	
						}
					}
				},{
					text: '重置',
					handler: function(){         	
						if (tableID != 127) {
							DeleteFile(config.action);
							uploadPanel.store.removeAll();
							if (title != '设计院题库' && title != '项目部题库' && tableID != 128 && title != '项目负责人' && title != '安全生产管理人员')
								insertFileToList();
						}
						forms.form.reset();
						if (config.action == "editProject") {
							forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
						}
					}
				}],
				renderTo: Ext.getBody()
			});// forms定义结束
			
			if (config.action == 'editProject') {
				forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据
				if (title == '项目部题库' || title == '设计院题库') {
					var answer = selRecs[0].data.Answer;
					if (QState == 'radioQuestion1') {
						if (answer == 'A')
							forms.getComponent('form_answer1').items.get(0).setValue(true);
						else if (answer == 'B')
							forms.getComponent('form_answer1').items.get(1).setValue(true);
						else if (answer == 'C')
							forms.getComponent('form_answer1').items.get(2).setValue(true);
						else if (answer == 'D')
							forms.getComponent('form_answer1').items.get(3).setValue(true);
					} else if (QState == 'radioQuestion2') {
						for (var i = 0; i < 5; i++)
							forms.getComponent('form_answer2').items.get(i).setValue(false);
						if (answer.indexOf('A') != -1)
							forms.getComponent('form_answer2').items.get(0).setValue(true);
						if (answer.indexOf('B') != -1)
							forms.getComponent('form_answer2').items.get(1).setValue(true);
						if (answer.indexOf('C') != -1)
							forms.getComponent('form_answer2').items.get(2).setValue(true);
						if (answer.indexOf('D') != -1)
							forms.getComponent('form_answer2').items.get(3).setValue(true);
						if (answer.indexOf('E') != -1)
							forms.getComponent('form_answer2').items.get(4).setValue(true);
					} else if (QState == 'radioQuestion3') {
						if (answer == 'T')
							forms.getComponent('form_answer3').items.get(0).setValue(true);
						else if (answer == 'F')
							forms.getComponent('form_answer3').items.get(1).setValue(true);
					} else if (QState == 'radioQuestion4') {
						forms.getComponent('form_answer4').items.get(0).setValue(answer);
					}
							
					
				}
			}
		};
		
		//创建装载formPanel的窗体，由工具栏按钮点击显示
		var showWin = function (config) {
			var width = 800;
			var height = 500;
			// 添加考卷的窗口大小
			if (tableID == 128 || tableID == 293) {
				if (config.winId == 'addProject' || config.winId == 'editProject') {
					width = 500;
					height = 500;					
				} else width = 700;
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
				loadMask: {						//IE8不兼容loadMask
					msg: '正在加载数据中……'
				},
				getRowClass: function(record, rowIndex, rowParams, store) {
					if (record.get('Score') < 60)  return 'x-grid-record-red';
				}
			},
			listeners: {
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
				'celldblclick': function(self, td, cellIndex, record, tr, rowIndex){
					var html_str = "";
					var record = dataStore.getAt(rowIndex);
					var Num = rowIndex + 1;
					var height = 350;
					var width = 700;
					if (tableID == 128) {
						height = 660;
						width = 800;
						createForm({
							autoScroll: true,
							action: 'projectView',
							bodyPadding: 5,
							items: grid_Testrecord
						});	
						var testID = record.get('ID');
						store_testpaper.load({ params: {projectName: projectName, testID: testID} })
						showWin({ winId: 'projectView', title: '项目查看', items: [forms]});
						return;
					} else if (title == '年度安全教育培训') {
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Content') + "详细信息</center></h1>"
								+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								+ "<tr><td width=\"15%\" style=\"padding:5px;\">教育培训主题</td><td width=\"40%\">" + record.get("Content") + "</td>"
								+ "<td style=\"padding:5px;\">培训对象</td><td>" + record.get('Employee') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">培训方式</td><td>" + record.get('Method') + "</td>"
								+ "<td style=\"padding:5px;\">计划时间</td><td>" + record.get('ClassDate') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">计划课时</td><td>" + record.get('ClassTime') + "</td>"
								+ "<td style=\"padding:5px;\">经费预算</td><td>" + record.get('Budget') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">落实情况</td><td colspan=\"3\">" + record.get('Result') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">"
						var misfile = record.get('Accessory').split('*');
						var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
						for (var i = 2; i < misfile.length; i++) {
							var scanfileName = getScanfileName(misfile[i]); 
		       		       	var displayfileName = misfile[i];
		       		       	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
						}
						html_str += "</td><tr></table>";
					} else if (title == '三项业务宣传培训') {
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Content') + "详细信息</center></h1>"
								+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								+ "<tr><td width=\"15%\" style=\"padding:5px;\">教育培训主题</td><td width=\"40%\">" + record.get("Content") + "</td>"
								+ "<td style=\"padding:5px;\">宣传培训形式</td><td>" + record.get('Method') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">活动时间</td><td>" + record.get('ActDate') + "</td>"
								+ "<td style=\"padding:5px;\">登记时间</td><td>" + record.get('RegistDate') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">经费记录</td><td>" + record.get('Funding') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">"
						var misfile = record.get('Accessory').split('*');
						var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
						for (var i = 2; i < misfile.length; i++) {
							var scanfileName = getScanfileName(misfile[i]); 
		       		       	var displayfileName = misfile[i];
		       		       	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
						}
						html_str += "</td><tr></table>";
					} else if (tableID == 116 || (tableID >= 118 && tableID <= 122) || tableID == 213) {	
						html_str = "<h1 style=\"padding: 5px;\"><center>" + record.get('Content') + "详细信息</center></h1>"
								+ "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								+ "<tr><td width=\"15%\" style=\"padding:5px;\">教育培训主题</td><td width=\"40%\">" + record.get("Content") + "</td>"
								+ "<td style=\"padding:5px;\">培训对象</td><td>" + record.get('Employee') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">培训方式</td><td>" + record.get('Method') + "</td>"
								+ "<td style=\"padding:5px;\">培训时间</td><td>" + record.get('TrainDate') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">登记时间</td><td>" + record.get('RegistDate') + "</td>"
								+ "<td style=\"padding:5px;\">经费记录</td><td>" + record.get('Funding') + "</td></tr>"
								+ "<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">"
								var misfile = record.get('Accessory').split('*');
								var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
								for (var i = 2; i < misfile.length; i++) {
									var scanfileName = getScanfileName(misfile[i]); 
				       		       	var displayfileName = misfile[i];
				       		       	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
								}
								html_str += "</td><tr></table>";
					} else if (title == '项目负责人' || title == '安全生产管理人员') {	
						var record = dataStore.getAt(rowIndex);
						 var Num = rowIndex+1;
						 html_str = "<h1 style=\"padding: 5px;\"><center>"+title+"人员类型</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">人员类型</td ><td>" + record.get("PType") + "</td><td width=\"15%\" style=\"padding:5px;\">姓名</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
						 \<tr><td style=\"padding:5px;\">性别</td><td>" + record.get('Sex') + "</td><td style=\"padding:5px;\">身份证号</td><td>" + record.get('IDCard') + "</td></tr>\
						 \<tr><td style=\"padding:5px;\">出生年月</td><td>" + record.get('Birthday') + "</td><td style=\"padding:5px;\">联系电话</td><td>" + record.get('Phone') + "</td></tr>\
						 \<tr><td style=\"padding:5px;\">紧急联系电话</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PhoneUrgent') + "</td></tr>\
						\<tr><td style=\"padding:5px;\">证件编号</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PapersNo') + "</td></tr>\
						\<tr><td style=\"padding:5px;\">有效期至</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PapersDate') + "</td></tr>\
						 " +
								"\<tr><td style=\"padding:5px;\">持证类型</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PapersType') + "</td></tr>";
					html_str += "</table>";

					} else if (title == '设计院题库' || title == '项目部题库') {
						var questionType = record.get('Type');
						html_str = "<h1 style=\"padding: 5px;\"><center>题目详细信息</center></h1>"
								 + "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"
								 + "<tr><td style=\"padding:5px;\">问题  </td><td colspan=\"3\">" + record.get('Question') + "</td></tr>"
						if (questionType == '单选题' || questionType == '多选题') {
							html_str += "<tr><td style=\"padding:5px;\">选项A</td><td colspan=\"3\">" + record.get('OptionA') + "</td></tr>"
									  + "<tr><td style=\"padding:5px;\">选项B</td><td colspan=\"3\">" + record.get('OptionB') + "</td></tr>"
									  + "<tr><td style=\"padding:5px;\">选项C</td><td colspan=\"3\">" + record.get('OptionC') + "</td></tr>"
									  + "<tr><td style=\"padding:5px;\">选项D</td><td colspan=\"3\">" + record.get('OptionD') + "</td></tr>"
						}
						if (questionType == '多选题'){
							html_str += "<tr><td style=\"padding:5px;\">选项E</td><td colspan=\"3\">" + record.get('OptionE') + "</td></tr>"
						}								
						html_str += "<tr><td style=\"padding:5px;\">答案  </td><td colspan=\"3\">" + record.get('Answer') + "</td></tr>"
						html_str += "</table>";
					} else if (tableID == 253) {
						height = 660;
						width = 800;
						html_str = "<iframe src=\"/PSM/EduTrainAction!testReview?testpaperID="+record.get('ID')+"&name="+record.get('Name')+"\" style=\"width:100%;height:100%;\">";
					} else return;
					Ext.create('Ext.window.Window', {
						title: '查看详情',
						titleAlign: 'center',
						height: height,
						width: width,
						closeAction: 'destroy',
						layout: 'fit',
						autoScroll: true,
						maximizable: true,
						html: html_str
					}).show();
				}
			}
		});
		
		var viewPanel;
		var dataView;
		String.prototype.endWith=function(str){var reg=new RegExp(str + '$');return reg.test(this);}//测试ok，直接使用str.endWith("abc")方式调用即可String.prototype.endWith=function(str){var reg=new RegExp(str+"$");return reg.test(this);} 
		
		if (title == '领导讲话' || title == '制度宣传' || title == '安全技术' || title == '其他专题学习') {
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
							},{
								text: "编辑",//根目录的名字直接不让修改就行了
								icon: 'Images/ims/toolbar/edit.png',
								handler: editH
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
		} else {
			panel.add(gridDT);
		}

		container.add(panel).show();
	}
});