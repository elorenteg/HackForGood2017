/**
 * Created by Juan on 10/03/2017.
 */

var express = require("express"),
  app = express(),
  bodyParser  = require("body-parser"),
  methodOverride = require("method-override"),
  mongoose = require('mongoose');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(methodOverride());

var xml2js = require('xml2js');
var fs = require('fs');
var json_preinscripciones = "";

//Load file
fs.readFile('./data/Prescripcion.xml', 'utf8', function (err,data) {
  if (err) {
    return console.log(err);
  }
  data;
  console.log("preinscripciones leidas");
  //parse file into Json
  xml2js.parseString(data, function (err, result) {
    if (err) {
      return console.log(err);
    }
    json_preinscripciones = JSON.stringify(result);
    console.log("xml parseado en JSON");
  });
});



//get by code function
function getByCode(code) {
  console.log(json_preinscripciones);
}

var router = express.Router();

router.get('/:code', function(req, res) {
  //var code = req.body.code; //post
  var code = req.params.code;


  res.send("code:" +  code);
});

router.get('/', function(req, res) {
  getByCode(66346);
  res.send("por favor, usa la api bien... MIAU");
});

app.use(router);

app.listen(80, function() {
  console.log("Node server running on http://c3cce9a9.ngrok.io/");
});