function debug() {
    return;
}
function unfollow(_dbid, callback) {
    $.get('unfollow_dashboard', {dbid: _dbid},
        function (result) {
            if (result.success) {
                update_nav_bar();
                if (!!callback) try {
                    callback()
                } catch (e) {
                }
            } else {
                alert('unfollow dashboard failed');
            }
        },
        "json"
    );
}


function save_userdashboard_orders(ids, callback) {
    $.get('save_userdashboard_orders', {ids: ids},
        function (result) {
            if (result.success) {
                update_nav_bar();
                if (!!callback) try {
                    callback()
                } catch (e) {
                }
            } else {
                alert('unfollow dashboard failed');
            }
        },
        "json"
    );
}