# Build for markturn

cmake_minimum_required(VERSION 3.18.4)

project(markturn VERSION "0.9.5")

option(LINK_STATIC "Build with static library" OFF)

find_package(CURL REQUIRED)
message("-- libcurl include: " ${CURL_INCLUDE_DIRS})
message("-- libcurl library: " ${CURL_LIBRARY})
find_package(LibXml2 REQUIRED) # @NOTE Since you have multiple instructions for each library, leave a blank line between these sections
message("-- Libxml2 include: " ${LIBXML2_INCLUDE_DIR})
message("-- libxml2 library: " ${LIBXML2_LIBRARY})

# @NOTE Why is this commented out? Delete or explain.
# Determine distribution
#if(EXISTS /etc/os-release)
#    file(STRINGS /etc/os-release DISTRO REGEX "^NAME=")
#endif()

# Determine appropriate JSON include path for Ubuntu
#if(DISTRO AND DISTRO MATCHES "Ubuntu")
#    set(JSON_INCLUDE_DIR "/usr/include/jsoncpp")
#endif()
#
# Determine appropriate JSON include path for Fedora
#if(DISTRO AND DISTRO MATCHES "Fedora")
#    set(JSON_INCLUDE_DIR "/usr/include/json")
#endif()

# Set paths for YAML and JSON depending on Linux distribution
find_path(JSON_INCLUDE_DIR REQUIRED NAMES json/json.h PATHS /usr/include/jsoncpp /usr/include/json)
find_path(YAML_INCLUDE_DIR REQUIRED NAMES yaml.h PATHS /usr/include/yaml-cpp)

# Builds markturn
set(CLIENT_SOURCES "src/markturn.cpp")
add_executable(markturn ${CLIENT_SOURCES})
if(LINK_STATIC)
    message("-- static library")
    target_link_libraries(markturn PUBLIC markturn_static)
    target_include_directories(markturn PUBLIC ${LIBXML2_INCLUDE_DIR})
else()
    message("-- shared library")
    target_link_libraries(markturn PUBLIC markturn_shared)
    target_include_directories(markturn PUBLIC ${LIBXML2_INCLUDE_DIR})
endif()
install(TARGETS markturn RUNTIME)

# Object library markturnobj
set(LIBRARY_INCLUDE_DIRS ${YAML_INCLUDE_DIR} ${JSON_INCLUDE_DIR} ${LIBXML2_INCLUDE_DIR})
file(GLOB LIBRARY_SOURCES "src/libmarkturn.cpp" "src/*Format.cpp")
add_library(markturnobj OBJECT ${LIBRARY_SOURCES})
target_include_directories(markturnobj PUBLIC ${LIBRARY_INCLUDE_DIRS})

# Static libmarkturn library
add_library(markturn_static STATIC $<TARGET_OBJECTS:markturnobj>)
target_link_libraries(markturn_static PUBLIC ${CURL_LIBRARY})
set_target_properties(markturn_static PROPERTIES OUTPUT_NAME markturn)
install(TARGETS markturn_static LIBRARY)

# Shared libmarkturn library
add_library(markturn_shared SHARED $<TARGET_OBJECTS:markturnobj>)
target_link_libraries(markturn_shared PUBLIC ${CURL_LIBRARY})
set_target_properties(markturn_shared PROPERTIES OUTPUT_NAME markturn)
set_target_properties(markturn_shared PROPERTIES VERSION ${PROJECT_VERSION})
set_target_properties(markturn_shared PROPERTIES SOVERSION ${PROJECT_VERSION_MAJOR})
install(TARGETS markturn_shared LIBRARY)

# Setup testing
enable_testing()

# all .cpp files in test/ are independent test programs
file(GLOB TEST_SOURCES test/*.cpp)
foreach(TESTFILE ${TEST_SOURCES})
    get_filename_component(TEST_NAME ${TESTFILE} NAME_WLE)
    add_executable(${TEST_NAME} ${TESTFILE})
    if(LINK_STATIC)
        target_link_libraries(${TEST_NAME} PUBLIC markturn_static)
        target_include_directories(${TEST_NAME} PUBLIC "${CMAKE_SOURCE_DIR}/src")
    else()
        target_link_libraries(${TEST_NAME} PUBLIC markturn_shared)
        target_include_directories(${TEST_NAME} PUBLIC "${CMAKE_SOURCE_DIR}/src")
    endif()
#    message("-- Added test ${TEST_NAME} command ${TEST_NAME} program ${TESTFILE}")
    add_test(NAME ${TEST_NAME} COMMAND ${TEST_NAME})
endforeach()

# @NOTE Comment needed here
set(CPACK_PACKAGE_VERSION_MAJOR "1")
set(CPACK_PACKAGE_VERSION_MINOR "0")
set(CPACK_PACKAGE_VERSION_PATCH "0")
set(CPACK_PACKAGE_CONTACT "dmr136@zips.uakron.edu")
include(CPack)
