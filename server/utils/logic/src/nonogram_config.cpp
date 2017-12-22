#include <nonogram_config.h>
#include <json.hpp>
#include <unordered_map>

using json = nlohmann::json;
using std::vector;
using std::string;
using std::unordered_map;

NonogramConfig::NonogramConfig(const string &jsonNonogram) {
    json nonogram = json::parse(jsonNonogram);
    height = nonogram["height"];
    width = nonogram["width"];
    colors = nonogram["colors"];

    rows.resize(height);
    columns.resize(width);
    
    for (int i = 0; i < height; i++) {
        json &row = nonogram["rows"][i];
        for (int j = 0; j < (int) row.size(); j++) {
            rows[i].push_back({row[j]["size"], row[j]["color"]});
        }
    }
    for (int i = 0; i < width; i++) {
        json &column = nonogram["columns"][i];
        for (int j = 0; j < (int) column.size(); j++) {
            columns[i].push_back({column[j]["size"], column[j]["color"]});
        }
    }
}

NonogramConfig::NonogramConfig(const vector<vector<int> > &field, int backgroundColor) {
    height = field.size();
    width = field[0].size();
    colors = 0;

    unordered_map<int, int> colorId(32);

    colorId[backgroundColor] = 0;
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (colorId.find(field[i][j]) == colorId.end()) {
                colorId[field[i][j]] = ++colors;
            }
        }
    }

    rows = vector<vector<Segment> >(height);
    for (int i = 0; i < height; i++) {
        for (int l = 0, r = 0; r < width; l = r) {
            while (r < width && field[i][l] == field[i][r]) {
                r++;
            }
            if (field[i][l] != backgroundColor) {
                rows[i].push_back({r - l, colorId[field[i][l]]});
            }
        }
    }

    columns = vector<vector<Segment> >(width);
    for (int i = 0; i < width; i++) {
        for (int l = 0, r = 0; r < width; l = r) {
            while (r < height && field[l][i] == field[r][i]) {
                r++;
            }
            if (field[l][i] != backgroundColor) {
                columns[i].push_back({r - l, colorId[field[l][i]]});
            }
        }
    }
}

int NonogramConfig::getHeight() const {
    return height;
}

int NonogramConfig::getWidth() const {
    return width;
}

int NonogramConfig::getColors() const {
    return colors;
}

vector<NonogramConfig::Segment> NonogramConfig::getRow(int row) const {
    return rows[row];
}

vector<NonogramConfig::Segment> NonogramConfig::getColumn(int column) const {
    return columns[column];
}

NonogramConfig::Segment::Segment(int size, int color) : size(size), color(color) {}

int NonogramConfig::Segment::getSize() const {
    return size;
}

int NonogramConfig::Segment::getColor() const {
    return color;
}
