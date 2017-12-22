#ifndef __NONOGRAM_FIELD
#define __NONOGRAM_FIELD

#include <string>
#include <vector>

class NonogramConfig {
  public:

    NonogramConfig(const std::string &jsonNonogram);
    NonogramConfig(const std::vector<std::vector<int> > &field, int backgroundColor);

    int getHeight() const;
    int getWidth() const;
    int getColors() const;
    
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
    std::vector<Segment> getColumn(int columnt) const;

  private:
    int height;
    int width;
    int colors;
    std::vector<std::vector<Segment> > rows;
    std::vector<std::vector<Segment> > columns;

    NonogramConfig();
};

#endif
