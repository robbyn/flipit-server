$(function() {
    Handlebars.registerHelper('index', function(o, p) {
        if (o && typeof p !== 'undefined') {
            return o[p];
        }
    });

    var templates = {};
    $('script[type="text/x-handlebars-template"]').each(function() {
        var $this = $(this);
        templates[$this.attr("id")] =
                Handlebars.compile($this.html());
    });

    $.ajax({
        type: "GET",
        url: "api/user",
        dataType: "json"
    }).done(function(user) {
        console.log(user);
        var table = templates.facetTable(user);
        console.log(table);
        $(".facets").html(table);
        loadActivities(user);
    });

    function loadActivities(user) {
        $.ajax({
            type: "GET",
            url: "api/activity",
            dataType: "json"
        }).done(function(data) {
            console.log(data);
            var table = templates.activityTable({
                user: user,
                activities: data
            });
            console.log(table);
            $(".activities").html(table);
        });
    }
});
