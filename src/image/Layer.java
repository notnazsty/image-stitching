package image;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Layer {

    ArrayList<Trait> traitList;
    double percentChance;
    String layerName;
    int layerPos;
    String directoryPath;

    public Layer(double percentChance, String layerName, int layerPos, String directoryPath ) {
        this.layerName = layerName;
        this.percentChance = percentChance;
        this.traitList = getTraitsFromDirPath(directoryPath);
        this.layerPos = layerPos;
        this.directoryPath = directoryPath;
    }

    private ArrayList<Trait> getTraitsFromDirPath(String directoryPath){
        ArrayList<Trait> traitArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes((Paths.get(directoryPath + "/config.json")))));
            int traitCount = jsonObject.getInt("layerTraitCount");
            for (int i=0; i < traitCount; i++){
                String traitType = jsonObject.getJSONObject(String.valueOf(i)).getString("traitName");
                String filePath = jsonObject.getJSONObject(String.valueOf(i)).getString("filePath");
                Double traitChance = jsonObject.getJSONObject(String.valueOf(i)).getDouble("percentChance");
                Trait img = new Trait(traitChance,traitType,filePath);
                traitArrayList.add(img);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return traitArrayList;
    }


    public ArrayList<Trait> getTraitList() {
        return traitList;
    }

    public double getPercentChance() {
        return percentChance;
    }

    public String getLayerName() {
        return layerName;
    }

    public int getLayerPos() {
        return layerPos;
    }
    public String getDirectoryPath() {
        return directoryPath;
    }

    public Trait getRandomTrait(){
        int rand = new SecureRandom().nextInt(100);
        int sum = 0;
        for (Trait img : traitList) {
            sum += img.getPercentChance();
            if (rand <= sum) {
                return img;
            }
        }
        return new Trait(0.0, "Failed", "Failed");
    }

    public boolean includeLayer() {
        return Math.random() < (percentChance / 100);
    }

    @Override
    public String toString() {
        return "Layers{" +
                "Layer: " + layerName +
                ", at position: " + layerPos +
                ", traitList = \n" + traitList  +
                "\n, percentChance=" + percentChance +
                ", directoryPath='" + directoryPath + '\'' +
                ", layerName='" + layerName + '\'' +
                '}';
    }
}
