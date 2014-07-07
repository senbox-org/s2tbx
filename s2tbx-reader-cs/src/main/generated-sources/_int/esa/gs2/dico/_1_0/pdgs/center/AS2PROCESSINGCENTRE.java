//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.center;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_S2_PROCESSING_CENTRE.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_S2_PROCESSING_CENTRE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SVLB"/>
 *     &lt;enumeration value="MASP"/>
 *     &lt;enumeration value="MATA"/>
 *     &lt;enumeration value="CGS1"/>
 *     &lt;enumeration value="CGS2"/>
 *     &lt;enumeration value="CGS3"/>
 *     &lt;enumeration value="CGS4"/>
 *     &lt;enumeration value="PAC1"/>
 *     &lt;enumeration value="MPCC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_S2_PROCESSING_CENTRE", namespace = "http://gs2.esa.int/DICO/1.0/PDGS/center/")
@XmlEnum
public enum AS2PROCESSINGCENTRE {

    SVLB("SVLB"),
    MASP("MASP"),
    MATA("MATA"),
    @XmlEnumValue("CGS1")
    CGS_1("CGS1"),
    @XmlEnumValue("CGS2")
    CGS_2("CGS2"),
    @XmlEnumValue("CGS3")
    CGS_3("CGS3"),
    @XmlEnumValue("CGS4")
    CGS_4("CGS4"),
    @XmlEnumValue("PAC1")
    PAC_1("PAC1"),
    MPCC("MPCC");
    private final String value;

    AS2PROCESSINGCENTRE(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AS2PROCESSINGCENTRE fromValue(String v) {
        for (AS2PROCESSINGCENTRE c: AS2PROCESSINGCENTRE.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
