#include <stdlib.h>
#include <math.h>
#include <SDL2/SDL.h>
#include "circles_drawer.h"


void RenderDrawCircle(SDL_Renderer *renderer,
                      real cx, real cy, real radius) {
    real prev_x = radius;
    real prev_y = 0.0;
    real x = radius;
    real y = 0;
    for (int i = 0; i < (int)radius; i++) {
        y = (real)i;
        // TODO: make a faster implementation of this function
        // that does not use sqrts or floats.
        x = sqrt(prev_x*prev_x - 2*prev_y - 1);
        _DrawFourArcs(renderer, cx, cy, prev_x, prev_y, x, y);
        prev_x = x;
        prev_y = y;
    } 
    _DrawFourArcs(renderer, cx, cy, prev_x, prev_y, 0.0, radius);
}


void RenderDrawCirclesSketch(SDL_Renderer *renderer, 
                             const real *frequencies, 
                             const struct Points *amplitudes,
                             struct Points *circles_draw,
                             int frame_count,
                             int remove_circles_count) {
    int n = amplitudes->n;
    int m = 4;
    real angle = 2.0*3.1415926535897932/((real)n*m);
    real cx = amplitudes->points[0].x;
    real cy = amplitudes->points[0].y;
    real ax = 0.0;
    real ay = 0.0;
    real f = 0.0;
    int half_n =  (n%2 == 0)? n/2: n/2 + 1;
    if (remove_circles_count < 0) remove_circles_count = 0;
    int remove_at_even_n_over_2 = 0;
    if (remove_circles_count >= 2 && n%2 == 0) {
        remove_at_even_n_over_2 = 1;
        remove_circles_count--; 
    }
    half_n -= remove_circles_count/2;
    remove_circles_count = (remove_circles_count%2 == 0)? 0: 1;
    for (int i = 1, k = n - 1; i < half_n; i++, k--) {

        ax = amplitudes->points[i].x;
        ay = amplitudes->points[i].y;
        f = frequencies[i];
        _RenderAddSingleCircleToSketch(renderer, 
                                       &cx, &cy, ax, ay, 
                                       f, angle, (real)frame_count);

        if (!(i == (half_n - 1) && remove_circles_count == 1)) {
            ax = amplitudes->points[k].x;
            ay = amplitudes->points[k].y;
            f = frequencies[k];
            _RenderAddSingleCircleToSketch(renderer, 
                                            &cx, &cy, ax, ay, 
                                            f, angle, (real)frame_count);
        }

    }
    if (n%2 == 0 && !remove_at_even_n_over_2) {
        ax = amplitudes->points[n/2].x;
        ay = amplitudes->points[n/2].y;
        f = frequencies[n/2];
        _RenderAddSingleCircleToSketch(renderer, 
                                       &cx, &cy, ax, ay, 
                                       f, angle, (real)frame_count);
    }
    circles_draw->points[circles_draw->n].x = cx;
    circles_draw->points[circles_draw->n].y = cy;
    circles_draw->n++;
    circles_draw->n = circles_draw->n % (m*n);
}


void FourierTransform(struct Points *fourier_points, 
                      const struct Points *data_points) {
    real angle = 2.0*3.1415926535897932/((real)data_points->n);
    real x = 0.0;
    real y = 0.0;
    int n = data_points->n;
    fourier_points->n = n;
    for (int i = 0; i < n; i++) {
        fourier_points->points[i].x = 0.0;
        fourier_points->points[i].y = 0.0;
        for (int j=0; j < n; j++) {
            x = data_points->points[j].x;
            y = data_points->points[j].y;
            fourier_points->points[i].x += (
                x*cos(angle*i*j) - y*sin(angle*i*j)); 
            fourier_points->points[i].y += (
                y*cos(angle*i*j) + x*sin(angle*i*j));
        }
        fourier_points->points[i].x = fourier_points->points[i].x/n;
        fourier_points->points[i].y = fourier_points->points[i].y/n;
    }
}


int AddPointsBetweenFirstAndEndPoint(struct Points *draw) {
    int n = draw->n;
    real x = draw->points[0].x - draw->points[n-1].x;
    real y = draw->points[0].y - draw->points[n-1].y;
    real screen_diag2 = (real)(HEIGHT*HEIGHT + WIDTH*WIDTH);
    if (sqrt(x*x + y*y) > sqrt(screen_diag2)/10.0) {
        int m = (int)(sqrt(x*x + y*y)/30.0);
        for (int i = 1;
             i <= m && draw->n < MAX_POINTS; i++) {
            draw->points[draw->n].x = (i*x)/m + draw->points[n-1].x;
            draw->points[draw->n].y = (i*y)/m + draw->points[n-1].y;
            draw->n++;
        }
    }
    return draw->n - n;
}


void FourierFrequencies(real *frequencies, size_t n) {
    int half_n = (n % 2)? n/2 + 1: n/2;
    for (int i = 0; i < half_n; i++) {
        frequencies[i] = i;
    }
    int k = -1;
    for (int j = n - 1; j >= half_n; j--) {
        frequencies[j] = k;
        k--;
    }
}


int main(int argc, char **argv) {
    SDL_Window* window = NULL;
    if (SDL_Init(SDL_INIT_VIDEO) < 0) {
        fprintf(stderr, "Unable to initialize SDL2: %s\n", SDL_GetError());
        exit(1);
    }
    window = SDL_CreateWindow("Fourier Drawing",
                              SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
                              WIDTH, HEIGHT,
                              SDL_WINDOW_RESIZABLE);
    if (window == NULL) {
        fprintf(stderr, "Unable to create window: %s\n", SDL_GetError());
        exit(1);
    }
    SDL_Renderer *renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED);
    if (renderer == NULL) {
        fprintf(stderr, "Unable to create renderer: %s\n", SDL_GetError());
        exit(1);
    }
    int window_width = WIDTH;
    int window_height = HEIGHT;
    SDL_Rect background_rect = {.x = 0, .y = 0, .w = window_width, .h = window_height};
    SDL_SetRenderDrawColor(renderer, 100, 100, 100, 255);
    SDL_RenderClear(renderer);

    // Setup draw points.
    struct Points draw;
    draw.n = 0;
    struct Point points[MAX_POINTS] = {{0.0}};
    draw.points = points;

    // Setup the Fourier data points.
    struct Points amplitudes;
    amplitudes.n = 0;
    struct Point amplitude_points[MAX_POINTS] = {{0.0}};
    amplitudes.points = amplitude_points;

    // Setup the drawing made by the circles.
    struct Points circles_draw;
    circles_draw.n = 0;
    struct Point circles_draw_points[MAX_POINTS] = {{0.0}};
    circles_draw.points = circles_draw_points;

    //Setup the Fourier frequencies
    real frequencies[MAX_POINTS] = {0.0};

    if (argc > 1) {
        for (int i = 2, j = 0; i < argc; i += 2, j++) {
            draw.points[j].x = (real)strtol(argv[i-1], NULL, 10); 
            draw.points[j].y = (real)strtol(argv[i], NULL, 10);
            draw.n++;
        }
        FourierTransform(&amplitudes, &draw);
        FourierFrequencies(frequencies, draw.n);
        printf("%s\n", "Fourier transform of entered points:\n"
               "frequency\tamplitude x\tamplitude y");
        for (int i = 0; i < amplitudes.n; i++) {
            printf("%f\t%f\t%f\n", frequencies[i], 
                   amplitudes.points[i].x, amplitudes.points[i].y);
        }
    }

    const Uint8 *keyboard_state;
    struct Point p = {.x=0.0, .y=0.0};
    int returned = 0;
    int mouse_pressed = 0;
    int mouse_released = 0;
    int mouse_x = 0;
    int mouse_y = 0;
    int remove_circle_count = 0;
    int circle_count_changed = 0;
    int delay_time = 15;
    // frame count when doing the circles animation.
    int frame_count = 0;
    // Points added to remove big jumps from the end point to the start point.
    int points_added = 0;

    do {
        SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);
        SDL_GetWindowSize(window, &window_width, &window_height);
        background_rect.w = window_width;
        background_rect.h = window_height;
        SDL_RenderFillRect(renderer, &background_rect);
        SDL_PumpEvents();
        mouse_released = 0;
        keyboard_state = SDL_GetKeyboardState(NULL);
        if (keyboard_state[SDL_SCANCODE_RETURN]) {
            returned = 1;
        }
        if (!circle_count_changed) {
            if (keyboard_state[SDL_SCANCODE_UP]) {
                circle_count_changed = (
                    (amplitudes.n - remove_circle_count) > 20)? 0: 8;
                circles_draw.n = 0;
                remove_circle_count -= (remove_circle_count <= 0)? 0: 1;
            }
            if (keyboard_state[SDL_SCANCODE_DOWN]) {
                circle_count_changed = (
                    (amplitudes.n - remove_circle_count) > 20)? 0: 8;
                circles_draw.n = 0;
                remove_circle_count += 1;
            }
        }
        circle_count_changed -= (circle_count_changed <= 0)? 0: 1;
        if (SDL_GetMouseState(&mouse_x, &mouse_y) & SDL_BUTTON(SDL_BUTTON_LEFT)) {
            if (!mouse_pressed) {
                remove_circle_count = 0;
                circle_count_changed = 0;
                draw.n = 0;
                mouse_pressed = 1;   
            }
            circles_draw.n = 0;
            p.x = (real)mouse_x;
            p.y = (real)mouse_y;
        } else if(mouse_pressed) {
            mouse_pressed = 0;
            mouse_released = 1;
        }
        SDL_SetRenderDrawColor(renderer, 0, 0, 255, 255);
        SDL_SetRenderDrawColor(renderer, 255, 165, 0, 255);
        if (mouse_released) {
            frame_count = 0;
            points_added = AddPointsBetweenFirstAndEndPoint(&draw);
            FourierTransform(&amplitudes, &draw);
            FourierFrequencies(frequencies, draw.n);
            draw.n -= points_added;
        }
        if (draw.n < 1000 && mouse_pressed) {
            draw.points[draw.n].x = p.x;
            draw.points[draw.n].y = p.y;
            draw.n++;
            for (int i = 1; i < draw.n && draw.n >= 1; i++) {
            SDL_RenderDrawLine(renderer, (int)draw.points[i].x, (int)draw.points[i].y, 
                              (int)draw.points[i-1].x, (int)draw.points[i-1].y);
            }
        } else if (amplitudes.n > 0 && !mouse_pressed) {
            RenderDrawCirclesSketch(renderer, frequencies, &amplitudes, 
                                    &circles_draw, 
                                    frame_count, remove_circle_count);
            SDL_SetRenderDrawColor(renderer, 75, 75, 75, 255);
            for (int i = 1; i < draw.n && draw.n >= 1; i++) {
                SDL_RenderDrawLine(renderer, (int)draw.points[i].x, (int)draw.points[i].y, 
                                   (int)draw.points[i-1].x, (int)draw.points[i-1].y);
            }
            SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);
            for (int i = 1; i < circles_draw.n && circles_draw.n >= 1; i++) {
                SDL_RenderDrawLine(renderer, (int)circles_draw.points[i].x, 
                                   (int)circles_draw.points[i].y, 
                                   (int)circles_draw.points[i-1].x,
                                   (int)circles_draw.points[i-1].y);
            }
            frame_count++;
        }
        SDL_RenderPresent(renderer);
        SDL_Delay(delay_time);
    } while(returned==0);
    SDL_DestroyRenderer(renderer);
    SDL_DestroyWindow(window);
    SDL_Quit();
    return 0;
}