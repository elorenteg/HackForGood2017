var fs = require('fs');
var http = require('https');
//var PDFParser = require("pdf2json");
//var parser = new PDFParser();

const QUE = 0;
const ANTES = 1;
const COMO = 2;
const EFECTOS = 3;
const CONSERVACION = 4;
const INFORMACION = 5;

var rawtext = "";

var exports = module.exports = {
	QUE: QUE,
	ANTES: ANTES,
	COMO: COMO,
	EFECTOS: EFECTOS,
	CONSERVACION: CONSERVACION,
	INFORMACION: INFORMACION
};

/*
parser.on("pdfParser_dataError", errData => console.error(errData.parserError));
parser.on("pdfParser_dataReady", pdfData => {
	// Prospecto
	rawtext = parser.getRawTextContent();
	// Export File
	//fs.writeFile("output/"+ID+"_p_raw.txt", rawtext);

	// Export File
	//fs.writeFile("output/"+ID+"_p_que.txt", sectionque);
	//fs.writeFile("output/"+ID+"_p_antes.txt", sectionantes);
	//fs.writeFile("output/"+ID+"_p_como.txt", sectioncomo);
	//fs.writeFile("output/"+ID+"_p_efectos.txt", sectionefectos);
	//fs.writeFile("output/"+ID+"_p_conservacion.txt", sectionconservacion);
	//fs.writeFile("output/"+ID+"_p_informacion.txt", sectioninformacion);
});
*/

function clearText(rawtext) {
	// Limpieza Saltos y Números de página 
	var aux = rawtext.replace(/^.*-----.*$/mg, "");
	var clearedtext = aux.replace(/^\d+ de \d+ *$/mg, "");
    return clearedtext;
}

function getRawSection(rawtext, startPattern, endPattern) {
	// Obtención de una sección del prospecto
	var aux = rawtext.split(startPattern)
	var section = aux[aux.length-1].split(endPattern)[0];
	return clearText(section);
}

exports.parsePDF = function (url) {
    let fs = require('fs'),
        PDFParser = require("pdf2json");
    let parser = new PDFParser();

    parser.on("pdfParser_dataError", errData => console.error(errData.parserError));
    parser.on("pdfParser_dataReady", pdfData => {
        rawtext = parser.getRawTextContent();
    });

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
	var file = fs.createWriteStream("./input/temp.pdf");
    var req = https.request(url, function(res) {
        console.log("statusCode: ", res.statusCode);
        console.log("headers: ", res.headers);
        res.on('data', function(d) {
            file.write(d);
            parser.loadPDF("./input/temp.pdf");
        });
    });
    req.end();
    req.on('error', function(e) {
        console.error(e);
    });
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
};

exports.getSection = function(sectionType) {
	var section = {};

	if (sectionType == QUE) section = getRawSection(rawtext, /^1\..*\n?/m, /^2\..*\n?/m);// Sección: Qué es
	else if (sectionType == ANTES) section = getRawSection(rawtext, /^2\..*\n?/m, /^3\..*\n?/m);// Sección: Antes de tomar
	else if (sectionType == COMO) section = getRawSection(rawtext, /^3\..*\n?/m, /^4\..*\n?/m);// Sección: Cómo Tomar
	else if (sectionType == EFECTOS) section = getRawSection(rawtext, /^4\..*\n?/m, /^5\..*\n?/m);// Sección: Posibles efectos adversos
	else if (sectionType == CONSERVACION) section = getRawSection(rawtext, /^5\..*\n?/m, /^6\..*\n?/m);// Sección: Conservación
	else if (sectionType == INFORMACION) section = getRawSection(rawtext, /^6\..*\n?/m, /$/);// Sección: Información adicional
	else return console.log("sectionType not found");

	return section;
};