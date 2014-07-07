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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_BAND_DISPLAY_ORDER complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_BAND_DISPLAY_ORDER">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RED_CHANNEL" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
 *         &lt;element name="GREEN_CHANNEL" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
 *         &lt;element name="BLUE_CHANNEL" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_BAND_DISPLAY_ORDER", propOrder = {
    "redchannel",
    "greenchannel",
    "bluechannel"
})
public class ABANDDISPLAYORDER {

    @XmlElement(name = "RED_CHANNEL", required = true)
    protected String redchannel;
    @XmlElement(name = "GREEN_CHANNEL", required = true)
    protected String greenchannel;
    @XmlElement(name = "BLUE_CHANNEL", required = true)
    protected String bluechannel;

    /**
     * Obtient la valeur de la propriété redchannel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREDCHANNEL() {
        return redchannel;
    }

    /**
     * Définit la valeur de la propriété redchannel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREDCHANNEL(String value) {
        this.redchannel = value;
    }

    /**
     * Obtient la valeur de la propriété greenchannel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGREENCHANNEL() {
        return greenchannel;
    }

    /**
     * Définit la valeur de la propriété greenchannel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGREENCHANNEL(String value) {
        this.greenchannel = value;
    }

    /**
     * Obtient la valeur de la propriété bluechannel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBLUECHANNEL() {
        return bluechannel;
    }

    /**
     * Définit la valeur de la propriété bluechannel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBLUECHANNEL(String value) {
        this.bluechannel = value;
    }

}
