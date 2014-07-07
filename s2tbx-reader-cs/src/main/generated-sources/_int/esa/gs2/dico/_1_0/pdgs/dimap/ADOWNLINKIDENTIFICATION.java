//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_DOWNLINK_IDENTIFICATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DOWNLINK_IDENTIFICATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RECEPTION_STATION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="CGS1"/>
 *               &lt;enumeration value="CGS2"/>
 *               &lt;enumeration value="CGS3"/>
 *               &lt;enumeration value="CGS4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DOWNLINK_ORBIT_NUMBER" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_DOWNLINK_IDENTIFICATION", propOrder = {
    "receptionstation",
    "downlinkorbitnumber"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGENERALINFODS.DownlinkInfo.class
})
public class ADOWNLINKIDENTIFICATION {

    @XmlElement(name = "RECEPTION_STATION", required = true)
    protected String receptionstation;
    @XmlElement(name = "DOWNLINK_ORBIT_NUMBER")
    protected int downlinkorbitnumber;

    /**
     * Obtient la valeur de la propriété receptionstation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRECEPTIONSTATION() {
        return receptionstation;
    }

    /**
     * Définit la valeur de la propriété receptionstation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRECEPTIONSTATION(String value) {
        this.receptionstation = value;
    }

    /**
     * Obtient la valeur de la propriété downlinkorbitnumber.
     * 
     */
    public int getDOWNLINKORBITNUMBER() {
        return downlinkorbitnumber;
    }

    /**
     * Définit la valeur de la propriété downlinkorbitnumber.
     * 
     */
    public void setDOWNLINKORBITNUMBER(int value) {
        this.downlinkorbitnumber = value;
    }

}
