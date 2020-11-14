package net.nekomura.utils.jixiv.Utils;

public class SortUtils {

    /**
     * 泡沫排列(小到大)
     * @param arr 數組
     * @return 經由泡沫排列排列完成的陣列(小到大)
     */
    public static int[] bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j + 1];
                    arr[j + 1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }

    /**
     * 泡沫排列(大到小)
     * @param arr 數組
     * @return 經由泡沫排列排列完成的陣列(大到小)
     */
    public static int[] reverseBubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] < arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        return arr;
    }

}
