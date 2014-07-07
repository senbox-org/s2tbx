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
 * <p>Classe Java pour A_S2_ShortCode_ACQ_CENTER_Composite_Type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="A_S2_ShortCode_ACQ_CENTER_Composite_Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value=".*SGS"/>
 *     &lt;enumeration value=".*MPS"/>
 *     &lt;enumeration value=".*MTI"/>
 *     &lt;enumeration value=".*PDB"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "A_S2_ShortCode_ACQ_CENTER_Composite_Type", namespace = "http://gs2.esa.int/DICO/1.0/PDGS/center/")
@XmlEnum
public enum AS2ShortCodeACQCENTERCompositeType {

    @XmlEnumValue(".*SGS")
    SGS(".*SGS"),
    @XmlEnumValue(".*MPS")
    MPS(".*MPS"),
    @XmlEnumValue(".*MTI")
    MTI(".*MTI"),
    @XmlEnumValue(".*PDB")
    PDB(".*PDB");
    private final String value;

    AS2ShortCodeACQCENTERCompositeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AS2ShortCodeACQCENTERCompositeType fromValue(String v) {
        for (AS2ShortCodeACQCENTERCompositeType c: AS2ShortCodeACQCENTERCompositeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
