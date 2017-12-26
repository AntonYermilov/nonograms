#include <algorithm>
#include <iostream>

#include <image.h>
#include <json.hpp>

using json = nlohmann::json;
using std::vector;
using std::string;

Image::Image(const json &image) {
    height = image["height"];
    width = image["width"];
    colors = image["colors"].get<int>() + 1;
    backgroundColor = image["backgroundColor"];

    pixels = vector<vector<int> >(height, vector<int>(width));
    auto &data = image["pixels"];
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            pixels[i][j] = data[i * width + j];
        }
    }
}

vector<int>& Image::operator[](int i) {
    return pixels[i];
}

const vector<int>& Image::operator[](int i) const {
    return pixels[i];
}

int Image::getHeight() const {
    return height;
}

int Image::getWidth() const {
    return width;
}

int Image::getColors() const {
    return colors;
}

int Image::getBackgroundColor() const {
    return backgroundColor;
}

void Image::setBackgroundColor(int color) {
    backgroundColor = color;
}

json Image::toJson() const {
    json image;
    image["height"] = height;
    image["width"] = width;
    image["colors"] = colors - 1;
    image["backgroundColor"] = backgroundColor;
    
    vector<int> data(height * width);
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            data[i * width + j] = pixels[i][j];
        }
    }
    image["pixels"] = data;
    return image;
}
