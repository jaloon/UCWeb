/*
 * 常用数据校验 
 * @Author: chenlong 
 * @Date: 2017-11-22 17:25:25 
 * @Last Modified by: chenlong
 * @Last Modified time: 2018-04-26 15:15:53
 */

/******************************************************数值校验 begin*****************************************************/

/**
 * 非负整数（正整数 + 0）
 * @param {*} num
 */
function isNonnegativeInteger(num) {
    var reg = /^\d+$/;
    return reg.test(num);
}

/**
 * 正整数
 * @param {*} num
 */
function isPositiveInteger(num) {
    var reg = /^[1-9]*[1-9][0-9]*$/;
    return reg.test(num);
}

/**
 * 整数
 * @param {*} num
 */
function isInteger(num) {
    var reg = /^-?\d+$/;
    return reg.test(num);
}

/**
 * 浮点数
 * @param {*} num 
 */
function isFloat(num) {
    var reg = /^(-?\d+)(.\d+)?$/;
    return reg.test(num);
}
/**
 * 16进制字符串校验
 * @param {string} str 校验字符
 * @param {number} bits 位数
 */
function isHEX(str, bits) {
    var reg;
    if (bits == undefined || bits == null) {
        reg = /^(\d|[a-fA-F])+$/;
    } else {
        reg = new RegExp("^(\\d|[a-fA-F]){" + bits + "}$");
    }
    return reg.test(str);
}

/******************************************************数值校验 end*******************************************************/

/**
 * IP地址校验
 * 验证规则：分4段，每段0~255，中间以.分隔
 * @param {string} ip
 */
function isIP(ip) {
    var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
    return reg.test(ip);
}

/**
 * 端口号校验
 * 验证规则：纯数字，0~65535
 * @param {number} port 
 */
function isPort(port) {
    var parten = /^(\d)+$/g;
    if (parten.test(port) && parseInt(port) <= 65535 && parseInt(port) >= 0) {
        return true;
    } else {
        return false;
    }
}

/**
 * URL校验
 * @param {string} url 
 */
function isURL(url) {
    var reg = new RegExp("^(http|https|ftp)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|localhost|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");
    return reg.test(url);
}

/**
 * ftp路径校验
 * ftp路径格式： "ftp:" "//" [ user [ ":" password ] "@" ] host [ ":" port ][ "/" path [ ";type=" value ]]
 * @param {string} path 
 */
function isFtpPath(path) {
    var reg = /^ftp:\/\/([a-zA-Z0-9\.\-]+(:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(:[0-9]+)*(\/[a-zA-Z0-9\.\,\?\'\\+&amp;%\$#\=~_\-]+)*(?<!\/)$/;
    return reg.test(path);
}

/**
 * ftp文件目录验证
 * 验证规则：若为用户根目录，为空字符串；若为子目录，以“/”开头，不以“/”结尾，并且不含“//”，例如：/user。
 * @param {string} dir 
 */
function isFtpDir(dir) {
    var len = dir.length;
    if (len == 0) {
        return true;
    } else {
        var reg = new RegExp("(?=(^/.+(?<!/)$))(^(?!.*//))");
        return reg.test(dir);
    }
}

/**
 * 经度校验
 * 验证规则：经度范围：-180.0000~180.0000
 * @param {number} longitude 
 */
function isLng(longitude) {
    var reg = /^-?((0|1?[0-7]?[0-9]?)(([.][0-9]+)?)|180(([.][0]+)?))$/;
    return reg.test(longitude);
}

/**
 * 纬度校验
 * 验证规则：纬度范围：-90.0000~90.0000
 * @param {number} latitude 
 */
function isLat(latitude) {
    var reg = /^-?((0|[1-8]?[0-9]?)(([.][0-9]+)?)|90(([.][0]+)?))$/;
    return reg.test(latitude);
}

/**
 * 车牌号校验
 * 验证规则：
 * 1.常规车牌号：仅允许以汉字开头，后面可录入六个字符，由大写英文字母和阿拉伯数字组成。如：粤B12345；
 * 2.武警车牌：允许前两位为大写英文字母，后面可录入七个字符，由大写英文字母和阿拉伯数字组成，其中第三位可录汉字也可录大写英文字母及阿拉伯数字，如：WJ01警0081、WJ0112345。
 * 3.最后一个为汉字的车牌：允许以汉字开头，后面可录入六个字符，前五位字符，由大写英文字母和阿拉伯数字组成，而最后一个字符为汉字，汉字包括“挂”、“学”、“警”、“军”、“港”、“澳”。如：粤Z1234港。
 * 4.新军车牌：以两位为大写英文字母开头，后面以5位阿拉伯数字组成。如：BA12345。
 * 5.黑龙江车牌存在08或38开头的情况
 * @param {*} carNo 
 */
function isCarNo(carNo) {
    var reg = /(^[\u4E00-\u9FA5]{1}[A-Z0-9]{6}$)|(^[A-Z]{2}[A-Z0-9]{2}[A-Z0-9\u4E00-\u9FA5]{1}[A-Z0-9]{4}$)|(^[\u4E00-\u9FA5]{1}[A-Z0-9]{5}[挂学警军港澳]{1}$)|(^[A-Z]{2}[0-9]{5}$)|(^(08|38){1}[A-Z0-9]{4}[A-Z0-9挂学警军港澳]{1}$)/;
    return reg.test(carNo);
}

/** 
 * 固定电话校验
 * 验证规则：区号+号码，区号以0开头，3位或4位；号码由7位或8位数字组成；区号与号码之间可以无连接符，也可以“-”连接
 * @param {*} tel 
 */
function checkTel(tel) {
    var reg = /^(([0\+]\d{2,3}-)?(0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/;
    return reg.test(tel);
}

/**
 * 手机号校验
 * 验证规则：共11位数字，首位1，第二位3、4、5、7、8等
 * @param {*} mobil 
 */
function checkMobil(mobil) {
    var reg = /^1[34578]\d{9}$/;
    return reg.test(mobil);
}

/**
 * 电话号码校验（不区分固定电话和手机）
 * @param {*} phone 
 */
function isPhone(phone) {
    if (checkTel(phone) || checkMobil(phone)) {
        return true;
    }
    return false;
}

/**
 * 账号校验
 * 验证规则：字母、数字、下划线组成，字母开头，2-16位。
 * @param {*} account 
 */
function isAccount(account) {
    var reg = /^[a-zA-z]\w{1,15}$/;
    return reg.test(account);
}

/**
 * 身份证号码校验
 * 验证规则：前6位为地区代码（前两位省级行政区编码），15位身份证第7~12位为出生年月日,13~14位为出生编号，第15位为性别码（奇数男，偶数女）；
 * 18位身份证第7~14位为出生年月日，15~16位为出生编号，第17位为性别码（奇数男，偶数女），第18位为校验码（为0123456789X其中之一）。
 * @param {*} idCard 
 */
function isIdCard(idCard) {
    var checkFlag = new clsIDCard(idCard);
    if (!checkFlag.IsValid()) {
        return false;
    }
    return true;
}

/******************************************* code for checking idcard begin... ******************************************/
// 构造函数，变量为15位或者18位的身份证号码
function clsIDCard(CardNo) {
    this.Valid = false;
    this.ID15 = "";
    this.ID18 = "";
    this.Local = "";
    if (CardNo != null) this.SetCardNo(CardNo);
}

// 设置身份证号码，15位或者18位
clsIDCard.prototype.SetCardNo = function(CardNo) {
    this.ID15 = "";
    this.ID18 = "";
    this.Local = "";
    CardNo = CardNo.replace(" ", "");
    var strCardNo;
    if (CardNo.length == 18) {
        pattern = /^\d{17}(\d|x|X)$/;
        if (pattern.exec(CardNo) == null) return;
        strCardNo = CardNo.toUpperCase();
    } else {
        pattern = /^\d{15}$/;
        if (pattern.exec(CardNo) == null) return;
        strCardNo = CardNo.substr(0, 6) + "19" + CardNo.substr(6, 9);
        strCardNo += this.GetVCode(strCardNo);
    }
    this.Valid = this.CheckValid(strCardNo);
};

// 校验身份证有效性
clsIDCard.prototype.IsValid = function() {
    return this.Valid;
};

// 返回生日字符串，格式如下，1981-10-10
clsIDCard.prototype.GetBirthDate = function() {
    var BirthDate = "";
    if (this.Valid)
        BirthDate =
        this.GetBirthYear() +
        "-" +
        this.GetBirthMonth() +
        "-" +
        this.GetBirthDay();
    return BirthDate;
};

// 返回生日中的年，格式如下，1981
clsIDCard.prototype.GetBirthYear = function() {
    var BirthYear = "";
    if (this.Valid) BirthYear = this.ID18.substr(6, 4);
    return BirthYear;
};

// 返回生日中的月，格式如下，10
clsIDCard.prototype.GetBirthMonth = function() {
    var BirthMonth = "";
    if (this.Valid) BirthMonth = this.ID18.substr(10, 2);
    if (BirthMonth.charAt(0) == "0") BirthMonth = BirthMonth.charAt(1);
    return BirthMonth;
};

// 返回生日中的日，格式如下，10
clsIDCard.prototype.GetBirthDay = function() {
    var BirthDay = "";
    if (this.Valid) BirthDay = this.ID18.substr(12, 2);
    return BirthDay;
};

// 返回性别，1：男，0：女
clsIDCard.prototype.GetSex = function() {
    var Sex = "";
    if (this.Valid) Sex = this.ID18.charAt(16) % 2;
    return Sex;
};

// 返回15位身份证号码
clsIDCard.prototype.Get15 = function() {
    var ID15 = "";
    if (this.Valid) ID15 = this.ID15;
    return ID15;
};

// 返回18位身份证号码
clsIDCard.prototype.Get18 = function() {
    var ID18 = "";
    if (this.Valid) ID18 = this.ID18;
    return ID18;
};

// 返回所在省，例如：上海市、浙江省
clsIDCard.prototype.GetLocal = function() {
    var Local = "";
    if (this.Valid) Local = this.Local;
    return Local;
};

clsIDCard.prototype.GetVCode = function(CardNo17) {
    var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1];
    var Ai = ["1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"];
    var cardNoSum = 0;
    for (var i = 0; i < CardNo17.length; i++)
        cardNoSum += CardNo17.charAt(i) * Wi[i];
    var seq = cardNoSum % 11;
    return Ai[seq];
};

clsIDCard.prototype.CheckValid = function(CardNo18) {
    if (this.GetVCode(CardNo18.substr(0, 17)) != CardNo18.charAt(17))
        return false;
    if (!this.IsDate(CardNo18.substr(6, 8))) return false;
    var aCity = {
        11: "北京",
        12: "天津",
        13: "河北",
        14: "山西",
        15: "内蒙古",
        21: "辽宁",
        22: "吉林",
        23: "黑龙江",
        31: "上海",
        32: "江苏",
        33: "浙江",
        34: "安徽",
        35: "福建",
        36: "江西",
        37: "山东",
        41: "河南",
        42: "湖北",
        43: "湖南",
        44: "广东",
        45: "广西",
        46: "海南",
        50: "重庆",
        51: "四川",
        52: "贵州",
        53: "云南",
        54: "西藏",
        61: "陕西",
        62: "甘肃",
        63: "青海",
        64: "宁夏",
        65: "新疆",
        71: "台湾",
        81: "香港",
        82: "澳门",
        91: "国外"
    };
    if (aCity[parseInt(CardNo18.substr(0, 2))] == null) return false;
    this.ID18 = CardNo18;
    this.ID15 = CardNo18.substr(0, 6) + CardNo18.substr(8, 9);
    this.Local = aCity[parseInt(CardNo18.substr(0, 2))];
    return true;
};

clsIDCard.prototype.IsDate = function(strDate) {
    var r = strDate.match(/^(\d{1,4})(\d{1,2})(\d{1,2})$/);
    if (r == null) return false;
    var d = new Date(r[1], r[2] - 1, r[3]);
    return (
        d.getFullYear() == r[1] && d.getMonth() + 1 == r[2] && d.getDate() == r[3]
    );
};
/******************************************* code for checking idcard end... ******************************************/