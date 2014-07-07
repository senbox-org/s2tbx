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
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHUNITATTR;


/**
 * <p>Classe Java pour A_LOCAL_RESIDUAL complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_LOCAL_RESIDUAL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ZONE_NUMBER" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="X_CENTER_POSITION" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_UNIT_ATTR"/>
 *         &lt;element name="Y_CENTER_POSITION" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_UNIT_ATTR"/>
 *         &lt;element name="X_MEAN" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_UNIT_ATTR"/>
 *         &lt;element name="Y_MEAN" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_UNIT_ATTR"/>
 *         &lt;element name="X_STDV" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_UNIT_ATTR"/>
 *         &lt;element name="Y_STDV" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_UNIT_ATTR"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_LOCAL_RESIDUAL", propOrder = {
    "zonenumber",
    "xcenterposition",
    "ycenterposition",
    "xmean",
    "ymean",
    "xstdv",
    "ystdv"
})
public class ALOCALRESIDUAL {

    @XmlElement(name = "ZONE_NUMBER")
    protected int zonenumber;
    @XmlElement(name = "X_CENTER_POSITION", required = true)
    protected ADOUBLEWITHUNITATTR xcenterposition;
    @XmlElement(name = "Y_CENTER_POSITION", required = true)
    protected ADOUBLEWITHUNITATTR ycenterposition;
    @XmlElement(name = "X_MEAN", required = true)
    protected ADOUBLEWITHUNITATTR xmean;
    @XmlElement(name = "Y_MEAN", required = true)
    protected ADOUBLEWITHUNITATTR ymean;
    @XmlElement(name = "X_STDV", required = true)
    protected ADOUBLEWITHUNITATTR xstdv;
    @XmlElement(name = "Y_STDV", required = true)
    protected ADOUBLEWITHUNITATTR ystdv;

    /**
     * Obtient la valeur de la propriété zonenumber.
     * 
     */
    public int getZONENUMBER() {
        return zonenumber;
    }

    /**
     * Définit la valeur de la propriété zonenumber.
     * 
     */
    public void setZONENUMBER(int value) {
        this.zonenumber = value;
    }

    /**
     * Obtient la valeur de la propriété xcenterposition.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public ADOUBLEWITHUNITATTR getXCENTERPOSITION() {
        return xcenterposition;
    }

    /**
     * Définit la valeur de la propriété xcenterposition.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public void setXCENTERPOSITION(ADOUBLEWITHUNITATTR value) {
        this.xcenterposition = value;
    }

    /**
     * Obtient la valeur de la propriété ycenterposition.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public ADOUBLEWITHUNITATTR getYCENTERPOSITION() {
        return ycenterposition;
    }

    /**
     * Définit la valeur de la propriété ycenterposition.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public void setYCENTERPOSITION(ADOUBLEWITHUNITATTR value) {
        this.ycenterposition = value;
    }

    /**
     * Obtient la valeur de la propriété xmean.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public ADOUBLEWITHUNITATTR getXMEAN() {
        return xmean;
    }

    /**
     * Définit la valeur de la propriété xmean.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public void setXMEAN(ADOUBLEWITHUNITATTR value) {
        this.xmean = value;
    }

    /**
     * Obtient la valeur de la propriété ymean.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public ADOUBLEWITHUNITATTR getYMEAN() {
        return ymean;
    }

    /**
     * Définit la valeur de la propriété ymean.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public void setYMEAN(ADOUBLEWITHUNITATTR value) {
        this.ymean = value;
    }

    /**
     * Obtient la valeur de la propriété xstdv.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public ADOUBLEWITHUNITATTR getXSTDV() {
        return xstdv;
    }

    /**
     * Définit la valeur de la propriété xstdv.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public void setXSTDV(ADOUBLEWITHUNITATTR value) {
        this.xstdv = value;
    }

    /**
     * Obtient la valeur de la propriété ystdv.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public ADOUBLEWITHUNITATTR getYSTDV() {
        return ystdv;
    }

    /**
     * Définit la valeur de la propriété ystdv.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHUNITATTR }
     *     
     */
    public void setYSTDV(ADOUBLEWITHUNITATTR value) {
        this.ystdv = value;
    }

}
