import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        // Парсим аргументы
        ArgumentParser.parse(args);

        // Далее три варианта работы программы в зависимости от команды
        int command = ArgumentParser.getCommand();
        if (command == 0) BlowfishTests.run();
        else {
            Blowfish blowfish = new Blowfish(ArgumentParser.getKey());
            blowfish.setIv(ArgumentParser.getIv());

            ArrayList<File> inputFiles = ArgumentParser.getInputFiles();

            for (File inputFile : inputFiles) {
                blowfish.resetInputBlockCFB64();

                RandomAccessFile file = new RandomAccessFile(inputFile, "rw");
                byte[] buffer = new byte[8];

                // Поблочная перезапись в файл зашифрованных/расшифрованных данных
                int read = file.read(buffer);
                while (read != -1) {
                    file.seek(file.getFilePointer() - read);

                    if (command == 1) blowfish.encryptCFB64(buffer);
                    else blowfish.decryptCFB64(buffer);

                    for (int j = 0; j < read; j++) {
                        file.writeByte(buffer[j]);
                    }

                    read = file.read(buffer);
                }

                file.close();
            }
        }
    }
}