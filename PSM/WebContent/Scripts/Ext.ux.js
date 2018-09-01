/// <reference path="extjs/ext-all-dev.js" />
//********************************************************
//更新日期: 2013.3.6
//开发人员: 何猛
//內容说明: Ext扩展
//********************************************************
Ext.define('Ext.ux.FileColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.filecolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);
                    
                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }                                      
                    
                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);
                    
                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });
                    
                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id                            
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }
                    
                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles
                        
                    })
                    winView.show();
                }
            }]
    },
    width: 80    
});



Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//复写招标内容
Ext.define('Ext.ux.ZbnrColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.zbnrcolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(招标内容)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(招标内容)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(招标内容)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(招标内容)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//la_neige13.5.2*****复写招标报备
Ext.define('Ext.ux.ZbbbColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.zbbbcolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(招标报备)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(招标报备)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(招标报备)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(招标报备)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//招标公告

Ext.define('Ext.ux.ZbggColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.zbggcolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(招标公告)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(招标公告)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(招标公告)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(招标公告)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//资格审查
Ext.define('Ext.ux.ZgscColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.zgsccolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(资格审查)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(资格审查)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(资格审查)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(资格审查)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//中标公示
Ext.define('Ext.ux.ZbgsColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.zbgscolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(中标公示)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(中标公示)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(中标公示)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(中标公示)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//招标投诉
Ext.define('Ext.ux.ZbtsColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.zbtscolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(招标投诉)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(招标投诉)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(招标投诉)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(招标投诉)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//投标结果
Ext.define('Ext.ux.TbjgColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.tbjgcolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(投标结果)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(投标结果)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(投标结果)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(投标结果)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//评标总评报告
Ext.define('Ext.ux.PbzpbgColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.pbzpbgcolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(评标总评报告)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(评标总评报告)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(评标总评报告)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(评标总评报告)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//评标文档附件
Ext.define('Ext.ux.PbwdfjColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.pbwdfjcolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(评标文档附件)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(评标文档附件)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(评标文档附件)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(评标文档附件)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});

//定标会结果
Ext.define('Ext.ux.DbhjgColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.dbhjgcolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(定标会结果)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(定标会结果)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(定标会结果)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(定标会结果)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});
Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});
//定标会文档附件
Ext.define('Ext.ux.DbhwdfjColumn', {
    extend: 'Ext.grid.column.Action',
    alias: 'widget.dbhwdfjcolumn',
    table: '',                                  //相关表名
    user: '',                                   //登陆用户
    constructor: function () {
        var me = this;
        var urlUploadT;
        this.callParent(arguments);
        this.width = 80;
        this.align = 'center';
        this.items = [
            {
                icon: '../Images/ims/toolbar/upload.png',
                tooltip: '单击上传附件',
                handler: function (grid, rowIndex, colIndex) {
                    var tablename = me.table;  //相关表名
                    var strSCR = me.user;     //相关上传人                    
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    var id = rec.get('ID');
                    var winWidth = 1000;
                    if (tablename === "GCZS_ZHXC_TZ") {
                        winWidth = 1200;
                        var tzmc = rec.get('TZMC');     //  图组名称                        
                        urlUploadT = "Ajax/tpgl/PicSWFHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&id=" + id + "&tzmc=" + encodeURIComponent(tzmc)
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(定标会文档附件)',
                            height: 600,
                            width: 1200,
                            layout: 'fit',
                            maximizable: true,
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPicPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    } else {
                        urlUploadT = "Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(strSCR) + "&xgbm=" + tablename + "&id=" + id;
                        Ext.create('Ext.window.Window', {
                            //id: 'upload-win',
                            title: '上传(定标会文档附件)',
                            height: 600,
                            width: 1000,
                            layout: 'fit',
                            closeAction: 'close',
                            items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                                //itemId: 'upload-grid',
                                //title: '上传文件（最大1G）',
                                addFileBtnText: '选择文件...',
                                uploadBtnText: '上传',
                                removeBtnText: '移除所有',
                                cancelBtnText: '取消上传',
                                file_size_limit: 1024,//MB
                                upload_url: urlUploadT,
                                width: 1000
                            }),
                            listeners: {
                                'beforeclose': function (me) {
                                    var gridPanel = me.down('panel');
                                    gridPanel.onRemove();
                                    gridPanel.store.removeAll();
                                    //注销swf对象
                                    var swf = gridPanel.swfupload;
                                    gridPanel.swfupload.destroy();
                                    gridPanel.swfupload = null;
                                }
                            }
                        }).show();
                    }

                }
            },
            {
                icon: '../Images/ims/toolbar/blank.png',
                disabled: true
            },
            {
                icon: '../Images/ims/toolbar/download.png',
                tooltip: '单击浏览、下载附件',
                handler: function (grid, rowIndex, colIndex) {
                    //selectionModel
                    var rec = grid.getStore().getAt(rowIndex);
                    var sm = grid.getSelectionModel();
                    sm.select(rec);

                    //弹出文档管理grid
                    var id = rec.get('ID');
                    var tablename = me.table;
                    var strSCR = me.user;
                    var panelViewFiles = Ext.create("Ext.panel.Panel", {
                        layout: 'fit'
                    });

                    var title = "浏览下载";
                    if (tablename === "GCZS_ZHXC_TZ") {
                        title = title + "图片(定标会文档附件)";
                        var tzmc = rec.get('TZMC');  //  图组名称
                        var folder = rec.get('ID');   // 图组文件夹名称
                        var gridWDGL = Ext.create('JJXM.TPGLGrid');
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/tpgl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                            //actionURL: "Ajax/tpgl/ActionHandler.ashx"
                        });

                        gridWDGL.createGrid({
                            table: 'GCZS_ZHXC_TP',
                            xgbm: tablename,
                            folder: folder,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    } else {
                        var gridWDGL = Ext.create('JJXM.WDGLGrid');
                        title = title + "附件(定标会文档附件)";
                        Ext.apply(gridWDGL, {
                            storeURL: "Ajax/dagl/StoreHandler.ashx?xgbm=" + tablename + "&foreignID=" + id
                        });

                        gridWDGL.createGrid({
                            table: 'DAGL_WDXX',
                            xgbm: tablename,
                            user: strSCR,
                            foreignID: id,
                            pageSize: 10,
                            container: panelViewFiles
                        });
                    }

                    var winView = Ext.create('Ext.window.Window', {
                        title: title,
                        height: 600,
                        width: 1100,
                        layout: 'fit',
                        closeAction: 'close',
                        items: panelViewFiles

                    })
                    winView.show();
                }
            }]
    },
    width: 80
});

Ext.define('Ext.ux.ThemeChange', {
    extend: 'Ext.form.field.ComboBox',
    fieldLabel: '请选择主题',
    queryMode: 'local',
    value: '默认',
    displayField: 'name',
    valueField: 'file',
    store: new Ext.data.ArrayStore({
        fields: ['name', 'file'],
        data: [
            ['默认', 'ext-all-new.css'],
            ['黑色', 'ext-all-access.css'],
            ['灰色', 'ext-all-gray.css'],
            ['巧克力色', 'ext-all-chocolate.css'],
            ['绿色', 'ext-all-green.css'],
            ['粉色', 'ext-all-pink.css']
        ]
    }),
    listeners: {
        'collapse': function (field) {
            Ext.util.CSS.swapStyleSheet('theme', '/jjcms/jjcms/scripts/extjs/resources/css/' + field.lastValue);
        }
    }
});