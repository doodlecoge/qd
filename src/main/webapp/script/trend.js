var colors = [ "fuchsia", "green", "maroon", "navy", "olive", "orange",
    "purple", "silver", "teal", "yellow" ];

var colorHexs = [ "FF00FF", "008000", "800000", "000080", "808000", "FFA500",
    "800080", "c0c0c0", "008080", "ffff00" ];


function drawTrendWidget(holder, sz, pd, datas) {
    var paper = null;

    drawTrend();
    drawColoredLabel();


    function resetAll() {
        paper.forEach(function (el) {
            if (!(el.type == "circle" || el.type == "path"))
                return;

            el.animate({
                r:4,
                'stroke-width':1
            }, 200);

            if (el.data('txt')) {
                el.data('txt').remove();
                el.data('txt', null);
            }

            if (el.data('pop')) {
                el.data('pop').remove();
                el.data('pop', null);
            }


            if (el.data('lbl')) {
                el.data('lbl').remove();
                el.data('lbl', null);
            }

        });

    }

    function resetColoredTable() {
        var tbl = $("#" + holder).find("table:first");

        tbl.find("td").each(function() {
            var td = $(this);
            var i = td.data('cid') || 0;
            td.css('background', '#ddd');
            td.css('color', colors[i % colors.length]);
        });
    }

// colored label for each project,
// on-hover will emphasize corresponding
// plot in trends graphic
    function drawColoredLabel() {
        var cols = Math.ceil(sz.w / 150);
        if (cols == 0) cols = 1;


        var idx = 0;
        var tbl = $('<table class="tbl"></table>')
            .css('width', '99%')
//            .css('table-layout', 'fixed')
            .css('margin', '10px 0 0 0');
        var tbody = $('<thead></thead>');
        tbl.append(tbody);
        var tr = null;


        $.each(datas, function (key, arr) {
            if ((idx % cols) == 0) {
                tr = $('<tr></tr>');
                tbody.append(tr);
                tbl.append(tbody);
            }


            var td = $('<td>' + key + '</td>')
                .css('cursor', 'pointer')
                .css('color', colors[idx % colors.length])
                .attr('align', 'center')
                .data('cid', idx);

            td.data('pc', key);
            tr.append(td);


            td.click((function(k, v, i) {
                return function(e) {
                    resetAll();

                    paper.forEach(function (el) {
                        if (el.data('key') == k)
                            el.animate({ 'stroke-width':3 }, 200);
                    });

                    var tbl = showTrendDetails(k, v);

                    $(this).parents("table").find("td").each(function(ii) {
                        var td = $(this);
                        if (!td.attr("hide"))
                            td.css('background', '#ddd').css('color', colors[(td.data('cid') || 0) % colors.length]);
                    });

                    var td = $(this);
                    td.css('background', colors[(td.data('cid') || 0) % colors.length])
                        .css('color', getContrastL(colorHexs[(td.data('cid') || 0) % colors.length]));

                    e.stopPropagation();
                }
            })(key, arr, idx));

            idx++;
        });

    if(idx >= cols && idx % cols != 0) {
//        tr.append($('<td colspan="' + (cols - idx % cols) + '"></td>').css('background', "#ddd"));
        tr.append($('<td hide="true" colspan="' + (cols - idx % cols) + '"></td>'));
    }

        $("#" + holder).append(tbl);
    }


    /* draw trend curve */
    function drawTrend() {
        paper = Raphael(holder, sz.w, sz.h);

        var x1 = pd.l, y1 = pd.t;
        var x2 = sz.w - pd.r, y2 = sz.h - pd.b;

        /* draw box */
        paper.path([ "M", x1, y1, "L", x1, y2 ].join(" "));
        paper.path([ "M", x1, y1, "L", x2, y1 ].join(" "));
        paper.path([ "M", x2, y2, "L", x1, y2 ].join(" "));
        paper.path([ "M", x2, y2, "L", x2, y1 ].join(" "));

        /* calculate scale */
        var len = 0;
        var max = 0;
        var min = 100;

        $.each(datas, function (key, val) {
            var l = 0;

            $.each(val, function (i, obj) {
                l++;
                var passrate = 0;
                if (obj.tt != 0)
                    passrate = (obj.tt - obj.ff) / obj.tt;
                obj.ps = Math.round(passrate * 10000) / 100;

                if (max < obj.ps)
                    max = obj.ps;
                if (min > obj.ps)
                    min = obj.ps;
            });

            if (l > len)
                len = l;
        });

        max = Math.ceil(max / 10) * 10;
        min = Math.floor(min / 10) * 10;

        if (max == min) {
            max += 5;
            min -= 5;
        }

        /* draw scale */

        // -- x --
        var dx = (sz.w - pd.l - pd.r) / len;

        // x axis scale
        for (var i = 0; i <= len; i++) {
            var tx = i * dx + pd.l;
            paper.path([ "M", tx, y2, "L", tx, y2 + 5 ]);
        }

        // x axis text
        for (var i = 0; i < len; i++) {
            if(i % 5 != 0 && i != len - 1) continue;

            var tx = x1 + dx / 2 + i * dx;

            if (i == len - 1)
                paper.text(tx, y2 + 10, "Latest").attr("font-size", 12).attr(
                    "font-family", "Courier New");
            else
                paper.text(tx, y2 + 10, "" + i).attr("font-size", 12).attr(
                    "font-family", "Courier New");
        }

        // -- y --
        var t = (max - min) / 10;
        var dy = (sz.h - pd.t - pd.b) / t;

        // y axis scale
        for (var i = 0; i <= t; i++) {
            var ty = sz.h - pd.b - i * dy;
            paper.path([ "M", x1, ty, "L", x1 - 5, ty ]);
        }

        // y min passrate text
        paper.text(x1 - 20, sz.h - pd.b, "" + min);

        // y max passrate text
        paper.text(x1 - 20, pd.t, "" + max);

        // y title text
        var txt = paper.text(x1 - 20, pd.t + (sz.h - pd.t - pd.b) / 2,
            "Passrate(%)").attr("font-size", 12).attr("font-family",
            "Courier New").attr("font-weight", "bold").rotate(-90);

        /* draw circle & lines */
//        var colors = [ "fuchsia", "green", "maroon", "navy", "olive", "orange",
//            "purple", "silver", "teal", "yellow" ];
        var color_id = 0;

        $.each(datas, function (key, val) {
            var color = colors[color_id++ % colors.length];
            $.each(val, function (i, obj) {

                if (i == 0)
                    return;

                var tx = x2 - dx / 2 - i * dx;
                var ty = y2 - (y2 - y1) * (obj.ps - min) / (max - min);
                var tx2 = x2 - dx / 2 - i * dx + dx;
                var ty2 = y2 - (y2 - y1) * (val[i - 1].ps - min) / (max - min);

                var line = paper.path([ "M", tx, ty, "L", tx2, ty2 ].join(" ")).attr(
                    "stroke", color);

                line.data("key", key);
                line.data("ps", obj.ps);
            });
        });


        color_id = 0;
        $.each(datas, function (key, val) {

            var color = colors[color_id++ % colors.length];

            // save created circles,
            // and corresponding test-status
            var circles = [];
            var rsts = [];

            // draw circles
            $.each(val, function (i, obj) {
                var tx = x2 - dx / 2 - i * dx;
                var ty = y2 - (y2 - y1) * (obj.ps - min) / (max - min);

                var circle = paper.circle(tx, ty, 4).attr({
                    fill:"#fff",
                    stroke:color,
                    "stroke-width":1
                });


                circles[i] = circle;
                rsts[i] = obj;


                circle.data("key", key);
                circle.data("ps", obj.ps);
                circle.data("bn", obj.bn)
            });





            // draw popups and labels,
            // and add hover hander for each circle

            $.each(circles, function(i, circle) {
                var obj = rsts[i];

                var x = Math.ceil(x2 - dx / 2 - i * dx);
                var y = Math.ceil(y2 - (y2 - y1) * (obj.ps - min) / (max - min));

                function moveOverCircle(circle) {
                    resetColoredTable();
                    var ctbl = $('#' + holder).find('table:first');
                    ctbl.find('td').each(function(i, el) {
                        var td = $(el);
                        var pc = td.data('pc');
                        var cid = td.data('cid');
                        if(pc == key) {
                            td.css('background', colors[cid % colors.length]);
                            td.css('color', getContrastL(colorHexs[cid % colors.length]));
                        }
                    });

                    var tbl = showTrendDetails(key, val);

                    $(".rpt-tbl").find("tr").each(function () {
                        if ($(this).data('bn') == obj.bn) {
                            $(this).addClass('sel');
                        }
                    });

                    resetAll();

                    paper.forEach(function (el) {
                        if (el.data('key') == key)
                            el.animate({ 'stroke-width':3 }, 200);
                    });

                    var txt = paper.text(x, y, obj.ps + "%").attr({font:'12px Helvetica, Arial', fill:"#fff"});

                    var pp = paper.popup(x, y, txt, "right");
                    pp.attr({fill:"#000", "fill-opacity":.7});


                    circle.data('pop', pp);
                    circle.data('txt', txt);
                }

                var timer;

                circle.mouseout(function(){
                    clearTimeout(timer);
                });

                circle.hover(function () {
                    timer = window.setTimeout(function(){moveOverCircle(circle)}, 500);
                }); // circle hover handler
            }); // hover popup

        });
    }




    /* create table,
     * will show latest several builds of the project
     */

    function showTrendDetails(key, val) {
        var tbl = $('<table width="99%" style="margin-top: 10px;" class="tbl details" cellspacing="1" cellpadding="0"></table>');

        tbl.data('val', val);

        tbl.append('<col />');
        tbl.append('<col width="60" />');
        tbl.append('<col width="60" />');
        tbl.append('<col width="60" />');
        tbl.append('<col width="100" />');


        var thead = $('<thead></thead>');
        tbl.append(thead);

        // project code line
        hdr = $('<tr></tr>');
        hdr.append($('<th colspan="5" style="text-align: center"><i class="icon-remove hide_tbl"></i>' + key + '</th>'));
        thead.append(hdr);


        // header line
        var hdr = $('<tr></tr>');

        hdr.append($('<th class="txt" title="Build#">Build#</th>'));
        hdr.append($('<th title="Total Cases">Cases</th>'));
        hdr.append($('<th class="fail" title="Fail Cases">Fail Cases</th>'));
        hdr.append($('<th title="Pass Rate">Pass Rate</th>'));
        hdr.append($('<th title="Last Modify Time">Last Modify Time</th>'));

        thead.append(hdr);


        // data lines
        $.each(val, function (i, obj) {
            var line = $('<tr></tr>');

            line.append($('<td><a target="_blank" style="text-decoration: none;" href="' + rptBaseUrl + "/tasche/reports/viewOverallBuildReport.action?projectCode=" + key + "&buildNumber=" + obj.bn + '">' + obj.bn + '</a></td>'));
            line.append($('<td>' + obj.tt + '</td>'));
            line.append($('<td class="fail">' + obj.ff + '</td>'));
            line.append($('<td>' + obj.ps + '%</td>'));
            line.append($('<td>' + obj.dt + '</td>'));

            line.data('bn', obj.bn);

            tbl.append(line);
        });


        // add table to holder/container
        if ($("#" + holder).children("table").length > 1)
            $("#" + holder).children("table:last").remove();
        $("#" + holder).append(tbl);

        $(".hide_tbl").click(function(e){
            $(".tbl.details").remove();
            resize();
        });
        resize();
        function clearSel() {
            tbl.find("tr").each(function () {
                $(this).removeClass("sel");
            });
        }


        tbl.mousemove(function (e) {
            var tr = $(e.target).parents("tr");
            var bn = tr.data('bn');

            if (!bn) return;

            clearSel();
            tr.addClass("sel");

            resetAll();

            paper.forEach(function (el) {
                if (el.data('key') == key) {
                    el.animate({
                        'stroke-width':3
                    }, 200);

                    // show popup
                    if (el.data('bn') == bn) {
                        var box = el.getBBox();

                        var bw = box.width;
                        var bh = box.height;
                        var bx = box.x;
                        var by = box.y;

                        var x = bx + Math.round(bw / 2);
                        var y = by + Math.round(bh / 2);

                        var txt = paper.text(x, y, el.data('ps') + "%").attr({font:'12px Helvetica, Arial', fill:"#fff"});

                        var pp = paper.popup(x, y, txt, "right");
                        pp.attr({fill:"#000", "fill-opacity":.7});


                        el.data('pop', pp);
                        el.data('txt', txt);
                    }
                }
            });
        });
        return tbl;
    }

}