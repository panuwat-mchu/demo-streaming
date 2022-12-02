package dev.mchu.demo.download;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping("/api")
public class DownloadController {

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> download(final HttpServletResponse response) {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition","attachment;filename=sample.zip");

        StreamingResponseBody stream = out -> {
            //final String home = System.getProperty("user.home");

            final File directory = new ClassPathResource("sample").getFile();

            final ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());

            if(directory.exists() && directory.isDirectory()) {
                try{
                    for(final File file: directory.listFiles()){
                        final InputStream is = new FileInputStream(file);

                        final ZipEntry zipEntry = new ZipEntry(file.getName());
                        zipOut.putNextEntry(zipEntry);
                        byte[] bytes=new byte[1024];
                        int length;
                        while ((length=is.read(bytes)) >=0 ){
                            zipOut.write(bytes, 0, length);
                        }
                        is.close();
                    }
                    zipOut.close();
                } catch (final IOException e) {
                    log.error("Exception while reading and streaming data {}", e);
                }
            }
        };
        log.info("streaming response {}", stream);
        return new ResponseEntity(stream, HttpStatus.OK);
    }
}
