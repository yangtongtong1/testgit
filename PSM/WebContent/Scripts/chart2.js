var tn;
var findstr ="";
var findstr2 ="";
var findstr3 ="";

Ext.define('ChartGrid2', 
{
    requires: 
    [
        'Ext.data.Model',
        'Ext.grid.Panel',
        'Ext.chart.*',
        'Ext.layout.container.Fit',
        'Ext.window.MessageBox',
        'Ext.Window',
        'Ext.fx.target.Sprite',
        'Ext.tip.*',
        'Ext.form.field.ComboBox',
        'Ext.form.FieldSet',
        'Ext.tip.QuickTipManager',
        'Ext.data.Record',
        'Ext.data.*'
    ],
    toolbar: null,
    selRecs: [],
    createGrid: function (config) 
    {
//    	Ext.draw.engine.ImageExporter.defaultUrl = 'SaftyCkeckAction!downImage';  
//    	Ext.draw.engine.ImageExporter.defaultUrl = 'SaftyCkeckAction!downImage'; 
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
        var roleName = config.userName;
        var userRole = config.userRole;
        var mychart;
        var myname;
        var mydata;
        var xtitle;
        var ytitle;
        var donut = false;
        var gridDT;
        var createForm;
        var dataStore;
    	var column;
    	var queryURL;
        var forms = [];
        var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';	//必填项红色星号*
        var selRecs = [];
        var param;		//存放gridDT选择的行
        var projectName = config.projectName;
        
        var tbar = new Ext.Toolbar(
        {
            defaults: 
            {
                scale: 'medium'
            }
        });
        
        var panel = Ext.create('Ext.panel.Panel', 
                {
                    itemId: tableID,
                    title: title,
                    layout: 'fit',
//                    layout: 'hbox',
                    closable: true,
                    autoScroll: true,
//                    id:'mypanel',
                    tbar: tbar
                    //items: mychart
                });
        
        
        var projectall ;
        var projectChange = function()
        {
        	var projectName = projectCombo.getRawValue();
        	var kind = kindCombo.getRawValue();
        	
        	//*****************************************
        	var project = Ext.getCmp('ChartColumn');
        	chart2 = project.nextSibling();
//        	alert(chart2.name);
        	
        	
        	projectall = projectName;
//        	var chart2 = Ext.getCmp('ChartColumn2');
//        	alert(chart2.hidden);
//        	alert(projectName);
//        	alert(chart2);
        	if(projectName=='全部'){
//        		panel.add('ChartColumn2');
//        		chart2.setDisabled(false);  
        		chart2.hidden=false;
//        		chart2.show();
//        		chart2.setVisible(true);
           }else{
//        	   panel.remove('ChartColumn2');
//        	   chart2.setDisabled(true); 
        	   chart2.hidden=true;
//        	   chart2.hide();
        	   chart2.setVisible(false);
           }
//        	alert(chart2.hidden);
        	
        	var searchUrl='SaftyCkeckAction!getColumnDataListDef?type=上级检查问题整改及回复&projectName=' + projectName + "&kind=" + kind;
        	searchUrl = encodeURI(searchUrl);
        	storechart.getProxy().url = searchUrl;
        	storechart.load({params: { start: 0, limit: psize }});
//        	bbar.moveFirst();
        }
        
        
        var kindChange = function()
        {
        	var projectName = projectCombo.getRawValue();
        	var kind = kindCombo.getRawValue();
        	/*if(findstr == kw) 
        	{
        		return;
        	}*/
//        	alert('kindchange');
        	var searchUrl='SaftyCkeckAction!getColumnDataListDef?type=上级检查问题整改及回复&projectName=' + projectName + "&kind=" + kind;
        	searchUrl = encodeURI(searchUrl);
        	storechart.getProxy().url = searchUrl;
        	storechart.load({params: { start: 0, limit: psize }});
//        	bbar.moveFirst();
        }
        
        //平均安全隐患数对比图 数据
        var storechart = Ext.create('Ext.data.Store',
                {
                	fields:[{ name: 'problem'},
               	            { name: 'numofpro', type: 'int'},
               	            { name: 'percentage'}
                	],
                	pageSize: psize,  //页容量20条数据
                    proxy: {
                        type: 'ajax',
                        url: 'SaftyCkeckAction!getColumnDataListDef?type=上级检查问题整改及回复'+ "&projectName=" + projectName+'&kind=基础管理类隐患',
                        reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                            type: 'json', //返回数据类型为json格式
                            root: 'rows',  //数据
                            totalProperty: 'total' //数据总条数
                        }
                    },
                    listeners: {
                        'beforeload': function (store, operation, eopts) {
                            store.removeAll();
                        },
                        'load': function (ths, records, successful, eopts) {
                            //var get = Ext.getCmp('center-panel');
                        	//alert("load");
                        	//alert(mychart.maximum);
                        	//mychart.axes[0].maximum  = 400;
                        }
                    },
                    autoLoad: true //即时加载数据
                });
        
        //平均安全隐患数 数据
        var storechart2 = Ext.create('Ext.data.Store',
                {
                	fields:[{ name: 'projectOn'},
               	            { name: 'numofpro', type: 'int'}
                	],
                	pageSize: psize,  //页容量20条数据
                    proxy: {
                        type: 'ajax',
                        url: 'SaftyCkeckAction!getColumnData2ListDef?type=上级检查问题整改及回复&projectName=全部',
                        reader: {   //这里的reader为数据存储组织的地方，下面的配置是为json格式的数据，例如：[{"total":50,"rows":[{"a":"3","b":"4"}]}]
                            type: 'json', //返回数据类型为json格式
                            root: 'rows',  //数据
                            totalProperty: 'total' //数据总条数
                        }
                    },
                    listeners: {
                        'beforeload': function (store, operation, eopts) {
                            store.removeAll();
                        },
                        'load': function (ths, records, successful, eopts) {
                            //var get = Ext.getCmp('center-panel');
                        	//alert("load");
                        	//alert(mychart.maximum);
                        	//mychart.axes[0].maximum  = 400;
                        }
                    },
                    autoLoad: true //即时加载数据
                });
        
        
        
        Ext.define('gridModel', {
            extend: 'Ext.data.Model',
            fields: [
                     {name: 'Name', type: 'String'},
                     {name: 'ID', type: 'String'}
            ]
        });
        //获得项目列表
        var storeproject = Ext.create('Ext.data.Store',
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
	                        	storeproject.insert(storeproject.getCount()-1,ins_rec);
	                        }
                        }
                    },
                    autoLoad: true //即时加载数据
                });
        
        
        var checkunitcombo = new Ext.data.ArrayStore({
            fields: ['id', 'checkunit'],
            data: [[1, '基础管理类隐患'], 
            	[2, '现场管理类隐患'], 
            	[3, '整改闭合类隐患'],
                [4,'全部']]
          });
        
        var sumOfNum = function(){
        	var sum  = 0;
        	for(var i =0;i<storechart.getCount();i++){
        	       sum=Number(sum)+Number(storechart.getAt(i).get('numofpro')); //遍历每一行
        	}
        	return sum;
        }
        
        var projectCombo = new Ext.form.ComboBox(
                {
                	fieldLabel:'项目',
                	width:500,
                	height: 32,
                	labelWidth:30,
//                	labelHeight:32,
                	xtype:'combo',
                	queryMode:'local',
                	editable:false,
                	//autoSelect:true,
        			store:storeproject,
        			 valueField:'Name',
        			 displayField:'Name',
        			 triggerAction:'all',
        			 //autoSelect:true,
        			 listeners:
        			 {
        				 'select':projectChange
        			 },
                     name:"project",
//                     id:"project",
                });

        
        var kindCombo = new Ext.form.ComboBox(
                {
                	fieldLabel:'隐患类型',
                	width:300,
                	height: 32,
                	labelWidth:60,
//                	labelHeight:32,
                	xtype:'combo',
                	queryMode:'local',
                	editable:false,
                	//autoSelect:true,
        			store:checkunitcombo,
        			 valueField:'checkunit',
        			 displayField:'checkunit',
        			 triggerAction:'all',
        			 //autoSelect:true,
        			 listeners:
        			 {
        				 'select':kindChange
        			 },
                     name:"kind",
                });
        
        
        
//        project.store.on('load',function(store,record,opts){  
//        	  
//        	var myvalue= record[0].data.Name;
//        	project.setValue(myvalue);
//        });
        
        projectCombo.setValue(projectName);
        kindCombo.setValue(kindCombo.store.getAt(0).get('checkunit'));
        tbar.add(projectCombo);
        tbar.add(kindCombo);
       
        
        var ChartColumn = Ext.create('Ext.chart.Chart', 
		        {
		        	width: 480,
		            height: 400,
		          //width: 48,
		          //height: 40,
		            style: 'background:#fff',
		            animate: true,
		            shadow: true,
		            name:'chartColumn',
		            id:'ChartColumn',
		            store: storechart,
		            axes:                      //注释杨通：纵坐标
		            [{                                 
		                type: 'Numeric',
		                position: 'left',
		                //fields: [mydata],
		                fields: ['numofpro'],
		                label: 
		                {
		                    renderer: Ext.util.Format.numberRenderer('0,0')
		                },
		                title: '问题数量',
		                //grid: true,
//		                maximum: 50,
		                minimum: 0
		            }, 
		            {                             //注释杨通：横坐标
		                type: 'Category',
		                position: 'bottom',
		                //fields: [myname],
		                fields: ['problem'],
		                title: '隐患类型'
		            }],
		            series: 
		            [{
		                type: 'column',
		                axis: 'left',
//		                axis: 'center',
		                highlight: true,
		                tips: 
		                {
		                  trackMouse: true,
		                  width: 120,
		                  height: 30,
		                  renderer: function(storeItem, item) 
		                  {
		                    //this.setTitle(storeItem.get(myname) + ': ' + storeItem.get(mydata) + ' 个');
		                	  this.setTitle(storeItem.get('problem') + ': ' + storeItem.get('numofpro') + ' 个');
		                	  //注释杨通：当鼠标选中其中的一列时，显示的内容
		                  }
		                },
		                label: {
                            display: 'outside',
                            field: 'percentage',
                            orientation: 'horizontal',
                            color: '#333',
                            font : '15px "Lucida Grande"',//字体 
                        },
		                xField: 'problem',
		    			yField: 'numofpro'
		            }]
		        });
        
        
        
        var ChartColumn2 = Ext.create('Ext.chart.Chart', 
		        {
		        	width: 480,
		            height: 400,
		          //width: 48,
		          //height: 40,
		            style: 'background:#fff',
		            animate: true,
		            shadow: true,
//		            hidden: true,
//		            visible:false,
		            name:'chartColumn2',
//		            id:'ChartColumn2',
		            store: storechart2,
//		            title:'安全隐患类型分布图',
		            axes:                      //注释杨通：纵坐标
		            [{                                 
		                type: 'Numeric',
		                position: 'left',
		                //fields: [mydata],
		                fields: ['numofpro'],
		                label: 
		                {
		                    renderer: Ext.util.Format.numberRenderer('0,0')
		                },
		                title: '安全隐患率',
		                //grid: true,
//		                maximum: 50,
		                minimum: 0
		            }, 
		            {                             //注释杨通：横坐标
		                type: 'Category',
		                position: 'bottom',
		                //fields: [myname],
		                fields: ['projectOn'],
		                title: '在建项目'
		            }],
		            series: 
		            [{
		                type: 'column',
		                axis: 'left',
//		                axis: 'center',
		                highlight: true,
		                style: { 
//		                	   width: 50 ,
//		                	   align:'center'
		                },//这里是宽度
		                tips: 
		                {
		                  trackMouse: true,
		                  width: 120,
		                  height: 30,
		                  renderer: function(storeItem, item) 
		                  {
		                    //this.setTitle(storeItem.get(myname) + ': ' + storeItem.get(mydata) + ' 个');
		                	  this.setTitle(storeItem.get('projectOn') + ': ' + storeItem.get('numofpro'));
		                	  //注释杨通：当鼠标选中其中的一列时，显示的内容
		                  }
		                },
		                label: 
		                {
		                   display: 'outside',
//		                  'text-anchor': 'middle',
		                    field: 'numofpro',
		                    font : '15px "Lucida Grande"',//字体 
		                    renderer: Ext.util.Format.numberRenderer('0'),
		                    orientation: 'horizontal',
		                    color: '#333',
		                    renderer : function(v){//自定义标签渲染函数  
	                            return v + '个';  
	                        } 
		                },
		                xField: 'projectOn',
		    			yField: 'numofpro'
		            }]
		        });
        
        
        var btnOutputPic1 = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '导出上图',
            icon: "./Images/ims/toolbar/report_picture.png",
            handler: outputPic1
        });
        
        function outputPic1()
        {
        	ChartColumn.save({
        		 type: 'image/png'
        		});
        };
        
        var btnOutputPic2 = Ext.create('Ext.Button', {
        	width: 100,
        	height: 32,
        	text: '导出下图',
            icon: "./Images/ims/toolbar/report_picture.png",
            handler: outputPic2
        });
        
        function outputPic2()
        {
        	ChartColumn2.save({
        		 type: 'image/png'
        		});
        };
        tbar.add(btnOutputPic1);
        tbar.add(btnOutputPic2);
     
        
        panel.add(ChartColumn);
        panel.add(ChartColumn2); 
        container.add(panel).show();
        
    }

});