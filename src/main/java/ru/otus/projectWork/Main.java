package ru.otus.projectWork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static ru.otus.projectWork.Util.*;


/**
 * @author Sergei on 11.08.2024 13:37.
 * @project Default (Template) Project
 */
public class Main {
    public static Integer finded = 0;

    public static void main(String[] args) throws IOException {
        Session session = new Session(getCurrentDir());
        String strInput = "";
        String[] commands;
        printInvition(session);
        while (true) {
            strInput = inputString();
            commands = strInput.split(" ");
            if (commands[0].equals("ls")) {
                if (commands.length == 2) {
                    processingLs(session, commands[1]);
                } else if (commands.length == 1) {
                    processingLs(session);
                }
                printInvition(session);
            } else if (commands[0].equals("cd")) {
                session.setCurrentPath(processingCd(session, commands[1]));
                printInvition(session);
            } else if (commands[0].equals("mkdir")) {
                processingMkDir(session, commands[1]);
                printInvition(session);
            } else if (commands[0].equals("rm")) {
                processingRm(commands[1]);
                printInvition(session);
            } else if (commands[0].equals("cp")) {
                if (commands.length == 3) {
                    processingCp(commands[1], commands[2]);
                } else if (commands.length == 4) {
                    processingCp(commands[1], commands[2], commands[2]);
                }
                printInvition(session);
            } else if (commands[0].equals("mv")) {
                if (commands.length == 3) {
                    processingMv(commands[1], commands[2]);
                } else if (commands.length == 4) {
                    processingMv(commands[1], commands[2], commands[2]);
                }
                printInvition(session);
            } else if (commands[0].equals("finfo")) {
                detailedDescriptionFile(session, commands[1]);
                printInvition(session);
            } else if (commands[0].equals("find")) {
                String beginPath = String.valueOf(session.getCurrentPath());
                File searchFile;
                Integer findedRoot = 0;
                finded = 0;
                searchFile = new File(beginPath, commands[1]);
                if (searchFile.isFile()) {
                    System.out.println("   " + searchFile.getPath());
                    findedRoot += 1;
                }
                finded = findedRoot + findFile(beginPath, commands[1]);
                if (finded == 0) {
                    System.out.println("Файл не найден");
                } else {
                    System.out.println("Найдено файлов: " + finded);
                }
                printInvition(session);
            } else if (commands[0].equals("help")) {
                Path helpPath = Paths.get(getCurrentDir().toString(), "src", "main", "resources", "help.txt");
                printFileOnScreen(String.valueOf(helpPath));
                printInvition(session);
            } else if (Arrays.asList("/q", "exit").contains(commands[0])) {
                System.out.println("Работа завершена");
                break;
            } else if (strInput.equals("")) {
                System.out.print(session.getCurrentPath() + ">");
            } else {
                System.out.println("Неизвестная команда");
                printInvition(session);
            }
        }
    }

    /**
     * Ищет файл во всех вложенных директориях (команда find)
     *
     * @param path     - путь начала поиска
     * @param fileName - имя файла
     * @return - количество найденых файлов.
     */
    private static Integer findFile(String path, String fileName) {
        File dir = new File(path);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                String childFolderName = file.getParent() + "\\" + file.getName();
                File searchFile = new File(childFolderName, fileName);
                if (searchFile.isFile()) {
                    System.out.println("   " + searchFile.getPath());
                    finded += 1;
                }
                findFile(childFolderName, fileName);
            }
        }
        return finded;
    }

    /**
     * Выводит на экран подробную информацию о файле (команда finfo)
     *
     * @param session  - ссылка на сессию с текущей директорией
     * @param fileName - имя файла
     */
    public static void detailedDescriptionFile(Session session, String fileName) {
        try {
            File file = new File(fileName);
            Path path = Paths.get(fileName);
            BasicFileAttributes attr;
            attr = Files.readAttributes(path, BasicFileAttributes.class);
            System.out.println("   Расположение: " + file.getParent());
            System.out.println("   Размер: " + file.length() + " байт");
            System.out.println("   Создан: " + attr.creationTime());
            System.out.println("   Изменен: " + attr.lastModifiedTime());
            System.out.println("   Открыт: " + attr.lastAccessTime());
            System.out.println("   Только чтение: " + file.canRead());
            System.out.println("   Запись: " + file.canWrite());
            System.out.println("   Скрытый: " + file.isHidden());
        } catch (
                FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Возникла ошибка при выводе подробной информации");
        }
    }

    /**
     * переход в директорию выше или ниже (команда cd)
     *
     * @param session - ссылка на сессию с текущей директорией
     * @param command - команда
     * @return
     */
    public static Path processingCd(Session session, String command) {
        if (command.equals("..")) {
            try {
                if (Files.exists(session.getCurrentPath().getParent())) {
                    return session.getCurrentPath().getParent();
                }
            } catch (NullPointerException e) {
                return session.getCurrentPath();
            }
        }
        if (!command.contains("\\") && !command.contains("/")) {
            command = String.valueOf(Paths.get(String.valueOf(session.getCurrentPath()), command));
            if (Files.exists(Path.of(command))) {
                return Path.of(command);
            } else {
                System.out.println("Неизвестный параметр cd");
            }
        }
        return session.getCurrentPath();
    }

    /**
     * Создает новую директорию (команда mkdir)
     *
     * @param session   - ссылка на сессию с текущей директорией
     * @param newFolder - имя новой директории
     */
    public static void processingMkDir(Session session, String newFolder) {
        String currentPath = String.valueOf(session.getCurrentPath());
        final File newDirectory = new File(currentPath, newFolder);
        if (!newDirectory.exists()) {
            newDirectory.mkdir();
        }
    }

    /**
     * Удаляет файл или директорию (команда rm)
     *
     * @param deleteObject - полный путь + имя файла/директории
     */
    public static void processingRm(String deleteObject) {
        final File deleteDirectory = new File(deleteObject);
        if (deleteDirectory.exists()) {
            deleteDirectory.delete();
        } else {
            System.out.println("Файл/директория не существует");
        }
    }

    /**
     * Выводит на экран список файлов и директорий текущего каталога с датой изменения и размером (команда ls -i)
     *
     * @param session - ссылка на сессию с текущей директорией
     * @param param
     */
    public static void processingLs(Session session, String param) {
        if (param.equals("-i")) {
            String currentPath = String.valueOf(session.getCurrentPath());
            File dir = new File(currentPath);
            Integer maxLenFileName = 0;
            Integer maxLenSize = 0;
            for (File file : dir.listFiles()) {
                if (file.getName().length() > maxLenFileName) {
                    maxLenFileName = file.getName().length();
                }
                if ((file.length() / 1024 + "Kb").length() > maxLenSize) {
                    maxLenSize = (file.length() / 1024 + "Kb").length();
                }
            }
            maxLenFileName += 4;
            maxLenSize += 4;
            Integer lenOneCol = 0;
            Integer lenTwoCol = 0;
            for (File file : dir.listFiles()) {
                lenOneCol = maxLenFileName - file.getName().length();
                lenTwoCol = maxLenSize - (file.length() / 1024 + "Kb").length();
                System.out.println(file.getName() + generateNChar(lenOneCol) +
                        file.length() / 1024 + "Kb" + generateNChar(lenTwoCol) + epochToTimestamp(file.lastModified()));
            }
        } else System.out.println("Неизвестный параметр ls");
    }

    /**
     * Выводит на экран список файлов и директорий текущего каталога (команда ls)
     *
     * @param session - ссылка на сессию с текущей директорией
     */
    public static void processingLs(Session session) {
        String currentPath = String.valueOf(session.getCurrentPath());
        File dir = new File(currentPath); //path указывает на директорию
        for (File file : dir.listFiles()) {
            System.out.println(file.getName());
        }
    }

    /**
     * Выводит на экран приглашение в виде текущей директории
     *
     * @param session - ссылка на сессию с текущей директорией
     */
    private static void printInvition(Session session) {
        System.out.println("");
        System.out.print(session.getCurrentPath() + ">");
    }

    /**
     * Копирует файл из одной директории в другую (команда cp)
     *
     * @param source      - файл источник
     * @param destination - файл назначения
     */
    public static void processingCp(String source, String destination) {
        File fileSource = new File(source);
        File fileDestination = new File(destination);
        try {
            Path bytes = Files.copy(fileSource.toPath(), fileDestination.toPath(), COPY_ATTRIBUTES, NOFOLLOW_LINKS);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Не могу выполнить, файл уже существует");
        } catch (NoSuchFileException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
        }
    }

    /**
     * Копирует файл из одной директории в другую с заменой файла (команда cp -f)
     *
     * @param parameter   - параметр
     * @param source      - файл источник
     * @param destination - файл назначения
     */
    public static void processingCp(String parameter, String source, String destination) {
        if (parameter.equals("-f")) {
            File fileSource = new File(source);
            File fileDestination = new File(destination);
            try {
                Path bytes = Files.copy(fileSource.toPath(), fileDestination.toPath(),
                        REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);
            } catch (NoSuchFileException e) {
                System.out.println("Файл не найден");
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода");
            }
        } else {
            System.out.println("Неизвестный параметр cp");
        }
    }

    /**
     * Переносит файл/директорию из одной директории в другую (команда mv)
     *
     * @param source      - файл источник
     * @param destination - файл назначения
     */
    public static void processingMv(String source, String destination) {
        File fileSource = new File(source);
        File fileDestination = new File(destination);
        try {
            Path bytes = Files.move(fileSource.toPath(), fileDestination.toPath());
        } catch (FileAlreadyExistsException e) {
            System.out.println("Не могу выполнить, файл уже существует");
        } catch (NoSuchFileException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода");
        }
    }

    /**
     * Переносит файл/директорию из одной директории в другую с заменой файла (команда mv -f)
     *
     * @param parameter   - параметр
     * @param source      - файл источник
     * @param destination - файл назначения
     */
    public static void processingMv(String parameter, String source, String destination) {
        if (parameter.equals("-f")) {
            File fileSource = new File(source);
            File fileDestination = new File(destination);
            try {
                Path bytes = Files.move(fileSource.toPath(), fileDestination.toPath(), REPLACE_EXISTING);
            } catch (NoSuchFileException e) {
                System.out.println("Файл не найден");
            } catch (IOException e) {
                System.out.println("Ошибка ввода/вывода");
            }
        } else {
            System.out.println("Неизвестный параметр mv");
        }
    }
}