package org.archive.modules.extractor;

import org.archive.modules.CrawlURI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by shriphani on 8/9/14.
 */
public class StreamOutExtractor extends ExtractorHTML {

    private static Logger logger =
            Logger.getLogger(ExtractorHTML.class.getName());

    protected void extract(CrawlURI curi, CharSequence cs) {

        try{

            Socket clientSocket = new Socket("localhost", 6790);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            BufferedReader fromReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            JSONObject object = new JSONObject();

            System.out.println(curi);

            object.put("uri", JSONObject.escape(curi.toString()));
            object.put("payload", JSONObject.escape(cs.toString()));

            logger.log(Level.INFO, "Sent uri and payload to server");

            out.println(JSONValue.toJSONString(object));

            String response = fromReader.readLine();

            JSONArray arr = (JSONArray)JSONValue.parse(response);

            for (int i = 0; i < arr.size(); i++) {
                processEmbed(curi, (String)arr.get(i), "stream-out-extractor");
            }

        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
