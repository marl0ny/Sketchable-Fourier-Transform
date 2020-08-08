"""
App for the circles drawing.
"""
import sys
from qt_widgets import QtWidgets, QtCore, QtGui
from qt_widgets import HorizontalSliderBox, DoubleHorizontalEntryBox
from matplotlib.backends.backend_qt5agg import FigureCanvasQTAgg
from circles_animator import CirclesAnimator
from typing import Tuple, Union


class Canvas(FigureCanvasQTAgg):
    """
    The canvas.
    """

    def __init__(self, 
                 parent: QtWidgets.QMainWindow, 
                 rect: QtCore.QRect) -> None:
        """
        The constructor.

        Parameters:
         parent: The parent widget that this
         canvas is being created from
         rect: used to get information
         about the screen width and screen height.
        """
        width = rect.width()
        dpi = int(150*width//1920)
        interval = int(1000/60)
        self._parent = parent
        self._ani = CirclesAnimator(dpi, interval)
        FigureCanvasQTAgg.__init__(self, self._ani.figure)
        # self.setMinimumWidth(500)
        self.setMinimumHeight(500)
        self._mouse_usage = 0
        self._prev_mouse_position = []

    def _mouse_coordinates_transform(self, 
                                     x: Union[int, float],
                                     y: Union[int, float]) -> Tuple[float, float]:
        """
        Transform the location of the mouse as expressed in terms
        of the coordinates of the GUI window into the coordinates
        of the plot.

        Parameters:
         x: x location of mouse
         y: y location of mouse

        Returns:
         A tuple containing the transformed x and y coordinates.
        """
        ax = self.figure.get_axes()[0]
        xlim = ax.get_xlim()
        ylim = ax.get_ylim()
        pixel_xlim = [ax.bbox.xmin, ax.bbox.xmax]
        pixel_ylim = [ax.bbox.ymin, ax.bbox.ymax]
        height = self.figure.bbox.ymax
        mx = (xlim[1] - xlim[0])/(pixel_xlim[1] - pixel_xlim[0])
        my = (ylim[1] - ylim[0])/(pixel_ylim[1] - pixel_ylim[0])
        x = (x - pixel_xlim[0])*mx + xlim[0]
        y = (height - y - pixel_ylim[0])*my + ylim[0]
        return x, y

    def _mouse_handler(self, qt_event: QtGui.QMouseEvent) -> None:
        """
        Mouse handling helper function.

        Parameters:
         qt_event: mouse event.
        """
        x = qt_event.x()
        y = qt_event.y()
        if qt_event.buttons() == QtCore.Qt.LeftButton:
            if self._mouse_usage == 0 or self._mouse_usage == 2:
                self._ani.append(self._mouse_coordinates_transform(x, y))
            elif self._mouse_usage == 1:
                x, y = self._mouse_coordinates_transform(x, y)
                if self._prev_mouse_position != []:
                    x_prev, y_prev = self._prev_mouse_position
                    dx = x - x_prev
                    dy = y - y_prev
                    # self.setCursor()
                    self._ani.move_axes(self._ani.figure.get_axes()[0],
                                        -dx, -dy)
                else:
                    self._prev_mouse_position = [x, y]
        if qt_event.buttons() == QtCore.Qt.MidButton:
            pass
        if qt_event.buttons() == QtCore.Qt.RightButton:
            pass

    def mousePressEvent(self, qt_event: QtGui.QMouseEvent) -> None:
        """
        Mouse is pressed.

        Parameters:
         qt_event: mouse event.
        """
        if qt_event.buttons() == QtCore.Qt.RightButton:
            pass
        x, y = qt_event.x(), qt_event.y()
        if self._mouse_usage == 1:
            self._prev_mouse_position = self._mouse_coordinates_transform(x, y)
        if self._mouse_usage == 0 or self._mouse_usage == 2:
            self._ani.clear()
        self._mouse_handler(qt_event)
        self.setMouseTracking(True)

    def mouseReleaseEvent(self, qt_event: QtGui.QMouseEvent) -> None:
        """
        Mouse is pressed.

        Parameters:
         qt_event: mouse event.
        """
        if qt_event.buttons() == QtCore.Qt.RightButton:
            pass
        if self._mouse_usage == 0 or self._mouse_usage == 2:
            self._ani.start_draw_circes()
            n = self._ani.get_number_of_points()
            self._parent.resolution_slider.set_range(1, n-1)
            self._parent.resolution_slider.set_number_of_ticks(n-1)
            self._parent.resolution_slider.set_slider(n-1)

    def mouseMoveEvent(self, qt_event: QtGui.QMouseEvent) -> None:
        """
        Mouse is moved.

        Parameters:
         qt_event: mouse event.
        """
        self._mouse_handler(qt_event)

    def mousePressRelease(self, qt_event: QtGui.QMouseEvent) -> None:
        """
        Mouse is released.

        Parameters:
         qt_event: mouse event.
        """
        self.setMouseTracking(False)

    def wheelEvent(self, qt_event: QtGui.QWheelEvent) -> None:
        """
        The mouse wheel is moved.

        Parameters:
         qt_event: mouse wheel event.
        """
        scroll_val = qt_event.angleDelta().y()
        ax = self._ani.figure.get_axes()[0]
        if scroll_val == 120:
            self._ani.scale_axes(ax, 0.9, 0.9)
        elif scroll_val == -120:
            self._ani.scale_axes(ax, 1.1, 1.1)

    def set_mouse_usage(self, index: int):
        """
        Setter for the mouse usage

        Parameter:
         index: a number for the mouse usage.
        """
        self._mouse_usage = index

    def get_animation(self):
        """
        Getter for the animation object.

        Returns:
         The animation object.
        """
        return self._ani

    def animation_loop(self) -> None:
        """
        Do the main animation loop.
        """
        self._ani.animation_loop()


class App(QtWidgets.QMainWindow):
    """
    Main qt5 app.
    """

    def __init__(self) -> None:
        """
        Initializer.
        """
        QtWidgets.QMainWindow.__init__(self)
        self.setWindowTitle("A simple GUI")
        self.sliders = []
        self.window = QtWidgets.QWidget(self)
        self.layout = QtWidgets.QHBoxLayout(self.window)
        rect = QtWidgets.QApplication.desktop().screenGeometry()
        self.canvas = Canvas(self, rect)
        color_name = self.window.palette().color(
                QtGui.QPalette.Background).name()
        self.canvas.get_animation().figure.patch.set_facecolor(color_name)
        self.layout.addWidget(self.canvas)
        self.setCentralWidget(self.window)
        self.canvas.animation_loop()
        self.speed_slider = HorizontalSliderBox(self, "Animation Speed ")
        self.resolution_slider = HorizontalSliderBox(self, "Number of circles ")
        self.mouse_dropdown = QtWidgets.QComboBox(self)
        self.mouse_dropdown.addItems(["Mouse: ",
                                      "Move plot view",
                                      "Draw line"])
        self.mouse_dropdown.activated.connect(self.on_mouse_dropdown_changed)
        self.widgets_layout = QtWidgets.QVBoxLayout(self.window)
        self.layout.addLayout(self.widgets_layout)
        self.widgets_layout.addWidget(self.mouse_dropdown)
        self.xy_entry = DoubleHorizontalEntryBox("Set x(t)", "Set y(t)")
        self.xy_entry.set_observers([self.canvas.get_animation(), self])
        self.sliders = []
        self.widgets_layout.addWidget(self.xy_entry)
        self.widgets_layout.addWidget(self.speed_slider)
        self.speed_slider.set_number_of_ticks(201)
        self.speed_slider.set_range(-100, 100)
        self.speed_slider.set_slider(20.0)
        self.speed_slider.set_observers([self])
        self.speed_slider.set_value_string_format("%d")
        self.widgets_layout.addWidget(self.resolution_slider)
        self.resolution_slider.set_number_of_ticks(100)
        self.resolution_slider.set_range(1, 100)
        self.resolution_slider.set_slider(100)
        self.resolution_slider.set_value_string_format("%d")
        self.resolution_slider.set_observers([self])

    def on_mouse_dropdown_changed(self, index: int) -> None:
        """
        On mouse dropdown changed

        Parameters:
         index: index of the mouse change.
        """
        self.canvas.set_mouse_usage(index)

    def on_entry_returned(self, entry_info) -> None:
        """
        On entry returned

        Parameters:
         entry_info: the dictionary containing info about
         the entry.
        """
        ani = self.canvas.get_animation()
        n = ani.get_number_of_points()
        self.resolution_slider.set_range(1, n-1)
        self.resolution_slider.set_number_of_ticks(n-1)
        self.resolution_slider.set_slider(n-1)
        ft = ani.get_functions()
        def_val = ft.get_enumerated_default_values()
        self.destroy_sliders()
        self.place_variable_sliders(def_val)


    def place_variable_sliders(self, d):
        """
        Place the variable sliders.

        Parameters:
         d: the dictionary of the variables and their values.
        """
        for i in range(len(d)):
            symbol = d[i][0]
            value = d[i][1]
            slider_box = HorizontalSliderBox(self, symbol)
            slider_box.set_range(-10.0, 10.0)
            slider_box.set_number_of_ticks(2001)
            slider_box.set_observers([self])
            slider_box.set_slider(value)
            self.widgets_layout.addWidget(slider_box)
            self.sliders.append(slider_box)
        self.widgets_layout.addWidget(self.speed_slider)
        self.widgets_layout.addWidget(self.resolution_slider)

    def destroy_sliders(self) -> None:
        """
        Destroy the sliders of the entry inputs.
        """
        while self.sliders:
            slider_box = self.sliders.pop()
            self.widgets_layout.removeWidget(slider_box)
            self.layout.removeWidget(slider_box)
            slider_box.destroy_slider()
            slider_box.close()
        self.widgets_layout.removeWidget(self.speed_slider)
        self.widgets_layout.removeWidget(self.resolution_slider)


    def on_slider_changed(self, slider_dict) -> None:
        """
        On slider changed.

        Parameters:
         slider_dict: dictionary containing information
         about the slider.
        """
        if slider_dict['id'] == "Animation Speed ":
            speed = slider_dict['value']
            self.canvas.get_animation().set_animation_speed(speed)
        elif slider_dict['id'] == "Number of circles ":
            resolution = slider_dict['value']
            self.canvas.get_animation().set_circle_resolution(int(resolution)+1)
        elif self.sliders != []:
            params = []
            for slider in self.sliders:
                info = slider.get_slider_info()
                params.append(info['value'])
            ani = self.canvas.get_animation()
            ani.set_params(*params)


if __name__ == "__main__":
    import matplotlib.pyplot as plt
    qt_app = QtWidgets.QApplication(sys.argv)
    app = App()
    app.show()
    sys.exit(qt_app.exec_())
