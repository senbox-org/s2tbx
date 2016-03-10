# -*- coding: utf-8 -*-

import sys
import glob
import os
from ToolAdapterGenerator import run_ToolAdapterGenerator


def getArguments():
    outputDirectory = sys.argv[1]
    if not os.path.isdir(outputDirectory):
        os.mkdir(outputDirectory)
    print sys.argv[3], bool(int(sys.argv[3]))
    return outputDirectory, os.path.realpath(sys.argv[2]), bool(int(sys.argv[3]))


if __name__ == '__main__':
    outputDirectory, directoryContainingProcessingXML, createAdapter = getArguments()
    print createAdapter
    listXmlFiles = glob.glob(os.path.join(directoryContainingProcessingXML, "*.xml"))
    print listXmlFiles
    for xmlProcessing in listXmlFiles:
        run_ToolAdapterGenerator(outputDirectory, xmlProcessing, createAdapter)