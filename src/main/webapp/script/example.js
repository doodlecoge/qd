// DOM ready!

$(function () {

    // Use the cookie plugin

    $.fn.EasyWidgets({

        behaviour: {
            useCookies: false
        },

        i18n: {
            closeTitle: "Delete the widget"
        },

        callbacks: {

            onAdd: function () {

//                Log('onAdd');
            },

            onEdit: function (link, widget) {
                var ifrm = widget.find(".widget-editbox iframe");
                if (ifrm.attr("src") == "") {
                    ifrm.attr("src", ifrm.attr('data-original'));
                }
                var h = ifrm.contents().find("form").height();
                if (h == null) {
                    h = 450;
                }
                ifrm.height(h);
            },

            onShow: function () {
            },

            onHide: function () {
            },

            onClose: function () {
                saveDashboardLayout(__DashboardId__);
            },

            onEnable: function () {
            },

            onExtend: function () {
            },

            onDisable: function () {
            },

            onDragStop: function () {
            },

            onCollapse: function () {
            },

            onAddQuery: function () {
                return true;
            },

            onEditQuery: function () {
                return true;
            },

            onShowQuery: function () {
                return true;
            },

            onHideQuery: function () {
                return true;
            },

            onCloseQuery: function (link, widget) {
                return removeWidget(widget.attr('id').substring(WIDGET_PREFIX.length));
            },

            onCancelEdit: function (link, widget) {
            },

            onEnableQuery: function () {
                return true;
            },

            onExtendQuery: function () {
                return true;
            },

            onDisableQuery: function () {
                return true;
            },

            onCollapseQuery: function () {
                return true;
            },

            onCancelEditQuery: function () {
                return true;
            },

            onChangePositions: function () {
                saveDashboardLayout(__DashboardId__);
            },

            onRefreshPositions: function () {
            },

            onSaveEditQuery: function (link, widget) {
                var saved = false;
                var widgetId = widget.attr('id').substring(WIDGET_PREFIX.length);
                $.ajaxSetup({async: false});

                if ($("iframe", $(widget))[0].contentWindow.getSettings) {
                    var settings = $("iframe", $(widget))[0].contentWindow.getSettings();
                    $.post("../widget/setting/save", {
                        widgetId: widgetId,
                        name: settings.name,
                        setting: JSON.stringify(settings)
                    }, function (data) {
                        var response = eval("(" + data + ")");
                        if (response["statusCode"] == 0) {
                            saved = true;
                            renameWidget(widget.attr('id'), settings.name);
                            var ifrm = widget.find(".widget-content iframe");
                            ifrm[0].contentWindow.location.reload();
                        } else {
                            alert(response["statusMessage"]);
                        }
                    });
                } else {
                    var iFrame = $("iframe", $(widget))[0];
                    var form = $('#jsonForm', $(iFrame).contents());
                    var name = '';
                    form.ajaxSubmit({
                        dataType: 'json',
                        type: 'POST',// must be post here
                        beforeSubmit: function (formData, jqForm, options) {
                            var wid = {name: "widgetId", value: widgetId, type: "text"};
                            formData[formData.length] = wid;
                            $.each(formData, function (i, fd) {
                                if (fd['name'] == 'name') {
                                    name = fd['value'];
                                }
                            });
                        },
                        success: function (response) {
                            if (response["statusCode"] == 0) {
                                saved = true;
                                renameWidget(widget.attr('id'), name);
                                var ifrm = widget.find(".widget-content iframe");
                                ifrm[0].contentWindow.location.reload();
                            } else {
                                alert(response["statusMessage"]);
                            }
                        }
                    });
                }


                return saved;
            },

            onSaveEdit: function (link, widget) {
                var src = $("iframe", $(widget))[1].src;
                $("iframe", $(widget))[1].src = src;
            }
        }

    });


});

var callbackNum = 0;


function AddWidget(url, placeId) {
    $.get(url, function (html) {
        $.fn.AddEasyWidget(html, placeId);
    });
}
