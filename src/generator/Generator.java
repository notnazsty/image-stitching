package generator;

import image.Layer;
import image.Trait;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class Generator {

    boolean unique;
    int outputAmount;
    int height;
    int width;
    String outputDir;
    String inputDir;
    ArrayList<Layer> layers;
    HashMap<String, String> genImages;
    Log logger;

    public Generator(int outputAmount, boolean unique, int height, int width, String inputDir, String outputDir){
        this.logger = new Log(new File("src/examples/output/log.txt"));
        this.unique = unique;
        this.outputAmount = outputAmount;
        this.height = height;
        this.width = width;
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.genImages = new HashMap(outputAmount);
        this.layers = getAllLayers();
    }



    public void generate() throws IOException {
        logger.log(
                "Image Stitching started with the following settings: { \n"
                        + " Output# " + outputAmount + "\n"
                        + " Unique: " + unique + "\n}"
        );
        if (unique){
            int max = getMaxUniqueOutputs();
            if (  max < outputAmount ){
                logger.log("Output of " + outputAmount + " exceeds the maximum combination possibilities of "+ max );
            } else {
                generateUnique();
            }
        } else {
            generateAll();
        }

    }

    private ArrayList<Layer> getAllLayers() {
        ArrayList<Layer> allLayers = new ArrayList<>();
        logger.log("Getting layer data");
        try {
            JSONObject jsonObject = new JSONObject(new String((Files.readAllBytes(Paths.get("src/config.json")))));
            JSONArray dirPaths = jsonObject.getJSONArray("layers");
            for (int i=0; i < dirPaths.length(); i++){
                JSONObject dirConfig = new JSONObject(new String(Files.readAllBytes((Paths.get(dirPaths.get(i) + "/config.json")))));
                double layerPercentChance = dirConfig.getDouble("layerPercentage") ;
                String layerName = dirConfig.getString("layerName");
                Layer newLayer = new Layer(layerPercentChance,layerName,i,dirPaths.get(i).toString());
                allLayers.add(newLayer);
            }
            logger.log("Finished getting layer data");

        } catch (IOException e) {
            e.printStackTrace();
            logger.log(e.toString());
        }
        return allLayers;
    }



    private int getMaxUniqueOutputs() throws IOException {
        int maxOutput = 0;
        JSONObject jsonObject = new JSONObject(new String((Files.readAllBytes(Paths.get("src/config.json")))));
        JSONArray dirPaths = jsonObject.getJSONArray("layers");
        if (dirPaths.length() > 0){
            maxOutput = 1;
            for (int i=0; i < dirPaths.length(); i++){
                JSONObject dirConfig = new JSONObject(new String(Files.readAllBytes((Paths.get(dirPaths.get(i) + "/config.json")))));
                int traitCount = dirConfig.getInt("layerTraitCount");
                maxOutput *= traitCount;
            }
        }

        return maxOutput;
    }

    private String hashString(ArrayList<Trait> imgSelectedArr) {
        String hashStr = "", stringHash = "";

        for (Trait t : imgSelectedArr) {
            hashStr += t.getType();
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(hashStr.getBytes());
            stringHash = new String(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.log(e.toString());
        }
        return stringHash;
    }

    public void generateUnique(){
        for (int i= 0; i < outputAmount; i++){
            BufferedImage output = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
            ArrayList<Trait> traitsSelected = new ArrayList<>();
            do {
                traitsSelected.clear();
                for (Layer l: layers){
                    if (l.includeLayer()){
                        traitsSelected.add(l.getRandomTrait());
                    }
                }
            } while (genImages.containsKey(hashString(traitsSelected)));
            ArrayList<BufferedImage> imagesSelected = new ArrayList<>();
            for (Layer l: layers){
                imagesSelected.add(l.getRandomTrait().getImage());
            }
            createImage(imagesSelected,output,i);
            logger.log("Created image # " + i + " as " + i+".png");
        }

    }


    private void generateAll() {
        for (int c = 0; c < outputAmount; c++) {
            BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            ArrayList<BufferedImage> imagesSelected = new ArrayList<BufferedImage>();
            for (Layer currentLayer : layers) {
                if (currentLayer.includeLayer()) {
                    imagesSelected.add(currentLayer.getRandomTrait().getImage());
                }
            }
            createImage(imagesSelected, output, c);
        }
    }

    private void createImage(ArrayList<BufferedImage> imagesSelected, BufferedImage output, int c) {
        for (BufferedImage img : imagesSelected) {
            output = drawOver(output, img);
        }
        try {
            ImageIO.write(output, "png", new File("src/examples/output/" + String.valueOf(c) + ".png"));
            System.out.println("Image #" + c + " created.");
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(e.toString());
        }
    }

    private BufferedImage drawOver(BufferedImage imgOne, BufferedImage imgTwo) {
        Graphics2D gbi = imgOne.createGraphics();
        gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        gbi.drawImage(imgTwo, 0, 0, null);
        return imgOne;
    }


}
