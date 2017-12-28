#include <nonogram_solver.h>
#include <algorithm>
#include <iostream>

using std::vector;
using std::fill;
using std::cerr;

const int8_t NonogramSolver::BACKGROUND = 0;
const int8_t NonogramSolver::UNKNOWN = -1;

NonogramSolver::NonogramSolver(const NonogramConfig &config) : config(config), isSolved(false), isCorrect(false) {
    field = vector<vector<int8_t> >(config.getHeight(), vector<int8_t>(config.getWidth(), UNKNOWN));
    isUpdatedRow = vector<int8_t>(config.getHeight(), true);
    isUpdatedColumn = vector<int8_t>(config.getWidth(), true);
}

int NonogramSolver::get(int row, int column) const {
    return field[row][column];
}

void NonogramSolver::set(int row, int column, int color) {
    field[row][column] = color;
    isUpdatedRow[row] = true;
    isUpdatedColumn[column] = true;
}

void NonogramSolver::printField() const {
    for (int i = 0; i < config.getHeight(); i++) {
        for (int j = 0; j < config.getWidth(); j++) {
            if (field[i][j] == UNKNOWN) {
                cerr << "?";
            } else if (field[i][j] == BACKGROUND) {
                cerr << " ";
            } else {
                cerr << int(field[i][j]);
            }
        }
        cerr << '\n';
    }
    cerr << '\n';
}

bool NonogramSolver::findSolution() {
    if (isSolved)
        return isCorrect;

    int filled = 0;
    while (!isSolved) {
        isSolved = true;

        for (int row = 0; row < config.getHeight(); row++) {
            if (!isUpdatedRow[row])
                continue;
            int added = fillRow(row, config.getRow(row));
            filled += added;
            isSolved &= added == 0;
        }
        fill(isUpdatedRow.begin(), isUpdatedRow.end(), false);

        for (int column = 0; column < config.getWidth(); column++) {
            if (!isUpdatedColumn[column])
                continue;
            int added = fillColumn(column, config.getColumn(column));
            filled += added;
            isSolved &= added == 0;
        }
        fill(isUpdatedColumn.begin(), isUpdatedColumn.end(), false);
    }

    isCorrect = filled == config.getHeight() * config.getWidth();
    return isCorrect;
}

int NonogramSolver::fillRow(int row, const vector<NonogramConfig::Segment> &sequence) {
    int filledNew = 0;
    
    vector<vector<int8_t> > isColored(config.getColors() + 1, vector<int8_t>(config.getWidth()));
    vector<int8_t> isWhite(config.getWidth());
    for (int column = 0; column < config.getWidth(); column++) {
        isWhite[column] = field[row][column] == BACKGROUND;
        for (int color = 1; color <= config.getColors(); color++) {
            isColored[color][column] = field[row][column] == color;
            isColored[0][column] |= isColored[color][column];
        }
    }

    vector<int8_t> updatedColors = getUpdatedColors(sequence, isColored, isWhite);
    for (int column = 0; column < config.getWidth(); column++) {
        if (updatedColors[column] != field[row][column]) {
            filledNew++;
            isUpdatedColumn[column] = true;
        }
        field[row][column] = updatedColors[column];
    }

    return filledNew;
}

int NonogramSolver::fillColumn(int column, const vector<NonogramConfig::Segment> &sequence) {
    int filledNew = 0;
    
    vector<vector<int8_t> > isColored(config.getColors() + 1, vector<int8_t>(config.getHeight()));
    vector<int8_t> isWhite(config.getHeight());
    for (int row = 0; row < config.getHeight(); row++) {
        isWhite[row] = field[row][column] == BACKGROUND;
        for (int color = 1; color <= config.getColors(); color++) {
            isColored[color][row] = field[row][column] == color;
            isColored[0][row] |= isColored[color][row];
        }
    }

    vector<int8_t> updatedColors = getUpdatedColors(sequence, isColored, isWhite);
    for (int row = 0; row < config.getHeight(); row++) {
        if (updatedColors[row] != field[row][column]) {
            filledNew++;
            isUpdatedRow[row] = true;
        }
        field[row][column] = updatedColors[row];
    }

    return filledNew;
}

vector<int8_t> NonogramSolver::getUpdatedColors(const vector<NonogramConfig::Segment> &sequence, 
        const vector<vector<int8_t> > &isColored, const vector<int8_t> &isWhite) const {
    int size = isWhite.size();
    
    auto getReversed_1 = [](auto seq) {
        reverse(seq.begin(), seq.end());
        return seq;
    };
    auto getReversed_2 = [](auto seq) {
        for (auto& row : seq)
            reverse(row.begin(), row.end());
        return seq;
    };

    vector<vector<int8_t> > canFitPrefix = getPossiblePrefixes(sequence, isColored, isWhite);
    vector<vector<int8_t> > canFitSuffix = getPossiblePrefixes(getReversed_1(sequence), 
            getReversed_2(isColored), getReversed_1(isWhite));

    vector<int8_t> canBeWhite = countIfCanBeWhite(sequence, isColored, isWhite, canFitPrefix, canFitSuffix);
    vector<vector<int8_t> > canBeColored = countIfCanBeColored(sequence, isColored, isWhite, canFitPrefix, canFitSuffix);

    vector<int8_t> updatedColors(size, UNKNOWN);
    for (int i = 0; i < size; i++) {
        if (canBeColored[0][i] > 0 && !canBeWhite[i]) {
            updatedColors[i] = canBeColored[0][i];
        }
        if (canBeColored[0][i] == 0 && canBeWhite[i]) {
            updatedColors[i] = BACKGROUND;
        }
    }

    return updatedColors;
}

vector<int8_t> NonogramSolver::countIfCanBeWhite(const vector<NonogramConfig::Segment> &sequence,
        const vector<vector<int8_t> > &isColored, const vector<int8_t> &isWhite,
        const vector<vector<int8_t> > &canFitPrefix, const vector<vector<int8_t> > &canFitSuffix) const {
    int size = isWhite.size();
    int blocks = sequence.size();

    vector<int8_t> canBeWhite(size);
    for (int i = 0; i < size; i++) {
        for (int k = 0; k <= blocks; k++) {
            if (!isColored[0][i] && canFitPrefix[k][i] && canFitSuffix[blocks - k][size - i - 1]) {
                canBeWhite[i] = true;
            }
        }
    }
    return canBeWhite;
}

vector<vector<int8_t> > NonogramSolver::countIfCanBeColored(const vector<NonogramConfig::Segment> &sequence,
        const vector<vector<int8_t> > &isColored, const vector<int8_t> &isWhite,
        const vector<vector<int8_t> > &canFitPrefix, const vector<vector<int8_t> > &canFitSuffix) const {
    int size = isWhite.size();
    int blocks = sequence.size();
    int colors = isColored.size();

    vector<int8_t> countWhite = getPrefixSums(isWhite);
    vector<vector<int8_t> > countColored(colors);
    for (int color = 0; color < colors; color++) {
        countColored[color] = getPrefixSums(isColored[color]);
    }

    vector<vector<int8_t> > intervals(colors, vector<int8_t>(size + 1));
    for (int k = 0; k < blocks; k++) {
        int segSize = sequence[k].getSize();
        int color = sequence[k].getColor();

        for (int i = 0; i + segSize <= size; i++) {
            if (i > 0 && isColored[color][i - 1]) {
                continue;
            }
            if (i + segSize < size && isColored[color][i + segSize]) {
                continue;
            }
            if (countWhite[i + segSize] - countWhite[i] != 0) {
                continue;
            }
            if (countColored[0][i + segSize] - countColored[0][i] != 
                    countColored[color][i + segSize] - countColored[color][i]) {
                continue;
            }

            int equalsPrev = k > 0 && sequence[k - 1].getColor() == color;
            int equalsNext = k + 1 < blocks && sequence[k + 1].getColor() == color;
            if (canFitPrefix[k][std::max(0, i - equalsPrev)] && 
                    canFitSuffix[blocks - k - 1][std::max(0, size - i - segSize - equalsNext)]) {
                intervals[color][i]++;
                intervals[color][i + segSize]--;
            }
        }
    }

    vector<vector<int8_t> > canBeColored(colors, vector<int8_t>(size));
    for (int color = 1; color < colors; color++) {
        for (int i = 0, inside = 0; i < size; i++) {
            inside += intervals[color][i];
            if (inside > 0) {
                canBeColored[color][i] = 1;
                canBeColored[0][i] = canBeColored[0][i] == 0 ? color : -1;
            }
        }
    }

    return canBeColored;
}

vector<vector<int8_t> > NonogramSolver::getPossiblePrefixes(const vector<NonogramConfig::Segment> &sequence,
        const vector<vector<int8_t> > isColored, const vector<int8_t> isWhite) const {
    int size = isWhite.size();
    int blocks = sequence.size();
    int colors = isColored.size();

    vector<int8_t> countWhite = getPrefixSums(isWhite);
    vector<vector<int8_t> > countColored(colors);
    for (int color = 0; color < colors; color++) {
        countColored[color] = getPrefixSums(isColored[color]);
    }

    vector<vector<int8_t> > possible(blocks + 1, vector<int8_t>(size + 1));
    for (int i = 0; i <= size; i++) {
        possible[0][i] = i == 0 || (possible[0][i - 1] && !isColored[0][i - 1]);
    }
    for (int k = 1; k <= blocks; k++) {
        int segSize = sequence[k - 1].getSize();
        int color = sequence[k - 1].getColor();

        for (int i = 1; i <= size; i++) {
            if (!isColored[0][i - 1]) {
                possible[k][i] = possible[k][i - 1];
            }
            if (!isWhite[i - 1]) {
                if (i < segSize) {
                    continue;
                }
                if (countWhite[i] - countWhite[i - segSize] != 0) {
                    continue;
                }
                if (countColored[0][i] - countColored[0][i - segSize] !=
                        countColored[color][i] - countColored[color][i - segSize]) {
                    continue;
                }
                if (i > segSize && isColored[color][i -  segSize - 1]) {
                    continue;
                }

                int equalsPrev = k > 1 && sequence[k - 2].getColor() == color;
                possible[k][i] |= possible[k - 1][std::max(0, i - segSize - equalsPrev)];
            }
        }
    }
    return possible;
}

vector<int8_t> NonogramSolver::getPrefixSums(const vector<int8_t> &array) const {
    int size = array.size();
    vector<int8_t> sums(size + 1);
    for (int i = 1; i <= size; i++) {
        sums[i] = sums[i - 1] + array[i - 1];
    }
    return sums;
}

