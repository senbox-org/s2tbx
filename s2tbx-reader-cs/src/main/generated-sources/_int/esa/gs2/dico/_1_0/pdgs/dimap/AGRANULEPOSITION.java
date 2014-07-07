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
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_GRANULE_POSITION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_GRANULE_POSITION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="POSITION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Geometric_Header">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="GROUND_CENTER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
 *                   &lt;element name="QL_CENTER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_2_DOUBLE"/>
 *                   &lt;element name="Incidence_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
 *                   &lt;element name="Solar_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="QL_FOOTPRINT" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_8_DOUBLE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_GRANULE_POSITION", propOrder = {
    "position",
    "geometricHeader",
    "qlfootprint"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFOGRL0 .GranulePosition.class,
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFO.GranulePosition.class
})
public class AGRANULEPOSITION {

    @XmlElement(name = "POSITION")
    protected int position;
    @XmlElement(name = "Geometric_Header", required = true)
    protected AGRANULEPOSITION.GeometricHeader geometricHeader;
    @XmlList
    @XmlElement(name = "QL_FOOTPRINT", type = Double.class)
    protected List<Double> qlfootprint;

    /**
     * Obtient la valeur de la propriété position.
     * 
     */
    public int getPOSITION() {
        return position;
    }

    /**
     * Définit la valeur de la propriété position.
     * 
     */
    public void setPOSITION(int value) {
        this.position = value;
    }

    /**
     * Obtient la valeur de la propriété geometricHeader.
     * 
     * @return
     *     possible object is
     *     {@link AGRANULEPOSITION.GeometricHeader }
     *     
     */
    public AGRANULEPOSITION.GeometricHeader getGeometricHeader() {
        return geometricHeader;
    }

    /**
     * Définit la valeur de la propriété geometricHeader.
     * 
     * @param value
     *     allowed object is
     *     {@link AGRANULEPOSITION.GeometricHeader }
     *     
     */
    public void setGeometricHeader(AGRANULEPOSITION.GeometricHeader value) {
        this.geometricHeader = value;
    }

    /**
     * Gets the value of the qlfootprint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qlfootprint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQLFOOTPRINT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Double }
     * 
     * 
     */
    public List<Double> getQLFOOTPRINT() {
        if (qlfootprint == null) {
            qlfootprint = new ArrayList<Double>();
        }
        return this.qlfootprint;
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
     *         &lt;element name="GROUND_CENTER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
     *         &lt;element name="QL_CENTER" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_2_DOUBLE"/>
     *         &lt;element name="Incidence_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
     *         &lt;element name="Solar_Angles" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
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
        "groundcenter",
        "qlcenter",
        "incidenceAngles",
        "solarAngles"
    })
    public static class GeometricHeader {

        @XmlList
        @XmlElement(name = "GROUND_CENTER", type = Double.class)
        protected List<Double> groundcenter;
        @XmlList
        @XmlElement(name = "QL_CENTER", type = Double.class)
        protected List<Double> qlcenter;
        @XmlElement(name = "Incidence_Angles", required = true)
        protected AZENITHANDAZIMUTHANGLES incidenceAngles;
        @XmlElement(name = "Solar_Angles", required = true)
        protected AZENITHANDAZIMUTHANGLES solarAngles;

        /**
         * Gets the value of the groundcenter property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the groundcenter property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGROUNDCENTER().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Double }
         * 
         * 
         */
        public List<Double> getGROUNDCENTER() {
            if (groundcenter == null) {
                groundcenter = new ArrayList<Double>();
            }
            return this.groundcenter;
        }

        /**
         * Gets the value of the qlcenter property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the qlcenter property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getQLCENTER().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Double }
         * 
         * 
         */
        public List<Double> getQLCENTER() {
            if (qlcenter == null) {
                qlcenter = new ArrayList<Double>();
            }
            return this.qlcenter;
        }

        /**
         * Obtient la valeur de la propriété incidenceAngles.
         * 
         * @return
         *     possible object is
         *     {@link AZENITHANDAZIMUTHANGLES }
         *     
         */
        public AZENITHANDAZIMUTHANGLES getIncidenceAngles() {
            return incidenceAngles;
        }

        /**
         * Définit la valeur de la propriété incidenceAngles.
         * 
         * @param value
         *     allowed object is
         *     {@link AZENITHANDAZIMUTHANGLES }
         *     
         */
        public void setIncidenceAngles(AZENITHANDAZIMUTHANGLES value) {
            this.incidenceAngles = value;
        }

        /**
         * Obtient la valeur de la propriété solarAngles.
         * 
         * @return
         *     possible object is
         *     {@link AZENITHANDAZIMUTHANGLES }
         *     
         */
        public AZENITHANDAZIMUTHANGLES getSolarAngles() {
            return solarAngles;
        }

        /**
         * Définit la valeur de la propriété solarAngles.
         * 
         * @param value
         *     allowed object is
         *     {@link AZENITHANDAZIMUTHANGLES }
         *     
         */
        public void setSolarAngles(AZENITHANDAZIMUTHANGLES value) {
            this.solarAngles = value;
        }

    }

}
