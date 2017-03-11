/**
 * Created by Juan on 10/03/2017.
 */

var express = require("express"),
    app = express(),
    bodyParser  = require("body-parser"),
    methodOverride = require("method-override"),
    xml2js = require('xml2js'),
    mongoose = require('mongoose'),
    path = require('path'),
    formidable = require('formidable'),
    fs = require('fs');

var json_preinscripciones = "";

//var XMLFILE = './data/Prescripcion.xml';
var XMLFILE = './data/Prescripcion_lite.xml';

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(methodOverride());
app.use(bodyParser({uploadDir:'./uploads'}));

var router = express.Router();

//Load file
fs.readFile(XMLFILE, 'utf8', function (err,data) {
  if (err) {
    return console.log(err);
  }
  data;
  console.log(XMLFILE + " preinscripciones leidas");
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



/**************************************************ROUTERS************************************************************/

/*********UPLOAD IMAGE*********/
router.post('/upload', function(req, res){
  var form = new formidable.IncomingForm();
  var filename = "";
  form.multiples = false;
  form.uploadDir = path.join(__dirname, '/uploads');
  form.on('file', function(field, file) {
    filename = file.name;
    fs.rename(file.path, path.join(form.uploadDir, file.name));
  });
  form.on('error', function(err) {
    console.log('An error has occured: \n' + err);
  });
  form.on('end', function() {
    res.end('{"url"="http://c3cce9a9.ngrok.io/getimage/'+ filename +'"}');
  });
  form.parse(req);
});

/*********GET IMAGE*********/
app.get('/getimage/:image', function (req, res) {
  var imagePath = req.params.image;
  res.sendfile(path.resolve('./uploads/' + imagePath));
});

router.get('/:code', function(req, res) {
  //var code = req.body.code; //post
  var code = req.params.code;

  res.send("code:" +  code);
});

router.get('/', function(req, res) {
  getByCode(66346);
  res.send("por favor, usa la api bien... MIAU! ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬");
});

/**************************************************WEB SERVER**********************************************************/
app.use(router);
app.listen(80, function() {
  console.log("Node server running on http://c3cce9a9.ngrok.io/");
});