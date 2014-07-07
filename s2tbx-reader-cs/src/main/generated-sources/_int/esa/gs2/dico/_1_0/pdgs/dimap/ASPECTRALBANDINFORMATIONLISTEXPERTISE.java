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
import javax.xml.bind.annotation.XmlValue;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHMSUNITATTR;


/**
 * <p>Classe Java pour A_SPECTRAL_BAND_INFORMATION_LIST_EXPERTISE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_SPECTRAL_BAND_INFORMATION_LIST_EXPERTISE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spectral_Band_Information" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PHYSICAL_GAINS">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
 *                           &lt;attribute name="geometry" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="QL"/>
 *                                 &lt;enumeration value="FULL_RESOLUTION"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="COMPRESSION_RATE" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_COMPRESSION_RATE"/>
 *                   &lt;element name="INTEGRATION_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MS_UNIT_ATTR"/>
 *                   &lt;element name="NUC_TABLE_ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "A_SPECTRAL_BAND_INFORMATION_LIST_EXPERTISE", propOrder = {
    "spectralBandInformation"
})
public class ASPECTRALBANDINFORMATIONLISTEXPERTISE {

    @XmlElement(name = "Spectral_Band_Information", required = true)
    protected List<ASPECTRALBANDINFORMATIONLISTEXPERTISE.SpectralBandInformation> spectralBandInformation;

    /**
     * Gets the value of the spectralBandInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spectralBandInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpectralBandInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ASPECTRALBANDINFORMATIONLISTEXPERTISE.SpectralBandInformation }
     * 
     * 
     */
    public List<ASPECTRALBANDINFORMATIONLISTEXPERTISE.SpectralBandInformation> getSpectralBandInformation() {
        if (spectralBandInformation == null) {
            spectralBandInformation = new ArrayList<ASPECTRALBANDINFORMATIONLISTEXPERTISE.SpectralBandInformation>();
        }
        return this.spectralBandInformation;
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
     *         &lt;element name="PHYSICAL_GAINS">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
     *                 &lt;attribute name="geometry" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="QL"/>
     *                       &lt;enumeration value="FULL_RESOLUTION"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="COMPRESSION_RATE" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_COMPRESSION_RATE"/>
     *         &lt;element name="INTEGRATION_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_MS_UNIT_ATTR"/>
     *         &lt;element name="NUC_TABLE_ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "physicalgains",
        "compressionrate",
        "integrationtime",
        "nuctableid"
    })
    public static class SpectralBandInformation {

        @XmlElement(name = "PHYSICAL_GAINS", required = true)
        protected ASPECTRALBANDINFORMATIONLISTEXPERTISE.SpectralBandInformation.PHYSICALGAINS physicalgains;
        @XmlElement(name = "COMPRESSION_RATE")
        protected double compressionrate;
        @XmlElement(name = "INTEGRATION_TIME", required = true)
        protected ADOUBLEWITHMSUNITATTR integrationtime;
        @XmlElement(name = "NUC_TABLE_ID", required = true)
        protected String nuctableid;
        @XmlAttribute(name = "bandId", required = true)
        protected String bandId;

        /**
         * Obtient la valeur de la propriété physicalgains.
         * 
         * @return
         *     possible object is
         *     {@link ASPECTRALBANDINFORMATIONLISTEXPERTISE.SpectralBandInformation.PHYSICALGAINS }
         *     
         */
        public ASPECTRALBANDINFORMATIONLISTEXPERTISE.SpectralBandInformation.PHYSICALGAINS getPHYSICALGAINS() {
            return physicalgains;
        }

        /**
         * Définit la valeur de la propriété physicalgains.
         * 
         * @param value
         *     allowed object is
         *     {@link ASPECTRALBANDINFORMATIONLISTEXPERTISE.SpectralBandInformation.PHYSICALGAINS }
         *     
         */
        public void setPHYSICALGAINS(ASPECTRALBANDINFORMATIONLISTEXPERTISE.SpectralBandInformation.PHYSICALGAINS value) {
            this.physicalgains = value;
        }

        /**
         * Obtient la valeur de la propriété compressionrate.
         * 
         */
        public double getCOMPRESSIONRATE() {
            return compressionrate;
        }

        /**
         * Définit la valeur de la propriété compressionrate.
         * 
         */
        public void setCOMPRESSIONRATE(double value) {
            this.compressionrate = value;
        }

        /**
         * Obtient la valeur de la propriété integrationtime.
         * 
         * @return
         *     possible object is
         *     {@link ADOUBLEWITHMSUNITATTR }
         *     
         */
        public ADOUBLEWITHMSUNITATTR getINTEGRATIONTIME() {
            return integrationtime;
        }

        /**
         * Définit la valeur de la propriété integrationtime.
         * 
         * @param value
         *     allowed object is
         *     {@link ADOUBLEWITHMSUNITATTR }
         *     
         */
        public void setINTEGRATIONTIME(ADOUBLEWITHMSUNITATTR value) {
            this.integrationtime = value;
        }

        /**
         * Obtient la valeur de la propriété nuctableid.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNUCTABLEID() {
            return nuctableid;
        }

        /**
         * Définit la valeur de la propriété nuctableid.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNUCTABLEID(String value) {
            this.nuctableid = value;
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


        /**
         * <p>Classe Java pour anonymous complex type.
         * 
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
         *       &lt;attribute name="geometry" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="QL"/>
         *             &lt;enumeration value="FULL_RESOLUTION"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class PHYSICALGAINS {

            @XmlValue
            protected double value;
            @XmlAttribute(name = "geometry", required = true)
            protected String geometry;

            /**
             * Obtient la valeur de la propriété value.
             * 
             */
            public double getValue() {
                return value;
            }

            /**
             * Définit la valeur de la propriété value.
             * 
             */
            public void setValue(double value) {
                this.value = value;
            }

            /**
             * Obtient la valeur de la propriété geometry.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getGeometry() {
                return geometry;
            }

            /**
             * Définit la valeur de la propriété geometry.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setGeometry(String value) {
                this.geometry = value;
            }

        }

    }

}
