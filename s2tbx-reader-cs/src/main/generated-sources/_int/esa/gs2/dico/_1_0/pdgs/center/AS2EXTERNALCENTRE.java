//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.center;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_S2_EXTERNAL_CENTRE.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_S2_EXTERNAL_CENTRE">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EDRS"/>
 *     &lt;enumeration value="FOS"/>
 *     &lt;enumeration value="FOCC"/>
 *     &lt;enumeration value="OPOD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_S2_EXTERNAL_CENTRE", namespace = "http://gs2.esa.int/DICO/1.0/PDGS/center/")
@XmlEnum
public enum AS2EXTERNALCENTRE {

    EDRS,
    FOS,
    FOCC,
    OPOD;

    public String value() {
        return name();
    }

    public static AS2EXTERNALCENTRE fromValue(String v) {
        return valueOf(v);
    }

}
