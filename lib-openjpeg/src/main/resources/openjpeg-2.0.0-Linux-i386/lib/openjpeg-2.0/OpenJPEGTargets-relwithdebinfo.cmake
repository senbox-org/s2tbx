#----------------------------------------------------------------
# Generated CMake target import file for configuration "RelWithDebInfo".
#----------------------------------------------------------------

# Commands may need to know the format version.
SET(CMAKE_IMPORT_FILE_VERSION 1)

# Compute the installation prefix relative to this file.
GET_FILENAME_COMPONENT(_IMPORT_PREFIX "${CMAKE_CURRENT_LIST_FILE}" PATH)
GET_FILENAME_COMPONENT(_IMPORT_PREFIX "${_IMPORT_PREFIX}" PATH)
GET_FILENAME_COMPONENT(_IMPORT_PREFIX "${_IMPORT_PREFIX}" PATH)

# Import target "openjp2" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET openjp2 APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(openjp2 PROPERTIES
  IMPORTED_LINK_INTERFACE_LIBRARIES_RELWITHDEBINFO "m"
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/lib/libopenjp2.so.2.0.0"
  IMPORTED_SONAME_RELWITHDEBINFO "libopenjp2.so.6"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS openjp2 )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_openjp2 "${_IMPORT_PREFIX}/lib/libopenjp2.so.2.0.0" )

# Import target "openjpwl" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET openjpwl APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(openjpwl PROPERTIES
  IMPORTED_LINK_INTERFACE_LIBRARIES_RELWITHDEBINFO "m"
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/lib/libopenjpwl.so.2.0.0"
  IMPORTED_SONAME_RELWITHDEBINFO "libopenjpwl.so.6"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS openjpwl )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_openjpwl "${_IMPORT_PREFIX}/lib/libopenjpwl.so.2.0.0" )

# Import target "openjpip" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET openjpip APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(openjpip PROPERTIES
  IMPORTED_LINK_INTERFACE_LIBRARIES_RELWITHDEBINFO "openjp2"
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/lib/libopenjpip.so.2.0.0"
  IMPORTED_SONAME_RELWITHDEBINFO "libopenjpip.so.6"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS openjpip )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_openjpip "${_IMPORT_PREFIX}/lib/libopenjpip.so.2.0.0" )

# Import target "opj_decompress" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_decompress APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_decompress PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_decompress"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS opj_decompress )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_opj_decompress "${_IMPORT_PREFIX}/bin/opj_decompress" )

# Import target "opj_compress" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_compress APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_compress PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_compress"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS opj_compress )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_opj_compress "${_IMPORT_PREFIX}/bin/opj_compress" )

# Import target "opj_dump" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_dump APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_dump PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_dump"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS opj_dump )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_opj_dump "${_IMPORT_PREFIX}/bin/opj_dump" )

# Import target "opj_jpip_addxml" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_jpip_addxml APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_jpip_addxml PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_jpip_addxml"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS opj_jpip_addxml )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_opj_jpip_addxml "${_IMPORT_PREFIX}/bin/opj_jpip_addxml" )

# Import target "opj_dec_server" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_dec_server APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_dec_server PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_dec_server"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS opj_dec_server )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_opj_dec_server "${_IMPORT_PREFIX}/bin/opj_dec_server" )

# Import target "opj_jpip_transcode" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_jpip_transcode APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_jpip_transcode PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_jpip_transcode"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS opj_jpip_transcode )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_opj_jpip_transcode "${_IMPORT_PREFIX}/bin/opj_jpip_transcode" )

# Import target "opj_jpip_test" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_jpip_test APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_jpip_test PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_jpip_test"
  )

LIST(APPEND _IMPORT_CHECK_TARGETS opj_jpip_test )
LIST(APPEND _IMPORT_CHECK_FILES_FOR_opj_jpip_test "${_IMPORT_PREFIX}/bin/opj_jpip_test" )

# Loop over all imported files and verify that they actually exist
FOREACH(target ${_IMPORT_CHECK_TARGETS} )
  FOREACH(file ${_IMPORT_CHECK_FILES_FOR_${target}} )
    IF(NOT EXISTS "${file}" )
      MESSAGE(FATAL_ERROR "The imported target \"${target}\" references the file
   \"${file}\"
but this file does not exist.  Possible reasons include:
* The file was deleted, renamed, or moved to another location.
* An install or uninstall procedure did not complete successfully.
* The installation package was faulty and contained
   \"${CMAKE_CURRENT_LIST_FILE}\"
but not all the files it references.
")
    ENDIF()
  ENDFOREACH()
  UNSET(_IMPORT_CHECK_FILES_FOR_${target})
ENDFOREACH()
UNSET(_IMPORT_CHECK_TARGETS)

# Cleanup temporary variables.
SET(_IMPORT_PREFIX)

# Commands beyond this point should not need to know the version.
SET(CMAKE_IMPORT_FILE_VERSION)
