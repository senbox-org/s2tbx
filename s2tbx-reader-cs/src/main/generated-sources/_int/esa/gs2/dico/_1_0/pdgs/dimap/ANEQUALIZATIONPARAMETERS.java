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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Equalization parameters
 * 
 * <p>Classe Java pour AN_EQUALIZATION_PARAMETERS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_EQUALIZATION_PARAMETERS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Equalized_Band_List" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Equalized_Band" maxOccurs="13">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="OFFSET_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                             &lt;element name="DARK_SIGNAL_NON_UNIFORMITY_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="processed" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_EQUALIZATION_PARAMETERS", propOrder = {
    "equalizedBandList"
})
public class ANEQUALIZATIONPARAMETERS {

    @XmlElement(name = "Equalized_Band_List")
    protected ANEQUALIZATIONPARAMETERS.EqualizedBandList equalizedBandList;
    @XmlAttribute(name = "processed", required = true)
    protected boolean processed;

    /**
     * Obtient la valeur de la propriété equalizedBandList.
     * 
     * @return
     *     possible object is
     *     {@link ANEQUALIZATIONPARAMETERS.EqualizedBandList }
     *     
     */
    public ANEQUALIZATIONPARAMETERS.EqualizedBandList getEqualizedBandList() {
        return equalizedBandList;
    }

    /**
     * Définit la valeur de la propriété equalizedBandList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANEQUALIZATIONPARAMETERS.EqualizedBandList }
     *     
     */
    public void setEqualizedBandList(ANEQUALIZATIONPARAMETERS.EqualizedBandList value) {
        this.equalizedBandList = value;
    }

    /**
     * Obtient la valeur de la propriété processed.
     * 
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Définit la valeur de la propriété processed.
     * 
     */
    public void setProcessed(boolean value) {
        this.processed = value;
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
     *         &lt;element name="Equalized_Band" maxOccurs="13">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="OFFSET_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *                   &lt;element name="DARK_SIGNAL_NON_UNIFORMITY_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
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
    @XmlType(name = "", propOrder = {
        "equalizedBand"
    })
    public static class EqualizedBandList {

        @XmlElement(name = "Equalized_Band", required = true)
        protected List<ANEQUALIZATIONPARAMETERS.EqualizedBandList.EqualizedBand> equalizedBand;

        /**
         * Gets the value of the equalizedBand property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the equalizedBand property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEqualizedBand().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ANEQUALIZATIONPARAMETERS.EqualizedBandList.EqualizedBand }
         * 
         * 
         */
        public List<ANEQUALIZATIONPARAMETERS.EqualizedBandList.EqualizedBand> getEqualizedBand() {
            if (equalizedBand == null) {
                equalizedBand = new ArrayList<ANEQUALIZATIONPARAMETERS.EqualizedBandList.EqualizedBand>();
            }
            return this.equalizedBand;
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
         *         &lt;element name="OFFSET_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
         *         &lt;element name="DARK_SIGNAL_NON_UNIFORMITY_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
         *       &lt;/sequence>
         *       &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "offsetproc",
            "darksignalnonuniformityproc"
        })
        public static class EqualizedBand {

            @XmlElement(name = "OFFSET_PROC")
            protected boolean offsetproc;
            @XmlElement(name = "DARK_SIGNAL_NON_UNIFORMITY_PROC")
            protected boolean darksignalnonuniformityproc;
            @XmlAttribute(name = "bandId", required = true)
            protected String bandId;

            /**
             * Obtient la valeur de la propriété offsetproc.
             * 
             */
            public boolean isOFFSETPROC() {
                return offsetproc;
            }

            /**
             * Définit la valeur de la propriété offsetproc.
             * 
             */
            public void setOFFSETPROC(boolean value) {
                this.offsetproc = value;
            }

            /**
             * Obtient la valeur de la propriété darksignalnonuniformityproc.
             * 
             */
            public boolean isDARKSIGNALNONUNIFORMITYPROC() {
                return darksignalnonuniformityproc;
            }

            /**
             * Définit la valeur de la propriété darksignalnonuniformityproc.
             * 
             */
            public void setDARKSIGNALNONUNIFORMITYPROC(boolean value) {
                this.darksignalnonuniformityproc = value;
            }

            /**
             * Obtient la valeur de la propriété bandId.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBandId() {
                return bandId;
            }

            /**
             * Définit la valeur de la propriété bandId.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBandId(String value) {
                this.bandId = value;
            }

        }

    }

}
