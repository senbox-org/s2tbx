# -*- coding: utf-8 -*-
"""
TODO: Header
TODO: ParameterChoice
TODO: Parameters
"""

import sys
import os
import lxml.etree as ET
from xml.parsers.expat import ExpatError
from collections import OrderedDict


#import loggin for debug messages
import logging
logging.basicConfig()
# create logger
logger = logging.getLogger( 'ToolAdapterGenerator' )
logger.setLevel(logging.DEBUG)
fh = logging.FileHandler("/tmp/s2TBX.log")
formatter = logging.Formatter('%(asctime)s %(name)s %(levelname)s - %(message)s')
fh.setFormatter(formatter)
fh.setLevel(logging.DEBUG)
logger.addHandler(fh)



class ExceptionTemplate(Exception):

    def __call__(self, *args):
        return self.__class__(*(self.args + args))

    def __str__(self):
        return ': '.join(self.args)


class MainXMLError(ExceptionTemplate):
    pass


def getArguments():
    """
    TBD
    :return:
    """
    outputDirectory = sys.argv[1]
    if not os.path.isdir(outputDirectory):
        os.mkdir(outputDirectory)
    return outputDirectory, sys.argv[2], bool(sys.argv[3])


def createDirectory(path, name):
    """
    Creates the given directory if it does not exist
    :param path: path of the directory to create
    :param name: short description of the directory
    :return:
    """
    logging.info("Create {name} directory: {pathD}".format(name=name, pathD=path))
    if not os.path.isdir(path):
        os.mkdir(path)

def createDirectories(path, name):
    """
    Creates the given directory if it does not exist.
    Creates all parent directories if needed
    :param path: path of the directory to create
    :param name: short description of the directory
    :return:
    """
    logging.info("Create {name} directory: {pathD}".format(name=name, pathD=path))
    if not os.path.isdir(path):
        os.makedirs(path)


def touchFile(path, name):
    """
    Creates the given file if it does not exist
    :param path: path of the file to create
    :param name: short description of the file
    :return:
    """
    logging.info("Create {name} file: {pathD}".format(name=name, pathD=path))
    if not os.path.isfile(path):
        os.system("touch " + path)


def generateStructure(outputDir, lowerToolName, createAdapter=False):
    """
    Creates the structure of the folders of the tool adapter

    createAdapter=False
    MeanShiftSmoothing/
        ├── MeanShiftSmoothing-template.vm
        └── META-INF
            └── descriptor.xml

    createAdapter=True
    sen2cor/
        ├── pom.xml
        └── resources
            ├── META-INF
            │   ├── descriptor.xml
            │   └── MANIFEST.MF
            ├── org
            │   └── esa
            │       └── snap
            │           └── ui
            │               └── tooladapter
            │                   └── layer.xml
            ├── Sen2Cor-post-template.vm


    :param outputDir: directory in which is created the tree
    :param lowerToolName: name of the tool
    :return: created directories
    """
    toolDirectory = os.path.join(outputDir, lowerToolName)
    createDirectory(toolDirectory, "toolDirectory")
    if not createAdapter:
        metaInfDirectory = os.path.join(toolDirectory, "META-INF")
        resourceDirectory = toolDirectory
        layerDirectory = None
    else:
        resourceDirectory = os.path.join(toolDirectory, "resources")
        createDirectory(resourceDirectory, "resourceDirectory")
        metaInfDirectory = os.path.join(resourceDirectory, "META-INF")
        layerDirectory = os.path.join(resourceDirectory, "org", "esa", "snap", "ui", "tooladapter")
        createDirectories(layerDirectory, "layerDirectory")
    createDirectory(metaInfDirectory, "metaInfDirectory")
    return resourceDirectory, metaInfDirectory, toolDirectory, layerDirectory


def generateTemplateVM(outputDir, applicationName, param):
    """
    Generates the file XX-template.vm
    :param outputDir: tool directory
    :param applicationName: name of the application
    :param param: string containing all parameters in right format
    :return: path to vm file
    """
    templateVM = os.path.join(outputDir, applicationName + "-template.vm")
    f = open(templateVM, "w")
    f.write(param)
    f.close()
    return templateVM


def getVariables(appName):
    """
    Creates the node variables
    <variables>
        <osvariable>
            <key>OTB_BIN_DIR</key>
            <value></value>
            <windows>C:/Documents/TOto</windows>
            <linux>/usr/bin</linux>
            <macosx>ValueForMac</macosx>
            <isTransient>false</isTransient>
        </osvariable>
    </variables>
    :return:
    """
    root = ET.Element("variables")

    met = ET.SubElement(root, "osvariable")

    submet = ET.SubElement(met, "key")
    submet.text = "OTB_BIN_DIR"
    submet = ET.SubElement(met, "value")
    submet.text = ""
    submet = ET.SubElement(met, "windows")
    submet.text = ""
    submet = ET.SubElement(met, "linux")
    submet.text = "/usr/bin"
    submet = ET.SubElement(met, "macosx")
    submet.text = ""
    submet = ET.SubElement(met, "isTransient")
    submet.text = "false"


    met = ET.SubElement(root, "osvariable")

    submet = ET.SubElement(met, "key")
    submet.text = "OTB_BIN_EXEC"
    submet = ET.SubElement(met, "value")
    submet.text = ""
    submet = ET.SubElement(met, "windows")
    submet.text = appName +".bat"
    submet = ET.SubElement(met, "linux")
    submet.text = appName
    submet = ET.SubElement(met, "macosx")
    submet.text = appName
    submet = ET.SubElement(met, "isTransient")
    submet.text = "false"


    # met = ET.SubElement(root, "osvariable")
    #
    # submet = ET.SubElement(met, "key")
    # submet.text = "OTB_BIN_SUFFIX"
    # submet = ET.SubElement(met, "value")
    # submet.text = ""
    # submet = ET.SubElement(met, "windows")
    # submet.text = ".bat"
    # submet = ET.SubElement(met, "linux")
    # submet.text = ""
    # submet = ET.SubElement(met, "macosx")
    # submet.text = ""
    # submet = ET.SubElement(met, "isTransient")
    # submet.text = "false"

    return root


def getXMLRoot(applicationName, info, vmFile, version):
    """
    Gets the root xml of the description.xml of the tool
    :param applicationName: name of the application
    :param info: dictionary containing information extracted from the xml of processing module
    :param vmFile: path to the vm template file
    :param version: version of the tool
    :return: the root
    """
    root = ET.Element("operator")

    met = ET.SubElement(root, "name")
    met.text = applicationName

    met = ET.SubElement(root, "operatorClass")
    met.text = "org.esa.snap.core.gpf.operators.tooladapter.ToolAdapterOp"

    met = ET.SubElement(root, "alias")
    met.text = applicationName

    met = ET.SubElement(root, "label")
    met.text = applicationName

    met = ET.SubElement(root, "version")
    met.text = version

    met = ET.SubElement(root, "description")
    met.text = info["description"]

    met = ET.SubElement(root, "internal")
    met.text = "false"

    met = ET.SubElement(root, "autoWriteSuppressed")
    met.text = "false"

    met = ET.SubElement(root, "menuLocation")
    met.text = "Menu/Raster/Image Analysis"

    met = ET.SubElement(root, "preprocessTool")
    met.text = "false"

    met = ET.SubElement(root, "writeForProcessing")
    met.text = "false"

    met = ET.SubElement(root, "mainToolFileLocation")
    met.text = "$OTB_BIN_DIR/$OTB_BIN_EXEC" # +  info["exec"] #+ "$OTB_BIN_SUFFIX"

    met = ET.SubElement(root, "workingDir")
    met.text = "/tmp"

    met = ET.SubElement(root, "templateFileLocation")
    met.text = os.path.basename(vmFile)

    met = ET.SubElement(root, "progressPattern")
    met.text = ": (\d{1,3})%(?:.+)"

    met = ET.SubElement(root, "errorPattern")
    met.text = "itk::ERROR(.+)"


    return root


def addClosing(root, hasOutputRaster):
    """
    Add last xml fields for root
    <source>user</source>
    <isSystem>false</isSystem>
    <isHandlingOutputName>true</isHandlingOutputName>
    <sourceProductDescriptors>
    <org.esa.snap.core.gpf.descriptor.DefaultSourceProductDescriptor>
    <name>sourceProduct 1</name>
    </org.esa.snap.core.gpf.descriptor.DefaultSourceProductDescriptor>
    </sourceProductDescriptors>
    <targetPropertyDescriptors/>
    <numSourceProducts>1</numSourceProducts>

    :param root: root xml to modify
    :return:
    """

    met = ET.SubElement(root, "source")
    met.text = "user"

    met = ET.SubElement(root, "isSystem")
    met.text = "false"

    #TBD
    met = ET.SubElement(root, "isHandlingOutputName")
    met.text = str(not hasOutputRaster).lower()

    #TBD
    met = ET.SubElement(root, "sourceProductDescriptors")
    submet = ET.SubElement(met, "org.esa.snap.core.gpf.descriptor.DefaultSourceProductDescriptor")
    subsubmet = ET.SubElement(submet, "name")
    subsubmet.text = "sourceProduct 1"

    #TBD for raster is true
    met = ET.SubElement(root, "targetPropertyDescriptors")

    #TBD
    met = ET.SubElement(root, "numSourceProducts")
    met.text = "1"


def getParameters(XMLParamList):
    """
    Add each <parameter> element from the XMLParamList ti the root
    :param XMLParamList:
    :return: xml root
    """
    root = ET.Element("parameters")
    for el in XMLParamList:
        root.append(el)

    return root


def generateDescriptorXml(outputDir, applicationName, xmlDescriptionProcessing, vmFile, info, XMLParamList,
                          version, hasOutputRaster):
    """
    Generate the description.xml file which contains the description of parameters
    :param outputDir:
    :param applicationName:
    :param xmlDescriptionProcessing:
    :param vmFile:
    :param info:
    :param XMLParamList:
    :param version:
    :return:
    """
    descriptorXml = os.path.join(outputDir, "descriptor.xml")

    root = getXMLRoot(applicationName, info, vmFile, version)
    root.append(getVariables(info["exec"]))
    root.append(getParameters(XMLParamList))

    addClosing(root, hasOutputRaster)
    tree = ET.ElementTree(root)

    f = open(descriptorXml, "w")
    f.write(ET.tostring(tree, pretty_print=True,encoding="UTF-8")) # xml_declaration=True, standalone='No',

    f.close()


def manageToolParameters(dicInfo):
    """
    For each <parameter> from processing xml file, extract fields and
    generate the string for XX-template.vm and xml node for description.xml
    :param dicInfo: parameters
    :return:
    """
    appName = dicInfo["key"]
    toolsProcessingParameters = dicInfo["parameters"]
    dicParameters = {}

    xpathParam = {
                "key": "./key/text()",
                "name": "./name/text()",
                "description": "./description/text()",
                "default": "./default/text()",
                "type":"./parameter_type/@source_parameter_type",
                "type_processing2":"./parameter_type/text()",
                "optional": "./optional/text()"
    }

    #dictionnary to convert processing type into s2tbx type
    typeConversion = {'ParameterType_Float': ['java.lang.Float', 'float'],
                      'ParameterType_Int': ['java.lang.Integer', 'int'],
                      'ParameterType_String': ['java.lang.String', 'string'],
                      'ParameterType_Choice': ['java.lang.String', 'string'],
                      'ParameterType_Empty': ['java.lang.Boolean', 'bool'], # TBD
                      "ParameterType_InputImage": ['java.io.File', 'str'],
                      "ParameterType_OutputImage": ['java.io.File', 'str'],
                      "ParameterType_OutputVectorData": ['java.io.File', 'str']
    }

    orderParam = []
    hasOutputRaster = False
    # go throught all parameters, get information to create S2TBX xml
    for param in toolsProcessingParameters:

        key = param.xpath("./key/text()")[0]
        logger.debug("key {}".format(key))
        dicParameters[key] = OrderedDict({})

        #tree = ET.ElementTree(param)
        #print "TOTO", ET.tostring(tree, pretty_print=True, encoding="UTF-8")
        for descriptionP, xpathP in xpathParam.iteritems():
            #print "description", descriptionP
            r = param.xpath(xpathP)
            if len(r) == 1:
                response = r[0]
                dicParameters[key][descriptionP] = response
                #print "dicParameters[", key, "][", descriptionP, "]", dicParameters[key][descriptionP]

        # Removing all parameters that are optional but without default value
        if "optional" in dicParameters[key] and dicParameters[key]["optional"] == "True" and not "default" in dicParameters[key]:
            logging.info("Removing {}".format(dicParameters[key]))
            del dicParameters[key]
            continue

        else:
            if "type" in dicParameters[key] and "type_processing2" in dicParameters[key]:
                typeParam = dicParameters[key]["type"]
                #do not manage empty parameters that are not boolean
                if typeParam == "ParameterType_Empty" and not dicParameters[key]["type_processing2"] == "ParameterBoolean":
                    logging.info("Removing {}".format(dicParameters[key]))
                    del dicParameters[key]
                    continue

                #case of input raster
                # WARNING : suppose that there is only one raster input, else use sourceProductFile
                if typeParam == "ParameterType_InputImage" and dicParameters[key]["type_processing2"] == "ParameterRaster":
                    logger.debug("ParameterType_InputImage & ParameterRaster")
                    dicParameters[key]["name"] = "sourceProductFile"
                    logger.debug("Changed key: {}".format(dicParameters[key]))

                #case of output raster
                if typeParam == "ParameterType_OutputImage" and dicParameters[key]["type_processing2"] == "OutputRaster":
                    logger.debug("ParameterType_OutputImage & OutputRaster")
                    dicParameters[key]["name"] = "targetProductFile"
                    logger.debug("Changed key: {}".format(dicParameters[key]))
                    hasOutputRaster = True

                #case of output vector
                if typeParam == "ParameterType_OutputVectorData" and dicParameters[key]["type_processing2"] == "OutputVector":
                    logger.debug("ParameterType_OutputVectorData & OutputVector")
                    dicParameters[key]["default"] = appName.lower() + ".shp"

                #case of choices
                if typeParam == "ParameterType_Choice":
                    print 'dicParameters[key]["default"] for', key, dicParameters[key]["default"]
                    logger.debug("ParameterType_Choice")
                    xpathToChoices = "./options/choices/choice"
                    r = param.xpath(xpathToChoices)
                    if r:
                        print r, len(r)
                        if len(r) == 1:
                            choices = r[0].text
                            print choices
                            dicParameters[key]["default"] = choices
                    print 'dicParameters[key]["default"] for', key, dicParameters[key]["default"]

                #convert type
                # set other type to unknown
                if typeParam in typeConversion:
                    dicParameters[key]["type"] = typeConversion[typeParam]
                else:
                    dicParameters[key]["type"] = ["Unknown", "Unknown"]
                    #do not manage unknown types
                    # print "remove", key
                    logging.info("Removing {}".format(dicParameters[key]))
                    del dicParameters[key]
                    continue

        if key in dicParameters:
            orderParam.append(key)
    logging.info("Order for parameters {}".format(orderParam))
    # print orderParam

    logger.debug("dicParameters {}".format(dicParameters))

    stringVMList = []
    XMLParamList = []
    for parameterDesc in orderParam:  # , parameterDic in dicParameters.iteritems():
        parameterDic = dicParameters[parameterDesc]

        # print parameterDic["type"]
        logging.debug("Parameter type {}".format(parameterDic["type"]))

        if parameterDic["name"] == "sourceProductFile":
            logging.debug("Case of sourceProductFile")
            paramName = "sourceProductFile"
        elif parameterDic["name"] == "targetProductFile":
            logging.debug("Case of targetProductFile")
            paramName = "targetProductFile"
        else:
            #.split(".")[-1] to avoir paramName like mode.vector.out that does not work
            paramName = parameterDic.get("key", "unknown_key").split(".")[-1] + "_" + parameterDic.get("type", "unknown_type")[1]
        #creating string for vm file
        stringVMList.append("-" + parameterDic.get("key", "unknown_key"))
        stringVMList.append("$" +paramName)

        #creating node
        # <parameter>
        #   <name>neighboor_boolean</name>
        #   <alias>8-neighbor connectivity</alias>
        #   <dataType>java.lang.Boolean</dataType>
        #   defaultValue
        #   <description>8-neighbor connectivity</description>
        #   <valueSet/>
        #   <notNull>false</notNull>
        #   <notEmpty>false</notEmpty>
        #   <parameterType>RegularParameter</parameterType>
        #   <toolParameterDescriptors/>
        # </parameter>
        root = ET.Element("parameter")

        met = ET.SubElement(root, "name")
        met.text = paramName
        met = ET.SubElement(root, "alias")
        met.text = parameterDic.get("name", "unknown_description")
        met = ET.SubElement(root, "dataType")
        met.text = parameterDic.get("type", "unknown_type")[0]
        if "default" in parameterDic:
            met = ET.SubElement(root, "defaultValue")
            met.text = parameterDic["default"]
        met = ET.SubElement(root, "description")
        met.text = parameterDic.get("description", "unknown_description")
        met = ET.SubElement(root, "valueSet")
        met = ET.SubElement(root, "notNull")
        met.text = "false"
        met = ET.SubElement(root, "notEmpty")
        met.text = "false"
        #TBD
        met = ET.SubElement(root, "parameterType")
        met.text = "RegularParameter"
        met = ET.SubElement(root, "toolParameterDescriptors")
        XMLParamList.append(root)


    stringVM = "\n".join(stringVMList)

    return dicParameters, stringVM, XMLParamList, hasOutputRaster





def getInfoFromProcessingXML(xmlDescriptionProcessing):
    """
    Extract information from given xml.
    Returns a dictionnay with:
    """

    dicXpath = {
            "key":"./key",
            "description": "./description",
            "exec": "./exec",
            "parameters": "./parameter"
    }
    dicResult = {}

    try:
        dom = ET.parse(xmlDescriptionProcessing)
    except ExpatError:
        raise MainXMLError("Error: Error parsing " + xmlDescriptionProcessing +
                           ". This XML is not readeable.")
    else:
        root = dom.getroot()
        root.findall(".")
        for description, xpath in dicXpath.iteritems():
            r = root.xpath(xpath)
            if len(r) == 1:
                response = r[0].text
            elif len(r) == 0:
                logging.info("{} is missing".format(description))
                response = None

            else:
                response = r #[el.text for el in r]
            dicResult[description] = response

    applicationName = dicResult.get("key", os.path.splitext(os.path.basename(xmlDescriptionProcessing))[0])
    logging.info("Application name: {}".format(applicationName))
    #print dicResult
    return dicResult, applicationName



def createPom(toolDirectory, dicResult, lowerToolName):
    """
    Create pom.xml file
    :param toolDirectory:
    :param dicResult:
    :return:
    """
    sourcePomFile = os.path.join(os.path.dirname(__file__), "pom.xml")
    pomXml = os.path.join(toolDirectory, "pom.xml")
    try:
        dom = ET.parse(sourcePomFile)
    except ExpatError:
        raise MainXMLError("Error: Error parsing " + xmlDescriptionProcessing +
                           ". This XML is not readeable.")
    else:
        dicXpath = {
                    "id":"//a:project/a:artifactId",
                    "name": "//a:project/a:name",
                    "description": "//a:project/a:description",
                   }
        newValues = {
                    "id":lowerToolName,
                    "name": "Sentinel-2 " + dicResult["key"],
                    "description": dicResult["description"],
                    }
        root = dom.getroot()
        for key, xpath in dicXpath.iteritems():
            logging.debug("key: {key}, xpath: {xpath}".format(key=key, xpath=xpath))
            newValue = newValues[key]
            r = root.xpath(xpath, namespaces={"a":"http://maven.apache.org/POM/4.0.0"})
            # print r
            if len(r) == 1:
                response = r[0]
                response.text = newValue

        tree = ET.ElementTree(root)
        f = open(pomXml, "w")
        f.write(ET.tostring(dom, pretty_print=True,encoding="UTF-8", xml_declaration=True, standalone='No'))

        f.close()


def createLayer(layerDirectory, dicResult):
    """
    Create layer.xml file
    :param layerDirectory:
    :param dicResult:
    :return:
    """
    sourceLayerFile = os.path.join(os.path.dirname(__file__), "layer.xml")
    layerXml = os.path.join(layerDirectory, "layer.xml")
    try:
        dom = ET.parse(sourceLayerFile)
    except ExpatError:
        raise MainXMLError("Error: Error parsing " + xmlDescriptionProcessing +
                           ". This XML is not readeable.")
    else:
        stringValueXpath = '//filesystem/folder[@name="Actions"]/folder[@name="Tools"]/attr[@name="displayName"]'
        newValue = dicResult["key"]

        root = dom.getroot()
        r = root.xpath(stringValueXpath)
        # print r
        if len(r) == 1:
            response = r[0]
            response.set("stringvalue", newValue)

        tree = ET.ElementTree(root)
        f = open(layerXml, "w")
        f.write(ET.tostring(dom, pretty_print=True,encoding="UTF-8")) # xml_declaration=True, standalone='No',

        f.close()


def createManifest(toolDirectory, dicResult, version):
    """
    Creates MANIFEST.MF
    :param toolDirectory:
    :param dicResult:
    :return:
    """
    manifestFile = os.path.join(toolDirectory, "MANIFEST.MF")
    stringFile = 'Manifest-Version: 1.0\n\
OpenIDE-Module-Specification-Version: ' + version + '\n\
OpenIDE-Module-Implementation-Version: ' + version + '\n\
OpenIDE-Module-Name: ' + dicResult['key'] + '\n\
OpenIDE-Module-Display-Category: Sentinel-2 Toolbox\n\
OpenIDE-Module-Java-Dependencies: Java > 1.8\n\
OpenIDE-Module-Type: STA\n\
OpenIDE-Module-Short-Description: ' + dicResult['key'] + '\n\
OpenIDE-Module: org.esa.s2tbx.' + dicResult['key'] + '\n\
OpenIDE-Module-Alias: ' + dicResult['key'] + '\n\
OpenIDE-Module-Module-Dependencies: org.esa.snap.snap.sta, org.esa.snap.snap.sta.ui\n\
OpenIDE-Module-Install: org/esa/snap/utils/ModuleInstaller.class\n\
AutoUpdate-Show-In-Client: false'
    f = open(manifestFile, "w")
    f.write(stringFile)
    f.close()





def run_ToolAdapterGenerator(outputDir, xmlDescriptionProcessing, createAdapter=False):
    dicResult, applicationName = getInfoFromProcessingXML(xmlDescriptionProcessing)

    lowerToolName = ''.join([x if x.lower() == x else "-"+x.lower() for x in dicResult["key"]])
    if lowerToolName.startswith("-"):
        lowerToolName = lowerToolName[1:]

    dicParameters, stringVM, XMLParamList, hasOutputRaster =manageToolParameters(dicResult)

    resourceDirectory, metaInfDirectory, toolDirectory, layerDirectory = generateStructure(outputDir,
                                                                                           lowerToolName,
                                                                                           createAdapter)
    vmFile = generateTemplateVM(resourceDirectory, applicationName, stringVM)
    generateDescriptorXml(metaInfDirectory, applicationName, xmlDescriptionProcessing, vmFile, dicResult,
                          XMLParamList, "5.2", hasOutputRaster)

    if createAdapter:
        createPom(toolDirectory, dicResult, lowerToolName)
        createLayer(layerDirectory, dicResult)
        createManifest(metaInfDirectory, dicResult, "3.0.0")



if __name__ == '__main__':
    """
    """
    outputDir, xmlDescriptionProcessing, createAdapter = getArguments()
    run_ToolAdapterGenerator(outputDir, xmlDescriptionProcessing, createAdapter)