/**
 * Created with IntelliJ IDEA.
 * User: guangyu
 * Date: 1/9/14
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */


var qd = qd || {};
qd.qddts_cache_info = {
//     QDDTS_CACHE_SERVER : 'tasman307.cisex.com' ,
    QDDTS_CACHE_SERVER : 'Cctg-qaqtts-vm1.cisex.com' ,
//    QDDTS_CACHE_SERVER: '10.224.105.205',
//    QDDTS_CACHE_SERVER_PORT: 443,
//    QDDTS_CACHE_SERVER_PROTOCOL: 'https',
    QDDTS_CACHE_SERVER_PROTOCOL: 'http',
    QDDTS_CACHE_SERVER_PORT: 8080,
    QDDTS_CACHE_SERVER_PATH: '/stddq/data/jsonp',
    QDDTS_CACHE_SERVER_PATH_SIMPLE:'/stddq/data/jsonp/simple',
    QDDTS_CACHE_SERVER_PATH_REFRESH:'/stddq/data/jsonp/refresh'

}
qd.qddts_cache_info.QDDTS_CACHE_SERVER_PATH_FULL = qd.qddts_cache_info.QDDTS_CACHE_SERVER_PROTOCOL + "://" + qd.qddts_cache_info.QDDTS_CACHE_SERVER + ":" + qd.qddts_cache_info.QDDTS_CACHE_SERVER_PORT + qd.qddts_cache_info.QDDTS_CACHE_SERVER_PATH;
qd.qddts_cache_info.QDDTS_CACHE_SERVER_PATH_FULL_SIMPLE = qd.qddts_cache_info.QDDTS_CACHE_SERVER_PROTOCOL + "://" + qd.qddts_cache_info.QDDTS_CACHE_SERVER + ":" + qd.qddts_cache_info.QDDTS_CACHE_SERVER_PORT + qd.qddts_cache_info.QDDTS_CACHE_SERVER_PATH_SIMPLE;
qd.qddts_cache_info.QDDTS_CACHE_SERVER_PATH_FULL_REFRESH= qd.qddts_cache_info.QDDTS_CACHE_SERVER_PROTOCOL + "://" + qd.qddts_cache_info.QDDTS_CACHE_SERVER + ":" + qd.qddts_cache_info.QDDTS_CACHE_SERVER_PORT + qd.qddts_cache_info.QDDTS_CACHE_SERVER_PATH_REFRESH;
qd.qddts_query_url = "http://wwwin-metrics.cisex.com/protected-cgi-bin/ddts_query.cgi?fields=Submitter-id,Engineer,Status,Priority,Severity,Found,DE-manager,Age,Component,Headline&type=directweb&expert=";
qd.user_info_api = "http://ta.webex.com.cn/tasche/jsonp/call.action";

