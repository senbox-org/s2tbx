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
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHDEGUNITATTR;


/**
 * Zenith and azimuth angles 
 * 
 * <p>Classe Java pour A_ZENITH_AND_AZIMUTH_ANGLES complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_ZENITH_AND_AZIMUTH_ANGLES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ZENITH_ANGLE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
 *         &lt;element name="AZIMUTH_ANGLE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_DEG_UNIT_ATTR"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_ZENITH_AND_AZIMUTH_ANGLES", propOrder = {
    "zenithangle",
    "azimuthangle"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AL1CANGLES.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGRANULESRESAMPLE.Tile.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle.class
})
public class AZENITHANDAZIMUTHANGLES {

    @XmlElement(name = "ZENITH_ANGLE", required = true)
    protected ADOUBLEWITHDEGUNITATTR zenithangle;
    @XmlElement(name = "AZIMUTH_ANGLE", required = true)
    protected ADOUBLEWITHDEGUNITATTR azimuthangle;

    /**
     * Obtient la valeur de la propriété zenithangle.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public ADOUBLEWITHDEGUNITATTR getZENITHANGLE() {
        return zenithangle;
    }

    /**
     * Définit la valeur de la propriété zenithangle.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public void setZENITHANGLE(ADOUBLEWITHDEGUNITATTR value) {
        this.zenithangle = value;
    }

    /**
     * Obtient la valeur de la propriété azimuthangle.
     * 
     * @return
     *     possible object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public ADOUBLEWITHDEGUNITATTR getAZIMUTHANGLE() {
        return azimuthangle;
    }

    /**
     * Définit la valeur de la propriété azimuthangle.
     * 
     * @param value
     *     allowed object is
     *     {@link ADOUBLEWITHDEGUNITATTR }
     *     
     */
    public void setAZIMUTHANGLE(ADOUBLEWITHDEGUNITATTR value) {
        this.azimuthangle = value;
    }

}
