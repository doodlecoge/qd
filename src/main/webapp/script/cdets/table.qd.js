/**
 * This js is used to create table more efficiently
 *
 */
/*var data = {
 login: "tomek",
 name:{
 first_name: "Thomas",
 last_name: "Mazur"}
 }
 var array = new Array();
 for (var i = 0;i<10;i++){
 array.push(data);
 }
 var loginFun = function(){
 return "<td>{login}</td>"
 }
 var last_name_Fun = function(){
 return "<td><span style='color:red'>{name.last_name}</span> {name.first_name}</td>"
 }
 $("#tables").renderTable(array,["login","name"],[loginFun,last_name_Fun
 ]);*/
;
(function ($) {
    $.fn.renderTable = function (data, keys, templateArray, contentFuncArray) {
        if (!(this.is("table") && data && keys && templateArray)) {
            return this;
        }
        contentFuncArray = contentFuncArray ? contentFuncArray : getDefaultContentFuncArray(keys.length);
        var template = createTemplate(templateArray);
        var cols = [];
        $.each(data, function (j) {
            var render_data = {};
            for (var i = 0, l = keys.length; i < l; i++)
                render_data[keys[i]] = contentFuncArray[i](data[j][keys[i]])
            cols.push(nano(template, render_data));
        });
        var content = cols.join("");
        this.html(content);
        return this;
    };
    function createTemplate(templateArray) {
        var tArray = [];
        $.each(templateArray, function (i, item) {
            tArray.push(templateArray[i]());
        });
        return ["<tr>", tArray.join(""), "</tr>"].join("")
    };

    function getDefaultContentFuncArray(length) {
        var arr = [];
        var func = function (value) {
            return value
        };
        while (length > 0) {
            arr.push(func);
            length--;
        }
        return arr;
    };

    function nano(template, data) {
        return template.replace(/\{([\w\.]*)\}/g, function (str, key) {
            var keys = key.split("."), v = data[keys.shift()];
            for (var i = 0, l = keys.length; i < l; i++) v = v[keys[i]];
            return (typeof v !== "undefined" && v !== null) ? v : "";
        });
    };

    $.fn.renderTableHeader = function (headers) {
        var tr = $("<tr></tr>")
        $.each(headers, function (j, element) {
            tr.append($("<th></th>").html(element));
        })
        this.append($("<thead></thead>").append(tr));
    };
    var getTotal = function(data,keys){
        var total = {};
        $.each(data,function(i){
            $.each(keys,function(j){
                var value = data[i][keys[j]] ==undefined?0:data[i][keys[j]];
                if(total[keys[j]]){
                    total[keys[j]]+=value;;
                }else{
                    total[keys[j]]=value;
                }
            })
        });
        return total;
    };
    $.fn.renderTableFooter = function (data, keys, templateArray, contentFuncArray) {
        if (!(this.is("table") && data && keys && templateArray)) {
            return this;
        }
        var total = getTotal(data,keys);
        contentFuncArray = contentFuncArray ? contentFuncArray : getDefaultContentFuncArray(keys.length);
        var template = createTemplate(templateArray);
        var render_data = {};
        for (var i = 0, l = keys.length; i < l; i++)
            render_data[keys[i]] = contentFuncArray[i](total[keys[i]])
        var tfooter = nano(template, render_data);
        this.append($("<tfoot></tfoot>").html(tfooter));
        return this;
    };


})(jQuery);

