var tn;

Ext.define('GoalDutyGrid', {
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
        var projectName = config.projectName;
        var param;	//存放gridDT选择的行
        var fileUploadPanel;
        
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
       	
       	
       	var store_Yearplan = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'Year'},
        	         { name: 'Workload'},
        	         { name: 'Completed'},
        	         { name: 'Content'},
        	         { name: 'Manager'},
        	         { name: 'PlanDate'},
        	         { name: 'RealDate'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getYearplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	/*var store_Yearplansum = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'Year'},
        	         { name: 'Workload'},
        	         { name: 'Completed'},
        	         { name: 'Content'},
        	         { name: 'Manager'},
        	         { name: 'PlanDate'},
        	         { name: 'RealDate'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getYearplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + ""),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });*/
       	
//jianglf---------------------------------------------------------------------------------
       	var store_Safetypromanagement = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'Name'},
        	         { name: 'Time'},
        	         { name: 'Person'},
        	         { name: 'Sperson'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getSafetypromanagementListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var store_Safetypromanagementfb = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'Name'},
        	         { name: 'FbName'},
        	         { name: 'Time'},
        	         { name: 'Person'},
        	         { name: 'Sperson'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getSafetypromanagementfbListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
       	var store_Threeworkplan = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'No'},
        	         { name: 'Year'},
        	         { name: 'Workload'},
        	         { name: 'Completed'},
        	         { name: 'Content'},
        	         { name: 'Manager'},
        	         { name: 'PlanDate'},
        	         { name: 'RealDate'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getThreeworkplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
    	/*var store_Threeworkplansum = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'No'},
        	         { name: 'Year'},
        	         { name: 'Workload'},
        	         { name: 'Completed'},
        	         { name: 'Content'},
        	         { name: 'Manager'},
        	         { name: 'PlanDate'},
        	         { name: 'RealDate'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getThreeworkplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + ""),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });*/
        
        var store_Threeworkplansum = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'FileName'},
					 { name: 'Year'},
					 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '三项业务年度目标分解及工作计划' + "&projectName=" + ""),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json'
				}
			}
		});
        
//end-------------------------------------------------------------------------------------       	
       	
     	var store_Monthplan = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'No'},
        	         { name: 'Year'},
        	         { name: 'Month'},
        	         { name: 'Workload'},
        	         { name: 'Completed'},
        	         { name: 'Content'},
        	         { name: 'Manager'},
        	         { name: 'PlanDate'},
        	         { name: 'RealDate'},
        	         { name: 'Unit'},
        	         { name: 'Completedsm'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getMonthplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
     	
     	/*var store_Monthplansum = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'No'},
        	         { name: 'Year'},
        	         { name: 'Month'},
        	         { name: 'Workload'},
        	         { name: 'Completed'},
        	         { name: 'Content'},
        	         { name: 'Manager'},
        	         { name: 'PlanDate'},
        	         { name: 'RealDate'},
        	         { name: 'Unit'},
        	         { name: 'Completedsm'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getMonthplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + ""),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });*/
        
        
        var store_Monthplansum = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'FileName'},
					 { name: 'Year'},
					 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '月度工作计划' + "&projectName=" + ""),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json'
				}
			}
		});	
		
		var store_Yearplansum = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'FileName'},
					 { name: 'Year'},
					 { name: 'ProjectName'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '年度工作计划' + "&projectName=" + ""),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json'
				}
			}
		});	
        
     	
     	var store_Fbplan = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'No'},
        	         { name: 'Name'},
        	         { name: 'PlanName'},
        	         { name: 'Date'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
                     
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getFbplanListDef?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
       	
		
		var store_securityplan = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
                     { name: 'Accessory'},
                     { name: 'Title'},
                     { name: 'Timeyear'},
                     { name: 'Fname'},
                     { name: 'Filename'},
                     { name: 'Time'},
                     { name: 'UserName'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getSecurityplanListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        
        var store_anweihui = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'Esorad'},
        	         { name: 'Time'},
        	         { name: 'Head'},
        	         { name: 'ViceHead'},
        	         { name: 'Form'},
        	         { name: 'Agency'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getAnweihuiListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        var store_Saveprodbook = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'Type'},
        	         { name: 'TimeYear'},
        	         { name: 'ToTarget'},
        	         { name: 'ProjectName'},
                     { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getSaveprodbookListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });

//jiang-----------------------------------------------------------------------        
        var store_saveproduct = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'EvaluateRate'},
        	         { name: 'TrainRate'},
        	         { name: 'ReformRate'},
        	         { name: 'ReachRate'},
        	         { name: 'ExamineRate'},
        	         { name: 'EnviPassRate'},
        	         { name: 'BottomRate'},
        	         { name: 'SickPassRate'},
        	         { name: 'CheckRate'},
        	         { name: 'WorkAcci'},
        	         { name: 'ProdAcci'},
        	         { name: 'AcciRate'},
        	         { name: 'FenBaoAcci'},
        	         { name: 'Disaster'},
        	         { name: 'FireAcci'},
        	         { name: 'JobEvent'},
        	         { name: 'PollutEvent'},
        	         { name: 'Behave'},
        	         { name: 'TimeYear'},     	         
                     { name: 'Accessory'},
                    // { name: 'Management'},
                     //{ name: 'Contrl'},
                     { name: 'Type'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getSaveproductListDef?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title+ "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        var store_saveproductzt = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'EvaluateRate'},
        	         { name: 'TrainRate'},
        	         { name: 'ReformRate'},
        	         { name: 'ReachRate'},
        	         { name: 'ExamineRate'},
        	         { name: 'EnviPassRate'},
        	         { name: 'BottomRate'},
        	         { name: 'SickPassRate'},
        	         { name: 'CheckRate'},
        	         { name: 'WorkAcci'},
        	         { name: 'ProdAcci'},
        	         { name: 'AcciRate'},
        	         { name: 'FenBaoAcci'},
        	         { name: 'Disaster'},
        	         { name: 'FireAcci'},
        	         { name: 'JobEvent'},
        	         { name: 'PollutEvent'},
        	         { name: 'Behave'},     	         
                     { name: 'Accessory'},
                    // { name: 'Management'},
                     //{ name: 'Contrl'},
                     { name: 'Type'},
                     { name: 'ProjectName'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getSaveproductListDef?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title +"&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        var store_saveproduct1 = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'EvaluateRate'},
        	         { name: 'TrainRate'},
        	         { name: 'ReformRate'},
        	         { name: 'ReachRate'},
        	         { name: 'ExamineRate'},
        	         { name: 'EnviPassRate'},
        	         { name: 'BottomRate'},
        	         { name: 'SickPassRate'},
        	         { name: 'CheckRate'},
        	         { name: 'WorkAcci'},
        	         { name: 'ProdAcci'},
        	         { name: 'AcciRate'},
        	         { name: 'FenBaoAcci'},
        	         { name: 'Disaster'},
        	         { name: 'FireAcci'},
        	         { name: 'JobEvent'},
        	         { name: 'PollutEvent'},
        	         { name: 'Behave'},
        	         { name: 'TimeYear'},     	         
                     { name: 'Accessory'},
                     { name: 'Management'},
                     { name: 'Contrl'},
                     { name: 'Type'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getSaveproductListDef?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
 //end---------------------------------------------------------------       
        var store_Saveproplan = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'Anweihui'},
        	         { name: 'ThreeGroup'},
        	         { name: 'FourBuild'},
        	         { name: 'SaveBuild'},
        	         { name: 'SavePlan'},
        	         { name: 'SaveCheck'},
        	         { name: 'BuildPlan'},
        	         { name: 'HandlePlan'},
        	         { name: 'SaveBuildPlan'},
        	         { name: 'DangerPublic'},
        	         { name: 'ExecutePlan'},
        	         { name: 'WorkPlan'},
        	         { name: 'ProjectName'},
        	         { name: 'Accessory'}
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getSaveproplanListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + projectName),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        var store_goaldecom = Ext.create('Ext.data.Store', {
        	fields: [
        	         { name: 'ID'},
        	         { name: 'Content'},
        	         { name: 'Mvalue'},
        	         { name: 'Measure'},
        	         { name: 'Time'},
        	         { name: 'Manager'},
        	         { name: 'Completed'},
        	         { name: 'ProjectName'},
        	         { name: 'Accessory'}
        	         /*{ name: 'Engineer'},
        	         { name: 'Affair'},
        	         { name: 'Buy'},
        	         { name: 'MakeOne'},
        	         { name: 'ExamineOne'},
        	         { name: 'AgreeOne'},
        	         { name: 'SignOne'}*/
            ],
            pageSize: psize,  //页容量20条数据
            proxy: {
                type: 'ajax',
                url: encodeURI('GoalDutyAction!getGoaldecomListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title+ "&projectName=" + projectName ),
                reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                    type: 'json', //返回数据类型为json格式
                    root: 'rows',  //数据
                    totalProperty: 'total' //数据总条数
                }
            },
            autoLoad: true //即时加载数据
        });
        
        var store_goaldecomsum = Ext.create('Ext.data.Store', {
			fields: [
					 { name: 'FileName'},
					 { name: 'Year'},
					 { name: 'ProjectName'},
					 { name: 'Accessory'}
			],
			pageSize: psize,  //页容量20条数据
			proxy: {
				type: 'ajax',
				url: encodeURI('GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '年度目标分解' + "&projectName=" + ""),
				reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
					type: 'json'
				}
			}
		});	
        
//jianglf-----------------------------------------------------        
        var items_Yearplan = [{
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
                	xtype : 'combo',
                	fieldLabel : '年份',
                    labelAlign: 'right',
                    name : 'Year',  
                    anchor : '95%', 
                	queryMode : 'local',
                	editable : false,
                	store : new Ext.data.ArrayStore({
                	fields : ['id','name'],
                	data : []
                	}),
                	valueField : 'name',
                	displayField : 'id',
                	triggerAction : 'all',
                	autoSelect : true,
                	listeners : {
                	beforerender :  function(){
                	var newyear = parseInt(Ext.Date.format(new Date(),'Y'));//这是为了取现在的年份数
                	var yearlist = [];
                	for(var i = (newyear+1);i>=(newyear-1);i--){
                	yearlist.push([i,i]);
                	}
                	this.store.loadData(yearlist);
                	}
                	}
                
                },{
          				xtype:"datefield",
	                fieldLabel: '计划完成时间',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'PlanDate',
	                anchor:'95%',
	                allowBlank: false 
                },{
                	xtype:'textfield',
                    fieldLabel: '相关单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Workload'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '责任部门',
                    afterLabelTextTpl: required,
                    allowBlank: false ,
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Manager'
                },{
                	xtype:"datefield",
	                fieldLabel: '实际完成时间',
	               // afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'RealDate',
	                anchor:'95%',
	                allowBlank: true 
                }]
	        }]
	    },{
            xtype:'textarea',
            fieldLabel: '计划内容',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            afterLabelTextTpl: required,
            allowBlank: false ,
            maxLength:500,
            anchor:'100%',
            name: 'Content'
	    },{
            xtype:'textarea',
            fieldLabel: '完成情况说明',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Completed'
	    },
        	uploadPanel
	    ]
        

        var items_Safetypromanagement = [{
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
                    fieldLabel: '机构名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                
                },
                	{
                	xtype:'textfield',
                    fieldLabel: '负责人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Person'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:"datefield",
	                fieldLabel: '成立时间',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'100%',
	                allowBlank: false 
                },{
                	xtype:'textfield',
                    fieldLabel: '安全管理人员',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Sperson'
                }]
	        }]
	    },
        	uploadPanel
	    ]
        
        var items_Safetypromanagementfb = [{
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
                    fieldLabel: '机构名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Name'
                
                },{
                	xtype:'textfield',
                    fieldLabel: '负责人',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Person'
                },{
                	xtype:'textfield',
                    fieldLabel: '安全管理人员',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Sperson'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:"datefield",
	                fieldLabel: '成立时间',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'100%',
	                allowBlank: false 
                },{
                	xtype:'textfield',
                    fieldLabel: '分包单位名称',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'FbName'
                }]
	        }]
	    },
        	uploadPanel
	    ]
        
        var items_Threeworkplan = [{
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
                	xtype : 'combo',
                	fieldLabel : '年份',
                    labelAlign: 'right',
                    name : 'Year',  
                    anchor : '95%', 
                	queryMode : 'local',
                	editable : false,
                	store : new Ext.data.ArrayStore({
                	fields : ['id','name'],
                	data : []
                	}),
                	valueField : 'name',
                	displayField : 'id',
                	triggerAction : 'all',
                	autoSelect : true,
                	listeners : {
                	beforerender :  function(){
                		var newyear = parseInt(Ext.Date.format(new Date(),'Y'));//这是为了取现在的年份数
                    	var yearlist = [];
                    	for(var i = (newyear+1);i>=(newyear-1);i--){
                	yearlist.push([i,i]);
                	}
                	this.store.loadData(yearlist);
                	}
                	}
                
                },{
          				xtype:"datefield",
	                fieldLabel: '计划完成时间',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'PlanDate',
	                anchor:'95%',
	                allowBlank: false 
                },{
                	xtype:'textfield',
                    fieldLabel: '相关单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Workload'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '责任部门',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    afterLabelTextTpl: required,
                    allowBlank: false ,
                    name: 'Manager'
                },{
                	xtype:"datefield",
	                fieldLabel: '实际完成时间',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'RealDate',
	                anchor:'95%',
	                allowBlank: true 
                }]
	        }]
	    },{
            xtype:'textarea',
            fieldLabel: '计划内容',
            //labelWidth: 120,
            labelAlign: 'right',
            afterLabelTextTpl: required,
            allowBlank: false ,
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Content'
	    },{
            xtype:'textarea',
            fieldLabel: '完成情况说明',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Completed'
	    },
        	uploadPanel
	    ]
//end-------------------------------------------------------------------------        
        
        var items_Monthplan = [{
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
                	xtype : 'combo',
                	fieldLabel : '年份',
                    labelAlign: 'right',
                    name : 'Year',  
                    anchor : '95%', 
                	queryMode : 'local',
                	editable : false,
                	store : new Ext.data.ArrayStore({
                	fields : ['id','name'],
                	data : []
                	}),
                	valueField : 'name',
                	displayField : 'id',
                	triggerAction : 'all',
                	autoSelect : true,
                	listeners : {
                	beforerender :  function(){
                		var newyear = parseInt(Ext.Date.format(new Date(),'Y'));//这是为了取现在的年份数
                    	var yearlist = [];
                    	for(var i = (newyear+1);i>=(newyear-1);i--){
                	yearlist.push([i,i]);
                	}
                	this.store.loadData(yearlist);
                	}
                	}
                
                },{
          				xtype:"datefield",
	                fieldLabel: '计划完成时间',
	                afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'PlanDate',
	                anchor:'95%',
	                allowBlank: false 
                },{
                	xtype:'textfield',
                    fieldLabel: '责任部门',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    afterLabelTextTpl: required,
                    allowBlank: false ,
                    name: 'Manager'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype : 'combo',  
                    fieldLabel : '月份',
                    labelAlign: 'right',
                    name : 'Month',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['1月', '1月'],  
                                        ['2月', '2月'],  
                                        ['3月', '3月'],  
                                        ['4月', '4月'],  
                                        ['5月', '5月'],  
                                        ['6月', '6月'],  
                                        ['7月', '7月'],  
                                        ['8月', '8月'],
                                        ['9月', '9月'],
                                        ['10月', '10月'],
                                        ['11月', '11月'],
                                        ['12月', '12月']]  
                            }),  
                    editable : false,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype:"datefield",
	                fieldLabel: '实际完成时间',
	                //afterLabelTextTpl: required,
	                //labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'RealDate',
	                anchor:'95%',
	                allowBlank: true 
                },{
                	xtype:'textfield',
                    fieldLabel: '相关单位',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Unit'
                }]
	        }]
	    },{
            xtype:'textarea',
            fieldLabel: '计划内容',
            //labelWidth: 120,
            labelAlign: 'right',
            afterLabelTextTpl: required,
            allowBlank: false ,
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Content'
	    },{
            xtype:'textarea',
            fieldLabel: '完成情况说明',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Completedsm'
	    },
        	uploadPanel
	    ]
        
        var items_Fbplan = [{
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
	            	xtype:"datefield",
	                fieldLabel: '报备时间',
	                afterLabelTextTpl: required,
	                labelWidth: 100,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Date',
	                anchor:'95%',
	                allowBlank: false 
                },{
                	xtype:'textfield',
                    fieldLabel: '安全生产计划',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Accessory',
                   	hidden: true,
                    hiddenLabel: true
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
                        fieldLabel: '分包单位名称',
                        //labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'100%',
                        name: 'Name'
                    },{
                    	xtype:'textfield',
                        fieldLabel: '安全生产计划名称',
                        //labelWidth: 120,
                        labelAlign: 'right',
                        anchor:'100%',
                        name: 'PlanName'
//                        listeners: {
//                        	'focus':AddZB
//                        }
                    }]
    	    }]
	        }]
	    },
        	uploadPanel
        ]
        
        
	   
        var items_securityplan = [{
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
                }]
	        }]
	    },
        	uploadPanel
        ]
        
        var items_securityplan1st = [{
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
                	xtype : 'combo',
                	fieldLabel : '年份',
                    labelAlign: 'right',
                    name : 'Timeyear',  
                    anchor : '95%', 
                	queryMode : 'local',
                	editable : false,
                	store : new Ext.data.ArrayStore({
                	fields : ['id','name'],
                	data : []
                	}),
                	valueField : 'name',
                	displayField : 'id',
                	triggerAction : 'all',
                	autoSelect : true,
                	listeners : {
                	beforerender :  function(){
                		var newyear = parseInt(Ext.Date.format(new Date(),'Y'));//这是为了取现在的年份数
                    	var yearlist = [];
                    	for(var i = newyear+1;i>=(newyear-1);i--){
                	yearlist.push([i,i]);
                	}
                	this.store.loadData(yearlist);
                	}
                	}
                
                },{
                	xtype:'textfield',
                    fieldLabel: '创建方案名称',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Fname'
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
                }]
	        }]
	    },
        	uploadPanel
        ]
        
        var items_securityplan2nd = [{
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
                	xtype : 'combo',
                	fieldLabel : '年份',
                    labelAlign: 'right',
                    name : 'Timeyear',  
                    anchor : '95%', 
                	queryMode : 'local',
                	editable : false,
                	store : new Ext.data.ArrayStore({
                	fields : ['id','name'],
                	data : []
                	}),
                	valueField : 'name',
                	displayField : 'id',
                	triggerAction : 'all',
                	autoSelect : true,
                	listeners : {
                	beforerender :  function(){
                		var newyear = parseInt(Ext.Date.format(new Date(),'Y'));//这是为了取现在的年份数
                    	var yearlist = [];
                    	for(var i = newyear+1;i>=(newyear-1);i--){
                	yearlist.push([i,i]);
                	}
                	this.store.loadData(yearlist);
                	}
                	}
                
                },{
                	xtype:'textfield',
                    fieldLabel: '绩效评定报告名称',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Fname'
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
                }]
	        }]
	    },
        	uploadPanel
        ]
        
         var items_securityplan3rd = [{
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
                	xtype : 'combo',
                	fieldLabel : '年份',
                    labelAlign: 'right',
                    name : 'Timeyear',  
                    anchor : '95%', 
                	queryMode : 'local',
                	editable : false,
                	store : new Ext.data.ArrayStore({
                	fields : ['id','name'],
                	data : []
                	}),
                	valueField : 'name',
                	displayField : 'id',
                	triggerAction : 'all',
                	autoSelect : true,
                	listeners : {
                	beforerender :  function(){
                		var newyear = parseInt(Ext.Date.format(new Date(),'Y'));//这是为了取现在的年份数
                    	var yearlist = [];
                    	for(var i = newyear+1;i>=(newyear-1);i--){
                	yearlist.push([i,i]);
                	}
                	this.store.loadData(yearlist);
                	}
                	}
                
                },{
                	xtype:'textfield',
                    fieldLabel: '自评方案名称',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Fname'
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
                }]
	        }]
	    },
        	uploadPanel
        ]
        
        var items_securityplan4th = [{
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
                	xtype : 'combo',
                	fieldLabel : '年份',
                    labelAlign: 'right',
                    name : 'Timeyear',  
                    anchor : '95%', 
                	queryMode : 'local',
                	editable : false,
                	store : new Ext.data.ArrayStore({
                	fields : ['id','name'],
                	data : []
                	}),
                	valueField : 'name',
                	displayField : 'id',
                	triggerAction : 'all',
                	autoSelect : true,
                	listeners : {
                	beforerender :  function(){
                		var newyear = parseInt(Ext.Date.format(new Date(),'Y'));//这是为了取现在的年份数
                    	var yearlist = [];
                    	for(var i = newyear+1;i>=(newyear-1);i--){
                	yearlist.push([i,i]);
                	}
                	this.store.loadData(yearlist);
                	}
                	}
                
                },{
                	xtype:'textfield',
                    fieldLabel: '自评报告名称',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Fname'
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
                }]
	        }]
	    },
        	uploadPanel
        ]
        
        
        var items_anweihui = [{
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
                	xtype:'combo',
                    fieldLabel: '安委会成立/调整',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    store:["安委会成立","安委会调整"],
                    name: 'Esorad',
                    value:'安委会成立'
                },{
                	xtype:"datefield",
	                fieldLabel: '成立/调整时间',
	                afterLabelTextTpl: required,
	                labelWidth: 120,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'95%',
	                allowBlank: false   
                },{
	            	xtype:'textfield',
                    fieldLabel: '安委会主任',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Head',
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
                    fieldLabel: '安委会副主任',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ViceHead'
                },{
                	xtype:'textfield',
                    fieldLabel: '成员组成',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Form'
                },{
                	xtype:'textfield',
                    fieldLabel: '工作机构',
                    labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Agency'
                }]
	        }]
	    },
        	uploadPanel]
        	
        	
        var items_Saveprodbook = [{
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
                    labelWidth: 200,
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
	            	xtype:'combo',
                    fieldLabel: '责任书类别（院级、项目级）',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    store:["院级","项目级"],
                    name: 'Type',
                    value:'院级'
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
                	xtype : 'combo',
                	fieldLabel : '年份',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'TimeYear',  
                    anchor : '95%', 
                	queryMode : 'local',
                	editable : false,
                	store : new Ext.data.ArrayStore({
                	fields : ['id','name'],
                	data : []
                	}),
                	valueField : 'name',
                	displayField : 'id',
                	triggerAction : 'all',
                	autoSelect : true,
                	listeners : {
                	beforerender :  function(){
                	var newyear = Ext.Date.format(new Date(),'Y');//这是为了取现在的年份数
                	var nyear = parseInt(newyear);
                	var yearlist = [];
                	for(var i = nyear+1;i>=(nyear-1);i--){
                	yearlist.push([i,i]);
                	}
                	this.store.loadData(yearlist);
                	}
                	}
                },{
                	xtype:'textfield',
                    fieldLabel: '签订对象（设计院、分包单位名称）',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ToTarget'
                }]
	        }]
	    },
        	uploadPanel]
        	
//jianglf-----------------------------------------------        	
		var items_saveproduct = [{
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
                    fieldLabel: 'Type',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Type',
                    valut:title,
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype : 'combo',
                	fieldLabel : '目标年份',
                    labelAlign: 'right',
                    name : 'TimeYear',  
                    anchor : '95%', 
                	queryMode : 'local',
                	editable : false,
                	store : new Ext.data.ArrayStore({
                	fields : ['id','name'],
                	data : []
                	}),
                	valueField : 'name',
                	displayField : 'id',
                	triggerAction : 'all',
                	autoSelect : true,
                	listeners : {
                	beforerender :  function(){
                	var newyear = parseInt(Ext.Date.format(new Date(),'Y'));//这是为了取现在的年份数
                	var yearlist = [];
                	for(var i = newyear+1;i>=(newyear-1);i--){
                	yearlist.push([i,i]);
                	}
                	this.store.loadData(yearlist);
                	}
                	}                
	 	         },
                	{
                	xtype:'textfield',
                    fieldLabel: '项目安全评估率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'EvaluateRate'
                },{
                	xtype:'textfield',
                    fieldLabel: '员工（包括分包单位）岗前安全培训、操作技能培训率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'TrainRate'
                },{
                	xtype:'textfield',
                    fieldLabel: '安全生产隐患限期整改率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ReformRate'
                },{
                	xtype:'textfield',
                    fieldLabel: '项目安全总监到位率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ReachRate'
                },{
                	xtype:'textfield',
                    fieldLabel: '建设项目的重点部位和关键环节安全措施的审批及交底率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ExamineRate'
                },{
                	xtype:'textfield',
                    fieldLabel: '工作场所环境合格率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'EnviPassRate'
                },{
                	xtype:'textfield',
                    fieldLabel: '危险性较大分部、分项工程专项安全技术措施编制、审批、交底率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'BottomRate'
                },{
                	xtype:'textfield',
                    fieldLabel: '工作场所职业病危害告知率、职业病危害因素监测率、主要危害因素监测合格率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'SickPassRate'
                },{
                	xtype:'textfield',
                    fieldLabel: '从事接触职业病危害作业劳动者的职业健康体检率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'CheckRate'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:'textfield',
                    fieldLabel: '因工程勘察设计原因造成的人身伤亡事故和较大财产损失事故',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'WorkAcci'
                },{
                	xtype:'textfield',
                    fieldLabel: '生产性轻伤、重伤和死亡责任事故',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'ProdAcci'
                },{
                	xtype:'textfield',
                    fieldLabel: '轻伤事故率',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'AcciRate'
                },{
                	xtype:'textfield',
                    fieldLabel: '分包单位生产性重伤和死亡责任事故',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'FenBaoAcci'
                },{
                	xtype:'textfield',
                    fieldLabel: '负主责的一般及以上交通事故、负管理责任的建设项目地质灾害',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Disaster'
                },{
                	xtype:'textfield',
                    fieldLabel: '一般及以上设备事故、直接经济损失20万元/次及以上的火灾事故',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'FireAcci'
                },{
                	xtype:'textfield',
                    fieldLabel: '职业病危害事件',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'JobEvent'
                },{
                	xtype:'textfield',
                    fieldLabel: '负管理责任的环境污染事件',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'PollutEvent'
                },{
                	xtype:'textfield',
                    fieldLabel: '事故瞒报、谎报、拖延不报行为',
                    labelWidth: 200,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Behave'
                }]
	        }]
	    },
        	uploadPanel
        ]
        
        var items_saveproductzt = [{
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
                    fieldLabel: 'Type',
                    //labelWidth: 120,
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Type',
                   	hidden: true,
                    hiddenLabel: true
                },{
                	xtype : 'combo',  
                    fieldLabel : '项目安全评估率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'EvaluateRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['100%', '100%'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '员工（包括分包单位）岗前安全培训、操作技能培训率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'TrainRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['100%', '100%'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '安全生产隐患限期整改率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'ReformRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['100%', '100%'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '项目安全总监到位率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'ReachRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['100%', '100%'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '建设项目的重点部位和关键环节安全措施的审批及交底率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'ExamineRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['100%', '100%'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '工作场所环境合格率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'EnviPassRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['100%', '100%'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '危险性较大分部、分项工程专项安全技术措施编制、审批、交底率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'BottomRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['100%', '100%'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '工作场所职业病危害告知率、职业病危害因素监测率、主要危害因素监测合格率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'SickPassRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['100%', '100%'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '从事接触职业病危害作业劳动者的职业健康体检率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'CheckRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['100%', '100%'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype : 'combo',  
                    fieldLabel : '因工程勘察设计原因造成的人身伤亡事故和较大财产损失事故',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'WorkAcci',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['不发生', '不发生'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '生产性轻伤、重伤和死亡责任事故',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'ProdAcci',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['不发生', '不发生'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '轻伤事故率',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'AcciRate',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['不发生', '不发生'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '分包单位生产性重伤和死亡责任事故',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'FenBaoAcci',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['不发生', '不发生'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '负主责的一般及以上交通事故、负管理责任的建设项目地质灾害',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'Disaster',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['不发生', '不发生'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '一般及以上设备事故、直接经济损失20万元/次及以上的火灾事故',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'FireAcci',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['不发生', '不发生'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '职业病危害事件',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'JobEvent',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['不发生', '不发生'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '负管理责任的环境污染事件',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'PollutEvent',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['不发生', '不发生'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                },{
                	xtype : 'combo',  
                    fieldLabel : '事故瞒报、谎报、拖延不报行为',
                    labelAlign: 'right',
                    labelWidth: 200,
                    name : 'Behave',  
                    anchor : '95%',  
                    mode : 'local',  
                    store : new Ext.data.SimpleStore({  
                                fields : ['value', 'text'],  
                                data : [['不发生', '不发生'],  
                                        ['其他', '其他']
                                       ]  
                            }),  
                    editable : true,  
                    triggerAction:'all',  
                    valueField : 'value',  
                    displayField : 'text'
                }]
	        }]
	    },
        	uploadPanel
        ]
        
        var items_saveproduct1 = [
                                  {
                                  	xtype : 'combo',
                                  	fieldLabel : '目标年份',
                                      labelAlign: 'right',
                                      name : 'TimeYear',  
                                      anchor : '100%', 
                                  	queryMode : 'local',
                                  	editable : false,
                                  	store : new Ext.data.ArrayStore({
                                  	fields : ['id','name'],
                                  	data : []
                                  	}),
                                  	valueField : 'name',
                                  	displayField : 'id',
                                  	triggerAction : 'all',
                                  	autoSelect : true,
                                  	listeners : {
                                  	beforerender :  function(){
                                  	var newyear = parseInt(Ext.Date.format(new Date(),'Y'));//这是为了取现在的年份数
                                  	var yearlist = [];
                                  	for(var i = newyear+1;i>=(newyear-1);i--){
                                  	yearlist.push([i,i]);
                                  	}
                                  	this.store.loadData(yearlist);
                                  	}
                                  	}             
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
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '项目安全评估率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'EvaluateRate',  
                  	 	                    anchor : '95%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['100%', '100%'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '员工（包括分包单位）岗前安全培训、操作技能培训率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'TrainRate',  
                  	 	                    anchor : '95%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['100%', '100%'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '安全生产隐患限期整改率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'ReformRate',  
                  	 	                    anchor : '95%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['100%', '100%'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '项目安全总监到位率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'ReachRate',  
                  	 	                    anchor : '95%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['100%', '100%'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '建设项目的重点部位和关键环节安全措施的审批及交底率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'ExamineRate',  
                  	 	                    anchor : '95%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['100%', '100%'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '工作场所环境合格率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'EnviPassRate',  
                  	 	                    anchor : '95%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['100%', '100%'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '危险性较大分部、分项工程专项安全技术措施编制、审批、交底率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'BottomRate',  
                  	 	                    anchor : '95%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['100%', '100%'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '工作场所职业病危害告知率、职业病危害因素监测率、主要危害因素监测合格率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'SickPassRate',  
                  	 	                    anchor : '95%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['100%', '100%'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '从事接触职业病危害作业劳动者的职业健康体检率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'CheckRate',  
                  	 	                    anchor : '95%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['100%', '100%'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                }]
                  	 		        },{
                  	 		        	xtype: 'container',
                  	 		            flex: 1,
                  	 		            layout: 'anchor',
                  	 		            items: [{
                  	 		            	xtype : 'combo',  
                  	 	                    fieldLabel : '因工程勘察设计原因造成的人身伤亡事故和较大财产损失事故',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'WorkAcci',  
                  	 	                    anchor : '100%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['不发生', '不发生'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '生产性轻伤、重伤和死亡责任事故',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'ProdAcci',  
                  	 	                    anchor : '100%',  
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['不发生', '不发生'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '轻伤事故率',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'AcciRate',  
                  	 	                    anchor : '100%',
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['不发生', '不发生'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '分包单位生产性重伤和死亡责任事故',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'FenBaoAcci',  
                  	 	                    anchor : '100%',
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['不发生', '不发生'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '负主责的一般及以上交通事故、负管理责任的建设项目地质灾害',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'Disaster',  
                  	 	                 anchor : '100%',
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['不发生', '不发生'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '一般及以上设备事故、直接经济损失20万元/次及以上的火灾事故',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'FireAcci',  
                  	 	                 anchor : '100%',
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['不发生', '不发生'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '职业病危害事件',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'JobEvent',  
                  	 	                 anchor : '100%',
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['不发生', '不发生'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '负管理责任的环境污染事件',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'PollutEvent',  
                  	 	                 anchor : '100%',
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['不发生', '不发生'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                },{
                  	 	                	xtype : 'combo',  
                  	 	                    fieldLabel : '事故瞒报、谎报、拖延不报行为',
                  	 	                    labelAlign: 'right',
                  	 	                    labelWidth: 200,
                  	 	                    name : 'Behave',  
                  	 	                 anchor : '100%',
                  	 	                    mode : 'local',  
                  	 	                    store : new Ext.data.SimpleStore({  
                  	 	                                fields : ['value', 'text'],  
                  	 	                                data : [['不发生', '不发生'],  
                  	 	                                        ['其他', '其他']
                  	 	                                       ]  
                  	 	                            }),  
                  	 	                    editable : true,  
                  	 	                    triggerAction:'all',  
                  	 	                    valueField : 'value',  
                  	 	                    displayField : 'text'
                  	 	                }]
                  	 		        }]
                  	 		    },
        	uploadPanel
        ]
 //end---------------------------------------------------------       
        var items_Saveproplan = [{
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
	            	xtype:"datefield",
	                fieldLabel: '成立安委会',
	                afterLabelTextTpl: required,
	                labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Anweihui',
	                anchor:'100%',
	                allowBlank: false 
                },{
	 	            xtype:"datefield",
	                fieldLabel: '成立三项业务领导小组',
	                afterLabelTextTpl: required,
	                labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'ThreeGroup',
	                anchor:'100%',
	                allowBlank: false                            
	 	         },
                	{
                	xtype:"datefield",
	                fieldLabel: '“四个责任体系”建设',
	                afterLabelTextTpl: required,
	                labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'FourBuild',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:"datefield",
	                fieldLabel: '编制安全生产（含三项业务）费用投入计划',
	                afterLabelTextTpl: required,
	                labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'SaveBuild',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:"datefield",
	                fieldLabel: '编制安全教育培训计划',
	                afterLabelTextTpl: required,
	                labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'SavePlan',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:"datefield",
	                fieldLabel: '编制专项施工方案',
	                afterLabelTextTpl: required,
	                labelWidth: 200,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'BuildPlan',
	                anchor:'100%',
	                allowBlank: false
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
                	xtype:"datefield",
	                fieldLabel: '编制应急预案及现场处置方案',
	                afterLabelTextTpl: required,
	                labelWidth: 250,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'HandlePlan',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:"datefield",
	                fieldLabel: '安全生产标准化及文明施工划策',
	                afterLabelTextTpl: required,
	                labelWidth: 250,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'SaveBuildPlan',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:"datefield",
	                fieldLabel: '危险源辨识与发布',
	                afterLabelTextTpl: required,
	                labelWidth: 250,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'DangerPublic',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:"datefield",
	                fieldLabel: '制定安全生产强制性条文执行计划',
	                afterLabelTextTpl: required,
	                labelWidth: 250,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'ExecutePlan',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:"datefield",
	                fieldLabel: '编制三项业务保障措施及工作计划',
	                afterLabelTextTpl: required,
	                labelWidth: 250,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'WorkPlan',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:"datefield",
	                fieldLabel: '安全检查及隐患排查治理',
	                afterLabelTextTpl: required,
	                labelWidth: 250,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'SaveCheck',
	                anchor:'100%',
	                allowBlank: false
                }]
	        }]
	    },
        	uploadPanel
        ]
        
        var items_goaldecom = [{
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
                    fieldLabel: '目标内容',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Content'
                },{
                	xtype:'textfield',
                    fieldLabel: '目标值',
                    labelAlign: 'right',
                    anchor:'95%',
                    name: 'Mvalue'
                }]
	        },{
	        	xtype: 'container',
	            flex: 1,
	            layout: 'anchor',
	            items: [{
	            	xtype:"datefield",
	                fieldLabel: '完成时间',
	                afterLabelTextTpl: required,
	                labelAlign: 'right',
	                format:"Y-m-d",
	                name: 'Time',
	                anchor:'100%',
	                allowBlank: false
                },{
                	xtype:'textfield',
                    fieldLabel: '责任人',
                    labelAlign: 'right',
                    anchor:'100%',
                    name: 'Manager'
                }]
	        }]
	    },{
            xtype:'textarea',
            fieldLabel: '实施措施',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Measure'
	    },{
            xtype:'textarea',
            fieldLabel: '完成情况',
            //labelWidth: 120,
            labelAlign: 'right',
            height: 75,
            emptyText: '不超过500个字符',
            maxLength:500,
            anchor:'100%',
            name: 'Completed'
	    },
	    uploadPanel
        ]
		
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
		if(getSel(gridDT))
			insertFileToList();
		var actionURL;
		var uploadURL;
		var items;      
		var folder;
		folder = selRecs[0].data.Accessory;
		//87:安全生产工作计划，88：年度安全生产目标，92：年度工作计划，93：月度工作计划，208：三项业务年度目标分解及工作计划，198：半年度工作总结，199：年度安全工作总结，200：完工安全工作总结
		if (tableID == 87 || tableID == 88 || tableID == 92 || tableID == 93 || tableID == 208|| tableID == 198 || tableID == 199 || tableID == 200) {
			actionURL = 'MissionAction!alloTask?title=分配任务&folder='+folder; 
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
        	var actionURL;
        	var uploadURL;
        	var items;
        	if(tableID >=  80 && tableID <= 84 || tableID == 171 || tableID == 175 ||tableID >= 177 && tableID <= 180
        	|| tableID == 150 || tableID == 151 || tableID == 159 ||  tableID == 161 ||  tableID == 162
        		    || tableID == 129 || tableID == 111 || (tableID >= 188 &&  tableID <= 191)|| (tableID >= 194 &&  tableID <= 201)
        		    || tableID == 172 || tableID == 174
        		   || tableID == 181 || tableID == 182
        		   || tableID == 164 || tableID >=  166 && tableID <= 170
        		   || tableID == 130 || tableID >= 217 && tableID <= 233
        		   || tableID == 133 || tableID == 135 || tableID == 137 || tableID == 141 || (tableID >= 145 && tableID <= 149)
        		   || tableID >= 211 && tableID <= 213 || tableID >= 123 && tableID <= 126 || tableID == 252 || tableID == 111|| tableID == 302) {
        		actionURL = 'GoalDutyAction!addSecurityplan?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_securityplan;
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
//end--------------------------------------------------------------------------------------   
        	else if( tableID == 192 ) {
                		actionURL = 'GoalDutyAction!addSecurityplan?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
                		uploadURL = "UploadAction!execute";
                		items = items_securityplan1st;
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
        	
        	
        	else if( tableID == 193 ) {
        		actionURL = 'GoalDutyAction!addSecurityplan?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_securityplan2nd;
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
        	
        	else if( tableID == 282 ) {
        		actionURL = 'GoalDutyAction!addSecurityplan?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_securityplan3rd;
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
        	
        	else if( tableID == 283 ) {
        		actionURL = 'GoalDutyAction!addSecurityplan?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_securityplan4th;
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
        	
        	else if(title == '年度工作计划')
          	{	        	
          		actionURL = 'GoalDutyAction!addYearplan?userName=' + user.name + "&userRole=" + user.role;
          		uploadURL = "UploadAction!execute";
          		items = items_Yearplan;
              	createForm({
          			autoScroll: true,
      	        	bodyPadding: 5,
      	        	action: 'addProject',
      	        	url: actionURL,
      	        	items: items
      	        });
              	uploadPanel.upload_url = uploadURL;
              	bbar.moveFirst();	//状态栏回到第一页
      	        showWin({ winId: 'addProject', title: '新增工作计划', items: [forms]});
          	}
//jianglf----------------------------------------------------------------------------------
        	else if(title == '安全生产管理机构')
          	{	        	
          		actionURL = 'GoalDutyAction!addSafetypromanagement?userName=' + user.name + "&userRole=" + user.role;
          		uploadURL = "UploadAction!execute";
          		items = items_Safetypromanagement;
              	createForm({
          			autoScroll: true,
      	        	bodyPadding: 5,
      	        	action: 'addProject',
      	        	url: actionURL,
      	        	items: items
      	        });
              	uploadPanel.upload_url = uploadURL;
              	bbar.moveFirst();	//状态栏回到第一页
      	        showWin({ winId: 'addProject', title: '新增机构', items: [forms]});
          	}
        	
        	else if(title == '分包方安全生产管理机构')
          	{	        	
          		actionURL = 'GoalDutyAction!addSafetypromanagementfb?userName=' + user.name + "&userRole=" + user.role;
          		uploadURL = "UploadAction!execute";
          		items = items_Safetypromanagementfb;
              	createForm({
          			autoScroll: true,
      	        	bodyPadding: 5,
      	        	action: 'addProject',
      	        	url: actionURL,
      	        	items: items
      	        });
              	uploadPanel.upload_url = uploadURL;
              	bbar.moveFirst();	//状态栏回到第一页
      	        showWin({ winId: 'addProject', title: '新增机构', items: [forms]});
          	}
        	
        	else if(title == '三项业务年度目标分解及工作计划')
          	{	        	
          		actionURL = 'GoalDutyAction!addThreeworkplan?userName=' + user.name + "&userRole=" + user.role;
          		uploadURL = "UploadAction!execute";
          		items = items_Threeworkplan;
              	createForm({
          			autoScroll: true,
      	        	bodyPadding: 5,
      	        	action: 'addProject',
      	        	url: actionURL,
      	        	items: items
      	        });
              	uploadPanel.upload_url = uploadURL;
              	bbar.moveFirst();	//状态栏回到第一页
      	        showWin({ winId: 'addProject', title: '新增工作计划', items: [forms]});
          	}
//end--------------------------------------------------------------------------------------        	
        	
        	else if(title == '月度工作计划')
          	{	        	
          		actionURL = 'GoalDutyAction!addMonthplan?userName=' + user.name + "&userRole=" + user.role;
          		uploadURL = "UploadAction!execute";
          		items = items_Monthplan;
              	createForm({
          			autoScroll: true,
      	        	bodyPadding: 5,
      	        	action: 'addProject',
      	        	url: actionURL,
      	        	items: items
      	        });
              	uploadPanel.upload_url = uploadURL;
              	bbar.moveFirst();	//状态栏回到第一页
      	        showWin({ winId: 'addProject', title: '新增工作计划', items: [forms]});
          	}
        	
        	else if(title == '分包方安全生产计划管理')
          	{	        	
          		actionURL = 'GoalDutyAction!addFbplan?userName=' + user.name + "&userRole=" + user.role;
          		uploadURL = "UploadAction!execute";
          		items = items_Fbplan;
              	createForm({
          			autoScroll: true,
      	        	bodyPadding: 5,
      	        	action: 'addProject',
      	        	url: actionURL,
      	        	items: items
      	        });
              	uploadPanel.upload_url = uploadURL;
              	bbar.moveFirst();	//状态栏回到第一页
      	        showWin({ winId: 'addProject', title: '新增报备', items: [forms]});
          	}
        	
        	else if( title == '安全生产责任书签订' ) {
        		actionURL = 'GoalDutyAction!addSaveprodbook?userName=' + user.name + "&userRole=" + user.role ;  
        		uploadURL = "UploadAction!execute";
        		items = items_Saveprodbook;
        		
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
        	
        	else if( title == '安全生产工作策划' ) {
        		actionURL = 'GoalDutyAction!addSaveproplan?userName=' + user.name + "&userRole=" + user.role ;  
        		uploadURL = "UploadAction!execute";
        		items = items_Saveproplan;
        		
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
        	
        	else if( title == '安委会' ) {
        		actionURL = 'GoalDutyAction!addAnweihui?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_anweihui;
        		
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
//jianglf-------------------------------------------        	
             /* else if( tableID >= 90 &&  tableID <= 91) {
        		actionURL = 'GoalDutyAction!addSaveproduct?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_saveproduct;
        		
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
        	}  */
        	
            else if( tableID == 86) {
        		actionURL = 'GoalDutyAction!addSaveproduct?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_saveproductzt;
        		
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
        	
            else if( tableID == 88) {
        		actionURL = 'GoalDutyAction!addSaveproduct?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_saveproduct1;
        		
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
//end--------------------------------------------        	
        	else if( title == '年度目标分解' ) {
        		actionURL = 'GoalDutyAction!addGoaldecom?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title;  
        		uploadURL = "UploadAction!execute";
        		items = items_goaldecom;
        		
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
//jianglf------------------------------------------------------------------------------------
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
        		if(tableID >=  80 && tableID <= 84  || tableID == 171 || tableID == 175 ||tableID >= 177 && tableID <= 180
        		|| tableID == 150 || tableID == 151 || tableID == 159 ||  tableID == 161 ||  tableID == 162 
        		   || tableID == 129 || tableID == 111 || (tableID >= 188 &&  tableID <= 191)|| (tableID >= 194 &&  tableID <= 201)
        		   || tableID == 172 || tableID == 174
        		   || tableID == 181 || tableID == 182
        		   || tableID == 164 || tableID >=  166 && tableID <= 170
        		   || tableID == 130 || tableID >= 217 && tableID <= 233
        		   || tableID == 133 || tableID == 135 || tableID == 137 || tableID == 141 || (tableID >= 145 && tableID <= 149)
        		   || tableID >= 211 && tableID <= 213 || tableID >= 123 && tableID <= 126 || tableID == 252 || tableID == 111|| tableID == 302) {
        				insertFileToList();	
        				actionURL = 'GoalDutyAction!editSecurityplan?userName=' + user.name + "&userRole=" + user.role; 
        				uploadURL = "UploadAction!execute"; 
        				items = items_securityplan;
                	
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
        		
        		else if( tableID == 192 ) {
                				insertFileToList();	
                				actionURL = 'GoalDutyAction!editSecurityplan?userName=' + user.name + "&userRole=" + user.role; 
                				uploadURL = "UploadAction!execute"; 
                				items = items_securityplan1st;
                        	
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
        		
        		else if( tableID == 193 ) {
    				insertFileToList();	
    				actionURL = 'GoalDutyAction!editSecurityplan?userName=' + user.name + "&userRole=" + user.role; 
    				uploadURL = "UploadAction!execute"; 
    				items = items_securityplan2nd;
            	
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
        		
        		else if( tableID == 282 ) {
    				insertFileToList();	
    				actionURL = 'GoalDutyAction!editSecurityplan?userName=' + user.name + "&userRole=" + user.role; 
    				uploadURL = "UploadAction!execute"; 
    				items = items_securityplan3rd;
            	
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
        		
        		else if( tableID == 283 ) {
    				insertFileToList();	
    				actionURL = 'GoalDutyAction!editSecurityplan?userName=' + user.name + "&userRole=" + user.role; 
    				uploadURL = "UploadAction!execute"; 
    				items = items_securityplan4th;
            	
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
//end---------------------------------------------------------------------------------------        		 
        		 else if(title == '年度工作计划')
    			{
    				insertFileToList();
    				uploadURL = "UploadAction!execute";
    				actionURL = 'GoalDutyAction!editYearplan?userName=' + user.name + "&userRole=" + user.role;  
    				items = items_Yearplan;
          
    			createForm({
        			autoScroll: true,
        			action: 'editProject',
    	        	bodyPadding: 5,
    	        	url: actionURL,
    	        	items: items
    	        });
    			uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'editProject', title: '修改工作计划', items: [forms]});
    			}
//jianglf-------------------------------------------------------------------------
        		 else if(title == '安全生产管理机构')
     			{
     				insertFileToList();
     				uploadURL = "UploadAction!execute";
     				actionURL = 'GoalDutyAction!editSafetypromanagement?userName=' + user.name + "&userRole=" + user.role;  
     				items = items_Safetypromanagement;
           
     			createForm({
         			autoScroll: true,
         			action: 'editProject',
     	        	bodyPadding: 5,
     	        	url: actionURL,
     	        	items: items
     	        });
     			uploadPanel.upload_url = uploadURL;
             	bbar.moveFirst();	//状态栏回到第一页
     	        showWin({ winId: 'editProject', title: '修改机构', items: [forms]});
     			}
        		
        		 else if(title == '分包方安全生产管理机构')
      			{
      				insertFileToList();
      				uploadURL = "UploadAction!execute";
      				actionURL = 'GoalDutyAction!editSafetypromanagementfb?userName=' + user.name + "&userRole=" + user.role;  
      				items = items_Safetypromanagementfb;
            
      			createForm({
          			autoScroll: true,
          			action: 'editProject',
      	        	bodyPadding: 5,
      	        	url: actionURL,
      	        	items: items
      	        });
      			uploadPanel.upload_url = uploadURL;
              	bbar.moveFirst();	//状态栏回到第一页
      	        showWin({ winId: 'editProject', title: '修改机构', items: [forms]});
      			}
        		
        		 else if(title == '三项业务年度目标分解及工作计划')
     			{
     				insertFileToList();
     				uploadURL = "UploadAction!execute";
     				actionURL = 'GoalDutyAction!editThreeworkplan?userName=' + user.name + "&userRole=" + user.role;  
     				items = items_Threeworkplan;
           
     			createForm({
         			autoScroll: true,
         			action: 'editProject',
     	        	bodyPadding: 5,
     	        	url: actionURL,
     	        	items: items
     	        });
     			uploadPanel.upload_url = uploadURL;
             	bbar.moveFirst();	//状态栏回到第一页
     	        showWin({ winId: 'editProject', title: '修改工作计划', items: [forms]});
     			}
//end-----------------------------------------------------------------------------        		
        		
        		else if(title == '月度工作计划')
    			{
    				insertFileToList();
    				uploadURL = "UploadAction!execute";
    				actionURL = 'GoalDutyAction!editMonthplan?userName=' + user.name + "&userRole=" + user.role;  
    				items = items_Monthplan;
          
    			createForm({
        			autoScroll: true,
        			action: 'editProject',
    	        	bodyPadding: 5,
    	        	url: actionURL,
    	        	items: items
    	        });
    			uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'editProject', title: '修改工作计划', items: [forms]});
    			}
        		
        		else if(title == '分包方安全生产计划管理')
    			{
    				insertFileToList();
    				uploadURL = "UploadAction!execute";
    				actionURL = 'GoalDutyAction!editFbplan?userName=' + user.name + "&userRole=" + user.role;  
    				items = items_Fbplan;
          
    			createForm({
        			autoScroll: true,
        			action: 'editProject',
    	        	bodyPadding: 5,
    	        	url: actionURL,
    	        	items: items
    	        });
    			uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'editProject', title: '修改报备', items: [forms]});
    			}
        		 
        		 else if(title == '安全生产责任书签订')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'GoalDutyAction!editSaveprodbook?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Saveprodbook;
              
        			createForm({
            			autoScroll: true,
            			action: 'editProject',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProject', title: '修改项目', items: [forms]});
        			}
        			
        			else if(title == '安全生产工作策划')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'GoalDutyAction!editSaveproplan?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_Saveproplan;
              
        			createForm({
            			autoScroll: true,
            			action: 'editProject',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProject', title: '修改项目', items: [forms]});
        			}
        		 
        		 else if(title == '安委会')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'GoalDutyAction!editAnweihui?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_anweihui;
              
        			createForm({
            			autoScroll: true,
            			action: 'editProject',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProject', title: '修改项目', items: [forms]});
        			}
//jianglf--------------------------------        		
        		/*else if(tableID >= 90 &&  tableID <= 91)
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'GoalDutyAction!editSaveproduct?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title;  
        				items = items_saveproduct;
              
        			createForm({
            			autoScroll: true,
            			action: 'editProject',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProject', title: '修改项目', items: [forms]});
        			}
*/        		
        		
        		else if( tableID == 86)
    			{
    				insertFileToList();
    				uploadURL = "UploadAction!execute";
    				actionURL = 'GoalDutyAction!editSaveproduct?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title;  
    				items = items_saveproductzt;
          
    			createForm({
        			autoScroll: true,
        			action: 'editProject',
    	        	bodyPadding: 5,
    	        	url: actionURL,
    	        	items: items
    	        });
    			uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'editProject', title: '修改项目', items: [forms]});
    			}
        			
        		else if( tableID == 88)
    			{
    				insertFileToList();
    				uploadURL = "UploadAction!execute";
    				actionURL = 'GoalDutyAction!editSaveproduct?userName=' + user.name + "&userRole=" + user.role  + "&type=" + title;  
    				items = items_saveproduct1;
          
    			createForm({
        			autoScroll: true,
        			action: 'editProject',
    	        	bodyPadding: 5,
    	        	url: actionURL,
    	        	items: items
    	        });
    			uploadPanel.upload_url = uploadURL;
            	bbar.moveFirst();	//状态栏回到第一页
    	        showWin({ winId: 'editProject', title: '修改项目', items: [forms]});
    			}
        		
//end------------------------------------------------------------------        		
        			else if(title == '年度目标分解')
        			{
        				insertFileToList();
        				uploadURL = "UploadAction!execute";
        				actionURL = 'GoalDutyAction!editGoaldecom?userName=' + user.name + "&userRole=" + user.role;  
        				items = items_goaldecom;
              
        			createForm({
            			autoScroll: true,
            			action: 'editProject',
        	        	bodyPadding: 5,
        	        	url: actionURL,
        	        	items: items
        	        });
        			uploadPanel.upload_url = uploadURL;
                	bbar.moveFirst();	//状态栏回到第一页
        	        showWin({ winId: 'editProject', title: '修改项目', items: [forms]});
        			}
        		}
        		else
				{
					 Ext.Msg.alert('警告', '只能选中一条记录！');
				}        		
	        }
        }
  //jianglf--------------------------------------------------------------------------------------      
        var deleteH = function() 
        {        	
        	if(getSel(gridDT))
        	{
        		Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) 
        		{
    				if (buttonID === 'yes') 
    				{
    					var delete_url = '';
    					if (tableID >=  80 && tableID <= 84  || tableID == 171 || tableID == 175 ||tableID >= 177 && tableID <= 180
    					|| tableID == 150 || tableID == 151 || tableID == 159 ||  tableID == 161 ||  tableID == 162 
        		   || tableID == 129 || tableID == 111 || tableID >= 188 &&  tableID <= 201
        		   || tableID == 172 || tableID == 174
        		   || tableID == 181 || tableID == 182|| tableID == 282 || tableID == 283
        		   || tableID == 164 || tableID >=  166 && tableID <= 170
        		   || tableID == 130 || tableID >= 217 && tableID <= 233
        		   || tableID == 133 || tableID == 135 || tableID == 137 || tableID == 141 || (tableID >= 145 && tableID <= 149)
        		   || tableID >= 211 && tableID <= 213 || tableID >= 123 && tableID <= 126 || tableID == 252 || tableID == 111|| tableID == 302) {
    						delete_url = 'GoalDutyAction!deleteSecurityplan';
                    	}
                    	else if(title == '安全生产责任书签订')
                    	{
                    		delete_url = 'GoalDutyAction!deleteSaveprodbook';
                    	}
                    	else if(title == '安全生产工作策划')
                    	{
                    		delete_url = 'GoalDutyAction!deleteSaveproplan';
                    	}
                    	else if(title == '安委会')
                    	{
                    		delete_url = 'GoalDutyAction!deleteAnweihui';
                    	}
                        else if( tableID == 86 || tableID == 88 || tableID >= 90 &&  tableID <= 91)
                    	{
                    		delete_url = 'GoalDutyAction!deleteSaveproduct';
                    	}
                    	else if(title == '年度目标分解')
                    	{
                    		delete_url = 'GoalDutyAction!deleteGoaldecom';
                    	}
//end--------------------------------------------------------------------------------------------------------------                    	
                    	else if(title == '年度工作计划')
    					{$.getJSON(encodeURI("GoalDutyAction!deleteYearplan?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
//jianglf-----------------------------------------------------------------------
                    	else if(title == '安全生产管理机构')
    					{$.getJSON(encodeURI("GoalDutyAction!deleteSafetypromanagement?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
                    	else if(title == '分包方安全生产管理机构')
    					{$.getJSON(encodeURI("GoalDutyAction!deleteSafetypromanagementfb?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
                    	else if(title == '三项业务年度目标分解及工作计划')
    					{$.getJSON(encodeURI("GoalDutyAction!deleteThreeworkplan?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
//end---------------------------------------------------------------------------    					
    					
                        else if(title == '月度工作计划')
    					{$.getJSON(encodeURI("GoalDutyAction!deleteMonthplan?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
    					
                        else if(title == '分包方安全生产计划管理')
    					{$.getJSON(encodeURI("GoalDutyAction!deleteFbplan?userName=" + user.name + "&userRole=" + user.role+ "&type=" + title),
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
                    	
    					$.getJSON(encodeURI(delete_url + "?userName=" + user.name + "&userRole=" + user.role),
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
        var btnScan2 = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '附件浏览',
            icon: "Images/ims/toolbar/view.png",
           	disabled: true,
            menu: fileMenu,
            handler: scanH2
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
        	
        	if(title == '目标指标实施汇总') {
				newtitle = '年度目标分解';
				storeSum = store_goaldecomsum;
        	}
			else if(title == '年度安全生产计划汇总') {
				newtitle = '年度工作计划';
				storeSum = store_Yearplansum;
			}
			else if(title == '三项业务目标计划汇总') {
				newtitle = '三项业务年度目标分解及工作计划';
				storeSum = store_Threeworkplansum;
			}
			else if(title == '月度安全生产计划汇总') {
				newtitle = '月度工作计划';
				storeSum = store_Monthplansum;
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
				    					$.getJSON(encodeURI("GoalDutyAction!deleteFileUpload?file=" + item.text + "&title=" + title + "&projectName=" + projectName),
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
        	width: 100,
        	height: 32,
        	text: '上传文件',
            icon: "Images/ims/toolbar/extexcel.png",
            handler: fileUploadH
        })
        
        var btnscanFileUpload = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '文件下载',
            icon: "Images/ims/toolbar/view.png",
           	//disabled: true,
            menu: scanFileUploadMenu,
            handler: scanFileUploadH
        })
        
        var btnscanFileUploadSum = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '文件下载',
            icon: "Images/ims/toolbar/view.png",
           	//disabled: true,
            menu: scanFileUploadMenu,
            handler: scanFileUploadSumH
        })
        
        var btndeleteFileUpload = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '文件删除',
            icon: "Images/ims/toolbar/view.png",
           	//disabled: true,
            menu: deleteFileUploadMenu,
            handler: deleteFileUploadH
        })
          //liuchi
        
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
		
		var removeBtn = function() {
			tbar.remove(btnAdd);
			tbar.remove(btnEdit);
			tbar.remove(btnDel);
		}
        
//jianglf-----------------------------------------------------------------------------------------------------     
        if (tableID>= 80 && tableID <= 84  || tableID == 171 || tableID == 175 ||tableID >= 177 && tableID <= 180
    					|| tableID == 150 || tableID == 151 || tableID == 159 ||  tableID == 161 ||  tableID == 162
        		   || tableID == 129 || tableID == 111 || (tableID >= 188 &&  tableID <= 191)|| (tableID >= 194 &&  tableID <198) || (tableID > 198 &&  tableID <= 201) 
        		   || tableID == 172 || tableID == 174
        		   || tableID == 181 || tableID == 182
        		   || tableID == 164 || tableID >=  166 && tableID <= 170
        		   || tableID == 130 || tableID >= 217 && tableID <233
        		   || tableID == 133 || tableID == 135 || tableID == 137 || tableID == 141 || (tableID >= 145 && tableID <= 149)
        		   || tableID >= 211 && tableID <= 213 || tableID >= 123 && tableID <= 126 || tableID == 252 || tableID == 111|| tableID == 302)
        {	
        	dataStore = store_securityplan;
        	
//end--------------------------------------------------------------------------------------------------------------        	
        	
        	
        	queryURL = 'GoalDutyAction!getSecurityplanListSearch?userName=' + user.name + "&userRole=" + user.role  + "&Type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	
    
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    //{ text: '用户名', dataIndex: 'UserName', align: 'center', width: 300},
            	    { text: '文件名', dataIndex: 'Filename', align: 'center', width: 500},
            	    { text: '上传时间', dataIndex: 'Time', align: 'center', width: 300}
            	 
            	]
            	
            
            	//tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add(btnScan);
        		tbar.add("-");
        		tbar.add(btnAdd);
        		//tbar.add(btnEdit);
        		tbar.add(btnDel);
        		if(tableID >= 198 &&  tableID <= 200){
        			tbar.add(btnAllotask);
        		}
        		

        		
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        		
        }
        
        if ((tableID >=198)&&(tableID<=200) )
        {	
        	dataStore = store_securityplan;
        	
        //end--------------------------------------------------------------------------------------------------------------        	
        	
        	
        	queryURL = 'GoalDutyAction!getSecurityplanListSearch?userName=' + user.name + "&userRole=" + user.role  + "&Type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	

        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    //{ text: '用户名', dataIndex: 'UserName', align: 'center', width: 300},
            	    { text: '文件名', dataIndex: 'Filename', align: 'center', width: 500},
            	    { text: '上传时间', dataIndex: 'Time', align: 'center', width: 300}
            	  
            	]
            	
            
            	//tbar.add("-");
        		tbar.add(textSearch);
        		tbar.add(btnSearch);
        		tbar.add(btnSearchR);
        		tbar.add(btnScan);
        		tbar.add("-");
        		tbar.add(btnAdd);
        		//tbar.add(btnEdit);
        		tbar.add(btnDel);
        		if(tableID >= 198 &&  tableID <= 200){
        			tbar.add(btnAllotask);
        		}
        		
        		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'GoalDutyAction!getSecurityplanListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title+ "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 80}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			
        		}
        		
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        		
        }
        
        if (tableID ==233 )
{	
	dataStore = store_securityplan;
	
//end--------------------------------------------------------------------------------------------------------------        	
	
	
	queryURL = 'GoalDutyAction!getSecurityplanListSearch?userName=' + user.name + "&userRole=" + user.role  + "&Type=" + title+ "&projectName=" + projectName;
	//dataStore.getProxy().url = encodeURI(queryURL);
	//style = "write";
	

	column = [
    	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
    	    //{ text: '用户名', dataIndex: 'UserName', align: 'center', width: 300},
    	    { text: '文件名', dataIndex: 'Filename', align: 'center', width: 500},
    	    { text: '上传时间', dataIndex: 'Time', align: 'center', width: 300}
    	    /*{ text: '预览', align: 'center', width: 125, renderer: function (value, meta, record) {
					//var testpaperID = record.get('Acc');
					
					var misfile = record.get('Accessory').split('*');
     		        
     		        var foldermis = 'upload\\'+misfile[0]+'\\'+misfile[1]+'\\';
     		        
     		               		       
     		        for(var i = 2;i<misfile.length-1;i++){
     		        	var filename = misfile[i];
     		        	if(filename.endWith('doc')) {
     		        		filename = filename.replace(/doc/, "pdf");
     		        	}
     		        	else if (filename.endWith('docx')) {
     		        		filename = filename.replace(/docx/, "pdf");
     		        	}
     		        	else if (filename.endWith('xlsx')) {
     		        		filename = filename.replace(/xlsx/, "pdf");
     		        	}
     		        	else if (filename.endWith('xls')) {
     		        		filename = filename.replace(/xls/, "pdf");
     		        	}
     		       		//Ext.Msg.alert(filename);
     		        }	//html_str = html_str + misfile[i]+"&nbsp;&nbsp;<div style=\"float:right; text-align:right;margin-right: 5\"><a href=\""+foldermis+misfile[i]+"\" target=\"_blank\">下载</a><a href=\""+foldermis+filename+"\" target=\"_blank\">      预览</a></div><br>";
					
					return	'<a><img src="Images/ims/toolbar/help.png" onclick="window.open(\\"foldermis+filename\\")"></a>';
				}
			}*/
    	]
    	
    
    	//tbar.add("-");
		tbar.add(textSearch);
		tbar.add(btnSearch);
		tbar.add(btnSearchR);
		tbar.add(btnScan);
		tbar.add("-");
		tbar.add(btnAdd);
		//tbar.add(btnEdit);
		tbar.add(btnDel);
		if(tableID >= 198 &&  tableID <= 200){
			tbar.add(btnAllotask);
		}
		
		if(user.role == '全部项目') {
			dataStore.getProxy().url = 'GoalDutyAction!getSecurityplanListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title+ "&projectName=" + "";
			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
			
			//给第一列添加所属项目，dataindex自己根据实际字段改
			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 80}];
			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
			
		}
		
		if(user.role === '其他管理员' || user.role === '院领导') {
			tbar.remove(btnAdd);
			tbar.remove(btnEdit);
			tbar.remove(btnDel);
		}
		
}
        
        else if ( tableID == 192 )
     {	
     	dataStore = store_securityplan;
     	queryURL = 'GoalDutyAction!getSecurityplanListSearch?userName=' + user.name + "&userRole=" + user.role  + "&Type=" + title;
     	column = [
         	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
         	    { text: '年份', dataIndex: 'Timeyear', align: 'center', width: 250},
         	    { text: '创建方案名称', dataIndex: 'Fname', align: 'center', width: 250},
         	    { text: '文件名', dataIndex: 'Filename', align: 'center', width: 250},
         	    { text: '上传时间', dataIndex: 'Time', align: 'center', width: 250}
         	    
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
     		
     		if(user.role == '全部项目') {
    			dataStore.getProxy().url = 'GoalDutyAction!getSecurityplanListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title+ "&projectName=" + "";
    			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
    			
    			//给第一列添加所属项目，dataindex自己根据实际字段改
    			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 80}];
    			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
    			
    		}
     		
     		if(user.role === '其他管理员' || user.role === '院领导') {
    			tbar.remove(btnAdd);
    			tbar.remove(btnEdit);
    			tbar.remove(btnDel);
    		}
     }
        
        else if ( tableID == 193 )
        {	
        	dataStore = store_securityplan;
        	queryURL = 'GoalDutyAction!getSecurityplanListSearch?userName=' + user.name + "&userRole=" + user.role  + "&Type=" + title;
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '年份', dataIndex: 'Timeyear', align: 'center', width: 250},
            	    { text: '绩效评定报告名称', dataIndex: 'Fname', align: 'center', width: 250},
            	    { text: '文件名', dataIndex: 'Filename', align: 'center', width: 250},
            	    { text: '上传时间', dataIndex: 'Time', align: 'center', width: 250}
            	    
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
        
         else if ( tableID == 282 )
        {	
        	dataStore = store_securityplan;
        	queryURL = 'GoalDutyAction!getSecurityplanListSearch?userName=' + user.name + "&userRole=" + user.role  + "&Type=" + title;
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '年份', dataIndex: 'Timeyear', align: 'center', width: 250},
            	    { text: '自评方案名称', dataIndex: 'Fname', align: 'center', width: 250},
            	    { text: '文件名', dataIndex: 'Filename', align: 'center', width: 250},
            	    { text: '上传时间', dataIndex: 'Time', align: 'center', width: 250}
            	    
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
        
        else if ( tableID == 283 )
        {	
        	dataStore = store_securityplan;
        	queryURL = 'GoalDutyAction!getSecurityplanListSearch?userName=' + user.name + "&userRole=" + user.role  + "&Type=" + title;
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '年份', dataIndex: 'Timeyear', align: 'center', width: 250},
            	    { text: '自评报告名称', dataIndex: 'Fname', align: 'center', width: 250},
            	    { text: '文件名', dataIndex: 'Filename', align: 'center', width: 250},
            	    { text: '上传时间', dataIndex: 'Time', align: 'center', width: 250}
            	    
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
        		
        		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'GoalDutyAction!getSecurityplanListDef?userName=' + user.name + "&userRole=" + user.role + "&Type=" + title+ "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 80}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        		}
        		
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}
        }
        
       else if(title == '年度工作计划')
        {	
        	dataStore = store_Yearplan;
        	
        	queryURL = 'GoalDutyAction!getYearplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    //{ text: '计划序号', dataIndex: 'No', align: 'center', width: 350},
            	    { text: '年份', dataIndex: 'Year', align: 'center', width: 150},
            	    { text: '计划内容', dataIndex: 'Content', align: 'center', width: 200},
            	    { text: '责任部门', dataIndex: 'Manager', align: 'center', width: 150},
            	    { text: '计划完成时间', dataIndex: 'PlanDate', align: 'center', width: 150},
            	    { text: '实际完成时间', dataIndex: 'RealDate', align: 'center', width: 150}


            	    
            	   // { text: '办毕验证材料', dataIndex: 'Accessory', align: 'center', width: 700}
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
        		tbar.add(btnAllotask);
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
        		
        		
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}

        }
        
       else if(title == '年度安全生产计划汇总')
       {	

    	   dataStore = store_Yearplansum;
	       	
	       dataStore.load();
	       //由于此处的title会被config.title(代码最前面)所覆盖，所以传另一个参数过去，取名为title2
			queryURL = 'GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '年度工作计划' + "&projectName=" + '';
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 400 },
					{ text: '年份', dataIndex: 'Year', align: 'center', width: 200 },
					{ text: '年度安全生产计划', dataIndex: 'FileName', align: 'center', width: 400 }
				]
			addBtn();
			removeBtn();
			//tbar.add(btnAllotask);
			//tbar.add(btnFileUpload);
        		tbar.add(btnscanFileUploadSum);

       }
        
//jianglf-------------------------------------------------------------------
       else if(title == '安全生产管理机构')
       {	
       	dataStore = store_Safetypromanagement;
       	
       	queryURL = 'GoalDutyAction!getSafetypromanagementListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
       	//dataStore.getProxy().url = encodeURI(queryURL);
       	//style = "write";
       	column = [
           	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
           	    { text: '机构名称', dataIndex: 'Name', align: 'center', width: 250},
           	  //  { text: '分包单位名称', dataIndex: 'FbName', align: 'center', width: 350},
           	    { text: '成立时间', dataIndex: 'Time', align: 'center', width: 250},
           	    { text: '负责人', dataIndex: 'Person', align: 'center', width: 250},
           	    { text: '安全管理人员', dataIndex: 'Sperson', align: 'center', width: 250}
           	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
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
       		
       }
        
       else if(title == '分包方安全生产管理机构')
       {	
       	dataStore = store_Safetypromanagementfb;
       	
       	queryURL = 'GoalDutyAction!getSafetypromanagementfbListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
       	//dataStore.getProxy().url = encodeURI(queryURL);
       	//style = "write";
       	column = [
           	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
           	    { text: '机构名称', dataIndex: 'Name', align: 'center', width: 200},
           	    { text: '分包单位名称', dataIndex: 'FbName', align: 'center', width: 200},
           	    { text: '成立时间', dataIndex: 'Time', align: 'center', width: 200},
           	    { text: '负责人', dataIndex: 'Person', align: 'center', width: 200},
           	    { text: '安全管理人员', dataIndex: 'Sperson', align: 'center', width: 200}
           	 //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
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
        
       else if(title == '三项业务年度目标分解及工作计划')
       {	
       	dataStore = store_Threeworkplan;
       	
       	queryURL = 'GoalDutyAction!getThreeworkplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
       	//dataStore.getProxy().url = encodeURI(queryURL);
       	//style = "write";
       	column = [
       	 { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
 	    //{ text: '计划序号', dataIndex: 'No', align: 'center', width: 350},
 	    { text: '年份', dataIndex: 'Year', align: 'center', width: 150},
 	    { text: '计划内容', dataIndex: 'Content', align: 'center', width: 200},
 	    { text: '责任部门', dataIndex: 'Manager', align: 'center', width: 150},
 	    { text: '计划完成时间', dataIndex: 'PlanDate', align: 'center', width: 150},
 	    { text: '实际完成时间', dataIndex: 'RealDate', align: 'center', width: 150}
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
       		tbar.add(btnAllotask);
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
       		
       		
       		if(user.role === '其他管理员' || user.role === '院领导') {
    			tbar.remove(btnAdd);
    			tbar.remove(btnEdit);
    			tbar.remove(btnDel);
    		}

       }
        
       else if(title == '三项业务目标计划汇总')
       {	
       	dataStore = store_Threeworkplansum;
       	
       	dataStore.load();
			queryURL = 'GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '三项业务年度目标分解及工作计划' + "&projectName=" + '';
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 400 },
					{ text: '年份', dataIndex: 'Year', align: 'center', width: 200 },
					{ text: '三项业务目标计划', dataIndex: 'FileName', align: 'center', width: 400 }
				]
			addBtn();
			removeBtn();
			//tbar.add(btnAllotask);
			//tbar.add(btnFileUpload);
        		tbar.add(btnscanFileUploadSum);

       }
//end----------------------------------------------------------------------        
        
        else if(title == '月度工作计划')
        {	
        	dataStore = store_Monthplan;
        	
        	queryURL = 'GoalDutyAction!getMonthplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    //{ text: '计划序号', dataIndex: 'No', align: 'center', width: 250},
            	    { text: '年份', dataIndex: 'Year', align: 'center', width: 150},
            	    { text: '月份', dataIndex: 'Month', align: 'center', width: 150},
            	    { text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150},
            	    { text: '计划内容', dataIndex: 'Content', align: 'center', width: 200},
            	    { text: '责任部门', dataIndex: 'Manager', align: 'center', width: 150},
            	    { text: '计划完成时间', dataIndex: 'PlanDate', align: 'center', width: 150},
            	    { text: '实际完成时间', dataIndex: 'RealDate', align: 'center', width: 150}
            	    //{ text: '任务量', dataIndex: 'Workload', align: 'center', width: 250}
            	  //  { text: '办毕验证材料', dataIndex: 'Accessory', align: 'center', width: 700}
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
        		tbar.add(btnAllotask);
        		tbar.add(btnFileUpload);
        		tbar.add(btnscanFileUpload);
        		tbar.add(btndeleteFileUpload);
        		tbar.add({
        			xtype: 'monthfield',
        			fieldLabel: '月份选择',
        			editable: false,
        			width: 250,
        			labelWidth: 150,
        			labelAlign: 'right',
                    format: 'Y-m',
                    onOkClick: function (picker, value) {
							        var me = this;
							        var month = value[0];
							        var year = value[1];
							        this.picker.hide();
							        //this.blur();
							        month = month+1;
							        if(month < 10)
							        	month = '0'+month;
							        year = year.toString();
							        FileUploadName_store.clearFilter();
							        FileUploadName_store.filter('Year',year);
							        FileUploadName_store.filter('Month',month);
							        FileUploadName_store.load();
							        
					}
        		});
        		
        		
        		
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}

        } 
        
        else if(title == '月度安全生产计划汇总')
        {	
        	dataStore = store_Monthplansum;
        	
        	dataStore.load();
			queryURL = 'GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '月度安全生产计划' + "&projectName=" + '';
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 400 },
					{ text: '年份', dataIndex: 'Year', align: 'center', width: 200 },
					{ text: '月度安全生产计划', dataIndex: 'FileName', align: 'center', width: 400 }
				]
			addBtn();
			removeBtn();
			//tbar.add(btnAllotask);
			//tbar.add(btnFileUpload);
        		tbar.add(btnscanFileUploadSum);

        } 
        
        else if(title == '分包方安全生产计划管理')
        {	
        	dataStore = store_Fbplan;
        	
        	queryURL = 'GoalDutyAction!getFbplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    //{ text: '报备序号', dataIndex: 'No', align: 'center', width: 250},
            	    { text: '分包单位名称', dataIndex: 'Name', align: 'center', width: 250},
            	    { text: '报备时间', dataIndex: 'Date', align: 'center', width: 250},
            	    { text: '安全生产计划名称', dataIndex: 'PlanName', align: 'center', width: 250}
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
            	   // { text: '安全生产计划', dataIndex: 'Accessory', align: 'center', width: 700}
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
        
        
        else if ( title == '安全生产责任书签订')
        {	
        	dataStore = store_Saveprodbook;
        	queryURL = 'GoalDutyAction!getSaveprodbookListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '责任书类别（院级、项目级）', dataIndex: 'Type', align: 'center', width: 350},
            	    { text: '年份', dataIndex: 'TimeYear', align: 'center', width: 100},
            	    { text: '签订对象（设计院、分包单位名称）', dataIndex: 'ToTarget', align: 'center', width: 350},
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
//jianglf-------------------------------------------------------------------------        
        else if ( title == '安全生产工作策划')
        {	
        	dataStore = store_Saveproplan;
        	queryURL = 'GoalDutyAction!getSaveproplanListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '成立安委会', dataIndex: 'Anweihui', align: 'center', width: 200},
            	    { text: '“四个责任体系”建设', dataIndex: 'FourBuild', align: 'center', width: 250},
            	    { text: '编制安全生产（含三项业务）费用投入计划', dataIndex: 'SaveBuild', align: 'center', width: 350},
            	    { text: '安全检查及隐患排查治理', dataIndex: 'SaveCheck', align: 'center', width: 350},
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
        		tbar.add(btnAllotask);
        		
        		if(user.role == '全部项目') {
        			dataStore.getProxy().url = 'GoalDutyAction!getSaveproplanListDef?userName=' + user.name + "&userRole=" + user.role + "&projectName=" + "";
        			dataStore.load({ params: { start: 0, limit: psize } });  //重新加载store
        			
        			//给第一列添加所属项目，dataindex自己根据实际字段改
        			var newline = [{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 80}];
        			column = Ext.Array.merge(column[0],newline,column.slice(1,column.length));
        			
        		}
        		
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}

        } 
 //emd--------------------------------------------------------------------------------------       
        else if ( title == '安委会')
        {	
        	dataStore = store_anweihui;
        	queryURL = 'GoalDutyAction!getAnweihuiListSearch?userName=' + user.name + "&userRole=" + user.role  + "&Type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
            	    { text: '安委会成立/调整', dataIndex: 'Esorad', align: 'center', width: 150},
            	    { text: '成立/调整时间', dataIndex: 'Time', align: 'center', width: 150},
            	    { text: '安委会主任', dataIndex: 'Head', align: 'center', width: 150},
            	    { text: '安委会副主任', dataIndex: 'ViceHead', align: 'center', width: 200},
            	    { text: '成员组成', dataIndex: 'Form', align: 'center', width: 150},
            	    { text: '工作机构', dataIndex: 'Agency', align: 'center', width: 150},
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
 //jianglf-------------------------------------------------------------------------------   
        
        //88:年度安全生产目标    90:安全生产目标检查与纠偏    91:分包方目标管理
        else if (tableID == 88 || tableID == 90 ||tableID == 91 )
        {	
        	dataStore = store_saveproduct;
        	queryURL = 'GoalDutyAction!getSaveproductListSearch?userName=' + user.name + "&userRole=" + user.role + "&type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
            	    { text: '目标年份', dataIndex: 'TimeYear', align: 'center', width: 150},
            	    { text: '项目安全评估率', dataIndex: 'EvaluateRate', align: 'center', width: 150},
            	    { text: '项目安全总监到位率', dataIndex: 'ReachRate', align: 'center', width: 200},
            	    { text: '工作场所环境合格率', dataIndex: 'EnviPassRate', align: 'center', width: 150},
            	    { text: '轻伤事故率', dataIndex: 'AcciRate', align: 'center', width: 150},
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
        		tbar.add(btnAllotask);
        		if(tableID == 90 ||tableID == 91){
        			tbar.remove(btnAllotask);
        		}
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}

        } 
        
        else if ( tableID == 86 )
        {	
        	dataStore = store_saveproductzt;
        	queryURL = 'GoalDutyAction!getSaveproductListSearch?userName=' + user.name + "&userRole=" + user.role + "&type=" + title+ "&projectName=" + projectName;
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},
            	    { text: '项目安全评估率', dataIndex: 'EvaluateRate', align: 'center', width: 150},
            	    { text: '项目安全总监到位率', dataIndex: 'ReachRate', align: 'center', width: 200},
            	    { text: '工作场所环境合格率', dataIndex: 'EnviPassRate', align: 'center', width: 150},

            	    { text: '一般及以上', dataIndex: 'FireAcci', align: 'center', width: 150},
            	    { text: '轻伤事故率', dataIndex: 'AcciRate', align: 'center', width: 150},
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
 //end--------------------------------------------------------------------------------------       
        
       
        
        
        else if ( title == '年度目标分解')
        {	
        	dataStore = store_goaldecom;
        	queryURL = 'GoalDutyAction!getGoaldecomListSearch?userName=' + user.name + "&userRole=" + user.role+ "&projectName=" + projectName;  
        	//dataStore.getProxy().url = encodeURI(queryURL);
        	//style = "write";
        	column = [
            	    { text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
            	    { text: '目标内容', dataIndex: 'Content', align: 'center', width: 150},
            	    { text: '目标值', dataIndex: 'Mvalue', align: 'center', width: 200},
            	    { text: '实施措施', dataIndex: 'Measure', align: 'center', width: 150},
            	    { text: '完成时间', dataIndex: 'Time', align: 'center', width: 150},
            	    { text: '责任人', dataIndex: 'Manager', align: 'center', width: 150},
            	    { text: '完成情况', dataIndex: 'Completed', align: 'center', width: 150}
            	    //{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 150}
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
        		if(user.role === '其他管理员' || user.role === '院领导') {
        			tbar.remove(btnAdd);
        			tbar.remove(btnEdit);
        			tbar.remove(btnDel);
        		}

        } 
        
        else if ( title == '目标指标实施汇总')
        {	
        	dataStore = store_goaldecomsum;
        	dataStore.load();
			queryURL = 'GoalDutyAction!getFileUploadNameList?userName=' + user.name + '&userRole=' + user.role + '&title=' + '年度目标分解' + "&projectName=" + '';
			column = [
					{ text: '序号',xtype: 'rownumberer',width: 40,sortable: false},	
					{ text: '所属项目', dataIndex: 'ProjectName', align: 'center', width: 400 },
					{ text: '年份', dataIndex: 'Year', align: 'center', width: 200 },
					{ text: '目标指标实施表', dataIndex: 'FileName', align: 'center', width: 400 }
				]
			addBtn();
			removeBtn();
			//tbar.add(btnAllotask);
			//tbar.add(btnFileUpload);
        		tbar.add(btnscanFileUploadSum);

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
          	                		
          	                		/************************* start *********************/
          	                		
          	                		var actionId = -1;
          	                		if (title == '安全生产责任书签订') actionId = 1;
          	                		else if(title == '标准化创建方案') actionId = 27;
          	                		else if(title == '标准化绩效评定') actionId = 28;
          	                		else if(title == '节能减排统计监测') actionId = 33;
          	                		else if(title == '总体安全生产目标') actionId = 15;
          	                		else if(title == '年度安全生产目标') actionId = 16;
          	                		else if(title == '年度目标分解') actionId = 17;
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
          	                		
          	                		/************************* start *********************/
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
        	
            if (config.action == 'editProject') {
            	selRecs = [];  //清空数组
                selRecs = gridDT.getSelectionModel().getSelection();
                forms.getForm().loadRecord(selRecs[0]);  //加载选中记录数据
            }
            if(config.action == 'alloTask')
        	{
               var selRecs = gridDT.getSelectionModel().getSelection();
    		   // 设置表单初始值 		   
    		   forms.getForm().findField('missionname').setValue(selRecs[0].data.No);
        	}
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
        				btnScan2.enable();
        			}
        			else
        			{
        				btnAllotask.disable();
        				btnEdit.disable();
        				btnScan.disable();
        				btnScan2.disable();
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
                'celldblclick': function (self, td, cellIndex, record, tr, rowIndex)
        	    {
                	if(title == '安全生产责任书签订')
                	{
                		var html_str = "";
                		if(title == '安全生产责任书签订')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		        html_str = "<h1 style=\"padding: 5px;\"><center>"+title+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">项目名称</td><td width=\"40%\">" + projectName + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">责任书类别（院级、项目级）</td><td>" + record.get('Type') + "</td><td style=\"padding:5px;\">年份</td><td>" + record.get('TimeYear') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">签订对象（设计院、分包单位名称）</td><td colspan=\"3\">" + record.get('ToTarget') + "</td></tr>"
                              html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">"
                              
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
                	else if(title == '年度工作计划')
                	{
                		var html_str = "";
                		if(title == '年度工作计划')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr><td style=\"padding:5px;\">年份</td><td>" + record.get('Year') + "</td><td style=\"padding:5px;\">责任部门</td><td>" + record.get('Manager') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">相关单位</td><td>" + record.get('Workload') + "</td><td style=\"padding:5px;\">完成情况说明</td><td>" + record.get('Completed') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">计划完成时间</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('PlanDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">实际完成时间</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('RealDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">任务内容</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('Content') + "</td></tr>";
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
                	
                	else if(title == '年度安全生产计划汇总')
                	{
                		var html_str = "";
                		if(title == '年度安全生产计划汇总')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr><td style=\"padding:5px;\">年份</td><td>" + record.get('Year') + "</td><td style=\"padding:5px;\">责任部门</td><td>" + record.get('Manager') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">相关单位</td><td>" + record.get('Workload') + "</td><td style=\"padding:5px;\">完成情况说明</td><td>" + record.get('Completed') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">计划完成时间</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('PlanDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">实际完成时间</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('RealDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">任务内容</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('Content') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">所属项目</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('ProjectName') + "</td></tr>";
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
                	
//jianglf-----------------------------------------------------------------------------
                	else if(title == '安全生产管理机构')
                	{
                		var html_str = "";
                		if(title == '安全生产管理机构')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		      
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr><td style=\"padding:5px;\">机构名称</td><td colspan=\"3\">" + record.get('Name') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">负责人</td><td colspan=\"3\">" + record.get('Person') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">安全管理人员</td><td colspan=\"3\">" + record.get('Sperson') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">成立时间</td><td colspan=\"3\">" + record.get('Time') + "</td></tr>"
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
                	
                	else if(title == '分包方安全生产管理机构')
                	{
                		var html_str = "";
                		if(title == '分包方安全生产管理机构')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         
             		      
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr><td style=\"padding:5px;\">机构名称</td><td colspan=\"3\">" + record.get('Name') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">分包单位名称</td><td colspan=\"3\">" + record.get('FbName') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">负责人</td><td colspan=\"3\">" + record.get('Person') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">安全管理人员</td><td colspan=\"3\">" + record.get('Sperson') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">成立时间</td><td colspan=\"3\">" + record.get('Time') + "</td></tr>"
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
                	
                	else if(title == '三项业务年度目标分解及工作计划')
                	{
                		var html_str = "";
                		if(title == '三项业务年度目标分解及工作计划')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr><td style=\"padding:5px;\">年份</td><td>" + record.get('Year') + "</td><td style=\"padding:5px;\">责任人</td><td>" + record.get('Manager') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">相关单位</td><td>" + record.get('Workload') + "</td><td style=\"padding:5px;\">完成情况说明</td><td>" + record.get('Completed') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">计划完成时间</td><td colspan=\"3\">" + record.get('PlanDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">实际完成时间</td><td colspan=\"3\">" + record.get('RealDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">任务内容</td><td colspan=\"3\">" + record.get('Content') + "</td></tr>";
             		        html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
              		        
           		         var misfile = record.get('Accessory').split('*');
//           		        var foldermis;
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
                	
                	else if(title == '三项业务目标计划汇总')
                	{
                		var html_str = "";
                		if(title == '三项业务目标计划汇总')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr><td style=\"padding:5px;\">年份</td><td>" + record.get('Year') + "</td><td style=\"padding:5px;\">责任人</td><td>" + record.get('Manager') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">相关单位</td><td>" + record.get('Workload') + "</td><td style=\"padding:5px;\">完成情况说明</td><td>" + record.get('Completed') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">计划完成时间</td><td colspan=\"3\">" + record.get('PlanDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">实际完成时间</td><td colspan=\"3\">" + record.get('RealDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">任务内容</td><td colspan=\"3\">" + record.get('Content') + "</td></tr>";
             		        html_str+="<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">";
              		        
           		         var misfile = record.get('Accessory').split('*');
//           		        var foldermis;
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
//end----------------------------------------------------------------------------------                	
                	
                	else if(title == '月度工作计划')
                	{
                		var html_str = "";
                		if(title == '月度工作计划')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr><td style=\"padding:5px;\">年份</td><td>" + record.get('Year') + "</td><td style=\"padding:5px;\">月份</td><td>" + record.get('Month') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">计划内容</td><td>" + record.get('Content') + "</td><td style=\"padding:5px;\">责任部门</td><td>" + record.get('Manager') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">完成情况说明</td><td>" + record.get('Completedsm') + "</td><td style=\"padding:5px;\">相关单位</td><td>" + record.get('Unit') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">计划完成时间</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('PlanDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">实际完成时间</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('RealDate') + "</td></tr>"
                              html_str+="<tr><td style=\"padding:5px;\">《办毕验证材料》</td><td colspan=\"3\">";
             		         
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
                	
                	else if(title == '月度安全生产计划汇总') {
                		var html_str = "";
                		if(title == '月度安全生产计划汇总')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr><td style=\"padding:5px;\">年份</td><td>" + record.get('Year') + "</td><td style=\"padding:5px;\">月份</td><td>" + record.get('Month') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">计划内容</td><td>" + record.get('Content') + "</td><td style=\"padding:5px;\">责任部门</td><td>" + record.get('Manager') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">完成情况说明</td><td>" + record.get('Completedsm') + "</td><td style=\"padding:5px;\">相关单位</td><td>" + record.get('Unit') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">计划完成时间</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('PlanDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">实际完成时间</td><td style=\"text-align:center;padding:10px;\" colspan=\"3\">" + record.get('RealDate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">《办毕验证材料》</td><td colspan=\"3\">" + record.get('Accessory') + "</td><tr>\
                              \<tr><td style=\"padding:5px;\">所属项目</td><td colspan=\"3\">" + record.get('ProjectName') + "</td><tr>";
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
                	
                	else if(title == '目标指标实施汇总')
                	{
                		var html_str = "";
                		if(title == '目标指标实施汇总')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		        var misfile = record.get('Accessory').split('*');
             		        var fileName;
             		       for(var i = 2;i<misfile.length;i++){
           		        	fileName = misfile[i];
             		       }
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr></td><td style=\"padding:5px;\">所属项目</td><td colspan=\"3\">" + record.get('ProjectName')+"</td></tr>\
                              \<tr><td style=\"padding:5px;\">年份</td><td colspan=\"3\">" + record.get('Year') + "</td></tr>"
                              	 html_str += "<tr><td style=\"padding:5px;\">目标指标实施表</td><td colspan=\"3\">";
             		         
             		        var misfile = record.get('FileName').split('*');
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
                	
                	else if(title == '分包方安全生产计划管理')
                	{
                		var html_str = "";
                		if(title == '分包方安全生产计划管理')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		        var misfile = record.get('Accessory').split('*');
             		        var fileName;
             		       for(var i = 2;i<misfile.length;i++){
           		        	
           		        	fileName = misfile[i];
             		       }
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr>\
                              \<tr></td><td style=\"padding:5px;\">分包单位名称</td><td colspan=\"3\">" + record.get('Name')+"</td></tr>\
                              \<tr><td style=\"padding:5px;\">安全生产计划名称</td><td colspan=\"3\">" + record.get('PlanName') + "</td></tr>\
                              	\<td style=\"padding:5px;\">报备时间</td><td colspan=\"3\">" + record.get('Date') + "</td></tr>"
                              	 html_str += "<tr><td style=\"padding:5px;\">《安全生产计划》</td><td colspan=\"3\">";
             		         
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
                	else if(title == '安全生产工作策划' )
                	{
                		var html_str = "";
                		if(title == '安全生产工作策划' )
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+title+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">项目名称</td><td width=\"40%\">" + projectName + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">成立安委会</td><td>" + record.get('Anweihui') + "</td><td style=\"padding:5px;\">成立三项业务领导小组</td><td>" + record.get('ThreeGroup') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">“四个责任体系”建设</td><td>" + record.get('FourBuild') + "</td><td style=\"padding:5px;\">编制安全生产（含三项业务）费用投入计划</td><td>" + record.get('SaveBuild') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">编制安全教育培训计划</td><td>" + record.get('SavePlan') + "</td><td style=\"padding:5px;\">安全检查及隐患排查治理</td><td>" + record.get('SaveCheck') + "</td></tr>\
								\<tr><td style=\"padding:5px;\">编制专项施工方案</td><td>" + record.get('BuildPlan') + "</td><td style=\"padding:5px;\">编制应急预案及现场处置方案</td><td>" + record.get('HandlePlan') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">安全生产标准化及文明施工划策</td><td>" + record.get('SaveBuildPlan') + "</td><td style=\"padding:5px;\">危险源辨识与发布</td><td>" + record.get('DangerPublic') + "</td></tr>\
                            \<tr><td style=\"padding:5px;\">制定安全生产强制性条文执行计划</td><td>" + record.get('ExecutePlan') + "</td><td style=\"padding:5px;\">编制三项业务保障措施及工作计划</td><td>" + record.get('WorkPlan') + "</td></tr>" 
                            
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
                	
                	else if(title == '安委会')
                	{
                		var html_str = "";
                		if(title == '安委会')
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+title+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">项目名称</td><td width=\"40%\">" + projectName + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">安委会成立/调整</td><td>" + record.get('Esorad') + "</td><td style=\"padding:5px;\">成立/调整时间</td><td>" + record.get('Time') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">安委会主任</td><td>" + record.get('Head') + "</td><td style=\"padding:5px;\">副主任</td><td>" + record.get('ViceHead') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">成员组成</td><td>" + record.get('Form') + "</td><td style=\"padding:5px;\">工作机构</td><td>" + record.get('Agency') + "</td></tr>"
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
                            
                          //  \<tr><td style=\"padding:5px;\">附件</td><td colspan=\"3\">" + record.get('pAttachment') + "</td><tr>";
             		         html_str += "</table>";
                		}  		
                        Ext.create('Ext.window.Window', 
                        {
                           title: '查看详情',
                           titleAlign: 'center',
                           height: 600,
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
                	
                	else if(title == '年度目标分解' )
                	{
                		var html_str = "";
                		if(title == '年度目标分解' )
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+title+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">项目名称</td><td width=\"40%\">" + projectName + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">目标内容</td><td>" + record.get('Content') + "</td><td style=\"padding:5px;\">目标值</td><td>" + record.get('Mvalue') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">实施措施</td><td>" + record.get('Measure') + "</td><td style=\"padding:5px;\">完成时间</td><td>" + record.get('Time') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">责任人</td><td>" + record.get('Manager') + "</td><td style=\"padding:5px;\">完成情况</td><td>" + record.get('Completed') + "</td></tr>" 
                            
                            
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
                	
                	else if(tableID == 86 || tableID == 88 || tableID >= 90 &&  tableID <= 91)
                	{
                		var html_str = "";
                		if( tableID == 86 || tableID == 88 || tableID >= 90 &&  tableID <= 91 )
                		{
                			var record = dataStore.getAt(rowIndex);
             		         var Num = rowIndex+1;
             		         //alert(record.get('pName'));
             		         html_str = "<h1 style=\"padding: 5px;\"><center>"+title+"详细信息</center></h1><table width=\"98%\" border=\"2\" style=\"table-layout:fixed;margin:auto;border-collapse:collapse;text-align:center;\"><tr><td width=\"15%\" style=\"padding:5px;\">序号</td ><td>" + Num + "</td><td width=\"15%\" style=\"padding:5px;\">项目名称</td><td width=\"40%\">" + projectName + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">项目安全评估率</td><td>" + record.get('EvaluateRate') + "</td><td style=\"padding:5px;\">员工（包括分包单位）岗前安全培训、操作技能培训率</td><td>" + record.get('TrainRate') + "</td></tr>\
                              \<tr><td style=\"padding:5px;\">安全生产隐患限期整改率</td><td>" + record.get('ReformRate') + "</td><td style=\"padding:5px;\">项目安全总监到位率</td><td>" + record.get('ReachRate') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">建设项目的重点部位和关键环节安全措施的审批及交底率</td><td>" + record.get('ExamineRate') + "</td><td style=\"padding:5px;\">设计部</td><td>" + record.get('Design') + "</td></tr>\\<tr><td style=\"padding:5px;\">事故瞒报、谎报、拖延不报行为</td><td>" + record.get('Behave') + "</td><td style=\"padding:5px;\">职业病危害事件</td><td>" + record.get('JobEvent') + "</td></tr>\
								\<tr><td style=\"padding:5px;\">工作场所环境合格率</td><td>" + record.get('EnviPassRate') + "</td><td style=\"padding:5px;\">因工程勘察设计原因造成的人身伤亡事故和较大财产损失事故</td><td>" + record.get('WorkAcci') + "</td></tr>\\<tr><td style=\"padding:5px;\">一般及以上设备事故、直接经济损失20万元/次及以上的火灾事故</td><td>" + record.get('FireAcci') + "</td><td style=\"padding:5px;\">负主责的一般及以上交通事故、负管理责任的建设项目地质灾害</td><td>" + record.get('Disaster') + "</td></tr>\
							\<tr><td style=\"padding:5px;\">危险性较大分部、分项工程专项安全技术措施编制、审批、交底率</td><td>" + record.get('BottomRate') + "</td><td style=\"padding:5px;\">从事接触职业病危害作业劳动者的职业健康体检率</td><td>" + record.get('CheckRate') + "</td></tr>\\<tr><td style=\"padding:5px;\">生产性轻伤、重伤和死亡责任事故</td><td>" + record.get('ProdAcci') + "</td><td style=\"padding:5px;\">轻伤事故率</td><td>" + record.get('AcciRate') + "</td></tr>\
                            \<tr><td style=\"padding:5px;\">工作场所职业病危害告知率、职业病危害因素监测率、主要危害因素监测合格率</td><td>" + record.get('SickPassRate') + "</td><td style=\"padding:5px;\">分包单位生产性重伤和死亡责任事故</td><td>" + record.get('FenBaoAcci') + "</td></tr>" 
                            
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
       	        }
        	}
        });
        panel.add(gridDT);
        container.add(panel).show();
        /*if (tableID == 83) {
        	gridDT.columns[2].hide(); }*/

    }
});