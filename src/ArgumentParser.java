import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;

public class ArgumentParser {

    private static int command;
    private static byte[] key;
    private static byte[] iv;
    private static ArrayList<File> inputFiles;

    /**
     * Метод парсинга аргументов.
     *
     * @param args массив из аргументов
     * @throws Exception Неверные аргументы.
     */
    public static void parse(String[] args) throws Exception {
        // Проверяем правильность введенных аргументов
        if (!(args != null &&
                (args.length >= 4 && (args[0].equals("enc") || args[0].equals("dec"))
                        || args.length == 1 && args[0].equals("test")))) {
            throw new Exception("Wrong arguments!");
        }

        if (args[0].equals("test")) command = 0;
        else {
            if (args[0].equals("enc")) command = 1;
            else command = 2;

            // Хэш пароля функцией SHA3-384
            key = MessageDigest.getInstance("SHA3-384").digest(args[1].getBytes());

            // Обрабатываем синхропосылку
            File ivFile = new File(args[2]);
            // Если размер >= 8 байт, то считываем
            if (ivFile.length() >= 8L) iv = Files.readAllBytes(ivFile.toPath());
                // Иначе генерируем новую синхропосылку и записываем ее по указанному пути
            else {
                iv = new byte[16];

                SecureRandom secureRandom = new SecureRandom();
                secureRandom.nextBytes(iv);

                Files.write(ivFile.toPath(), iv);
            }

            /*
            Составляем список файлов для шифрования,
            если файл является директорией, то добавляем рекурсивно все файлы оттуда
             */
            inputFiles = new ArrayList<>();
            for (int i = 3; i < args.length; i++) {
                File file = new File(args[i]);

                if (!file.isDirectory()) inputFiles.add(file);
                else addFilesRecursive(file, inputFiles);
            }
        }
    }

    /**
     * Геттер номера команды.
     *
     * @return 0 - test;
     * 1 - enc;
     * 2 - dec.
     */
    public static int getCommand() {
        return command;
    }

    /**
     * Геттер ключа.
     *
     * @return Массив байтов, состоящий из хэша (SHA3-384) пароля.
     */
    public static byte[] getKey() {
        return key;
    }

    /**
     * Геттер синхропосылки.
     *
     * @return Синхропосылку из файла.
     */
    public static byte[] getIv() {
        return iv;
    }

    /**
     * Геттер списка входных файлов.
     *
     * @return Список входных файлов.
     */
    public static ArrayList<File> getInputFiles() {
        return inputFiles;
    }

    /**
     * Метод рекурсивного добавления файлов из папки.
     *
     * @param directory  папка, из которой надо добавить файлы
     * @param inputFiles список файлов, в который вносятся все файлы текущей папки
     */
    private static void addFilesRecursive(File directory, ArrayList<File> inputFiles) {
        File[] currentDirectoryFiles = directory.listFiles();

        if (currentDirectoryFiles != null) {
            for (int i = 0; i < currentDirectoryFiles.length; i++) {
                if (!currentDirectoryFiles[i].isDirectory())
                    inputFiles.add(currentDirectoryFiles[i]);
                else addFilesRecursive(currentDirectoryFiles[i], inputFiles);
            }
        }
    }
}
