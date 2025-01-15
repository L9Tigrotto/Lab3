
import Networking.Connection;
import Networking.RequestHandler;

import java.util.Scanner;

public class Test
{

    public static void Run(Connection connection)
    {
        Scanner scanner = new Scanner(System.in);
        String input;
        String[] words;

        // ##### LOGIN #####
        input = "login leon 5678";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendLogin(connection, words);

        /*
        // ##### MARKET ASK #####
        input = "insertMarketOrder ask 20";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertMarketOrder(connection, words);
        //scanner.nextLine();

        // ##### MARKET BID #####
        input = "insertMarketOrder bid 20";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertMarketOrder(connection, words);
        //scanner.nextLine();

        // ##### LIMIT ASK #####
        input = "insertLimitOrder ask 20 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        //scanner.nextLine();

        // ##### LIMIT BID #####
        input = "insertLimitOrder bid 20 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        //scanner.nextLine();

        // ##### LIMIT BID #####
        input = "insertLimitOrder bid 20 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        //scanner.nextLine();

        // ##### LIMIT ASK #####
        input = "insertLimitOrder ask 20 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        //scanner.nextLine();
        */
        // ##### LIMIT ASK #####
        input = "insertLimitOrder ask 20 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        scanner.nextLine();

        // ##### LIMIT BID #####
        input = "insertLimitOrder bid 10 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        scanner.nextLine();

        // ##### LIMIT BID #####
        input = "insertLimitOrder bid 10 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);


        System.out.println("asdasdasdsa");
        scanner.nextLine();

        /*
        // ##### LIMIT BID #####
        input = "insertLimitOrder bid 20 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        //scanner.nextLine();

        // ##### MARKET ASK #####
        input = "insertMarketOrder ask 10";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertMarketOrder(connection, words);
        //scanner.nextLine();

        // ##### MARKET ASK #####
        input = "insertMarketOrder ask 10";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertMarketOrder(connection, words);
        //scanner.nextLine();

        // ##### STOP ASK #####
        input = "insertStopOrder ask 100 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertStopOrder(connection, words);
        //scanner.nextLine();

        // ##### LIMIT BID #####
        input = "insertLimitOrder bid 50 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        scanner.nextLine();

        // ##### LIMIT BID #####
        input = "insertLimitOrder bid 50 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        //scanner.nextLine();

        // ##### STOP BID #####
        input = "insertStopOrder bid 100 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertStopOrder(connection, words);
        //scanner.nextLine();

        // ##### LIMIT ASK #####
        input = "insertLimitOrder ask 50 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        scanner.nextLine();

        // ##### LIMIT ASK #####
        input = "insertLimitOrder ask 50 1000";
        words = input.split(" ");
        System.out.printf("[TEST] %s\n", input);
        RequestHandler.SendInsertLimitOrder(connection, words);
        //scanner.nextLine();
        */
    }
}
