/// <reference path="extjs/ext-all-dev.js" />
function showGrid(record, item) {
    var container = Ext.getCmp("center-panel");
    var user = sessionStorage.jjimsUserNickName;            //记录当前登录用户名
    var isLeaf = item.raw.leaf;
    var title = item.raw.text;
    var table = item.raw.id;
    //判断是否叶节点
    if (isLeaf) {
        //刷新右边区域
        Ext.getCmp('east-form').removeAll(false);
        //判断container是否已经包含这个表
        if (container.getComponent(table)) {
            container.setActiveTab(table);
            return;
        }
        if (table) {
            var gridP;
            if (table.indexOf("DAGL_WDXX") >= 0) {
                var gridWDGL = Ext.create('JJXM.WDGLGrid');
                Ext.apply(gridWDGL, {
                    storeURL: "Ajax/dagl/WDGLStoreHandler.ashx?mkmc=" + encodeURIComponent(title)
                });
                gridWDGL.createGrid({
                    title: title + "档案",
                    gridId: table,
                    table: 'DAGL_WDXX',
                    xgmk: title,
                    user: user,             //获取到当前登录用户
                    pageSize: 20,
                    container: container
                });
            }
            else {
                gridP = Ext.create('JJXM.GernericGrid');
                gridP.createGrid({
                    title: title,
                    table: table,
                    pageSize: 20,
                    unVisables: ['FOREIGNID'],
                    user: user,             //获取到当前登录用户
                    //tbAdds: ['-', {
                    //    text: '测试',
                    //    handler: function () {
                    //        if (gridP.selRecs.length == 1) {
                    //            alert(gridP.selRecs[0].data.LZDW);
                    //        }
                    //    }
                    //}],
                    container: container
                });
            }
        }
    }
}
