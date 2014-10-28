var rptBaseUrl = "http://ta.webex.com.cn";
var url = rptBaseUrl + "/tasche/reports/viewGenericReport.action";

function makeProjectLink(pc, bn) {
    return url + "?projectCode=" + pc + "&buildNumber=" + bn;
}

function isVmlSupported() {
    if (typeof supportsVml.supported == "undefined") {
        var a = document.body.appendChild(document.createElement('div'));
        a.innerHTML = '<v:shape id="vml_flag1" adj="1" />';
        var b = a.firstChild;
        b.style.behavior = "url(#default#VML)";
        supportsVml.supported = b ? typeof b.adj == "object" : true;
        a.parentNode.removeChild(a);
    }
    return supportsVml.supported
}

function isSvgSupported() {
    return document.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#Shape", "1.0") ||
        ("http://www.w3.org/TR/SVG11/feature#Shape", "1.1");
}

function isCanvasSupported() {
    var elem = document.createElement('canvas');
    return !!(elem.getContext && elem.getContext('2d'));
}


function ts() {
    var d = new Date();
    return (d - 0) * 1000 + d.getMilliseconds();
}

function resizeIfrmSizeJ(id, j) {
//    var loc = window.location;
//    var id = loc.toString().match(/\/([0-9]+)$/)[1];
    var ifrm = j("#view" + id, parent.document);
    var h = j(document.body).outerHeight(true);
    ifrm.height(h + 1);
}

function resizeIfrmSize(cat) {
    if (!cat) return;
    var loc = window.location;
    var id = loc.toString().match(/\/([0-9]+)$/)[1];
    var ifrm = $("#" + cat + id, parent.document);
    var h = $(document.body).outerHeight(true);
    if (document.body.scrollWidth > $(document.body).innerWidth()) {
        ifrm.height(h + 20);
    } else {
//    var h = $(document.body).scrollHeight;
        ifrm.height(h + 1);
    }
}

function resizeIfrmSizeWithMaxHeight(cat, maxHeight) {
    if (!cat) return;
    var loc = window.location;
    var id = loc.toString().match(/\/([0-9]+)$/)[1];
    var ifrm = $("#" + cat + id, parent.document);
    var h = $(document.body).outerHeight(true);
    if (h > maxHeight)
        h = maxHeight;
    if (document.body.scrollWidth > $(document.body).innerWidth()) {
        ifrm.height(h + 20);
    } else {
        ifrm.height(h + 1);
    }
}


function resize(hMin) {
    var min = 0;
    if (!!hMin) {
        try {
            min = parseInt(hMin)
        } catch (e) {
        }
    }
    //console.log(hMin);
    if (top == this) return;
    var loc = window.location;
    var m = loc.toString().match(/\/(view|setting)\/(\d+)/);
    if (!m) return;
    var ifrm = $("#" + m[1] + m[2], parent.document);


    var h = $(document.body).outerHeight(true);
    h = Math.max(h, min);
    //console.log(h)
    ifrm.height(h + 5);

    h = Math.max(h, document.body.scrollHeight);
    h = Math.max(h, min);

    ifrm.height(h + 5);


    ifrm.css('width', '100%');

}


function getContrastL($hexcolor) {
    var arr = $hexcolor.split('');
    $r = parseInt(arr[0], 16) * 10 + parseInt(arr[1], 16);
    $g = parseInt(arr[2], 16) * 10 + parseInt(arr[3], 16);
    $b = parseInt(arr[4], 16) * 10 + parseInt(arr[5], 16);
    $l = ($r * 0.2126) + ($g * 0.7152) + ($b * 0.0722);
    return ($l >= 128) ? 'black' : 'white';
}


if (!Array.isArray) {
    Array.isArray = function (obj) {
        var str = Object.prototype.toString.call(obj);
        return str.match(/\[object Array\]/);
    }
}

function binarySearch(arr, val) {
    var len = arr.length;
    if (len == 0) return -1;

    var l = 0, h = len - 1, m = 0;

    while (l <= h) {
        m = Math.floor((l + h) / 2);
        if (arr[m] < val) l = m + 1;
        else if (arr[m] > val) h = m - 1;
        else return m;
    }

    return -l - 1;
}


function heapSort(arr) {
    _sort(arr);
    return arr;

    function _sort(arr) {
        _heapify(arr);

        var end = arr.length - 1;
        while (end > 0) {
            _swap(arr, 0, end);
            end--;
            _siftDown(arr, 0, end);
        }
    }

    function _heapify(arr) {
        var count = arr.length;
        var start = Math.floor(count / 2) - 1;

        while (start >= 0) {
            _siftDown(arr, start, count - 1);
            start--;
        }
    }

    function _siftDown(arr, start, end) {
        var root = start;

        while (root * 2 < end) {
            var child = root * 2 + 1;
            var swap = root;

            if (arr[swap] < arr[child]) swap = child;

            if (child < end && arr[swap] < arr[child + 1]) swap = child + 1;

            if (swap != root) {
                _swap(arr, swap, root);
                root = swap;
            } else return;
        }
    }

    function _swap(arr, i, j) {
        var t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}

function HashArray() {
    var arr = [];

    this.add = function (val) {
        var pos = binarySearch(arr, val);
        if (pos >= 0) return;

        arr.push(val);

        heapSort(arr);
    }

    this.data = function () {
        return arr;
    }
}

function HashArray2() {
    var obj = {};

    this.add = function (val) {
        obj[val] = true;
    }

    this.remove = function (val) {
        delete obj[val];
    }

    this.data = function () {
        var arr = [];
        $.each(obj, function (k, v) {
            arr.push(k);
        })
        return arr;
    }
}


function urlEncode(unencoded) {
    return encodeURIComponent(unencoded);
//    return encodeURIComponent(unencoded).replace(/'/g,"%27").replace(/"/g,"%22");
}

function urlDecode(encoded) {
    return decodeURIComponent(encoded.replace(/\+/g, " "));
}


function sort(arr, comparer, dir) {
    if(!$.isArray(arr) || typeof comparer != 'function') {
        return;
    }

    var len = arr.length;

    for(var i = 0; i < len - 1; i++) {
        var k = i;
        for(var j = i + 1; j < len; j++) {
            if(dir * comparer(arr[k], arr[j]) > 0) {
                k = j;
            }
        }
        if(k != i) {
            var t = arr[k];
            arr[k] = arr[i];
            arr[i] = t;
        }
    }
}