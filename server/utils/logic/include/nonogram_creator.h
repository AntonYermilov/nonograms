#ifndef __NONOGRAM_CREATOR
#define __NONOGRAM_CREATOR

#include <vector>
#include <image.h>

class NonogramCreator {
  public:
    static void transformImage(Image &image);

  private:
    static std::vector<int> selectMainColors(Image &image);
    
};

#endif
