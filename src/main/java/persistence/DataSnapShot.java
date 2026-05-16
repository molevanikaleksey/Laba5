package persistence;


import domain.AttachmentLink;
import domain.FileMeta;

import java.util.ArrayList;
import java.util.List;

public class DataSnapShot {
    private List<FileMeta> files = new ArrayList<>();
    private List<AttachmentLink> links = new ArrayList<>();

    public DataSnapShot(){}

    public DataSnapShot(List<AttachmentLink> links, List<FileMeta> files) {
        this.links = links;
        this.files = files;
    }
    public List<FileMeta> getFiles(){
        return files;
    }
    public void setFiles(List<FileMeta> files){
        this.files = files;
    }


    public List<AttachmentLink> getLinks(){
        return links;
    }
    public void setLink(List<AttachmentLink> links){
        this.links = links;
    }
}
