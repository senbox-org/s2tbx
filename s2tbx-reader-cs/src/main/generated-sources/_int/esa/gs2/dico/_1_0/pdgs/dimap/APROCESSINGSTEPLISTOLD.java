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
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.pdgs.center.AS2PROCESSINGCENTRE;
import _int.esa.gs2.dico._1_0.sy.misc.ASTRINGWITHVERSIONATTR;


/**
 * <p>Classe Java pour A_PROCESSING_STEP_LIST_OLD complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PROCESSING_STEP_LIST_OLD">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Processing_Step" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="UTC_DATE_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
 *                   &lt;element name="SOFTWARE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_STRING_WITH_VERSION_ATTR"/>
 *                   &lt;element name="PROCESSING_CENTER" type="{http://gs2.esa.int/DICO/1.0/PDGS/center/}A_S2_PROCESSING_CENTRE"/>
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
@XmlType(name = "A_PROCESSING_STEP_LIST_OLD", propOrder = {
    "processingStep"
})
public class APROCESSINGSTEPLISTOLD {

    @XmlElement(name = "Processing_Step", required = true)
    protected List<APROCESSINGSTEPLISTOLD.ProcessingStep> processingStep;

    /**
     * Gets the value of the processingStep property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processingStep property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessingStep().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link APROCESSINGSTEPLISTOLD.ProcessingStep }
     * 
     * 
     */
    public List<APROCESSINGSTEPLISTOLD.ProcessingStep> getProcessingStep() {
        if (processingStep == null) {
            processingStep = new ArrayList<APROCESSINGSTEPLISTOLD.ProcessingStep>();
        }
        return this.processingStep;
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
     *         &lt;element name="UTC_DATE_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}AN_UTC_DATE_TIME"/>
     *         &lt;element name="SOFTWARE" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_STRING_WITH_VERSION_ATTR"/>
     *         &lt;element name="PROCESSING_CENTER" type="{http://gs2.esa.int/DICO/1.0/PDGS/center/}A_S2_PROCESSING_CENTRE"/>
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
        "utcdatetime",
        "software",
        "processingcenter"
    })
    public static class ProcessingStep {

        @XmlElement(name = "UTC_DATE_TIME", required = true)
        protected XMLGregorianCalendar utcdatetime;
        @XmlElement(name = "SOFTWARE", required = true)
        protected ASTRINGWITHVERSIONATTR software;
        @XmlElement(name = "PROCESSING_CENTER", required = true)
        protected AS2PROCESSINGCENTRE processingcenter;

        /**
         * Obtient la valeur de la propriété utcdatetime.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getUTCDATETIME() {
            return utcdatetime;
        }

        /**
         * Définit la valeur de la propriété utcdatetime.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setUTCDATETIME(XMLGregorianCalendar value) {
            this.utcdatetime = value;
        }

        /**
         * Obtient la valeur de la propriété software.
         * 
         * @return
         *     possible object is
         *     {@link ASTRINGWITHVERSIONATTR }
         *     
         */
        public ASTRINGWITHVERSIONATTR getSOFTWARE() {
            return software;
        }

        /**
         * Définit la valeur de la propriété software.
         * 
         * @param value
         *     allowed object is
         *     {@link ASTRINGWITHVERSIONATTR }
         *     
         */
        public void setSOFTWARE(ASTRINGWITHVERSIONATTR value) {
            this.software = value;
        }

        /**
         * Obtient la valeur de la propriété processingcenter.
         * 
         * @return
         *     possible object is
         *     {@link AS2PROCESSINGCENTRE }
         *     
         */
        public AS2PROCESSINGCENTRE getPROCESSINGCENTER() {
            return processingcenter;
        }

        /**
         * Définit la valeur de la propriété processingcenter.
         * 
         * @param value
         *     allowed object is
         *     {@link AS2PROCESSINGCENTRE }
         *     
         */
        public void setPROCESSINGCENTER(AS2PROCESSINGCENTRE value) {
            this.processingcenter = value;
        }

    }

}
