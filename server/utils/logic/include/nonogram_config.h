#ifndef __NONOGRAM_FIELD
#define __NONOGRAM_FIELD

#include <string>
#include <vector>
#include <unordered_map>

#include <image.h>

class NonogramConfig {
  public:
    NonogramConfig(const Image &image);

    int getHeight() const;
    int getWidth() const;
    int getColors() const;

    int getColorId(int color) const;
    int getColorRGB(int id) const;
    
    class Segment {
      public:
        Segment(int size, int color);
        
        int getSize() const;
        int getColor() const;
      private:
        int size;
        int color;

        Segment() {}
    };

    std::vector<Segment> getRow(int row) const;
    std::vector<Segment> getColumn(int column) const;

  private:
    int height;
    int width;
    int colors;
    std::vector<std::vector<Segment> > rows;
    std::vector<std::vector<Segment> > columns;

    std::vector<int> colorRGB;
    std::unordered_map<int, int> colorId;

    NonogramConfig();
};

#endif
