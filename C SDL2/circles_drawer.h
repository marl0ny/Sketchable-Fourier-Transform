#include <SDL2/SDL.h>


#ifndef _CIRCLES_DRAWER_H
#define _CIRCLES_DRAWER_H

#define WIDTH 640
#define HEIGHT 480
// #define WIDTH 1280
// #define HEIGHT 720

typedef double real;

/*
* Point.
*
* x: x position
* y: y position
*
*/
struct Point {
    real x;
    real y;
};

/*
* A collection of points.
*
* n: number of points.
* points: pointer to an array of points.
*
*/
struct Points {
    size_t n;
    struct Point *points;
};

#define MAX_POINTS 10000

/*
* Perform the discrete Fourier transform
* on some data points.
*
* fourier_points: the destination for the
                  output of the Fourier transform.
* data_points: the input data.
*/
void FourierTransform(struct Points *fourier_points, 
                      const struct Points *data_points);

/*
* Add points between the first and end points of
* the drawing array. This is to alleviate the Gibbs effect.
*
* draw: the array of points that represent the drawing.
*
* returns: the number of points added.
*
*/
int AddPointsBetweenFirstAndEndPoint(struct Points *draw);

/*
* Obtain the frequencies for a Fourier transform output
* of size n.
*
* frequencies: pointer to the destination array
*              to copy the generated frequencies.
* n: size of the Fourier transform output.
*
*/
void FourierFrequencies(real *frequencies, size_t n);

/*
* Render a circle. SDL2 does not contain
* a function to draw circles, so this needs
* to be implemented separately.
* This is done using the [midpoint circle algorithm]
* (https://en.wikipedia.org/wiki/Midpoint_circle_algorithm).
*
* renderer: the renderer.
* cx: the x position of the centre of the circle.
* cy: the y position of the centre of the circle.
* radius: the radius of the circle.
*
*
* ----------------------------------------------------------------
* References:
*
* Wikipedia contributors. (2019, July 23).
* Midpoint circle algorithm. 
* In Wikipedia, The Free Encyclopedia. 
* Retrieved 20:11, February 9, 2020, 
* from https://en.wikipedia.org/w/index.php?title=
* Midpoint_circle_algorithm&oldid=907517844
*
*/
void RenderDrawCircle(SDL_Renderer *renderer, 
                      real cx, real cy, real radius);

/*
* Render an animation frame of the collection circles.
*
* renderer: the renderer.
* frequencies: the frequency of each amplitude.
* amplitudes: the output of the Fourier transform.
* circle_draw: pointer to the array that stores the
               points drawn by the circles.
* frame_count: the current frame of the animation.
* remove_circle_count: the number of circles to remove.
*
*/
void RenderDrawCirclesSketch(SDL_Renderer *renderer, 
                             const real *frequencies, 
                             const struct Points *amplitudes,
                             struct Points *circles_draw,
                             int frame_count,
                             int remove_circle_count);

static inline void _DrawFourArcs(SDL_Renderer *renderer,
                           real cx, real cy, real prev_x, real prev_y,
                           real x, real y) {
    SDL_RenderDrawLine(renderer, (int)(x + cx), (int)(y + cy), 
                        (int)(prev_x + cx), (int)(prev_y + cy));
    SDL_RenderDrawLine(renderer, (int)(-x + cx), (int)(y + cy), 
                        (int)(-prev_x + cx), (int)(prev_y + cy));
    SDL_RenderDrawLine(renderer, (int)(-x + cx), (int)(-y + cy), 
                        (int)(-prev_x + cx), (int)(-prev_y + cy));
    SDL_RenderDrawLine(renderer, (int)(x + cx), (int)(-y + cy), 
                        (int)(prev_x + cx), (int)(-prev_y + cy));
}


static inline void _RenderAddSingleCircleToSketch(SDL_Renderer *renderer,
                                                  real *cx, real *cy,
                                                  real ax, real ay, 
                                                  real f, real angle, real c) {
    // TODO: the radii can be put into a separate array.
    real rx = ax*cos(angle*f*c) + ay*sin(angle*f*c);
    real ry = ay*cos(angle*f*c) - ax*sin(angle*f*c);
    SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);
    SDL_RenderDrawLine(renderer, (int)(*cx), (int)(*cy),
                           (int)(*cx + rx), 
                           (int)(*cy + ry));
    SDL_SetRenderDrawColor(renderer,  255, 165, 0, 255);
    RenderDrawCircle(renderer, *cx, *cy, sqrt(ax*ax + ay*ay));
    *cx += rx;
    *cy += ry;
}

#endif
