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