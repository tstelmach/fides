$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("file:src/test/java/features/Statements.feature");
formatter.feature({
  "description": "",
  "name": "Statements",
  "keyword": "Feature"
});
formatter.scenario({
  "description": "",
  "keyword": "Scenario",
  "name": "MT942"
});
formatter.step({
  "keyword": "Given ",
  "name": "Create MT942"
});
formatter.match({
  "location": "Statements.create_mt942()"
});
formatter.result({
  "status": "passed"
});
});