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
 * <p>Classe Java pour A_QUALITY_INDICATORS_INFO_GRL0 complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_QUALITY_INDICATORS_INFO_GRL0">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Image_Content_QI">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Common_IMG_QI" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GRANULE_COMMON_IMG_CONTENT_QI"/>
 *                   &lt;element name="Source_Packet_description" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SOURCE_PACKET_DESCRIPTION_GRL0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_QUALITY_INDICATORS_INFO_GRL0", propOrder = {
    "imageContentQI"
})
public class AQUALITYINDICATORSINFOGRL0 {

    @XmlElement(name = "Image_Content_QI", required = true)
    protected AQUALITYINDICATORSINFOGRL0 .ImageContentQI imageContentQI;

    /**
     * Obtient la valeur de la propriété imageContentQI.
     * 
     * @return
     *     possible object is
     *     {@link AQUALITYINDICATORSINFOGRL0 .ImageContentQI }
     *     
     */
    public AQUALITYINDICATORSINFOGRL0 .ImageContentQI getImageContentQI() {
        return imageContentQI;
    }

    /**
     * Définit la valeur de la propriété imageContentQI.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUALITYINDICATORSINFOGRL0 .ImageContentQI }
     *     
     */
    public void setImageContentQI(AQUALITYINDICATORSINFOGRL0 .ImageContentQI value) {
        this.imageContentQI = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Common_IMG_QI" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GRANULE_COMMON_IMG_CONTENT_QI"/>
     *         &lt;element name="Source_Packet_description" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SOURCE_PACKET_DESCRIPTION_GRL0"/>
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
    @XmlType(name = "", propOrder = {
        "commonIMGQI",
        "sourcePacketDescription"
    })
    public static class ImageContentQI {

        @XmlElement(name = "Common_IMG_QI", required = true)
        protected AGRANULECOMMONIMGCONTENTQI commonIMGQI;
        @XmlElement(name = "Source_Packet_description", required = true)
        protected ASOURCEPACKETDESCRIPTIONGRL0 sourcePacketDescription;
        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

        /**
         * Obtient la valeur de la propriété commonIMGQI.
         * 
         * @return
         *     possible object is
         *     {@link AGRANULECOMMONIMGCONTENTQI }
         *     
         */
        public AGRANULECOMMONIMGCONTENTQI getCommonIMGQI() {
            return commonIMGQI;
        }

        /**
         * Définit la valeur de la propriété commonIMGQI.
         * 
         * @param value
         *     allowed object is
         *     {@link AGRANULECOMMONIMGCONTENTQI }
         *     
         */
        public void setCommonIMGQI(AGRANULECOMMONIMGCONTENTQI value) {
            this.commonIMGQI = value;
        }

        /**
         * Obtient la valeur de la propriété sourcePacketDescription.
         * 
         * @return
         *     possible object is
         *     {@link ASOURCEPACKETDESCRIPTIONGRL0 }
         *     
         */
        public ASOURCEPACKETDESCRIPTIONGRL0 getSourcePacketDescription() {
            return sourcePacketDescription;
        }

        /**
         * Définit la valeur de la propriété sourcePacketDescription.
         * 
         * @param value
         *     allowed object is
         *     {@link ASOURCEPACKETDESCRIPTIONGRL0 }
         *     
         */
        public void setSourcePacketDescription(ASOURCEPACKETDESCRIPTIONGRL0 value) {
            this.sourcePacketDescription = value;
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

}
