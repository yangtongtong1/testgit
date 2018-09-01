var tn;

Ext.define('BasicInfoGrid', {
    requires: [
        'Ext.data.Model',
        'Ext.grid.Panel',
        'Ext.ux.MonthField'
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
        var startDate = Ext.util.Format.date(new Date(), 'Ymd');
        var endDate = Ext.util.Format.date(new Date(), 'Ymd');
        var mdate = Ext.util.Format.date(new Date(), 'Ym');
        
        var getBLen = function(str) {
  			if (str == null) return 0;
  			if (typeof str != "string"){
   			str += "";
 			}
 			return str.replace(/[^\x00-\xff]/g,"01").length;
		}
        
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
        
        
        var getBirthdayFromIdCard = function(idCard) {
	  		var birthday = "";
			if(idCard != null && idCard != ""){
				if(idCard.length == 15){
					birthday = "19"+idCard.substr(6,6);
				} else if(idCard.length == 18){
					birthday = idCard.substr(6,8);
				}
		
				birthday = birthday.replace(/(.{4})(.{2})/,"$1-$2-");
			}
		
			return birthday;
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
        	if(title == '项目概况' || title == '设计分包单位' || title == '土建分包单位' || title == '安装分包单位' ||title =='扣分策略'||title=='考核结果')
        	{
        		info_url = 'BasicInfoAction!getFileInfo';
        		delete_url = 'BasicInfoAction!deleteOneFile';
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
       		if(title == "项目概况" || title == '设计分包单位' || title == '土建分包单位'  || title == '安装分包单位' || title == '调试分包单位')
       		{
       			deleteAllUrl = "BasicInfoAction!deleteAllFile";
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
  	        if(action == "addProject"||action == "editProject" || action == "addfb" || action == "editfb" )
  	        {
				if(action == "editProject" ||  action == "editfb"  )
				{               			
					ppid = selRecs[0].data.ID;			
				}		
				deleteFile(style, fileName, ppid);
				uploadPanel.store.removeAll();
  	        }
       	}
       	
        
        
        var forms_var=[{
	    	fieldLabel: "旧密码",		//编号框用于绑定数据ID，隐藏不显示
	        name: "OldSc",
	        inputType: 'password', 
	        labelAlign: 'right',
	        
	        allowBlank:false
        },{
        	fieldLabel:"新密码",
        	name:"NewSc",   
        	inputType: 'password', 
        	labelAlign: 'right',
        	allowBlank:false
	    },{
	    	fieldLabel:"确认新密码",
        	name:"DNewSc",
        	inputType: 'password', 
        	labelAlign: 'right',
        	allowBlank:false
	    }]
	    
	    var UpdatePwd = function(){
        	forms = Ext.create('Ext.form.Panel', {
    			minWidth: 200,
    			minHeight: 100,
    			bodyPadding: 5,
    			buttonAlign: 'center',
    		    layout: 'anchor',
    	        defaults: { anchor: '100%', height: 30, labelWidth: 80 },
    	        autoScroll: true,
    	        url:'BasicInfoAction!ChangePersondbPwd?userName=' + user.name + "&userRole=" + user.role+ "&userId=" + user.identity,
    	        frame: false,
    	        defaultType:'textfield',
    	        items: forms_var,
    	        buttons: [{
    	        	text: '确定',
    	        	formBind: true,
    	        	handler: function() {  	
    	        		var form = this.up('form').getForm();
      	                	form.submit({   	        			
    	  	                	success: function(form, action){
    	  	                		 Ext.Msg.alert('提示',action.result.msg);
    	  	                	},
    	  	                	failure: function(form, action){
    	  	                		 Ext.Msg.alert('警告',action.result.msg);
    	  	                	}
    	                	});
    	                	this.up('window').close();  	        	
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
    	        }]
    	   })		       	
        	Ext.create('Ext.window.Window', {
        		title: '修改密码',
                height: 230,
                width: 280,
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
                items:forms
                }).show(); 	
        }
       
        var btnUpdatePwd = Ext.create('Ext.Button', {
        	width: 120,
         	height: 32,
        	icon: "Images/ims/toolbar/save.png",
        	text: '修改密码',
            type: 'button',
            ItemId:'tbtn-dlll',   
            handler:UpdatePwd
        })

        
        var nodename_store = new Ext.data.JsonStore({
            proxy: {
                type: 'ajax',
                url: 'GoalDutyAction!getNodeNameList'
            },
            reader: {
                type: 'json'
            },
            fields: ['NodeName'],
            autoLoad: true
        });
        
        var managername_store = new Ext.data.JsonStore({
            proxy: {
                type: 'ajax',
                url: 'BasicInfoAction!getManagerNameList'
            },
            reader: {
                type: 'json'
            },
            fields: ['Manager'],
            autoLoad: true
        });
        
        var store_XMJL = new Ext.data.JsonStore({
            proxy: {
                type: 'ajax',
                url: 'BasicInfoAction!getXMJLNameList'
            },
            reader: {
                type: 'json'
            },
            fields: ['Manager'],
            autoLoad: true
        });
		
		var store_Person = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'UnitName'},
                     { name: 'Job'},
                     { name: 'IdentityNo'},
                     { name: 'PhoneNo'},
                     { name: 'Type'},
                     { name: 'UserPwd'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getPersonListDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        var store_Projectperson = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Name'},
                     { name: 'Job'},
                     { name: 'Duty'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getProjectpersonListDef?userName=' + user.name + "&userRole=" + user.role+"&projectName="+projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
		
        var store_fb = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     //{ name: 'ProNo'},
                     { name: 'ProRange'},
                     { name: 'Cost'},
                     { name: 'Head'},
                     { name: 'TechHead'},
                     { name: 'ProHead'},
                     { name: 'ProTechHead'},
                     { name: 'ProSaveHead'},
                     { name: 'Accessory'},
                     { name: 'Type'},
                     { name: 'Name'},
                     { name: 'Rank'},
                     { name: 'ProSavePeople'},
                     { name: 'Project'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getfbListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName + "&type=" + title),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        var store_fbtongji = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     //{ name: 'ProNo'},
                     { name: 'ProRange'},
                     { name: 'Cost'},
                     { name: 'Head'},
                     { name: 'TechHead'},
                     { name: 'ProHead'},
                     { name: 'ProTechHead'},
                     { name: 'ProSaveHead'},
                     { name: 'Accessory'},
                     { name: 'Type'},
                     { name: 'Name'},
                     { name: 'Rank'},
                     { name: 'ProSavePeople'},
                     { name: 'Project'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getfbListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName="  + "&type="),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
    
        
        //"XH", "UserName", "Role", "LoginTime", "LoginIp"
        var store_Log = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'XH'},
                     { name: 'UserName'},
                     { name: 'Role'},
                     { name: 'Logintime'},
                     { name: 'Loginip'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('LogAction!GetLoginLogs'),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
       //"XH", "UserName", "Department", "Role", "OptTime", "Opt"
       var store_OptLog = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'XH'},
                     { name: 'UserName'},
                     { name: 'Role'},
                     { name: 'OptTime'},
                     { name: 'Opt'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('LogAction!GetOptLogs'),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       
       var store_MessageLog = Ext.create('Ext.data.Store', {
       	fields: [
       	         { name: 'ID'},
       	      { name: 'Type'},
                    { name: 'SendTo'},
                    { name: 'SendTime'},
                    { name: 'SendPhone'},
                    { name: 'SendContent'}
           ],
           pageSize: psize,  //页容量20条数据
           proxy: {
               type: 'ajax',
               url: encodeURI('LogAction!GetMessageLogs'),
               reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                   type: 'json', //返回数据类型为json格式
                   root: 'rows',  //数据
                   totalProperty: 'total' //数据总条数
               }
           },
           autoLoad: true //即时加载数据
       });
        
        
         var store_Projectmanagement = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'No'},
                     { name: 'Name'},
                     { name: 'Scale'},
                     { name: 'BuildUnit'},
                     { name: 'Place'},
                     { name: 'Price'},
                     { name: 'Manager'},
                     { name: 'StartDate'},
                     { name: 'Schedule'},
                     { name: 'Content'},
                     { name: 'Cost'},
                     { name: 'Progress'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getProjectmanagementListDef?userName=' + user.name + "&userRole=" +user.role + "&projectName=" + ""),//+ "&projectName=" + projectName
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        var store_ProjectmanagementContent = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'No'},
                     { name: 'Name'},
                     { name: 'Scale'},
                     { name: 'BuildUnit'},
                     { name: 'Place'},
                     { name: 'Price'},
                     { name: 'Manager'},
                     { name: 'StartDate'},
                     { name: 'Schedule'},
                     { name: 'Content'},
                     { name: 'Cost'},
                     { name: 'Progress'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getProjectmanagementListDef?userName=' + user.name + "&userRole=" +user.role + "&projectName=" + projectName),//+ "&projectName=" + projectName
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
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
                url: encodeURI('BasicInfoAction!getPersondbListDef?userName=' + user.name + "&userRole=" +user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        var store_Kaoheresult = Ext.create('Ext.data.Store', {
        	fields: [
        		 {name:'ID'},
    	         { name: 'year'},
                 { name: 'month'},
                 { name: 'score'},
                 { name: 'reason'},
                 { name: 'Accessory'},
                 { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getKaoheresultListDef?userName=' + user.name + "&userRole=" +user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        
        
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
                },
                	{
          			xtype:"datefield",
	                fieldLabel: '有效期1',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                //plugins: 'monthPickerPlugin',
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
	    }
        ]
        
        
        var items_Person = [{
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
	                fieldLabel: '角色',
	                labelAlign: 'right',
	                anchor:'95%',
	                store:["院领导","质安部管理员","其他管理员"],
	                value :'院领导',
	                name: 'Type'
                },{
                	xtype:'textfield',
                    fieldLabel: '身份证号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'IdentityNo',
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
                    fieldLabel: '单位名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'UnitName'
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
                	xtype:'textfield',
                    fieldLabel: '联系电话',
                    allowBlank : false,
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'PhoneNo'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '岗位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Job'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '密码',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'UserPwd',
                    hidden: true,
                    hiddenLabel: true
                }]
	        }]
	    }
        ]
		
		
		var items_project = [{
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
                    fieldLabel: '编号',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'No'
                },{
                	xtype:'textfield',
                    fieldLabel: '名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '项目经理',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Manager'
                
                },
                	{
                	xtype:'textfield',
                    fieldLabel: '规模',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Scale'
                },
                	{
          				xtype:"datefield",
	                fieldLabel: '开工日期',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'StartTime',
	                anchor:'95%',
	                allowBlank: false 
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '建设单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'BuildUnit'
                },{
                	xtype:'textfield',
                    fieldLabel: '地理位置',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Place'
                },{
                	xtype:'combo',
	                fieldLabel: '进度',
	                labelAlign: 'right',
	                anchor:'95%',
	                store:["策划完成","工程开工","土建转序","安装完成及送电","竣工验收","收尾完成","完工结束"],
	                value :'策划完成',
	                name: 'Progress'
                },{
                	xtype:'textfield',
                    fieldLabel: '建安费用',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Cost'
                },{
                	xtype:"datefield",
	                fieldLabel: '合同工期',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'FileTime',
	                anchor:'95%',
	                allowBlank: false 
                }]
	        }]
	    },{
            xtype:'textarea',
            fieldLabel: '建设内容',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'BuildContent'
	    },
        	uploadPanel
        ]
		
		var items_projectperson = [{
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
                    fieldLabel: '职位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Job'
                
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '姓名',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                }]
	        }]
	    },{
            xtype:'textarea',
            fieldLabel: '职责',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Duty'
	    }
        ]
          
        var items_fb = [{
	    	xtype: 'container',
	        anchor: '95%',
	        layout: 'hbox',
	        items:[{
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
                fieldLabel: '附件',
                labelWidth: 120,
                labelAlign: 'right',
                anchor:'95%',
                name: 'Accessory',
               	hidden: true,
                hiddenLabel: true
            },{
            	xtype: 'container',
	        	//id:'s1',
	        	//itemId:'s11',
            	
	        	anchor:'95%',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'combobox',
                    fieldLabel: '所属项目部',
                    store: Ext.create('Ext.data.Store', {
                    	fields: [{ name: 'Name'}],
                    	pageSize: 1000,
                    	proxy: {
                    		type: 'ajax',
                    		url: encodeURI('BasicInfoAction!getProjectNameListDef?userRole=' + role),
                    		reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    			type: 'json', //返回数据类型为json格式
                    			root: 'rows',  //数据
                    			totalProperty: 'total' //数据总条数
                    		}
		                },
		                autoLoad: true //即时加载数据
		            }),
		            displayField: 'Name',
		            valueField: 'Name',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Project'
                },{
                	xtype:'textfield',
                    fieldLabel: '分包单位名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Name'
                },{
                	xtype:'textfield',
                    fieldLabel: '资质等级',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Rank'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            anchor:'95%',
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '分包单位负责人',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Head'
                },{
                	xtype:'textfield',
                    fieldLabel: '分包技术负责人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'TechHead'
                },{
                	xtype:'textfield',
                    fieldLabel: '分包费用',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Cost'
                }]
	    },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '项目负责人',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ProHead'
                },{
                	xtype:'textfield',
                    fieldLabel: '项目技术负责人',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ProTechHead'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '项目安全负责人',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ProSaveHead'
                },{
                	xtype:'textfield',
                    fieldLabel: '项目安全员',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ProSavePeople'
                }]}]
	    },{
                xtype:'textarea',
                fieldLabel: '分包范围',
                labelAlign: 'right',
                height: 75,
                //emptyText: '不超过500个字符',
                maxLength:500,
                anchor:'100%',
                name: 'ProRange'
    	    },
        	uploadPanel]
        	
        	
        	
      	var items_Projectmanagement = [{
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
                        fieldLabel: '项目编号',
                        //labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'95%',
                        name: 'No'
                    },{
                    	xtype:'textfield',
                        fieldLabel: '项目名称',
                        //labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'95%',
                        name: 'Name'
                    },{
                    	xtype:'textfield',
                        fieldLabel: '项目简称',
                        //labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'95%',
                        name: 'Scale'
                    
                    },
                    	{
                    	xtype: 'combo',
                    fieldLabel: '项目经理',
                    labelWidth: 100,
                    labelAlign: 'right',
                    afterLabelTextTpl: required,
                    allowBlank:false,
                    anchor:'95%',
                    name: 'Manager',
                    mode:'local',
                    triggerAction: 'all',
                    editable:true,
                    store: managername_store,
                    displayField:'Manager'
                    /*listeners:
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
                    }*/
                    },
                    	{
              		xtype:"datefield",
    	                fieldLabel: '开工日期',
    	                afterLabelTextTpl: required,
    	                //labelWidth: 200,
    	                labelAlign: 'right',
    	                format:"Y-m-d",
    	                name: 'StartDate',
    	                anchor:'95%',
    	                allowBlank: false 
                    }]
    	        },{
    	        	xtype: 'container',
    	            flex: 1,
    	            layout: 'anchor',
    	            items: [{
                    	xtype:'textfield',
                        fieldLabel: '建设单位',
                        labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'95%',
                        name: 'BuildUnit'
                    },{
                    	xtype:'textfield',
                        fieldLabel: '项目地点',
                        labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'95%',
                        name: 'Place'
                    },{
                  	  xtype:'textfield',
                        fieldLabel: '合同总价（万元）',
                        labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'95%',
                        name: 'Price'
                    },{
                    	xtype:'textfield',
                        fieldLabel: '建安费用（万元）',
                        labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'95%',
                        name: 'Cost'
                    },{
                  	  xtype:'textfield',
                        fieldLabel: '合同工期',
                        labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'95%',
                        name: 'Schedule'
                    },{
                    	xtype:'combo',
    	                fieldLabel: '施工进度',
    	                labelWidth: 122,
    	                labelAlign: 'right',
    	                anchor:'95%',
    	                store:["开工准备阶段","在建阶段","收尾移交阶段","完工总结阶段"],
    	                value :'开工准备阶段',
    	                name: 'Progress'
                    }]
    	        }]
    	    },{
                xtype:'textarea',
                fieldLabel: '建设内容',
                //labelWidth: 120,
                labelAlign: 'right',
                height: 75,
                emptyText: '不超过500个字符',
                maxLength:500,
                anchor:'100%',
                name: 'Content'
    	    }
    	    ]
        
        
        var store_project = Ext.create('Ext.data.Store',
                {
        	       model: 'gridModel',
        	       fields: ['Name','ID'],
                    proxy: {
                        type: 'ajax',
                        url: 'BasicInfoAction!getProjectmanagementListDef?projectName='+'',
                        reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数；9据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                            type: 'json', //返回数据类型为json格式
                            root: 'rows',  //数据
                            totalProperty: 'total' //数据总条数
                        }
                    },
                    listeners:{
                        load : function( store, records, successful, operation){
	                        if(successful){
	                        	var ins_rec = Ext.create('gridModel',{
	                            	Name : '全部',
	                            	ID : '0'
	                            });
	                        	store_project.insert(store_project.getCount()-1,ins_rec);
	                        }
                        }
                    },
                    autoLoad: true //即时加载数据
                });
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
        var items_Kaoheresult =[{
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
                },
//                {
//	            	xtype:'textfield',
//                    fieldLabel: 'ProjectName',
//                    //labelWidth: 120,
//                    labelAlign: 'right',
//                    anchor:'95%',
//                    name: 'ProjectName',
//                    value : projectName,
//                   	hidden: true,
//                    hiddenLabel: true
//                },
                {
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
                	xtype:'combobox',
                    fieldLabel: '月份',
                    store: storemonth,
                    valueField: 'month',
                    displayField: 'month',
//                    labelWidth: 120,
                    editable: false,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'month'
                },
                {
                	xtype:'combo',
                	queryMode:'local',
                	fieldLabel: '项目名称',
                	editable:false,
                	labelAlign: 'right',
//                	labelWidth: 560,
                    anchor:'95%',
                	store:store_project,
                	valueField:'Name',
                	displayField:'Name',
                	triggerAction:'all',
                	autoSelect:true,
                    allowBlank: false,
                    listeners:
                	{
                    	afterrender :function(combo){
//                		  var kind = combo.value;
//                    		alert('ddddd');
              		    if(user.role == '全部项目'){
//              			  var year = forms.getForm().findField('year');
              			 
	              			combo.setVisible(true);
	          		    	combo.hidden = false;
	          		    	combo.hiddenLabel=false;
	          		    	combo.allowBlank = false;
              			   
              		    }
              		    else{
//              		    	 var year = forms.getForm().findField('year');
              		    	 combo.setVisible(false);
                 			  combo.hidden = true;
                 			  combo.hiddenLabel=true;
                 			  combo.allowBlank = true;
              		    }
                	  }
                    },
                    value : projectName,
                    name:'ProjectName'
                }
                ]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'numberfield',
                    fieldLabel: '考核得分',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'score'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '考评得分原因理由',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'reason'
                }]
	        }]
	    },
        	uploadPanel
        ]
        
        
        
        var reSetPdbPwdH = function() 
        {
        	var selRecs = gridDT.getSelectionModel().getSelection();
        	var idcard = selRecs[0].data.IDCard;
        	//alert(idcard);
        	resetURL = 'BasicInfoAction!ReSetPwd?userID=' + idcard+ "&userRole=" + '项目部人员';
        	Ext.Msg.confirm("提示", "确认重置？", function(btn) {  
               if(btn == 'yes')
              	 { 	
              	 Ext.Ajax.request({
                    method : 'POST',
                    url: resetURL,
                    success: function(){
                    	Ext.Msg.alert('提示','重置成功');
                    }
              	 });
              	 }
           })       
        }
        
        var reSetPersonPwdH = function() 
        {
        	var selRecs = gridDT.getSelectionModel().getSelection();
        	var idcard = selRecs[0].data.IdentityNo;
        	//alert(idcard);
        	resetURL = 'BasicInfoAction!ReSetPwd?userID=' + idcard+ "&userRole=" + '其他';
        	Ext.Msg.confirm("提示", "确认重置？", function(btn) {  
               if(btn == 'yes')
              	 { 	
              	 Ext.Ajax.request({
                    method : 'POST',
                    url: resetURL,
                    success: function(){
                    	Ext.Msg.alert('提示','重置成功');
                    }
              	 });
              	 }
           })       
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
        
        var UpdatedepAdmpwd = function(){
        	forms = Ext.create('Ext.form.Panel', {
    			minWidth: 200,
    			minHeight: 100,
    			bodyPadding: 5,
    			buttonAlign: 'center',
    		    layout: 'anchor',
    	        defaults: { anchor: '100%', height: 30, labelWidth: 80 },
    	        autoScroll: true,
    	        url:'AdminInfoAction!ChangedepPwd',
    	        frame: false,
    	        defaultType:'textfield',
    	        items: forms_var,
    	        buttons: [{
    	        	text: '确定',
    	        	formBind: true,
    	        	handler: function() {  	
    	        		var form = this.up('form').getForm();
      	                	form.submit({   	        			
    	  	                	success: function(form, action){
    	  	                		 Ext.Msg.alert('提示',action.result.msg);
    	  	                	},
    	  	                	failure: function(form, action){
    	  	                		 Ext.Msg.alert('警告',action.result.msg);
    	  	                	}
    	                	});
    	                	this.up('window').close();  	        	
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
    	        }]
    	   })	
    	   Ext.create('Ext.window.Window', {
       		title: '修改密码',
               height: 230,
               width: 280,
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
               items:forms
               }).show(); 	
     	}
        
        var addH = function(){
        	var actionURL;
        	var uploadURL;
        	var items;
        	alert(projectName);
        	if(title == '项目概况')
        	{	        	
        		actionURL = 'BasicInfoAction!addProject?userName=' + user.name + "&userRole=" + user.role;  
        		uploadURL = "UploadAction!execute";
        		items = items_project;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addProject',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addProject', title: '新增项目', items: [forms]});
        	}        	
        	else if(title == '用户管理')
        	{	        	
        		actionURL = 'BasicInfoAction!addPerson?userName=' + user.name + "&userRole=" + user.role;  
        		items = items_Person;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addPerson',
    	        	url: actionURL,
    	        	items: items
    	        });
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addPerson', title: '新增人员', items: [forms]});
        	}
        	
        	else if(title == '项目部人员配置')
        	{	        	
        		actionURL = 'BasicInfoAction!addProjectperson?userName=' + user.name + "&userRole=" + user.role;  
        		items = items_projectperson;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addProjectperson',
    	        	url: actionURL,
    	        	items: items
    	        });
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addProjectperson', title: '新增人员', items: [forms]});
        	}
        	
         else if(title == '设计分包单位' || title == '土建分包单位' || title == '安装分包单位' || title == '调试分包单位')
        	{	        	
        		
        		actionURL = 'BasicInfoAction!addfb?userName=' + user.name + "&userRole=" + user.role + "&type=" + title+ "&projectName=" + projectName;  
        		uploadURL = "UploadAction!execute";
        		items = items_fb;
        		//items2 = items_securityPerson;
            	createForm({
        			autoScroll: true,
    	        	bodyPadding: 5,
    	        	action: 'addfb',
    	        	url: actionURL,
    	        	items: items
    	        });
            	uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'addfb', title: '新增项目', items: [forms]});
        	}
        	
        	
        else	if(title == '项目管理')
      	{	        	
      		actionURL = 'BasicInfoAction!addProjectmanagement?userName=' + user.name + "&userRole=" + user.role;
      		//uploadURL = "UploadAction!execute";
      		items = items_Projectmanagement;
          	createForm({
      			autoScroll: true,
  	        	bodyPadding: 5,
  	        	action: 'addProjectmanagement',
  	        	url: actionURL,
  	        	items: items
  	        });
          	//uploadPanel.upload_url = uploadURL;
          	bbar.moveFirst();	//状态栏回到第一页
  	        showWin({ winId: 'addProjectmanagement', title: '新增项目', items: [forms]});
      	}
      	
      	else	if(title == '项目人员库')
      	{	        	
      		actionURL = 'BasicInfoAction!addPersondb?userName=' + user.name + "&userRole=" + user.role;
      		
      		items = items_Persondb;
          	createForm({
      			autoScroll: true,
  	        	bodyPadding: 5,
  	        	action: 'addPersondb',
  	        	url: actionURL,
  	        	items: items
  	        });
          	
          	bbar.moveFirst();	//状态栏回到第一页
  	        showWin({ winId: 'addPersondb', title: '新增项目', items: [forms]});
      	}
      	
      	else  if(title == '考核指标')
           	{	        	
           		
           		actionURL = 'BasicInfoAction!addSaftyproblem?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
           		uploadURL = "UploadAction!execute";
           		items = items_saftyproblem;
               	createForm({
           			autoScroll: true,
       	        	bodyPadding: 5,
       	        	action: 'addSaftyproblem',
       	        	url: actionURL,
       	        	items: items
       	        });
               	uploadPanel.upload_url = uploadURL;
               	bbar.moveFirst();	//状态栏回到第一页
       	        showWin({ winId: 'addSaftyproblem', title: '新增项目', items: [forms]});
           	}
        	
      	else  if(title == '扣分策略')
       	{	        	
       		
       		actionURL = 'BasicInfoAction!addKoufen?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
       		uploadURL = "UploadAction!execute";
       		items = items_Koufen;
           	createForm({
       			autoScroll: true,
   	        	bodyPadding: 5,
   	        	action: 'addKoufen',
   	        	url: actionURL,
   	        	items: items
   	        });
           	uploadPanel.upload_url = uploadURL;
           	bbar.moveFirst();	//状态栏回到第一页
   	        showWin({ winId: 'addKoufen', title: '新增项目', items: [forms]});
       	}
        	
    	else  if(title == '扣分统计')
       	{	        	
       		
       		actionURL = 'BasicInfoAction!addKoufentongji?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
       		uploadURL = "UploadAction!execute";
       		items = items_Koufentongji;
           	createForm({
       			autoScroll: true,
   	        	bodyPadding: 5,
   	        	action: 'addKoufentongji',
   	        	url: actionURL,
   	        	items: items
   	        });
           	uploadPanel.upload_url = uploadURL;
           	bbar.moveFirst();	//状态栏回到第一页
   	        showWin({ winId: 'addKoufentongji', title: '新增项目', items: [forms]});
       	}
        	
        	
        	
    	else  if(title == '考核结果')
       	{	        	
       		
       		actionURL = 'BasicInfoAction!addKaoheresult?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
       		uploadURL = "UploadAction!execute";
       		items = items_Kaoheresult;
           	createForm({
       			autoScroll: true,
   	        	bodyPadding: 5,
   	        	action: 'addKaoheresult',
   	        	url: actionURL,
   	        	items: items
   	        });
           	uploadPanel.upload_url = uploadURL;
           	bbar.moveFirst();	//状态栏回到第一页
   	        showWin({ winId: 'addKaoheresult', title: '新增项目', items: [forms]});
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
        				
        			if(title == '项目概况')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'BasicInfoAction!editProjectmanagement?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Projectmanagement;
              
        			createForm({
            			autoScroll: true,
            			action: 'editProjectmanagement',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProjectmanagement', title: '修改项目', items: [forms]});
        			}
        			
        			else if(title == '项目部人员配置')
        			{
        				actionURL = 'BasicInfoAction!editProjectperson?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_projectperson;
              
        			createForm({
            			autoScroll: true,
            			action: 'editProjectperson',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProjectperson', title: '修改人员', items: [forms]});
        			}
        			
        			else if(title == '用户管理')
        			{
        				actionURL = 'BasicInfoAction!editPerson?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Person;
              
        			createForm({
            			autoScroll: true,
            			action: 'editPerson',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editPerson', title: '修改人员', items: [forms]});
        			}
        			
        			else if(title == '项目管理')
        			{
        				var prevName = selRecs[0].data.Name;
        				//Ext.Msg.alert('a', prevName);
        				actionURL = 'BasicInfoAction!editProjectmanagement?userName=' + user.name + "&userRole=" + user.role + "&prevName=" + prevName;  
        				items = items_Projectmanagement;
              
        			createForm({
            			autoScroll: true,
            			action: 'editProjectmanagement',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProjectmanagement', title: '修改项目', items: [forms]});
        			}
        			
					else if(title == '设计分包单位' || title == '土建分包单位' || title == '安装分包单位' || title == '调试分包单位')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'BasicInfoAction!editfb?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
        				items = items_fb;
              
        			createForm({
            			autoScroll: true,
            			action: 'editfb',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
                	//gridDT.getSelectionModel().clearSelections();
        	        showWin({ winId: 'editfb', title: '修改项目', items: [forms]});
        			}
        			
        			else if(title == '项目人员库' )
        			{
        				actionURL = 'BasicInfoAction!editPersondb?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Persondb;
              
        			createForm({
            			autoScroll: true,
            			action: 'editPersondb',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editPersondb', title: '修改人员', items: [forms]});
        			}
        			
        			else if(title == '考核指标')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'BasicInfoAction!editSaftyproblem?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
        				items = items_saftyproblem;
              
        			createForm({
            			autoScroll: true,
            			action: 'editSaftyproblem',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editSaftyproblem', title: '修改项目', items: [forms]});
        			}
        			
        			else if(title == '扣分策略')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'BasicInfoAction!editKoufen?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
        				items = items_Koufen;
              
        			createForm({
            			autoScroll: true,
            			action: 'editKoufen',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editKoufen', title: '修改项目', items: [forms]});
        			}
        			
        			
        			else if(title == '扣分统计')
        			{
//        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'BasicInfoAction!editKoufentongji?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
        				items = items_Koufentongji;
              
        			createForm({
            			autoScroll: true,
            			action: 'editKoufentongji',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editKoufentongji', title: '修改项目', items: [forms]});
        			}
        			
        			
        			else if(title == '考核结果')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'BasicInfoAction!editKaoheresult?userName=' + user.name + "&userRole=" + user.role + "&type=" + title;  
        				items = items_Kaoheresult;
              
        			createForm({
            			autoScroll: true,
            			action: 'editKaoheresult',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editKaoheresult', title: '修改项目', items: [forms]});
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
    					if(title == '项目概况')
    					{$.getJSON(encodeURI("BasicInfoAction!deleteProject?userName=" + user.name + "&userRole=" + user.role),
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
    					else if(title == '项目部人员配置')
    					{$.getJSON(encodeURI("BasicInfoAction!deleteProjectperson?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					else if(title == '项目管理')
    					{$.getJSON(encodeURI("BasicInfoAction!deleteProjectmanagement?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
    							{id: keyIDs.toString()},	//Ajax参数
                                function (res) 
                                {
    								if (res.success) 
    								{
                                        //重新加载store
    									dataStore.load({ params: { start: 0, limit: psize } });
    									bbar.moveFirst();	//状态栏回到第一页
    									window.location.reload();
                                    }
                                    else 
                                    {
                                    	Ext.Msg.alert("信息", res.msg);
                                    }
                                });
    					}
    					
    					else if(title == '用户管理')
    					{$.getJSON(encodeURI("BasicInfoAction!deletePerson?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					else if(title == '设计分包单位' || title == '土建分包单位' || title == '安装分包单位' || title == '调试分包单位')
    					{$.getJSON(encodeURI("BasicInfoAction!deletefb?userName=" + user.name + "&userRole=" + user.role + "&type=" + title),
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
    					else if(title == '考核指标')
    					{$.getJSON(encodeURI("BasicInfoAction!deleteSaftyproblem?userName=" + user.name + "&userRole=" + user.role + "&type=" + title),
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
    					
    					else if(title == '扣分策略')
    					{$.getJSON(encodeURI("BasicInfoAction!deleteKoufen?userName=" + user.name + "&userRole=" + user.role + "&type=" + title),
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
    					
    					
    					else if(title == '扣分统计')
    					{$.getJSON(encodeURI("BasicInfoAction!deletekoufentongji?userName=" + user.name + "&userRole=" + user.role + "&type=" + title),
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
    					
    					
    					else if(title == '扣分统计')
    					{$.getJSON(encodeURI("BasicInfoAction!deleteKaoheresult?userName=" + user.name + "&userRole=" + user.role + "&type=" + title),
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
    					
    					else if(title == '项目人员库')
    					{$.getJSON(encodeURI("BasicInfoAction!deletePersondb?userName=" + user.name + "&userRole=" + user.role),
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
    					
    					
    					else if(title == '考核结果')
    					{$.getJSON(encodeURI("BasicInfoAction!deleteKaoheresult?userName=" + user.name + "&userRole=" + user.role),
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
        
        
        
        //*************saftyproblem
        function  showitem( )
        {
        	var kindstr = "";
        	var subkindstr = "";
        	var showkindstr = "";
        	kind = forms.getForm().findField('kind');
        	subkind = forms.getForm().findField('subkind');
        	showkind = forms.getForm().findField('showkind');
        	kindstr = kind.getValue().substring(0,4);
    		subkindstr = subkind.getValue();
    		if(kindstr==""||subkindstr==""){
    			showkindstr = "";
    		}else{
    			showkindstr = subkindstr+"("+kindstr+")";
    		}
    		showkind.setValue("");
    		showkind.setValue(showkindstr);
        }
        
        var items_saftyproblem =[{
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
                      fieldLabel: '附件',
                      labelWidth: 120,
                      labelAlign: 'right',
                      anchor:'90%',
                      name: 'Accessory',
                     	hidden: true,
                      hiddenLabel: true
                  },
                  {
                  	xtype: 'textfield',
                      fieldLabel: '隐患类别', 
                      anchor:'90%',
                      labelAlign: 'right',
                      name: 'kind'
                  },
                  {
  	            	xtype:'textfield',
                      fieldLabel: '子类',
//                      labelWidth: 120,
                      labelAlign: 'right',
                      anchor:'90%',
                      name: 'subkind'
                  },{
                  	xtype:'textfield',
                      fieldLabel: '显示',
//                      labelWidth: 120,
                      labelAlign: 'right',
                      anchor:'90%',
                      name: 'showkind',
                      listeners:{
                          'focus': showitem
                      }
                  },{
                  	xtype:'numberfield',
                      fieldLabel: '分数',
//                      labelWidth: 120,
                      labelAlign: 'right',
                      anchor:'90%',
                      name: 'score'
                  }]
  	        }]
  	    }]
        
        
        var zhicombo = new Ext.data.ArrayStore({
            fields: ['id', 'zhi'],
            data: [[1, '2分/项'], 
            	[2, '3分/项'], 
            	[3, '10分/项'],
            	[4, '3分/次'], 
            	[5, '3分/单位'], 
            	[6, '2分'], 
            	[7, '3分']]
          });
        
        var zhouqicombo = new Ext.data.ArrayStore({
            fields: ['id', 'zhouqi'],
            data: [[1, '月/节点'], 
            	[2, '年度'], 
            	[3, '月度'],
            	[4, '整个项目'], 
            	[5, '整个项目/年度'], 
            	[6, '3、6、9、12月']]
          });
        
        var qidiancombo = new Ext.data.ArrayStore({
            fields: ['id', 'qidian'],
            data: [[1, '项目部成立'], 
            	[2, '项目部成立/每年3月1日'], 
            	[3, '正式开工'],
            	[4, '正式开工/1月1日'],
            	[5, '正式开工/3月1日'],
            	[6, '培训计划制定之日'],
            	[7, '进入土建施工阶段后15日'], 
            	[8, '进入安装调试阶段后15日'], 
            	[9, '进入试运行阶段后15日'],
            	[10, '每年底12月25日'],
            	[11, '安全检查']]
          });
        
        
      //*************Koufen
        var items_Koufen =[{
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
                      fieldLabel: '附件',
                      labelWidth: 120,
                      labelAlign: 'right',
                      anchor:'90%',
                      name: 'Accessory',
                     	hidden: true,
                      hiddenLabel: true
                  },
                  {
                  	xtype: 'textfield',
                      fieldLabel: '扣分项', 
                      anchor:'90%',
                      labelAlign: 'right',
                      allowBlank: false,
                      name: 'koufenx'
                  },{
	            	xtype:'combobox',
                    fieldLabel: '扣分值',
                    store: zhicombo,
                    valueField: 'zhi',
                    displayField: 'zhi',
//                    labelWidth: 120,
                    editable: false,
                    labelAlign: 'right',
                    anchor:'90%',
                    allowBlank: false,
                    name: 'koufenzhi'
                  },{
	            	xtype:'combobox',
                    fieldLabel: '扣分周期',
                    store: zhouqicombo,
                    valueField: 'zhouqi',
                    displayField: 'zhouqi',
//                    labelWidth: 120,
                    editable: false,
                    labelAlign: 'right',
                    anchor:'90%',
                    allowBlank: false,
                    name: 'koufenzq'
                  },{
	            	xtype:'combobox',
                    fieldLabel: '考核起点',
                    store: qidiancombo,
                    valueField: 'qidian',
                    displayField: 'qidian',
//                    labelWidth: 120,
                    editable: false,
                    labelAlign: 'right',
                    anchor:'90%',
                    allowBlank: false,
                    name: 'kaoheqidian'
                  },{
                 	xtype:'textfield',
                    fieldLabel: '对应库表',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    allowBlank: false,
                    name: 'duiykub'
                },{
                	xtype:'textfield',
                    fieldLabel: '对应字段',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    allowBlank: false,
                    name: 'duiyzid'
                },{
                	xtype:'textfield',
                    fieldLabel: '包含文件',
//                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'90%',
                    allowBlank: false,
                    name: 'baohfile'
                }]
  	        }]
  	    }]
        
        
        //*************Koufentongji
        var items_Koufentongji =[{
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
                    fieldLabel: '项目名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'proname'
                },{
                	xtype:'numberfield',
                    fieldLabel: '扣分值',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'koufenzhi'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'numberfield',
                    fieldLabel: '总分',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    allowBlank: false,
                    name: 'zongfen'
                }]
	        }]
	    },{ 
            xtype:'textarea',
            fieldLabel: '扣分项',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 100,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            //allowBlank: false,
            name: 'koufenitem'
	    }]
        
        
      //***********Saftyproblem
        var store_saftyproblem = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'kind'},
                     { name: 'subkind'},
                     { name: 'showkind'},
                     { name: 'score'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getSaftyproblemListDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        //***********Koufen
        var store_Koufen = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'koufenx'},
                     { name: 'koufenzhi'},
                     { name: 'koufenzq'},
                     { name: 'kaoheqidian'},
                     { name: 'duiykub'},
                     { name: 'duiyzid'},
                     { name: 'baohfile'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getKoufenListDef?userName=' + user.name + "&userRole=" + user.role),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
      //***********Koufentongji
        
        //获取当前日期，并转换成201705这种格式，传给后台
        var nowdate = function() {
        	var mydate = new Date();
        	var year = mydate.getFullYear();
        	var month = mydate.getMonth()+1;
        	if(month < 10)
        		month = '0' + month;
        	return year.toString() + month;
        }
        var store_Koufentongji = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'proname'},
                     { name: 'koufenitem'},
                     { name: 'koufenzhi'},
                     { name: 'kaoheqidian'},
                     { name: 'zongfen'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('BasicInfoAction!getKoufentongjiListDef?userName=' + user.name + "&userRole=" + user.role + "&date=" + nowdate()),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
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
        
        var btnResetPdbPwd = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '重置密码',
        	disabled:true,
           	icon: "Images/ims/toolbar/edit.png",
            handler: reSetPdbPwdH
        })
        
        var btnResetPersonPwd = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '重置密码',
        	disabled:true,
           	icon: "Images/ims/toolbar/edit.png",
            handler: reSetPersonPwdH
        })
        
        /*var selRecs = gridDT.getSelectionModel().getSelection();
        	var idcard = selRecs[0].data.Idcard;
        	resetURL = 'BasicInfoAction!ReSetPwd?userID=' + idcard;
        	alert('进来了么');
        	Ext.Msg.alert('aaaaaaaaaaaaaaaaaaaaaaaa');*/
        
        
        //建立工具栏
        var tbar = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            }
        })
     
        if(title == '项目概况')
        {	
			dataStore = store_ProjectmanagementContent;
        	
        	queryURL = 'BasicInfoAction!getProjectmanagementListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	
        	
        	
        	
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '项目编号', dataIndex: 'No', align: 'center', width: 80},
            	    { text: '项目名称', dataIndex: 'Name', align: 'center', width: 150},
            	    //{ text: '项目简称', dataIndex: 'Scale', align: 'center', width: 100},
            	    { text: '建设单位', dataIndex: 'BuildUnit', align: 'center', width: 150},
            	    { text: '项目地点', dataIndex: 'Place', align: 'center', width: 150},
            	    //{ text: '合同总价（万元）', dataIndex: 'Price', align: 'center', width: 120},
            	    { text: '项目经理', dataIndex: 'Manager', align: 'center', width: 80},
            	    { text: '开工日期', dataIndex: 'StartDate', align: 'center', width: 120},
            	    { text: '合同工期', dataIndex: 'Schedule', align: 'center', width: 80}
            	    //{ text: '项目概况', dataIndex: 'Content', align: 'center', width: 80},
            	    //{ text: '建安费用（万元）', dataIndex: 'Cost', align: 'center', width: 120},
            	    //{ text: '附件', dataIndex: 'Accessory', align: 'center', width: 80}
            	]
            	//tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		//tbar.add(btnScan);
        		//tbar.add("-");
        		//tbar.add(btnAdd);
        		//tbar.add(btnEdit);
        		//tbar.add(btnDel);
        		
        		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'BasicInfoAction!getProjectmanagementListDef?userName=' + user.name + "&userRole=" +user.role + "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			/*var newline = [{ text: '所属项目', dataIndex: 'Name', align: 'center', width: 80}];
        			column = Ext.Array.merge(newline,column);*/
        			
        		}
        		
        }
        
        else if(title == '项目部人员配置')
        {	
        	dataStore = store_Projectperson;
        	queryURL = 'BasicInfoAction!getProjectpersonListSearch?userName=' + user.name + "&userRole=" + user.role+"&projectName="+projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '职位', dataIndex: 'Job', align: 'center', width: 200},
            	    { text: '姓名', dataIndex: 'Name', align: 'center', width: 150},
            	    { text: '职责', dataIndex: 'Duty', align: 'center', width: 300}
            	]
            	//tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		//tbar.add(btnScan);
        		//tbar.add("-");
        		//tbar.add(btnAdd);
        		//tbar.add(btnEdit);
        		//tbar.add(btnDel);
        }
        
        
        
        else if(title == '用户管理')
        {	
        	dataStore = store_Person;
        	queryURL = 'BasicInfoAction!getPersonListSearch?userName=' + user.name + "&userRole=" + user.role;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
            	    { text: '角色', dataIndex: 'Type', align: 'center', width: 150},
            	    { text: '姓名', dataIndex: 'Name', align: 'center', width: 150},
            	    { text: '单位名称', dataIndex: 'UnitName', align: 'center', width: 150},
            	    { text: '岗位', dataIndex: 'Job', align: 'center', width: 150},           	    
            	    { text: '身份证号', dataIndex: 'IdentityNo', align: 'center', width: 300},
            	    { text: '手机号', dataIndex: 'PhoneNo', align: 'center', width: 200}
            	]
            	//tbar.add("-");
            	tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add(btnResetPersonPwd);
        		tbar.add("-");
        		tbar.add({
            		xtype: 'radiogroup',
            		itemId: 'grid_type',
            		width: 500,
            		items: [{
        				xtype: 'radio',
        				boxLabel: '全部',
        				name: 'Type',
        				inputValue: 'all',
        				labelWidth: 30,
        				checked: true
        			},{
        				xtype: 'radio',
        				boxLabel: '院领导',
        				name: 'Type',
        				labelWidth: 80,
        				inputValue: 'yld'
        				
        			},{
        				xtype: 'radio',
        				boxLabel: '质安部管理员',
        				name: 'Type',
        				labelWidth: 80,
        				inputValue: 'zabgly'
        			},{
        				xtype: 'radio',
        				boxLabel: '其他管理员',
        				name: 'Type',
        				labelWidth: 150,
        				inputValue: 'other'
        			}],
        			listeners: {
                    	'change' : function(obj) {
                    		var PType = obj.lastValue['Type'];
                    		if (PType == 'all') {
                    			findStr = '';
                    		} else if (PType == 'yld') {
                    			findStr = '院领导';
                    			
                    		} else if (PType == 'zabgly') {
                    			findStr = '质安部管理员';
                    			
                    		} else if (PType == 'other') {
                    			findStr = '其他管理员';
                    			
                    		}
                    		dataStore.getProxy().url = encodeURI(queryURL + '&findStr=' + findStr);
                        	dataStore.load({params: { start: 0, limit: psize }});
                        	bbar.moveFirst();
                    	}
                    }
            	});
				if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
				//tbar.add(btnResetPwd);
        		//tbar.add(btnScan);
        }
        

        
        else if(title == '登陆日志')
        {	
        	dataStore = store_Log;
        	queryURL = 'LogAction!SearchLoginLog';
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: 'ID', dataIndex: 'XH', align: 'center', width: 200,hidden:true},
            	    { text: '用户名', dataIndex: 'UserName', align: 'center', width: 150},
            	    { text: '用户角色', dataIndex: 'Role', align: 'center', width: 150},           	    
            	    { text: '登录时间', dataIndex: 'Logintime', align: 'center', width: 300},
            	    { text: '登录IP', dataIndex: 'Loginip', align: 'center', width: 300}
            	]
            	//tbar.add("-");
				tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);


        		//tbar.add(btnScan);
        }
        
        
        else if(title == '操作日志')
        {	
        	dataStore = store_OptLog;
        	queryURL = 'LogAction!SearchOptLog';
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: 'ID', dataIndex: 'XH', align: 'center', width: 200,hidden:true},
            	    { text: '用户名', dataIndex: 'UserName', align: 'center', width: 150},
            	    { text: '用户角色', dataIndex: 'Role', align: 'center', width: 150},           	    
            	    { text: '操作时间', dataIndex: 'OptTime', align: 'center', width: 300},
            	    { text: '操作内容', dataIndex: 'Opt', align: 'center', width: 400}
            	]
            	//tbar.add("-");
				tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);


        		//tbar.add(btnScan);
        }
        
        else if(title == '短信日志')
        {	
        	dataStore = store_MessageLog;
        	queryURL = 'LogAction!SearchMessageLog';
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: 'ID', dataIndex: 'ID', align: 'center', width: 200,hidden:true},
            	    { text: '类型', dataIndex: 'Type', align: 'center', width: 100},
            	    { text: '接收人', dataIndex: 'SendTo', align: 'center', width: 120},           	    
            	    { text: '手机号', dataIndex: 'SendPhone', align: 'center', width: 150},
            	    { text: '发送时间', dataIndex: 'SendTime', align: 'center', width: 150},
            	    { text: '短信内容', dataIndex: 'SendContent', align: 'center', width: 600}
            	]
            	//tbar.add("-");
				tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);


        		//tbar.add(btnScan);
        }
        
        else if(title == '项目人员库')
        {	
        	dataStore = store_Persondb;
        	queryURL = 'BasicInfoAction!getPersondbListSearch?userName=' + user.name + "&userRole=" + user.role;
        	reflashURL = 'BasicInfoAction!getPersondbListReflash?userName=' + user.name + "&userRole=" + user.role;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
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
        		//tbar.add(btnScan);
        		tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		tbar.add(btnResetPdbPwd);
        		tbar.add("-");
        		tbar.add({
            		xtype: 'radiogroup',
            		itemId: 'grid_type',
            		width: 500,
            		items: [{
        				xtype: 'radio',
        				boxLabel: '全部',
        				name: 'PType',
        				inputValue: 'all',
        				labelWidth: 30,
        				checked: true
        			},{
        				xtype: 'radio',
        				boxLabel: '项目经理',
        				name: 'PType',
        				labelWidth: 120,
        				inputValue: 'xmjl'
        				
        			},{
        				xtype: 'radio',
        				boxLabel: '项目副经理',
        				name: 'PType',
        				labelWidth: 150,
        				inputValue: 'xmfjl'
        			},{
        				xtype: 'radio',
        				boxLabel: '项目总工',
        				name: 'PType',
        				labelWidth: 1200,
        				inputValue: 'xmzg'
        			},{
        				xtype: 'radio',
        				boxLabel: '项目施工经理',
        				name: 'PType',
        				labelWidth: 220,
        				inputValue: 'xmsgjl'
        			},{
        				xtype: 'radio',
        				boxLabel: '项目安全总监',
        				name: 'PType',
        				labelWidth: 220,
        				inputValue: 'xmaqzj'
        			}],
        			listeners: {
                    	'change' : function(obj) {
                    		var PType = obj.lastValue['PType'];
                    		if (PType == 'all') {
                    			findStr = '';
                    		} else if (PType == 'xmjl') {
                    			findStr = '项目经理';
                    			
                    		} else if (PType == 'xmzg') {
                    			findStr = '项目总工';
                    			
                    		} else if (PType == 'xmsgjl') {
                    			findStr = '项目施工经理';
                    			
                    		}else if (PType == 'xmaqzj') {
                    			findStr = '项目安全总监';
                    			
                    		}
                    		else if (PType == 'xmfjl') {
                    			findStr = '项目副经理';
                    			
                    		}
                    		dataStore.getProxy().url = encodeURI(reflashURL + '&findStr=' + findStr);
                        	dataStore.load({params: { start: 0, limit: psize }});
                        	bbar.moveFirst();
                    	}
                    }
            	});
            	if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        		
        }
        
		else if(title == '设计分包单位' || title == '土建分包单位' || title == '安装分包单位' || title == '调试分包单位')
        {	
        	dataStore = store_fb;
        	
        	queryURL = 'BasicInfoAction!getfbListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    //{ text: '编号', dataIndex: 'ProNo', align: 'center', width: 150},
            	    //{ text: '所属项目部', dataIndex: 'Project', align: 'center', width: 150},
            	    { text: '分包单位名称', dataIndex: 'Name', align: 'center', width: 150},
            	    { text: '分包范围', dataIndex: 'ProRange', align: 'center', width: 200},
            	    { text: '分包费用', dataIndex: 'Cost', align: 'center', width: 150},
            	    { text: '单位负责人', dataIndex: 'Head', align: 'center', width: 150},
            	    { text: '单位技术负责人', dataIndex: 'TechHead', align: 'center', width: 150},
            	    { text: '项目负责人', dataIndex: 'ProHead', align: 'center', width: 150},
            	    { text: '项目技术负责人', dataIndex: 'ProTechHead', align: 'center', width: 80},
            	    { text: '项目安全负责人', dataIndex: 'ProSaveHead', align: 'center', width: 80},
            	    { text: '项目安全员', dataIndex: 'ProSavePeople', align: 'center', width: 80},
            	    { text: '资质等级', dataIndex: 'Rank', align: 'center', width: 80},
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
        
        
        
		else if(title == '分包单位汇总')
        {	
//			alert('分包单位汇总');
        	dataStore = store_fbtongji;
        	
        	queryURL = 'BasicInfoAction!getfbListSearch?userName=' + user.name + "&userRole=" + user.role+ "&type="  + "&projectName=" ;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    //{ text: '编号', dataIndex: 'ProNo', align: 'center', width: 150},
            	    { text: '分包单位名称', dataIndex: 'Name', align: 'center', width: 150},
            	    { text: '所属项目部', dataIndex: 'Project', align: 'center', width: 150},
            	    
            	    { text: '分包范围', dataIndex: 'ProRange', align: 'center', width: 200},
            	    { text: '分包费用', dataIndex: 'Cost', align: 'center', width: 150},
            	    { text: '单位负责人', dataIndex: 'Head', align: 'center', width: 150},
            	    { text: '单位技术负责人', dataIndex: 'TechHead', align: 'center', width: 150},
            	    { text: '项目负责人', dataIndex: 'ProHead', align: 'center', width: 150},
            	    { text: '项目技术负责人', dataIndex: 'ProTechHead', align: 'center', width: 80},
            	    { text: '项目安全负责人', dataIndex: 'ProSaveHead', align: 'center', width: 80},
            	    { text: '项目安全员', dataIndex: 'ProSavePeople', align: 'center', width: 80},
            	    { text: '资质等级', dataIndex: 'Rank', align: 'center', width: 80},  
            	    //{ text: '所属项目', dataIndex: 'Project', align: 'center', width: 120}, 
            	    { text: '分包单位类别', dataIndex: 'Type', align: 'center', width: 120},
            	    { text: '附件', dataIndex: 'Accessory', align: 'center', width: 80, hidden: true}
            	    
            	]
            	
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add(btnScan);
        		//alert(user.role);
        }
        
        else if(title == '修改密码') {
        	tbar.add(btnUpdatePwd);
        }
        
        else if(title == '退出登陆')
      	 {
      	 Ext.MessageBox.confirm("提示", "确认退出？", function(btn) {  
               if(btn == 'yes')
              	 { 	
              	 Ext.Ajax.request({
                    method : 'POST',
                    url: 'BasicInfoAction!DestroySession',
                    success: function(){
                    location.href='login.html';
                    }
              	 });
              	 }
           })  
      	 }
      	 
      	else if(title == '项目管理')
        {	
        	dataStore = store_Projectmanagement;
        	
        	queryURL = 'BasicInfoAction!getProjectmanagementListSearch?userName=' + user.name + "&userRole=" + user.role;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '项目编号', dataIndex: 'No', align: 'center', width: 80},
            	    { text: '项目名称', dataIndex: 'Name', align: 'center', width: 150},
            	    { text: '项目简称', dataIndex: 'Scale', align: 'center', width: 100},
            	    { text: '建设单位', dataIndex: 'BuildUnit', align: 'center', width: 150},
            	    { text: '项目地点', dataIndex: 'Place', align: 'center', width: 150},
            	    { text: '合同总价（万元）', dataIndex: 'Price', align: 'center', width: 120},
            	    { text: '项目经理', dataIndex: 'Manager', align: 'center', width: 80},
            	    { text: '开工日期', dataIndex: 'StartDate', align: 'center', width: 120},
            	    { text: '合同工期', dataIndex: 'Schedule', align: 'center', width: 80},
            	    { text: '项目概况', dataIndex: 'Content', align: 'center', width: 80},
            	    { text: '建安费用（万元）', dataIndex: 'Cost', align: 'center', width: 120},
            	    { text: '施工进度', dataIndex: 'Progress', align: 'center', width: 80}
            	]
            	//tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		//tbar.add(btnScan);
        		tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		//项目部人员可看不可编辑
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        } 
        else if(title == '考核指标')
        {	
        	dataStore = store_saftyproblem;
        	
        	queryURL = 'BasicInfoAction!getSaftyproblemListSearch?userName=' + user.name + "&userRole=" + user.role;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '隐患类型', dataIndex: 'kind', align: 'center', width: 150},
            	    { text: '子类', dataIndex: 'subkind', align: 'center', width: 200},
            	    { text: '显示', dataIndex: 'showkind', align: 'center', width: 220},
            	    { text: '分数', dataIndex: 'score', align: 'center', width: 150}
            	]
        	    tbar.add("-");
    		    tbar.add(textSearch);
    		    tbar.add(btnSearch);
    		    tbar.add(btnSearchR);
//    		tbar.add(btnScan);
            	tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		
        		if(user.role === '其他管理员' || user.role === '院领导' ||  user.role === '项目部人员') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        }
        else if(title == '扣分策略')
        {	
        	dataStore = store_Koufen;
        	
        	queryURL = 'BasicInfoAction!getKoufenListSearch?userName=' + user.name + "&userRole=" + user.role;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '扣分项', dataIndex: 'koufenx', align: 'center', width: 350},
            	    { text: '扣分值', dataIndex: 'koufenzhi', align: 'center', width: 100},
            	    { text: '扣分周期', dataIndex: 'koufenzq', align: 'center', width: 150},
            	    { text: '考核起点', dataIndex: 'kaoheqidian', align: 'center', width: 220},
            	    { text: '对应库表', dataIndex: 'duiykub', align: 'center', width: 200},
            	    { text: '对应字段', dataIndex: 'duiyzid', align: 'center', width: 220},
            	    { text: '包含文件', dataIndex: 'baohfile', align: 'center', width: 150}
            	]
        	    tbar.add("-");
    		    tbar.add(textSearch);
    		    tbar.add(btnSearch);
    		    tbar.add(btnSearchR);
//    		tbar.add(btnScan);
            	tbar.add("-");
        		tbar.add(btnAdd);
        		tbar.add(btnEdit);
        		tbar.add(btnDel);
        		
        		if(user.role === '其他管理员' || user.role === '院领导' ||  user.role === '项目部人员') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        }
        
        else if(title == '扣分统计')
        {	
        	//Ext.Msg.alert('警告',nowdate());
        	dataStore = store_Koufentongji;
        	queryURL = 'BasicInfoAction!getKoufentongjiListSearch?userName=' + user.name + "&userRole=" + user.role + '&startDate=' + startDate+ '&endDate=' + endDate+'&date=' + mdate;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	reflashURL = 'BasicInfoAction!getKoufentongjiListDef?userName=' + user.name + "&userRole=" + user.role + '&startDate=' + startDate+ '&endDate=' + endDate+'&date=' + mdate;
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '项目名称', dataIndex: 'proname', align: 'center', width: 300},
            	    { text: '扣分项', dataIndex: 'koufenitem', align: 'center', width: 550},
            	    { text: '扣分值', dataIndex: 'koufenzhi', align: 'center', width: 150},
            	    { text: '总分', dataIndex: 'zongfen', align: 'center', width: 150}
            	]
        	    
//    		tbar.add(btnScan);
            	//tbar.add("-");
        		//tbar.add(btnAdd);
        		//tbar.add(btnEdit);
        		//tbar.add(btnDel);
        		tbar.add({
        			xtype: 'monthfield',
        			fieldLabel: '日期(月度/年度)',
        			editable: false,
        			width: 250,
        			labelWidth: 150,
        			labelAlign: 'right',
                    format: 'Y-m',
                    onOkClick: function (picker, value) {
							        var me = this,
							            month = value[0],
							            year = value[1],
							            date = new Date(year, month, 1);
							        me.startDay = date;
							        me.setValue(date);
							        this.picker.hide();
							        //this.blur();
							        month = month+1;
							        if(month < 10)
							        	month = '0'+month;
							        mdate = year.toString() + month;
							        //Ext.Msg.alert('警告',mdate);
							        dataStore.getProxy().url = encodeURI('BasicInfoAction!getKoufentongjiListDef?userName=' + user.name + "&userRole=" + user.role + '&startDate=' + startDate+ '&endDate=' + endDate+'&date=' + mdate);
                        			dataStore.load({params: { start: 0, limit: psize }});
                        			bbar.moveFirst();
					}
        		});
        		tbar.add({
        			xtype:"datefield",
    	                fieldLabel: '月/节点考核起始',
    	                //afterLabelTextTpl: required,
    	                labelWidth: 160,
    	                width: 260,
    	                labelAlign: 'right',
    	                format:"Y-m-d" ,              
    	                //anchor:'95%'
    	                listeners:{
    	                	'select': function ( field, value, eOpts ) {
    	                		startDate = Ext.util.Format.date(value, 'Ymd');
    	                	}
    	                }
    	                
    	                
        		},{
        			xtype:"datefield",
    	                fieldLabel: '终止',
    	                //afterLabelTextTpl: required,
    	                labelWidth: 60,
    	                width: 160,
    	                labelAlign: 'right',
    	                format:"Y-m-d",
    	                listeners:{
    	                	'select': function ( field, value, eOpts ) {
    	                		
    	                		endDate = Ext.util.Format.date(value, 'Ymd');
    	                		mdate = endDate.substring(0,4);
    	                		
    	                		//var test = Ext.util.Format.date(new Date(), 'm');
    	                		
    	                		//Ext.Msg.alert('警告',test);
    	                		dataStore.getProxy().url = encodeURI('BasicInfoAction!getKoufentongjiListDef?userName=' + user.name + "&userRole=" + user.role + '&startDate=' + startDate+ '&endDate=' + endDate+'&date=' + mdate);
                        		dataStore.load({params: { start: 0, limit: psize }});
                        		bbar.moveFirst();
    	                	}
    	                }
        		});
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
       
        else if(title == '考核结果')
        {	
        	dataStore = store_Kaoheresult;
//        	getKaoheresultListDef
        	queryURL = 'BasicInfoAction!getKaoheresultListSearch?userName=' + user.name + "&userRole=" + user.role;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '年份', dataIndex: 'year', align: 'center', width: 100},
            	    { text: '月份', dataIndex: 'month', align: 'center', width: 100},
            	    { text: '考核得分', dataIndex: 'score', align: 'center', width: 220},
            	    { text: '考评得分原因理由', dataIndex: 'reason', align: 'center', width: 300},
            	    { text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 250}
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
        		
        		if(user.role === '其他管理员' || user.role === '院领导' ||  user.role === '项目部人员') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        		
        		
        		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'BasicInfoAction!getKaoheresultListDef?userName=' + user.name 
                                             + "&userRole=" +user.role + "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
//        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}];
//        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			
        			queryURL = 'BasicInfoAction!getKaoheresultListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=";
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
                		
                		var myflag = true;
            			uploadPanel.store.each(function(record)
            			{            	//alert(record.get('status'));
            				if(record.get('status') != -9 && record.get('status') != -4)
            				{
            					myflag = false;
            				}           
            			})
                		
                		
                		if(myflag&&forms.form.isValid()){
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

                				case "addfb":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				case "editfb": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				/*case "addProjectmanagement":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}*/
                				
                				case "addPersondb":{
                					//Ext.Msg.alert('test','test');
                					var IDCard = forms.getForm().findField('IDCard');
                    				var IDCardtext = IDCard.getValue();
                    			
                    				var birth = forms.getForm().findField('Birthday');
                    				birth.setValue( getBirthdayFromIdCard(IDCardtext));
                    				
                    				var pwd = forms.getForm().findField('UserPwd');
                    				pwd.setValue( IDCardtext.substring(IDCardtext.length-6, IDCardtext.length) );
                    				//Ext.Msg.alert('警告',pwd.getValue());
                    			
                    				//Ext.Msg.alert('警告',birth.getValue());
                    				break;
                				}
                				
                				
                				case "editPersondb":{
                					var IDCard = forms.getForm().findField('IDCard');
                    				var IDCardtext = IDCard.getValue();
                    			
                    				var birth = forms.getForm().findField('Birthday');
                    				birth.setValue( getBirthdayFromIdCard(IDCardtext));
                    				
                    				var pwd = forms.getForm().findField('UserPwd');
                    				pwd.setValue( IDCardtext.substring(IDCardtext.length-6, IDCardtext.length) );
                    				//Ext.Msg.alert('警告',pwd.getValue());
                					break;
                				}
                				
                				case "addPerson":{
                					//Ext.Msg.alert('test','test');
                					var IDCard = forms.getForm().findField('IdentityNo');
                    				var IDCardtext = IDCard.getValue();
                    				
                    				var pwd = forms.getForm().findField('UserPwd');
                    				pwd.setValue( IDCardtext.substring(IDCardtext.length-6, IDCardtext.length) );
                    				//Ext.Msg.alert('警告',pwd.getValue());
                    			
                    				//Ext.Msg.alert('警告',birth.getValue());
                    				break;
                				}
                				
                				case "addSaftyproblem":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					
                				}
                				
                				case "editSaftyproblem": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				
                				case "addKoufen":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					
                				}
                				
                				case "editKoufen": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				
                				
                				case "addKoufentongji":{
//                					var fileName = getFileName();
//                					if(fileName == null)
//                						fileName = "";
//                					config.url += "&fileName=" + fileName;
//                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				case "editKoufentongji": {
//                					var fileName = getFileName();
//                					if(fileName == null)
//                						fileName = "";
//                					config.url += "&fileName=" + fileName;
//                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				
                				case "addKaoheresult":{
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				case "editKaoheresult": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}
                				
                				case "editProjectmanagement": {
                					window.location.reload();
                					break;
                				}
                				
                				case "addProjectmanagement": {
                					window.location.reload();
                					break;
                				}
                				
                				
                				case "editPerson":{
                					var IDCard = forms.getForm().findField('IdentityNo');
                    				var IDCardtext = IDCard.getValue();
                    			
                    				
                    				
                    				var pwd = forms.getForm().findField('UserPwd');
                    				pwd.setValue( IDCardtext.substring(IDCardtext.length-6, IDCardtext.length) );
                    				//Ext.Msg.alert('警告',pwd.getValue());
                					break;
                				}
                				
                				
                				/*case "editProjectmanagement": {
                					var fileName = getFileName();
                					if(fileName == null)
                						fileName = "";
                					config.url += "&fileName=" + fileName;
                					uploadPanel.store.removeAll();
                					break;
                				}*/
                				
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
      	                	if (myflag==false) 
      	                	{
                				Ext.Msg.alert('警告','请先点击上传按钮上传附件！');
      	                	}
                    		if(config.action == "addProject" || config.action == "editProject")
                    		{
                    			var brief = forms.getForm().findField('BuildContent');
                    			var brieftext = brief.getValue();
                    			if(brieftext.length>500)
                    			{
                    				Ext.Msg.alert('警告','专利简介不能超过500个字符！');
                    			}
                    			else
                    			{
                    				Ext.Msg.alert('警告','请完善信息！');
                    			}
                    		}
      	                	else if(config.action == "addProjectperson" || config.action == "editProjectperson")
                    		{
                    			var brief = forms.getForm().findField('Duty');
                    			var brieftext = brief.getValue();
                    			if(brieftext.length>500)
                    			{
                    				Ext.Msg.alert('警告','职责不能超过500个字符！');
                    			}
                    			else
                    			{
                    				Ext.Msg.alert('警告','请完善信息！');
                    			}
                    		}
      	                	else
      	                	{
      	                		Ext.Msg.alert('警告','请完善信息！！！');
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
                		if (config.action == "editProject") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                		if (config.action == "editProjectperson") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        if (config.action == "editPerson") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        if (config.action == "editfb") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        if (config.action == "editProjectmanagement") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        if (config.action == "editPersondb") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        if (config.action == "editSaftyproblem") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                        if (config.action == "editKoufen") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }if (config.action == "editKoufentongji") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }if (config.action == "editKaoheresult") 
                        {
                            forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
                        }
                	}
                }],
                renderTo: Ext.getBody()
        	});// forms定义结束
        	
            if (config.action == 'editProject') 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == "editProjectperson") 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == "editPerson") 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }

            if (config.action == "editfb") 
            {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
			
            if (config.action == "editProjectmanagement") 
            {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            
            if (config.action == "editPersondb") 
            {
                 forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == "editSaftyproblem") 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == "editKoufen") 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == "editKoufentongji") 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            if (config.action == "editKaoheresult") 
            {
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据   
            }
            
        };       
        
        //创建装载formPanel的窗体，由工具栏按钮点击显示
        var showWin = function (config) {
        	var width = 420;
        	var height = 320;
        	
        	if(title == '项目概况')
        	{//编辑提案信息框
        		width = 1000;
        		height = 400;
        	}
        	
        	if(title == '项目部人员配置' || title == '用户管理')
        	{//编辑提案信息框
        		width = 800;
        		height = 300;
        	}
        	
           if(title == '设计分包单位' || title == '土建分包单位' || title == '安装分包单位' || title == '调试分包单位')
        	{//编辑提案信息框
        		width = 1000;
        		height = 500;
        	}
        	
        	if(title == '项目管理')
          	{//编辑提案信息框
          		width = 1000;
          		height = 500;
          	}
          	
          	if(title == '项目人员库')
          	{//编辑提案信息框
          		width = 1000;
          		height = 400;
          	}
          	else if(title == '扣分策略'){
          	    width = 500;
          		height = 400;
          	}
          	else if(title == '扣分统计'){
          	    width = 600;
          		height = 400;
          	}else if(title == '考核结果'){
          	    width = 1000;
          		height = 600;
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
        
        
        var Editor_Sec = [{
	    	fieldLabel: "旧密码",		//编号框用于绑定数据ID，隐藏不显示
	        name: "OldSc",
	        inputType: 'password', 
	        labelAlign: 'right',
	        afterLabelTextTpl: required,
	        allowBlank:false
        },{
        	fieldLabel:"新密码",
        	name:"NewSc",   
        	inputType: 'password', 
        	labelAlign: 'right',
        	afterLabelTextTpl: required,
        	allowBlank:false
	    },{
	    	fieldLabel:"确认新密码",
        	name:"DNewSc",
        	inputType: 'password', 
        	labelAlign: 'right',
        	afterLabelTextTpl: required,
        	allowBlank:false
	    }]
        
        
        if(tableID == 255)
        	 {
        	 UpdatePwd();
        	 }else {
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
        				btnResetPdbPwd.enable();
        				btnResetPersonPwd.enable();
        			}
        			else
        			{
        				btnEdit.disable();
        				btnScan.disable();
        				btnResetPdbPwd.disable();
        				btnResetPersonPwd.disable();
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
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td width=\"35%\">" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">项目名称</td><td>" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">项目编号</td><td>" + record.get('No') + "</td><td style=\"padding:5px;\">项目简称</td><td>" + record.get('Scale') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">建设单位</td><td>" + record.get('BuildUnit') + "</td><td style=\"padding:5px;\">项目地点</td><td>" + record.get('Place') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">合同总价（万元）</td><td>" + record.get('Price') + "</td><td style=\"padding:5px;\">项目经理</td><td>" + record.get('Manager') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">开工日期</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('StartDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">合同工期</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Schedule') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">项目概况</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Content') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">建安费用（万元）</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Cost') + "</td></tr>";

             		         
             		        //html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
             		        
            		        
            		        
             		         html_str += "</table>";
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
                	
                	else if(title == '项目管理')
                	{
                		var html_str = "";
                		if(title == '项目管理')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Name')+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">项目名称</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">项目编号</td><td>" + record.get('No') + "</td><td style=\"padding:5px;\">项目简称</td><td>" + record.get('Scale') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">建设单位</td><td>" + record.get('BuildUnit') + "</td><td style=\"padding:5px;\">项目地点</td><td>" + record.get('Place') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">合同总价（万元）</td><td>" + record.get('Price') + "</td><td style=\"padding:5px;\">项目经理</td><td>" + record.get('Manager') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">开工日期</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('StartDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">合同工期</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Schedule') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">项目概况</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Content') + "</td></tr>\
								\<tr><td style=\"padding:5px;\">施工进度</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Progress') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">建安费用（万元）</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Cost') + "</td></tr>";
             		         
         
            		        
             		         html_str += "</table>";
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
                	
                	else if(title == '设计分包单位' || title == '土建分包单位' || title == '安装分包单位' || title == '调试分包单位')
                	{
                		var html_str = "";
                		if(title == '设计分包单位' || title == '土建分包单位' || title == '安装分包单位' || title == '调试分包单位')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Type')+"</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">分包单位负责人</td ><td>" + record.get("Head") +
             		         "</td><td width=\"15%\" style=\"padding:5px;\">技术负责人</td><td width=\"40%\">" + record.get("TechHead") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">项目负责人</td><td>" + record.get('ProHead') + "</td><td style=\"padding:5px;\">项目技术负责人</td><td>" + record.get('ProTechHead') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">项目安全负责人</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('ProSaveHead') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">分包费用</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Cost') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">业务范围</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('ProRange') + "</td></tr>";
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
                	
                	
                	else if(title == '分包单位汇总')
                	{
                		var html_str = "";
                		if(title == '分包单位汇总')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+record.get('Type')+"</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">分包单位负责人</td ><td>" + record.get("Head") +
             		         "</td><td width=\"15%\" style=\"padding:5px;\">技术负责人</td><td width=\"40%\">" + record.get("TechHead") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">项目负责人</td><td>" + record.get('ProHead') + "</td><td style=\"padding:5px;\">项目技术负责人</td><td>" + record.get('ProTechHead') + "</td></tr>\
								\<tr><td style=\"padding:5px;\">项目安全负责人</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('ProSaveHead') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">分包费用</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Cost') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">业务范围</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('ProRange') + "</td></tr>\
                             \<tr><td style=\"padding:5px;\">项目名称</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Project') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">分包单位名称</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('Type') + "</td></tr>";
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
                	
                	else if(title == '项目部人员配置')
                	{
                		var html_str = "";
                		if(title == '项目部人员配置')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+"项目部人员配置</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">姓名</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">职位</td><td>" + record.get('Job') + "</td><td style=\"padding:5px;\">职责</td><td>" + record.get('Duty') + "</td></tr>\
                              ";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
             		         html_str += "</table>";
                		}  		
                        Ext.create('Ext.window.Window', 
                        {
                           title: '查看详情',
                           titleAlign: 'center',
                           height: 200,
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
                	
                	else if(title == '考核指标')
                	{
                		var html_str = "";
                		if(title == '考核指标')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		        html_str = "<h1 style=\"padding: 5px;\"><center>"+
 		                    "详细信息</center></h1>"+
 		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
 		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
 		                    "</td><td width=\"15%\" style=\"padding:5px;\">隐患类型</td><td width=\"40%\">" + record.get("kind") + 
 		                    "</td></tr><tr><td style=\"padding:5px;\">子类</td><td>" + record.get('subkind') + 
                            "</td><td style=\"padding:5px;\">显示</td><td>" + record.get('showkind') + 
                            "</tr><tr></td><td style=\"padding:5px;\">分数</td><td  align=\"left\"  colspan=\"3\">" + record.get('score') +  "</td></tr>";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
//             		       html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
//            		        
//           		         var misfile = record.get('Accessory').split('*');
////           		        var foldermis;
//         				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
//           		         
//         				    height = 500;
//             		        width = 800;
//           		        for(var i = 2;i<misfile.length;i++){
//             		        	
//             		        	scanfileName = getScanfileName(misfile[i]); 
//             		        	displayfileName = misfile[i];
//                        		/*if(getBLen(scanfileName)>10)
//                        			displayfileName = displayfileName.substring(0,7)+"···";*/
//             		        	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
//             		        }
           		        
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
                	
                	else if(title == '项目人员库')
                	{
                		var html_str = "";
                		if(title == '项目人员库')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+title+"人员类型</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">人员类型</td ><td>" + record.get("PType") + "</td><td width=\"15%\" style=\"padding:5px;\">姓名</td><td width=\"40%\">" + record.get("Name") + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">性别</td><td>" + record.get('Sex') + "</td><td style=\"padding:5px;\">身份证号</td><td>" + record.get('IDCard') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">出生年月</td><td>" + record.get('Birthday') + "</td><td style=\"padding:5px;\">联系电话</td><td>" + record.get('Phone') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">紧急联系电话</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PhoneUrgent') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">证件编号1</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PapersNo') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">有效期至1</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PapersDate') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">持证类型1</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PapersType') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">证件编号1</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PapersNoTwo') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">有效期至2</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PapersDateTwo') + "</td></tr>\
                              " +
                              		"\<tr><td style=\"padding:5px;\">持证类型2</td><td style=\"text-align:left;padding:10px;\" colspan=\"3\">" + record.get('PapersTypeTwo') + "</td></tr>";
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
                	
                	
                	else if(title == '扣分策略')
                	{
                		var html_str = "";
                		if(title == '扣分策略')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		        html_str = "<h1 style=\"padding: 5px;\"><center>"+
 		                    "详细信息</center></h1>"+
 		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
 		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
 		                    "</td><td width=\"15%\" style=\"padding:5px;\">扣分项</td><td width=\"40%\">" + record.get("koufenx") + 
 		                    "</td></tr><tr><td style=\"padding:5px;\">扣分值</td><td>" + record.get('koufenzhi') + 
                            "</td><td style=\"padding:5px;\">扣分周期</td><td>" + record.get('koufenzq') + 
                            
                            "</td></tr><tr><td style=\"padding:5px;\">考核起点</td><td>" + record.get('kaoheqidian') + 
                            "</td><td style=\"padding:5px;\">对应库表</td><td>" + record.get('duiykub') + 
                            
                            "</td></tr><tr><td style=\"padding:5px;\">对应字段</td><td>" + record.get('duiyzid') + 
                            "</td><td style=\"padding:5px;\">包含文件</td><td>" + record.get('baohfile') +"</td></tr>";
//                            "</tr><tr></td><td style=\"padding:5px;\">分数</td><td  align=\"left\"  colspan=\"3\">" + record.get('score') +  "</td></tr>";
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
//             		       html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
//            		        
//           		         var misfile = record.get('Accessory').split('*');
////           		        var foldermis;
//         				    if(misfile.length>2)  var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
//           		         
//         				    height = 500;
//             		        width = 800;
//           		        for(var i = 2;i<misfile.length;i++){
//             		        	
//             		        	scanfileName = getScanfileName(misfile[i]); 
//             		        	displayfileName = misfile[i];
//                        		/*if(getBLen(scanfileName)>10)
//                        			displayfileName = displayfileName.substring(0,7)+"···";*/
//             		        	html_str = html_str + displayfileName+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+scanfileName+"\" target=\"_blank\">预览&nbsp;&nbsp;</a><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载&nbsp;&nbsp;</a></div><br>";
//             		        }
//           		        
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
                	
                	
                	else if(title == '扣分统计')
                	{
                		var html_str = "";
                		if(title == '扣分统计')
                		{
                			var record = dataStore.getAt(rowIndex);
                			
                			var koufenitem = record.get('koufenitem');
                			var items = koufenitem.split(", ");
                			koufenitem = "";
                			for(var i=0;i<items.length;i++){
                				items[i] = i+1 + ". " + items[i] + "<br/>";
                				koufenitem = koufenitem + items[i];
                			}
                			
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		        html_str = "<h1 style=\"padding: 5px;\"><center>"+
 		                    "详细信息</center></h1>"+
 		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
 		                    "<tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + 
 		                    "</td><td width=\"15%\" style=\"padding:5px;\">项目名称</td><td width=\"40%\">" + record.get("proname") + 
 		                    "</td></tr><tr><td style=\"padding:5px;\">扣分值</td><td>" + record.get('koufenzhi') + 
                            "</td><td style=\"padding:5px;\">总分</td><td>" + record.get('zongfen') + 
                            "</tr><tr></td><td style=\"padding:5px;\">扣分项</td><td  align=\"left\"  colspan=\"3\">" + koufenitem +  "</td></tr>";
             		         html_str += "</table>";
                		}  		
                        Ext.create('Ext.window.Window', 
                        {
                           title: '查看详情',
                           titleAlign: 'center',
                           height: 550,
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
                	
                	
               
                	if(title == '考核结果')
                	{
//                		alert(title);
                		var html_str = "";
                		if(title == '考核结果')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+
             		                    "详细信息</center></h1>"+
             		                    "<table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\">"+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">年份</td ><td>" + record.get("year") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">月份</td><td width=\"40%\">" + record.get("month")+
             		                    "<tr><td width=\"15%\" style=\"padding:5px;\">考核得分</td ><td>" + record.get("score") + 
             		                    "</td><td width=\"15%\" style=\"padding:5px;\">考评得分原因理由</td><td width=\"40%\">" + record.get("reason")+ "</td></tr>";
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
    }
    
    
    
});