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
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.pdgs.base.ASATELLITEIDENTIFIER;
import _int.esa.gs2.dico._1_0.sy.orbital.ANORBITDIRECTION;


/**
 * <p>Classe Java pour A_DATASTRIP_IDENTIFICATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_DATASTRIP_IDENTIFICATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DATA_STRIP_ID" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DATA_STRIP_ID"/>
 *         &lt;element name="DATA_STRIP_TYPE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="INS-IMG"/>
 *               &lt;enumeration value="INS-NOBS"/>
 *               &lt;enumeration value="INS-EOBS"/>
 *               &lt;enumeration value="INS-DASC"/>
 *               &lt;enumeration value="INS-ABSR"/>
 *               &lt;enumeration value="INS-VIC"/>
 *               &lt;enumeration value="INS-RAW"/>
 *               &lt;enumeration value="INS-TST"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ARCHIVING_STATION" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ARCHIVING_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *         &lt;element name="DOWNLINK_ORBIT_NUMBER" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ORBIT_NUMBER">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="1"/>
 *               &lt;maxInclusive value="143"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ACQUISITION_PLATFORM" type="{http://gs2.esa.int/DICO/1.0/PDGS/base/}A_SATELLITE_IDENTIFIER"/>
 *         &lt;element name="ACQUISITION_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *         &lt;element name="ACQUISITION_ORBIT_DIRECTION" type="{http://gs2.esa.int/DICO/1.0/SY/orbital/}AN_ORBIT_DIRECTION"/>
 *         &lt;element name="DOWNLINK_PRIORITY_FLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="NOMINAL"/>
 *               &lt;enumeration value="NRT"/>
 *               &lt;enumeration value="RT"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
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
@XmlType(name = "A_DATASTRIP_IDENTIFICATION", propOrder = {
    "datastripid",
    "datastriptype",
    "archivingstation",
    "archivingdate",
    "downlinkorbitnumber",
    "orbitnumber",
    "acquisitionplatform",
    "acquisitiondate",
    "acquisitionorbitdirection",
    "downlinkpriorityflag"
})
public class ADATASTRIPIDENTIFICATION {

    @XmlElement(name = "DATA_STRIP_ID", required = true)
    protected String datastripid;
    @XmlElement(name = "DATA_STRIP_TYPE", required = true)
    protected String datastriptype;
    @XmlElement(name = "ARCHIVING_STATION", required = true)
    protected String archivingstation;
    @XmlElement(name = "ARCHIVING_DATE", required = true)
    protected XMLGregorianCalendar archivingdate;
    @XmlElement(name = "DOWNLINK_ORBIT_NUMBER")
    protected int downlinkorbitnumber;
    @XmlElement(name = "ORBIT_NUMBER")
    protected int orbitnumber;
    @XmlElement(name = "ACQUISITION_PLATFORM", required = true)
    protected ASATELLITEIDENTIFIER acquisitionplatform;
    @XmlElement(name = "ACQUISITION_DATE", required = true)
    protected XMLGregorianCalendar acquisitiondate;
    @XmlElement(name = "ACQUISITION_ORBIT_DIRECTION", required = true)
    protected ANORBITDIRECTION acquisitionorbitdirection;
    @XmlElement(name = "DOWNLINK_PRIORITY_FLAG", required = true)
    protected String downlinkpriorityflag;

    /**
     * Obtient la valeur de la propriété datastripid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDATASTRIPID() {
        return datastripid;
    }

    /**
     * Définit la valeur de la propriété datastripid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDATASTRIPID(String value) {
        this.datastripid = value;
    }

    /**
     * Obtient la valeur de la propriété datastriptype.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDATASTRIPTYPE() {
        return datastriptype;
    }

    /**
     * Définit la valeur de la propriété datastriptype.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDATASTRIPTYPE(String value) {
        this.datastriptype = value;
    }

    /**
     * Obtient la valeur de la propriété archivingstation.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getARCHIVINGSTATION() {
        return archivingstation;
    }

    /**
     * Définit la valeur de la propriété archivingstation.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setARCHIVINGSTATION(String value) {
        this.archivingstation = value;
    }

    /**
     * Obtient la valeur de la propriété archivingdate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getARCHIVINGDATE() {
        return archivingdate;
    }

    /**
     * Définit la valeur de la propriété archivingdate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setARCHIVINGDATE(XMLGregorianCalendar value) {
        this.archivingdate = value;
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

    /**
     * Obtient la valeur de la propriété orbitnumber.
     * 
     */
    public int getORBITNUMBER() {
        return orbitnumber;
    }

    /**
     * Définit la valeur de la propriété orbitnumber.
     * 
     */
    public void setORBITNUMBER(int value) {
        this.orbitnumber = value;
    }

    /**
     * Obtient la valeur de la propriété acquisitionplatform.
     * 
     * @return
     *     possible object is
     *     {@link ASATELLITEIDENTIFIER }
     *     
     */
    public ASATELLITEIDENTIFIER getACQUISITIONPLATFORM() {
        return acquisitionplatform;
    }

    /**
     * Définit la valeur de la propriété acquisitionplatform.
     * 
     * @param value
     *     allowed object is
     *     {@link ASATELLITEIDENTIFIER }
     *     
     */
    public void setACQUISITIONPLATFORM(ASATELLITEIDENTIFIER value) {
        this.acquisitionplatform = value;
    }

    /**
     * Obtient la valeur de la propriété acquisitiondate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getACQUISITIONDATE() {
        return acquisitiondate;
    }

    /**
     * Définit la valeur de la propriété acquisitiondate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setACQUISITIONDATE(XMLGregorianCalendar value) {
        this.acquisitiondate = value;
    }

    /**
     * Obtient la valeur de la propriété acquisitionorbitdirection.
     * 
     * @return
     *     possible object is
     *     {@link ANORBITDIRECTION }
     *     
     */
    public ANORBITDIRECTION getACQUISITIONORBITDIRECTION() {
        return acquisitionorbitdirection;
    }

    /**
     * Définit la valeur de la propriété acquisitionorbitdirection.
     * 
     * @param value
     *     allowed object is
     *     {@link ANORBITDIRECTION }
     *     
     */
    public void setACQUISITIONORBITDIRECTION(ANORBITDIRECTION value) {
        this.acquisitionorbitdirection = value;
    }

    /**
     * Obtient la valeur de la propriété downlinkpriorityflag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOWNLINKPRIORITYFLAG() {
        return downlinkpriorityflag;
    }

    /**
     * Définit la valeur de la propriété downlinkpriorityflag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOWNLINKPRIORITYFLAG(String value) {
        this.downlinkpriorityflag = value;
    }

}
