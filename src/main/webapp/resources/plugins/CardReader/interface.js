var READER_TYPE = {
    _reader_type_contactLess: 1,
    _reader_type_contact: 2,
    _reader_type_keyBoard: 3,
};

var FUNCIDS = {
    _fid_adaptReader: 0,
    _fid_initialcom: 1, // 初始化通讯口
    _fid_exit: 2, // 关闭端口
    _fid_beep: 3, // 蜂鸣
    _fid_GetDevSN: 4, // 获取读卡器序列号

    _fid_setBright: 7, // 设置 LCD 背光点亮或熄灭

    _fid_findCard: 31, // 寻卡
    _fid_findCardStr: 32, // 寻卡
    _fid_findCardHex: 33, // 寻卡

    _fid_halt: 45, // 中止对该卡操作
};


var SmartReader = {
    OBJ: function() {
        var reader = {};
        var SocketOpen = false;
        var socket = null;
        var target = null;
        var adaptedID = READER_TYPE._reader_type_contactLess;

        reader.onResult = function(func) {
            target.addEvent("Result", func);
        };

        var WSonOpen = function() {
            var pseudoName = "webReader";
            socket.send('0' + pseudoName);
            SocketOpen = true;
            reader.adaptReader(adaptedID);
        };
        var WSonMessage = function(msg) {
            var str = "";
            str = msg.data;

            var id = str.substr(0, 1);
            var separator = str.indexOf("|");
            var funcid = "";
            var arg1 = "";

            if (separator != -1) {
                funcid = str.substr(1, separator - 1);
                arg1 = str.substr(separator + 1);
            } else
                funcid = str.substr(1);

            //alert("id:" + id + ",funcid:" + funcid +",arg1:" + arg1);

            var resultData = {
                type: "Result",
                FunctionID: parseInt(funcid),
                RePara_Int: parseInt(_getCmdResult(arg1)),
                RePara_Str: _getResultPara(arg1)
            };


            if (target != null)
                target.fireEvent(resultData);
        };
        var WSonClose = function() {
            SocketOpen = false;
        };
        var WSonError = function() {
            alert("Card Reader Server not running");
        };
        reader.createSocket = function() {
            try {
                if ("WebSocket" in window) {
                    socket = new WebSocket("mws://localhost:81/webReaderServer");
                } else if ("MozWebSocket" in window) {
                    socket = new MozWebSocket("mws://localhost:81/webReaderServer");
                } else {
                    alert("None");
                    return false;
                }
                socket.onopen = WSonOpen;
                socket.onmessage = WSonMessage;
                socket.onclose = WSonClose;
                socket.onerror = WSonError;
                target = new EventTarget();
                return true;
            } catch (ex) {
                return false;
            }
        };
        reader.Disconnect = function() {
            if (socket != null)
                socket.close();
        };

        reader.getOBJ = function(id) {
            adaptedID = id;
            return reader;
        };

        /**
         * 发送命令
         * @param {string} FuncName 方法名称
         * @param {number} FunctionID 方法ID
         * @param {*} ParamStr 参数
         */
        var SendCmd = function(FuncName, FunctionID, ParamStr) {
            var entryCmd;
            if (true == SocketOpen) {
                entryCmd = '1' + FunctionID + "|" + FuncName + "|" + ParamStr;
                socket.send(entryCmd);
            }
        };

        /**
         * FUNCIDS._fid_adaptReader
         * @param {*} readerType
         */
        reader.adaptReader = function(readerType) {
            SendCmd("adaptReader", FUNCIDS._fid_adaptReader, readerType);
        };

        /**
         * 初始化通讯口 FUNCIDS._fid_initialcom
         * @param {number} portType 取值为0～19时，表示串口1～20；取值为100时，表示USB口通讯，此时波特率无效。
         * @param {number} baud 通讯波特率（ 9600～115200）
         */
        reader.initialcom = function(portType, baud) {
            SendCmd("initialcom", FUNCIDS._fid_initialcom, portType + "," + baud);
        };

        /**
         * 关闭端口 FUNCIDS._fid_exit
         * @param {number} hdev 通讯设备标识符
         */
        reader.exit = function(hdev) {
            SendCmd("exit", FUNCIDS._fid_exit, hdev);
        };

        /**
         * 蜂鸣 FUNCIDS._fid_beep
         * @param {number} hdev 通讯设备标识符
         * @param {number} delay 蜂鸣时间，单位是 10 毫秒
         */
        reader.beep = function(hdev, delay) {
            SendCmd("beep", FUNCIDS._fid_beep, hdev + "," + delay);
        };

        /**
         * 获取读卡器序列号 FUNCIDS._fid_GetDevSN
         * @param {number} hdev 通讯设备标识符
         */
        reader.GetDevSN = function(hdev) {
            SendCmd("GetDevSN", FUNCIDS._fid_GetDevSN, hdev);
        };

        /**
         * 设置 LCD 背光点亮或熄灭 FUNCIDS._fid_setBright
         * @param {number} hdev 通讯设备标识符
         * @param {number} fBright 点亮或熄灭的标志， 15-点亮， 0-熄灭
         */
        reader.lcd_setBright = function(hdev, fBright) {
            SendCmd("lcd_setBright", FUNCIDS._fid_setBright, hdev + "," + fBright);
        };

        /**
         * 寻卡，能返回在工作区域内某张卡的序列号 FUNCIDS._fid_findCard
         * @param {number} hdev 通讯设备标识符
         * @param {number} mode 寻卡模式（0 表示IDLE模式，一次只对一张卡操作；1 表示ALL模式，一次可对多张卡操作）
         */
        reader.findcard = function(hdev, mode) {
            SendCmd("findcard", FUNCIDS._fid_findCard, hdev + "," + mode);
        };

        /**
         * 寻卡，能返回在工作区域内某张卡的序列号(10进制形式字符串) FUNCIDS._fid_findCardStr
         * @param {number} hdev 通讯设备标识符
         * @param {number} mode 寻卡模式（0 表示IDLE模式，一次只对一张卡操作；1 表示ALL模式，一次可对多张卡操作）
         */
        reader.findcardStr = function(hdev, mode) {
            SendCmd("findcardStr", FUNCIDS._fid_findCardStr, hdev + "," + mode);
        };

        /**
         * 寻卡，能返回在工作区域内某张卡的序列号(16进制形式字符串) FUNCIDS._fid_findCardHex
         * @param {number} hdev 通讯设备标识符
         * @param {number} mode 寻卡模式（0 表示IDLE模式，一次只对一张卡操作；1 表示ALL模式，一次可对多张卡操作）
         */
        reader.findcardHex = function(hdev, mode) {
            SendCmd("findcardHex", FUNCIDS._fid_findCardHex, hdev + "," + mode);
        };

        /**
         * 中止对该卡操作 FUNCIDS._fid_halt（寻卡模式为0时，使用此方法后必须把卡移开感应区再进来才能寻得这张卡）
         * @param {number} hdev 通讯设备标识符
         */
        reader.halt = function(hdev) {
            SendCmd("halt", FUNCIDS._fid_halt, hdev);
        };

        return reader;
    }
};

function EventTarget() {
    this.handlers = {};
}

EventTarget.prototype = {
    constructor: EventTarget,
    addEvent: function(type, handler) {
        if (typeof this.handlers[type] == 'undefined') {
            this.handlers[type] = [];
        }
        this.handlers[type].push(handler);
    },
    fireEvent: function(event) {
        if (!event.target) {
            event.target = this;
        }
        if (this.handlers[event.type] instanceof Array) {
            var handlers = this.handlers[event.type];
            for (var i = 0; i < handlers.length; i++) {
                handlers[i](event);
            }
        }
    },
    removeEvent: function(type, handler) {
        if (this.handlers[type] instanceof Array) {
            var handlers = this.handlers[type];
            for (var i = 0; i < handlers.length; i++) {
                if (handlers[i] == handler) {
                    break;
                }
            }
            handlers.splice(i, 1);
        }
    }
};

function _getCmdResult(relPara) {
    var iRel;
    var separator = relPara.indexOf(",");
    if (separator != -1) {
        iRel = relPara.substr(0, separator);
    } else
        iRel = relPara.substr(0);

    return iRel;
}

function _getResultPara(relPara) {
    var szPara = "";
    var separator = relPara.indexOf(",");
    if (separator != -1) {
        szPara = relPara.substr(separator + 1);
    }
    return szPara;
}

try {
    var embed_reader = SmartReader.OBJ();
} catch (e) {}

if (!embed_reader.createSocket()) {}