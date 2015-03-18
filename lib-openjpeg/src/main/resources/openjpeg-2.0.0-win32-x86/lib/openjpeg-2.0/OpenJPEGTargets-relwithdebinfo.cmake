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
  IMPORTED_IMPLIB_RELWITHDEBINFO "${_IMPORT_PREFIX}/lib/openjp2.lib"
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/openjp2.dll"
  )

# Import target "openjpwl" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET openjpwl APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(openjpwl PROPERTIES
  IMPORTED_IMPLIB_RELWITHDEBINFO "${_IMPORT_PREFIX}/lib/openjpwl.lib"
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/openjpwl.dll"
  )

# Import target "openjpip" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET openjpip APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(openjpip PROPERTIES
  IMPORTED_IMPLIB_RELWITHDEBINFO "${_IMPORT_PREFIX}/lib/openjpip.lib"
  IMPORTED_LINK_INTERFACE_LIBRARIES_RELWITHDEBINFO "openjp2;ws2_32"
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/openjpip.dll"
  )

# Import target "opj_decompress" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_decompress APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_decompress PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_decompress.exe"
  )

# Import target "opj_compress" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_compress APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_compress PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_compress.exe"
  )

# Import target "opj_dump" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_dump APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_dump PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_dump.exe"
  )

# Import target "opj_jpip_addxml" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_jpip_addxml APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_jpip_addxml PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_jpip_addxml.exe"
  )

# Import target "opj_dec_server" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_dec_server APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_dec_server PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_dec_server.exe"
  )

# Import target "opj_jpip_transcode" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_jpip_transcode APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_jpip_transcode PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_jpip_transcode.exe"
  )

# Import target "opj_jpip_test" for configuration "RelWithDebInfo"
SET_PROPERTY(TARGET opj_jpip_test APPEND PROPERTY IMPORTED_CONFIGURATIONS RELWITHDEBINFO)
SET_TARGET_PROPERTIES(opj_jpip_test PROPERTIES
  IMPORTED_LOCATION_RELWITHDEBINFO "${_IMPORT_PREFIX}/bin/opj_jpip_test.exe"
  )

# Cleanup temporary variables.
SET(_IMPORT_PREFIX)

# Commands beyond this point should not need to know the version.
SET(CMAKE_IMPORT_FILE_VERSION)
