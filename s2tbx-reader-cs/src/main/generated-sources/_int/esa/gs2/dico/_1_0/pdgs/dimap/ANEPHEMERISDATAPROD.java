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
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour AN_EPHEMERIS_DATA_PROD complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="AN_EPHEMERIS_DATA_PROD">
 *   &lt;complexContent>
 *     &lt;extension base="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_EPHEMERIS_DATA_INV">
 *       &lt;sequence>
 *         &lt;element name="POD_Point_List" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="POD_Point" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="POSITION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
 *                             &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
 *                             &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="use" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AN_EPHEMERIS_DATA_PROD", propOrder = {
    "podPointList"
})
public class ANEPHEMERISDATAPROD
    extends ANEPHEMERISDATAINV
{

    @XmlElement(name = "POD_Point_List")
    protected ANEPHEMERISDATAPROD.PODPointList podPointList;

    /**
     * Obtient la valeur de la propriété podPointList.
     * 
     * @return
     *     possible object is
     *     {@link ANEPHEMERISDATAPROD.PODPointList }
     *     
     */
    public ANEPHEMERISDATAPROD.PODPointList getPODPointList() {
        return podPointList;
    }

    /**
     * Définit la valeur de la propriété podPointList.
     * 
     * @param value
     *     allowed object is
     *     {@link ANEPHEMERISDATAPROD.PODPointList }
     *     
     */
    public void setPODPointList(ANEPHEMERISDATAPROD.PODPointList value) {
        this.podPointList = value;
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
     *         &lt;element name="POD_Point" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="POSITION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
     *                   &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
     *                   &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="use" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "podPoint"
    })
    public static class PODPointList {

        @XmlElement(name = "POD_Point", required = true)
        protected List<ANEPHEMERISDATAPROD.PODPointList.PODPoint> podPoint;
        @XmlAttribute(name = "use", required = true)
        protected boolean use;

        /**
         * Gets the value of the podPoint property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the podPoint property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPODPoint().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ANEPHEMERISDATAPROD.PODPointList.PODPoint }
         * 
         * 
         */
        public List<ANEPHEMERISDATAPROD.PODPointList.PODPoint> getPODPoint() {
            if (podPoint == null) {
                podPoint = new ArrayList<ANEPHEMERISDATAPROD.PODPointList.PODPoint>();
            }
            return this.podPoint;
        }

        /**
         * Obtient la valeur de la propriété use.
         * 
         */
        public boolean isUse() {
            return use;
        }

        /**
         * Définit la valeur de la propriété use.
         * 
         */
        public void setUse(boolean value) {
            this.use = value;
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
         *         &lt;element name="POSITION_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
         *         &lt;element name="VELOCITY_VALUES" type="{http://gs2.esa.int/DICO/1.0/SY/misc/}A_LIST_OF_3_DOUBLE"/>
         *         &lt;element name="GPS_TIME" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
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
            "positionvalues",
            "velocityvalues",
            "gpstime"
        })
        public static class PODPoint {

            @XmlList
            @XmlElement(name = "POSITION_VALUES", type = Double.class)
            protected List<Double> positionvalues;
            @XmlList
            @XmlElement(name = "VELOCITY_VALUES", type = Double.class)
            protected List<Double> velocityvalues;
            @XmlElement(name = "GPS_TIME", required = true)
            protected XMLGregorianCalendar gpstime;

            /**
             * Gets the value of the positionvalues property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the positionvalues property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPOSITIONVALUES().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Double }
             * 
             * 
             */
            public List<Double> getPOSITIONVALUES() {
                if (positionvalues == null) {
                    positionvalues = new ArrayList<Double>();
                }
                return this.positionvalues;
            }

            /**
             * Gets the value of the velocityvalues property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the velocityvalues property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getVELOCITYVALUES().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Double }
             * 
             * 
             */
            public List<Double> getVELOCITYVALUES() {
                if (velocityvalues == null) {
                    velocityvalues = new ArrayList<Double>();
                }
                return this.velocityvalues;
            }

            /**
             * Obtient la valeur de la propriété gpstime.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getGPSTIME() {
                return gpstime;
            }

            /**
             * Définit la valeur de la propriété gpstime.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setGPSTIME(XMLGregorianCalendar value) {
                this.gpstime = value;
            }

        }

    }

}
