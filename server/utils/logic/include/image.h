#ifndef __IMAGE
#define __IMAGE

#include <vector>
#include <string>

#include <json.hpp>

class Image {
  public:
    /*
     *  Receives json in the following format:
     *  {"height"::int, "width"::int, "colors"::int, "backgroundColor"::int, "pixels"::string}
     *  Constructs an Image with the same fields.
     *  
     *  height           number of rows
     *  width            number of columns
     *  colors           number of colors (not including background color)
     *  backgroundColor  rgb value of background color
     *  pixels           colors of cells of the specified image
     */
    Image(const nlohmann::json &image);

    std::vector<int>& operator[](int i);
    const std::vector<int>& operator[](int i) const;

    int getHeight() const;
    int getWidth() const;
    int getColors() const;
    int getBackgroundColor() const;

    void setBackgroundColor(int color);

    nlohmann::json toJson() const;

  private:
    int height;
    int width;
    int colors;
    int backgroundColor;
    std::vector<std::vector<int> > pixels;
};

#endif
