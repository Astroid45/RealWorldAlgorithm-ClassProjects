import bridges.base.LineChart;
import bridges.benchmark.SortingBenchmark;
import bridges.connect.Bridges;
import bridges.validation.RateLimitException;
import java.io.IOException;
import java.util.function.Consumer;

public class Quick_Sort {
    
    private static void insertionSort(int arr[], int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            for (int j = i - 1; j >= low; j--) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                } else {
                    break;
                }
            }
        }
    }

    private static int hoarePartition(int arr[], int low, int high) {
        int pivot = arr[low]; 
        int i = low - 1;
        int j = high + 1;

        while (true) {
            do {
                i++;
            } while (arr[i] < pivot);

            do {
                j--;
            } while (arr[j] > pivot);

            if (i >= j) {
                return j;
            }
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    private static int optimizedQuickSort(int arr[], int low, int high){
        //* This finds the middle point of the array */
        int mid = (low + high) / 2;

        //* Below determines the index of the pivot element by comparing values at low, high, and mid range */ 
        int pivotIndex = (arr[low] < arr[mid]) ? ((arr[mid] < arr[high]) ? mid : ((arr[low] < arr[high]) ? high : low))
                : ((arr[high] < arr[mid]) ? mid : ((arr[high] < arr[low]) ? high : low));
        
        //* Gets the pivot value */
        int pivot = arr[pivotIndex];

        int i = low - 1;
        int j = high + 1;

        //* Below is the actual partitioning loop used */
        while (true) {
            do {
                i++;
            } while (arr[i] < pivot);

            do {
                j--;
            } while (arr[j] > pivot);

            if (i >= j) {
                return j;
            }
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void hybridQuickSort(int arr[], int low, int high, Consumer<int[]> sortingConsumer) {
        while (low < high) {
            if (high - low < 100) {
                insertionSort(arr, low, high);
                break;
            } else {
                int pivot = hoarePartition(arr, low, high);

                if (pivot - low < high - pivot) {
                    hybridQuickSort(arr, low, pivot, sortingConsumer);
                    low = pivot + 1;
                } else {
                    hybridQuickSort(arr, pivot + 1, high, sortingConsumer);
                    high = pivot;
                }
            }
        }
    }

    public static void hybridQuickSort2(int arr[], int low, int high, Consumer<int[]> sortingConsumer) {
        while (low < high) {
            if (high - low < 100) {
                insertionSort(arr, low, high);
                break;
            } else {
                int pivot = optimizedQuickSort(arr, low, high);

                if (pivot - low < high - pivot) {
                    hybridQuickSort2(arr, low, pivot, sortingConsumer);
                    low = pivot + 1;
                } else {
                    hybridQuickSort2(arr, pivot+1, high, sortingConsumer);
                    high = pivot - 1;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, RateLimitException, InterruptedException {
        String username = Credential.readUsername();
        String apiKey = Credential.readApiKey();
        Bridges bridges = new Bridges(8, username, apiKey);
        bridges.setTitle("Sorting Benchmark");
        bridges.setDescription("Sorting Benchmark test");

        LineChart plot = new LineChart();
        plot.setTitle("Quick Sort Runtime");

        SortingBenchmark bench = new SortingBenchmark(plot);

        bench.linearRange(100, 100000000, 20);

        // Pass hybridQuickSort as a Consumer<int[]>
        Consumer<int[]> hybridQuickSortConsumer = arr -> hybridQuickSort(arr, 0, arr.length - 1, null);
        bench.run("Hybrid Quick Sort", hybridQuickSortConsumer);

        Consumer<int[]> hybridQuickSortConsumer2 = arr -> hybridQuickSort2(arr, 0, arr.length - 1, null);
        bench.run("Quick Sort: NLogN ", hybridQuickSortConsumer2);

        bridges.setDataStructure(plot);
        bridges.visualize();
    }
}
