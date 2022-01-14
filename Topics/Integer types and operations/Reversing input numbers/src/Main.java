import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

class Main {
    public static void main(String[] args) {
        int[] input = Arrays.stream(new Scanner(System.in)
                        .nextLine().split("\\s+"))
                .mapToInt(Integer::valueOf)
                .toArray();

        for (int i = input.length - 1; i >= 0; i--) {
            System.out.print(input[i] + " ");
        }
    }
}