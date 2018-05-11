/*
 * tab插件实现方法 
 * @Author: chenlong 
 * @Date: 2018-03-26 15:44:10 
 * @Last Modified by:   chenlong 
 * @Last Modified time: 2018-03-26 15:44:10 
 */

$(function() {
    /*tab标签切换*/
    function tabs(tabTit, on, tabCon) {
        $(tabCon).each(function() {
            $(this).children().eq(0).show();
        });
        $(tabTit).each(function() {
            $(this).children().eq(0).addClass(on);
        });
        $(tabTit).children().click(function() {
            $(this).addClass(on).siblings().removeClass(on);
            var index = $(tabTit).children().index(this);
            $(tabCon).children().eq(index).show().siblings().hide();
        });
    }
    tabs(".tab-title", "on", ".tab-con");
});