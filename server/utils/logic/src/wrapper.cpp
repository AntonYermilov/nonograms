#include <string>
#include <iostream>

#include <image.h>
#include <nonogram_config.h>
#include <nonogram_solver.h>
#include <nonogram_creator.h>
#include <json.hpp>

using json = nlohmann::json;
using std::string;

extern "C" {
    bool checkSolvability(const char* data) {
        Image image(json::parse(data));
        
        NonogramConfig config(image);
        NonogramSolver solver(config);
        return solver.findSolution();
    }

    const char* createNonogram(const char* data) {
        Image image(json::parse(data));
        NonogramCreator::transformImage(image);
        
        NonogramConfig config(image);
        NonogramSolver solver(config);

        json response;
        response["solvable"] = solver.findSolution();
        response["image"] = image.toJson();
        return response.dump().c_str();
    }
}
