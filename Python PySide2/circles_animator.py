"""
Circles animator.
"""
import numpy as np
from animator import Animator
from typing import Union, List
from functions import FunctionRtoR


class CirclesAnimator(Animator):

    def __init__(self, dpi: int, interval: int) -> None:
        """
        Initializer.

        Parameters:
         dpi: dots per square inches.
         interval: interval in milliseconds between
         each animation frame.
        """
        Animator.__init__(self, dpi, interval)
        ax = self.figure.add_subplot(1, 1, 1, aspect='equal')
        self._pts_per_circle = 50
        self._points = []
        self._circle_points = []
        self._amplitudes = np.array([])
        self._frequencies = np.array([])
        self._circles = np.array([])
        self._original_amplitudes = np.array([])
        self._velocity = 10.0
        self._angular_dist = 0.0
        self._resolution = 0
        self._gen_circle = np.array([np.exp(
            2*1.0j*np.pi*((m + 1)/self._pts_per_circle))
                                     for m in range(self._pts_per_circle)],
                                    np.complex)
        self._frequencies = np.array([])
        self._sort_arr = np.array([])
        ax.set_xlim(-10, 10)
        ax.set_ylim(-8, 8)
        plot, = ax.plot([], [], color="red")
        line_plot, = ax.plot([], [], color="black")
        line_drawn_by_circle_plot, = ax.plot([], [], color="red")
        self.add_plots([line_plot, plot, line_drawn_by_circle_plot])
        self._circles_plot = plot
        self._line_plot = line_plot
        self._line_drawn_by_circle_plot = line_drawn_by_circle_plot
        self._time_elapsed = 0
        self._STATE = 0
        self._ft = None

    def append(self, point: list) -> None:
        """
        Append a point to the sketch that will be later drawn out
        by summation of rotating circles.
        """
        point = point[0] + 1.0j*point[1]
        self._points.append(point)

    def on_entry_returned(self, entry_info) -> None:
        n = len(self._points) 
        if n < 64:
            n = 64
        x_func_name = entry_info['1']
        y_func_name = entry_info['2']
        function_name = "%s + 1.0j*(%s)" % (x_func_name, y_func_name) \
                        if y_func_name != "" else "%s" % x_func_name
        if function_name == "":
            function_name = "zero(t)"
        f = FunctionRtoR(function_name)
        self._ft = f
        t = np.linspace(-np.pi, np.pi, n)
        self._points = list(f(t))
        self._line_plot.set_xdata(np.real(np.array(self._points)))
        self._line_plot.set_ydata(np.imag(np.array(self._points)))
        self.update(0.01)
        self.clear()
        self._points = list(f(t))
        self.start_draw_circes()

    def set_params(self, *params):
        n = len(self._points) 
        if n < 64:
            n = 64
        z = self._ft(np.linspace(np.pi, -np.pi, len(self._points)), *params)
        self._points = list(z) # TODO: this line may not be necessary.
        self._line_plot.set_xdata(np.real(np.array(self._points)))
        self._line_plot.set_ydata(np.imag(np.array(self._points)))
        self.update(0.01)
        self.clear()
        self._points = list(z)
        self.start_draw_circes()

    def get_functions(self):
        """
        Getter for x(t) and y(t).
        """
        return self._ft

    def set_animation_speed(self, 
                            animation_speed: Union[float, int]) -> None:
        """
        Set the speed of the animation.

        Parameters:
         animation_speed: speed of the animation.
        """
        self._velocity = animation_speed

    def set_circle_resolution(self, resolution: int) -> None:
        """
        Set the resolution in terms of circles being drawn.

        Parameters:
         resolution: number of circles to draw.
        """
        if 1 < resolution <= len(self._amplitudes)+1:
            self._resolution = resolution
            self._circle_points = []

    def get_number_of_points(self) -> int:
        """
        Getter for the number of points.

        Returns:
         The number of points.
        """
        return len(self._points)

    def clear(self) -> None:
        """
        Clear the animation of the circles.
        """
        self._STATE = 0
        self._points = []
        self._circle_points = []
        self._line_plot.set_color("black")
        self._line_plot.set_alpha(1.0) 
        self._angular_dist = 0.0
        self._line_drawn_by_circle_plot.set_xdata([])
        self._line_drawn_by_circle_plot.set_ydata([])
        self._circles_plot.set_xdata([])
        self._circles_plot.set_ydata([])
        self._circles = np.array([])

    def start_draw_circes(self) -> None:
        """
        Begin the animation of the circles.
        """
        self._STATE = 1
        self._time_elapsed = 0.0
        self._line_plot.set_color("gray")
        self._line_plot.set_alpha(0.5)
        self._frequencies = np.fft.fftfreq(len(self._points))
        self._resolution = len(self._points)
        self._circles = np.zeros(
            [(self._pts_per_circle + 1)*len(self._points)], np.complex)
        self._original_amplitudes = np.fft.fft(np.array(self._points))
        self._amplitudes = self._original_amplitudes/len(self._points)
        self._alternate_between_negative_and_positive()

    def _alternate_between_negative_and_positive(self) -> None:
        """
        Arange the frequencies and amplitudes so that the amplitude
        with zero frequency comes first, and then the amplitude with
        the smallest negative frequency, then the amplitude with
        the smallest positive frequency, and then the amplitude with the
        second smallest negative frequency, and so on.
        """
        amplitudes = np.zeros(len(self._amplitudes), np.complex)
        frequencies = np.zeros(len(self._amplitudes))
        amplitudes[0] = self._amplitudes[0]
        # TODO: Check if this is correct.
        half_len = len(self._amplitudes)//2
        for i in range(1, half_len):
            amplitudes[2*i] = self._amplitudes[i]
            frequencies[2*i] = self._frequencies[i]
            amplitudes[2*i - 1] = self._amplitudes[-i]
            frequencies[2*i - 1] = self._frequencies[-i]
        if len(self._amplitudes) % 2:
            amplitudes[2*half_len] = self._amplitudes[half_len]
            frequencies[2*half_len] = self._frequencies[half_len]
        amplitudes[2*half_len - 1] = self._amplitudes[-half_len]
        frequencies[2*half_len - 1] = self._frequencies[-half_len]
        # print(frequencies)
        self._amplitudes = amplitudes
        self._frequencies = frequencies

    def update(self, delta_t: float) -> None:
        """
        Update the animation.

        Paramters:
         delta_t: the time in seconds between each frame.
        """
        if self._STATE == 0:
            self._line_plot.set_xdata(np.real(np.array(self._points)))
            self._line_plot.set_ydata(np.imag(np.array(self._points)))
        else:
            for _ in range(int(np.abs(self._velocity))):
                if self._angular_dist >= 1.0 or self._angular_dist <= -1.0:
                    self._angular_dist = 0.0
                    self._circle_points = []
                self._time_elapsed -= np.sign(self._velocity)*delta_t
                self._angular_dist += np.sign(
                    self._velocity)*delta_t*np.abs(self._frequencies[1])
            self._draw_circles(self._time_elapsed)

    def _draw_circles(self, i: Union[float, int]) -> None:
        """
        Update the graph of the circles.

        Parameters:
         i: this parameter controls the
         angular distance of the phasor from its
         initial position.
        """
        a = self._amplitudes
        f = self._frequencies
        circles = self._circles
        circles_line = self._circle_points
        # Draw all phasors and circles.
        for j in range(self._resolution):
            if j == 0:
                circles[0] = a[0]*np.exp(-2*np.pi*i*1.0j*f[0])
                for m in range(self._pts_per_circle):
                    circles[m + 1] = a[0]*np.exp(0*1.0j*np.pi*(
                            (m + 1)/self._pts_per_circle))
            else:
                k = j*(self._pts_per_circle + 1)
                amplitude = np.exp(-2*np.pi*i*1.0j*f[j])*a[j]
                circles[k] = amplitude + self._circles[k - 1]
                self._draw_circle(circles[k - 1], amplitude, k)
        stop_index = self._resolution*(self._pts_per_circle + 1) - 1
        for j in range(stop_index, len(circles)):
            circles[j] = circles[stop_index]
        circles_line.append(circles[-1])
        circles_line = np.array(circles_line)
        self._line_drawn_by_circle_plot.set_xdata(np.real(circles_line))
        self._line_drawn_by_circle_plot.set_ydata(np.imag(circles_line))
        self._circles_plot.set_xdata(np.real(circles))
        self._circles_plot.set_ydata(np.imag(circles))

    def _draw_circle(self, 
                     centre: np.complex, 
                     amp: np.complex, k: int) -> None:
        """
        Draw a single circle. Helper method for update_plots.

        Parameters:
         centre: the centre of the circle
         amp: the amplitude of the circle
         k: the index location of the circle in the circles array.
        """
        for m in range(self._pts_per_circle):
            self._circles[k + m + 1] = centre + amp*self._gen_circle[m]
