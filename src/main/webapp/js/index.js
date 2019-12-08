$(function() {
    var DEG2RAD = 2*Math.PI/360;
    var DEG36 = 36*DEG2RAD;
    var COS36 = Math.cos(DEG36);
    var SIN36 = Math.sin(DEG36);
    var DEG72 = 72*DEG2RAD;
    var SIN72 = Math.sin(DEG72);
    var COS72 = Math.cos(DEG72);

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
        }).done(function(activities) {
            console.log(activities);
            var table = templates.activityTable({
                user: user,
                activities: activities
            });
            console.log(table);
            $(".activities").html(table);
            var symbol = "";
            if (activities && activities.length > 0) {
                symbol = facetSymbol(user, activities[0].facetNumber);
            }
            drawPentagon(symbol);
        });
    }

    function facetSymbol(user, facetNumber) {
        var facets = user && user.facets;
        if (facets && typeof facetNumber === "number" &&
                        facetNumber >= 0 && facetNumber < facets.length) {
            var s = facets[facetNumber].symbol;
            if (s) {
                return s;
            }
        }
        return "";
    }

    function drawPentagon(symbol) {
        var margin = {top: 20, right: 20, bottom: 20, left: 20}
        var $cont = $("#pentagon");
        var width = $cont.width();
        var height = $cont.height();
        var hi = height-margin.top-margin.bottom;
        var wi = width-margin.left-margin.right;
        var rh = hi/(COS36+1);
        var rw = wi/(2*SIN72);
        var r = Math.min(rh, rw);
        var xm = margin.left+wi/2;
        var ym = margin.top+(hi-(r*(1+COS36)))/2;
        var poly = [
            {x: xm+(r*SIN36), y: ym},
            {x: xm+(r*SIN72), y: ym+(r*(COS36+COS72))},
            {x: xm, y: ym+(r*(COS36+1))},
            {x: xm-(r*SIN72), y: ym+(r*(COS36+COS72))},
            {x: xm-(r*SIN36), y: ym},
        ];
        var points = poly.map(function(p) {
            return [p.x,p.y].join(",");
        }).join(" ");
        d3.select("#pentagon svg").remove();
        var svg = d3.select("#pentagon").append("svg")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g");
        svg.append("polygon")
                .attr("points",points)
                .attr("stroke","black")
                .attr("stroke-width",8)
                .attr("fill", "none")
                .attr("stroke-linejoin", "round");
        svg.append("text")
                .text(symbol)
                .attr("id", "facetSymbol")
                .attr("x", xm)
                .attr("y", ym+(r*COS36))
                .style("text-anchor", "middle")
                .style("alignment-baseline", "middle");
    }
});
