package com.polsl.firmakurierska.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.polsl.firmakurierska.exception.BadRequestException;

/**
 * A helper class for sending http requests and receiving responses
 */
public class RequestController {
    private static final String baseURL = "http://localhost:8080";
    private String destURL;
    private String reqMode;

    /**
     * <p>requestMode = 3 [DELETE]</p>
     * <p>requestMode = 2 [PUT]</p>
     * <p>requestMode = 1 [POST]</p>
     * <p>requestMode = 0 [GET]</p>
     */
    public RequestController(String destination, int requestMode) {
        switch (requestMode) {
            case 3:
                reqMode = "DELETE";
                break;
            case 2:
                reqMode = "PUT";
                break;
            case 1:
                reqMode = "POST";
                break;
            case 0:
            default:
                reqMode = "GET";
                break;
        }

        destURL = baseURL + destination;
    };

    /**
     * Method for sending HTTP requests with JSON data
     * <p>If there is no response, it returns "NRP" by default</p>
     * @param data
     * @return Response string
     * @throws BadRequestException
     */
    public String sendJsonReq(String data) throws BadRequestException {
        String responseString = "NRP";
        if (data == null) {
            throw new BadRequestException("No data to send!");
        }

        try {
            URL obj = new URI(this.destURL).toURL();

            HttpURLConnection konnect = (HttpURLConnection)obj.openConnection();

            konnect.setRequestMethod(this.reqMode);
            konnect.setDoOutput(true);
            konnect.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream os = new DataOutputStream(konnect.getOutputStream())) {
                os.writeBytes(data);
                os.flush();
            }

            int httpCode = konnect.getResponseCode();

            if (httpCode == 200) {
                StringBuilder response = new StringBuilder();

                    try (
                        BufferedReader reader = new BufferedReader( new InputStreamReader( konnect.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                //System.out.println("Response: " + response.toString());
                responseString = response.toString();
            }
            konnect.disconnect();
        }
        catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
            responseString = "NRP";
        }
        catch (URISyntaxException ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            responseString = "NRP";
        }
        return responseString;
    }

    /**
     * Method for sending HTTP requests with data in URL path
     * <p>If there is no response, it returns "NRP" by default</p>
     * @return Response string
     * @throws BadRequestException
     */
    public String sendPathReq() throws BadRequestException {
        String responseString = "NRP";

        try {
            URL obj = new URI(this.destURL).toURL();

            HttpURLConnection konnect = (HttpURLConnection)obj.openConnection();

            konnect.setRequestMethod(this.reqMode);
            konnect.setDoOutput(true);

            int httpCode = konnect.getResponseCode();

            if (httpCode == 200) {
                StringBuilder response = new StringBuilder();

                    try (
                        BufferedReader reader = new BufferedReader( new InputStreamReader( konnect.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                //System.out.println("Response: " + response.toString());
                responseString = response.toString();
            }
            konnect.disconnect();
        }
        catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
            responseString = "NRP";
        }
        catch (URISyntaxException ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            responseString = "NRP";
        }
        return responseString;
    }

    /**
     * Method that parses stanowisko ID from JSON
     * @param jsonData
     * @return stanowisko_id
     */
    public String getStanowisko(String jsonData) {

        String stanowiskoID = "";

        try {
            JSONObject pracownikDataJSON = new JSONObject(jsonData);
            String links = pracownikDataJSON.getString("_links");
            JSONObject linksData = new JSONObject(links);
            String stanowisko = linksData.getString("stanowisko");
            JSONObject stanowiskoData = new JSONObject(stanowisko);
            String href = stanowiskoData.getString("href");

            // String[] hrefTokens = href.split("/");
            // boolean stupidFlag = false;
            // for (String t : hrefTokens) {
            //     if (stupidFlag == true) {
            //         stanowiskoID = new String(t);
            //         break;
            //     }
            //     if (t.equals("stanowisko")) stupidFlag = true;
            // }
            stanowiskoID = returnValueFromHref("stanowisko", href);

        } catch (JSONException jex) {
            System.out.println(jex.toString());
            jex.printStackTrace();
        }

        return stanowiskoID;
    }

    /**
     * Method that parses driving license categories from JSON
     * @param jsonData
     * @return all driving license categories from jsonData
     */
    public List<String> getKategorie(String jsonData) {

        List<String> categoriesID = new ArrayList<>();

        try {
            JSONObject pracownikDataJSON = new JSONObject(jsonData);
            String links = pracownikDataJSON.getString("_links");
            JSONObject linksData = new JSONObject(links);
            String prawoJazdy = linksData.getString("prawo_jazdy");

            List<String> hrefsList = new ArrayList<>();
            String href = "";

            try {
                JSONArray hrefData = new JSONArray(prawoJazdy);

                for (int i = 0; i < hrefData.length(); i++) {
                    JSONObject hrefJson = hrefData.optJSONObject(i);
                    hrefsList.add(hrefJson.getString("href"));
                }
            }
            catch (JSONException e) {
                JSONObject hrefData = new JSONObject(prawoJazdy);
                href = hrefData.getString("href");
            }

            if (!hrefsList.isEmpty()) {
                hrefsList.forEach(hrefElement -> {
                    String[] hrefTokens = hrefElement.split("/");
                    boolean stupidFlag = false;
                    for (String t : hrefTokens) {
                        if (stupidFlag == true) {
                            categoriesID.add(t);
                            break;
                        }
                        if (t.equals("prawojazdy")) stupidFlag = true;
                    }
                });
            }
            else {
                String[] hrefTokens = href.split("/");
                boolean stupidFlag = false;
                for (String t : hrefTokens) {
                    if (stupidFlag == true) {
                        categoriesID.add(t);
                        break;
                    }
                    if (t.equals("prawojazdy")) stupidFlag = true;
                }
            }
        }
        catch (JSONException jex) {
            System.out.println(jex.toString());
            jex.printStackTrace();
        }

        return categoriesID;
    }

    /**
     * Method that parses href links from DTO
     * @param fieldToRetrieve field that we want value from, eg. stanowisko in pracownik
     * @param href link to parse
     * @return all driving license categories from jsonData
     */
    public String returnValueFromHref(String fieldToRetrieve, String href) {
        String result = "";

        String[] hrefTokens = href.split("/");
        boolean stupidFlag = false;
        for (String t : hrefTokens) {
            if (stupidFlag == true) {
                result = t;
                break;
            }
            if (t.equals(fieldToRetrieve)) stupidFlag = true;
        }
        return result;
    }
}
