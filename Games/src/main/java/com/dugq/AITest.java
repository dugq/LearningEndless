package com.dugq;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by dugq on 2024/4/10.
 */
public class AITest {

    public static void main(String[] args) {
        try {
            ClassLoader classLoader = AITest.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("content.txt");

            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                }
                bufferedReader.close();
            } else {
                System.out.println("File not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
