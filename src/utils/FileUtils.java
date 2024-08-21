package utils;

import java.io.*;

public class FileUtils {
    public static Object LoadFromSerializedFile(String FileName) throws FileNotFoundException {
        FileName = "data/" + FileName;
        try (FileInputStream fileIn = new FileInputStream(FileName);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return in.readObject();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException | ClassNotFoundException i) {
            throw new RuntimeException("Error loading TaskStatusMap from file " + FileName, i);
        }
    }

    public static void StoreSerializedInFile(String FileName, Object obj) {
        FileName = "data/" + FileName;
        try (FileOutputStream fileOut = new FileOutputStream(FileName);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(obj);
            out.flush();
            System.out.println("Serialized data is saved in " + FileName);
        } catch (IOException i) {
            throw new RuntimeException("Error saving obj in file " + FileName, i);
        }
    }
}
