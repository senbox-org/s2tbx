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
 * <p>Classe Java pour A_SENSOR_CONFIGURATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_SENSOR_CONFIGURATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Acquisition_Configuration" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ACQUISITION_CONFIGURATION"/>
 *         &lt;element name="Source_Packet_Description" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SOURCE_PACKET_DESCRIPTION_DSL0"/>
 *         &lt;element name="Time_Stamp">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_TIME_STAMP">
 *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *               &lt;/extension>
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
@XmlType(name = "A_SENSOR_CONFIGURATION", propOrder = {
    "acquisitionConfiguration",
    "sourcePacketDescription",
    "timeStamp"
})
public class ASENSORCONFIGURATION {

    @XmlElement(name = "Acquisition_Configuration", required = true)
    protected AACQUISITIONCONFIGURATION acquisitionConfiguration;
    @XmlElement(name = "Source_Packet_Description", required = true)
    protected ASOURCEPACKETDESCRIPTIONDSL0 sourcePacketDescription;
    @XmlElement(name = "Time_Stamp", required = true)
    protected ASENSORCONFIGURATION.TimeStamp timeStamp;

    /**
     * Obtient la valeur de la propriété acquisitionConfiguration.
     * 
     * @return
     *     possible object is
     *     {@link AACQUISITIONCONFIGURATION }
     *     
     */
    public AACQUISITIONCONFIGURATION getAcquisitionConfiguration() {
        return acquisitionConfiguration;
    }

    /**
     * Définit la valeur de la propriété acquisitionConfiguration.
     * 
     * @param value
     *     allowed object is
     *     {@link AACQUISITIONCONFIGURATION }
     *     
     */
    public void setAcquisitionConfiguration(AACQUISITIONCONFIGURATION value) {
        this.acquisitionConfiguration = value;
    }

    /**
     * Obtient la valeur de la propriété sourcePacketDescription.
     * 
     * @return
     *     possible object is
     *     {@link ASOURCEPACKETDESCRIPTIONDSL0 }
     *     
     */
    public ASOURCEPACKETDESCRIPTIONDSL0 getSourcePacketDescription() {
        return sourcePacketDescription;
    }

    /**
     * Définit la valeur de la propriété sourcePacketDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ASOURCEPACKETDESCRIPTIONDSL0 }
     *     
     */
    public void setSourcePacketDescription(ASOURCEPACKETDESCRIPTIONDSL0 value) {
        this.sourcePacketDescription = value;
    }

    /**
     * Obtient la valeur de la propriété timeStamp.
     * 
     * @return
     *     possible object is
     *     {@link ASENSORCONFIGURATION.TimeStamp }
     *     
     */
    public ASENSORCONFIGURATION.TimeStamp getTimeStamp() {
        return timeStamp;
    }

    /**
     * Définit la valeur de la propriété timeStamp.
     * 
     * @param value
     *     allowed object is
     *     {@link ASENSORCONFIGURATION.TimeStamp }
     *     
     */
    public void setTimeStamp(ASENSORCONFIGURATION.TimeStamp value) {
        this.timeStamp = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_TIME_STAMP">
     *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class TimeStamp
        extends ATIMESTAMP
    {

        @XmlAttribute(name = "metadataLevel")
        protected String metadataLevel;

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
