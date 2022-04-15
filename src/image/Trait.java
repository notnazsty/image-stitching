package image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Trait {

    BufferedImage image;
    double percentChance;
    String type, filePath;

    public Trait(double percentChance, String type, String filePath) {
        this.filePath = filePath;
        this.type = type;
        this.percentChance = percentChance;

        try {
            this.image = ImageIO.read(new File(filePath));
        } catch (IOException e){
            System.err.println(e);
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getPercentChance() {
        return percentChance;
    }

    public String getType() {
        return type;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return "\n Trait{" +
                ", type='" + type + '\'' +
                ", percentChance='" + percentChance + '\'' +
                ", filePath='" + filePath + '\'' +
                "} \n";
    }

}
