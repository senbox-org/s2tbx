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
import _int.esa.gs2.dico._1_0.sy.image.ANIMAGEGEOMETRY;


/**
 * <p>Classe Java pour A_MASK_LIST complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_MASK_LIST">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MASK_FILENAME" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="type" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;enumeration value="MSK_TECQUA"/>
 *                       &lt;enumeration value="MSK_DEFECT"/>
 *                       &lt;enumeration value="MSK_SATURA"/>
 *                       &lt;enumeration value="MSK_NODATA"/>
 *                       &lt;enumeration value="MSK_DETFOO"/>
 *                       &lt;enumeration value="MSK_CLOLOW"/>
 *                       &lt;enumeration value="MSK_CLOUDS"/>
 *                       &lt;enumeration value="MSK_LANWAT"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="bandId" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
 *                 &lt;attribute name="detectorId" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="geometry" use="required" type="{http://gs2.esa.int/DICO/1.0/SY/image/}AN_IMAGE_GEOMETRY" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_MASK_LIST", propOrder = {
    "maskfilename"
})
public class AMASKLIST {

    @XmlElement(name = "MASK_FILENAME", required = true)
    protected List<AMASKLIST.MASKFILENAME> maskfilename;
    @XmlAttribute(name = "geometry", required = true)
    protected ANIMAGEGEOMETRY geometry;

    /**
     * Gets the value of the maskfilename property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the maskfilename property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMASKFILENAME().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AMASKLIST.MASKFILENAME }
     * 
     * 
     */
    public List<AMASKLIST.MASKFILENAME> getMASKFILENAME() {
        if (maskfilename == null) {
            maskfilename = new ArrayList<AMASKLIST.MASKFILENAME>();
        }
        return this.maskfilename;
    }

    /**
     * Obtient la valeur de la propriété geometry.
     * 
     * @return
     *     possible object is
     *     {@link ANIMAGEGEOMETRY }
     *     
     */
    public ANIMAGEGEOMETRY getGeometry() {
        return geometry;
    }

    /**
     * Définit la valeur de la propriété geometry.
     * 
     * @param value
     *     allowed object is
     *     {@link ANIMAGEGEOMETRY }
     *     
     */
    public void setGeometry(ANIMAGEGEOMETRY value) {
        this.geometry = value;
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
     *       &lt;attribute name="type" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;enumeration value="MSK_TECQUA"/>
     *             &lt;enumeration value="MSK_DEFECT"/>
     *             &lt;enumeration value="MSK_SATURA"/>
     *             &lt;enumeration value="MSK_NODATA"/>
     *             &lt;enumeration value="MSK_DETFOO"/>
     *             &lt;enumeration value="MSK_CLOLOW"/>
     *             &lt;enumeration value="MSK_CLOUDS"/>
     *             &lt;enumeration value="MSK_LANWAT"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="bandId" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER" />
     *       &lt;attribute name="detectorId" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_DETECTOR_NUMBER" />
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
    public static class MASKFILENAME {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "type", required = true)
        protected String type;
        @XmlAttribute(name = "bandId")
        protected String bandId;
        @XmlAttribute(name = "detectorId")
        protected String detectorId;

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
         * Obtient la valeur de la propriété type.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            return type;
        }

        /**
         * Définit la valeur de la propriété type.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
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
         * Obtient la valeur de la propriété detectorId.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDetectorId() {
            return detectorId;
        }

        /**
         * Définit la valeur de la propriété detectorId.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDetectorId(String value) {
            this.detectorId = value;
        }

    }

}
