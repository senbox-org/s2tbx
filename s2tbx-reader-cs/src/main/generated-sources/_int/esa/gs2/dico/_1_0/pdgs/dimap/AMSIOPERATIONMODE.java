//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2014.07.07 à 03:07:45 PM CEST 
//


package _int.esa.gs2.dico._1_0.pdgs.dimap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour A_MSI_OPERATION_MODE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_MSI_OPERATION_MODE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Nominal_Observation" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Dark_Signal_Calibration" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Extended_Observation" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Absolute_Radiometry_Calibration" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Vicarious_Calibration" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Raw_Measurement" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Test_Mode" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_MSI_OPERATION_MODE", propOrder = {
    "nominalObservation",
    "darkSignalCalibration",
    "extendedObservation",
    "absoluteRadiometryCalibration",
    "vicariousCalibration",
    "rawMeasurement",
    "testMode"
})
public class AMSIOPERATIONMODE {

    @XmlElement(name = "Nominal_Observation", required = true)
    protected Object nominalObservation;
    @XmlElement(name = "Dark_Signal_Calibration", required = true)
    protected Object darkSignalCalibration;
    @XmlElement(name = "Extended_Observation", required = true)
    protected Object extendedObservation;
    @XmlElement(name = "Absolute_Radiometry_Calibration", required = true)
    protected Object absoluteRadiometryCalibration;
    @XmlElement(name = "Vicarious_Calibration", required = true)
    protected Object vicariousCalibration;
    @XmlElement(name = "Raw_Measurement", required = true)
    protected Object rawMeasurement;
    @XmlElement(name = "Test_Mode", required = true)
    protected Object testMode;

    /**
     * Obtient la valeur de la propriété nominalObservation.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getNominalObservation() {
        return nominalObservation;
    }

    /**
     * Définit la valeur de la propriété nominalObservation.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setNominalObservation(Object value) {
        this.nominalObservation = value;
    }

    /**
     * Obtient la valeur de la propriété darkSignalCalibration.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDarkSignalCalibration() {
        return darkSignalCalibration;
    }

    /**
     * Définit la valeur de la propriété darkSignalCalibration.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDarkSignalCalibration(Object value) {
        this.darkSignalCalibration = value;
    }

    /**
     * Obtient la valeur de la propriété extendedObservation.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getExtendedObservation() {
        return extendedObservation;
    }

    /**
     * Définit la valeur de la propriété extendedObservation.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setExtendedObservation(Object value) {
        this.extendedObservation = value;
    }

    /**
     * Obtient la valeur de la propriété absoluteRadiometryCalibration.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getAbsoluteRadiometryCalibration() {
        return absoluteRadiometryCalibration;
    }

    /**
     * Définit la valeur de la propriété absoluteRadiometryCalibration.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setAbsoluteRadiometryCalibration(Object value) {
        this.absoluteRadiometryCalibration = value;
    }

    /**
     * Obtient la valeur de la propriété vicariousCalibration.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getVicariousCalibration() {
        return vicariousCalibration;
    }

    /**
     * Définit la valeur de la propriété vicariousCalibration.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setVicariousCalibration(Object value) {
        this.vicariousCalibration = value;
    }

    /**
     * Obtient la valeur de la propriété rawMeasurement.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getRawMeasurement() {
        return rawMeasurement;
    }

    /**
     * Définit la valeur de la propriété rawMeasurement.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setRawMeasurement(Object value) {
        this.rawMeasurement = value;
    }

    /**
     * Obtient la valeur de la propriété testMode.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getTestMode() {
        return testMode;
    }

    /**
     * Définit la valeur de la propriété testMode.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setTestMode(Object value) {
        this.testMode = value;
    }

}
