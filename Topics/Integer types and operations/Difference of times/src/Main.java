import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LocalDateTime start = LocalDateTime.of(2000, 1, 1, scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
        LocalDateTime stop = LocalDateTime.of(2000, 1, 1, scanner.nextInt(), scanner.nextInt(), scanner.nextInt());

        System.out.println(Duration.between(start, stop).getSeconds());
    }
}