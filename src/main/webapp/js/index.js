$(function() {
    Handlebars.registerHelper('index', function(o, p) {
        if (o && typeof p !== 'undefined') {
            return o[p];
        }
    });

    var msg = {};
    $('script[type="text/x-handlebars-template"]').each(function() {
        var $this = $(this);
        msg[$this.attr("id")] =
                Handlebars.compile($this.html());
    });

    $.ajax({
        type: "GET",
        url: "api/user",
        dataType: "json",
        success: function(user) {
            console.log(user);
            var table = msg.facetTable(user);
            console.log(table);
            $(".facets").html(table);
            loadActivities(user);
        }
    });

    function loadActivities(user) {
        $.ajax({
            type: "GET",
            url: "api/activity",
            dataType: "json",
            success: function(data) {
                console.log(data);
                var table = msg.activityTable({user: user, activities: data});
                console.log(table);
                $(".activities").html(table);
            }
        });
    }
});
