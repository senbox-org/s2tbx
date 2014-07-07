//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.base;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour SATELLITE_INDEPENDANT.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="SATELLITE_INDEPENDANT">
 *   &lt;restriction base="{http://gs2.esa.int/DICO/1.0/PDGS/base/}SATELLITE">
 *     &lt;enumeration value="None"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SATELLITE_INDEPENDANT")
@XmlEnum
public enum SATELLITEINDEPENDANT {

    @XmlEnumValue("None")
    NONE("None");
    private final String value;

    SATELLITEINDEPENDANT(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SATELLITEINDEPENDANT fromValue(String v) {
        for (SATELLITEINDEPENDANT c: SATELLITEINDEPENDANT.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
