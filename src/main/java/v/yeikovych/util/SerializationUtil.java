package v.yeikovych.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializationUtil {

    private static final String DIRECTORY_PATH = "src\\main\\resources\\extents";
    private static final String FILE_ABSOLUTE_PATH = DIRECTORY_PATH + "\\ser-files.ser";
    private static final Map<Class<? extends Extent>, List<? extends Extent>> extents = new HashMap<>();

    @SuppressWarnings("all")
    public static void readExtent() {
        File dir = new File(DIRECTORY_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Directory does not exist or is not a directory: " + DIRECTORY_PATH);
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            System.out.println("No serialized files found in: " + DIRECTORY_PATH);
            return;
        }

        File file = files[0];
        String fileName = file.getName();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<Class<? extends Extent>, List<? extends Extent>> loadedExtents =
                    (Map<Class<? extends Extent>, List<? extends Extent>>) ois.readObject();

            extents.clear();
            extents.putAll(loadedExtents);

            System.out.println("Successfully deserialized all extents.");
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found during deserialization.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Failed to deserialize file.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public static void writeExtent() {
        try {
            Files.createDirectories(Paths.get(DIRECTORY_PATH));

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_ABSOLUTE_PATH))) {
                oos.writeObject(extents);
            }
            System.out.println("Extents written to: " + FILE_ABSOLUTE_PATH);
        } catch (IOException e) {
            System.out.println("Serialization failed with stacktrace: ");
            e.printStackTrace();
        }
    }

    public static <T extends Extent> void registerExtent(List<T> extent, Class<T> extentClass) {
        extents.put(extentClass, extent);
    }
}
