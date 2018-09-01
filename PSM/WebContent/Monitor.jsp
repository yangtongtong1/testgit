<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Cache-Control" content="no-cache, must-revalidate" />
    <meta http-equiv="Expires" content="0" />
    <link href="Monitor/demo.css" rel="stylesheet">
<title>视频监控</title>
</head>
<body>
<div id="divPlugin" class="plugin" style="margin:auto"></div>
<div class="left">
    <fieldset class="login disable">
        <legend>登录</legend>
        <table cellpadding="0" cellspacing="3" border="0">
            <tr>
                <td colspan="4">
                    <input type="button" class="btn" value="登录" onclick="clickLogin();" />
                    <input type="button" class="btn" value="退出" onclick="clickLogout();" />
                    <input type="button" class="btn2" value="获取基本信息" onclick="clickGetDeviceInfo();" />
                </td>
            </tr>
            <tr>
                <td class="tt">设备端口</td>
                <td colspan="2"><input id="deviceport" type="text" class="txt" />（可选参数）</td>
                <td>窗口分割数&nbsp;
                    <select class="sel2" onchange="changeWndNum(this.value);">
                        <option value="1" selected>1x1</option>
                        <option value="2">2x2</option>
                        <option value="3">3x3</option>
                        <option value="4">4x4</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="tt">RTSP端口</td>
                <td colspan="3"><input id="rtspport" type="text" class="txt" />（可选参数）</td>
            </tr>
            <tr>
                <td class="tt">已登录设备</td>
                <td>
                    <select id="ip" class="sel" onchange="getChannelInfo();getDevicePort();"></select>
                </td>
                <td class="tt">通道列表</td>
                <td>
                    <select id="channels" class="sel"></select>
                </td>
            </tr>
        </table>
    </fieldset>
    <fieldset class="preview disable">
        <legend>预览</legend>
        <table cellpadding="0" cellspacing="3" border="0">
            <tr>
                <td class="tt">码流类型</td>
                <td>
                    <select id="streamtype" class="sel">
                        <option value="1">主码流</option>
                        <option value="2">子码流</option>
                        <option value="3">第三码流</option>
                        <option value="4">转码码流</option>
                    </select>
                </td>
                <td>
                    <input type="button" class="btn" value="开始预览" onclick="clickStartRealPlay();" />
                    <input type="button" class="btn" value="停止预览" onclick="clickStopRealPlay();" />
                </td>
            </tr>
            <tr>
                <td class="tt">音量</td>
                <td>
                    <input type="text" id="volume" class="txt" value="50" maxlength="3" />&nbsp;<input type="button" class="btn" value="设置" onclick="clickSetVolume();" />（范围：0~100）
                </td>
                <td>
                    <input type="button" class="btn" value="打开声音" onclick="clickOpenSound();" />
                    <input type="button" class="btn" value="关闭声音" onclick="clickCloseSound();" />
                </td>
            </tr>
            <tr>
                <td colspan="3">
                    分辨率：<input id="resolutionWidth" type="text" class="txt" /> x <input id="resolutionHeight" type="text" class="txt" />
                    <input type="button" class="btn" value="设备抓图" onclick="clickDeviceCapturePic();" />
                </td>
            </tr>
        </table>
    </fieldset>
</div>
<div class="left">	
    <fieldset class="ipchannel disable">
        <legend>数字通道</legend>
        <table width="100%" cellpadding="0" cellspacing="3" border="0">
            <tr>
                <td><input type="button" class="btn" value="获取数字通道列表" onclick="clickGetDigitalChannelInfo();" /></td>
            </tr>
            <tr>
                <td>
                    <div class="digitaltdiv">
                        <table id="digitalchannellist" class="digitalchannellist" cellpadding="0" cellspacing="0" border="0"></table>
                    </div>
                </td>
            </tr>
        </table>
    </fieldset>
    
    <fieldset class="operate disable">
        <legend>操作信息</legend>
        <div id="opinfo" class="opinfo"></div>
    </fieldset>
    <fieldset class="callbacks disable">
        <legend>事件回调信息</legend>
        <div id="cbinfo" class="cbinfo"></div>
    </fieldset>
</div>
</body>
<script>
var id = ${param.id};
//登录
function clickLogin() {
 // var szIP = $("#loginip").val(),
 //     szPort = $("#port").val(),
 //     szUsername = $("#username").val(),
 //     szPassword = $("#password").val();

 // if ("" == szIP || "" == szPort) {
 //     return;
 // }
 
	var szIP = '',
	    szPort = '',
	    szUsername = '',
	    szPassword = '';
	$.ajax({ url: "MapAction!getMonitorByID?id=" + id, async: false, datatype: "json", success: function(data){
		szIP = data.ipaddress;
		szPort = data.port;
	    szUsername = data.userName,
	    szPassword = data.userPwd;
 	}});
 	login(szIP, szPort, szUsername, szPassword); 
}
</script>
<script src="Monitor/jquery-1.7.1.min.js"></script>
<script id="videonode" src="Monitor/codebase/webVideoCtrl.js"></script>
<script src="Monitor/demo.js"></script>
</html>