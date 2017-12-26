#include <algorithm>

#include <image.h>
#include <json.hpp>

using json = nlohmann::json;
using std::vector;
using std::string;

Image::Image(const json &image) {
    height = image["height"];
    width = image["width"];
    colors = image["colors"];
    backgroundColor = image["backgroundColor"];

    pixels = vector<vector<int> >(height, vector<int>(width));

    string buffer = image["pixels"].get<string>();
    const int* data_ptr = reinterpret_cast<const int*>(buffer.c_str());
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            pixels[i][j] = data_ptr[i * width + j];
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
    image["colors"] = colors;
    image["backgroundColor"] = backgroundColor;
    
    string buffer(width * height * 4, 0x00);
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            std::copy(reinterpret_cast<const char*>(&pixels[i][j]), 
                      reinterpret_cast<const char*>(&pixels[i][j]) + 4,
                      &buffer[(i * width + j) * 4]);
        }
    }
    image["pixels"] = buffer;
    return image;
}
