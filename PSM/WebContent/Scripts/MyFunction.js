//********************************************************
//更新日期: 2013.3.7
//开发人员: 何猛
//內容说明: 常用JavaScript函数
//********************************************************

/**
* 删除左右两端的空格
*/
function trim(str) {
    return str.replace(/(^\s*)|(\s*$)/g, '');
}

/**
* 获取URL的参数
*/
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r !== null)
        return decodeURIComponent(r[2]);
    return null;
}

/**
* 操作Cookie的函数
*/
function setCookie(name, value, expires, path, domain, secure) {
    // set time, it's in milliseconds
    var today = new Date();
    today.setTime(today.getTime());
    /*
	if the expires variable is set, make the correct 
	expires time, the current script below will set 
	it for x number of days, to make it for hours, 
	delete * 24, for minutes, delete * 60 * 24
	*/
    if (expires) {
        expires = expires * 1000 * 60 * 60 * 24;   //   days
    }
    var expires_date = new Date(today.getTime() + (expires));

    document.cookie = name + "=" + escape(value) +
	((expires) ? ";expires=" + expires_date.toGMTString() : "") +
	((path) ? ";path=" + path : "") +
	((domain) ? ";domain=" + domain : "") +
	((secure) ? ";secure" : "");
}

/**
* 操作Cookie的函数
*/

function getCookie(name) {
    var start = document.cookie.indexOf(name + "=");
    var len = start + name.length + 1;
    if ((!start) && (name !== document.cookie.substring(0, name.length))) {
        return null;
    }
    if (start === -1) return null;
    var end = document.cookie.indexOf(";", len);
    if (end === -1) end = document.cookie.length;
    return unescape(document.cookie.substring(len, end));
}

function deleteCookie(name, path, domain) {
    if (getCookie(name)) document.cookie = name + "=" + ((path) ? ";path=" + path : "")
        + ((domain) ? ";domain=" + domain : "") + ";expires=Thu, 01-Jan-1970 00:00:01 GMT";
}

/**
* 关键字的页面纯客户端搜索
*/
(function ($) {
    $.fn.textSearch = function (str, options) {
        var defaults = {
            divFlag: true,
            divStr: " ",
            markClass: "",
            markColor: "red",
            nullReport: true,
            callback: function () {
                return false;
            }
        };
        var sets = $.extend({}, defaults, options || {}), clStr;
        if (sets.markClass) {
            clStr = "class='" + sets.markClass + "'";
        } else {
            clStr = "style='color:" + sets.markColor + ";'";
        }

        //对前一次高亮处理的文字还原
        $("span[rel='mark']").removeAttr("class").removeAttr("style").removeAttr("rel");


        //字符串正则表达式关键字转化
        $.regTrim = function (s) {
            var imp = /[\^\.\\\|\(\)\*\+\-\$\[\]\?]/g;
            var imp_c = {};
            imp_c["^"] = "\\^";
            imp_c["."] = "\\.";
            imp_c["\\"] = "\\\\";
            imp_c["|"] = "\\|";
            imp_c["("] = "\\(";
            imp_c[")"] = "\\)";
            imp_c["*"] = "\\*";
            imp_c["+"] = "\\+";
            imp_c["-"] = "\\-";
            imp_c["$"] = "\$";
            imp_c["["] = "\\[";
            imp_c["]"] = "\\]";
            imp_c["?"] = "\\?";
            s = s.replace(imp, function (o) {
                return imp_c[o];
            });
            return s;
        };
        $(this).each(function () {
            var t = $(this);
            str = $.trim(str);
            if (str === "") {
                alert("关键字为空");
                return false;
            } else {
                //将关键字push到数组之中
                var arr = [];
                if (sets.divFlag) {
                    arr = str.split(sets.divStr);
                } else {
                    arr.push(str);
                }
            }
            var v_html = t.html();
            //删除注释
            v_html = v_html.replace(/<!--(?:.*)\-->/g, "");

            //将HTML代码支离为HTML片段和文字片段，其中文字片段用于正则替换处理，而HTML片段置之不理
            var tags = /[^<>]+|<(\/?)([A-Za-z]+)([^<>]*)>/g;
            var a = v_html.match(tags), test = 0;
            $.each(a, function (i, c) {
                if (!/<(?:.|\s)*?>/.test(c)) {//非标签
                    //开始执行替换
                    $.each(arr, function (index, con) {
                        if (con === "") { return; }
                        var reg = new RegExp($.regTrim(con), "g");
                        if (reg.test(c)) {
                            //正则替换
                            c = c.replace(reg, "♂" + con + "♀");
                            test = 1;
                        }
                    });
                    c = c.replace(/♂/g, "<span rel='mark' " + clStr + ">").replace(/♀/g, "</span>");
                    a[i] = c;
                }
            });
            //将支离数组重新组成字符串
            var new_html = a.join("");

            $(this).html(new_html);

            if (test === 0 && sets.nullReport) {
                alert("没有搜索结果");
                return false;
            }

            //执行回调函数
            sets.callback();
        });
    };
})(jQuery);