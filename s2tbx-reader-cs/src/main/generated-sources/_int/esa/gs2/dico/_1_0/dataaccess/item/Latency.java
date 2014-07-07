//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.dataaccess.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Latency.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="Latency">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="On-line"/>
 *     &lt;enumeration value="Off-line"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Latency", namespace = "http://gs2.esa.int/DICO/1.0/DataAccess/item/")
@XmlEnum
public enum Latency {

    @XmlEnumValue("On-line")
    ON_LINE("On-line"),
    @XmlEnumValue("Off-line")
    OFF_LINE("Off-line");
    private final String value;

    Latency(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Latency fromValue(String v) {
        for (Latency c: Latency.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
