var fs = require('fs');
var http = require('https');
var PDFParser = require("pdf2json");
var parser = new PDFParser(this,1);
var path = require('path');

const QUE = 0;
const ANTES = 1;
const COMO = 2;
const EFECTOS = 3;
const CONSERVACION = 4;
const INFORMACION = 5;

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


exports.parseAndSave2Txt = function() {
    var filepath = path.join(__dirname, "/input/66337_p.pdf");

    parser.on("pdfParser_dataError", errData => console.error(errData.parserError));
    parser.on("pdfParser_dataReady", pdfData => {
        //rawtext = parser.getRawTextContent();
        //console.log(rawtext);
        var dest_filename = path.join(__dirname, "/parsed/", path.basename(filepath, '.pdf')+".txt");
        fs.writeFile(dest_filename, parser.getRawTextContent());
    });

    parser.loadPDF(filepath);
}

function clearText(rawtext) {
	// Limpieza Saltos y Números de página

	var aux = rawtext.replace(/^.*-----.*$/mg, "");
	var clearedtext = aux.replace(/^\d+ de \d+ *$/mg, "");
    return clearedtext;
}

function getRawSection(data, startPattern, endPattern) {
	// Obtención de una sección del prospecto
	var aux = data.split(startPattern);
	var section = aux[aux.length-1].split(endPattern)[0];
	return clearText(section);
}

exports.getAllSections = function (data) {
    var all = {};
    all['que'] = getSectionInternal(data,QUE);
    all['antes'] = getSectionInternal(data,ANTES);
    all['como'] = getSectionInternal(data,COMO);
    all['efectos'] = getSectionInternal(data,EFECTOS);
    all['conservacion'] = getSectionInternal(data,CONSERVACION);
    all['informacion'] = getSectionInternal(data,INFORMACION);
    return all;
};

exports.parsePDF = function (url) {
    let fs = require('fs'),
        PDFParser = require("pdf2json");
    let parser = new PDFParser();

    parser.on("pdfParser_dataError", errData => console.error(errData.parserError));
    parser.on("pdfParser_dataReady", pdfData => {
        rawtext = parser.getRawTextContent();
        //TODO: por hacer callback de mierda
    });

    var urlParser = require("url");
    var path = require("path");
    var parsed = urlParser.parse(url);
    console.log(path.basename(parsed.pathname));

    parser.loadPDF("./input/"+path.basename(parsed.pathname));
};
/*
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
*/
function getSectionInternal(data,sectionType) {
    var section = {};

    if (sectionType == QUE) section = getRawSection(data, /^1\..*\n?/m, /^2\..*\n?/m);// Sección: Qué es
    else if (sectionType == ANTES) section = getRawSection(data, /^2\..*\n?/m, /^3\..*\n?/m);// Sección: Antes de tomar
    else if (sectionType == COMO) section = getRawSection(data, /^3\..*\n?/m, /^4\..*\n?/m);// Sección: Cómo Tomar
    else if (sectionType == EFECTOS) section = getRawSection(data, /^4\..*\n?/m, /^5\..*\n?/m);// Sección: Posibles efectos adversos
    else if (sectionType == CONSERVACION) section = getRawSection(data, /^5\..*\n?/m, /^6\..*\n?/m);// Sección: Conservación
    else if (sectionType == INFORMACION) section = getRawSection(data, /^6\..*\n?/m, /$/);// Sección: Información adicional
    else return console.log("sectionType not found");

    return section;
};