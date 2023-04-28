package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class ReadJson {
    public static Table parseTable(JSONArray putput) {
        int rows = putput.size();
        int columns = ((JSONArray)putput.get(0)).size();
        Table table = new Table(rows, columns);

        for (int i = 0; i < rows; i++) {
            JSONArray row = (JSONArray)putput.get(i);
            for (int j = 0; j < columns; j++) {
                String value = (String) row.get(j);
                table.setData(i, j, value);
            }
        }

        return table;

    }
    public static Table[] readJson(String name) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(name));

            JSONObject jsonObject = (JSONObject) obj;

            JSONArray input = (JSONArray) jsonObject.get("input");
            JSONArray result = (JSONArray) jsonObject.get("result");

            Table inputTable = parseTable(input);
            Table expectedTable = parseTable(result);

            return new Table[] {inputTable, expectedTable};

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
}
