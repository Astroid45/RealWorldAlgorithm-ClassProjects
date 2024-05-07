from bridges import *
from bridges.sorting_benchmark import *
from sorting_benchmark import *
from BRIDGES_API import USERNAME
from BRIDGES_API import BRIDGES_API
import sys
import random

def linear_search(arr): 
    comparisons = 0
    value_array=set(random.sample(arr, min(100, len(arr)))) #* Creates random list of values to search for comparison
    for value in value_array:
        for i in range(len(arr)):
            comparisons +=1 
            if arr[i] == value:
                break
    return comparisons


def binary_search(arr): 
    comparisons = 0
    values_search = set(random.sample(arr, min(100, len(arr)))) #* Creates random list of values to search for comparison 
    for value in values_search:
        left, right = 0, len(arr)-1
        while left <= right:
            comparisons+=1
            mid = left + (right-left)//2
            if arr[mid] == value:
                break
            elif arr[mid] < value:
                left = mid+1
            else:
                right = mid-1
    return comparisons #* This just is a counter value for comparison of operations within the search function
            
        
        
        

def main():
    args = ('5', USERNAME, BRIDGES_API)
    # create the Bridges object, set credentials
    # command line args provide credentials and server to test on
    args = sys.argv[1:]
    bridges = Bridges(int(args[0]), args[1], args[2])
    if len(args) > 3:
        bridges.connector.set_server(args[3])

    bridges.set_title("Sorting Benchmark")
    bridges.set_description("Plot the performance of sorting algorithms using Bridges Line Chart.")

    plot = LineChart()
    plot.title = "Run Times: Linear vs. Binary"
    bench = SortingBenchmark(plot)
    bench.generator = "random"
    bench.linear_range(10000, 1000000, 10)
    bench.run("linear-search", linear_search)
    bench.run("binary-search", binary_search)

    bridges.set_data_structure(plot)
    plot.mouse_track = True
    bridges.visualize()

    plot2 = LineChart()
    plot2.title = "Operation Counts: Linear vs. Binary Search"
    initial_point = 0 
    final_point = 1000000
    num_points = 10
    x1 = sorted([random.uniform(initial_point, final_point) for _ in range(num_points)]) #* Creates random x valued list for searching operations
    y1 = []
    y2 = []

    for size in x1: #* for loop for running searches
        arr = sorted(random.choices(range(1, 10001), k=int(size)))
        y1.append(linear_search(arr))
        y2.append(binary_search(arr))

    plot2.set_data_series("Linear Search", x1, y1)
    plot2.set_data_series("Binary Search", x1, y2)

    bridges.set_data_structure(plot2)
    plot2.mouse_track = True
    bridges.visualize()
    
    plot3 = LineChart()
    plot3.title = 'Operation Count: Binary Search (Log profile)'
    x2 = [size for size in range(0, 10000001, 100000)] #* When testing used a size of 1 million in incriments of 100k
    y3 = []

    for size in x2:
        arr = random.choices(range(1, 10001), k=size)
        y3.append(binary_search(arr))
    plot3.set_data_series("Binary Search", x2, y3)
    bridges.set_data_structure(plot3)
    plot3.mouse_track = True
    bridges.visualize()
    
    


if __name__ == "__main__":
    main()
