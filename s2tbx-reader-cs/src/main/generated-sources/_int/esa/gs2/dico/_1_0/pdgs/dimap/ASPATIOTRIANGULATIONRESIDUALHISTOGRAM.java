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
 * <p>Classe Java pour A_SPATIOTRIANGULATION_RESIDUAL_HISTOGRAM complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="A_SPATIOTRIANGULATION_RESIDUAL_HISTOGRAM">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Ground_Residual">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="X">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="HISTOGRAM" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Y" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="Z" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Image_Residual" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "A_SPATIOTRIANGULATION_RESIDUAL_HISTOGRAM", propOrder = {
    "groundResidual",
    "imageResidual"
})
public class ASPATIOTRIANGULATIONRESIDUALHISTOGRAM {

    @XmlElement(name = "Ground_Residual", required = true)
    protected ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual groundResidual;
    @XmlElement(name = "Image_Residual", required = true)
    protected Object imageResidual;

    /**
     * Obtient la valeur de la propriété groundResidual.
     * 
     * @return
     *     possible object is
     *     {@link ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual }
     *     
     */
    public ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual getGroundResidual() {
        return groundResidual;
    }

    /**
     * Définit la valeur de la propriété groundResidual.
     * 
     * @param value
     *     allowed object is
     *     {@link ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual }
     *     
     */
    public void setGroundResidual(ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual value) {
        this.groundResidual = value;
    }

    /**
     * Obtient la valeur de la propriété imageResidual.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getImageResidual() {
        return imageResidual;
    }

    /**
     * Définit la valeur de la propriété imageResidual.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setImageResidual(Object value) {
        this.imageResidual = value;
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
     *         &lt;element name="X">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="HISTOGRAM" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Y" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="Z" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
        "x",
        "y",
        "z"
    })
    public static class GroundResidual {

        @XmlElement(name = "X", required = true)
        protected ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual.X x;
        @XmlElement(name = "Y", required = true)
        protected Object y;
        @XmlElement(name = "Z", required = true)
        protected Object z;

        /**
         * Obtient la valeur de la propriété x.
         * 
         * @return
         *     possible object is
         *     {@link ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual.X }
         *     
         */
        public ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual.X getX() {
            return x;
        }

        /**
         * Définit la valeur de la propriété x.
         * 
         * @param value
         *     allowed object is
         *     {@link ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual.X }
         *     
         */
        public void setX(ASPATIOTRIANGULATIONRESIDUALHISTOGRAM.GroundResidual.X value) {
            this.x = value;
        }

        /**
         * Obtient la valeur de la propriété y.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getY() {
            return y;
        }

        /**
         * Définit la valeur de la propriété y.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setY(Object value) {
            this.y = value;
        }

        /**
         * Obtient la valeur de la propriété z.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getZ() {
            return z;
        }

        /**
         * Définit la valeur de la propriété z.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setZ(Object value) {
            this.z = value;
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
         *         &lt;element name="HISTOGRAM" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
            "histogram"
        })
        public static class X {

            @XmlElement(name = "HISTOGRAM", required = true)
            protected Object histogram;

            /**
             * Obtient la valeur de la propriété histogram.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getHISTOGRAM() {
                return histogram;
            }

            /**
             * Définit la valeur de la propriété histogram.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setHISTOGRAM(Object value) {
                this.histogram = value;
            }

        }

    }

}
