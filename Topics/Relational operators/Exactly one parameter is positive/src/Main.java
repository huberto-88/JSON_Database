import java.util.Scanner;
import java.util.stream.IntStream;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long counter = IntStream.generate(() -> scanner.nextInt())
                .limit(3)
                .filter(n -> n > 0)
                .count();
        System.out.println(counter > 1 ? "false" : "true");
    }
}
