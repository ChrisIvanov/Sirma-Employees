package com.sirma.employees.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class FileIOService {
    public List<String[]> fileReader(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {

            return readCsvFile(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<String[]> readCsvFile(InputStream inputStream) {

        List<String[]> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] field = parseCsvLine(line);
                data.add(field);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private static String[] parseCsvLine(String line) {
        List<String> field = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(line, ", ");

        while (tokenizer.hasMoreTokens()) {
            field.add(tokenizer.nextToken().trim());
        }

        return field.toArray(new String[0]);
    }
}
