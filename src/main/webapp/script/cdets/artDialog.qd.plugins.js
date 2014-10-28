/*!
 * artDialog plugins for quality dashboard
 */

;(function ($) {
    /**
     * Input Area To solve the problem in setting
     */
    $.prompt = $.dialog.prompt = function (title, ok, defaultValue) {
        defaultValue = defaultValue || '';
        var input;

        return $.dialog({
            title:title,
            id: 'Prompt',
            fixed: true,
            lock: true,
            content: [
                '<div>',
                '<textarea style="width:500px;padding:6px 4px" class="d-input-textarea">',
                defaultValue,
                '</textarea>',
                '</div>'
            ].join(''),
            initialize: function () {
                input = this.dom.content.find('.d-input-textarea')[0];
                input.select();
                input.focus();
            },
            ok: function () {
                return ok && ok.call(this, $(input).val().replace(/\\{1,2}/g,"\\\\"));
            },
            cancel: function () {}
        });
    };
    $.fn.showDetailEditor = function(){
        var $input = $(this);
        var defaultContent = $input.val();
        var label = $input.attr('label');
        resize(500);
        $.prompt(label,function(value){
            $input.val(value);
        },
        defaultContent
        )
    }
    $.fn.bindEditor = function(){
        this.attr('readonly','readonly');
        this.bind('click',this.showDetailEditor);
        return this;
    }
}(this.art || this.jQuery));