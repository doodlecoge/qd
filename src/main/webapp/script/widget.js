var WIDGET_PREFIX = "widgetid-";

function addNewWidget(type, dashboardId, name) {
    var newWidgetId = -1;
    $.ajaxSetup({async:false});
    $.post("add_widget", {dashboardId : dashboardId || 1, type : type, name: name || "New Widget"},
        function(apiResult) {
            if (apiResult["statusCode"] == 0) {
                newWidgetId = apiResult["ext"]["widgetId"];
            }
        },
        "json"
    );
    return newWidgetId;
}

function generateEditableWidget(widgetId, name) {
    return "<div class=\"widget movable removable editable closeconfirm\" id=\"" + WIDGET_PREFIX + widgetId + "\"><div class=\"widget-header\">\n" +
        "       <div class='widget-gradient'>"+
        "           <strong class=\"widget-title\">" + name + "</strong>\n" +
        "       </div>" +
        "    </div>\n" +
        "    <div class=\"widget-editbox\">\n" +
        "        <iframe id='setting" + widgetId + "' src=\"../widget/setting/" + widgetId + "\" style=\"width: 100%; overflow: hidden; border: 0px; height: auto\"\n" +
        "                scrolling=\"no\" scrollbar=\"no\"></iframe>\n" +
        "    </div>\n" +
        "    <div class=\"widget-content\">\n" +
        "        <iframe id='view" + widgetId + "' src=\"../widget/view/" + widgetId + "\" style=\"width: 100%; overflow: hidden; border: 0px; height: auto\"\n" +
        "                scrolling=\"no\" scrollbar=\"no\"></iframe>\n" +
        "    </div></div>";
}

function saveDashboardLayout(dbId) {
//    alert(dbId);
    var layout = getDashboardLayout();
//    alert(JSON.stringify(layout));
    $.ajaxSetup({async:false});
    layout = JSON.stringify(layout);
    $.post("save_layout", {dashboardId : dbId, layout: layout},
        function(data) {
        });
}

function removeWidget(widgetId) {
    var removed = false;
    $.ajaxSetup({async:false});
    $.post("delete_widget", { widgetId: widgetId}, function(apiResult) {
        if (apiResult["statusCode"] == 0)
            removed = true;
    }, "json");
    return removed;
}

function renameWidget(widgetId, newName) {
    $(".widget-title", $("#" + widgetId)).text(newName);
}

function getDashboardLayout() {
    var layout = [];
    $(".widget-place").each(function(index, element) {
        var widgetPlace = $(element);
        var column = [];
        widgetPlace.find(".widget").each(function(index, element) {
            column[index] = $(element).attr("id").substring(WIDGET_PREFIX.length);
        });
        layout[index] = column;
    });
    return layout;
}


function update() {

}