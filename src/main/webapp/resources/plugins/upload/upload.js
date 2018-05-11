/*
 * 自定义文件上传插件
 * @Author: chenlong 
 * @Date: 2018-03-27 09:08:40 
 * @Last Modified by: chenlong
 * @Last Modified time: 2018-03-27 10:51:33
 */

(function() {
    var options = {
        upload: false, //点击按钮是否直接上传，默认false
        fnBack: function(fileInput, fileName) {} //点击按钮直接上传时的回调函数
    };

    function dealInputFiles(upload, fnBack) {
        var inputs = document.querySelectorAll('.inputfile');
        Array.prototype.forEach.call(inputs, function(input) {
            var label = input.nextElementSibling,
                labelVal = label.innerHTML;

            input.addEventListener('change', function(e) {
                // get input files
                var fileName = '';
                if (this.files && this.files.length > 1) {
                    fileName = (this.getAttribute('data-multiple-caption') || '').replace('{count}', this.files.length);
                } else {
                    fileName = e.target.value.split('\\').pop();
                }
                if (fileName) {
                    label.querySelector('span').innerHTML = fileName;
                } else {
                    label.innerHTML = labelVal;
                }

                //点击按钮直接上传
                if (upload) {
                    fnBack(this, fileName, labelVal);
                }
            });

            // Firefox bug fix
            input.addEventListener('focus', function() { input.classList.add('has-focus'); });
            input.addEventListener('blur', function() { input.classList.remove('has-focus'); });
        });
    }
    var api = {
        config: function(opts) {
            if (!opts) {
                return options;
            }
            for (var key in opts) {
                options[key] = opts[key];
            }
            return this;
        },
        listen: function listen(elem) {
            if (typeof elem === 'string') {
                var elems = document.querySelectorAll(elem),
                    i = elems.length;
                while (i--) {
                    listen(elems[i]);
                }
                return;
            }
            //插件功能函数
            
            dealInputFiles(options.upload, options.fnBack);
            return this;
        }
    };
    this.customUpload = api;
})();