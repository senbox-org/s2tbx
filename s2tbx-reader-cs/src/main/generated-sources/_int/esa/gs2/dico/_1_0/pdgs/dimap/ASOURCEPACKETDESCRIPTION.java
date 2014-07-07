//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;
import _int.esa.gs2.dico._1_0.sy.image.ASOURCEPACKETDEGRADATIONTYPE;


/**
 * <p>Classe Java pour A_SOURCE_PACKET_DESCRIPTION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_SOURCE_PACKET_DESCRIPTION">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Lost_Source_Packet" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DEGRADATION_TYPE" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_SOURCE_PACKET_DEGRADATION_TYPE"/>
 *                   &lt;element name="Error_Type_List" maxOccurs="6" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ERROR_NUMBER">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>positiveInteger">
 *                                     &lt;attribute name="errorType" use="required">
 *                                       &lt;simpleType>
 *                                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
 *                                           &lt;minInclusive value="1"/>
 *                                           &lt;maxInclusive value="6"/>
 *                                         &lt;/restriction>
 *                                       &lt;/simpleType>
 *                                     &lt;/attribute>
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SCENE_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
 *                   &lt;element name="FIRST_SP_ERROR">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                         &lt;minInclusive value="0"/>
 *                         &lt;maxInclusive value="143"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="NUMBER_OF_SP_ERROR">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                         &lt;minInclusive value="1"/>
 *                         &lt;maxInclusive value="144"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="bandId" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;minInclusive value="0"/>
 *                       &lt;maxInclusive value="15"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="detectorId" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;minInclusive value="0"/>
 *                       &lt;maxInclusive value="15"/>
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
@XmlType(name = "A_SOURCE_PACKET_DESCRIPTION", propOrder = {
    "lostSourcePacket"
})
public class ASOURCEPACKETDESCRIPTION {

    @XmlElement(name = "Lost_Source_Packet")
    protected List<ASOURCEPACKETDESCRIPTION.LostSourcePacket> lostSourcePacket;

    /**
     * Gets the value of the lostSourcePacket property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lostSourcePacket property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLostSourcePacket().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ASOURCEPACKETDESCRIPTION.LostSourcePacket }
     * 
     * 
     */
    public List<ASOURCEPACKETDESCRIPTION.LostSourcePacket> getLostSourcePacket() {
        if (lostSourcePacket == null) {
            lostSourcePacket = new ArrayList<ASOURCEPACKETDESCRIPTION.LostSourcePacket>();
        }
        return this.lostSourcePacket;
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
     *         &lt;element name="DEGRADATION_TYPE" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_SOURCE_PACKET_DEGRADATION_TYPE"/>
     *         &lt;element name="Error_Type_List" maxOccurs="6" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ERROR_NUMBER">
     *                     &lt;complexType>
     *                       &lt;simpleContent>
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>positiveInteger">
     *                           &lt;attribute name="errorType" use="required">
     *                             &lt;simpleType>
     *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
     *                                 &lt;minInclusive value="1"/>
     *                                 &lt;maxInclusive value="6"/>
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
     *         &lt;element name="SCENE_DATE" type="{http://gs2.esa.int/DICO/1.0/SY/date_time/}A_GPS_DATE_TIME"/>
     *         &lt;element name="FIRST_SP_ERROR">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *               &lt;minInclusive value="0"/>
     *               &lt;maxInclusive value="143"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="NUMBER_OF_SP_ERROR">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *               &lt;minInclusive value="1"/>
     *               &lt;maxInclusive value="144"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="bandId" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;minInclusive value="0"/>
     *             &lt;maxInclusive value="15"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="detectorId" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;minInclusive value="0"/>
     *             &lt;maxInclusive value="15"/>
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
        "degradationtype",
        "errorTypeList",
        "scenedate",
        "firstsperror",
        "numberofsperror"
    })
    public static class LostSourcePacket {

        @XmlElement(name = "DEGRADATION_TYPE", required = true)
        protected ASOURCEPACKETDEGRADATIONTYPE degradationtype;
        @XmlElement(name = "Error_Type_List")
        protected List<ASOURCEPACKETDESCRIPTION.LostSourcePacket.ErrorTypeList> errorTypeList;
        @XmlElement(name = "SCENE_DATE", required = true)
        protected XMLGregorianCalendar scenedate;
        @XmlElement(name = "FIRST_SP_ERROR")
        protected int firstsperror;
        @XmlElement(name = "NUMBER_OF_SP_ERROR")
        protected int numberofsperror;
        @XmlAttribute(name = "bandId", required = true)
        protected int bandId;
        @XmlAttribute(name = "detectorId", required = true)
        protected int detectorId;

        /**
         * Obtient la valeur de la propriété degradationtype.
         * 
         * @return
         *     possible object is
         *     {@link ASOURCEPACKETDEGRADATIONTYPE }
         *     
         */
        public ASOURCEPACKETDEGRADATIONTYPE getDEGRADATIONTYPE() {
            return degradationtype;
        }

        /**
         * Définit la valeur de la propriété degradationtype.
         * 
         * @param value
         *     allowed object is
         *     {@link ASOURCEPACKETDEGRADATIONTYPE }
         *     
         */
        public void setDEGRADATIONTYPE(ASOURCEPACKETDEGRADATIONTYPE value) {
            this.degradationtype = value;
        }

        /**
         * Gets the value of the errorTypeList property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the errorTypeList property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getErrorTypeList().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ASOURCEPACKETDESCRIPTION.LostSourcePacket.ErrorTypeList }
         * 
         * 
         */
        public List<ASOURCEPACKETDESCRIPTION.LostSourcePacket.ErrorTypeList> getErrorTypeList() {
            if (errorTypeList == null) {
                errorTypeList = new ArrayList<ASOURCEPACKETDESCRIPTION.LostSourcePacket.ErrorTypeList>();
            }
            return this.errorTypeList;
        }

        /**
         * Obtient la valeur de la propriété scenedate.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getSCENEDATE() {
            return scenedate;
        }

        /**
         * Définit la valeur de la propriété scenedate.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setSCENEDATE(XMLGregorianCalendar value) {
            this.scenedate = value;
        }

        /**
         * Obtient la valeur de la propriété firstsperror.
         * 
         */
        public int getFIRSTSPERROR() {
            return firstsperror;
        }

        /**
         * Définit la valeur de la propriété firstsperror.
         * 
         */
        public void setFIRSTSPERROR(int value) {
            this.firstsperror = value;
        }

        /**
         * Obtient la valeur de la propriété numberofsperror.
         * 
         */
        public int getNUMBEROFSPERROR() {
            return numberofsperror;
        }

        /**
         * Définit la valeur de la propriété numberofsperror.
         * 
         */
        public void setNUMBEROFSPERROR(int value) {
            this.numberofsperror = value;
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

        /**
         * Obtient la valeur de la propriété detectorId.
         * 
         */
        public int getDetectorId() {
            return detectorId;
        }

        /**
         * Définit la valeur de la propriété detectorId.
         * 
         */
        public void setDetectorId(int value) {
            this.detectorId = value;
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
         *         &lt;element name="ERROR_NUMBER">
         *           &lt;complexType>
         *             &lt;simpleContent>
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>positiveInteger">
         *                 &lt;attribute name="errorType" use="required">
         *                   &lt;simpleType>
         *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
         *                       &lt;minInclusive value="1"/>
         *                       &lt;maxInclusive value="6"/>
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
            "errornumber"
        })
        public static class ErrorTypeList {

            @XmlElement(name = "ERROR_NUMBER", required = true)
            protected ASOURCEPACKETDESCRIPTION.LostSourcePacket.ErrorTypeList.ERRORNUMBER errornumber;

            /**
             * Obtient la valeur de la propriété errornumber.
             * 
             * @return
             *     possible object is
             *     {@link ASOURCEPACKETDESCRIPTION.LostSourcePacket.ErrorTypeList.ERRORNUMBER }
             *     
             */
            public ASOURCEPACKETDESCRIPTION.LostSourcePacket.ErrorTypeList.ERRORNUMBER getERRORNUMBER() {
                return errornumber;
            }

            /**
             * Définit la valeur de la propriété errornumber.
             * 
             * @param value
             *     allowed object is
             *     {@link ASOURCEPACKETDESCRIPTION.LostSourcePacket.ErrorTypeList.ERRORNUMBER }
             *     
             */
            public void setERRORNUMBER(ASOURCEPACKETDESCRIPTION.LostSourcePacket.ErrorTypeList.ERRORNUMBER value) {
                this.errornumber = value;
            }


            /**
             * <p>Classe Java pour anonymous complex type.
             * 
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;simpleContent>
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>positiveInteger">
             *       &lt;attribute name="errorType" use="required">
             *         &lt;simpleType>
             *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
             *             &lt;minInclusive value="1"/>
             *             &lt;maxInclusive value="6"/>
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
            public static class ERRORNUMBER {

                @XmlValue
                @XmlSchemaType(name = "positiveInteger")
                protected BigInteger value;
                @XmlAttribute(name = "errorType", required = true)
                protected int errorType;

                /**
                 * Obtient la valeur de la propriété value.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigInteger }
                 *     
                 */
                public BigInteger getValue() {
                    return value;
                }

                /**
                 * Définit la valeur de la propriété value.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigInteger }
                 *     
                 */
                public void setValue(BigInteger value) {
                    this.value = value;
                }

                /**
                 * Obtient la valeur de la propriété errorType.
                 * 
                 */
                public int getErrorType() {
                    return errorType;
                }

                /**
                 * Définit la valeur de la propriété errorType.
                 * 
                 */
                public void setErrorType(int value) {
                    this.errorType = value;
                }

            }

        }

    }

}
