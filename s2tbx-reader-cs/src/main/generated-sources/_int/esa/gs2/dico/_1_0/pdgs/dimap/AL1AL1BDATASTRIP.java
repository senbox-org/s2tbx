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
 * <p>Classe Java pour A_L1A_L1B_DATA_STRIP complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_L1A_L1B_DATA_STRIP">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Data_Strip_Identification" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_DATASTRIP_IDENTIFICATION"/>
 *         &lt;element name="Sensor_Configuration" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SENSOR_CONFIGURATION_EXPERTISE"/>
 *         &lt;element name="Satellite_Ancillary_Data" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_ANCILLARY_DATA_L1A_L1B_L1C"/>
 *         &lt;element name="IERS_Bulletin" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_IERS_BULLETIN"/>
 *         &lt;element name="Granules_Information" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GRANULES_INIT_LOC_PROD"/>
 *         &lt;element name="Quicklook_Descriptor" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_QUICKLOOK_DESCRIPTOR" minOccurs="0"/>
 *         &lt;element name="Geometric_Header_List" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_GEOMETRIC_HEADER_LIST_EXPERTISE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_L1A_L1B_DATA_STRIP", propOrder = {
    "dataStripIdentification",
    "sensorConfiguration",
    "satelliteAncillaryData",
    "iersBulletin",
    "granulesInformation",
    "quicklookDescriptor",
    "geometricHeaderList"
})
public class AL1AL1BDATASTRIP {

    @XmlElement(name = "Data_Strip_Identification", required = true)
    protected ADATASTRIPIDENTIFICATION dataStripIdentification;
    @XmlElement(name = "Sensor_Configuration", required = true)
    protected ASENSORCONFIGURATIONEXPERTISE sensorConfiguration;
    @XmlElement(name = "Satellite_Ancillary_Data", required = true)
    protected ANANCILLARYDATAL1AL1BL1C satelliteAncillaryData;
    @XmlElement(name = "IERS_Bulletin", required = true)
    protected ANIERSBULLETIN iersBulletin;
    @XmlElement(name = "Granules_Information", required = true)
    protected AGRANULESINITLOCPROD granulesInformation;
    @XmlElement(name = "Quicklook_Descriptor")
    protected AQUICKLOOKDESCRIPTOR quicklookDescriptor;
    @XmlElement(name = "Geometric_Header_List", required = true)
    protected AGEOMETRICHEADERLISTEXPERTISE geometricHeaderList;

    /**
     * Obtient la valeur de la propriété dataStripIdentification.
     * 
     * @return
     *     possible object is
     *     {@link ADATASTRIPIDENTIFICATION }
     *     
     */
    public ADATASTRIPIDENTIFICATION getDataStripIdentification() {
        return dataStripIdentification;
    }

    /**
     * Définit la valeur de la propriété dataStripIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link ADATASTRIPIDENTIFICATION }
     *     
     */
    public void setDataStripIdentification(ADATASTRIPIDENTIFICATION value) {
        this.dataStripIdentification = value;
    }

    /**
     * Obtient la valeur de la propriété sensorConfiguration.
     * 
     * @return
     *     possible object is
     *     {@link ASENSORCONFIGURATIONEXPERTISE }
     *     
     */
    public ASENSORCONFIGURATIONEXPERTISE getSensorConfiguration() {
        return sensorConfiguration;
    }

    /**
     * Définit la valeur de la propriété sensorConfiguration.
     * 
     * @param value
     *     allowed object is
     *     {@link ASENSORCONFIGURATIONEXPERTISE }
     *     
     */
    public void setSensorConfiguration(ASENSORCONFIGURATIONEXPERTISE value) {
        this.sensorConfiguration = value;
    }

    /**
     * Obtient la valeur de la propriété satelliteAncillaryData.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATAL1AL1BL1C }
     *     
     */
    public ANANCILLARYDATAL1AL1BL1C getSatelliteAncillaryData() {
        return satelliteAncillaryData;
    }

    /**
     * Définit la valeur de la propriété satelliteAncillaryData.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATAL1AL1BL1C }
     *     
     */
    public void setSatelliteAncillaryData(ANANCILLARYDATAL1AL1BL1C value) {
        this.satelliteAncillaryData = value;
    }

    /**
     * Obtient la valeur de la propriété iersBulletin.
     * 
     * @return
     *     possible object is
     *     {@link ANIERSBULLETIN }
     *     
     */
    public ANIERSBULLETIN getIERSBulletin() {
        return iersBulletin;
    }

    /**
     * Définit la valeur de la propriété iersBulletin.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIERSBULLETIN }
     *     
     */
    public void setIERSBulletin(ANIERSBULLETIN value) {
        this.iersBulletin = value;
    }

    /**
     * Obtient la valeur de la propriété granulesInformation.
     * 
     * @return
     *     possible object is
     *     {@link AGRANULESINITLOCPROD }
     *     
     */
    public AGRANULESINITLOCPROD getGranulesInformation() {
        return granulesInformation;
    }

    /**
     * Définit la valeur de la propriété granulesInformation.
     * 
     * @param value
     *     allowed object is
     *     {@link AGRANULESINITLOCPROD }
     *     
     */
    public void setGranulesInformation(AGRANULESINITLOCPROD value) {
        this.granulesInformation = value;
    }

    /**
     * Obtient la valeur de la propriété quicklookDescriptor.
     * 
     * @return
     *     possible object is
     *     {@link AQUICKLOOKDESCRIPTOR }
     *     
     */
    public AQUICKLOOKDESCRIPTOR getQuicklookDescriptor() {
        return quicklookDescriptor;
    }

    /**
     * Définit la valeur de la propriété quicklookDescriptor.
     * 
     * @param value
     *     allowed object is
     *     {@link AQUICKLOOKDESCRIPTOR }
     *     
     */
    public void setQuicklookDescriptor(AQUICKLOOKDESCRIPTOR value) {
        this.quicklookDescriptor = value;
    }

    /**
     * Obtient la valeur de la propriété geometricHeaderList.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICHEADERLISTEXPERTISE }
     *     
     */
    public AGEOMETRICHEADERLISTEXPERTISE getGeometricHeaderList() {
        return geometricHeaderList;
    }

    /**
     * Définit la valeur de la propriété geometricHeaderList.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICHEADERLISTEXPERTISE }
     *     
     */
    public void setGeometricHeaderList(AGEOMETRICHEADERLISTEXPERTISE value) {
        this.geometricHeaderList = value;
    }

}
