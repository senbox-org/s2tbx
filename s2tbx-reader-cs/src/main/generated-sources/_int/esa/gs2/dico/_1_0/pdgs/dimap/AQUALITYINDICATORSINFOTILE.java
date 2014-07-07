//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_QUALITY_INDICATORS_INFO_TILE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_QUALITY_INDICATORS_INFO_TILE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Image_Content_QI" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GRANULE_COMMON_IMG_CONTENT_QI"/>
 *         &lt;element name="Pixel_Level_QI" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_MASK_LIST"/>
 *         &lt;element name="PVI_FILENAME" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}PVI_ID"/>
 *       &lt;/sequence>
 *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_QUALITY_INDICATORS_INFO_TILE", propOrder = {
    "imageContentQI",
    "pixelLevelQI",
    "pvifilename"
})
public class AQUALITYINDICATORSINFOTILE {

    @XmlElement(name = "Image_Content_QI", required = true)
    protected AGRANULECOMMONIMGCONTENTQI imageContentQI;
    @XmlElement(name = "Pixel_Level_QI", required = true)
    protected AMASKLIST pixelLevelQI;
    @XmlElement(name = "PVI_FILENAME", required = true)
    protected String pvifilename;
    @XmlAttribute(name = "metadataLevel")
    protected String metadataLevel;

    /**
     * Obtient la valeur de la propriété imageContentQI.
     * 
     * @return
     *     possible object is
     *     {@link AGRANULECOMMONIMGCONTENTQI }
     *     
     */
    public AGRANULECOMMONIMGCONTENTQI getImageContentQI() {
        return imageContentQI;
    }

    /**
     * Définit la valeur de la propriété imageContentQI.
     * 
     * @param value
     *     allowed object is
     *     {@link AGRANULECOMMONIMGCONTENTQI }
     *     
     */
    public void setImageContentQI(AGRANULECOMMONIMGCONTENTQI value) {
        this.imageContentQI = value;
    }

    /**
     * Obtient la valeur de la propriété pixelLevelQI.
     * 
     * @return
     *     possible object is
     *     {@link AMASKLIST }
     *     
     */
    public AMASKLIST getPixelLevelQI() {
        return pixelLevelQI;
    }

    /**
     * Définit la valeur de la propriété pixelLevelQI.
     * 
     * @param value
     *     allowed object is
     *     {@link AMASKLIST }
     *     
     */
    public void setPixelLevelQI(AMASKLIST value) {
        this.pixelLevelQI = value;
    }

    /**
     * Obtient la valeur de la propriété pvifilename.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPVIFILENAME() {
        return pvifilename;
    }

    /**
     * Définit la valeur de la propriété pvifilename.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPVIFILENAME(String value) {
        this.pvifilename = value;
    }

    /**
     * Obtient la valeur de la propriété metadataLevel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetadataLevel() {
        if (metadataLevel == null) {
            return "Standard";
        } else {
            return metadataLevel;
        }
    }

    /**
     * Définit la valeur de la propriété metadataLevel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetadataLevel(String value) {
        this.metadataLevel = value;
    }

}
