#! /usr/bin/python
# -*- coding: utf-8 -*-
"""
TODO: Header
TODO: ParameterChoice
TODO: Parameters
"""

import sys
import os
import shutil
import glob
import logging
import lxml.etree as ET
from xml.parsers.expat import ExpatError
from collections import OrderedDict



logging.basicConfig()
logger = logging.getLogger( 'ToolAdapterGenerator' )
logger.setLevel(logging.DEBUG)

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
    else:
        resourceDirectory = os.path.join(toolDirectory, "resources")
        createDirectory(resourceDirectory, "resourceDirectory")
        metaInfDirectory = os.path.join(resourceDirectory, "META-INF")

    createDirectory(metaInfDirectory, "metaInfDirectory")
    return resourceDirectory, metaInfDirectory, toolDirectory


def generateTemplateVM(outputDir, applicationName, param, lowerToolName):
    """
    Generates the file XX-template.vm
    :param outputDir: tool directory
    :param applicationName: name of the application
    :param param: string containing all parameters in right format
    :return: path to vm file
    """
    templateVM = os.path.join(outputDir, lowerToolName + "-template.vm")
    f = open(templateVM, "w")
    f.write(param)
    f.close()
    return templateVM


def getVariables(appName, envVarTool):
    """
    Creates the node variables
    <variables>
        <osvariable>
            <key>OTB_BIN_DIR</key>
            <value/>
            <windows/>
            <linux>/usr/bin</linux>
            <macosx/>
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
    submet = ET.SubElement(met, "isShared")
    submet.text = "true"

    met = ET.SubElement(root, "osvariable")

    submet = ET.SubElement(met, "key")
    submet.text = envVarTool
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
    submet = ET.SubElement(met, "isShared")
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


def getXMLRoot(applicationName, info, vmFile, version, envVarTool):
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
    met.text = "Menu/Optical/OrfeoToolbox"

    met = ET.SubElement(root, "preprocessTool")
    met.text = "false"

    met = ET.SubElement(root, "writeForProcessing")
    met.text = "false"

    met = ET.SubElement(root, "mainToolFileLocation")
    met.text = "$OTB_BIN_DIR/$" + envVarTool # +  info["exec"] #+ "$OTB_BIN_SUFFIX"

    met = ET.SubElement(root, "workingDir")
    met.text = "/tmp"

    met = ET.SubElement(root, "templateType")
    met.text = "VELOCITY"

    met = ET.SubElement(root, "template")
    templateFile = ET.SubElement(met, "file")
    templateFile.text = os.path.basename(vmFile)

    met = ET.SubElement(root, "progressPattern")
    met.text = ".*: (\d{1,3})%(?:.+)"

    met = ET.SubElement(root, "errorPattern")
    met.text = "(?:ERROR:|itk::ERROR)(.+)"


    return root


def addClosing(root, hasOutputRaster, inputParameterNames):
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
    # For all products, get the name
    met = ET.SubElement(root, "sourceProductDescriptors")
    for inputParameterName in inputParameterNames:
        submet = ET.SubElement(met, "org.esa.snap.core.gpf.descriptor.DefaultSourceProductDescriptor")
        subsubmet = ET.SubElement(submet, "name")
        subsubmet.text = inputParameterName

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
                          version, hasOutputRaster, envVarTool, inputParameterNames):
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

    root = getXMLRoot(applicationName, info, vmFile, version, envVarTool)
    root.append(getVariables(info["exec"], envVarTool))
    root.append(getParameters(XMLParamList))

    addClosing(root, hasOutputRaster, inputParameterNames)
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
                      "ParameterType_OutputVectorData": ['java.io.File', 'str'],
                      "ParameterType_InputFilename": ['java.io.File', 'str']

                      }

    orderParam = []
    hasOutputRaster = False
    inputParameterIndex = 0
    inputParameterNames = []

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
                    #dicParameters[key]["name"] = "sourceProductFile[{}]".format(inputParameterIndex)
                    dicParameters[key]["name"] = "sourceProductFile[{}]".format(inputParameterIndex)
                    inputParameterNames.append(param.xpath("./name/text()")[0])
                    inputParameterIndex += 1
                    logger.debug("Changed key: {}".format(dicParameters[key]))

                #case of output raster
                if typeParam == "ParameterType_OutputImage" and dicParameters[key]["type_processing2"] == "OutputRaster":
                    logger.debug("ParameterType_OutputImage & OutputRaster")
                    dicParameters[key]["name"] = "targetProductFile"
                    hasOutputRaster = True

                # case of output vector
                if typeParam == "ParameterType_OutputVectorData" and dicParameters[key][
                    "type_processing2"] == "OutputVector":
                    logger.debug("ParameterType_OutputVectorData & OutputVector")
                    dicParameters[key]["default"] = appName.lower() + ".shp"

                #case of choices
                if typeParam == "ParameterType_Choice":
                    print 'dicParameters[key]["default"] for', key, dicParameters[key]["default"]


                    logger.debug("ParameterType_Choice")
                    xpathToChoices = "./options/choices/choice"
                    r = param.xpath(xpathToChoices)
                    if r:
                        print r, len(r), "for", key
                        if len(r) == 1:
                            choices = r[0].text
                            print "choices", choices, "for", key, "for", appName
                            dicParameters[key]["default"] = choices

                    # Bug from xml processing files
                    if "default" in dicParameters[key] and dicParameters[key]["default"] in ["0", 0]:
                        print "WARNING: This value may be wrong !!", key, appName, " (from app", ")"

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
    commandLine = dicInfo["cli_args_template"].split()

    # create the template string
    for i in range(len(commandLine)):
        commandLineElement = commandLine[i]

        if commandLineElement.startswith("-"):
            #we check the parameter is in the dic (optional parameter without default are removed)
            paramKey = commandLineElement[1:len(commandLineElement)]
            if paramKey in dicParameters:
                stringVMList.append(commandLineElement)
                nextCommandLineElement = commandLine[i+1]
                #we replace parameters values surrouded by {}, we just append others
                if nextCommandLineElement.startswith("{") and nextCommandLineElement.endswith("}"):
                    parameterDic = dicParameters[paramKey]

                    if parameterDic["name"].startswith("sourceProductFile"):
                        logging.debug("Case of sourceProductFile")
                        if inputParameterIndex > 1:
                            paramName = parameterDic["name"]
                        else:
                            paramName ="sourceProductFile"
                    elif parameterDic["name"].startswith("targetProductFile"):
                        logging.debug("Case of targetProductFile")
                        paramName = "targetProductFile"

                    else:
                        # .split(".")[-1] to avoir paramName like mode.vector.out that does not work
                        paramName = parameterDic.get("key", "unknown_key").split(".")[-1] + "_" + \
                                    parameterDic.get("type", "unknown_type")[1]

                    stringVMList.append("$" + paramName)
                else:
                    stringVMList.append(nextCommandLineElement)

    stringVM = "\n".join(stringVMList)

    for parameterDesc in orderParam:  # , parameterDic in dicParameters.iteritems():

        parameterDic = dicParameters[parameterDesc]

        # print parameterDic["type"]
        logging.debug("Parameter type {}".format(parameterDic["type"]))


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
        if not parameterDic["name"].startswith("sourceProductFile") :

            if parameterDic["name"].startswith("targetProductFile") :
                paramName = "targetProductFile"
            else :
                paramName = parameterDic.get("key", "unknown_key").split(".")[-1] + "_" + \
                            parameterDic.get("type", "unknown_type")[1]

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
            #met = ET.SubElement(root, "toolParameterDescriptors")
            XMLParamList.append(root)



    return dicParameters, stringVM, XMLParamList, hasOutputRaster, inputParameterNames


def getInfoFromProcessingXML(xmlDescriptionProcessing):
    """
    Extract information from given xml.
    Returns a dictionnay with:
    """

    dicXpath = {
            "key":"./key",
            "longname":"./longname",
            "description": "./description",
            "exec": "./exec",
            "parameters": "./parameter",
            "cli_args_template": "./cli_args_template"

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
        raise MainXMLError("Error: Error parsing " + sourcePomFile +
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

def createManifest(toolDirectory, dicResult, version):
    """
    Creates MANIFEST.MF
    :param toolDirectory:
    :param dicResult:
    :return:
    """
    appKeySplitted = dicResult['key'].replace("-", ".")
    manifestFile = os.path.join(toolDirectory, "MANIFEST.MF")

    manifest = """\
Manifest-Version: 1.0
OpenIDE-Module-Specification-Version: {version}
OpenIDE-Module-Implementation-Version: {version}
OpenIDE-Module-Name: {appKeySplitted}
OpenIDE-Module-Display-Category: SNAP Supported Plugins
OpenIDE-Module-Java-Dependencies: Java > 1.8
OpenIDE-Module-Type: STA
OpenIDE-Module-Short-Description: {longName}
OpenIDE-Module: org.esa.s2tbx.{appKeySplitted}
OpenIDE-Module-Alias: {appKey}
OpenIDE-Module-Module-Dependencies: org.esa.snap.snap.sta, org.esa.snap.snap.sta.ui
AutoUpdate-Show-In-Client: false
""".format(version=version, appKeySplitted=appKeySplitted, longName=dicResult['longname'], appKey=dicResult['key'] )

    with open(manifestFile, "w") as f:
        f.write(manifest)

def run_ToolAdapterGenerator(outputDir, xmlDescriptionProcessing, createAdapter=False):
    dicResult, applicationName = getInfoFromProcessingXML(xmlDescriptionProcessing)

    lowerToolName = ''.join([x if x.lower() == x else "-"+x.lower() for x in dicResult["key"]])
    if lowerToolName.startswith("-"):
        lowerToolName = lowerToolName[1:]
    envVarTool = "OTB_BIN" + lowerToolName.upper().replace("-", "_") +"_EXEC"

    dicParameters, stringVM, XMLParamList, hasOutputRaster, inputParameterNames =manageToolParameters(dicResult)

    resourceDirectory, metaInfDirectory, toolDirectory = generateStructure(outputDir,
                                                                           lowerToolName,
                                                                           createAdapter)
    vmFile = generateTemplateVM(resourceDirectory, applicationName, stringVM, lowerToolName)
    rootVar = generateDescriptorXml(metaInfDirectory, applicationName, xmlDescriptionProcessing, vmFile, dicResult,
                          XMLParamList, "5.2", hasOutputRaster, envVarTool, inputParameterNames)

    if createAdapter:
        createPom(toolDirectory, dicResult, lowerToolName)
        createManifest(metaInfDirectory, dicResult, "6.0.0")



if __name__ == '__main__':
    outputDirectory = os.path.join(os.path.dirname(__file__), '..')
    
    dirlist = [folder for folder in os.listdir(outputDirectory) if os.path.isdir(os.path.join(outputDirectory, folder)) and folder not in ["maintenance", "s2tbx-otb-adapters-kit", ".sonar", "target"]]
    print dirlist
    for existingAdapter in dirlist:
        print os.path.abspath(os.path.join(outputDirectory, existingAdapter))
        shutil.rmtree( os.path.abspath(os.path.join(outputDirectory, existingAdapter)) )
    
    directoryContainingProcessingXML = os.path.join(os.path.dirname(__file__), 'xml')
    createAdapter = True
    listXmlFiles = glob.glob(os.path.join(directoryContainingProcessingXML, "*.xml"))
    for xmlProcessing in listXmlFiles:
        run_ToolAdapterGenerator(outputDirectory, xmlProcessing, createAdapter)
