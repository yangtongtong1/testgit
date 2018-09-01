/// <reference path="extjs/ext-all.js" />
/**
 * 多文件上传组件 
 * for extjs4.0
 * @author caizhiping
 * @since 2012-11-15
 */

Ext.define('Ext.ux.uploadPanel.UploadPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.uploadpanel',
    width: 700,
    height: 300,
   	
    columns: [
        { xtype: 'rownumberer' },
		{ text: '文件名', width: 200, dataIndex: 'name' },
        { text: '类型', width: 70, dataIndex: 'type' },
        {
            text: '大小', width: 70,align:'center', dataIndex: 'size', renderer: function (v) {
                return Ext.util.Format.fileSize(v);
            }
        },
        {
            text: '进度', width: 130, dataIndex: 'percent', renderer: function (v) {
                var stml =
                    '<div>' +
                        '<div style="border:1px solid #008000;height:10px;width:115px;margin:2px 0px 1px 0px;float:left;">' +
                            '<div style="float:left;background:#FFCC66;width:' + v + '%;height:8px;"><div></div></div>' +
                        '</div>' +
                    //'<div style="text-align:center;float:right;width:40px;margin:3px 0px 1px 0px;height:10px;font-size:12px;">{3}%</div>'+			
                '</div>';
                return stml;
            }
        },
        {
            text: '状态', width:200, align:'center', dataIndex: 'status', renderer: function (v) {
                var status;
                if (v == -1) {
                    status = "等待上传";
                } else if (v == -2) {
                    status = "上传中...";
                } else if (v == -3) {
                    status = "<div style='color:red;'>上传失败</div>";
                } else if (v == -4) {
                    status = "上传成功";
                } else if (v == -5) {
                    status = "停止上传";
                } else if (v == -6){
                	status = "上传失败";
                } else if (v == -7){
                	status = "上传失败,请先上传专利信息";
                }
                else if (v == -8){
                	status = "上传失败,该包不属于武汉大学";
                }else if (v == -9){
                	status = "已上传";
                }
                else {
                    status = "<div style='color:red;'>"+v+"</div>";
                }
                return status;
            }
        },
        {  
        	text: '移除',
           	xtype: 'actioncolumn',
           	width: 50,
           	items: [{
           		icon: 'Images/ims/toolbar/delete.gif',
                tooltip: '移除上传文件',
                handler: function (grid, rowIndex, colIndex) {
                	var upload_path = grid.store.getAt(rowIndex).get('path');
                	var file_name = grid.store.getAt(rowIndex).get('name');
                	var ppid = grid.store.getAt(rowIndex).get('ppid');
                	var delete_url = grid.store.getAt(rowIndex).get('deleteurl');
                	 Ext.Ajax.request({
                         method : 'POST',
                         url: 'BasicInfoAction!deleteOneFile',
                         params : {                     
                            name:file_name,
                            id:ppid
                         },
                         success: function(){
                         	 //bbr.moveFirst();
                         	 //store.load({ params: { start: 0, limit: psize } });         	
                         }
                        });
                    //var id = grid.store.getAt(rowIndex).get('id');
                    grid.store.remove(grid.store.getAt(rowIndex));
                 }
            }]
         } 
    ],
    plugins: [
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })
    ],
    store: Ext.create('Ext.data.JsonStore', {
        autoLoad: false,
        //storeId:'swfFilesStore',
        fields: ['id', 'name', 'type', 'size', 'percent', 'status', 'fileName', 'level','ppid','deleteurl']
    }),
    addFileBtnText: 'Add File',
    uploadBtnText: 'Upload',
    removeBtnText: 'Remove All',
    cancelBtnText: 'Cancel', 
    typePar: '123',
    debug: false,
    file_size_limit: 100,//MB
    file_types: '*.*',
    file_types_description: 'All Files',
    file_upload_limit: 50,
    file_queue_limit: 0,
    post_params: {},
    upload_url: '',
    user: '',
    flash_url: "Scripts/swfupload.swf",
    flash9_url: "swfupload/swfupload_fp9.swf",
    failedFiles: [],  //存储上传失败的文件ID
    initComponent: function () {
//    	this.store.add({
//            id: "123",
//            name: this.existFile,
//            fileName:"",
//            level: '普通',
//            size: "123",
//            type: ".pdf",
//            status: "等待上传",
//            percent: 0
//        });
        this.btnAddFile = Ext.create('Ext.Button', {
            icon: 'Images/ims/toolbar/add.gif',            
            text: this.addFileBtnText
        });
        this.btnUpload = Ext.create('Ext.Button', {
            icon: 'Images/ims/toolbar/upload.png',
            text: this.uploadBtnText,
            scope: this,
            handler: this.onUpload
        });
        this.btnRemove = Ext.create('Ext.Button', {
            icon: 'Images/ims/toolbar/delete.gif',
            text: this.removeBtnText,
            scope: this,
            handler: this.onRemove
        });
        this.btnCancel = Ext.create('Ext.Button', {
            icon: 'Images/ims/toolbar/delete.gif',
            disabled: true,
            text: this.cancelBtnText,
            scope: this,
            handler: this.onCancelUpload
        });
        this.dockedItems = [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
                this.btnAddFile, { xtype: 'tbseparator' }, this.btnUpload, { xtype: 'tbseparator' },this.btnRemove , { xtype: 'tbseparator' }, this.btnCancel
            ]
        }];

        this.callParent();
        this.btnAddFile.on({
            afterrender: function (btn) {
                var config = this.getSWFConfig(btn);
                this.swfupload = new SWFUpload(config);
                Ext.get(this.swfupload.movieName).setStyle({
                    position: 'absolute',
                    top: 0,
                    left: -2
                });
            },
            scope: this,
            buffer: 300
        });
    },
    getSWFConfig: function (btn) {
        var me = this;
        var placeHolderId = Ext.id();
        var em = btn.getEl().child('em');
        if (em == null) {
            em = Ext.get(btn.getId() + '-btnWrap');
        }
        em.setStyle({
            position: 'relative',
            display: 'block'
        }); 
        em.createChild({
            tag: 'div',
            id: placeHolderId
        });
        
        return {
            debug: me.debug,
            flash_url: me.flash_url,
            flash9_url: me.flash9_url,
            upload_url: me.upload_url,//+"&sm="+str,            
            post_params: me.post_params ,
            file_size_limit: (me.file_size_limit * 1024),
            file_types: me.file_types,
            file_types_description: me.file_types_description,
            file_upload_limit: me.file_upload_limit,
            file_queue_limit: me.file_queue_limit,
            button_width: em.getWidth(),
            button_height: em.getHeight(),
            button_window_mode: SWFUpload.WINDOW_MODE.TRANSPARENT,
            button_cursor: SWFUpload.CURSOR.HAND,
            button_placeholder_id: placeHolderId,
            custom_settings: {
                scope_handler: me
            },
            swfupload_preload_handler: me.swfupload_preload_handler,
            file_queue_error_handler: me.file_queue_error_handler,
            swfupload_load_failed_handler: me.swfupload_load_failed_handler,
            upload_start_handler: me.upload_start_handler,
            upload_progress_handler: me.upload_progress_handler,
            upload_error_handler: me.upload_error_handler,
            upload_success_handler: me.upload_success_handler,
            upload_complete_handler: me.upload_complete_handler,
            file_queued_handler: me.file_queued_handler/*,
			file_dialog_complete_handler : me.file_dialog_complete_handler*/
        };
    },
    swfupload_preload_handler: function () {
        if (!this.support.loading) {
            Ext.Msg.show({
                title: '提示',
                msg: '浏览器Flash Player版本太低,不能使用该上传功能！',
                width: 250,
                icon: Ext.Msg.ERROR,
                buttons: Ext.Msg.OK
            });
            return false;
        }
    },
    file_queue_error_handler: function (file, errorCode, message) {
        switch (errorCode) {
            case SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED: msg('上传文件列表数量超限,不能选择！');
                break;
            case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT: msg('文件大小超过限制, 不能选择！');
                break;
            case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE: msg('该文件大小为0,不能选择！');
                break;
            case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE: msg('该文件类型不允许上传！');
                break;
        }
        function msg(info) {
            Ext.Msg.show({
                title: '提示',
                msg: info,
                width: 250,
                icon: Ext.Msg.WARNING,
                buttons: Ext.Msg.OK
            });
        }
    },
    swfupload_load_failed_handler: function () {
        Ext.Msg.show({
            title: '提示',
            msg: 'SWFUpload加载失败！',
            width: 180,
            icon: Ext.Msg.ERROR,
            buttons: Ext.Msg.OK
        });
    },
    upload_start_handler: function (file) {
        var me = this.settings.custom_settings.scope_handler;
        me.btnCancel.setDisabled(false);        
        var rec = me.store.getById(file.id);
        //向后台传参        
        this.setPostParams({
            name: rec.get('name'),
            type: me.typePar
        }); 

        this.setFilePostName(encodeURIComponent(rec.get('fileName')));
    },
    upload_progress_handler: function (file, bytesLoaded, bytesTotal) {
        var me = this.settings.custom_settings.scope_handler;
        var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);
        percent = percent == 100 ? 99 : percent;
        var rec = me.store.getById(file.id);
        rec.set('percent', percent);
        rec.set('status', file.filestatus);
        rec.commit();
    },
    upload_error_handler: function (file, errorCode, message) {
        var me = this.settings.custom_settings.scope_handler;
        var rec = me.store.getById(file.id);
        rec.set('percent', 100);
        rec.set('status', -6); 
        rec.commit();
        
        if (this.getStats().files_queued > 0 && this.uploadStopped == false) {
            this.startUpload();
        } else {
            me.showBtn(me, true);
        }
    },
   
    upload_success_handler: function (file, serverData, responseReceived) {
        var me = this.settings.custom_settings.scope_handler;
        var rec = me.store.getById(file.id);
        var ret = Ext.JSON.decode(serverData);
        if (ret.success) {
            rec.set('percent', 100);
            rec.set('status', file.filestatus);
//            alert(serverData);
            if(ret.newFilename)
            {
				//*************************
            	var i=0;
            	for ( var p in ret )
            	{     
            		if(i>1){
            			
            			//******************yangtong**********************
            			if(p=="saftycheck_problem"){
            				 clearall();
            				 var editProblems = ret[p].split("*");
            		    	 var editProblemsNum = editProblems.length;
            		    	 if(editProblemsNum==0)
            		    	  {
            		    		  
            		    	  }
            		    	  else if(editProblemsNum==1)
            		    	  {
            		    		  store_saftyproblem.load();
            		    		  forms.getForm().findField('problem1').setValue(editProblems[0]);
            		    	  }
            		    	  else
            		    	  {
            		    		  store_saftyproblem.load();
            		    		  forms.getForm().findField('problem1').setValue(editProblems[0]);
            		    		  problemNum = 1;
            		    		  for(var j = 2;j<=editProblemsNum;j++)
            		    		  {
            		    			  addEdit();
            		    			  forms.getForm().findField('problem'+j).setValue(editProblems[j-1]);
            		    		  }
            		    	  }
            			}
            			else if(p=="saftycheck_advice")
            			{
            				clearalladv();
            		    	var editAdvices = ret[p].split("*");
            		    	var editAdvicesNum = editAdvices.length;
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
            		  			  forms.getForm().findField('advice'+j).setValue(editAdvices[j-1]);
            		  		  }
            		  	  }
            		    
            			}
            			//******************yangtong**********************
            			else
            			{
            			    forms.getForm().findField(p).setValue(ret[p].replace("<br>", "\r"));
//            			    alert(p+":"+ret[p]);
            			}
            		}
            		i++;
            	}        
                //************************				
            	rec.set('name',ret.newFilename);
            } 
            //this.down('actioncolumn').hide();
        } else {
            rec.set('percent', 0);
            rec.set('status', parseInt(ret.msg));
            var me = this.settings.custom_settings.scope_handler;
            me.failedFiles.push(file.id);
            //Ext.getCmp('upload-grid').failedFiles.push(file.id);                     
        }
        rec.commit();
        if (this.getStats().files_queued > 0 && this.uploadStopped == false) {
            this.startUpload();
        } else {
            me.showBtn(me, true);
        }
        
        //***************yangtong****************************
        function addEdit()
        {
      	  var textgf = forms.getForm().findField('problem1');
      	  var fm1 = textgf.up('container');
          	
//          	problemNum = problemNum+1;
      	  problemNum = checkNum();
          	
          	if(problemNum>10){
          		alert("问题数过多!");
          	}else{
          		var cmp1 = fm1.add({
                  	xtype:'textfield',
                      //labelWidth: 120,
                      fieldLabel: '整改问题'+problemNum,
                      labelAlign: 'right',
//                      width:685,
                      margin:'6 0 0 0',
                      columnWidth : .57,
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
//  	                 blankText: '请选择政治面貌',
  	                 editable: true,
  	                 mode: 'remote',
//                      labelWidth: 120,
                      labelAlign: 'right',
//                      anchor:'20%',
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
//                      columnWidth:0.1,
                      name: 'delproblem'+problemNum,
                      listeners: { click: function (btn) {
                          	var textgf = forms.getForm().findField('problem1');
                        	    var fm1 = textgf.up('container');
                        	    fm1.remove(forms.getForm().findField('problem'+btn.name.substring(10,11)));
                        	    fm1.remove(forms.getForm().findField('kind'+btn.name.substring(10,11)));
                        	    fm1.remove(forms.getForm().findField('degree'+btn.name.substring(10,11)));
                        	    fm1.remove(btn);
//                        	    refresh();
                        	    refresh2();
                       }
                      }
                  });
          		refresh2();
          	}
        }
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
        function refresh2 (){
     	   var newNum = 2;
     	   var textgf = forms.getForm().findField('problem1');
     	   var tem = textgf.nextSibling();
     	   while(tem!=null)
     	   {
     		   if(tem.name.substring(0,7)=="problem")
     		   {
//     			   alert(tem.name);
     			   tem.setFieldLabel("整改问题"+newNum);
     			   newNum++;
     		   }
     		  tem = tem.nextSibling();
     	   }
        }
        function clearall()
        {
      	  var textgf = forms.getForm().findField('degree1');
      	  var parent = textgf.up('container');
      	  var tmp = textgf.nextSibling();
//      	 alert(parent.name+tmp.name);
      	  while(tmp!=null)
      	  {
      		  var next = tmp.nextSibling();
//      		  alert(tmp.name.substring(0,7));
      		  if(tmp.name.substring(0,7)=="delprob"||tmp.name.substring(0,7)=="problem"||tmp.name.substring(0,4)=="kind"||tmp.name.substring(0,6)=="degree"){
      			  parent.remove(tmp);
      		  }
      		  tmp = next;
      	  }
        }
        
        function addEditadv()
        {
      	  var textgf = forms.getForm().findField('advice1');
      	  var fm1 = textgf.up('container');
          	
//          	problemNum = problemNum+1;
      	  AdviceNum = checkNumadv();
          	
          	if(AdviceNum>10){
          		alert("建议过多!");
          	}else{
          		var cmp1 = fm1.add({
                  	xtype:'textfield',
                      //labelWidth: 120,
                      fieldLabel: '建议'+AdviceNum,
                      labelAlign: 'right',
//                      width:685,
                      margin:'6 0 0 0',
                      columnWidth : .9,
                      anchor:'90%',
                      allowBlank: false,
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
//                      columnWidth:0.1,
                      name: 'deladvice'+AdviceNum,
                      listeners: { click: function (btn) {
                          	var textgf = forms.getForm().findField('advice1');
                        	    var fm1 = textgf.up('container');
                        	    fm1.remove(forms.getForm().findField('advice'+btn.name.substring(9,10)));
                        	    fm1.remove(btn);
//                        	    refresh();
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
        function refresh2adv(){
       	   var newNum = 2;
       	   var textgf = forms.getForm().findField('advice1');
       	   var tem = textgf.nextSibling();
       	   while(tem!=null)
       	   {  
       		   if(tem.name.substring(0,6)=="advice")
       		   {
//       			   alert(tem.name);
       			   tem.setFieldLabel("建议"+newNum);
       			   newNum++;
       		   }
       		  tem = tem.nextSibling();
       	   }
          }
        function clearalladv()
        {
      	  var textgf = forms.getForm().findField('advice1');
      	  var parent = textgf.up('container');
      	  var tmp = textgf.nextSibling();
//      	 alert(parent.name+tmp.name);
      	  while(tmp!=null)
      	  {
      		  var next = tmp.nextSibling();
//      		  alert(tmp.name.substring(0,7));
      		  if(tmp.name.substring(0,6)=="deladv"||tmp.name.substring(0,6)=="advice"){
      			  parent.remove(tmp);
      		  }
      		  tmp = next;
      	  }
        }
      //******************yangtong**********************
        
    },
    upload_complete_handler: function (file) {
        //var me = this;
        if (this.getStats().files_queued === 0) {
            var me = this.settings.custom_settings.scope_handler;
            //var arrFiles = Ext.getCmp('upload-grid').failedFiles;
            var arrFiles = me.failedFiles;
            Ext.each(arrFiles, function (fileId) {
                this.requeueUpload(fileId);
            },this)
        }
    },
    file_queued_handler: function (file) {
        var me = this.settings.custom_settings.scope_handler;
        me.store.add({
            id: file.id,
            name: file.name,
            fileName:"",
            level: '普通',
            size: file.size,
            type: file.type,
            status: file.filestatus,
            percent: 0
        });
    },

    onUpload: function () {        
        if (this.swfupload && this.store.getCount() > 0) {
            var fq = this.swfupload.getStats().files_queued;
            if (this.swfupload.getStats().files_queued > 0) {
                this.showBtn(this, false);
                this.swfupload.uploadStopped = false;
                this.swfupload.startUpload();
            }
        }
    },
    showBtn: function (me, bl) {
        me.btnAddFile.setDisabled(!bl);
        me.btnUpload.setDisabled(!bl);
        me.btnRemove.setDisabled(!bl);
        me.btnCancel.setDisabled(bl);
        if (bl) {
            me.down('actioncolumn').show();
        } else {
            me.down('actioncolumn').hide();
        }
    },
    onRemove: function () {
       
        var ds = this.store;
        for (var i = 0; i < ds.getCount() ; i++) {
            var record = ds.getAt(i);
            var file_id = record.get('id');
            this.swfupload.cancelUpload(file_id, false);
        }
        ds.removeAll();
        this.swfupload.uploadStopped = false;
    },
    onCancelUpload: function () {
        if (this.swfupload) {

            this.swfupload.uploadStopped = true;
            this.swfupload.stopUpload();
            this.showBtn(this, true);
        }
    }
    
  
    
});