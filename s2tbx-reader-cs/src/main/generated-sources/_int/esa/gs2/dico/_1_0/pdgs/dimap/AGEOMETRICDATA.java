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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import _int.esa.gs2.dico._1_0.sy.image.ARGMFUSECASE;


/**
 * Geometric refining results. Created by GEO_S2. The refined geometric model can be updated by RESAMPLE_S2.
 * 
 * <p>Classe Java pour A_GEOMETRIC_DATA complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GEOMETRIC_DATA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RGM" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_RGMF_USE_CASE"/>
 *         &lt;element name="Image_Refining">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Refining_Characteristics" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="REFERENCE_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
 *                             &lt;element name="Reference_Image_List">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="REFERENCE_IMAGE" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;simpleContent>
 *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                               &lt;attribute name="referenceBand" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
 *                                             &lt;/extension>
 *                                           &lt;/simpleContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="flag" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="REFINED"/>
 *                       &lt;enumeration value="NOT_REFINED"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="VNIR_SWIR_Registration">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Registration_Characteristics" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="SWIR_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
 *                             &lt;element name="VNIR_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="flag" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="VNIR_SWIR_REGISTERED"/>
 *                       &lt;enumeration value="VNIR_SWIR_NOT_REGISTERED"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Refined_Corrections_List" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Refined_Corrections" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_REFINED_CORRECTIONS" maxOccurs="unbounded"/>
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
@XmlType(name = "A_GEOMETRIC_DATA", propOrder = {
    "rgm",
    "imageRefining",
    "vnirswirRegistration",
    "refinedCorrectionsList"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANANCILLARYDATADSL1B.GeometricRefiningInfo.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.ANIMAGEDATAINFODSL1B.GeometricInfo.class
})
public class AGEOMETRICDATA {

    @XmlElement(name = "RGM", required = true)
    protected ARGMFUSECASE rgm;
    @XmlElement(name = "Image_Refining", required = true)
    protected AGEOMETRICDATA.ImageRefining imageRefining;
    @XmlElement(name = "VNIR_SWIR_Registration", required = true)
    protected AGEOMETRICDATA.VNIRSWIRRegistration vnirswirRegistration;
    @XmlElement(name = "Refined_Corrections_List")
    protected AGEOMETRICDATA.RefinedCorrectionsList refinedCorrectionsList;

    /**
     * Obtient la valeur de la propriété rgm.
     * 
     * @return
     *     possible object is
     *     {@link ARGMFUSECASE }
     *     
     */
    public ARGMFUSECASE getRGM() {
        return rgm;
    }

    /**
     * Définit la valeur de la propriété rgm.
     * 
     * @param value
     *     allowed object is
     *     {@link ARGMFUSECASE }
     *     
     */
    public void setRGM(ARGMFUSECASE value) {
        this.rgm = value;
    }

    /**
     * Obtient la valeur de la propriété imageRefining.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICDATA.ImageRefining }
     *     
     */
    public AGEOMETRICDATA.ImageRefining getImageRefining() {
        return imageRefining;
    }

    /**
     * Définit la valeur de la propriété imageRefining.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICDATA.ImageRefining }
     *     
     */
    public void setImageRefining(AGEOMETRICDATA.ImageRefining value) {
        this.imageRefining = value;
    }

    /**
     * Obtient la valeur de la propriété vnirswirRegistration.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICDATA.VNIRSWIRRegistration }
     *     
     */
    public AGEOMETRICDATA.VNIRSWIRRegistration getVNIRSWIRRegistration() {
        return vnirswirRegistration;
    }

    /**
     * Définit la valeur de la propriété vnirswirRegistration.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICDATA.VNIRSWIRRegistration }
     *     
     */
    public void setVNIRSWIRRegistration(AGEOMETRICDATA.VNIRSWIRRegistration value) {
        this.vnirswirRegistration = value;
    }

    /**
     * Obtient la valeur de la propriété refinedCorrectionsList.
     * 
     * @return
     *     possible object is
     *     {@link AGEOMETRICDATA.RefinedCorrectionsList }
     *     
     */
    public AGEOMETRICDATA.RefinedCorrectionsList getRefinedCorrectionsList() {
        return refinedCorrectionsList;
    }

    /**
     * Définit la valeur de la propriété refinedCorrectionsList.
     * 
     * @param value
     *     allowed object is
     *     {@link AGEOMETRICDATA.RefinedCorrectionsList }
     *     
     */
    public void setRefinedCorrectionsList(AGEOMETRICDATA.RefinedCorrectionsList value) {
        this.refinedCorrectionsList = value;
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
     *         &lt;element name="Refining_Characteristics" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="REFERENCE_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
     *                   &lt;element name="Reference_Image_List">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="REFERENCE_IMAGE" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;simpleContent>
     *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                                     &lt;attribute name="referenceBand" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
     *                                   &lt;/extension>
     *                                 &lt;/simpleContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
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
     *       &lt;attribute name="flag" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="REFINED"/>
     *             &lt;enumeration value="NOT_REFINED"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "refiningCharacteristics"
    })
    public static class ImageRefining {

        @XmlElement(name = "Refining_Characteristics")
        protected AGEOMETRICDATA.ImageRefining.RefiningCharacteristics refiningCharacteristics;
        @XmlAttribute(name = "flag", required = true)
        protected String flag;

        /**
         * Obtient la valeur de la propriété refiningCharacteristics.
         * 
         * @return
         *     possible object is
         *     {@link AGEOMETRICDATA.ImageRefining.RefiningCharacteristics }
         *     
         */
        public AGEOMETRICDATA.ImageRefining.RefiningCharacteristics getRefiningCharacteristics() {
            return refiningCharacteristics;
        }

        /**
         * Définit la valeur de la propriété refiningCharacteristics.
         * 
         * @param value
         *     allowed object is
         *     {@link AGEOMETRICDATA.ImageRefining.RefiningCharacteristics }
         *     
         */
        public void setRefiningCharacteristics(AGEOMETRICDATA.ImageRefining.RefiningCharacteristics value) {
            this.refiningCharacteristics = value;
        }

        /**
         * Obtient la valeur de la propriété flag.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFlag() {
            return flag;
        }

        /**
         * Définit la valeur de la propriété flag.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFlag(String value) {
            this.flag = value;
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
         *         &lt;element name="REFERENCE_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
         *         &lt;element name="Reference_Image_List">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="REFERENCE_IMAGE" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;simpleContent>
         *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                           &lt;attribute name="referenceBand" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
         *                         &lt;/extension>
         *                       &lt;/simpleContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
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
        @XmlType(name = "", propOrder = {
            "referenceband",
            "referenceImageList"
        })
        public static class RefiningCharacteristics {

            @XmlElement(name = "REFERENCE_BAND", required = true)
            protected String referenceband;
            @XmlElement(name = "Reference_Image_List", required = true)
            protected AGEOMETRICDATA.ImageRefining.RefiningCharacteristics.ReferenceImageList referenceImageList;

            /**
             * Obtient la valeur de la propriété referenceband.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getREFERENCEBAND() {
                return referenceband;
            }

            /**
             * Définit la valeur de la propriété referenceband.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setREFERENCEBAND(String value) {
                this.referenceband = value;
            }

            /**
             * Obtient la valeur de la propriété referenceImageList.
             * 
             * @return
             *     possible object is
             *     {@link AGEOMETRICDATA.ImageRefining.RefiningCharacteristics.ReferenceImageList }
             *     
             */
            public AGEOMETRICDATA.ImageRefining.RefiningCharacteristics.ReferenceImageList getReferenceImageList() {
                return referenceImageList;
            }

            /**
             * Définit la valeur de la propriété referenceImageList.
             * 
             * @param value
             *     allowed object is
             *     {@link AGEOMETRICDATA.ImageRefining.RefiningCharacteristics.ReferenceImageList }
             *     
             */
            public void setReferenceImageList(AGEOMETRICDATA.ImageRefining.RefiningCharacteristics.ReferenceImageList value) {
                this.referenceImageList = value;
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
             *         &lt;element name="REFERENCE_IMAGE" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;simpleContent>
             *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *                 &lt;attribute name="referenceBand" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
             *               &lt;/extension>
             *             &lt;/simpleContent>
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
                "referenceimage"
            })
            public static class ReferenceImageList {

                @XmlElement(name = "REFERENCE_IMAGE", required = true)
                protected List<AGEOMETRICDATA.ImageRefining.RefiningCharacteristics.ReferenceImageList.REFERENCEIMAGE> referenceimage;

                /**
                 * Gets the value of the referenceimage property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the referenceimage property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getREFERENCEIMAGE().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link AGEOMETRICDATA.ImageRefining.RefiningCharacteristics.ReferenceImageList.REFERENCEIMAGE }
                 * 
                 * 
                 */
                public List<AGEOMETRICDATA.ImageRefining.RefiningCharacteristics.ReferenceImageList.REFERENCEIMAGE> getREFERENCEIMAGE() {
                    if (referenceimage == null) {
                        referenceimage = new ArrayList<AGEOMETRICDATA.ImageRefining.RefiningCharacteristics.ReferenceImageList.REFERENCEIMAGE>();
                    }
                    return this.referenceimage;
                }


                /**
                 * <p>Classe Java pour anonymous complex type.
                 * 
                 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;simpleContent>
                 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
                 *       &lt;attribute name="referenceBand" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
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
                public static class REFERENCEIMAGE {

                    @XmlValue
                    protected String value;
                    @XmlAttribute(name = "referenceBand")
                    protected String referenceBand;

                    /**
                     * Obtient la valeur de la propriété value.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getValue() {
                        return value;
                    }

                    /**
                     * Définit la valeur de la propriété value.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setValue(String value) {
                        this.value = value;
                    }

                    /**
                     * Obtient la valeur de la propriété referenceBand.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getReferenceBand() {
                        return referenceBand;
                    }

                    /**
                     * Définit la valeur de la propriété referenceBand.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setReferenceBand(String value) {
                        this.referenceBand = value;
                    }

                }

            }

        }

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
     *         &lt;element name="Refined_Corrections" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_REFINED_CORRECTIONS" maxOccurs="unbounded"/>
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
        "refinedCorrections"
    })
    public static class RefinedCorrectionsList {

        @XmlElement(name = "Refined_Corrections", required = true)
        protected List<AREFINEDCORRECTIONS> refinedCorrections;

        /**
         * Gets the value of the refinedCorrections property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the refinedCorrections property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRefinedCorrections().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AREFINEDCORRECTIONS }
         * 
         * 
         */
        public List<AREFINEDCORRECTIONS> getRefinedCorrections() {
            if (refinedCorrections == null) {
                refinedCorrections = new ArrayList<AREFINEDCORRECTIONS>();
            }
            return this.refinedCorrections;
        }

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
     *         &lt;element name="Registration_Characteristics" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="SWIR_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
     *                   &lt;element name="VNIR_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="flag" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="VNIR_SWIR_REGISTERED"/>
     *             &lt;enumeration value="VNIR_SWIR_NOT_REGISTERED"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "registrationCharacteristics"
    })
    public static class VNIRSWIRRegistration {

        @XmlElement(name = "Registration_Characteristics")
        protected AGEOMETRICDATA.VNIRSWIRRegistration.RegistrationCharacteristics registrationCharacteristics;
        @XmlAttribute(name = "flag", required = true)
        protected String flag;

        /**
         * Obtient la valeur de la propriété registrationCharacteristics.
         * 
         * @return
         *     possible object is
         *     {@link AGEOMETRICDATA.VNIRSWIRRegistration.RegistrationCharacteristics }
         *     
         */
        public AGEOMETRICDATA.VNIRSWIRRegistration.RegistrationCharacteristics getRegistrationCharacteristics() {
            return registrationCharacteristics;
        }

        /**
         * Définit la valeur de la propriété registrationCharacteristics.
         * 
         * @param value
         *     allowed object is
         *     {@link AGEOMETRICDATA.VNIRSWIRRegistration.RegistrationCharacteristics }
         *     
         */
        public void setRegistrationCharacteristics(AGEOMETRICDATA.VNIRSWIRRegistration.RegistrationCharacteristics value) {
            this.registrationCharacteristics = value;
        }

        /**
         * Obtient la valeur de la propriété flag.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFlag() {
            return flag;
        }

        /**
         * Définit la valeur de la propriété flag.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFlag(String value) {
            this.flag = value;
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
         *         &lt;element name="SWIR_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
         *         &lt;element name="VNIR_BAND" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER"/>
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
            "swirband",
            "vnirband"
        })
        public static class RegistrationCharacteristics {

            @XmlElement(name = "SWIR_BAND", required = true)
            protected String swirband;
            @XmlElement(name = "VNIR_BAND", required = true)
            protected String vnirband;

            /**
             * Obtient la valeur de la propriété swirband.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSWIRBAND() {
                return swirband;
            }

            /**
             * Définit la valeur de la propriété swirband.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSWIRBAND(String value) {
                this.swirband = value;
            }

            /**
             * Obtient la valeur de la propriété vnirband.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getVNIRBAND() {
                return vnirband;
            }

            /**
             * Définit la valeur de la propriété vnirband.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setVNIRBAND(String value) {
                this.vnirband = value;
            }

        }

    }

}
