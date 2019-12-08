$(function() {
    const DEG2RAD = 2*Math.PI/360;
    const DEG36 = 36*DEG2RAD;
    const COS36 = Math.cos(DEG36);
    const SIN36 = Math.sin(DEG36);
    const DEG72 = 72*DEG2RAD;
    const SIN72 = Math.sin(DEG72);
    const COS72 = Math.cos(DEG72);

    var currentUser = null;

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
        const table = templates.facetTable(user);
        console.log(table);
        $(".facets").html(table);
        drawPentagon("");
        loadActivities(user);
    });

    function loadActivities(user) {
        $.ajax({
            type: "GET",
            url: "api/activity",
            dataType: "json"
        }).done(function(activities) {
            console.log(activities);
            const table = templates.activityTable({
                user: user,
                activities: activities
            });
            console.log(table);
            $(".activities").html(table);
            const symbol = (activities && activities.length > 0) ?
                facetSymbol(user, activities[0].facetNumber) : "";
            $("#facetSymbol").text(symbol);
            setTimeout(refreshActivities, 2000);
        });
    }

    function refreshActivities() {
        if (currentUser) {
            loadActivities(currentUser);
        }
    }

    function facetSymbol(user, facetNumber) {
        const facets = user && user.facets;
        if (facets && typeof facetNumber === "number" &&
                        facetNumber >= 0 && facetNumber < facets.length) {
            const s = facets[facetNumber].symbol;
            if (s) {
                return s;
            }
        }
        return "";
    }

    function drawPentagon(symbol) {
        const margin = {top: 20, right: 20, bottom: 20, left: 20};
        const $cont = $("#pentagon");
        const width = $cont.width();
        const height = $cont.height();
        const hi = height-margin.top-margin.bottom;
        const wi = width-margin.left-margin.right;
        const rh = hi/(COS36+1);
        const rw = wi/(2*SIN72);
        const r = Math.min(rh, rw);
        const xm = margin.left+wi/2;
        const ym = margin.top+(hi-(r*(1+COS36)))/2;
        const poly = [
            [xm+(r*SIN36), ym],
            [xm+(r*SIN72), ym+(r*(COS36+COS72))],
            [xm, ym+(r*(COS36+1))],
            [xm-(r*SIN72), ym+(r*(COS36+COS72))],
            [xm-(r*SIN36), ym],
        ];
        const points = poly.map(function(p) {
            return p.join(",");
        }).join(" ");
        d3.select("#pentagon svg").remove();
        const svg = d3.select("#pentagon").append("svg")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g");
        svg.append("polygon")
                .attr("points",poly.map(function(p) {
                            return p.join(",");
                        }).join(" "))
                .attr("stroke", "black")
                .attr("stroke-width", 8)
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
