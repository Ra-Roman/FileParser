package test;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;


public class FilesParsingTest {
    ClassLoader cl = FilesParsingTest.class.getClassLoader();//добавили класс для файла

    @Test
//скачать пдф
    void pdfParsTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File downloadedPdf = $("a[href='junit-user-guide-5.9.2.pdf']").download();
        PDF content = new PDF(downloadedPdf);
        assertThat(content.author).contains("Sam Brannen");
    }

    @Test
//скачать эксель
    void xlsParsTest() throws Exception {

        try (InputStream resourceAsStream = cl.getResourceAsStream("file_example_XLS_10.xls")) {
            XLS content = new XLS(resourceAsStream);
            assertThat(content.excel.getSheetAt(0).getRow(2).getCell(1).getStringCellValue()).contains("Mara");

        }

    }

    @Test
//скачать csv
    void csvParseTest() throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("testcsv.csv");
                CSVReader reader = new CSVReader(new InputStreamReader(resource))
        ) {
            List<String[]> content = reader.readAll();
            assertThat(content.get(0)[1]).contains("Lesson");
        }
    }

    @Test
    void zipParseTest() throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("testcsv.csv.zip");
                ZipInputStream zis = new ZipInputStream(resource)
        ) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                assertThat(entry.getName()).isEqualTo("testcsv.csv");

            }

        }
    }

    @Test
    void jsonParseTest () throws Exception  {
        Gson gson = new Gson();
        try (
                InputStream resource = cl.getResourceAsStream("jsontest.json");
                InputStreamReader  reader = new InputStreamReader(resource)
        ){
           JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
           assertThat(jsonObject.get("title").getAsString()).isEqualTo("example glossary");
           assertThat(jsonObject.get("GlossDiv").getAsJsonObject().get("title").getAsString()).isEqualTo("S");
        }
    }
}



