(function ($) {

    var methods = {
        init: function (options, function_filter_call_back) {
            if (options) {
                if (options.dialog) $.extend(methods.settings.dialog, options.dialog);
            }
            if (function_filter_call_back) {
                methods.func_filter_callback = function_filter_call_back;
            }

            this.after(
                '<ul id="ui-tableFilter-menu" style="display: none; position: absolute; list-style: none; margin: 0; padding: 6px 8px;" class="ui-corner-all ui-widget-content ui-widget">' +
                    '<li><button id="ui-tableFilter-menu-filter">Filter</button></li>' +
                    '<li><button id="ui-tableFilter-menu-unFilter">Un-Filter</button></li>' +
                    '<li><button id="ui-tableFilter-menu-sortAsc">Sort Asc</button></li>' +
                    '<li><button id="ui-tableFilter-menu-sortDesc">Sort Desc</button></li>' +
                    '</ul>' +
                    '<div id="ui-tableFilter-dialog" style="display: none;"></div>' +
                    '<style>.ui-tableFilter-hidden { display: none; } .ui-widget{font-size:12px} .ui-dialog .ui-dialog-titlebar-close span { display: block; margin: -8px; }</style>'
            );

            $("#ui-tableFilter-menu").bind('click.tableFilter', function () {
                return false;
            });
            $("#ui-tableFilter-menu button")
                .css('width', '9em')
                .click(function () {
                    var t = $(this);
                    $("#ui-tableFilter-menu").css('display', 'none');
                    methods.evaluate(t.find('span.ui-button-text').html());
                })
                .first().button({ icons: { primary: 'ui-icon-search'} });

            $("#ui-tableFilter-menu button#ui-tableFilter-menu-unFilter").button({ icons: { primary: 'ui-icon-cancel'} }).addClass('ui-state-active').attr('disabled', 'disabled');
            $("#ui-tableFilter-menu button#ui-tableFilter-menu-sortAsc").button({ icons: { primary: 'ui-icon-circle-triangle-n'} });
            $("#ui-tableFilter-menu button#ui-tableFilter-menu-sortDesc").button({ icons: { primary: 'ui-icon-circle-triangle-s'} });

            $(window)
                .delegate('#ui-tableFilter-checkAll', 'click.tableFilter', function () {
                    var t = $(this);
                    t.siblings('div').find("input[type='checkbox']").attr('checked', t.is(":checked"));
                    return;
                })
                .bind('resize.tableFilter', methods.reposition)
                .bind('click.tableFilter', methods.defaultBrowserMenu);

            return this.each(function () {
                methods.instances.push(this);
                $(this).find('th').bind('click.tableFilter', methods.leftClick);
            });
        },
        instances: [],
        func_filter_callback: function (a, b) {
        },
        destroy: function () {
            this.each(function () {
                var indexOf = $.inArray(this, methods.instances);
                methods.instances.splice(indexOf, 1);
                $(this).find('th').unbind('.tableFilter');
            });

            if (methods.instances.length === 0) {
                $("#ui-tableFilter-menu").unbind('.tableFilter');
                $("#ui-tableFilter-dialog").dialog('destroy');
                $(window)
                    .undelegate('#ui-tableFilter-checkAll', '.tableFilter')
                    .unbind('.tableFilter');
                $("#ui-tableFilter-menu, #ui-tableFilter-dialog").remove();
            }
            return this;
        },
        evaluate: function (s) {
            if (s == 'Filter') methods.filter();
            else if (s == 'Un-Filter') methods.unFilter();
            else methods.sort((s == 'Sort Asc'));
        },
        unFilter: function () {
            var th = methods.myColumn;
            var table = th.closest("table");
            table.find("tr.ui-tableFilter-hidden").removeClass('ui-tableFilter-hidden');
            methods.func_filter_callback(table, methods.findNotFilteredRows());
            table.find("th").removeClass('ui-tableFilter-filtered');
        },
        sort: function (asc) {
            var areTheseDates = methods.myColumn.hasClass('ui-tableFilter-date');
            var areInner = methods.myColumn.hasClass('ui-tableFilter-inner');//add inner html support
            var areNumber = methods.myColumn.hasClass('ui-tableFilter-number');//add number sort support
            var attr_name = methods.myColumn.attr("sort_attr_name");
            var areInnerAttr = methods.myColumn.hasClass("ui-tableFilter-innerAttr") && attr_name != undefined;
            methods.createDistinctList();

            if (areTheseDates) methods.convertDistinctListToDates();
            if (areInner) methods.covertInnerHtml();
            if (areInnerAttr) methods.covertInnerAttr(attr_name);
            if (areNumber) {
                methods.distinctList.sort(function (a, b) {
                    return Number(a) - Number(b)
                });
            } else {
                methods.distinctList.sort()
            }
            if (!asc) methods.distinctList.reverse();

            var table = methods.myColumn.closest("table");
            for (var i = 0; i < methods.distinctList.length; i++) {
                table
                    .find("tr:not(.ui-tableFilter-hidden) td:nth-child(" + methods.getColumnNumber() + ")")
                    .each(function () {
                        var t = $(this);
                        var html = t.html(), tr = t.parent();
                        if (areTheseDates) {
                            var val = new Date(html).getTime();
                            if (val) html = val;
                        }
                        if (areInner) {
                            var val = $(html).html();
                            if (val) html = val;
                        }
                        if (areInnerAttr) {
                            var val = $(html).attr(attr_name);
                            if (val) html = val;
                        }
                        if (methods.distinctList[i] == html) table.append(tr);
                    });
            }
        },
        createDistinctList: function () {
            methods.distinctList = [];
            methods.myColumn
                .closest("table")
                .find("tr:not(.ui-tableFilter-hidden) td:nth-child(" + methods.getColumnNumber() + ")")
                .each(function () {
                    var t = $(this).html();
                    if (jQuery.inArray(t, methods.distinctList) == -1) methods.distinctList.push(t);
                });
        },
        convertDistinctListToDates: function () {
            for (var i = 0; i < methods.distinctList.length; i++) {
                var val = new Date(methods.distinctList[i]).getTime();
                if (val) methods.distinctList[i] = val;
            }
        },
        covertInnerHtml: function () {    //new Add
            for (var i = 0; i < methods.distinctList.length; i++) {
                var val = $(methods.distinctList[i]).html();
                if (val) methods.distinctList[i] = val;
            }
        },
        covertInnerAttr: function (attr_name) {    //new Add
            for (var i = 0; i < methods.distinctList.length; i++) {
                var val = $(methods.distinctList[i]).attr(attr_name);
                if (val) methods.distinctList[i] = val;
            }
        },
        findNotFilteredRows: function () {
            return methods.myColumn
                .closest("table")
                .find("tbody tr:not(.ui-tableFilter-hidden)")
        },
        filter: function () {
            var dialogHTML = '', dialogButtons = {};

            methods.createDistinctList();

            if (methods.distinctList.length > 1) {
                methods.distinctList.sort()
                dialogHTML +=
                    '<input id="ui-tableFilter-checkAll" type="checkbox">' +
                        '<label for="ui-tableFilter-checkAll" style="color: #bbb; font-size: 0.9em;">' +
                        '(Select All)' +
                        '</label>' +
                        '<div>';
                for (i = 0; i < methods.distinctList.length; i++) {
                    dialogHTML +=
                        '<div style="padding-left: 20px;">' +
                            '<input type="checkbox" id="checks_' + i + '" />' +
                            '<label for="checks_' + i + '">' + methods.distinctList[i] + '</label>' +
                            '</div>';
                }
                dialogHTML += '</div>';
                dialogButtons = {
                    "Cancel": function () {
                        $(this).dialog('close');
                    },
                    "Filter": function () {
                        var table = methods.myColumn.closest("table");
                        var allInputsLength = $("#ui-tableFilter-dialog div div input").length;
                        var selectedList = [];
                        $("#ui-tableFilter-dialog div div input:checked").each(function () {
                            selectedList.push($(this).siblings("label").html());
                        });
                        var selectedListLength = selectedList.length;
                        if (selectedListLength > 0 && allInputsLength != selectedListLength) {
                            table
                                .find("tr:not(.ui-tableFilter-hidden) td:nth-child(" + methods.getColumnNumber() + ")")
                                .each(function () {
                                    var t = $(this);
                                    var text = t.html();
                                    for (var i = 0; i < selectedListLength; i++) {
                                        if (text == selectedList[i]) i = selectedListLength;
                                        else if (i == selectedListLength - 1) t.parent().addClass('ui-tableFilter-hidden');
                                    }
                                });
                            methods.func_filter_callback(table, methods.findNotFilteredRows());
                            table.find("th").addClass("ui-tableFilter-filtered");
                            $(this).dialog('close');

                        } else {
                            if (allInputsLength == selectedListLength) {
                                $(this).dialog('close');
                            } else {
                                var t = $(methods.anError('Please check at least one option.')).prependTo($(this));
                                setTimeout(function () {
                                    t.fadeOut().remove();
                                }, 2000);
                            }
                        }
                    }
                };
            } else {
                dialogHTML = methods.anError('Not enough distinct values in this column.');
                dialogButtons = {
                    "Ok": function () {
                        $(this).dialog('close');
                    }
                };
            }
            $("#ui-tableFilter-dialog").html(dialogHTML).dialog($.extend(methods.settings.dialog, { autoOpen: true, buttons: dialogButtons }));
        },
        reposition: function () {
        },
        leftClick: function (e) {
            var btnUnFilter = $("#ui-tableFilter-menu-unFilter");
            methods.myColumn = $(e.target);
            if (methods.myColumn.hasClass('ui-tableFilter-filtered')) {
                btnUnFilter.removeClass('ui-state-active').removeAttr('disabled');
                btnUnFilter.parent().show();
            }
            else {
                btnUnFilter.addClass('ui-state-active').attr('disabled', 'disabled');
                btnUnFilter.parent().hide();  //disabled button should not be seen
            }
            var btnFilter = $("#ui-tableFilter-menu-filter"); //add this to avoid be filter
            methods.myColumn = $(e.target);
            if (!methods.myColumn.hasClass('ui-tableFilter-no-filter')) {
                btnFilter.removeClass('ui-state-active').removeAttr('disabled');
                btnFilter.parent().show();
            }
            else {
                btnFilter.addClass('ui-state-active').attr('disabled', 'disabled');
                btnFilter.parent().hide();
            }
            $("#ui-tableFilter-menu").css({ 'display': '', 'left': e.pageX, 'top': e.pageY });

            return false;
        },
        defaultBrowserMenu: function (e) {
            $("#ui-tableFilter-menu").css({ 'display': 'none' });
            return true;
        },
        getColumnNumber: function () {
            return methods.myColumn.parent().children("th").index(methods.myColumn) + 1;
        },
        myColumn: [], distinctList: [],
        settings: {
            dialog: {
                modal: true, width: 400, height: 400, title: 'Filter'
            }
        },
        anError: function (s) {
            return '<div class="ui-widget">' +
                '<div style="padding: 0pt 0.7em;" class="ui-state-error ui-corner-all">' +
                '<p>' +
                '<span style="float: left; margin-right: 0.3em;" class="ui-icon ui-icon-alert"></span>' +
                '<strong>Alert:</strong> ' + s +
                '</p>' +
                '</div>' +
                '</div>'
        }
    };

    jQuery.fn.tableFilter = function (method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.tableFilter');
        }
    };

})(jQuery);