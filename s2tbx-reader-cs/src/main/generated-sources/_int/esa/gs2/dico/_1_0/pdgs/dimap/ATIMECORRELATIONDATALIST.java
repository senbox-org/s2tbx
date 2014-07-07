//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.misc.AINTWITHNSUNITATTR;
import _int.esa.gs2.dico._1_0.sy.misc.ANSM;


/**
 * <p>Classe Java pour A_TIME_CORRELATION_DATA_LIST complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_TIME_CORRELATION_DATA_LIST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Time_Correlation_Data" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="NSM" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_NSM"/>
 *                   &lt;element name="QUALITY_INDEX" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_NS_UNIT_ATTR"/>
 *                   &lt;element name="TDOP" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *                   &lt;element name="IMT" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT"/>
 *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                   &lt;element name="UTC_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
 *                 &lt;/sequence>
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
@XmlType(name = "A_TIME_CORRELATION_DATA_LIST", propOrder = {
    "timeCorrelationData"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANANCILLARYDATADSL0 .TimeCorrelationDataList.class
})
public class ATIMECORRELATIONDATALIST {

    @XmlElement(name = "Time_Correlation_Data", required = true)
    protected List<ATIMECORRELATIONDATALIST.TimeCorrelationData> timeCorrelationData;

    /**
     * Gets the value of the timeCorrelationData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timeCorrelationData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimeCorrelationData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ATIMECORRELATIONDATALIST.TimeCorrelationData }
     * 
     * 
     */
    public List<ATIMECORRELATIONDATALIST.TimeCorrelationData> getTimeCorrelationData() {
        if (timeCorrelationData == null) {
            timeCorrelationData = new ArrayList<ATIMECORRELATIONDATALIST.TimeCorrelationData>();
        }
        return this.timeCorrelationData;
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
     *         &lt;element name="NSM" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_NSM"/>
     *         &lt;element name="QUALITY_INDEX" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_INT_WITH_NS_UNIT_ATTR"/>
     *         &lt;element name="TDOP" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
     *         &lt;element name="IMT" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_POSITIVE_INT"/>
     *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *         &lt;element name="UTC_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "nsm",
        "qualityindex",
        "tdop",
        "imt",
        "gpstime",
        "utctime"
    })
    public static class TimeCorrelationData {

        @XmlElement(name = "NSM", required = true)
        protected ANSM nsm;
        @XmlElement(name = "QUALITY_INDEX", required = true)
        protected AINTWITHNSUNITATTR qualityindex;
        @XmlElement(name = "TDOP")
        protected Double tdop;
        @XmlElement(name = "IMT")
        protected int imt;
        @XmlElement(name = "GPS_TIME", required = true)
        protected XMLGregorianCalendar gpstime;
        @XmlElement(name = "UTC_TIME", required = true)
        protected XMLGregorianCalendar utctime;

        /**
         * Obtient la valeur de la propriété nsm.
         * 
         * @return
         *     possible object is
         *     {@link ANSM }
         *     
         */
        public ANSM getNSM() {
            return nsm;
        }

        /**
         * Définit la valeur de la propriété nsm.
         * 
         * @param value
         *     allowed object is
         *     {@link ANSM }
         *     
         */
        public void setNSM(ANSM value) {
            this.nsm = value;
        }

        /**
         * Obtient la valeur de la propriété qualityindex.
         * 
         * @return
         *     possible object is
         *     {@link AINTWITHNSUNITATTR }
         *     
         */
        public AINTWITHNSUNITATTR getQUALITYINDEX() {
            return qualityindex;
        }

        /**
         * Définit la valeur de la propriété qualityindex.
         * 
         * @param value
         *     allowed object is
         *     {@link AINTWITHNSUNITATTR }
         *     
         */
        public void setQUALITYINDEX(AINTWITHNSUNITATTR value) {
            this.qualityindex = value;
        }

        /**
         * Obtient la valeur de la propriété tdop.
         * 
         * @return
         *     possible object is
         *     {@link Double }
         *     
         */
        public Double getTDOP() {
            return tdop;
        }

        /**
         * Définit la valeur de la propriété tdop.
         * 
         * @param value
         *     allowed object is
         *     {@link Double }
         *     
         */
        public void setTDOP(Double value) {
            this.tdop = value;
        }

        /**
         * Obtient la valeur de la propriété imt.
         * 
         */
        public int getIMT() {
            return imt;
        }

        /**
         * Définit la valeur de la propriété imt.
         * 
         */
        public void setIMT(int value) {
            this.imt = value;
        }

        /**
         * Obtient la valeur de la propriété gpstime.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getGPSTIME() {
            return gpstime;
        }

        /**
         * Définit la valeur de la propriété gpstime.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setGPSTIME(XMLGregorianCalendar value) {
            this.gpstime = value;
        }

        /**
         * Obtient la valeur de la propriété utctime.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getUTCTIME() {
            return utctime;
        }

        /**
         * Définit la valeur de la propriété utctime.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setUTCTIME(XMLGregorianCalendar value) {
            this.utctime = value;
        }

    }

}
