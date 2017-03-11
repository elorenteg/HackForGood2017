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
    fs = require('fs'),
    pdfp = require('./uriPdfParser.js');

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
    //json_preinscripciones = JSON.stringify(result);
    json_preinscripciones = result;
    console.log("xml parseado en JSON");
  });
});

/*********GET PDF URL FROM CODE*********/
function getUrlByCode(code) {
  console.log("start filtering");
  var url;
  var array = json_preinscripciones.aemps_prescripcion.prescription;
  for (var i = 0; i < array.length; ++i) {
    if (array[i].nro_definitivo[0] == code) {
      url = array[i].url_prosp[0];
      break;
    }
  }
  return url;
}

/*********GET PDF URL FROM CONSTRAINTS*********/
function getUrlByConstraints(constraints) {
  //TODO: to implement
  console.log("start filtering");
  var constraints = {
    name: 'AMOXICILINA',
    dosis: 500,
    dosis_unit: 'mg',
    content: 100,
    content_type: 'viales',
    type: 'INYECTABLE'
  };
  var url;
  var input_array = json_preinscripciones.aemps_prescripcion.prescription;
  var out_array = [];
  for (var key in Object.keys(constraints)) {
    if (input_array.length == 0) break;
    //TODO: me quedo aqui, para cada key hay que mirar si coincide y ponerlo en el array de salida. cuando se quede vacio el array de salida es que no hay solucion. si solo queda 1 al final, hay solucion.
    if () {
      array[i].des_prese[0] == code
      url = array[i].url_prosp[0];
      break;
    }
    input_array = out_array;
  }
  return url;
}

/*********GET PROSPECTO*********/
function getProspecto(url) {
  console.log("procesando pdf");
  pdfp.parsePDF(url);
  var prospecto = pdfp.getSection(pdfp.QUE);
  return prospecto;
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

/*********GET PROSPECTO BY CODE*********/
router.get('/getprospecto/bycode/:code', function(req, res) {
  //var code = req.body.code; //para postspost
  var code = req.params.code;
  var url = getUrlByCode(code);
  var prospecto = getProspecto(url);
  res.send(prospecto);
});

/*********GET PROSPECTO BY constraints*********/
router.get('/getprospecto/byconstraints/:constraints', function(req, res) {
  var constraints = req.params.constraints;
  var url = getUrlByConstraints(constraints);
  var prospecto = getProspecto(url);
  res.send(prospecto);
});

router.get('/', function(req, res) {
  res.send("por favor, usa la api bien... MIAU! ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬ ¬¬");
});

/**************************************************WEB SERVER**********************************************************/
app.use(router);
app.listen(80, function() {
  console.log("Node server running on http://c3cce9a9.ngrok.io/");
});