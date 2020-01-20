"""
Abstract animator class that manages the animation.
"""
import matplotlib.pyplot as plt
from matplotlib.lines import Line2D
from matplotlib.text import Text
from matplotlib.collections import Collection, PathCollection
from matplotlib.quiver import QuiverKey, Quiver
import matplotlib.animation as animation
from typing import List
from time import perf_counter


artists = [Line2D, Collection, Text, QuiverKey, Quiver, PathCollection]


class Animator:
    """
    Abstract animation class that adds a small layer of abstraction
    over the Matplotlib animation functions and interfaces.

    To use this class:
        -Inherit this in a derived class.
        -The Figure object is already instantiated in this class as the
         attribute self.figure. Create instances of
         plotting objects from this, such as Line2D.
        -Update the plots inside the update method, which must be
         overridden.
        -Call the animation_loop method to show the animation.

    Attributes:
    figure [Figure]: Use this to obtain plot elements.
    """

    def __init__(self, dpi: int,
                 animation_interval: int) -> None:
        """
        Initializer

        Parameters:
         dpi: dots per square inches.
         animation_interval: minimum number of
         milliseconds between each frame.
        """
        self.dots_per_inches = dpi
        self.animation_interval = animation_interval

        self.figure = plt.figure(
                dpi=self.dots_per_inches,
                figsize=(6, 6)
        )
        self.main_animation = None

        # All private attributes.
        self._plots = []
        self._delta_t = 1.0/60.0
        self._t = perf_counter()

    def add_plot(self, plot: plt.Artist) -> None:
        """
        Add a list of plot objects so that they can be animated.

        Parameters:
         plot: the plot artist to add.
        """
        self._plots.append(plot)

    def add_plots(self, plot_objects: List[plt.Artist]) -> None:
        """
        Add multiple plots to be animated.

        Parameters:
         plot_objects: the list of plot artists to add.
        """
        self._plots.extend(plot_objects)

    def get_plot(self, index: int) -> plt.Artist:
        """
        Getter for a plot.

        Parameter:
         index: get a plot at an index.

        Returns:
         The plot artist to get.
        """
        return self._plots[index]

    def set_plot(self, index: int,
                 plot_object: plt.Artist) -> None:
        """
        Setter for a plot.

        Parameter:
         index: index to set the plot
         plot_object: the plot object to set.
        """
        self._plots[index] = plot_object

    def update(self, delta_t: float) -> None:
        """
        Update how each plots will change between each animation frame.
        This must be implemented in any derived classes.

        Parameter:
         delta_t: the time interval between the last frame
         and this one in seconds.
        """
        raise NotImplementedError

    def _make_frame(self, i: int) -> list:
        """
        Generate a single animation frame.

        Parameter:
         i: the current index of the animation.
        """
        self.update(self._delta_t)
        t = perf_counter()
        self._delta_t = t - self._t
        self._t = t
        # print(self._plots)
        return self._plots

    def _add_plots(self) -> None:
        """
        Add plots before doing the main animation loop.
        """
        text_objects = []  # Ensure that text boxes are rendered last
        self_dict = self.__dict__
        for key in self_dict:
            if any([isinstance(self_dict[key], artist) for
                    artist in artists]):
                if self_dict[key] not in self._plots:
                    # Ensure that text boxes are rendered last
                    if isinstance(self_dict[key], Text):
                        text_objects.append(self_dict[key])
                    else:
                        self._plots.append(self_dict[key])
        self._plots.extend(text_objects)

    def animation_loop(self) -> None:
        """This method plays the animation. This must be called in order
        for an animation to be shown.
        """
        self._add_plots()
        self.main_animation = animation.FuncAnimation(
                self.figure,
                self._make_frame,
                blit=True,
                interval=self.animation_interval,
                # init_func=lambda *arg: []
        )

    def is_blit(self) -> bool:
        """
        Check if blitting is turned on or not.
        """
        if self.main_animation is not None:
            return self.main_animation._blit
        else:
            return False

    def toggle_blit(self) -> None:
        """
        Toggle blit. This is used so that it is possible to
        update the appearance of the plot title and axes, which would otherwise
        be entirely static with blitting.
        """
        # TODO: Find a better way of updating the axes of the plot
        # that uses blitting and does not access the
        # protected members of the Animation class.
        if self.main_animation._blit:
            self.main_animation._blit_clear(
                self.main_animation._drawn_artists, 
                self.main_animation._blit_cache)
            self.main_animation._blit = False
        else:
            # self.main_animation._init_draw()
            self.main_animation._step()
            self.main_animation._blit = True
            self.main_animation._setup_blit()

    def scale_axes(self, ax, 
                   x_scale_factor: float,
                   y_scale_factor: float) -> None:
        """
        Enlarge or reduce the range of the axes of the plots,
        with respect to the centre of the plot.

        Parameters:
         ax: the AxesSubplot object to modify. 
         x_scale_factor: scale the x axes.
         y_scale_factor: scale the y axes.
        """
        xlim = ax.get_xlim()
        ylim = ax.get_ylim()
        dx = xlim[1] - xlim[0]
        dy = ylim[1] - ylim[0]
        xc = (xlim[1] + xlim[0])/2
        yc = (ylim[1] + ylim[0])/2
        xlim = [xc - x_scale_factor*dx/2.0, xc + x_scale_factor*dx/2.0]
        ylim = [yc - y_scale_factor*dy/2.0, yc + y_scale_factor*dy/2.0]
        self.toggle_blit()
        ax.set_xlim(xlim)
        ax.set_ylim(ylim)
        self.toggle_blit()

    def move_axes(self, ax, 
                  move_by_x: float, move_by_y: float) -> None:
        """
        Translate the x and y axes.

        Parameters:
         ax: the AxesSubplot object to modify.
         move_by_x: translation value for the x axis.
         move_by_y: translation value for the y axis.
        """
        xlim = ax.get_xlim()
        ylim = ax.get_ylim()
        xlim = [xlim[0] + move_by_x,
                xlim[1] + move_by_x]
        ylim = [ylim[0] + move_by_y,
                ylim[1] + move_by_y]
        self.toggle_blit()
        ax.set_xlim(xlim)
        ax.set_ylim(ylim)
        self.toggle_blit()
