#include <vector>
#include <cmath>
#include <algorithm>
#include <iostream>

#include <nonogram_creator.h>
#include <image.h>
#include <color.h>
#include <constants.h>

using std::vector;

void NonogramCreator::transformImage(Image &image) {
    vector<int> optimalColors = selectMainColors(image);
    int backgroundColor = 0;
    for (int color : optimalColors) {
        int intensity = Color::intensity(color);
        if (intensity > Color::intensity(backgroundColor)) {
            backgroundColor = color;
        }
    }
    image.setBackgroundColor(backgroundColor);
}

vector<int> NonogramCreator::selectMainColors(Image &image) {
    vector<int> countAll(MAX_INTENSITY);
    vector<int> sumRed(MAX_INTENSITY);
    vector<int> sumGreen(MAX_INTENSITY);
    vector<int> sumBlue(MAX_INTENSITY);
    for (int i = 0; i < image.getHeight(); i++) {
        for (int j = 0; j < image.getWidth(); j++) {
            int color = image[i][j];
            int red = Color::red(color);
            int green = Color::green(color);
            int blue = Color::blue(color);
            int intensity = Color::intensity(color);

            countAll[intensity]++;
            sumRed[intensity] += (int) pow(1.0 * red, 1.075);
            sumGreen[intensity] += (int) pow(1.0 * green, 1.075);
            sumBlue[intensity] += (int) pow(1.0 * blue, 1.075);
        }
    }

    int average = image.getHeight() * image.getWidth() / image.getColors();
    vector<vector<int> > dp(image.getColors() + 1, vector<int>(MAX_INTENSITY + 1, INF));
    vector<vector<int> > parent(image.getColors() + 1, vector<int>(MAX_INTENSITY + 1));

    int max_range = MAX_INTENSITY * 1.5 / image.getColors();
    int min_range = 0; //MAX_INTENSITY * 0.5 / image.getColors();

    dp[0][0] = 0;
    for (int color = 1; color <= image.getColors(); color++) {
        for (int i = 1; i <= MAX_INTENSITY; i++) {
            int sum = 0;
            for (int j = i - min_range; j > 0 && i - j < max_range; j--) {
                sum += countAll[j - 1];
                if (dp[color][i] > dp[color - 1][j - 1] + (sum - average) * (sum - average)) {
                    dp[color][i] = dp[color - 1][j - 1] + (sum - average) * (sum - average);
                    parent[color][i] = j - 1;
                }
            }
        }
    }

    vector<int> left_bound(image.getColors());
    vector<int> right_bound(image.getColors());
    for (int color = image.getColors(), i = MAX_INTENSITY; i != 0; i = parent[color--][i]) {
        left_bound[color - 1] = parent[color][i];
        right_bound[color - 1] = i;
    }

    vector<int> type(MAX_INTENSITY);
    vector<int> optimalColors(image.getColors());
    for (int color = 0; color < image.getColors(); color++) {
        if (image.getColors() == 2) {
            optimalColors[color] = color == 0 ? Color::argb(255, 0, 0, 0) : Color::argb(255, 255, 255, 255);
        } else {
            int red = 0, green = 0, blue = 0, weight = 1;
            for (int i = left_bound[color]; i < right_bound[color]; i++) {
                weight += countAll[i];
                red += sumRed[i];
                green += sumGreen[i];
                blue += sumBlue[i];
            }
            red = std::min(255, red / weight);
            green = std::min(255, green / weight);
            blue = std::min(255, blue / weight);
            optimalColors[color] = Color::argb(255, red, green, blue);
        }
        for (int i = left_bound[color]; i < right_bound[color]; i++) {
            type[i] = color;
        }
    }

    for (int i = 0; i < image.getHeight(); i++) {
        for (int j = 0; j < image.getWidth(); j++) {
            int color = image[i][j];
            int intensity = Color::intensity(color);
            image[i][j] = optimalColors[type[intensity]];
        }
    }

    return optimalColors;
}
