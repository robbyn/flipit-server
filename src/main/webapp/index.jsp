<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>FlipIt API</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="css/bulma.css">
        <link rel="stylesheet" href="css/index.css">
<script src="https://kit.fontawesome.com/1bebc5f4f2.js" crossorigin="anonymous"></script>
<!-- script defer src="https://use.fontawesome.com/releases/v5.3.1/js/all.js"></script -->
    </head>
    <body>
        <section class="section">
          <div class="container">
            <h1 class="title">
              Facettes
            </h1>
            <form id="facetForm">
                <div class="facets"></div>
                <input type="submit" value="Enregistrer" />
            </form>
          </div>
        </section>
        <section class="section">
          <div class="container">
            <h1 class="title">
              Activités
            </h1>
            <div class="activities"></div>
          </div>
        </section>
        <script type="text/x-handlebars-template" id="facetTable">
            <table class="table">
            <tbody>
            {{#each facets}}
            <tr>
            <td><input type="number" name="ix{{@index}}" value="{{@index}}" maxlength="2" /></td>
            <td><span class="fas">{{symbol}}</span></td>
            <td>{{label}}</td>
            </tr>
            {{/each}}
            </tbody>
            </table>
        </script>
        <script type="text/x-handlebars-template" id="activityTable">
            <table class="table">
            <tbody>
            {{#each activities}}
            <tr>
                <td>{{startTime}}</td>
                {{#if (isNumber facetNumber)}}
                {{#with (itemAt ../user.facets facetNumber)}}
                <td><span class="fas">{{symbol}}</span></td><td>{{label}}</td>
                {{/with}}
                {{else}}
                <td></td><td></td>
                {{/if}}
                <td>{{comment}}</td>
            </tr>
            {{/each}}
            </tbody>
            </table>
        </script>
        <script src="js/jquery.js"></script>
        <script src="js/handlebars.js"></script>
        <script src="js/index.js"></script>
    </body>
</html>