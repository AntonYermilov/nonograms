#include <nonogram_config.h>
#include <nonogram_solver.h>
#include <string>

using std::string;

extern "C" {
    bool checkSolvability(char* json) {
        NonogramConfig config(json);
        NonogramSolver solver(config);
        return solver.solve();
    }
}
