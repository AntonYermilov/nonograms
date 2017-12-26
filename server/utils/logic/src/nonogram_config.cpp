#include <nonogram_config.h>
#include <iostream>

using std::vector;
using std::string;
using std::unordered_map;

NonogramConfig::NonogramConfig(const Image &image) {
    height = image.getHeight();
    width = image.getWidth();
    colors = image.getColors();

    colorId = unordered_map<int, int>(32);
    colorRGB = vector<int>(colors + 1);

    colorId[image.getBackgroundColor()] = 0;
    colorRGB[0] = image.getBackgroundColor();
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (colorId.find(image[i][j]) == colorId.end()) {
                int size = colorId.size();
                colorRGB[size] = image[i][j];
                colorId[image[i][j]] = size;
            }
        }
    }

    rows = vector<vector<Segment> >(height);
    for (int i = 0; i < height; i++) {
        for (int l = 0, r = 0; r < width; l = r) {
            while (r < width && image[i][l] == image[i][r]) {
                r++;
            }
            if (image[i][l] != image.getBackgroundColor()) {
                rows[i].push_back({r - l, colorId[image[i][l]]});
            }
        }
    }

    columns = vector<vector<Segment> >(width);
    for (int i = 0; i < width; i++) {
        for (int l = 0, r = 0; r < width; l = r) {
            while (r < height && image[l][i] == image[r][i]) {
                r++;
            }
            if (image[l][i] != image.getBackgroundColor()) {
                columns[i].push_back({r - l, colorId[image[l][i]]});
            }
        }
    }
}

int NonogramConfig::getColorId(int color) const {
    auto element = colorId.find(color);
    return element != colorId.end() ? element->second : -1;
}

int NonogramConfig::getColorRGB(int id) const {
    return id >= 0 && id < (int) colorRGB.size() ? colorRGB[id] : -1;
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
