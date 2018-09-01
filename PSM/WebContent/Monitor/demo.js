// 初始化插件

// 全局保存当前选中窗口
var g_iWndIndex = 0; //可以不用设置这个变量，有窗口参数的接口中，不用传值，开发包会默认使用当前选择窗口
$(function () {
    // 检查插件是否已经安装过
    var iRet = WebVideoCtrl.I_CheckPluginInstall();
    if (-1 == iRet) {
        alert("您还未安装过插件，双击开发包目录里的WebComponentsKit.exe安装！");
        return;
    }

    // 初始化插件参数及插入插件
    WebVideoCtrl.I_InitPlugin(800, 480, {
        bWndFull: true,     //是否支持单窗口双击全屏，默认支持 true:支持 false:不支持
        iPackageType: 2,    //2:PS 11:MP4
        iWndowType: 1,
        cbSelWnd: function (xmlDoc) {
            g_iWndIndex = parseInt($(xmlDoc).find("SelectWnd").eq(0).text(), 10);
            var szInfo = "当前选择的窗口编号：" + g_iWndIndex;
            showCBInfo(szInfo);
        },
        cbDoubleClickWnd: function (iWndIndex, bFullScreen) {
            var szInfo = "当前放大的窗口编号：" + iWndIndex;
            if (!bFullScreen) {            
                szInfo = "当前还原的窗口编号：" + iWndIndex;
            }
            showCBInfo(szInfo);
                        
            // 此处可以处理单窗口的码流切换
            /*if (bFullScreen) {
                clickStartRealPlay(1);
            } else {
                clickStartRealPlay(2);
            }*/
        },
        cbEvent: function (iEventType, iParam1, iParam2) {
            if (2 == iEventType) {// 回放正常结束
                showCBInfo("窗口" + iParam1 + "回放结束！");
            } else if (-1 == iEventType) {
                showCBInfo("设备" + iParam1 + "网络错误！");
            } else if (3001 == iEventType) {
                clickStopRecord(g_szRecordType, iParam1);
            }
        },
        cbRemoteConfig: function () {
            showCBInfo("关闭远程配置库！");
        },
        cbInitPluginComplete: function () {
            WebVideoCtrl.I_InsertOBJECTPlugin("divPlugin");

            // 检查插件是否最新
            if (-1 == WebVideoCtrl.I_CheckPluginVersion()) {
                alert("检测到新的插件版本，双击开发包目录里的WebComponentsKit.exe升级！");
                return;
            }
        	clickLogin();
        }
    });

    // 窗口事件绑定
    $(window).bind({
        resize: function () {
            var $Restart = $("#restartDiv");
            if ($Restart.length > 0) {
                var oSize = getWindowSize();
                $Restart.css({
                    width: oSize.width + "px",
                    height: oSize.height + "px"
                });
            }
        }
    });

    //初始化日期时间
    var szCurTime = dateFormat(new Date(), "yyyy-MM-dd");
    $("#starttime").val(szCurTime + " 00:00:00");
    $("#endtime").val(szCurTime + " 23:59:59");
});

// 显示操作信息
function showOPInfo(szInfo, status, xmlDoc) {
    var szTip = "<div>" + dateFormat(new Date(), "yyyy-MM-dd hh:mm:ss") + " " + szInfo;
    if (typeof status != "undefined" && status != 200) {
        var szStatusString = $(xmlDoc).find("statusString").eq(0).text();
        var szSubStatusCode = $(xmlDoc).find("subStatusCode").eq(0).text();
        if ("" === szSubStatusCode) {
            szTip += "(" + status + ", " + szStatusString + ")";
        } else {
            szTip += "(" + status + ", " + szSubStatusCode + ")";
        }
    }
    szTip += "</div>";

    $("#opinfo").html(szTip + $("#opinfo").html());
}

// 显示回调信息
function showCBInfo(szInfo) {
    szInfo = "<div>" + dateFormat(new Date(), "yyyy-MM-dd hh:mm:ss") + " " + szInfo + "</div>";
    $("#cbinfo").html(szInfo + $("#cbinfo").html());
}

// 格式化时间
function dateFormat(oDate, fmt) {
    var o = {
        "M+": oDate.getMonth() + 1, //月份
        "d+": oDate.getDate(), //日
        "h+": oDate.getHours(), //小时
        "m+": oDate.getMinutes(), //分
        "s+": oDate.getSeconds(), //秒
        "q+": Math.floor((oDate.getMonth() + 3) / 3), //季度
        "S": oDate.getMilliseconds()//毫秒
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (oDate.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}

// 获取窗口尺寸
function getWindowSize() {
    var nWidth = $(this).width() + $(this).scrollLeft(),
        nHeight = $(this).height() + $(this).scrollTop();

    return {width: nWidth, height: nHeight};
}

// 打开选择框 0：文件夹  1：文件
function clickOpenFileDlg(id, iType) {
    var szDirPath = WebVideoCtrl.I_OpenFileDlg(iType);
    
    if (szDirPath != -1 && szDirPath != "" && szDirPath != null) {
        $("#" + id).val(szDirPath);
    }
}

// 获取本地参数
function clickGetLocalCfg() {
    var xmlDoc = WebVideoCtrl.I_GetLocalCfg();

    if (xmlDoc != null) {
        $("#netsPreach").val($(xmlDoc).find("BuffNumberType").eq(0).text());
        $("#wndSize").val($(xmlDoc).find("PlayWndType").eq(0).text());
        $("#rulesInfo").val($(xmlDoc).find("IVSMode").eq(0).text());
        $("#captureFileFormat").val($(xmlDoc).find("CaptureFileFormat").eq(0).text());
        $("#packSize").val($(xmlDoc).find("PackgeSize").eq(0).text());
        $("#recordPath").val($(xmlDoc).find("RecordPath").eq(0).text());
        $("#downloadPath").val($(xmlDoc).find("DownloadPath").eq(0).text());
        $("#previewPicPath").val($(xmlDoc).find("CapturePath").eq(0).text());
        $("#playbackPicPath").val($(xmlDoc).find("PlaybackPicPath").eq(0).text());
        $("#devicePicPath").val($(xmlDoc).find("DeviceCapturePath").eq(0).text());
        $("#playbackFilePath").val($(xmlDoc).find("PlaybackFilePath").eq(0).text());
        $("#protocolType").val($(xmlDoc).find("ProtocolType").eq(0).text());

        showOPInfo("本地配置获取成功！");
    } else {
        showOPInfo("本地配置获取失败！");
    }
}

// 设置本地参数
function clickSetLocalCfg() {
    var arrXml = [],
        szInfo = "";
    
    arrXml.push("<LocalConfigInfo>");
    arrXml.push("<PackgeSize>" + $("#packSize").val() + "</PackgeSize>");
    arrXml.push("<PlayWndType>" + $("#wndSize").val() + "</PlayWndType>");
    arrXml.push("<BuffNumberType>" + $("#netsPreach").val() + "</BuffNumberType>");
    arrXml.push("<RecordPath>" + $("#recordPath").val() + "</RecordPath>");
    arrXml.push("<CapturePath>" + $("#previewPicPath").val() + "</CapturePath>");
    arrXml.push("<PlaybackFilePath>" + $("#playbackFilePath").val() + "</PlaybackFilePath>");
    arrXml.push("<PlaybackPicPath>" + $("#playbackPicPath").val() + "</PlaybackPicPath>");
    arrXml.push("<DeviceCapturePath>" + $("#devicePicPath").val() + "</DeviceCapturePath>");
    arrXml.push("<DownloadPath>" + $("#downloadPath").val() + "</DownloadPath>");
    arrXml.push("<IVSMode>" + $("#rulesInfo").val() + "</IVSMode>");
    arrXml.push("<CaptureFileFormat>" + $("#captureFileFormat").val() + "</CaptureFileFormat>");
    arrXml.push("<ProtocolType>" + $("#protocolType").val() + "</ProtocolType>");
    arrXml.push("</LocalConfigInfo>");

    var iRet = WebVideoCtrl.I_SetLocalCfg(arrXml.join(""));

    if (0 == iRet) {
        szInfo = "本地配置设置成功！";
    } else {
        szInfo = "本地配置设置失败！";
    }
    showOPInfo(szInfo);
}

// 窗口分割数
function changeWndNum(iType) {
    iType = parseInt(iType, 10);
    WebVideoCtrl.I_ChangeWndNum(iType);
}

function login(szIP, szPort, szUsername, szPassword) {
    var szDeviceIdentify = szIP + "_" + szPort;
    var iRet = WebVideoCtrl.I_Login(szIP, 1, szPort, szUsername, szPassword, {
        success: function (xmlDoc) {            
            showOPInfo(szDeviceIdentify + " 登录成功！");

            $("#ip").prepend("<option value='" + szDeviceIdentify + "'>" + szDeviceIdentify + "</option>");
            setTimeout(function () {
                $("#ip").val(szDeviceIdentify);
                getChannelInfo();
                getDevicePort();
            }, 10);
        },
        error: function (status, xmlDoc) {
            showOPInfo(szDeviceIdentify + " 登录失败！", status, xmlDoc);
        }
    });

    if (-1 == iRet) {
        showOPInfo(szDeviceIdentify + " 已登录过！");
    }
}

// 退出
function clickLogout() {
    var szDeviceIdentify = $("#ip").val(),
        szInfo = "";

    if (null == szDeviceIdentify) {
        return;
    }

    var iRet = WebVideoCtrl.I_Logout(szDeviceIdentify);
    if (0 == iRet) {
        szInfo = "退出成功！";

        $("#ip option[value='" + szDeviceIdentify + "']").remove();
        getChannelInfo();
        getDevicePort();
    } else {
        szInfo = "退出失败！";
    }
    showOPInfo(szDeviceIdentify + " " + szInfo);
}

// 获取设备信息
function clickGetDeviceInfo() {
    var szDeviceIdentify = $("#ip").val();

    if (null == szDeviceIdentify) {
        return;
    }

    WebVideoCtrl.I_GetDeviceInfo(szDeviceIdentify, {
        success: function (xmlDoc) {
            var arrStr = [];
            arrStr.push("设备名称：" + $(xmlDoc).find("deviceName").eq(0).text() + "\r\n");
            arrStr.push("设备ID：" + $(xmlDoc).find("deviceID").eq(0).text() + "\r\n");
            arrStr.push("型号：" + $(xmlDoc).find("model").eq(0).text() + "\r\n");
            arrStr.push("设备序列号：" + $(xmlDoc).find("serialNumber").eq(0).text() + "\r\n");
            arrStr.push("MAC地址：" + $(xmlDoc).find("macAddress").eq(0).text() + "\r\n");
            arrStr.push("主控版本：" + $(xmlDoc).find("firmwareVersion").eq(0).text() + " " + $(xmlDoc).find("firmwareReleasedDate").eq(0).text() + "\r\n");
            arrStr.push("编码版本：" + $(xmlDoc).find("encoderVersion").eq(0).text() + " " + $(xmlDoc).find("encoderReleasedDate").eq(0).text() + "\r\n");
            
            showOPInfo(szDeviceIdentify + " 获取设备信息成功！");
            alert(arrStr.join(""));
        },
        error: function (status, xmlDoc) {
            showOPInfo(szDeviceIdentify + " 获取设备信息失败！", status, xmlDoc);
        }
    });
}

// 获取通道
function getChannelInfo() {
    var szDeviceIdentify = $("#ip").val(),
        oSel = $("#channels").empty();

    if (null == szDeviceIdentify) {
        return;
    }

    // 模拟通道
    WebVideoCtrl.I_GetAnalogChannelInfo(szDeviceIdentify, {
        async: false,
        success: function (xmlDoc) {
            var oChannels = $(xmlDoc).find("VideoInputChannel");

            $.each(oChannels, function (i) {
                var id = $(this).find("id").eq(0).text(),
                    name = $(this).find("name").eq(0).text();
                if ("" == name) {
                    name = "Camera " + (i < 9 ? "0" + (i + 1) : (i + 1));
                }
                oSel.append("<option value='" + id + "' bZero='false'>" + name + "</option>");
            });
            showOPInfo(szDeviceIdentify + " 获取模拟通道成功！");
        },
        error: function (status, xmlDoc) {
            showOPInfo(szDeviceIdentify + " 获取模拟通道失败！", status, xmlDoc);
        }
    });
    // 数字通道
    WebVideoCtrl.I_GetDigitalChannelInfo(szDeviceIdentify, {
        async: false,
        success: function (xmlDoc) {
            var oChannels = $(xmlDoc).find("InputProxyChannelStatus");

            $.each(oChannels, function (i) {
                var id = $(this).find("id").eq(0).text(),
                    name = $(this).find("name").eq(0).text(),
                    online = $(this).find("online").eq(0).text();
                if ("false" == online) {// 过滤禁用的数字通道
                    return true;
                }
                if ("" == name) {
                    name = "IPCamera " + (i < 9 ? "0" + (i + 1) : (i + 1));
                }
                oSel.append("<option value='" + id + "' bZero='false'>" + name + "</option>");
            });
            showOPInfo(szDeviceIdentify + " 获取数字通道成功！");
        },
        error: function (status, xmlDoc) {
            showOPInfo(szDeviceIdentify + " 获取数字通道失败！", status, xmlDoc);
        }
    });
    // 零通道
    WebVideoCtrl.I_GetZeroChannelInfo(szDeviceIdentify, {
        async: false,
        success: function (xmlDoc) {
            var oChannels = $(xmlDoc).find("ZeroVideoChannel");
            
            $.each(oChannels, function (i) {
                var id = $(this).find("id").eq(0).text(),
                    name = $(this).find("name").eq(0).text();
                if ("" == name) {
                    name = "Zero Channel " + (i < 9 ? "0" + (i + 1) : (i + 1));
                }
                if ("true" == $(this).find("enabled").eq(0).text()) {// 过滤禁用的零通道
                    oSel.append("<option value='" + id + "' bZero='true'>" + name + "</option>");
                }
            });
            showOPInfo(szDeviceIdentify + " 获取零通道成功！");
        },
        error: function (status, xmlDoc) {
            showOPInfo(szDeviceIdentify + " 获取零通道失败！", status, xmlDoc);
        }
    });
}

// 获取端口
function getDevicePort() {
    var szDeviceIdentify = $("#ip").val();

    if (null == szDeviceIdentify) {
        return;
    }

    var oPort = WebVideoCtrl.I_GetDevicePort(szDeviceIdentify);
    if (oPort != null) {
        $("#deviceport").val(oPort.iDevicePort);
        $("#rtspport").val(oPort.iRtspPort);

        showOPInfo(szDeviceIdentify + " 获取端口成功！");
    } else {
        showOPInfo(szDeviceIdentify + " 获取端口失败！");
    }
}

// 获取数字通道
function clickGetDigitalChannelInfo() {
    var szDeviceIdentify = $("#ip").val(),
        iAnalogChannelNum = 0;

    $("#digitalchannellist").empty();

    if (null == szDeviceIdentify) {
        return;
    }

    // 模拟通道
    WebVideoCtrl.I_GetAnalogChannelInfo(szDeviceIdentify, {
        async: false,
        success: function (xmlDoc) {
            iAnalogChannelNum = $(xmlDoc).find("VideoInputChannel").length;
        },
        error: function () {
            
        }
    });

    // 数字通道
    WebVideoCtrl.I_GetDigitalChannelInfo(szDeviceIdentify, {
        async: false,
        success: function (xmlDoc) {
            var oChannels = $(xmlDoc).find("InputProxyChannelStatus");
            
            $.each(oChannels, function () {
                var id = parseInt($(this).find("id").eq(0).text(), 10),
                    ipAddress = $(this).find("ipAddress").eq(0).text(),
                    srcInputPort = $(this).find("srcInputPort").eq(0).text(),
                    managePortNo = $(this).find("managePortNo").eq(0).text(),
                    online = $(this).find("online").eq(0).text(),
                    proxyProtocol = $(this).find("proxyProtocol").eq(0).text();
                            
                var objTr = $("#digitalchannellist").get(0).insertRow(-1);
                var objTd = objTr.insertCell(0);
                objTd.innerHTML = (id - iAnalogChannelNum) < 10 ? "D0" + (id - iAnalogChannelNum) : "D" + (id - iAnalogChannelNum);
                objTd = objTr.insertCell(1);
                objTd.width = "25%";
                objTd.innerHTML = ipAddress;
                objTd = objTr.insertCell(2);
                objTd.width = "15%";
                objTd.innerHTML = srcInputPort;
                objTd = objTr.insertCell(3);
                objTd.width = "20%";
                objTd.innerHTML = managePortNo;
                objTd = objTr.insertCell(4);
                objTd.width = "15%";
                objTd.innerHTML = "true" == online ? "在线" : "离线";
                objTd = objTr.insertCell(5);
                objTd.width = "25%";
                objTd.innerHTML = proxyProtocol;
            });
            showOPInfo(szDeviceIdentify + " 获取数字通道成功！");
        },
        error: function (status, xmlDoc) {
            showOPInfo(szDeviceIdentify + " 没有数字通道！", status, xmlDoc);
        }
    });
}

// 开始预览
function clickStartRealPlay(iStreamType) {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        szDeviceIdentify = $("#ip").val(),
        iRtspPort = parseInt($("#rtspport").val(), 10),
        iChannelID = parseInt($("#channels").val(), 10),
        bZeroChannel = $("#channels option").eq($("#channels").get(0).selectedIndex).attr("bZero") == "true" ? true : false,
        szInfo = "";

    if ("undefined" === typeof iStreamType) {
        iStreamType = parseInt($("#streamtype").val(), 10);
    }

    if (null == szDeviceIdentify) {
        return;
    }

    var startRealPlay = function () {
        WebVideoCtrl.I_StartRealPlay(szDeviceIdentify, {
            iRtspPort: iRtspPort,
            iStreamType: iStreamType,
            iChannelID: iChannelID,
            bZeroChannel: bZeroChannel,
            success: function () {
                szInfo = "开始预览成功！";
                showOPInfo(szDeviceIdentify + " " + szInfo);
            },
            error: function (status, xmlDoc) {
                if (403 === status) {
                    szInfo = "设备不支持Websocket取流！";
                } else {
                    szInfo = "开始预览失败！";
                }
                showOPInfo(szDeviceIdentify + " " + szInfo);
            }
        });
    };

    if (oWndInfo != null) {// 已经在播放了，先停止
        WebVideoCtrl.I_Stop({
            success: function () {
                startRealPlay();
            }
        });
    } else {
        startRealPlay();
    }
}

// 停止预览
function clickStopRealPlay() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        szInfo = "";

    if (oWndInfo != null) {
        WebVideoCtrl.I_Stop({
            success: function () {
                szInfo = "停止预览成功！";
                showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
            },
            error: function () {
                szInfo = "停止预览失败！";
                showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
            }
        });
    }
}

// 打开声音
function clickOpenSound() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        szInfo = "";

    if (oWndInfo != null) {
        var allWndInfo = WebVideoCtrl.I_GetWindowStatus();
        // 循环遍历所有窗口，如果有窗口打开了声音，先关闭
        for (var i = 0, iLen = allWndInfo.length; i < iLen; i++) {
            oWndInfo = allWndInfo[i];
            if (oWndInfo.bSound) {
                WebVideoCtrl.I_CloseSound(oWndInfo.iIndex);
                break;
            }
        }

        var iRet = WebVideoCtrl.I_OpenSound();

        if (0 == iRet) {
            szInfo = "打开声音成功！";
        } else {
            szInfo = "打开声音失败！";
        }
        showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
    }
}

// 关闭声音
function clickCloseSound() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        szInfo = "";

    if (oWndInfo != null) {
        var iRet = WebVideoCtrl.I_CloseSound();
        if (0 == iRet) {
            szInfo = "关闭声音成功！";
        } else {
            szInfo = "关闭声音失败！";
        }
        showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
    }
}

// 设置音量
function clickSetVolume() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        iVolume = parseInt($("#volume").val(), 10),
        szInfo = "";

    if (oWndInfo != null) {
        var iRet = WebVideoCtrl.I_SetVolume(iVolume);
        if (0 == iRet) {
            szInfo = "音量设置成功！";
        } else {
            szInfo = "音量设置失败！";
        }
        showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
    }
}

// 抓图
function clickCapturePic() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        szInfo = "";

    if (oWndInfo != null) {
        var xmlDoc = WebVideoCtrl.I_GetLocalCfg();
        var szCaptureFileFormat = "0";
        if (xmlDoc != null) {
            szCaptureFileFormat = $(xmlDoc).find("CaptureFileFormat").eq(0).text();
        }

        var szChannelID = $("#channels").val();
        var szPicName = oWndInfo.szDeviceIdentify + "_" + szChannelID + "_" + new Date().getTime();
        
        szPicName += ("0" === szCaptureFileFormat) ? ".jpg": ".bmp";

        var iRet = WebVideoCtrl.I_CapturePic(szPicName, {
            bDateDir: true  //是否生成日期文件
        });
        if (0 == iRet) {
            szInfo = "抓图成功！";
        } else {
            szInfo = "抓图失败！";
        }
        showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
    }
}

// 开始录像
var g_szRecordType = "";
function clickStartRecord(szType) {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        szInfo = "";

    g_szRecordType = szType;

    if (oWndInfo != null) {
        var szChannelID = $("#channels").val(),
            szFileName = oWndInfo.szDeviceIdentify + "_" + szChannelID + "_" + new Date().getTime();

        WebVideoCtrl.I_StartRecord(szFileName, {
            bDateDir: true, //是否生成日期文件
            success: function () {
                if ('realplay' === szType) {
                    szInfo = "开始录像成功！";
                } else if ('playback' === szType) {
                    szInfo = "开始剪辑成功！";
                }
                showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
            },
            error: function () {
                if ('realplay' === szType) {
                    szInfo = "开始录像失败！";
                } else if ('playback' === szType) {
                    szInfo = "开始剪辑失败！";
                }
                showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
            }
        });
    }
}

// 停止录像
function clickStopRecord(szType, iWndIndex) {
    if ("undefined" === typeof iWndIndex) {
        iWndIndex = g_iWndIndex;
    }
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(iWndIndex),
        szInfo = "";

    if (oWndInfo != null) {
        WebVideoCtrl.I_StopRecord({
            success: function () {
                if ('realplay' === szType) {
                    szInfo = "停止录像成功！";
                } else if ('playback' === szType) {
                    szInfo = "停止剪辑成功！";
                }
                showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
            },
            error: function () {
                if ('realplay' === szType) {
                    szInfo = "停止录像失败！";
                } else if ('playback' === szType) {
                    szInfo = "停止剪辑失败！";
                }
                showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
            }
        });
    }
}

// 启用电子放大
function clickEnableEZoom() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        szInfo = "";

    if (oWndInfo != null) {
        var iRet = WebVideoCtrl.I_EnableEZoom();
        if (0 == iRet) {
            szInfo = "启用电子放大成功！";
        } else {
            szInfo = "启用电子放大失败！";
        }
        showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
    }
}

// 禁用电子放大
function clickDisableEZoom() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        szInfo = "";

    if (oWndInfo != null) {
        var iRet = WebVideoCtrl.I_DisableEZoom();
        if (0 == iRet) {
            szInfo = "禁用电子放大成功！";
        } else {
            szInfo = "禁用电子放大失败！";
        }
        showOPInfo(oWndInfo.szDeviceIdentify + " " + szInfo);
    }
}

// 全屏
function clickFullScreen() {
    WebVideoCtrl.I_FullScreen(true);
}

// PTZ控制 9为自动，1,2,3,4,5,6,7,8为方向PTZ
var g_bPTZAuto = false;
function mouseDownPTZControl(iPTZIndex) {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        bZeroChannel = $("#channels option").eq($("#channels").get(0).selectedIndex).attr("bZero") == "true" ? true : false,
        iPTZSpeed = $("#ptzspeed").val();

    if (bZeroChannel) {// 零通道不支持云台
        return;
    }
    
    if (oWndInfo != null) {
        if (9 == iPTZIndex && g_bPTZAuto) {
            iPTZSpeed = 0;// 自动开启后，速度置为0可以关闭自动
        } else {
            g_bPTZAuto = false;// 点击其他方向，自动肯定会被关闭
        }

        WebVideoCtrl.I_PTZControl(iPTZIndex, false, {
            iPTZSpeed: iPTZSpeed,
            success: function (xmlDoc) {
                if (9 == iPTZIndex && g_bPTZAuto) {
                    showOPInfo(oWndInfo.szDeviceIdentify + " 停止云台成功！");
                } else {
                    showOPInfo(oWndInfo.szDeviceIdentify + " 开启云台成功！");
                }
                if (9 == iPTZIndex) {
                    g_bPTZAuto = !g_bPTZAuto;
                }
            },
            error: function (status, xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 开启云台失败！", status, xmlDoc);
            }
        });
    }
}

// 方向PTZ停止
function mouseUpPTZControl() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex);

    if (oWndInfo != null) {
        WebVideoCtrl.I_PTZControl(1, true, {
            success: function (xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 停止云台成功！");
            },
            error: function (status, xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 停止云台失败！", status, xmlDoc);
            }
        });
    }
}

// 设置预置点
function clickSetPreset() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        iPresetID = parseInt($("#preset").val(), 10);

    if (oWndInfo != null) {
        WebVideoCtrl.I_SetPreset(iPresetID, {
            success: function (xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 设置预置点成功！");
            },
            error: function (status, xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 设置预置点失败！", status, xmlDoc);
            }
        });
    }
}

// 调用预置点
function clickGoPreset() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex),
        iPresetID = parseInt($("#preset").val(), 10);

    if (oWndInfo != null) {
        WebVideoCtrl.I_GoPreset(iPresetID, {
            success: function (xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 调用预置点成功！");
            },
            error: function (status, xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 调用预置点失败！", status, xmlDoc);
            }
        });
    }
}

// OSD时间
function clickGetOSDTime() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex);
    
    if (oWndInfo != null) {
        var szTime = WebVideoCtrl.I_GetOSDTime({
            success: function (szOSDTime) {
                $("#osdtime").val(szOSDTime);
                showOPInfo(oWndInfo.szDeviceIdentify + " 获取OSD时间成功！");
            },
            error: function () {
                showOPInfo(oWndInfo.szDeviceIdentify + " 获取OSD时间失败！");
            }
        });
    }
}


// 重连
function reconnect(szDeviceIdentify) {
    WebVideoCtrl.I_Reconnect(szDeviceIdentify, {
        success: function (xmlDoc) {
            $("#restartDiv").remove();
        },
        error: function (status, xmlDoc) {
            if (401 == status) {// 无插件方案，重启后session已失效，程序执行登出，从已登录设备中删除
                $("#restartDiv").remove();
                clickLogout();
            } else {
                setTimeout(function () {reconnect(szDeviceIdentify);}, 5000);
            }
        }
    });
}

// 开始升级
var g_tUpgrade = 0;
function clickStartUpgrade(szDeviceIdentify) {
    var szDeviceIdentify = $("#ip").val(),
        szFileName = $("#upgradeFile").val();

    if (null == szDeviceIdentify) {
        return;
    }

    if ("" == szFileName) {
        alert("请选择升级文件！");
        return;
    }

    var iRet = WebVideoCtrl.I_StartUpgrade(szDeviceIdentify, szFileName);
    if (0 == iRet) {
        g_tUpgrade = setInterval("getUpgradeStatus('" + szDeviceIdentify + "')", 1000);
    } else {
        showOPInfo(szDeviceIdentify + " 升级失败！");
    }
}

// 获取升级状态
function getUpgradeStatus(szDeviceIdentify) {
    var iStatus = WebVideoCtrl.I_UpgradeStatus();
    if (iStatus == 0) {
        var iProcess = WebVideoCtrl.I_UpgradeProgress();
        if (iProcess < 0) {
            clearInterval(g_tUpgrade);
            g_tUpgrade = 0;
            showOPInfo(szDeviceIdentify + " 获取进度失败！");
            return;
        } else if (iProcess < 100) {
            if (0 == $("#restartDiv").length) {
                $("<div id='restartDiv' class='freeze'></div>").appendTo("body");
                var oSize = getWindowSize();
                $("#restartDiv").css({
                    width: oSize.width + "px",
                    height: oSize.height + "px",
                    lineHeight: oSize.height + "px",
                    left: 0,
                    top: 0
                });
            }
            $("#restartDiv").text(iProcess + "%");
        } else {
            WebVideoCtrl.I_StopUpgrade();
            clearInterval(g_tUpgrade);
            g_tUpgrade = 0;

            $("#restartDiv").remove();

            WebVideoCtrl.I_Restart(szDeviceIdentify, {
                success: function (xmlDoc) {
                    $("<div id='restartDiv' class='freeze'>重启中...</div>").appendTo("body");
                    var oSize = getWindowSize();
                    $("#restartDiv").css({
                        width: oSize.width + "px",
                        height: oSize.height + "px",
                        lineHeight: oSize.height + "px",
                        left: 0,
                        top: 0
                    });
                    setTimeout("reconnect('" + szDeviceIdentify + "')", 20000);
                },
                error: function (status, xmlDoc) {
                    showOPInfo(szDeviceIdentify + " 重启失败！", status, xmlDoc);
                }
            });
        }
    } else if (iStatus == 1) {
        WebVideoCtrl.I_StopUpgrade();
        showOPInfo(szDeviceIdentify + " 升级失败！");
        clearInterval(g_tUpgrade);
        g_tUpgrade = 0;
    } else if (iStatus == 2) {
        mWebVideoCtrl.I_StopUpgrade();
        showOPInfo(szDeviceIdentify + " 语言不匹配！");
        clearInterval(g_tUpgrade);
        g_tUpgrade = 0;
    } else {
        mWebVideoCtrl.I_StopUpgrade();
        showOPInfo(szDeviceIdentify + " 获取状态失败！");
        clearInterval(g_tUpgrade);
        g_tUpgrade = 0;
    }
}

// 检查插件版本
function clickCheckPluginVersion() {
    var iRet = WebVideoCtrl.I_CheckPluginVersion();
    if (0 == iRet) {
        alert("您的插件版本已经是最新的！");
    } else {
        alert("检测到新的插件版本！");
    }
}

// 远程配置库
function clickRemoteConfig() {
    var szDeviceIdentify = $("#ip").val(),
        iDevicePort = parseInt($("#deviceport").val(), 10) || "",
        szInfo = "";
    
    if (null == szDeviceIdentify) {
        return;
    }

    var iRet = WebVideoCtrl.I_RemoteConfig(szDeviceIdentify, {
        iDevicePort: iDevicePort,
        iLan: 1
    });

    if (-1 == iRet) {
        szInfo = "调用远程配置库失败！";
    } else {
        szInfo = "调用远程配置库成功！";
    }
    showOPInfo(szDeviceIdentify + " " + szInfo);
}

function clickRestoreDefault() {
    var szDeviceIdentify = $("#ip").val(),
        szMode = "basic";
    WebVideoCtrl.I_RestoreDefault(szDeviceIdentify, szMode, {
        timeout: 30000,
        success: function (xmlDoc) {
            $("#restartDiv").remove();
            showOPInfo(szDeviceIdentify + " 恢复默认参数成功！");
            //恢复完成后需要重启
            WebVideoCtrl.I_Restart(szDeviceIdentify, {
                success: function (xmlDoc) {
                    $("<div id='restartDiv' class='freeze'>重启中...</div>").appendTo("body");
                    var oSize = getWindowSize();
                    $("#restartDiv").css({
                        width: oSize.width + "px",
                        height: oSize.height + "px",
                        lineHeight: oSize.height + "px",
                        left: 0,
                        top: 0
                    });
                    setTimeout("reconnect('" + szDeviceIdentify + "')", 20000);
                },
                error: function (status, xmlDoc) {
                    showOPInfo(szDeviceIdentify + " 重启失败！", status, xmlDoc);
                }
            });
        },
        error: function (status, xmlDoc) {
            showOPInfo(szDeviceIdentify + " 恢复默认参数失败！", status, xmlDoc);
        }
    });
}

function PTZZoomIn() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex);

    if (oWndInfo != null) {
        WebVideoCtrl.I_PTZControl(10, false, {
            iWndIndex: g_iWndIndex,
            success: function (xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 调焦+成功！");
            },
            error: function (status, xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + "  调焦+失败！", status, xmlDoc);
            }
        });
    }
}

function PTZZoomout() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex);

    if (oWndInfo != null) {
        WebVideoCtrl.I_PTZControl(11, false, {
            iWndIndex: g_iWndIndex,
            success: function (xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 调焦-成功！");
            },
            error: function (status, xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + "  调焦-失败！", status, xmlDoc);
            }
        });
    }
}

function PTZZoomStop() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex);

    if (oWndInfo != null) {
        WebVideoCtrl.I_PTZControl(11, true, {
            iWndIndex: g_iWndIndex,
            success: function (xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 调焦停止成功！");
            },
            error: function (status, xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + "  调焦停止失败！", status, xmlDoc);
            }
        });
    }
}

function PTZFocusIn() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex);

    if (oWndInfo != null) {
        WebVideoCtrl.I_PTZControl(12, false, {
            iWndIndex: g_iWndIndex,
            success: function (xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 聚焦+成功！");
            },
            error: function (status, xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + "  聚焦+失败！", status, xmlDoc);
            }
        });
    }
}


function PTZIrisStop() {
    var oWndInfo = WebVideoCtrl.I_GetWindowStatus(g_iWndIndex);

    if (oWndInfo != null) {
        WebVideoCtrl.I_PTZControl(14, true, {
            iWndIndex: g_iWndIndex,
            success: function (xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + " 光圈停止成功！");
            },
            error: function (status, xmlDoc) {
                showOPInfo(oWndInfo.szDeviceIdentify + "  光圈停止失败！", status, xmlDoc);
            }
        });
    }
}

// 切换模式
function changeIPMode(iType) {
    var arrPort = [0, 7071, 80];

    $("#serverport").val(arrPort[iType]);
}

// 获取设备IP
function clickGetDeviceIP() {
    var iDeviceMode = parseInt($("#devicemode").val(), 10),
        szAddress = $("#serveraddress").val(),
        iPort = parseInt($("#serverport").val(), 10) || 0,
        szDeviceID = $("#deviceid").val(),
        szDeviceInfo = "";

    szDeviceInfo = WebVideoCtrl.I_GetIPInfoByMode(iDeviceMode, szAddress, iPort, szDeviceID);

    if ("" == szDeviceInfo) {
        showOPInfo("设备IP和端口解析失败！");
    } else {
        showOPInfo("设备IP和端口解析成功！");

        var arrTemp = szDeviceInfo.split("-");
        $("#loginip").val(arrTemp[0]);
        $("#deviceport").val(arrTemp[1]);
    }
}

function getSnapPolygon(szId) {
    var szInfo = WebVideoCtrl.I_GetSnapPolygonInfo(g_iWndIndex);
    var oXML = loadXML(szInfo);

    if (typeof szId === "undefined") {
        return oXML;
    } else {
        var iIndex = -1;

        var aNodeList = $(oXML).find("SnapPolygon");
        if (aNodeList.length > 0) {
            $.each(aNodeList, function (i) {
                if ($(this).find("id").text() === szId) {
                    iIndex = i;
                    return false;
                }
            });
        }

        return iIndex;
    }
}


function loadXML(szXml) {
    if(null == szXml || "" == szXml) {
        return null;
    }

    var oXmlDoc = null;

    if (window.DOMParser) {
        var oParser = new DOMParser();
        oXmlDoc = oParser.parseFromString(szXml, "text/xml");
    } else {
        oXmlDoc = new ActiveXObject("Microsoft.XMLDOM");
        oXmlDoc.async = false;
        oXmlDoc.loadXML(szXml);
    }

    return oXmlDoc;
}

function toXMLStr(oXmlDoc) {
    var szXmlDoc = "";

    try {
        var oSerializer = new XMLSerializer();
        szXmlDoc = oSerializer.serializeToString(oXmlDoc);
    } catch (e) {
        try {
            szXmlDoc = oXmlDoc.xml;
        } catch (e) {
            return "";
        }
    }
    if (szXmlDoc.indexOf("<?xml") == -1) {
        szXmlDoc = "<?xml version='1.0' encoding='utf-8'?>" + szXmlDoc;
    }

    return szXmlDoc;
}

function encodeString(str) {
    if (str) {
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    } else {
        return "";
    }
}