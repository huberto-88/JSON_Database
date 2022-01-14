import java.util.Arrays;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        int[] input = Arrays.stream(new Scanner(System.in)
                        .nextLine().split("\\s+"))
                .mapToInt(Integer::valueOf)
                .toArray();

        for (int i = 0; i < input.length; i++) {
            System.out.println(input[i]);
        }
    }
}