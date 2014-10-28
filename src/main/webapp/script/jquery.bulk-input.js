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

            bipt.append('<a href="javascript:void(0)" class="ib add" title="Add">+</a>');


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