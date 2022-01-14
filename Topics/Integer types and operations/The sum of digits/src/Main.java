import java.util.Arrays;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int answer = Arrays.stream(scanner.next()
                        .split(""))
                        .mapToInt(Integer::valueOf)
                        .sum();

        System.out.println(answer);
    }
}