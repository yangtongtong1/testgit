/// <reference path="extjs/ext-all.js" />
/**
 * 多文件上传组件 
 * for extjs4.0
 * @author caizhiping
 * @since 2012-11-15
 */
Ext.define('Ext.ux.uploadPanel.UploadPicPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.uploadpanel',
    width: 700,
    height: 300,
    columns: [
        { xtype: 'rownumberer' },
		{ text: '图片名称', width: 100, dataIndex: 'name' },
		{ text: '自定义图片名称', width: 200, dataIndex: 'fileName', editor: { xtype: 'textfield' } },
        { text: '图片描述', width: 200, dataIndex: 'picdesc', editor: { xtype: 'textfield' } },
        {

            text: '密级',
            width: 80,
            dataIndex: 'level',
            editor: new Ext.form.ComboBox({
                store: ['普通', '秘密', '机密', '绝密'],
                value: '普通',
                allowBlank: false,
                forceSelection: true,
                queryMode: 'local'
            })
        },
        { text: '类型', width: 70, dataIndex: 'type' },
        {
            text: '大小', width: 70, dataIndex: 'size', renderer: function (v) {
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
            text: '状态', width: 200, dataIndex: 'status', renderer: function (v) {
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
                }
                else {
                    status = "<div style='color:red;'>" + v + "</div>";
                }
                return status;
            }
        },
        {
            xtype: 'actioncolumn',
            width: 50,
            items: [{
                icon: '/JJCMS/JJCMS/Images/ims/toolbar/delete.gif',
                tooltip: '移除上传文件',
                handler: function (grid, rowIndex, colIndex) {
                    var id = grid.store.getAt(rowIndex).get('id');
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
        fields: ['id', 'name', 'type', 'size', 'percent', 'status', 'fileName', 'level','picdesc']
    }),
    addFileBtnText: 'Add File',
    uploadBtnText: 'Upload',
    removeBtnText: 'Remove All',
    cancelBtnText: 'Cancel',
    debug: false,
    file_size_limit: 100,//MB
    file_types: '*.*',
    file_types_description: 'All Files',
    file_upload_limit: 50,
    file_queue_limit: 0,
    post_params: {},
    upload_url: '',
    flash_url: "/JJCMS/JJCMS/Scripts/swfupload.swf",
    flash9_url: "/JJCMS/JJCMS/swfupload/swfupload_fp9.swf",
    failedFiles: [],  //存储上传失败的文件ID
    initComponent: function () {

        this.btnAddFile = Ext.create('Ext.Button', {
            iconCls: 'add',
            text: this.addFileBtnText
        });
        this.btnUpload = Ext.create('Ext.Button', {
            iconCls: 'up',
            text: this.uploadBtnText,
            scope: this,
            handler: this.onUpload
        });
        this.btnRemove = Ext.create('Ext.Button', {
            iconCls: 'trash',
            text: this.removeBtnText,
            scope: this,
            handler: this.onRemove
        });
        this.btnCancel = Ext.create('Ext.Button', {
            iconCls: 'cancel',
            disabled: true,
            text: this.cancelBtnText,
            scope: this,
            handler: this.onCancelUpload
        });
        this.dockedItems = [{
            xtype: 'toolbar',
            dock: 'top',
            items: [
		        //{
		        //    xtype: 'button',
		        //    itemId: 'addFileBtn',
		        //    iconCls: 'add',
		        //    //id: '_btn_for_swf_',
		        //    text: this.addFileBtnText
		        //}, { xtype: 'tbseparator' }, {
		        //    xtype: 'button',
		        //    itemId: 'uploadBtn',
		        //    iconCls: 'up',
		        //    text: this.uploadBtnText,
		        //    scope: this,
		        //    handler: this.onUpload
		        //}, { xtype: 'tbseparator' }, {
		        //    xtype: 'button',
		        //    itemId: 'removeBtn',
		        //    iconCls: 'trash',
		        //    text: this.removeBtnText,
		        //    scope: this,
		        //    handler: this.onRemove
		        //}, { xtype: 'tbseparator' }, {
		        //    xtype: 'button',
		        //    itemId: 'cancelBtn',
		        //    iconCls: 'cancel',
		        //    disabled: true,
		        //    text: this.cancelBtnText,
		        //    scope: this,
		        //    handler: this.onCancelUpload
		        //}
                this.btnAddFile, { xtype: 'tbseparator' }, this.btnUpload, { xtype: 'tbseparator' }, this.btnRemove, { xtype: 'tbseparator' }, this.btnCancel
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
            upload_url: me.upload_url,
            post_params: me.post_params,
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
            //table: "KYGL_CGGL_BZGF",
            //FID: "1",
            fileName: rec.raw.fileName,
            mj: rec.raw.level,//rec.get('level')
            size: file.size,
            type: file.type
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
        rec.set('percent', 0);
        rec.set('status', file.filestatus);
        rec.commit();
    },
    upload_success_handler: function (file, serverData, responseReceived) {
        var me = this.settings.custom_settings.scope_handler;
        var rec = me.store.getById(file.id);
        var ret = Ext.JSON.decode(serverData);
        if (ret.success) {
            rec.set('percent', 100);
            rec.set('status', file.filestatus);
        } else {
            rec.set('percent', 0);
            rec.set('status', ret.msg);
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
    },
    upload_complete_handler: function (file) {
        //var me = this;
        if (this.getStats().files_queued === 0) {
            var me = this.settings.custom_settings.scope_handler;
            //var arrFiles = Ext.getCmp('upload-grid').failedFiles;
            var arrFiles = me.failedFiles;
            Ext.each(arrFiles, function (fileId) {
                this.requeueUpload(fileId);
            }, this)
        }
    },
    file_queued_handler: function (file) {
        var me = this.settings.custom_settings.scope_handler;
        me.store.add({
            id: file.id,
            name: file.name,
            fileName: file.name,
            picdesc: '',
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
        //me.btnAddf.setDisabled(!bl);
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