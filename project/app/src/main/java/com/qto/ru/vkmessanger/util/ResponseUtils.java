package com.qto.ru.vkmessanger.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.client.Response;

public class ResponseUtils {

    /**
     * Возвращает Json строку полученную из результата запроса
     * @param response
     * Объект запроса к REST API
     * @return
     * Json строка
     */
    public static String stringFromResponse(Response response){
        if (response == null){
            return "";
        }
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getBody().in()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
