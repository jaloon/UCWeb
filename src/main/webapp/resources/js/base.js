/*
 * 基础方法封装
 * @Author: chenlong 
 * @Date: 2017-09-26 15:00:06 
 * @Last Modified by: chenlong
 * @Last Modified time: 2018-05-03 16:42:40
 */

/**
 * 空对象判断
 * @param {*} obj
 */
function isNull(obj) {
    if (obj == null || obj == "" || obj == undefined) {
        return true;
    } else {
        return false;
    }
}

/**
* 将整形数版本号转为版本号字符串
* @param ver {number} 整形数版本号
* @returns {string} 版本号字符串（格式：1.2.3456）
*/
function stringifyVer(ver) {
    if (ver == 0) {
        return "";
    }
    return ((ver >> 24) & 0xff) + '.'
        + ((ver >> 16) & 0xff) + '.'
        + (ver & 0xffff);
}

/**
 * 将版本号字符串解析为整形数版本号
 * @param verStr {string} 版本号字符串（格式：1.2.3456）
 * @returns {number} 整形数版本号
 */
function parseVerToInt(verStr) {
    if (isNull(verStr)) {
        return 0;
    }
    var verArr = verStr.split("\.");
    if (verArr.length != 3) {
        throw "版本号字符串格式不正确！";
    }
    var v1 = parseInt(verArr[0], 10);
    var v2 = parseInt(verArr[1], 10);
    var v3 = parseInt(verArr[2], 10);
    return ((0xff & v1) << 24) | ((0xff & v2) << 16) | (0xffff & v3);
}

/**
 * 十进制ID转为十六进制
 * @param {number} id
 */
function toHexId(id) {
    var hexId = id.toString(16).toUpperCase();
    if (hexId.length < 8) {
        for (var i = 0, len = 8 - hexId.length; i < len; i++) {
            hexId = "0" + hexId;
        }
    }
    return hexId;
}

/**
 * 将一个4字节整形数转为两个2字节整形数
 * @param {*} dword 
 */
function parseIntToTwoShort(dword) {
    var hex = dword.toString(16);
    var len = hex.length;
    if (len > 4) { //第一个2字节整形数不为0
        var word1 = hex.substr(0, len - 4);
        var word2 = hex.substr(len - 4, 4);
        return [parseInt(word1, 16), parseInt(word2, 16)];
    }
    return [0, dword];
}

/**
 * 将一个4字节整形数转为两个2字节整形数
 * @param {*} dword 
 */
function parseIntToTwoShortUseBit(dword) {
    return [dword >>> 16, dword & 0xFFFF];
}

/**
 * UUID生成
 */
function generateUUID() {
    var d = new Date().getTime();
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
    return uuid;
}

/**
 * 替换字符串首尾空格
 * @param {string} str
 */
function trim(str) {
    // str：要替换的字符串
    // \s : 表示 space ，空格
    // +： 一个或多个
    // ^： 开始，^\s，以空格开始
    // $： 结束，\s$，以空格结束
    // |：或者
    // /g：global， 全局
    if (isNull(str)) {
        return "";
    } else {
        str = str.replace(/^\s+|\s+$/g, "");
        return str;
    }
}

/**
 * 替换所有空格
 * @param {*} str
 */
function trimAll(str) {
    if (isNull(str)) {
        return "";
    } else {
        return str.replace(/\s/g, "");
    }
}

/**
 * 对String字符串的扩展，判断当前字符串是否以str开始
 * @param {*} str 
 */
String.prototype.startsWith = function(str) {
    return this.slice(0, str.length) == str;
};

/**
 * 对String字符串的扩展，判断当前字符串是否以str结束
 * @param {*} str 
 */
String.prototype.endsWith = function(str) {
    return this.slice(-str.length) == str;
};

/**
 * 对String字符串的扩展，判断当前字符串是否以str开始，忽略大小写
 * @param {*} str 
 */
String.prototype.startsWithIgnoreCase = function(str) {
    return this.slice(0, str.length).toLowerCase() == str.toLowerCase();
};

/**
 * 对String字符串的扩展，判断当前字符串是否以str结束，忽略大小写
 * @param {*} str 
 */
String.prototype.endsWithIgnoreCase = function(str) {
    return this.slice(-str.length).toLowerCase() == str.toLowerCase();
};

/**
 * js获取url中的参数值
 * @param {*} name 参数名称
 */
function getUrlParam(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        // decodeURIComponent() 对编码后的 URI 进行解码
        return decodeURIComponent(r[2]);
    }
    return null;
}

/**
 * 角度转方向
 * @param {number} angle 角度
 */
function angle2aspect(angle) {
    if (angle == 0 || angle == 360) {
        return "正东";
    }
    if (angle == 45) {
        return "东北";
    }
    if (angle == 90) {
        return "正北";
    }
    if (angle == 135) {
        return "西北";
    }
    if (angle == 180) {
        return "正西";
    }
    if (angle == 225) {
        return "西南";
    }
    if (angle == 270) {
        return "正南";
    }
    if (angle == 315) {
        return "东南";
    }
    if (angle > 0 && angle < 90) {
        return "北偏东" + (90 - angle) + "度";
    }
    if (angle > 90 && angle < 180) {
        return "北偏西" + (angle - 90) + "度";
    }
    if (angle > 180 && angle < 270) {
        return "南偏西" + (270 - angle) + "度";
    }
    if (angle > 270 && angle < 360) {
        return "南偏东" + (angle - 270) + "度";
    }
    if (isNull(angle)) {
        return "数据异常"
    }
    return "角度超出范围";
}

/**
 * 比较任意两个对象是否相等
 * @param {*} x 
 * @param {*} y 
 */
function equals(x, y) {
    // If both x and y are null or undefined and exactly the same 
    if (x === y) {
        return true;
    }

    // If they are not strictly equal, they both need to be Objects 
    if (!(x instanceof Object) || !(y instanceof Object)) {
        return false;
    }

    //They must have the exact same prototype chain,the closest we can do is
    //test the constructor. 
    if (x.constructor !== y.constructor) {
        return false;
    }

    for (var p in x) {
        //Inherited properties were tested using x.constructor === y.constructor
        if (x.hasOwnProperty(p)) {
            // Allows comparing x[ p ] and y[ p ] when set to undefined 
            if (!y.hasOwnProperty(p)) {
                return false;
            }

            // If they have the same strict value or identity then they are equal 
            if (x[p] === y[p]) {
                continue;
            }

            // Numbers, Strings, Functions, Booleans must be strictly equal 
            if (typeof(x[p]) !== "object") {
                return false;
            }

            // Objects and Arrays must be tested recursively 
            if (!Object.equals(x[p], y[p])) {
                return false;
            }
        }
    }

    for (p in y) {
        // allows x[ p ] to be set to undefined 
        if (y.hasOwnProperty(p) && !x.hasOwnProperty(p)) {
            return false;
        }
    }
    return true;
}
/**
 * 对Array数组的扩展，根据元素值从数组中删除指定元素
 * eg:
 * var somearray = ["mon", "tue", "wed", "thur"]
 * somearray.removeByValue("tue");
 * ==> somearray will now have "mon", "wed", "thur"
 * @param {*} val 
 */
Array.prototype.removeByValue = function(val) {
    for (var i = 0, len = this.length; i < len; i++) {
        if (equals(this[i], val)) {
            this.splice(i, 1);
            break;
        }
    }
};

/**
 * 对Array数组的扩展，数组中是否包含指定元素
 * eg:
 * var somearray = ["mon", "tue", "wed", "thur"]
 * somearray.isContain("tue");
 * ==> true
 * @param {*} val 
 */
Array.prototype.isContain = function(val) {
    for (var i = 0, len = this.length; i < len; i++) {
        // if (JSON.stringify(this[i]) == JSON.stringify(val)) { //将对象转换成字符串比较
        if (equals(this[i], val)) {
            return true;
        }
    }
    return false;
};

/** 
 * 对Date的扩展，将 Date 转化为指定格式的String
 * 月(M)、日(d)、12小时(h)、24小时(H)、分(m)、秒(s)、周(E)、季度(q)可以用 1-2 个占位符
 * 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 * eg:
 * (new Date()).format("yyyy-MM-dd hh:mm:ss.S")==> 2006-07-02 08:09:04.423      
 * (new Date()).format("yyyy-MM-dd E HH:mm:ss") ==> 2009-03-10 二 20:09:04      
 * (new Date()).format("yyyy-MM-dd EE hh:mm:ss") ==> 2009-03-10 周二 08:09:04      
 * (new Date()).format("yyyy-MM-dd EEE hh:mm:ss") ==> 2009-03-10 星期二 08:09:04      
 * (new Date()).format("yyyy-M-d h:m:s.S") ==> 2006-7-2 8:9:4.18      
 */
Date.prototype.format = function(fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份         
        "d+": this.getDate(), //日         
        "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时         
        "H+": this.getHours(), //小时         
        "m+": this.getMinutes(), //分         
        "s+": this.getSeconds(), //秒         
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度         
        "S": this.getMilliseconds() //毫秒         
    };
    var week = {
        "0": "\u65e5",
        "1": "\u4e00",
        "2": "\u4e8c",
        "3": "\u4e09",
        "4": "\u56db",
        "5": "\u4e94",
        "6": "\u516d"
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    if (/(E+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "\u661f\u671f" : "\u5468") : "") + week[this.getDay() + ""]);
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
};