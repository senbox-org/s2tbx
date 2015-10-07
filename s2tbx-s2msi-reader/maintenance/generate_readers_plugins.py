#!/usr/bin/env python

import sys
import os

module_root = os.path.abspath( os.path.join( os.path.dirname(__file__),  os.pardir ) )
target_dir_java = os.path.join( module_root , 'src', 'main', 'java', 'org', 'esa', 's2tbx', 'dataio', 's2', 'l1c', 'plugins' )
target_file_spi = os.path.join( module_root , 'src', 'main', 'resources', 'META-INF', 'services', 'org.esa.snap.core.dataio.ProductReaderPlugIn' )

template = """
package org.esa.s2tbx.dataio.s2.l1c.plugins;

import org.esa.s2tbx.dataio.s2.l1c.{PARENT_CLASS};

/**
 * Reader plugin for S2 MSI L1C over {UTM_ZONE_DISPLAY_NAME}
 */
public class {READER_CLASS} extends {PARENT_CLASS} {{

    @Override
    public String getEPSG() {{
        return "{EPSG}";
    }}

}}
"""

def get_epsg(hemisphere, zone):
  
  if hemisphere == 'N':
    hemispherenumber = '6'
  else:
    hemispherenumber = '7'
    
  return "EPSG:32%s%s" % ( hemispherenumber , zone )

def get_parent_class(resolution):
  if resolution == "Multi":
    return "Sentinel2L1CProductReaderPlugIn"
  else:
    return "Sentinel2L1CProduct%sReaderPlugIn" % resolution

readers = []
for hemisphere in ['N', 'S'] :
  for zoneNumber in range(1, 61) :
    zone = "%02d" % zoneNumber
    epsg = get_epsg(hemisphere,zone)
    utm_zone_displayname = "WGS84 / UTM Zone %s %s" % (zone, hemisphere)
    
    for resolution in [ "10M", "20M", "60M", "Multi" ]:
      reader_class = "Sentinel2L1CProduct_%s_UTM%s%s_ReaderPlugIn" % (resolution, zone, hemisphere)
      parent_class = get_parent_class(resolution)
      code = template.format( READER_CLASS=reader_class, PARENT_CLASS=parent_class, UTM_ZONE_DISPLAY_NAME=utm_zone_displayname, EPSG=epsg )
      outfilename = os.path.join(target_dir_java, "{READER_CLASS}.java".format(READER_CLASS=reader_class))
      print "Writing to %s" % outfilename
      with open( outfilename , "w" ) as outfile:
        outfile.write(code)
      readers.append(reader_class)

print "Writing to %s" % target_file_spi
with open( target_file_spi , "w" ) as outfile:
  outfile.write("\n".join( ["org.esa.s2tbx.dataio.s2.l1c.plugins.%s" % r for r in readers]))

sys.exit(0)
