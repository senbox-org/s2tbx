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
import _int.esa.gs2.dico._1_0.sy.image.APIXELMANAGEMENT;


/**
 * Radiometric corrections applied
 * 
 * <p>Classe Java pour A_RADIOMETRIC_DATA_EXPERTISE complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_RADIOMETRIC_DATA_EXPERTISE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SWIR_REARRANGEMENT_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Equalization" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}AN_EQUALIZATION_PARAMETERS_EXPERTISE"/>
 *         &lt;element name="CROSSTALK_OPTICAL_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="CROSSTALK_ELECTRONIC_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="REMOVE_BLIND_PIXELS_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="DEFECTIVE_PIXELS_PROC" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PIXEL_MANAGEMENT"/>
 *         &lt;element name="Restoration" type="{http://gs2.esa.int/DICO/1.0/PDGS/dimap/}A_RESTORATION_PARAMETERS"/>
 *         &lt;element name="BINNING_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="PIXELS_NO_DATA_PROC" type="{http://gs2.esa.int/DICO/1.0/SY/image/}A_PIXEL_MANAGEMENT"/>
 *         &lt;element name="SATURATED_PIXELS_PROC" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_RADIOMETRIC_DATA_EXPERTISE", propOrder = {
    "swirrearrangementproc",
    "equalization",
    "crosstalkopticalproc",
    "crosstalkelectronicproc",
    "removeblindpixelsproc",
    "defectivepixelsproc",
    "restoration",
    "binningproc",
    "pixelsnodataproc",
    "saturatedpixelsproc"
})
public class ARADIOMETRICDATAEXPERTISE {

    @XmlElement(name = "SWIR_REARRANGEMENT_PROC")
    protected boolean swirrearrangementproc;
    @XmlElement(name = "Equalization", required = true)
    protected ANEQUALIZATIONPARAMETERSEXPERTISE equalization;
    @XmlElement(name = "CROSSTALK_OPTICAL_PROC")
    protected boolean crosstalkopticalproc;
    @XmlElement(name = "CROSSTALK_ELECTRONIC_PROC")
    protected boolean crosstalkelectronicproc;
    @XmlElement(name = "REMOVE_BLIND_PIXELS_PROC")
    protected boolean removeblindpixelsproc;
    @XmlElement(name = "DEFECTIVE_PIXELS_PROC", required = true)
    protected APIXELMANAGEMENT defectivepixelsproc;
    @XmlElement(name = "Restoration", required = true)
    protected ARESTORATIONPARAMETERS restoration;
    @XmlElement(name = "BINNING_PROC")
    protected boolean binningproc;
    @XmlElement(name = "PIXELS_NO_DATA_PROC", required = true)
    protected APIXELMANAGEMENT pixelsnodataproc;
    @XmlElement(name = "SATURATED_PIXELS_PROC")
    protected boolean saturatedpixelsproc;

    /**
     * Obtient la valeur de la propriété swirrearrangementproc.
     * 
     */
    public boolean isSWIRREARRANGEMENTPROC() {
        return swirrearrangementproc;
    }

    /**
     * Définit la valeur de la propriété swirrearrangementproc.
     * 
     */
    public void setSWIRREARRANGEMENTPROC(boolean value) {
        this.swirrearrangementproc = value;
    }

    /**
     * Obtient la valeur de la propriété equalization.
     * 
     * @return
     *     possible object is
     *     {@link ANEQUALIZATIONPARAMETERSEXPERTISE }
     *     
     */
    public ANEQUALIZATIONPARAMETERSEXPERTISE getEqualization() {
        return equalization;
    }

    /**
     * Définit la valeur de la propriété equalization.
     * 
     * @param value
     *     allowed object is
     *     {@link ANEQUALIZATIONPARAMETERSEXPERTISE }
     *     
     */
    public void setEqualization(ANEQUALIZATIONPARAMETERSEXPERTISE value) {
        this.equalization = value;
    }

    /**
     * Obtient la valeur de la propriété crosstalkopticalproc.
     * 
     */
    public boolean isCROSSTALKOPTICALPROC() {
        return crosstalkopticalproc;
    }

    /**
     * Définit la valeur de la propriété crosstalkopticalproc.
     * 
     */
    public void setCROSSTALKOPTICALPROC(boolean value) {
        this.crosstalkopticalproc = value;
    }

    /**
     * Obtient la valeur de la propriété crosstalkelectronicproc.
     * 
     */
    public boolean isCROSSTALKELECTRONICPROC() {
        return crosstalkelectronicproc;
    }

    /**
     * Définit la valeur de la propriété crosstalkelectronicproc.
     * 
     */
    public void setCROSSTALKELECTRONICPROC(boolean value) {
        this.crosstalkelectronicproc = value;
    }

    /**
     * Obtient la valeur de la propriété removeblindpixelsproc.
     * 
     */
    public boolean isREMOVEBLINDPIXELSPROC() {
        return removeblindpixelsproc;
    }

    /**
     * Définit la valeur de la propriété removeblindpixelsproc.
     * 
     */
    public void setREMOVEBLINDPIXELSPROC(boolean value) {
        this.removeblindpixelsproc = value;
    }

    /**
     * Obtient la valeur de la propriété defectivepixelsproc.
     * 
     * @return
     *     possible object is
     *     {@link APIXELMANAGEMENT }
     *     
     */
    public APIXELMANAGEMENT getDEFECTIVEPIXELSPROC() {
        return defectivepixelsproc;
    }

    /**
     * Définit la valeur de la propriété defectivepixelsproc.
     * 
     * @param value
     *     allowed object is
     *     {@link APIXELMANAGEMENT }
     *     
     */
    public void setDEFECTIVEPIXELSPROC(APIXELMANAGEMENT value) {
        this.defectivepixelsproc = value;
    }

    /**
     * Obtient la valeur de la propriété restoration.
     * 
     * @return
     *     possible object is
     *     {@link ARESTORATIONPARAMETERS }
     *     
     */
    public ARESTORATIONPARAMETERS getRestoration() {
        return restoration;
    }

    /**
     * Définit la valeur de la propriété restoration.
     * 
     * @param value
     *     allowed object is
     *     {@link ARESTORATIONPARAMETERS }
     *     
     */
    public void setRestoration(ARESTORATIONPARAMETERS value) {
        this.restoration = value;
    }

    /**
     * Obtient la valeur de la propriété binningproc.
     * 
     */
    public boolean isBINNINGPROC() {
        return binningproc;
    }

    /**
     * Définit la valeur de la propriété binningproc.
     * 
     */
    public void setBINNINGPROC(boolean value) {
        this.binningproc = value;
    }

    /**
     * Obtient la valeur de la propriété pixelsnodataproc.
     * 
     * @return
     *     possible object is
     *     {@link APIXELMANAGEMENT }
     *     
     */
    public APIXELMANAGEMENT getPIXELSNODATAPROC() {
        return pixelsnodataproc;
    }

    /**
     * Définit la valeur de la propriété pixelsnodataproc.
     * 
     * @param value
     *     allowed object is
     *     {@link APIXELMANAGEMENT }
     *     
     */
    public void setPIXELSNODATAPROC(APIXELMANAGEMENT value) {
        this.pixelsnodataproc = value;
    }

    /**
     * Obtient la valeur de la propriété saturatedpixelsproc.
     * 
     */
    public boolean isSATURATEDPIXELSPROC() {
        return saturatedpixelsproc;
    }

    /**
     * Définit la valeur de la propriété saturatedpixelsproc.
     * 
     */
    public void setSATURATEDPIXELSPROC(boolean value) {
        this.saturatedpixelsproc = value;
    }

}
