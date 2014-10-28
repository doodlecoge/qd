/**
 * @Author huaiwang@cisex.com
 * @Date Sep/30/2013
 * Auto sizing menu bar, will fit into parent
 * with spare items hidden in collapsed drop down menu.
 */

(function ($) {

    var CARET = {h: 8, w: 12, r: 5};
    var StrRes = {
        'onClick': 'click'
    };



    function fitIntoParent() {
        this.children('.drop-handle').remove();

        var children = this.children('.menu-item').removeClass('sel');
        var start = this.data('start-index');
        var total = children.length;


        // visible children with sum should not exceed this value
        var widthLimit = this.width() * 0.9;
        var max = Math.max(total - start - 1, start);

        // show at least selected one
        var arr = [start];
        var sum = $(children.get(start)).addClass('sel').outerWidth(true);

        for (var i = 0; i < max; i++) {
            var left = start - i - 1;
            if (left >= 0) {
                sum += $(children.get(left)).outerWidth(true);
                if (sum > widthLimit) break;
                arr.push(left);
            }

            var right = start + i + 1;
            if (right < total) {
                sum += $(children.get(right)).outerWidth(true);
                if (sum > widthLimit) break;
                arr.push(right);
            }
        }

        children.hide();

        $.each(arr, function (i, val) {
            $(children.get(val)).show();
        });


        // if some menu items are hidden
        var hides = this.children('.menu-item:hidden:not(.drop-handle)').length;
        if (hides > 0) {
            this.append('<a href="javascript:;" class="menu-item right drop-handle"><i class="icon-caret-down"></i></a>');
        }

        // show drop item anyway if it is there
        this.children('.drop-handle').show();

        this.children('.menu-item').removeClass('left').removeClass('right');
        this.children('.menu-item:visible:first').addClass('left');
        this.children('.menu-item:visible:last').addClass('right');


        hideDropMenu.call(this);
    }

    function createDropMenu() {
        var hidedChildren = this.children(':hidden');

        var items = $.map(hidedChildren, function (item, idx) {
            item = $(item);
            var idx = item.index();
            var name = item.html();
            return {index: idx, name: name};
        });


        this.children('.drop-menu').remove();
        var dropMenu = $('<div class="drop-menu"></div>');
        this.append(dropMenu);

        // add hidden items to drop down menu
        $.each(items, function (i, item) {
            var a = $('<a href="javascript:;" class="drop-menu-item" idx="' + item.index + '">' + item.name + '</a>');
            dropMenu.append(a);
        });
    }

    function showDropMenu() {
        var dropMenu = this.children('.drop-menu');
        dropMenu.show();

        // set drop down menu position
        var handle = this.children('.drop-handle').addClass('sel');

        var handleOffset = handle.offset();
        var handleWidth = handle.width();
        var handleHeight = handle.outerHeight(true);
        var menuWidth = dropMenu.width();
        var menuHeight = dropMenu.outerHeight();

        var handleTop = handleOffset.top;
        var handleBottom = $(window).height() - handle.height() - handleTop;


        var left = handleOffset.left + handleWidth - menuWidth + CARET.r;
        var top = handleTop - menuHeight - CARET.h;

        if (handleTop > handleBottom) {
            var top = handleTop - menuHeight - CARET.h;
            dropMenu.removeClass('down').addClass('up');
        } else {
            var top = handleOffset.top + handleHeight + CARET.h;
            dropMenu.removeClass('up').addClass('down');
        }

        dropMenu.offset({left: left, top: top});

        var $this = this;
        // add click handler
        dropMenu.click(function (e) {
            var el = $(e.target);
            if (!el.hasClass('drop-menu-item')) return;
            $this.data('start-index', parseInt(el.attr('idx')));
            fitIntoParent.call($this);

            selectMenuItem.call($this);
        });
    }


    function hideDropMenu() {
        this.children('.drop-menu').remove();
        this.children('.drop-handle').removeClass('sel');
    }

    function selectMenuItem() {
        var idx = this.data('start-index');
        var el = this.children(':eq(' + idx + ')');

        this.children('.menu-item').removeClass('sel');
        el.addClass('sel');

        var clickCallback = this.data(StrRes.onClick);
        if($.isFunction(clickCallback)) {
            clickCallback.call(null, this, el);
        }
    }


    var Methods = {
        init: function () {
            if (this.data('initialized')) {
                return;
            }

            // index of menu item which shown first
            this.data('start-index', 0);

            // stylize
            this.addClass('menu-bar');
            this.children().addClass('menu-item');

            // fit into parent container, hide those exceed parent width
            fitIntoParent.call(this);

            // handle click event
            this.click(function (e) {
                var $this = $(this);
                var el = $(e.target);

                if (el.hasClass('icon-caret-down')) {
                    el = el.parent();
                }

                if (!el.hasClass('menu-item')) return;

                if (el.hasClass('drop-handle')) {
                    createDropMenu.call($this);
                    showDropMenu.call($this);
                } else {

                    $this.data('start-index', el.index());
                    selectMenuItem.call($this);
                    hideDropMenu.call($this);
                }
            });

            this.data('initialized', true);
        },

        on: function(key, value) {
            this.data(key, value);
        }
    };


    $.fn.MenuBar = function () {
        var $fstObj = $(this.get(0));

        if (arguments.length == 0) {
            Methods['init'].call($fstObj);
        } else {
            var args = arguments;
            var arg0 = args[0];

            if (Methods[arg0]) {
                args = Array.prototype.slice.call(args, 1);
                return Methods[arg0].apply($fstObj, args);
            } else {
                $.error('No arguments!');
            }
        }
    };
})(jQuery);













/**
 * @Author huaiwang@cisex.com
 * @Date Apr/1/2013
 * input multiple items at the same time, auto complete is also enabled.
 */
(function ($) {
    var _ts = new Date() - 0;

    var _clipboard_enable = window.clipboardData && window.clipboardData.setData;

    var CStr = {
        Initialized: 'bulk_input_' + _ts,

        Options: 'options',
        OnAdding: 'on-adding',
        OnAdded: 'on-added',
        OnDeleting: 'on-deleting',
        OnDeleted: 'on-deleted',
        Equal: 'equal',
        CtrlDown: 'ctrl-down'
    };

    var KeyCodes = {
        ctrl: 17,
        v: 86,
        c: 67,
        enter: 13
    };


    //---------------------------------------------------
    function _addInputBox() {
        var iptbox = $('<span class="ib ipt-box"></span>');
        var ipt = $('<input type="text" class="ipt" />');
        var iptwidth = $('<span class="ipt-width">W</span>');

        iptbox.append(ipt);
        iptbox.append(iptwidth);
        this.append(iptbox);

        ipt.css('width', iptwidth.width() + 'px');

        var bipt = this;

        ipt.blur(function() {
            bipt.find('a.add').show();
            iptbox.css('visibility', 'hidden');
        });

        ipt.keypress(function(e) {

            if(e.keyCode == KeyCodes.enter) {
                var val = $.trim(ipt.val());
                if(val == '') return;
                METHODS.add.call(bipt, val);
                ipt.val('');
            }

            var val = ipt.val();
            iptwidth.html(val + 'W');
            ipt.css('width', iptwidth.width() + 'px');
        });


        ipt.keydown(function(e) {
            if(e.keyCode == KeyCodes.ctrl) ipt.data(CStr.CtrlDown, true);
        });

        ipt.keyup(function(e) {
            if(e.keyCode == KeyCodes.ctrl)
                setTimeout(function() { ipt.data(CStr.CtrlDown, false); },200);

            if(ipt.data(CStr.CtrlDown) && e.keyCode == KeyCodes.v) {
                var cont = ipt.val();

                var rega = /<([^<>]+)>/g;
                var regb = /<([^<>]+)>/;
                var m = cont.match(rega);

                if(m) {
                    var els = bipt.children('span.item');

                    $.each(m, function(i, v) {
                        var xxx = v.match(regb)[1];
                        bipt.BulkInput('add', xxx);
                    });
                }

                ipt.val('');
            }
        });
    }

    function _addCopyButton() {
        var bipt = this;
        var cp = $('<a href="javascript:void(0)" class="copy">Copy</a>');
        bipt.append(cp);

        cp.click(function() {

            if(_clipboard_enable) {
                var vals = bipt.children('span.item').map(function(i, el) {
                    return '<' + $(el).data('val').text + '>';
                }).get().join(',');

                var copy_success = window.clipboardData.setData('Text', vals);

                if(copy_success) {
                    bipt.find('a.add').css('width', 'auto').html('copy success!');
                    setTimeout(function() { bipt.find('a.add').css('width', '16px').html('+'); }, 2000);
                    return;
                }
            }

            // copy failed or clipboard not accessible
            var cb = bipt.find('.copy-box');
            if (cb.length == 0) {
                cb = $('<div class="copy-box"></div>');
                cb.append('<div class="copy-title">Your clipboard is not accessible, copy the following content manually.</div>');
                cb.append('<textarea class="txt-area"></textarea>');
                bipt.append(cb);
            }

            var vals = bipt.children('span.item').map(function (i, el) {
                return '<' + $(el).data('val').text + '>';
            }).get().join(',');
            cb.find('.txt-area').val(vals).select();
        });
    }

    function _getOption(key) {
        var opts = this.data(CStr.Options);
        if(!opts) return null;
        return opts[key];
    }

    function _eq(va, vb) {
        var a = va.text;
        var b = vb.text;

        return a == b;
    }

    function _add(val) {
        if(typeof val == 'string') val = {text: val};

        var eq = _getOption.call(this, CStr.Equal) || _eq;
        var els = this.children('span.item');
        var found = false;
        els.each(function(i, el) {
            var va = $(el).data('val');
            if(eq(va, val)) {
                found = true;
                return false;
            }
        });

        if(found) return;

        var item = $('<span class="item ib"></span>');
        var txt = $('<span class="txt ib"></span>');
        var del = $('<a href="javascript:void(0)" class="del ib">&times;</a>');

        txt.html(val.text);

        item.append(txt);
        item.append(del);

        item.data('val', val);

        var lst = this.find('span.item:last');
        if(lst.length == 1) item.insertAfter(lst);
        else this.prepend(item);

        var foo = _getOption.call(this, CStr.OnAdded);

        if(typeof foo == 'function') {
            foo.call(this, item);
        }
    }

    function _delete(e) {
        var el = $(e.target);

        var val = el.parent().data('val');

        el.parent().remove();

        var foo = _getOption.call(this, CStr.OnDeleted);

        if(typeof foo == 'function') {
            foo.call(this, val);
        }
    }


    var METHODS = {
        init: function () {
            if(!!this.data(CStr.Initialized)) return;

            var bipt = this;
            bipt.addClass('bipt');

            bipt.append('<a href="javascript:void(0)" class="ib add">+</a>');


            _addInputBox.call(bipt);
            _addCopyButton.call(bipt);

            //-------------------------------------------------------
            bipt.click(function(e) {
                var el = $(e.target);

                // delete item
                if(el.hasClass('del')) {
                    var foo = _getOption.call(bipt, CStr.OnDeleting);

                    if(!foo) _delete.call(bipt, e);

                    else if(typeof foo == 'function') {
                        // you have the Responsibility to call
                        // the passed in function after you finished deleting data,
                        // otherwise the ui won't delete corCStrponding item.
                        var val = el.parent().data('val');
                        foo.call(bipt, val, function() {
                            _delete.call(bipt, e);
                        });
                    }
                }

                // show input text box
                else if(el.hasClass('add')) {
                    el.hide();
                    bipt.find('.ipt-box').css('visibility', 'visible').find('.ipt').select().focus();
                }
            });

            //-------------------------------------------------------
            bipt.hover(function() {
                bipt.find('.copy').stop().show();
            }, function() {
                bipt.find('.copy').stop().hide();
            });
        },

        option: function(key, val) {
            var opts = this.data(CStr.Options);
            opts = opts || {};
            opts[key] = val;
            this.data(CStr.Options, opts);
        },

        add: function(val) {
            var bipt = this;
            var foo = _getOption.call(bipt, CStr.OnAdding);

            if(!foo) _add.call(bipt, val);

            else if(typeof foo == 'function') {
                // you have the CStrponsibility to call
                // the passed in function after you finished persisting data,
                // otherwise the ui won't show the item added.
                foo.call(bipt, val, function() {
                    _add.call(bipt, val);
                });
            }
        },

        items: function() {
            return this.children('span.item');
        },

        source: function(src) {
            var bipt = this;
            this.find('.ipt').autocomplete({
                source: src,
                select: function(e, ui) {
                    var val = ui.item.label;
                    $(e.target).val('');
                    bipt.BulkInput('add', val);
                    return false;
                }
            });
        },

        autocomplete: function(ac) {
            this.find('.ipt').autocomplete(ac);
        }
    };





    $.fn.BulkInput = function () {
        var args = arguments;
        var arg0 = args[0];

        var $1stEl = $(this.get(0));

        if(args.length == 0) {
            METHODS['init'].call($1stEl);
            return;
        }

        if (METHODS[arg0]) {
            args = Array.prototype.slice.call(args, 1);
            return METHODS[arg0].apply($1stEl, args);
        } else {
            $.error('No arguments!');
        }
    };
})(jQuery);
















//------------
(function ($) {
    var Methods = {
        up: function(html) {
            if(!!$(this).data('tooltip')) return;
            var tooltip = $('<a class="tooltip"></a>');
            var arrow = $('<span class="up_arrow"></span>');
            var arrow_container = $('<div></div>');
            var container = $('<div class="container"></div>');

            arrow_container.css('height', '10px');
            arrow_container.css('text-align', 'center');
            arrow_container.css('overflow', 'visible');

            if(!!html) container.html(html);

            arrow_container.append(arrow);
            tooltip.append(arrow_container);
            tooltip.append(container);

            tooltip.hide();
            $(document.body).append(tooltip);
            $(this).data('tooltip', tooltip);

            var $this = $(this);
            var off = $this.offset();
            var w = $this.outerWidth();
            var h = $this.outerHeight();

            $this.hover(function() {
                var top = off.top + h;
                var left = off.left + w / 2 - tooltip.outerWidth() / 2;
                tooltip.stop(true, true).slideDown(200).offset({top: top, left: left});
            }, function() {
                tooltip.stop(true, true).delay(200).slideUp(200);
            });

            tooltip.hover(function() {
                tooltip.stop(true, true);
            }, function() {
                tooltip.stop(true, true).delay(200).slideUp(200);
            });

            return container;
        }
    };

    jQuery.fn.Tooltip = function () {
        var args = arguments;
        var arg0 = args[0];

        if (Methods[arg0]) {
            args = Array.prototype.slice.call(args, 1);
            return Methods[arg0].apply(this, args);
        } else {
            $.error('No arguments!');
        }
    }
})(jQuery);














//-----------sort table

(function ($) {
    function defaultExtractor(html) {
        html = $.trim(html);

        var reg = (/^([+-]?[0-9]+[.]?[0-9]*)%?$/);
        var m = html.match(reg);
        if (m) {
            return parseFloat(m[1]);
        } else {
            return html;
        }
    }

    function defaultComparator(A, B) {
        var a = defaultExtractor($(A).html());
        var b = defaultExtractor($(B).html());

        if(typeof a != 'number' || typeof b != 'number') {
            a += "";
            b += "";
        }

        return a > b ? 1 : -1;
    }

    var C = {
        Options: "options",
        Comparators: "comparators",
        SortingRows: "sorting-rows",
        OnSorted: "on-sorted",
        OnSorting: "on-sorting",
        TriggerSort: "trigger-sort",
        TriggerCell: "trigger-cell"
    };

    function getOption(key) {
        var options = this.data(C.Options);
        if (options) {
            return options[key];
        }
        return null;
    }

    function getTriggerCell(idx) {
        var triggerCell = getOption.call(this, C.TriggerCell);
        if(triggerCell) return triggerCell.call(this, idx);
        return this.find("tr:first").children(':eq(' + idx + ')');
    }

    function getTopRow(e) {
        var el = $(e.target);

        var t = el;

        while (t.parent().get(0) != this.get(0)) t = t.parent();

        if (t.get(0).nodeName.toLowerCase() == 'tr') return t;


        while (el.parent().get(0) != t.get(0)) {
            el = el.parent();
        }

        return el;
    }

    function getTopCell(e) {
        var tr = getTopRow.call(this, e);
        var el = $(e.target);

        while (el.parent().get(0) != tr.get(0)) el = el.parent();

        return el;
    }

    function getComparator(idx) {
        idx = typeof idx == 'number' ? idx : -1;
        var comparators = this.data(C.Comparators) || {};
        var comparator = comparators[idx];

        if (!comparator) return defaultComparator;
        return comparator;
    }

    function getSortingRows() {
        var sortingRows = getOption.call(this, C.SortingRows);
        if (!sortingRows || typeof sortingRows != 'function') {
            return this.find('tr:gt(0)');
        }
        return sortingRows.call(this);
    }

    function triggerSort(e) {
        var trigger = getOption.call(this, C.TriggerSort);
        if (!trigger) {
            var tr = getTopRow.call(this, e);
            var trs = this.find("tr");
            if (tr && tr.index(trs) == 0) return true;
            else return false;
        } else return trigger.call(this, e);
    }

    function orderTable(th) {
        var dir = 0;

        var onSorting = getOption.call(this, C.OnSorting);

        if (th.hasClass('desc')) {
            dir = 1;
            th.removeClass('desc').addClass('asc');
            if (onSorting) onSorting.call(this, 'asc', th);
        } else {
            dir = -1;
            th.removeClass('asc').addClass('desc');
            if (onSorting) onSorting.call(this, 'desc', th);
        }

        var tbl = this;
        var idx = th.index();
        var trs = getSortingRows.call(this);

        if (trs.length < 2) return; // too short to sort
        var tds = trs.find('td:eq(' + idx + ')');

        var comparator = getComparator.call(this, idx);

        var len = trs.length;
        var switched = false;
        do {
            switched = false;

            for (var i = 0; i < len - 1; i++) {
                var rst = comparator(tds.get(i), tds.get(i + 1));

                if (rst * dir > 0) {
                    var tr = $(trs.get(i));
//                    tr.insertAfter(tr.next());
                    tr.insertAfter($(trs.get(i + 1)));
                    switched = true;
                }

                if (switched) { // refresh row/col set after sorting
                    trs = getSortingRows.call(this);
                    tds = trs.find('td:eq(' + idx + ')');
                }
            }

            len--;
        } while (switched);


        var onSorted = getOption.call(this, C.OnSorted);
        if (th.hasClass('asc')) {
            if (onSorted) onSorted.call(this, 'asc', th);
        } else {
            if (onSorted) onSorted.call(this, 'desc', th);
        }
    }

    var METHODS = {
        init: function () {
            var $this = $(this);

            if ($this.data('initialized')) return;

            $this.click(function (e) {
                var b = triggerSort.call($this, e);

                if (!b) return;

                var th = getTopCell.call($this, e);
                if (!th) return;

                orderTable.call($this, th);
            });

            $this.data('initialized', true);
        },

        option: function (key, val) {
            var options = $(this).data(C.Options) || {};
            options[key] = val;
            $(this).data(C.Options, options);
        },

        setComparator: function (foo, idx) {
            idx = typeof idx == 'number' ? idx : -1;
            var comparators = $(this).data(C.Comparators) || {};
            comparators[idx] = foo;
            $(this).data(C.Comparators, comparators);
        },

        sort: function (idx) {
            var th = getTriggerCell.call(this, idx);
            th.click();
        }
    };

    $.fn.SorTable = function () {
        var args = arguments;
        var arg0 = args[0];

        var $fsTable = $(this.get(0));

        if (METHODS[arg0]) {
            args = Array.prototype.slice.call(args, 1);
            return METHODS[arg0].apply($fsTable, args);
        } else {
            $.error('No arguments!');
        }
    };
})(jQuery);