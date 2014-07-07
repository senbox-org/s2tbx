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
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import _int.esa.gs2.dico._1_0.sy.image.APHYSICALBANDNAME;
import _int.esa.gs2.dico._1_0.sy.misc.ADOUBLEWITHNMATTR;


/**
 * General PDGS Product Information
 * 
 * <p>Classe Java pour A_PRODUCT_INFO_USERL1A complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PRODUCT_INFO_USERL1A">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Product_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_INFO"/>
 *         &lt;element name="Product_Image_Characteristics">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Special_Values" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SPECIAL_VALUES" maxOccurs="2" minOccurs="2"/>
 *                   &lt;element name="Image_Display_Order" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_BAND_DISPLAY_ORDER"/>
 *                   &lt;element name="Product_Image_Size" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RASTER_DIMENSIONS_L1A"/>
 *                   &lt;element name="Spectral_Information_List" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Spectral_Information" maxOccurs="13">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="RESOLUTION" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_RESOLUTION"/>
 *                                       &lt;element name="Wavelength">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="MIN" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
 *                                                 &lt;element name="MAX" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
 *                                                 &lt;element name="CENTRAL" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="Spectral_Response">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="STEP" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
 *                                                 &lt;element name="VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="physicalBand" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PHYSICAL_BAND_NAME" />
 *                                     &lt;attribute name="bandId" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="PHYSICAL_GAINS">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
 *                           &lt;attribute name="bandId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;enumeration value="0"/>
 *                                 &lt;enumeration value="1"/>
 *                                 &lt;enumeration value="2"/>
 *                                 &lt;enumeration value="3"/>
 *                                 &lt;enumeration value="4"/>
 *                                 &lt;enumeration value="5"/>
 *                                 &lt;enumeration value="6"/>
 *                                 &lt;enumeration value="7"/>
 *                                 &lt;enumeration value="8"/>
 *                                 &lt;enumeration value="9"/>
 *                                 &lt;enumeration value="10"/>
 *                                 &lt;enumeration value="11"/>
 *                                 &lt;enumeration value="12"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="REFERENCE_BAND" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "A_PRODUCT_INFO_USERL1A", propOrder = {
    "productInfo",
    "productImageCharacteristics"
})
public class APRODUCTINFOUSERL1A {

    @XmlElement(name = "Product_Info", required = true)
    protected APRODUCTINFO productInfo;
    @XmlElement(name = "Product_Image_Characteristics", required = true)
    protected APRODUCTINFOUSERL1A.ProductImageCharacteristics productImageCharacteristics;

    /**
     * Obtient la valeur de la propriété productInfo.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTINFO }
     *     
     */
    public APRODUCTINFO getProductInfo() {
        return productInfo;
    }

    /**
     * Définit la valeur de la propriété productInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFO }
     *     
     */
    public void setProductInfo(APRODUCTINFO value) {
        this.productInfo = value;
    }

    /**
     * Obtient la valeur de la propriété productImageCharacteristics.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics }
     *     
     */
    public APRODUCTINFOUSERL1A.ProductImageCharacteristics getProductImageCharacteristics() {
        return productImageCharacteristics;
    }

    /**
     * Définit la valeur de la propriété productImageCharacteristics.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics }
     *     
     */
    public void setProductImageCharacteristics(APRODUCTINFOUSERL1A.ProductImageCharacteristics value) {
        this.productImageCharacteristics = value;
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
     *         &lt;element name="Special_Values" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SPECIAL_VALUES" maxOccurs="2" minOccurs="2"/>
     *         &lt;element name="Image_Display_Order" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_BAND_DISPLAY_ORDER"/>
     *         &lt;element name="Product_Image_Size" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RASTER_DIMENSIONS_L1A"/>
     *         &lt;element name="Spectral_Information_List" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Spectral_Information" maxOccurs="13">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="RESOLUTION" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_RESOLUTION"/>
     *                             &lt;element name="Wavelength">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="MIN" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
     *                                       &lt;element name="MAX" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
     *                                       &lt;element name="CENTRAL" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="Spectral_Response">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="STEP" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
     *                                       &lt;element name="VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                           &lt;attribute name="physicalBand" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PHYSICAL_BAND_NAME" />
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
     *         &lt;element name="PHYSICAL_GAINS">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>double">
     *                 &lt;attribute name="bandId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;enumeration value="0"/>
     *                       &lt;enumeration value="1"/>
     *                       &lt;enumeration value="2"/>
     *                       &lt;enumeration value="3"/>
     *                       &lt;enumeration value="4"/>
     *                       &lt;enumeration value="5"/>
     *                       &lt;enumeration value="6"/>
     *                       &lt;enumeration value="7"/>
     *                       &lt;enumeration value="8"/>
     *                       &lt;enumeration value="9"/>
     *                       &lt;enumeration value="10"/>
     *                       &lt;enumeration value="11"/>
     *                       &lt;enumeration value="12"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="REFERENCE_BAND" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
        "specialValues",
        "imageDisplayOrder",
        "productImageSize",
        "spectralInformationList",
        "physicalgains",
        "referenceband"
    })
    public static class ProductImageCharacteristics {

        @XmlElement(name = "Special_Values", required = true)
        protected List<ASPECIALVALUES> specialValues;
        @XmlElement(name = "Image_Display_Order", required = true)
        protected ABANDDISPLAYORDER imageDisplayOrder;
        @XmlElement(name = "Product_Image_Size", required = true)
        protected ARASTERDIMENSIONSL1A productImageSize;
        @XmlElement(name = "Spectral_Information_List")
        protected APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList spectralInformationList;
        @XmlElement(name = "PHYSICAL_GAINS", required = true)
        protected APRODUCTINFOUSERL1A.ProductImageCharacteristics.PHYSICALGAINS physicalgains;
        @XmlElement(name = "REFERENCE_BAND")
        protected int referenceband;

        /**
         * Gets the value of the specialValues property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the specialValues property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSpecialValues().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ASPECIALVALUES }
         * 
         * 
         */
        public List<ASPECIALVALUES> getSpecialValues() {
            if (specialValues == null) {
                specialValues = new ArrayList<ASPECIALVALUES>();
            }
            return this.specialValues;
        }

        /**
         * Obtient la valeur de la propriété imageDisplayOrder.
         * 
         * @return
         *     possible object is
         *     {@link ABANDDISPLAYORDER }
         *     
         */
        public ABANDDISPLAYORDER getImageDisplayOrder() {
            return imageDisplayOrder;
        }

        /**
         * Définit la valeur de la propriété imageDisplayOrder.
         * 
         * @param value
         *     allowed object is
         *     {@link ABANDDISPLAYORDER }
         *     
         */
        public void setImageDisplayOrder(ABANDDISPLAYORDER value) {
            this.imageDisplayOrder = value;
        }

        /**
         * Obtient la valeur de la propriété productImageSize.
         * 
         * @return
         *     possible object is
         *     {@link ARASTERDIMENSIONSL1A }
         *     
         */
        public ARASTERDIMENSIONSL1A getProductImageSize() {
            return productImageSize;
        }

        /**
         * Définit la valeur de la propriété productImageSize.
         * 
         * @param value
         *     allowed object is
         *     {@link ARASTERDIMENSIONSL1A }
         *     
         */
        public void setProductImageSize(ARASTERDIMENSIONSL1A value) {
            this.productImageSize = value;
        }

        /**
         * Obtient la valeur de la propriété spectralInformationList.
         * 
         * @return
         *     possible object is
         *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList }
         *     
         */
        public APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList getSpectralInformationList() {
            return spectralInformationList;
        }

        /**
         * Définit la valeur de la propriété spectralInformationList.
         * 
         * @param value
         *     allowed object is
         *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList }
         *     
         */
        public void setSpectralInformationList(APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList value) {
            this.spectralInformationList = value;
        }

        /**
         * Obtient la valeur de la propriété physicalgains.
         * 
         * @return
         *     possible object is
         *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics.PHYSICALGAINS }
         *     
         */
        public APRODUCTINFOUSERL1A.ProductImageCharacteristics.PHYSICALGAINS getPHYSICALGAINS() {
            return physicalgains;
        }

        /**
         * Définit la valeur de la propriété physicalgains.
         * 
         * @param value
         *     allowed object is
         *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics.PHYSICALGAINS }
         *     
         */
        public void setPHYSICALGAINS(APRODUCTINFOUSERL1A.ProductImageCharacteristics.PHYSICALGAINS value) {
            this.physicalgains = value;
        }

        /**
         * Obtient la valeur de la propriété referenceband.
         * 
         */
        public int getREFERENCEBAND() {
            return referenceband;
        }

        /**
         * Définit la valeur de la propriété referenceband.
         * 
         */
        public void setREFERENCEBAND(int value) {
            this.referenceband = value;
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
         *       &lt;attribute name="bandId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;enumeration value="0"/>
         *             &lt;enumeration value="1"/>
         *             &lt;enumeration value="2"/>
         *             &lt;enumeration value="3"/>
         *             &lt;enumeration value="4"/>
         *             &lt;enumeration value="5"/>
         *             &lt;enumeration value="6"/>
         *             &lt;enumeration value="7"/>
         *             &lt;enumeration value="8"/>
         *             &lt;enumeration value="9"/>
         *             &lt;enumeration value="10"/>
         *             &lt;enumeration value="11"/>
         *             &lt;enumeration value="12"/>
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
            @XmlAttribute(name = "bandId", required = true)
            protected int bandId;

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
             * Obtient la valeur de la propriété bandId.
             * 
             */
            public int getBandId() {
                return bandId;
            }

            /**
             * Définit la valeur de la propriété bandId.
             * 
             */
            public void setBandId(int value) {
                this.bandId = value;
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
         *         &lt;element name="Spectral_Information" maxOccurs="13">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="RESOLUTION" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_RESOLUTION"/>
         *                   &lt;element name="Wavelength">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="MIN" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
         *                             &lt;element name="MAX" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
         *                             &lt;element name="CENTRAL" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="Spectral_Response">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="STEP" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
         *                             &lt;element name="VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *                 &lt;attribute name="physicalBand" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PHYSICAL_BAND_NAME" />
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
            "spectralInformation"
        })
        public static class SpectralInformationList {

            @XmlElement(name = "Spectral_Information", required = true)
            protected List<APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation> spectralInformation;

            /**
             * Gets the value of the spectralInformation property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the spectralInformation property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSpectralInformation().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation }
             * 
             * 
             */
            public List<APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation> getSpectralInformation() {
                if (spectralInformation == null) {
                    spectralInformation = new ArrayList<APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation>();
                }
                return this.spectralInformation;
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
             *         &lt;element name="RESOLUTION" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_RESOLUTION"/>
             *         &lt;element name="Wavelength">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="MIN" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
             *                   &lt;element name="MAX" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
             *                   &lt;element name="CENTRAL" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="Spectral_Response">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="STEP" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
             *                   &lt;element name="VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *       &lt;attribute name="physicalBand" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PHYSICAL_BAND_NAME" />
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
                "resolution",
                "wavelength",
                "spectralResponse"
            })
            public static class SpectralInformation {

                @XmlElement(name = "RESOLUTION")
                protected int resolution;
                @XmlElement(name = "Wavelength", required = true)
                protected APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.Wavelength wavelength;
                @XmlElement(name = "Spectral_Response", required = true)
                protected APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.SpectralResponse spectralResponse;
                @XmlAttribute(name = "physicalBand", required = true)
                protected APHYSICALBANDNAME physicalBand;
                @XmlAttribute(name = "bandId", required = true)
                protected String bandId;

                /**
                 * Obtient la valeur de la propriété resolution.
                 * 
                 */
                public int getRESOLUTION() {
                    return resolution;
                }

                /**
                 * Définit la valeur de la propriété resolution.
                 * 
                 */
                public void setRESOLUTION(int value) {
                    this.resolution = value;
                }

                /**
                 * Obtient la valeur de la propriété wavelength.
                 * 
                 * @return
                 *     possible object is
                 *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.Wavelength }
                 *     
                 */
                public APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.Wavelength getWavelength() {
                    return wavelength;
                }

                /**
                 * Définit la valeur de la propriété wavelength.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.Wavelength }
                 *     
                 */
                public void setWavelength(APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.Wavelength value) {
                    this.wavelength = value;
                }

                /**
                 * Obtient la valeur de la propriété spectralResponse.
                 * 
                 * @return
                 *     possible object is
                 *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.SpectralResponse }
                 *     
                 */
                public APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.SpectralResponse getSpectralResponse() {
                    return spectralResponse;
                }

                /**
                 * Définit la valeur de la propriété spectralResponse.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.SpectralResponse }
                 *     
                 */
                public void setSpectralResponse(APRODUCTINFOUSERL1A.ProductImageCharacteristics.SpectralInformationList.SpectralInformation.SpectralResponse value) {
                    this.spectralResponse = value;
                }

                /**
                 * Obtient la valeur de la propriété physicalBand.
                 * 
                 * @return
                 *     possible object is
                 *     {@link APHYSICALBANDNAME }
                 *     
                 */
                public APHYSICALBANDNAME getPhysicalBand() {
                    return physicalBand;
                }

                /**
                 * Définit la valeur de la propriété physicalBand.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link APHYSICALBANDNAME }
                 *     
                 */
                public void setPhysicalBand(APHYSICALBANDNAME value) {
                    this.physicalBand = value;
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
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="STEP" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
                 *         &lt;element name="VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_DOUBLE"/>
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
                    "step",
                    "values"
                })
                public static class SpectralResponse {

                    @XmlElement(name = "STEP", required = true)
                    protected ADOUBLEWITHNMATTR step;
                    @XmlList
                    @XmlElement(name = "VALUES", type = Double.class)
                    protected List<Double> values;

                    /**
                     * Obtient la valeur de la propriété step.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHNMATTR }
                     *     
                     */
                    public ADOUBLEWITHNMATTR getSTEP() {
                        return step;
                    }

                    /**
                     * Définit la valeur de la propriété step.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHNMATTR }
                     *     
                     */
                    public void setSTEP(ADOUBLEWITHNMATTR value) {
                        this.step = value;
                    }

                    /**
                     * Gets the value of the values property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the values property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getVALUES().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link Double }
                     * 
                     * 
                     */
                    public List<Double> getVALUES() {
                        if (values == null) {
                            values = new ArrayList<Double>();
                        }
                        return this.values;
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
                 *         &lt;element name="MIN" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
                 *         &lt;element name="MAX" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
                 *         &lt;element name="CENTRAL" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_DOUBLE_WITH_NM_ATTR"/>
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
                    "min",
                    "max",
                    "central"
                })
                public static class Wavelength {

                    @XmlElement(name = "MIN", required = true)
                    protected ADOUBLEWITHNMATTR min;
                    @XmlElement(name = "MAX", required = true)
                    protected ADOUBLEWITHNMATTR max;
                    @XmlElement(name = "CENTRAL", required = true)
                    protected ADOUBLEWITHNMATTR central;

                    /**
                     * Obtient la valeur de la propriété min.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHNMATTR }
                     *     
                     */
                    public ADOUBLEWITHNMATTR getMIN() {
                        return min;
                    }

                    /**
                     * Définit la valeur de la propriété min.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHNMATTR }
                     *     
                     */
                    public void setMIN(ADOUBLEWITHNMATTR value) {
                        this.min = value;
                    }

                    /**
                     * Obtient la valeur de la propriété max.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHNMATTR }
                     *     
                     */
                    public ADOUBLEWITHNMATTR getMAX() {
                        return max;
                    }

                    /**
                     * Définit la valeur de la propriété max.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHNMATTR }
                     *     
                     */
                    public void setMAX(ADOUBLEWITHNMATTR value) {
                        this.max = value;
                    }

                    /**
                     * Obtient la valeur de la propriété central.
                     * 
                     * @return
                     *     possible object is
                     *     {@link ADOUBLEWITHNMATTR }
                     *     
                     */
                    public ADOUBLEWITHNMATTR getCENTRAL() {
                        return central;
                    }

                    /**
                     * Définit la valeur de la propriété central.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link ADOUBLEWITHNMATTR }
                     *     
                     */
                    public void setCENTRAL(ADOUBLEWITHNMATTR value) {
                        this.central = value;
                    }

                }

            }

        }

    }

}
