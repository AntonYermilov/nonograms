#ifndef __COLOR
#define __COLOR

class Color {
  public:
    static int red(int color);
    static int green(int color);
    static int blue(int color);
    static int intensity(int color);
    static int argb(int alpha, int red, int green, int blue);

  private:
    Color();
};

#endif
