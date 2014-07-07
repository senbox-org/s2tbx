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
import _int.esa.gs2.dico._1_0.sy.image.ARESTORATIONSCENARIO;


/**
 * <p>Classe Java pour A_RESTORATION_PARAMETERS complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_RESTORATION_PARAMETERS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Restored_Band_List" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Restored_Band" maxOccurs="13">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="RESTORATION_SCENARIO" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_RESTORATION_SCENARIO"/>
 *                             &lt;element name="Levelling_Values">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="XMIN">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                             &lt;minInclusive value="0"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="XMAX">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                                             &lt;minInclusive value="0"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
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
 *       &lt;/sequence>
 *       &lt;attribute name="processed" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_RESTORATION_PARAMETERS", propOrder = {
    "restoredBandList"
})
public class ARESTORATIONPARAMETERS {

    @XmlElement(name = "Restored_Band_List")
    protected ARESTORATIONPARAMETERS.RestoredBandList restoredBandList;
    @XmlAttribute(name = "processed", required = true)
    protected boolean processed;

    /**
     * Obtient la valeur de la propriété restoredBandList.
     * 
     * @return
     *     possible object is
     *     {@link ARESTORATIONPARAMETERS.RestoredBandList }
     *     
     */
    public ARESTORATIONPARAMETERS.RestoredBandList getRestoredBandList() {
        return restoredBandList;
    }

    /**
     * Définit la valeur de la propriété restoredBandList.
     * 
     * @param value
     *     allowed object is
     *     {@link ARESTORATIONPARAMETERS.RestoredBandList }
     *     
     */
    public void setRestoredBandList(ARESTORATIONPARAMETERS.RestoredBandList value) {
        this.restoredBandList = value;
    }

    /**
     * Obtient la valeur de la propriété processed.
     * 
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Définit la valeur de la propriété processed.
     * 
     */
    public void setProcessed(boolean value) {
        this.processed = value;
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
     *         &lt;element name="Restored_Band" maxOccurs="13">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="RESTORATION_SCENARIO" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_RESTORATION_SCENARIO"/>
     *                   &lt;element name="Levelling_Values">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="XMIN">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                                   &lt;minInclusive value="0"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                             &lt;element name="XMAX">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *                                   &lt;minInclusive value="0"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
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
    @XmlType(name = "", propOrder = {
        "restoredBand"
    })
    public static class RestoredBandList {

        @XmlElement(name = "Restored_Band", required = true)
        protected List<ARESTORATIONPARAMETERS.RestoredBandList.RestoredBand> restoredBand;

        /**
         * Gets the value of the restoredBand property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the restoredBand property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRestoredBand().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ARESTORATIONPARAMETERS.RestoredBandList.RestoredBand }
         * 
         * 
         */
        public List<ARESTORATIONPARAMETERS.RestoredBandList.RestoredBand> getRestoredBand() {
            if (restoredBand == null) {
                restoredBand = new ArrayList<ARESTORATIONPARAMETERS.RestoredBandList.RestoredBand>();
            }
            return this.restoredBand;
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
         *         &lt;element name="RESTORATION_SCENARIO" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_RESTORATION_SCENARIO"/>
         *         &lt;element name="Levelling_Values">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="XMIN">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *                         &lt;minInclusive value="0"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                   &lt;element name="XMAX">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
         *                         &lt;minInclusive value="0"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
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
            "restorationscenario",
            "levellingValues"
        })
        public static class RestoredBand {

            @XmlElement(name = "RESTORATION_SCENARIO", required = true)
            protected ARESTORATIONSCENARIO restorationscenario;
            @XmlElement(name = "Levelling_Values", required = true)
            protected ARESTORATIONPARAMETERS.RestoredBandList.RestoredBand.LevellingValues levellingValues;
            @XmlAttribute(name = "bandId", required = true)
            protected String bandId;

            /**
             * Obtient la valeur de la propriété restorationscenario.
             * 
             * @return
             *     possible object is
             *     {@link ARESTORATIONSCENARIO }
             *     
             */
            public ARESTORATIONSCENARIO getRESTORATIONSCENARIO() {
                return restorationscenario;
            }

            /**
             * Définit la valeur de la propriété restorationscenario.
             * 
             * @param value
             *     allowed object is
             *     {@link ARESTORATIONSCENARIO }
             *     
             */
            public void setRESTORATIONSCENARIO(ARESTORATIONSCENARIO value) {
                this.restorationscenario = value;
            }

            /**
             * Obtient la valeur de la propriété levellingValues.
             * 
             * @return
             *     possible object is
             *     {@link ARESTORATIONPARAMETERS.RestoredBandList.RestoredBand.LevellingValues }
             *     
             */
            public ARESTORATIONPARAMETERS.RestoredBandList.RestoredBand.LevellingValues getLevellingValues() {
                return levellingValues;
            }

            /**
             * Définit la valeur de la propriété levellingValues.
             * 
             * @param value
             *     allowed object is
             *     {@link ARESTORATIONPARAMETERS.RestoredBandList.RestoredBand.LevellingValues }
             *     
             */
            public void setLevellingValues(ARESTORATIONPARAMETERS.RestoredBandList.RestoredBand.LevellingValues value) {
                this.levellingValues = value;
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
             *         &lt;element name="XMIN">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
             *               &lt;minInclusive value="0"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
             *         &lt;element name="XMAX">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
             *               &lt;minInclusive value="0"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
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
                "xmin",
                "xmax"
            })
            public static class LevellingValues {

                @XmlElement(name = "XMIN")
                protected int xmin;
                @XmlElement(name = "XMAX")
                protected int xmax;

                /**
                 * Obtient la valeur de la propriété xmin.
                 * 
                 */
                public int getXMIN() {
                    return xmin;
                }

                /**
                 * Définit la valeur de la propriété xmin.
                 * 
                 */
                public void setXMIN(int value) {
                    this.xmin = value;
                }

                /**
                 * Obtient la valeur de la propriété xmax.
                 * 
                 */
                public int getXMAX() {
                    return xmax;
                }

                /**
                 * Définit la valeur de la propriété xmax.
                 * 
                 */
                public void setXMAX(int value) {
                    this.xmax = value;
                }

            }

        }

    }

}
