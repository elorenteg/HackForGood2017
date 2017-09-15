package com.hackforgood.dev.hackforgood2017.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Medicine implements Serializable {
    private final String TAG = Medicine.class.getSimpleName();

    private int code = -1;
    private String name = "";           // ibuprofeno
    private int dosis = -1;             // 600
    private String dosisUnit = "";      // mg
    private int content = -1;           // 40
    private String contentType = "";    // comprimidos
    private String type = "";           // oral
    private String que = "";
    private String como = "";
    private String antes = "";
    private String efectos = "";
    private String conservacion = "";
    private String informacion = "";
    private String imageURL = "";

    public static Medicine deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Medicine) is.readObject();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDosis() {
        return dosis;
    }

    public void setDosis(int dosis) {
        this.dosis = dosis;
    }

    public String getDosisUnit() {
        return dosisUnit;
    }

    public void setDosisUnit(String dosisUnit) {
        this.dosisUnit = dosisUnit;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQue() {
        return que;
    }

    public void setQue(String que) {
        this.que = que;
    }

    public String getComo() {
        return como;
    }

    public void setComo(String como) {
        this.como = como;
    }

    public String getAntes() {
        return antes;
    }

    public void setAntes(String antes) {
        this.antes = antes;
    }

    public String getEfectos() {
        return efectos;
    }

    public void setEfectos(String efectos) {
        this.efectos = efectos;
    }

    public String getConservacion() {
        return conservacion;
    }

    public void setConservacion(String conservacion) {
        this.conservacion = conservacion;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void parseInfo(String parsedText) {
        String pattern;
        Pattern p;
        Matcher m;

        // Code
        String numberText = parsedText.replaceAll("[a-zA-Z]", "");
        numberText = numberText.replaceAll("\\s{2,}", " ").trim();
        numberText = numberText.replaceAll("\\.", "").trim();
        numberText = numberText.replaceAll(",", "").trim();
        numberText = numberText.replaceAll("-", "").trim();
        numberText = numberText.replaceAll("\\b\\w{1,5}\\b\\s?", "").trim();
        if (numberText.length() >= 7) {
            numberText = numberText.substring(0, 7);
            String securityNum = numberText.substring(numberText.length() - 1);
            numberText = numberText.substring(0, numberText.length() - 1);
            try {
                int code = Integer.parseInt(numberText);
                setCode(code);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //int securityCode = Integer.parseInt(securityNum);
            //if (codeIsCorrect(code, securityCode)) {
            //setCode(code);
            //}
            //else Log.e(TAG, "Code is not correct");
        }

        // Dosis
        if (parsedText.contains("mg") || parsedText.contains("microgramos")) {
            setDosisUnit("mg");
        } else if (parsedText.contains("g") || parsedText.contains("gramos")) {
            setDosisUnit("g");
        }

        if (!"".equals(getDosisUnit())) {
            pattern = "(\\d+)([^\\d])" + getDosisUnit();
            p = Pattern.compile(pattern);
            m = p.matcher(parsedText);
            if (m.find()) {
                String dosis = m.group().replace(getDosisUnit(), "").trim();
                setDosis(Integer.parseInt(dosis));
                parsedText = parsedText.replace(getDosisUnit(), "");
            }
        }

        // Content
        if (parsedText.contains("comprimidos")) {
            setContentType("comprimidos");
            pattern = "(\\d+)([^\\d])" + "comprimidos";
            p = Pattern.compile(pattern);
            m = p.matcher(parsedText);
            if (m.find()) {
                String content = m.group().replace("comprimidos", "").trim();
                setContent(Integer.parseInt(content));
            }
        }

        parsedText = parsedText.replaceAll("comprimidos", "");
        parsedText = parsedText.replaceAll("cornprimidos", "");
        parsedText = parsedText.replaceAll("recubiertos", "");
        parsedText = parsedText.replaceAll("pelicula", "");
        parsedText = parsedText.replaceAll(" EFG ", "");
        parsedText = parsedText.replaceAll("\\d", "");
        parsedText = parsedText.replaceAll("\n", " ");

        parsedText = parsedText.replaceAll("cuerpo", "");
        parsedText = parsedText.replaceAll("dolor", "");
        parsedText = parsedText.replaceAll("muscular", "");
        parsedText = parsedText.replaceAll("cabeza", "");
        parsedText = parsedText.replaceAll("anti-inflamatorio", "");
        parsedText = parsedText.replaceAll("película", "");
        parsedText = parsedText.replaceAll("solucion", "");
        parsedText = parsedText.replaceAll("microgramos", "");
        parsedText = parsedText.replaceAll("peucu", "");
        parsedText = parsedText.replaceAll(" /", " ");
        parsedText = parsedText.replaceAll("/ ", " ");
        parsedText = parsedText.replaceAll("'", "");
        parsedText = parsedText.replaceAll(" +", " ");
        parsedText = parsedText.replaceAll("\\[", "");
        parsedText = parsedText.replaceAll("\\]", "");
        parsedText = parsedText.replaceAll("\\.", " ");
        parsedText = parsedText.replaceAll(",", " ");

        parsedText = parsedText.replaceAll("/", " / ");
        parsedText = parsedText.replaceAll(" +", " ");

        parsedText = parsedText.toLowerCase();

        if (parsedText.contains("oral")) {
            setType("oral");
            parsedText = parsedText.replace("oral", "");
        } else if (parsedText.contains("nasal")) {
            setType("nasal");
        } else if (parsedText.contains("ótica") || parsedText.contains("otica") || parsedText.contains("ötica")) {
            setType("ótica");
        }

        parsedText = parsedText.replaceAll("\\b\\w{1,3}\\b\\s?", "");
        parsedText = parsedText.replaceAll("\\s{2,}", " ").trim();

        // Name
        parsedText = parsedText.replaceAll(",", " ");
        parsedText = new LinkedHashSet<>(Arrays.asList(parsedText.split(" "))).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ");
        setName(parsedText);
    }

    private boolean codeIsCorrect(int code, int securityCode) {
        String numText = String.valueOf(code);

        int count = 0;
        for (int i = 0; i < numText.length(); ++i) {
            if (i % 2 == 0) count += 3 * Integer.parseInt(numText.substring(i, i + 1));
            else count += Integer.parseInt(numText.substring(i, i + 1));
        }
        count += 27;

        int ten = (int) (Math.rint((double) count / 10) * 10);
        if (ten < count) ten += 10;
        int calculatedSecurityCode = ten - count;

        return calculatedSecurityCode == securityCode;
    }

    @Override
    public String toString() {
        String str = "";
        if (code >= 0) str += "Code: " + code + "\n";
        if (!name.equals("")) str += "Name: " + name + "\n";
        if (dosis >= 0) str += "Dosis: " + dosis + " " + dosisUnit + "\n";
        if (content >= 0) str += "Content: " + content + " " + contentType + "\n";
        if (!type.equals("")) str += "Type: " + type + "\n";
        if (!que.equals("")) str += "Que: " + que + "\n";
        if (!como.equals("")) str += "Como: " + como + "\n";
        if (!antes.equals("")) str += "Antes: " + antes + "\n";
        if (!efectos.equals("")) str += "Efectos: " + efectos + "\n";
        if (!conservacion.equals("")) str += "Conservacion: " + conservacion + "\n";
        if (!informacion.equals("")) str += "Informacion: " + informacion + "\n";
        if (!imageURL.equals("")) str += "ImageURL: " + imageURL + "\n";
        return str;
    }

    public boolean hasACode() {
        return getCode() >= 0;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bs);
        os.writeObject(this);
        os.close();
        return bs.toByteArray();
    }
}
