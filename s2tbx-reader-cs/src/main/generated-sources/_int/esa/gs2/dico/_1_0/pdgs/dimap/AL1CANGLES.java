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


/**
 * <p>Classe Java pour A_L1C_ANGLES complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_L1C_ANGLES">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Sun_Angles_Grid" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_SUN_INCIDENCE_ANGLE_GRID"/>
 *         &lt;element name="Mean_Sun_Angle" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES"/>
 *         &lt;element name="Viewing_Incidence_Angles_Grids" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_INCIDENCE_ANGLE_GRID" maxOccurs="unbounded"/>
 *         &lt;element name="Mean_Viewing_Incidence_Angle_List">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Mean_Viewing_Incidence_Angle" maxOccurs="13" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES">
 *                           &lt;attribute name="bandId" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER">
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
 *                       &lt;/complexContent>
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
@XmlType(name = "A_L1C_ANGLES", propOrder = {
    "sunAnglesGrid",
    "meanSunAngle",
    "viewingIncidenceAnglesGrids",
    "meanViewingIncidenceAngleList"
})
@XmlSeeAlso({
    _int.esa.gs2.dico._1_0.pdgs.dimap.AGEOMETRICINFOTILE.TileAngles.class
})
public class AL1CANGLES {

    @XmlElement(name = "Sun_Angles_Grid", required = true)
    protected ASUNINCIDENCEANGLEGRID sunAnglesGrid;
    @XmlElement(name = "Mean_Sun_Angle", required = true)
    protected AZENITHANDAZIMUTHANGLES meanSunAngle;
    @XmlElement(name = "Viewing_Incidence_Angles_Grids", required = true)
    protected List<ANINCIDENCEANGLEGRID> viewingIncidenceAnglesGrids;
    @XmlElement(name = "Mean_Viewing_Incidence_Angle_List", required = true)
    protected AL1CANGLES.MeanViewingIncidenceAngleList meanViewingIncidenceAngleList;

    /**
     * Obtient la valeur de la propriété sunAnglesGrid.
     * 
     * @return
     *     possible object is
     *     {@link ASUNINCIDENCEANGLEGRID }
     *     
     */
    public ASUNINCIDENCEANGLEGRID getSunAnglesGrid() {
        return sunAnglesGrid;
    }

    /**
     * Définit la valeur de la propriété sunAnglesGrid.
     * 
     * @param value
     *     allowed object is
     *     {@link ASUNINCIDENCEANGLEGRID }
     *     
     */
    public void setSunAnglesGrid(ASUNINCIDENCEANGLEGRID value) {
        this.sunAnglesGrid = value;
    }

    /**
     * Obtient la valeur de la propriété meanSunAngle.
     * 
     * @return
     *     possible object is
     *     {@link AZENITHANDAZIMUTHANGLES }
     *     
     */
    public AZENITHANDAZIMUTHANGLES getMeanSunAngle() {
        return meanSunAngle;
    }

    /**
     * Définit la valeur de la propriété meanSunAngle.
     * 
     * @param value
     *     allowed object is
     *     {@link AZENITHANDAZIMUTHANGLES }
     *     
     */
    public void setMeanSunAngle(AZENITHANDAZIMUTHANGLES value) {
        this.meanSunAngle = value;
    }

    /**
     * Gets the value of the viewingIncidenceAnglesGrids property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the viewingIncidenceAnglesGrids property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getViewingIncidenceAnglesGrids().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ANINCIDENCEANGLEGRID }
     * 
     * 
     */
    public List<ANINCIDENCEANGLEGRID> getViewingIncidenceAnglesGrids() {
        if (viewingIncidenceAnglesGrids == null) {
            viewingIncidenceAnglesGrids = new ArrayList<ANINCIDENCEANGLEGRID>();
        }
        return this.viewingIncidenceAnglesGrids;
    }

    /**
     * Obtient la valeur de la propriété meanViewingIncidenceAngleList.
     * 
     * @return
     *     possible object is
     *     {@link AL1CANGLES.MeanViewingIncidenceAngleList }
     *     
     */
    public AL1CANGLES.MeanViewingIncidenceAngleList getMeanViewingIncidenceAngleList() {
        return meanViewingIncidenceAngleList;
    }

    /**
     * Définit la valeur de la propriété meanViewingIncidenceAngleList.
     * 
     * @param value
     *     allowed object is
     *     {@link AL1CANGLES.MeanViewingIncidenceAngleList }
     *     
     */
    public void setMeanViewingIncidenceAngleList(AL1CANGLES.MeanViewingIncidenceAngleList value) {
        this.meanViewingIncidenceAngleList = value;
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
     *         &lt;element name="Mean_Viewing_Incidence_Angle" maxOccurs="13" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES">
     *                 &lt;attribute name="bandId" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER">
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
        "meanViewingIncidenceAngle"
    })
    public static class MeanViewingIncidenceAngleList {

        @XmlElement(name = "Mean_Viewing_Incidence_Angle")
        protected List<AL1CANGLES.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle> meanViewingIncidenceAngle;

        /**
         * Gets the value of the meanViewingIncidenceAngle property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the meanViewingIncidenceAngle property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMeanViewingIncidenceAngle().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AL1CANGLES.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle }
         * 
         * 
         */
        public List<AL1CANGLES.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle> getMeanViewingIncidenceAngle() {
            if (meanViewingIncidenceAngle == null) {
                meanViewingIncidenceAngle = new ArrayList<AL1CANGLES.MeanViewingIncidenceAngleList.MeanViewingIncidenceAngle>();
            }
            return this.meanViewingIncidenceAngle;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         * 
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_ZENITH_AND_AZIMUTH_ANGLES">
         *       &lt;attribute name="bandId" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://gs2.esa.int/DICO/1.0/SY/image/}A_BAND_NUMBER">
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
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class MeanViewingIncidenceAngle
            extends AZENITHANDAZIMUTHANGLES
        {

            @XmlAttribute(name = "bandId", required = true)
            protected String bandId;

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
