package tqs.project.backend.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResolveLocation {

    private ResolveLocation() {
    }

    public static ArrayList<Double> resolveAddress(String zipCode){

        ArrayList<Double> array = new ArrayList<Double>();

        URL url;

        try {
            url = new URL("https://json.geoapi.pt/cp/" + zipCode);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();

            if (responsecode != 200){
                return new ArrayList<Double>();
            }

            String inline = "";
            try (Scanner scanner = new Scanner(url.openStream())) {
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
            } catch (IOException e) {
                return new ArrayList<Double>();
            }

            JSONParser parse = new JSONParser();
            JSONObject dataObj = (JSONObject) parse.parse(inline);

            log.info("" + dataObj);

            JSONArray jsonArray = (JSONArray) dataObj.get("pontos");
            JSONObject obj = (JSONObject) jsonArray.get(0); //get 1st element
            JSONArray coordenadas = (JSONArray) obj.get("coordenadas");

            Double latitude = (Double) coordenadas.get(0);
            Double longitude = (Double) coordenadas.get(1);

            array.add(latitude);
            array.add(longitude);

            log.info(latitude + ", " + longitude);

        } catch (Exception e) {
            return new ArrayList<Double>();
        }

        return array;

    }

}