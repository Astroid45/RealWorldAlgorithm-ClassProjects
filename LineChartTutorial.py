from bridges.bridges import *
from bridges.line_chart import *
import sys
import array
from BRIDGES_API import USERNAME
from BRIDGES_API import BRIDGES_API 


def main():
    bridges = Bridges(5, USERNAME, BRIDGES_API)


    # create the Bridges object, set credentials
    bridges.set_title ("Test Line Chart Tutorial")

    plot = LineChart()
    plot.title = "Line Chart Plot"
    # set  the data
    x1 = [1, 3, 5, 20]
    y1 = [2, 3, 5, 20]
    plot.set_data_series("1", x1, y1)
    bridges.set_data_structure(plot)
    bridges.visualize()

    x2 = [2, 15.2,  40]
    y2 = [4, 30.5, 400.99]
    plot.set_data_series("2", x2, y2)
    bridges.visualize()

    plot.logarithmicx = True
    bridges.visualize()

    plot.mouse_track = True
    

    bridges.visualize()


if __name__ == "__main__":
    main()