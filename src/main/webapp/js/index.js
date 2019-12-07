$(function() {
    var currentUser = null;
    $(document).on("submit", "#facetForm", function(e) {
        e.preventDefault();
        e.stopPropagation();
        if (currentUser) {
            var facets = currentUser.facets;
            if (facets) {
                var newFacets = [];
                for (var i = 0; i < facets.length; ++i) {
                    var index = $("[name=ix" + i + "]", this).val();
                    newFacets[index] = facets[i];
                }
                $.ajax({
                    type: "PUT",
                    url: "api/user/facets",
                    dataType: "json",
                    data: JSON.stringify(newFacets)
                }).done(function(user) {
                    currentUser = user;
                    console.log(user);
                    var table = templates.facetTable(user);
                    console.log(table);
                    $(".facets").html(table);
                    loadActivities(user);
                });
            }
        }
    });

    Handlebars.registerHelper('itemAt', function(o, p) {
        if (o && typeof p !== 'undefined') {
            return o[p];
        }
    });
    Handlebars.registerHelper('isNumber', function(x) {
        return typeof x === 'number';
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
        currentUser = user;
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
