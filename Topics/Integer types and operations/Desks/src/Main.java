import java.util.Scanner;
import java.util.stream.IntStream;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int result = IntStream.generate(scanner::nextInt)
                .limit(3)
                .map(n -> (int) Math.ceil(n / 2.0) )
                .sum();

        System.out.println(result);
    }
}
