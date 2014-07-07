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
 * <p>Classe Java pour A_SENSOR_CONFIGURATION_EXPERTISE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_SENSOR_CONFIGURATION_EXPERTISE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Acquisition_Configuration" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ACQUISITION_CONFIGURATION_EXPERTISE"/>
 *         &lt;element name="Source_Packet_Description" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SOURCE_PACKET_DESCRIPTION"/>
 *         &lt;element name="Time_Stamp" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_TIME_STAMP"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_SENSOR_CONFIGURATION_EXPERTISE", propOrder = {
    "acquisitionConfiguration",
    "sourcePacketDescription",
    "timeStamp"
})
public class ASENSORCONFIGURATIONEXPERTISE {

    @XmlElement(name = "Acquisition_Configuration", required = true)
    protected ANACQUISITIONCONFIGURATIONEXPERTISE acquisitionConfiguration;
    @XmlElement(name = "Source_Packet_Description", required = true)
    protected ASOURCEPACKETDESCRIPTION sourcePacketDescription;
    @XmlElement(name = "Time_Stamp", required = true)
    protected ATIMESTAMP timeStamp;

    /**
     * Obtient la valeur de la propriété acquisitionConfiguration.
     * 
     * @return
     *     possible object is
     *     {@link ANACQUISITIONCONFIGURATIONEXPERTISE }
     *     
     */
    public ANACQUISITIONCONFIGURATIONEXPERTISE getAcquisitionConfiguration() {
        return acquisitionConfiguration;
    }

    /**
     * Définit la valeur de la propriété acquisitionConfiguration.
     * 
     * @param value
     *     allowed object is
     *     {@link ANACQUISITIONCONFIGURATIONEXPERTISE }
     *     
     */
    public void setAcquisitionConfiguration(ANACQUISITIONCONFIGURATIONEXPERTISE value) {
        this.acquisitionConfiguration = value;
    }

    /**
     * Obtient la valeur de la propriété sourcePacketDescription.
     * 
     * @return
     *     possible object is
     *     {@link ASOURCEPACKETDESCRIPTION }
     *     
     */
    public ASOURCEPACKETDESCRIPTION getSourcePacketDescription() {
        return sourcePacketDescription;
    }

    /**
     * Définit la valeur de la propriété sourcePacketDescription.
     * 
     * @param value
     *     allowed object is
     *     {@link ASOURCEPACKETDESCRIPTION }
     *     
     */
    public void setSourcePacketDescription(ASOURCEPACKETDESCRIPTION value) {
        this.sourcePacketDescription = value;
    }

    /**
     * Obtient la valeur de la propriété timeStamp.
     * 
     * @return
     *     possible object is
     *     {@link ATIMESTAMP }
     *     
     */
    public ATIMESTAMP getTimeStamp() {
        return timeStamp;
    }

    /**
     * Définit la valeur de la propriété timeStamp.
     * 
     * @param value
     *     allowed object is
     *     {@link ATIMESTAMP }
     *     
     */
    public void setTimeStamp(ATIMESTAMP value) {
        this.timeStamp = value;
    }

}
