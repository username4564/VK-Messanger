package com.qto.ru.vkmessanger.vk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Используется для обработки строки перенаправления
 */
public class VkUtil {
    /**
     * Получает из адресной строки ключ доступа
     * и id пользователя
     * @param url
     * Url ссылка
     * @return
     * Массив с ключем доступа и id пользователя
     * @throws Exception
     * Общая ошибка
     */
    public static String[] parseRedirectUrl(String url) throws Exception {
        String access_token = extractPattern(url, "access_token=(.*?)&");
        String user_id = extractPattern(url, "user_id=(\\d*)");
        if( user_id == null || user_id.length() == 0 || access_token == null || access_token.length() == 0 ) {
            throw new Exception("Failed to parse redirect url " + url);
        }
        return new String[]{access_token, user_id};
    }

    /**
     * Получает указанные данные из заданной строки
     * @param string
     * Строка содержащая данные
     * @param pattern
     * Строка содержащая данные которые нужно получить
     * @return
     * Указанные данные
     */
    public static String extractPattern(String string, String pattern){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (!m.find()) {
            return null;
        }
        return m.toMatchResult().group(1);
    }
}