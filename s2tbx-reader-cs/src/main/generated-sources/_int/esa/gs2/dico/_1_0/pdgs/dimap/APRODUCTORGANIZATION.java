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


/**
 * General PDGS Product Information
 * 
 * <p>Classe Java pour A_PRODUCT_ORGANIZATION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_PRODUCT_ORGANIZATION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Granules">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="IMAGE_ID" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/DataAccess/item/>IMAGE_ID">
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="datastripIdentifier" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}DATASTRIP_ID" />
 *                 &lt;attribute name="granuleIdentifier" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}GRANULE_TILE_ID" />
 *                 &lt;attribute name="imageFormat">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="JPEG2000"/>
 *                       &lt;enumeration value="BINARY"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
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
@XmlType(name = "A_PRODUCT_ORGANIZATION", propOrder = {
    "granules"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.APRODUCTINFO.ProductOrganisation.GranuleList.class
})
public class APRODUCTORGANIZATION {

    @XmlElement(name = "Granules", required = true)
    protected APRODUCTORGANIZATION.Granules granules;

    /**
     * Obtient la valeur de la propriété granules.
     * 
     * @return
     *     possible object is
     *     {@link APRODUCTORGANIZATION.Granules }
     *     
     */
    public APRODUCTORGANIZATION.Granules getGranules() {
        return granules;
    }

    /**
     * Définit la valeur de la propriété granules.
     * 
     * @param value
     *     allowed object is
     *     {@link APRODUCTORGANIZATION.Granules }
     *     
     */
    public void setGranules(APRODUCTORGANIZATION.Granules value) {
        this.granules = value;
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
     *         &lt;element name="IMAGE_ID" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/DataAccess/item/>IMAGE_ID">
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="datastripIdentifier" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}DATASTRIP_ID" />
     *       &lt;attribute name="granuleIdentifier" type="{http://gs2.esa.int/DICO/1.0/DataAccess/item/}GRANULE_TILE_ID" />
     *       &lt;attribute name="imageFormat">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="JPEG2000"/>
     *             &lt;enumeration value="BINARY"/>
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
        "imageid"
    })
    public static class Granules {

        @XmlElement(name = "IMAGE_ID", required = true)
        protected List<APRODUCTORGANIZATION.Granules.IMAGEID> imageid;
        @XmlAttribute(name = "datastripIdentifier")
        protected String datastripIdentifier;
        @XmlAttribute(name = "granuleIdentifier")
        protected String granuleIdentifier;
        @XmlAttribute(name = "imageFormat")
        protected String imageFormat;

        /**
         * Gets the value of the imageid property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the imageid property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getIMAGEID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link APRODUCTORGANIZATION.Granules.IMAGEID }
         * 
         * 
         */
        public List<APRODUCTORGANIZATION.Granules.IMAGEID> getIMAGEID() {
            if (imageid == null) {
                imageid = new ArrayList<APRODUCTORGANIZATION.Granules.IMAGEID>();
            }
            return this.imageid;
        }

        /**
         * Obtient la valeur de la propriété datastripIdentifier.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDatastripIdentifier() {
            return datastripIdentifier;
        }

        /**
         * Définit la valeur de la propriété datastripIdentifier.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDatastripIdentifier(String value) {
            this.datastripIdentifier = value;
        }

        /**
         * Obtient la valeur de la propriété granuleIdentifier.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGranuleIdentifier() {
            return granuleIdentifier;
        }

        /**
         * Définit la valeur de la propriété granuleIdentifier.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGranuleIdentifier(String value) {
            this.granuleIdentifier = value;
        }

        /**
         * Obtient la valeur de la propriété imageFormat.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImageFormat() {
            return imageFormat;
        }

        /**
         * Définit la valeur de la propriété imageFormat.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImageFormat(String value) {
            this.imageFormat = value;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         * 
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://gs2.esa.int/DICO/1.0/DataAccess/item/>IMAGE_ID">
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
        public static class IMAGEID {

            @XmlValue
            protected String value;

            /**
             * Product Data Item identification
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

        }

    }

}
