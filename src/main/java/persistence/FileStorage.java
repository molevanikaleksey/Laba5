package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.AttachmentManager;
import service.FilleManager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

public class FileStorage {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .setPrettyPrinting()
            .create();
    public void save(String path, FilleManager fileManager, AttachmentManager attachmentManager){
        DataSnapShot snapShot = new DataSnapShot(
                attachmentManager.getAllLinksForSave(),
                fileManager.getAllFiles()
                );
        try(FileWriter writer = new FileWriter(path)){
            gson.toJson(snapShot, writer);
        }catch (IOException e){
            throw new IllegalArgumentException("Ошибка сохранения: не удалось сохранить файл");
        }
    }
    public void load(String path, FilleManager filleManager, AttachmentManager attachmentManager){
        DataSnapShot snapShot;
        try(FileReader reader = new FileReader(path)){
            snapShot = gson.fromJson(reader, DataSnapShot.class);
        }catch(IOException e){
            throw new IllegalArgumentException("Ошибка загрузки: не удалось загрузить файл");
        }catch(Exception e){
            throw new IllegalArgumentException("Ошибка загрузки: Неверный формат");
        }
        FileValidator.validate(snapShot);

        filleManager.replaceAllFiles(snapShot.getFiles());
        attachmentManager.loadFromDatabase();

        //attachmentManager.replaceAllLinks(snapShot.getLinks());
    }

}
