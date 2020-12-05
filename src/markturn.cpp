/*
    markturn.cpp

    Client program for the libmarkturn utility
*/

#include <CLI11.hpp>
#include "markturn.hpp"
#include <curl/curl.h>

int main(int argc, char* argv[]) {

    // command-line parsing
    CLI::App app{"markturn among data formats"};

    std::string filename = "default";
    app.add_option("-f,--file", filename, "A help string");

    CLI11_PARSE(app, argc, argv);

    // ...

    // Conversions using libmarkturn

    return 0;
}
