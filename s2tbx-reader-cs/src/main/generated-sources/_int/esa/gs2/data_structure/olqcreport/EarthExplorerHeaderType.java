//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.data_structure.olqcreport;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * EOFFS header type
 * 
 * <p>Classe Java pour EarthExplorerHeaderType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="EarthExplorerHeaderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Fixed_Header">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="File_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="File_Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Notes" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Mission">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="S2_"/>
 *                         &lt;enumeration value="S2A"/>
 *                         &lt;enumeration value="S2B"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="File_Class" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="File_Type">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;length value="10"/>
 *                         &lt;pattern value="REP_OLQCPA"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="Validity_Period">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Validity_Start" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CcsdsDateType"/>
 *                             &lt;element name="Validity_Stop" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CcsdsDateType"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="File_Version">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
 *                         &lt;totalDigits value="4"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="Source">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="System" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Creator" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Creator_Version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="Creation_Date" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CcsdsDateType"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Variable_Header" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EarthExplorerHeaderType", propOrder = {
    "fixedHeader",
    "variableHeader"
})
public class EarthExplorerHeaderType {

    @XmlElement(name = "Fixed_Header", required = true)
    protected EarthExplorerHeaderType.FixedHeader fixedHeader;
    @XmlElement(name = "Variable_Header")
    protected Object variableHeader;

    /**
     * Obtient la valeur de la propriété fixedHeader.
     * 
     * @return
     *     possible object is
     *     {@link EarthExplorerHeaderType.FixedHeader }
     *     
     */
    public EarthExplorerHeaderType.FixedHeader getFixedHeader() {
        return fixedHeader;
    }

    /**
     * Définit la valeur de la propriété fixedHeader.
     * 
     * @param value
     *     allowed object is
     *     {@link EarthExplorerHeaderType.FixedHeader }
     *     
     */
    public void setFixedHeader(EarthExplorerHeaderType.FixedHeader value) {
        this.fixedHeader = value;
    }

    /**
     * Obtient la valeur de la propriété variableHeader.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getVariableHeader() {
        return variableHeader;
    }

    /**
     * Définit la valeur de la propriété variableHeader.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setVariableHeader(Object value) {
        this.variableHeader = value;
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
     *         &lt;element name="File_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="File_Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Notes" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Mission">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="S2_"/>
     *               &lt;enumeration value="S2A"/>
     *               &lt;enumeration value="S2B"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="File_Class" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="File_Type">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;length value="10"/>
     *               &lt;pattern value="REP_OLQCPA"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="Validity_Period">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Validity_Start" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CcsdsDateType"/>
     *                   &lt;element name="Validity_Stop" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CcsdsDateType"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="File_Version">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
     *               &lt;totalDigits value="4"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="Source">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="System" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="Creator" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="Creator_Version" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="Creation_Date" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CcsdsDateType"/>
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
    @XmlType(name = "", propOrder = {
        "fileName",
        "fileDescription",
        "notes",
        "mission",
        "fileClass",
        "fileType",
        "validityPeriod",
        "fileVersion",
        "source"
    })
    public static class FixedHeader {

        @XmlElement(name = "File_Name", required = true)
        protected String fileName;
        @XmlElement(name = "File_Description", required = true)
        protected String fileDescription;
        @XmlElement(name = "Notes", required = true)
        protected String notes;
        @XmlElement(name = "Mission", required = true)
        protected String mission;
        @XmlElement(name = "File_Class", required = true)
        protected String fileClass;
        @XmlElement(name = "File_Type", required = true)
        protected String fileType;
        @XmlElement(name = "Validity_Period", required = true)
        protected EarthExplorerHeaderType.FixedHeader.ValidityPeriod validityPeriod;
        @XmlElement(name = "File_Version", required = true)
        protected BigInteger fileVersion;
        @XmlElement(name = "Source", required = true)
        protected EarthExplorerHeaderType.FixedHeader.Source source;

        /**
         * Obtient la valeur de la propriété fileName.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Définit la valeur de la propriété fileName.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFileName(String value) {
            this.fileName = value;
        }

        /**
         * Obtient la valeur de la propriété fileDescription.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFileDescription() {
            return fileDescription;
        }

        /**
         * Définit la valeur de la propriété fileDescription.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFileDescription(String value) {
            this.fileDescription = value;
        }

        /**
         * Obtient la valeur de la propriété notes.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNotes() {
            return notes;
        }

        /**
         * Définit la valeur de la propriété notes.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNotes(String value) {
            this.notes = value;
        }

        /**
         * Obtient la valeur de la propriété mission.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMission() {
            return mission;
        }

        /**
         * Définit la valeur de la propriété mission.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMission(String value) {
            this.mission = value;
        }

        /**
         * Obtient la valeur de la propriété fileClass.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFileClass() {
            return fileClass;
        }

        /**
         * Définit la valeur de la propriété fileClass.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFileClass(String value) {
            this.fileClass = value;
        }

        /**
         * Obtient la valeur de la propriété fileType.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFileType() {
            return fileType;
        }

        /**
         * Définit la valeur de la propriété fileType.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFileType(String value) {
            this.fileType = value;
        }

        /**
         * Obtient la valeur de la propriété validityPeriod.
         * 
         * @return
         *     possible object is
         *     {@link EarthExplorerHeaderType.FixedHeader.ValidityPeriod }
         *     
         */
        public EarthExplorerHeaderType.FixedHeader.ValidityPeriod getValidityPeriod() {
            return validityPeriod;
        }

        /**
         * Définit la valeur de la propriété validityPeriod.
         * 
         * @param value
         *     allowed object is
         *     {@link EarthExplorerHeaderType.FixedHeader.ValidityPeriod }
         *     
         */
        public void setValidityPeriod(EarthExplorerHeaderType.FixedHeader.ValidityPeriod value) {
            this.validityPeriod = value;
        }

        /**
         * Obtient la valeur de la propriété fileVersion.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getFileVersion() {
            return fileVersion;
        }

        /**
         * Définit la valeur de la propriété fileVersion.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setFileVersion(BigInteger value) {
            this.fileVersion = value;
        }

        /**
         * Obtient la valeur de la propriété source.
         * 
         * @return
         *     possible object is
         *     {@link EarthExplorerHeaderType.FixedHeader.Source }
         *     
         */
        public EarthExplorerHeaderType.FixedHeader.Source getSource() {
            return source;
        }

        /**
         * Définit la valeur de la propriété source.
         * 
         * @param value
         *     allowed object is
         *     {@link EarthExplorerHeaderType.FixedHeader.Source }
         *     
         */
        public void setSource(EarthExplorerHeaderType.FixedHeader.Source value) {
            this.source = value;
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
         *         &lt;element name="System" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="Creator" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="Creator_Version" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="Creation_Date" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CcsdsDateType"/>
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
            "system",
            "creator",
            "creatorVersion",
            "creationDate"
        })
        public static class Source {

            @XmlElement(name = "System", required = true)
            protected String system;
            @XmlElement(name = "Creator", required = true)
            protected String creator;
            @XmlElement(name = "Creator_Version", required = true)
            protected String creatorVersion;
            @XmlElement(name = "Creation_Date", required = true)
            protected String creationDate;

            /**
             * Obtient la valeur de la propriété system.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSystem() {
                return system;
            }

            /**
             * Définit la valeur de la propriété system.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSystem(String value) {
                this.system = value;
            }

            /**
             * Obtient la valeur de la propriété creator.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCreator() {
                return creator;
            }

            /**
             * Définit la valeur de la propriété creator.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCreator(String value) {
                this.creator = value;
            }

            /**
             * Obtient la valeur de la propriété creatorVersion.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCreatorVersion() {
                return creatorVersion;
            }

            /**
             * Définit la valeur de la propriété creatorVersion.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCreatorVersion(String value) {
                this.creatorVersion = value;
            }

            /**
             * Obtient la valeur de la propriété creationDate.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCreationDate() {
                return creationDate;
            }

            /**
             * Définit la valeur de la propriété creationDate.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCreationDate(String value) {
                this.creationDate = value;
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
         *         &lt;element name="Validity_Start" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CcsdsDateType"/>
         *         &lt;element name="Validity_Stop" type="{http://gs2.esa.int/DATA_STRUCTURE/olqcReport}CcsdsDateType"/>
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
            "validityStart",
            "validityStop"
        })
        public static class ValidityPeriod {

            @XmlElement(name = "Validity_Start", required = true)
            protected String validityStart;
            @XmlElement(name = "Validity_Stop", required = true)
            protected String validityStop;

            /**
             * Obtient la valeur de la propriété validityStart.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValidityStart() {
                return validityStart;
            }

            /**
             * Définit la valeur de la propriété validityStart.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValidityStart(String value) {
                this.validityStart = value;
            }

            /**
             * Obtient la valeur de la propriété validityStop.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValidityStop() {
                return validityStop;
            }

            /**
             * Définit la valeur de la propriété validityStop.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValidityStop(String value) {
                this.validityStop = value;
            }

        }

    }

}
