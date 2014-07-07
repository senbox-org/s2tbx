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


/**
 * Quality assessement created by DATATION
 * 
 * <p>Classe Java pour AN_ANCILLARY_DATA_QUALITY complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_ANCILLARY_DATA_QUALITY">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Degraded_Anc_Data_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DEGRADED_ANC_DATA" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
 *                           &lt;attribute name="hz" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                 &lt;minInclusive value="1"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
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
@XmlType(name = "AN_ANCILLARY_DATA_QUALITY", propOrder = {
    "degradedAncDataList"
})
public class ANANCILLARYDATAQUALITY {

    @XmlElement(name = "Degraded_Anc_Data_List", required = true)
    protected ANANCILLARYDATAQUALITY.DegradedAncDataList degradedAncDataList;

    /**
     * Obtient la valeur de la propriété degradedAncDataList.
     * 
     * @return
     *     possible object is
     *     {@link ANANCILLARYDATAQUALITY.DegradedAncDataList }
     *     
     */
    public ANANCILLARYDATAQUALITY.DegradedAncDataList getDegradedAncDataList() {
        return degradedAncDataList;
    }

    /**
     * Définit la valeur de la propriété degradedAncDataList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANANCILLARYDATAQUALITY.DegradedAncDataList }
     *     
     */
    public void setDegradedAncDataList(ANANCILLARYDATAQUALITY.DegradedAncDataList value) {
        this.degradedAncDataList = value;
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
     *         &lt;element name="DEGRADED_ANC_DATA" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
     *                 &lt;attribute name="hz" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                       &lt;minInclusive value="1"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
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
        "degradedancdata"
    })
    public static class DegradedAncDataList {

        @XmlElement(name = "DEGRADED_ANC_DATA", required = true)
        protected List<ANANCILLARYDATAQUALITY.DegradedAncDataList.DEGRADEDANCDATA> degradedancdata;

        /**
         * Gets the value of the degradedancdata property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the degradedancdata property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDEGRADEDANCDATA().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ANANCILLARYDATAQUALITY.DegradedAncDataList.DEGRADEDANCDATA }
         * 
         * 
         */
        public List<ANANCILLARYDATAQUALITY.DegradedAncDataList.DEGRADEDANCDATA> getDEGRADEDANCDATA() {
            if (degradedancdata == null) {
                degradedancdata = new ArrayList<ANANCILLARYDATAQUALITY.DegradedAncDataList.DEGRADEDANCDATA>();
            }
            return this.degradedancdata;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         * 
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>int">
         *       &lt;attribute name="hz" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *             &lt;minInclusive value="1"/>
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
        public static class DEGRADEDANCDATA {

            @XmlValue
            protected int value;
            @XmlAttribute(name = "hz", required = true)
            protected int hz;

            /**
             * Obtient la valeur de la propriété value.
             * 
             */
            public int getValue() {
                return value;
            }

            /**
             * Définit la valeur de la propriété value.
             * 
             */
            public void setValue(int value) {
                this.value = value;
            }

            /**
             * Obtient la valeur de la propriété hz.
             * 
             */
            public int getHz() {
                return hz;
            }

            /**
             * Définit la valeur de la propriété hz.
             * 
             */
            public void setHz(int value) {
                this.hz = value;
            }

        }

    }

}
