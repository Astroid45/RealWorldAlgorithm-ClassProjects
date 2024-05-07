from bridges import *
from bridges.sorting_benchmark import *
import random
import sys
from BRIDGES_API import BRIDGES_API
from BRIDGES_API import USERNAME
done_plotting = False

def insertion_sort(arr):
    n = len(arr)
    comparisons = 0 #* Comparison used to count operations per sort
    for i in range(1, n):
        key = arr[i]
        j = i - 1
        while j >= 0 and key < arr[j]:
            comparisons += 1
            arr[j + 1] = arr[j]
            j -= 1
        arr[j + 1] = key

    return comparisons

def bubblesort(arr): #* Comparison is used to count operations per sort
    n = len(arr)
    comparisons = 0
    for i in range(n-1):
        for j in range(n-i-1):
            comparisons += 1
            if arr[j] > arr[j+1]:
                arr[j], arr[j+1] = arr[j+1], arr[j]  

    return comparisons

def main():
    bridges = Bridges(6, USERNAME, BRIDGES_API )
    bridges.set_title("Sorting Benchmark")
    bridges.set_description("Plot the performance of sorting algorithms using Bridges Line Chart.")
    
    plot = LineChart()
    plot.title = "Bubble vs. Insertion Sort Runtime"
    bench = SortingBenchmark(plot)
    bench.linear_range(0, 10000, 10)  #* Reduced range so my computer could handle the runtime
    bench.run("Insertion Sort", insertion_sort)
    bench.run("Bubble Sort", bubblesort)
    plot.mouse_track = True
    bridges.set_data_structure(plot)
    bridges.visualize()
    
    
    plot2 = LineChart()
    plot2.title = 'Operation Count: Bubble vs. Insertion Sort'
    
    x_values = [size for size in range(1000, 10001, 1000)] #* Had to reduce the range to 10,000 due to my computer not being able to run large amounts in a decent time
    y_insertion_sort_comparisons = []
    y_bubble_sort_comparisons = []

    for size in x_values:  
        arr = random.sample(range(1, 10001), k=size)  #* Generates a random unsorted list to sort 
        comparisons_insertion_sort = insertion_sort(arr)
        comparisons_bubble_sort = bubblesort(arr)
        y_insertion_sort_comparisons.append(comparisons_insertion_sort)
        y_bubble_sort_comparisons.append(comparisons_bubble_sort)
    
    plot2.set_data_series("Insertion Sort", x_values, y_insertion_sort_comparisons)
    plot2.set_data_series("Bubble Sort", x_values, y_bubble_sort_comparisons)

    bridges.set_data_structure(plot2)
    plot2.mouse_track = True
    bridges.visualize()

if __name__ == "__main__":
    main()
