package com.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.MINUTES, 5);
        CrptApi.Document document = new CrptApi.Document(
                new CrptApi.Document.Description("string"), "string", "string",
                "LP_INTRODUCE_GOODS", true, "string", "string",
                "string", "2020-01-23", "string",
                new ArrayList< CrptApi.Document.Product > () {{
                    add(new CrptApi.Document.Product("string", "2020-01-23",
                            "string", "string", "string",
                            "2020-01-23", "string", "string", "string"));
                }},
                "2020-01-23", "string");
        for (;;) {
            crptApi.CreateDocument(document, "signature");
        }
    }
}