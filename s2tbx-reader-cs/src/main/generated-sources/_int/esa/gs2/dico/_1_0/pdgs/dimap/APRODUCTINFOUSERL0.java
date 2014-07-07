//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * General PDGS Product Information
 * 
 * <p>Classe Java pour A_PRODUCT_INFO_USERL0 complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PRODUCT_INFO_USERL0">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Product_Info" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_PRODUCT_INFO"/>
 *         &lt;element name="Product_Image_Characteristics">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
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
 *                   &lt;element name="ON_BOARD_COMPRESSION_MODE" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "A_PRODUCT_INFO_USERL0", propOrder = {
    "productInfo",
    "productImageCharacteristics"
})
public class APRODUCTINFOUSERL0 {

    @XmlElement(name = "Product_Info", required = true)
    protected APRODUCTINFO productInfo;
    @XmlElement(name = "Product_Image_Characteristics", required = true)
    protected APRODUCTINFOUSERL0 .ProductImageCharacteristics productImageCharacteristics;

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
     *     {@link APRODUCTINFOUSERL0 .ProductImageCharacteristics }
     *     
     */
    public APRODUCTINFOUSERL0 .ProductImageCharacteristics getProductImageCharacteristics() {
        return productImageCharacteristics;
    }

    /**
     * Définit la valeur de la propriété productImageCharacteristics.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTINFOUSERL0 .ProductImageCharacteristics }
     *     
     */
    public void setProductImageCharacteristics(APRODUCTINFOUSERL0 .ProductImageCharacteristics value) {
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
     *         &lt;element name="ON_BOARD_COMPRESSION_MODE" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
        "physicalgains",
        "referenceband",
        "onboardcompressionmode"
    })
    public static class ProductImageCharacteristics {

        @XmlElement(name = "PHYSICAL_GAINS", required = true)
        protected APRODUCTINFOUSERL0 .ProductImageCharacteristics.PHYSICALGAINS physicalgains;
        @XmlElement(name = "REFERENCE_BAND")
        protected int referenceband;
        @XmlElement(name = "ON_BOARD_COMPRESSION_MODE")
        protected boolean onboardcompressionmode;

        /**
         * Obtient la valeur de la propriété physicalgains.
         * 
         * @return
         *     possible object is
         *     {@link APRODUCTINFOUSERL0 .ProductImageCharacteristics.PHYSICALGAINS }
         *     
         */
        public APRODUCTINFOUSERL0 .ProductImageCharacteristics.PHYSICALGAINS getPHYSICALGAINS() {
            return physicalgains;
        }

        /**
         * Définit la valeur de la propriété physicalgains.
         * 
         * @param value
         *     allowed object is
         *     {@link APRODUCTINFOUSERL0 .ProductImageCharacteristics.PHYSICALGAINS }
         *     
         */
        public void setPHYSICALGAINS(APRODUCTINFOUSERL0 .ProductImageCharacteristics.PHYSICALGAINS value) {
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
         * Obtient la valeur de la propriété onboardcompressionmode.
         * 
         */
        public boolean isONBOARDCOMPRESSIONMODE() {
            return onboardcompressionmode;
        }

        /**
         * Définit la valeur de la propriété onboardcompressionmode.
         * 
         */
        public void setONBOARDCOMPRESSIONMODE(boolean value) {
            this.onboardcompressionmode = value;
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

    }

}
