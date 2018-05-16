/*
 * 各浏览器灰度滤镜兼容性解决方案
 * @Author: chenlong 
 * @Date: 2018-03-05 10:18:30 
 * @Last Modified by: chenlong
 * @Last Modified time: 2018-03-26 15:37:10
 */

/**
 * 添加灰度滤镜
 * @param {*} obj 要添加滤镜的对象
 * @return {boolean} 是否CSS滤镜
 */
function grayscale(obj) {
    if (-[1, ]) {
        // 不是IE8及以下版本IE浏览器
        var browser = IEVersion();
        if (browser == -1) {
            // Webkit内核的浏览器：CSS3 滤镜：
            // 格式，filer: grayscale(效果范围)
            // 效果范围，取值范围为0-1或0-100%；0表示无效果，1或100%表示最大效果
            obj.css("filter", 'grayscale(100%)');
            return true;
        }
        if (browser > 9) {
            // IE10+
            gray(obj);
            return false;
        }
    }
    // IE5.5~9：IE滤镜
    obj.css("filter", 'gray');
    return true;
}

/**
 * 移除灰度滤镜
 * @param {*} obj 要移除滤镜的对象
 * @param {*} cssfilter 是否CSS滤镜
 * @param {*} imgUrl 原始图像路径
 */
function removeGrayscale(obj, cssfilter, imgUrl) {
    if (cssfilter) {
        obj.css("filter", 'grayscale(0)');
        return;
    }
    if (obj instanceof jQuery) {
        obj = obj[0];
    }
    obj.src = imgUrl;
}

/**
 * IE浏览器版本判断
 */
function IEVersion() {
    var userAgent = navigator.userAgent; // 取得浏览器的userAgent字符串
    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; // 判断是否IE<11浏览器
    var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1;
    var isEdge = userAgent.indexOf("Edge") > -1 && !isIE; // 判断是否IE的Edge浏览器
    if (isIE) {
        var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
        reIE.test(userAgent);
        var fIEVersion = parseFloat(RegExp["$1"]);
        if (fIEVersion == 7) {
            return 7;
        } else if (fIEVersion == 8) {
            return 8;
        } else if (fIEVersion == 9) {
            return 9;
        } else if (fIEVersion == 10) {
            return 10;
        } else {
            return 6; // IE版本<=6
        }
    } else if (isIE11) {
        return 11; // IE11
    } else if (isEdge) {
        return 12; // edge
    } else {
        return -1; // 不是ie浏览器
    }
}

/**
 * 使用canvas画布添加灰度滤镜
 * @param {*} imgObj 要添加滤镜的对象
 */
function gray(imgObj) {
    var jqObj = imgObj instanceof jQuery;
    if (jqObj) {
        imgObj = imgObj[0];
    }
    var canvasImageSource =
        imgObj instanceof HTMLImageElement ||
        imgObj instanceof HTMLVideoElement ||
        imgObj instanceof HTMLCanvasElement;
    if (!canvasImageSource) {
        throw "绘制到上下文的元素不是允许的 canvas 图像源！";
    }

    var canvas = document.createElement('canvas');
    var canvasContext = canvas.getContext('2d');

    var imgW = imgObj.width;
    var imgH = imgObj.height;
    canvas.width = imgW;
    canvas.height = imgH;

    canvasContext.drawImage(imgObj, 0, 0);
    var imgPixels = canvasContext.getImageData(0, 0, imgW, imgH);

    for (var y = 0; y < imgPixels.height; y++) {
        for (var x = 0; x < imgPixels.width; x++) {
            var i = (y * 4) * imgPixels.width + x * 4;
            var avg = (imgPixels.data[i] + imgPixels.data[i + 1] + imgPixels.data[i + 2]) / 3;
            imgPixels.data[i] = avg;
            imgPixels.data[i + 1] = avg;
            imgPixels.data[i + 2] = avg;
        }
    }
    canvasContext.putImageData(imgPixels, 0, 0, 0, 0, imgPixels.width, imgPixels.height);
    imgObj.src = canvas.toDataURL();
}