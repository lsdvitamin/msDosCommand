package ru.otus.projectWork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/**
 * @author Sergei on 12.08.2024 13:00.
 * @project msDosCommand
 */

public class Util {

    public Util() {
    }

    /**
     * Запрашивает у пользователя ввод строки
     *
     * @return
     */
    static String inputString() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    static Path getCurrentDir() {
        return Path.of(System.getProperty("user.dir"));
    }

    static String epochToTimestamp(Long epoch) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(epoch);
    }

    static String generateNChar(Integer n) {
        return String.valueOf(" ").repeat(n);
    }

    /**
     * Печатает файл на экран
     *
     * @param fileName - полный путь + имя файла
     */
    public static void printFileOnScreen(String fileName) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            for (String line; (line = bufferedReader.readLine()) != null; ) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл помощи не найден");
        } catch (IOException e) {
            System.out.println("Ошибка вывода помощи");
        }
    }

}

