#include <color.h>
#include <algorithm>

int Color::red(int color) {
    return (color >> 16) & 0xff;
}

int Color::green(int color) {
    return (color >> 8) & 0xff;
}

int Color::blue(int color) {
    return color & 0xff;
}

int Color::intensity(int color) {
    return std::min(255, (int) (0.299 * red(color) + 0.587 * green(color) + 0.114 * blue(color)));
}

int Color::argb(int alpha, int red, int green, int blue) {
    return ((alpha & 0xff) << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
}

