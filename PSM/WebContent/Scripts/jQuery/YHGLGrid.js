/// <reference path="../swfuploadfiles.aspx" />
/// <reference path="../swfuploadfiles.aspx" />
/// <reference path="extjs/ext-all-dev.js" />
Ext.define('JJXM.YHGLGrid', {
    requires: [
        'Ext.data.Model',
        'Ext.grid.Panel'
    ],
    gridURL: "/JJCMS/JJCMS/Ajax/GenericGridHandler.ashx",
    storeURL: "/JJCMS/JJCMS/Ajax/StoreHandler.ashx",
    actionURL: "/JJCMS/JJCMS/Ajax/ActionHandler.ashx",
    toolbar: null,
    selRecs: [],
    createGrid: function (config) {
        var me = this;
        var title = config.title;                //标题，模块名
        var table = config.table;                //表名
        var xgbm = config.xgbm;                  //相关表名
        var xgmk = config.xgmk;                  //相关模块
        var gridId = config.gridId;              //grid的Id
        var user = config.user;                  //登陆用户
        var psize = config.pageSize;             //pagesize
        var container = config.container;        //存grid的panel的容器            

        var foreignID = config.foreignID;        //DAGL_WDXX的外键
        var filterStr = config.filterStr         //过滤条件，示例：（"key1=value1;key2=value2"）        
        var tbAdds = config.tbAdds;              //toolbar添加项,类型为数组
        var unVisables = config.unVisables;      //不显示的列数组

        var fmURL = me.actionURL;                //toolbar相关的后台链接 
        var newStURL = me.storeURL;
        var toolbar = me.toolbar;                //定制toolbar
        var stFields = [],                       //gridDt.storefields
            grColumns = [],                      //gridDt.gridcolumns
            fmItems = [];                        //gridDt.formitems  获取相关数组
        Ext.define(table, {
            extend: 'Ext.data.Model',
            fields: stFields
        });
        var st;     //有效数据
        var stInValid = new Ext.data.JsonStore({ model: table });    //无效数据
        var createForm;
        var fm;
        //选中记录
        var selRecs = [];
        var keyIDs = [];
        var keyXGBMs = [];
        var keyWDMCs = [];
        var keyNames = [];
        var getSel = function (grid) {
            selRecs = [];  //清空数组
            keyIDs = [];
            keyNames = [];
            keyXGBMs = [];
            keyWDMCs = [];
            selRecs = grid.getSelectionModel().getSelection();
            for (var i = 0; i < selRecs.length; i++) {
                keyIDs.push(selRecs[i].data.ID);
                keyNames.push(selRecs[i].data.F_XMMC);
                keyXGBMs.push(selRecs[i].raw.XGBM);
                keyWDMCs.push(selRecs[i].raw.WDMC);
            }
            if (selRecs.length === 0) {
                Ext.Msg.alert('警告', '没有选中任何记录！');
                return false;
            }
            return true;
        };
        //删除左右两端的空格
        String.prototype.trim = function () {
            return this.replace(/(^\s*)|(\s*$)/g, '');
        };
        //删除Handler
        var deleteH = function () {
            if (getSel(gridDT)) {
                //获取到哪个单选按钮被选中
                var isValid = tbr.getComponent('rdg-status').lastValue[xgmk + table];
                if (isValid === 'y') {
                    Ext.Msg.confirm('删除', '确定将所选记录加入回收站吗？', function (buttonID) {
                        if (buttonID === 'yes') {
                            $.getJSON(fmURL,
                                { action: "delete", table: table, id: keyIDs.toString() },
                                function (res) {
                                    if (res.success) {
                                        //重新加载store
                                        st.load({
                                            params: { start: 0, limit: psize }
                                        });
                                    }
                                    else {
                                        Ext.Msg.alert("信息", res.msg);
                                    }
                                });
                        }
                    });
                }
                else {
                    Ext.Msg.confirm('删除', '确定彻底删除吗？', function (buttonID) {
                        if (buttonID === 'yes') {
                            $.getJSON(fmURL,
                                { action: "drop", table: table, id: keyIDs.toString(), xgbm: keyXGBMs.toString(), wdmc: keyWDMCs.toString() },
                                function (res) {
                                    if (res.success) {
                                        //重新加载store
                                        stInValid.load({
                                            params: { start: 0, limit: psize }
                                        });
                                    }
                                    else {
                                        Ext.Msg.alert("信息", res.msg);
                                    }
                                });
                        }
                    });
                }
            }
        };
        //还原Handler
        var restoreH = function () {
            if (getSel(gridDT)) {
                $.getJSON(fmURL,
                                { action: "restore", table: table, id: keyIDs.toString() },
                                function (res) {
                                    if (res.success) {
                                        //重新加载store
                                        stInValid.load({
                                            params: { start: 0, limit: psize }
                                        });
                                    }
                                    else {
                                        Ext.Msg.alert("信息", res.msg);
                                    }
                                });
            }
        };
        //查询Handler
        var searchH = function () {
            var kw = tbr.getComponent("keyword").getValue();
            var upKw = kw.toUpperCase().trim();  //转换为大写

            st.filterBy(function (record, id) {
                //遍历所有字段，有一个字段匹配就返回true
                var dt = record.data;
                for (var p in dt) {
                    if (dt[p].toString().toUpperCase().indexOf(upKw) >= 0) {
                        return true;
                    }
                }
                return false;
            });
        };
        //上传UploadH
        var uploadH = function () {


            urlUpload = "/JJCMS/JJCMS/Ajax/dagl/swfuploadHandler.ashx?SCR=" + encodeURIComponent(user) + "&xgbm=" + xgbm + "&xgmk=" + encodeURIComponent(xgmk) + "&id=" + encodeURIComponent(foreignID);
            Ext.create('Ext.window.Window', {
                title: '上传',
                height: 600,
                width: 1000,
                layout: 'fit',
                closeAction: 'close',
                items: Ext.create('Ext.ux.uploadPanel.UploadPanel', {
                    itemId: 'uploadDAGL-grid',
                    //title: '上传文件（最大1G）',
                    addFileBtnText: '选择文件...',
                    uploadBtnText: '上传',
                    removeBtnText: '移除所有',
                    cancelBtnText: '取消上传',
                    file_size_limit: 1024,//MB
                    upload_url: urlUpload,
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
                    }
                }
            }).show();
        }
        //创建tbar
        var tbr = new Ext.Toolbar({
            defaults: {
                scale: 'medium'
            },
            items: [
                {
                    text: '上传',
                    itemId: 'tbtn-upload',
                    tooltip: '上传附件',
                    iconCls: 'upload',
                    handler: uploadH
                }, '-',
                {
                    text: '删除',
                    tooltip: '删除记录',
                    iconCls: 'remove',
                    handler: deleteH
                }, '-', {
                    text: '还原',
                    itemId: 'tbtn-res',
                    tooltip: '还原记录',
                    iconCls: 'restore',
                    disabled: true,
                    handler: restoreH
                }, '-', {
                    xtype: 'radiogroup',
                    itemId: 'rdg-status',
                    width: 150,
                    items: [
                        {
                            xtype: 'radio',
                            boxLabel: '已录入',
                            inputValue: 'y',
                            name: xgmk + table,
                            checked: true,
                        },
                        {
                            xtype: 'radio',
                            boxLabel: '回收站',
                            inputValue: 'n',
                            name: xgmk + table
                        }
                    ],
                    listeners: {
                        'change': function (newValue) {
                            var stInURL = '';
                            if (me.storeURL.indexOf("?") >= 0) {
                                stInURL = me.storeURL + '&table=' + table + '&valid=n';
                            } else {
                                stInURL = me.storeURL + '?table=' + table + '&valid=n';
                            }

                            //if (user) {
                            //    stInURL += '&ursname=' + user;
                            //}
                            var isValid = newValue.lastValue[xgmk + table];
                            Ext.apply(stInValid, st);

                            stInValid.setProxy({
                                type: 'ajax',
                                url: stInURL,
                                reader: {
                                    type: 'json',
                                    totalProperty: "total",
                                    root: "rows",
                                    idProperty: 'ID'
                                }
                            });

                            if (isValid === 'n') {
                                tbr.getComponent('tbtn-res').setDisabled(false);

                                //切换bbar的store
                                bbr.bindStore(stInValid);
                                stInValid.load({ params: { start: 0, limit: psize } });
                            }
                            else {
                                tbr.getComponent('tbtn-res').setDisabled(true);
                                //切换bbar的store
                                bbr.bindStore(st);
                                st.load({ params: { start: 0, limit: psize } });
                            }
                        }
                    }
                }, '-', {
                    xtype: 'textfield',
                    itemId: 'keyword',
                    emptyText: '请输入查询内容',
                    width: 150,
                    height: 25,
                    listeners: {
                        'change': function (newValue, oldValue) {     //查询框为空时，事件
                            if (newValue.getValue() === '') {
                                var isValid = tbr.getComponent('rdg-status').lastValue[xgmk + table];
                                //重新加载store
                                st.load({
                                    params: { start: 0, limit: psize, valid: isValid }
                                });
                            }
                        }
                    }
                },
                {
                    text: '查询',
                    tooltip: '根据关键字进行模糊查询，忽略前后的空格。',
                    iconCls: 'search',
                    handler: searchH
                }
            ]
        });
        tbr.add(tbAdds);
        tbr = toolbar ? toolbar : tbr;
        var bbr;
        var gridDT;    //数据grid
        //浏览状态
        if (config.action === 'view') {
            btnOK.hide();
            btnReset.hide();
            btnCancel.setText("关闭");
            fm.getForm().loadRecord(selRecs[0]);  //加载选中记录数据
        }

        //创建store
        var stURL;
        if (me.storeURL.indexOf("?") >= 0) {
            stURL = me.storeURL + '&table=' + table + '&valid=y';
        } else {
            stURL = me.storeURL + '?table=' + table + '&valid=y';
        }

        //if (user) {
        //    stURL += '&ursname=' + user;
        //}
        encodeURI(stURL);
        st = new Ext.data.JsonStore({
            //storeId: table,
            autoLoad: false,
            pageSize: psize,
            fields: ["ID", "FOREIGNID", "WDMC", "MJ", "XGBM", "LX", "DX", "SCSJ", "SCR", "XGMK"],
            proxy: {
                type: 'ajax',
                url: stURL,
                reader: {
                    type: 'json',
                    totalProperty: "total",
                    root: "rows",
                    idProperty: 'ID'
                }
            }
        });
        //创建bbar
        //更新store的proxy的url
        bbr = new Ext.PagingToolbar({
            displayInfo: true,
            emptyMsg: "没有数据要显示！",
            displayMsg: "当前为第{0}--{1}条，共{2}条数据", //参数是固定的，分别是起始和结束记录数、总记录数
            store: st,
            items: ['-', {
                xtype: 'combo',
                fieldLabel: '显示行数',
                labelWidth: 65,
                width: 120,
                store: [10, 20, 50, 200, '全部'],
                value: psize,
                forceSelection: true,
                listeners: {
                    'collapse': function (field) {
                        var size = field.lastValue;
                        psize = size === "全部" ? 100000 : size;
                        Ext.apply(st, { pageSize: psize });     //更改store的pageSize参数
                        st.load({ params: { start: 0, limit: psize } });  //重新加载store                                
                    }
                }
            }]
        });
        //生成数据gridDT
        gridDT = Ext.create('Ext.grid.Panel', {
            selModel: new Ext.selection.CheckboxModel({ mode: 'MULTI' }),
            store: st,
            columns: [
                 { xtype: 'rownumberer', width: 60 },
                 {
                     header: '操作',
                     xtype: 'actioncolumn',
                     width: 100,
                     align: 'center',
                     items: [
                         {
                             icon: '/JJCMS/JJCMS/Images/wdgl/view.png',
                             tooltip: '预览',
                             handler: function (grid, rowIndex, colIndex) {
                                 var rec = grid.getStore().getAt(rowIndex);
                                 var tablename = rec.raw.XGBM;
                                 var filename = rec.raw.FILENAME;
                                 var fileHZM = filename.substr(filename.lastIndexOf(".") + 1).toLocaleUpperCase();
                                 var filenameNoHZM = filename.substr(0, filename.lastIndexOf('.'));
                                 var savepath = tablename;
                                 var saveUrl = "";
                                 switch (fileHZM) {
                                     case "DOC":
                                     case "DOCX":
                                     case "XLS":
                                     case "XLSX":
                                     case "PPT":
                                     case "PPTX":
                                         //需要加入判断语句，是否已经转换完成。table是指业务模块的表名。
                                         //var urlZHZT = "Ajax/dagl/ZHZTHandler.ashx";
                                         saveUrl = "UploadFiles/" + savepath + "/" + filenameNoHZM + ".pdf";  //                                                                                  
                                         break;
                                     case "PDF":
                                     case "PNG":
                                     case "JPEG":
                                     case "JPG":
                                     case "GIF":
                                     case "TXT":
                                         saveUrl = "UploadFiles/" + savepath + "/" + filename;
                                         break;
                                     default:
                                         saveUrl = "";
                                 }
                                 saveUrl = encodeURIComponent(saveUrl);
                                 if (saveUrl !== "") {
                                     $.ajax({
                                         url: saveUrl,
                                         type: 'GET',
                                         complete: function (response) {
                                             if (response.status == 200) {
                                                 //window.open("Default.aspx");
                                                 window.open(saveUrl, "文档预览", "height=700,width=1200");
                                                 //全屏打开
                                                 //var tmp = window.open("about:blank", ""); //, "fullscreen=1"
                                                 //tmp.moveTo(0, 0);
                                                 //tmp.resizeTo(screen.width + 20, screen.height);
                                                 //tmp.focus();
                                                 //tmp.location = savaUrl;

                                             } else {
                                                 Ext.Msg.alert('提示', '后台正忙，请稍后！');

                                             }
                                         }
                                     });
                                 } else {
                                     Ext.Msg.alert('提示', '此格式文件暂不支持预览！');
                                 }

                             }
                         }, {
                             icon: '/JJCMS/JJCMS/Images/ims/toolbar/blank.png',
                             disabled: true,
                         }, {
                             icon: '/JJCMS/JJCMS/Images/wdgl/download.png',
                             tooltip: '下载',
                             handler: function (grid, rowIndex, colIndex) {
                                 var rec = grid.getStore().getAt(rowIndex);
                                 var tablename = rec.raw.XGBM;
                                 var filename = rec.raw.FILENAME;
                                 var downname = rec.get('WDMC');
                                 var savepath = "";
                                 savepath = tablename;
                                 window.open("/JJCMS/JJCMS/DownloadFile.aspx?filename=" + encodeURIComponent(filename) + "&downname=" + encodeURIComponent(downname) + "&folder=" + encodeURIComponent(tablename));
                             }
                         }]
                 },
                 {
                     header: "文档名称",
                     dataIndex: "WDMC",
                     width: 250,
                     renderer: function (data, metadata, record, rowIndex, columnIndex, store) {
                         //这里可以根据rowIndex，columnIndex 设置字体颜色
                         var filename = record.data["WDMC"];
                         var tablename = record.data["XGBM"];
                         var recId = record.data["ID"];
                         var status = filename;

                         return status;
                     }

                 },
                 { header: "密级", dataIndex: "MJ" },
                 { header: "类型", dataIndex: "LX" },
                 {
                     header: "大小", dataIndex: "DX", renderer: function (v) {
                         return Ext.util.Format.fileSize(v);
                     }
                 },
                 { header: "上传时间", dataIndex: "SCSJ" },
                 { header: "上传人", dataIndex: "SCR" },
                 { header: "相关模块", dataIndex: "XGMK" }
            ],
            autoScroll: true,
            tbar: tbr,
            bbar: bbr,
            viewConfig: {
                //loadMask: false
                loadMask: {                       //IE8不兼容loadMask
                    msg: '加载数据中……'
                }
            },
            listeners: {
                'cellclick': function (self, td, cellIndex, record) {
                    //浏览数据的Panel
                    var viewDataPanel = Ext.getCmp('east-form');
                    var items = viewDataPanel.items;
                    if (items.length === 0) {
                        viewDataPanel.add(me.fmItems);
                        viewDataPanel.getForm().loadRecord(record);
                    } else {
                        viewDataPanel.getForm().loadRecord(record);  //加载选中记录数据me.selRecs[0]
                    }
                },
                'afterrender': function () {
                    st.load({ params: { start: 0, limit: psize } });   //渲染之后加载store

                }
            }
        });
        var panel = Ext.create('Ext.panel.Panel', {
            itemId: gridId,
            title: title,
            layout: 'fit',
            closable: true,
            autoScroll: true
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

