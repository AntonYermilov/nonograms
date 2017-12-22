#ifndef __NONOGRAM_SOLVER
#define __NONOGRAM_SOLVER

#include <nonogram_config.h>
#include <vector>

/**
 * Takes specified nonogram and tells if it is possible to solve it or not.
 */
class NonogramSolver {
  public:
    NonogramSolver(const NonogramConfig &config);

    void printField() const;
    bool solve();

  private:
    static const int EXACTLY_WHITE;
    static const int UNKNOWN;
    
    NonogramConfig config;

    bool isSolved;
    bool isCorrect;

    std::vector<std::vector<int> > field;

    /**
     * int8_t used there instead of bool type to improve the performance by avoiding bit compression of vector
     */
    std::vector<int8_t> isUpdatedRow;
    std::vector<int8_t> isUpdatedColumn;

    int fillRow(int row, const std::vector<NonogramConfig::Segment> &sequence);
    int fillColumn(int column, const std::vector<NonogramConfig::Segment> &sequence);

    std::vector<int> getUpdatedColors(const std::vector<NonogramConfig::Segment> &sequence, 
                                      const std::vector<std::vector<int8_t> > &isColored, 
                                      const std::vector<int8_t> &isWhite) const;
    
    std::vector<int8_t> countIfCanBeWhite(const std::vector<NonogramConfig::Segment> &sequence,
                                           const std::vector<std::vector<int8_t> > &isColored, 
                                           const std::vector<int8_t> &isWhite, 
                                           const std::vector<std::vector<int8_t> > &canFitPrefix, 
                                           const std::vector<std::vector<int8_t> > &canFitSuffix) const;

    std::vector<std::vector<int> > countIfCanBeColored(const std::vector<NonogramConfig::Segment> &sequence,
                                                          const std::vector<std::vector<int8_t> > &isColored, 
                                                          const std::vector<int8_t> &isWhite,
                                                          const std::vector<std::vector<int8_t> > &canFitPrefix, 
                                                          const std::vector<std::vector<int8_t> > &canFitSuffix) const;

    std::vector<std::vector<int8_t> > getPossiblePrefixes(const std::vector<NonogramConfig::Segment> &sequence,
                                                           const std::vector<std::vector<int8_t> > isColored, 
                                                           const std::vector<int8_t> isWhite) const;

    std::vector<int> getPrefixSums(const std::vector<int8_t> &array) const;

    NonogramSolver();
    NonogramSolver(const NonogramSolver& other);
    NonogramSolver operator=(const NonogramSolver& other);
};

#endif
