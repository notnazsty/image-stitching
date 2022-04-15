import generator.Generator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args){
        try {
            JSONObject config = new JSONObject(new String((Files.readAllBytes(Paths.get("src/config.json")))));
            int outputAmount = config.getInt("outputAmount");
            boolean unique = config.getBoolean("unique");
            int imageWidth = config.getInt("imageWidth");
            int imageHeight = config.getInt("imageHeight");
            String input = config.getString("imageDataDirectory");
            String output = config.getString("imageOutputDirectory");


            Generator g = new Generator(outputAmount, unique,imageHeight,imageWidth,input,output);
            g.generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //When placing the directory paths to the folders containing your images remember to order the array from layers at the back to the front.

}
