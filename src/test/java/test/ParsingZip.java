package test;

import book.JsonBook;
import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ParsingZip {
    ClassLoader cl = ParsingZip.class.getClassLoader();


    @Test
    void zipParse() throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("parseArchive.zip");
                ZipInputStream zis = new ZipInputStream(resource)
        ) {
            ZipEntry entry;


            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {

                    CSVReader reader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> content = reader.readAll();
                    assertThat(content.get(0)[1]).contains("Lesson");


                }
                else if (entry.getName().endsWith(".xls")){
                    XLS content = new XLS(zis);
                    assertThat(content.excel.getSheetAt(0).getRow(2).getCell(1).getStringCellValue()).contains("Mara");
                }
                else if (entry.getName().endsWith(".pdf")){
                    PDF content = new PDF(zis);
                    assertThat(content.producer).contains("Stefan Bechtold");
                }


            }
        }
    }
    @Test
    void jacksonJson () throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("jsonJack.json");
                InputStreamReader reader = new InputStreamReader(resource);
       )
        {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonBook book = objectMapper.readValue(reader, JsonBook.class);
            assertThat(book.name).isEqualTo("Roman");
            assertThat(book.age).isEqualTo(33);
            assertThat(book.married).isEqualTo(true);
        }
    }
}