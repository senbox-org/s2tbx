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
 * <p>Classe Java pour A_SPECTRAL_BAND_INFORMATION_LIST complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_SPECTRAL_BAND_INFORMATION_LIST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spectral_Band_Information" maxOccurs="13">
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
 *                           &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="COMPRESSION_RATE">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/image/>A_COMPRESSION_RATE">
 *                           &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="INTEGRATION_TIME">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_MS_UNIT_ATTR">
 *                           &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
@XmlType(name = "A_SPECTRAL_BAND_INFORMATION_LIST", propOrder = {
    "spectralBandInformation"
})
public class ASPECTRALBANDINFORMATIONLIST {

    @XmlElement(name = "Spectral_Band_Information", required = true)
    protected List<ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation> spectralBandInformation;

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
     * {@link ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation }
     * 
     * 
     */
    public List<ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation> getSpectralBandInformation() {
        if (spectralBandInformation == null) {
            spectralBandInformation = new ArrayList<ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation>();
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
     *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="COMPRESSION_RATE">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/image/>A_COMPRESSION_RATE">
     *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="INTEGRATION_TIME">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_MS_UNIT_ATTR">
     *                 &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "integrationtime"
    })
    public static class SpectralBandInformation {

        @XmlElement(name = "PHYSICAL_GAINS", required = true)
        protected ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.PHYSICALGAINS physicalgains;
        @XmlElement(name = "COMPRESSION_RATE", required = true)
        protected ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.COMPRESSIONRATE compressionrate;
        @XmlElement(name = "INTEGRATION_TIME", required = true)
        protected ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.INTEGRATIONTIME integrationtime;
        @XmlAttribute(name = "bandId", required = true)
        protected String bandId;

        /**
         * Obtient la valeur de la propriété physicalgains.
         * 
         * @return
         *     possible object is
         *     {@link ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.PHYSICALGAINS }
         *     
         */
        public ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.PHYSICALGAINS getPHYSICALGAINS() {
            return physicalgains;
        }

        /**
         * Définit la valeur de la propriété physicalgains.
         * 
         * @param value
         *     allowed object is
         *     {@link ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.PHYSICALGAINS }
         *     
         */
        public void setPHYSICALGAINS(ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.PHYSICALGAINS value) {
            this.physicalgains = value;
        }

        /**
         * Obtient la valeur de la propriété compressionrate.
         * 
         * @return
         *     possible object is
         *     {@link ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.COMPRESSIONRATE }
         *     
         */
        public ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.COMPRESSIONRATE getCOMPRESSIONRATE() {
            return compressionrate;
        }

        /**
         * Définit la valeur de la propriété compressionrate.
         * 
         * @param value
         *     allowed object is
         *     {@link ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.COMPRESSIONRATE }
         *     
         */
        public void setCOMPRESSIONRATE(ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.COMPRESSIONRATE value) {
            this.compressionrate = value;
        }

        /**
         * Obtient la valeur de la propriété integrationtime.
         * 
         * @return
         *     possible object is
         *     {@link ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.INTEGRATIONTIME }
         *     
         */
        public ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.INTEGRATIONTIME getINTEGRATIONTIME() {
            return integrationtime;
        }

        /**
         * Définit la valeur de la propriété integrationtime.
         * 
         * @param value
         *     allowed object is
         *     {@link ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.INTEGRATIONTIME }
         *     
         */
        public void setINTEGRATIONTIME(ASPECTRALBANDINFORMATIONLIST.SpectralBandInformation.INTEGRATIONTIME value) {
            this.integrationtime = value;
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
         *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/image/>A_COMPRESSION_RATE">
         *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
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
        public static class COMPRESSIONRATE {

            @XmlValue
            protected double value;
            @XmlAttribute(name = "metadataLevel")
            protected String metadataLevel;

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
             * Obtient la valeur de la propriété metadataLevel.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMetadataLevel() {
                if (metadataLevel == null) {
                    return "Expertise";
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


        /**
         * <p>Classe Java pour anonymous complex type.
         * 
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/SY/misc/>A_DOUBLE_WITH_MS_UNIT_ATTR">
         *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Expertise" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class INTEGRATIONTIME
            extends ADOUBLEWITHMSUNITATTR
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
                    return "Expertise";
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
         *       &lt;attribute name="metadataLevel" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}metadataLevel" fixed="Standard" />
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
            @XmlAttribute(name = "metadataLevel")
            protected String metadataLevel;

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

}
